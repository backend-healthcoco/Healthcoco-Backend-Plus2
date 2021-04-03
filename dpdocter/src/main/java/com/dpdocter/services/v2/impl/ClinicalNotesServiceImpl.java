package com.dpdocter.services.v2.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.mail.MessagingException;

import org.apache.log4j.Logger;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.ClinicalnoteLookupBean;
import com.dpdocter.beans.CustomAggregationOperation;
import com.dpdocter.beans.TreatmentObservation;
import com.dpdocter.beans.v2.AppointmentDetails;
import com.dpdocter.beans.v2.ClinicalNotes;
import com.dpdocter.beans.v2.Diagram;
import com.dpdocter.collections.AppointmentCollection;
import com.dpdocter.collections.ClinicalNotesCollection;
import com.dpdocter.collections.DiagramsCollection;
import com.dpdocter.collections.PatientVisitCollection;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.AppointmentRepository;
import com.dpdocter.repository.ClinicalNotesRepository;
import com.dpdocter.repository.PatientVisitRepository;
import com.dpdocter.services.MailService;
import com.dpdocter.services.PushNotificationServices;
import com.dpdocter.services.v2.ClinicalNotesService;
import com.mongodb.BasicDBObject;

import common.util.web.DPDoctorUtils;

@Service(value = "ClinicalNotesServiceImplV2")
public class ClinicalNotesServiceImpl implements ClinicalNotesService {

	private static Logger logger = Logger.getLogger(ClinicalNotesServiceImpl.class.getName());

	@Autowired
	private MailService mailService;
	
	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private PatientVisitRepository patientVisitRepository;

	@Autowired
	PushNotificationServices pushNotificationServices;
	
	@Autowired
	private AppointmentRepository appointmentRepository;
	
	@Autowired
	private ClinicalNotesRepository clinicalNotesRepository;
	
	@Value(value = "${image.path}")
	private String imagePath;

	@Value(value = "${ClinicalNotes.getPatientsClinicalNotesWithVerifiedOTP}")
	private String getPatientsClinicalNotesWithVerifiedOTP;

	@Value(value = "${jasper.print.clinicalnotes.a4.fileName}")
	private String clinicalNotesA4FileName;

	@Value(value = "${jasper.print.visit.a4.fileName}")
	private String visitA4FileName;

	@Value(value = "${jasper.print.visit.clinicalnotes.a4.fileName}")
	private String visitClinicalNotesA4FileName;

	@Value(value = "${jasper.print.visit.diagrams.a4.fileName}")
	private String visitDiagramsA4FileName;


	@Override
	@Transactional
	public List<ClinicalNotes> getClinicalNotes(int page, int size, String doctorId, String locationId,
			String hospitalId, String patientId, String updatedTime, Boolean isOTPVerified, String from,String to,Boolean discarded,
			Boolean inHistory) {
		List<ClinicalnoteLookupBean> clinicalNotesCollections = null;
		List<ClinicalNotes> clinicalNotes = null;
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

			long createdTimestamp = Long.parseLong(updatedTime);

			Criteria criteria = new Criteria("updatedTime").gt(new Date(createdTimestamp)).and("patientId")
					.is(patientObjectId).and("isPatientDiscarded").ne(true);
			
			Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("IST"));

