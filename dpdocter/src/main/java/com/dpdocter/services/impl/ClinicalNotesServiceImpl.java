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

import javax.mail.MessagingException;

import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.Appointment;
import com.dpdocter.beans.ClinicalNotes;
import com.dpdocter.beans.Complaint;
import com.dpdocter.beans.Diagnoses;
import com.dpdocter.beans.Diagram;
import com.dpdocter.beans.GeneralExam;
import com.dpdocter.beans.IndicationOfUSG;
import com.dpdocter.beans.Investigation;
import com.dpdocter.beans.MailAttachment;
import com.dpdocter.beans.MenstrualHistory;
import com.dpdocter.beans.Notes;
import com.dpdocter.beans.Observation;
import com.dpdocter.beans.ObstetricHistory;
import com.dpdocter.beans.PA;
import com.dpdocter.beans.PV;
import com.dpdocter.beans.PresentComplaint;
import com.dpdocter.beans.PresentComplaintHistory;
import com.dpdocter.beans.ProvisionalDiagnosis;
import com.dpdocter.beans.SystemExam;
import com.dpdocter.collections.ClinicalNotesCollection;
import com.dpdocter.collections.ComplaintCollection;
import com.dpdocter.collections.DiagnosisCollection;
import com.dpdocter.collections.DiagramsCollection;
import com.dpdocter.collections.DoctorCollection;
import com.dpdocter.collections.EmailTrackCollection;
import com.dpdocter.collections.GeneralExamCollection;
import com.dpdocter.collections.HistoryCollection;
import com.dpdocter.collections.IndicationOfUSGCollection;
import com.dpdocter.collections.InvestigationCollection;
import com.dpdocter.collections.LocationCollection;
import com.dpdocter.collections.MenstrualHistoryCollection;
import com.dpdocter.collections.NotesCollection;
import com.dpdocter.collections.ObservationCollection;
import com.dpdocter.collections.ObstetricHistoryCollection;
import com.dpdocter.collections.PACollection;
import com.dpdocter.collections.PVCollection;
import com.dpdocter.collections.PatientCollection;
import com.dpdocter.collections.PatientVisitCollection;
import com.dpdocter.collections.PresentComplaintCollection;
import com.dpdocter.collections.PresentComplaintHistoryCollection;
import com.dpdocter.collections.PrintSettingsCollection;
import com.dpdocter.collections.ProvisionalDiagnosisCollection;
import com.dpdocter.collections.SystemExamCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.elasticsearch.document.ESComplaintsDocument;
import com.dpdocter.elasticsearch.document.ESGeneralExamDocument;
import com.dpdocter.elasticsearch.document.ESInvestigationsDocument;
import com.dpdocter.elasticsearch.document.ESMenstrualHistoryDocument;
import com.dpdocter.elasticsearch.document.ESNotesDocument;
import com.dpdocter.elasticsearch.document.ESObservationsDocument;
import com.dpdocter.elasticsearch.document.ESObstetricHistoryDocument;
import com.dpdocter.elasticsearch.document.ESPresentComplaintDocument;
import com.dpdocter.elasticsearch.document.ESPresentComplaintHistoryDocument;
import com.dpdocter.elasticsearch.document.ESProvisionalDiagnosisDocument;
import com.dpdocter.elasticsearch.document.ESSystemExamDocument;
import com.dpdocter.elasticsearch.services.ESClinicalNotesService;
import com.dpdocter.enums.ClinicalItems;
import com.dpdocter.enums.ComponentType;
import com.dpdocter.enums.Range;
import com.dpdocter.enums.Resource;
import com.dpdocter.enums.UniqueIdInitial;
import com.dpdocter.enums.VitalSignsUnit;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.ClinicalNotesRepository;
import com.dpdocter.repository.ComplaintRepository;
import com.dpdocter.repository.DiagnosisRepository;
import com.dpdocter.repository.DiagramsRepository;
import com.dpdocter.repository.DoctorRepository;
import com.dpdocter.repository.GeneralExamRepository;
import com.dpdocter.repository.HistoryRepository;
import com.dpdocter.repository.IndicationOfUSGRepository;
import com.dpdocter.repository.InvestigationRepository;
import com.dpdocter.repository.LocationRepository;
import com.dpdocter.repository.MenstrualHistoryRepository;
import com.dpdocter.repository.NotesRepository;
import com.dpdocter.repository.ObservationRepository;
import com.dpdocter.repository.ObstetricHistoryRepository;
import com.dpdocter.repository.PARepository;
import com.dpdocter.repository.PVRepository;
import com.dpdocter.repository.PatientRepository;
import com.dpdocter.repository.PatientVisitRepository;
import com.dpdocter.repository.PresentComplaintHistoryRepository;
import com.dpdocter.repository.PresentComplaintRepository;
import com.dpdocter.repository.PrintSettingsRepository;
import com.dpdocter.repository.ProvisionalDiagnosisRepository;
import com.dpdocter.repository.SpecialityRepository;
import com.dpdocter.repository.SystemExamRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.request.AppointmentRequest;
import com.dpdocter.request.ClinicalNotesAddRequest;
import com.dpdocter.request.ClinicalNotesEditRequest;
import com.dpdocter.response.ImageURLResponse;
import com.dpdocter.response.JasperReportResponse;
import com.dpdocter.response.MailResponse;
import com.dpdocter.services.AppointmentService;
import com.dpdocter.services.ClinicalNotesService;
import com.dpdocter.services.EmailTackService;
import com.dpdocter.services.FileManager;
import com.dpdocter.services.JasperReportService;
import com.dpdocter.services.MailBodyGenerator;
import com.dpdocter.services.MailService;
import com.dpdocter.services.PatientVisitService;
import com.dpdocter.services.TransactionalManagementService;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import common.util.web.DPDoctorUtils;

@Service
public class ClinicalNotesServiceImpl implements ClinicalNotesService {

	private static Logger logger = Logger.getLogger(ClinicalNotesServiceImpl.class.getName());

	@Autowired
	private ClinicalNotesRepository clinicalNotesRepository;

	@Autowired
	private ComplaintRepository complaintRepository;

	@Autowired
	private ObservationRepository observationRepository;

	@Autowired
	private ProvisionalDiagnosisRepository provisionalDiagnosisRepository;

	@Autowired
	private GeneralExamRepository generalExamRepository;

	@Autowired
	private SystemExamRepository systemExamRepository;

	@Autowired
	private MenstrualHistoryRepository menstrualHistoryRepository;

	@Autowired
	private InvestigationRepository investigationRepository;

	@Autowired
	private DiagnosisRepository diagnosisRepository;
	
	@Autowired
	private PresentComplaintRepository presentComplaintRepository;
	
	@Autowired
	private PresentComplaintHistoryRepository presentComplaintHistoryRepository;
	
	@Autowired
	private ObstetricHistoryRepository obstetricHistoryRepository;
	
	@Autowired
	private IndicationOfUSGRepository indicationOfUSGRepository;
	
	@Autowired
	private PARepository paRepository;
	
	@Autowired
	private PVRepository pvRepository ;

	@Autowired
	private NotesRepository notesRepository;

	@Autowired
	private DiagramsRepository diagramsRepository;

	@Autowired
	private FileManager fileManager;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private DoctorRepository doctorRepository;

	@Autowired
	private SpecialityRepository specialityRepository;

	@Autowired
	private JasperReportService jasperReportService;

	@Autowired
	private MailService mailService;

	@Autowired
	private PrintSettingsRepository printSettingsRepository;

	@Autowired
	private LocationRepository locationRepository;

	@Autowired
	private EmailTackService emailTackService;
	
	@Autowired
	private PatientRepository patientRepository;

	@Autowired
	private PatientVisitRepository patientVisitRepository;

	@Autowired
	private MailBodyGenerator mailBodyGenerator;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private AppointmentService appointmentService;
	
	@Autowired
	private ESClinicalNotesService esClinicalNotesService;
	
	@Autowired
	private TransactionalManagementService transactionalManagementService;

	@Autowired
	private HistoryRepository historyRepository;

	@Autowired
	private PatientVisitService patientVisitService;

	@Value(value = "${image.path}")
	private String imagePath;

	@Value(value = "${ClinicalNotes.getPatientsClinicalNotesWithVerifiedOTP}")
	private String getPatientsClinicalNotesWithVerifiedOTP;

	@Value(value = "${jasper.print.clinicalnotes.a4.fileName}")
	private String clinicalNotesA4FileName;

	@Value(value = "${jasper.print.clinicalnotes.a5.fileName}")
	private String clinicalNotesA5FileName;

