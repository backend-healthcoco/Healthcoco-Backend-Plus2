package com.dpdocter.services.impl;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import com.dpdocter.beans.GenericCode;
import com.dpdocter.beans.MailAttachment;
import com.dpdocter.beans.Patient;
import com.dpdocter.beans.PrescriptionItem;
import com.dpdocter.beans.PrescriptionJasperDetails;
import com.dpdocter.collections.AdmitCardCollection;
import com.dpdocter.collections.DischargeSummaryCollection;
import com.dpdocter.collections.DrugCollection;
import com.dpdocter.collections.EmailTrackCollection;
import com.dpdocter.collections.LocationCollection;
import com.dpdocter.collections.PatientCollection;
import com.dpdocter.collections.PrescriptionCollection;
import com.dpdocter.collections.PrintSettingsCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.enums.ComponentType;
import com.dpdocter.enums.LineSpace;
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
import com.dpdocter.response.AdmitCardResponse;
import com.dpdocter.response.JasperReportResponse;
import com.dpdocter.response.MailResponse;
import com.dpdocter.services.AdmitCardService;
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

	@Value(value = "${jasper.print.admitCard.a4.fileName}")
	private String admitCardReportA4FileName;

	@Override
	public AdmitCardResponse addEditAdmitcard(AdmitCardRequest request) {
		AdmitCardResponse response = null;
		try {
			Patient patientdetail = new Patient();
			UserCollection doctor = userRepository.findOne(new ObjectId(request.getDoctorId()));
			if (doctor == null) {
				throw new BusinessException(ServiceError.InvalidInput, "Invalid DoctorId");
			}
			PatientCollection patientCollection = patientRepository.findByUserIdDoctorIdLocationIdAndHospitalId(
					new ObjectId(request.getPatientId()), new ObjectId(request.getDoctorId()),
					new ObjectId(request.getLocationId()), new ObjectId(request.getHospitalId()));
			if (patientCollection == null) {
				throw new BusinessException(ServiceError.InvalidInput, "Invalid patient");
			}
			BeanUtil.map(patientCollection, patientdetail);

			AdmitCardCollection admitCardCollection = new AdmitCardCollection();

			BeanUtil.map(request, admitCardCollection);
			if (admitCardCollection.getId() == null) {
				admitCardCollection.setCreatedTime(new Date());
				admitCardCollection.setCreatedBy(doctor.getTitle() + " " + doctor.getFirstName());
				admitCardCollection.setUniqueEmrId(
						UniqueIdInitial.ADMIT_CARD.getInitial() + "-" + DPDoctorUtils.generateRandomId());
			} else {
				AdmitCardCollection oldAdmitCardCollection = admitCardRepository.findOne(admitCardCollection.getId());
				admitCardCollection.setCreatedTime(oldAdmitCardCollection.getCreatedTime());
				admitCardCollection.setCreatedBy(oldAdmitCardCollection.getCreatedBy());
				admitCardCollection.setUniqueEmrId(oldAdmitCardCollection.getUniqueEmrId());
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
			AdmitCardCollection admitCardCollection = admitCardRepository.findOne(new ObjectId(cardId));

			if (admitCardCollection == null) {
				throw new BusinessException(ServiceError.InvalidInput, "Invalid Id");
			}
			PatientCollection patientCollection = patientRepository.findByUserIdDoctorIdLocationIdAndHospitalId(
					admitCardCollection.getPatientId(), admitCardCollection.getDoctorId(),
					admitCardCollection.getLocationId(), admitCardCollection.getHospitalId());
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
			String patientId, int page, int size, long updatedTime, Boolean discarded) {
		List<AdmitCardResponse> response = null;
		try {
			Criteria criteria = new Criteria();
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
				patientCollection = patientRepository.findByUserIdDoctorIdLocationIdAndHospitalId(
						new ObjectId(admitCardResponse.getPatientId()), new ObjectId(admitCardResponse.getDoctorId()),
						new ObjectId(admitCardResponse.getLocationId()),
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
	public AdmitCardResponse deleteAdmitCard(String cardId, String doctorId, String hospitalId, String locationId,
			Boolean discarded) {

		AdmitCardResponse response = null;
		try {
			AdmitCardCollection admitCardCollection = admitCardRepository.findOne(new ObjectId(cardId));
			if (admitCardCollection != null) {
				if (!DPDoctorUtils.anyStringEmpty(admitCardCollection.getDoctorId(),
						admitCardCollection.getHospitalId(), admitCardCollection.getLocationId())) {
					if (admitCardCollection.getDoctorId().toString().equals(doctorId)
							&& admitCardCollection.getHospitalId().toString().equals(hospitalId)
							&& admitCardCollection.getLocationId().toString().equals(locationId)) {
						admitCardCollection.setDiscarded(discarded);
						admitCardCollection.setUpdatedTime(new Date());
						admitCardRepository.save(admitCardCollection);
						response = new AdmitCardResponse();
						BeanUtil.map(admitCardCollection, response);
						PatientCollection patientCollection = patientRepository
								.findByUserIdDoctorIdLocationIdAndHospitalId(admitCardCollection.getPatientId(),
										admitCardCollection.getDoctorId(), admitCardCollection.getLocationId(),
										admitCardCollection.getHospitalId());
						Patient patient = new Patient();
						BeanUtil.map(patientCollection, patient);
						response.setPatient(patient);

					} else {
						logger.warn("Invalid Doctor Id, Hospital Id, Or Location Id");
						throw new BusinessException(ServiceError.InvalidInput,
								"Invalid Doctor Id, Hospital Id, Or Location Id");
					}
				}
			} else {
				logger.warn("Discharge Summary not found!");
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
			AdmitCardCollection admitCardCollection = admitCardRepository.findOne(new ObjectId(admitCardId));
			if (admitCardCollection != null) {
				PatientCollection patient = patientRepository.findByUserIdLocationIdAndHospitalId(
						admitCardCollection.getPatientId(), admitCardCollection.getLocationId(),
						admitCardCollection.getHospitalId());

				UserCollection user = userRepository.findOne(admitCardCollection.getPatientId());
				JasperReportResponse jasperReportResponse = createJasper(admitCardCollection, patient, user);
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
			throw new BusinessException(ServiceError.Unknown, "Exception in download Discharge Summary ");
		}
		return response;
	}

	private JasperReportResponse createJasper(AdmitCardCollection admitCardCollection, PatientCollection patient,
			UserCollection user) throws NumberFormatException, IOException, ParseException {
		JasperReportResponse response = null;
		Map<String, Object> parameters = new HashMap<String, Object>();
		String pattern = "dd/MM/yyyy";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		Boolean show = false;

		PrintSettingsCollection printSettings = printSettingsRepository.getSettings(admitCardCollection.getDoctorId(),
				admitCardCollection.getLocationId(), admitCardCollection.getHospitalId(), ComponentType.ALL.getType());

		if (printSettings == null) {
			printSettings = new PrintSettingsCollection();
			DefaultPrintSettings defaultPrintSettings = new DefaultPrintSettings();
			BeanUtil.map(defaultPrintSettings, printSettings);
		}

		if (admitCardCollection.getAdmissionDate() != null) {
			show = true;
			parameters.put("dOA",
					"<b>Date of Admission:-</b>" + simpleDateFormat.format(admitCardCollection.getAdmissionDate()));
		}
		parameters.put("showDOA", show);
		show = false;
		if (admitCardCollection.getDischargeDate() != null) {
			show = true;
			parameters.put("dOD",
					"<b>Date of Discharge:-</b>" + simpleDateFormat.format(admitCardCollection.getDischargeDate()));
		}
		parameters.put("showDOD", show);
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

		parameters.put("contentLineSpace",
				(printSettings != null && !DPDoctorUtils.anyStringEmpty(printSettings.getContentLineStyle()))
						? printSettings.getContentLineSpace() : LineSpace.SMALL.name());
		patientVisitService.generatePatientDetails(
				(printSettings != null && printSettings.getHeaderSetup() != null
						? printSettings.getHeaderSetup().getPatientDetails() : null),
				patient,
				"<b>ADMIT-CARD-ID: </b>"
						+ (admitCardCollection.getUniqueEmrId() != null ? admitCardCollection.getUniqueEmrId() : "--"),
				patient.getLocalPatientName(), user.getMobileNumber(), parameters,admitCardCollection.getUpdatedTime());
		patientVisitService.generatePrintSetup(parameters, printSettings, admitCardCollection.getDoctorId());
		String pdfName = (user != null ? user.getFirstName() : "") + "ADMIT-CARD-"
				+ admitCardCollection.getUniqueEmrId() + new Date().getTime();

		String layout = printSettings != null
				? (printSettings.getPageSetup() != null ? printSettings.getPageSetup().getLayout() : "PORTRAIT")
				: "PORTRAIT";
		String pageSize = printSettings != null
				? (printSettings.getPageSetup() != null ? printSettings.getPageSetup().getPageSize() : "A4") : "A4";
		Integer topMargin = printSettings != null
				? (printSettings.getPageSetup() != null ? printSettings.getPageSetup().getTopMargin() : 20) : 20;
		Integer bottonMargin = printSettings != null
				? (printSettings.getPageSetup() != null ? printSettings.getPageSetup().getBottomMargin() : 20) : 20;
		Integer leftMargin = printSettings != null
				? (printSettings.getPageSetup() != null && printSettings.getPageSetup().getLeftMargin() != 20
						? printSettings.getPageSetup().getLeftMargin() : 20)
				: 20;
		Integer rightMargin = printSettings != null
				? (printSettings.getPageSetup() != null && printSettings.getPageSetup().getRightMargin() != null
						? printSettings.getPageSetup().getRightMargin() : 20)
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
			admitCardCollection = admitCardRepository.findOne(new ObjectId(admitcardId));
			if (admitCardCollection != null) {
				if (admitCardCollection.getDoctorId() != null && admitCardCollection.getHospitalId() != null
						&& admitCardCollection.getLocationId() != null) {
					if (admitCardCollection.getDoctorId().equals(doctorId)
							&& admitCardCollection.getHospitalId().equals(hospitalId)
							&& admitCardCollection.getLocationId().equals(locationId)) {

						user = userRepository.findOne(admitCardCollection.getPatientId());
						patient = patientRepository.findByUserIdLocationIdAndHospitalId(
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

						JasperReportResponse jasperReportResponse = createJasper(admitCardCollection, patient, user);
						mailAttachment = new MailAttachment();
						mailAttachment.setAttachmentName(FilenameUtils.getName(jasperReportResponse.getPath()));
						mailAttachment.setFileSystemResource(jasperReportResponse.getFileSystemResource());
						UserCollection doctorUser = userRepository.findOne(new ObjectId(doctorId));
						LocationCollection locationCollection = locationRepository.findOne(new ObjectId(locationId));

						mailResponse = new MailResponse();
						mailResponse.setMailAttachment(mailAttachment);
						mailResponse.setDoctorName(doctorUser.getTitle() + " " + doctorUser.getFirstName());
						String address = (!DPDoctorUtils.anyStringEmpty(locationCollection.getStreetAddress())
								? locationCollection.getStreetAddress() + ", " : "")
								+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getLandmarkDetails())
										? locationCollection.getLandmarkDetails() + ", " : "")
								+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getLocality())
										? locationCollection.getLocality() + ", " : "")
								+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getCity())
										? locationCollection.getCity() + ", " : "")
								+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getState())
										? locationCollection.getState() + ", " : "")
								+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getCountry())
										? locationCollection.getCountry() + ", " : "")
								+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getPostalCode())
										? locationCollection.getPostalCode() : "");

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
			mailService.sendEmail(emailAddress, mailResponse.getDoctorName() + " sent you Admit Card", body,
					mailResponse.getMailAttachment());
			if (mailResponse.getMailAttachment() != null
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
