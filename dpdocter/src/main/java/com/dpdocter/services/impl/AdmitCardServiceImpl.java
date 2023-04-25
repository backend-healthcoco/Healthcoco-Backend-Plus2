package com.dpdocter.services.impl;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.DefaultPrintSettings;
import com.dpdocter.beans.MailAttachment;
import com.dpdocter.beans.Patient;
import com.dpdocter.collections.AdmitCardCollection;
import com.dpdocter.collections.EmailTrackCollection;
import com.dpdocter.collections.LocationCollection;
import com.dpdocter.collections.PatientCollection;
import com.dpdocter.collections.PrintSettingsCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.enums.ComponentType;
import com.dpdocter.enums.LineSpace;
import com.dpdocter.enums.PrintSettingType;
import com.dpdocter.enums.UniqueIdInitial;
import com.dpdocter.enums.VitalSignsUnit;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.AdmitCardRepository;
import com.dpdocter.repository.LocationRepository;
import com.dpdocter.repository.PatientRepository;
import com.dpdocter.repository.PrintSettingsRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.request.AdmitCardRequest;
import com.dpdocter.request.DischargeSummaryRequest;
import com.dpdocter.response.AdmitCardResponse;
import com.dpdocter.response.JasperReportResponse;
import com.dpdocter.response.MailResponse;
import com.dpdocter.services.AdmitCardService;
import com.dpdocter.services.DischargeSummaryService;
import com.dpdocter.services.EmailTackService;
import com.dpdocter.services.JasperReportService;
import com.dpdocter.services.MailBodyGenerator;
import com.dpdocter.services.MailService;
import com.dpdocter.services.PatientVisitService;

import common.util.web.DPDoctorUtils;

@Transactional
@Service
public class AdmitCardServiceImpl implements AdmitCardService {

	private static Logger logger = Logger.getLogger(AdmitCardServiceImpl.class.getName());

	@Autowired
	private MongoTemplate mongoTemplate;

	@Value(value = "${image.path}")
	private String imagePath;

	@Autowired
	private AdmitCardRepository admitCardRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PatientRepository patientRepository;

	@Autowired
	private JasperReportService jasperReportService;

	@Autowired
	private PatientVisitService patientVisitService;

	@Autowired
	private PrintSettingsRepository printSettingsRepository;

	@Autowired
	private LocationRepository locationRepository;

	@Autowired
	private EmailTackService emailTackService;

	@Autowired
	private MailBodyGenerator mailBodyGenerator;

	@Autowired
	private MailService mailService;

	@Autowired
	private DischargeSummaryService dischargeSummaryService;

	@Value(value = "${jasper.print.admitCard.a4.fileName}")
	private String admitCardReportA4FileName;

	@Override
	public AdmitCardResponse addEditAdmitcard(AdmitCardRequest request) {
		AdmitCardResponse response = null;
		try {
			Patient patientdetail = new Patient();
			UserCollection doctor = userRepository.findById(new ObjectId(request.getDoctorId())).orElse(null);
			if (doctor == null) {
				throw new BusinessException(ServiceError.InvalidInput, "Invalid DoctorId");
			}

			PatientCollection patientCollection = patientRepository.findByUserIdAndLocationIdAndHospitalId(
					new ObjectId(request.getPatientId()), new ObjectId(request.getLocationId()),
					new ObjectId(request.getHospitalId()));

			if (patientCollection == null) {
				throw new BusinessException(ServiceError.InvalidInput, "Invalid patient");
			}
			BeanUtil.map(patientCollection, patientdetail);

			AdmitCardCollection admitCardCollection = new AdmitCardCollection();

			BeanUtil.map(request, admitCardCollection);
			if (admitCardCollection.getId() == null) {
				admitCardCollection.setAdminCreatedTime(new Date());
				if (request.getCreatedTime() == null) {
					admitCardCollection.setCreatedTime(new Date());
				}
				admitCardCollection.setCreatedBy(doctor.getTitle() + " " + doctor.getFirstName());
				admitCardCollection.setUniqueEmrId(
						UniqueIdInitial.ADMIT_CARD.getInitial() + "-" + DPDoctorUtils.generateRandomId());
				DischargeSummaryRequest dischargeSummaryRequest = new DischargeSummaryRequest();
				BeanUtil.map(request, dischargeSummaryRequest);
				dischargeSummaryRequest.setTreatmentsGiven(request.getTreatmentsPlan());
				dischargeSummaryRequest.setGeneralExam(request.getExamination());
				dischargeSummaryService.addEditDischargeSummary(dischargeSummaryRequest);
			} else {
				AdmitCardCollection oldAdmitCardCollection = admitCardRepository.findById(admitCardCollection.getId())
						.orElse(null);
				admitCardCollection.setAdminCreatedTime(oldAdmitCardCollection.getAdminCreatedTime());
				if (request.getCreatedTime() == null) {
					admitCardCollection.setCreatedTime(oldAdmitCardCollection.getCreatedTime());
				}
				admitCardCollection.setCreatedBy(oldAdmitCardCollection.getCreatedBy());
				admitCardCollection.setUniqueEmrId(oldAdmitCardCollection.getUniqueEmrId());
				admitCardCollection.setIsPatientDiscarded(oldAdmitCardCollection.getIsPatientDiscarded());
			}
			admitCardCollection = admitCardRepository.save(admitCardCollection);
			response = new AdmitCardResponse();
			BeanUtil.map(admitCardCollection, response);
			response.setPatient(patientdetail);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While add edit Admit card ");
		}
		return response;
	}