	@Override
	@Transactional
	public ClinicalNotes addNotes(ClinicalNotesAddRequest request, Boolean isAppointmentAdd) {
		ClinicalNotes clinicalNotes = null;
		List<ObjectId> diagnosisIds = null;
		List<ObjectId> diagramIds = null;
		Appointment appointment = null;
		Date createdTime = new Date();
		try {
			if (isAppointmentAdd) {
				if (request.getAppointmentRequest() != null) {
					appointment = addNotesAppointment(request.getAppointmentRequest());
				}
			}
			String createdBy = null;
			ClinicalNotesCollection clinicalNotesCollection = new ClinicalNotesCollection();
			if (appointment != null) {
				request.setAppointmentId(appointment.getAppointmentId());
				request.setTime(appointment.getTime());
				request.setFromDate(appointment.getFromDate());
			}
			BeanUtil.map(request, clinicalNotesCollection);
			UserCollection userCollection = userRepository.findOne(clinicalNotesCollection.getDoctorId());
			if (userCollection != null) {
				createdBy = (userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
						+ userCollection.getFirstName();
				clinicalNotesCollection.setCreatedBy(createdBy);
			}
			
			/*if(request.getPresentComplaint() != null || !request.getPresentComplaint().isEmpty())
			{
				ArrayList<String> presentComplaints = new ArrayList<String>(Arrays.asList(request.getPresentComplaint().split(",")));
				for( String presentComplaint : presentComplaints)
				{
					PresentComplaint complaint = new PresentComplaint(presentComplaint, request.getDoctorId(), request.getLocationId(), request.getHospitalId(), false, null);
				}
			}
			*/
			//complaintIds = new ArrayList<ObjectId>();
			if (request.getComplaints() != null && !request.getComplaints().isEmpty()) {
				for (Complaint complaint : request.getComplaints()) {
					if (DPDoctorUtils.anyStringEmpty(complaint.getId())) {
						ComplaintCollection complaintCollection = new ComplaintCollection();
						BeanUtil.map(complaint, complaintCollection);
						BeanUtil.map(request, complaintCollection);
						complaintCollection.setCreatedBy(createdBy);
						complaintCollection.setCreatedTime(createdTime);
						complaintCollection.setId(null);
						complaintCollection = complaintRepository.save(complaintCollection);

						transactionalManagementService.addResource(complaintCollection.getId(), Resource.COMPLAINT,
								false);
						ESComplaintsDocument esComplaints = new ESComplaintsDocument();
						BeanUtil.map(complaintCollection, esComplaints);
						esClinicalNotesService.addComplaints(esComplaints);

						//complaintIds.add(complaintCollection.getId());
					} else {
						//complaintIds.add(new ObjectId(complaint.getId()));
					}
				}
			}

			//observationIds = new ArrayList<ObjectId>();
			if (request.getObservations() != null && !request.getObservations().isEmpty()) {
				for (Observation observation : request.getObservations()) {
					if (DPDoctorUtils.anyStringEmpty(observation.getId())) {
						ObservationCollection observationCollection = new ObservationCollection();
						BeanUtil.map(observation, observationCollection);
						BeanUtil.map(request, observationCollection);
						observationCollection.setCreatedBy(createdBy);
						observationCollection.setCreatedTime(createdTime);
						observationCollection.setId(null);
						observationCollection = observationRepository.save(observationCollection);

						transactionalManagementService.addResource(observationCollection.getId(), Resource.OBSERVATION,
								false);
						ESObservationsDocument esObservations = new ESObservationsDocument();
						BeanUtil.map(observationCollection, esObservations);
						esClinicalNotesService.addObservations(esObservations);

					//	observationIds.add(observationCollection.getId());
					} else {
					//	observationIds.add(new ObjectId(observation.getId()));
					}
				}
			}

			//investigationIds = new ArrayList<ObjectId>();
			if (request.getInvestigations() != null && !request.getInvestigations().isEmpty()) {
				for (Investigation investigation : request.getInvestigations()) {
					if (DPDoctorUtils.anyStringEmpty(investigation.getId())) {
						InvestigationCollection investigationCollection = new InvestigationCollection();
						BeanUtil.map(investigation, investigationCollection);
						BeanUtil.map(request, investigationCollection);
						investigationCollection.setCreatedBy(createdBy);
						investigationCollection.setCreatedTime(createdTime);
						investigationCollection.setId(null);
						investigationCollection = investigationRepository.save(investigationCollection);

						transactionalManagementService.addResource(investigationCollection.getId(),
								Resource.INVESTIGATION, false);
						ESInvestigationsDocument esInvestigations = new ESInvestigationsDocument();
						BeanUtil.map(investigationCollection, esInvestigations);
						esClinicalNotesService.addInvestigations(esInvestigations);

						//investigationIds.add(investigationCollection.getId());
					} else {
						//investigationIds.add(new ObjectId(investigation.getId()));
					}
				}
			}

		//	noteIds = new ArrayList<ObjectId>();
			if (request.getNotes() != null && !request.getNotes().isEmpty()) {
				for (Notes note : request.getNotes()) {
					if (DPDoctorUtils.anyStringEmpty(note.getId())) {
						NotesCollection notesCollection = new NotesCollection();
						BeanUtil.map(note, notesCollection);
						BeanUtil.map(request, notesCollection);
						notesCollection.setCreatedBy(createdBy);
						notesCollection.setCreatedTime(createdTime);
						notesCollection.setId(null);
						notesCollection = notesRepository.save(notesCollection);
						transactionalManagementService.addResource(notesCollection.getId(), Resource.NOTES, false);
						ESNotesDocument esNotes = new ESNotesDocument();
						BeanUtil.map(notesCollection, esNotes);
						esClinicalNotesService.addNotes(esNotes);
						//noteIds.add(notesCollection.getId());
					} else {
						//noteIds.add(new ObjectId(note.getId()));
					}
			}
			}
			if (request.getProvisionalDiagnoses() != null && !request.getProvisionalDiagnoses().isEmpty()) {
				for (ProvisionalDiagnosis provisionalDiagnosis : request.getProvisionalDiagnoses()) {
					if (DPDoctorUtils.anyStringEmpty(provisionalDiagnosis.getId())) {
						ProvisionalDiagnosisCollection provisionalDiagnosisCollection = new ProvisionalDiagnosisCollection();
						BeanUtil.map(provisionalDiagnosis, provisionalDiagnosisCollection);
						BeanUtil.map(request, provisionalDiagnosisCollection);
						provisionalDiagnosisCollection.setCreatedBy(createdBy);
						provisionalDiagnosisCollection.setCreatedTime(createdTime);
						provisionalDiagnosisCollection.setId(null);
						provisionalDiagnosisCollection = provisionalDiagnosisRepository.save(provisionalDiagnosisCollection);
						transactionalManagementService.addResource(provisionalDiagnosisCollection.getId(), Resource.PROVISIONAL_DIAGNOSIS, false);
						ESProvisionalDiagnosisDocument esProvisionalDiagnosis = new ESProvisionalDiagnosisDocument();
						BeanUtil.map(provisionalDiagnosisCollection, esProvisionalDiagnosis);
						esClinicalNotesService.addProvisionalDiagnosis(esProvisionalDiagnosis);
						//noteIds.add(notesCollection.getId());
					} else {
						//noteIds.add(new ObjectId(note.getId()));
					}
				}
			}
			
			if (request.getPresentComplaints() != null && !request.getPresentComplaints().isEmpty()) {
				for (PresentComplaint presentComplaint : request.getPresentComplaints()) {
					if (DPDoctorUtils.anyStringEmpty(presentComplaint.getId())) {
						PresentComplaintCollection presentComplaintCollection = new PresentComplaintCollection();
						BeanUtil.map(presentComplaint, presentComplaintCollection);
						BeanUtil.map(request, presentComplaintCollection);
						presentComplaintCollection.setCreatedBy(createdBy);
						presentComplaintCollection.setCreatedTime(createdTime);
						presentComplaintCollection.setId(null);
						presentComplaintCollection = presentComplaintRepository.save(presentComplaintCollection);
						transactionalManagementService.addResource(presentComplaintCollection.getId(), Resource.PRESENT_COMPLAINT, false);
						ESPresentComplaintDocument esPresentComplaint = new ESPresentComplaintDocument();
						BeanUtil.map(presentComplaintCollection, esPresentComplaint);
						esClinicalNotesService.addPresentComplaint(esPresentComplaint);
						//noteIds.add(notesCollection.getId());
					} else {
						//noteIds.add(new ObjectId(note.getId()));
					}
				}
			}
			
			if (request.getPresentComplaintHistories() != null && !request.getPresentComplaintHistories().isEmpty()) {
				for (PresentComplaintHistory presentComplaintHistory : request.getPresentComplaintHistories()) {
					if (DPDoctorUtils.anyStringEmpty(presentComplaintHistory.getId())) {
						PresentComplaintHistoryCollection presentComplaintHistoryCollection = new PresentComplaintHistoryCollection();
						BeanUtil.map(presentComplaintHistory, presentComplaintHistoryCollection);
						BeanUtil.map(request, presentComplaintHistoryCollection);
						presentComplaintHistoryCollection.setCreatedBy(createdBy);
						presentComplaintHistoryCollection.setCreatedTime(createdTime);
						presentComplaintHistoryCollection.setId(null);
						presentComplaintHistoryCollection = presentComplaintHistoryRepository.save(presentComplaintHistoryCollection);
						transactionalManagementService.addResource(presentComplaintHistoryCollection.getId(), Resource.HISTORY_OF_PRESENT_COMPLAINT, false);
						ESPresentComplaintHistoryDocument esPresentComplaintHistory = new ESPresentComplaintHistoryDocument();
						BeanUtil.map(presentComplaintHistoryCollection, esPresentComplaintHistory);
						esClinicalNotesService.addPresentComplaintHistory(esPresentComplaintHistory);
						//noteIds.add(notesCollection.getId());
					} else {
						//noteIds.add(new ObjectId(note.getId()));
					}
				}
			}
			
			if (request.getGeneralExams() != null && !request.getGeneralExams().isEmpty()) {
				for (GeneralExam generalExam : request.getGeneralExams()) {
					if (DPDoctorUtils.anyStringEmpty(generalExam.getId())) {
						GeneralExamCollection generalExamCollection = new GeneralExamCollection();
						BeanUtil.map(generalExam, generalExamCollection);
						BeanUtil.map(request, generalExamCollection);
						generalExamCollection.setCreatedBy(createdBy);
						generalExamCollection.setCreatedTime(createdTime);
						generalExamCollection.setId(null);
						generalExamCollection = generalExamRepository.save(generalExamCollection);
						transactionalManagementService.addResource(generalExamCollection.getId(), Resource.GENERAL_EXAMINATION, false);
						ESGeneralExamDocument esGeneralExam = new ESGeneralExamDocument();
						BeanUtil.map(generalExamCollection, esGeneralExam);
						esClinicalNotesService.addGeneralExam(esGeneralExam);
						//noteIds.add(notesCollection.getId());
					} else {
						//noteIds.add(new ObjectId(note.getId()));
					}
				}
			}
			
			if (request.getSystemExams() != null && !request.getSystemExams().isEmpty()) {
				for (SystemExam systemExam : request.getSystemExams()) {
					if (DPDoctorUtils.anyStringEmpty(systemExam.getId())) {
						SystemExamCollection systemExamCollection = new SystemExamCollection();
						BeanUtil.map(systemExam, systemExamCollection);
						BeanUtil.map(request, systemExamCollection);
						systemExamCollection.setCreatedBy(createdBy);
						systemExamCollection.setCreatedTime(createdTime);
						systemExamCollection.setId(null);
						systemExamCollection = systemExamRepository.save(systemExamCollection);
						transactionalManagementService.addResource(systemExamCollection.getId(), Resource.SYSTEMATIC_EXAMINATION, false);
						ESSystemExamDocument esSystemExam = new ESSystemExamDocument();
						BeanUtil.map(systemExamCollection, esSystemExam);
						esClinicalNotesService.addSystemExam(esSystemExam);
						//noteIds.add(notesCollection.getId());
					} else {
						//noteIds.add(new ObjectId(note.getId()));
					}
				}
			}
			
			if (request.getMenstrualHistories() != null && !request.getMenstrualHistories().isEmpty()) {
				for (MenstrualHistory menstrualHistory : request.getMenstrualHistories()) {
					if (DPDoctorUtils.anyStringEmpty(menstrualHistory.getId())) {
						MenstrualHistoryCollection menstrualHistoryCollection = new MenstrualHistoryCollection();
						BeanUtil.map(menstrualHistory, menstrualHistoryCollection);
						BeanUtil.map(request, menstrualHistoryCollection);
						menstrualHistoryCollection.setCreatedBy(createdBy);
						menstrualHistoryCollection.setCreatedTime(createdTime);
						menstrualHistoryCollection.setId(null);
						menstrualHistoryCollection = menstrualHistoryRepository.save(menstrualHistoryCollection);
						transactionalManagementService.addResource(menstrualHistoryCollection.getId(), Resource.MENSTRUAL_HISTORY, false);
						ESMenstrualHistoryDocument esMenstrualHistory = new ESMenstrualHistoryDocument();
						BeanUtil.map(menstrualHistoryCollection, esMenstrualHistory);
						esClinicalNotesService.addMenstrualHistory(esMenstrualHistory);
						//noteIds.add(notesCollection.getId());
					} else {
						//noteIds.add(new ObjectId(note.getId()));
					}
				}
			}
			
			if (request.getObstetricHistories() != null && !request.getObstetricHistories().isEmpty()) {
				for (ObstetricHistory obstetricHistory : request.getObstetricHistories()) {
					if (DPDoctorUtils.anyStringEmpty(obstetricHistory.getId())) {
						ObstetricHistoryCollection obstetricHistoryCollection = new ObstetricHistoryCollection();
						BeanUtil.map(obstetricHistory, obstetricHistoryCollection);
						BeanUtil.map(request, obstetricHistoryCollection);
						obstetricHistoryCollection.setCreatedBy(createdBy);
						obstetricHistoryCollection.setCreatedTime(createdTime);
						obstetricHistoryCollection.setId(null);
						obstetricHistoryCollection = obstetricHistoryRepository.save(obstetricHistoryCollection);
						transactionalManagementService.addResource(obstetricHistoryCollection.getId(), Resource.OBSTETRIC_HISTORY, false);
						ESObstetricHistoryDocument esObstetricHistory = new ESObstetricHistoryDocument();
						BeanUtil.map(obstetricHistoryCollection, esObstetricHistory);
						esClinicalNotesService.addObstetricsHistory(esObstetricHistory);
						//noteIds.add(notesCollection.getId());
					} else {
						//noteIds.add(new ObjectId(note.getId()));
					}
				}
			}
			
			diagnosisIds = new ArrayList<ObjectId>();
			if (request.getDiagnoses() != null && !request.getDiagnoses().isEmpty()) {
				for (Diagnoses diagnosis : request.getDiagnoses()) {
					if (!DPDoctorUtils.anyStringEmpty(diagnosis.getId())) {
						diagnosisIds.add(new ObjectId(diagnosis.getId()));
					}
				}
			}
			//
			// clinicalNotesCollection.setComplaints(complaintIds);
			// clinicalNotesCollection.setInvestigations(investigationIds);
			// clinicalNotesCollection.setObservations(observationIds);
			clinicalNotesCollection.setDiagnoses(diagnosisIds);
			// clinicalNotesCollection.setNotes(noteIds);
			if (request.getDiagrams() == null) {
				clinicalNotesCollection.setDiagrams(null);
			} else {
				diagramIds = new ArrayList<ObjectId>();
				for (String diagramId : request.getDiagrams()) {
					diagramIds.add(new ObjectId(diagramId));
				}
			}
			clinicalNotesCollection
					.setUniqueEmrId(UniqueIdInitial.CLINICALNOTES.getInitial() + DPDoctorUtils.generateRandomId());
			clinicalNotesCollection.setCreatedTime(createdTime);
			clinicalNotesCollection = clinicalNotesRepository.save(clinicalNotesCollection);

			clinicalNotes = new ClinicalNotes();
			BeanUtil.map(clinicalNotesCollection, clinicalNotes);

			// if(complaintIds != null &&
			// !complaintIds.isEmpty())clinicalNotes.setComplaints(sortComplaints(mongoTemplate.aggregate(Aggregation.newAggregation(Aggregation.match(new
			// Criteria("id").in(complaintIds))), ComplaintCollection.class,
			// Complaint.class).getMappedResults(), complaintIds));
			// if(investigationIds != null &&
			// !investigationIds.isEmpty())clinicalNotes.setInvestigations(sortInvestigations(mongoTemplate.aggregate(Aggregation.newAggregation(Aggregation.match(new
			// Criteria("id").in(investigationIds))),
			// InvestigationCollection.class,
			// Investigation.class).getMappedResults(),investigationIds));
			// if(observationIds != null &&
			// !observationIds.isEmpty())clinicalNotes.setObservations(sortObservations(mongoTemplate.aggregate(Aggregation.newAggregation(Aggregation.match(new
			// Criteria("id").in(observationIds))), ObservationCollection.class,
			// Observation.class).getMappedResults(), observationIds));
			// if(diagnosisIds != null &&
			// !diagnosisIds.isEmpty())clinicalNotes.setDiagnoses(sortDiagnoses(mongoTemplate.aggregate(Aggregation.newAggregation(Aggregation.match(new
			// Criteria("id").in(diagnosisIds))), DiagnosisCollection.class,
			// Diagnoses.class).getMappedResults(), diagnosisIds));
			// if(noteIds != null &&
			// !noteIds.isEmpty())clinicalNotes.setNotes(sortNotes(mongoTemplate.aggregate(Aggregation.newAggregation(Aggregation.match(new
			// Criteria("id").in(noteIds))), NotesCollection.class,
			// Notes.class).getMappedResults(), noteIds));
			if (diagramIds != null && !diagramIds.isEmpty())
				clinicalNotes
						.setDiagrams(
								sortDiagrams(
										mongoTemplate.aggregate(
												Aggregation.newAggregation(
														Aggregation.match(new Criteria("id").in(diagramIds))),
												DiagramsCollection.class, Diagram.class).getMappedResults(),
										diagramIds));
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			try {
				mailService.sendExceptionMail("Backend Business Exception :: While adding Clinical notes", e.getMessage());
			} catch (MessagingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return clinicalNotes;
	}

	@Override
	@Transactional
	public ClinicalNotes getNotesById(String id, ObjectId visitId) {
		ClinicalNotes clinicalNote = null;
		try {
			ClinicalNotesCollection clinicalNotesCollection = clinicalNotesRepository.findOne(new ObjectId(id));
			if (clinicalNotesCollection != null) {
				clinicalNote = new ClinicalNotes();
				BeanUtil.map(clinicalNotesCollection, clinicalNote);

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
					clinicalNote
							.setDiagrams(
									sortDiagrams(
											mongoTemplate.aggregate(
													Aggregation.newAggregation(Aggregation.match(new Criteria("id")
															.in(clinicalNotesCollection.getDiagrams()))),
													DiagramsCollection.class, Diagram.class).getMappedResults(),
											clinicalNotesCollection.getDiagrams()));

				if(DPDoctorUtils.anyStringEmpty(visitId)){
					PatientVisitCollection patientVisitCollection = patientVisitRepository
							.findByClinialNotesId(clinicalNotesCollection.getId());
					if (patientVisitCollection != null)
						clinicalNote.setVisitId(patientVisitCollection.getId().toString());
				}else{
					clinicalNote.setVisitId(visitId.toString());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			
			try {
				mailService.sendExceptionMail("Backend Business Exception :: While getting notes for id:"+id, e.getMessage());
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
	public ClinicalNotes editNotes(ClinicalNotesEditRequest request) {
		ClinicalNotes clinicalNotes = null;
		 List<ObjectId> diagnosisIds = null;
		List<ObjectId> diagramIds = null;
		Appointment appointment = null;
		Date createdTime = new Date();

		try {
			// save clinical notes.
			if (request.getAppointmentRequest() != null) {
				appointment = addNotesAppointment(request.getAppointmentRequest());
			}

			ClinicalNotesCollection clinicalNotesCollection = new ClinicalNotesCollection();
			if (appointment != null) {
				request.setAppointmentId(appointment.getAppointmentId());
				request.setTime(appointment.getTime());
				request.setFromDate(appointment.getFromDate());
			}
			BeanUtil.map(request, clinicalNotesCollection);
			ClinicalNotesCollection oldClinicalNotesCollection = clinicalNotesRepository.findOne(clinicalNotesCollection.getId());
			String createdBy = oldClinicalNotesCollection.getCreatedBy();
			if (request.getComplaints() != null && !request.getComplaints().isEmpty()) {
				for (Complaint complaint : request.getComplaints()) {
					if (DPDoctorUtils.anyStringEmpty(complaint.getId())) {
						ComplaintCollection complaintCollection = new ComplaintCollection();
						BeanUtil.map(complaint, complaintCollection);
						BeanUtil.map(request, complaintCollection);
						complaintCollection.setCreatedBy(createdBy);
						complaintCollection.setCreatedTime(createdTime);
						complaintCollection.setId(null);
						complaintCollection = complaintRepository.save(complaintCollection);

						transactionalManagementService.addResource(complaintCollection.getId(), Resource.COMPLAINT,
								false);
						ESComplaintsDocument esComplaints = new ESComplaintsDocument();
						BeanUtil.map(complaintCollection, esComplaints);
						esClinicalNotesService.addComplaints(esComplaints);

						//complaintIds.add(complaintCollection.getId());
					} else {
						//complaintIds.add(new ObjectId(complaint.getId()));
					}
				}
			}

			//observationIds = new ArrayList<ObjectId>();
			if (request.getObservations() != null && !request.getObservations().isEmpty()) {
				for (Observation observation : request.getObservations()) {
					if (DPDoctorUtils.anyStringEmpty(observation.getId())) {
						ObservationCollection observationCollection = new ObservationCollection();
						BeanUtil.map(observation, observationCollection);
						BeanUtil.map(request, observationCollection);
						observationCollection.setCreatedBy(createdBy);
						observationCollection.setCreatedTime(createdTime);
						observationCollection.setId(null);
						observationCollection = observationRepository.save(observationCollection);

						transactionalManagementService.addResource(observationCollection.getId(), Resource.OBSERVATION,
								false);
						ESObservationsDocument esObservations = new ESObservationsDocument();
						BeanUtil.map(observationCollection, esObservations);
						esClinicalNotesService.addObservations(esObservations);

					//	observationIds.add(observationCollection.getId());
					} else {
					//	observationIds.add(new ObjectId(observation.getId()));
					}
				}
			}

			//investigationIds = new ArrayList<ObjectId>();
			if (request.getInvestigations() != null && !request.getInvestigations().isEmpty()) {
				for (Investigation investigation : request.getInvestigations()) {
					if (DPDoctorUtils.anyStringEmpty(investigation.getId())) {
						InvestigationCollection investigationCollection = new InvestigationCollection();
						BeanUtil.map(investigation, investigationCollection);
						BeanUtil.map(request, investigationCollection);
						investigationCollection.setCreatedBy(createdBy);
						investigationCollection.setCreatedTime(createdTime);
						investigationCollection.setId(null);
						investigationCollection = investigationRepository.save(investigationCollection);

						transactionalManagementService.addResource(investigationCollection.getId(),
								Resource.INVESTIGATION, false);
						ESInvestigationsDocument esInvestigations = new ESInvestigationsDocument();
						BeanUtil.map(investigationCollection, esInvestigations);
						esClinicalNotesService.addInvestigations(esInvestigations);

						//investigationIds.add(investigationCollection.getId());
					} else {
						//investigationIds.add(new ObjectId(investigation.getId()));
					}
				}
			}

		//	noteIds = new ArrayList<ObjectId>();
			if (request.getNotes() != null && !request.getNotes().isEmpty()) {
				for (Notes note : request.getNotes()) {
					if (DPDoctorUtils.anyStringEmpty(note.getId())) {
						NotesCollection notesCollection = new NotesCollection();
						BeanUtil.map(note, notesCollection);
						BeanUtil.map(request, notesCollection);
						notesCollection.setCreatedBy(createdBy);
						notesCollection.setCreatedTime(createdTime);
						notesCollection.setId(null);
						notesCollection = notesRepository.save(notesCollection);
						transactionalManagementService.addResource(notesCollection.getId(), Resource.NOTES, false);
						ESNotesDocument esNotes = new ESNotesDocument();
						BeanUtil.map(notesCollection, esNotes);
						esClinicalNotesService.addNotes(esNotes);
						//noteIds.add(notesCollection.getId());
					} else {
						//noteIds.add(new ObjectId(note.getId()));
					}
			}
			}
			if (request.getProvisionalDiagnoses() != null && !request.getProvisionalDiagnoses().isEmpty()) {
				for (ProvisionalDiagnosis provisionalDiagnosis : request.getProvisionalDiagnoses()) {
					if (DPDoctorUtils.anyStringEmpty(provisionalDiagnosis.getId())) {
						ProvisionalDiagnosisCollection provisionalDiagnosisCollection = new ProvisionalDiagnosisCollection();
						BeanUtil.map(provisionalDiagnosis, provisionalDiagnosisCollection);
						BeanUtil.map(request, provisionalDiagnosisCollection);
						provisionalDiagnosisCollection.setCreatedBy(createdBy);
						provisionalDiagnosisCollection.setCreatedTime(createdTime);
						provisionalDiagnosisCollection.setId(null);
						provisionalDiagnosisCollection = provisionalDiagnosisRepository.save(provisionalDiagnosisCollection);
						transactionalManagementService.addResource(provisionalDiagnosisCollection.getId(), Resource.PROVISIONAL_DIAGNOSIS, false);
						ESProvisionalDiagnosisDocument esProvisionalDiagnosis = new ESProvisionalDiagnosisDocument();
						BeanUtil.map(provisionalDiagnosisCollection, esProvisionalDiagnosis);
						esClinicalNotesService.addProvisionalDiagnosis(esProvisionalDiagnosis);
						//noteIds.add(notesCollection.getId());
					} else {
						//noteIds.add(new ObjectId(note.getId()));
					}
				}
			}
			
			if (request.getPresentComplaints() != null && !request.getPresentComplaints().isEmpty()) {
				for (PresentComplaint presentComplaint : request.getPresentComplaints()) {
					if (DPDoctorUtils.anyStringEmpty(presentComplaint.getId())) {
						PresentComplaintCollection presentComplaintCollection = new PresentComplaintCollection();
						BeanUtil.map(presentComplaint, presentComplaintCollection);
						BeanUtil.map(request, presentComplaintCollection);
						presentComplaintCollection.setCreatedBy(createdBy);
						presentComplaintCollection.setCreatedTime(createdTime);
						presentComplaintCollection.setId(null);
						presentComplaintCollection = presentComplaintRepository.save(presentComplaintCollection);
						transactionalManagementService.addResource(presentComplaintCollection.getId(), Resource.PRESENT_COMPLAINT, false);
						ESPresentComplaintDocument esPresentComplaint = new ESPresentComplaintDocument();
						BeanUtil.map(presentComplaintCollection, esPresentComplaint);
						esClinicalNotesService.addPresentComplaint(esPresentComplaint);
						//noteIds.add(notesCollection.getId());
					} else {
						//noteIds.add(new ObjectId(note.getId()));
					}
				}
			}
			
			if (request.getPresentComplaintHistories() != null && !request.getPresentComplaintHistories().isEmpty()) {
				for (PresentComplaintHistory presentComplaintHistory : request.getPresentComplaintHistories()) {
					if (DPDoctorUtils.anyStringEmpty(presentComplaintHistory.getId())) {
						PresentComplaintHistoryCollection presentComplaintHistoryCollection = new PresentComplaintHistoryCollection();
						BeanUtil.map(presentComplaintHistory, presentComplaintHistoryCollection);
						BeanUtil.map(request, presentComplaintHistoryCollection);
						presentComplaintHistoryCollection.setCreatedBy(createdBy);
						presentComplaintHistoryCollection.setCreatedTime(createdTime);
						presentComplaintHistoryCollection.setId(null);
						presentComplaintHistoryCollection = presentComplaintHistoryRepository.save(presentComplaintHistoryCollection);
						transactionalManagementService.addResource(presentComplaintHistoryCollection.getId(), Resource.HISTORY_OF_PRESENT_COMPLAINT, false);
						ESPresentComplaintHistoryDocument esPresentComplaintHistory = new ESPresentComplaintHistoryDocument();
						BeanUtil.map(presentComplaintHistoryCollection, esPresentComplaintHistory);
						esClinicalNotesService.addPresentComplaintHistory(esPresentComplaintHistory);
						//noteIds.add(notesCollection.getId());
					} else {
						//noteIds.add(new ObjectId(note.getId()));
					}
				}
			}
			
			if (request.getGeneralExams() != null && !request.getGeneralExams().isEmpty()) {
				for (GeneralExam generalExam : request.getGeneralExams()) {
					if (DPDoctorUtils.anyStringEmpty(generalExam.getId())) {
						GeneralExamCollection generalExamCollection = new GeneralExamCollection();
						BeanUtil.map(generalExam, generalExamCollection);
						BeanUtil.map(request, generalExamCollection);
						generalExamCollection.setCreatedBy(createdBy);
						generalExamCollection.setCreatedTime(createdTime);
						generalExamCollection.setId(null);
						generalExamCollection = generalExamRepository.save(generalExamCollection);
						transactionalManagementService.addResource(generalExamCollection.getId(), Resource.GENERAL_EXAMINATION, false);
						ESGeneralExamDocument esGeneralExam = new ESGeneralExamDocument();
						BeanUtil.map(generalExamCollection, esGeneralExam);
						esClinicalNotesService.addGeneralExam(esGeneralExam);
						//noteIds.add(notesCollection.getId());
					} else {
						//noteIds.add(new ObjectId(note.getId()));
					}
				}
			}
			
			if (request.getSystemExams() != null && !request.getSystemExams().isEmpty()) {
				for (SystemExam systemExam : request.getSystemExams()) {
					if (DPDoctorUtils.anyStringEmpty(systemExam.getId())) {
						SystemExamCollection systemExamCollection = new SystemExamCollection();
						BeanUtil.map(systemExam, systemExamCollection);
						BeanUtil.map(request, systemExamCollection);
						systemExamCollection.setCreatedBy(createdBy);
						systemExamCollection.setCreatedTime(createdTime);
						systemExamCollection.setId(null);
						systemExamCollection = systemExamRepository.save(systemExamCollection);
						transactionalManagementService.addResource(systemExamCollection.getId(), Resource.SYSTEMATIC_EXAMINATION, false);
						ESSystemExamDocument esSystemExam = new ESSystemExamDocument();
						BeanUtil.map(systemExamCollection, esSystemExam);
						esClinicalNotesService.addSystemExam(esSystemExam);
						//noteIds.add(notesCollection.getId());
					} else {
						//noteIds.add(new ObjectId(note.getId()));
					}
				}
			}
			
			if (request.getMenstrualHistories() != null && !request.getMenstrualHistories().isEmpty()) {
				for (MenstrualHistory menstrualHistory : request.getMenstrualHistories()) {
					if (DPDoctorUtils.anyStringEmpty(menstrualHistory.getId())) {
						MenstrualHistoryCollection menstrualHistoryCollection = new MenstrualHistoryCollection();
						BeanUtil.map(menstrualHistory, menstrualHistoryCollection);
						BeanUtil.map(request, menstrualHistoryCollection);
						menstrualHistoryCollection.setCreatedBy(createdBy);
						menstrualHistoryCollection.setCreatedTime(createdTime);
						menstrualHistoryCollection.setId(null);
						menstrualHistoryCollection = menstrualHistoryRepository.save(menstrualHistoryCollection);
						transactionalManagementService.addResource(menstrualHistoryCollection.getId(), Resource.MENSTRUAL_HISTORY, false);
						ESMenstrualHistoryDocument esMenstrualHistory = new ESMenstrualHistoryDocument();
						BeanUtil.map(menstrualHistoryCollection, esMenstrualHistory);
						esClinicalNotesService.addMenstrualHistory(esMenstrualHistory);
						//noteIds.add(notesCollection.getId());
					} else {
						//noteIds.add(new ObjectId(note.getId()));
					}
				}
			}
			
			if (request.getObstetricHistories() != null && !request.getObstetricHistories().isEmpty()) {
				for (ObstetricHistory obstetricHistory : request.getObstetricHistories()) {
					if (DPDoctorUtils.anyStringEmpty(obstetricHistory.getId())) {
						ObstetricHistoryCollection obstetricHistoryCollection = new ObstetricHistoryCollection();
						BeanUtil.map(obstetricHistory, obstetricHistoryCollection);
						BeanUtil.map(request, obstetricHistoryCollection);
						obstetricHistoryCollection.setCreatedBy(createdBy);
						obstetricHistoryCollection.setCreatedTime(createdTime);
						obstetricHistoryCollection.setId(null);
						obstetricHistoryCollection = obstetricHistoryRepository.save(obstetricHistoryCollection);
						transactionalManagementService.addResource(obstetricHistoryCollection.getId(), Resource.OBSTETRIC_HISTORY, false);
						ESObstetricHistoryDocument esObstetricHistory = new ESObstetricHistoryDocument();
						BeanUtil.map(obstetricHistoryCollection, esObstetricHistory);
						esClinicalNotesService.addObstetricsHistory(esObstetricHistory);
						//noteIds.add(notesCollection.getId());
					} else {
						//noteIds.add(new ObjectId(note.getId()));
					}
				}
			}
			
			diagnosisIds = new ArrayList<ObjectId>();
			if (request.getDiagnoses() != null && !request.getDiagnoses().isEmpty()) {
				for (Diagnoses diagnosis : request.getDiagnoses()) {
					if (!DPDoctorUtils.anyStringEmpty(diagnosis.getId())) {
						diagnosisIds.add(new ObjectId(diagnosis.getId()));
					}
				}
			}
			//
			// clinicalNotesCollection.setComplaints(complaintIds);
			// clinicalNotesCollection.setInvestigations(investigationIds);
			// clinicalNotesCollection.setObservations(observationIds);
			clinicalNotesCollection.setDiagnoses(diagnosisIds);
			if (request.getDiagrams() == null) {
				clinicalNotesCollection.setDiagrams(diagramIds);
			} else {
				diagramIds = new ArrayList<ObjectId>();
				for (String diagramId : request.getDiagrams()) {
					diagramIds.add(new ObjectId(diagramId));
				}
			}

			clinicalNotesCollection.setCreatedTime(oldClinicalNotesCollection.getCreatedTime());
			clinicalNotesCollection.setCreatedBy(oldClinicalNotesCollection.getCreatedBy());
			clinicalNotesCollection.setDiscarded(oldClinicalNotesCollection.getDiscarded());
			clinicalNotesCollection.setInHistory(oldClinicalNotesCollection.isInHistory());
			clinicalNotesCollection.setUpdatedTime(new Date());
			clinicalNotesCollection.setUniqueEmrId(oldClinicalNotesCollection.getUniqueEmrId());
			clinicalNotesCollection = clinicalNotesRepository.save(clinicalNotesCollection);

			clinicalNotes = new ClinicalNotes();
			BeanUtil.map(clinicalNotesCollection, clinicalNotes);

			// if(complaintIds != null &&
			// !complaintIds.isEmpty())clinicalNotes.setComplaints(sortComplaints(mongoTemplate.aggregate(Aggregation.newAggregation(Aggregation.match(new
			// Criteria("id").in(complaintIds))), ComplaintCollection.class,
			// Complaint.class).getMappedResults(), complaintIds));
			// if(investigationIds != null &&
			// !investigationIds.isEmpty())clinicalNotes.setInvestigations(sortInvestigations(mongoTemplate.aggregate(Aggregation.newAggregation(Aggregation.match(new
			// Criteria("id").in(investigationIds))),
			// InvestigationCollection.class,
			// Investigation.class).getMappedResults(),investigationIds));
			// if(observationIds != null &&
			// !observationIds.isEmpty())clinicalNotes.setObservations(sortObservations(mongoTemplate.aggregate(Aggregation.newAggregation(Aggregation.match(new
			// Criteria("id").in(observationIds))), ObservationCollection.class,
			// Observation.class).getMappedResults(), observationIds));
			// if(diagnosisIds != null &&
			// !diagnosisIds.isEmpty())clinicalNotes.setDiagnoses(sortDiagnoses(mongoTemplate.aggregate(Aggregation.newAggregation(Aggregation.match(new
			// Criteria("id").in(diagnosisIds))), DiagnosisCollection.class,
			// Diagnoses.class).getMappedResults(), diagnosisIds));
			// if(noteIds != null &&
			// !noteIds.isEmpty())clinicalNotes.setNotes(sortNotes(mongoTemplate.aggregate(Aggregation.newAggregation(Aggregation.match(new
			// Criteria("id").in(noteIds))), NotesCollection.class,
			// Notes.class).getMappedResults(), noteIds));
			if (diagramIds != null && !diagramIds.isEmpty())
				clinicalNotes
						.setDiagrams(
								sortDiagrams(
										mongoTemplate.aggregate(
												Aggregation.newAggregation(
														Aggregation.match(new Criteria("id").in(diagramIds))),
												DiagramsCollection.class, Diagram.class).getMappedResults(),
										diagramIds));

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			try {
				mailService.sendExceptionMail("Backend Business Exception :: While editing clinical notes", e.getMessage());
			} catch (MessagingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}

		return clinicalNotes;

	}

	@Override
	@Transactional
	public ClinicalNotes deleteNote(String id, Boolean discarded) {
		ClinicalNotes response = null;
		try {
			ClinicalNotesCollection clinicalNotes = clinicalNotesRepository.findOne(new ObjectId(id));
			if (clinicalNotes != null) {
				clinicalNotes.setDiscarded(discarded);
				clinicalNotes.setUpdatedTime(new Date());
				clinicalNotes = clinicalNotesRepository.save(clinicalNotes);
				response = new ClinicalNotes();
				BeanUtil.map(clinicalNotes, response);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			try {
				mailService.sendExceptionMail("Backend Business Exception :: While deleting note for id:"+id, e.getMessage());
			} catch (MessagingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	@Transactional
	public List<ClinicalNotes> getClinicalNotes(int page, int size, String doctorId, String locationId,
			String hospitalId, String patientId, String updatedTime, Boolean isOTPVerified, Boolean discarded,
			Boolean inHistory) {
		List<ClinicalNotesCollection> clinicalNotesCollections = null;
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

			Criteria criteria = new Criteria("updatedTime").gt(new Date(createdTimestamp)).and("patientId").is(patientObjectId);
			if(!discarded)criteria.and("discarded").is(discarded);
			if(inHistory)criteria.and("inHistory").is(inHistory);
			
			if(!isOTPVerified){
				if(!DPDoctorUtils.anyStringEmpty(locationId, hospitalId))criteria.and("locationId").is(locationObjectId).and("hospitalId").is(hospitalObjectId);
				if(!DPDoctorUtils.anyStringEmpty(doctorId))criteria.and("doctorId").is(doctorObjectId);	
			}
			
			Aggregation aggregation = null;
			
			if (size > 0)aggregation = Aggregation.newAggregation(Aggregation.match(criteria), Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")), Aggregation.skip((page) * size), Aggregation.limit(size));
			else aggregation = Aggregation.newAggregation(Aggregation.match(criteria), Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));
			
			AggregationResults<ClinicalNotesCollection> aggregationResults = mongoTemplate.aggregate(aggregation, ClinicalNotesCollection.class, ClinicalNotesCollection.class);
			clinicalNotesCollections = aggregationResults.getMappedResults();
			
			if (clinicalNotesCollections != null && !clinicalNotesCollections.isEmpty()) {
				clinicalNotes = new ArrayList<ClinicalNotes>();
				for (ClinicalNotesCollection clinicalNotesCollection : clinicalNotesCollections) {
					ClinicalNotes clinicalNote = getClinicalNote(clinicalNotesCollection);
					clinicalNotes.add(clinicalNote);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(" Error Occurred While Getting Clinical Notes");
			try {
				mailService.sendExceptionMail("Backend Business Exception :: While getting clinical notes", e.getMessage());
			} catch (MessagingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Clinical Notes");
		}
		return clinicalNotes;
	}

	public ClinicalNotes getClinicalNote(ClinicalNotesCollection clinicalNotesCollection) {
		ClinicalNotes clinicalNote = new ClinicalNotes();
		BeanUtil.map(clinicalNotesCollection, clinicalNote);
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
				.findByClinialNotesId(clinicalNotesCollection.getId());
		if (patientVisitCollection != null)
			clinicalNote.setVisitId(patientVisitCollection.getId().toString());

		return clinicalNote;
	}

	@Override
	@Transactional
	public Complaint addEditComplaint(Complaint complaint) {
		try {
			ComplaintCollection complaintCollection = new ComplaintCollection();
			BeanUtil.map(complaint, complaintCollection);
			if (DPDoctorUtils.anyStringEmpty(complaintCollection.getId())) {
				complaintCollection.setCreatedTime(new Date());
				if (!DPDoctorUtils.anyStringEmpty(complaintCollection.getDoctorId())) {
					UserCollection userCollection = userRepository.findOne(complaintCollection.getDoctorId());
					if (userCollection != null) {
						complaintCollection
								.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
										+ userCollection.getFirstName());
					}
				} else {
					complaintCollection.setCreatedBy("ADMIN");
				}
			} else {
				ComplaintCollection oldComplaintCollection = complaintRepository.findOne(complaintCollection.getId());
				complaintCollection.setCreatedBy(oldComplaintCollection.getCreatedBy());
				complaintCollection.setCreatedTime(oldComplaintCollection.getCreatedTime());
				complaintCollection.setDiscarded(oldComplaintCollection.getDiscarded());
			}
			complaintCollection = complaintRepository.save(complaintCollection);

			BeanUtil.map(complaintCollection, complaint);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			try {
				mailService.sendExceptionMail("Backend Business Exception :: While adding/editing complaint", e.getMessage());
			} catch (MessagingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return complaint;
	}

	@Override
	@Transactional
	public Observation addEditObservation(Observation observation) {
		try {
			ObservationCollection observationCollection = new ObservationCollection();
			BeanUtil.map(observation, observationCollection);
			if (DPDoctorUtils.anyStringEmpty(observationCollection.getId())) {
				observationCollection.setCreatedTime(new Date());
				if (!DPDoctorUtils.anyStringEmpty(observationCollection.getDoctorId())) {
					UserCollection userCollection = userRepository.findOne(observationCollection.getDoctorId());
					if (userCollection != null) {
						observationCollection
								.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
										+ userCollection.getFirstName());
					}
				} else {
					observationCollection.setCreatedBy("ADMIN");
				}
			} else {
				ObservationCollection oldObservationCollection = observationRepository
						.findOne(observationCollection.getId());
				observationCollection.setCreatedBy(oldObservationCollection.getCreatedBy());
				observationCollection.setCreatedTime(oldObservationCollection.getCreatedTime());
				observationCollection.setDiscarded(oldObservationCollection.getDiscarded());
			}
			observationCollection = observationRepository.save(observationCollection);

			BeanUtil.map(observationCollection, observation);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			try {
				mailService.sendExceptionMail("Backend Business Exception :: While adding/editing observation", e.getMessage());
			} catch (MessagingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return observation;
	}

	@Override
	@Transactional
	public ProvisionalDiagnosis addEditProvisionalDiagnosis(ProvisionalDiagnosis provisionalDiagnosis) {
		try {
			ProvisionalDiagnosisCollection provisionalDiagnosisCollection = new ProvisionalDiagnosisCollection();
			BeanUtil.map(provisionalDiagnosis, provisionalDiagnosisCollection);
			if (DPDoctorUtils.anyStringEmpty(provisionalDiagnosisCollection.getId())) {
				provisionalDiagnosisCollection.setCreatedTime(new Date());
				if (!DPDoctorUtils.anyStringEmpty(provisionalDiagnosisCollection.getDoctorId())) {
					UserCollection userCollection = userRepository
							.findOne(provisionalDiagnosisCollection.getDoctorId());
					if (userCollection != null) {
						provisionalDiagnosisCollection
								.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
										+ userCollection.getFirstName());
					}
				} else {
					provisionalDiagnosisCollection.setCreatedBy("ADMIN");
				}
			} else {
				ProvisionalDiagnosisCollection oldProvisionalDiagnosisCollection = provisionalDiagnosisRepository
						.findOne(provisionalDiagnosisCollection.getId());
				provisionalDiagnosisCollection.setCreatedBy(oldProvisionalDiagnosisCollection.getCreatedBy());
				provisionalDiagnosisCollection.setCreatedTime(oldProvisionalDiagnosisCollection.getCreatedTime());
				provisionalDiagnosisCollection.setDiscarded(oldProvisionalDiagnosisCollection.getDiscarded());
			}
			provisionalDiagnosisCollection = provisionalDiagnosisRepository.save(provisionalDiagnosisCollection);

			BeanUtil.map(provisionalDiagnosisCollection, provisionalDiagnosis);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			try {
				mailService.sendExceptionMail("Backend Business Exception :: While adding/editing provisional diagnosis", e.getMessage());
			} catch (MessagingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return provisionalDiagnosis;
	}

	@Override
	@Transactional
	public GeneralExam addEditGeneralExam(GeneralExam generalExam) {
		try {
			GeneralExamCollection generalExamCollection = new GeneralExamCollection();
			BeanUtil.map(generalExam, generalExamCollection);
			if (DPDoctorUtils.anyStringEmpty(generalExamCollection.getId())) {
				generalExamCollection.setCreatedTime(new Date());
				if (!DPDoctorUtils.anyStringEmpty(generalExamCollection.getDoctorId())) {
					UserCollection userCollection = userRepository.findOne(generalExamCollection.getDoctorId());
					if (userCollection != null) {
						generalExamCollection
								.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
										+ userCollection.getFirstName());
					}
				} else {
					generalExamCollection.setCreatedBy("ADMIN");
				}
			} else {
				GeneralExamCollection oldGeneralExamCollection = generalExamRepository
						.findOne(generalExamCollection.getId());
				generalExamCollection.setCreatedBy(oldGeneralExamCollection.getCreatedBy());
				generalExamCollection.setCreatedTime(oldGeneralExamCollection.getCreatedTime());
				generalExamCollection.setDiscarded(oldGeneralExamCollection.getDiscarded());
			}
			generalExamCollection = generalExamRepository.save(generalExamCollection);

			BeanUtil.map(generalExamCollection, generalExam);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			try {
				mailService.sendExceptionMail("Backend Business Exception :: While adding/editing general examination", e.getMessage());
			} catch (MessagingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return generalExam;
	}

	@Override
	@Transactional
	public SystemExam addEditSystemExam(SystemExam systemExam) {
		try {
			SystemExamCollection systemExamCollection = new SystemExamCollection();
			BeanUtil.map(systemExam, systemExamCollection);
			if (DPDoctorUtils.anyStringEmpty(systemExamCollection.getId())) {
				systemExamCollection.setCreatedTime(new Date());
				if (!DPDoctorUtils.anyStringEmpty(systemExamCollection.getDoctorId())) {
					UserCollection userCollection = userRepository.findOne(systemExamCollection.getDoctorId());
					if (userCollection != null) {
						systemExamCollection
								.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
										+ userCollection.getFirstName());
					}
				} else {
					systemExamCollection.setCreatedBy("ADMIN");
				}
			} else {
				SystemExamCollection oldSystemExamCollection = systemExamRepository
						.findOne(systemExamCollection.getId());
				systemExamCollection.setCreatedBy(oldSystemExamCollection.getCreatedBy());
				systemExamCollection.setCreatedTime(oldSystemExamCollection.getCreatedTime());
				systemExamCollection.setDiscarded(oldSystemExamCollection.getDiscarded());
			}
			systemExamCollection = systemExamRepository.save(systemExamCollection);

			BeanUtil.map(systemExamCollection, systemExam);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			try {
				mailService.sendExceptionMail("Backend Business Exception :: While adding/editing systematic examination", e.getMessage());
			} catch (MessagingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return systemExam;
	}

	@Override
	@Transactional
	public MenstrualHistory addEditMenstrualHistory(MenstrualHistory menstrualHistory) {
		try {
			MenstrualHistoryCollection menstrualHistoryCollection = new MenstrualHistoryCollection();
			BeanUtil.map(menstrualHistory, menstrualHistoryCollection);
			if (DPDoctorUtils.anyStringEmpty(menstrualHistoryCollection.getId())) {
				menstrualHistoryCollection.setCreatedTime(new Date());
				if (!DPDoctorUtils.anyStringEmpty(menstrualHistoryCollection.getDoctorId())) {
					UserCollection userCollection = userRepository.findOne(menstrualHistoryCollection.getDoctorId());
					if (userCollection != null) {
						menstrualHistoryCollection
								.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
										+ userCollection.getFirstName());
					}
				} else {
					menstrualHistoryCollection.setCreatedBy("ADMIN");
				}
			} else {
				MenstrualHistoryCollection oldMenstrualHistoryCollection = menstrualHistoryRepository
						.findOne(menstrualHistoryCollection.getId());
				menstrualHistoryCollection.setCreatedBy(oldMenstrualHistoryCollection.getCreatedBy());
				menstrualHistoryCollection.setCreatedTime(oldMenstrualHistoryCollection.getCreatedTime());
				menstrualHistoryCollection.setDiscarded(oldMenstrualHistoryCollection.getDiscarded());
			}
			menstrualHistoryCollection = menstrualHistoryRepository.save(menstrualHistoryCollection);

			BeanUtil.map(menstrualHistoryCollection, menstrualHistory);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			try {
				mailService.sendExceptionMail("Backend Business Exception :: While adding/editing menstrual history", e.getMessage());
			} catch (MessagingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return menstrualHistory;
	}
	
	@Override
	@Transactional
	public PresentComplaint addEditPresentComplaint(PresentComplaint presentComplaint) {
		try {
			PresentComplaintCollection presentComplaintCollection = new PresentComplaintCollection();
			BeanUtil.map(presentComplaint, presentComplaintCollection);
			if (DPDoctorUtils.anyStringEmpty(presentComplaintCollection.getId())) {
				presentComplaintCollection.setCreatedTime(new Date());
				if (!DPDoctorUtils.anyStringEmpty(presentComplaintCollection.getDoctorId())) {
					UserCollection userCollection = userRepository.findOne(presentComplaintCollection.getDoctorId());
					if (userCollection != null) {
						presentComplaintCollection
								.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
										+ userCollection.getFirstName());
					}
				} else {
					presentComplaintCollection.setCreatedBy("ADMIN");
				}
			} else {
				PresentComplaintCollection oldPresentComplaintCollection = presentComplaintRepository
						.findOne(presentComplaintCollection.getId());
				presentComplaintCollection.setCreatedBy(oldPresentComplaintCollection.getCreatedBy());
				presentComplaintCollection.setCreatedTime(oldPresentComplaintCollection.getCreatedTime());
				presentComplaintCollection.setDiscarded(oldPresentComplaintCollection.getDiscarded());
			}
			presentComplaintCollection = presentComplaintRepository.save(presentComplaintCollection);

			BeanUtil.map(presentComplaintCollection, presentComplaint);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			try {
				mailService.sendExceptionMail("Backend Business Exception :: While adding/editing present complaint", e.getMessage());
			} catch (MessagingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return presentComplaint;
	}
	
	@Override
	@Transactional
	public PresentComplaintHistory addEditPresentComplaintHistory(PresentComplaintHistory presentComplaintHistory) {
		try {
			PresentComplaintHistoryCollection presentComplaintHistoryCollection = new PresentComplaintHistoryCollection();
			BeanUtil.map(presentComplaintHistory, presentComplaintHistoryCollection);
			if (DPDoctorUtils.anyStringEmpty(presentComplaintHistoryCollection.getId())) {
				presentComplaintHistoryCollection.setCreatedTime(new Date());
				if (!DPDoctorUtils.anyStringEmpty(presentComplaintHistoryCollection.getDoctorId())) {
					UserCollection userCollection = userRepository.findOne(presentComplaintHistoryCollection.getDoctorId());
					if (userCollection != null) {
						presentComplaintHistoryCollection
								.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
										+ userCollection.getFirstName());
					}
				} else {
					presentComplaintHistoryCollection.setCreatedBy("ADMIN");
				}
			} else {
				PresentComplaintHistoryCollection oldPresentComplaintHistoryCollection = presentComplaintHistoryRepository
						.findOne(presentComplaintHistoryCollection.getId());
				presentComplaintHistoryCollection.setCreatedBy(oldPresentComplaintHistoryCollection.getCreatedBy());
				presentComplaintHistoryCollection.setCreatedTime(oldPresentComplaintHistoryCollection.getCreatedTime());
				presentComplaintHistoryCollection.setDiscarded(oldPresentComplaintHistoryCollection.getDiscarded());
			}
			presentComplaintHistoryCollection = presentComplaintHistoryRepository.save(presentComplaintHistoryCollection);

			BeanUtil.map(presentComplaintHistoryCollection, presentComplaintHistory);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			try {
				mailService.sendExceptionMail("Backend Business Exception :: While adding/editing present complaint history", e.getMessage());
			} catch (MessagingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return presentComplaintHistory;
	}
	
	@Override
	@Transactional
	public ObstetricHistory addEditObstetricHistory(ObstetricHistory obstetricHistory) {
		try {
			ObstetricHistoryCollection obstetricHistoryCollection = new ObstetricHistoryCollection();
			BeanUtil.map(obstetricHistory, obstetricHistoryCollection);
			if (DPDoctorUtils.anyStringEmpty(obstetricHistoryCollection.getId())) {
				obstetricHistoryCollection.setCreatedTime(new Date());
				if (!DPDoctorUtils.anyStringEmpty(obstetricHistoryCollection.getDoctorId())) {
					UserCollection userCollection = userRepository.findOne(obstetricHistoryCollection.getDoctorId());
					if (userCollection != null) {
						obstetricHistoryCollection
								.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
										+ userCollection.getFirstName());
					}
				} else {
					obstetricHistoryCollection.setCreatedBy("ADMIN");
				}
			} else {
				ObstetricHistoryCollection oldObstetricHistoryCollection= obstetricHistoryRepository
						.findOne(obstetricHistoryCollection.getId());
				obstetricHistoryCollection.setCreatedBy(oldObstetricHistoryCollection.getCreatedBy());
				obstetricHistoryCollection.setCreatedTime(oldObstetricHistoryCollection.getCreatedTime());
				obstetricHistoryCollection.setDiscarded(oldObstetricHistoryCollection.getDiscarded());
			}
			obstetricHistoryCollection = obstetricHistoryRepository.save(obstetricHistoryCollection);

			BeanUtil.map(obstetricHistoryCollection, obstetricHistory);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			try {
				mailService.sendExceptionMail("Backend Business Exception :: While adding/editing obstetrics history", e.getMessage());
			} catch (MessagingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return obstetricHistory;
	}
	

	@Override
	@Transactional
	public Investigation addEditInvestigation(Investigation investigation) {
		try {
			InvestigationCollection investigationCollection = new InvestigationCollection();
			BeanUtil.map(investigation, investigationCollection);
			if (DPDoctorUtils.anyStringEmpty(investigationCollection.getId())) {
				investigationCollection.setCreatedTime(new Date());
				if (!DPDoctorUtils.anyStringEmpty(investigationCollection.getDoctorId())) {
					UserCollection userCollection = userRepository.findOne(investigationCollection.getDoctorId());
					if (userCollection != null) {
						investigationCollection
								.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
										+ userCollection.getFirstName());
					}
				} else {
					investigationCollection.setCreatedBy("ADMIN");
				}
			} else {
				InvestigationCollection oldInvestigationCollection = investigationRepository
						.findOne(investigationCollection.getId());
				investigationCollection.setCreatedBy(oldInvestigationCollection.getCreatedBy());
				investigationCollection.setCreatedTime(oldInvestigationCollection.getCreatedTime());
				investigationCollection.setDiscarded(oldInvestigationCollection.getDiscarded());
			}
			investigationCollection = investigationRepository.save(investigationCollection);

			BeanUtil.map(investigationCollection, investigation);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			try {
				mailService.sendExceptionMail("Backend Business Exception :: While adding/editing investigation", e.getMessage());
			} catch (MessagingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return investigation;
	}

	@Override
	@Transactional
	public Diagnoses addEditDiagnosis(Diagnoses diagnosis) {
		try {
			DiagnosisCollection diagnosisCollection = new DiagnosisCollection();
			BeanUtil.map(diagnosis, diagnosisCollection);
			if (DPDoctorUtils.anyStringEmpty(diagnosisCollection.getId())) {
				diagnosisCollection.setCreatedTime(new Date());
				if (!DPDoctorUtils.anyStringEmpty(diagnosisCollection.getDoctorId())) {
					UserCollection userCollection = userRepository.findOne(diagnosisCollection.getDoctorId());
					if (userCollection != null) {
						diagnosisCollection
								.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
										+ userCollection.getFirstName());
					}
				} else {
					diagnosisCollection.setCreatedBy("ADMIN");
				}
			} else {
				DiagnosisCollection oldDiagnosisCollection = diagnosisRepository.findOne(diagnosisCollection.getId());
				diagnosisCollection.setCreatedBy(oldDiagnosisCollection.getCreatedBy());
				diagnosisCollection.setCreatedTime(oldDiagnosisCollection.getCreatedTime());
				diagnosisCollection.setDiscarded(oldDiagnosisCollection.getDiscarded());
			}
			diagnosisCollection = diagnosisRepository.save(diagnosisCollection);

			BeanUtil.map(diagnosisCollection, diagnosis);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			try {
				mailService.sendExceptionMail("Backend Business Exception :: While adding/editing diagnosis", e.getMessage());
			} catch (MessagingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return diagnosis;
	}

	@Override
	@Transactional
	public Notes addEditNotes(Notes notes) {
		try {
			NotesCollection notesCollection = new NotesCollection();
			BeanUtil.map(notes, notesCollection);
			if (DPDoctorUtils.anyStringEmpty(notesCollection.getId())) {
				notesCollection.setCreatedTime(new Date());
				if (!DPDoctorUtils.anyStringEmpty(notesCollection.getDoctorId())) {
					UserCollection userCollection = userRepository.findOne(notesCollection.getDoctorId());
					if (userCollection != null) {
						notesCollection
								.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
										+ userCollection.getFirstName());
					}
				} else {
					notesCollection.setCreatedBy("ADMIN");
				}
			} else {
				NotesCollection oldNotesCollection = notesRepository.findOne(notesCollection.getId());
				notesCollection.setCreatedBy(oldNotesCollection.getCreatedBy());
				notesCollection.setCreatedTime(oldNotesCollection.getCreatedTime());
				notesCollection.setDiscarded(oldNotesCollection.getDiscarded());
			}
			notesCollection = notesRepository.save(notesCollection);

			BeanUtil.map(notesCollection, notes);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			try {
				mailService.sendExceptionMail("Backend Business Exception :: While adding/editing notes", e.getMessage());
			} catch (MessagingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return notes;
	}

	@Override
	@Transactional
	public Diagram addEditDiagram(Diagram diagram) {
		try {
			if (diagram.getDiagram() != null) {
				String path = "clinicalNotes" + File.separator + "diagrams";
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
					UserCollection userCollection = userRepository.findOne(diagramsCollection.getDoctorId());
					if (userCollection != null) {
						diagramsCollection
								.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
										+ userCollection.getFirstName());
					}
				} else {
					diagramsCollection.setCreatedBy("ADMIN");
				}
			} else {
				DiagramsCollection oldDiagramsCollection = diagramsRepository.findOne(diagramsCollection.getId());
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
				mailService.sendExceptionMail("Backend Business Exception :: While adding/editing diagram", e.getMessage());
			} catch (MessagingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return diagram;
	}

	@Override
	@Transactional
	public Complaint deleteComplaint(String id, String doctorId, String locationId, String hospitalId,
			Boolean discarded) {
		Complaint response = null;
		try {
			ComplaintCollection complaintCollection = complaintRepository.findOne(new ObjectId(id));
			if (complaintCollection != null) {
				if (!DPDoctorUtils.anyStringEmpty(complaintCollection.getDoctorId(),
						complaintCollection.getHospitalId(), complaintCollection.getLocationId())) {
					if (complaintCollection.getDoctorId().toString().equals(doctorId)
							&& complaintCollection.getHospitalId().toString().equals(hospitalId)
							&& complaintCollection.getLocationId().toString().equals(locationId)) {

						complaintCollection.setDiscarded(discarded);
						complaintCollection.setUpdatedTime(new Date());
						complaintRepository.save(complaintCollection);
						response = new Complaint();
						BeanUtil.map(complaintCollection, response);
					} else {
						logger.warn("Invalid Doctor Id, Hospital Id, Or Location Id");
						throw new BusinessException(ServiceError.InvalidInput,
								"Invalid Doctor Id, Hospital Id, Or Location Id");
					}
				} else {
					complaintCollection.setDiscarded(discarded);
					complaintCollection.setUpdatedTime(new Date());
					complaintRepository.save(complaintCollection);
					response = new Complaint();
					BeanUtil.map(complaintCollection, response);
				}
			} else {
				logger.warn("Complaint not found!");
				throw new BusinessException(ServiceError.NoRecord, "Complaint not found!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			try {
				mailService.sendExceptionMail("Backend Business Exception :: While deleting complaint", e.getMessage());
			} catch (MessagingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	@Transactional
	public Observation deleteObservation(String id, String doctorId, String locationId, String hospitalId,
			Boolean discarded) {
		Observation response = null;
		try {
			ObservationCollection observationCollection = observationRepository.findOne(new ObjectId(id));
			if (observationCollection != null) {
				if (DPDoctorUtils.anyStringEmpty(observationCollection.getDoctorId(),
						observationCollection.getHospitalId(), observationCollection.getLocationId())) {
					if (observationCollection.getDoctorId().toString().equals(doctorId)
							&& observationCollection.getHospitalId().toString().equals(hospitalId)
							&& observationCollection.getLocationId().toString().equals(locationId)) {
						observationCollection.setDiscarded(discarded);
						observationCollection.setUpdatedTime(new Date());
						observationRepository.save(observationCollection);
						response = new Observation();
						BeanUtil.map(observationCollection, response);
					} else {
						logger.warn("Invalid Doctor Id, Hospital Id, Or Location Id");
						throw new BusinessException(ServiceError.InvalidInput,
								"Invalid Doctor Id, Hospital Id, Or Location Id");
					}
				} else {
					observationCollection.setDiscarded(discarded);
					observationCollection.setUpdatedTime(new Date());
					observationRepository.save(observationCollection);
					response = new Observation();
					BeanUtil.map(observationCollection, response);
				}
			} else {
				logger.warn("Observation not found!");
				throw new BusinessException(ServiceError.NoRecord, "Observation not found!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			try {
				mailService.sendExceptionMail("Backend Business Exception :: While deleting observation", e.getMessage());
			} catch (MessagingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	@Transactional
	public Investigation deleteInvestigation(String id, String doctorId, String locationId, String hospitalId,
			Boolean discarded) {
		Investigation response = null;
		try {
			InvestigationCollection investigationCollection = investigationRepository.findOne(new ObjectId(id));
			if (investigationCollection != null) {
				if (investigationCollection.getDoctorId() != null && investigationCollection.getHospitalId() != null
						&& investigationCollection.getLocationId() != null) {
					if (investigationCollection.getDoctorId().toString().equals(doctorId)
							&& investigationCollection.getHospitalId().toString().equals(hospitalId)
							&& investigationCollection.getLocationId().toString().equals(locationId)) {
						investigationCollection.setDiscarded(discarded);
						investigationCollection.setUpdatedTime(new Date());
						investigationRepository.save(investigationCollection);
						response = new Investigation();
						BeanUtil.map(investigationCollection, response);
					} else {
						logger.warn("Invalid Doctor Id, Hospital Id, Or Location Id");
						throw new BusinessException(ServiceError.InvalidInput,
								"Invalid Doctor Id, Hospital Id, Or Location Id");
					}
				} else {
					investigationCollection.setDiscarded(discarded);
					investigationCollection.setUpdatedTime(new Date());
					investigationRepository.save(investigationCollection);
					response = new Investigation();
					BeanUtil.map(investigationCollection, response);
				}
			} else {
				logger.warn("Investigation not found!");
				throw new BusinessException(ServiceError.NoRecord, "Investigation not found!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			try {
				mailService.sendExceptionMail("Backend Business Exception :: While deleting investigation", e.getMessage());
			} catch (MessagingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	@Transactional
	public Diagnoses deleteDiagnosis(String id, String doctorId, String locationId, String hospitalId,
			Boolean discarded) {
		Diagnoses response = null;
		try {
			DiagnosisCollection diagnosisCollection = diagnosisRepository.findOne(new ObjectId(id));
			if (diagnosisCollection != null) {
				if (diagnosisCollection.getDoctorId() != null && diagnosisCollection.getHospitalId() != null
						&& diagnosisCollection.getLocationId() != null) {
					if (diagnosisCollection.getDoctorId().toString().equals(doctorId)
							&& diagnosisCollection.getHospitalId().toString().equals(hospitalId)
							&& diagnosisCollection.getLocationId().toString().equals(locationId)) {
						diagnosisCollection.setDiscarded(discarded);
						diagnosisCollection.setUpdatedTime(new Date());
						diagnosisRepository.save(diagnosisCollection);
						response = new Diagnoses();
						BeanUtil.map(diagnosisCollection, response);
					} else {
						logger.warn("Invalid Doctor Id, Hospital Id, Or Location Id");
						throw new BusinessException(ServiceError.InvalidInput,
								"Invalid Doctor Id, Hospital Id, Or Location Id");
					}
				} else {
					diagnosisCollection.setDiscarded(discarded);
					diagnosisCollection.setUpdatedTime(new Date());
					diagnosisRepository.save(diagnosisCollection);
					response = new Diagnoses();
					BeanUtil.map(diagnosisCollection, response);
				}

			} else {
				logger.warn("Diagnosis not found!");
				throw new BusinessException(ServiceError.NoRecord, "Diagnosis not found!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			try {
				mailService.sendExceptionMail("Backend Business Exception :: While deleting diagnosis", e.getMessage());
			} catch (MessagingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	@Transactional
	public Notes deleteNotes(String id, String doctorId, String locationId, String hospitalId, Boolean discarded) {
		Notes response = null;
		try {
			NotesCollection notesCollection = notesRepository.findOne(new ObjectId(id));
			if (notesCollection != null) {
				if (notesCollection.getDoctorId() != null && notesCollection.getHospitalId() != null
						&& notesCollection.getLocationId() != null) {
					if (notesCollection.getDoctorId().toString().equals(doctorId)
							&& notesCollection.getHospitalId().toString().equals(hospitalId)
							&& notesCollection.getLocationId().toString().equals(locationId)) {
						notesCollection.setDiscarded(discarded);
						notesCollection.setUpdatedTime(new Date());
						notesRepository.save(notesCollection);
						response = new Notes();
						BeanUtil.map(notesCollection, response);
					} else {
						logger.warn("Invalid Doctor Id, Hospital Id, Or Location Id");
						throw new BusinessException(ServiceError.InvalidInput,
								"Invalid Doctor Id, Hospital Id, Or Location Id");
					}
				} else {
					notesCollection.setDiscarded(discarded);
					notesCollection.setUpdatedTime(new Date());
					notesRepository.save(notesCollection);
					response = new Notes();
					BeanUtil.map(notesCollection, response);
				}

			} else {
				logger.warn("Notes not found!");
				throw new BusinessException(ServiceError.NoRecord, "Notes not found!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			try {
				mailService.sendExceptionMail("Backend Business Exception :: While deleting notes", e.getMessage());
			} catch (MessagingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	@Transactional
	public Diagram deleteDiagram(String id, String doctorId, String locationId, String hospitalId, Boolean discarded) {
		Diagram response = null;
		try {
			DiagramsCollection diagramsCollection = diagramsRepository.findOne(new ObjectId(id));
			if (diagramsCollection != null) {
				if (diagramsCollection.getDoctorId() != null && diagramsCollection.getHospitalId() != null
						&& diagramsCollection.getLocationId() != null) {
					if (diagramsCollection.getDoctorId().toString().equals(doctorId)
							&& diagramsCollection.getHospitalId().toString().equals(hospitalId)
							&& diagramsCollection.getLocationId().toString().equals(locationId)) {
						diagramsCollection.setDiscarded(discarded);
						diagramsCollection.setUpdatedTime(new Date());
						diagramsRepository.save(diagramsCollection);
						response = new Diagram();
						BeanUtil.map(diagramsCollection, response);
					} else {
						logger.warn("Invalid Doctor Id, Hospital Id, Or Location Id");
						throw new BusinessException(ServiceError.InvalidInput,
								"Invalid Doctor Id, Hospital Id, Or Location Id");
					}
				} else {
					diagramsCollection.setDiscarded(discarded);
					diagramsCollection.setUpdatedTime(new Date());
					diagramsRepository.save(diagramsCollection);
					response = new Diagram();
					BeanUtil.map(diagramsCollection, response);
				}

			} else {
				logger.warn("Diagram not found!");
				throw new BusinessException(ServiceError.NoRecord, "Diagram not found!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			try {
				mailService.sendExceptionMail("Backend Business Exception :: While deleting diagram", e.getMessage());
			} catch (MessagingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	@Transactional
	public Integer getClinicalNotesCount(ObjectId doctorObjectId, ObjectId patientObjectId, ObjectId locationObjectId,
			ObjectId hospitalObjectId, boolean isOTPVerified) {
		Integer clinicalNotesCount = 0;
		try {
			Criteria criteria = new Criteria("discarded").is(false).and("patientId").is(patientObjectId);
			if (!isOTPVerified) {
				if (!DPDoctorUtils.anyStringEmpty(locationObjectId, hospitalObjectId))
					criteria.and("locationId").is(locationObjectId).and("hospitalId").is(hospitalObjectId);
				if (!DPDoctorUtils.anyStringEmpty(doctorObjectId))
					criteria.and("doctorId").is(doctorObjectId);
			}
			clinicalNotesCount = (int) mongoTemplate.count(new Query(criteria), ClinicalNotesCollection.class);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			try {
				mailService.sendExceptionMail("Backend Business Exception :: While getting clinical notes count", e.getMessage());
			} catch (MessagingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Clinical Notes Count");
		}
		return clinicalNotesCount;
	}

	@Override
	@Transactional
	public List<?> getClinicalItems(String type, String range, int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded, String searchTerm) {
		List<?> response = new ArrayList<Object>();

		switch (ClinicalItems.valueOf(type.toUpperCase())) {

		case COMPLAINTS: {

			switch (Range.valueOf(range.toUpperCase())) {

			case GLOBAL:
				response = getGlobalComplaints(page, size, doctorId, updatedTime, discarded);
				break;
			case CUSTOM:
				response = getCustomComplaints(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
				break;
			case BOTH:
				response = getCustomGlobalComplaints(page, size, doctorId, locationId, hospitalId, updatedTime,
						discarded);
				break;
			default:
				break;
			}
			break;
		}
		case INVESTIGATIONS: {
			switch (Range.valueOf(range.toUpperCase())) {

			case GLOBAL:
				response = getGlobalInvestigations(page, size, doctorId, updatedTime, discarded);
				break;
			case CUSTOM:
				response = getCustomInvestigations(page, size, doctorId, locationId, hospitalId, updatedTime,
						discarded);
				break;
			case BOTH:
				response = getCustomGlobalInvestigations(page, size, doctorId, locationId, hospitalId, updatedTime,
						discarded);
				break;
			default:
				break;
			}
			break;
		}
		case OBSERVATIONS: {
			switch (Range.valueOf(range.toUpperCase())) {

			case GLOBAL:
				response = getGlobalObservations(page, size, doctorId, updatedTime, discarded);
				break;
			case CUSTOM:
				response = getCustomObservations(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
				break;
			case BOTH:
				response = getCustomGlobalObservations(page, size, doctorId, locationId, hospitalId, updatedTime,
						discarded);
				break;
			default:
				break;
			}
			break;
		}
		case DIAGNOSIS: {
			switch (Range.valueOf(range.toUpperCase())) {

			case GLOBAL:
				response = getGlobalDiagnosis(page, size, doctorId, updatedTime, discarded);
				break;
			case CUSTOM:
				response = getCustomDiagnosis(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
				break;
			case BOTH:
				response = getCustomGlobalDiagnosis(page, size, doctorId, locationId, hospitalId, updatedTime,
						discarded);
				break;
			default:
				break;
			}
			break;
		}
		case NOTES: {
			switch (Range.valueOf(range.toUpperCase())) {

			case GLOBAL:
				response = getGlobalNotes(page, size, doctorId, updatedTime, discarded);
				break;
			case CUSTOM:
				response = getCustomNotes(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
				break;
			case BOTH:
				response = getCustomGlobalNotes(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
				break;
			default:
				break;
			}
			break;
		}
		case DIAGRAMS: {
			switch (Range.valueOf(range.toUpperCase())) {

			case GLOBAL:
				response = getGlobalDiagrams(page, size, doctorId, updatedTime, discarded);
				break;
			case CUSTOM:
				response = getCustomDiagrams(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
				break;
			case BOTH:
				response = getCustomGlobalDiagrams(page, size, doctorId, locationId, hospitalId, updatedTime,
						discarded);
				break;
			default:
				break;
			}
			break;
		}
		
		case PRESENT_COMPLAINT: {
			switch (Range.valueOf(range.toUpperCase())) {

			case GLOBAL:
				response = getGlobalPresentComplaint(page, size, doctorId, updatedTime, discarded);
				break;
			case CUSTOM:
				response = getCustomPresentComplaint(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
				break;
			case BOTH:
				response = getCustomGlobalPresentComplaint(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
				break;
			default:
				break;
			}
			break;
		}
		
		case HISTORY_OF_PRESENT_COMPLAINT: {
			switch (Range.valueOf(range.toUpperCase())) {

			case GLOBAL:
				response = getGlobalPresentComplaintHistory(page, size, doctorId, updatedTime, discarded);
				break;
			case CUSTOM:
				response = getCustomPresentComplaintHistory(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
				break;
			case BOTH:
				response = getCustomGlobalPresentComplaintHistory(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
				break;
			default:
				break;
			}
			break;
		}
		
		case PROVISIONAL_DIAGNOSIS: {
			switch (Range.valueOf(range.toUpperCase())) {

			case GLOBAL:
				response = getGlobalProvisionalDiagnosis(page, size, doctorId, updatedTime, discarded);
				break;
			case CUSTOM:
				response = getCustomProvisionalDiagnosis(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
				break;
			case BOTH:
				response = getCustomGlobalProvisionalDiagnosis(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
				break;
			default:
				break;
			}
			break;
		}
		
		case GENERAL_EXAMINATION: {
			switch (Range.valueOf(range.toUpperCase())) {

			case GLOBAL:
				response = getGlobalGeneralExam(page, size, doctorId, updatedTime, discarded);
				break;
			case CUSTOM:
				response = getCustomGeneralExam(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
				break;
			case BOTH:
				response = getCustomGlobalGeneralExam(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
				break;
			default:
				break;
			}
			break;
		}
		
		case SYSTEMATIC_EXAMINATION: {
			switch (Range.valueOf(range.toUpperCase())) {

			case GLOBAL:
				response = getGlobalSystemExam(page, size, doctorId, updatedTime, discarded);
				break;
			case CUSTOM:
				response = getCustomSystemExam(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
				break;
			case BOTH:
				response = getCustomGlobalSystemExam(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
				break;
			default:
				break;
			}
			break;
		}
		
		case MENSTRUAL_HISTORY: {
			switch (Range.valueOf(range.toUpperCase())) {

			case GLOBAL:
				response = getGlobalMenstrualHistory(page, size, doctorId, updatedTime, discarded);
				break;
			case CUSTOM:
				response = getCustomMenstrualHistory(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
				break;
			case BOTH:
				response = getCustomGlobalMenstrualHistory(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
				break;
			default:
				break;
			}
			break;
		}
		
		case OBSTETRIC_HISTORY: {
			switch (Range.valueOf(range.toUpperCase())) {

			case GLOBAL:
				response = getGlobalObstetricHistory(page, size, doctorId, updatedTime, discarded);
				break;
			case CUSTOM:
				response = getCustomObstetricHistory(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
				break;
			case BOTH:
				response = getCustomGlobalObstetricHistory(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
				break;
			default:
				break;
			}
			break;
		}
		
		case INDICATION_OF_USG: {
			switch (Range.valueOf(range.toUpperCase())) {

			case GLOBAL:
				response = getGlobalIndicationOfUSG(page, size, doctorId, updatedTime, discarded);
				break;
			case CUSTOM:
				response = getCustomIndicationOfUSG(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
				break;
			case BOTH:
				response = getCustomGlobalIndicationOfUSG(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
				break;
			default:
				break;
			}
			break;
		}
		
		case PA: {
			switch (Range.valueOf(range.toUpperCase())) {

			case GLOBAL:
				response = getGlobalPA(page, size, doctorId, updatedTime, discarded);
				break;
			case CUSTOM:
				response = getCustomPA(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
				break;
			case BOTH:
				response = getCustomGlobalPA(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
				break;
			default:
				break;
			}
			break;
		}
		
		case PV: {
			switch (Range.valueOf(range.toUpperCase())) {

			case GLOBAL:
				response = getGlobalPV(page, size, doctorId, updatedTime, discarded);
				break;
			case CUSTOM:
				response = getCustomPV(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
				break;
			case BOTH:
				response = getCustomGlobalPV(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
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
	private List<Complaint> getCustomGlobalComplaints(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded) {
		List<Complaint> response = new ArrayList<Complaint>();
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findAll(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add(null);
				specialities.add("ALL");
			}

			AggregationResults<Complaint> results = mongoTemplate.aggregate(
					DPDoctorUtils.createCustomGlobalAggregation(page, size, doctorId, locationId, hospitalId,
							updatedTime, discarded, null, null, specialities,null),
					ComplaintCollection.class, Complaint.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			try {
				mailService.sendExceptionMail("Backend Business Exception :: While getting custom global complaints", e.getMessage());
			} catch (MessagingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Complaints");
		}
		return response;

	}

	@SuppressWarnings("unchecked")
	private List<Complaint> getGlobalComplaints(int page, int size, String doctorId, String updatedTime,
			Boolean discarded) {
		List<Complaint> response = null;
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findAll(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add("ALL");
				specialities.add(null);
			}

			AggregationResults<Complaint> results = mongoTemplate.aggregate(
					DPDoctorUtils.createGlobalAggregation(page, size, updatedTime, discarded, null, null, specialities,null),
					ComplaintCollection.class, Complaint.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			try {
				mailService.sendExceptionMail("Backend Business Exception :: While Getting global complaints", e.getMessage());
			} catch (MessagingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting global complaints");
		}
		return response;
	}

	private List<Complaint> getCustomComplaints(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded) {
		List<Complaint> response = null;
		try {
			AggregationResults<Complaint> results = mongoTemplate.aggregate(DPDoctorUtils.createCustomAggregation(page,
					size, doctorId, locationId, hospitalId, updatedTime, discarded, null, null,null),
					ComplaintCollection.class, Complaint.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			try {
				mailService.sendExceptionMail("Backend Business Exception :: While getting custom complaints", e.getMessage());
			} catch (MessagingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Complaints");
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private List<Investigation> getCustomGlobalInvestigations(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded) {
		List<Investigation> response = new ArrayList<Investigation>();
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findAll(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add(null);
				specialities.add("ALL");
			}

			AggregationResults<Investigation> results = mongoTemplate.aggregate(
					DPDoctorUtils.createCustomGlobalAggregation(page, size, doctorId, locationId, hospitalId,
							updatedTime, discarded, null, null, specialities,null),
					InvestigationCollection.class, Investigation.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			try {
				mailService.sendExceptionMail("Backend Business Exception :: While getting custom global investigation", e.getMessage());
			} catch (MessagingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Investigations");
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private List<Investigation> getGlobalInvestigations(int page, int size, String doctorId, String updatedTime,
			Boolean discarded) {
		List<Investigation> response = null;
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findAll(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add("ALL");
				specialities.add(null);
			}

			AggregationResults<Investigation> results = mongoTemplate.aggregate(
					DPDoctorUtils.createGlobalAggregation(page, size, updatedTime, discarded, null, null, specialities,null),
					InvestigationCollection.class, Investigation.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			try {
				mailService.sendExceptionMail("Backend Business Exception :: While getting global investigation", e.getMessage());
			} catch (MessagingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Investigations");
		}
		return response;
	}

	private List<Investigation> getCustomInvestigations(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded) {
		List<Investigation> response = null;
		boolean[] discards = new boolean[2];
		discards[0] = false;
		try {
			AggregationResults<Investigation> results = mongoTemplate
					.aggregate(
							DPDoctorUtils.createCustomAggregation(page, size, doctorId, locationId, hospitalId,
									updatedTime, discarded, null, null,null),
							InvestigationCollection.class, Investigation.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			try {
				mailService.sendExceptionMail("Backend Business Exception :: While getting custom investigation", e.getMessage());
			} catch (MessagingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Investigations");
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private List<Observation> getCustomGlobalObservations(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded) {
		List<Observation> response = new ArrayList<Observation>();
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findAll(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add(null);
				specialities.add("ALL");
			}

			AggregationResults<Observation> results = mongoTemplate.aggregate(
					DPDoctorUtils.createCustomGlobalAggregation(page, size, doctorId, locationId, hospitalId,
							updatedTime, discarded, null, null, specialities,null),
					ObservationCollection.class, Observation.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Observations");
		}
		return response;

	}

	@SuppressWarnings("unchecked")
	private List<Observation> getGlobalObservations(int page, int size, String doctorId, String updatedTime,
			Boolean discarded) {
		List<Observation> response = null;
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findAll(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add("ALL");
				specialities.add(null);
			}

			AggregationResults<Observation> results = mongoTemplate.aggregate(
					DPDoctorUtils.createGlobalAggregation(page, size, updatedTime, discarded, null, null, specialities,null),
					ObservationCollection.class, Observation.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Observations");
		}
		return response;
	}

	private List<Observation> getCustomObservations(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded) {
		List<Observation> response = null;
		try {
			AggregationResults<Observation> results = mongoTemplate
					.aggregate(DPDoctorUtils.createCustomAggregation(page, size, doctorId, locationId, hospitalId,
							updatedTime, discarded, null, null,null), Observation.class, Observation.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Observations");
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private List<Diagnoses> getCustomGlobalDiagnosis(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded) {
		List<Diagnoses> response = new ArrayList<Diagnoses>();

		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findAll(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add(null);
				specialities.add("ALL");
			}

			AggregationResults<Diagnoses> results = mongoTemplate.aggregate(
					DPDoctorUtils.createCustomGlobalAggregation(page, size, doctorId, locationId, hospitalId,
							updatedTime, discarded, null, null, specialities,null),
					DiagnosisCollection.class, Diagnoses.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Diagnosis");
		}
		return response;

	}

	@SuppressWarnings("unchecked")
	private List<Diagnoses> getGlobalDiagnosis(int page, int size, String doctorId, String updatedTime,
			Boolean discarded) {
		List<Diagnoses> response = null;
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findAll(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add("ALL");
				specialities.add(null);
			}

			AggregationResults<Diagnoses> results = mongoTemplate.aggregate(
					DPDoctorUtils.createGlobalAggregation(page, size, updatedTime, discarded, null, null, specialities,null),
					DiagnosisCollection.class, Diagnoses.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Diagnosis");
		}
		return response;
	}

	private List<Diagnoses> getCustomDiagnosis(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded) {
		List<Diagnoses> response = null;
		try {
			AggregationResults<Diagnoses> results = mongoTemplate.aggregate(DPDoctorUtils.createCustomAggregation(page,
					size, doctorId, locationId, hospitalId, updatedTime, discarded, null, null,null),
					DiagnosisCollection.class, Diagnoses.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Diagnosis");
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private List<Notes> getCustomGlobalNotes(int page, int size, String doctorId, String locationId, String hospitalId,
			String updatedTime, Boolean discarded) {
		List<Notes> response = new ArrayList<Notes>();
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findAll(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add(null);
				specialities.add("ALL");
			}

			AggregationResults<Notes> results = mongoTemplate
					.aggregate(
							DPDoctorUtils.createCustomGlobalAggregation(page, size, doctorId, locationId, hospitalId,
									updatedTime, discarded, null, null, specialities,null),
							NotesCollection.class, Notes.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Notes");
		}
		return response;

	}

	@SuppressWarnings("unchecked")
	private List<Notes> getGlobalNotes(int page, int size, String doctorId, String updatedTime, Boolean discarded) {
		List<Notes> response = null;
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findAll(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add("ALL");
				specialities.add(null);
			}

			AggregationResults<Notes> results = mongoTemplate.aggregate(
					DPDoctorUtils.createGlobalAggregation(page, size, updatedTime, discarded, null, null, specialities,null),
					NotesCollection.class, Notes.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Notes");
		}
		return response;
	}

	private List<Notes> getCustomNotes(int page, int size, String doctorId, String locationId, String hospitalId,
			String updatedTime, Boolean discarded) {
		List<Notes> response = null;
		try {
			AggregationResults<Notes> results = mongoTemplate.aggregate(DPDoctorUtils.createCustomAggregation(page,
					size, doctorId, locationId, hospitalId, updatedTime, discarded, null, null,null), NotesCollection.class,
					Notes.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Notes");
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private List<Diagram> getCustomGlobalDiagrams(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded) {
		List<Diagram> response = new ArrayList<Diagram>();
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findAll(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add(null);
				specialities.add("ALL");
			}

			AggregationResults<Diagram> results = mongoTemplate
					.aggregate(
							DPDoctorUtils.createCustomGlobalAggregation(page, size, doctorId, locationId, hospitalId,
									updatedTime, discarded, null, null, specialities,null),
							DiagramsCollection.class, Diagram.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Diagrams");
		}
		return response;

	}

	@SuppressWarnings("unchecked")
	private List<Diagram> getGlobalDiagrams(int page, int size, String doctorId, String updatedTime,
			Boolean discarded) {
		List<Diagram> response = null;
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findAll(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add("ALL");
				specialities.add(null);
			}

			AggregationResults<Diagram> results = mongoTemplate.aggregate(
					DPDoctorUtils.createGlobalAggregation(page, size, updatedTime, discarded, null, null, specialities,null),
					DiagramsCollection.class, Diagram.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Diagrams");
		}
		return response;
	}

	private List<Diagram> getCustomDiagrams(int page, int size, String doctorId, String locationId, String hospitalId,
			String updatedTime, Boolean discarded) {
		List<Diagram> response = null;
		try {
			AggregationResults<Diagram> results = mongoTemplate.aggregate(DPDoctorUtils.createCustomAggregation(page,
					size, doctorId, locationId, hospitalId, updatedTime, discarded, null, null,null),
					DiagramsCollection.class, Diagram.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Diagrams");
		}
		return response;
	}

	@Override
	@Transactional
	public void emailClinicalNotes(String clinicalNotesId, String doctorId, String locationId, String hospitalId,
			String emailAddress) {
		try {
			MailResponse mailResponse = createMailData(clinicalNotesId, doctorId, locationId, hospitalId);
			String body = mailBodyGenerator.generateEMREmailBody(mailResponse.getPatientName(),
					mailResponse.getDoctorName(), mailResponse.getClinicName(), mailResponse.getClinicAddress(),
					mailResponse.getMailRecordCreatedDate(), "Clinical Notes", "emrMailTemplate.vm");
			mailService.sendEmail(emailAddress, mailResponse.getDoctorName() + " sent you Clinical Notes", body,
					mailResponse.getMailAttachment());

			if (mailResponse.getMailAttachment() != null
					&& mailResponse.getMailAttachment().getFileSystemResource() != null)
				if (mailResponse.getMailAttachment().getFileSystemResource().getFile().exists())
					mailResponse.getMailAttachment().getFileSystemResource().getFile().delete();
		} catch (Exception e) {
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}

	}

	@Override
	@Transactional
	public MailResponse getClinicalNotesMailData(String clinicalNotesId, String doctorId, String locationId,
			String hospitalId) {
		return createMailData(clinicalNotesId, doctorId, locationId, hospitalId);
	}

	private MailResponse createMailData(String clinicalNotesId, String doctorId, String locationId, String hospitalId) {
		MailResponse response = null;
		ClinicalNotesCollection clinicalNotesCollection = null;
		MailAttachment mailAttachment = null;
		PatientCollection patient = null;
		UserCollection user = null;
		EmailTrackCollection emailTrackCollection = new EmailTrackCollection();
		try {
			clinicalNotesCollection = clinicalNotesRepository.findOne(new ObjectId(clinicalNotesId));
			if (clinicalNotesCollection != null) {
				if (clinicalNotesCollection.getDoctorId() != null && clinicalNotesCollection.getHospitalId() != null
						&& clinicalNotesCollection.getLocationId() != null) {
					if (clinicalNotesCollection.getDoctorId().equals(doctorId)
							&& clinicalNotesCollection.getHospitalId().equals(hospitalId)
							&& clinicalNotesCollection.getLocationId().equals(locationId)) {

						user = userRepository.findOne(clinicalNotesCollection.getPatientId());
						patient = patientRepository.findByUserIdLocationIdAndHospitalId(
								clinicalNotesCollection.getPatientId(), 
								clinicalNotesCollection.getLocationId(), clinicalNotesCollection.getHospitalId());

						emailTrackCollection.setDoctorId(new ObjectId(doctorId));
						emailTrackCollection.setHospitalId(new ObjectId(hospitalId));
						emailTrackCollection.setLocationId(new ObjectId(locationId));
						emailTrackCollection.setType(ComponentType.CLINICAL_NOTES.getType());
						emailTrackCollection.setSubject("Clinical Notes");
						if (user != null) {
							emailTrackCollection.setPatientName(user.getFirstName());
							emailTrackCollection.setPatientId(user.getId());
						}
						JasperReportResponse jasperReportResponse = createJasper(clinicalNotesCollection, patient,
								user, null, false, false, false, false, false);
						mailAttachment = new MailAttachment();
						mailAttachment.setAttachmentName(FilenameUtils.getName(jasperReportResponse.getPath()));
						mailAttachment.setFileSystemResource(jasperReportResponse.getFileSystemResource());
						UserCollection doctorUser = userRepository.findOne(new ObjectId(doctorId));
						LocationCollection locationCollection = locationRepository.findOne(new ObjectId(locationId));

						response = new MailResponse();
						response.setMailAttachment(mailAttachment);
						response.setDoctorName(doctorUser.getTitle() + " " + doctorUser.getFirstName());
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
						response.setClinicAddress(address);
						response.setClinicName(locationCollection.getLocationName());
						SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
						sdf.setTimeZone(TimeZone.getTimeZone("IST"));
						response.setMailRecordCreatedDate(sdf.format(clinicalNotesCollection.getCreatedTime()));
						response.setPatientName(user.getFirstName());

						emailTackService.saveEmailTrack(emailTrackCollection);

					} else {
						logger.warn("Clinical Notes Id, doctorId, location Id, hospital Id does not match");
						throw new BusinessException(ServiceError.NotFound,
								"Clinical Notes Id, doctorId, location Id, hospital Id does not match");
					}
				}
			} else {
				logger.warn("Clinical Notes not found. Please check clinicalNotesId.");
				throw new BusinessException(ServiceError.NotFound,
						"Clinical Notes not found. Please check clinicalNotesId.");
			}
		} catch (BusinessException e) {
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}

		return response;
	}

	private String getFinalImageURL(String imageURL) {
		if (imageURL != null) {
			return imagePath + imageURL;
		} else
			return null;
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
	public List<ClinicalNotes> getClinicalNotes(String patientId, int page, int size, String updatedTime,
			Boolean discarded) {
		List<ClinicalNotesCollection> clinicalNotesCollections = null;
		List<ClinicalNotes> clinicalNotes = null;
		boolean[] discards = new boolean[2];
		discards[0] = false;

		boolean[] inHistorys = new boolean[2];
		inHistorys[0] = true;
		inHistorys[1] = false;

		try {
			if (discarded)
				discards[1] = true;
			long createdTimestamp = Long.parseLong(updatedTime);
			ObjectId patientObjectId = null;
			if (!DPDoctorUtils.anyStringEmpty(patientId))
				patientObjectId = new ObjectId(patientId);

			if (size > 0)
				clinicalNotesCollections = clinicalNotesRepository.getClinicalNotes(patientObjectId,
						new Date(createdTimestamp), discards, inHistorys,
						new PageRequest(page, size, Direction.DESC, "createdTime"));
			else
				clinicalNotesCollections = clinicalNotesRepository.getClinicalNotes(patientObjectId,
						new Date(createdTimestamp), discards, inHistorys, new Sort(Sort.Direction.DESC, "createdTime"));

			if (clinicalNotesCollections != null && !clinicalNotesCollections.isEmpty()) {
				clinicalNotes = new ArrayList<ClinicalNotes>();
				for (ClinicalNotesCollection clinicalNotesCollection : clinicalNotesCollections) {
					ClinicalNotes clinicalNote = getClinicalNote(clinicalNotesCollection);
					clinicalNotes.add(clinicalNote);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return clinicalNotes;
	}

	@Override
	public String getClinicalNotesFile(String clinicalNotesId, Boolean showPH, Boolean showPLH, Boolean showFH, Boolean showDA, Boolean showUSG) {
		String response = null;
		HistoryCollection historyCollection = null;
		try {
			ClinicalNotesCollection clinicalNotesCollection = clinicalNotesRepository
					.findOne(new ObjectId(clinicalNotesId));

			if (clinicalNotesCollection != null) {
				PatientCollection patient = patientRepository.findByUserIdLocationIdAndHospitalId(
						clinicalNotesCollection.getPatientId(), 
						clinicalNotesCollection.getLocationId(), clinicalNotesCollection.getHospitalId());
				UserCollection user = userRepository.findOne(clinicalNotesCollection.getPatientId());
				if(showPH || showPLH || showFH || showDA){
					historyCollection  = historyRepository.findHistory(clinicalNotesCollection.getLocationId(), clinicalNotesCollection.getHospitalId(), clinicalNotesCollection.getPatientId());
				}
				JasperReportResponse jasperReportResponse = createJasper(clinicalNotesCollection, patient, user, historyCollection, showPH, showPLH, showFH, showDA, showUSG);
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

	private JasperReportResponse createJasper(ClinicalNotesCollection clinicalNotesCollection,
			PatientCollection patient, UserCollection user, HistoryCollection historyCollection, Boolean showPH, Boolean showPLH, Boolean showFH, Boolean showDA, Boolean showUSG) throws IOException, ParseException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		JasperReportResponse response = null;
		PrintSettingsCollection printSettings = printSettingsRepository.getSettings(
				clinicalNotesCollection.getDoctorId(), clinicalNotesCollection.getLocationId(),
				clinicalNotesCollection.getHospitalId(), ComponentType.ALL.getType());
		parameters.put("observations", clinicalNotesCollection.getObservation());
		parameters.put("notes", clinicalNotesCollection.getNote());
		parameters.put("investigations", clinicalNotesCollection.getInvestigation());
		parameters.put("diagnosis", clinicalNotesCollection.getDiagnosis());
		parameters.put("complaints", clinicalNotesCollection.getComplaint());

		parameters.put("presentComplaint", clinicalNotesCollection.getPresentComplaint());
		parameters.put("presentComplaintHistory", clinicalNotesCollection.getPresentComplaintHistory());
		parameters.put("generalExam", clinicalNotesCollection.getGeneralExam());
		parameters.put("systemExam", clinicalNotesCollection.getSystemExam());
		parameters.put("menstrualHistory", clinicalNotesCollection.getMenstrualHistory());
		parameters.put("obstetricHistory", clinicalNotesCollection.getObstetricHistory());
		parameters.put("provisionalDiagnosis", clinicalNotesCollection.getProvisionalDiagnosis());

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
			parameters.put("diagrams", diagramIds);
		else
			parameters.put("diagrams", null);

		parameters.put("clinicalNotesId", clinicalNotesCollection.getId().toString());
		if (clinicalNotesCollection.getVitalSigns() != null) {
			String vitalSigns = null;

			String pulse = clinicalNotesCollection.getVitalSigns().getPulse();
			pulse = (pulse != null && !pulse.isEmpty() ? "Pulse: " + pulse.trim() + " " + VitalSignsUnit.PULSE.getUnit()
					: "");
			if (!DPDoctorUtils.allStringsEmpty(pulse))
				vitalSigns = pulse;

			String temp = clinicalNotesCollection.getVitalSigns().getTemperature();
			temp = (temp != null && !temp.isEmpty()
					? "Temperature: " + temp.trim() + " " + VitalSignsUnit.TEMPERATURE.getUnit() : "");
			if (!DPDoctorUtils.allStringsEmpty(temp)) {
				if (!DPDoctorUtils.allStringsEmpty(vitalSigns))
					vitalSigns = vitalSigns + ",  " + temp;
				else
					vitalSigns = temp;
			}

			String breathing = clinicalNotesCollection.getVitalSigns().getBreathing();
			breathing = (breathing != null && !breathing.isEmpty()
					? "Breathing: " + breathing.trim() + " " + VitalSignsUnit.BREATHING.getUnit() : "");
			if (!DPDoctorUtils.allStringsEmpty(breathing)) {
				if (!DPDoctorUtils.allStringsEmpty(vitalSigns))
					vitalSigns = vitalSigns + ",  " + breathing;
				else
					vitalSigns = breathing;
			}

			String weight = clinicalNotesCollection.getVitalSigns().getWeight();
			weight = (weight != null && !weight.isEmpty()
					? "Weight: " + weight.trim() + " " + VitalSignsUnit.WEIGHT.getUnit() : "");
			if (!DPDoctorUtils.allStringsEmpty(temp)) {
				if (!DPDoctorUtils.allStringsEmpty(vitalSigns))
					vitalSigns = vitalSigns + ",  " + weight;
				else
					vitalSigns = weight;
			}

			String bloodPressure = "";
			if (clinicalNotesCollection.getVitalSigns().getBloodPressure() != null) {
				String systolic = clinicalNotesCollection.getVitalSigns().getBloodPressure().getSystolic();
				systolic = systolic != null && !systolic.isEmpty() ? systolic.trim() : "";

				String diastolic = clinicalNotesCollection.getVitalSigns().getBloodPressure().getDiastolic();
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

			parameters.put("vitalSigns", vitalSigns != null && !vitalSigns.isEmpty() ? vitalSigns : null);
		} else
			parameters.put("vitalSigns", null);

		if (parameters.get("followUpAppointment") == null
				&& !DPDoctorUtils.anyStringEmpty(clinicalNotesCollection.getAppointmentId())
				&& clinicalNotesCollection.getTime() != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("MMM dd");
			String _24HourTime = String.format("%02d:%02d", clinicalNotesCollection.getTime().getFromTime() / 60,
					clinicalNotesCollection.getTime().getFromTime() % 60);
			SimpleDateFormat _24HourSDF = new SimpleDateFormat("HH:mm");
			SimpleDateFormat _12HourSDF = new SimpleDateFormat("hh:mm a");
			sdf.setTimeZone(TimeZone.getTimeZone("IST"));
			_24HourSDF.setTimeZone(TimeZone.getTimeZone("IST"));
			_12HourSDF.setTimeZone(TimeZone.getTimeZone("IST"));

			Date _24HourDt = _24HourSDF.parse(_24HourTime);
			String dateTime = _12HourSDF.format(_24HourDt) + ", " + sdf.format(clinicalNotesCollection.getFromDate());
			parameters.put("followUpAppointment", "Next Review on " + dateTime);
		}
		
		if(historyCollection != null){
			parameters.put("showHistory", true);
			patientVisitService.includeHistoryInPdf(historyCollection, showPH, showPLH, showFH, showDA, parameters);
		}
		patientVisitService.generatePatientDetails(
				(printSettings != null && printSettings.getHeaderSetup() != null
						? printSettings.getHeaderSetup().getPatientDetails() : null),
				patient, "<b>CID: </b>" + (clinicalNotesCollection.getUniqueEmrId() != null ? clinicalNotesCollection.getUniqueEmrId() : "--"), patient.getLocalPatientName(),
				user.getMobileNumber(), parameters);
		patientVisitService.generatePrintSetup(parameters, printSettings, clinicalNotesCollection.getDoctorId());
		String pdfName = (user != null ? user.getFirstName() : "") + "CLINICALNOTES-"
				+ clinicalNotesCollection.getUniqueEmrId() + new Date().getTime();

		String layout = printSettings != null
				? (printSettings.getPageSetup() != null ? printSettings.getPageSetup().getLayout() : "PORTRAIT")
				: "PORTRAIT";
		String pageSize = printSettings != null
				? (printSettings.getPageSetup() != null ? printSettings.getPageSetup().getPageSize() : "A4") : "A4";
		Integer topMargin = printSettings != null
				? (printSettings.getPageSetup() != null ? printSettings.getPageSetup().getTopMargin() : null) : null;
		Integer bottonMargin = printSettings != null
				? (printSettings.getPageSetup() != null ? printSettings.getPageSetup().getBottomMargin() : null) : null;
		Integer leftMargin = printSettings != null
				? (printSettings.getPageSetup() != null && printSettings.getPageSetup().getLeftMargin() != null
						? printSettings.getPageSetup().getLeftMargin() : 20)
				: 20;
		Integer rightMargin = printSettings != null
				? (printSettings.getPageSetup() != null && printSettings.getPageSetup().getRightMargin() != null
						? printSettings.getPageSetup().getRightMargin() : 20)
				: 20;
		response = jasperReportService.createPDF(ComponentType.CLINICAL_NOTES, parameters, clinicalNotesA4FileName,
				layout, pageSize, topMargin, bottonMargin, leftMargin, rightMargin,
				Integer.parseInt(parameters.get("contentFontSize").toString()), pdfName.replaceAll("\\s+", ""));
		return response;
	}

	public List<Complaint> sortComplaints(List<Complaint> complaints, List<ObjectId> complaintIds) {
		List<Complaint> response = new ArrayList<Complaint>();
		if (complaints != null && !complaints.isEmpty()) {
			for (ObjectId id : complaintIds)
				for (Complaint complaint : complaints)
					if (complaint.getId().equalsIgnoreCase(id.toString()))
						response.add(complaint);
		}
		return response;
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

	public List<Notes> sortNotes(List<Notes> mappedResults, List<ObjectId> notes) {
		List<Notes> response = new ArrayList<Notes>();
		if (mappedResults != null && !mappedResults.isEmpty()) {
			for (ObjectId id : notes)
				for (Notes note : mappedResults)
					if (note.getId().equalsIgnoreCase(id.toString()))
						response.add(note);
		}
		return response;
	}

	public List<Diagnoses> sortDiagnoses(List<Diagnoses> mappedResults, List<ObjectId> diagnoses) {
		List<Diagnoses> response = new ArrayList<Diagnoses>();
		if (mappedResults != null && !mappedResults.isEmpty()) {
			for (ObjectId id : diagnoses)
				for (Diagnoses diagnosis : mappedResults)
					if (diagnosis.getId().equalsIgnoreCase(id.toString()))
						response.add(diagnosis);
		}
		return response;
	}

	public List<Observation> sortObservations(List<Observation> mappedResults, List<ObjectId> observations) {
		List<Observation> response = new ArrayList<Observation>();
		if (mappedResults != null && !mappedResults.isEmpty()) {
			for (ObjectId id : observations)
				for (Observation observation : mappedResults)
					if (observation.getId().equalsIgnoreCase(id.toString()))
						response.add(observation);
		}
		return response;
	}

	public List<Investigation> sortInvestigations(List<Investigation> mappedResults, List<ObjectId> investigations) {
		List<Investigation> response = new ArrayList<Investigation>();
		if (mappedResults != null && !mappedResults.isEmpty()) {
			for (ObjectId id : investigations)
				for (Investigation investigation : mappedResults)
					if (investigation.getId().equalsIgnoreCase(id.toString()))
						response.add(investigation);
		}
		return response;
	}

	private Appointment addNotesAppointment(AppointmentRequest appointment) {
		Appointment response = null;
		if (appointment.getAppointmentId() == null) {
			response = appointmentService.addAppointment(appointment);
		} else {
			response = new Appointment();
			BeanUtil.map(appointment, response);
		}
		return response;
	}


	@Override
	public Boolean updateQuery() {
		Boolean response = false;
		try {
				response = true;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}
	
	@SuppressWarnings("unchecked")
	private List<PresentComplaint> getCustomGlobalPresentComplaint(int page, int size, String doctorId, String locationId, String hospitalId,
			String updatedTime, Boolean discarded) {
		List<PresentComplaint> response = new ArrayList<PresentComplaint>();
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findAll(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add(null);
				specialities.add("ALL");
			}

			AggregationResults<PresentComplaint> results = mongoTemplate
					.aggregate(
							DPDoctorUtils.createCustomGlobalAggregation(page, size, doctorId, locationId, hospitalId,
									updatedTime, discarded, null, null, specialities,null),
							PresentComplaintCollection.class, PresentComplaint.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Present Complaint");
		}
		return response;

	}

	@SuppressWarnings("unchecked")
	private List<PresentComplaint> getGlobalPresentComplaint(int page, int size, String doctorId, String updatedTime, Boolean discarded) {
		List<PresentComplaint> response = null;
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findAll(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add("ALL");
				specialities.add(null);
			}

			AggregationResults<PresentComplaint> results = mongoTemplate.aggregate(
					DPDoctorUtils.createGlobalAggregation(page, size, updatedTime, discarded, null, null, specialities,null),
					PresentComplaintCollection.class, PresentComplaint.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Present Complaint");
		}
		return response;
	}

	private List<PresentComplaint> getCustomPresentComplaint(int page, int size, String doctorId, String locationId, String hospitalId,
			String updatedTime, Boolean discarded) {
		List<PresentComplaint> response = null;
		try {
			AggregationResults<PresentComplaint> results = mongoTemplate.aggregate(DPDoctorUtils.createCustomAggregation(page,
					size, doctorId, locationId, hospitalId, updatedTime, discarded, null, null,null), PresentComplaintCollection.class,
					PresentComplaint.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Present Complaint");
		}
		return response;
	}

	

	@SuppressWarnings("unchecked")
	private List<PresentComplaintHistory> getCustomGlobalPresentComplaintHistory(int page, int size, String doctorId, String locationId, String hospitalId,
			String updatedTime, Boolean discarded) {
		List<PresentComplaintHistory> response = new ArrayList<PresentComplaintHistory>();
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findAll(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add(null);
				specialities.add("ALL");
			}

			AggregationResults<PresentComplaintHistory> results = mongoTemplate
					.aggregate(
							DPDoctorUtils.createCustomGlobalAggregation(page, size, doctorId, locationId, hospitalId,
									updatedTime, discarded, null, null, specialities,null),
							PresentComplaintHistoryCollection.class, PresentComplaintHistory.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Present Complaint History");
		}
		return response;

	}

	@SuppressWarnings("unchecked")
	private List<PresentComplaintHistory> getGlobalPresentComplaintHistory(int page, int size, String doctorId, String updatedTime, Boolean discarded) {
		List<PresentComplaintHistory> response = null;
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findAll(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add("ALL");
				specialities.add(null);
			}

			AggregationResults<PresentComplaintHistory> results = mongoTemplate.aggregate(
					DPDoctorUtils.createGlobalAggregation(page, size, updatedTime, discarded, null, null, specialities,null),
					PresentComplaintHistoryCollection.class, PresentComplaintHistory.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Present Complaint History");
		}
		return response;
	}

	private List<PresentComplaintHistory> getCustomPresentComplaintHistory(int page, int size, String doctorId, String locationId, String hospitalId,
			String updatedTime, Boolean discarded) {
		List<PresentComplaintHistory> response = null;
		try {
			AggregationResults<PresentComplaintHistory> results = mongoTemplate.aggregate(DPDoctorUtils.createCustomAggregation(page,
					size, doctorId, locationId, hospitalId, updatedTime, discarded, null, null,null), PresentComplaintHistoryCollection.class,
					PresentComplaintHistory.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Present Complaint History");
		}
		return response;
	}
	
	@SuppressWarnings("unchecked")
	private List<ProvisionalDiagnosis> getCustomGlobalProvisionalDiagnosis(int page, int size, String doctorId, String locationId, String hospitalId,
			String updatedTime, Boolean discarded) {
		List<ProvisionalDiagnosis> response = new ArrayList<ProvisionalDiagnosis>();
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findAll(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add(null);
				specialities.add("ALL");
			}

			AggregationResults<ProvisionalDiagnosis> results = mongoTemplate
					.aggregate(
							DPDoctorUtils.createCustomGlobalAggregation(page, size, doctorId, locationId, hospitalId,
									updatedTime, discarded, null, null, specialities,null),
							ProvisionalDiagnosisCollection.class, ProvisionalDiagnosis.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Provisional Diagnosis");
		}
		return response;

	}

	@SuppressWarnings("unchecked")
	private List<ProvisionalDiagnosis> getGlobalProvisionalDiagnosis(int page, int size, String doctorId, String updatedTime, Boolean discarded) {
		List<ProvisionalDiagnosis> response = null;
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findAll(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add("ALL");
				specialities.add(null);
			}

			AggregationResults<ProvisionalDiagnosis> results = mongoTemplate.aggregate(
					DPDoctorUtils.createGlobalAggregation(page, size, updatedTime, discarded, null, null, specialities,null),
					ProvisionalDiagnosisCollection.class, ProvisionalDiagnosis.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Provisional Diagnosis");
		}
		return response;
	}

	private List<ProvisionalDiagnosis> getCustomProvisionalDiagnosis(int page, int size, String doctorId, String locationId, String hospitalId,
			String updatedTime, Boolean discarded) {
		List<ProvisionalDiagnosis> response = null;
		try {
			AggregationResults<ProvisionalDiagnosis> results = mongoTemplate.aggregate(DPDoctorUtils.createCustomAggregation(page,
					size, doctorId, locationId, hospitalId, updatedTime, discarded, null, null,null), ProvisionalDiagnosisCollection.class,
					ProvisionalDiagnosis.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Provisional Diagnosis");
		}
		return response;
	}
	
	
	@SuppressWarnings("unchecked")
	private List<GeneralExam> getCustomGlobalGeneralExam(int page, int size, String doctorId, String locationId, String hospitalId,
			String updatedTime, Boolean discarded) {
		List<GeneralExam> response = new ArrayList<GeneralExam>();
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findAll(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add(null);
				specialities.add("ALL");
			}

			AggregationResults<GeneralExam> results = mongoTemplate
					.aggregate(
							DPDoctorUtils.createCustomGlobalAggregation(page, size, doctorId, locationId, hospitalId,
									updatedTime, discarded, null, null, specialities,null),
							GeneralExamCollection.class, GeneralExam.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting General Exam");
		}
		return response;

	}

	@SuppressWarnings("unchecked")
	private List<GeneralExam> getGlobalGeneralExam(int page, int size, String doctorId, String updatedTime, Boolean discarded) {
		List<GeneralExam> response = null;
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findAll(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add("ALL");
				specialities.add(null);
			}

			AggregationResults<GeneralExam> results = mongoTemplate.aggregate(
					DPDoctorUtils.createGlobalAggregation(page, size, updatedTime, discarded, null, null, specialities,null),
					GeneralExamCollection.class, GeneralExam.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting General Exam");
		}
		return response;
	}

	private List<GeneralExam> getCustomGeneralExam(int page, int size, String doctorId, String locationId, String hospitalId,
			String updatedTime, Boolean discarded) {
		List<GeneralExam> response = null;
		try {
			AggregationResults<GeneralExam> results = mongoTemplate.aggregate(DPDoctorUtils.createCustomAggregation(page,
					size, doctorId, locationId, hospitalId, updatedTime, discarded, null, null,null), GeneralExamCollection.class,
					GeneralExam.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting General Exam");
		}
		return response;
	}


	@SuppressWarnings("unchecked")
	private List<SystemExam> getCustomGlobalSystemExam(int page, int size, String doctorId, String locationId, String hospitalId,
			String updatedTime, Boolean discarded) {
		List<SystemExam> response = new ArrayList<SystemExam>();
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findAll(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add(null);
				specialities.add("ALL");
			}

			AggregationResults<SystemExam> results = mongoTemplate
					.aggregate(
							DPDoctorUtils.createCustomGlobalAggregation(page, size, doctorId, locationId, hospitalId,
									updatedTime, discarded, null, null, specialities,null),
							SystemExamCollection.class, SystemExam.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting System Exam");
		}
		return response;

	}

	@SuppressWarnings("unchecked")
	private List<SystemExam> getGlobalSystemExam(int page, int size, String doctorId, String updatedTime, Boolean discarded) {
		List<SystemExam> response = null;
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findAll(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add("ALL");
				specialities.add(null);
			}

			AggregationResults<SystemExam> results = mongoTemplate.aggregate(
					DPDoctorUtils.createGlobalAggregation(page, size, updatedTime, discarded, null, null, specialities,null),
					SystemExamCollection.class, SystemExam.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting System Exam");
		}
		return response;
	}

	private List<SystemExam> getCustomSystemExam(int page, int size, String doctorId, String locationId, String hospitalId,
			String updatedTime, Boolean discarded) {
		List<SystemExam> response = null;
		try {
			AggregationResults<SystemExam> results = mongoTemplate.aggregate(DPDoctorUtils.createCustomAggregation(page,
					size, doctorId, locationId, hospitalId, updatedTime, discarded, null, null,null), SystemExamCollection.class,
					SystemExam.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting System Exam");
		}
		return response;
	}
	
	@SuppressWarnings("unchecked")
	private List<MenstrualHistory> getCustomGlobalMenstrualHistory(int page, int size, String doctorId, String locationId, String hospitalId,
			String updatedTime, Boolean discarded) {
		List<MenstrualHistory> response = new ArrayList<MenstrualHistory>();
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findAll(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add(null);
				specialities.add("ALL");
			}

			AggregationResults<MenstrualHistory> results = mongoTemplate
					.aggregate(
							DPDoctorUtils.createCustomGlobalAggregation(page, size, doctorId, locationId, hospitalId,
									updatedTime, discarded, null, null, specialities,null),
							MenstrualHistoryCollection.class, MenstrualHistory.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Menstrual History");
		}
		return response;

	}

	@SuppressWarnings("unchecked")
	private List<MenstrualHistory> getGlobalMenstrualHistory(int page, int size, String doctorId, String updatedTime, Boolean discarded) {
		List<MenstrualHistory> response = null;
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findAll(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add("ALL");
				specialities.add(null);
			}

			AggregationResults<MenstrualHistory> results = mongoTemplate.aggregate(
					DPDoctorUtils.createGlobalAggregation(page, size, updatedTime, discarded, null, null, specialities,null),
					MenstrualHistoryCollection.class, MenstrualHistory.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Menstrual History");
		}
		return response;
	}

	private List<MenstrualHistory> getCustomMenstrualHistory(int page, int size, String doctorId, String locationId, String hospitalId,
			String updatedTime, Boolean discarded) {
		List<MenstrualHistory> response = null;
		try {
			AggregationResults<MenstrualHistory> results = mongoTemplate.aggregate(DPDoctorUtils.createCustomAggregation(page,
					size, doctorId, locationId, hospitalId, updatedTime, discarded, null, null,null), MenstrualHistoryCollection.class,
					MenstrualHistory.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Menstrual History");
		}
		return response;
	}

	
	@SuppressWarnings("unchecked")
	private List<ObstetricHistory> getCustomGlobalObstetricHistory(int page, int size, String doctorId, String locationId, String hospitalId,
			String updatedTime, Boolean discarded) {
		List<ObstetricHistory> response = new ArrayList<ObstetricHistory>();
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findAll(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add(null);
				specialities.add("ALL");
			}

			AggregationResults<ObstetricHistory> results = mongoTemplate
					.aggregate(
							DPDoctorUtils.createCustomGlobalAggregation(page, size, doctorId, locationId, hospitalId,
									updatedTime, discarded, null, null, specialities,null),
							ObstetricHistoryCollection.class, ObstetricHistory.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Obstetric History");
		}
		return response;

	}

	@SuppressWarnings("unchecked")
	private List<ObstetricHistory> getGlobalObstetricHistory(int page, int size, String doctorId, String updatedTime, Boolean discarded) {
		List<ObstetricHistory> response = null;
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findAll(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add("ALL");
				specialities.add(null);
			}

			AggregationResults<ObstetricHistory> results = mongoTemplate.aggregate(
					DPDoctorUtils.createGlobalAggregation(page, size, updatedTime, discarded, null, null, specialities,null),
					ObstetricHistoryCollection.class, ObstetricHistory.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Menstrual History");
		}
		return response;
	}

	private List<ObstetricHistory> getCustomObstetricHistory(int page, int size, String doctorId, String locationId, String hospitalId,
			String updatedTime, Boolean discarded) {
		List<ObstetricHistory> response = null;
		try {
			AggregationResults<ObstetricHistory> results = mongoTemplate.aggregate(DPDoctorUtils.createCustomAggregation(page,
					size, doctorId, locationId, hospitalId, updatedTime, discarded, null, null,null), ObstetricHistoryCollection.class,
					ObstetricHistory.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Obstetric History");
		}
		return response;
	}
	
	@Override
	@Transactional
	public ProvisionalDiagnosis deleteProvisionalDiagnosis(String id, String doctorId, String locationId, String hospitalId,
			Boolean discarded) {
		ProvisionalDiagnosis response = null;
		try {
			ProvisionalDiagnosisCollection provisionalDiagnosisCollection = provisionalDiagnosisRepository.findOne(new ObjectId(id));
			if (provisionalDiagnosisCollection != null) {
				if (!DPDoctorUtils.anyStringEmpty(provisionalDiagnosisCollection.getDoctorId(),
						provisionalDiagnosisCollection.getHospitalId(), provisionalDiagnosisCollection.getLocationId())) {
					if (provisionalDiagnosisCollection.getDoctorId().toString().equals(doctorId)
							&& provisionalDiagnosisCollection.getHospitalId().toString().equals(hospitalId)
							&& provisionalDiagnosisCollection.getLocationId().toString().equals(locationId)) {

						provisionalDiagnosisCollection.setDiscarded(discarded);
						provisionalDiagnosisCollection.setUpdatedTime(new Date());
						provisionalDiagnosisRepository.save(provisionalDiagnosisCollection);
						response = new ProvisionalDiagnosis();
						BeanUtil.map(provisionalDiagnosisCollection, response);
					} else {
						logger.warn("Invalid Doctor Id, Hospital Id, Or Location Id");
						throw new BusinessException(ServiceError.InvalidInput,
								"Invalid Doctor Id, Hospital Id, Or Location Id");
					}
				} else {
					provisionalDiagnosisCollection.setDiscarded(discarded);
					provisionalDiagnosisCollection.setUpdatedTime(new Date());
					provisionalDiagnosisRepository.save(provisionalDiagnosisCollection);
					response = new ProvisionalDiagnosis();
					BeanUtil.map(provisionalDiagnosisCollection, response);
				}
			} else {
				logger.warn("Provisional Diagnosis not found!");
				throw new BusinessException(ServiceError.NoRecord, "Provisional Diagnosis not found!");
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
	public GeneralExam deleteGeneralExam(String id, String doctorId, String locationId, String hospitalId,
			Boolean discarded) {
		GeneralExam response = null;
		try {
			GeneralExamCollection generalExamCollection = generalExamRepository.findOne(new ObjectId(id));
			if (generalExamCollection != null) {
				if (!DPDoctorUtils.anyStringEmpty(generalExamCollection.getDoctorId(),
						generalExamCollection.getHospitalId(), generalExamCollection.getLocationId())) {
					if (generalExamCollection.getDoctorId().toString().equals(doctorId)
							&& generalExamCollection.getHospitalId().toString().equals(hospitalId)
							&& generalExamCollection.getLocationId().toString().equals(locationId)) {

						generalExamCollection.setDiscarded(discarded);
						generalExamCollection.setUpdatedTime(new Date());
						generalExamRepository.save(generalExamCollection);
						response = new GeneralExam();
						BeanUtil.map(generalExamCollection, response);
					} else {
						logger.warn("Invalid Doctor Id, Hospital Id, Or Location Id");
						throw new BusinessException(ServiceError.InvalidInput,
								"Invalid Doctor Id, Hospital Id, Or Location Id");
					}
				} else {
					generalExamCollection.setDiscarded(discarded);
					generalExamCollection.setUpdatedTime(new Date());
					generalExamRepository.save(generalExamCollection);
					response = new GeneralExam();
					BeanUtil.map(generalExamCollection, response);
				}
			} else {
				logger.warn("General Exam not found!");
				throw new BusinessException(ServiceError.NoRecord, "General Exam not found!");
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
	public PresentComplaintHistory deletePresentComplaintHistory(String id, String doctorId, String locationId, String hospitalId,
			Boolean discarded) {
		PresentComplaintHistory response = null;
		try {
			PresentComplaintHistoryCollection presentComplaintHistoryCollection = presentComplaintHistoryRepository.findOne(new ObjectId(id));
			if (presentComplaintHistoryCollection != null) {
				if (!DPDoctorUtils.anyStringEmpty(presentComplaintHistoryCollection.getDoctorId(),
						presentComplaintHistoryCollection.getHospitalId(), presentComplaintHistoryCollection.getLocationId())) {
					if (presentComplaintHistoryCollection.getDoctorId().toString().equals(doctorId)
							&& presentComplaintHistoryCollection.getHospitalId().toString().equals(hospitalId)
							&& presentComplaintHistoryCollection.getLocationId().toString().equals(locationId)) {

						presentComplaintHistoryCollection.setDiscarded(discarded);
						presentComplaintHistoryCollection.setUpdatedTime(new Date());
						presentComplaintHistoryRepository.save(presentComplaintHistoryCollection);
						response = new PresentComplaintHistory();
						BeanUtil.map(presentComplaintHistoryCollection, response);
					} else {
						logger.warn("Invalid Doctor Id, Hospital Id, Or Location Id");
						throw new BusinessException(ServiceError.InvalidInput,
								"Invalid Doctor Id, Hospital Id, Or Location Id");
					}
				} else {
					presentComplaintHistoryCollection.setDiscarded(discarded);
					presentComplaintHistoryCollection.setUpdatedTime(new Date());
					presentComplaintHistoryRepository.save(presentComplaintHistoryCollection);
					response = new PresentComplaintHistory();
					BeanUtil.map(presentComplaintHistoryCollection, response);
				}
			} else {
				logger.warn("Present Complaint History not found!");
				throw new BusinessException(ServiceError.NoRecord, "Present Complaint History not found!");
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
	public SystemExam deleteSystemExam(String id, String doctorId, String locationId, String hospitalId,
			Boolean discarded) {
		SystemExam response = null;
		try {
			SystemExamCollection systemExamCollection = systemExamRepository.findOne(new ObjectId(id));
			if (systemExamCollection != null) {
				if (!DPDoctorUtils.anyStringEmpty(systemExamCollection.getDoctorId(),
						systemExamCollection.getHospitalId(), systemExamCollection.getLocationId())) {
					if (systemExamCollection.getDoctorId().toString().equals(doctorId)
							&& systemExamCollection.getHospitalId().toString().equals(hospitalId)
							&& systemExamCollection.getLocationId().toString().equals(locationId)) {

						systemExamCollection.setDiscarded(discarded);
						systemExamCollection.setUpdatedTime(new Date());
						systemExamRepository.save(systemExamCollection);
						response = new SystemExam();
						BeanUtil.map(systemExamCollection, response);
					} else {
						logger.warn("Invalid Doctor Id, Hospital Id, Or Location Id");
						throw new BusinessException(ServiceError.InvalidInput,
								"Invalid Doctor Id, Hospital Id, Or Location Id");
					}
				} else {
					systemExamCollection.setDiscarded(discarded);
					systemExamCollection.setUpdatedTime(new Date());
					systemExamRepository.save(systemExamCollection);
					response = new SystemExam();
					BeanUtil.map(systemExamCollection, response);
				}
			} else {
				logger.warn("System Exam not found!");
				throw new BusinessException(ServiceError.NoRecord, "System Exam not found!");
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
	public PresentComplaint deletePresentComplaint(String id, String doctorId, String locationId, String hospitalId,
			Boolean discarded) {
		PresentComplaint response = null;
		try {
			PresentComplaintCollection presentComplaintCollection = presentComplaintRepository.findOne(new ObjectId(id));
			if (presentComplaintCollection != null) {
				if (!DPDoctorUtils.anyStringEmpty(presentComplaintCollection.getDoctorId(),
						presentComplaintCollection.getHospitalId(), presentComplaintCollection.getLocationId())) {
					if (presentComplaintCollection.getDoctorId().toString().equals(doctorId)
							&& presentComplaintCollection.getHospitalId().toString().equals(hospitalId)
							&& presentComplaintCollection.getLocationId().toString().equals(locationId)) {

						presentComplaintCollection.setDiscarded(discarded);
						presentComplaintCollection.setUpdatedTime(new Date());
						presentComplaintRepository.save(presentComplaintCollection);
						response = new PresentComplaint();
						BeanUtil.map(presentComplaintCollection, response);
					} else {
						logger.warn("Invalid Doctor Id, Hospital Id, Or Location Id");
						throw new BusinessException(ServiceError.InvalidInput,
								"Invalid Doctor Id, Hospital Id, Or Location Id");
					}
				} else {
					presentComplaintCollection.setDiscarded(discarded);
					presentComplaintCollection.setUpdatedTime(new Date());
					presentComplaintRepository.save(presentComplaintCollection);
					response = new PresentComplaint();
					BeanUtil.map(presentComplaintCollection, response);
				}
			} else {
				logger.warn("Present Complaint not found!");
				throw new BusinessException(ServiceError.NoRecord, "Present Complaint not found!");
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
	public ObstetricHistory deleteObstetricHistory(String id, String doctorId, String locationId, String hospitalId,
			Boolean discarded) {
		ObstetricHistory response = null;
		try {
			ObstetricHistoryCollection obstetricHistoryCollection = obstetricHistoryRepository.findOne(new ObjectId(id));
			if (obstetricHistoryCollection != null) {
				if (!DPDoctorUtils.anyStringEmpty(obstetricHistoryCollection.getDoctorId(),
						obstetricHistoryCollection.getHospitalId(), obstetricHistoryCollection.getLocationId())) {
					if (obstetricHistoryCollection.getDoctorId().toString().equals(doctorId)
							&& obstetricHistoryCollection.getHospitalId().toString().equals(hospitalId)
							&& obstetricHistoryCollection.getLocationId().toString().equals(locationId)) {

						obstetricHistoryCollection.setDiscarded(discarded);
						obstetricHistoryCollection.setUpdatedTime(new Date());
						obstetricHistoryRepository.save(obstetricHistoryCollection);
						response = new ObstetricHistory();
						BeanUtil.map(obstetricHistoryCollection, response);
					} else {
						logger.warn("Invalid Doctor Id, Hospital Id, Or Location Id");
						throw new BusinessException(ServiceError.InvalidInput,
								"Invalid Doctor Id, Hospital Id, Or Location Id");
					}
				} else {
					obstetricHistoryCollection.setDiscarded(discarded);
					obstetricHistoryCollection.setUpdatedTime(new Date());
					obstetricHistoryRepository.save(obstetricHistoryCollection);
					response = new ObstetricHistory();
					BeanUtil.map(obstetricHistoryCollection, response);
				}
			} else {
				logger.warn("Obstetric History not found!");
				throw new BusinessException(ServiceError.NoRecord, "Obstetric History not found!");
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
	public MenstrualHistory deleteMenstrualHistory(String id, String doctorId, String locationId, String hospitalId,
			Boolean discarded) {
		MenstrualHistory response = null;
		try {
			MenstrualHistoryCollection menstrualHistoryCollection = menstrualHistoryRepository.findOne(new ObjectId(id));
			if (menstrualHistoryCollection != null) {
				if (!DPDoctorUtils.anyStringEmpty(menstrualHistoryCollection.getDoctorId(),
						menstrualHistoryCollection.getHospitalId(), menstrualHistoryCollection.getLocationId())) {
					if (menstrualHistoryCollection.getDoctorId().toString().equals(doctorId)
							&& menstrualHistoryCollection.getHospitalId().toString().equals(hospitalId)
							&& menstrualHistoryCollection.getLocationId().toString().equals(locationId)) {

						menstrualHistoryCollection.setDiscarded(discarded);
						menstrualHistoryCollection.setUpdatedTime(new Date());
						menstrualHistoryRepository.save(menstrualHistoryCollection);
						response = new MenstrualHistory();
						BeanUtil.map(menstrualHistoryCollection, response);
					} else {
						logger.warn("Invalid Doctor Id, Hospital Id, Or Location Id");
						throw new BusinessException(ServiceError.InvalidInput,
								"Invalid Doctor Id, Hospital Id, Or Location Id");
					}
				} else {
					menstrualHistoryCollection.setDiscarded(discarded);
					menstrualHistoryCollection.setUpdatedTime(new Date());
					menstrualHistoryRepository.save(menstrualHistoryCollection);
					response = new MenstrualHistory();
					BeanUtil.map(menstrualHistoryCollection, response);
				}
			} else {
				logger.warn("Menstrual History not found!");
				throw new BusinessException(ServiceError.NoRecord, "Menstrual History not found!");
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
	public IndicationOfUSG addEditIndicationOfUSG(IndicationOfUSG indicationOfUSG) {
		try {
			IndicationOfUSGCollection indicationOfUSGCollection = new IndicationOfUSGCollection();
			BeanUtil.map(indicationOfUSG, indicationOfUSGCollection);
			if (DPDoctorUtils.anyStringEmpty(indicationOfUSGCollection.getId())) {
				indicationOfUSGCollection.setCreatedTime(new Date());
				if (!DPDoctorUtils.anyStringEmpty(indicationOfUSGCollection.getDoctorId())) {
					UserCollection userCollection = userRepository.findOne(indicationOfUSGCollection.getDoctorId());
					if (userCollection != null) {
						indicationOfUSGCollection
								.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
										+ userCollection.getFirstName());
					}
				} else {
					indicationOfUSGCollection.setCreatedBy("ADMIN");
				}
			} else {
				IndicationOfUSGCollection oldIndicationOfUSGCollection= indicationOfUSGRepository
						.findOne(indicationOfUSGCollection.getId());
				indicationOfUSGCollection.setCreatedBy(oldIndicationOfUSGCollection.getCreatedBy());
				indicationOfUSGCollection.setCreatedTime(oldIndicationOfUSGCollection.getCreatedTime());
				indicationOfUSGCollection.setDiscarded(oldIndicationOfUSGCollection.getDiscarded());
			}
			indicationOfUSGCollection = indicationOfUSGRepository.save(indicationOfUSGCollection);

			BeanUtil.map(indicationOfUSGCollection, indicationOfUSG);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return indicationOfUSG;
	}

	@Override
	public IndicationOfUSG deleteIndicationOfUSG(String id, String doctorId, String locationId, String hospitalId,
			Boolean discarded) {
		IndicationOfUSG response = null;
		try {
			IndicationOfUSGCollection indicationOfUSGCollection = indicationOfUSGRepository.findOne(new ObjectId(id));
			if (indicationOfUSGCollection != null) {
				if (!DPDoctorUtils.anyStringEmpty(indicationOfUSGCollection.getDoctorId(),
						indicationOfUSGCollection.getHospitalId(), indicationOfUSGCollection.getLocationId())) {
					if (indicationOfUSGCollection.getDoctorId().toString().equals(doctorId)
							&& indicationOfUSGCollection.getHospitalId().toString().equals(hospitalId)
							&& indicationOfUSGCollection.getLocationId().toString().equals(locationId)) {

						indicationOfUSGCollection.setDiscarded(discarded);
						indicationOfUSGCollection.setUpdatedTime(new Date());
						indicationOfUSGRepository.save(indicationOfUSGCollection);
						response = new IndicationOfUSG();
						BeanUtil.map(indicationOfUSGCollection, response);
					} else {
						logger.warn("Invalid Doctor Id, Hospital Id, Or Location Id");
						throw new BusinessException(ServiceError.InvalidInput,
								"Invalid Doctor Id, Hospital Id, Or Location Id");
					}
				} else {
					indicationOfUSGCollection.setDiscarded(discarded);
					indicationOfUSGCollection.setUpdatedTime(new Date());
					indicationOfUSGRepository.save(indicationOfUSGCollection);
					response = new IndicationOfUSG();
					BeanUtil.map(indicationOfUSGCollection, response);
				}
			} else {
				logger.warn("Indication of USG not found!");
				throw new BusinessException(ServiceError.NoRecord, "Indication of USG not found!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}
	
	@SuppressWarnings("unchecked")
	private List<IndicationOfUSG> getCustomGlobalIndicationOfUSG(int page, int size, String doctorId, String locationId, String hospitalId,
			String updatedTime, Boolean discarded) {
		List<IndicationOfUSG> response = new ArrayList<IndicationOfUSG>();
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findAll(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add(null);
				specialities.add("ALL");
			}

			AggregationResults<IndicationOfUSG> results = mongoTemplate
					.aggregate(
							DPDoctorUtils.createCustomGlobalAggregation(page, size, doctorId, locationId, hospitalId,
									updatedTime, discarded, null, null, specialities,null),
							IndicationOfUSGCollection.class, IndicationOfUSG.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Indication Of USG");
		}
		return response;

	}

	@SuppressWarnings("unchecked")
	private List<IndicationOfUSG> getGlobalIndicationOfUSG(int page, int size, String doctorId, String updatedTime, Boolean discarded) {
		List<IndicationOfUSG> response = null;
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findAll(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add("ALL");
				specialities.add(null);
			}

			AggregationResults<IndicationOfUSG> results = mongoTemplate.aggregate(
					DPDoctorUtils.createGlobalAggregation(page, size, updatedTime, discarded, null, null, specialities,null),
					IndicationOfUSGCollection.class, IndicationOfUSG.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Indication of USG");
		}
		return response;
	}

	private List<IndicationOfUSG> getCustomIndicationOfUSG(int page, int size, String doctorId, String locationId, String hospitalId,
			String updatedTime, Boolean discarded) {
		List<IndicationOfUSG> response = null;
		try {
			AggregationResults<IndicationOfUSG> results = mongoTemplate.aggregate(DPDoctorUtils.createCustomAggregation(page,
					size, doctorId, locationId, hospitalId, updatedTime, discarded, null, null,null), IndicationOfUSGCollection.class,
					IndicationOfUSG.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Indication of USG");
		}
		return response;
	}
	
	
	@Override
	@Transactional
	public PV addEditPV(PV pv) {
		try {
			PVCollection pvCollection = new PVCollection();
			BeanUtil.map(pv, pvCollection);
			if (DPDoctorUtils.anyStringEmpty(pvCollection.getId())) {
				pvCollection.setCreatedTime(new Date());
				if (!DPDoctorUtils.anyStringEmpty(pvCollection.getDoctorId())) {
					UserCollection userCollection = userRepository.findOne(pvCollection.getDoctorId());
					if (userCollection != null) {
						pvCollection
								.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
										+ userCollection.getFirstName());
					}
				} else {
					pvCollection.setCreatedBy("ADMIN");
				}
			} else {
				PVCollection oldPVCollecion= pvRepository
						.findOne(pvCollection.getId());
				pvCollection.setCreatedBy(oldPVCollecion.getCreatedBy());
				pvCollection.setCreatedTime(oldPVCollecion.getCreatedTime());
				pvCollection.setDiscarded(oldPVCollecion.getDiscarded());
			}
			pvCollection = pvRepository.save(pvCollection);

			BeanUtil.map(pvCollection, pv);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return pv;
	}

	@Override
	public PV deletePV(String id, String doctorId, String locationId, String hospitalId,
			Boolean discarded) {
		PV response = null;
		try {
			PVCollection pvCollection = pvRepository.findOne(new ObjectId(id));
			if (pvCollection != null) {
				if (!DPDoctorUtils.anyStringEmpty(pvCollection.getDoctorId(),
						pvCollection.getHospitalId(), pvCollection.getLocationId())) {
					if (pvCollection.getDoctorId().toString().equals(doctorId)
							&& pvCollection.getHospitalId().toString().equals(hospitalId)
							&& pvCollection.getLocationId().toString().equals(locationId)) {

						pvCollection.setDiscarded(discarded);
						pvCollection.setUpdatedTime(new Date());
						pvRepository.save(pvCollection);
						response = new PV();
						BeanUtil.map(pvCollection, response);
					} else {
						logger.warn("Invalid Doctor Id, Hospital Id, Or Location Id");
						throw new BusinessException(ServiceError.InvalidInput,
								"Invalid Doctor Id, Hospital Id, Or Location Id");
					}
				} else {
					pvCollection.setDiscarded(discarded);
					pvCollection.setUpdatedTime(new Date());
					pvRepository.save(pvCollection);
					response = new PV();
					BeanUtil.map(pvCollection, response);
				}
			} else {
				logger.warn("P/V not found!");
				throw new BusinessException(ServiceError.NoRecord, "P/V not found!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}
	
	@SuppressWarnings("unchecked")
	private List<PV> getCustomGlobalPV(int page, int size, String doctorId, String locationId, String hospitalId,
			String updatedTime, Boolean discarded) {
		List<PV> response = new ArrayList<PV>();
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findAll(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add(null);
				specialities.add("ALL");
			}

			AggregationResults<PV> results = mongoTemplate
					.aggregate(
							DPDoctorUtils.createCustomGlobalAggregation(page, size, doctorId, locationId, hospitalId,
									updatedTime, discarded, null, null, specialities,null),
							PVCollection.class, PV.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting P/V");
		}
		return response;

	}

	@SuppressWarnings("unchecked")
	private List<PV> getGlobalPV(int page, int size, String doctorId, String updatedTime, Boolean discarded) {
		List<PV> response = null;
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findAll(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add("ALL");
				specialities.add(null);
			}

			AggregationResults<PV> results = mongoTemplate.aggregate(
					DPDoctorUtils.createGlobalAggregation(page, size, updatedTime, discarded, null, null, specialities,null),
					PVCollection.class, PV.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting P/V");
		}
		return response;
	}

	private List<PV> getCustomPV(int page, int size, String doctorId, String locationId, String hospitalId,
			String updatedTime, Boolean discarded) {
		List<PV> response = null;
		try {
			AggregationResults<PV> results = mongoTemplate.aggregate(DPDoctorUtils.createCustomAggregation(page,
					size, doctorId, locationId, hospitalId, updatedTime, discarded, null, null,null), PVCollection.class,
					PV.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting P/V");
		}
		return response;
	}
	
	@Override
	@Transactional
	public PA addEditPA(PA pa) {
		try {
			PACollection paCollection = new PACollection();
			BeanUtil.map(pa, paCollection);
			if (DPDoctorUtils.anyStringEmpty(paCollection.getId())) {
				paCollection.setCreatedTime(new Date());
				if (!DPDoctorUtils.anyStringEmpty(paCollection.getDoctorId())) {
					UserCollection userCollection = userRepository.findOne(paCollection.getDoctorId());
					if (userCollection != null) {
						paCollection
								.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
										+ userCollection.getFirstName());
					}
				} else {
					paCollection.setCreatedBy("ADMIN");
				}
			} else {
				PACollection oldPACollecion= paRepository
						.findOne(paCollection.getId());
				paCollection.setCreatedBy(oldPACollecion.getCreatedBy());
				paCollection.setCreatedTime(oldPACollecion.getCreatedTime());
				paCollection.setDiscarded(oldPACollecion.getDiscarded());
			}
			paCollection = paRepository.save(paCollection);

			BeanUtil.map(paCollection, pa);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return pa;
	}

	@Override
	public PA deletePA(String id, String doctorId, String locationId, String hospitalId,
			Boolean discarded) {
		PA response = null;
		try {
			PACollection paCollection = paRepository.findOne(new ObjectId(id));
			if (paCollection != null) {
				if (!DPDoctorUtils.anyStringEmpty(paCollection.getDoctorId(),
						paCollection.getHospitalId(), paCollection.getLocationId())) {
					if (paCollection.getDoctorId().toString().equals(doctorId)
							&& paCollection.getHospitalId().toString().equals(hospitalId)
							&& paCollection.getLocationId().toString().equals(locationId)) {

						paCollection.setDiscarded(discarded);
						paCollection.setUpdatedTime(new Date());
						paRepository.save(paCollection);
						response = new PA();
						BeanUtil.map(paCollection, response);
					} else {
						logger.warn("Invalid Doctor Id, Hospital Id, Or Location Id");
						throw new BusinessException(ServiceError.InvalidInput,
								"Invalid Doctor Id, Hospital Id, Or Location Id");
					}
				} else {
					paCollection.setDiscarded(discarded);
					paCollection.setUpdatedTime(new Date());
					paRepository.save(paCollection);
					response = new PA();
					BeanUtil.map(paCollection, response);
				}
			} else {
				logger.warn("Indication of USG not found!");
				throw new BusinessException(ServiceError.NoRecord, "P/A not found!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}
	
	@SuppressWarnings("unchecked")
	private List<PA> getCustomGlobalPA(int page, int size, String doctorId, String locationId, String hospitalId,
			String updatedTime, Boolean discarded) {
		List<PA> response = new ArrayList<PA>();
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findAll(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add(null);
				specialities.add("ALL");
			}

			AggregationResults<PA> results = mongoTemplate
					.aggregate(
							DPDoctorUtils.createCustomGlobalAggregation(page, size, doctorId, locationId, hospitalId,
									updatedTime, discarded, null, null, specialities,null),
							PACollection.class, PA.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting P/A");
		}
		return response;

	}

	@SuppressWarnings("unchecked")
	private List<PA> getGlobalPA(int page, int size, String doctorId, String updatedTime, Boolean discarded) {
		List<PA> response = null;
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findAll(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add("ALL");
				specialities.add(null);
			}

			AggregationResults<PA> results = mongoTemplate.aggregate(
					DPDoctorUtils.createGlobalAggregation(page, size, updatedTime, discarded, null, null, specialities,null),
					PACollection.class, PA.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting P/A");
		}
		return response;
	}

	private List<PA> getCustomPA(int page, int size, String doctorId, String locationId, String hospitalId,
			String updatedTime, Boolean discarded) {
		List<PA> response = null;
		try {
			AggregationResults<PA> results = mongoTemplate.aggregate(DPDoctorUtils.createCustomAggregation(page,
					size, doctorId, locationId, hospitalId, updatedTime, discarded, null, null,null), PACollection.class,
					PA.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting P/A");
		}
		return response;
	}

	
}
