package com.dpdocter.services.impl;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.function.BiFunction;

import javax.mail.MessagingException;

import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.Appointment;
import com.dpdocter.beans.BabyNote;
import com.dpdocter.beans.Cement;
import com.dpdocter.beans.ClinicalNotes;
import com.dpdocter.beans.DefaultPrintSettings;
import com.dpdocter.beans.Diagram;
import com.dpdocter.beans.FileDetails;
import com.dpdocter.beans.FlowSheet;
import com.dpdocter.beans.FlowSheetJasperBean;
import com.dpdocter.beans.GenericCode;
import com.dpdocter.beans.Implant;
import com.dpdocter.beans.LabourNote;
import com.dpdocter.beans.MailAttachment;
import com.dpdocter.beans.Medication;
import com.dpdocter.beans.MonitoringChart;
import com.dpdocter.beans.MonitoringChartJasperBean;
import com.dpdocter.beans.OperationNote;
import com.dpdocter.beans.PatientVisitLookupBean;
import com.dpdocter.beans.Prescription;
import com.dpdocter.beans.PrescriptionItem;
import com.dpdocter.beans.PrescriptionItemDetail;
import com.dpdocter.beans.PrescriptionJasperDetails;
import com.dpdocter.beans.TestAndRecordData;
import com.dpdocter.collections.BabyNoteCollection;
import com.dpdocter.collections.CementCollection;
import com.dpdocter.collections.DiagnosticTestCollection;
import com.dpdocter.collections.DiagramsCollection;
import com.dpdocter.collections.DischargeSummaryCollection;
import com.dpdocter.collections.DoctorCollection;
import com.dpdocter.collections.DrugCollection;
import com.dpdocter.collections.EmailTrackCollection;
import com.dpdocter.collections.FlowsheetCollection;
import com.dpdocter.collections.ImplantCollection;
import com.dpdocter.collections.LabourNoteCollection;
import com.dpdocter.collections.LocationCollection;
import com.dpdocter.collections.OperationNoteCollection;
import com.dpdocter.collections.PatientCollection;
import com.dpdocter.collections.PatientVisitCollection;
import com.dpdocter.collections.PrescriptionCollection;
import com.dpdocter.collections.PrintSettingsCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.enums.ComponentType;
import com.dpdocter.enums.DischargeSummaryItem;
import com.dpdocter.enums.FieldAlign;
import com.dpdocter.enums.LineSpace;
import com.dpdocter.enums.LineStyle;
import com.dpdocter.enums.PrintSettingType;
import com.dpdocter.enums.Range;
import com.dpdocter.enums.UniqueIdInitial;
import com.dpdocter.enums.VisitedFor;
import com.dpdocter.enums.VitalSignsUnit;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.BabyNoteRepository;
import com.dpdocter.repository.CementRepository;
import com.dpdocter.repository.DiagramsRepository;
import com.dpdocter.repository.DischargeSummaryRepository;
import com.dpdocter.repository.DoctorRepository;
import com.dpdocter.repository.DrugRepository;
import com.dpdocter.repository.FlowsheetRepository;
import com.dpdocter.repository.ImplantRepository;
import com.dpdocter.repository.LabourNoteRepository;
import com.dpdocter.repository.LocationRepository;
import com.dpdocter.repository.OperationNoteRepository;
import com.dpdocter.repository.PatientRepository;
import com.dpdocter.repository.PrescriptionRepository;
import com.dpdocter.repository.PrintSettingsRepository;
import com.dpdocter.repository.SpecialityRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.request.AddEditFlowSheetRequest;
import com.dpdocter.request.AppointmentRequest;
import com.dpdocter.request.DischargeSummaryRequest;
import com.dpdocter.request.DoctorLabReportUploadRequest;
import com.dpdocter.request.PrescriptionAddEditRequest;
import com.dpdocter.response.DischargeSummaryResponse;
import com.dpdocter.response.FlowsheetResponse;
import com.dpdocter.response.ImageURLResponse;
import com.dpdocter.response.JasperReportResponse;
import com.dpdocter.response.MailResponse;
import com.dpdocter.response.PrescriptionAddEditResponseDetails;
import com.dpdocter.services.AppointmentService;
import com.dpdocter.services.ClinicalNotesService;
import com.dpdocter.services.DischargeSummaryService;
import com.dpdocter.services.EmailTackService;
import com.dpdocter.services.FileManager;
import com.dpdocter.services.JasperReportService;
import com.dpdocter.services.MailBodyGenerator;
import com.dpdocter.services.MailService;
import com.dpdocter.services.PatientVisitService;
import com.dpdocter.services.PrescriptionServices;
import com.dpdocter.services.PushNotificationServices;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataBodyPart;

import common.util.web.DPDoctorUtils;

@Service
public class DischargeSummaryServiceImpl implements DischargeSummaryService {

	private static Logger logger = Logger.getLogger(DischargeSummaryServiceImpl.class.getName());

	@Autowired
	private DischargeSummaryRepository dischargeSummaryRepository;

	@Autowired
	private ClinicalNotesService clinicalNotesService;

	@Autowired
	private AppointmentService appointmentService;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private PrintSettingsRepository printSettingsRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private SpecialityRepository specialityRepository;

	@Autowired
	private PrescriptionRepository prescriptionRepository;

	@Autowired
	private CementRepository cementRepository;
	@Autowired
	private EmailTackService emailTackService;

	@Autowired
	private MailService mailService;

	@Autowired
	private DrugRepository drugRepository;

	@Autowired
	private DoctorRepository doctorRepository;

	@Autowired
	private LocationRepository locationRepository;

	@Autowired
	private LabourNoteRepository labourNoteRepository;

	@Autowired
	private BabyNoteRepository babyNoteRepository;

	@Autowired
	private OperationNoteRepository operationNoteRepository;

	@Autowired
	private ImplantRepository implantRepository;

	@Autowired
	private MailBodyGenerator mailBodyGenerator;

	@Autowired
	private PrescriptionServices prescriptionServices;

	@Autowired
	private JasperReportService jasperReportService;

	@Autowired
	private PatientRepository patientRepository;

	@Autowired
	private PatientVisitService patientVisitService;

	@Autowired
	private FlowsheetRepository flowsheetRepository;

	@Autowired
	private FileManager fileManager;

	@Autowired
	private DiagramsRepository diagramsRepository;

	@Value(value = "${jasper.print.dischargeSummary.a4.fileName}")
	private String dischargeSummaryReportA4FileName;

	@Value(value = "${image.path}")
	private String imagePath;

	@Autowired
	PushNotificationServices pushNotificationServices;

	@Transactional
	@Override
	public DischargeSummaryResponse addEditDischargeSummary(DischargeSummaryRequest dischargeSummary) {

		DischargeSummaryResponse response = null;
		DischargeSummaryCollection oldDischargeSummaryCollection = null;
		try {

			Appointment appointment = null;
			Prescription prescription = null;
			DischargeSummaryCollection dischargeSummaryCollection = null;
			PrescriptionAddEditResponseDetails addEditResponseDetails = null;
			UserCollection doctor = userRepository.findById(new ObjectId(dischargeSummary.getDoctorId())).orElse(null);
			dischargeSummaryCollection = new DischargeSummaryCollection();
			if (dischargeSummary.getId() == null) {
				if (dischargeSummary.getCreatedTime() == null)
					dischargeSummary.setCreatedTime(new Date());
				dischargeSummary.setAdminCreatedTime(new Date());
				dischargeSummary.setCreatedBy(doctor.getFirstName());
				dischargeSummary.setUniqueEmrId(
						UniqueIdInitial.DISCHARGE_SUMMARY.getInitial() + "-" + DPDoctorUtils.generateRandomId());
			}

			BeanUtil.map(dischargeSummary, dischargeSummaryCollection);
			if (dischargeSummary.getDiagrams() != null && !dischargeSummary.getDiagrams().isEmpty()) {
				dischargeSummaryCollection.setDiagrams(new ArrayList<String>());
				for (String img : dischargeSummary.getDiagrams()) {
					img = img.replaceAll(imagePath, "");
					dischargeSummaryCollection.getDiagrams().add(img);
				}

			}
			if (!DPDoctorUtils.anyStringEmpty(dischargeSummary.getId())) {
				oldDischargeSummaryCollection = dischargeSummaryRepository
						.findById(new ObjectId(dischargeSummary.getId())).orElse(null);

				if (DPDoctorUtils.anyStringEmpty(oldDischargeSummaryCollection.getUniqueEmrId())) {
					oldDischargeSummaryCollection.setUniqueEmrId(
							UniqueIdInitial.DISCHARGE_SUMMARY.getInitial() + "-" + DPDoctorUtils.generateRandomId());
				}

				if (dischargeSummaryCollection.getCreatedTime() == null) {
					dischargeSummaryCollection.setCreatedTime(oldDischargeSummaryCollection.getCreatedTime());
				}
				dischargeSummaryCollection.setCreatedBy(oldDischargeSummaryCollection.getCreatedBy());
				dischargeSummaryCollection.setAdminCreatedTime(oldDischargeSummaryCollection.getAdminCreatedTime());
				dischargeSummaryCollection.setDiscarded(oldDischargeSummaryCollection.getDiscarded());
				dischargeSummaryCollection.setUniqueEmrId(oldDischargeSummaryCollection.getUniqueEmrId());
				dischargeSummaryCollection.setIsPatientDiscarded(oldDischargeSummaryCollection.getIsPatientDiscarded());
				dischargeSummaryCollection.setUpdatedTime(new Date());
			}
			if (dischargeSummary.getPrescriptions() != null) {
				PrescriptionAddEditRequest request = new PrescriptionAddEditRequest();
				BeanUtil.map(dischargeSummary.getPrescriptions(), request);
				request.setHospitalId(dischargeSummary.getHospitalId());
				request.setLocationId(dischargeSummary.getLocationId());
				request.setDoctorId(dischargeSummary.getDoctorId());
				request.setPatientId(dischargeSummary.getPatientId());
				prescription = new Prescription();
				if (DPDoctorUtils.anyStringEmpty(request.getId())) {
					addEditResponseDetails = prescriptionServices.addPrescriptionHandheld(request);
					if (addEditResponseDetails != null) {
						String visitId = patientVisitService.addRecord(addEditResponseDetails, VisitedFor.PRESCRIPTION,
								addEditResponseDetails.getVisitId());
						addEditResponseDetails.setVisitId(visitId);
					}
				} else {
					addEditResponseDetails = prescriptionServices.editPrescription(request);
					if (addEditResponseDetails != null) {
						String visitId = patientVisitService.editRecord(addEditResponseDetails.getId(),
								VisitedFor.PRESCRIPTION);
						addEditResponseDetails.setVisitId(visitId);
					}

				}
				BeanUtil.map(addEditResponseDetails, prescription);

				dischargeSummaryCollection.setPrescriptionId(new ObjectId(addEditResponseDetails.getId()));
			}
			if (dischargeSummary.getAppointmentRequest() != null) {
				appointment = addDischageSummaryAppointment(dischargeSummary.getAppointmentRequest());
			}
			if (appointment != null) {

				dischargeSummaryCollection.setFromDate(appointment.getFromDate());
				dischargeSummaryCollection.setTime(appointment.getTime());
				dischargeSummaryCollection.setAppointmentId(appointment.getAppointmentId());

			}
			dischargeSummaryCollection.setFlowSheets(dischargeSummary.getFlowSheets());
			dischargeSummaryCollection.setMonitoringChart(dischargeSummary.getMonitoringChart());
			dischargeSummaryCollection = dischargeSummaryRepository.save(dischargeSummaryCollection);

			if (dischargeSummary.getFlowSheets() != null) {
				AddEditFlowSheetRequest addEditFlowSheetRequest = new AddEditFlowSheetRequest();
				addEditFlowSheetRequest.setDoctorId(dischargeSummary.getDoctorId());
				addEditFlowSheetRequest.setLocationId(dischargeSummary.getLocationId());
				addEditFlowSheetRequest.setHospitalId(dischargeSummary.getHospitalId());
				addEditFlowSheetRequest.setPatientId(dischargeSummary.getPatientId());
				addEditFlowSheetRequest.setFlowSheets(dischargeSummary.getFlowSheets());
				addEditFlowSheetRequest.setMonitoringChart(dischargeSummary.getMonitoringChart());
				addEditFlowSheetRequest.setDischargeSummaryId(dischargeSummaryCollection.getId().toString());
				addEditFlowSheets(addEditFlowSheetRequest);
			}
			response = new DischargeSummaryResponse();
			BeanUtil.map(dischargeSummaryCollection, response);
			response.setPrescriptions(prescription);
			if (dischargeSummaryCollection.getDiagrams() != null
					&& !dischargeSummaryCollection.getDiagrams().isEmpty()) {
				response.setDiagrams(new ArrayList<String>());
				for (String img : dischargeSummary.getDiagrams()) {
					img = getFinalImageURL(img);
					response.getDiagrams().add(img);
				}

			}
			pushNotificationServices.notifyUser(dischargeSummary.getDoctorId(), "Discharge Summary Added",
					ComponentType.DISCHARGE_SUMMARY_REFRESH.getType(), response.getPatientId(), null);

		} catch (

		Exception e) {
			e.printStackTrace();
			logger.error(e + " Error while adding  discharge summary : " + e.getCause().getMessage());
			throw new BusinessException(ServiceError.Unknown,
					"Error while adding discharge summary : " + e.getCause().getMessage());

		}
		return response;
	}

	/*
	 * @Transactional
	 * 
	 * @Override public List<DischargeSummary> getAllDischargeSummary() {
	 * List<DischargeSummary> response = null; DischargeSummary dischargeSummary =
	 * null; List<DischargeSummaryCollection> dischargeSummaryCollections = null;
	 * 
	 * dischargeSummaryCollections = dischargeSummaryRepository.findAll(); for
	 * (DischargeSummaryCollection dischargeSummaryCollection :
	 * dischargeSummaryCollections) { dischargeSummary = new DischargeSummary();
	 * BeanUtil.map(dischargeSummaryCollection, dischargeSummary);
	 * response.add(dischargeSummary); }
	 * 
	 * return response; }
	 */

	@Override
	@Transactional
	public List<DischargeSummaryResponse> getDischargeSummary(String doctorId, String locationId, String hospitalId,
			String patientId, long page, int size, String updatedTime) {
		List<DischargeSummaryResponse> response = null;
		try {
			DischargeSummaryResponse summaryResponse = null;
			ObjectId patientObjectId = null, doctorObjectId = null, locationObjectId = null, hospitalObjectId = null;
			if (!DPDoctorUtils.anyStringEmpty(patientId))
				patientObjectId = new ObjectId(patientId);
			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				doctorObjectId = new ObjectId(doctorId);
			if (!DPDoctorUtils.anyStringEmpty(locationId))
				locationObjectId = new ObjectId(locationId);
			if (!DPDoctorUtils.anyStringEmpty(hospitalId))
				hospitalObjectId = new ObjectId(hospitalId);

			Criteria criteria = new Criteria("updatedTime").gt(new Date(Long.parseLong(updatedTime))).and("patientId")
					.is(patientObjectId).and("isPatientDiscarded").ne(true);
			if (!DPDoctorUtils.anyStringEmpty(locationId))
				criteria.and("locationId").is(locationObjectId);
			if (!DPDoctorUtils.anyStringEmpty(hospitalId))
				criteria.and("hospitalId").is(hospitalObjectId);
			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				criteria.and("doctorId").is(doctorObjectId);

			Aggregation aggregation = null;

			if (size > 0)
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")), Aggregation.skip((page) * size),
						Aggregation.limit(size));
			else
				aggregation = Aggregation.newAggregation(

						Aggregation.match(criteria), Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));