	@Override
	public AdmitCardResponse getAdmitCard(String cardId) {
		AdmitCardResponse response = null;
		try {
			AdmitCardCollection admitCardCollection = admitCardRepository.findById(new ObjectId(cardId)).orElse(null);

			if (admitCardCollection == null) {
				throw new BusinessException(ServiceError.InvalidInput, "Invalid Id");
			}
			PatientCollection patientCollection = patientRepository.findByUserIdAndLocationIdAndHospitalId(
					admitCardCollection.getPatientId(), admitCardCollection.getLocationId(),
					admitCardCollection.getHospitalId());

			response = new AdmitCardResponse();
			BeanUtil.map(admitCardCollection, response);
			Patient patient = new Patient();
			BeanUtil.map(patientCollection, patient);
			response.setPatient(patient);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While get Admit card by Id ");
		}
		return response;

	}

	@Override
	public List<AdmitCardResponse> getAdmitCards(String doctorId, String locationId, String hospitalId,
			String patientId, long page, int size, long updatedTime, Boolean discarded) {
		List<AdmitCardResponse> response = null;
		try {
			Criteria criteria = new Criteria("isPatientDiscarded").ne(true);
			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				criteria = criteria.and("doctorId").is(new ObjectId(doctorId));
			}
			if (!DPDoctorUtils.anyStringEmpty(locationId)) {
				criteria = criteria.and("locationId").is(new ObjectId(locationId));
			}
			if (!DPDoctorUtils.anyStringEmpty(patientId)) {
				criteria = criteria.and("patientId").is(new ObjectId(patientId));
			}
			if (!DPDoctorUtils.anyStringEmpty(hospitalId)) {
				criteria = criteria.and("hospitalId").is(new ObjectId(hospitalId));
			}
			if (updatedTime > 0) {
				criteria = criteria.and("updatedTime").is(new Date(updatedTime));
			}

			criteria = criteria.and("discarded").is(discarded);

			Aggregation aggregation = null;

			if (size > 0)
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")), Aggregation.skip((page) * size),
						Aggregation.limit(size));
			else
				aggregation = Aggregation.newAggregation(

						Aggregation.match(criteria), Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));

			AggregationResults<AdmitCardResponse> aggregationResults = mongoTemplate.aggregate(aggregation,
					AdmitCardCollection.class, AdmitCardResponse.class);
			response = aggregationResults.getMappedResults();

			PatientCollection patientCollection = null;
			Patient patient = null;

			for (AdmitCardResponse admitCardResponse : response) {
				/*
				 * patientCollection = patientRepository.
				 * findByUserIdDoctorIdLocationIdAndHospitalId( new
				 * ObjectId(admitCardResponse.getPatientId()), new
				 * ObjectId(admitCardResponse.getDoctorId()), new
				 * ObjectId(admitCardResponse.getLocationId()), new
				 * ObjectId(admitCardResponse.getHospitalId()));
				 */

				patientCollection = patientRepository.findByUserIdAndLocationIdAndHospitalId(
						new ObjectId(admitCardResponse.getPatientId()), new ObjectId(admitCardResponse.getLocationId()),
						new ObjectId(admitCardResponse.getHospitalId()));
				patient = new Patient();
				BeanUtil.map(patientCollection, patient);
				admitCardResponse.setPatient(patient);

			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While get Admit cards ");
		}
		return response;
	}

	@Transactional
	@Override
	public Boolean deleteAdmitCard(String cardId, String doctorId, String hospitalId, String locationId,
			Boolean discarded) {

		Boolean response = false;
		try {
			AdmitCardCollection admitCardCollection = admitCardRepository.findById(new ObjectId(cardId)).orElse(null);
			if (admitCardCollection != null) {
				if (!DPDoctorUtils.anyStringEmpty(admitCardCollection.getDoctorId(),
						admitCardCollection.getHospitalId(), admitCardCollection.getLocationId())) {
					if (admitCardCollection.getDoctorId().toString().equals(doctorId)
							&& admitCardCollection.getHospitalId().toString().equals(hospitalId)
							&& admitCardCollection.getLocationId().toString().equals(locationId)) {
						admitCardCollection.setDiscarded(discarded);
						admitCardCollection.setUpdatedTime(new Date());
						admitCardRepository.save(admitCardCollection);
						response = true;

					} else {
						logger.warn("Invalid Doctor Id, Hospital Id, Or Location Id");
						throw new BusinessException(ServiceError.InvalidInput,
								"Invalid Doctor Id, Hospital Id, Or Location Id");
					}
				}
			} else {
				logger.warn("Admit card not found!");
				throw new BusinessException(ServiceError.NoRecord, "Admit card  not found!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;

	}

	@Override
	public int getAdmitCardCount(ObjectId doctorObjectId, ObjectId patientObjectId, ObjectId locationObjectId,
			ObjectId hospitalObjectId, boolean isOTPVerified) {
		int response = 0;
		try {
			if (isOTPVerified)
				response = admitCardRepository.countByPatientId(patientObjectId);
			else
				response = admitCardRepository.countByPatientIdDoctorLocationHospital(patientObjectId, doctorObjectId,
						locationObjectId, hospitalObjectId);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error while count discharge summary : " + e.getCause().getMessage());
			throw new BusinessException(ServiceError.Unknown,
					"Error while count discharge summary : " + e.getCause().getMessage());
		}

		return response;
	}

	@Override
	public String downloadDischargeSummary(String admitCardId) {
		String response = null;

		try {
			AdmitCardCollection admitCardCollection = admitCardRepository.findById(new ObjectId(admitCardId))
					.orElse(null);
			if (admitCardCollection != null) {
				PatientCollection patient = patientRepository.findByUserIdAndLocationIdAndHospitalId(
						admitCardCollection.getPatientId(), admitCardCollection.getLocationId(),
						admitCardCollection.getHospitalId());

				UserCollection user = userRepository.findById(admitCardCollection.getPatientId()).orElse(null);
				JasperReportResponse jasperReportResponse = createJasper(admitCardCollection, patient, user,
						PrintSettingType.IPD.getType());
				if (jasperReportResponse != null)
					response = getFinalImageURL(jasperReportResponse.getPath());
				if (jasperReportResponse != null && jasperReportResponse.getFileSystemResource() != null)
					if (jasperReportResponse.getFileSystemResource().getFile().exists())
						jasperReportResponse.getFileSystemResource().getFile().delete();
			} else {
				logger.warn("Invoice Id does not exist");
				throw new BusinessException(ServiceError.NotFound, "Id does not exist");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Exception in download admit card ");
		}
		return response;
	}

	private JasperReportResponse createJasper(AdmitCardCollection admitCardCollection, PatientCollection patient,
			UserCollection user, String printSettingType) throws NumberFormatException, IOException, ParseException {
		JasperReportResponse response = null;
		Map<String, Object> parameters = new HashMap<String, Object>();
		String pattern = "dd/MM/yyyy";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		simpleDateFormat.setTimeZone(TimeZone.getTimeZone("IST"));
		Boolean show = false;
		PrintSettingsCollection printSettings = null;
		printSettings = printSettingsRepository
				.findByDoctorIdAndLocationIdAndHospitalIdAndComponentTypeAndPrintSettingType(
						admitCardCollection.getDoctorId(), admitCardCollection.getLocationId(),
						admitCardCollection.getHospitalId(), ComponentType.ALL.getType(), printSettingType);
		if (printSettings == null) {
			List<PrintSettingsCollection> printSettingsCollections = printSettingsRepository
					.findListByDoctorIdAndLocationIdAndHospitalIdAndComponentTypeAndPrintSettingType(
							admitCardCollection.getDoctorId(), admitCardCollection.getLocationId(),
							admitCardCollection.getHospitalId(), ComponentType.ALL.getType(),
							PrintSettingType.DEFAULT.getType(), new Sort(Sort.Direction.DESC, "updatedTime"));
			if (!DPDoctorUtils.isNullOrEmptyList(printSettingsCollections))
				printSettings = printSettingsCollections.get(0);
		}
		if (printSettings == null) {
			printSettings = new PrintSettingsCollection();
			DefaultPrintSettings defaultPrintSettings = new DefaultPrintSettings();
			BeanUtil.map(defaultPrintSettings, printSettings);
		}

		if (admitCardCollection.getAdmissionDate() != null) {
			show = true;
			parameters.put("dOA", simpleDateFormat.format(admitCardCollection.getAdmissionDate()));
		}
		parameters.put("showDOA", show);
		show = false;
		if (admitCardCollection.getDischargeDate() != null) {
			show = true;
			parameters.put("dOD", simpleDateFormat.format(admitCardCollection.getDischargeDate()));
		}
		parameters.put("showDOD", show);
		show = false;
		if (!DPDoctorUtils.allStringsEmpty(admitCardCollection.getTimeOfAdmission())) {
			show = true;
			SimpleDateFormat sdfForMins = new SimpleDateFormat("mm");
			Date dt = sdfForMins.parse(admitCardCollection.getTimeOfAdmission());
			sdfForMins = new SimpleDateFormat("hh:mm a");
			parameters.put("tOA", sdfForMins.format(dt));
		}
		parameters.put("showTOA", show);
		show = false;
		if (!DPDoctorUtils.allStringsEmpty(admitCardCollection.getTimeOfDischarge())) {
			show = true;
			SimpleDateFormat sdfForMins = new SimpleDateFormat("mm");
			Date dt = sdfForMins.parse(admitCardCollection.getTimeOfDischarge());
			sdfForMins = new SimpleDateFormat("hh:mm a");
			parameters.put("tOD", sdfForMins.format(dt));
		}
		parameters.put("showTOD", show);
		show = false;
		if (!DPDoctorUtils.allStringsEmpty(admitCardCollection.getTimeOfOperation())) {
			show = true;
			SimpleDateFormat sdfForMins = new SimpleDateFormat("mm");
			Date dt = sdfForMins.parse(admitCardCollection.getTimeOfOperation());
			sdfForMins = new SimpleDateFormat("hh:mm a");

			parameters.put("tOO", sdfForMins.format(dt));
		}

		parameters.put("showTOO", show);
		show = false;
		if (admitCardCollection.getOperationDate() != null) {
			show = true;
			parameters.put("operationdate", simpleDateFormat.format(admitCardCollection.getOperationDate()));
		}
		parameters.put("showOD", show);
		show = false;

		if (!DPDoctorUtils.allStringsEmpty(admitCardCollection.getNatureOfOperation())) {
			show = true;
			parameters.put("natureOfOperation", admitCardCollection.getNatureOfOperation());
		}
		parameters.put("showNOfOp", show);
		show = false;

		if (!DPDoctorUtils.allStringsEmpty(admitCardCollection.getPastHistory())) {
			show = true;
			parameters.put("pastHistory", admitCardCollection.getPastHistory());
		}
		parameters.put("showPH", show);
		show = false;

		if (!DPDoctorUtils.allStringsEmpty(admitCardCollection.getFamilyHistory())) {
			show = true;
			parameters.put("familyHistory", admitCardCollection.getFamilyHistory());
		}
		parameters.put("showFH", show);
		show = false;

		if (!DPDoctorUtils.allStringsEmpty(admitCardCollection.getPersonalHistory())) {
			show = true;
			parameters.put("personalHistory", admitCardCollection.getPersonalHistory());
		}
		parameters.put("showPersonalHistory", show);
		show = false;

		if (!DPDoctorUtils.allStringsEmpty(admitCardCollection.getComplaint())) {
			show = true;
			parameters.put("complaints", admitCardCollection.getComplaint());
		}
		parameters.put("showcompl", show);
		show = false;

		if (!DPDoctorUtils.allStringsEmpty(admitCardCollection.getJointInvolvement())) {
			show = true;
			parameters.put("jointInvolvement", admitCardCollection.getJointInvolvement());
		}
		parameters.put("showJINV", show);
		show = false;

		if (!DPDoctorUtils.allStringsEmpty(admitCardCollection.getxRayDetails())) {
			show = true;
			parameters.put("xRayDetails", admitCardCollection.getxRayDetails());
		}
		parameters.put("showXD", show);
		show = false;

		if (!DPDoctorUtils.allStringsEmpty(admitCardCollection.getDiagnosis())) {
			show = true;
			parameters.put("diagnosis", admitCardCollection.getDiagnosis());
		}
		parameters.put("showDiagnosis", show);
		show = false;

		if (!DPDoctorUtils.allStringsEmpty(admitCardCollection.getTreatmentsPlan())) {
			show = true;
			parameters.put("treatmentPlan", admitCardCollection.getTreatmentsPlan());
		}
		parameters.put("showTP", show);
		show = false;

		if (!DPDoctorUtils.allStringsEmpty(admitCardCollection.getExamination())) {
			show = true;
			parameters.put("examination", admitCardCollection.getExamination());
		}
		parameters.put("showEx", show);
		show = false;

		if (!DPDoctorUtils.allStringsEmpty(admitCardCollection.getIp())) {
			show = true;
			parameters.put("ip", admitCardCollection.getIp());
		}
		parameters.put("showIp", show);
		show = false;

		if (!DPDoctorUtils.allStringsEmpty(admitCardCollection.getAddress())) {
			show = true;
			parameters.put("address", admitCardCollection.getAddress());
		}
		parameters.put("showAddress", show);
		show = false;

		// new fields
		show = false;
		if (!DPDoctorUtils.allStringsEmpty(admitCardCollection.getPreOprationalOrders())) {
			show = true;
			parameters.put("preOprationalOrders", admitCardCollection.getPreOprationalOrders());
		}
		parameters.put("showOrd", show);

		show = false;
		if (!DPDoctorUtils.allStringsEmpty(admitCardCollection.getNursingCare())) {
			show = true;
			parameters.put("nursingCare", admitCardCollection.getNursingCare());
		}
		parameters.put("showNCare", show);

		show = false;
		if (!DPDoctorUtils.allStringsEmpty(admitCardCollection.getIpdNumber())) {
			show = true;
			parameters.put("ipdNumber", admitCardCollection.getIpdNumber());
		}
		parameters.put("showIpdNumber", show);

		if (admitCardCollection.getVitalSigns() != null) {
			String vitalSigns = null;

			String pulse = admitCardCollection.getVitalSigns().getPulse();
			pulse = (pulse != null && !pulse.isEmpty() ? "Pulse: " + pulse + " " + VitalSignsUnit.PULSE.getUnit() : "");
			if (!DPDoctorUtils.allStringsEmpty(pulse))
				vitalSigns = pulse;

			String temp = admitCardCollection.getVitalSigns().getTemperature();
			temp = (temp != null && !temp.isEmpty()
					? "Temperature: " + temp + " " + VitalSignsUnit.TEMPERATURE.getUnit()
					: "");
			if (!DPDoctorUtils.allStringsEmpty(temp)) {
				if (!DPDoctorUtils.allStringsEmpty(vitalSigns))
					vitalSigns = vitalSigns + ",  " + temp;
				else
					vitalSigns = temp;
			}

			String breathing = admitCardCollection.getVitalSigns().getBreathing();
			breathing = (breathing != null && !breathing.isEmpty()
					? "Breathing: " + breathing + " " + VitalSignsUnit.BREATHING.getUnit()
					: "");
			if (!DPDoctorUtils.allStringsEmpty(breathing)) {
				if (!DPDoctorUtils.allStringsEmpty(vitalSigns))
					vitalSigns = vitalSigns + ",  " + breathing;
				else
					vitalSigns = breathing;
			}

			String weight = admitCardCollection.getVitalSigns().getWeight();
			weight = (weight != null && !weight.isEmpty() ? "Weight: " + weight + " " + VitalSignsUnit.WEIGHT.getUnit()
					: "");
			if (!DPDoctorUtils.allStringsEmpty(weight)) {
				if (!DPDoctorUtils.allStringsEmpty(vitalSigns))
					vitalSigns = vitalSigns + ",  " + weight;
				else
					vitalSigns = weight;
			}

			String bloodPressure = "";
			if (admitCardCollection.getVitalSigns().getBloodPressure() != null) {
				String systolic = admitCardCollection.getVitalSigns().getBloodPressure().getSystolic();
				systolic = systolic != null && !systolic.isEmpty() ? systolic : "";

				String diastolic = admitCardCollection.getVitalSigns().getBloodPressure().getDiastolic();
				diastolic = diastolic != null && !diastolic.isEmpty() ? diastolic : "";

				if (!DPDoctorUtils.anyStringEmpty(systolic, diastolic))
					bloodPressure = "B.P: " + systolic + "/" + diastolic + " " + VitalSignsUnit.BLOODPRESSURE.getUnit();
				if (!DPDoctorUtils.allStringsEmpty(bloodPressure)) {
					if (!DPDoctorUtils.allStringsEmpty(vitalSigns))
						vitalSigns = vitalSigns + ",  " + bloodPressure;
					else
						vitalSigns = bloodPressure;
				}
			}

			String spo2 = admitCardCollection.getVitalSigns().getSpo2();
			spo2 = (spo2 != null && !spo2.isEmpty() ? "SPO2: " + spo2 + " " + VitalSignsUnit.SPO2.getUnit() : "");
			if (!DPDoctorUtils.allStringsEmpty(spo2)) {
				if (!DPDoctorUtils.allStringsEmpty(vitalSigns))
					vitalSigns = vitalSigns + ",  " + spo2;
				else
					vitalSigns = spo2;
			}
			String height = admitCardCollection.getVitalSigns().getHeight();
			height = (height != null && !height.isEmpty() ? "Height: " + height + " " + VitalSignsUnit.HEIGHT.getUnit()
					: "");
			if (!DPDoctorUtils.allStringsEmpty(height)) {
				if (!DPDoctorUtils.allStringsEmpty(vitalSigns))
					vitalSigns = vitalSigns + ",  " + height;
				else
					vitalSigns = spo2;
			}

			String bmi = admitCardCollection.getVitalSigns().getBmi();
			if (!DPDoctorUtils.allStringsEmpty(bmi)) {
				if (bmi.equalsIgnoreCase("nan")) {
					bmi = "";
				}

			} else {
				bmi = "";
			}

			if (!DPDoctorUtils.allStringsEmpty(bmi)) {
				bmi = "Bmi: " + String.format("%.3f", Double.parseDouble(bmi));
				if (!DPDoctorUtils.allStringsEmpty(bmi)) {
					vitalSigns = vitalSigns + ",  " + bmi;
				} else {
					vitalSigns = bmi;
				}
			}

			String bsa = admitCardCollection.getVitalSigns().getBsa();
			if (!DPDoctorUtils.allStringsEmpty(bsa)) {
				if (bsa.equalsIgnoreCase("nan"))
					bsa = "";

			} else {
				bsa = "";
			}
			if (!DPDoctorUtils.allStringsEmpty(bsa)) {
				bsa = "Bsa: " + String.format("%.3f", Double.parseDouble(bsa));
				if (!DPDoctorUtils.allStringsEmpty(vitalSigns))
					vitalSigns = vitalSigns + ",  " + bsa;
				else
					vitalSigns = bsa;
			}
			parameters.put("vitalSigns", vitalSigns != null && !vitalSigns.isEmpty() ? vitalSigns : null);
		} else {
			parameters.put("vitalSigns", null);
		}

		parameters.put("contentLineSpace",
				(printSettings != null && !DPDoctorUtils.anyStringEmpty(printSettings.getContentLineStyle()))
						? printSettings.getContentLineSpace()
						: LineSpace.SMALL.name());
		patientVisitService.generatePatientDetails(
				(printSettings != null && printSettings.getHeaderSetup() != null
						? printSettings.getHeaderSetup().getPatientDetails()
						: null),
				patient,
				"<b>ADMIT-CARD-ID: </b>"
						+ (admitCardCollection.getUniqueEmrId() != null ? admitCardCollection.getUniqueEmrId() : "--"),
				patient.getLocalPatientName(), user.getMobileNumber(), parameters,
				admitCardCollection.getCreatedTime() != null ? admitCardCollection.getCreatedTime() : new Date(),
				printSettings.getHospitalUId(), printSettings.getIsPidHasDate());
		patientVisitService.generatePrintSetup(parameters, printSettings, admitCardCollection.getDoctorId());
		String pdfName = (user != null ? user.getFirstName() : "") + "ADMIT-CARD-"
				+ admitCardCollection.getUniqueEmrId() + new Date().getTime();

		String layout = printSettings != null
				? (printSettings.getPageSetup() != null ? printSettings.getPageSetup().getLayout() : "PORTRAIT")
				: "PORTRAIT";
		String pageSize = printSettings != null
				? (printSettings.getPageSetup() != null ? printSettings.getPageSetup().getPageSize() : "A4")
				: "A4";
		Integer topMargin = printSettings != null
				? (printSettings.getPageSetup() != null ? printSettings.getPageSetup().getTopMargin() : 20)
				: 20;
		Integer bottonMargin = printSettings != null
				? (printSettings.getPageSetup() != null ? printSettings.getPageSetup().getBottomMargin() : 20)
				: 20;
		Integer leftMargin = printSettings != null
				? (printSettings.getPageSetup() != null && printSettings.getPageSetup().getLeftMargin() != 20
						? printSettings.getPageSetup().getLeftMargin()
						: 20)
				: 20;
		Integer rightMargin = printSettings != null
				? (printSettings.getPageSetup() != null && printSettings.getPageSetup().getRightMargin() != null
						? printSettings.getPageSetup().getRightMargin()
						: 20)
				: 20;
		response = jasperReportService.createPDF(ComponentType.ADMIT_CARD, parameters, admitCardReportA4FileName,
				layout, pageSize, topMargin, bottonMargin, leftMargin, rightMargin,
				Integer.parseInt(parameters.get("contentFontSize").toString()), pdfName.replaceAll("\\s+", ""));

		return response;

	}

	private String getFinalImageURL(String imageURL) {
		if (imageURL != null) {
			return imagePath + imageURL;
		} else
			return null;
	}

	@Override
	public void emailAdmitCard(String admitcardId, String doctorId, String locationId, String hospitalId,
			String emailAddress) {
		MailResponse mailResponse = null;
		AdmitCardCollection admitCardCollection = null;
		MailAttachment mailAttachment = null;
		UserCollection user = null;
		PatientCollection patient = null;
		EmailTrackCollection emailTrackCollection = new EmailTrackCollection();
		try {
			admitCardCollection = admitCardRepository.findById(new ObjectId(admitcardId)).orElse(null);
			if (admitCardCollection != null) {
				if (admitCardCollection.getDoctorId() != null && admitCardCollection.getHospitalId() != null
						&& admitCardCollection.getLocationId() != null) {
					if (admitCardCollection.getDoctorId().equals(doctorId)
							&& admitCardCollection.getHospitalId().equals(hospitalId)
							&& admitCardCollection.getLocationId().equals(locationId)) {

						user = userRepository.findById(admitCardCollection.getPatientId()).orElse(null);
						patient = patientRepository.findByUserIdAndLocationIdAndHospitalId(
								admitCardCollection.getPatientId(), admitCardCollection.getLocationId(),
								admitCardCollection.getHospitalId());
						user.setFirstName(patient.getLocalPatientName());
						emailTrackCollection.setDoctorId(admitCardCollection.getDoctorId());
						emailTrackCollection.setHospitalId(admitCardCollection.getHospitalId());
						emailTrackCollection.setLocationId(admitCardCollection.getLocationId());
						emailTrackCollection.setType(ComponentType.ADMIT_CARD.getType());
						emailTrackCollection.setSubject("ADMIT CARD");
						if (user != null) {
							emailTrackCollection.setPatientName(patient.getLocalPatientName());
							emailTrackCollection.setPatientId(user.getId());
						}

						JasperReportResponse jasperReportResponse = createJasper(admitCardCollection, patient, user,
								PrintSettingType.EMAIL.getType());
						mailAttachment = new MailAttachment();
						mailAttachment.setAttachmentName(FilenameUtils.getName(jasperReportResponse.getPath()));
						mailAttachment.setFileSystemResource(jasperReportResponse.getFileSystemResource());
						UserCollection doctorUser = userRepository.findById(new ObjectId(doctorId)).orElse(null);
						LocationCollection locationCollection = locationRepository.findById(new ObjectId(locationId))
								.orElse(null);

						mailResponse = new MailResponse();
						mailResponse.setMailAttachment(mailAttachment);
						mailResponse.setDoctorName(doctorUser.getTitle() + " " + doctorUser.getFirstName());
						String address = (!DPDoctorUtils.anyStringEmpty(locationCollection.getStreetAddress())
								? locationCollection.getStreetAddress() + ", "
								: "")
								+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getLandmarkDetails())
										? locationCollection.getLandmarkDetails() + ", "
										: "")
								+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getLocality())
										? locationCollection.getLocality() + ", "
										: "")
								+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getCity())
										? locationCollection.getCity() + ", "
										: "")
								+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getState())
										? locationCollection.getState() + ", "
										: "")
								+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getCountry())
										? locationCollection.getCountry() + ", "
										: "")
								+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getPostalCode())
										? locationCollection.getPostalCode()
										: "");

						if (address.charAt(address.length() - 2) == ',') {
							address = address.substring(0, address.length() - 2);
						}
						mailResponse.setClinicAddress(address);
						mailResponse.setClinicName(locationCollection.getLocationName());
						SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
						sdf.setTimeZone(TimeZone.getTimeZone("IST"));
						mailResponse.setMailRecordCreatedDate(sdf.format(admitCardCollection.getCreatedTime()));
						mailResponse.setPatientName(user.getFirstName());
						emailTackService.saveEmailTrack(emailTrackCollection);

					} else {
						logger.warn("Admit Card Id, doctorId, location Id, hospital Id does not match");
						throw new BusinessException(ServiceError.NotFound,
								" Admit Card  Id, doctorId, location Id, hospital Id does not match");
					}
				}

			} else {
				logger.warn("Discharge Summary  not found.Please check Admit Card Id.");
				throw new BusinessException(ServiceError.NoRecord,
						"Discharge Summary not found.Please check Admit Card Id.");
			}

			String body = mailBodyGenerator.generateEMREmailBody(mailResponse.getPatientName(),
					mailResponse.getDoctorName(), mailResponse.getClinicName(), mailResponse.getClinicAddress(),
					mailResponse.getMailRecordCreatedDate(), "Admit Card", "emrMailTemplate.vm");
			Boolean response = mailService.sendEmail(emailAddress,
					mailResponse.getDoctorName() + " sent you Admit Card", body, mailResponse.getMailAttachment());
			if (response != null && mailResponse.getMailAttachment() != null
					&& mailResponse.getMailAttachment().getFileSystemResource() != null)
				if (mailResponse.getMailAttachment().getFileSystemResource().getFile().exists())
					mailResponse.getMailAttachment().getFileSystemResource().getFile().delete();
		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}

	}

	@Override
	public void emailAdmitCardForWeb(String admitcardId, String doctorId, String locationId, String hospitalId,
			String emailAddress) {
		MailResponse mailResponse = null;
		AdmitCardCollection admitCardCollection = null;
		MailAttachment mailAttachment = null;
		UserCollection user = null;
		PatientCollection patient = null;
		EmailTrackCollection emailTrackCollection = new EmailTrackCollection();
		try {
			admitCardCollection = admitCardRepository.findById(new ObjectId(admitcardId)).orElse(null);
			if (admitCardCollection != null) {
				user = userRepository.findById(admitCardCollection.getPatientId()).orElse(null);
				patient = patientRepository.findByUserIdAndLocationIdAndHospitalId(admitCardCollection.getPatientId(),
						admitCardCollection.getLocationId(), admitCardCollection.getHospitalId());
				user.setFirstName(patient.getLocalPatientName());
				emailTrackCollection.setDoctorId(admitCardCollection.getDoctorId());
				emailTrackCollection.setHospitalId(admitCardCollection.getHospitalId());
				emailTrackCollection.setLocationId(admitCardCollection.getLocationId());
				emailTrackCollection.setType(ComponentType.ADMIT_CARD.getType());
				emailTrackCollection.setSubject("ADMIT CARD");
				if (user != null) {
					emailTrackCollection.setPatientName(patient.getLocalPatientName());
					emailTrackCollection.setPatientId(user.getId());
				}

				JasperReportResponse jasperReportResponse = createJasper(admitCardCollection, patient, user,
						PrintSettingType.EMAIL.getType());
				mailAttachment = new MailAttachment();
				mailAttachment.setAttachmentName(FilenameUtils.getName(jasperReportResponse.getPath()));
				mailAttachment.setFileSystemResource(jasperReportResponse.getFileSystemResource());
				UserCollection doctorUser = userRepository.findById(admitCardCollection.getDoctorId()).orElse(null);
				LocationCollection locationCollection = locationRepository.findById(admitCardCollection.getLocationId())
						.orElse(null);

				mailResponse = new MailResponse();
				mailResponse.setMailAttachment(mailAttachment);
				mailResponse.setDoctorName(doctorUser.getTitle() + " " + doctorUser.getFirstName());
				String address = (!DPDoctorUtils.anyStringEmpty(locationCollection.getStreetAddress())
						? locationCollection.getStreetAddress() + ", "
						: "")
						+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getLandmarkDetails())
								? locationCollection.getLandmarkDetails() + ", "
								: "")
						+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getLocality())
								? locationCollection.getLocality() + ", "
								: "")
						+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getCity())
								? locationCollection.getCity() + ", "
								: "")
						+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getState())
								? locationCollection.getState() + ", "
								: "")
						+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getCountry())
								? locationCollection.getCountry() + ", "
								: "")
						+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getPostalCode())
								? locationCollection.getPostalCode()
								: "");

				if (address.charAt(address.length() - 2) == ',') {
					address = address.substring(0, address.length() - 2);
				}
				mailResponse.setClinicAddress(address);
				mailResponse.setClinicName(locationCollection.getLocationName());
				SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
				sdf.setTimeZone(TimeZone.getTimeZone("IST"));
				mailResponse.setMailRecordCreatedDate(sdf.format(admitCardCollection.getCreatedTime()));
				mailResponse.setPatientName(user.getFirstName());
				emailTackService.saveEmailTrack(emailTrackCollection);

			} else {
				logger.warn("Discharge Summary  not found.Please check Admit Card Id.");
				throw new BusinessException(ServiceError.NoRecord,
						"Discharge Summary not found.Please check Admit Card Id.");
			}

			String body = mailBodyGenerator.generateEMREmailBody(mailResponse.getPatientName(),
					mailResponse.getDoctorName(), mailResponse.getClinicName(), mailResponse.getClinicAddress(),
					mailResponse.getMailRecordCreatedDate(), "Admit Card", "emrMailTemplate.vm");
			Boolean response = mailService.sendEmail(emailAddress,
					mailResponse.getDoctorName() + " sent you Admit Card", body, mailResponse.getMailAttachment());
			if (response != null && mailResponse.getMailAttachment() != null
					&& mailResponse.getMailAttachment().getFileSystemResource() != null)
				if (mailResponse.getMailAttachment().getFileSystemResource().getFile().exists())
					mailResponse.getMailAttachment().getFileSystemResource().getFile().delete();
		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}

	}
}
