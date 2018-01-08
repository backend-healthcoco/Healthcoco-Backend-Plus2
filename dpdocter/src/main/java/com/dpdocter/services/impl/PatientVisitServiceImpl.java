package com.dpdocter.services.impl;

import static com.dpdocter.enums.VisitedFor.CLINICAL_NOTES;
import static com.dpdocter.enums.VisitedFor.PRESCRIPTION;
import static com.dpdocter.enums.VisitedFor.REPORTS;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.IteratorUtils;
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
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.Age;
import com.dpdocter.beans.Appointment;
import com.dpdocter.beans.ClinicalNotes;
import com.dpdocter.beans.ClinicalNotesJasperDetails;
import com.dpdocter.beans.CustomAggregationOperation;
import com.dpdocter.beans.DefaultPrintSettings;
import com.dpdocter.beans.Diagram;
import com.dpdocter.beans.DoctorContactsResponse;
import com.dpdocter.beans.Drug;
import com.dpdocter.beans.EyePrescription;
import com.dpdocter.beans.GenericCode;
import com.dpdocter.beans.MailAttachment;
import com.dpdocter.beans.PatientDetails;
import com.dpdocter.beans.PatientTreatment;
import com.dpdocter.beans.PatientTreatmentJasperDetails;
import com.dpdocter.beans.PatientVisit;
import com.dpdocter.beans.PatientVisitLookupBean;
import com.dpdocter.beans.Prescription;
import com.dpdocter.beans.PrescriptionItem;
import com.dpdocter.beans.PrescriptionJasperDetails;
import com.dpdocter.beans.PrintSettingsText;
import com.dpdocter.beans.Records;
import com.dpdocter.beans.TestAndRecordData;
import com.dpdocter.beans.Treatment;
import com.dpdocter.beans.TreatmentFields;
import com.dpdocter.beans.WorkingHours;
import com.dpdocter.collections.AppointmentCollection;
import com.dpdocter.collections.ClinicalNotesCollection;
import com.dpdocter.collections.DiagnosticTestCollection;
import com.dpdocter.collections.DiagramsCollection;
import com.dpdocter.collections.DiseasesCollection;
import com.dpdocter.collections.DrugCollection;
import com.dpdocter.collections.EmailTrackCollection;
import com.dpdocter.collections.EyePrescriptionCollection;
import com.dpdocter.collections.HistoryCollection;
import com.dpdocter.collections.LocationCollection;
import com.dpdocter.collections.PatientCollection;
import com.dpdocter.collections.PatientTreatmentCollection;
import com.dpdocter.collections.PatientVisitCollection;
import com.dpdocter.collections.PrescriptionCollection;
import com.dpdocter.collections.PrintSettingsCollection;
import com.dpdocter.collections.RecordsCollection;
import com.dpdocter.collections.ReferencesCollection;
import com.dpdocter.collections.TreatmentServicesCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.enums.ComponentType;
import com.dpdocter.enums.FONTSTYLE;
import com.dpdocter.enums.FieldAlign;
import com.dpdocter.enums.LineSpace;
import com.dpdocter.enums.LineStyle;
import com.dpdocter.enums.UniqueIdInitial;
import com.dpdocter.enums.VisitedFor;
import com.dpdocter.enums.VitalSignsUnit;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.AppointmentRepository;
import com.dpdocter.repository.ClinicalNotesRepository;
import com.dpdocter.repository.DiagnosticTestRepository;
import com.dpdocter.repository.DiagramsRepository;
import com.dpdocter.repository.DiseasesRepository;
import com.dpdocter.repository.DrugRepository;
import com.dpdocter.repository.EyePrescriptionRepository;
import com.dpdocter.repository.HistoryRepository;
import com.dpdocter.repository.PatientTreamentRepository;
import com.dpdocter.repository.PatientVisitRepository;
import com.dpdocter.repository.PrescriptionRepository;
import com.dpdocter.repository.PrintSettingsRepository;
import com.dpdocter.repository.ReferenceRepository;
import com.dpdocter.repository.TreatmentServicesRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.request.AddMultipleDataRequest;
import com.dpdocter.request.AppointmentRequest;
import com.dpdocter.response.EyeTestJasperResponse;
import com.dpdocter.response.JasperReportResponse;
import com.dpdocter.response.MailResponse;
import com.dpdocter.response.PatientTreatmentResponse;
import com.dpdocter.response.PatientVisitLookupResponse;
import com.dpdocter.response.PatientVisitResponse;
import com.dpdocter.response.PrescriptionAddEditResponse;
import com.dpdocter.response.TestAndRecordDataResponse;
import com.dpdocter.services.AppointmentService;
import com.dpdocter.services.ClinicalNotesService;
import com.dpdocter.services.ContactsService;
import com.dpdocter.services.EmailTackService;
import com.dpdocter.services.JasperReportService;
import com.dpdocter.services.MailBodyGenerator;
import com.dpdocter.services.MailService;
import com.dpdocter.services.PatientTreatmentServices;
import com.dpdocter.services.PatientVisitService;
import com.dpdocter.services.PrescriptionServices;
import com.dpdocter.services.RecordsService;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import common.util.web.DPDoctorUtils;

@Service
public class PatientVisitServiceImpl implements PatientVisitService {

	private static Logger logger = Logger.getLogger(PatientVisitServiceImpl.class.getName());

	@Value(value = "${pdf.footer.text}")
	private String footerText;

	@Autowired
	private PatientTreatmentServices patientTreatmentServices;

	@Autowired
	private PatientVisitRepository patientVisitRepository;

	@Autowired
	private HistoryRepository historyRepository;

	@Autowired
	private ContactsService contactsService;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private ClinicalNotesService clinicalNotesService;

	@Autowired
	private PrescriptionServices prescriptionServices;

	@Autowired
	private PrescriptionRepository prescriptionRepository;

	@Autowired
	private ClinicalNotesRepository clinicalNotesRepository;

	@Autowired
	private RecordsService recordsService;

	@Autowired
	private DrugRepository drugRepository;

	@Autowired
	private JasperReportService jasperReportService;

	@Autowired
	private MailService mailService;

	@Autowired
	private PrintSettingsRepository printSettingsRepository;

	@Autowired
	private DiagnosticTestRepository diagnosticTestRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private EmailTackService emailTackService;

	@Autowired
	private DiagramsRepository diagramsRepository;

	@Autowired
	private ReferenceRepository referenceRepository;

	@Autowired
	private AppointmentService appointmentService;

	@Autowired
	private MailBodyGenerator mailBodyGenerator;

	@Autowired
	private PatientTreamentRepository patientTreamentRepository;

	@Autowired
	private TreatmentServicesRepository treatmentServicesRepository;

	@Autowired
	private DiseasesRepository diseasesRepository;

	@Autowired
	private AppointmentRepository appointmentRepository;

	@Value(value = "${image.path}")
	private String imagePath;

	@Value(value = "${jasper.print.visit.a4.fileName}")
	private String visitA4FileName;

	@Value(value = "${jasper.print.visit.clinicalnotes.a4.fileName}")
	private String visitClinicalNotesA4FileName;

	@Value(value = "${jasper.print.visit.prescription.a4.fileName}")
	private String visitPrescriptionA4FileName;

	@Value(value = "${jasper.print.visit.diagrams.a4.fileName}")
	private String visitDiagramsA4FileName;

	@Value(value = "${jasper.print.visit.a5.fileName}")
	private String visitA5FileName;

	@Value(value = "${jasper.print.visit.clinicalnotes.a5.fileName}")
	private String visitClinicalNotesA5FileName;

	@Value(value = "${jasper.print.visit.prescription.a5.fileName}")
	private String visitPrescriptionA5FileName;

	@Value(value = "${jasper.print.visit.diagrams.a5.fileName}")
	private String visitDiagramsA5FileName;

	@Value(value = "${jasper.print.prescription.subreport.a4.fileName}")
	private String prescriptionSubReportA4FileName;

	@Value(value = "${jasper.print.prescription.subreport.a5.fileName}")
	private String prescriptionSubReportA5FileName;

	@Autowired
	private EyePrescriptionRepository eyePrescriptionRepository;