			AggregationResults<DischargeSummaryCollection> aggregationResults = mongoTemplate.aggregate(aggregation,
					DischargeSummaryCollection.class, DischargeSummaryCollection.class);
			List<DischargeSummaryCollection> dischargeSummaryCollections = aggregationResults.getMappedResults();
			response = new ArrayList<DischargeSummaryResponse>();
			for (DischargeSummaryCollection dischargeSummaryCollection : dischargeSummaryCollections) {
				summaryResponse = new DischargeSummaryResponse();
				BeanUtil.map(dischargeSummaryCollection, summaryResponse);

				if (!DPDoctorUtils.anyStringEmpty(dischargeSummaryCollection.getPrescriptionId())) {
					summaryResponse.setPrescriptions(prescriptionServices
							.getPrescriptionById(dischargeSummaryCollection.getPrescriptionId().toString()));
				}
				if (dischargeSummaryCollection.getDiagrams() != null
						&& !dischargeSummaryCollection.getDiagrams().isEmpty()) {
					summaryResponse.setDiagrams(new ArrayList<String>());
					for (String img : dischargeSummaryCollection.getDiagrams()) {
						img = getFinalImageURL(img);
						summaryResponse.getDiagrams().add(img);
					}

				}
				response.add(summaryResponse);

			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error while getting discharge summary : " + e.getCause().getMessage());
			throw new BusinessException(ServiceError.Unknown,
					"Error while getting discharge summary : " + e.getCause().getMessage());
		}
		return response;
	}

	@Override
	public DischargeSummaryResponse viewDischargeSummary(String dischargeSummeryId) {
		DischargeSummaryResponse response = null;
		try {
			DischargeSummaryCollection dischargeSummaryCollection = dischargeSummaryRepository
					.findById(new ObjectId(dischargeSummeryId)).orElse(null);
			if (dischargeSummaryCollection != null) {
				response = new DischargeSummaryResponse();
				BeanUtil.map(dischargeSummaryCollection, response);
				Prescription prescription = null;

				if (!DPDoctorUtils.anyStringEmpty(dischargeSummaryCollection.getPrescriptionId())) {
					prescription = prescriptionServices
							.getPrescriptionById(dischargeSummaryCollection.getPrescriptionId().toString());

					if (prescription.getItems() == null) {
						prescription.setItems(new ArrayList<PrescriptionItemDetail>());
					}
				} else {
					prescription = new Prescription();
					if (prescription.getItems() == null) {
						prescription.setItems(new ArrayList<PrescriptionItemDetail>());
					}
				}
				if (dischargeSummaryCollection.getDiagrams() != null
						&& !dischargeSummaryCollection.getDiagrams().isEmpty()) {
					response.setDiagrams(new ArrayList<String>());
					for (String img : dischargeSummaryCollection.getDiagrams()) {
						img = getFinalImageURL(img);
						response.getDiagrams().add(img);
					}

				}
				response.setPrescriptions(prescription);

			} else {
				throw new BusinessException(ServiceError.InvalidInput, "Invalid discharge summaryId ");

			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error while view discharge summary : " + e.getCause().getMessage());
			throw new BusinessException(ServiceError.Unknown,
					"Error while view discharge summary : " + e.getCause().getMessage());
		}

		return response;
	}

	@Override
	public int getDischargeSummaryCount(ObjectId doctorObjectId, ObjectId patientObjectId, ObjectId locationObjectId,
			ObjectId hospitalObjectId, boolean isOTPVerified) {
		int response = 0;
		try {
			if (isOTPVerified)
				response = dischargeSummaryRepository.countByPatientId(patientObjectId);
			else
				response = dischargeSummaryRepository.countByPatientIdDoctorLocationHospital(patientObjectId,
						doctorObjectId, locationObjectId, hospitalObjectId);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error while count discharge summary : " + e.getCause().getMessage());
			throw new BusinessException(ServiceError.Unknown,
					"Error while count discharge summary : " + e.getCause().getMessage());
		}

		return response;
	}

	@Override
	public DischargeSummaryResponse deleteDischargeSummary(String dischargeSummeryId, String doctorId,
			String hospitalId, String locationId, Boolean discarded) {
		DischargeSummaryResponse response = null;
		try {
			DischargeSummaryCollection dischargeSummaryCollection = dischargeSummaryRepository
					.findById(new ObjectId(dischargeSummeryId)).orElse(null);
			if (dischargeSummaryCollection != null) {
				if (!DPDoctorUtils.anyStringEmpty(dischargeSummaryCollection.getDoctorId(),
						dischargeSummaryCollection.getHospitalId(), dischargeSummaryCollection.getLocationId())) {
					if (dischargeSummaryCollection.getDoctorId().toString().equals(doctorId)
							&& dischargeSummaryCollection.getHospitalId().toString().equals(hospitalId)
							&& dischargeSummaryCollection.getLocationId().toString().equals(locationId)) {
						dischargeSummaryCollection.setDiscarded(discarded);
						dischargeSummaryCollection.setUpdatedTime(new Date());
						dischargeSummaryRepository.save(dischargeSummaryCollection);
						response = new DischargeSummaryResponse();
						BeanUtil.map(dischargeSummaryCollection, response);
						if (!DPDoctorUtils.allStringsEmpty(dischargeSummaryCollection.getPrescriptionId())) {
							prescriptionServices
									.getPrescriptionById(dischargeSummaryCollection.getPrescriptionId().toString());
						}

						if (dischargeSummaryCollection.getDiagrams() != null
								&& !dischargeSummaryCollection.getDiagrams().isEmpty()) {
							response.setDiagrams(new ArrayList<String>());
							for (String img : response.getDiagrams()) {
								img = getFinalImageURL(img);
								response.getDiagrams().add(img);
							}

						}
					} else {
						logger.warn("Invalid Doctor Id, Hospital Id, Or Location Id");
						throw new BusinessException(ServiceError.InvalidInput,
								"Invalid Doctor Id, Hospital Id, Or Location Id");
					}
				}
			} else {
				logger.warn("Discharge Summary not found!");
				throw new BusinessException(ServiceError.NoRecord, "Discharge Summary not found!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	public String downloadDischargeSummary(String dischargeSummeryId) {
		String response = null;

		try {
			DischargeSummaryCollection dischargeSummaryCollection = dischargeSummaryRepository
					.findById(new ObjectId(dischargeSummeryId)).orElse(null);
			if (dischargeSummaryCollection != null) {
				PatientCollection patient = patientRepository.findByUserIdAndLocationIdAndHospitalId(
						dischargeSummaryCollection.getPatientId(), dischargeSummaryCollection.getLocationId(),
						dischargeSummaryCollection.getHospitalId());

				UserCollection user = userRepository.findById(dischargeSummaryCollection.getPatientId()).orElse(null);
				JasperReportResponse jasperReportResponse = null;

				jasperReportResponse = createJasper(dischargeSummaryCollection, patient, user,
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
			throw new BusinessException(ServiceError.Unknown, "Exception in download Discharge Summary ");
		}
		return response;
	}

	private String getFinalImageURL(String imageURL) {
		if (imageURL != null) {
			return imagePath + imageURL;
		} else
			return null;
	}

	@Override
	public String downloadFlowSheet(String id, Boolean byFlowsheetId) {
		String response = null;

		try {

			FlowsheetCollection flowsheetCollection = null;
			if (byFlowsheetId) {
				flowsheetCollection = flowsheetRepository.findById(new ObjectId(id)).orElse(null);
			} else {
				flowsheetCollection = flowsheetRepository.findByDischargeSummaryId(new ObjectId(id));
			}

			if (flowsheetCollection != null) {
				PatientCollection patient = patientRepository.findByUserIdAndLocationIdAndHospitalId(
						flowsheetCollection.getPatientId(), flowsheetCollection.getLocationId(),
						flowsheetCollection.getHospitalId());

				UserCollection user = userRepository.findById(flowsheetCollection.getPatientId()).orElse(null);
				JasperReportResponse jasperReportResponse = null;
				jasperReportResponse = createJasperForFlowSheet(flowsheetCollection, patient, user,
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
			throw new BusinessException(ServiceError.Unknown, "Exception in download Flow Sheet" + e.getMessage());
		}
		return response;
	}

	private JasperReportResponse createJasper(DischargeSummaryCollection dischargeSummaryCollection,
			PatientCollection patient, UserCollection user, String printSettingType)
			throws NumberFormatException, IOException, ParseException {
		JasperReportResponse response = null;
		List<PrescriptionJasperDetails> prescriptionItems = null;
		Map<String, Object> parameters = new HashMap<String, Object>();
		String pattern = "dd/MM/yyyy";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		simpleDateFormat.setTimeZone(TimeZone.getTimeZone("IST"));
		SimpleDateFormat _12HourSDF = new SimpleDateFormat("hh:mm a");
		SimpleDateFormat sdf = new SimpleDateFormat("MMM dd");
		_12HourSDF.setTimeZone(TimeZone.getTimeZone("IST"));
		SimpleDateFormat _24HourSDF = new SimpleDateFormat("HH:mm");
		String _24HourTime = "";
		PrintSettingsCollection printSettings = null;
		printSettings = printSettingsRepository
				.findByDoctorIdAndLocationIdAndHospitalIdAndComponentTypeAndPrintSettingType(
						dischargeSummaryCollection.getDoctorId(), dischargeSummaryCollection.getLocationId(),
						dischargeSummaryCollection.getHospitalId(), ComponentType.ALL.getType(), printSettingType);
		String dateTime = "";
		if (printSettings == null) {
			List<PrintSettingsCollection> printSettingsCollections = printSettingsRepository
					.findListByDoctorIdAndLocationIdAndHospitalIdAndComponentTypeAndPrintSettingType(
							dischargeSummaryCollection.getDoctorId(), dischargeSummaryCollection.getLocationId(),
							dischargeSummaryCollection.getHospitalId(), ComponentType.ALL.getType(),
							PrintSettingType.DEFAULT.getType(), new Sort(Sort.Direction.DESC, "updatedTime"));
			if (!DPDoctorUtils.isNullOrEmptyList(printSettingsCollections))
				printSettings = printSettingsCollections.get(0);
		}
		if (printSettings == null) {
			printSettings = new PrintSettingsCollection();
			DefaultPrintSettings defaultPrintSettings = new DefaultPrintSettings();
			BeanUtil.map(defaultPrintSettings, printSettings);
		}
		if (dischargeSummaryCollection.getVitalSigns() != null) {
			String vitalSigns = "";

			String pulse = dischargeSummaryCollection.getVitalSigns().getPulse();
			pulse = (pulse != null && !pulse.isEmpty() ? "Pulse: " + pulse.trim() + " " + VitalSignsUnit.PULSE.getUnit()
					: "");
			if (!DPDoctorUtils.allStringsEmpty(pulse))
				vitalSigns = pulse;

			String temp = dischargeSummaryCollection.getVitalSigns().getTemperature();
			temp = (temp != null && !temp.isEmpty()
					? "Temperature: " + temp.trim() + " " + VitalSignsUnit.TEMPERATURE.getUnit()
					: "");
			if (!DPDoctorUtils.allStringsEmpty(temp)) {
				if (!DPDoctorUtils.allStringsEmpty(vitalSigns))
					vitalSigns = vitalSigns + ",  " + temp;
				else
					vitalSigns = temp;
			}

			String breathing = dischargeSummaryCollection.getVitalSigns().getBreathing();
			breathing = (breathing != null && !breathing.isEmpty()
					? "Breathing: " + breathing.trim() + " " + VitalSignsUnit.BREATHING.getUnit()
					: "");

			if (!DPDoctorUtils.allStringsEmpty(breathing)) {
				if (!DPDoctorUtils.allStringsEmpty(vitalSigns))
					vitalSigns = vitalSigns + ",  " + breathing;
				else
					vitalSigns = breathing;
			}

			String weight = dischargeSummaryCollection.getVitalSigns().getWeight();
			weight = (weight != null && !weight.isEmpty()
					? "Weight: " + weight.trim() + " " + VitalSignsUnit.WEIGHT.getUnit()
					: "");
			if (!DPDoctorUtils.allStringsEmpty(weight)) {
				if (!DPDoctorUtils.allStringsEmpty(vitalSigns))
					vitalSigns = vitalSigns + ",  " + weight;
				else
					vitalSigns = weight;
			}

			String bloodPressure = "";
			if (dischargeSummaryCollection.getVitalSigns().getBloodPressure() != null) {
				String systolic = dischargeSummaryCollection.getVitalSigns().getBloodPressure().getSystolic();
				systolic = systolic != null && !systolic.isEmpty() ? systolic.trim() : "";

				String diastolic = dischargeSummaryCollection.getVitalSigns().getBloodPressure().getDiastolic();
				diastolic = diastolic != null && !diastolic.isEmpty() ? diastolic.trim() : "";

				if (!DPDoctorUtils.anyStringEmpty(systolic, diastolic))
					bloodPressure = "B.P: " + systolic + "/" + diastolic + " " + VitalSignsUnit.BLOODPRESSURE.getUnit();
				if (!DPDoctorUtils.allStringsEmpty(bloodPressure)) {
					if (!DPDoctorUtils.allStringsEmpty(vitalSigns))
						vitalSigns = vitalSigns + ",  " + bloodPressure;
					else
						vitalSigns = bloodPressure;
				}
			}
			String spo2 = dischargeSummaryCollection.getVitalSigns().getSpo2();
			spo2 = (spo2 != null && !spo2.isEmpty() ? "SPO2: " + spo2 + " " + VitalSignsUnit.SPO2.getUnit() : "");
			if (!DPDoctorUtils.allStringsEmpty(spo2)) {
				if (!DPDoctorUtils.allStringsEmpty(vitalSigns))
					vitalSigns = vitalSigns + ",  " + spo2;
				else
					vitalSigns = spo2;
			}
			String height = dischargeSummaryCollection.getVitalSigns().getHeight();
			height = (height != null && !height.isEmpty() ? "Height: " + height + " " + VitalSignsUnit.HEIGHT.getUnit()
					: "");
			if (!DPDoctorUtils.allStringsEmpty(height)) {
				if (!DPDoctorUtils.allStringsEmpty(vitalSigns))
					vitalSigns = vitalSigns + ",  " + height;
				else
					vitalSigns = height;
			}

			String bmi = dischargeSummaryCollection.getVitalSigns().getBmi();
			if (!DPDoctorUtils.allStringsEmpty(bmi)) {
				if (bmi.equalsIgnoreCase("nan"))
					bmi = "";

			} else {
				bmi = "";
			}

			if (!DPDoctorUtils.allStringsEmpty(bmi)) {
				bmi = "Bmi: " + String.format("%.3f", Double.parseDouble(bmi));
				if (!DPDoctorUtils.allStringsEmpty(bmi))
					vitalSigns = vitalSigns + ",  " + bmi;
				else
					vitalSigns = bmi;
			}

			String bsa = dischargeSummaryCollection.getVitalSigns().getBsa();
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
			parameters.put("vitalSigns", vitalSigns);

		}
		if (!DPDoctorUtils.anyStringEmpty(dischargeSummaryCollection.getPrescriptionId())) {
			PrescriptionCollection prescription = prescriptionRepository
					.findById(dischargeSummaryCollection.getPrescriptionId()).orElse(null);
			int no = 0;
			Boolean showIntructions = false, showDirection = false;

			if (prescription.getItems() != null && !prescription.getItems().isEmpty())

				for (PrescriptionItem prescriptionItem : prescription.getItems()) {

					if (prescriptionItem != null && prescriptionItem.getDrugId() != null) {
						DrugCollection drug = drugRepository.findById(prescriptionItem.getDrugId()).orElse(null);
						if (drug != null) {
							String drugType = drug.getDrugType() != null
									? (drug.getDrugType().getType() != null ? drug.getDrugType().getType() + " " : "")
									: "";
							String genericName = "";
							if (printSettings.getShowDrugGenericNames() && drug.getGenericNames() != null
									&& !drug.getGenericNames().isEmpty()) {
								for (GenericCode genericCode : drug.getGenericNames()) {
									if (DPDoctorUtils.anyStringEmpty(genericName))
										genericName = genericCode.getName();
									else
										genericName = genericName + "+" + genericCode.getName();
								}
								genericName = "<br><font size='1'><i>" + genericName + "</i></font>";
							}
							String drugName = drug.getDrugName() != null ? drug.getDrugName() : "";
							drugName = (drugType + drugName) == "" ? "--" : drugType + " " + drugName + genericName;
							String durationValue = prescriptionItem.getDuration() != null
									? (prescriptionItem.getDuration().getValue() != null
											? prescriptionItem.getDuration().getValue()
											: "")
									: "";
							String durationUnit = prescriptionItem.getDuration() != null
									? (prescriptionItem.getDuration().getDurationUnit() != null
											? (!DPDoctorUtils.anyStringEmpty(
													prescriptionItem.getDuration().getDurationUnit().getUnit())
															? prescriptionItem.getDuration().getDurationUnit().getUnit()
															: "")
											: "")
									: "";

							String directions = "";
							if (prescriptionItem.getDirection() != null && !prescriptionItem.getDirection().isEmpty()) {
								showDirection = true;
								if (prescriptionItem.getDirection().get(0).getDirection() != null) {
									if (directions == "")
										directions = directions
												+ (prescriptionItem.getDirection().get(0).getDirection());
									else
										directions = directions + ","
												+ (prescriptionItem.getDirection().get(0).getDirection());
								}
							}
							if (!DPDoctorUtils.anyStringEmpty(prescriptionItem.getInstructions())) {
								if (printSettings.getContentSetup() != null) {
									if (printSettings.getContentSetup().getInstructionAlign() != null && printSettings
											.getContentSetup().getInstructionAlign().equals(FieldAlign.HORIZONTAL)) {
										prescriptionItem.setInstructions(
												!DPDoctorUtils.anyStringEmpty(prescriptionItem.getInstructions())
														? "<b>Instruction </b>: " + prescriptionItem.getInstructions()
														: null);
									} else {
										prescriptionItem.setInstructions(
												!DPDoctorUtils.anyStringEmpty(prescriptionItem.getInstructions())
														? prescriptionItem.getInstructions()
														: null);
									}
								} else {
									prescriptionItem.setInstructions(
											!DPDoctorUtils.anyStringEmpty(prescriptionItem.getInstructions())
													? prescriptionItem.getInstructions()
													: null);
								}

								showIntructions = true;
							}
							String duration = "";
							if (durationValue == "" && durationValue == "")
								duration = "--";
							else
								duration = durationValue + " " + durationUnit;
							no = no + 1;

							PrescriptionJasperDetails prescriptionJasperDetails = null;
							if (printSettings.getContentSetup() != null) {
								if (printSettings.getContentSetup().getInstructionAlign() != null && printSettings
										.getContentSetup().getInstructionAlign().equals(FieldAlign.HORIZONTAL)) {

									prescriptionJasperDetails = new PrescriptionJasperDetails(no, drugName,
											!DPDoctorUtils.anyStringEmpty(prescriptionItem.getDosage())
													? prescriptionItem.getDosage()
													: "--",
											duration, directions.isEmpty() ? "--" : directions,
											!DPDoctorUtils.anyStringEmpty(prescriptionItem.getInstructions())
													? prescriptionItem.getInstructions()
													: null,
											genericName);
								} else {
									prescriptionJasperDetails = new PrescriptionJasperDetails(no, drugName,
											!DPDoctorUtils.anyStringEmpty(prescriptionItem.getDosage())
													? prescriptionItem.getDosage()
													: "--",
											duration, directions.isEmpty() ? "--" : directions,
											!DPDoctorUtils.anyStringEmpty(prescriptionItem.getInstructions())
													? prescriptionItem.getInstructions()
													: "--",
											genericName);
								}
							} else {
								prescriptionJasperDetails = new PrescriptionJasperDetails(++no, drugName,
										!DPDoctorUtils.anyStringEmpty(prescriptionItem.getDosage())
												? prescriptionItem.getDosage()
												: "--",
										duration, directions.isEmpty() ? "--" : directions,
										!DPDoctorUtils.anyStringEmpty(prescriptionItem.getInstructions())
												? prescriptionItem.getInstructions()
												: "--",
										genericName);
							}
							if (prescriptionItems == null)
								prescriptionItems = new ArrayList<PrescriptionJasperDetails>();
							prescriptionItems.add(prescriptionJasperDetails);
						}
					}
				}

			parameters.put("prescriptionItems", prescriptionItems);
			parameters.put("showIntructions", showIntructions);
			parameters.put("showDirection", showDirection);
			if (!DPDoctorUtils.allStringsEmpty(prescription.getAdvice())) {
				parameters.put("advice", prescription.getAdvice());
			}
		}

		if (dischargeSummaryCollection.getAdmissionDate() != null) {
			parameters.put("dOA", simpleDateFormat.format(dischargeSummaryCollection.getAdmissionDate()));
		}
		if (dischargeSummaryCollection.getDischargeDate() != null) {
			parameters.put("dOD", simpleDateFormat.format(dischargeSummaryCollection.getDischargeDate()));
		}
		if (dischargeSummaryCollection.getOperationDate() != null) {
			parameters.put("operationDate", simpleDateFormat.format(dischargeSummaryCollection.getOperationDate()));
		}
		if (dischargeSummaryCollection.getSurgeryDate() != null) {
			parameters.put("surgeryDate", simpleDateFormat.format(dischargeSummaryCollection.getSurgeryDate()) + " "
					+ _12HourSDF.format(dischargeSummaryCollection.getSurgeryDate()));

		}
		if (!DPDoctorUtils.anyStringEmpty(dischargeSummaryCollection.getAgeOnAdmission())) {
			parameters.put("ageOnAdmission", dischargeSummaryCollection.getAgeOnAdmission());
		}
		if (!DPDoctorUtils.anyStringEmpty(dischargeSummaryCollection.getAgeOnDischarge())) {
			parameters.put("ageOnDischarge", dischargeSummaryCollection.getAgeOnDischarge());
		}
		if (!DPDoctorUtils.anyStringEmpty(dischargeSummaryCollection.getWeightOnAdmission())) {
			parameters.put("weightOnAdmission", dischargeSummaryCollection.getWeightOnAdmission());
		}
		if (!DPDoctorUtils.anyStringEmpty(dischargeSummaryCollection.getWeightOnDischarge())) {
			parameters.put("weightOnDischarge", dischargeSummaryCollection.getWeightOnDischarge());
		}

		if (!DPDoctorUtils.anyStringEmpty(dischargeSummaryCollection.getTimeOfAdmission())) {
			SimpleDateFormat sdfForMins = new SimpleDateFormat("mm");
			Date dt = sdfForMins.parse(dischargeSummaryCollection.getTimeOfAdmission());
			sdfForMins = new SimpleDateFormat("hh:mm a");

			parameters.put("timeOfAdmission", sdfForMins.format(dt));
		}

		if (!DPDoctorUtils.anyStringEmpty(dischargeSummaryCollection.getTimeOfDischarge())) {
			SimpleDateFormat sdfForMins = new SimpleDateFormat("mm");
			Date dt = sdfForMins.parse(dischargeSummaryCollection.getTimeOfDischarge());
			sdfForMins = new SimpleDateFormat("hh:mm a");
			parameters.put("timeOfDischarge", sdfForMins.format(dt));
		}

		if (!DPDoctorUtils.anyStringEmpty(dischargeSummaryCollection.getTimeOfOperation())) {
			SimpleDateFormat sdfForMins = new SimpleDateFormat("mm");
			Date dt = sdfForMins.parse(dischargeSummaryCollection.getTimeOfOperation());
			sdfForMins = new SimpleDateFormat("hh:mm a");
			parameters.put("timeOfOperation", sdfForMins.format(dt));
		}

		if (!DPDoctorUtils.anyStringEmpty(dischargeSummaryCollection.getReferenceName())) {
			parameters.put("referenceName", "<b>Reference Name:-</b>" + dischargeSummaryCollection.getReferenceName());
		}
		if (!DPDoctorUtils.anyStringEmpty(dischargeSummaryCollection.getDischargeStatus())) {
			parameters.put("dischargeStatus",
					"<b>Discharge Status:-</b>" + dischargeSummaryCollection.getDischargeStatus());
		}
		if (!DPDoctorUtils.anyStringEmpty(dischargeSummaryCollection.getDischargeOutcome())) {
			parameters.put("dischargeOutcome",
					"<b>Discharge Outcome:-</b>" + dischargeSummaryCollection.getDischargeOutcome());
		}
		if (!DPDoctorUtils.anyStringEmpty(dischargeSummaryCollection.getBedLog())) {
			parameters.put("bedLog", "<b>Bed Log:-</b>" + dischargeSummaryCollection.getBedLog());
		}
		if (!DPDoctorUtils.anyStringEmpty(dischargeSummaryCollection.getHospitalCourse())) {
			parameters.put("hospitalCourse",
					"<b>Hospital Course:-</b>" + dischargeSummaryCollection.getHospitalCourse());
		}

		if (!DPDoctorUtils.allStringsEmpty(dischargeSummaryCollection.getBabyNotes())) {
			parameters.put("babyNotes", dischargeSummaryCollection.getBabyNotes());
		}

		if (!DPDoctorUtils.allStringsEmpty(dischargeSummaryCollection.getBabyWeight())) {
			parameters.put("babyWeight", dischargeSummaryCollection.getBabyWeight());
		}

		if (!DPDoctorUtils.allStringsEmpty(dischargeSummaryCollection.getComplaint())) {
			parameters.put("complaints", dischargeSummaryCollection.getComplaint());
		}

		if (!DPDoctorUtils.allStringsEmpty(dischargeSummaryCollection.getEcho())) {
			parameters.put("echo", dischargeSummaryCollection.getEcho());
		}

		if (!DPDoctorUtils.allStringsEmpty(dischargeSummaryCollection.getConditionsAtDischarge())) {
			parameters.put("condition", dischargeSummaryCollection.getConditionsAtDischarge());
		}

		if (!DPDoctorUtils.allStringsEmpty(dischargeSummaryCollection.getDiagnosis())) {
			parameters.put("diagnosis", dischargeSummaryCollection.getDiagnosis());
		}

		if (!DPDoctorUtils.allStringsEmpty(dischargeSummaryCollection.getFamilyHistory())) {
			parameters.put("familyHistory", dischargeSummaryCollection.getFamilyHistory());
		}

		if (!DPDoctorUtils.allStringsEmpty(dischargeSummaryCollection.getHolter())) {
			parameters.put("holter", dischargeSummaryCollection.getHolter());
		}

		if (!DPDoctorUtils.allStringsEmpty(dischargeSummaryCollection.getEcgDetails())) {
			parameters.put("ecgDetails", dischargeSummaryCollection.getEcgDetails());
		}

		if (!DPDoctorUtils.allStringsEmpty(dischargeSummaryCollection.getGeneralExam())) {
			parameters.put("generalExam", dischargeSummaryCollection.getGeneralExam());
		}

		if (!DPDoctorUtils.allStringsEmpty(dischargeSummaryCollection.getIndicationOfUSG())) {
			parameters.put("indicationOfUSG", dischargeSummaryCollection.getIndicationOfUSG());
		}
		if (!DPDoctorUtils.allStringsEmpty(dischargeSummaryCollection.getxRayDetails())) {
			parameters.put("xRayDetails", dischargeSummaryCollection.getxRayDetails());
		}

		if (!DPDoctorUtils.allStringsEmpty(dischargeSummaryCollection.getPresentComplaintHistory())) {
			parameters.put("historyOfPresentComplaints", dischargeSummaryCollection.getPresentComplaintHistory());
		}

		if (!DPDoctorUtils.allStringsEmpty(dischargeSummaryCollection.getObservation())) {
			parameters.put("observations", dischargeSummaryCollection.getObservation());
		}

		if (!DPDoctorUtils.allStringsEmpty(dischargeSummaryCollection.getInvestigation())) {
			parameters.put("investigations", dischargeSummaryCollection.getInvestigation());
		}

		if (!DPDoctorUtils.allStringsEmpty(dischargeSummaryCollection.getLabourNotes())) {
			parameters.put("labourNotes", dischargeSummaryCollection.getLabourNotes());
		}

		if (!DPDoctorUtils.allStringsEmpty(dischargeSummaryCollection.getMenstrualHistory())) {
			parameters.put("menstrualHistory", dischargeSummaryCollection.getMenstrualHistory());
		}
		if (!DPDoctorUtils.allStringsEmpty(dischargeSummaryCollection.getObstetricHistory())) {
			parameters.put("obstetricHistory", dischargeSummaryCollection.getObstetricHistory());
		}

		if (!DPDoctorUtils.allStringsEmpty(dischargeSummaryCollection.getPastHistory())) {
			parameters.put("pastHistory", dischargeSummaryCollection.getPastHistory());
		}
		if (!DPDoctorUtils.allStringsEmpty(dischargeSummaryCollection.getImplant())) {
			parameters.put("implant", dischargeSummaryCollection.getImplant());
		}

		if (!DPDoctorUtils.allStringsEmpty(dischargeSummaryCollection.getCement())) {
			parameters.put("cement", dischargeSummaryCollection.getCement());
		}

		if (!DPDoctorUtils.allStringsEmpty(dischargeSummaryCollection.getSurgeon())) {
			parameters.put("surgeon", dischargeSummaryCollection.getSurgeon());
		}

		if (!DPDoctorUtils.allStringsEmpty(dischargeSummaryCollection.getAnesthetist())) {
			parameters.put("anesthetist", dischargeSummaryCollection.getAnesthetist());
		}

		if (!DPDoctorUtils.allStringsEmpty(dischargeSummaryCollection.getOperationNotes())) {
			parameters.put("operationNotes", dischargeSummaryCollection.getOperationNotes());
		}

		if (!DPDoctorUtils.allStringsEmpty(dischargeSummaryCollection.getTreatmentsGiven())) {
			parameters.put("treatmentGiven", dischargeSummaryCollection.getTreatmentsGiven());
		}

		if (!DPDoctorUtils.allStringsEmpty(dischargeSummaryCollection.getSystemExam())) {
			parameters.put("systemExam", dischargeSummaryCollection.getSystemExam());
		}

		if (!DPDoctorUtils.allStringsEmpty(dischargeSummaryCollection.getSummary())) {
			parameters.put("summary", dischargeSummaryCollection.getSummary());
		}

		if (!DPDoctorUtils.allStringsEmpty(dischargeSummaryCollection.getPa())) {
			parameters.put("pa", dischargeSummaryCollection.getPa());
		}

		if (!DPDoctorUtils.allStringsEmpty(dischargeSummaryCollection.getPs())) {
			parameters.put("ps", dischargeSummaryCollection.getPs());
		}

		if (!DPDoctorUtils.allStringsEmpty(dischargeSummaryCollection.getPv())) {
			parameters.put("pv", dischargeSummaryCollection.getPv());
		}

		if (!DPDoctorUtils.allStringsEmpty(dischargeSummaryCollection.getPersonalHistory())) {
			parameters.put("pesonalHistory", dischargeSummaryCollection.getPersonalHistory());
		}

		if (!DPDoctorUtils.allStringsEmpty(dischargeSummaryCollection.getPresentComplaint())) {
			parameters.put("presentComplaints", dischargeSummaryCollection.getPresentComplaint());
		}

		if (!DPDoctorUtils.allStringsEmpty(dischargeSummaryCollection.getProcedureNote())) {
			parameters.put("procedureNote", dischargeSummaryCollection.getProcedureNote());
		}

		if (!DPDoctorUtils.allStringsEmpty(dischargeSummaryCollection.getOperationName())) {
			parameters.put("operationName", dischargeSummaryCollection.getOperationName());
		}
		if (!DPDoctorUtils.allStringsEmpty(dischargeSummaryCollection.getLmp())) {
			parameters.put("lmp", dischargeSummaryCollection.getLmp());
		}
		if (!DPDoctorUtils.allStringsEmpty(dischargeSummaryCollection.getEdd())) {
			parameters.put("edd", dischargeSummaryCollection.getEdd());
		}
		if (!DPDoctorUtils.allStringsEmpty(dischargeSummaryCollection.getAnesthesia())) {
			parameters.put("ansthesia", dischargeSummaryCollection.getAnesthesia());
		}

		if (!DPDoctorUtils.allStringsEmpty(dischargeSummaryCollection.getTreatingConsultant())) {
			parameters.put("treatingConsultant", dischargeSummaryCollection.getTreatingConsultant());
		}

		if (!DPDoctorUtils.allStringsEmpty(dischargeSummaryCollection.getSurgeryNotes())) {
			parameters.put("surgeryNotes", dischargeSummaryCollection.getSurgeryNotes());
		}
		if (!DPDoctorUtils.allStringsEmpty(dischargeSummaryCollection.getTreatmentAdviceForBaby())) {
			parameters.put("adviceForBaby", dischargeSummaryCollection.getTreatmentAdviceForBaby());
		}
		if (!DPDoctorUtils.allStringsEmpty(dischargeSummaryCollection.getTreatmentAdviceForMother())) {
			parameters.put("adviceForMother", dischargeSummaryCollection.getTreatmentAdviceForMother());
		}

		if (!DPDoctorUtils.allStringsEmpty(dischargeSummaryCollection.getPediatricianName())) {
			parameters.put("pediatrician", dischargeSummaryCollection.getPediatricianName());
		}

		if (!DPDoctorUtils.allStringsEmpty(dischargeSummaryCollection.getBloodLoss())) {
			parameters.put("bloodLoss", dischargeSummaryCollection.getBloodLoss());
		}
		if (!DPDoctorUtils.allStringsEmpty(dischargeSummaryCollection.getConsultantDoctor())) {
			parameters.put("consultant", dischargeSummaryCollection.getConsultantDoctor());
		}
		if (!DPDoctorUtils.allStringsEmpty(dischargeSummaryCollection.getAssistantDoctor())) {
			parameters.put("assistant", dischargeSummaryCollection.getAssistantDoctor());
		}
		if (!DPDoctorUtils.allStringsEmpty(dischargeSummaryCollection.getTimeOfEntryInOt())) {

			_24HourTime = String.format("%02d:%02d",
					Integer.parseInt(dischargeSummaryCollection.getTimeOfEntryInOt()) / 60,
					Integer.parseInt(dischargeSummaryCollection.getTimeOfEntryInOt()) % 60);
			_24HourSDF = new SimpleDateFormat("HH:mm");

			_24HourSDF.setTimeZone(TimeZone.getTimeZone("IST"));

			Date _24HourDt = _24HourSDF.parse(_24HourTime);
			dateTime = _12HourSDF.format(_24HourDt);

			dateTime = "<b>OT Entry Time :</b> " + dateTime;

		}
		if (!DPDoctorUtils.allStringsEmpty(dischargeSummaryCollection.getTimeOfExitFromOt())) {

			_24HourTime = String.format("%02d:%02d",
					Integer.parseInt(dischargeSummaryCollection.getTimeOfExitFromOt()) / 60,
					Integer.parseInt(dischargeSummaryCollection.getTimeOfExitFromOt()) % 60);
			_24HourSDF = new SimpleDateFormat("HH:mm");
			_24HourSDF.setTimeZone(TimeZone.getTimeZone("IST"));

			Date _24HourDt = _24HourSDF.parse(_24HourTime);
			dateTime = dateTime + "&nbsp&nbsp&nbsp&nbsp<b>OT Exit Time :</b> " + _12HourSDF.format(_24HourDt);

		}
		parameters.put("timeOfEntryAndExitFromOT", dateTime);
		if (dischargeSummaryCollection.getFlowSheets() != null && !dischargeSummaryCollection.getFlowSheets().isEmpty())
			getFlowsheetJasper(dischargeSummaryCollection.getFlowSheets(),
					dischargeSummaryCollection.getMonitoringChart(), parameters);
		if (dischargeSummaryCollection.getFromDate() != null && dischargeSummaryCollection.getTime() != null) {
			_24HourTime = String.format("%02d:%02d", dischargeSummaryCollection.getTime().getFromTime() / 60,
					dischargeSummaryCollection.getTime().getFromTime() % 60);

			sdf.setTimeZone(TimeZone.getTimeZone("IST"));
			_24HourSDF.setTimeZone(TimeZone.getTimeZone("IST"));

			Date _24HourDt = _24HourSDF.parse(_24HourTime);
			dateTime = _12HourSDF.format(_24HourDt) + ", " + sdf.format(dischargeSummaryCollection.getFromDate());
			parameters.put("followUpAppointment", "Next Review on " + dateTime);
		}
		parameters.put("contentLineSpace",
				(printSettings != null && !DPDoctorUtils.anyStringEmpty(printSettings.getContentLineStyle()))

						? printSettings.getContentLineSpace()
						: LineSpace.SMALL.name());
		patientVisitService.generatePatientDetails((printSettings != null
				&& printSettings.getHeaderSetup() != null ? printSettings.getHeaderSetup().getPatientDetails() : null),
				patient,
				"<b>DIS-ID: </b>" + (dischargeSummaryCollection.getUniqueEmrId() != null
						? dischargeSummaryCollection.getUniqueEmrId()
						: "--"),
				patient.getLocalPatientName(), user.getMobileNumber(), parameters,
				dischargeSummaryCollection.getCreatedTime() != null ? dischargeSummaryCollection.getCreatedTime()
						: new Date(),
				printSettings.getHospitalUId(), printSettings.getIsPidHasDate());

		patientVisitService.generatePrintSetup(parameters, printSettings, dischargeSummaryCollection.getDoctorId());
		String pdfName = (patient != null ? patient.getLocalPatientName() : "") + "DISCHARGE-SUMMARY-"
				+ dischargeSummaryCollection.getUniqueEmrId() + new Date().getTime();

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
		response = jasperReportService.createPDF(ComponentType.DISCHARGE_SUMMARY, parameters,
				dischargeSummaryReportA4FileName, layout, pageSize, topMargin, bottonMargin, leftMargin, rightMargin,
				Integer.parseInt(parameters.get("contentFontSize").toString()), pdfName.replaceAll("\\s+", ""));

		return response;

	}

	@Override
	public void emailDischargeSummary(String dischargeSummeryId, String doctorId, String locationId, String hospitalId,
			String emailAddress) {
		MailResponse mailResponse = null;
		DischargeSummaryCollection dischargeSummaryCollection = null;
		MailAttachment mailAttachment = null;
		UserCollection user = null;
		PatientCollection patient = null;
		EmailTrackCollection emailTrackCollection = new EmailTrackCollection();
		try {
			dischargeSummaryCollection = dischargeSummaryRepository.findById(new ObjectId(dischargeSummeryId))
					.orElse(null);
			if (dischargeSummaryCollection != null) {
				if (dischargeSummaryCollection.getDoctorId() != null
						&& dischargeSummaryCollection.getHospitalId() != null
						&& dischargeSummaryCollection.getLocationId() != null) {
					if (dischargeSummaryCollection.getDoctorId().toString().equals(doctorId)
							&& dischargeSummaryCollection.getHospitalId().toString().equals(hospitalId)
							&& dischargeSummaryCollection.getLocationId().toString().equals(locationId)) {

						user = userRepository.findById(dischargeSummaryCollection.getPatientId()).orElse(null);
						patient = patientRepository.findByUserIdAndLocationIdAndHospitalId(
								dischargeSummaryCollection.getPatientId(), dischargeSummaryCollection.getLocationId(),
								dischargeSummaryCollection.getHospitalId());
						user.setFirstName(patient.getLocalPatientName());
						emailTrackCollection.setDoctorId(dischargeSummaryCollection.getDoctorId());
						emailTrackCollection.setHospitalId(dischargeSummaryCollection.getHospitalId());
						emailTrackCollection.setLocationId(dischargeSummaryCollection.getLocationId());
						emailTrackCollection.setType(ComponentType.DISCHARGE_SUMMARY.getType());
						emailTrackCollection.setSubject("Discharge Summary");
						if (user != null) {
							emailTrackCollection.setPatientName(patient.getLocalPatientName());
							emailTrackCollection.setPatientId(user.getId());
						}

						JasperReportResponse jasperReportResponse = createJasper(dischargeSummaryCollection, patient,
								user, PrintSettingType.EMAIL.getType());
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
						mailResponse.setMailRecordCreatedDate(sdf.format(dischargeSummaryCollection.getCreatedTime()));
						mailResponse.setPatientName(user.getFirstName());
						emailTackService.saveEmailTrack(emailTrackCollection);

					} else {
						logger.warn("DischargeSummary Id, doctorId, location Id, hospital Id does not match");
						throw new BusinessException(ServiceError.NotFound,
								" DischargeSummary  Id, doctorId, location Id, hospital Id does not match");
					}
				}

			} else {
				logger.warn("Discharge Summary  not found.Please check summaryId.");
				throw new BusinessException(ServiceError.NoRecord,
						"Discharge Summary not found.Please check summaryId.");
			}

			String body = mailBodyGenerator.generateEMREmailBody(mailResponse.getPatientName(),
					mailResponse.getDoctorName(), mailResponse.getClinicName(), mailResponse.getClinicAddress(),
					mailResponse.getMailRecordCreatedDate(), "Discharge Summary", "emrMailTemplate.vm");
			Boolean response = mailService.sendEmail(emailAddress,
					mailResponse.getDoctorName() + " sent you Discharge Summary", body,
					mailResponse.getMailAttachment());
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

	private Appointment addDischageSummaryAppointment(AppointmentRequest appointment) {
		Appointment response = null;
		if (appointment.getAppointmentId() == null) {
			response = appointmentService.addAppointment(appointment, false);
		} else {
			response = new Appointment();
			BeanUtil.map(appointment, response);
		}
		return response;
	}

	@Override
	public DischargeSummaryResponse addMultiVisit(List<String> visitIds) {
		DischargeSummaryResponse response = null;
		try {
			List<ObjectId> visitObjectIds = new ArrayList<ObjectId>();
			for (String visitId : visitIds) {
				visitObjectIds.add(new ObjectId(visitId));
			}
			Criteria criteria = new Criteria("patientId").exists(true).and("locationId").exists(true).and("hospitalId")
					.exists(true).and("doctorId").exists(true).and("_id").in(visitObjectIds);
			Aggregation aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
					Aggregation.sort(new Sort.Order(Sort.Direction.DESC, "createdTime").withProperties("createdTime")));
			AggregationResults<PatientVisitLookupBean> aggregationResults = mongoTemplate.aggregate(aggregation,
					PatientVisitCollection.class, PatientVisitLookupBean.class);
			List<PatientVisitLookupBean> patientVisitLookupBeans = aggregationResults.getMappedResults();
			if (patientVisitLookupBeans != null && !patientVisitLookupBeans.isEmpty()) {
				for (PatientVisitLookupBean patientVisitlookupBean : patientVisitLookupBeans) {
					response = new DischargeSummaryResponse();

					if (patientVisitlookupBean.getPrescriptionId() != null) {
						List<Prescription> prescriptions = prescriptionServices.getPrescriptionsByIds(
								patientVisitlookupBean.getPrescriptionId(), patientVisitlookupBean.getId());

						if (response.getPrescriptions() == null && prescriptions != null) {
							response.setPrescriptions(new Prescription());
							response.getPrescriptions().setAdvice(prescriptions.get(0).getAdvice());
							response.getPrescriptions().setItems(prescriptions.get(0).getItems());

						} else if (prescriptions != null) {

							response.getPrescriptions().setAdvice("," + prescriptions.get(0).getAdvice());
							response.getPrescriptions().setItems(prescriptions.get(0).getItems());

						}

					}

					if (patientVisitlookupBean.getClinicalNotesId() != null) {

						for (ObjectId clinicalNotesId : patientVisitlookupBean.getClinicalNotesId()) {
							ClinicalNotes clinicalNote = clinicalNotesService.getNotesById(clinicalNotesId.toString(),
									patientVisitlookupBean.getId());
							String pattern = "dd/MM/yyyy";
							SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
							String date = simpleDateFormat.format(patientVisitlookupBean.getVisitedTime());

							if (!DPDoctorUtils.anyStringEmpty(response.getDiagnosis(), clinicalNote.getDiagnosis())) {

								response.setDiagnosis(",<br>" + date + ":-" + clinicalNote.getDiagnosis());

							} else if (!DPDoctorUtils.anyStringEmpty(clinicalNote.getDiagnosis())) {

								response.setDiagnosis(date + ":-" + clinicalNote.getDiagnosis());
							}

							if (!DPDoctorUtils.anyStringEmpty(response.getComplaint(), clinicalNote.getComplaint())) {

								response.setComplaint(",<br>" + date + ":-" + clinicalNote.getComplaint());

							} else if (!DPDoctorUtils.anyStringEmpty(clinicalNote.getComplaint())) {
								response.setComplaint(date + ":-" + clinicalNote.getComplaint());
							}

							if (!DPDoctorUtils.anyStringEmpty(response.getObservation(),
									clinicalNote.getObservation())) {
								response.setObservation(",<br>" + date + ":-" + clinicalNote.getObservation());
							} else if (!DPDoctorUtils.anyStringEmpty(clinicalNote.getObservation())) {
								response.setObservation(date + ":-" + clinicalNote.getObservation());
							}

							if (!DPDoctorUtils.anyStringEmpty(response.getInvestigation(),
									clinicalNote.getInvestigation())) {
								response.setInvestigation(",<br>" + date + ":-" + clinicalNote.getInvestigation());

							} else if (!DPDoctorUtils.anyStringEmpty(clinicalNote.getInvestigation())) {
								response.setInvestigation(date + ":-" + clinicalNote.getInvestigation());
							}

							if (!DPDoctorUtils.anyStringEmpty(response.getSystemExam(), clinicalNote.getSystemExam())) {
								response.setSystemExam(",<br>" + date + ":-" + clinicalNote.getSystemExam());
							} else if (!DPDoctorUtils.anyStringEmpty(clinicalNote.getSystemExam())) {
								response.setSystemExam(date + ":-" + clinicalNote.getSystemExam());
							}

							if (!DPDoctorUtils.anyStringEmpty(response.getGeneralExam(),
									clinicalNote.getGeneralExam())) {
								response.setGeneralExam(",<br>" + date + ":-" + clinicalNote.getGeneralExam());

							} else if (!DPDoctorUtils.anyStringEmpty(clinicalNote.getGeneralExam())) {
								response.setGeneralExam(date + ":-" + clinicalNote.getGeneralExam());
							}

							if (!DPDoctorUtils.anyStringEmpty(response.getPresentComplaint(),
									clinicalNote.getPresentComplaint())) {
								response.setPresentComplaint(
										",<br>" + date + ":-" + clinicalNote.getPresentComplaint());

							} else if (!DPDoctorUtils.anyStringEmpty(clinicalNote.getPresentComplaint())) {
								response.setPresentComplaint(date + ":-" + clinicalNote.getPresentComplaint());

							}

							if (!DPDoctorUtils.anyStringEmpty(response.getPresentComplaintHistory(),
									clinicalNote.getPresentComplaintHistory())) {
								response.setPresentComplaintHistory(
										",<br>" + date + ":-" + clinicalNote.getPresentComplaintHistory());

							} else if (!DPDoctorUtils.anyStringEmpty(clinicalNote.getPresentComplaintHistory())) {
								response.setPresentComplaintHistory(
										date + ":-" + clinicalNote.getPresentComplaintHistory());
							}

							if (!DPDoctorUtils.anyStringEmpty(response.getEcgDetails(), clinicalNote.getEcgDetails())) {

								response.setEcgDetails(",<br>" + date + ":-" + clinicalNote.getEcgDetails());

							} else if (!DPDoctorUtils.anyStringEmpty(clinicalNote.getEcgDetails())) {
								response.setEcgDetails(date + ":-" + clinicalNote.getEcgDetails());
							}

							if (!DPDoctorUtils.anyStringEmpty(response.getEcho(), clinicalNote.getEcho())) {

								response.setEcho(",<br>" + date + ":-" + clinicalNote.getEcho());

							} else if (!DPDoctorUtils.anyStringEmpty(clinicalNote.getEcho())) {
								response.setEcho(date + ":-" + clinicalNote.getEcho());
							}
							if (!DPDoctorUtils.anyStringEmpty(response.getxRayDetails(),
									clinicalNote.getxRayDetails())) {
								response.setxRayDetails(",<br>" + date + ":-" + clinicalNote.getxRayDetails());

							} else if (!DPDoctorUtils.anyStringEmpty(clinicalNote.getxRayDetails())) {

								response.setxRayDetails(date + ":-" + clinicalNote.getxRayDetails());

							}
							if (!DPDoctorUtils.anyStringEmpty(response.getHolter(), clinicalNote.getHolter())) {

								response.setHolter(",<br>" + date + ":-" + clinicalNote.getHolter());

							} else if (!DPDoctorUtils.anyStringEmpty(clinicalNote.getHolter())) {
								response.setHolter(date + ":-" + clinicalNote.getHolter());
							}

							if (!DPDoctorUtils.anyStringEmpty(response.getPv(), clinicalNote.getPv())) {
								response.setPv(",<br>" + date + ":-" + clinicalNote.getPv());

							} else if (!DPDoctorUtils.anyStringEmpty(clinicalNote.getPv())) {
								response.setPv(date + ":-" + clinicalNote.getPv());
							}
							if (!DPDoctorUtils.anyStringEmpty(response.getPa(), clinicalNote.getPa())) {
								response.setPa(",<br>" + date + ":-" + clinicalNote.getPa());

							} else if (!DPDoctorUtils.anyStringEmpty(clinicalNote.getPa())) {
								response.setPa(date + ":-" + clinicalNote.getPa());
							}
							if (!DPDoctorUtils.anyStringEmpty(response.getPs(), clinicalNote.getPs())) {
								response.setPs(",<br>" + date + ":-" + clinicalNote.getPs());

							} else if (!DPDoctorUtils.anyStringEmpty(clinicalNote.getPs())) {

								response.setPs(date + ":-" + clinicalNote.getPs());

							}
							if (!DPDoctorUtils.anyStringEmpty(response.getMenstrualHistory(),
									clinicalNote.getMenstrualHistory())) {
								response.setMenstrualHistory(
										",<br>" + date + ":-" + clinicalNote.getMenstrualHistory());

							} else if (!DPDoctorUtils.anyStringEmpty(clinicalNote.getMenstrualHistory())) {
								response.setMenstrualHistory(date + ":-" + clinicalNote.getMenstrualHistory());
							}
							if (!DPDoctorUtils.anyStringEmpty(response.getObstetricHistory(),
									clinicalNote.getObstetricHistory())) {
								response.setObstetricHistory(
										",<br>" + date + ":-" + clinicalNote.getObstetricHistory());

							} else if (!DPDoctorUtils.anyStringEmpty(clinicalNote.getObstetricHistory())) {
								response.setObstetricHistory(date + ":-" + clinicalNote.getObstetricHistory());
							}

							if (!DPDoctorUtils.anyStringEmpty(response.getIndicationOfUSG(),
									clinicalNote.getIndicationOfUSG())) {
								response.setIndicationOfUSG(",<br>" + date + ":-" + clinicalNote.getIndicationOfUSG());

							} else if (!DPDoctorUtils.anyStringEmpty(clinicalNote.getIndicationOfUSG())) {
								response.setIndicationOfUSG(date + ":-" + clinicalNote.getIndicationOfUSG());
							}

							if (!DPDoctorUtils.anyStringEmpty(response.getProcedureNote(),
									clinicalNote.getProcedureNote())) {

								response.setDiagnosis(",<br>" + date + ":-" + clinicalNote.getDiagnosis());

							} else if (!DPDoctorUtils.anyStringEmpty(clinicalNote.getProcedureNote())) {

								response.setDiagnosis(date + ":-" + clinicalNote.getDiagnosis());
							}

						}
						for (int index = 0; index < response.getPrescriptions().getItems().size(); index++) {
							for (int ptr = index + 1; ptr < response.getPrescriptions().getItems().size(); ptr++) {
								if (response.getPrescriptions().getItems().get(index).toString()
										.equals(response.getPrescriptions().getItems().get(ptr).toString())) {
									response.getPrescriptions().getItems().remove(ptr);
								}
							}

						}
					}

				}

			} else {

				throw new BusinessException(ServiceError.InvalidInput, "visit not found for ids");

			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());

		}
		return response;
	}

	public Integer upadateDischargeSummaryData() {
		Integer response = 0;
		try {
			List<DischargeSummaryCollection> dischargeSummaryCollections = dischargeSummaryRepository.findAll();
			for (DischargeSummaryCollection dischargeSummaryCollection : dischargeSummaryCollections) {
				if (!DPDoctorUtils.anyStringEmpty(dischargeSummaryCollection.getComplaints())) {
					dischargeSummaryCollection.setComplaint(dischargeSummaryCollection.getComplaints());
				}
				if (!DPDoctorUtils.anyStringEmpty(dischargeSummaryCollection.setUniqueEmrId())) {
					dischargeSummaryCollection.setUniqueEmrId(dischargeSummaryCollection.setUniqueEmrId());
				}
				if (!DPDoctorUtils.anyStringEmpty(dischargeSummaryCollection.getGeneralExamination())) {
					dischargeSummaryCollection.setGeneralExam(dischargeSummaryCollection.getGeneralExamination());
				}
				if (!DPDoctorUtils.anyStringEmpty(dischargeSummaryCollection.getSystemicExamination())) {
					dischargeSummaryCollection.setSystemExam(dischargeSummaryCollection.getSystemicExamination());
				}
				if (!DPDoctorUtils.anyStringEmpty(dischargeSummaryCollection.getHistoryOfPresentComplaints())) {
					dischargeSummaryCollection
							.setPresentComplaintHistory(dischargeSummaryCollection.getHistoryOfPresentComplaints());
				}
				if (dischargeSummaryCollection.getPrescriptions() != null) {
					if ((dischargeSummaryCollection.getPrescriptions().getItems() != null
							&& !dischargeSummaryCollection.getPrescriptions().getItems().isEmpty())
							|| (dischargeSummaryCollection.getPrescriptions().getAdvice() != null
									&& !dischargeSummaryCollection.getPrescriptions().getAdvice().isEmpty())) {
						PrescriptionAddEditRequest addEditRequest = new PrescriptionAddEditRequest();

						BeanUtil.map(dischargeSummaryCollection.getPrescriptions(), addEditRequest);
						addEditRequest.setHospitalId(dischargeSummaryCollection.getHospitalId().toString());
						addEditRequest.setLocationId(dischargeSummaryCollection.getLocationId().toString());
						addEditRequest.setDoctorId(dischargeSummaryCollection.getDoctorId().toString());
						addEditRequest.setPatientId(dischargeSummaryCollection.getPatientId().toString());
						PrescriptionAddEditResponseDetails addEditResponseDetails = prescriptionServices
								.addPrescriptionHandheld(addEditRequest);
						dischargeSummaryCollection.setPrescriptionId(new ObjectId(addEditResponseDetails.getId()));
						if (addEditResponseDetails != null) {
							String visitId = patientVisitService.addRecord(addEditResponseDetails,
									VisitedFor.PRESCRIPTION, addEditResponseDetails.getVisitId());
							addEditResponseDetails.setVisitId(visitId);
						}
					}
				}
			}

			dischargeSummaryCollections = (List<DischargeSummaryCollection>) dischargeSummaryRepository
					.saveAll(dischargeSummaryCollections);
			response = dischargeSummaryCollections.size();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());

		}
		return response;
	}

	@Override
	@Transactional
	public LabourNote addEditLabourNote(LabourNote labourNote) {
		try {
			LabourNoteCollection labourNoteCollection = new LabourNoteCollection();
			BeanUtil.map(labourNote, labourNoteCollection);
			if (DPDoctorUtils.anyStringEmpty(labourNote.getId())) {
				labourNoteCollection.setCreatedTime(new Date());
				if (!DPDoctorUtils.anyStringEmpty(labourNoteCollection.getDoctorId())) {
					UserCollection userCollection = userRepository.findById(labourNoteCollection.getDoctorId())
							.orElse(null);
					if (userCollection != null) {
						labourNoteCollection
								.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
										+ userCollection.getFirstName());
					}
				} else {
					labourNoteCollection.setCreatedBy("ADMIN");
				}
			} else {
				LabourNoteCollection oldLabourNoteCollection = labourNoteRepository
						.findById(labourNoteCollection.getId()).orElse(null);
				labourNoteCollection.setCreatedBy(oldLabourNoteCollection.getCreatedBy());
				labourNoteCollection.setCreatedTime(oldLabourNoteCollection.getCreatedTime());
				labourNoteCollection.setDiscarded(oldLabourNoteCollection.getDiscarded());
			}
			labourNoteCollection = labourNoteRepository.save(labourNoteCollection);

			BeanUtil.map(labourNoteCollection, labourNote);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return labourNote;
	}

	@Override
	public LabourNote deleteLabourNote(String id, String doctorId, String locationId, String hospitalId,
			Boolean discarded) {
		LabourNote response = null;
		try {
			LabourNoteCollection labourNoteCollection = labourNoteRepository.findById(new ObjectId(id)).orElse(null);
			if (labourNoteCollection != null) {
				if (!DPDoctorUtils.anyStringEmpty(labourNoteCollection.getDoctorId(),
						labourNoteCollection.getHospitalId(), labourNoteCollection.getLocationId())) {
					if (labourNoteCollection.getDoctorId().toString().equals(doctorId)
							&& labourNoteCollection.getHospitalId().toString().equals(hospitalId)
							&& labourNoteCollection.getLocationId().toString().equals(locationId)) {

						labourNoteCollection.setDiscarded(discarded);
						labourNoteCollection.setUpdatedTime(new Date());
						labourNoteRepository.save(labourNoteCollection);
						response = new LabourNote();
						BeanUtil.map(labourNoteCollection, response);
					} else {
						logger.warn("Invalid Doctor Id, Hospital Id, Or Location Id");
						throw new BusinessException(ServiceError.InvalidInput,
								"Invalid Doctor Id, Hospital Id, Or Location Id");
					}
				} else {
					labourNoteCollection.setDiscarded(discarded);
					labourNoteCollection.setUpdatedTime(new Date());
					labourNoteRepository.save(labourNoteCollection);
					response = new LabourNote();
					BeanUtil.map(labourNoteCollection, response);
				}
			} else {
				logger.warn("Labour Note not found!");
				throw new BusinessException(ServiceError.NoRecord, "Labour Note not found!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	@Transactional
	public BabyNote addEditBabyNote(BabyNote babyNote) {
		try {
			BabyNoteCollection babyNoteCollection = new BabyNoteCollection();
			BeanUtil.map(babyNote, babyNoteCollection);
			if (DPDoctorUtils.anyStringEmpty(babyNote.getId())) {
				babyNoteCollection.setCreatedTime(new Date());
				if (!DPDoctorUtils.anyStringEmpty(babyNoteCollection.getDoctorId())) {
					UserCollection userCollection = userRepository.findById(babyNoteCollection.getDoctorId())
							.orElse(null);
					if (userCollection != null) {
						babyNoteCollection
								.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
										+ userCollection.getFirstName());
					}
				} else {
					babyNoteCollection.setCreatedBy("ADMIN");
				}
			} else {
				BabyNoteCollection oldBabyNoteCollection = babyNoteRepository.findById(babyNoteCollection.getId())
						.orElse(null);
				babyNoteCollection.setCreatedBy(oldBabyNoteCollection.getCreatedBy());
				babyNoteCollection.setCreatedTime(oldBabyNoteCollection.getCreatedTime());
				babyNoteCollection.setDiscarded(oldBabyNoteCollection.getDiscarded());
			}
			babyNoteCollection = babyNoteRepository.save(babyNoteCollection);

			BeanUtil.map(babyNoteCollection, babyNote);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return babyNote;
	}

	@Override
	public BabyNote deleteBabyNote(String id, String doctorId, String locationId, String hospitalId,
			Boolean discarded) {
		BabyNote response = null;
		try {
			BabyNoteCollection babyNoteCollection = babyNoteRepository.findById(new ObjectId(id)).orElse(null);
			if (babyNoteCollection != null) {
				if (!DPDoctorUtils.anyStringEmpty(babyNoteCollection.getDoctorId(), babyNoteCollection.getHospitalId(),
						babyNoteCollection.getLocationId())) {
					if (babyNoteCollection.getDoctorId().toString().equals(doctorId)
							&& babyNoteCollection.getHospitalId().toString().equals(hospitalId)
							&& babyNoteCollection.getLocationId().toString().equals(locationId)) {

						babyNoteCollection.setDiscarded(discarded);
						babyNoteCollection.setUpdatedTime(new Date());
						babyNoteRepository.save(babyNoteCollection);
						response = new BabyNote();
						BeanUtil.map(babyNoteCollection, response);
					} else {
						logger.warn("Invalid Doctor Id, Hospital Id, Or Location Id");
						throw new BusinessException(ServiceError.InvalidInput,
								"Invalid Doctor Id, Hospital Id, Or Location Id");
					}
				} else {
					babyNoteCollection.setDiscarded(discarded);
					babyNoteCollection.setUpdatedTime(new Date());
					babyNoteRepository.save(babyNoteCollection);
					response = new BabyNote();
					BeanUtil.map(babyNoteCollection, response);
				}
			} else {
				logger.warn("Baby Note  not found!");
				throw new BusinessException(ServiceError.NoRecord, "Baby Note not found!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	@Transactional
	public OperationNote addEditOperationNote(OperationNote operationNote) {
		try {
			OperationNoteCollection operationNoteCollection = new OperationNoteCollection();
			BeanUtil.map(operationNote, operationNoteCollection);
			if (DPDoctorUtils.anyStringEmpty(operationNote.getId())) {
				operationNoteCollection.setCreatedTime(new Date());
				if (!DPDoctorUtils.anyStringEmpty(operationNoteCollection.getDoctorId())) {
					UserCollection userCollection = userRepository.findById(operationNoteCollection.getDoctorId())
							.orElse(null);
					if (userCollection != null) {
						operationNoteCollection
								.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
										+ userCollection.getFirstName());
					}
				} else {
					operationNoteCollection.setCreatedBy("ADMIN");
				}
			} else {
				OperationNoteCollection oldoperationNoteCollection = operationNoteRepository
						.findById(operationNoteCollection.getId()).orElse(null);
				operationNoteCollection.setCreatedBy(oldoperationNoteCollection.getCreatedBy());
				operationNoteCollection.setCreatedTime(oldoperationNoteCollection.getCreatedTime());
				operationNoteCollection.setDiscarded(oldoperationNoteCollection.getDiscarded());
			}
			operationNoteCollection = operationNoteRepository.save(operationNoteCollection);

			BeanUtil.map(operationNoteCollection, operationNote);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return operationNote;
	}

	@Override
	public OperationNote deleteOperationNote(String id, String doctorId, String locationId, String hospitalId,
			Boolean discarded) {
		OperationNote response = null;
		try {
			OperationNoteCollection operationNoteCollection = operationNoteRepository.findById(new ObjectId(id))
					.orElse(null);
			if (operationNoteCollection != null) {
				if (!DPDoctorUtils.anyStringEmpty(operationNoteCollection.getDoctorId(),
						operationNoteCollection.getHospitalId(), operationNoteCollection.getLocationId())) {
					if (operationNoteCollection.getDoctorId().toString().equals(doctorId)
							&& operationNoteCollection.getHospitalId().toString().equals(hospitalId)
							&& operationNoteCollection.getLocationId().toString().equals(locationId)) {

						operationNoteCollection.setDiscarded(discarded);
						operationNoteCollection.setUpdatedTime(new Date());
						operationNoteRepository.save(operationNoteCollection);
						response = new OperationNote();
						BeanUtil.map(operationNoteCollection, response);
					} else {
						logger.warn("Invalid Doctor Id, Hospital Id, Or Location Id");
						throw new BusinessException(ServiceError.InvalidInput,
								"Invalid Doctor Id, Hospital Id, Or Location Id");
					}
				} else {
					operationNoteCollection.setDiscarded(discarded);
					operationNoteCollection.setUpdatedTime(new Date());
					operationNoteRepository.save(operationNoteCollection);
					response = new OperationNote();
					BeanUtil.map(operationNoteCollection, response);
				}
			} else {
				logger.warn("Operation Note  not found!");
				throw new BusinessException(ServiceError.NoRecord, "Operation Note not found!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	public List<?> getDischargeSummaryItems(String type, String range, long page, int size, String doctorId,
			String locationId, String hospitalId, String updatedTime, Boolean discarded, String searchTerm) {
		List<?> response = new ArrayList<Object>();

		switch (DischargeSummaryItem.valueOf(type.toUpperCase())) {

		case BABY_NOTES: {

			switch (Range.valueOf(range.toUpperCase())) {

			case GLOBAL:
				response = getGlobalBabyNote(page, size, doctorId, updatedTime, discarded);
				break;
			case CUSTOM:
				response = getCustomBabyNote(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
				break;
			case BOTH:
				response = getCustomGlobalBabyNote(page, size, doctorId, locationId, hospitalId, updatedTime,
						discarded);
				break;
			default:
				break;
			}
			break;
		}
		case LABOUR_NOTES: {
			switch (Range.valueOf(range.toUpperCase())) {

			case GLOBAL:
				response = getGlobalLabourNote(page, size, doctorId, updatedTime, discarded);
				break;
			case CUSTOM:
				response = getCustomLabourNote(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
				break;
			case BOTH:
				response = getCustomGlobalLabourNote(page, size, doctorId, locationId, hospitalId, updatedTime,
						discarded);
				break;
			default:
				break;
			}
			break;
		}
		case OPERATION_NOTES: {
			switch (Range.valueOf(range.toUpperCase())) {

			case GLOBAL:
				response = getGlobalOperationNote(page, size, doctorId, updatedTime, discarded);
				break;
			case CUSTOM:
				response = getCustomOperationNote(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
				break;
			case BOTH:
				response = getCustomGlobalOperationNote(page, size, doctorId, locationId, hospitalId, updatedTime,
						discarded);
				break;
			default:
				break;
			}
			break;
		}
		case IMPLANT: {
			switch (Range.valueOf(range.toUpperCase())) {

			case GLOBAL:
				response = getGlobalImplant(page, size, doctorId, updatedTime, discarded);
				break;
			case CUSTOM:
				response = getCustomImplant(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
				break;
			case BOTH:
				response = getCustomGlobalImplant(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
				break;
			default:
				break;
			}
			break;
		}
		case CEMENT: {
			switch (Range.valueOf(range.toUpperCase())) {

			case GLOBAL:
				response = getGlobalCement(page, size, doctorId, updatedTime, discarded);
				break;
			case CUSTOM:
				response = getCustomCement(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
				break;
			case BOTH:
				response = getCustomGlobalCement(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
				break;
			default:
				break;
			}
			break;
		}
		}

		return response;
	}

	@SuppressWarnings("unchecked")
	private List<BabyNote> getCustomGlobalBabyNote(long page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded) {
		List<BabyNote> response = new ArrayList<BabyNote>();
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findAllById(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add(null);
				specialities.add("ALL");
			}

			AggregationResults<BabyNote> results = mongoTemplate.aggregate(
					DPDoctorUtils.createCustomGlobalAggregation(page, size, doctorId, locationId, hospitalId,
							updatedTime, discarded, null, null, specialities, null),
					BabyNoteCollection.class, BabyNote.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Baby Note");
		}
		return response;

	}

	@SuppressWarnings("unchecked")
	private List<BabyNote> getGlobalBabyNote(long page, int size, String doctorId, String updatedTime,
			Boolean discarded) {
		List<BabyNote> response = null;
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findAllById(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add("ALL");
				specialities.add(null);
			}

			AggregationResults<BabyNote> results = mongoTemplate.aggregate(DPDoctorUtils.createGlobalAggregation(page,
					size, updatedTime, discarded, null, null, specialities, null), BabyNoteCollection.class,
					BabyNote.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Baby Note");
		}
		return response;
	}

	private List<BabyNote> getCustomBabyNote(long page, int size, String doctorId, String locationId, String hospitalId,
			String updatedTime, Boolean discarded) {
		List<BabyNote> response = null;
		try {
			AggregationResults<BabyNote> results = mongoTemplate
					.aggregate(
							DPDoctorUtils.createCustomAggregation(page, size, doctorId, locationId, hospitalId,
									updatedTime, discarded, null, null, null),
							BabyNoteCollection.class, BabyNote.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Baby Note");
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private List<OperationNote> getCustomGlobalOperationNote(long page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded) {
		List<OperationNote> response = new ArrayList<OperationNote>();
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findAllById(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add(null);
				specialities.add("ALL");
			}

			AggregationResults<OperationNote> results = mongoTemplate.aggregate(
					DPDoctorUtils.createCustomGlobalAggregation(page, size, doctorId, locationId, hospitalId,
							updatedTime, discarded, null, null, specialities, null),
					OperationNoteCollection.class, OperationNote.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Operation Note");
		}
		return response;

	}

	@SuppressWarnings("unchecked")
	private List<OperationNote> getGlobalOperationNote(long page, int size, String doctorId, String updatedTime,
			Boolean discarded) {
		List<OperationNote> response = null;
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findAllById(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add("ALL");
				specialities.add(null);
			}

			AggregationResults<OperationNote> results = mongoTemplate.aggregate(DPDoctorUtils
					.createGlobalAggregation(page, size, updatedTime, discarded, null, null, specialities, null),
					OperationNoteCollection.class, OperationNote.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Operation Note");
		}
		return response;
	}

	private List<OperationNote> getCustomOperationNote(long page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded) {
		List<OperationNote> response = null;
		try {
			AggregationResults<OperationNote> results = mongoTemplate
					.aggregate(
							DPDoctorUtils.createCustomAggregation(page, size, doctorId, locationId, hospitalId,
									updatedTime, discarded, null, null, null),
							OperationNoteCollection.class, OperationNote.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Operation Note");
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private List<LabourNote> getCustomGlobalLabourNote(long page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded) {
		List<LabourNote> response = new ArrayList<LabourNote>();
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findAllById(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add(null);
				specialities.add("ALL");
			}

			AggregationResults<LabourNote> results = mongoTemplate.aggregate(
					DPDoctorUtils.createCustomGlobalAggregation(page, size, doctorId, locationId, hospitalId,
							updatedTime, discarded, null, null, specialities, null),
					LabourNoteCollection.class, LabourNote.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Labour Note");
		}
		return response;

	}

	@SuppressWarnings("unchecked")
	private List<LabourNote> getGlobalLabourNote(long page, int size, String doctorId, String updatedTime,
			Boolean discarded) {
		List<LabourNote> response = null;
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findAllById(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add("ALL");
				specialities.add(null);
			}

			AggregationResults<LabourNote> results = mongoTemplate.aggregate(DPDoctorUtils.createGlobalAggregation(page,
					size, updatedTime, discarded, null, null, specialities, null), LabourNoteCollection.class,
					LabourNote.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Labour Note");
		}
		return response;
	}

	private List<LabourNote> getCustomLabourNote(long page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded) {
		List<LabourNote> response = null;
		try {
			AggregationResults<LabourNote> results = mongoTemplate
					.aggregate(
							DPDoctorUtils.createCustomAggregation(page, size, doctorId, locationId, hospitalId,
									updatedTime, discarded, null, null, null),
							LabourNoteCollection.class, LabourNote.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Labour Note");
		}
		return response;
	}

	@Override
	@Transactional
	public Implant addEditImplant(Implant implant) {
		try {
			ImplantCollection implantCollection = new ImplantCollection();
			BeanUtil.map(implant, implantCollection);
			if (DPDoctorUtils.anyStringEmpty(implant.getId())) {
				implantCollection.setCreatedTime(new Date());
				if (!DPDoctorUtils.anyStringEmpty(implantCollection.getDoctorId())) {
					UserCollection userCollection = userRepository.findById(implantCollection.getDoctorId())
							.orElse(null);
					if (userCollection != null) {
						implantCollection
								.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
										+ userCollection.getFirstName());
					}
				} else {
					implantCollection.setCreatedBy("ADMIN");
				}
			} else {
				ImplantCollection oldImplantCollection = implantRepository.findById(implantCollection.getId())
						.orElse(null);
				implantCollection.setCreatedBy(oldImplantCollection.getCreatedBy());
				implantCollection.setCreatedTime(oldImplantCollection.getCreatedTime());
				implantCollection.setDiscarded(oldImplantCollection.getDiscarded());
			}
			implantCollection = implantRepository.save(implantCollection);
			BeanUtil.map(implantCollection, implant);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Adding Implant");
		}
		return implant;
	}

	@SuppressWarnings("unchecked")
	private List<Implant> getCustomGlobalImplant(long page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded) {
		List<Implant> response = null;
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findAllById(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add(null);
				specialities.add("ALL");
			}

			AggregationResults<Implant> results = mongoTemplate.aggregate(
					DPDoctorUtils.createCustomGlobalAggregation(page, size, doctorId, locationId, hospitalId,
							updatedTime, discarded, null, null, specialities, null),
					ImplantCollection.class, Implant.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Implant");
		}
		return response;

	}

	@SuppressWarnings("unchecked")
	private List<Implant> getGlobalImplant(long page, int size, String doctorId, String updatedTime,
			Boolean discarded) {
		List<Implant> response = null;
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findAllById(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add("ALL");
				specialities.add(null);
			}

			AggregationResults<Implant> results = mongoTemplate.aggregate(DPDoctorUtils.createGlobalAggregation(page,
					size, updatedTime, discarded, null, null, specialities, null), ImplantCollection.class,
					Implant.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Implant");
		}
		return response;
	}

	private List<Implant> getCustomImplant(long page, int size, String doctorId, String locationId, String hospitalId,
			String updatedTime, Boolean discarded) {
		List<Implant> response = null;
		try {
			AggregationResults<Implant> results = mongoTemplate.aggregate(DPDoctorUtils.createCustomAggregation(page,
					size, doctorId, locationId, hospitalId, updatedTime, discarded, null, null, null),
					ImplantCollection.class, Implant.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Implant");
		}
		return response;
	}

	@Override
	public Implant deleteImplant(String id, String doctorId, String locationId, String hospitalId, Boolean discarded) {
		Implant response = null;
		try {
			ImplantCollection implantCollection = implantRepository.findById(new ObjectId(id)).orElse(null);
			if (implantCollection != null) {
				if (!DPDoctorUtils.anyStringEmpty(implantCollection.getDoctorId(), implantCollection.getHospitalId(),
						implantCollection.getLocationId())) {
					if (implantCollection.getDoctorId().toString().equals(doctorId)
							&& implantCollection.getHospitalId().toString().equals(hospitalId)
							&& implantCollection.getLocationId().toString().equals(locationId)) {

						implantCollection.setDiscarded(discarded);
						implantCollection.setUpdatedTime(new Date());
						implantRepository.save(implantCollection);
						response = new Implant();
						BeanUtil.map(implantCollection, response);
					} else {
						logger.warn("Invalid Doctor Id, Hospital Id, Or Location Id");
						throw new BusinessException(ServiceError.InvalidInput,
								"Invalid Doctor Id, Hospital Id, Or Location Id");
					}
				} else {
					implantCollection.setDiscarded(discarded);
					implantCollection.setUpdatedTime(new Date());
					implantRepository.save(implantCollection);
					response = new Implant();
					BeanUtil.map(implantCollection, response);
				}
			} else {
				logger.warn("Implant  not found!");
				throw new BusinessException(ServiceError.NoRecord, "Implant not found!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	public Cement addEditCement(Cement cement) {
		try {
			CementCollection cementCollection = new CementCollection();
			BeanUtil.map(cement, cementCollection);
			if (DPDoctorUtils.anyStringEmpty(cement.getId())) {
				cementCollection.setCreatedTime(new Date());
				if (!DPDoctorUtils.anyStringEmpty(cementCollection.getDoctorId())) {
					UserCollection userCollection = userRepository.findById(cementCollection.getDoctorId())
							.orElse(null);
					if (userCollection != null) {
						cementCollection
								.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
										+ userCollection.getFirstName());
					}
				} else {
					cementCollection.setCreatedBy("ADMIN");
				}
			} else {
				CementCollection oldCementCollection = cementRepository.findById(cementCollection.getId()).orElse(null);
				cementCollection.setCreatedBy(oldCementCollection.getCreatedBy());
				cementCollection.setCreatedTime(oldCementCollection.getCreatedTime());
				cementCollection.setDiscarded(oldCementCollection.getDiscarded());
			}
			cementCollection = cementRepository.save(cementCollection);
			BeanUtil.map(cementCollection, cement);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Adding Cement");
		}
		return cement;
	}

	@Override
	public Cement deleteCement(String id, String doctorId, String locationId, String hospitalId, Boolean discarded) {
		Cement response = null;
		try {
			CementCollection cementCollection = cementRepository.findById(new ObjectId(id)).orElse(null);
			if (cementCollection != null) {
				if (!DPDoctorUtils.anyStringEmpty(cementCollection.getDoctorId(), cementCollection.getHospitalId(),
						cementCollection.getLocationId())) {
					if (cementCollection.getDoctorId().toString().equals(doctorId)
							&& cementCollection.getHospitalId().toString().equals(hospitalId)
							&& cementCollection.getLocationId().toString().equals(locationId)) {

						cementCollection.setDiscarded(discarded);
						cementCollection.setUpdatedTime(new Date());
						cementRepository.save(cementCollection);
						response = new Cement();
						BeanUtil.map(cementCollection, response);
					} else {
						logger.warn("Invalid Doctor Id, Hospital Id, Or Location Id");
						throw new BusinessException(ServiceError.InvalidInput,
								"Invalid Doctor Id, Hospital Id, Or Location Id");
					}
				} else {
					cementCollection.setDiscarded(discarded);
					cementCollection.setUpdatedTime(new Date());
					cementRepository.save(cementCollection);
					response = new Cement();
					BeanUtil.map(cementCollection, response);
				}
			} else {
				logger.warn("Cement not found!");
				throw new BusinessException(ServiceError.NoRecord, "Cement not found!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private List<Cement> getCustomGlobalCement(long page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded) {
		List<Cement> response = null;
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findAllById(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add(null);
				specialities.add("ALL");
			}

			AggregationResults<Cement> results = mongoTemplate.aggregate(
					DPDoctorUtils.createCustomGlobalAggregation(page, size, doctorId, locationId, hospitalId,
							updatedTime, discarded, null, null, specialities, null),
					CementCollection.class, Cement.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Cement");
		}
		return response;

	}

	@SuppressWarnings("unchecked")
	private List<Cement> getGlobalCement(long page, int size, String doctorId, String updatedTime, Boolean discarded) {
		List<Cement> response = null;
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findAllById(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add("ALL");
				specialities.add(null);
			}

			AggregationResults<Cement> results = mongoTemplate.aggregate(DPDoctorUtils.createGlobalAggregation(page,
					size, updatedTime, discarded, null, null, specialities, null), CementCollection.class,
					Cement.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Cement");
		}
		return response;
	}

	private List<Cement> getCustomCement(long page, int size, String doctorId, String locationId, String hospitalId,
			String updatedTime, Boolean discarded) {
		List<Cement> response = null;
		try {
			AggregationResults<Cement> results = mongoTemplate.aggregate(DPDoctorUtils.createCustomAggregation(page,
					size, doctorId, locationId, hospitalId, updatedTime, discarded, null, null, null),
					CementCollection.class, Cement.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Cement");
		}
		return response;
	}

	@Override
	public void emailDischargeSummaryForWeb(String dischargeSummeryId, String doctorId, String locationId,
			String hospitalId, String emailAddress) {
		MailResponse mailResponse = null;
		DischargeSummaryCollection dischargeSummaryCollection = null;
		MailAttachment mailAttachment = null;
		UserCollection user = null;
		PatientCollection patient = null;
		EmailTrackCollection emailTrackCollection = new EmailTrackCollection();
		try {
			dischargeSummaryCollection = dischargeSummaryRepository.findById(new ObjectId(dischargeSummeryId))
					.orElse(null);
			if (dischargeSummaryCollection != null) {

				user = userRepository.findById(dischargeSummaryCollection.getPatientId()).orElse(null);
				patient = patientRepository.findByUserIdAndLocationIdAndHospitalId(
						dischargeSummaryCollection.getPatientId(), dischargeSummaryCollection.getLocationId(),
						dischargeSummaryCollection.getHospitalId());
				user.setFirstName(patient.getLocalPatientName());
				emailTrackCollection.setDoctorId(dischargeSummaryCollection.getDoctorId());
				emailTrackCollection.setHospitalId(dischargeSummaryCollection.getHospitalId());
				emailTrackCollection.setLocationId(dischargeSummaryCollection.getLocationId());
				emailTrackCollection.setType(ComponentType.DISCHARGE_SUMMARY.getType());
				emailTrackCollection.setSubject("Discharge Summary");
				if (user != null) {
					emailTrackCollection.setPatientName(patient.getLocalPatientName());
					emailTrackCollection.setPatientId(user.getId());
				}

				JasperReportResponse jasperReportResponse = createJasper(dischargeSummaryCollection, patient, user,
						PrintSettingType.EMAIL.getType());
				mailAttachment = new MailAttachment();
				mailAttachment.setAttachmentName(FilenameUtils.getName(jasperReportResponse.getPath()));
				mailAttachment.setFileSystemResource(jasperReportResponse.getFileSystemResource());
				UserCollection doctorUser = userRepository.findById(dischargeSummaryCollection.getDoctorId())
						.orElse(null);
				LocationCollection locationCollection = locationRepository
						.findById(dischargeSummaryCollection.getLocationId()).orElse(null);

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
				mailResponse.setMailRecordCreatedDate(sdf.format(dischargeSummaryCollection.getCreatedTime()));
				mailResponse.setPatientName(user.getFirstName());
				emailTackService.saveEmailTrack(emailTrackCollection);

			} else {
				logger.warn("Discharge Summary  not found.Please check summaryId.");
				throw new BusinessException(ServiceError.NoRecord,
						"Discharge Summary not found.Please check summaryId.");
			}

			String body = mailBodyGenerator.generateEMREmailBody(mailResponse.getPatientName(),
					mailResponse.getDoctorName(), mailResponse.getClinicName(), mailResponse.getClinicAddress(),
					mailResponse.getMailRecordCreatedDate(), "Discharge Summary", "emrMailTemplate.vm");
			Boolean response = mailService.sendEmail(emailAddress,
					mailResponse.getDoctorName() + " sent you Discharge Summary", body,
					mailResponse.getMailAttachment());
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
	@Transactional
	public FlowsheetResponse addEditFlowSheets(AddEditFlowSheetRequest request) {

		JSONObject jsonObj = new JSONObject(request);
		System.out.println(jsonObj.toString(4)); // pretty print json

		DischargeSummaryCollection dischargeSummaryCollection = null;
		FlowsheetResponse response = null;
		FlowsheetCollection flowsheetCollection = null;
		UserCollection userCollection = null;
		try {

			if (request.getDoctorId() != null) {
				userCollection = userRepository.findById(new ObjectId(request.getDoctorId())).orElse(null);
			}
			if (request.getId() != null) {
				flowsheetCollection = flowsheetRepository.findById(new ObjectId(request.getId())).orElse(null);
				flowsheetCollection.setUpdatedTime(new Date());
			} else if (request.getDischargeSummaryId() != null) {
				flowsheetCollection = flowsheetRepository
						.findByDischargeSummaryId(new ObjectId(request.getDischargeSummaryId()));
				if (flowsheetCollection == null) {
					flowsheetCollection = new FlowsheetCollection();
					flowsheetCollection
							.setUniqueId(UniqueIdInitial.FLOW_SHEET.getInitial() + DPDoctorUtils.generateRandomId());
					flowsheetCollection.setCreatedTime(new Date());
					if (userCollection != null) {
						flowsheetCollection.setCreatedBy(
								(!DPDoctorUtils.anyStringEmpty(userCollection.getTitle()) ? userCollection.getTitle()
										: "") + userCollection.getFirstName());
					}
				}
				dischargeSummaryCollection = dischargeSummaryRepository
						.findById(new ObjectId(request.getDischargeSummaryId())).orElse(null);
				dischargeSummaryCollection.setFlowSheets(request.getFlowSheets());
				dischargeSummaryCollection.setMonitoringChart(request.getMonitoringChart());
				dischargeSummaryCollection = dischargeSummaryRepository.save(dischargeSummaryCollection);
				flowsheetCollection.setDischargeSummaryId(dischargeSummaryCollection.getId());
				flowsheetCollection.setDischargeSummaryUniqueEMRId(dischargeSummaryCollection.getUniqueEmrId());
			} else {
				flowsheetCollection = new FlowsheetCollection();

				flowsheetCollection
						.setUniqueId(UniqueIdInitial.FLOW_SHEET.getInitial() + DPDoctorUtils.generateRandomId());

				flowsheetCollection.setCreatedTime(new Date());
				if (userCollection != null) {
					flowsheetCollection.setCreatedBy(
							(!DPDoctorUtils.anyStringEmpty(userCollection.getTitle()) ? userCollection.getTitle() : "")
									+ userCollection.getFirstName());
				}
			}

			if (request.getDischargeSummaryId() != null) {
				dischargeSummaryCollection = dischargeSummaryRepository
						.findById(new ObjectId(request.getDischargeSummaryId())).orElse(null);

			} else {
				dischargeSummaryCollection = new DischargeSummaryCollection();
				dischargeSummaryCollection.setDoctorId(new ObjectId(request.getDoctorId()));
				dischargeSummaryCollection.setLocationId(new ObjectId(request.getLocationId()));
				dischargeSummaryCollection.setHospitalId(new ObjectId(request.getHospitalId()));
				dischargeSummaryCollection.setPatientId(new ObjectId(request.getPatientId()));
				dischargeSummaryCollection.setDiscarded(false);
				dischargeSummaryCollection.setFlowSheets(request.getFlowSheets());
				dischargeSummaryCollection.setMonitoringChart(request.getMonitoringChart());
				dischargeSummaryCollection = dischargeSummaryRepository.save(dischargeSummaryCollection);
			}
			flowsheetCollection.setDischargeSummaryId(dischargeSummaryCollection.getId());
			flowsheetCollection.setDoctorId(new ObjectId(request.getDoctorId()));
			flowsheetCollection.setLocationId(new ObjectId(request.getLocationId()));
			flowsheetCollection.setHospitalId(new ObjectId(request.getHospitalId()));
			flowsheetCollection.setPatientId(new ObjectId(request.getPatientId()));
			flowsheetCollection.setFlowSheets(request.getFlowSheets());
			flowsheetCollection.setMonitoringChart(request.getMonitoringChart());
			flowsheetCollection = flowsheetRepository.save(flowsheetCollection);
			if (flowsheetCollection != null) {
				response = new FlowsheetResponse();
				BeanUtil.map(flowsheetCollection, response);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	@Transactional
	public List<FlowsheetResponse> getFlowSheets(String doctorId, String locationId, String hospitalId,
			String patientId, int page, int size, String updatedTime, Boolean discarded) {
		List<FlowsheetResponse> response = null;
		try {
			ObjectId patientObjectId = null, doctorObjectId = null, locationObjectId = null, hospitalObjectId = null;
			if (!DPDoctorUtils.anyStringEmpty(patientId))
				patientObjectId = new ObjectId(patientId);
			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				doctorObjectId = new ObjectId(doctorId);
			if (!DPDoctorUtils.anyStringEmpty(locationId))
				locationObjectId = new ObjectId(locationId);
			if (!DPDoctorUtils.anyStringEmpty(hospitalId))
				hospitalObjectId = new ObjectId(hospitalId);

			Criteria criteria = new Criteria("updatedTime").gt(new Date(Long.parseLong(updatedTime))).and("patientId")
					.is(patientObjectId);
			if (!DPDoctorUtils.anyStringEmpty(locationId))
				criteria.and("locationId").is(locationObjectId);
			if (!DPDoctorUtils.anyStringEmpty(hospitalId))
				criteria.and("hospitalId").is(hospitalObjectId);
			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				criteria.and("doctorId").is(doctorObjectId);

			if (!discarded)
				criteria.and("discarded").is(discarded);
			Aggregation aggregation = null;

			if (size > 0)
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")), Aggregation.skip((page) * size),
						Aggregation.limit(size));
			else
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));

			AggregationResults<FlowsheetResponse> aggregationResults = mongoTemplate.aggregate(aggregation,
					FlowsheetCollection.class, FlowsheetResponse.class);
			response = aggregationResults.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error while getting flow sheets : " + e.getCause().getMessage());
			throw new BusinessException(ServiceError.Unknown,
					"Error while getting flow sheets : " + e.getCause().getMessage());
		}
		return response;
	}

	@Override
	@Transactional
	public FlowsheetResponse getFlowSheetsById(String id) {
		FlowsheetResponse response = null;
		FlowsheetCollection flowsheetCollection = null;
		try {
			if (DPDoctorUtils.anyStringEmpty(id)) {
				throw new BusinessException(ServiceError.InvalidInput, "Id is null");
			}

			flowsheetCollection = flowsheetRepository.findById(new ObjectId(id)).orElse(null);
			if (flowsheetCollection == null) {
				throw new BusinessException(ServiceError.NoRecord, "Record not found");
			}
			response = new FlowsheetResponse();
			BeanUtil.map(flowsheetCollection, response);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error while getting flow sheets : " + e.getCause().getMessage());
			throw new BusinessException(ServiceError.Unknown,
					"Error while getting flow sheets : " + e.getCause().getMessage());
		}
		return response;
	}

	private void getFlowsheetJasper(List<FlowSheet> flowSheets, List<MonitoringChart> monitoringChart,
			Map<String, Object> parameters) {

		String pattern = "dd/MM/yyyy hh.mm a";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		List<FlowSheetJasperBean> jasperBeans = null;
		List<MonitoringChartJasperBean> monitorBeans = null;
		if (flowSheets != null) {

			for (FlowSheet flowsheet : flowSheets) {
				jasperBeans = new ArrayList<FlowSheetJasperBean>();
				FlowSheetJasperBean jasperBean = null;
				int i = 1;

				jasperBean = new FlowSheetJasperBean();
				jasperBean.setNo(i);
				if (!DPDoctorUtils.anyStringEmpty(flowsheet.getAdvice())) {
					jasperBean.setAdvice("<b>Advice :-    </b>" + flowsheet.getAdvice());
				}
				if (!DPDoctorUtils.anyStringEmpty(flowsheet.getComplaint())) {
					jasperBean.setComplaint("<b>Complaint :- </b>" + flowsheet.getComplaint());
				}

				if (!DPDoctorUtils.anyStringEmpty(flowsheet.getDiagnosis())) {
					jasperBean.setDiagnosis("<b>Diagnosis :- </b>" + flowsheet.getDiagnosis());
				}
				if (!DPDoctorUtils.anyStringEmpty(flowsheet.getReferTo())) {
					jasperBean.setReferTo("<b>ReferTo :- </b>" + flowsheet.getReferTo());
				}

				if (flowsheet.getMedication() != null) {
					parameters.put("MedicineTitle", "Medicine List :");
					List<DBObject> dbObjects = new ArrayList<DBObject>();
					for (Medication res : flowsheet.getMedication()) {
						DBObject dbObject = new BasicDBObject();
						if (!DPDoctorUtils.allStringsEmpty(res.getDrugName()))
							dbObject.put("drugName", res.getDrugName());
						if (!DPDoctorUtils.allStringsEmpty(res.getFrequency()))
							dbObject.put("frequency", res.getFrequency());
						else
							dbObject.put("frequency", "--");

						dbObjects.add(dbObject);
					}
					parameters.put("vaccination", dbObjects);
				}

				if (flowsheet.getDate() != null) {
					if (flowsheet.getDate() != 0) {
						jasperBean.setDate(simpleDateFormat.format(new Date(flowsheet.getDate())));
					}
				}
				String field = "";

				field = field + (!DPDoctorUtils.anyStringEmpty(flowsheet.getPulse())
						? "Pulse (" + VitalSignsUnit.PULSE.getUnit() + ") : " + flowsheet.getPulse()
						: "");

				field = field + (!DPDoctorUtils.anyStringEmpty(flowsheet.getWeight(), field) ? ", " : "")
						+ (!DPDoctorUtils.anyStringEmpty(flowsheet.getWeight())
								? "Weight (" + VitalSignsUnit.WEIGHT.getUnit() + ") : " + flowsheet.getWeight()
								: "");

				field = field + (!DPDoctorUtils.anyStringEmpty(flowsheet.getBsa(), field) ? ", " : "")
						+ (!DPDoctorUtils.anyStringEmpty(flowsheet.getBsa()) ? "BSA (" + VitalSignsUnit.BSA.getUnit()
								+ ") : " + String.format("%.3f", Double.parseDouble(flowsheet.getBsa())) : "");

				field = field + (!DPDoctorUtils.anyStringEmpty(flowsheet.getTemperature(), field) ? ", " : "")
						+ (!DPDoctorUtils.anyStringEmpty(flowsheet.getTemperature())
								? "Temp (" + VitalSignsUnit.TEMPERATURE.getUnit() + ") : " + flowsheet.getTemperature()
								: "");

				field = field + (!DPDoctorUtils.anyStringEmpty(flowsheet.getHeight(), field) ? ", " : "")
						+ (!DPDoctorUtils.anyStringEmpty(flowsheet.getHeight())
								? "Height (" + VitalSignsUnit.HEIGHT.getUnit() + ") : " + flowsheet.getHeight()
								: "");

				field = field + (!DPDoctorUtils.anyStringEmpty(flowsheet.getBreathing(), field) ? ", " : "")
						+ (!DPDoctorUtils.anyStringEmpty(flowsheet.getBreathing())
								? "Resp Rate (" + VitalSignsUnit.BREATHING.getUnit() + ") : " + flowsheet.getBreathing()
								: "");

				field = field
						+ (!DPDoctorUtils.anyStringEmpty(flowsheet.getDiastolic(), flowsheet.getSystolic(), field)
								? ", "
								: "")
						+ (!DPDoctorUtils.anyStringEmpty(flowsheet.getDiastolic(), flowsheet.getSystolic())
								? "B. P. (" + VitalSignsUnit.BLOODPRESSURE.getUnit() + ") : " + flowsheet.getSystolic()
										+ "/" + flowsheet.getDiastolic() + ""
								: "");

				field = field + (!DPDoctorUtils.anyStringEmpty(flowsheet.getBmi(), field) ? ", " : "")
						+ (!DPDoctorUtils.anyStringEmpty(flowsheet.getBmi()) ? " BMI (" + VitalSignsUnit.BMI.getUnit()
								+ ") : " + String.format("%.3f", Double.parseDouble(flowsheet.getBmi())) : "");

				field = field + (!DPDoctorUtils.anyStringEmpty(flowsheet.getSpo2(), field) ? ", " : "")
						+ (!DPDoctorUtils.anyStringEmpty(flowsheet.getSpo2())
								? "Spo2 (" + VitalSignsUnit.SPO2.getUnit() + ") : " + flowsheet.getSpo2()
								: "");

				field = field + (!DPDoctorUtils.anyStringEmpty(flowsheet.getiBP(), field) ? ", " : "")
						+ (!DPDoctorUtils.anyStringEmpty(flowsheet.getiBP())
								? "IBP (" + VitalSignsUnit.IBP.getUnit() + ") : " + flowsheet.getiBP()
								: "");

				field = field + (!DPDoctorUtils.anyStringEmpty(flowsheet.getcVP(), field) ? ", " : "")
						+ (!DPDoctorUtils.anyStringEmpty(flowsheet.getcVP())
								? "CVP (" + VitalSignsUnit.CVP.getUnit() + ") : " + flowsheet.getcVP()
								: "");

				field = field + (!DPDoctorUtils.anyStringEmpty(flowsheet.getFiO2(), field) ? ", " : "")
						+ (!DPDoctorUtils.anyStringEmpty(flowsheet.getFiO2())
								? "FiO2 (" + VitalSignsUnit.FIO2.getUnit() + ") : " + flowsheet.getFiO2()
								: "");
				field = field + (!DPDoctorUtils.anyStringEmpty(flowsheet.getVenhlatorMode(), field) ? ", " : "")
						+ (!DPDoctorUtils.anyStringEmpty(flowsheet.getVenhlatorMode()) ? "VenhlatorMode ("
								+ VitalSignsUnit.VENTILATION_MODE.getUnit() + ") : " + flowsheet.getVenhlatorMode()
								: "");

				field = field + (!DPDoctorUtils.anyStringEmpty(flowsheet.getUrineOutput(), field) ? ", " : "")
						+ (!DPDoctorUtils.anyStringEmpty(flowsheet.getUrineOutput())
								? "UrineOutput (" + VitalSignsUnit.URINE.getUnit() + ") : " + flowsheet.getUrineOutput()
								: "");

				field = field + (!DPDoctorUtils.anyStringEmpty(flowsheet.getFeeding(), field) ? ", " : "")
						+ (!DPDoctorUtils.anyStringEmpty(flowsheet.getFeeding())
								? "Feeding (" + VitalSignsUnit.SPO2.getUnit() + ") : " + flowsheet.getFeeding()
								: "");

				field = field + (!DPDoctorUtils.anyStringEmpty(flowsheet.getOtherVitals(), field) ? ", " : "")
						+ (!DPDoctorUtils.anyStringEmpty(flowsheet.getOtherVitals())
								? "OtherVitals (" + VitalSignsUnit.SPO2.getUnit() + ") : " + flowsheet.getOtherVitals()
								: "");

				field = field + (!DPDoctorUtils.anyStringEmpty(flowsheet.getRylesTubeOralIntake(), field) ? ", " : "")
						+ (!DPDoctorUtils.anyStringEmpty(flowsheet.getRylesTubeOralIntake())
								? "Ryles Tube Oral Intake : " + flowsheet.getRylesTubeOralIntake()
								: "");

				field = field + (!DPDoctorUtils.anyStringEmpty(flowsheet.getTracheostomySuction(), field) ? ", " : "")
						+ (!DPDoctorUtils.anyStringEmpty(flowsheet.getTracheostomySuction())
								? "Tracheostomy Suction : " + flowsheet.getTracheostomySuction()
								: "");

				field = field + (!DPDoctorUtils.anyStringEmpty(flowsheet.getBloodSugar(), field) ? ", " : "")
						+ (!DPDoctorUtils.anyStringEmpty(flowsheet.getBloodSugar()) ? "Blood Sugar ("
								+ VitalSignsUnit.BLOODSUGAR.getUnit() + ") : " + flowsheet.getBloodSugar() : "");

				jasperBean.setExamination(field);

				i++;
				jasperBeans.add(jasperBean);
			}

			parameters.put("flowsheet", jasperBeans);
		}

		if (monitoringChart != null){
			pattern = "dd/MM/yyyy hh.mm a";
			simpleDateFormat = new SimpleDateFormat(pattern);
			monitorBeans = new ArrayList<MonitoringChartJasperBean>();
			MonitoringChartJasperBean jasperBean = null;
			int i = 1;

			for (MonitoringChart flowsheet : monitoringChart) {

				jasperBean = new MonitoringChartJasperBean();
				jasperBean.setNo(i);
				if (!DPDoctorUtils.anyStringEmpty(flowsheet.getNurseName())) {
					jasperBean.setNurseName("<b>Nurse Name :-    </b>" + flowsheet.getNurseName());
				}
				if (flowsheet.getTime() != null) {
					jasperBean.setTime("<b>Time:- </b>" + flowsheet.getTime().getFromTime().toString());
				}
				if (!DPDoctorUtils.anyStringEmpty(flowsheet.getOutputDrain())) {
					jasperBean.setOutputDrain("<b>Output Drain :- </b>" + flowsheet.getOutputDrain());
				}
				if (!DPDoctorUtils.anyStringEmpty(flowsheet.getAnySpecialEventsAndStatDrugs())) {
					jasperBean.setAnySpecialEventsAndStatDrugs(
							"<b>Any Special Events & Stat Drugs :- </b>" + flowsheet.getAnySpecialEventsAndStatDrugs());
				}

				if (!DPDoctorUtils.anyStringEmpty(flowsheet.getDiagnosis())) {
					jasperBean.setDiagnosis("<b>Diagnosis :- </b>" + flowsheet.getDiagnosis());
				}
				if (!DPDoctorUtils.anyStringEmpty(flowsheet.getReferanceDone())) {
					jasperBean.setReferanceDone("<b>Reference Done :- </b>" + flowsheet.getReferanceDone());
				}

				String field = "";

				field = field
						+ (!DPDoctorUtils.anyStringEmpty(flowsheet.getIntake()) ? "Intake : " + flowsheet.getIntake()
								: "");

				field = field + (!DPDoctorUtils.anyStringEmpty(flowsheet.gethR(), field) ? ", " : "")
						+ (!DPDoctorUtils.anyStringEmpty(flowsheet.gethR()) ? "HR : " + flowsheet.gethR() : "");

				field = field + (!DPDoctorUtils.anyStringEmpty(flowsheet.getbP(), field) ? ", " : "")
						+ (!DPDoctorUtils.anyStringEmpty(flowsheet.getbP()) ? "BP : " + flowsheet.getbP() : "");

				field = field + (!DPDoctorUtils.anyStringEmpty(flowsheet.getsPO2(), field) ? ", " : "")
						+ (!DPDoctorUtils.anyStringEmpty(flowsheet.getsPO2())
								? "Spo2 (" + VitalSignsUnit.SPO2.getUnit() + ") : " + flowsheet.getsPO2()
								: "");

				jasperBean.setExamination(field);

				i++;
				monitorBeans.add(jasperBean);
			}

			parameters.put("monitoringChart", monitorBeans);
		}
	}

	private JasperReportResponse createJasperForFlowSheet(FlowsheetCollection flowsheetCollection,
			PatientCollection patient, UserCollection user, String printSettingType)
			throws NumberFormatException, IOException, ParseException {
		JasperReportResponse response = null;
		Boolean showMonitor = false;
		Map<String, Object> parameters = new HashMap<String, Object>();
		String pattern = "dd/MM/yyyy";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		List<FlowSheetJasperBean> jasperBeans = null;
		List<MonitoringChartJasperBean> monitorBeans = null;
		PrintSettingsCollection printSettings = null;
		printSettings = printSettingsRepository
				.findByDoctorIdAndLocationIdAndHospitalIdAndComponentTypeAndPrintSettingType(
						flowsheetCollection.getDoctorId(), flowsheetCollection.getLocationId(),
						flowsheetCollection.getHospitalId(), ComponentType.ALL.getType(), printSettingType);
		if (printSettings == null) {
			List<PrintSettingsCollection> printSettingsCollections = printSettingsRepository
					.findListByDoctorIdAndLocationIdAndHospitalIdAndComponentTypeAndPrintSettingType(
							flowsheetCollection.getDoctorId(), flowsheetCollection.getLocationId(),
							flowsheetCollection.getHospitalId(), ComponentType.ALL.getType(),
							PrintSettingType.DEFAULT.getType(), new Sort(Sort.Direction.DESC, "updatedTime"));
			if (!DPDoctorUtils.isNullOrEmptyList(printSettingsCollections))
				printSettings = printSettingsCollections.get(0);
		}
		if (printSettings == null) {
			printSettings = new PrintSettingsCollection();
			DefaultPrintSettings defaultPrintSettings = new DefaultPrintSettings();
			BeanUtil.map(defaultPrintSettings, printSettings);
		}

		/*
		 * if (dischargeSummaryCollection.getAdmissionDate() != null) {
		 * parameters.put("dOA", "<b>Date of Admission:-</b>" +
		 * simpleDateFormat.format(dischargeSummaryCollection.getAdmissionDate() )); }
		 * if (dischargeSummaryCollection.getDischargeDate() != null) {
		 * parameters.put("dOD", "<b>Date of Discharge:-</b>" +
		 * simpleDateFormat.format(dischargeSummaryCollection.getDischargeDate() )); }
		 * if (dischargeSummaryCollection.getOperationDate() != null) {
		 * parameters.put("operationDate", "<b>Date of Operation:-</b>" +
		 * simpleDateFormat.format(dischargeSummaryCollection.getOperationDate() )); }
		 * 
		 * if (!DPDoctorUtils.allStringsEmpty(dischargeSummaryCollection.
		 * getDiagnosis())) { parameters.put("diagnosis",
		 * dischargeSummaryCollection.getDiagnosis()); }
		 */

		if (flowsheetCollection.getFlowSheets() != null && !flowsheetCollection.getFlowSheets().isEmpty()) {
			pattern = "dd/MM/yyyy hh.mm a";
			simpleDateFormat = new SimpleDateFormat(pattern);
			jasperBeans = new ArrayList<FlowSheetJasperBean>();
			FlowSheetJasperBean jasperBean = null;
			int i = 1;
			String contentLineStyle = (printSettings != null
					&& !DPDoctorUtils.anyStringEmpty(printSettings.getContentLineStyle()))
							? printSettings.getContentLineStyle()
							: LineStyle.INLINE.name();
			for (FlowSheet flowsheet : flowsheetCollection.getFlowSheets()) {

				jasperBean = new FlowSheetJasperBean();
				jasperBean.setNo(i);
				if (!DPDoctorUtils.anyStringEmpty(flowsheet.getAdvice())) {
					jasperBean.setAdvice("<b>Advice :-    </b>" + flowsheet.getAdvice());
				}
				if (!DPDoctorUtils.anyStringEmpty(flowsheet.getComplaint())) {
					jasperBean.setComplaint("<b>Complaint :- </b>" + flowsheet.getComplaint());
				}

				if (!DPDoctorUtils.anyStringEmpty(flowsheet.getDiagnosis())) {
					jasperBean.setDiagnosis("<b>Diagnosis :- </b>" + flowsheet.getDiagnosis());
				}
				if (!DPDoctorUtils.anyStringEmpty(flowsheet.getReferTo())) {
					jasperBean.setReferTo("<b>ReferTo :- </b>" + flowsheet.getReferTo());
				}

				if (flowsheet.getMedication() != null) {
					parameters.put("MedicineTitle", "Medicine List :");
					List<DBObject> dbObjects = new ArrayList<DBObject>();
					for (Medication res : flowsheet.getMedication()) {
						DBObject dbObject = new BasicDBObject();
						if (!DPDoctorUtils.allStringsEmpty(res.getDrugName()))
							dbObject.put("drugName", res.getDrugName());
						if (!DPDoctorUtils.allStringsEmpty(res.getFrequency()))
							dbObject.put("frequency", res.getFrequency());
						else
							dbObject.put("frequency", "--");

						dbObjects.add(dbObject);
					}
					parameters.put("vaccination", dbObjects);
				}

				if (flowsheet.getDate() != null) {
					if (flowsheet.getDate() != 0) {
						jasperBean.setDate(simpleDateFormat.format(new Date(flowsheet.getDate())));
					}
				}
				String field = "";

				field = field + (!DPDoctorUtils.anyStringEmpty(flowsheet.getPulse())
						? "Pulse (" + VitalSignsUnit.PULSE.getUnit() + ") : " + flowsheet.getPulse()
						: "");

				field = field + (!DPDoctorUtils.anyStringEmpty(flowsheet.getWeight(), field) ? ", " : "")
						+ (!DPDoctorUtils.anyStringEmpty(flowsheet.getWeight())
								? "Weight (" + VitalSignsUnit.WEIGHT.getUnit() + ") : " + flowsheet.getWeight()
								: "");

				field = field + (!DPDoctorUtils.anyStringEmpty(flowsheet.getBsa(), field) ? ", " : "")
						+ (!DPDoctorUtils.anyStringEmpty(flowsheet.getBsa()) ? "BSA (" + VitalSignsUnit.BSA.getUnit()
								+ ") : " + String.format("%.3f", Double.parseDouble(flowsheet.getBsa())) : "");

				field = field + (!DPDoctorUtils.anyStringEmpty(flowsheet.getTemperature(), field) ? ", " : "")
						+ (!DPDoctorUtils.anyStringEmpty(flowsheet.getTemperature())
								? "Temp (" + VitalSignsUnit.TEMPERATURE.getUnit() + ") : " + flowsheet.getTemperature()
								: "");

				field = field + (!DPDoctorUtils.anyStringEmpty(flowsheet.getHeight(), field) ? ", " : "")
						+ (!DPDoctorUtils.anyStringEmpty(flowsheet.getHeight())
								? "Height (" + VitalSignsUnit.HEIGHT.getUnit() + ") : " + flowsheet.getHeight()
								: "");

				field = field + (!DPDoctorUtils.anyStringEmpty(flowsheet.getBreathing(), field) ? ", " : "")
						+ (!DPDoctorUtils.anyStringEmpty(flowsheet.getBreathing())
								? "Resp Rate (" + VitalSignsUnit.BREATHING.getUnit() + ") : " + flowsheet.getBreathing()
								: "");

				field = field
						+ (!DPDoctorUtils.anyStringEmpty(flowsheet.getDiastolic(), flowsheet.getSystolic(), field)
								? ", "
								: "")
						+ (!DPDoctorUtils.anyStringEmpty(flowsheet.getDiastolic(), flowsheet.getSystolic())
								? "B. P. (" + VitalSignsUnit.BLOODPRESSURE.getUnit() + ") : " + flowsheet.getSystolic()
										+ "/" + flowsheet.getDiastolic() + ""
								: "");

				field = field + (!DPDoctorUtils.anyStringEmpty(flowsheet.getBmi(), field) ? ", " : "")
						+ (!DPDoctorUtils.anyStringEmpty(flowsheet.getBmi()) ? " BMI (" + VitalSignsUnit.BMI.getUnit()
								+ ") : " + String.format("%.3f", Double.parseDouble(flowsheet.getBmi())) : "");

				field = field + (!DPDoctorUtils.anyStringEmpty(flowsheet.getSpo2(), field) ? ", " : "")
						+ (!DPDoctorUtils.anyStringEmpty(flowsheet.getSpo2())
								? "Spo2 (" + VitalSignsUnit.SPO2.getUnit() + ") : " + flowsheet.getSpo2()
								: "");

				field = field + (!DPDoctorUtils.anyStringEmpty(flowsheet.getiBP(), field) ? ", " : "")
						+ (!DPDoctorUtils.anyStringEmpty(flowsheet.getiBP())
								? "IBP (" + VitalSignsUnit.IBP.getUnit() + ") : " + flowsheet.getiBP()
								: "");

				field = field + (!DPDoctorUtils.anyStringEmpty(flowsheet.getcVP(), field) ? ", " : "")
						+ (!DPDoctorUtils.anyStringEmpty(flowsheet.getcVP())
								? "CVP (" + VitalSignsUnit.CVP.getUnit() + ") : " + flowsheet.getcVP()
								: "");

				field = field + (!DPDoctorUtils.anyStringEmpty(flowsheet.getFiO2(), field) ? ", " : "")
						+ (!DPDoctorUtils.anyStringEmpty(flowsheet.getFiO2())
								? "FiO2 (" + VitalSignsUnit.FIO2.getUnit() + ") : " + flowsheet.getFiO2()
								: "");
				field = field + (!DPDoctorUtils.anyStringEmpty(flowsheet.getVenhlatorMode(), field) ? ", " : "")
						+ (!DPDoctorUtils.anyStringEmpty(flowsheet.getVenhlatorMode()) ? "VenhlatorMode ("
								+ VitalSignsUnit.VENTILATION_MODE.getUnit() + ") : " + flowsheet.getVenhlatorMode()
								: "");

				field = field + (!DPDoctorUtils.anyStringEmpty(flowsheet.getUrineOutput(), field) ? ", " : "")
						+ (!DPDoctorUtils.anyStringEmpty(flowsheet.getUrineOutput())
								? "UrineOutput (" + VitalSignsUnit.URINE.getUnit() + ") : " + flowsheet.getUrineOutput()
								: "");

				field = field + (!DPDoctorUtils.anyStringEmpty(flowsheet.getFeeding(), field) ? ", " : "")
						+ (!DPDoctorUtils.anyStringEmpty(flowsheet.getFeeding())
								? "Feeding (" + VitalSignsUnit.SPO2.getUnit() + ") : " + flowsheet.getFeeding()
								: "");

				field = field + (!DPDoctorUtils.anyStringEmpty(flowsheet.getOtherVitals(), field) ? ", " : "")
						+ (!DPDoctorUtils.anyStringEmpty(flowsheet.getOtherVitals())
								? "OtherVitals (" + VitalSignsUnit.SPO2.getUnit() + ") : " + flowsheet.getOtherVitals()
								: "");

				field = field + (!DPDoctorUtils.anyStringEmpty(flowsheet.getRylesTubeOralIntake(), field) ? ", " : "")
						+ (!DPDoctorUtils.anyStringEmpty(flowsheet.getRylesTubeOralIntake())
								? "Ryles Tube Oral Intake : " + flowsheet.getRylesTubeOralIntake()
								: "");

				field = field + (!DPDoctorUtils.anyStringEmpty(flowsheet.getTracheostomySuction(), field) ? ", " : "")
						+ (!DPDoctorUtils.anyStringEmpty(flowsheet.getTracheostomySuction())
								? "Tracheostomy Suction : " + flowsheet.getTracheostomySuction()
								: "");

				field = field + (!DPDoctorUtils.anyStringEmpty(flowsheet.getBloodSugar(), field) ? ", " : "")
						+ (!DPDoctorUtils.anyStringEmpty(flowsheet.getBloodSugar()) ? "Blood Sugar ("
								+ VitalSignsUnit.BLOODSUGAR.getUnit() + ") : " + flowsheet.getBloodSugar() : "");

				jasperBean.setExamination(field);

				i++;
				jasperBeans.add(jasperBean);
			}

			parameters.put("flowsheet", jasperBeans);
		}

		if (flowsheetCollection.getMonitoringChart() != null && !flowsheetCollection.getMonitoringChart().isEmpty()) {
			pattern = "dd/MM/yyyy hh.mm a";
			simpleDateFormat = new SimpleDateFormat(pattern);
			monitorBeans = new ArrayList<MonitoringChartJasperBean>();
			MonitoringChartJasperBean jasperBean = null;
			int i = 1;

			for (MonitoringChart flowsheet : flowsheetCollection.getMonitoringChart()) {

				jasperBean = new MonitoringChartJasperBean();
				jasperBean.setNo(i);
				if (!DPDoctorUtils.anyStringEmpty(flowsheet.getNurseName())) {
					jasperBean.setNurseName("<b>Nurse Name :-    </b>" + flowsheet.getNurseName());
				}
				if (flowsheet.getTime() != null) {
					jasperBean.setTime("<b>Time:- </b>" + flowsheet.getTime().getFromTime().toString());
				}
				if (!DPDoctorUtils.anyStringEmpty(flowsheet.getOutputDrain())) {
					jasperBean.setOutputDrain("<b>Output Drain :- </b>" + flowsheet.getOutputDrain());
				}
				if (!DPDoctorUtils.anyStringEmpty(flowsheet.getAnySpecialEventsAndStatDrugs())) {
					jasperBean.setAnySpecialEventsAndStatDrugs(
							"<b>Any Special Events & Stat Drugs :- </b>" + flowsheet.getAnySpecialEventsAndStatDrugs());
				}

				if (!DPDoctorUtils.anyStringEmpty(flowsheet.getDiagnosis())) {
					jasperBean.setDiagnosis("<b>Diagnosis :- </b>" + flowsheet.getDiagnosis());
				}
				if (!DPDoctorUtils.anyStringEmpty(flowsheet.getReferanceDone())) {
					jasperBean.setReferanceDone("<b>Reference Done :- </b>" + flowsheet.getReferanceDone());
				}

				String field = "";

				field = field
						+ (!DPDoctorUtils.anyStringEmpty(flowsheet.getIntake()) ? "Intake : " + flowsheet.getIntake()
								: "");

				field = field + (!DPDoctorUtils.anyStringEmpty(flowsheet.gethR(), field) ? ", " : "")
						+ (!DPDoctorUtils.anyStringEmpty(flowsheet.gethR()) ? "HR : " + flowsheet.gethR() : "");

				field = field + (!DPDoctorUtils.anyStringEmpty(flowsheet.getbP(), field) ? ", " : "")
						+ (!DPDoctorUtils.anyStringEmpty(flowsheet.getbP()) ? "BP : " + flowsheet.getbP() : "");

				field = field + (!DPDoctorUtils.anyStringEmpty(flowsheet.getsPO2(), field) ? ", " : "")
						+ (!DPDoctorUtils.anyStringEmpty(flowsheet.getsPO2())
								? "Spo2 (" + VitalSignsUnit.SPO2.getUnit() + ") : " + flowsheet.getsPO2()
								: "");

				jasperBean.setExamination(field);

				i++;
				monitorBeans.add(jasperBean);
			}

			parameters.put("monitoringChart", monitorBeans);
		}

//		List<DBObject> prescriptions = null;
//		if (! showMonitor) {
//			if (flowsheetCollection.getId() != null) {
//				prescriptions = new ArrayList<DBObject>();
//				for (MonitoringChart monitoringChart : flowsheetCollection.getMonitoringChart()) {
//					if (monitoringChart !=null) {
//						DBObject prescriptionItems = new BasicDBObject();
//						List<MonitoringChartJasperBean> prescriptionJasperDetails = getMonitoringChartJasperDetails( flowsheetCollection.getId().toString(),
//								   parameters,showMonitor, printSettings);
//						if (prescriptionJasperDetails != null && !prescriptionJasperDetails.isEmpty())
//							prescriptionItems.put("items", prescriptionJasperDetails);
//						if (prescriptionItems.toMap().size() > 1)
//							prescriptions.add(prescriptionItems);
//					}
//				}
//			}
//		}

//		if(flowsheetCollection.getMonitoringChart()!=null && !flowsheetCollection.getMonitoringChart().isEmpty()) {
//			monitorBeans=new ArrayList<MonitoringChartJasperBean>();
//			MonitoringChartJasperBean monitorBean = null;
//		int i = 1;
//			for(MonitoringChart monitoringchart:flowsheetCollection.getMonitoringChart())
//			{
//				monitorBean = new MonitoringChartJasperBean();
//				monitorBean.setNo(i);
//				
//				if (monitoringchart.getTime()!=null) {
//					monitorBean.setTime("<b>Time:-    </b>" + monitoringchart.getTime());
//				}
//				
//				if (!DPDoctorUtils.anyStringEmpty(monitoringchart.getIntake())) {
//					monitorBean.setIntake("<b>Intake :-    </b>" + monitoringchart.getIntake());
//				}
//				
//				if (!DPDoctorUtils.anyStringEmpty(monitoringchart.getOutputDrain())) {
//					monitorBean.setOutputDrain("<b>Bp :-    </b>" + monitoringchart.getOutputDrain());
//				}
//				
//				if (!DPDoctorUtils.anyStringEmpty(monitoringchart.getbP())) {
//					monitorBean.setOutputDrain("<b>Hr :-    </b>" + monitoringchart.gethR());
//				}
//				
//				if (!DPDoctorUtils.anyStringEmpty(monitoringchart.getbP())) {
//					monitorBean.setsPO2("<b>SPO2 :-    </b>" + monitoringchart.getsPO2());
//				}
//				if (!DPDoctorUtils.anyStringEmpty(monitoringchart.getDiagnosis())) {
//					monitorBean.setDiagnosis("<b>Diagnosis :- </b>" + monitoringchart.getDiagnosis());
//				}
//				if (!DPDoctorUtils.anyStringEmpty(monitoringchart.getReferanceDone())) {
//					monitorBean.setReferanceDone("<b>ReferTo :- </b>" + monitoringchart.getReferanceDone());
//				}
//				if (!DPDoctorUtils.anyStringEmpty(monitoringchart.getAnySpecialEventsAndStatDrugs())) {
//					monitorBean.setAnySpecialEventsAndStatDrugs("<b>AnySpecialEventsAndStatDrugs:-    </b>" + monitoringchart.getAnySpecialEventsAndStatDrugs());
//				}
//				

//				String monitor=" ";
//				
//				
//				
//				monitor = monitor + (!DPDoctorUtils.anyStringEmpty(monitoringchart.getTime().getFromTime().toString(), monitor) ? ", " : "")
//						+ (!DPDoctorUtils.anyStringEmpty(monitoringchart.getTime().getFromTime().toString())
//								? "FromTime (" + VitalSignsUnit.SPO2.getUnit() + ") : " + monitoringchart.getTime().getFromTime().toString()
//								: "");
//				
//				monitor = monitor + (!DPDoctorUtils.anyStringEmpty(monitoringchart.getTime().getToTime().toString(), monitor) ? ", " : "")
//						+ (!DPDoctorUtils.anyStringEmpty(monitoringchart.getTime().getToTime().toString())
//								? "ToTime (" + VitalSignsUnit.SPO2.getUnit() + ") : " + monitoringchart.getTime().getToTime().toString()
//								: "");
//				
//				monitor = monitor + (!DPDoctorUtils.anyStringEmpty(monitoringchart.getIntake(), monitor) ? ", " : "")
//						+ (!DPDoctorUtils.anyStringEmpty(monitoringchart.getIntake())
//								? "Intake (" + VitalSignsUnit.SPO2.getUnit() + ") : " + monitoringchart.getIntake()
//								: "");
//				
//				monitor = monitor + (!DPDoctorUtils.anyStringEmpty(monitoringchart.getOutputDrain(), monitor) ? ", " : "")
//						+ (!DPDoctorUtils.anyStringEmpty(monitoringchart.getOutputDrain())
//								? "Output/Drain (" + VitalSignsUnit.SPO2.getUnit() + ") : " + monitoringchart.getOutputDrain()
//								: "");
//				monitor = monitor + (!DPDoctorUtils.anyStringEmpty(monitoringchart.getbP(), monitor) ? ", " : "")
//						+ (!DPDoctorUtils.anyStringEmpty(monitoringchart.getbP())
//								? "Bp (" + VitalSignsUnit.SPO2.getUnit() + ") : " + monitoringchart.getbP()
//								: "");
//				monitor = monitor + (!DPDoctorUtils.anyStringEmpty(monitoringchart.gethR(), monitor) ? ", " : "")
//						+ (!DPDoctorUtils.anyStringEmpty(monitoringchart.gethR())
//								? "HR (" + VitalSignsUnit.SPO2.getUnit() + ") : " + monitoringchart.gethR()
//								: "");
//				monitor = monitor + (!DPDoctorUtils.anyStringEmpty(monitoringchart.getsPO2(), monitor) ? ", " : "")
//						+ (!DPDoctorUtils.anyStringEmpty(monitoringchart.getsPO2())
//								? "SPO2 (" + VitalSignsUnit.SPO2.getUnit() + ") : " + monitoringchart.getsPO2()
//								: "");
//				
//				monitor = monitor + (!DPDoctorUtils.anyStringEmpty(monitoringchart.getAnySpecialEventsAndStatDrugs(), monitor) ? ", " : "")
//						+ (!DPDoctorUtils.anyStringEmpty(monitoringchart.getAnySpecialEventsAndStatDrugs())
//								? "AnySpecialEvents&StatDrugs (" + VitalSignsUnit.SPO2.getUnit() + ") : " + monitoringchart.getAnySpecialEventsAndStatDrugs()
//								: "");
//				
//				

//				i++;
//				monitorBeans.add(monitorBean);
//				
//			}
//			parameters.put("monitoringChart", monitorBeans);
//		}

		parameters.put("contentLineSpace",
				(printSettings != null && !DPDoctorUtils.anyStringEmpty(printSettings.getContentLineStyle()))
						? printSettings.getContentLineSpace()
						: LineSpace.SMALL.name());
		patientVisitService.generatePatientDetails(
				(printSettings != null && printSettings.getHeaderSetup() != null
						? printSettings.getHeaderSetup().getPatientDetails()
						: null),
				patient,
				"<b>DIS-ID: </b>"
						+ (flowsheetCollection.getUniqueId() != null ? flowsheetCollection.getUniqueId() : "--"),
				patient.getLocalPatientName(), user.getMobileNumber(), parameters, flowsheetCollection.getUpdatedTime(),
				printSettings.getHospitalUId(), printSettings.getIsPidHasDate());
		patientVisitService.generatePrintSetup(parameters, printSettings, flowsheetCollection.getDoctorId());
		String pdfName = (patient != null ? patient.getLocalPatientName() : "") + "DISCHARGE-SUMMARY-FLOWSHEET-"
				+ (!DPDoctorUtils.anyStringEmpty(flowsheetCollection.getUniqueId()) ? flowsheetCollection.getUniqueId()
						: "")
				+ new Date().getTime();

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
		response = jasperReportService.createPDF(ComponentType.FLOW_SHEET, parameters, dischargeSummaryReportA4FileName,
				layout, pageSize, topMargin, bottonMargin, leftMargin, rightMargin,
				Integer.parseInt(parameters.get("contentFontSize").toString()), pdfName.replaceAll("\\s+", ""));

		return response;

	}

	private List<MonitoringChartJasperBean> getMonitoringChartJasperDetails(FlowsheetCollection prescriptionCollection,
			Map<String, Object> parameters, Boolean isMonitoringChart, PrintSettingsCollection printSettings) {
//		FlowsheetCollection prescriptionCollection = null;
		List<MonitoringChartJasperBean> prescriptionItems = new ArrayList<MonitoringChartJasperBean>();
		try {

//			prescriptionCollection = flowsheetRepository.findById(new ObjectId(prescriptionId)).orElse(null);
			Boolean showIntake = false, showOutputDrain = false, showBp = false, showHr = false, showSpo2 = false,
					showanySpecialEventsAndStatDrugs = false;

			if (prescriptionCollection.getMonitoringChart() != null) {

				if (!isMonitoringChart) {

					int no = 0;
					String Intake = null, OutputDrain = null, Bp = null, hR = null, sPO2 = null,
							anySpecialEventsAndStatDrugs = null;
					if (prescriptionCollection.getMonitoringChart() != null)
						for (MonitoringChart prescriptionItem : prescriptionCollection.getMonitoringChart()) {
							if (prescriptionItem != null) {
								if (prescriptionItem.getIntake() != null) {
									Intake = prescriptionItem.getIntake() != null ? prescriptionItem.getIntake() : "";
									showIntake = true;
								}

								if (prescriptionItem.getOutputDrain() != null) {
									OutputDrain = prescriptionItem.getOutputDrain() != null
											? prescriptionItem.getOutputDrain()
											: "";
									showOutputDrain = true;
								}
								if (prescriptionItem.getbP() != null) {
									Bp = prescriptionItem.getbP() != null ? prescriptionItem.getbP() : "";
									showBp = true;
								}
								if (prescriptionItem.gethR() != null) {
									hR = prescriptionItem.gethR() != null ? prescriptionItem.gethR() : "";
									showHr = true;
								}

								if (prescriptionItem.getsPO2() != null) {
									sPO2 = prescriptionItem.getsPO2() != null ? prescriptionItem.getsPO2() : "";
									showSpo2 = true;
								}

								if (prescriptionItem.getAnySpecialEventsAndStatDrugs() != null) {
									anySpecialEventsAndStatDrugs = prescriptionItem
											.getAnySpecialEventsAndStatDrugs() != null
													? prescriptionItem.getAnySpecialEventsAndStatDrugs()
													: "";
									showanySpecialEventsAndStatDrugs = true;
								}

							}

							MonitoringChartJasperBean prescriptionJasperDetails = null;
							if (printSettings.getContentSetup() != null) {
								if (printSettings.getContentSetup().getInstructionAlign() != null && printSettings
										.getContentSetup().getInstructionAlign().equals(FieldAlign.HORIZONTAL)) {

									prescriptionJasperDetails = new MonitoringChartJasperBean(++no,
											!DPDoctorUtils.anyStringEmpty(Intake) ? Intake : "--",
											!DPDoctorUtils.anyStringEmpty(OutputDrain) ? OutputDrain : "--",
											!DPDoctorUtils.anyStringEmpty(Bp) ? Bp : "--",
											!DPDoctorUtils.anyStringEmpty(hR) ? hR : "--",
											!DPDoctorUtils.anyStringEmpty(hR) ? hR : "--",
											!DPDoctorUtils.anyStringEmpty(sPO2) ? sPO2 : "--",
											!DPDoctorUtils.anyStringEmpty(anySpecialEventsAndStatDrugs)
													? anySpecialEventsAndStatDrugs
													: "--");
								}

								prescriptionItems.add(prescriptionJasperDetails);
							}
						}
				}

				parameters.put("showIntake", showIntake);
				parameters.put("showOutputDrain", showOutputDrain);
				parameters.put("showBp", showBp);
				parameters.put("showHr", showHr);
				parameters.put("showSpo2", showSpo2);
				parameters.put("showSpo2", showSpo2);
				parameters.put("showanySpecialEventsAndStatDrugs", showanySpecialEventsAndStatDrugs);
			}

			else {
				logger.warn("Prescription not found.Please check prescriptionId.");
				throw new BusinessException(ServiceError.Unknown,
						"Prescription not found.Please check prescriptionId.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}

		return prescriptionItems;
	}

	@Override
	@Transactional
	public Diagram addEditDiagram(Diagram diagram) {
		try {
			if (diagram.getDiagram() != null) {
				String path = "dischargeSummary" + File.separator + "diagrams";
				diagram.getDiagram().setFileName(diagram.getDiagram().getFileName() + new Date().getTime());
				ImageURLResponse imageURLResponse = fileManager.saveImageAndReturnImageUrl(diagram.getDiagram(), path,
						false);
				diagram.setDiagramUrl(imageURLResponse.getImageUrl());

			}
			DiagramsCollection diagramsCollection = new DiagramsCollection();
			BeanUtil.map(diagram, diagramsCollection);
			if (DPDoctorUtils.allStringsEmpty(diagram.getDoctorId()))
				diagramsCollection.setDoctorId(null);
			if (DPDoctorUtils.allStringsEmpty(diagram.getLocationId()))
				diagramsCollection.setLocationId(null);
			if (DPDoctorUtils.allStringsEmpty(diagram.getHospitalId()))
				diagramsCollection.setHospitalId(null);

			if (DPDoctorUtils.anyStringEmpty(diagramsCollection.getId())) {
				diagramsCollection.setCreatedTime(new Date());
				if (!DPDoctorUtils.anyStringEmpty(diagramsCollection.getDoctorId())) {
					UserCollection userCollection = userRepository.findById(diagramsCollection.getDoctorId())
							.orElse(null);
					if (userCollection != null) {
						diagramsCollection
								.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
										+ userCollection.getFirstName());
					}
				} else {
					diagramsCollection.setCreatedBy("ADMIN");
				}
			} else {
				DiagramsCollection oldDiagramsCollection = diagramsRepository.findById(diagramsCollection.getId())
						.orElse(null);
				diagramsCollection.setCreatedBy(oldDiagramsCollection.getCreatedBy());
				diagramsCollection.setCreatedTime(oldDiagramsCollection.getCreatedTime());
				diagramsCollection.setDiscarded(oldDiagramsCollection.getDiscarded());
				if (diagram.getDiagram() == null) {
					diagramsCollection.setDiagramUrl(oldDiagramsCollection.getDiagramUrl());
					diagramsCollection.setFileExtension(oldDiagramsCollection.getFileExtension());
				}
			}
			diagramsCollection = diagramsRepository.save(diagramsCollection);
			BeanUtil.map(diagramsCollection, diagram);
			diagram.setDiagram(null);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			try {
				mailService.sendExceptionMail("Backend Business Exception :: While adding/editing diagram",
						e.getMessage());
			} catch (MessagingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return diagram;
	}

	@Override
	public String uploadDischargeDiagram(DoctorLabReportUploadRequest request) {

		String response = null;
		try {
			Date createdTime = null;

			createdTime = new Date();

			FileDetails fileDetail = request.getFileDetails();

			String path = "dischargeSummary" + File.separator + "diagram";

			String fileName = fileDetail.getFileName().replaceFirst("." + fileDetail.getFileExtension(), "");
			String recordPath = path + File.separator + fileName + createdTime.getTime() + "."
					+ fileDetail.getFileExtension();
			fileManager.saveRecordBase64(fileDetail, recordPath);
			if (!DPDoctorUtils.anyStringEmpty(recordPath))
				response = getFinalImageURL(recordPath);

		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "error while uploading discharge Diagram");

		}
		return response;

	}

	@Override
	public String uploadDischargeSummaryMultipart(FormDataBodyPart file) {
		String recordPath = null;
		try {
			Date createdTime = new Date();
			Double fileSizeInMB = 0.0;

			if (file != null) {
				String path = "dischargeSummary" + File.separator + "diagram";
				FormDataContentDisposition fileDetail = file.getFormDataContentDisposition();
				String fileExtension = FilenameUtils.getExtension(fileDetail.getFileName());
				String fileName = fileDetail.getFileName().replaceFirst("." + fileExtension, "");
				recordPath = path + File.separator + fileName + createdTime.getTime() + "." + fileExtension;
				fileManager.saveRecord(file, recordPath, fileSizeInMB, false);
			}
			if (!DPDoctorUtils.anyStringEmpty(recordPath))
				recordPath = getFinalImageURL(recordPath);

		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "error while uploading discharge diagram Multipart");

		}
		return recordPath;

	}

}
