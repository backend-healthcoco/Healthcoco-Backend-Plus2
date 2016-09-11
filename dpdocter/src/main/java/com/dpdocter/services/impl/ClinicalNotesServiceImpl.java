package com.dpdocter.services.impl;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.Age;
import com.dpdocter.beans.ClinicalNotes;
import com.dpdocter.beans.ClinicalNotesComplaint;
import com.dpdocter.beans.ClinicalNotesDiagnosis;
import com.dpdocter.beans.ClinicalNotesInvestigation;
import com.dpdocter.beans.ClinicalNotesNote;
import com.dpdocter.beans.ClinicalNotesObservation;
import com.dpdocter.beans.Complaint;
import com.dpdocter.beans.Diagnoses;
import com.dpdocter.beans.Diagram;
import com.dpdocter.beans.Investigation;
import com.dpdocter.beans.MailAttachment;
import com.dpdocter.beans.Notes;
import com.dpdocter.beans.Observation;
import com.dpdocter.beans.PatientDetails;
import com.dpdocter.beans.PrintSettingsText;
import com.dpdocter.collections.ClinicalNotesCollection;
import com.dpdocter.collections.ComplaintCollection;
import com.dpdocter.collections.DiagnosisCollection;
import com.dpdocter.collections.DiagramsCollection;
import com.dpdocter.collections.DoctorCollection;
import com.dpdocter.collections.EmailTrackCollection;
import com.dpdocter.collections.InvestigationCollection;
import com.dpdocter.collections.LocationCollection;
import com.dpdocter.collections.NotesCollection;
import com.dpdocter.collections.ObservationCollection;
import com.dpdocter.collections.PatientCollection;
import com.dpdocter.collections.PatientVisitCollection;
import com.dpdocter.collections.PrintSettingsCollection;
import com.dpdocter.collections.ReferencesCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.elasticsearch.document.ESComplaintsDocument;
import com.dpdocter.elasticsearch.document.ESDiagnosesDocument;
import com.dpdocter.elasticsearch.document.ESInvestigationsDocument;
import com.dpdocter.elasticsearch.document.ESNotesDocument;
import com.dpdocter.elasticsearch.document.ESObservationsDocument;
import com.dpdocter.elasticsearch.services.ESClinicalNotesService;
import com.dpdocter.enums.ClinicalItems;
import com.dpdocter.enums.ComponentType;
import com.dpdocter.enums.FONTSTYLE;
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
import com.dpdocter.repository.InvestigationRepository;
import com.dpdocter.repository.LocationRepository;
import com.dpdocter.repository.NotesRepository;
import com.dpdocter.repository.ObservationRepository;
import com.dpdocter.repository.PatientRepository;
import com.dpdocter.repository.PatientVisitRepository;
import com.dpdocter.repository.PrintSettingsRepository;
import com.dpdocter.repository.ReferenceRepository;
import com.dpdocter.repository.SpecialityRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.request.ClinicalNotesAddRequest;
import com.dpdocter.request.ClinicalNotesEditRequest;
import com.dpdocter.response.ImageURLResponse;
import com.dpdocter.response.JasperReportResponse;
import com.dpdocter.response.MailResponse;
import com.dpdocter.services.ClinicalNotesService;
import com.dpdocter.services.EmailTackService;
import com.dpdocter.services.FileManager;
import com.dpdocter.services.JasperReportService;
import com.dpdocter.services.MailBodyGenerator;
import com.dpdocter.services.MailService;
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
    private InvestigationRepository investigationRepository;

    @Autowired
    private DiagnosisRepository diagnosisRepository;

    @Autowired
    private NotesRepository notesRepository;

    @Autowired
    private DiagramsRepository diagramsRepository;

    @Autowired
    private FileManager fileManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DoctorRepository  doctorRepository;

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
    private ESClinicalNotesService esClinicalNotesService;

    @Autowired
    private TransactionalManagementService transactionalManagementService;

    @Autowired
    private PatientVisitRepository patientVisitRepository;

     @Autowired
     private ReferenceRepository referenceRepository;

     @Autowired
     private MailBodyGenerator mailBodyGenerator;

     @Autowired
     private MongoTemplate mongoTemplate;

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
    public ClinicalNotes addNotes(ClinicalNotesAddRequest request) {
	ClinicalNotes clinicalNotes = null;
	List<ObjectId> complaintIds = null;
	List<ObjectId> observationIds = null;
	List<ObjectId> investigationIds = null;
	List<ObjectId> noteIds = null;
	List<ObjectId> diagnosisIds = null;
	List<ObjectId> diagramIds = null;
	Date createdTime = new Date();

	try {
		String createdBy = null;
	    ClinicalNotesCollection clinicalNotesCollection = new ClinicalNotesCollection();
	    BeanUtil.map(request, clinicalNotesCollection);
	    UserCollection userCollection = userRepository.findOne(clinicalNotesCollection.getDoctorId());
	    if (userCollection != null) {
	    	createdBy = (userCollection.getTitle() != null ? userCollection.getTitle() + " " : "") + userCollection.getFirstName();
	    	clinicalNotesCollection.setCreatedBy(createdBy);
	    }
	    complaintIds = new ArrayList<ObjectId>();
	    if (request.getComplaints() != null && !request.getComplaints().isEmpty()) {
		for (ClinicalNotesComplaint complaint : request.getComplaints()) {
		    if (DPDoctorUtils.anyStringEmpty(complaint.getId())) {
			ComplaintCollection complaintCollection = new ComplaintCollection();
			BeanUtil.map(complaint, complaintCollection);
			BeanUtil.map(request, complaintCollection);
			complaintCollection.setCreatedBy(createdBy);
			complaintCollection.setCreatedTime(createdTime);
			complaintCollection.setId(null);
			complaintCollection = complaintRepository.save(complaintCollection);
			
			transactionalManagementService.addResource(complaintCollection.getId(), Resource.COMPLAINT, false);
			ESComplaintsDocument esComplaints = new ESComplaintsDocument();
			BeanUtil.map(complaintCollection, esComplaints);
			esClinicalNotesService.addComplaints(esComplaints);
			
			complaintIds.add(complaintCollection.getId());
		    } else {
			complaintIds.add(new ObjectId(complaint.getId()));
		    }
		}
	    }

	    observationIds = new ArrayList<ObjectId>();
	    if (request.getObservations() != null && !request.getObservations().isEmpty()) {
		for (ClinicalNotesObservation observation : request.getObservations()) {
		    if (DPDoctorUtils.anyStringEmpty(observation.getId())) {
			ObservationCollection observationCollection = new ObservationCollection();
			BeanUtil.map(observation, observationCollection);
			BeanUtil.map(request, observationCollection);
			observationCollection.setCreatedBy(createdBy);
			observationCollection.setCreatedTime(createdTime);
			observationCollection.setId(null);
			observationCollection = observationRepository.save(observationCollection);

			transactionalManagementService.addResource(observationCollection.getId(), Resource.OBSERVATION, false);
			ESObservationsDocument esObservations = new ESObservationsDocument();
			BeanUtil.map(observationCollection, esObservations);
			esClinicalNotesService.addObservations(esObservations);
			
			observationIds.add(observationCollection.getId());
		    } else {
			observationIds.add(new ObjectId(observation.getId()));
		    }
		}
	    }

	    investigationIds = new ArrayList<ObjectId>();
	    if (request.getInvestigations() != null && !request.getInvestigations().isEmpty()) {
		for (ClinicalNotesInvestigation investigation : request.getInvestigations()) {
		    if (DPDoctorUtils.anyStringEmpty(investigation.getId())) {
			InvestigationCollection investigationCollection = new InvestigationCollection();
			BeanUtil.map(investigation, investigationCollection);
			BeanUtil.map(request, investigationCollection);
			investigationCollection.setCreatedBy(createdBy);
			investigationCollection.setCreatedTime(createdTime);
			investigationCollection.setId(null);
			investigationCollection = investigationRepository.save(investigationCollection);

			transactionalManagementService.addResource(investigationCollection.getId(), Resource.INVESTIGATION, false);
			ESInvestigationsDocument esInvestigations = new ESInvestigationsDocument();
			BeanUtil.map(investigationCollection, esInvestigations);
			esClinicalNotesService.addInvestigations(esInvestigations);
			
			investigationIds.add(investigationCollection.getId());
		    } else {
			investigationIds.add(new ObjectId(investigation.getId()));
		    }
		}
	    }

	    noteIds = new ArrayList<ObjectId>();
	    if (request.getNotes() != null && !request.getNotes().isEmpty()) {
		for (ClinicalNotesNote note : request.getNotes()) {
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
			noteIds.add(notesCollection.getId());
		    } else {
			noteIds.add(new ObjectId(note.getId()));
		    }
		}
	    }

	    diagnosisIds = new ArrayList<ObjectId>();
	    if (request.getDiagnoses() != null && !request.getDiagnoses().isEmpty()) {
		for (ClinicalNotesDiagnosis diagnosis : request.getDiagnoses()) {
		    if (DPDoctorUtils.anyStringEmpty(diagnosis.getId())) {
			DiagnosisCollection diagnosisCollection = new DiagnosisCollection();
			BeanUtil.map(diagnosis, diagnosisCollection);
			BeanUtil.map(request, diagnosisCollection);
			diagnosisCollection.setCreatedBy(createdBy);
			diagnosisCollection.setCreatedTime(createdTime);
			diagnosisCollection.setId(null);
			diagnosisCollection = diagnosisRepository.save(diagnosisCollection);
			
			transactionalManagementService.addResource(diagnosisCollection.getId(), Resource.DIAGNOSIS, false);
			ESDiagnosesDocument esDiagnoses = new ESDiagnosesDocument();
			BeanUtil.map(diagnosisCollection, esDiagnoses);
			esClinicalNotesService.addDiagnoses(esDiagnoses);
			
			diagnosisIds.add(diagnosisCollection.getId());
		    } else {
			diagnosisIds.add(new ObjectId(diagnosis.getId()));
		    }
		}
	    }

	    clinicalNotesCollection.setComplaints(complaintIds);
	    clinicalNotesCollection.setInvestigations(investigationIds);
	    clinicalNotesCollection.setObservations(observationIds);
	    clinicalNotesCollection.setDiagnoses(diagnosisIds);
	    clinicalNotesCollection.setNotes(noteIds);
	    if (request.getDiagrams() == null) {
		clinicalNotesCollection.setDiagrams(null);
	    } else {
	    	diagramIds = new ArrayList<ObjectId>();
	    	for(String diagramId : request.getDiagrams()){
	    		diagramIds.add(new ObjectId(diagramId));
	    	}
	    }
	    clinicalNotesCollection.setUniqueEmrId(UniqueIdInitial.CLINICALNOTES.getInitial() + DPDoctorUtils.generateRandomId());
	    clinicalNotesCollection.setCreatedTime(createdTime);
	    clinicalNotesCollection = clinicalNotesRepository.save(clinicalNotesCollection);

	    clinicalNotes = new ClinicalNotes();
	    BeanUtil.map(clinicalNotesCollection, clinicalNotes);
	    
	    if(complaintIds != null && !complaintIds.isEmpty())clinicalNotes.setComplaints(mongoTemplate.aggregate(Aggregation.newAggregation(Aggregation.match(new Criteria("id").in(complaintIds))), ComplaintCollection.class, Complaint.class).getMappedResults());
	    if(investigationIds != null && !investigationIds.isEmpty())clinicalNotes.setInvestigations(mongoTemplate.aggregate(Aggregation.newAggregation(Aggregation.match(new Criteria("id").in(investigationIds))), InvestigationCollection.class, Investigation.class).getMappedResults());
	    if(observationIds != null && !observationIds.isEmpty())clinicalNotes.setObservations(mongoTemplate.aggregate(Aggregation.newAggregation(Aggregation.match(new Criteria("id").in(observationIds))), ObservationCollection.class, Observation.class).getMappedResults());
	    if(diagnosisIds != null && !diagnosisIds.isEmpty())clinicalNotes.setDiagnoses(mongoTemplate.aggregate(Aggregation.newAggregation(Aggregation.match(new Criteria("id").in(diagnosisIds))), DiagnosisCollection.class, Diagnoses.class).getMappedResults());
	    if(noteIds != null && !noteIds.isEmpty())clinicalNotes.setNotes(mongoTemplate.aggregate(Aggregation.newAggregation(Aggregation.match(new Criteria("id").in(noteIds))), NotesCollection.class, Notes.class).getMappedResults());
	    if(diagramIds != null && !diagramIds.isEmpty())clinicalNotes.setDiagrams(mongoTemplate.aggregate(Aggregation.newAggregation(Aggregation.match(new Criteria("id").in(diagramIds))), DiagramsCollection.class, Diagram.class).getMappedResults());
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}

	return clinicalNotes;
    }

    @Override
    @Transactional
    public ClinicalNotes getNotesById(String id) {
	ClinicalNotes clinicalNote = null;
	try {
	    ClinicalNotesCollection clinicalNotesCollection = clinicalNotesRepository.findOne(new ObjectId(id));
	    if (clinicalNotesCollection != null) {
		clinicalNote = new ClinicalNotes();
		BeanUtil.map(clinicalNotesCollection, clinicalNote);

		if(clinicalNotesCollection.getComplaints() != null && !clinicalNotesCollection.getComplaints().isEmpty())
			clinicalNote.setComplaints(mongoTemplate.aggregate(Aggregation.newAggregation(Aggregation.match(new Criteria("id").in(clinicalNotesCollection.getComplaints()))), ComplaintCollection.class, Complaint.class).getMappedResults());
		if(clinicalNotesCollection.getInvestigations() != null && !clinicalNotesCollection.getInvestigations().isEmpty())
			clinicalNote.setInvestigations(mongoTemplate.aggregate(Aggregation.newAggregation(Aggregation.match(new Criteria("id").in(clinicalNotesCollection.getInvestigations()))), InvestigationCollection.class, Investigation.class).getMappedResults());
		if(clinicalNotesCollection.getObservations() != null && !clinicalNotesCollection.getObservations().isEmpty())
			clinicalNote.setObservations(mongoTemplate.aggregate(Aggregation.newAggregation(Aggregation.match(new Criteria("id").in(clinicalNotesCollection.getObservations()))), ObservationCollection.class, Observation.class).getMappedResults());
		if(clinicalNotesCollection.getDiagnoses() != null && !clinicalNotesCollection.getDiagnoses().isEmpty())
			clinicalNote.setDiagnoses(mongoTemplate.aggregate(Aggregation.newAggregation(Aggregation.match(new Criteria("id").in(clinicalNotesCollection.getDiagnoses()))), DiagnosisCollection.class, Diagnoses.class).getMappedResults());
		if(clinicalNotesCollection.getNotes() != null && !clinicalNotesCollection.getNotes().isEmpty())
			clinicalNote.setNotes(mongoTemplate.aggregate(Aggregation.newAggregation(Aggregation.match(new Criteria("id").in(clinicalNotesCollection.getNotes()))), NotesCollection.class, Notes.class).getMappedResults());
		if(clinicalNotesCollection.getDiagrams() != null && !clinicalNotesCollection.getDiagrams().isEmpty())
			clinicalNote.setDiagrams(mongoTemplate.aggregate(Aggregation.newAggregation(Aggregation.match(new Criteria("id").in(clinicalNotesCollection.getDiagrams()))), DiagramsCollection.class, Diagram.class).getMappedResults());

		PatientVisitCollection patientVisitCollection = patientVisitRepository.findByClinialNotesId(clinicalNotesCollection.getId());
		if (patientVisitCollection != null)
		    clinicalNote.setVisitId(patientVisitCollection.getId().toString());
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
	return clinicalNote;
    }

    @Override
    @Transactional
    public ClinicalNotes editNotes(ClinicalNotesEditRequest request) {
	ClinicalNotes clinicalNotes = null;
	List<ObjectId> complaintIds = null;
	List<ObjectId> observationIds = null;
	List<ObjectId> investigationIds = null;
	List<ObjectId> noteIds = null;
	List<ObjectId> diagnosisIds = null;
	List<ObjectId> diagramIds = null;
	Date createdTime = new Date();

	try {
	    // save clinical notes.
	    ClinicalNotesCollection clinicalNotesCollection = new ClinicalNotesCollection();
	    BeanUtil.map(request, clinicalNotesCollection);

	    complaintIds = new ArrayList<ObjectId>();
	    if (request.getComplaints() != null && !request.getComplaints().isEmpty()) {
		for (ClinicalNotesComplaint complaint : request.getComplaints()) {
		    if (DPDoctorUtils.anyStringEmpty(complaint.getId())) {
			ComplaintCollection complaintCollection = new ComplaintCollection();
			BeanUtil.map(complaint, complaintCollection);
			BeanUtil.map(request, complaintCollection);
			complaintCollection.setId(null);
			complaintCollection.setCreatedTime(createdTime);
			complaintCollection = complaintRepository.save(complaintCollection);
			
			transactionalManagementService.addResource(complaintCollection.getId(), Resource.COMPLAINT, false);
			ESComplaintsDocument esComplaints = new ESComplaintsDocument();
			BeanUtil.map(complaintCollection, esComplaints);
			esClinicalNotesService.addComplaints(esComplaints);
			
			complaintIds.add(complaintCollection.getId());
		    } else {
			complaintIds.add(new ObjectId(complaint.getId()));
		    }
		}
	    }

	    observationIds = new ArrayList<ObjectId>();
	    if (request.getObservations() != null && !request.getObservations().isEmpty()) {
		for (ClinicalNotesObservation observation : request.getObservations()) {
		    if (DPDoctorUtils.anyStringEmpty(observation.getId())) {
			ObservationCollection observationCollection = new ObservationCollection();
			BeanUtil.map(observation, observationCollection);
			BeanUtil.map(request, observationCollection);
			observationCollection.setCreatedTime(createdTime);
			observationCollection.setId(null);
			observationCollection = observationRepository.save(observationCollection);
			transactionalManagementService.addResource(observationCollection.getId(), Resource.OBSERVATION, false);
			ESObservationsDocument esObservations = new ESObservationsDocument();
			BeanUtil.map(observationCollection, esObservations);
			esClinicalNotesService.addObservations(esObservations);
			observationIds.add(observationCollection.getId());
		    } else {
			observationIds.add(new ObjectId(observation.getId()));
		    }
		}
	    }

	    investigationIds = new ArrayList<ObjectId>();
	    if (request.getInvestigations() != null && !request.getInvestigations().isEmpty()) {
		for (ClinicalNotesInvestigation investigation : request.getInvestigations()) {
		    if (DPDoctorUtils.anyStringEmpty(investigation.getId())) {
			InvestigationCollection investigationCollection = new InvestigationCollection();
			BeanUtil.map(investigation, investigationCollection);
			BeanUtil.map(request, investigationCollection);
			investigationCollection.setCreatedTime(createdTime);
			investigationCollection.setId(null);
			investigationCollection = investigationRepository.save(investigationCollection);
			
			transactionalManagementService.addResource(investigationCollection.getId(), Resource.INVESTIGATION, false);
			ESInvestigationsDocument esInvestigations = new ESInvestigationsDocument();
			BeanUtil.map(investigationCollection, esInvestigations);
			esClinicalNotesService.addInvestigations(esInvestigations);
			
			investigationIds.add(investigationCollection.getId());
		    } else {
			investigationIds.add(new ObjectId(investigation.getId()));
		    }
		}
	    }

	    noteIds = new ArrayList<ObjectId>();
	    if (request.getNotes() != null && !request.getNotes().isEmpty()) {
		for (ClinicalNotesNote note : request.getNotes()) {
		    if (DPDoctorUtils.anyStringEmpty(note.getId())) {
			NotesCollection notesCollection = new NotesCollection();
			BeanUtil.map(note, notesCollection);
			BeanUtil.map(request, notesCollection);
			notesCollection.setCreatedTime(createdTime);
			notesCollection.setId(null);
			notesCollection = notesRepository.save(notesCollection);
			transactionalManagementService.addResource(notesCollection.getId(), Resource.NOTES, false);
			ESNotesDocument esNotes = new ESNotesDocument();
			BeanUtil.map(notesCollection, esNotes);
			esClinicalNotesService.addNotes(esNotes);
			noteIds.add(notesCollection.getId());
		    } else {
			noteIds.add(new ObjectId(note.getId()));
		    }
		}
	    }

	    diagnosisIds = new ArrayList<ObjectId>();
	    if (request.getDiagnoses() != null && !request.getDiagnoses().isEmpty()) {
		for (ClinicalNotesDiagnosis diagnosis : request.getDiagnoses()) {
		    if (DPDoctorUtils.anyStringEmpty(diagnosis.getId())) {
			DiagnosisCollection diagnosisCollection = new DiagnosisCollection();
			BeanUtil.map(diagnosis, diagnosisCollection);
			BeanUtil.map(request, diagnosisCollection);
			diagnosisCollection.setCreatedTime(createdTime);
			diagnosisCollection.setId(null);
			diagnosisCollection = diagnosisRepository.save(diagnosisCollection);
			
			transactionalManagementService.addResource(diagnosisCollection.getId(), Resource.DIAGNOSIS, false);
			ESDiagnosesDocument esDiagnoses = new ESDiagnosesDocument();
			BeanUtil.map(diagnosisCollection, esDiagnoses);
			esClinicalNotesService.addDiagnoses(esDiagnoses);
			
			diagnosisIds.add(diagnosisCollection.getId());
		    } else {
			diagnosisIds.add(new ObjectId(diagnosis.getId()));
		    }
		}
	    }

	    clinicalNotesCollection.setComplaints(complaintIds);
	    clinicalNotesCollection.setInvestigations(investigationIds);
	    clinicalNotesCollection.setObservations(observationIds);
	    clinicalNotesCollection.setDiagnoses(diagnosisIds);
	    clinicalNotesCollection.setNotes(noteIds);
	    if (request.getDiagrams() == null) {
		clinicalNotesCollection.setDiagrams(diagramIds);
	    } else {
	    	diagramIds = new ArrayList<ObjectId>();
	    	for(String diagramId : request.getDiagrams()){
	    		diagramIds.add(new ObjectId(diagramId));
	    	}
	    }

	    ClinicalNotesCollection oldClinicalNotesCollection = clinicalNotesRepository.findOne(clinicalNotesCollection.getId());
	    clinicalNotesCollection.setCreatedTime(oldClinicalNotesCollection.getCreatedTime());
	    clinicalNotesCollection.setCreatedBy(oldClinicalNotesCollection.getCreatedBy());
	    clinicalNotesCollection.setDiscarded(oldClinicalNotesCollection.getDiscarded());
	    clinicalNotesCollection.setInHistory(oldClinicalNotesCollection.isInHistory());
	    clinicalNotesCollection.setUpdatedTime(new Date());
	    clinicalNotesCollection.setUniqueEmrId(oldClinicalNotesCollection.getUniqueEmrId());
	    clinicalNotesCollection = clinicalNotesRepository.save(clinicalNotesCollection);
	    
	    clinicalNotes = new ClinicalNotes();
	    BeanUtil.map(clinicalNotesCollection, clinicalNotes);

	    if(complaintIds != null && !complaintIds.isEmpty())clinicalNotes.setComplaints(mongoTemplate.aggregate(Aggregation.newAggregation(Aggregation.match(new Criteria("id").in(complaintIds))), ComplaintCollection.class, Complaint.class).getMappedResults());
	    if(investigationIds != null && !investigationIds.isEmpty())clinicalNotes.setInvestigations(mongoTemplate.aggregate(Aggregation.newAggregation(Aggregation.match(new Criteria("id").in(investigationIds))), InvestigationCollection.class, Investigation.class).getMappedResults());
	    if(observationIds != null && !observationIds.isEmpty())clinicalNotes.setObservations(mongoTemplate.aggregate(Aggregation.newAggregation(Aggregation.match(new Criteria("id").in(observationIds))), ObservationCollection.class, Observation.class).getMappedResults());
	    if(diagnosisIds != null && !diagnosisIds.isEmpty())clinicalNotes.setDiagnoses(mongoTemplate.aggregate(Aggregation.newAggregation(Aggregation.match(new Criteria("id").in(diagnosisIds))), DiagnosisCollection.class, Diagnoses.class).getMappedResults());
	    if(noteIds != null && !noteIds.isEmpty())clinicalNotes.setNotes(mongoTemplate.aggregate(Aggregation.newAggregation(Aggregation.match(new Criteria("id").in(noteIds))), NotesCollection.class, Notes.class).getMappedResults());
	    if(diagramIds != null && !diagramIds.isEmpty())clinicalNotes.setDiagrams(mongoTemplate.aggregate(Aggregation.newAggregation(Aggregation.match(new Criteria("id").in(diagramIds))), DiagramsCollection.class, Diagram.class).getMappedResults());	
	    
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
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
	    if(clinicalNotes != null){
	    	clinicalNotes.setDiscarded(discarded);
		    clinicalNotes.setUpdatedTime(new Date());
		    clinicalNotes = clinicalNotesRepository.save(clinicalNotes);
		    response = new ClinicalNotes();
		    BeanUtil.map(clinicalNotes, response);
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
	public List<ClinicalNotes> getClinicalNotes(int page, int size, String doctorId, String locationId,	String hospitalId, String patientId, String updatedTime, Boolean isOTPVerified, Boolean discarded, Boolean inHistory) {
		List<ClinicalNotesCollection> clinicalNotesCollections = null;
		List<ClinicalNotes> clinicalNotes = null;
		boolean[] discards = new boolean[2];
		discards[0] = false;

		boolean[] inHistorys = new boolean[2];
		inHistorys[0] = true;

		ObjectId patientObjectId = null, doctorObjectId = null, locationObjectId = null , hospitalObjectId= null;
		if(!DPDoctorUtils.anyStringEmpty(patientId))patientObjectId = new ObjectId(patientId);
    	if(!DPDoctorUtils.anyStringEmpty(doctorId))doctorObjectId = new ObjectId(doctorId);
    	if(!DPDoctorUtils.anyStringEmpty(locationId))locationObjectId = new ObjectId(locationId);
    	if(!DPDoctorUtils.anyStringEmpty(hospitalId))hospitalObjectId = new ObjectId(hospitalId);
    	
		try {
		    if (discarded)discards[1] = true;
		    if (!inHistory)inHistorys[1] = false;

		    long createdTimestamp = Long.parseLong(updatedTime);

		    if (!isOTPVerified) {
			if (locationObjectId == null && hospitalObjectId == null) {
			    if (size > 0)
				clinicalNotesCollections = clinicalNotesRepository.getClinicalNotes(doctorObjectId, patientObjectId, new Date(createdTimestamp), discards, inHistorys,
					new PageRequest(page, size, Direction.DESC, "createdTime"));
			    else
				clinicalNotesCollections = clinicalNotesRepository.getClinicalNotes(doctorObjectId, patientObjectId, new Date(createdTimestamp), discards, inHistorys,
					new Sort(Sort.Direction.DESC, "createdTime"));
			} else {
			    if (size > 0)
				clinicalNotesCollections = clinicalNotesRepository.getClinicalNotes(doctorObjectId, hospitalObjectId, locationObjectId, patientObjectId,
					new Date(createdTimestamp), discards, inHistorys, new PageRequest(page, size, Direction.DESC, "createdTime"));
			    else
				clinicalNotesCollections = clinicalNotesRepository.getClinicalNotes(doctorObjectId, hospitalObjectId, locationObjectId, patientObjectId,
					new Date(createdTimestamp), discards, inHistorys, new Sort(Sort.Direction.DESC, "createdTime"));
			}
		    } else {
			if (size > 0)
			    clinicalNotesCollections = clinicalNotesRepository.getClinicalNotes(patientObjectId, new Date(createdTimestamp), discards, inHistorys,
				    new PageRequest(page, size, Direction.DESC, "createdTime"));
			else
			    clinicalNotesCollections = clinicalNotesRepository.getClinicalNotes(patientObjectId, new Date(createdTimestamp), discards, inHistorys, new Sort(
				    Sort.Direction.DESC, "createdTime"));
		    }
		    
		    if(clinicalNotesCollections != null && !clinicalNotesCollections.isEmpty()){
		    	clinicalNotes = new ArrayList<ClinicalNotes>();
		    	 for(ClinicalNotesCollection clinicalNotesCollection : clinicalNotesCollections) {
		    			ClinicalNotes clinicalNote = getClinicalNote(clinicalNotesCollection);
		    			clinicalNotes.add(clinicalNote);
		    		    }
		    }
		} catch (Exception e) {
		    e.printStackTrace();
		    logger.error(" Error Occurred While Getting Clinical Notes");
		    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Clinical Notes");
		}
		return clinicalNotes;
	}

	public ClinicalNotes getClinicalNote(ClinicalNotesCollection clinicalNotesCollection){
		   ClinicalNotes clinicalNote = new ClinicalNotes();
			BeanUtil.map(clinicalNotesCollection, clinicalNote);
			if(clinicalNotesCollection.getComplaints() != null && !clinicalNotesCollection.getComplaints().isEmpty())
				clinicalNote.setComplaints(mongoTemplate.aggregate(Aggregation.newAggregation(Aggregation.match(new Criteria("id").in(clinicalNotesCollection.getComplaints()))), ComplaintCollection.class, Complaint.class).getMappedResults());
			if(clinicalNotesCollection.getInvestigations() != null && !clinicalNotesCollection.getInvestigations().isEmpty())
				clinicalNote.setInvestigations(mongoTemplate.aggregate(Aggregation.newAggregation(Aggregation.match(new Criteria("id").in(clinicalNotesCollection.getInvestigations()))), InvestigationCollection.class, Investigation.class).getMappedResults());
			if(clinicalNotesCollection.getObservations() != null && !clinicalNotesCollection.getObservations().isEmpty())
				clinicalNote.setObservations(mongoTemplate.aggregate(Aggregation.newAggregation(Aggregation.match(new Criteria("id").in(clinicalNotesCollection.getObservations()))), ObservationCollection.class, Observation.class).getMappedResults());
			if(clinicalNotesCollection.getDiagnoses() != null && !clinicalNotesCollection.getDiagnoses().isEmpty())
				clinicalNote.setDiagnoses(mongoTemplate.aggregate(Aggregation.newAggregation(Aggregation.match(new Criteria("id").in(clinicalNotesCollection.getDiagnoses()))), DiagnosisCollection.class, Diagnoses.class).getMappedResults());
			if(clinicalNotesCollection.getNotes() != null && !clinicalNotesCollection.getNotes().isEmpty())
				clinicalNote.setNotes(mongoTemplate.aggregate(Aggregation.newAggregation(Aggregation.match(new Criteria("id").in(clinicalNotesCollection.getNotes()))), NotesCollection.class, Notes.class).getMappedResults());
			if(clinicalNotesCollection.getDiagrams() != null && !clinicalNotesCollection.getDiagrams().isEmpty())
				clinicalNote.setDiagrams(mongoTemplate.aggregate(Aggregation.newAggregation(Aggregation.match(new Criteria("id").in(clinicalNotesCollection.getDiagrams()))), DiagramsCollection.class, Diagram.class).getMappedResults());

			PatientVisitCollection patientVisitCollection = patientVisitRepository.findByClinialNotesId(clinicalNotesCollection.getId());
			if (patientVisitCollection != null) clinicalNote.setVisitId(patientVisitCollection.getId().toString());

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
		if(!DPDoctorUtils.anyStringEmpty(complaintCollection.getDoctorId())){
			UserCollection userCollection = userRepository.findOne(complaintCollection.getDoctorId());
		    if (userCollection != null) {
		    	complaintCollection.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "") + userCollection.getFirstName());
		    }
		}else{
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
		if(!DPDoctorUtils.anyStringEmpty(observationCollection.getDoctorId())){
			UserCollection userCollection = userRepository.findOne(observationCollection.getDoctorId());
		    if (userCollection != null) {
		    	observationCollection.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "") + userCollection.getFirstName());
		    }
		}else{
			observationCollection.setCreatedBy("ADMIN");
		}
	    } else {
		ObservationCollection oldObservationCollection = observationRepository.findOne(observationCollection.getId());
		observationCollection.setCreatedBy(oldObservationCollection.getCreatedBy());
		observationCollection.setCreatedTime(oldObservationCollection.getCreatedTime());
		observationCollection.setDiscarded(oldObservationCollection.getDiscarded());
	    }
	    observationCollection = observationRepository.save(observationCollection);
	    
	    BeanUtil.map(observationCollection, observation);
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
	return observation;
    }

    @Override
    @Transactional
    public Investigation addEditInvestigation(Investigation investigation) {
	try {
	    InvestigationCollection investigationCollection = new InvestigationCollection();
	    BeanUtil.map(investigation, investigationCollection);
	    if (DPDoctorUtils.anyStringEmpty(investigationCollection.getId())) {
		investigationCollection.setCreatedTime(new Date());
		if(!DPDoctorUtils.anyStringEmpty(investigationCollection.getDoctorId())){
			UserCollection userCollection = userRepository.findOne(investigationCollection.getDoctorId());
		    if (userCollection != null) {
		    	investigationCollection.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "") + userCollection.getFirstName());
		    }
		}else{
			investigationCollection.setCreatedBy("ADMIN");
		}
	    } else {
		InvestigationCollection oldInvestigationCollection = investigationRepository.findOne(investigationCollection.getId());
		investigationCollection.setCreatedBy(oldInvestigationCollection.getCreatedBy());
		investigationCollection.setCreatedTime(oldInvestigationCollection.getCreatedTime());
		investigationCollection.setDiscarded(oldInvestigationCollection.getDiscarded());
	    }
	    investigationCollection = investigationRepository.save(investigationCollection);
	    
	    BeanUtil.map(investigationCollection, investigation);
	    
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
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
		if(!DPDoctorUtils.anyStringEmpty(diagnosisCollection.getDoctorId())){
			UserCollection userCollection = userRepository.findOne(diagnosisCollection.getDoctorId());
		    if (userCollection != null) {
		    	diagnosisCollection.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "") + userCollection.getFirstName());
		    }
		}else{
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
		if(!DPDoctorUtils.anyStringEmpty(notesCollection.getDoctorId())){
			UserCollection userCollection = userRepository.findOne(notesCollection.getDoctorId());
		    if (userCollection != null) {
		    	notesCollection.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "") + userCollection.getFirstName());
		    }
		}else{
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
		ImageURLResponse imageURLResponse = fileManager.saveImageAndReturnImageUrl(diagram.getDiagram(), path, false);
		diagram.setDiagramUrl(imageURLResponse.getImageUrl());

	    }
	    DiagramsCollection diagramsCollection = new DiagramsCollection();
	    BeanUtil.map(diagram, diagramsCollection);
	    if(DPDoctorUtils.allStringsEmpty(diagram.getDoctorId()))diagramsCollection.setDoctorId(null);
	    if(DPDoctorUtils.allStringsEmpty(diagram.getLocationId()))diagramsCollection.setLocationId(null);
	    if(DPDoctorUtils.allStringsEmpty(diagram.getHospitalId()))diagramsCollection.setHospitalId(null);
	    
	    if (DPDoctorUtils.anyStringEmpty(diagramsCollection.getId())) {
		diagramsCollection.setCreatedTime(new Date());
		if(!DPDoctorUtils.anyStringEmpty(diagramsCollection.getDoctorId())){
			UserCollection userCollection = userRepository.findOne(diagramsCollection.getDoctorId());
		    if (userCollection != null) {
		    	diagramsCollection.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "") + userCollection.getFirstName());
		    }
		}else{
			diagramsCollection.setCreatedBy("ADMIN");
		}
	    } else {
		DiagramsCollection oldDiagramsCollection = diagramsRepository.findOne(diagramsCollection.getId());
		diagramsCollection.setCreatedBy(oldDiagramsCollection.getCreatedBy());
		diagramsCollection.setCreatedTime(oldDiagramsCollection.getCreatedTime());
		diagramsCollection.setDiscarded(oldDiagramsCollection.getDiscarded());
		if(diagram.getDiagram() == null){
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
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
	return diagram;
    }

    @Override
    @Transactional
    public Complaint deleteComplaint(String id, String doctorId, String locationId, String hospitalId, Boolean discarded) {
	Complaint response = null;
    try {
	    ComplaintCollection complaintCollection = complaintRepository.findOne(new ObjectId(id));
	    if (complaintCollection != null) {
		if (!DPDoctorUtils.anyStringEmpty(complaintCollection.getDoctorId(), complaintCollection.getHospitalId(), complaintCollection.getLocationId())) {
		    if (complaintCollection.getDoctorId().toString().equals(doctorId) && complaintCollection.getHospitalId().toString().equals(hospitalId) && complaintCollection.getLocationId().toString().equals(locationId)) {

			complaintCollection.setDiscarded(discarded);
			complaintCollection.setUpdatedTime(new Date());
			complaintRepository.save(complaintCollection);
			response = new Complaint();
			BeanUtil.map(complaintCollection, response);
		    } else {
			logger.warn("Invalid Doctor Id, Hospital Id, Or Location Id");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Doctor Id, Hospital Id, Or Location Id");
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
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
    	 return response;
    }

    @Override
    @Transactional
    public Observation deleteObservation(String id, String doctorId, String locationId, String hospitalId, Boolean discarded) {
	Observation response = null;
    	try {
	    ObservationCollection observationCollection = observationRepository.findOne(new ObjectId(id));
	    if (observationCollection != null) {
		if (DPDoctorUtils.anyStringEmpty(observationCollection.getDoctorId(), observationCollection.getHospitalId(), observationCollection.getLocationId())) {
		    if (observationCollection.getDoctorId().toString().equals(doctorId) && observationCollection.getHospitalId().toString().equals(hospitalId)
			    && observationCollection.getLocationId().toString().equals(locationId)) {
			observationCollection.setDiscarded(discarded);
			observationCollection.setUpdatedTime(new Date());
			observationRepository.save(observationCollection);
			response = new Observation();
			BeanUtil.map(observationCollection, response);
			} else {
			logger.warn("Invalid Doctor Id, Hospital Id, Or Location Id");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Doctor Id, Hospital Id, Or Location Id");
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
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
    	return response;
    }

    @Override
    @Transactional
    public Investigation deleteInvestigation(String id, String doctorId, String locationId, String hospitalId, Boolean discarded) {
    	Investigation response = null;
    	try {
	    InvestigationCollection investigationCollection = investigationRepository.findOne(new ObjectId(id));
	    if (investigationCollection != null) {
		if (investigationCollection.getDoctorId() != null && investigationCollection.getHospitalId() != null
			&& investigationCollection.getLocationId() != null) {
		    if (investigationCollection.getDoctorId().toString().equals(doctorId) && investigationCollection.getHospitalId().toString().equals(hospitalId)
			    && investigationCollection.getLocationId().toString().equals(locationId)) {
			investigationCollection.setDiscarded(discarded);
			investigationCollection.setUpdatedTime(new Date());
			investigationRepository.save(investigationCollection);
			response = new  Investigation();
			BeanUtil.map(investigationCollection, response);
		    } else {
			logger.warn("Invalid Doctor Id, Hospital Id, Or Location Id");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Doctor Id, Hospital Id, Or Location Id");
		    }
		} else {
			investigationCollection.setDiscarded(discarded);
			investigationCollection.setUpdatedTime(new Date());
			investigationRepository.save(investigationCollection);
			response = new  Investigation();
			BeanUtil.map(investigationCollection, response);
		}
	    } else {
		logger.warn("Investigation not found!");
		throw new BusinessException(ServiceError.NoRecord, "Investigation not found!");
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
    public Diagnoses deleteDiagnosis(String id, String doctorId, String locationId, String hospitalId, Boolean discarded) {
	Diagnoses response = null;
    	try {
	    DiagnosisCollection diagnosisCollection = diagnosisRepository.findOne(new ObjectId(id));
	    if (diagnosisCollection != null) {
		if (diagnosisCollection.getDoctorId() != null && diagnosisCollection.getHospitalId() != null && diagnosisCollection.getLocationId() != null) {
		    if (diagnosisCollection.getDoctorId().toString().equals(doctorId) && diagnosisCollection.getHospitalId().toString().equals(hospitalId)
			    && diagnosisCollection.getLocationId().toString().equals(locationId)) {
			diagnosisCollection.setDiscarded(discarded);
			diagnosisCollection.setUpdatedTime(new Date());
			diagnosisRepository.save(diagnosisCollection);
			response = new Diagnoses();
			BeanUtil.map(diagnosisCollection, response);
			} else {
			logger.warn("Invalid Doctor Id, Hospital Id, Or Location Id");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Doctor Id, Hospital Id, Or Location Id");
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
		if (notesCollection.getDoctorId() != null && notesCollection.getHospitalId() != null && notesCollection.getLocationId() != null) {
		    if (notesCollection.getDoctorId().toString().equals(doctorId) && notesCollection.getHospitalId().toString().equals(hospitalId)
			    && notesCollection.getLocationId().toString().equals(locationId)) {
			notesCollection.setDiscarded(discarded);
			notesCollection.setUpdatedTime(new Date());
			notesRepository.save(notesCollection);
			response = new Notes();
			BeanUtil.map(notesCollection, response);
			} else {
			logger.warn("Invalid Doctor Id, Hospital Id, Or Location Id");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Doctor Id, Hospital Id, Or Location Id");
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
		if (diagramsCollection.getDoctorId() != null && diagramsCollection.getHospitalId() != null && diagramsCollection.getLocationId() != null) {
		    if (diagramsCollection.getDoctorId().toString().equals(doctorId) && diagramsCollection.getHospitalId().toString().equals(hospitalId)
			    && diagramsCollection.getLocationId().toString().equals(locationId)) {
			diagramsCollection.setDiscarded(discarded);
			diagramsCollection.setUpdatedTime(new Date());
			diagramsRepository.save(diagramsCollection);
			response = new Diagram();
			BeanUtil.map(diagramsCollection, response);
			} else {
			logger.warn("Invalid Doctor Id, Hospital Id, Or Location Id");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Doctor Id, Hospital Id, Or Location Id");
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
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
    	return response;
    }

    @Override
    @Transactional
    public Integer getClinicalNotesCount(String doctorId, String patientId, String locationId, String hospitalId, boolean isOTPVerified) {
	Integer clinicalNotesCount = 0;
	try {
		ObjectId patientObjectId = null, doctorObjectId = null, locationObjectId = null , hospitalObjectId= null;
		if(!DPDoctorUtils.anyStringEmpty(patientId))patientObjectId = new ObjectId(patientId);
    	if(!DPDoctorUtils.anyStringEmpty(doctorId))doctorObjectId = new ObjectId(doctorId);
    	if(!DPDoctorUtils.anyStringEmpty(locationId))locationObjectId = new ObjectId(locationId);
    	if(!DPDoctorUtils.anyStringEmpty(hospitalId))hospitalObjectId = new ObjectId(hospitalId);
    	
		if(isOTPVerified)clinicalNotesCount = clinicalNotesRepository.getClinicalNotesCount(patientObjectId, false);
	    else clinicalNotesCount = clinicalNotesRepository.getClinicalNotesCount(doctorObjectId, patientObjectId, hospitalObjectId, locationObjectId, false);
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Clinical Notes Count");
	}
	return clinicalNotesCount;
    }

    @Override
    @Transactional
    public List<?> getClinicalItems(String type, String range, int page, int size, String doctorId, String locationId, String hospitalId,
	    String updatedTime, Boolean discarded, Boolean isAdmin, String searchTerm) {
	List<?> response = new ArrayList<Object>();

	switch (ClinicalItems.valueOf(type.toUpperCase())) {

	case COMPLAINTS: {

	    switch (Range.valueOf(range.toUpperCase())) {

	    case GLOBAL:
	    	if(isAdmin)response = getGlobalComplaintsForAdmin(page, size, updatedTime, discarded, searchTerm);
	    	else response = getGlobalComplaints(page, size, doctorId, updatedTime, discarded);
		break;
	    case CUSTOM:
	    	if(isAdmin)response = getCustomComplaintsForAdmin(page, size, updatedTime, discarded, searchTerm);
	    	else response = getCustomComplaints(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
		break;
	    case BOTH:
	    	if(isAdmin)response = getCustomGlobalComplaintsForAdmin(page, size, updatedTime, discarded, searchTerm);
	    	else response = getCustomGlobalComplaints(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
		break;
		default:
			break;
	    }
	    break;
	}
	case INVESTIGATIONS: {
	    switch (Range.valueOf(range.toUpperCase())) {

	    case GLOBAL:
			if(isAdmin)response = getGlobalInvestigationsForAdmin(page, size, updatedTime, discarded, searchTerm);
			else response = getGlobalInvestigations(page, size, doctorId, updatedTime, discarded);
		break;
	    case CUSTOM:
	    	if(isAdmin)response = getCustomInvestigationsForAdmin(page, size, updatedTime, discarded, searchTerm);
	    	else response = getCustomInvestigations(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
		break;
	    case BOTH:
	    	if(isAdmin)response = getCustomGlobalInvestigationsForAdmin(page, size, updatedTime, discarded, searchTerm);
	    	else response = getCustomGlobalInvestigations(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
		break;
		default:
			break;
	    }
	    break;
	}
	case OBSERVATIONS: {
	    switch (Range.valueOf(range.toUpperCase())) {

	    case GLOBAL:
	    	if(isAdmin)response = getGlobalObservationsForAdmin(page, size, updatedTime, discarded, searchTerm);
	    	else response = getGlobalObservations(page, size, doctorId, updatedTime, discarded);
		break;
	    case CUSTOM:
	    	if(isAdmin)response = getCustomObservationsForAdmin(page, size, updatedTime, discarded, searchTerm);
	    	else response = getCustomObservations(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
		break;
	    case BOTH:
	    	if(isAdmin)response = getCustomGlobalObservationsForAdmin(page, size, updatedTime, discarded, searchTerm);
	    	else response = getCustomGlobalObservations(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
		break;
		default:
			break;
	    }
	    break;
	}
	case DIAGNOSIS: {
	    switch (Range.valueOf(range.toUpperCase())) {

	    case GLOBAL:
	    	if(isAdmin)response = getGlobalDiagnosisForAdmin(page, size, updatedTime, discarded, searchTerm);
	    	else response = getGlobalDiagnosis(page, size, doctorId, updatedTime, discarded);
		break;
	    case CUSTOM:
	    	if(isAdmin)response = getCustomDiagnosisForAdmin(page, size, updatedTime, discarded, searchTerm);
	    	else response = getCustomDiagnosis(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
		break;
	    case BOTH:
	    	if(isAdmin)response = getCustomGlobalDiagnosisForAdmin(page, size, updatedTime, discarded, searchTerm);
	    	else response = getCustomGlobalDiagnosis(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
		break;
		default:
			break;
	    }
	    break;
	}
	case NOTES: {
	    switch (Range.valueOf(range.toUpperCase())) {

	    case GLOBAL:
	    	if(isAdmin)response = getGlobalNotesForAdmin(page, size, updatedTime, discarded, searchTerm);
	    	else response = getGlobalNotes(page, size, doctorId, updatedTime, discarded);
		break;
	    case CUSTOM:
	    	if(isAdmin)response = getCustomNotesForAdmin(page, size, updatedTime, discarded, searchTerm);
	    	else response = getCustomNotes(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
		break;
	    case BOTH:
	    	if(isAdmin)response = getCustomGlobalNotesForAdmin(page, size, updatedTime, discarded, searchTerm);
	    	else response = getCustomGlobalNotes(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
		break;
		default:
			break;
	    }
	    break;
	}
	case DIAGRAMS: {
	    switch (Range.valueOf(range.toUpperCase())) {

	    case GLOBAL:
	    	if(isAdmin)response = getGlobalDiagramsForAdmin(page, size, updatedTime, discarded, searchTerm);
	    	else response = getGlobalDiagrams(page, size, doctorId, updatedTime, discarded);
		break;
	    case CUSTOM:
	    	if(isAdmin)response = getCustomDiagramsForAdmin(page, size, updatedTime, discarded, searchTerm);
	    	else response = getCustomDiagrams(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
		break;
	    case BOTH:
	    	if(isAdmin)response = getCustomGlobalDiagramsForAdmin(page, size, updatedTime, discarded, searchTerm);
	    	else response = getCustomGlobalDiagrams(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
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
private List<Complaint> getCustomGlobalComplaints(int page, int size, String doctorId, String locationId, String hospitalId, String updatedTime,
	    Boolean discarded) {
	List<Complaint> response = new ArrayList<Complaint>();
	try {
	    DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
	    if(doctorCollection == null){
	    	logger.warn("No Doctor Found");
    		throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
	    }
	    Collection<String> specialities = null;
		if(doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()){
		    	specialities = CollectionUtils.collect((Collection<?>) specialityRepository.findAll(doctorCollection.getSpecialities()),new BeanToPropertyValueTransformer("speciality"));
			    specialities.add(null);specialities.add("ALL");
		}
		
		AggregationResults<Complaint> results = mongoTemplate.aggregate(DPDoctorUtils.createCustomGlobalAggregation(page, size, doctorId, locationId, hospitalId, updatedTime, discarded, null, null, specialities), ComplaintCollection.class, Complaint.class); 
		response = results.getMappedResults();
		    
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Complaints");
	}
	return response;

    }

    @SuppressWarnings("unchecked")
	private List<Complaint> getGlobalComplaints(int page, int size, String doctorId, String updatedTime, Boolean discarded) {
	List<Complaint> response = null;
	try {
	    DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
	    if(doctorCollection == null){
	    	logger.warn("No Doctor Found");
    		throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
	    }
	    Collection<String> specialities = null; 
	    if(doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()){
	    	specialities = CollectionUtils.collect((Collection<?>) specialityRepository.findAll(doctorCollection.getSpecialities()),new BeanToPropertyValueTransformer("speciality"));
	    	specialities.add("ALL");specialities.add(null);
	    }
		
		AggregationResults<Complaint> results = mongoTemplate.aggregate(DPDoctorUtils.createGlobalAggregation(page, size, updatedTime, discarded, null, null, specialities), ComplaintCollection.class, Complaint.class); 
		response = results.getMappedResults();

	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Complaints");
	}
	return response;
    }

    private List<Complaint> getCustomComplaints(int page, int size, String doctorId, String locationId, String hospitalId, String updatedTime, Boolean discarded) {
	List<Complaint> response = null;
	try {
		AggregationResults<Complaint> results = mongoTemplate.aggregate(DPDoctorUtils.createCustomAggregation(page, size, doctorId, locationId, hospitalId, updatedTime, discarded, null, null), ComplaintCollection.class, Complaint.class); 
		response = results.getMappedResults();
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Complaints");
	}
	return response;
    }

    @SuppressWarnings("unchecked")
	private List<Investigation> getCustomGlobalInvestigations(int page, int size, String doctorId, String locationId, String hospitalId, String updatedTime, Boolean discarded) {
	List<Investigation> response = new ArrayList<Investigation>();
	try {
		DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
	    if(doctorCollection == null){
	    	logger.warn("No Doctor Found");
    		throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
	    }
	    Collection<String> specialities = null;
		if(doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()){
		    	specialities = CollectionUtils.collect((Collection<?>) specialityRepository.findAll(doctorCollection.getSpecialities()),new BeanToPropertyValueTransformer("speciality"));
			    specialities.add(null);specialities.add("ALL");
		}
		
		AggregationResults<Investigation> results = mongoTemplate.aggregate(DPDoctorUtils.createCustomGlobalAggregation(page, size, doctorId, locationId, hospitalId, updatedTime, discarded, null, null, specialities), InvestigationCollection.class, Investigation.class); 
		response = results.getMappedResults();
		
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Investigations");
	}
	return response;
    }

    @SuppressWarnings("unchecked")
	private List<Investigation> getGlobalInvestigations(int page, int size, String doctorId, String updatedTime, Boolean discarded) {
	List<Investigation> response = null;
	try {
		DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
	    if(doctorCollection == null){
	    	logger.warn("No Doctor Found");
    		throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
	    }
	    Collection<String> specialities = null; 
	    if(doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()){
	    	specialities = CollectionUtils.collect((Collection<?>) specialityRepository.findAll(doctorCollection.getSpecialities()),new BeanToPropertyValueTransformer("speciality"));
	    	specialities.add("ALL");specialities.add(null);
	    }
		
		AggregationResults<Investigation> results = mongoTemplate.aggregate(DPDoctorUtils.createGlobalAggregation(page, size, updatedTime, discarded, null, null, specialities), InvestigationCollection.class, Investigation.class); 
		response = results.getMappedResults();
		
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Investigations");
	}
	return response;
    }

    private List<Investigation> getCustomInvestigations(int page, int size, String doctorId, String locationId, String hospitalId, String updatedTime, Boolean discarded) {
	List<Investigation> response = null;
	boolean[] discards = new boolean[2];
	discards[0] = false;
	try {
		AggregationResults<Investigation> results = mongoTemplate.aggregate(DPDoctorUtils.createCustomAggregation(page, size, doctorId, locationId, hospitalId, updatedTime, discarded, null, null), InvestigationCollection.class, Investigation.class); 
		response = results.getMappedResults();
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Investigations");
	}
	return response;
    }

    @SuppressWarnings("unchecked")
	private List<Observation> getCustomGlobalObservations(int page, int size, String doctorId, String locationId, String hospitalId, String updatedTime, Boolean discarded) {
	List<Observation> response = new ArrayList<Observation>();
	try {
		DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
	    if(doctorCollection == null){
	    	logger.warn("No Doctor Found");
    		throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
	    }
	    Collection<String> specialities = null;
		if(doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()){
		    	specialities = CollectionUtils.collect((Collection<?>) specialityRepository.findAll(doctorCollection.getSpecialities()),new BeanToPropertyValueTransformer("speciality"));
			    specialities.add(null);specialities.add("ALL");
		}
		
		AggregationResults<Observation> results = mongoTemplate.aggregate(DPDoctorUtils.createCustomGlobalAggregation(page, size, doctorId, locationId, hospitalId, updatedTime, discarded, null, null, specialities), ObservationCollection.class, Observation.class); 
		response = results.getMappedResults();	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Observations");
	}
	return response;

    }

    @SuppressWarnings("unchecked")
	private List<Observation> getGlobalObservations(int page, int size, String doctorId, String updatedTime, Boolean discarded) {
	List<Observation> response = null;
	try {
		DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
	    if(doctorCollection == null){
	    	logger.warn("No Doctor Found");
    		throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
	    }
	    Collection<String> specialities = null; 
	    if(doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()){
	    	specialities = CollectionUtils.collect((Collection<?>) specialityRepository.findAll(doctorCollection.getSpecialities()),new BeanToPropertyValueTransformer("speciality"));
	    	specialities.add("ALL");specialities.add(null);
	    }
		
		AggregationResults<Observation> results = mongoTemplate.aggregate(DPDoctorUtils.createGlobalAggregation(page, size, updatedTime, discarded, null, null, specialities), ObservationCollection.class, Observation.class); 
		response = results.getMappedResults();
		
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Observations");
	}
	return response;
    }

    private List<Observation> getCustomObservations(int page, int size, String doctorId, String locationId, String hospitalId, String updatedTime,
	    Boolean discarded) {
	List<Observation> response = null;
	try {
		AggregationResults<Observation> results = mongoTemplate.aggregate(DPDoctorUtils.createCustomAggregation(page, size, doctorId, locationId, hospitalId, updatedTime, discarded, null, null), Observation.class, Observation.class); 
		response = results.getMappedResults();
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Observations");
	}
	return response;
    }

    @SuppressWarnings("unchecked")
	private List<Diagnoses> getCustomGlobalDiagnosis(int page, int size, String doctorId, String locationId, String hospitalId, String updatedTime,
	    Boolean discarded) {
	List<Diagnoses> response = new ArrayList<Diagnoses>();
	
	try {
		DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
	    if(doctorCollection == null){
	    	logger.warn("No Doctor Found");
    		throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
	    }
	    Collection<String> specialities = null;
		if(doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()){
		    	specialities = CollectionUtils.collect((Collection<?>) specialityRepository.findAll(doctorCollection.getSpecialities()),new BeanToPropertyValueTransformer("speciality"));
			    specialities.add(null);specialities.add("ALL");
		}
		
		AggregationResults<Diagnoses> results = mongoTemplate.aggregate(DPDoctorUtils.createCustomGlobalAggregation(page, size, doctorId, locationId, hospitalId, updatedTime, discarded, null, null, specialities), DiagnosisCollection.class, Diagnoses.class); 
		response = results.getMappedResults();	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Diagnosis");
	}
	return response;

    }

    @SuppressWarnings("unchecked")
	private List<Diagnoses> getGlobalDiagnosis(int page, int size, String doctorId, String updatedTime, Boolean discarded) {
	List<Diagnoses> response = null;
	try {
		DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
	    if(doctorCollection == null){
	    	logger.warn("No Doctor Found");
    		throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
	    }
	    Collection<String> specialities = null; 
	    if(doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()){
	    	specialities = CollectionUtils.collect((Collection<?>) specialityRepository.findAll(doctorCollection.getSpecialities()),new BeanToPropertyValueTransformer("speciality"));
	    	specialities.add("ALL");specialities.add(null);
	    }
		
		AggregationResults<Diagnoses> results = mongoTemplate.aggregate(DPDoctorUtils.createGlobalAggregation(page, size, updatedTime, discarded, null, null, specialities), DiagnosisCollection.class, Diagnoses.class); 
		response = results.getMappedResults();
		
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Diagnosis");
	}
	return response;
    }

    private List<Diagnoses> getCustomDiagnosis(int page, int size, String doctorId, String locationId, String hospitalId, String updatedTime, Boolean discarded) {
	List<Diagnoses> response = null;
	try {
		AggregationResults<Diagnoses> results = mongoTemplate.aggregate(DPDoctorUtils.createCustomAggregation(page, size, doctorId, locationId, hospitalId, updatedTime, discarded, null, null), DiagnosisCollection.class, Diagnoses.class); 
		response = results.getMappedResults();
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Diagnosis");
	}
	return response;
    }

    @SuppressWarnings("unchecked")
	private List<Notes> getCustomGlobalNotes(int page, int size, String doctorId, String locationId, String hospitalId, String updatedTime,
	    Boolean discarded) {
	List<Notes> response = new ArrayList<Notes>();
	try {
		DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
	    if(doctorCollection == null){
	    	logger.warn("No Doctor Found");
    		throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
	    }
	    Collection<String> specialities = null;
		if(doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()){
		    	specialities = CollectionUtils.collect((Collection<?>) specialityRepository.findAll(doctorCollection.getSpecialities()),new BeanToPropertyValueTransformer("speciality"));
			    specialities.add(null);specialities.add("ALL");
		}
		
		AggregationResults<Notes> results = mongoTemplate.aggregate(DPDoctorUtils.createCustomGlobalAggregation(page, size, doctorId, locationId, hospitalId, updatedTime, discarded, null, null, specialities), NotesCollection.class, Notes.class); 
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
	    if(doctorCollection == null){
	    	logger.warn("No Doctor Found");
    		throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
	    }
	    Collection<String> specialities = null; 
	    if(doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()){
	    	specialities = CollectionUtils.collect((Collection<?>) specialityRepository.findAll(doctorCollection.getSpecialities()),new BeanToPropertyValueTransformer("speciality"));
	    	specialities.add("ALL");specialities.add(null);
	    }
		
		AggregationResults<Notes> results = mongoTemplate.aggregate(DPDoctorUtils.createGlobalAggregation(page, size, updatedTime, discarded, null, null, specialities), NotesCollection.class, Notes.class); 
		response = results.getMappedResults();
		
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Notes");
	}
	return response;
    }

    private List<Notes> getCustomNotes(int page, int size, String doctorId, String locationId, String hospitalId, String updatedTime, Boolean discarded) {
	List<Notes> response = null;
	try {
		AggregationResults<Notes> results = mongoTemplate.aggregate(DPDoctorUtils.createCustomAggregation(page, size, doctorId, locationId, hospitalId, updatedTime, discarded, null, null), NotesCollection.class, Notes.class); 
		response = results.getMappedResults();
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Notes");
	}
	return response;
    }

    @SuppressWarnings("unchecked")
	private List<Diagram> getCustomGlobalDiagrams(int page, int size, String doctorId, String locationId, String hospitalId, String updatedTime,
	    Boolean discarded) {
	List<Diagram> response = new ArrayList<Diagram>();
	try {
		DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
	    if(doctorCollection == null){
	    	logger.warn("No Doctor Found");
    		throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
	    }
	    Collection<String> specialities = null;
		if(doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()){
		    	specialities = CollectionUtils.collect((Collection<?>) specialityRepository.findAll(doctorCollection.getSpecialities()),new BeanToPropertyValueTransformer("speciality"));
			    specialities.add(null);specialities.add("ALL");
		}
		
		AggregationResults<Diagram> results = mongoTemplate.aggregate(DPDoctorUtils.createCustomGlobalAggregation(page, size, doctorId, locationId, hospitalId, updatedTime, discarded, null, null, specialities), DiagramsCollection.class, Diagram.class); 
		response = results.getMappedResults();	
		
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Diagrams");
	}
	return response;

    }

    @SuppressWarnings("unchecked")
	private List<Diagram> getGlobalDiagrams(int page, int size, String doctorId, String updatedTime, Boolean discarded) {
	List<Diagram> response = null;
	try {
		DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
	    if(doctorCollection == null){
	    	logger.warn("No Doctor Found");
    		throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
	    }
	    Collection<String> specialities = null; 
	    if(doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()){
	    	specialities = CollectionUtils.collect((Collection<?>) specialityRepository.findAll(doctorCollection.getSpecialities()),new BeanToPropertyValueTransformer("speciality"));
	    	specialities.add("ALL");specialities.add(null);
	    }
		
		AggregationResults<Diagram> results = mongoTemplate.aggregate(DPDoctorUtils.createGlobalAggregation(page, size, updatedTime, discarded, null, null, specialities), DiagramsCollection.class, Diagram.class); 
		response = results.getMappedResults();
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Diagrams");
	}
	return response;
    }

    private List<Diagram> getCustomDiagrams(int page, int size, String doctorId, String locationId, String hospitalId, String updatedTime, Boolean discarded) {
	List<Diagram> response = null;
	try {
		AggregationResults<Diagram> results = mongoTemplate.aggregate(DPDoctorUtils.createCustomAggregation(page, size, doctorId, locationId, hospitalId, updatedTime, discarded, null, null), DiagramsCollection.class, Diagram.class); 
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
    public void emailClinicalNotes(String clinicalNotesId, String doctorId, String locationId, String hospitalId, String emailAddress) {
	try {
		MailResponse mailResponse = createMailData(clinicalNotesId, doctorId, locationId, hospitalId);
	    String body = mailBodyGenerator.generateEMREmailBody(mailResponse.getPatientName(), mailResponse.getDoctorName(), mailResponse.getClinicName(), mailResponse.getClinicAddress(), mailResponse.getMailRecordCreatedDate(), "Clinical Notes", "emrMailTemplate.vm");
	    mailService.sendEmail(emailAddress, mailResponse.getDoctorName()+" sent you Clinical Notes", body, mailResponse.getMailAttachment());
	    
		if(mailResponse.getMailAttachment() != null && mailResponse.getMailAttachment().getFileSystemResource() != null)
	    	if(mailResponse.getMailAttachment().getFileSystemResource().getFile().exists())mailResponse.getMailAttachment().getFileSystemResource().getFile().delete() ;
	} catch (Exception e) {
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}

    }

    @Override
    @Transactional
    public MailResponse getClinicalNotesMailData(String clinicalNotesId, String doctorId, String locationId, String hospitalId) {
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
		    if (clinicalNotesCollection.getDoctorId().equals(doctorId) && clinicalNotesCollection.getHospitalId().equals(hospitalId)
			    && clinicalNotesCollection.getLocationId().equals(locationId)) {

			    user = userRepository.findOne(clinicalNotesCollection.getPatientId());
			    patient = patientRepository.findByUserIdDoctorIdLocationIdAndHospitalId(clinicalNotesCollection.getPatientId(), clinicalNotesCollection.getDoctorId(), clinicalNotesCollection.getLocationId(), clinicalNotesCollection.getHospitalId());

			    emailTrackCollection.setDoctorId(new ObjectId(doctorId));
			    emailTrackCollection.setHospitalId(new ObjectId(hospitalId));
			    emailTrackCollection.setLocationId(new ObjectId(locationId));
			    emailTrackCollection.setType(ComponentType.CLINICAL_NOTES.getType());
			    emailTrackCollection.setSubject("Clinical Notes");
			    if (user != null) {
				emailTrackCollection.setPatientName(user.getFirstName());
				emailTrackCollection.setPatientId(user.getId());
			    }
			   JasperReportResponse jasperReportResponse = createJasper(clinicalNotesCollection, patient, user);
				mailAttachment = new MailAttachment();
				mailAttachment.setAttachmentName(FilenameUtils.getName(jasperReportResponse.getPath()));
				mailAttachment.setFileSystemResource(jasperReportResponse.getFileSystemResource());
				UserCollection doctorUser = userRepository.findOne(new ObjectId(doctorId));
				LocationCollection locationCollection = locationRepository.findOne(new ObjectId(locationId));
				
				response = new MailResponse();
				response.setMailAttachment(mailAttachment);
				response.setDoctorName(doctorUser.getTitle()+" "+doctorUser.getFirstName());
				String address = 
    	    			(!DPDoctorUtils.anyStringEmpty(locationCollection.getStreetAddress()) ? locationCollection.getStreetAddress()+", ":"")+
    	    			(!DPDoctorUtils.anyStringEmpty(locationCollection.getLandmarkDetails()) ? locationCollection.getLandmarkDetails()+", ":"")+
    	    			(!DPDoctorUtils.anyStringEmpty(locationCollection.getLocality()) ? locationCollection.getLocality()+", ":"")+
    	    			(!DPDoctorUtils.anyStringEmpty(locationCollection.getCity()) ? locationCollection.getCity()+", ":"")+
    	    			(!DPDoctorUtils.anyStringEmpty(locationCollection.getState()) ? locationCollection.getState()+", ":"")+
    	    			(!DPDoctorUtils.anyStringEmpty(locationCollection.getCountry()) ? locationCollection.getCountry()+", ":"")+
    	    			(!DPDoctorUtils.anyStringEmpty(locationCollection.getPostalCode()) ? locationCollection.getPostalCode():"");
    	    	
    		    if(address.charAt(address.length() - 2) == ','){
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
			    throw new BusinessException(ServiceError.NotFound, "Clinical Notes Id, doctorId, location Id, hospital Id does not match");
			}
		}
	    } else {
		logger.warn("Clinical Notes not found. Please check clinicalNotesId.");
		throw new BusinessException(ServiceError.NotFound, "Clinical Notes not found. Please check clinicalNotesId.");
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
    public List<ClinicalNotes> getClinicalNotes(String patientId, int page, int size, String updatedTime, Boolean discarded) {
		List<ClinicalNotesCollection> clinicalNotesCollections = null;
		List<ClinicalNotes> clinicalNotes = null;
		boolean[] discards = new boolean[2];
		discards[0] = false;

		boolean[] inHistorys = new boolean[2];
		inHistorys[0] = true;
		inHistorys[1] = false;

		try {
		    if (discarded)discards[1] = true;
		    long createdTimestamp = Long.parseLong(updatedTime);
		    ObjectId patientObjectId = null;
			if(!DPDoctorUtils.anyStringEmpty(patientId))patientObjectId = new ObjectId(patientId);
	    	
			if (size > 0)clinicalNotesCollections = clinicalNotesRepository.getClinicalNotes(patientObjectId, new Date(createdTimestamp), discards, inHistorys, new PageRequest(page, size, Direction.DESC, "createdTime"));
			else  clinicalNotesCollections = clinicalNotesRepository.getClinicalNotes(patientObjectId, new Date(createdTimestamp), discards, inHistorys, new Sort(Sort.Direction.DESC, "createdTime"));
		    
		    if(clinicalNotesCollections != null && !clinicalNotesCollections.isEmpty()){
		    	clinicalNotes = new ArrayList<ClinicalNotes>();
		    	 for(ClinicalNotesCollection clinicalNotesCollection : clinicalNotesCollections) {
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
	public String getClinicalNotesFile(String clinicalNotesId) {
		String response = null;
		try{
			ClinicalNotesCollection clinicalNotesCollection = clinicalNotesRepository.findOne(new ObjectId(clinicalNotesId));

		    if (clinicalNotesCollection != null) {
			PatientCollection patient = patientRepository.findByUserIdDoctorIdLocationIdAndHospitalId(clinicalNotesCollection.getPatientId(), clinicalNotesCollection.getDoctorId(), clinicalNotesCollection.getLocationId(), clinicalNotesCollection.getHospitalId());
			UserCollection user = userRepository.findOne(clinicalNotesCollection.getPatientId());

			JasperReportResponse jasperReportResponse = createJasper(clinicalNotesCollection, patient, user);
			if(jasperReportResponse != null)response = getFinalImageURL(jasperReportResponse.getPath());
			if(jasperReportResponse != null && jasperReportResponse.getFileSystemResource() != null)
		    	if(jasperReportResponse.getFileSystemResource().getFile().exists())jasperReportResponse.getFileSystemResource().getFile().delete() ;
		    } else {
				logger.warn("Patient Visit Id does not exist");
				throw new BusinessException(ServiceError.NotFound, "Patient Visit Id does not exist");
			}
		}catch(Exception e){
			e.printStackTrace();
		    logger.error(e + " Error while getting Patient Visits PDF");
		    throw new BusinessException(ServiceError.Unknown, "Error while getting Patient Visits PDF");
		}
		return response;
	}

	private JasperReportResponse createJasper(ClinicalNotesCollection clinicalNotesCollection, PatientCollection patient, UserCollection user) throws IOException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		JasperReportResponse response = null;
		String observations = "";
		for (ObjectId observationId : clinicalNotesCollection.getObservations()) {
		    ObservationCollection observationCollection = observationRepository.findOne(observationId);
		    if (observationCollection != null) {
			if (observations.isEmpty())
			    observations = observationCollection.getObservation();
			else
			    observations = observations + ", " + observationCollection.getObservation();
		    }
		}
		parameters.put("observationIds", observations);

		String notes = "";
		for (ObjectId noteId : clinicalNotesCollection.getNotes()) {
		    NotesCollection note = notesRepository.findOne(noteId);
		    if (note != null) {
			if (notes.isEmpty())
			    notes = note.getNote();
			else
			    notes = notes + ", " + note.getNote();
		    }
		}
		parameters.put("noteIds", notes);

		String investigations = "";
		for (ObjectId investigationId : clinicalNotesCollection.getInvestigations()) {
		    InvestigationCollection investigation = investigationRepository.findOne(investigationId);
		    if (investigation != null) {
			if (investigations.isEmpty())
			    investigations = investigation.getInvestigation();
			else
			    investigations = investigations + ", " + investigation.getInvestigation();
		    }
		}
		parameters.put("investigationIds", investigations);

		String diagnosis = "";
		for (ObjectId diagnosisId : clinicalNotesCollection.getDiagnoses()) {
		    DiagnosisCollection diagnosisCollection = diagnosisRepository.findOne(diagnosisId);
		    if (diagnosisCollection != null) {
			if (diagnosis.isEmpty())
			    diagnosis = diagnosisCollection.getDiagnosis();
			else
			    diagnosis = diagnosis + ", " + diagnosisCollection.getDiagnosis();
		    }
		}
		parameters.put("diagnosesIds", diagnosis);

		String complaints = "";
		for (ObjectId complaintId : clinicalNotesCollection.getComplaints()) {
		    ComplaintCollection complaint = complaintRepository.findOne(complaintId);
		    if (complaint != null) {
			if (complaints.isEmpty())
			    complaints = complaint.getComplaint();
			else
			    complaints = complaints + ", " + complaint.getComplaint();
		    }
		}
		parameters.put("complaintIds", complaints);

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
		    parameters.put("diagramIds", diagramIds);
		else
		    parameters.put("diagramIds", null);
	    
	    parameters.put("clinicalNotesId", clinicalNotesCollection.getId().toString());
	    if (clinicalNotesCollection.getVitalSigns() != null) {
	    	String vitalSigns = null;
	    	
			String pulse = clinicalNotesCollection.getVitalSigns().getPulse();
			pulse =  (pulse != null && !pulse.isEmpty() ? "Pulse("+VitalSignsUnit.PULSE.getUnit()+"): "+pulse.trim(): "");
			if(!DPDoctorUtils.allStringsEmpty(pulse))vitalSigns = pulse;
	
			String temp = clinicalNotesCollection.getVitalSigns().getTemperature();
			temp = (temp != null && !temp.isEmpty() ? "Temperature("+VitalSignsUnit.TEMPERATURE.getUnit() +"): " + temp.trim(): "");
			if(!DPDoctorUtils.allStringsEmpty(temp)){
				if(!DPDoctorUtils.allStringsEmpty(vitalSigns))vitalSigns = vitalSigns+", "+temp;
				else vitalSigns = temp;
			}
	
			String breathing = clinicalNotesCollection.getVitalSigns().getBreathing();
			breathing = (breathing != null && !breathing.isEmpty() ? "Breathing("+VitalSignsUnit.BREATHING.getUnit() + "): " + breathing.trim(): "");
			if(!DPDoctorUtils.allStringsEmpty(breathing)){
				if(!DPDoctorUtils.allStringsEmpty(vitalSigns))vitalSigns = vitalSigns+", "+breathing;
				else vitalSigns = breathing;
			}
			
			String weight = clinicalNotesCollection.getVitalSigns().getWeight();
			weight = (weight != null && !weight.isEmpty() ? "Weight("+VitalSignsUnit.WEIGHT.getUnit() +"): " + weight.trim(): "");
			if(!DPDoctorUtils.allStringsEmpty(temp)){
				if(!DPDoctorUtils.allStringsEmpty(vitalSigns))vitalSigns = vitalSigns+", "+weight;
				else vitalSigns = weight;
			}
			
			String bloodPressure = "";
			if (clinicalNotesCollection.getVitalSigns().getBloodPressure() != null) {
			    String systolic = clinicalNotesCollection.getVitalSigns().getBloodPressure().getSystolic();
			    systolic = systolic != null && !systolic.isEmpty() ? systolic.trim() : "";
	
			    String diastolic = clinicalNotesCollection.getVitalSigns().getBloodPressure().getDiastolic();
			    diastolic = diastolic != null && !diastolic.isEmpty() ? diastolic.trim() : "";
	
			    if(!DPDoctorUtils.allStringsEmpty(systolic, diastolic))
			    	bloodPressure = "Blood Pressure("+VitalSignsUnit.BLOODPRESSURE.getUnit()+"): " + systolic + "/" + diastolic;
			    if(!DPDoctorUtils.allStringsEmpty(bloodPressure)){
					if(!DPDoctorUtils.allStringsEmpty(vitalSigns))vitalSigns = vitalSigns+", "+bloodPressure;
					else vitalSigns = bloodPressure;
				}
			}
			
			parameters.put("vitalSigns", vitalSigns != null && !vitalSigns.isEmpty() ? vitalSigns : null);
	    } else
		parameters.put("vitalSigns", null);

	    PrintSettingsCollection printSettings = printSettingsRepository.getSettings(clinicalNotesCollection.getDoctorId(), clinicalNotesCollection.getLocationId(), clinicalNotesCollection.getHospitalId(), ComponentType.ALL.getType());
		generatePatientDetails((printSettings != null && printSettings.getHeaderSetup() != null ? printSettings.getHeaderSetup().getPatientDetails() : null), patient, clinicalNotesCollection.getUniqueEmrId(), user.getFirstName(), user.getMobileNumber(), parameters);
		generatePrintSetup(parameters, printSettings, clinicalNotesCollection.getDoctorId());
		String pdfName = (user != null ? user.getFirstName() : "") + "CLINICALNOTES-"+ clinicalNotesCollection.getUniqueEmrId();

		String layout = printSettings != null ? (printSettings.getPageSetup() != null ? printSettings.getPageSetup().getLayout() : "PORTRAIT") : "PORTRAIT";
		String pageSize = printSettings != null	? (printSettings.getPageSetup() != null ? printSettings.getPageSetup().getPageSize() : "A4") : "A4";
		Integer topMargin = printSettings != null	? (printSettings.getPageSetup() != null ? printSettings.getPageSetup().getTopMargin() : null) : null;
		Integer bottonMargin = printSettings != null	? (printSettings.getPageSetup() != null ? printSettings.getPageSetup().getBottomMargin() : null) : null;
		if(pageSize.equalsIgnoreCase("A5")){
			response = jasperReportService.createPDF(parameters, clinicalNotesA5FileName, layout, pageSize, topMargin, bottonMargin, Integer.parseInt(parameters.get("contentFontSize").toString()), pdfName.replaceAll("\\s+", ""));	
		}else {
			response = jasperReportService.createPDF(parameters, clinicalNotesA4FileName, layout, pageSize, topMargin, bottonMargin, Integer.parseInt(parameters.get("contentFontSize").toString()), pdfName.replaceAll("\\s+", ""));
		}
		return response;
	}
	
	private void generatePrintSetup(Map<String, Object> parameters, PrintSettingsCollection printSettings, ObjectId doctorId) {
		parameters.put("printSettingsId", printSettings != null ? printSettings.getId().toString() : "");
		String headerLeftText = "", headerRightText = "", footerBottomText = "", logoURL = "";
		int headerLeftTextLength = 0, headerRightTextLength = 0;
		Integer contentFontSize = 10;
		if (printSettings != null) {
			if(printSettings.getContentSetup() != null){
				contentFontSize = !DPDoctorUtils.anyStringEmpty(printSettings.getContentSetup().getFontSize()) ? Integer.parseInt(printSettings.getContentSetup().getFontSize().replaceAll("pt", "")) : 10;
			}
			if (printSettings.getHeaderSetup() != null &&  printSettings.getHeaderSetup().getCustomHeader()) {
				if(printSettings.getHeaderSetup().getTopLeftText() != null)
				for (PrintSettingsText str : printSettings.getHeaderSetup().getTopLeftText()) {

					boolean isBold = containsIgnoreCase(FONTSTYLE.BOLD.getStyle(), str.getFontStyle());
					boolean isItalic = containsIgnoreCase(FONTSTYLE.ITALIC.getStyle(), str.getFontStyle());
					if (!DPDoctorUtils.anyStringEmpty(str.getText())) {
						headerLeftTextLength++;
						String text = str.getText();
						if (isItalic)text = "<i>" + text + "</i>";
						if (isBold)text = "<b>" + text + "</b>";

						if (headerLeftText.isEmpty())headerLeftText = "<span style='font-size:" + str.getFontSize() + "'>" + text + "</span>";
						else headerLeftText = headerLeftText + "<br/>" + "<span style='font-size:" + str.getFontSize() + "'>" + text + "</span>";
					}
				}
				if(printSettings.getHeaderSetup().getTopRightText() != null)
				for (PrintSettingsText str : printSettings.getHeaderSetup().getTopRightText()) {

					boolean isBold = containsIgnoreCase(FONTSTYLE.BOLD.getStyle(), str.getFontStyle());
					boolean isItalic = containsIgnoreCase(FONTSTYLE.ITALIC.getStyle(), str.getFontStyle());
					
					if (!DPDoctorUtils.anyStringEmpty(str.getText())) {
						headerRightTextLength++;
						String text = str.getText();
						if (isItalic)text = "<i>" + text + "</i>";
						if (isBold)text = "<b>" + text + "</b>";

						if (headerRightText.isEmpty())headerRightText = "<span style='font-size:" + str.getFontSize() + "'>" + text + "</span>";
						else headerRightText = headerRightText + "<br/>" + "<span style='font-size:" + str.getFontSize() + "'>" + text + "</span>";
					}
				}
			}
			
			if (printSettings.getHeaderSetup() != null  && printSettings.getHeaderSetup().getCustomHeader() &&  printSettings.getHeaderSetup().getCustomLogo() && printSettings.getClinicLogoUrl() != null) {
				logoURL = getFinalImageURL(printSettings.getClinicLogoUrl());
			}
			
			if (printSettings.getFooterSetup() != null && printSettings.getFooterSetup().getCustomFooter()) {
				for (PrintSettingsText str : printSettings.getFooterSetup().getBottomText()) {
					boolean isBold = containsIgnoreCase(FONTSTYLE.BOLD.getStyle(), str.getFontStyle());
					boolean isItalic = containsIgnoreCase(FONTSTYLE.ITALIC.getStyle(), str.getFontStyle());
					String text = str.getText();
					if (isItalic)text = "<i>" + text + "</i>";
					if (isBold)text = "<b>" + text + "</b>";

					if (footerBottomText.isEmpty())	footerBottomText = "<span style='font-size:" + str.getFontSize() + "'>" + text + "</span>";
					else footerBottomText = footerBottomText + "" + "<span style='font-size:" + str.getFontSize() + "'>" + text + "</span>";
				}
			}
			
			if (printSettings.getFooterSetup() != null && printSettings.getFooterSetup().getShowSignature()) {
				UserCollection doctorUser = userRepository.findOne(doctorId);
				if (doctorUser != null)	parameters.put("footerSignature", doctorUser.getTitle() + " " + doctorUser.getFirstName());	
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

	private void generatePatientDetails(PatientDetails patientDetails, PatientCollection patient, String uniqueEMRId, String firstName, String mobileNumber, Map<String, Object> parameters) {
		String age = null, gender = (patient != null && patient.getGender() != null ? patient.getGender() : null), patientLeftText = "", patientRightText = "";
		if(patientDetails == null){
			patientDetails = new PatientDetails();
		}
		List<String> patientDetailList = new ArrayList<String>();
		patientDetailList.add("<b>Patient Name:</b> " + firstName);
		patientDetailList.add("<b>Patient Id: </b>" + (patient != null && patient.getPID() != null ? patient.getPID() : "--"));
		patientDetailList.add("<b>CID: </b>"+ (uniqueEMRId != null ? uniqueEMRId : "--"));
		patientDetailList.add("<b>Mobile: </b>" + (mobileNumber != null && mobileNumber != null ? mobileNumber : "--"));
		
		if (patient != null && patient.getDob() != null) {
			Age ageObj = patient.getDob().getAge();
			if (ageObj.getYears() > 14)age = ageObj.getYears() + "yrs";
			else {
				if(ageObj.getYears()>0)age = ageObj.getYears() + "yrs";
				if(ageObj.getMonths()>0){
					if(DPDoctorUtils.anyStringEmpty(age))age = ageObj.getMonths()+ "months";
					else age = age+" "+ageObj.getMonths()+ " months";
				}
				if(ageObj.getDays()>0){
					if(DPDoctorUtils.anyStringEmpty(age))age = ageObj.getDays()+ "days";
					else age = age+" "+ageObj.getDays()+ "days";
				}
			}
		}
		
		if(patientDetails.getShowDOB()){
			if(!DPDoctorUtils.allStringsEmpty(age, gender))patientDetailList.add("<b>Age | Gender: </b>"+age+" | "+gender);
			else if(!DPDoctorUtils.anyStringEmpty(age))patientDetailList.add("<b>Age | Gender: </b>"+age+" | --");
			else if(!DPDoctorUtils.anyStringEmpty(gender))patientDetailList.add("<b>Age | Gender: </b>-- | "+gender);
		}
                
        if(patientDetails.getShowBloodGroup() && patient != null && !DPDoctorUtils.anyStringEmpty(patient.getBloodGroup())){
        	patientDetailList.add("<b>Blood Group: </b>" + patient.getBloodGroup());
        }
        
        if(patientDetails.getShowReferedBy() && patient != null && patient.getReferredBy() != null){
        	ReferencesCollection referencesCollection = referenceRepository.findOne(patient.getReferredBy());
    		if (referencesCollection != null && !DPDoctorUtils.anyStringEmpty(referencesCollection.getReference()))
    			patientDetailList.add("<b>Referred By: </b>" + referencesCollection.getReference());
    		}
        patientDetailList.add("<b>Date: </b>" + new SimpleDateFormat("dd-MM-yyyy").format(new Date()));
		
		boolean isBold = patientDetails.getStyle() != null && patientDetails.getStyle().getFontStyle() != null? containsIgnoreCase(FONTSTYLE.BOLD.getStyle(), patientDetails.getStyle().getFontStyle()) : false;
		boolean isItalic = patientDetails.getStyle() != null && patientDetails.getStyle().getFontStyle() != null? containsIgnoreCase(FONTSTYLE.ITALIC.getStyle(), patientDetails.getStyle().getFontStyle()) : false;
		String fontSize = patientDetails.getStyle() != null && patientDetails.getStyle().getFontSize() != null ? patientDetails.getStyle().getFontSize() : "";

		for(int i = 0; i < patientDetailList.size();i++){
			String text = patientDetailList.get(i);
			if(isItalic)text = "<i>"+text+"</i>";
			if(isBold)text = "<b>"+text+"</b>";
			text = "<span style='font-size:" + fontSize + "'>" + text + "</span>";
			
			if (i % 2 == 0){
				if(!DPDoctorUtils.anyStringEmpty(patientLeftText))patientLeftText = patientLeftText+"<br>"+text;
				else patientLeftText = text;
			}
			else {
				if(!DPDoctorUtils.anyStringEmpty(patientRightText))patientRightText = patientRightText+"<br>"+text;
				else patientRightText = text;
			}
		}
		parameters.put("patientLeftText", patientLeftText);
		parameters.put("patientRightText", patientRightText);
	}

   private List<Complaint> getCustomGlobalComplaintsForAdmin(int page, int size, String updatedTime, Boolean discarded, String searchTerm) {
	List<Complaint> response = null;
	try {
		AggregationResults<Complaint> results = mongoTemplate.aggregate(DPDoctorUtils.createCustomGlobalAggregationForAdmin(page, size, updatedTime, discarded, searchTerm, "complaint"), ComplaintCollection.class, Complaint.class); 
		response = results.getMappedResults();
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Complaints");
	}
	return response;

    }

	private List<Complaint> getGlobalComplaintsForAdmin(int page, int size, String updatedTime, Boolean discarded, String searchTerm) {
	List<Complaint> response = null;
	try {
		AggregationResults<Complaint> results = mongoTemplate.aggregate(DPDoctorUtils.createCustomGlobalAggregationForAdmin(page, size, updatedTime, discarded, searchTerm, "complaint"), ComplaintCollection.class, Complaint.class); 
		response = results.getMappedResults();		
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Complaints");
	}
	return response;
    }

    private List<Complaint> getCustomComplaintsForAdmin(int page, int size, String updatedTime, Boolean discarded, String searchTerm) {
	List<Complaint> response = null;
	try {
		AggregationResults<Complaint> results = mongoTemplate.aggregate(DPDoctorUtils.createCustomAggregationForAdmin(page, size, updatedTime, discarded, searchTerm, "complaint"), ComplaintCollection.class, Complaint.class); 
		response = results.getMappedResults();
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Complaints");
	}
	return response;
    }

	private List<Investigation> getCustomGlobalInvestigationsForAdmin(int page, int size, String updatedTime, Boolean discarded, String searchTerm) {
	List<Investigation> response = null;
	try {
		AggregationResults<Investigation> results = mongoTemplate.aggregate(DPDoctorUtils.createCustomGlobalAggregationForAdmin(page, size, updatedTime, discarded, searchTerm, "investigation"), InvestigationCollection.class, Investigation.class); 
		response = results.getMappedResults();
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Investigations");
	}
	return response;
    }

    private List<Investigation> getGlobalInvestigationsForAdmin(int page, int size, String updatedTime, Boolean discarded, String searchTerm) {
	List<Investigation> response = null;
	
	try {
		AggregationResults<Investigation> results = mongoTemplate.aggregate(DPDoctorUtils.createGlobalAggregationForAdmin(page, size, updatedTime, discarded, searchTerm, "investigation"), InvestigationCollection.class, Investigation.class); 
		response = results.getMappedResults();
		
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Investigations");
	}
	return response;
    }

    private List<Investigation> getCustomInvestigationsForAdmin(int page, int size, String updatedTime, Boolean discarded, String searchTerm) {
	List<Investigation> response = null;
	try {
		AggregationResults<Investigation> results = mongoTemplate.aggregate(DPDoctorUtils.createCustomAggregationForAdmin(page, size, updatedTime, discarded, searchTerm, "investigation"), InvestigationCollection.class, Investigation.class); 
		response = results.getMappedResults();
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Investigations");
	}
	return response;
    }

    private List<Observation> getCustomGlobalObservationsForAdmin(int page, int size, String updatedTime, Boolean discarded, String searchTerm) {
	List<Observation> response = null;
	try {
		AggregationResults<Observation> results = mongoTemplate.aggregate(DPDoctorUtils.createCustomGlobalAggregationForAdmin(page, size, updatedTime, discarded, searchTerm, "observation"), ObservationCollection.class, Observation.class); 
		response = results.getMappedResults();
		
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Observations");
	}
	return response;

    }

    private List<Observation> getGlobalObservationsForAdmin(int page, int size, String updatedTime, Boolean discarded, String searchTerm) {
	List<Observation> response = null;
	try {

		AggregationResults<Observation> results = mongoTemplate.aggregate(DPDoctorUtils.createGlobalAggregationForAdmin(page, size, updatedTime, discarded, searchTerm, "observation"), ObservationCollection.class, Observation.class); 
		response = results.getMappedResults();
		
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Observations");
	}
	return response;
    }

    private List<Observation> getCustomObservationsForAdmin(int page, int size, String updatedTime, Boolean discarded, String searchTerm) {
	List<Observation> response = null;
	
	try {
		AggregationResults<Observation> results = mongoTemplate.aggregate(DPDoctorUtils.createCustomAggregationForAdmin(page, size, updatedTime, discarded, searchTerm, "observation"), ObservationCollection.class, Observation.class); 
		response = results.getMappedResults();
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Observations");
	}
	return response;
    }

    private List<Diagnoses> getCustomGlobalDiagnosisForAdmin(int page, int size, String updatedTime, Boolean discarded, String searchTerm) {
	List<Diagnoses> response = null;
	try {
		AggregationResults<Diagnoses> results = mongoTemplate.aggregate(DPDoctorUtils.createCustomGlobalAggregationForAdmin(page, size, updatedTime, discarded, searchTerm, "diagnosis"), DiagnosisCollection.class, Diagnoses.class); 
		response = results.getMappedResults();
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Diagnosis");
	}
	return response;

    }

    private List<Diagnoses> getGlobalDiagnosisForAdmin(int page, int size, String updatedTime, Boolean discarded, String searchTerm) {
	List<Diagnoses> response = null;
	try {
		AggregationResults<Diagnoses> results = mongoTemplate.aggregate(DPDoctorUtils.createGlobalAggregationForAdmin(page, size, updatedTime, discarded, searchTerm, "diagnosis"), DiagnosisCollection.class, Diagnoses.class); 
		response = results.getMappedResults();
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Diagnosis");
	}
	return response;
    }

    private List<Diagnoses> getCustomDiagnosisForAdmin(int page, int size, String updatedTime, Boolean discarded, String searchTerm) {
	List<Diagnoses> response = null;
	try {
		AggregationResults<Diagnoses> results = mongoTemplate.aggregate(DPDoctorUtils.createCustomAggregationForAdmin(page, size, updatedTime, discarded, searchTerm, "diagnosis"), DiagnosisCollection.class, Diagnoses.class); 
		response = results.getMappedResults();
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Diagnosis");
	}
	return response;
    }

    private List<Notes> getCustomGlobalNotesForAdmin(int page, int size, String updatedTime, Boolean discarded, String searchTerm) {
	List<Notes> response = null;
	try {
		AggregationResults<Notes> results = mongoTemplate.aggregate(DPDoctorUtils.createCustomGlobalAggregationForAdmin(page, size, updatedTime, discarded, searchTerm, "note"), NotesCollection.class, Notes.class); 
		response = results.getMappedResults();
		
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Notes");
	}
	return response;

    }

    private List<Notes> getGlobalNotesForAdmin(int page, int size, String updatedTime, Boolean discarded, String searchTerm) {
	List<Notes> response = null;
	try {
		AggregationResults<Notes> results = mongoTemplate.aggregate(DPDoctorUtils.createGlobalAggregationForAdmin(page, size, updatedTime, discarded, searchTerm, "note"), NotesCollection.class, Notes.class); 
		response = results.getMappedResults();
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Notes");
	}
	return response;
    }

    private List<Notes> getCustomNotesForAdmin(int page, int size, String updatedTime, Boolean discarded, String searchTerm) {
	List<Notes> response = null;
	try {
		AggregationResults<Notes> results = mongoTemplate.aggregate(DPDoctorUtils.createCustomAggregationForAdmin(page, size, updatedTime, discarded, searchTerm, "note"), NotesCollection.class, Notes.class); 
		response = results.getMappedResults();
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Notes");
	}
	return response;
    }

    private List<Diagram> getCustomGlobalDiagramsForAdmin(int page, int size, String updatedTime, Boolean discarded, String searchTerm) {
	List<Diagram> response = null;
	try {
		AggregationResults<Diagram> results = mongoTemplate.aggregate(DPDoctorUtils.createCustomGlobalAggregationForAdmin(page, size, updatedTime, discarded, searchTerm, "tags"), DiagramsCollection.class, Diagram.class); 
		response = results.getMappedResults();
		
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Diagrams");
	}
	return response;

    }

    private List<Diagram> getGlobalDiagramsForAdmin(int page, int size, String updatedTime, Boolean discarded, String searchTerm) {
	List<Diagram> response = null;
	try {
		AggregationResults<Diagram> results = mongoTemplate.aggregate(DPDoctorUtils.createGlobalAggregationForAdmin(page, size, updatedTime, discarded, searchTerm, "tags"), DiagramsCollection.class, Diagram.class); 
		response = results.getMappedResults();
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Diagrams");
	}
	return response;
    }

    private List<Diagram> getCustomDiagramsForAdmin(int page, int size, String updatedTime, Boolean discarded, String searchTerm) {
	List<Diagram> response = null;
	boolean[] discards = new boolean[2];
	discards[0] = false;
	try {
		AggregationResults<Diagram> results = mongoTemplate.aggregate(DPDoctorUtils.createCustomAggregationForAdmin(page, size, updatedTime, discarded, searchTerm, "tags"), DiagramsCollection.class, Diagram.class); 
		response = results.getMappedResults();
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Diagrams");
	}
	return response;
    }
}