	@Override
	@Transactional
	public String addRecord(Object details, VisitedFor visitedFor, String visitId) {
		PatientVisitCollection patientVisitCollection = new PatientVisitCollection();
		try {

			BeanUtil.map(details, patientVisitCollection);
			ObjectId id = patientVisitCollection.getId();

			if (visitId != null)
				patientVisitCollection = patientVisitRepository.findOne(new ObjectId(visitId));
			else
				patientVisitCollection.setId(null);

			if (patientVisitCollection.getId() == null) {
				patientVisitCollection.setCreatedTime(new Date());
				patientVisitCollection
						.setUniqueEmrId(UniqueIdInitial.VISITS.getInitial() + DPDoctorUtils.generateRandomId());
				UserCollection userCollection = userRepository.findOne(patientVisitCollection.getDoctorId());
				if (userCollection != null) {
					patientVisitCollection
							.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
									+ userCollection.getFirstName());
				}
			}

			if (patientVisitCollection.getVisitedFor() != null) {
				if (!patientVisitCollection.getVisitedFor().contains(visitedFor))
					patientVisitCollection.getVisitedFor().add(visitedFor);
			} else {
				List<VisitedFor> visitedforList = new ArrayList<VisitedFor>();
				visitedforList.add(visitedFor);
				patientVisitCollection.setVisitedFor(visitedforList);
			}

			patientVisitCollection.setVisitedTime(new Date());
			if (visitedFor.equals(VisitedFor.PRESCRIPTION)) {
				if (patientVisitCollection.getPrescriptionId() == null) {
					List<ObjectId> prescriptionId = new ArrayList<ObjectId>();
					prescriptionId.add(id);
					patientVisitCollection.setPrescriptionId(prescriptionId);
				} else {
					if (!patientVisitCollection.getPrescriptionId().contains(id))
						patientVisitCollection.getPrescriptionId().add(id);
				}

			} else if (visitedFor.equals(VisitedFor.CLINICAL_NOTES)) {
				if (patientVisitCollection.getClinicalNotesId() == null) {
					List<ObjectId> clinicalNotes = new ArrayList<ObjectId>();
					clinicalNotes.add(id);
					patientVisitCollection.setClinicalNotesId(clinicalNotes);
				} else {
					if (!patientVisitCollection.getClinicalNotesId().contains(id))
						patientVisitCollection.getClinicalNotesId().add(id);
				}
			} else if (visitedFor.equals(VisitedFor.REPORTS)) {
				if (patientVisitCollection.getRecordId() == null) {
					List<ObjectId> recordId = new ArrayList<ObjectId>();
					recordId.add(id);
					patientVisitCollection.setRecordId(recordId);
				} else {
					if (!patientVisitCollection.getRecordId().contains(id))
						patientVisitCollection.getRecordId().add(id);
				}
			} else if (visitedFor.equals(VisitedFor.TREATMENT)) {
				if (patientVisitCollection.getTreatmentId() == null) {
					List<ObjectId> treatmentId = new ArrayList<ObjectId>();
					treatmentId.add(id);
					patientVisitCollection.setTreatmentId(treatmentId);
				} else {
					if (!patientVisitCollection.getTreatmentId().add(id))
						patientVisitCollection.getTreatmentId().add(id);
				}
			}

			else if (visitedFor.equals(VisitedFor.EYE_PRESCRIPTION)) {
				if (patientVisitCollection.getEyePrescriptionId() == null) {
					patientVisitCollection.setEyePrescriptionId(id);
				}
			}
			patientVisitCollection.setUpdatedTime(new Date());
			patientVisitCollection = patientVisitRepository.save(patientVisitCollection);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error while saving patient visit record : " + e.getCause().getMessage());
			throw new BusinessException(ServiceError.Unknown,
					"Error while saving patient visit record : " + e.getCause().getMessage());
		}
		return patientVisitCollection.getId().toString();
	}

	@Override
	@Transactional
	public boolean addRecord(String patientId, String doctorId, String locationId, String hospitalId,
			VisitedFor visitedFor) {
		boolean response = false;
		try {
			ObjectId patientObjectId = null, doctorObjectId = null, locationObjectId = null, hospitalObjectId = null;
			if (!DPDoctorUtils.anyStringEmpty(patientId))
				patientObjectId = new ObjectId(patientId);
			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				doctorObjectId = new ObjectId(doctorId);
			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				doctorObjectId = new ObjectId(doctorId);
			if (!DPDoctorUtils.anyStringEmpty(locationId))
				locationObjectId = new ObjectId(locationId);
			if (!DPDoctorUtils.anyStringEmpty(hospitalId))
				hospitalObjectId = new ObjectId(hospitalId);

			PatientVisitCollection patientTrackCollection = patientVisitRepository.find(doctorObjectId,
					locationObjectId, hospitalObjectId, patientObjectId);
			UserCollection userCollection = userRepository.findOne(doctorObjectId);

			if (patientTrackCollection == null) {
				patientTrackCollection = new PatientVisitCollection();
				patientTrackCollection.setDoctorId(doctorObjectId);
				patientTrackCollection.setLocationId(locationObjectId);
				patientTrackCollection.setHospitalId(hospitalObjectId);
				patientTrackCollection.setVisitedTime(new Date());
				patientTrackCollection.setCreatedTime(new Date());
				patientTrackCollection
						.setUniqueEmrId(UniqueIdInitial.VISITS.getInitial() + DPDoctorUtils.generateRandomId());
				patientTrackCollection.setPatientId(patientObjectId);

				if (userCollection.getFirstName() != null) {
					patientTrackCollection
							.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
									+ userCollection.getFirstName());
				}

				List<VisitedFor> visitedforList = new ArrayList<VisitedFor>();
				visitedforList.add(visitedFor);
				patientTrackCollection.setVisitedFor(visitedforList);
			} else {
				patientTrackCollection.setVisitedTime(new Date());
				patientTrackCollection.getVisitedFor().add(visitedFor);
			}
			patientTrackCollection.setUpdatedTime(new Date());
			patientVisitRepository.save(patientTrackCollection);
			response = true;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error while saving patient visit record : " + e.getCause().getMessage());
			throw new BusinessException(ServiceError.Unknown,
					"Error while saving patient visit record : " + e.getCause().getMessage());
		}
		return response;
	}

	@Override
	@Transactional
	public DoctorContactsResponse recentlyVisited(String doctorId, String locationId, String hospitalId, int page,
			int size, String role) {
		DoctorContactsResponse response = null;
		try {
			ObjectId doctorObjectId = null, locationObjectId = null, hospitalObjectId = null;
			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				doctorObjectId = new ObjectId(doctorId);
			if (!DPDoctorUtils.anyStringEmpty(locationId))
				locationObjectId = new ObjectId(locationId);
			if (!DPDoctorUtils.anyStringEmpty(hospitalId))
				hospitalObjectId = new ObjectId(hospitalId);

			Criteria criteria = new Criteria("locationId").is(locationObjectId).and("hospitalId").is(hospitalObjectId).and("isPatientDiscarded").is(false);
			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				criteria.and("doctorId").is(doctorObjectId);

			Aggregation aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
					Aggregation.group("patientId").max("visitedTime").as("visitedTime"),
					Aggregation.sort(new Sort(Sort.Direction.DESC, "visitedTime")));

			AggregationResults<PatientVisitCollection> groupResults = mongoTemplate.aggregate(aggregation,
					PatientVisitCollection.class, PatientVisitCollection.class);
			List<PatientVisitCollection> results = groupResults.getMappedResults();

			if (results != null && !results.isEmpty()) {
				@SuppressWarnings("unchecked")
				List<ObjectId> patientIds = (List<ObjectId>) CollectionUtils.collect(results,
						new BeanToPropertyValueTransformer("id"));
				response = contactsService.getSpecifiedPatientCards(patientIds, doctorId, locationId, hospitalId, page,
						size, "0", true, false, role);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error while recently visited patients record : " + e.getCause().getMessage());
			throw new BusinessException(ServiceError.Unknown,
					"Error while getting recently visited patients record : " + e.getCause().getMessage());
		}
		return response;
	}

	@Override
	@Transactional
	public DoctorContactsResponse mostVisited(String doctorId, String locationId, String hospitalId, int page, int size,
			String role) {
		DoctorContactsResponse response = null;
		try {
			ObjectId doctorObjectId = null, locationObjectId = null, hospitalObjectId = null;
			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				doctorObjectId = new ObjectId(doctorId);
			if (!DPDoctorUtils.anyStringEmpty(locationId))
				locationObjectId = new ObjectId(locationId);
			if (!DPDoctorUtils.anyStringEmpty(hospitalId))
				hospitalObjectId = new ObjectId(hospitalId);

			Criteria matchCriteria = new Criteria("locationId").is(locationObjectId).and("hospitalId")
					.is(hospitalObjectId).and("isPatientDiscarded").is(false);
			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				matchCriteria.and("doctorId").is(doctorObjectId);

			Aggregation aggregation = Aggregation.newAggregation(Aggregation.match(matchCriteria),
					Aggregation.group("patientId").count().as("total"),
					Aggregation.project("total").and("patientId").previousOperation(),
					Aggregation.sort(Sort.Direction.DESC, "total"));

			AggregationResults<PatientVisitCollection> aggregationResults = mongoTemplate.aggregate(aggregation,
					PatientVisitCollection.class, PatientVisitCollection.class);

			List<PatientVisitCollection> patientTrackCollections = aggregationResults.getMappedResults();

			if (patientTrackCollections != null && !patientTrackCollections.isEmpty()) {
				@SuppressWarnings("unchecked")
				List<ObjectId> patientIds = (List<ObjectId>) CollectionUtils.collect(patientTrackCollections,
						new BeanToPropertyValueTransformer("patientId"));
				response = contactsService.getSpecifiedPatientCards(patientIds, doctorId, locationId, hospitalId, page,
						size, "0", true, false, role);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error while getting most visited patients record : " + e.getCause().getMessage());
			throw new BusinessException(ServiceError.Unknown,
					"Error while getting most visited patients record : " + e.getCause().getMessage());
		}
		return response;
	}

	@Override
	@Transactional
	public PatientVisitResponse addMultipleData(AddMultipleDataRequest request) {
		PatientVisitResponse response = new PatientVisitResponse();
		String visitId = request.getVisitId();
		Appointment appointment = null;
		PatientVisitCollection patientVisitCollection = null;
		try {

			if (!DPDoctorUtils.anyStringEmpty(visitId)) {
				patientVisitCollection = patientVisitRepository.findOne(new ObjectId(visitId));
				patientVisitCollection.setUpdatedTime(new Date());
				if (request.getPrescription() != null && request.getPrescription().getId() == null
						&& patientVisitCollection.getPrescriptionId() != null
						&& patientVisitCollection.getPrescriptionId().size() > 0) {
					throw new BusinessException(ServiceError.NotAcceptable,
							"Trying to add multipl prescription in visit");
				}
				if (request.getClinicalNote() != null && request.getClinicalNote().getId() == null
						&& patientVisitCollection.getClinicalNotesId() != null
						&& patientVisitCollection.getClinicalNotesId().size() > 0) {
					throw new BusinessException(ServiceError.NotAcceptable,
							"Trying to add multipl clinical notes in visit");
				}
			} else {
				patientVisitCollection = new PatientVisitCollection();
				patientVisitCollection.setDoctorId(new ObjectId(request.getDoctorId()));
				patientVisitCollection.setLocationId(new ObjectId(request.getLocationId()));
				patientVisitCollection.setHospitalId(new ObjectId(request.getHospitalId()));
				patientVisitCollection.setPatientId(new ObjectId(request.getPatientId()));
				patientVisitCollection.setCreatedTime(new Date());
				patientVisitCollection
						.setUniqueEmrId(UniqueIdInitial.VISITS.getInitial() + DPDoctorUtils.generateRandomId());
				UserCollection userCollection = userRepository.findOne(patientVisitCollection.getDoctorId());
				if (userCollection != null) {
					patientVisitCollection
							.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
									+ userCollection.getFirstName());
				}
				patientVisitCollection = patientVisitRepository.save(patientVisitCollection);
				visitId = patientVisitCollection.getId().toString();
				request.setVisitId(visitId);
			}

			if (request.getAppointmentRequest() != null) {
				request.getAppointmentRequest().setVisitId(visitId);
				appointment = addVisitAppointment(request.getAppointmentRequest());
				patientVisitCollection.setAppointmentId(appointment.getAppointmentId());
				patientVisitCollection.setTime(appointment.getTime());
				patientVisitCollection.setFromDate(appointment.getFromDate());
			}

			BeanUtil.map(request, response);
			if (request.getClinicalNote() != null) {
				addClinicalNotes(request, response, patientVisitCollection, visitId, appointment,
						patientVisitCollection.getCreatedBy());
			}

			if (request.getPrescription() != null) {
				addPrescription(request, response, patientVisitCollection, visitId, appointment,
						patientVisitCollection.getCreatedBy());
			}

			if (request.getRecord() != null) {
				addRecords(request, response, patientVisitCollection, visitId, appointment,
						patientVisitCollection.getCreatedBy());
			}
			if (request.getTreatmentRequest() != null) {
				addTreatments(request, response, patientVisitCollection, visitId, appointment,
						patientVisitCollection.getCreatedBy());
			}
			patientVisitCollection.setVisitedTime(new Date());
			patientVisitCollection = patientVisitRepository.save(patientVisitCollection);
			if (patientVisitCollection != null) {
				response.setId(patientVisitCollection.getId().toString());
				response.setVisitedFor(patientVisitCollection.getVisitedFor());
				response.setVisitedTime(patientVisitCollection.getVisitedTime());
				response.setUniqueEmrId(patientVisitCollection.getUniqueEmrId());
				response.setCreatedTime(patientVisitCollection.getCreatedTime());
				response.setUpdatedTime(patientVisitCollection.getUpdatedTime());
				response.setCreatedBy(patientVisitCollection.getCreatedBy());
				if (patientVisitCollection.getAppointmentId() != null) {
					response.setAppointmentId(patientVisitCollection.getAppointmentId());
					response.setTime(patientVisitCollection.getTime());
					response.setFromDate(patientVisitCollection.getFromDate());
				}
				if ((response.getPatientTreatment() == null || response.getPatientTreatment().isEmpty())
						&& (patientVisitCollection.getTreatmentId() != null
								&& !patientVisitCollection.getTreatmentId().isEmpty())) {
					List<PatientTreatment> list = patientTreatmentServices.getPatientTreatmentByIds(
							patientVisitCollection.getTreatmentId(), patientVisitCollection.getId());
					response.setPatientTreatment(list);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error while adding patient Visit : " + e.getCause().getMessage());
			throw new BusinessException(ServiceError.Unknown,
					"Error while adding patient Visit : " + e.getCause().getMessage());
		}
		return response;
	}

	private void addTreatments(AddMultipleDataRequest request, PatientVisitResponse response,
			PatientVisitCollection patientVisitCollection, String visitId, Appointment appointment, String createdBy) {

		PatientTreatmentResponse patientTreatmentResponse = patientTreatmentServices
				.addEditPatientTreatment(request.getTreatmentRequest(), false, createdBy, appointment);

		PatientTreatment patientTreatment = new PatientTreatment();
		BeanUtil.map(patientTreatmentResponse, patientTreatment);

		if (patientTreatmentResponse != null) {
			if (patientVisitCollection.getVisitedFor() != null) {
				if (!patientVisitCollection.getVisitedFor().contains(VisitedFor.TREATMENT))
					patientVisitCollection.getVisitedFor().add(VisitedFor.TREATMENT);
			} else {
				List<VisitedFor> visitedforList = new ArrayList<VisitedFor>();
				visitedforList.add(VisitedFor.TREATMENT);
				patientVisitCollection.setVisitedFor(visitedforList);
			}
			if (patientVisitCollection.getTreatmentId() == null) {
				patientVisitCollection.setTreatmentId(Arrays.asList(new ObjectId(patientTreatment.getId())));
			} else {
				if (!patientVisitCollection.getTreatmentId().contains(new ObjectId(patientTreatment.getId())))
					patientVisitCollection.getTreatmentId().add(new ObjectId(patientTreatment.getId()));
			}

			patientTreatment.setVisitId(visitId);
			List<PatientTreatment> list = new ArrayList<PatientTreatment>();
			list.add(patientTreatment);
			response.setPatientTreatment(list);
		}
	}

	private void addRecords(AddMultipleDataRequest request, PatientVisitResponse response,
			PatientVisitCollection patientVisitCollection, String visitId, Appointment appointment, String createdBy) {
		Records records = recordsService.addRecord(request.getRecord(), createdBy);

		if (records != null) {
			records.setRecordsUrl(getFinalImageURL(records.getRecordsUrl()));

			if (patientVisitCollection.getVisitedFor() != null) {
				if (!patientVisitCollection.getVisitedFor().contains(VisitedFor.REPORTS))
					patientVisitCollection.getVisitedFor().add(VisitedFor.REPORTS);
			} else {
				List<VisitedFor> visitedforList = new ArrayList<VisitedFor>();
				visitedforList.add(VisitedFor.REPORTS);
				patientVisitCollection.setVisitedFor(visitedforList);
			}
			if (patientVisitCollection.getRecordId() == null) {
				patientVisitCollection.setRecordId(Arrays.asList(new ObjectId(records.getId())));
			} else {
				if (!patientVisitCollection.getRecordId().contains(new ObjectId(records.getId())))
					patientVisitCollection.getRecordId().add(new ObjectId(records.getId()));
			}

			records.setVisitId(visitId);
			List<Records> list = new ArrayList<Records>();
			list.add(records);
			response.setRecords(list);
		}
	}

	private void addPrescription(AddMultipleDataRequest request, PatientVisitResponse response,
			PatientVisitCollection patientVisitCollection, String visitId, Appointment appointment, String createdBy) {

		PrescriptionAddEditResponse prescriptionResponse = prescriptionServices
				.addPrescription(request.getPrescription(), false, createdBy, appointment);
		Prescription prescription = new Prescription();

		List<TestAndRecordDataResponse> prescriptionTest = prescriptionResponse.getDiagnosticTests();
		prescriptionResponse.setDiagnosticTests(null);
		BeanUtil.map(prescriptionResponse, prescription);
		prescription.setDiagnosticTests(prescriptionTest);

		if (prescriptionResponse != null) {
			if (patientVisitCollection.getVisitedFor() != null) {
				if (!patientVisitCollection.getVisitedFor().contains(VisitedFor.PRESCRIPTION))
					patientVisitCollection.getVisitedFor().add(VisitedFor.PRESCRIPTION);
			} else {
				List<VisitedFor> visitedforList = new ArrayList<VisitedFor>();
				visitedforList.add(VisitedFor.PRESCRIPTION);
				patientVisitCollection.setVisitedFor(visitedforList);
			}
			if (patientVisitCollection.getPrescriptionId() == null) {
				patientVisitCollection.setPrescriptionId(Arrays.asList(new ObjectId(prescription.getId())));
			} else {
				if (!patientVisitCollection.getPrescriptionId().contains(new ObjectId(prescription.getId())))
					patientVisitCollection.getPrescriptionId().add(new ObjectId(prescription.getId()));
			}

			prescription.setVisitId(visitId);
			List<Prescription> list = new ArrayList<Prescription>();
			list.add(prescription);
			response.setPrescriptions(list);
		}
	}

	private void addClinicalNotes(AddMultipleDataRequest request, PatientVisitResponse response,
			PatientVisitCollection patientVisitCollection, String visitId, Appointment appointment, String createdBy) {

		ClinicalNotes clinicalNotes = clinicalNotesService.addNotes(request.getClinicalNote(), false, createdBy,
				appointment);
		if (clinicalNotes.getDiagrams() != null && !clinicalNotes.getDiagrams().isEmpty()) {
			clinicalNotes.setDiagrams(getFinalDiagrams(clinicalNotes.getDiagrams()));
		}

		if (patientVisitCollection.getVisitedFor() != null) {
			if (!patientVisitCollection.getVisitedFor().contains(VisitedFor.CLINICAL_NOTES))
				patientVisitCollection.getVisitedFor().add(VisitedFor.CLINICAL_NOTES);
		} else {
			List<VisitedFor> visitedforList = new ArrayList<VisitedFor>();
			visitedforList.add(VisitedFor.CLINICAL_NOTES);
			patientVisitCollection.setVisitedFor(visitedforList);
		}
		if (patientVisitCollection.getClinicalNotesId() == null) {
			patientVisitCollection.setClinicalNotesId(Arrays.asList(new ObjectId(clinicalNotes.getId())));
		} else {
			if (!patientVisitCollection.getClinicalNotesId().contains(new ObjectId(clinicalNotes.getId())))
				patientVisitCollection.getClinicalNotesId().add(new ObjectId(clinicalNotes.getId()));
		}
		clinicalNotes.setVisitId(visitId);
		List<ClinicalNotes> list = new ArrayList<ClinicalNotes>();
		list.add(clinicalNotes);
		response.setClinicalNotes(list);
	}

	@Override
	@Transactional
	public List<PatientVisitResponse> getVisit(String doctorId, String locationId, String hospitalId, String patientId,
			int page, int size, Boolean isOTPVerified, String updatedTime, String visitFor) {
		List<PatientVisitResponse> response = null;
		List<PatientVisitLookupBean> patientVisitlookupbeans = null;
		try {
			List<VisitedFor> visitedFors = new ArrayList<VisitedFor>();
			if (visitFor == VisitedFor.ALL.toString() || visitFor == null) {
				visitedFors.add(CLINICAL_NOTES);
				visitedFors.add(PRESCRIPTION);
				visitedFors.add(REPORTS);
			} else if (visitFor.equalsIgnoreCase(VisitedFor.TREATMENT.getVisitedFor())) {
				visitedFors.add(CLINICAL_NOTES);
				visitedFors.add(PRESCRIPTION);
				visitedFors.add(REPORTS);
				visitedFors.add(VisitedFor.TREATMENT);
			} else if (visitFor.equalsIgnoreCase("WEB")) {
				visitedFors.add(CLINICAL_NOTES);
				visitedFors.add(PRESCRIPTION);
				visitedFors.add(VisitedFor.TREATMENT);
				visitedFors.add(VisitedFor.EYE_PRESCRIPTION);
			} else {
				visitedFors.add(VisitedFor.valueOf(visitFor.toUpperCase()));
			}

			long createdTimestamp = Long.parseLong(updatedTime);
			ObjectId patientObjectId = null, doctorObjectId = null, locationObjectId = null, hospitalObjectId = null;
			if (!DPDoctorUtils.anyStringEmpty(patientId))
				patientObjectId = new ObjectId(patientId);
			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				doctorObjectId = new ObjectId(doctorId);
			if (!DPDoctorUtils.anyStringEmpty(locationId))
				locationObjectId = new ObjectId(locationId);
			if (!DPDoctorUtils.anyStringEmpty(hospitalId))
				hospitalObjectId = new ObjectId(hospitalId);

			Criteria criteria = new Criteria("updatedTime").gt(new Date(createdTimestamp)).and("patientId")
					.is(patientObjectId).and("visitedFor").in(visitedFors).and("isPatientDiscarded").is(false);

			if (!isOTPVerified) {
				if (!DPDoctorUtils.anyStringEmpty(locationId, hospitalId))
					criteria.and("locationId").is(locationObjectId).and("hospitalId").is(hospitalObjectId);
				if (!DPDoctorUtils.anyStringEmpty(doctorId))
					criteria.and("doctorId").is(doctorObjectId);
			}
			Aggregation aggregation = null;

			if (size > 0)
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.lookup("appointment_cl", "appointmentId", "appointmentId", "appointmentRequest"),
						new CustomAggregationOperation(new BasicDBObject("$unwind",
								new BasicDBObject("path", "$appointmentRequest").append("preserveNullAndEmptyArrays",
										true))),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")), Aggregation.skip((page) * size),
						Aggregation.limit(size));
			else
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.lookup("appointment_cl", "appointmentId", "appointmentId",
								"appointmentRequest"),
						new CustomAggregationOperation(
								new BasicDBObject("$unwind",
										new BasicDBObject("path", "$appointmentRequest")
												.append("preserveNullAndEmptyArrays", true))),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));

			AggregationResults<PatientVisitLookupBean> aggregationResults = mongoTemplate.aggregate(aggregation,
					PatientVisitCollection.class, PatientVisitLookupBean.class);
			patientVisitlookupbeans = aggregationResults.getMappedResults();
			if (patientVisitlookupbeans != null) {
				response = new ArrayList<PatientVisitResponse>();

				for (PatientVisitLookupBean patientVisitlookupBean : patientVisitlookupbeans) {
					PatientVisitResponse patientVisitResponse = new PatientVisitResponse();
					BeanUtil.map(patientVisitlookupBean, patientVisitResponse);

					if (patientVisitlookupBean.getPrescriptionId() != null) {
						List<Prescription> prescriptions = prescriptionServices.getPrescriptionsByIds(
								patientVisitlookupBean.getPrescriptionId(), patientVisitlookupBean.getId());
						patientVisitResponse.setPrescriptions(prescriptions);
					}

					if (patientVisitlookupBean.getClinicalNotesId() != null) {
						List<ClinicalNotes> clinicalNotes = new ArrayList<ClinicalNotes>();
						for (ObjectId clinicalNotesId : patientVisitlookupBean.getClinicalNotesId()) {
							ClinicalNotes clinicalNote = clinicalNotesService.getNotesById(clinicalNotesId.toString(),
									patientVisitlookupBean.getId());
							if (clinicalNote != null) {
								if (clinicalNote.getDiagrams() != null && !clinicalNote.getDiagrams().isEmpty()) {
									clinicalNote.setDiagrams(getFinalDiagrams(clinicalNote.getDiagrams()));
								}
								clinicalNotes.add(clinicalNote);
							}
						}
						patientVisitResponse.setClinicalNotes(clinicalNotes);
					}

					if (patientVisitlookupBean.getRecordId() != null) {
						List<Records> records = recordsService.getRecordsByIds(patientVisitlookupBean.getRecordId(),
								patientVisitlookupBean.getId());
						patientVisitResponse.setRecords(records);
					}

					if (patientVisitlookupBean.getTreatmentId() != null) {
						List<PatientTreatment> patientTreatment = patientTreatmentServices.getPatientTreatmentByIds(
								patientVisitlookupBean.getTreatmentId(), patientVisitlookupBean.getId());
						patientVisitResponse.setPatientTreatment(patientTreatment);
					}

					if (patientVisitlookupBean.getEyePrescriptionId() != null) {
						EyePrescription eyePrescription = prescriptionServices
								.getEyePrescription(String.valueOf(patientVisitlookupBean.getEyePrescriptionId()));
						patientVisitResponse.setEyePrescription(eyePrescription);

					}
					response.add(patientVisitResponse);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error while geting patient Visit : " + e.getCause().getMessage());
			throw new BusinessException(ServiceError.Unknown,
					"Error while geting patient Visit : " + e.getCause().getMessage());
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
	@Transactional
	public Boolean email(String visitId, String emailAddress) {
		Boolean response = false;
		MailAttachment mailAttachment = null;
		EmailTrackCollection emailTrackCollection = new EmailTrackCollection();

		try {

			PatientVisitLookupResponse patientVisitLookupResponse = mongoTemplate.aggregate(Aggregation.newAggregation(
					Aggregation.match(new Criteria("id").is(new ObjectId(visitId))),
					Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"), Aggregation.unwind("doctor"),
					Aggregation.lookup("location_cl", "locationId", "_id", "location"), Aggregation.unwind("location"),
					Aggregation.lookup("patient_cl", "patientId", "userId", "patient"),
					new CustomAggregationOperation(new BasicDBObject("$unwind",
							new BasicDBObject("path", "$patient").append("preserveNullAndEmptyArrays",
									true))),
					new CustomAggregationOperation(new BasicDBObject("$redact",
							new BasicDBObject("$cond",
									new BasicDBObject("if",
											new BasicDBObject("$eq",
													Arrays.asList("$patient.locationId", "$locationId")))
															.append("then", "$$KEEP").append("else", "$$PRUNE")))),

					Aggregation.lookup("user_cl", "patientId", "_id", "patientUser"),
					Aggregation.unwind("patientUser")), PatientVisitCollection.class, PatientVisitLookupResponse.class)
					.getUniqueMappedResult();

			if (patientVisitLookupResponse != null) {
				PatientCollection patient = patientVisitLookupResponse.getPatient();
				UserCollection user = patientVisitLookupResponse.getPatientUser();
				user.setFirstName(patient.getLocalPatientName());
				JasperReportResponse jasperReportResponse = createJasper(patientVisitLookupResponse, patient, user,
						null, false, false, false, false, false, false, false, false, false, false);
				if (jasperReportResponse != null) {
					if (user != null) {
						emailTrackCollection.setPatientName(patient.getLocalPatientName());
						emailTrackCollection.setPatientId(user.getId());
					}
					List<MailAttachment> mailAttachments = new ArrayList<MailAttachment>();

					mailAttachment = new MailAttachment();
					mailAttachment.setAttachmentName(FilenameUtils.getName(jasperReportResponse.getPath()));
					mailAttachment.setFileSystemResource(jasperReportResponse.getFileSystemResource());
					mailAttachments.add(mailAttachment);
					if (patientVisitLookupResponse.getRecords() != null) {
						for (RecordsCollection record : patientVisitLookupResponse.getRecords()) {
							MailResponse mailResponse = recordsService.getRecordMailData(record.getId().toString(),
									record.getDoctorId().toString(), record.getLocationId().toString(),
									record.getHospitalId().toString());
							if (mailResponse.getMailAttachment() != null)
								mailAttachments.add(mailResponse.getMailAttachment());
						}
					}
					UserCollection doctorUser = patientVisitLookupResponse.getDoctor();
					LocationCollection locationCollection = patientVisitLookupResponse.getLocation();
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
					SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
					sdf.setTimeZone(TimeZone.getTimeZone("IST"));
					String body = mailBodyGenerator.generateEMREmailBody(patient.getLocalPatientName(),
							doctorUser.getTitle() + " " + doctorUser.getFirstName(),
							locationCollection.getLocationName(), address,
							sdf.format(patientVisitLookupResponse.getCreatedTime()), "Visit Details",
							"emrMailTemplate.vm");
					mailService.sendEmailMultiAttach(emailAddress,
							doctorUser.getTitle() + " " + doctorUser.getFirstName() + " sent you Visit Details", body,
							mailAttachments);

					emailTrackCollection.setDoctorId(patientVisitLookupResponse.getDoctorId());
					emailTrackCollection.setHospitalId(patientVisitLookupResponse.getHospitalId());
					emailTrackCollection.setLocationId(patientVisitLookupResponse.getLocationId());
					emailTrackCollection.setType(ComponentType.ALL.getType());
					emailTrackCollection.setSubject("Patient Visit");
					emailTackService.saveEmailTrack(emailTrackCollection);
					response = true;
					if (mailAttachment != null && mailAttachment.getFileSystemResource() != null)
						if (mailAttachment.getFileSystemResource().getFile().exists())
							mailAttachment.getFileSystemResource().getFile().delete();
				}
			} else {
				logger.warn("Patient Visit Id does not exist");
				throw new BusinessException(ServiceError.NotFound, "Patient Visit Id does not exist");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	private JasperReportResponse createJasper(PatientVisitLookupResponse patientVisitLookupResponse,
			PatientCollection patient, UserCollection user, HistoryCollection historyCollection, Boolean showPH,
			Boolean showPLH, Boolean showFH, Boolean showDA, Boolean showUSG, Boolean isLabPrint, Boolean isCustomPDF,
			Boolean showLMP, Boolean showEDD, Boolean showNoOfChildren) throws IOException, ParseException {
		JasperReportResponse response = null;
		Map<String, Object> parameters = new HashMap<String, Object>();
		String resourceId = "<b>VID: </b>" + (patientVisitLookupResponse.getUniqueEmrId() != null
				? patientVisitLookupResponse.getUniqueEmrId() : "--");

		PrintSettingsCollection printSettings = printSettingsRepository.getSettings(
				patientVisitLookupResponse.getDoctorId(), patientVisitLookupResponse.getLocationId(),
				patientVisitLookupResponse.getHospitalId(), ComponentType.ALL.getType());

		if (printSettings == null) {
			printSettings = new PrintSettingsCollection();
			DefaultPrintSettings defaultPrintSettings = new DefaultPrintSettings();
			BeanUtil.map(defaultPrintSettings, printSettings);
		}
		if (printSettings.getContentSetup() != null) {

			parameters.put("isEnableTreatmentcost", printSettings.getContentSetup().getShowTreatmentcost());

		} else {
			parameters.put("isEnableTreatmentcost", true);
		}
		List<DBObject> prescriptions = null;
		if (!showUSG) {
			if (patientVisitLookupResponse.getPrescriptionId() != null) {
				prescriptions = new ArrayList<DBObject>();
				for (ObjectId prescriptionId : patientVisitLookupResponse.getPrescriptionId()) {
					if (!DPDoctorUtils.anyStringEmpty(prescriptionId)) {
						DBObject prescriptionItems = new BasicDBObject();
						List<PrescriptionJasperDetails> prescriptionJasperDetails = getPrescriptionJasperDetails(
								prescriptionId.toString(), prescriptionItems, parameters, isLabPrint, printSettings);
						if (prescriptionJasperDetails != null && !prescriptionJasperDetails.isEmpty())
							prescriptionItems.put("items", prescriptionJasperDetails);
						resourceId = (String) prescriptionItems.get("resourceId");
						if (prescriptionItems.toMap().size() > 1)
							prescriptions.add(prescriptionItems);
					}
				}
			}
		}
		List<ClinicalNotesJasperDetails> clinicalNotes = null;

		if (!isLabPrint) {
			if (patientVisitLookupResponse.getClinicalNotesId() != null) {
				clinicalNotes = new ArrayList<ClinicalNotesJasperDetails>();
				String contentLineStyle = (printSettings != null
						&& !DPDoctorUtils.anyStringEmpty(printSettings.getContentLineStyle()))
								? printSettings.getContentLineStyle() : LineStyle.INLINE.name();
				for (ObjectId clinicalNotesId : patientVisitLookupResponse.getClinicalNotesId()) {
					if (!DPDoctorUtils.anyStringEmpty(clinicalNotesId)) {
						ClinicalNotesJasperDetails clinicalJasperDetails = getClinicalNotesJasperDetails(
								clinicalNotesId.toString(), contentLineStyle, parameters, showUSG, isCustomPDF, showLMP,
								showEDD, showNoOfChildren);
						clinicalNotes.add(clinicalJasperDetails);
					}
				}
			}
		}
		List<DBObject> patientTreatments = null;
		if (!showUSG && !isLabPrint) {
			if (patientVisitLookupResponse.getTreatmentId() != null) {
				patientTreatments = new ArrayList<DBObject>();
				for (ObjectId treatmentId : patientVisitLookupResponse.getTreatmentId()) {
					if (!DPDoctorUtils.anyStringEmpty(treatmentId)) {
						DBObject patientTreatmentServices = getPatientTreatmentJasperDetails(treatmentId.toString(),
								parameters);
						if (patientTreatmentServices != null)
							patientTreatments.add(patientTreatmentServices);
						break;
					}
				}
			}
		}
		if (!showUSG && !isLabPrint) {
			if (patientVisitLookupResponse.getEyePrescriptionId() != null) {
				EyePrescriptionCollection eyePrescriptionCollection = eyePrescriptionRepository
						.findOne(patientVisitLookupResponse.getEyePrescriptionId());
				EyeTestJasperResponse eyResponse = new EyeTestJasperResponse();
				if (eyePrescriptionCollection.getLeftEyeTest() != null) {

					BeanUtil.map(eyePrescriptionCollection.getLeftEyeTest(), eyResponse);
					if (!DPDoctorUtils.anyStringEmpty(eyePrescriptionCollection.getLeftEyeTest().getDistanceSPH())
							|| eyePrescriptionCollection.getLeftEyeTest().getDistanceSPH().equalsIgnoreCase("plain")
							|| eyePrescriptionCollection.getLeftEyeTest().getDistanceSPH().equalsIgnoreCase(" plain"))
						eyResponse.setDistanceSPH(String.format("%.2f",
								Double.parseDouble(eyePrescriptionCollection.getLeftEyeTest().getDistanceSPH())));
					if (!DPDoctorUtils.anyStringEmpty(eyePrescriptionCollection.getLeftEyeTest().getNearSPH())
							|| eyePrescriptionCollection.getLeftEyeTest().getNearSPH().equalsIgnoreCase("plain")
							|| eyePrescriptionCollection.getLeftEyeTest().getNearSPH().equalsIgnoreCase(" plain"))
						eyResponse.setNearSPH(String.format("%.2f",
								Double.parseDouble(eyePrescriptionCollection.getLeftEyeTest().getNearSPH())));
					eyResponse.setDistanceCylinder(
							String.format("%.2f", eyePrescriptionCollection.getLeftEyeTest().getDistanceCylinder()));
					eyResponse.setDistanceBaseCurve(
							String.format("%.2f", eyePrescriptionCollection.getLeftEyeTest().getDistanceBaseCurve()));
					eyResponse.setDistanceDiameter(
							String.format("%.2f", eyePrescriptionCollection.getLeftEyeTest().getDistanceDiameter()));
					eyResponse.setNearCylinder(
							String.format("%.2f", eyePrescriptionCollection.getLeftEyeTest().getNearCylinder()));
					eyResponse.setNearBaseCurve(
							String.format("%.2f", eyePrescriptionCollection.getLeftEyeTest().getNearBaseCurve()));
					eyResponse.setNearDiameter(
							String.format("%.2f", eyePrescriptionCollection.getLeftEyeTest().getNearDiameter()));
				}
				parameters.put("leftEyeTest", eyResponse);
				eyResponse = new EyeTestJasperResponse();
				if (eyePrescriptionCollection.getRightEyeTest() != null) {
					BeanUtil.map(eyePrescriptionCollection.getRightEyeTest(), eyResponse);
					if (!DPDoctorUtils.anyStringEmpty(eyePrescriptionCollection.getRightEyeTest().getDistanceSPH())
							|| eyePrescriptionCollection.getRightEyeTest().getDistanceSPH().equalsIgnoreCase("plain")
							|| eyePrescriptionCollection.getRightEyeTest().getDistanceSPH().equalsIgnoreCase(" plain"))
						eyResponse.setDistanceSPH(String.format("%.2f",
								Double.parseDouble(eyePrescriptionCollection.getRightEyeTest().getDistanceSPH())));
					if (!DPDoctorUtils.anyStringEmpty(eyePrescriptionCollection.getRightEyeTest().getNearSPH())
							|| eyePrescriptionCollection.getRightEyeTest().getNearSPH().equalsIgnoreCase("plain")
							|| eyePrescriptionCollection.getRightEyeTest().getNearSPH().equalsIgnoreCase(" plain"))
						eyResponse.setNearSPH(String.format("%.2f",
								Double.parseDouble(eyePrescriptionCollection.getRightEyeTest().getNearSPH())));
					eyResponse.setDistanceCylinder(
							String.format("%.2f", eyePrescriptionCollection.getRightEyeTest().getDistanceCylinder()));
					eyResponse.setDistanceBaseCurve(
							String.format("%.2f", eyePrescriptionCollection.getRightEyeTest().getDistanceBaseCurve()));
					eyResponse.setDistanceDiameter(
							String.format("%.2f", eyePrescriptionCollection.getRightEyeTest().getDistanceDiameter()));
					eyResponse.setNearCylinder(
							String.format("%.2f", eyePrescriptionCollection.getRightEyeTest().getNearCylinder()));
					eyResponse.setNearBaseCurve(
							String.format("%.2f", eyePrescriptionCollection.getRightEyeTest().getNearBaseCurve()));
					eyResponse.setNearDiameter(
							String.format("%.2f", eyePrescriptionCollection.getRightEyeTest().getNearDiameter()));
				}
				parameters.put("rightEyeTest", eyResponse);
				if (!DPDoctorUtils.anyStringEmpty(eyePrescriptionCollection.getType())
						&& eyePrescriptionCollection.getType().equalsIgnoreCase("CONTACT LENS"))
					if ((eyePrescriptionCollection.getLeftEyeTest() != null
							&& DPDoctorUtils.allStringsEmpty(eyePrescriptionCollection.getLeftEyeTest().getDistanceVA(),
									eyePrescriptionCollection.getLeftEyeTest().getNearVA())
							|| (eyePrescriptionCollection.getRightEyeTest() != null && DPDoctorUtils.allStringsEmpty(
									eyePrescriptionCollection.getRightEyeTest().getDistanceVA(),
									eyePrescriptionCollection.getRightEyeTest().getNearVA()))))
						parameters.put("noOfFields", 5);
					else
						parameters.put("noOfFields", 6);
				else {
					if ((eyePrescriptionCollection.getLeftEyeTest() != null
							&& DPDoctorUtils.allStringsEmpty(eyePrescriptionCollection.getLeftEyeTest().getDistanceVA(),
									eyePrescriptionCollection.getLeftEyeTest().getNearVA())
							|| (eyePrescriptionCollection.getRightEyeTest() != null && DPDoctorUtils.allStringsEmpty(
									eyePrescriptionCollection.getRightEyeTest().getDistanceVA(),
									eyePrescriptionCollection.getRightEyeTest().getNearVA()))))
						parameters.put("noOfFields", 3);
					else
						parameters.put("noOfFields", 4);
				}
				parameters.put("quality", eyePrescriptionCollection.getQuality());
				parameters.put("type", eyePrescriptionCollection.getType());
				parameters.put("pupilaryDistance", eyePrescriptionCollection.getPupilaryDistance() != null
						? eyePrescriptionCollection.getPupilaryDistance() + " mm" : null);
				parameters.put("lensType", eyePrescriptionCollection.getLensType());
				parameters.put("usage", eyePrescriptionCollection.getUsage());
				parameters.put("remarks", eyePrescriptionCollection.getRemarks());
				parameters.put("replacementInterval", eyePrescriptionCollection.getReplacementInterval());
				parameters.put("lensColor", eyePrescriptionCollection.getLensColor());
				parameters.put("lensBrand", eyePrescriptionCollection.getLensBrand());

				parameters.put("eyePrescriptions", "eyePrescriptions");
			}
		}

		parameters.put("contentLineSpace",
				(printSettings != null && !DPDoctorUtils.anyStringEmpty(printSettings.getContentLineStyle()))
						? printSettings.getContentLineSpace() : LineSpace.SMALL.name());
		parameters.put("prescriptions", prescriptions);
		parameters.put("clinicalNotes", clinicalNotes);
		parameters.put("treatments", patientTreatments);
		parameters.put("visitId", patientVisitLookupResponse.getId().toString());
		if (parameters.get("followUpAppointment") == null
				&& !DPDoctorUtils.anyStringEmpty(patientVisitLookupResponse.getAppointmentId())
				&& patientVisitLookupResponse.getTime() != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("MMM dd");
			String _24HourTime = String.format("%02d:%02d", patientVisitLookupResponse.getTime().getFromTime() / 60,
					patientVisitLookupResponse.getTime().getFromTime() % 60);
			SimpleDateFormat _24HourSDF = new SimpleDateFormat("HH:mm");
			SimpleDateFormat _12HourSDF = new SimpleDateFormat("hh:mm a");
			sdf.setTimeZone(TimeZone.getTimeZone("IST"));
			_24HourSDF.setTimeZone(TimeZone.getTimeZone("IST"));
			_12HourSDF.setTimeZone(TimeZone.getTimeZone("IST"));

			Date _24HourDt = _24HourSDF.parse(_24HourTime);
			String dateTime = _12HourSDF.format(_24HourDt) + ", "
					+ sdf.format(patientVisitLookupResponse.getFromDate());
			parameters.put("followUpAppointment", "Next Review on " + dateTime);
		}
		if (historyCollection != null) {
			parameters.put("showHistory", true);
			includeHistoryInPdf(historyCollection, showPH, showPLH, showFH, showDA, parameters);
		} else
			parameters.put("showHistory", false);
		generatePatientDetails(
				(printSettings != null && printSettings.getHeaderSetup() != null
						? printSettings.getHeaderSetup().getPatientDetails() : null),
				patient, resourceId, patient.getLocalPatientName(), user.getMobileNumber(), parameters,
				patientVisitLookupResponse.getUpdatedTime());
		generatePrintSetup(parameters, printSettings, patientVisitLookupResponse.getDoctorId());
		String layout = printSettings != null
				? (printSettings.getPageSetup() != null ? printSettings.getPageSetup().getLayout() : "PORTRAIT")
				: "PORTRAIT";
		String pageSize = printSettings != null
				? (printSettings.getPageSetup() != null ? (printSettings.getPageSetup().getPageSize() != null
						? printSettings.getPageSetup().getPageSize() : "A4") : "A4")
				: "A4";

		String pdfName = (patient != null ? patient.getLocalPatientName() : "") + "VISITS-"
				+ patientVisitLookupResponse.getUniqueEmrId() + new Date().getTime();
		Integer topMargin = printSettings != null
				? (printSettings.getPageSetup() != null ? printSettings.getPageSetup().getTopMargin() : 20) : 20;
		Integer bottonMargin = printSettings != null
				? (printSettings.getPageSetup() != null ? printSettings.getPageSetup().getBottomMargin() : 20) : 20;
		Integer leftMargin = printSettings != null
				? (printSettings.getPageSetup() != null && printSettings.getPageSetup().getLeftMargin() != null
						? printSettings.getPageSetup().getLeftMargin() : 20)
				: 20;
		Integer rightMargin = printSettings != null
				? (printSettings.getPageSetup() != null && printSettings.getPageSetup().getRightMargin() != null
						? printSettings.getPageSetup().getRightMargin() : 20)
				: 20;

		response = jasperReportService.createPDF(ComponentType.VISITS, parameters, visitA4FileName, layout, pageSize,
				topMargin, bottonMargin, leftMargin, rightMargin,
				Integer.parseInt(parameters.get("contentFontSize").toString()), pdfName.replaceAll("\\s+", ""),
				visitClinicalNotesA4FileName, visitPrescriptionA4FileName, visitDiagramsA4FileName,
				prescriptionSubReportA4FileName);
		return response;
	}

	private DBObject getPatientTreatmentJasperDetails(String treatmentId, Map<String, Object> parameters) {
		DBObject response = new BasicDBObject();
		PatientTreatmentCollection patientTreatmentCollection = null;
		List<PatientTreatmentJasperDetails> patientTreatmentJasperDetails = null;
		try {
			Boolean showTreatmentQuantity = false, showTreatmentDiscount = false;
			patientTreatmentCollection = patientTreamentRepository.findOne(new ObjectId(treatmentId));
			if (patientTreatmentCollection != null) {
				if (patientTreatmentCollection.getDoctorId() != null
						&& patientTreatmentCollection.getHospitalId() != null
						&& patientTreatmentCollection.getLocationId() != null) {

					if (patientTreatmentCollection.getTreatments() != null
							&& !patientTreatmentCollection.getTreatments().isEmpty()) {
						int no = 0;
						patientTreatmentJasperDetails = new ArrayList<PatientTreatmentJasperDetails>();
						for (Treatment treatment : patientTreatmentCollection.getTreatments()) {
							PatientTreatmentJasperDetails patientTreatments = new PatientTreatmentJasperDetails();
							TreatmentServicesCollection treatmentServicesCollection = treatmentServicesRepository
									.findOne(treatment.getTreatmentServiceId());
							patientTreatments.setNo(++no);
							if (treatment.getStatus() != null) {
								String status = treatment.getStatus().replaceAll("_", " ");
								status = status.substring(0, 1).toUpperCase() + status.substring(1).toLowerCase();
								patientTreatments.setStatus(status);
							} else {
								patientTreatments.setStatus("--");
							}
							String serviceName = treatmentServicesCollection.getName() != null
									? treatmentServicesCollection.getName() : "";
							String fieldName = "";
							if (treatment.getTreatmentFields() != null && !treatment.getTreatmentFields().isEmpty()) {
								String key = "";
								for (TreatmentFields treatmentFile : treatment.getTreatmentFields()) {
									key = treatmentFile.getKey();
									if (!DPDoctorUtils.anyStringEmpty(key)) {
										if (key.equalsIgnoreCase("toothNumber")) {
											key = "Tooth No :";
										}
										if (key.equalsIgnoreCase("material")) {
											key = "Material :";
										}

										if (!DPDoctorUtils.anyStringEmpty(treatmentFile.getValue())) {
											fieldName = fieldName + "<br><font size='1'><i>" + key
													+ treatmentFile.getValue() + "</i></font>";
										}
									}
								}
							}
							serviceName = serviceName == "" ? "--" : serviceName + fieldName;
							patientTreatments.setTreatmentServiceName(serviceName);

							if (treatment.getQuantity() != null && treatment.getQuantity().getValue() > 0) {
								showTreatmentQuantity = true;
								String quantity = treatment.getQuantity().getValue() + " ";
								if (treatment.getQuantity().getType() != null)
									quantity = quantity + treatment.getQuantity().getType().getDuration();
								patientTreatments.setQuantity(quantity);
							}
							if (treatment.getDiscount() != null && treatment.getDiscount().getValue() > 0)
								showTreatmentDiscount = true;
							patientTreatments.setNote(treatment.getNote() != null
									? "<font size='1'><b>Note :</b> " + treatment.getNote() + "</font>" : "");
							patientTreatments.setCost(treatment.getCost() + "");
							patientTreatments
									.setDiscount((treatment.getDiscount() != null) ? treatment.getDiscount().getValue()
											+ " " + treatment.getDiscount().getUnit().getUnit() : "");
							patientTreatments.setFinalCost(treatment.getFinalCost() + "");
							patientTreatmentJasperDetails.add(patientTreatments);
						}
						parameters.put("showTreatmentDiscount", showTreatmentDiscount);
						parameters.put("showTreatmentQuantity", showTreatmentQuantity);
						if (parameters.get("followUpAppointment") == null
								&& !DPDoctorUtils.anyStringEmpty(patientTreatmentCollection.getAppointmentId())
								&& patientTreatmentCollection.getTime() != null) {
							SimpleDateFormat sdf = new SimpleDateFormat("MMM dd");
							String _24HourTime = String.format("%02d:%02d",
									patientTreatmentCollection.getTime().getFromTime() / 60,
									patientTreatmentCollection.getTime().getFromTime() % 60);
							SimpleDateFormat _24HourSDF = new SimpleDateFormat("HH:mm");
							SimpleDateFormat _12HourSDF = new SimpleDateFormat("hh:mm a");
							sdf.setTimeZone(TimeZone.getTimeZone("IST"));
							_24HourSDF.setTimeZone(TimeZone.getTimeZone("IST"));
							_12HourSDF.setTimeZone(TimeZone.getTimeZone("IST"));

							Date _24HourDt = _24HourSDF.parse(_24HourTime);
							String dateTime = _12HourSDF.format(_24HourDt) + ", "
									+ sdf.format(patientTreatmentCollection.getFromDate());
							parameters.put("followUpAppointment", "Next Review on " + dateTime);
						}
						if (patientTreatmentJasperDetails != null && !patientTreatmentJasperDetails.isEmpty()) {
							response.put("services", patientTreatmentJasperDetails);
							String total = "";
							if (patientTreatmentCollection.getTotalCost() > 0)
								total = "<b>Total Cost:</b> " + patientTreatmentCollection.getTotalCost() + "₹   ";
							if (patientTreatmentCollection.getTotalDiscount() != null
									&& patientTreatmentCollection.getTotalDiscount().getValue() > 0.0) {
								total = total + "<b>Total Discount:</b> "
										+ patientTreatmentCollection.getTotalDiscount().getValue() + " "
										+ patientTreatmentCollection.getTotalDiscount().getUnit().getUnit() + "   ";
							}
							if (patientTreatmentCollection.getGrandTotal() != 0)
								total = total + "<b>Grand Total:</b> " + patientTreatmentCollection.getGrandTotal()
										+ "₹";
							response.put("grandTotal", total);
						}
					}
				}
			} else {
				logger.warn("Patient Treatment not found. Please check Id.");
				throw new BusinessException(ServiceError.NotFound, "Patient Treatment not found. Please check Id.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	public void includeHistoryInPdf(HistoryCollection historyCollection, Boolean showPH, Boolean showPLH,
			Boolean showFH, Boolean showDA, Map<String, Object> parameters) {
		if (showPH && historyCollection.getMedicalhistory() != null && !historyCollection.getMedicalhistory().isEmpty())
			parameters.put("PH", getDiseases(historyCollection.getMedicalhistory()));

		if (showPLH && historyCollection.getPersonalHistory() != null) {
			parameters.put("showPLH", showPLH);
			parameters.put("diet", historyCollection.getPersonalHistory().getDiet());
			parameters.put("addictions", historyCollection.getPersonalHistory().getAddictions());
			parameters.put("bowelHabit", historyCollection.getPersonalHistory().getBowelHabit());
			parameters.put("bladderHabit", historyCollection.getPersonalHistory().getBladderHabit());
		}

		if (showFH && historyCollection.getFamilyhistory() != null && !historyCollection.getFamilyhistory().isEmpty())
			parameters.put("FH", getDiseases(historyCollection.getFamilyhistory()));
		if (showDA && historyCollection.getDrugsAndAllergies() != null) {
			String drugs = null;
			if (historyCollection.getDrugsAndAllergies().getDrugs() != null
					&& !historyCollection.getDrugsAndAllergies().getDrugs().isEmpty()) {
				for (Drug drug : historyCollection.getDrugsAndAllergies().getDrugs()) {
					if (drugs == null)
						drugs = (drug.getDrugType() != null ? drug.getDrugType().getType() + " " : "")
								+ drug.getDrugName();
					else
						drugs = drugs + ", " + (drug.getDrugType() != null ? drug.getDrugType().getType() + " " : "")
								+ drug.getDrugName();

				}
			}
			parameters.put("ongoingDrugs", drugs);
			parameters.put("allergies", historyCollection.getDrugsAndAllergies().getAllergies());
		}
	}

	@SuppressWarnings("unchecked")
	private String getDiseases(List<ObjectId> medicalhistory) {
		List<DiseasesCollection> diseasesCollections = IteratorUtils
				.toList(diseasesRepository.findAll(medicalhistory).iterator());
		Collection<String> diseases = CollectionUtils.collect(diseasesCollections,
				new BeanToPropertyValueTransformer("disease"));
		if (diseases != null && !diseases.isEmpty())
			return diseases.toString().replaceAll("\\[", "").replaceAll("\\]", "");
		else
			return null;
	}

	@Override
	public void generatePrintSetup(Map<String, Object> parameters, PrintSettingsCollection printSettings,
			ObjectId doctorId) {
		parameters.put("printSettingsId",
				(printSettings != null && printSettings.getId() != null) ? printSettings.getId().toString() : "");
		String headerLeftText = "", headerRightText = "", footerBottomText = "", logoURL = "";
		int headerLeftTextLength = 0, headerRightTextLength = 0;
		Integer contentFontSize = 10;
		if (printSettings != null) {
			if (printSettings.getContentSetup() != null) {
				contentFontSize = !DPDoctorUtils.anyStringEmpty(printSettings.getContentSetup().getFontSize())
						? Integer.parseInt(printSettings.getContentSetup().getFontSize().replaceAll("pt", "")) : 10;
				if (printSettings.getContentSetup().getInstructionAlign() != null) {
					parameters.put("instructionAlign",
							printSettings.getContentSetup().getInstructionAlign().getAlign());
				} else {
					parameters.put("instructionAlign", FieldAlign.VERTICAL.getAlign());
				}
			}
			if (printSettings.getHeaderSetup() != null && printSettings.getHeaderSetup().getCustomHeader()) {
				parameters.put("headerHtml", printSettings.getHeaderSetup().getHeaderHtml());
				if (printSettings.getHeaderSetup().getTopLeftText() != null)
					for (PrintSettingsText str : printSettings.getHeaderSetup().getTopLeftText()) {
						boolean isBold = containsIgnoreCase(FONTSTYLE.BOLD.getStyle(), str.getFontStyle());
						boolean isItalic = containsIgnoreCase(FONTSTYLE.ITALIC.getStyle(), str.getFontStyle());
						if (!DPDoctorUtils.anyStringEmpty(str.getText())) {
							headerLeftTextLength++;
							String text = str.getText();
							if (isItalic)
								text = "<i>" + text + "</i>";
							if (isBold)
								text = "<b>" + text + "</b>";

							if (headerLeftText.isEmpty())
								headerLeftText = "<span style='font-size:" + str.getFontSize() + "'>" + text
										+ "</span>";
							else
								headerLeftText = headerLeftText + "<br/>" + "<span style='font-size:"
										+ str.getFontSize() + "'>" + text + "</span>";
						}
					}
				if (printSettings.getHeaderSetup().getTopRightText() != null)
					for (PrintSettingsText str : printSettings.getHeaderSetup().getTopRightText()) {

						boolean isBold = containsIgnoreCase(FONTSTYLE.BOLD.getStyle(), str.getFontStyle());
						boolean isItalic = containsIgnoreCase(FONTSTYLE.ITALIC.getStyle(), str.getFontStyle());

						if (!DPDoctorUtils.anyStringEmpty(str.getText())) {
							headerRightTextLength++;
							String text = str.getText();
							if (isItalic)
								text = "<i>" + text + "</i>";
							if (isBold)
								text = "<b>" + text + "</b>";

							if (headerRightText.isEmpty())
								headerRightText = "<span style='font-size:" + str.getFontSize() + "'>" + text
										+ "</span>";
							else
								headerRightText = headerRightText + "<br/>" + "<span style='font-size:"
										+ str.getFontSize() + "'>" + text + "</span>";
						}
					}
			}

			if (printSettings.getHeaderSetup() != null && printSettings.getHeaderSetup().getCustomHeader()
					&& printSettings.getHeaderSetup().getCustomLogo() && printSettings.getClinicLogoUrl() != null) {
				logoURL = getFinalImageURL(printSettings.getClinicLogoUrl());
			}

			if (printSettings.getFooterSetup() != null && printSettings.getFooterSetup().getCustomFooter()
					&& printSettings.getFooterSetup().getBottomText() != null) {
				for (PrintSettingsText str : printSettings.getFooterSetup().getBottomText()) {
					boolean isBold = containsIgnoreCase(FONTSTYLE.BOLD.getStyle(), str.getFontStyle());
					boolean isItalic = containsIgnoreCase(FONTSTYLE.ITALIC.getStyle(), str.getFontStyle());
					String text = str.getText();
					if (isItalic)
						text = "<i>" + text + "</i>";
					if (isBold)
						text = "<b>" + text + "</b>";

					if (footerBottomText.isEmpty())
						footerBottomText = "<span style='font-size:" + str.getFontSize() + "'>" + text + "</span>";
					else
						footerBottomText = footerBottomText + "" + "<span style='font-size:" + str.getFontSize() + "'>"
								+ text + "</span>";
				}
			}

			if (printSettings.getFooterSetup() != null && printSettings.getFooterSetup().getShowSignature()) {
				UserCollection doctorUser = userRepository.findOne(doctorId);
				if (doctorUser != null)
					parameters.put("footerSignature", doctorUser.getTitle() + " " + doctorUser.getFirstName());
			} else {
				parameters.put("footerSignature", "");
			}
			if (printSettings.getFooterSetup() != null) {
				if (printSettings.getFooterSetup().getShowPoweredBy()) {
					parameters.put("poweredBy", "<font color='#9d9fa0'>" + footerText + "</font>");
				} else {
					parameters.put("poweredBy", "");
				}
				if (printSettings.getFooterSetup().getShowBottomSignText()
						&& !DPDoctorUtils.anyStringEmpty(printSettings.getFooterSetup().getBottomSignText())) {
					parameters.put("bottomSignText", printSettings.getFooterSetup().getBottomSignText());
				} else {
					parameters.put("bottomSignText", "");
				}
			} else {
				parameters.put("poweredBy", "");
			}
		}
		parameters.put("contentFontSize", contentFontSize);
		parameters.put("headerLeftText", headerLeftText);
		parameters.put("headerRightText", headerRightText);
		parameters.put("footerBottomText", footerBottomText);
		parameters.put("logoURL", logoURL);
		if (headerLeftTextLength > 2 || headerRightTextLength > 2) {
			parameters.put("showTableOne", true);
		} else {
			parameters.put("showTableOne", false);
		}
	}

	@Override
	public void generatePatientDetails(PatientDetails patientDetails, PatientCollection patientCard, String uniqueEMRId,
			String firstName, String mobileNumber, Map<String, Object> parameters, Date date) {
		String age = null,
				gender = (patientCard != null && patientCard.getGender() != null ? patientCard.getGender() : null),
				patientLeftText = "", patientRightText = "";
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		sdf.setTimeZone(TimeZone.getTimeZone("IST"));
		if (patientDetails == null) {
			patientDetails = new PatientDetails();
		}
		List<String> patientDetailList = new ArrayList<String>();
		patientDetailList.add("<b>Patient Name: " + firstName.toUpperCase() + "</b>");
		patientDetailList.add("<b>Patient ID: </b>"
				+ (patientCard != null && patientCard.getPID() != null ? patientCard.getPID() : "--"));

		if (patientCard != null && patientCard.getDob() != null && patientCard.getDob().getAge() != null) {
			Age ageObj = patientCard.getDob().getAge();
			if (ageObj.getYears() > 14)
				age = ageObj.getYears() + "yrs";
			else {
				if (ageObj.getYears() > 0)
					age = ageObj.getYears() + "yrs";
				if (ageObj.getMonths() > 0) {
					if (DPDoctorUtils.anyStringEmpty(age))
						age = ageObj.getMonths() + "months";
					else
						age = age + " " + ageObj.getMonths() + " months";
				}
				if (ageObj.getDays() > 0) {
					if (DPDoctorUtils.anyStringEmpty(age))
						age = ageObj.getDays() + "days";
					else
						age = age + " " + ageObj.getDays() + "days";
				}
			}
		}

		if (patientDetails.getShowDOB()) {
			if (!DPDoctorUtils.anyStringEmpty(age, gender))
				patientDetailList.add("<b>Age | Gender: </b>" + age + " | " + gender);
			else if (!DPDoctorUtils.anyStringEmpty(age))
				patientDetailList.add("<b>Age | Gender: </b>" + age + " | --");
			else if (!DPDoctorUtils.anyStringEmpty(gender))
				patientDetailList.add("<b>Age | Gender: </b>-- | " + gender);
		}

		patientDetailList.add(uniqueEMRId);
		if (patientDetails.getShowDOB()) {
			if(patientDetails.getShowDate())patientDetailList.add("<b>Date: </b>" + sdf.format(date));
			patientDetailList
					.add("<b>Mobile: </b>" + (mobileNumber != null && mobileNumber != null ? mobileNumber : "--"));
		} else {
			patientDetailList
					.add("<b>Mobile: </b>" + (mobileNumber != null && mobileNumber != null ? mobileNumber : "--"));
			if(patientDetails.getShowDate())patientDetailList.add("<b>Date: </b>" + sdf.format(date));
		}

		if (patientDetails.getShowBloodGroup() && patientCard != null
				&& !DPDoctorUtils.anyStringEmpty(patientCard.getBloodGroup())) {
			patientDetailList.add("<b>Blood Group: </b>" + patientCard.getBloodGroup());
		}
		if (patientDetails.getShowCity() && patientCard != null && !DPDoctorUtils
				.anyStringEmpty(patientCard.getAddress() != null ? patientCard.getAddress().getCity() : null)) {
			patientDetailList.add("<b>City: </b>" + patientCard.getAddress().getCity());
		}
		if (patientDetails.getShowReferedBy() && patientCard != null && patientCard.getReferredBy() != null) {
			ReferencesCollection referencesCollection = referenceRepository.findOne(patientCard.getReferredBy());
			if (referencesCollection != null && !DPDoctorUtils.allStringsEmpty(referencesCollection.getReference()))
				patientDetailList.add("<b>Referred By: </b>" + referencesCollection.getReference());
		}

		boolean isBold = patientDetails.getStyle() != null && patientDetails.getStyle().getFontStyle() != null
				? containsIgnoreCase(FONTSTYLE.BOLD.getStyle(), patientDetails.getStyle().getFontStyle()) : false;
		boolean isItalic = patientDetails.getStyle() != null && patientDetails.getStyle().getFontStyle() != null
				? containsIgnoreCase(FONTSTYLE.ITALIC.getStyle(), patientDetails.getStyle().getFontStyle()) : false;
		String fontSize = patientDetails.getStyle() != null && patientDetails.getStyle().getFontSize() != null
				? patientDetails.getStyle().getFontSize() : "";

		for (int i = 0; i < patientDetailList.size(); i++) {
			String text = patientDetailList.get(i);
			if (isItalic)
				text = "<i>" + text + "</i>";
			if (isBold)
				text = "<b>" + text + "</b>";
			text = "<span style='font-size:" + fontSize + "'>" + text + "</span>";

			if (i % 2 == 0) {
				if (!DPDoctorUtils.anyStringEmpty(patientLeftText))
					patientLeftText = patientLeftText + "<br>" + text;
				else
					patientLeftText = text;
			} else {
				if (!DPDoctorUtils.anyStringEmpty(patientRightText))
					patientRightText = patientRightText + "<br>" + text;
				else
					patientRightText = text;
			}
		}
		parameters.put("patientLeftText", patientLeftText);
		parameters.put("patientRightText", patientRightText);
	}

	private ClinicalNotesJasperDetails getClinicalNotesJasperDetails(String clinicalNotesId, String contentLineStyle,
			Map<String, Object> parameters, Boolean showUSG, Boolean isCustomPDF, Boolean showLMP, Boolean showEDD,
			Boolean showNoOfChildren) {
		ClinicalNotesCollection clinicalNotesCollection = null;
		ClinicalNotesJasperDetails clinicalNotesJasperDetails = null;
		Boolean showTitle = false;
		try {
			clinicalNotesCollection = clinicalNotesRepository.findOne(new ObjectId(clinicalNotesId));
			if (clinicalNotesCollection != null) {
				if (clinicalNotesCollection.getDoctorId() != null && clinicalNotesCollection.getHospitalId() != null
						&& clinicalNotesCollection.getLocationId() != null) {

					clinicalNotesJasperDetails = new ClinicalNotesJasperDetails();
					if (clinicalNotesCollection.getVitalSigns() != null) {
						String vitalSigns = null;

						String pulse = clinicalNotesCollection.getVitalSigns().getPulse();
						pulse = (pulse != null && !pulse.isEmpty()
								? "Pulse: " + pulse + " " + VitalSignsUnit.PULSE.getUnit() : "");
						if (!DPDoctorUtils.allStringsEmpty(pulse))
							vitalSigns = pulse;

						String temp = clinicalNotesCollection.getVitalSigns().getTemperature();
						temp = (temp != null && !temp.isEmpty()
								? "Temperature: " + temp + " " + VitalSignsUnit.TEMPERATURE.getUnit() : "");
						if (!DPDoctorUtils.allStringsEmpty(temp)) {
							if (!DPDoctorUtils.allStringsEmpty(vitalSigns))
								vitalSigns = vitalSigns + ",  " + temp;
							else
								vitalSigns = temp;
						}

						String breathing = clinicalNotesCollection.getVitalSigns().getBreathing();
						breathing = (breathing != null && !breathing.isEmpty()
								? "Breathing: " + breathing + " " + VitalSignsUnit.BREATHING.getUnit() : "");
						if (!DPDoctorUtils.allStringsEmpty(breathing)) {
							if (!DPDoctorUtils.allStringsEmpty(vitalSigns))
								vitalSigns = vitalSigns + ",  " + breathing;
							else
								vitalSigns = breathing;
						}

						String weight = clinicalNotesCollection.getVitalSigns().getWeight();
						weight = (weight != null && !weight.isEmpty()
								? "Weight: " + weight + " " + VitalSignsUnit.WEIGHT.getUnit() : "");
						if (!DPDoctorUtils.allStringsEmpty(weight)) {
							if (!DPDoctorUtils.allStringsEmpty(vitalSigns))
								vitalSigns = vitalSigns + ",  " + weight;
							else
								vitalSigns = weight;
						}

						String bloodPressure = "";
						if (clinicalNotesCollection.getVitalSigns().getBloodPressure() != null) {
							String systolic = clinicalNotesCollection.getVitalSigns().getBloodPressure().getSystolic();
							systolic = systolic != null && !systolic.isEmpty() ? systolic : "";

							String diastolic = clinicalNotesCollection.getVitalSigns().getBloodPressure()
									.getDiastolic();
							diastolic = diastolic != null && !diastolic.isEmpty() ? diastolic : "";

							if (!DPDoctorUtils.anyStringEmpty(systolic, diastolic))
								bloodPressure = "B.P: " + systolic + "/" + diastolic + " "
										+ VitalSignsUnit.BLOODPRESSURE.getUnit();
							if (!DPDoctorUtils.allStringsEmpty(bloodPressure)) {
								if (!DPDoctorUtils.allStringsEmpty(vitalSigns))
									vitalSigns = vitalSigns + ",  " + bloodPressure;
								else
									vitalSigns = bloodPressure;
							}
						}

						String spo2 = clinicalNotesCollection.getVitalSigns().getSpo2();
						spo2 = (spo2 != null && !spo2.isEmpty() ? "SPO2: " + spo2 + " " + VitalSignsUnit.SPO2.getUnit()
								: "");
						if (!DPDoctorUtils.allStringsEmpty(spo2)) {
							if (!DPDoctorUtils.allStringsEmpty(vitalSigns))
								vitalSigns = vitalSigns + ",  " + spo2;
							else
								vitalSigns = spo2;
						}
						String height = clinicalNotesCollection.getVitalSigns().getHeight();
						height = (height != null && !height.isEmpty()
								? "Height: " + height + " " + VitalSignsUnit.HEIGHT.getUnit() : "");
						if (!DPDoctorUtils.allStringsEmpty(height)) {
							if (!DPDoctorUtils.allStringsEmpty(vitalSigns))
								vitalSigns = vitalSigns + ",  " + height;
							else
								vitalSigns = spo2;
						}

						String bmi = clinicalNotesCollection.getVitalSigns().getBmi();
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

						String bsa = clinicalNotesCollection.getVitalSigns().getBsa();
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
						clinicalNotesJasperDetails
								.setVitalSigns(vitalSigns != null && !vitalSigns.isEmpty() ? vitalSigns : null);
					}

					clinicalNotesJasperDetails.setObservations(clinicalNotesCollection.getObservation());
					clinicalNotesJasperDetails.setNotes(clinicalNotesCollection.getNote());
					clinicalNotesJasperDetails.setInvestigations(clinicalNotesCollection.getInvestigation());
					clinicalNotesJasperDetails.setDiagnosis(clinicalNotesCollection.getDiagnosis());
					clinicalNotesJasperDetails.setComplaints(clinicalNotesCollection.getComplaint());
					clinicalNotesJasperDetails.setPresentComplaint(clinicalNotesCollection.getPresentComplaint());
					clinicalNotesJasperDetails
							.setPresentComplaintHistory(clinicalNotesCollection.getPresentComplaintHistory());
					clinicalNotesJasperDetails.setGeneralExam(clinicalNotesCollection.getGeneralExam());
					clinicalNotesJasperDetails.setSystemExam(clinicalNotesCollection.getSystemExam());
					clinicalNotesJasperDetails.setMenstrualHistory(clinicalNotesCollection.getMenstrualHistory());
					clinicalNotesJasperDetails.setObstetricHistory(clinicalNotesCollection.getObstetricHistory());
					clinicalNotesJasperDetails
							.setProvisionalDiagnosis(clinicalNotesCollection.getProvisionalDiagnosis());

					if (!isCustomPDF || showUSG) {
						clinicalNotesJasperDetails.setIndicationOfUSG(clinicalNotesCollection.getIndicationOfUSG());
					}
					clinicalNotesJasperDetails.setPv(clinicalNotesCollection.getPv());
					clinicalNotesJasperDetails.setPa(clinicalNotesCollection.getPa());
					clinicalNotesJasperDetails.setPs(clinicalNotesCollection.getPs());
					clinicalNotesJasperDetails.setEcgDetails(clinicalNotesCollection.getEcgDetails());
					clinicalNotesJasperDetails.setxRayDetails(clinicalNotesCollection.getxRayDetails());
					clinicalNotesJasperDetails.setEcho(clinicalNotesCollection.getEcho());
					clinicalNotesJasperDetails.setHolter(clinicalNotesCollection.getHolter());
					clinicalNotesJasperDetails.setProcedureNote(clinicalNotesCollection.getProcedureNote());
					clinicalNotesJasperDetails.setNoseExam(clinicalNotesCollection.getNoseExam());
					clinicalNotesJasperDetails
							.setOralCavityThroatExam(clinicalNotesCollection.getOralCavityThroatExam());
					clinicalNotesJasperDetails
							.setIndirectLarygoscopyExam(clinicalNotesCollection.getIndirectLarygoscopyExam());
					clinicalNotesJasperDetails.setEarsExam(clinicalNotesCollection.getEarsExam());
					clinicalNotesJasperDetails.setNeckExam(clinicalNotesCollection.getNeckExam());
					clinicalNotesJasperDetails.setPcNose(clinicalNotesCollection.getPcNose());
					clinicalNotesJasperDetails.setPcOralCavity(clinicalNotesCollection.getPcOralCavity());
					clinicalNotesJasperDetails.setPcThroat(clinicalNotesCollection.getPcThroat());
					clinicalNotesJasperDetails.setPcEars(clinicalNotesCollection.getPcEars());
					clinicalNotesJasperDetails
							.setPersonalHistoryTobacco(clinicalNotesCollection.getPersonalHistoryTobacco());
					clinicalNotesJasperDetails.setPcEars(clinicalNotesCollection.getPcEars());
					clinicalNotesJasperDetails
							.setPersonalHistoryAlcohol(clinicalNotesCollection.getPersonalHistoryAlcohol());
					clinicalNotesJasperDetails.setPcEars(clinicalNotesCollection.getPcEars());
					clinicalNotesJasperDetails
							.setPersonalHistorySmoking(clinicalNotesCollection.getPersonalHistorySmoking());
					clinicalNotesJasperDetails.setPcEars(clinicalNotesCollection.getPcEars());
					clinicalNotesJasperDetails.setPersonalHistoryDiet(clinicalNotesCollection.getPersonalHistoryDiet());
					clinicalNotesJasperDetails.setPcEars(clinicalNotesCollection.getPcEars());
					clinicalNotesJasperDetails
							.setPersonalHistoryOccupation(clinicalNotesCollection.getPersonalHistoryOccupation());
					clinicalNotesJasperDetails.setGeneralHistoryDrugs(clinicalNotesCollection.getGeneralHistoryDrugs());
					clinicalNotesJasperDetails
							.setGeneralHistoryMedicine(clinicalNotesCollection.getGeneralHistoryMedicine());
					clinicalNotesJasperDetails
							.setGeneralHistoryAllergies(clinicalNotesCollection.getGeneralHistoryAllergies());
					clinicalNotesJasperDetails
							.setGeneralHistorySurgical(clinicalNotesCollection.getGeneralHistorySurgical());
					clinicalNotesJasperDetails.setPastHistory(clinicalNotesCollection.getPastHistory());
					clinicalNotesJasperDetails.setFamilyHistory(clinicalNotesCollection.getFamilyHistory());
					clinicalNotesJasperDetails.setPainScale(clinicalNotesCollection.getPainScale());
					if (!DPDoctorUtils.allStringsEmpty(clinicalNotesCollection.getPcNose())
							|| !DPDoctorUtils.allStringsEmpty(clinicalNotesCollection.getPcEars())
							|| !DPDoctorUtils.allStringsEmpty(clinicalNotesCollection.getPcOralCavity())
							|| !DPDoctorUtils.allStringsEmpty(clinicalNotesCollection.getPcThroat())) {
						parameters.put("ComplaintsTitle", "Complaints :");
						showTitle = true;
					}
					parameters.put("showPCTitle", showTitle);
					showTitle = false;
					if (!DPDoctorUtils.allStringsEmpty(clinicalNotesCollection.getEarsExam())
							|| !DPDoctorUtils.allStringsEmpty(clinicalNotesCollection.getNeckExam())
							|| !DPDoctorUtils.allStringsEmpty(clinicalNotesCollection.getIndirectLarygoscopyExam())
							|| !DPDoctorUtils.allStringsEmpty(clinicalNotesCollection.getOralCavityThroatExam())
							|| !DPDoctorUtils.allStringsEmpty(clinicalNotesCollection.getNoseExam())) {
						parameters.put("Examination", "Examination :");
						showTitle = true;
					}
					parameters.put("showExamTitle", showTitle);

					if (clinicalNotesCollection.getLmp() != null && (!isCustomPDF || showLMP))
						clinicalNotesJasperDetails
								.setLmp(new SimpleDateFormat("dd-MM-yyyy").format(clinicalNotesCollection.getLmp()));
					if (clinicalNotesCollection.getEdd() != null && (!isCustomPDF || showEDD))
						clinicalNotesJasperDetails
								.setEdd(new SimpleDateFormat("dd-MM-yyyy").format(clinicalNotesCollection.getEdd()));
					if ((!isCustomPDF || showNoOfChildren) && (clinicalNotesCollection.getNoOfMaleChildren() > 0
							|| clinicalNotesCollection.getNoOfFemaleChildren() > 0)) {
						clinicalNotesJasperDetails.setNoOfChildren(clinicalNotesCollection.getNoOfMaleChildren() + "|"
								+ clinicalNotesCollection.getNoOfFemaleChildren());
					}

					List<DBObject> diagramIds = new ArrayList<DBObject>();
					if (clinicalNotesCollection.getDiagrams() != null)
						for (ObjectId diagramId : clinicalNotesCollection.getDiagrams()) {
							DBObject diagram = new BasicDBObject();
							DiagramsCollection diagramsCollection = diagramsRepository.findOne(diagramId);
							if (diagramsCollection != null) {
								if (diagramsCollection.getDiagramUrl() != null) {
									diagram.put("url", getFinalImageURL(diagramsCollection.getDiagramUrl()));
								}
								diagram.put("tags", diagramsCollection.getTags());
								diagramIds.add(diagram);
							}
						}
					if (!diagramIds.isEmpty())
						clinicalNotesJasperDetails.setDiagrams(diagramIds);
					else
						clinicalNotesJasperDetails.setDiagrams(null);
				}
				if (parameters.get("followUpAppointment") == null
						&& !DPDoctorUtils.anyStringEmpty(clinicalNotesCollection.getAppointmentId())
						&& clinicalNotesCollection.getTime() != null) {
					SimpleDateFormat sdf = new SimpleDateFormat("MMM dd");
					String _24HourTime = String.format("%02d:%02d",
							clinicalNotesCollection.getTime().getFromTime() / 60,
							clinicalNotesCollection.getTime().getFromTime() % 60);
					SimpleDateFormat _24HourSDF = new SimpleDateFormat("HH:mm");
					SimpleDateFormat _12HourSDF = new SimpleDateFormat("hh:mm a");
					sdf.setTimeZone(TimeZone.getTimeZone("IST"));
					_24HourSDF.setTimeZone(TimeZone.getTimeZone("IST"));
					_12HourSDF.setTimeZone(TimeZone.getTimeZone("IST"));

					Date _24HourDt = _24HourSDF.parse(_24HourTime);
					String dateTime = _12HourSDF.format(_24HourDt) + ", "
							+ sdf.format(clinicalNotesCollection.getFromDate());
					parameters.put("followUpAppointment", "Next Review on " + dateTime);
				}
			} else {
				logger.warn("Clinical Notes not found. Please check clinicalNotesId.");
				throw new BusinessException(ServiceError.NotFound,
						"Clinical Notes not found. Please check clinicalNotesId.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return clinicalNotesJasperDetails;
	}

	private List<PrescriptionJasperDetails> getPrescriptionJasperDetails(String prescriptionId,
			DBObject prescriptionItemsObj, Map<String, Object> parameters, Boolean isLabPrint,
			PrintSettingsCollection printSettings) {
		PrescriptionCollection prescriptionCollection = null;
		List<PrescriptionJasperDetails> prescriptionItems = new ArrayList<PrescriptionJasperDetails>();
		try {
			prescriptionCollection = prescriptionRepository.findOne(new ObjectId(prescriptionId));
			if (prescriptionCollection != null) {
				prescriptionItemsObj.put("resourceId",
						"<b>RxID: </b>" + (prescriptionCollection.getUniqueEmrId() != null
								? prescriptionCollection.getUniqueEmrId() : "--"));
				if (prescriptionCollection.getDiagnosticTests() != null
						&& !prescriptionCollection.getDiagnosticTests().isEmpty()) {
					String labTest = "";
					for (TestAndRecordData tests : prescriptionCollection.getDiagnosticTests()) {
						DiagnosticTestCollection diagnosticTestCollection = diagnosticTestRepository
								.findOne(tests.getTestId());
						if (diagnosticTestCollection != null) {
							if (DPDoctorUtils.anyStringEmpty(labTest))
								labTest = diagnosticTestCollection.getTestName();
							else
								labTest = labTest + ", " + diagnosticTestCollection.getTestName();
						}
					}
					prescriptionItemsObj.put("labTest", labTest);
				}

				if (!isLabPrint) {
					if (!DPDoctorUtils.anyStringEmpty(prescriptionCollection.getAdvice()))
						prescriptionItemsObj.put("advice", prescriptionCollection.getAdvice());

					int no = 0;
					Boolean showIntructions = false, showDirection = false;
					if (prescriptionCollection.getItems() != null)
						for (PrescriptionItem prescriptionItem : prescriptionCollection.getItems()) {
							if (prescriptionItem != null && prescriptionItem.getDrugId() != null) {
								DrugCollection drug = drugRepository.findOne(prescriptionItem.getDrugId());
								if (drug != null) {
									String drugType = drug.getDrugType() != null
											? (drug.getDrugType().getType() != null ? drug.getDrugType().getType() : "")
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
									drugName = (drugType + drugName) == "" ? "--"
											: drugType + " " + drugName + genericName;
									String durationValue = prescriptionItem.getDuration() != null
											? (prescriptionItem.getDuration().getValue() != null
													? prescriptionItem.getDuration().getValue() : "")
											: "";
									String durationUnit = prescriptionItem
											.getDuration() != null
													? (prescriptionItem.getDuration().getDurationUnit() != null
															? (!DPDoctorUtils.anyStringEmpty(prescriptionItem
																	.getDuration().getDurationUnit().getUnit())
																			? prescriptionItem.getDuration()
																					.getDurationUnit().getUnit()
																			: "")
															: "")
													: "";

									String directions = "";
									if (prescriptionItem.getDirection() != null
											&& !prescriptionItem.getDirection().isEmpty()) {
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
										showIntructions = true;
										if (printSettings.getContentSetup() != null) {
											if (printSettings.getContentSetup().getInstructionAlign() != null
													&& printSettings.getContentSetup().getInstructionAlign()
															.equals(FieldAlign.HORIZONTAL)) {
												prescriptionItem.setInstructions(!DPDoctorUtils
														.anyStringEmpty(prescriptionItem.getInstructions())
																? "<b>Instruction </b> : "
																		+ prescriptionItem.getInstructions()
																: null);
											} else {
												prescriptionItem.setInstructions(!DPDoctorUtils
														.anyStringEmpty(prescriptionItem.getInstructions())
																? prescriptionItem.getInstructions() : null);
											}
										} else {
											prescriptionItem.setInstructions(
													!DPDoctorUtils.anyStringEmpty(prescriptionItem.getInstructions())
															? prescriptionItem.getInstructions() : null);
										}
									}
									String duration = "";
									if (durationValue == "" && durationValue == "")
										duration = "--";
									else
										duration = durationValue + " " + durationUnit;
									PrescriptionJasperDetails prescriptionJasperDetails = null;
									if (printSettings.getContentSetup() != null) {
										if (printSettings.getContentSetup().getInstructionAlign() != null
												&& printSettings.getContentSetup().getInstructionAlign()
														.equals(FieldAlign.HORIZONTAL)) {

											prescriptionJasperDetails = new PrescriptionJasperDetails(++no, drugName,
													!DPDoctorUtils.anyStringEmpty(prescriptionItem.getDosage())
															? prescriptionItem.getDosage() : "--",
													duration, directions.isEmpty() ? "--" : directions,
													!DPDoctorUtils.anyStringEmpty(prescriptionItem.getInstructions())
															? prescriptionItem.getInstructions() : null,
													genericName);
										} else {
											prescriptionJasperDetails = new PrescriptionJasperDetails(++no, drugName,
													!DPDoctorUtils.anyStringEmpty(prescriptionItem.getDosage())
															? prescriptionItem.getDosage() : "--",
													duration, directions.isEmpty() ? "--" : directions,
													!DPDoctorUtils.anyStringEmpty(prescriptionItem.getInstructions())
															? prescriptionItem.getInstructions() : "--",
													genericName);
										}
									} else {
										prescriptionJasperDetails = new PrescriptionJasperDetails(++no, drugName,
												!DPDoctorUtils.anyStringEmpty(prescriptionItem.getDosage())
														? prescriptionItem.getDosage() : "--",
												duration, directions.isEmpty() ? "--" : directions,
												!DPDoctorUtils.anyStringEmpty(prescriptionItem.getInstructions())
														? prescriptionItem.getInstructions() : "--",
												genericName);
									}
									prescriptionItems.add(prescriptionJasperDetails);
								}
							}
						}
					parameters.put("showIntructions", showIntructions);
					parameters.put("showDirection", showDirection);
					if (parameters.get("followUpAppointment") == null
							&& !DPDoctorUtils.anyStringEmpty(prescriptionCollection.getAppointmentId())
							&& prescriptionCollection.getTime() != null) {
						SimpleDateFormat sdf = new SimpleDateFormat("MMM dd");
						String _24HourTime = String.format("%02d:%02d",
								prescriptionCollection.getTime().getFromTime() / 60,
								prescriptionCollection.getTime().getFromTime() % 60);
						SimpleDateFormat _24HourSDF = new SimpleDateFormat("HH:mm");
						SimpleDateFormat _12HourSDF = new SimpleDateFormat("hh:mm a");
						sdf.setTimeZone(TimeZone.getTimeZone("IST"));
						_24HourSDF.setTimeZone(TimeZone.getTimeZone("IST"));
						_12HourSDF.setTimeZone(TimeZone.getTimeZone("IST"));

						Date _24HourDt = _24HourSDF.parse(_24HourTime);
						String dateTime = _12HourSDF.format(_24HourDt) + ", "
								+ sdf.format(prescriptionCollection.getFromDate());
						parameters.put("followUpAppointment", "Next Review on " + dateTime);
					}
				}

			} else {
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
	public PatientVisitResponse deleteVisit(String visitId, Boolean discarded) {
		PatientVisitResponse response = null;
		try {
			List<Prescription> prescriptions = new ArrayList<Prescription>();
			List<ClinicalNotes> clinicalNotes = new ArrayList<ClinicalNotes>();
			List<Records> records = new ArrayList<Records>();
			List<PatientTreatment> patientTreatments = new ArrayList<PatientTreatment>();

			PatientVisitCollection patientVisitCollection = patientVisitRepository.findOne(new ObjectId(visitId));
			patientVisitCollection.setDiscarded(discarded);
			patientVisitRepository.save(patientVisitCollection);
			// doctorPatientReceiptRepository.findOne(new ObjectId(receiptId));

			// discard treatment
			if (patientVisitCollection.getTreatmentId() != null)
				for (ObjectId id : patientVisitCollection.getTreatmentId()) {
					PatientTreatmentResponse patientTreatmentResponse = patientTreatmentServices.deletePatientTreatment(
							id.toString(), patientVisitCollection.getDoctorId().toString(),
							patientVisitCollection.getLocationId().toString(),
							patientVisitCollection.getHospitalId().toString(), discarded);
					PatientTreatment patientTreatment = new PatientTreatment();
					BeanUtil.map(patientTreatmentResponse, patientTreatment);
					patientTreatments.add(patientTreatment);
				}
			// discard Clinical Notes
			if (patientVisitCollection.getClinicalNotesId() != null)
				for (ObjectId id : patientVisitCollection.getClinicalNotesId()) {
					clinicalNotes.add(clinicalNotesService.deleteNote(id.toString(), discarded));
				}

			// discard prescription
			if (patientVisitCollection.getPrescriptionId() != null)
				for (ObjectId id : patientVisitCollection.getPrescriptionId()) {
					prescriptions.add(prescriptionServices.deletePrescription(id.toString(),
							patientVisitCollection.getDoctorId().toString(),
							patientVisitCollection.getHospitalId().toString(),
							patientVisitCollection.getLocationId().toString(),
							patientVisitCollection.getPatientId().toHexString(), discarded));
				}

			// discard records

			if (patientVisitCollection.getRecordId() != null)
				for (ObjectId id : patientVisitCollection.getRecordId()) {
					records.add(recordsService.deleteRecord(id.toString(), discarded));

				}
			EyePrescription eyePrescription = null;
			EyePrescriptionCollection eyePrescriptionCollection = null;
			// discard EyePrescription

			if (patientVisitCollection.getEyePrescriptionId() != null) {
				eyePrescriptionCollection = eyePrescriptionRepository
						.findOne(patientVisitCollection.getEyePrescriptionId());
				eyePrescriptionCollection.setDiscarded(discarded);

				eyePrescriptionRepository.save(eyePrescriptionCollection);

				eyePrescription = new EyePrescription();
				BeanUtil.map(eyePrescriptionCollection, eyePrescription);
			}
			response = new PatientVisitResponse();
			BeanUtil.map(patientVisitCollection, response);
			response.setPrescriptions(prescriptions);
			response.setClinicalNotes(clinicalNotes);
			response.setRecords(records);
			response.setPatientTreatment(patientTreatments);
			response.setEyePrescription(eyePrescription);
		} catch (BusinessException be) {
			logger.error(be);
			throw be;
		} catch (Exception e) {
			logger.error("Error while deleting visit " + e);
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while deleting visit" + e);
		}

		return response;
	}

	@Override
	@Transactional
	public Boolean smsVisit(String visitId, String doctorId, String locationId, String hospitalId,
			String mobileNumber) {
		Boolean response = false;
		try {
			PatientVisitCollection patientVisitCollection = patientVisitRepository.findOne(new ObjectId(visitId));
			if (patientVisitCollection != null) {
				if (doctorId != null && hospitalId != null && locationId != null) {
					if (patientVisitCollection.getPrescriptionId() != null) {
						for (ObjectId prescriptionId : patientVisitCollection.getPrescriptionId()) {
							response = prescriptionServices.smsPrescription(prescriptionId.toString(), doctorId,
									locationId, hospitalId, mobileNumber, "VISITS");
						}
					}
				}
				else
				{
					if (patientVisitCollection.getPrescriptionId() != null) {
						for (ObjectId prescriptionId : patientVisitCollection.getPrescriptionId()) {
							response = prescriptionServices.smsPrescriptionforWeb(prescriptionId.toString(), doctorId, locationId, hospitalId, mobileNumber, "VISITS");
						}
					}
				}
			} else {
				logger.warn("Visit not found!");
				throw new BusinessException(ServiceError.Unknown, "Visit not found!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	public boolean containsIgnoreCase(String str, List<String> list) {
		if (list != null && !list.isEmpty())
			for (String i : list) {
				if (i.equalsIgnoreCase(str))
					return true;
			}
		return false;
	}

	@Override
	@Transactional
	public PatientVisitResponse getVisit(String visitId) {
		PatientVisitResponse response = null;
		try {
			Appointment appointment = null;
			PatientVisitCollection patientVisitCollection = patientVisitRepository.findOne(new ObjectId(visitId));
			if (patientVisitCollection != null) {
				List<Prescription> prescriptions = new ArrayList<Prescription>();
				List<ClinicalNotes> clinicalNotes = new ArrayList<ClinicalNotes>();
				List<Records> records = new ArrayList<Records>();
				List<PatientTreatment> patientTreatments = new ArrayList<PatientTreatment>();

				if (patientVisitCollection.getPrescriptionId() != null
						&& !patientVisitCollection.getPrescriptionId().isEmpty()) {
					prescriptions.addAll(prescriptionServices.getPrescriptionsByIds(
							patientVisitCollection.getPrescriptionId(), patientVisitCollection.getId()));
				}
				if (!DPDoctorUtils.anyStringEmpty(patientVisitCollection.getAppointmentId())) {

					AppointmentCollection appointmentCollection = appointmentRepository
							.findByAppointmentId(patientVisitCollection.getAppointmentId());
					appointment = new Appointment();
					BeanUtil.map(appointmentCollection, appointment);

				}

				if (patientVisitCollection.getClinicalNotesId() != null
						&& !patientVisitCollection.getClinicalNotesId().isEmpty()) {
					for (ObjectId clinicalNotesId : patientVisitCollection.getClinicalNotesId()) {
						ClinicalNotes clinicalNote = clinicalNotesService.getNotesById(clinicalNotesId.toString(),
								patientVisitCollection.getId());
						if (clinicalNote != null) {
							if (clinicalNote.getDiagrams() != null && !clinicalNote.getDiagrams().isEmpty()) {
								clinicalNote.setDiagrams(getFinalDiagrams(clinicalNote.getDiagrams()));
							}
							clinicalNotes.add(clinicalNote);
						}
					}
				}

				if (patientVisitCollection.getRecordId() != null && !patientVisitCollection.getRecordId().isEmpty()) {
					records = recordsService.getRecordsByIds(patientVisitCollection.getRecordId(),
							patientVisitCollection.getId());
					// if (records != null && !records.isEmpty()) {
					// records.addAll(records);
					// }
				}
				if (patientVisitCollection.getTreatmentId() != null
						&& !patientVisitCollection.getTreatmentId().isEmpty()) {
					patientTreatments = patientTreatmentServices.getPatientTreatmentByIds(
							patientVisitCollection.getTreatmentId(), patientVisitCollection.getId());
					// if (patientTreatments != null &&
					// !patientTreatments.isEmpty()) {
					// patientTreatments.addAll(patientTreatments);
					// }
				}
				response = new PatientVisitResponse();
				BeanUtil.map(patientVisitCollection, response);
				response.setAppointmentRequest(appointment);
				response.setPrescriptions(prescriptions);
				response.setClinicalNotes(clinicalNotes);
				response.setRecords(records);
				response.setPatientTreatment(patientTreatments);
			} else {
				logger.warn("Visit not found!");
				throw new BusinessException(ServiceError.NotFound, "Visit not found!");
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
	public List<PatientVisit> getVisitsHandheld(String doctorId, String locationId, String hospitalId, String patientId,
			int page, int size, Boolean isOTPVerified, String updatedTime) {
		List<PatientVisit> response = null;
		try {
			List<VisitedFor> visitedFors = new ArrayList<VisitedFor>();
			visitedFors.add(VisitedFor.CLINICAL_NOTES);
			visitedFors.add(VisitedFor.PRESCRIPTION);
			visitedFors.add(VisitedFor.REPORTS);
			visitedFors.add(VisitedFor.TREATMENT);

			long createdTimestamp = Long.parseLong(updatedTime);

			ObjectId patientObjectId = null, doctorObjectId = null, locationObjectId = null, hospitalObjectId = null;
			if (!DPDoctorUtils.anyStringEmpty(patientId))
				patientObjectId = new ObjectId(patientId);
			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				doctorObjectId = new ObjectId(doctorId);
			if (!DPDoctorUtils.anyStringEmpty(locationId))
				locationObjectId = new ObjectId(locationId);
			if (!DPDoctorUtils.anyStringEmpty(hospitalId))
				hospitalObjectId = new ObjectId(hospitalId);

			Criteria criteria = new Criteria("updatedTime").gte(new Date(createdTimestamp)).and("visitedFor")
					.in(visitedFors).and("patientId").is(patientObjectId).and("isPatientDiscarded").is(false);
			if (!isOTPVerified) {
				if (!DPDoctorUtils.anyStringEmpty(doctorObjectId))
					criteria.and("doctorId").is(doctorObjectId);
				if (!DPDoctorUtils.anyStringEmpty(locationObjectId, hospitalObjectId))
					criteria.and("locationId").is(locationObjectId).and("hospitalId").is(hospitalObjectId);
			}
			Aggregation aggregation = null;
			if (size > 0)
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")), Aggregation.skip((page) * size),
						Aggregation.limit(size));
			else
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));
			AggregationResults<PatientVisit> aggregationResults = mongoTemplate.aggregate(aggregation,
					PatientVisitCollection.class, PatientVisit.class);
			response = aggregationResults.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error while geting patient Visit : " + e.getCause().getMessage());
			throw new BusinessException(ServiceError.Unknown,
					"Error while geting patient Visit : " + e.getCause().getMessage());
		}
		return response;
	}

	@Override
	@Transactional
	public String editRecord(String id, VisitedFor visitedFor) {
		PatientVisitCollection patientTrackCollection = null;
		try {
			switch (visitedFor) {
			case PRESCRIPTION:
				patientTrackCollection = patientVisitRepository.findByPrescriptionId(new ObjectId(id));
				break;
			case CLINICAL_NOTES:
				patientTrackCollection = patientVisitRepository.findByClinialNotesId(new ObjectId(id));
				break;
			case REPORTS:
				patientTrackCollection = patientVisitRepository.findByRecordId(new ObjectId(id));
			case TREATMENT:
				patientTrackCollection = patientVisitRepository.findByTreatmentId(new ObjectId(id));
				break;
			default:
				break;
			}
			if (patientTrackCollection != null) {
				patientTrackCollection.setUpdatedTime(new Date());
				patientTrackCollection = patientVisitRepository.save(patientTrackCollection);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error while editing patient visit record : " + e.getCause().getMessage());
			throw new BusinessException(ServiceError.Unknown,
					"Error while editing patient visit record : " + e.getCause().getMessage());
		}
		return patientTrackCollection.getId().toString();

	}

	private List<Diagram> getFinalDiagrams(List<Diagram> diagrams) {
		for (Diagram diagram : diagrams) {
			if (diagram.getDiagramUrl() != null) {
				diagram.setDiagramUrl(getFinalImageURL(diagram.getDiagramUrl()));
			}
		}
		return diagrams;
	}

	@Override
	@Transactional
	public int getVisitCount(ObjectId doctorObjectId, ObjectId patientObjectId, ObjectId locationObjectId,
			ObjectId hospitalObjectId, boolean isOTPVerified) {
		Integer visitCount = 0;
		try {
			List<VisitedFor> visitedFors = new ArrayList<VisitedFor>();
			visitedFors.add(VisitedFor.CLINICAL_NOTES);
			visitedFors.add(VisitedFor.PRESCRIPTION);
			visitedFors.add(VisitedFor.REPORTS);
			visitedFors.add(VisitedFor.TREATMENT);
			visitedFors.add(VisitedFor.EYE_PRESCRIPTION);

			Criteria criteria = new Criteria("discarded").is(false).and("patientId").is(patientObjectId)
					.and("visitedFor").in(visitedFors).and("isPatientDiscarded").is(false);
			if (!isOTPVerified) {
				if (!DPDoctorUtils.anyStringEmpty(locationObjectId, hospitalObjectId))
					criteria.and("locationId").is(locationObjectId).and("hospitalId").is(hospitalObjectId);
				if (!DPDoctorUtils.anyStringEmpty(doctorObjectId))
					criteria.and("doctorId").is(doctorObjectId);
			}
			visitCount = (int) mongoTemplate.count(new Query(criteria), PatientVisitCollection.class);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error while getting Visits Count");
			throw new BusinessException(ServiceError.Unknown, "Error while getting Visits Count");
		}
		return visitCount;
	}

	@Override
	public String getPatientVisitFile(String visitId, Boolean showPH, Boolean showPLH, Boolean showFH, Boolean showDA,
			Boolean showUSG, Boolean isLabPrint, Boolean isCustomPDF, Boolean showLMP, Boolean showEDD,
			Boolean showNoOfChildren) {
		String response = null;
		HistoryCollection historyCollection = null;
		try {
			PatientVisitLookupResponse patientVisitLookupResponse = mongoTemplate.aggregate(Aggregation.newAggregation(
					Aggregation.match(new Criteria("id").is(new ObjectId(visitId))),
					Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"), Aggregation.unwind("doctor"),
					Aggregation.lookup("location_cl", "locationId", "_id", "location"), Aggregation.unwind("location"),
					Aggregation.lookup("patient_cl", "patientId", "userId", "patient"),
					new CustomAggregationOperation(new BasicDBObject("$unwind",
							new BasicDBObject("path", "$patient").append("preserveNullAndEmptyArrays",
									true))),
					new CustomAggregationOperation(new BasicDBObject("$redact",
							new BasicDBObject("$cond",
									new BasicDBObject("if",
											new BasicDBObject("$eq",
													Arrays.asList("$patient.locationId", "$locationId")))
															.append("then", "$$KEEP").append("else", "$$PRUNE")))),

					Aggregation.lookup("user_cl", "patientId", "_id", "patientUser"),
					Aggregation.unwind("patientUser")), PatientVisitCollection.class, PatientVisitLookupResponse.class)
					.getUniqueMappedResult();

			if (patientVisitLookupResponse != null) {
				PatientCollection patient = patientVisitLookupResponse.getPatient();
				UserCollection user = patientVisitLookupResponse.getPatientUser();

				if (showPH || showPLH || showFH || showDA) {
					historyCollection = historyRepository.findHistory(patientVisitLookupResponse.getLocationId(),
							patientVisitLookupResponse.getHospitalId(), patientVisitLookupResponse.getPatientId());
				}
				JasperReportResponse jasperReportResponse = createJasper(patientVisitLookupResponse, patient, user,
						historyCollection, showPH, showPLH, showFH, showDA, showUSG, isLabPrint, isCustomPDF, showLMP,
						showEDD, showNoOfChildren);
				if (jasperReportResponse != null)
					response = getFinalImageURL(jasperReportResponse.getPath());
				if (jasperReportResponse != null && jasperReportResponse.getFileSystemResource() != null)
					if (jasperReportResponse.getFileSystemResource().getFile().exists())
						jasperReportResponse.getFileSystemResource().getFile().delete();
			} else {
				logger.warn("Patient Visit Id does not exist");
				throw new BusinessException(ServiceError.NotFound, "Patient Visit Id does not exist");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error while getting Patient Visits PDF");
			throw new BusinessException(ServiceError.Unknown, "Error while getting Patient Visits PDF");
		}
		return response;
	}

	private Appointment addVisitAppointment(AppointmentRequest appointment) {
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
	public void updateAppointmentTime(ObjectId visitId, String appointmentId, WorkingHours workingHours,
			Date fromDate) {
		try {
			PatientVisitCollection patientVisitCollection = patientVisitRepository.findOne(visitId);
			patientVisitCollection.setAppointmentId(appointmentId);
			patientVisitCollection.setFromDate(fromDate);
			patientVisitCollection.setTime(workingHours);
			patientVisitCollection.setUpdatedTime(new Date());
			patientVisitRepository.save(patientVisitCollection);

			if (patientVisitCollection.getClinicalNotesId() != null
					&& !patientVisitCollection.getClinicalNotesId().isEmpty()) {
				for (ObjectId clinicalNotesId : patientVisitCollection.getClinicalNotesId()) {
					ClinicalNotesCollection clinicalNotesCollection = clinicalNotesRepository.findOne(clinicalNotesId);
					clinicalNotesCollection.setAppointmentId(appointmentId);
					clinicalNotesCollection.setFromDate(fromDate);
					clinicalNotesCollection.setTime(workingHours);
					clinicalNotesCollection.setUpdatedTime(new Date());
					clinicalNotesRepository.save(clinicalNotesCollection);
				}
			}
			if (patientVisitCollection.getPrescriptionId() != null
					&& !patientVisitCollection.getPrescriptionId().isEmpty()) {
				for (ObjectId prescriptionId : patientVisitCollection.getPrescriptionId()) {
					PrescriptionCollection prescriptionCollection = prescriptionRepository.findOne(prescriptionId);
					prescriptionCollection.setAppointmentId(appointmentId);
					prescriptionCollection.setFromDate(fromDate);
					prescriptionCollection.setTime(workingHours);
					prescriptionCollection.setUpdatedTime(new Date());
					prescriptionRepository.save(prescriptionCollection);
				}
			}
			if (patientVisitCollection.getTreatmentId() != null && !patientVisitCollection.getTreatmentId().isEmpty()) {
				for (ObjectId treatmentId : patientVisitCollection.getTreatmentId()) {
					PatientTreatmentCollection patientTreatmentCollection = patientTreamentRepository
							.findOne(treatmentId);
					patientTreatmentCollection.setAppointmentId(appointmentId);
					patientTreatmentCollection.setFromDate(fromDate);
					patientTreatmentCollection.setTime(workingHours);
					patientTreatmentCollection.setUpdatedTime(new Date());
					patientTreamentRepository.save(patientTreatmentCollection);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error while updating Appointment Time");
			throw new BusinessException(ServiceError.Unknown, "Error while updating Appointment Time");
		}
	}

	@Override
	public PatientVisitResponse getPatientLastVisit(String doctorId, String locationId, String hospitalId, String patientId) {
		PatientVisitResponse response = null;
		try {
			
			ObjectId patientObjectId = new ObjectId(patientId), 
					doctorObjectId = new ObjectId(doctorId), 
					locationObjectId = new ObjectId(locationId), 
					hospitalObjectId = new ObjectId(hospitalId);
			
			Criteria criteria = new Criteria("patientId").is(patientObjectId).and("doctorId").is(doctorObjectId)
					.and("locationId").is(locationObjectId).and("hospitalId").is(hospitalObjectId).and("isPatientDiscarded").is(false);

			Aggregation aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.lookup("appointment_cl", "appointmentId", "appointmentId", "appointmentRequest"),
						new CustomAggregationOperation(new BasicDBObject("$unwind",
								new BasicDBObject("path", "$appointmentRequest").append("preserveNullAndEmptyArrays",
										true))),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")),
						Aggregation.limit(1)
					);
			
			List<PatientVisitLookupBean> patientVisitlookupbeans = mongoTemplate.aggregate(aggregation, PatientVisitCollection.class, PatientVisitLookupBean.class).getMappedResults();
			
			if (patientVisitlookupbeans != null && !patientVisitlookupbeans.isEmpty()) {
				for (PatientVisitLookupBean patientVisitlookupBean : patientVisitlookupbeans) {
					response = new PatientVisitResponse();
					BeanUtil.map(patientVisitlookupBean, response);

					if (patientVisitlookupBean.getPrescriptionId() != null) {
						List<Prescription> prescriptions = prescriptionServices.getPrescriptionsByIds(
								patientVisitlookupBean.getPrescriptionId(), patientVisitlookupBean.getId());
						response.setPrescriptions(prescriptions);
					}

					if (patientVisitlookupBean.getClinicalNotesId() != null) {
						List<ClinicalNotes> clinicalNotes = new ArrayList<ClinicalNotes>();
						for (ObjectId clinicalNotesId : patientVisitlookupBean.getClinicalNotesId()) {
							ClinicalNotes clinicalNote = clinicalNotesService.getNotesById(clinicalNotesId.toString(),
									patientVisitlookupBean.getId());
							if (clinicalNote != null) {
								if (clinicalNote.getDiagrams() != null && !clinicalNote.getDiagrams().isEmpty()) {
									clinicalNote.setDiagrams(getFinalDiagrams(clinicalNote.getDiagrams()));
								}
								clinicalNotes.add(clinicalNote);
							}
						}
						response.setClinicalNotes(clinicalNotes);
					}

					if (patientVisitlookupBean.getRecordId() != null) {
						List<Records> records = recordsService.getRecordsByIds(patientVisitlookupBean.getRecordId(),
								patientVisitlookupBean.getId());
						response.setRecords(records);
					}

					if (patientVisitlookupBean.getTreatmentId() != null) {
						List<PatientTreatment> patientTreatment = patientTreatmentServices.getPatientTreatmentByIds(
								patientVisitlookupBean.getTreatmentId(), patientVisitlookupBean.getId());
						response.setPatientTreatment(patientTreatment);
					}

					if (patientVisitlookupBean.getEyePrescriptionId() != null) {
						EyePrescription eyePrescription = prescriptionServices
								.getEyePrescription(String.valueOf(patientVisitlookupBean.getEyePrescriptionId()));
						response.setEyePrescription(eyePrescription);

					}
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error while geting patient last Visit : " + e.getCause().getMessage());
			throw new BusinessException(ServiceError.Unknown,
					"Error while geting patient last Visit : " + e.getCause().getMessage());
		}
		return response;
	}

}