			DateTime fromDateTime = null, toDateTime= null;
			if (!DPDoctorUtils.anyStringEmpty(from)) {
				localCalendar.setTime(new Date(Long.parseLong(from)));
				int currentDay = localCalendar.get(Calendar.DATE);
				int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
				int currentYear = localCalendar.get(Calendar.YEAR);

				 fromDateTime = new DateTime(currentYear, currentMonth, currentDay, 0, 0, 0, DateTimeZone.forTimeZone(TimeZone.getTimeZone("IST")));				
			}
			if (!DPDoctorUtils.anyStringEmpty(to)) {
				localCalendar.setTime(new Date(Long.parseLong(to)));
				int currentDay = localCalendar.get(Calendar.DATE);
				int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
				int currentYear = localCalendar.get(Calendar.YEAR);

				 toDateTime = new DateTime(currentYear, currentMonth, currentDay, 23, 59, 59, DateTimeZone.forTimeZone(TimeZone.getTimeZone("IST")));			
			}
			if(fromDateTime!= null && toDateTime != null) {
				criteria.and("createdTime").gte(fromDateTime).lte(toDateTime);
			}else if(fromDateTime!= null) {
				criteria.and("createdTime").gte(fromDateTime);
			}else if(toDateTime != null) {
				criteria.and("createdTime").lte(toDateTime);
			}


			
			if (discarded !=null)
				criteria.and("discarded").is(discarded);
			if (inHistory)
				criteria.and("inHistory").is(inHistory);

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
						new CustomAggregationOperation(new Document("$unwind",
								new BasicDBObject("path", "$appointmentRequest").append("preserveNullAndEmptyArrays",
										true))),
						Aggregation.lookup("patient_treatment_cl", "doctorId","_id", "treatments"),
						Aggregation.unwind("treatments",true),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")), Aggregation.skip((page) * size),
						Aggregation.limit(size));
			else
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.lookup("appointment_cl", "appointmentId", "appointmentId",
								"appointmentRequest"),
						new CustomAggregationOperation(
								new Document("$unwind",
										new BasicDBObject("path", "$appointmentRequest")
												.append("preserveNullAndEmptyArrays", true))),
						Aggregation.lookup("patient_treatment_cl", "doctorId","_id", "treatments"),
						Aggregation.unwind("treatments",true),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));

			AggregationResults<ClinicalnoteLookupBean> aggregationResults = mongoTemplate.aggregate(aggregation,
					ClinicalNotesCollection.class, ClinicalnoteLookupBean.class);
			clinicalNotesCollections = aggregationResults.getMappedResults();

			if (clinicalNotesCollections != null && !clinicalNotesCollections.isEmpty()) {
				clinicalNotes = new ArrayList<ClinicalNotes>();
				for (ClinicalnoteLookupBean clinicalNotesCollection : clinicalNotesCollections) {
					ClinicalNotes clinicalNote = getClinicalNote(clinicalNotesCollection);
					clinicalNotes.add(clinicalNote);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(" Error Occurred While Getting Clinical Notes");
			try {
				mailService.sendExceptionMail("Backend Business Exception :: While getting clinical notes",
						e.getMessage());
			} catch (MessagingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Clinical Notes");
		}
		return clinicalNotes;
	}

	
	public ClinicalNotes getClinicalNote(ClinicalnoteLookupBean clinicalNotesCollection) {
		ClinicalNotes clinicalNote = new ClinicalNotes();
		BeanUtil.map(clinicalNotesCollection, clinicalNote);
		TreatmentObservation treatmentObservation =new TreatmentObservation();
		treatmentObservation.setTreatments(clinicalNotesCollection.getTreatments());
		treatmentObservation.setObservations(clinicalNotesCollection.getTreatmentObservation());
		clinicalNote.setTreatmentObservation(treatmentObservation);

		// if(clinicalNotesCollection.getComplaints() != null &&
		// !clinicalNotesCollection.getComplaints().isEmpty())
		// clinicalNote.setComplaints(sortComplaints(mongoTemplate.aggregate(Aggregation.newAggregation(Aggregation.match(new
		// Criteria("id").in(clinicalNotesCollection.getComplaints()))),
		// ComplaintCollection.class, Complaint.class).getMappedResults(),
		// clinicalNotesCollection.getComplaints()));
		// if(clinicalNotesCollection.getInvestigations() != null &&
		// !clinicalNotesCollection.getInvestigations().isEmpty())
		// clinicalNote.setInvestigations(sortInvestigations(mongoTemplate.aggregate(Aggregation.newAggregation(Aggregation.match(new
		// Criteria("id").in(clinicalNotesCollection.getInvestigations()))),
		// InvestigationCollection.class,
		// Investigation.class).getMappedResults(),
		// clinicalNotesCollection.getInvestigations()));
		// if(clinicalNotesCollection.getObservations() != null &&
		// !clinicalNotesCollection.getObservations().isEmpty())
		// clinicalNote.setObservations(sortObservations(mongoTemplate.aggregate(Aggregation.newAggregation(Aggregation.match(new
		// Criteria("id").in(clinicalNotesCollection.getObservations()))),
		// ObservationCollection.class, Observation.class).getMappedResults(),
		// clinicalNotesCollection.getObservations()));
		// if(clinicalNotesCollection.getDiagnoses() != null &&
		// !clinicalNotesCollection.getDiagnoses().isEmpty())
		// clinicalNote.setDiagnoses(sortDiagnoses(mongoTemplate.aggregate(Aggregation.newAggregation(Aggregation.match(new
		// Criteria("id").in(clinicalNotesCollection.getDiagnoses()))),
		// DiagnosisCollection.class, Diagnoses.class).getMappedResults(),
		// clinicalNotesCollection.getDiagnoses()));
		// if(clinicalNotesCollection.getNotes() != null &&
		// !clinicalNotesCollection.getNotes().isEmpty())
		// clinicalNote.setNotes(sortNotes(mongoTemplate.aggregate(Aggregation.newAggregation(Aggregation.match(new
		// Criteria("id").in(clinicalNotesCollection.getNotes()))),
		// NotesCollection.class, Notes.class).getMappedResults(),
		// clinicalNotesCollection.getNotes()));
		if (clinicalNotesCollection.getDiagrams() != null && !clinicalNotesCollection.getDiagrams().isEmpty())
			clinicalNote
					.setDiagrams(
							sortDiagrams(
									mongoTemplate
											.aggregate(
													Aggregation.newAggregation(Aggregation.match(new Criteria("id")
															.in(clinicalNotesCollection.getDiagrams()))),
													DiagramsCollection.class, Diagram.class)
											.getMappedResults(),
									clinicalNotesCollection.getDiagrams()));

		PatientVisitCollection patientVisitCollection = patientVisitRepository
				.findByClinicalNotesId(clinicalNotesCollection.getId());
		if (patientVisitCollection != null)
			clinicalNote.setVisitId(patientVisitCollection.getId().toString());

		return clinicalNote;
	}
	
	public List<Diagram> sortDiagrams(List<Diagram> mappedResults, List<ObjectId> diagrams) {
		List<Diagram> response = new ArrayList<Diagram>();
		if (mappedResults != null && !mappedResults.isEmpty()) {
			for (ObjectId id : diagrams)
				for (Diagram diagram : mappedResults)
					if (diagram.getId().equalsIgnoreCase(id.toString()))
						response.add(diagram);
		}
		return response;
	}

	@Override
	public List<ClinicalNotes> getClinicalNotes(String patientId, int page, int size, String updatedTime,
			Boolean discarded) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	@Transactional
	public ClinicalNotes getNotesById(String id, ObjectId visitId) {
		ClinicalNotes clinicalNote = null;
		try {
			ClinicalNotesCollection clinicalNotesCollection = clinicalNotesRepository.findById(new ObjectId(id)).orElse(null);
			if (clinicalNotesCollection != null) {
				clinicalNote = new ClinicalNotes();
				BeanUtil.map(clinicalNotesCollection, clinicalNote);
				if (!DPDoctorUtils.anyStringEmpty(clinicalNotesCollection.getAppointmentId())) {
					AppointmentCollection appointmentCollection = appointmentRepository
							.findByAppointmentId(clinicalNotesCollection.getAppointmentId());
					AppointmentDetails appointment = new AppointmentDetails();
					BeanUtil.map(appointmentCollection, appointment);
					clinicalNote.setAppointmentRequest(appointment);
				}

				// if(clinicalNotesCollection.getComplaints() != null &&
				// !clinicalNotesCollection.getComplaints().isEmpty())
				// clinicalNote.setComplaints(sortComplaints(mongoTemplate.aggregate(Aggregation.newAggregation(Aggregation.match(new
				// Criteria("id").in(clinicalNotesCollection.getComplaints()))),
				// ComplaintCollection.class,
				// Complaint.class).getMappedResults(),
				// clinicalNotesCollection.getComplaints()));
				// if(clinicalNotesCollection.getInvestigations() != null &&
				// !clinicalNotesCollection.getInvestigations().isEmpty())
				// clinicalNote.setInvestigations(sortInvestigations(mongoTemplate.aggregate(Aggregation.newAggregation(Aggregation.match(new
				// Criteria("id").in(clinicalNotesCollection.getInvestigations()))),
				// InvestigationCollection.class,
				// Investigation.class).getMappedResults(),
				// clinicalNotesCollection.getInvestigations()));
				// if(clinicalNotesCollection.getObservations() != null &&
				// !clinicalNotesCollection.getObservations().isEmpty())
				// clinicalNote.setObservations(sortObservations(mongoTemplate.aggregate(Aggregation.newAggregation(Aggregation.match(new
				// Criteria("id").in(clinicalNotesCollection.getObservations()))),
				// ObservationCollection.class,
				// Observation.class).getMappedResults(),
				// clinicalNotesCollection.getObservations()));
				// if(clinicalNotesCollection.getDiagnoses() != null &&
				// !clinicalNotesCollection.getDiagnoses().isEmpty())
				// clinicalNote.setDiagnoses(sortDiagnoses(mongoTemplate.aggregate(Aggregation.newAggregation(Aggregation.match(new
				// Criteria("id").in(clinicalNotesCollection.getDiagnoses()))),
				// DiagnosisCollection.class,
				// Diagnoses.class).getMappedResults(),
				// clinicalNotesCollection.getDiagnoses()));
				// if(clinicalNotesCollection.getNotes() != null &&
				// !clinicalNotesCollection.getNotes().isEmpty())
				// clinicalNote.setNotes(sortNotes(mongoTemplate.aggregate(Aggregation.newAggregation(Aggregation.match(new
				// Criteria("id").in(clinicalNotesCollection.getNotes()))),
				// NotesCollection.class, Notes.class).getMappedResults(),
				// clinicalNotesCollection.getNotes()));
				if (clinicalNotesCollection.getDiagrams() != null && !clinicalNotesCollection.getDiagrams().isEmpty())
					clinicalNote.setDiagrams(sortDiagrams(mongoTemplate.aggregate(Aggregation.newAggregation(
							Aggregation.match(new Criteria("id").in(clinicalNotesCollection.getDiagrams()))),

							DiagramsCollection.class, Diagram.class).getMappedResults(),
							clinicalNotesCollection.getDiagrams()));

				if (DPDoctorUtils.anyStringEmpty(visitId)) {
					PatientVisitCollection patientVisitCollection = patientVisitRepository
							.findByClinicalNotesId(clinicalNotesCollection.getId());
					if (patientVisitCollection != null)
						clinicalNote.setVisitId(patientVisitCollection.getId().toString());
				} else {
					clinicalNote.setVisitId(visitId.toString());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);

			try {
				mailService.sendExceptionMail("Backend Business Exception :: While getting notes for id:" + id,
						e.getMessage());
			} catch (MessagingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return clinicalNote;
	}


	@Override
	@Transactional
	public List<ClinicalNotes> getClinicalNotesNEWCODE(int page, int size, String doctorId, String locationId,
			String hospitalId, String patientId, String updatedTime, Boolean isOTPVerified, String from,String to,Boolean discarded,
			Boolean inHistory) {
		List<ClinicalnoteLookupBean> clinicalNotesCollections = null;
		List<ClinicalNotes> clinicalNotes = null;
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

			long createdTimestamp = Long.parseLong(updatedTime);

			Criteria criteria = new Criteria("updatedTime").gt(new Date(createdTimestamp)).and("patientId")
					.is(patientObjectId).and("isPatientDiscarded").ne(true);
			
			Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("IST"));

			DateTime fromDateTime = null, toDateTime= null;
			if (!DPDoctorUtils.anyStringEmpty(from)) {
				localCalendar.setTime(new Date(Long.parseLong(from)));
				int currentDay = localCalendar.get(Calendar.DATE);
				int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
				int currentYear = localCalendar.get(Calendar.YEAR);

				 fromDateTime = new DateTime(currentYear, currentMonth, currentDay, 0, 0, 0, DateTimeZone.forTimeZone(TimeZone.getTimeZone("IST")));				
			}
			if (!DPDoctorUtils.anyStringEmpty(to)) {
				localCalendar.setTime(new Date(Long.parseLong(to)));
				int currentDay = localCalendar.get(Calendar.DATE);
				int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
				int currentYear = localCalendar.get(Calendar.YEAR);

				 toDateTime = new DateTime(currentYear, currentMonth, currentDay, 23, 59, 59, DateTimeZone.forTimeZone(TimeZone.getTimeZone("IST")));			
			}
			if(fromDateTime!= null && toDateTime != null) {
				criteria.and("createdTime").gte(fromDateTime).lte(toDateTime);
			}else if(fromDateTime!= null) {
				criteria.and("createdTime").gte(fromDateTime);
			}else if(toDateTime != null) {
				criteria.and("createdTime").lte(toDateTime);
			}


			
			if (discarded !=null)
				criteria.and("discarded").is(discarded);
			if (inHistory)
				criteria.and("inHistory").is(inHistory);

			if (!isOTPVerified) {
				if (!DPDoctorUtils.anyStringEmpty(locationId, hospitalId))
					criteria.and("locationId").is(locationObjectId).and("hospitalId").is(hospitalObjectId);
				if (!DPDoctorUtils.anyStringEmpty(doctorId))
					criteria.and("doctorId").is(doctorObjectId);
			}

			Aggregation aggregation = null;

			if (size > 0)
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						new CustomAggregationOperation(new Document("$unwind",
								new BasicDBObject("path", "$_id").append("preserveNullAndEmptyArrays",
										true))),
						Aggregation.lookup("clinical_notes_cl", "_id", "_id", "clinicalNotes"),
						new CustomAggregationOperation(new Document("$unwind",
								new BasicDBObject("path", "$clinicalNotes").append("preserveNullAndEmptyArrays",
										true))),
						new CustomAggregationOperation(new Document("$unwind",
								new BasicDBObject("path", "$clinicalNotes.diagrams")
										.append("preserveNullAndEmptyArrays", true)
										.append("includeArrayIndex", "arrayIndex5"))),
						Aggregation.lookup("diagrams_cl", "clinicalNotes.diagrams", "_id", "diagrams"),
						new CustomAggregationOperation(new Document("$unwind",
								new BasicDBObject("path", "$diagrams").append("preserveNullAndEmptyArrays", true)
										.append("includeArrayIndex", "arrayIndex6"))),
						clinicalNotesFirstProjectAggregationOperation(), clinicalNotesFirstGroupAggregationOperation(),
//						clinicalNotesSecondProjectAggregationOperation(), clinicalNotesSecondGroupAggregationOperation(),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")), Aggregation.skip((page) * size),
						Aggregation.limit(size));
			else
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.lookup("appointment_cl", "appointmentId", "appointmentId",
								"appointmentRequest"),
						new CustomAggregationOperation(
								new Document("$unwind",
										new BasicDBObject("path", "$appointmentRequest")
												.append("preserveNullAndEmptyArrays", true))),
						Aggregation.lookup("patient_treatment_cl", "doctorId","_id", "treatments"),
						Aggregation.unwind("treatments",true),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));

			AggregationResults<ClinicalnoteLookupBean> aggregationResults = mongoTemplate.aggregate(aggregation,
					ClinicalNotesCollection.class, ClinicalnoteLookupBean.class);
			clinicalNotesCollections = aggregationResults.getMappedResults();

			if (clinicalNotesCollections != null && !clinicalNotesCollections.isEmpty()) {
				clinicalNotes = new ArrayList<ClinicalNotes>();
				for (ClinicalnoteLookupBean clinicalNotesCollection : clinicalNotesCollections) {
					ClinicalNotes clinicalNote = getClinicalNote(clinicalNotesCollection);
					clinicalNotes.add(clinicalNote);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(" Error Occurred While Getting Clinical Notes");
			try {
				mailService.sendExceptionMail("Backend Business Exception :: While getting clinical notes",
						e.getMessage());
			} catch (MessagingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Clinical Notes");
		}
		return clinicalNotes;
	}
	
	private AggregationOperation clinicalNotesFirstProjectAggregationOperation() {
		return new CustomAggregationOperation(new Document("$project", new BasicDBObject("_id", "$_id")
				.append("uniqueEmrId", "$uniqueEmrId").append("patientId", "$patientId").append("doctorId", "$doctorId")
				.append("locationId", "$locationId").append("hospitalId", "$hospitalId")
				.append("visitedTime", "$visitedTime").append("visitedFor", "$visitedFor")
				.append("treatmentId", "$treatmentId").append("recordId", "$recordId")
				.append("eyePrescriptionId", "$eyePrescriptionId").append("appointmentId", "$appointmentId")
				.append("time", "$time").append("fromDate", "$fromDate").append("discarded", "$discarded")
				.append("appointmentRequest", "$appointmentRequest").append("createdTime", "$createdTime")
				.append("updatedTime", "$updatedTime").append("createdBy", "$createdBy")
				.append("adminCreatedTime", "$adminCreatedTime")
				.append("clinicalNotes", "$clinicalNotes")
				.append("clinicalNotesid", "$clinicalNotes._id").append("clinicalNotesDiagrams._id", "$diagrams._id")
				.append("diagramUrl", "$diagrams.diagramUrl")
				.append("tags", "$diagrams.tags")
				.append("doctorId", "$diagrams.doctorId")
				.append("locationId", "$diagrams.locationId")
				.append("hospitalId", "$diagrams.hospitalId")
				.append("fileExtension", "$diagrams.fileExtension")
				.append("discarded", "$diagrams.discarded")
				.append("speciality", "$diagrams.speciality")
				.append("clinicalNotesId", "$clinicalNotes._id")));
	}

	private AggregationOperation clinicalNotesFirstGroupAggregationOperation() {
		return new CustomAggregationOperation(new Document("$group",
				new BasicDBObject("_id", "$_id").append("uniqueEmrId", new BasicDBObject("$first", "$uniqueEmrId"))
						.append("patientId", new BasicDBObject("$first", "$patientId"))
						.append("locationId", new BasicDBObject("$first", "$locationId"))
						.append("hospitalId", new BasicDBObject("$first", "$hospitalId"))
						.append("doctorId", new BasicDBObject("$first", "$doctorId"))
						.append("discarded", new BasicDBObject("$first", "$discarded"))
						.append("visitedTime", new BasicDBObject("$first", "$visitedTime"))
						.append("visitedFor", new BasicDBObject("$first", "$visitedFor"))
						.append("clinicalNotes", new BasicDBObject("$first", "$clinicalNotes"))
						.append("clinicalNotesDiagrams", new BasicDBObject("$push", "$clinicalNotesDiagrams"))
						.append("treatmentId", new BasicDBObject("$first", "$treatmentId"))
						.append("recordId", new BasicDBObject("$first", "$recordId"))
						.append("eyePrescriptionId", new BasicDBObject("$first", "$eyePrescriptionId"))
						.append("fromDate", new BasicDBObject("$first", "$fromDate"))
						.append("appointmentRequest", new BasicDBObject("$first", "$appointmentRequest"))
						.append("createdTime", new BasicDBObject("$first", "$createdTime"))
						.append("updatedTime", new BasicDBObject("$first", "$updatedTime"))
						.append("adminCreatedTime", new BasicDBObject("$first", "$adminCreatedTime"))
						.append("createdBy", new BasicDBObject("$first", "$createdBy"))));
	}

	private AggregationOperation clinicalNotesSecondProjectAggregationOperation() {
		return new CustomAggregationOperation(new Document("$project",
				new BasicDBObject("_id", "$_id").append("uniqueEmrId", "$uniqueEmrId").append("patientId", "$patientId")
						.append("doctorId", "$doctorId").append("locationId", "$locationId")
						.append("hospitalId", "$hospitalId").append("visitedTime", "$visitedTime")
						.append("visitedFor", "$visitedFor").append("treatmentId", "$treatmentId")
						.append("recordId", "$recordId").append("eyePrescriptionId", "$eyePrescriptionId")
						.append("appointmentId", "$appointmentId").append("time", "$time")
						.append("fromDate", "$fromDate").append("discarded", "$discarded")
						.append("appointmentRequest", "$appointmentRequest").append("createdTime", "$createdTime")
						.append("updatedTime", "$updatedTime").append("createdBy", "$createdBy")
						.append("prescriptions", "$prescriptions").append("clinicalNotes", "$clinicalNotes")
						.append("clinicalNotesDiagrams", "$clinicalNotesDiagrams")));
	}

	private AggregationOperation clinicalNotesSecondGroupAggregationOperation() {
		return new CustomAggregationOperation(new Document("$group",
				new BasicDBObject("_id", "$_id").append("uniqueEmrId", new BasicDBObject("$first", "$uniqueEmrId"))
						.append("patientId", new BasicDBObject("$first", "$patientId"))
						.append("locationId", new BasicDBObject("$first", "$locationId"))
						.append("hospitalId", new BasicDBObject("$first", "$hospitalId"))
						.append("doctorId", new BasicDBObject("$first", "$doctorId"))
						.append("discarded", new BasicDBObject("$first", "$discarded"))
						.append("visitedTime", new BasicDBObject("$first", "$visitedTime"))
						.append("visitedFor", new BasicDBObject("$first", "$visitedFor"))
						.append("clinicalNotes", new BasicDBObject("$push", "$clinicalNotes"))
						.append("clinicalNotesDiagrams", new BasicDBObject("$first", "$clinicalNotesDiagrams"))
						.append("recordId", new BasicDBObject("$first", "$recordId"))
						.append("eyePrescriptionId", new BasicDBObject("$first", "$eyePrescriptionId"))
						.append("fromDate", new BasicDBObject("$first", "$fromDate"))
						.append("appointmentRequest", new BasicDBObject("$first", "$appointmentRequest"))
						.append("createdTime", new BasicDBObject("$first", "$createdTime"))
						.append("adminCreatedTime", new BasicDBObject("$first", "$adminCreatedTime"))
						.append("updatedTime", new BasicDBObject("$first", "$updatedTime"))
						.append("createdBy", new BasicDBObject("$first", "$createdBy"))));
	}
}
