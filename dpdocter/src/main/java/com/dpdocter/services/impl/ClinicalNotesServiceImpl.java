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

import javax.mail.MessagingException;

import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
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
import com.dpdocter.collections.NotesCollection;
import com.dpdocter.collections.ObservationCollection;
import com.dpdocter.collections.PatientCollection;
import com.dpdocter.collections.PatientVisitCollection;
import com.dpdocter.collections.PrintSettingsCollection;
import com.dpdocter.collections.ReferencesCollection;
import com.dpdocter.collections.SpecialityCollection;
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
import com.dpdocter.services.ClinicalNotesService;
import com.dpdocter.services.EmailTackService;
import com.dpdocter.services.FileManager;
import com.dpdocter.services.JasperReportService;
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

     @Value(value = "${image.path}")
     private String imagePath;
     
     @Value(value = "${ClinicalNotes.getPatientsClinicalNotesWithVerifiedOTP}")
     private String getPatientsClinicalNotesWithVerifiedOTP;
    
    @Override
    @Transactional
    @SuppressWarnings("unchecked")
    public ClinicalNotes addNotes(ClinicalNotesAddRequest request) {
	ClinicalNotes clinicalNotes = null;
	List<String> complaintIds = null;
	List<String> observationIds = null;
	List<String> investigationIds = null;
	List<String> noteIds = null;
	List<String> diagnosisIds = null;
	List<String> diagramIds = null;
	Date createdTime = new Date();

	try {
	    // save clinical notes.
	    ClinicalNotesCollection clinicalNotesCollection = new ClinicalNotesCollection();
	    BeanUtil.map(request, clinicalNotesCollection);

	    complaintIds = new ArrayList<String>();
	    if (request.getComplaints() != null && !request.getComplaints().isEmpty()) {
		for (ClinicalNotesComplaint complaint : request.getComplaints()) {
		    if (DPDoctorUtils.anyStringEmpty(complaint.getId())) {
			ComplaintCollection complaintCollection = new ComplaintCollection();
			BeanUtil.map(complaint, complaintCollection);
			BeanUtil.map(request, complaintCollection);
			complaintCollection.setCreatedTime(createdTime);
			complaintCollection.setId(null);
			complaintCollection = complaintRepository.save(complaintCollection);
			
			transactionalManagementService.addResource(complaintCollection.getId(), Resource.COMPLAINT, false);
			ESComplaintsDocument esComplaints = new ESComplaintsDocument();
			BeanUtil.map(complaintCollection, esComplaints);
			esClinicalNotesService.addComplaints(esComplaints);
			
			complaintIds.add(complaintCollection.getId());
		    } else {
			complaintIds.add(complaint.getId());
		    }
		}
	    }

	    observationIds = new ArrayList<String>();
	    if (request.getObservations() != null && !request.getObservations().isEmpty()) {
		for (ClinicalNotesObservation observation : request.getObservations()) {
		    if (DPDoctorUtils.anyStringEmpty(observation.getId())) {
			ObservationCollection observationCollection = new ObservationCollection();
			BeanUtil.map(observation, observationCollection);
			BeanUtil.map(request, observationCollection);
			observationCollection.setCreatedTime(createdTime);
			observationCollection.setId(null);
			observationCollection = observationRepository.save(observationCollection);

			transactionalManagementService.addResource(observation.getId(), Resource.OBSERVATION, false);
			ESObservationsDocument esObservations = new ESObservationsDocument();
			BeanUtil.map(observation, esObservations);
			esClinicalNotesService.addObservations(esObservations);
			
			observationIds.add(observationCollection.getId());
		    } else {
			observationIds.add(observation.getId());
		    }
		}
	    }

	    investigationIds = new ArrayList<String>();
	    if (request.getInvestigations() != null && !request.getInvestigations().isEmpty()) {
		for (ClinicalNotesInvestigation investigation : request.getInvestigations()) {
		    if (DPDoctorUtils.anyStringEmpty(investigation.getId())) {
			InvestigationCollection investigationCollection = new InvestigationCollection();
			BeanUtil.map(investigation, investigationCollection);
			BeanUtil.map(request, investigationCollection);
			investigationCollection.setCreatedTime(createdTime);
			investigationCollection.setId(null);
			investigationCollection = investigationRepository.save(investigationCollection);

			transactionalManagementService.addResource(investigation.getId(), Resource.INVESTIGATION, false);
			ESInvestigationsDocument esInvestigations = new ESInvestigationsDocument();
			BeanUtil.map(investigation, esInvestigations);
			esClinicalNotesService.addInvestigations(esInvestigations);
			
			investigationIds.add(investigationCollection.getId());
		    } else {
			investigationIds.add(investigation.getId());
		    }
		}
	    }

	    noteIds = new ArrayList<String>();
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
			noteIds.add(note.getId());
		    }
		}
	    }

	    diagnosisIds = new ArrayList<String>();
	    if (request.getDiagnoses() != null && !request.getDiagnoses().isEmpty()) {
		for (ClinicalNotesDiagnosis diagnosis : request.getDiagnoses()) {
		    if (DPDoctorUtils.anyStringEmpty(diagnosis.getId())) {
			DiagnosisCollection diagnosisCollection = new DiagnosisCollection();
			BeanUtil.map(diagnosis, diagnosisCollection);
			BeanUtil.map(request, diagnosisCollection);
			diagnosisCollection.setCreatedTime(createdTime);
			diagnosisCollection.setId(null);
			diagnosisCollection = diagnosisRepository.save(diagnosisCollection);
			
			transactionalManagementService.addResource(diagnosis.getId(), Resource.DIAGNOSIS, false);
			ESDiagnosesDocument esDiagnoses = new ESDiagnosesDocument();
			BeanUtil.map(diagnosis, esDiagnoses);
			esClinicalNotesService.addDiagnoses(esDiagnoses);
			
			diagnosisIds.add(diagnosisCollection.getId());
		    } else {
			diagnosisIds.add(diagnosis.getId());
		    }
		}
	    }

	    clinicalNotesCollection.setComplaints(complaintIds);
	    clinicalNotesCollection.setInvestigations(investigationIds);
	    clinicalNotesCollection.setObservations(observationIds);
	    clinicalNotesCollection.setDiagnoses(diagnosisIds);
	    clinicalNotesCollection.setNotes(noteIds);
	    if (request.getDiagrams() == null) {
		diagramIds = new ArrayList<String>();
		clinicalNotesCollection.setDiagrams(diagramIds);
	    } else {
		diagramIds = request.getDiagrams();
	    }
	    clinicalNotesCollection.setUniqueEmrId(UniqueIdInitial.CLINICALNOTES.getInitial() + DPDoctorUtils.generateRandomId());
	    clinicalNotesCollection.setCreatedTime(createdTime);
	    UserCollection userCollection = userRepository.findOne(clinicalNotesCollection.getDoctorId());
	    if (userCollection != null) {
		clinicalNotesCollection
			.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "") + userCollection.getFirstName());
	    }
	    clinicalNotesCollection = clinicalNotesRepository.save(clinicalNotesCollection);

	    clinicalNotes = new ClinicalNotes();
	    BeanUtil.map(clinicalNotesCollection, clinicalNotes);

	    List<Complaint> complaints = IteratorUtils.toList(complaintRepository.findAll(complaintIds).iterator());
	    List<Investigation> investigations = IteratorUtils.toList(investigationRepository.findAll(investigationIds).iterator());
	    List<Observation> observations = IteratorUtils.toList(observationRepository.findAll(observationIds).iterator());
	    List<Diagnoses> diagnoses = IteratorUtils.toList(diagnosisRepository.findAll(diagnosisIds).iterator());
	    List<Notes> notes = IteratorUtils.toList(notesRepository.findAll(noteIds).iterator());
	    List<DiagramsCollection> diagramsCollections = IteratorUtils.toList(diagramsRepository.findAll(diagramIds).iterator());
	    List<Diagram> diagrams = new ArrayList<Diagram>();
	    if (diagramsCollections != null) {
		for (DiagramsCollection collection : diagramsCollections) {
		    Diagram diagram = new Diagram();
		    BeanUtil.map(collection, diagram);
		    diagrams.add(diagram);
		}
	    }
	    clinicalNotes.setComplaints(complaints);
	    clinicalNotes.setInvestigations(investigations);
	    clinicalNotes.setObservations(observations);
	    clinicalNotes.setDiagnoses(diagnoses);
	    clinicalNotes.setNotes(notes);
	    clinicalNotes.setDiagrams(diagrams);
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
	    ClinicalNotesCollection clinicalNotesCollection = clinicalNotesRepository.findOne(id);
	    if (clinicalNotesCollection != null) {
		clinicalNote = new ClinicalNotes();
		BeanUtil.map(clinicalNotesCollection, clinicalNote);
		@SuppressWarnings("unchecked")
		List<ComplaintCollection> complaintCollections = IteratorUtils
			.toList(complaintRepository.findAll(clinicalNotesCollection.getComplaints()).iterator());
		if (complaintCollections != null) {
		    List<Complaint> complaints = new ArrayList<Complaint>();
		    for (ComplaintCollection complaintCollection : complaintCollections) {
			Complaint complaint = new Complaint();
			complaint.setComplaint(complaintCollection.getComplaint());
			BeanUtil.map(complaintCollection, complaint);
			// complaint.setDoctorId(null);
			// complaint.setHospitalId(null);
			// complaint.setLocationId(null);
			complaints.add(complaint);
		    }
		    clinicalNote.setComplaints(complaints);
		}
		@SuppressWarnings("unchecked")
		List<ObservationCollection> observationCollections = IteratorUtils
			.toList(observationRepository.findAll(clinicalNotesCollection.getObservations()).iterator());
		if (observationCollections != null) {
		    List<Observation> observations = new ArrayList<Observation>();
		    for (ObservationCollection observationCollection : observationCollections) {
			Observation observation = new Observation();
			BeanUtil.map(observationCollection, observation);
			// observation.setDoctorId(null);
			// observation.setHospitalId(null);
			// observation.setLocationId(null);
			observations.add(observation);
		    }
		    clinicalNote.setObservations(observations);
		}
		@SuppressWarnings("unchecked")
		List<InvestigationCollection> investigationCollections = IteratorUtils
			.toList(investigationRepository.findAll(clinicalNotesCollection.getInvestigations()).iterator());
		if (investigationCollections != null) {
		    List<Investigation> investigations = new ArrayList<Investigation>();
		    for (InvestigationCollection investigationCollection : investigationCollections) {
			Investigation investigation = new Investigation();
			BeanUtil.map(investigationCollection, investigation);
			// investigation.setDoctorId(null);
			// investigation.setHospitalId(null);
			// investigation.setLocationId(null);
			investigations.add(investigation);
		    }
		    clinicalNote.setInvestigations(investigations);
		}
		@SuppressWarnings("unchecked")
		List<DiagnosisCollection> diagnosisCollections = IteratorUtils
			.toList(diagnosisRepository.findAll(clinicalNotesCollection.getDiagnoses()).iterator());
		if (diagnosisCollections != null) {
		    List<Diagnoses> diagnosisList = new ArrayList<Diagnoses>();
		    for (DiagnosisCollection diagnosisCollection : diagnosisCollections) {
			Diagnoses diagnosis = new Diagnoses();
			BeanUtil.map(diagnosisCollection, diagnosis);
			// diagnosis.setDoctorId(null);
			// diagnosis.setHospitalId(null);
			// diagnosis.setLocationId(null);
			diagnosisList.add(diagnosis);
		    }
		    clinicalNote.setDiagnoses(diagnosisList);
		}

		@SuppressWarnings("unchecked")
		List<NotesCollection> notesCollections = IteratorUtils.toList(notesRepository.findAll(clinicalNotesCollection.getNotes()).iterator());
		if (notesCollections != null) {
		    List<Notes> notes = new ArrayList<Notes>();
		    for (NotesCollection notesCollection : notesCollections) {
			Notes note = new Notes();
			BeanUtil.map(notesCollection, note);
			// note.setDoctorId(null);
			// note.setLocationId(null);
			// note.setHospitalId(null);
			notes.add(note);
		    }
		    clinicalNote.setNotes(notes);
		}
		if (clinicalNotesCollection.getDiagrams() != null) {
		    @SuppressWarnings("unchecked")
		    List<DiagramsCollection> diagramsCollections = IteratorUtils
			    .toList(diagramsRepository.findAll(clinicalNotesCollection.getDiagrams()).iterator());
		    if (diagramsCollections != null) {
			List<Diagram> diagrams = new ArrayList<Diagram>();
			for (DiagramsCollection diagramsCollection : diagramsCollections) {
			    Diagram diagram = new Diagram();
			    BeanUtil.map(diagramsCollection, diagram);
			    // diagram.setDoctorId(null);
			    // diagram.setHospitalId(null);
			    // diagram.setLocationId(null);
			    diagrams.add(diagram);
			}
			clinicalNote.setDiagrams(diagrams);
		    }
		}

		PatientVisitCollection patientVisitCollection = patientVisitRepository.findByClinialNotesId(clinicalNote.getId());
		if (patientVisitCollection != null)
		    clinicalNote.setVisitId(patientVisitCollection.getId());
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
	return clinicalNote;
    }

    @SuppressWarnings("unchecked")
    @Override
    @Transactional
    public ClinicalNotes editNotes(ClinicalNotesEditRequest request) {
	ClinicalNotes clinicalNotes = null;
	List<String> complaintIds = null;
	List<String> observationIds = null;
	List<String> investigationIds = null;
	List<String> noteIds = null;
	List<String> diagnosisIds = null;
	List<String> diagramIds = null;
	Date createdTime = new Date();

	try {
	    // save clinical notes.
	    ClinicalNotesCollection clinicalNotesCollection = new ClinicalNotesCollection();
	    BeanUtil.map(request, clinicalNotesCollection);

	    complaintIds = new ArrayList<String>();
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
			complaintIds.add(complaint.getId());
		    }
		}
	    }

	    observationIds = new ArrayList<String>();
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
			observationIds.add(observation.getId());
		    }
		}
	    }

	    investigationIds = new ArrayList<String>();
	    if (request.getInvestigations() != null && !request.getInvestigations().isEmpty()) {
		for (ClinicalNotesInvestigation investigation : request.getInvestigations()) {
		    if (DPDoctorUtils.anyStringEmpty(investigation.getId())) {
			InvestigationCollection investigationCollection = new InvestigationCollection();
			BeanUtil.map(investigation, investigationCollection);
			BeanUtil.map(request, investigationCollection);
			investigationCollection.setCreatedTime(createdTime);
			investigationCollection.setId(null);
			investigationCollection = investigationRepository.save(investigationCollection);
			
			transactionalManagementService.addResource(investigation.getId(), Resource.INVESTIGATION, false);
			ESInvestigationsDocument esInvestigations = new ESInvestigationsDocument();
			BeanUtil.map(investigation, esInvestigations);
			esClinicalNotesService.addInvestigations(esInvestigations);
			
			investigationIds.add(investigationCollection.getId());
		    } else {
			investigationIds.add(investigation.getId());
		    }
		}
	    }

	    noteIds = new ArrayList<String>();
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
			noteIds.add(note.getId());
		    }
		}
	    }

	    diagnosisIds = new ArrayList<String>();
	    if (request.getDiagnoses() != null && !request.getDiagnoses().isEmpty()) {
		for (ClinicalNotesDiagnosis diagnosis : request.getDiagnoses()) {
		    if (DPDoctorUtils.anyStringEmpty(diagnosis.getId())) {
			DiagnosisCollection diagnosisCollection = new DiagnosisCollection();
			BeanUtil.map(diagnosis, diagnosisCollection);
			BeanUtil.map(request, diagnosisCollection);
			diagnosisCollection.setCreatedTime(createdTime);
			diagnosisCollection.setId(null);
			diagnosisCollection = diagnosisRepository.save(diagnosisCollection);
			
			transactionalManagementService.addResource(diagnosis.getId(), Resource.DIAGNOSIS, false);
			ESDiagnosesDocument esDiagnoses = new ESDiagnosesDocument();
			BeanUtil.map(diagnosis, esDiagnoses);
			esClinicalNotesService.addDiagnoses(esDiagnoses);
			
			diagnosisIds.add(diagnosisCollection.getId());
		    } else {
			diagnosisIds.add(diagnosis.getId());
		    }
		}
	    }

	    clinicalNotesCollection.setComplaints(complaintIds);
	    clinicalNotesCollection.setInvestigations(investigationIds);
	    clinicalNotesCollection.setObservations(observationIds);
	    clinicalNotesCollection.setDiagnoses(diagnosisIds);
	    clinicalNotesCollection.setNotes(noteIds);
	    if (request.getDiagrams() == null) {
		diagramIds = new ArrayList<String>();
		clinicalNotesCollection.setDiagrams(diagramIds);
	    } else {
		diagramIds = request.getDiagrams();
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

	    // Setting detail of complaints, investigations, observations,
	    // diagnoses, notes and diagrams into response.
	    List<Complaint> complaints = IteratorUtils.toList(complaintRepository.findAll(complaintIds).iterator());
	    List<Investigation> investigations = IteratorUtils.toList(investigationRepository.findAll(investigationIds).iterator());
	    List<Observation> observations = IteratorUtils.toList(observationRepository.findAll(observationIds).iterator());
	    List<Diagnoses> diagnoses = IteratorUtils.toList(diagnosisRepository.findAll(diagnosisIds).iterator());
	    List<Notes> notes = IteratorUtils.toList(notesRepository.findAll(noteIds).iterator());
	    List<DiagramsCollection> diagramsCollections = IteratorUtils.toList(diagramsRepository.findAll(diagramIds).iterator());
	    List<Diagram> diagrams = new ArrayList<Diagram>();
	    if (diagramsCollections != null) {
		for (DiagramsCollection collection : diagramsCollections) {
		    Diagram diagram = new Diagram();
		    BeanUtil.map(collection, diagram);
		    diagrams.add(diagram);
		}
	    }

	    clinicalNotes.setComplaints(complaints);
	    clinicalNotes.setInvestigations(investigations);
	    clinicalNotes.setObservations(observations);
	    clinicalNotes.setDiagnoses(diagnoses);
	    clinicalNotes.setNotes(notes);
	    clinicalNotes.setDiagrams(diagrams);
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
	    ClinicalNotesCollection clinicalNotes = clinicalNotesRepository.findOne(id);
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
		inHistorys[1] = true;

		try {
		    if (discarded)discards[1] = true;
		    if (!inHistory)inHistorys[1] = false;

		    long createdTimestamp = Long.parseLong(updatedTime);

		    if (!isOTPVerified) {
			if (locationId == null && hospitalId == null) {
			    if (size > 0)
				clinicalNotesCollections = clinicalNotesRepository.getClinicalNotes(doctorId, patientId, new Date(createdTimestamp), discards, inHistorys,
					new PageRequest(page, size, Direction.DESC, "createdTime"));
			    else
				clinicalNotesCollections = clinicalNotesRepository.getClinicalNotes(doctorId, patientId, new Date(createdTimestamp), discards, inHistorys,
					new Sort(Sort.Direction.DESC, "createdTime"));
			} else {
			    if (size > 0)
				clinicalNotesCollections = clinicalNotesRepository.getClinicalNotes(doctorId, hospitalId, locationId, patientId,
					new Date(createdTimestamp), discards, inHistorys, new PageRequest(page, size, Direction.DESC, "createdTime"));
			    else
				clinicalNotesCollections = clinicalNotesRepository.getClinicalNotes(doctorId, hospitalId, locationId, patientId,
					new Date(createdTimestamp), discards, inHistorys, new Sort(Sort.Direction.DESC, "createdTime"));
			}
		    } else {
			if (size > 0)
			    clinicalNotesCollections = clinicalNotesRepository.getClinicalNotes(patientId, new Date(createdTimestamp), discards, inHistorys,
				    new PageRequest(page, size, Direction.DESC, "createdTime"));
			else
			    clinicalNotesCollections = clinicalNotesRepository.getClinicalNotes(patientId, new Date(createdTimestamp), discards, inHistorys, new Sort(
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
			@SuppressWarnings("unchecked")
			List<ComplaintCollection> complaintCollections = IteratorUtils
				.toList(complaintRepository.findAll(clinicalNotesCollection.getComplaints()).iterator());
			if (complaintCollections != null) {
			    List<Complaint> complaints = new ArrayList<Complaint>();
			    for (ComplaintCollection complaintCollection : complaintCollections) {
				Complaint complaint = new Complaint();
				complaint.setComplaint(complaintCollection.getComplaint());
				BeanUtil.map(complaintCollection, complaint);
				complaints.add(complaint);
			    }
			    clinicalNote.setComplaints(complaints);
			}
			@SuppressWarnings("unchecked")
			List<ObservationCollection> observationCollections = IteratorUtils
				.toList(observationRepository.findAll(clinicalNotesCollection.getObservations()).iterator());
			if (observationCollections != null) {
			    List<Observation> observations = new ArrayList<Observation>();
			    for (ObservationCollection observationCollection : observationCollections) {
				Observation observation = new Observation();
				BeanUtil.map(observationCollection, observation);
				observations.add(observation);
			    }
			    clinicalNote.setObservations(observations);
			}
			@SuppressWarnings("unchecked")
			List<InvestigationCollection> investigationCollections = IteratorUtils
				.toList(investigationRepository.findAll(clinicalNotesCollection.getInvestigations()).iterator());
			if (investigationCollections != null) {
			    List<Investigation> investigations = new ArrayList<Investigation>();
			    for (InvestigationCollection investigationCollection : investigationCollections) {
				Investigation investigation = new Investigation();
				BeanUtil.map(investigationCollection, investigation);
				investigations.add(investigation);
			    }
			    clinicalNote.setInvestigations(investigations);
			}
			@SuppressWarnings("unchecked")
			List<DiagnosisCollection> diagnosisCollections = IteratorUtils
				.toList(diagnosisRepository.findAll(clinicalNotesCollection.getDiagnoses()).iterator());
			if (diagnosisCollections != null) {
			    List<Diagnoses> diagnosisList = new ArrayList<Diagnoses>();
			    for (DiagnosisCollection diagnosisCollection : diagnosisCollections) {
				Diagnoses diagnosis = new Diagnoses();
				BeanUtil.map(diagnosisCollection, diagnosis);
				diagnosisList.add(diagnosis);
			    }
			    clinicalNote.setDiagnoses(diagnosisList);
			}

			@SuppressWarnings("unchecked")
			List<NotesCollection> notesCollections = IteratorUtils.toList(notesRepository.findAll(clinicalNotesCollection.getNotes()).iterator());
			if (notesCollections != null) {
			    List<Notes> notes = new ArrayList<Notes>();
			    for (NotesCollection notesCollection : notesCollections) {
				Notes note = new Notes();
				BeanUtil.map(notesCollection, note);
				notes.add(note);
			    }
			    clinicalNote.setNotes(notes);
			}
			if (clinicalNotesCollection.getDiagrams() != null) {
			    @SuppressWarnings("unchecked")
			    List<DiagramsCollection> diagramsCollections = IteratorUtils
				    .toList(diagramsRepository.findAll(clinicalNotesCollection.getDiagrams()).iterator());
			    if (diagramsCollections != null) {
				List<Diagram> diagrams = new ArrayList<Diagram>();
				for (DiagramsCollection diagramsCollection : diagramsCollections) {
				    Diagram diagram = new Diagram();
				    BeanUtil.map(diagramsCollection, diagram);
				    diagrams.add(diagram);
				}
				clinicalNote.setDiagrams(diagrams);
			    }
			}

			PatientVisitCollection patientVisitCollection = patientVisitRepository.findByClinialNotesId(clinicalNote.getId());
			if (patientVisitCollection != null) clinicalNote.setVisitId(patientVisitCollection.getId());

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
	    } else {
		DiagramsCollection oldDiagramsCollection = diagramsRepository.findOne(diagramsCollection.getId());
		diagramsCollection.setCreatedBy(oldDiagramsCollection.getCreatedBy());
		diagramsCollection.setCreatedTime(oldDiagramsCollection.getCreatedTime());
		diagramsCollection.setDiscarded(oldDiagramsCollection.getDiscarded());
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
	    ComplaintCollection complaintCollection = complaintRepository.findOne(id);
	    if (complaintCollection != null) {
		if (complaintCollection.getDoctorId() != null && complaintCollection.getHospitalId() != null && complaintCollection.getLocationId() != null) {
		    if (complaintCollection.getDoctorId().equals(doctorId) && complaintCollection.getHospitalId().equals(hospitalId)
			    && complaintCollection.getLocationId().equals(locationId)) {

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
	    ObservationCollection observationCollection = observationRepository.findOne(id);
	    if (observationCollection != null) {
		if (observationCollection.getDoctorId() != null && observationCollection.getHospitalId() != null
			&& observationCollection.getLocationId() != null) {
		    if (observationCollection.getDoctorId().equals(doctorId) && observationCollection.getHospitalId().equals(hospitalId)
			    && observationCollection.getLocationId().equals(locationId)) {
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
	    InvestigationCollection investigationCollection = investigationRepository.findOne(id);
	    if (investigationCollection != null) {
		if (investigationCollection.getDoctorId() != null && investigationCollection.getHospitalId() != null
			&& investigationCollection.getLocationId() != null) {
		    if (investigationCollection.getDoctorId().equals(doctorId) && investigationCollection.getHospitalId().equals(hospitalId)
			    && investigationCollection.getLocationId().equals(locationId)) {
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
	    DiagnosisCollection diagnosisCollection = diagnosisRepository.findOne(id);
	    if (diagnosisCollection != null) {
		if (diagnosisCollection.getDoctorId() != null && diagnosisCollection.getHospitalId() != null && diagnosisCollection.getLocationId() != null) {
		    if (diagnosisCollection.getDoctorId().equals(doctorId) && diagnosisCollection.getHospitalId().equals(hospitalId)
			    && diagnosisCollection.getLocationId().equals(locationId)) {
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
	    NotesCollection notesCollection = notesRepository.findOne(id);
	    if (notesCollection != null) {
		if (notesCollection.getDoctorId() != null && notesCollection.getHospitalId() != null && notesCollection.getLocationId() != null) {
		    if (notesCollection.getDoctorId().equals(doctorId) && notesCollection.getHospitalId().equals(hospitalId)
			    && notesCollection.getLocationId().equals(locationId)) {
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
	    DiagramsCollection diagramsCollection = diagramsRepository.findOne(id);
	    if (diagramsCollection != null) {
		if (diagramsCollection.getDoctorId() != null && diagramsCollection.getHospitalId() != null && diagramsCollection.getLocationId() != null) {
		    if (diagramsCollection.getDoctorId().equals(doctorId) && diagramsCollection.getHospitalId().equals(hospitalId)
			    && diagramsCollection.getLocationId().equals(locationId)) {
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
		if(isOTPVerified)clinicalNotesCount = clinicalNotesRepository.getClinicalNotesCount(patientId, false);
	    else clinicalNotesCount = clinicalNotesRepository.getClinicalNotesCount(doctorId, patientId, hospitalId, locationId, false);
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Clinical Notes Count");
	}
	return clinicalNotesCount;
    }

    @Override
    @Transactional
    public List<Object> getClinicalItems(String type, String range, int page, int size, String doctorId, String locationId, String hospitalId,
	    String updatedTime, Boolean discarded) {
	List<Object> response = new ArrayList<Object>();

	switch (ClinicalItems.valueOf(type.toUpperCase())) {

	case COMPLAINTS: {

	    switch (Range.valueOf(range.toUpperCase())) {

	    case GLOBAL:
		response = getGlobalComplaints(page, size, updatedTime, discarded);
		break;
	    case CUSTOM:
		response = getCustomComplaints(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
		break;
	    case BOTH:
		response = getCustomGlobalComplaints(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
		break;
	    }
	    break;
	}
	case INVESTIGATIONS: {
	    switch (Range.valueOf(range.toUpperCase())) {

	    case GLOBAL:
		response = getGlobalInvestigations(page, size, updatedTime, discarded);
		break;
	    case CUSTOM:
		response = getCustomInvestigations(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
		break;
	    case BOTH:
		response = getCustomGlobalInvestigations(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
		break;
	    }
	    break;
	}
	case OBSERVATIONS: {
	    switch (Range.valueOf(range.toUpperCase())) {

	    case GLOBAL:
		response = getGlobalObservations(page, size, updatedTime, discarded);
		break;
	    case CUSTOM:
		response = getCustomObservations(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
		break;
	    case BOTH:
		response = getCustomGlobalObservations(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
		break;
	    }
	    break;
	}
	case DIAGNOSIS: {
	    switch (Range.valueOf(range.toUpperCase())) {

	    case GLOBAL:
		response = getGlobalDiagnosis(page, size, updatedTime, discarded);
		break;
	    case CUSTOM:
		response = getCustomDiagnosis(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
		break;
	    case BOTH:
		response = getCustomGlobalDiagnosis(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
		break;
	    }
	    break;
	}
	case NOTES: {
	    switch (Range.valueOf(range.toUpperCase())) {

	    case GLOBAL:
		response = getGlobalNotes(page, size, updatedTime, discarded);
		break;
	    case CUSTOM:
		response = getCustomNotes(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
		break;
	    case BOTH:
		response = getCustomGlobalNotes(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
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
		response = getCustomGlobalDiagrams(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
		break;
	    }
	    break;
	}

	}
	return response;
    }

    private List<Object> getCustomGlobalComplaints(int page, int size, String doctorId, String locationId, String hospitalId, String updatedTime,
	    Boolean discarded) {
	List<Object> response = new ArrayList<Object>();
	List<ComplaintCollection> complaintCollections = null;
	boolean[] discards = new boolean[2];
	discards[0] = false;

	try {
	    if (discarded)
		discards[1] = true;
	    long createdTimeStamp = Long.parseLong(updatedTime);
	    if (doctorId == null) {
		if (size > 0)
		    complaintCollections = complaintRepository.findCustomGlobalComplaints(new Date(createdTimeStamp), discards,
			    new PageRequest(page, size, Direction.ASC, "complaint"));
		else
		    complaintCollections = complaintRepository.findCustomGlobalComplaints(new Date(createdTimeStamp), discards,
			    new Sort(Sort.Direction.ASC, "complaint"));

	    } else {
		if (DPDoctorUtils.anyStringEmpty(locationId, hospitalId)) {
		    if (size > 0)
			complaintCollections = complaintRepository.findCustomGlobalComplaints(doctorId, new Date(createdTimeStamp), discards,
				new PageRequest(page, size, Direction.ASC, "complaint"));
		    else
			complaintCollections = complaintRepository.findCustomGlobalComplaints(doctorId, new Date(createdTimeStamp), discards,
				new Sort(Sort.Direction.ASC, "complaint"));
		} else {
		    if (size > 0)
			complaintCollections = complaintRepository.findCustomGlobalComplaints(doctorId, locationId, hospitalId, new Date(createdTimeStamp),
				discards, new PageRequest(page, size, Direction.ASC, "complaint"));
		    else
			complaintCollections = complaintRepository.findCustomGlobalComplaints(doctorId, locationId, hospitalId, new Date(createdTimeStamp),
				discards, new Sort(Sort.Direction.ASC, "complaint"));
		}
	    }
	    BeanUtil.map(complaintCollections, response);
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Complaints");
	}
	return response;

    }

    private List<Object> getGlobalComplaints(int page, int size, String updatedTime, Boolean discarded) {
	List<ComplaintCollection> complaintCollections = null;
	List<Object> response = null;
	boolean[] discards = new boolean[2];
	discards[0] = false;

	try {
	    if (discarded)
		discards[1] = true;
	    long createdTimeStamp = Long.parseLong(updatedTime);
	    if (size > 0)
		complaintCollections = complaintRepository.findGlobalComplaints(new Date(createdTimeStamp), discards,
			new PageRequest(page, size, Direction.ASC, "complaint"));
	    else
		complaintCollections = complaintRepository.findGlobalComplaints(new Date(createdTimeStamp), discards,
			new Sort(Sort.Direction.ASC, "complaint"));

	    if (complaintCollections != null) {
		response = new ArrayList<Object>();
		BeanUtil.map(complaintCollections, response);
	    } else {
		throw new BusinessException(ServiceError.NotFound, "No Complaints Found");
	    }

	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Complaints");
	}
	return response;
    }

    private List<Object> getCustomComplaints(int page, int size, String doctorId, String locationId, String hospitalId, String updatedTime, Boolean discarded) {
	List<ComplaintCollection> complaintCollections = null;
	List<Object> response = null;
	boolean[] discards = new boolean[2];
	discards[0] = false;

	try {
	    if (discarded)
		discards[1] = true;

	    if (doctorId == null)
		complaintCollections = new ArrayList<ComplaintCollection>();

	    else {
		long createdTimeStamp = Long.parseLong(updatedTime);
		if (DPDoctorUtils.anyStringEmpty(locationId, hospitalId)) {
		    if (size > 0)
			complaintCollections = complaintRepository.findCustomComplaints(doctorId, new Date(createdTimeStamp), discards,
				new PageRequest(page, size, Direction.ASC, "complaint"));
		    else
			complaintCollections = complaintRepository.findCustomComplaints(doctorId, new Date(createdTimeStamp), discards,
				new Sort(Sort.Direction.ASC, "complaint"));
		} else {
		    if (size > 0)
			complaintCollections = complaintRepository.findCustomComplaints(doctorId, locationId, hospitalId, new Date(createdTimeStamp), discards,
				new PageRequest(page, size, Direction.ASC, "complaint"));
		    else
			complaintCollections = complaintRepository.findCustomComplaints(doctorId, locationId, hospitalId, new Date(createdTimeStamp), discards,
				new Sort(Sort.Direction.ASC, "complaint"));

		}
	    }
	    if (complaintCollections != null) {
		response = new ArrayList<Object>();
		BeanUtil.map(complaintCollections, response);
	    } else {
		logger.warn("No Complaints Found");
		throw new BusinessException(ServiceError.NotFound, "No Complaints Found");
	    }

	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Complaints");
	}
	return response;
    }

    private List<Object> getCustomGlobalInvestigations(int page, int size, String doctorId, String locationId, String hospitalId, String updatedTime,
	    Boolean discarded) {
	List<Object> response = new ArrayList<Object>();
	List<InvestigationCollection> investigationsCollections = null;
	boolean[] discards = new boolean[2];
	discards[0] = false;

	try {
	    if (discarded)
		discards[1] = true;
	    long createdTimeStamp = Long.parseLong(updatedTime);

	    if (doctorId == null) {
		if (size > 0)
		    investigationsCollections = investigationRepository.findCustomGlobalInvestigations(new Date(createdTimeStamp), discards,
			    new PageRequest(page, size, Direction.ASC, "investigation"));
		else
		    investigationsCollections = investigationRepository.findCustomGlobalInvestigations(new Date(createdTimeStamp), discards,
			    new Sort(Sort.Direction.ASC, "investigation"));

	    } else {
		if (DPDoctorUtils.anyStringEmpty(locationId, hospitalId)) {
		    if (size > 0)
			investigationsCollections = investigationRepository.findCustomGlobalInvestigations(doctorId, new Date(createdTimeStamp), discards,
				new PageRequest(page, size, Direction.ASC, "investigation"));
		    else
			investigationsCollections = investigationRepository.findCustomGlobalInvestigations(doctorId, new Date(createdTimeStamp), discards,
				new Sort(Sort.Direction.ASC, "investigation"));
		} else {
		    if (size > 0)
			investigationsCollections = investigationRepository.findCustomGlobalInvestigations(doctorId, locationId, hospitalId,
				new Date(createdTimeStamp), discards, new PageRequest(page, size, Direction.ASC, "investigation"));
		    else
			investigationsCollections = investigationRepository.findCustomGlobalInvestigations(doctorId, locationId, hospitalId,
				new Date(createdTimeStamp), discards, new Sort(Sort.Direction.ASC, "investigation"));
		}
	    }

	    BeanUtil.map(investigationsCollections, response);
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Investigations");
	}
	return response;
    }

    private List<Object> getGlobalInvestigations(int page, int size, String updatedTime, Boolean discarded) {
	List<InvestigationCollection> investigationsCollections = null;
	List<Object> response = null;
	boolean[] discards = new boolean[2];
	discards[0] = false;

	try {
	    if (discarded)
		discards[1] = true;
	    long createdTimeStamp = Long.parseLong(updatedTime);

	    if (size > 0)
		investigationsCollections = investigationRepository.findGlobalInvestigations(new Date(createdTimeStamp), discards,
			new PageRequest(page, size, Direction.ASC, "investigation"));
	    else
		investigationsCollections = investigationRepository.findGlobalInvestigations(new Date(createdTimeStamp), discards,
			new Sort(Sort.Direction.ASC, "investigation"));

	    if (investigationsCollections != null) {
		response = new ArrayList<Object>();
		BeanUtil.map(investigationsCollections, response);
	    } else {
		logger.warn("No Investigations Found");
		throw new BusinessException(ServiceError.NotFound, "No Investigations Found");
	    }

	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Investigations");
	}
	return response;
    }

    private List<Object> getCustomInvestigations(int page, int size, String doctorId, String locationId, String hospitalId, String updatedTime,
	    Boolean discarded) {
	List<InvestigationCollection> investigationsCollections = null;
	List<Object> response = null;
	boolean[] discards = new boolean[2];
	discards[0] = false;
	try {
	    if (discarded)
		discards[1] = true;
	    long createdTimeStamp = Long.parseLong(updatedTime);

	    if (doctorId == null)
		investigationsCollections = new ArrayList<InvestigationCollection>();
	    else {
		if (locationId == null && hospitalId == null) {
		    if (size > 0)
			investigationsCollections = investigationRepository.findCustomInvestigations(doctorId, new Date(createdTimeStamp), discards,
				new PageRequest(page, size, Direction.ASC, "investigation"));
		    else
			investigationsCollections = investigationRepository.findCustomInvestigations(doctorId, new Date(createdTimeStamp), discards,
				new Sort(Sort.Direction.ASC, "investigation"));
		} else {
		    if (size > 0)
			investigationsCollections = investigationRepository.findCustomInvestigations(doctorId, locationId, hospitalId,
				new Date(createdTimeStamp), discards, new PageRequest(page, size, Direction.ASC, "investigation"));
		    else
			investigationsCollections = investigationRepository.findCustomInvestigations(doctorId, locationId, hospitalId,
				new Date(createdTimeStamp), discards, new Sort(Sort.Direction.ASC, "investigation"));
		}
	    }
	    if (investigationsCollections != null) {
		response = new ArrayList<Object>();
		BeanUtil.map(investigationsCollections, response);
	    } else {
		logger.warn("No Investigations Found");
		throw new BusinessException(ServiceError.NotFound, "No Investigations Found");
	    }

	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Investigations");
	}
	return response;
    }

    private List<Object> getCustomGlobalObservations(int page, int size, String doctorId, String locationId, String hospitalId, String updatedTime,
	    Boolean discarded) {
	List<Object> response = new ArrayList<Object>();
	List<ObservationCollection> observationCollections = null;
	boolean[] discards = new boolean[2];
	discards[0] = false;
	try {
	    if (discarded)
		discards[1] = true;
	    long createdTimeStamp = Long.parseLong(updatedTime);
	    if (doctorId == null) {
		if (size > 0)
		    observationCollections = observationRepository.findCustomGlobalObservations(new Date(createdTimeStamp), discards,
			    new PageRequest(page, size, Direction.ASC, "investigation"));
		else
		    observationCollections = observationRepository.findCustomGlobalObservations(new Date(createdTimeStamp), discards,
			    new Sort(Sort.Direction.ASC, "investigation"));

	    } else {
		if (locationId == null && hospitalId == null) {
		    if (size > 0)
			observationCollections = observationRepository.findCustomGlobalObservations(doctorId, new Date(createdTimeStamp), discards,
				new PageRequest(page, size, Direction.ASC, "investigation"));
		    else
			observationCollections = observationRepository.findCustomGlobalObservations(doctorId, new Date(createdTimeStamp), discards,
				new Sort(Sort.Direction.ASC, "investigation"));
		} else {
		    if (size > 0)
			observationCollections = observationRepository.findCustomGlobalObservations(doctorId, locationId, hospitalId,
				new Date(createdTimeStamp), discards, new PageRequest(page, size, Direction.ASC, "investigation"));
		    else
			observationCollections = observationRepository.findCustomGlobalObservations(doctorId, locationId, hospitalId,
				new Date(createdTimeStamp), discards, new Sort(Sort.Direction.ASC, "investigation"));

		}
	    }
	    BeanUtil.map(observationCollections, response);
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Observations");
	}
	return response;

    }

    private List<Object> getGlobalObservations(int page, int size, String updatedTime, Boolean discarded) {
	List<ObservationCollection> observationCollections = null;
	List<Object> response = null;
	boolean[] discards = new boolean[2];
	discards[0] = false;
	try {

	    if (discarded)
		discards[1] = true;
	    long createdTimeStamp = Long.parseLong(updatedTime);

	    if (size > 0)
		observationCollections = observationRepository.findGlobalObservations(new Date(createdTimeStamp), discards,
			new PageRequest(page, size, Direction.ASC, "observation"));
	    else
		observationCollections = observationRepository.findGlobalObservations(new Date(createdTimeStamp), discards,
			new Sort(Sort.Direction.ASC, "observation"));

	    if (observationCollections != null) {
		response = new ArrayList<Object>();
		BeanUtil.map(observationCollections, response);
	    } else {
		logger.warn("No Observations Found");
		throw new BusinessException(ServiceError.NotFound, "No Observations Found");
	    }

	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Observations");
	}
	return response;
    }

    private List<Object> getCustomObservations(int page, int size, String doctorId, String locationId, String hospitalId, String updatedTime,
	    Boolean discarded) {
	List<ObservationCollection> observationCollections = null;
	List<Object> response = null;
	boolean[] discards = new boolean[2];
	discards[0] = false;

	try {
	    if (discarded)
		discards[1] = true;
	    long createdTimeStamp = Long.parseLong(updatedTime);

	    if (doctorId == null)
		observationCollections = new ArrayList<ObservationCollection>();

	    else if (DPDoctorUtils.anyStringEmpty(locationId, hospitalId)) {
		if (size > 0)
		    observationCollections = observationRepository.findCustomObservations(doctorId, new Date(createdTimeStamp), discards,
			    new PageRequest(page, size, Direction.ASC, "observation"));
		else
		    observationCollections = observationRepository.findCustomObservations(doctorId, new Date(createdTimeStamp), discards,
			    new Sort(Sort.Direction.ASC, "observation"));
	    } else {
		if (size > 0)
		    observationCollections = observationRepository.findCustomObservations(doctorId, locationId, hospitalId, new Date(createdTimeStamp),
			    discards, new PageRequest(page, size, Direction.ASC, "observation"));
		else
		    observationCollections = observationRepository.findCustomObservations(doctorId, locationId, hospitalId, new Date(createdTimeStamp),
			    discards, new Sort(Sort.Direction.ASC, "observation"));
	    }

	    if (observationCollections != null) {
		response = new ArrayList<Object>();
		BeanUtil.map(observationCollections, response);
	    } else {
		logger.warn("No Observations Found");
		throw new BusinessException(ServiceError.NotFound, "No Observations Found");
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Observations");
	}
	return response;
    }

    private List<Object> getCustomGlobalDiagnosis(int page, int size, String doctorId, String locationId, String hospitalId, String updatedTime,
	    Boolean discarded) {
	List<Object> response = new ArrayList<Object>();
	List<DiagnosisCollection> diagnosisCollections = null;
	boolean[] discards = new boolean[2];
	discards[0] = false;

	try {
	    if (discarded)
		discards[1] = true;
	    long createdTimeStamp = Long.parseLong(updatedTime);

	    if (doctorId == null) {
		if (size > 0)
		    diagnosisCollections = diagnosisRepository.findCustomGlobalDiagnosis(new Date(createdTimeStamp), discards,
			    new PageRequest(page, size, Direction.ASC, "diagnosis"));
		else
		    diagnosisCollections = diagnosisRepository.findCustomGlobalDiagnosis(new Date(createdTimeStamp), discards,
			    new Sort(Sort.Direction.ASC, "diagnosis"));
	    } else {

		if (locationId == null && hospitalId == null) {
		    if (size > 0)
			diagnosisCollections = diagnosisRepository.findCustomGlobalDiagnosis(doctorId, new Date(createdTimeStamp), discards,
				new PageRequest(page, size, Direction.ASC, "diagnosis"));
		    else
			diagnosisCollections = diagnosisRepository.findCustomGlobalDiagnosis(doctorId, new Date(createdTimeStamp), discards,
				new Sort(Sort.Direction.ASC, "diagnosis"));

		} else {
		    if (size > 0)
			diagnosisCollections = diagnosisRepository.findCustomGlobalDiagnosis(doctorId, locationId, hospitalId, new Date(createdTimeStamp),
				discards, new PageRequest(page, size, Direction.ASC, "diagnosis"));
		    else
			diagnosisCollections = diagnosisRepository.findCustomGlobalDiagnosis(doctorId, locationId, hospitalId, new Date(createdTimeStamp),
				discards, new Sort(Sort.Direction.ASC, "diagnosis"));
		}
	    }
	    BeanUtil.map(diagnosisCollections, response);
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Diagnosis");
	}
	return response;

    }

    private List<Object> getGlobalDiagnosis(int page, int size, String updatedTime, Boolean discarded) {
	List<DiagnosisCollection> diagnosisCollections = null;
	List<Object> response = null;
	boolean[] discards = new boolean[2];
	discards[0] = false;
	try {
	    if (discarded)
		discards[1] = true;
	    long createdTimeStamp = Long.parseLong(updatedTime);

	    if (size > 0)
		diagnosisCollections = diagnosisRepository.findGlobalDiagnosis(new Date(createdTimeStamp), discards,
			new PageRequest(page, size, Direction.ASC, "diagnosis"));
	    else
		diagnosisCollections = diagnosisRepository.findGlobalDiagnosis(new Date(createdTimeStamp), discards,
			new Sort(Sort.Direction.ASC, "diagnosis"));
	    if (diagnosisCollections != null) {
		response = new ArrayList<Object>();
		BeanUtil.map(diagnosisCollections, response);
	    } else {
		logger.warn("No Diagnosis Found");
		throw new BusinessException(ServiceError.NotFound, "No Diagnosis Found");
	    }

	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Diagnosis");
	}
	return response;
    }

    private List<Object> getCustomDiagnosis(int page, int size, String doctorId, String locationId, String hospitalId, String updatedTime, Boolean discarded) {
	List<DiagnosisCollection> diagnosisCollections = null;
	List<Object> response = null;
	boolean[] discards = new boolean[2];
	discards[0] = false;
	try {
	    if (discarded)
		discards[1] = true;
	    long createdTimeStamp = Long.parseLong(updatedTime);

	    if (doctorId == null)
		diagnosisCollections = new ArrayList<DiagnosisCollection>();
	    else if (DPDoctorUtils.anyStringEmpty(locationId, hospitalId)) {
		if (size > 0)
		    diagnosisCollections = diagnosisRepository.findCustomDiagnosis(doctorId, new Date(createdTimeStamp), discards,
			    new PageRequest(page, size, Direction.ASC, "diagnosis"));
		else
		    diagnosisCollections = diagnosisRepository.findCustomDiagnosis(doctorId, new Date(createdTimeStamp), discards,
			    new Sort(Sort.Direction.ASC, "diagnosis"));
	    } else {
		if (size > 0)
		    diagnosisCollections = diagnosisRepository.findCustomDiagnosis(doctorId, locationId, hospitalId, new Date(createdTimeStamp), discards,
			    new PageRequest(page, size, Direction.ASC, "diagnosis"));
		else
		    diagnosisCollections = diagnosisRepository.findCustomDiagnosis(doctorId, locationId, hospitalId, new Date(createdTimeStamp), discards,
			    new Sort(Sort.Direction.ASC, "diagnosis"));
	    }

	    if (diagnosisCollections != null) {
		response = new ArrayList<Object>();
		BeanUtil.map(diagnosisCollections, response);
	    } else {
		logger.warn("No Diagnosis Found");
		throw new BusinessException(ServiceError.NotFound, "No Diagnosis Found");
	    }

	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Diagnosis");
	}
	return response;
    }

    private List<Object> getCustomGlobalNotes(int page, int size, String doctorId, String locationId, String hospitalId, String updatedTime,
	    Boolean discarded) {
	List<Object> response = new ArrayList<Object>();
	List<NotesCollection> notesCollections = null;
	boolean[] discards = new boolean[2];
	discards[0] = false;
	try {
	    if (discarded)
		discards[1] = true;
	    long createdTimeStamp = Long.parseLong(updatedTime);

	    if (doctorId == null) {
		if (size > 0)
		    notesCollections = notesRepository.findCustomGlobalNotes(new Date(createdTimeStamp), discards,
			    new PageRequest(page, size, Direction.ASC, "note"));
		else
		    notesCollections = notesRepository.findCustomGlobalNotes(new Date(createdTimeStamp), discards,
			    new Sort(Sort.Direction.ASC, "note"));
	    } else {
		if (DPDoctorUtils.anyStringEmpty(locationId, hospitalId)) {
		    if (size > 0)
			notesCollections = notesRepository.findCustomGlobalNotes(doctorId, new Date(createdTimeStamp), discards,
				new PageRequest(page, size, Direction.ASC, "note"));
		    else
			notesCollections = notesRepository.findCustomGlobalNotes(doctorId, new Date(createdTimeStamp), discards,
				new Sort(Sort.Direction.ASC, "note"));
		} else {
		    if (size > 0)
			notesCollections = notesRepository.findCustomGlobalNotes(doctorId, locationId, hospitalId, new Date(createdTimeStamp), discards,
				new PageRequest(page, size, Direction.ASC, "note"));
		    else
			notesCollections = notesRepository.findCustomGlobalNotes(doctorId, locationId, hospitalId, new Date(createdTimeStamp), discards,
				new Sort(Sort.Direction.ASC, "note"));
		}
	    }
	    BeanUtil.map(notesCollections, response);
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Notes");
	}
	return response;

    }

    private List<Object> getGlobalNotes(int page, int size, String updatedTime, Boolean discarded) {
	List<NotesCollection> notesCollections = null;
	List<Object> response = null;
	boolean[] discards = new boolean[2];
	discards[0] = false;
	try {
	    if (discarded)
		discards[1] = true;
	    long createdTimeStamp = Long.parseLong(updatedTime);
	    if (size > 0)
		notesCollections = notesRepository.findGlobalNotes(new Date(createdTimeStamp), discards,
			new PageRequest(page, size, Direction.ASC, "note"));
	    else
		notesCollections = notesRepository.findGlobalNotes(new Date(createdTimeStamp), discards, new Sort(Sort.Direction.ASC, "note"));

	    if (notesCollections != null) {
		response = new ArrayList<Object>();
		BeanUtil.map(notesCollections, response);
	    } else {
		logger.warn("No Notes Found");
		throw new BusinessException(ServiceError.NotFound, "No Notes Found");
	    }

	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Notes");
	}
	return response;
    }

    private List<Object> getCustomNotes(int page, int size, String doctorId, String locationId, String hospitalId, String updatedTime, Boolean discarded) {
	List<NotesCollection> notesCollections = null;
	List<Object> response = null;
	boolean[] discards = new boolean[2];
	discards[0] = false;
	try {
	    if (discarded)
		discards[1] = true;
	    long createdTimeStamp = Long.parseLong(updatedTime);
	    if (doctorId == null)
		notesCollections = new ArrayList<NotesCollection>();

	    else {
		if (locationId == null && hospitalId == null) {
		    if (size > 0)
			notesCollections = notesRepository.findCustomNotes(doctorId, new Date(createdTimeStamp), discards,
				new PageRequest(page, size, Direction.ASC, "note"));
		    else
			notesCollections = notesRepository.findCustomNotes(doctorId, new Date(createdTimeStamp), discards,
				new Sort(Sort.Direction.ASC, "note"));
		} else {
		    if (size > 0)
			notesCollections = notesRepository.findCustomNotes(doctorId, locationId, hospitalId, new Date(createdTimeStamp), discards,
				new PageRequest(page, size, Direction.ASC, "note"));
		    else
			notesCollections = notesRepository.findCustomNotes(doctorId, locationId, hospitalId, new Date(createdTimeStamp), discards,
				new Sort(Sort.Direction.ASC, "note"));
		}
	    }
	    if (notesCollections != null) {
		response = new ArrayList<Object>();
		BeanUtil.map(notesCollections, response);
	    } else {
		logger.warn("No Notes Found");
		throw new BusinessException(ServiceError.NotFound, "No Notes Found");
	    }

	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Notes");
	}
	return response;
    }

    private List<Object> getCustomGlobalDiagrams(int page, int size, String doctorId, String locationId, String hospitalId, String updatedTime,
	    Boolean discarded) {
	List<Object> response = new ArrayList<Object>();
	List<DiagramsCollection> diagramCollections = null;
	boolean[] discards = new boolean[2];
	discards[0] = false;
	try {
	    if (discarded)
		discards[1] = true;
	    long createdTimeStamp = Long.parseLong(updatedTime);

	    if (doctorId == null) {
		if (size > 0)
		    diagramCollections = diagramsRepository.findCustomGlobalDiagrams(new Date(createdTimeStamp), discards,
			    new PageRequest(page, size, Direction.ASC, "speciality"));
		else
		    diagramCollections = diagramsRepository.findCustomGlobalDiagrams(new Date(createdTimeStamp), discards,
			    new Sort(Sort.Direction.ASC, "speciality"));

	    } else {
	    	DoctorCollection doctorCollection = doctorRepository.findByUserId(doctorId);
	 	   
		    List<SpecialityCollection> specialityCollections = specialityRepository.findById(doctorCollection.getSpecialities());
		    @SuppressWarnings("unchecked")
		    Collection<String> specialities = CollectionUtils.collect(specialityCollections, new BeanToPropertyValueTransformer("speciality"));
		   
		if (locationId == null && hospitalId == null) {
		    if (size > 0)
			diagramCollections = diagramsRepository.findCustomGlobalDiagrams(doctorId, specialities, new Date(createdTimeStamp), discards,
				new PageRequest(page, size, Direction.ASC, "speciality"));
		    else
			diagramCollections = diagramsRepository.findCustomGlobalDiagrams(doctorId, specialities, new Date(createdTimeStamp), discards,
				new Sort(Sort.Direction.ASC, "speciality"));
		} else {
		    if (size > 0)
			diagramCollections = diagramsRepository.findCustomGlobalDiagrams(doctorId, locationId, hospitalId, specialities, new Date(createdTimeStamp), discards,
				new PageRequest(page, size, Direction.ASC, "speciality"));
		    else
			diagramCollections = diagramsRepository.findCustomGlobalDiagrams(doctorId, locationId, hospitalId, specialities, new Date(createdTimeStamp), discards,
				new Sort(Sort.Direction.ASC, "speciality"));
		}
	    }
	    BeanUtil.map(diagramCollections, response);
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Diagrams");
	}
	return response;

    }

    private List<Object> getGlobalDiagrams(int page, int size, String doctorId, String updatedTime, Boolean discarded) {
	List<DiagramsCollection> diagramCollections = null;
	List<Object> response = null;
	boolean[] discards = new boolean[2];
	discards[0] = false;
	try {
	    if (discarded)
		discards[1] = true;
	    long createdTimeStamp = Long.parseLong(updatedTime);
	    
	    DoctorCollection doctorCollection = doctorRepository.findByUserId(doctorId);
	    Collection<String> specialities = null;
	    
	    if(doctorCollection != null){
	    	List<SpecialityCollection> specialityCollections = specialityRepository.findById(doctorCollection.getSpecialities());
		    if(specialityCollections != null && !specialityCollections.isEmpty())specialities = CollectionUtils.collect(specialityCollections, new BeanToPropertyValueTransformer("speciality"));  
	    }
	    if(specialities != null){
	    	if (size > 0)
	    		diagramCollections = diagramsRepository.findGlobalDiagrams(new Date(createdTimeStamp), discards, specialities, new PageRequest(page, size, Direction.ASC, "speciality"));
	    	    else
	    		diagramCollections = diagramsRepository.findGlobalDiagrams(new Date(createdTimeStamp), discards, specialities, new Sort(Sort.Direction.ASC, "speciality"));
	    }else{
	    	if (size > 0)
	    		diagramCollections = diagramsRepository.findGlobalDiagrams(new Date(createdTimeStamp), discards, new PageRequest(page, size, Direction.ASC, "speciality"));
	    	    else
	    		diagramCollections = diagramsRepository.findGlobalDiagrams(new Date(createdTimeStamp), discards, new Sort(Sort.Direction.ASC, "speciality"));
	    }
	    if (diagramCollections != null) {
		response = new ArrayList<Object>();
		BeanUtil.map(diagramCollections, response);
	    } else {
		logger.warn("No Diagrams Found");
		throw new BusinessException(ServiceError.NotFound, "No Diagrams Found");
	    }

	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Diagrams");
	}
	return response;
    }

    private List<Object> getCustomDiagrams(int page, int size, String doctorId, String locationId, String hospitalId, String updatedTime, Boolean discarded) {
	List<DiagramsCollection> diagramCollections = null;
	List<Object> response = null;
	boolean[] discards = new boolean[2];
	discards[0] = false;
	try {
	    if (discarded)
		discards[1] = true;
	    long createdTimeStamp = Long.parseLong(updatedTime);

	    if (doctorId == null)
		diagramCollections = new ArrayList<DiagramsCollection>();
	    else {
	    DoctorCollection doctorCollection = doctorRepository.findByUserId(doctorId);
	   
	    List<SpecialityCollection> specialityCollections = specialityRepository.findById(doctorCollection.getSpecialities());
	    @SuppressWarnings("unchecked")
	    Collection<String> specialities = CollectionUtils.collect(specialityCollections, new BeanToPropertyValueTransformer("speciality"));
	    
		if (locationId == null && hospitalId == null) {
		    if (size > 0)
			diagramCollections = diagramsRepository.findCustomDiagrams(doctorId, specialities, new Date(createdTimeStamp), discards,
				new PageRequest(page, size, Direction.ASC, "speciality"));
		    else
			diagramCollections = diagramsRepository.findCustomDiagrams(doctorId, specialities, new Date(createdTimeStamp), discards,
				new Sort(Sort.Direction.ASC, "speciality"));
		} else {
		    if (size > 0)
			diagramCollections = diagramsRepository.findCustomDiagrams(doctorId, locationId, hospitalId, specialities, new Date(createdTimeStamp), discards,
				new PageRequest(page, size, Direction.ASC, "speciality"));
		    else
			diagramCollections = diagramsRepository.findCustomDiagrams(doctorId, locationId, hospitalId, specialities, new Date(createdTimeStamp), discards,
				new Sort(Sort.Direction.ASC, "speciality"));
		}
	    }

	    if (diagramCollections != null) {
		response = new ArrayList<Object>();
		BeanUtil.map(diagramCollections, response);
	    } else {
		logger.warn("No Diagrams Found");
		throw new BusinessException(ServiceError.NotFound, "No Diagrams Found");
	    }

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
	MailAttachment mailAttachment = createMailData(clinicalNotesId, doctorId, locationId, hospitalId);
	try {
	    mailService.sendEmail(emailAddress, "Clinical Notes", "PFA.", mailAttachment);
	} catch (MessagingException e) {
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}

    }

    @Override
    @Transactional
    public MailAttachment getClinicalNotesMailData(String clinicalNotesId, String doctorId, String locationId, String hospitalId) {
	return createMailData(clinicalNotesId, doctorId, locationId, hospitalId);
    }

    private MailAttachment createMailData(String clinicalNotesId, String doctorId, String locationId, String hospitalId) {
	ClinicalNotesCollection clinicalNotesCollection = null;
	MailAttachment mailAttachment = null;
	PatientCollection patient = null;
	UserCollection user = null;
	EmailTrackCollection emailTrackCollection = new EmailTrackCollection();
	try {
	    clinicalNotesCollection = clinicalNotesRepository.findOne(clinicalNotesId);
	    if (clinicalNotesCollection != null) {
		if (clinicalNotesCollection.getDoctorId() != null && clinicalNotesCollection.getHospitalId() != null
			&& clinicalNotesCollection.getLocationId() != null) {
		    if (clinicalNotesCollection.getDoctorId().equals(doctorId) && clinicalNotesCollection.getHospitalId().equals(hospitalId)
			    && clinicalNotesCollection.getLocationId().equals(locationId)) {

			    user = userRepository.findOne(clinicalNotesCollection.getPatientId());
			    patient = patientRepository.findByUserIdDoctorIdLocationIdAndHospitalId(clinicalNotesCollection.getPatientId(), doctorId, locationId, hospitalId);

			    emailTrackCollection.setDoctorId(doctorId);
			    emailTrackCollection.setHospitalId(hospitalId);
			    emailTrackCollection.setLocationId(locationId);
			    emailTrackCollection.setType(ComponentType.CLINICAL_NOTES.getType());
			    emailTrackCollection.setSubject("Clinical Notes");
			    if (user != null) {
				emailTrackCollection.setPatientName(user.getFirstName());
				emailTrackCollection.setPatientId(user.getId());
			    }
			   JasperReportResponse jasperReportResponse = createJasper(clinicalNotesCollection, patient, user);
				mailAttachment = new MailAttachment();
				mailAttachment.setAttachmentName(FilenameUtils.getName(jasperReportResponse.getPath()));
				mailAttachment.setFileSystemResource(new FileSystemResource(jasperReportResponse.getPath()));
//				mailAttachment.setInputStream(jasperReportResponse.getInputStream());
	
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

	return mailAttachment;
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

			if (size > 0)
			    clinicalNotesCollections = clinicalNotesRepository.getClinicalNotes(patientId, new Date(createdTimestamp), discards, inHistorys,
				    new PageRequest(page, size, Direction.DESC, "createdTime"));
			else
			    clinicalNotesCollections = clinicalNotesRepository.getClinicalNotes(patientId, new Date(createdTimestamp), discards, inHistorys, new Sort(
				    Sort.Direction.DESC, "createdTime"));
		    
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
			ClinicalNotesCollection clinicalNotesCollection = clinicalNotesRepository.findOne(clinicalNotesId);

		    if (clinicalNotesCollection != null) {
			PatientCollection patient = patientRepository.findByUserIdDoctorIdLocationIdAndHospitalId(clinicalNotesCollection.getPatientId(),
					clinicalNotesCollection.getDoctorId(), clinicalNotesCollection.getLocationId(), clinicalNotesCollection.getHospitalId());
			UserCollection user = userRepository.findOne(clinicalNotesCollection.getPatientId());

			JasperReportResponse jasperReportResponse = createJasper(clinicalNotesCollection, patient, user);
			if(jasperReportResponse != null && jasperReportResponse.getPath() != null)response = jasperReportResponse.getPath();
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
		for (String observationId : clinicalNotesCollection.getObservations()) {
		    ObservationCollection observationCollection = observationRepository.findOne(observationId);
		    if (observationCollection != null) {
			if (observations == "")
			    observations = observationCollection.getObservation();
			else
			    observations = observations + ", " + observationCollection.getObservation();
		    }
		}
		parameters.put("observationIds", observations);

		String notes = "";
		for (String noteId : clinicalNotesCollection.getNotes()) {
		    NotesCollection note = notesRepository.findOne(noteId);
		    if (note != null) {
			if (notes == "")
			    notes = note.getNote();
			else
			    notes = notes + ", " + note.getNote();
		    }
		}
		parameters.put("noteIds", notes);

		String investigations = "";
		for (String investigationId : clinicalNotesCollection.getInvestigations()) {
		    InvestigationCollection investigation = investigationRepository.findOne(investigationId);
		    if (investigation != null) {
			if (investigations == "")
			    investigations = investigation.getInvestigation();
			else
			    investigations = investigations + ", " + investigation.getInvestigation();
		    }
		}
		parameters.put("investigationIds", investigations);

		String diagnosis = "";
		for (String diagnosisId : clinicalNotesCollection.getDiagnoses()) {
		    DiagnosisCollection diagnosisCollection = diagnosisRepository.findOne(diagnosisId);
		    if (diagnosisCollection != null) {
			if (diagnosis == "")
			    diagnosis = diagnosisCollection.getDiagnosis();
			else
			    diagnosis = diagnosis + ", " + diagnosisCollection.getDiagnosis();
		    }
		}
		parameters.put("diagnosesIds", diagnosis);

		String complaints = "";
		for (String complaintId : clinicalNotesCollection.getComplaints()) {
		    ComplaintCollection complaint = complaintRepository.findOne(complaintId);
		    if (complaint != null) {
			if (complaints == "")
			    complaints = complaint.getComplaint();
			else
			    complaints = complaints + ", " + complaint.getComplaint();
		    }
		}
		parameters.put("complaintIds", complaints);

		List<DBObject> diagramIds = new ArrayList<DBObject>();
		if (clinicalNotesCollection.getDiagrams() != null)
		    for (String diagramId : clinicalNotesCollection.getDiagrams()) {
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
	    
	    parameters.put("clinicalNotesId", clinicalNotesCollection.getId());
	    if (clinicalNotesCollection.getVitalSigns() != null) {
		String pulse = clinicalNotesCollection.getVitalSigns().getPulse();
		pulse = pulse != null && !pulse.isEmpty() ? "Pulse: " + pulse +" " +VitalSignsUnit.PULSE.getUnit() + "    " : "";

		String temp = clinicalNotesCollection.getVitalSigns().getTemperature();
		temp = temp != null && !temp.isEmpty() ? "Temperature: " + temp +" " +VitalSignsUnit.TEMPERATURE.getUnit() +"    " : "";

		String breathing = clinicalNotesCollection.getVitalSigns().getBreathing();
		breathing = breathing != null && !breathing.isEmpty() ? "Breathing: " + breathing + " "+VitalSignsUnit.BREATHING.getUnit() + "    " : "";

		String bloodPressure = "";
		if (clinicalNotesCollection.getVitalSigns().getBloodPressure() != null) {
		    String systolic = clinicalNotesCollection.getVitalSigns().getBloodPressure().getSystolic();
		    systolic = systolic != null && !systolic.isEmpty() ? systolic : "";

		    String diastolic = clinicalNotesCollection.getVitalSigns().getBloodPressure().getDiastolic();
		    diastolic = diastolic != null && !diastolic.isEmpty() ? diastolic : "";

		    bloodPressure = "Blood Pressure: " + systolic + "/" + diastolic + " "+VitalSignsUnit.BLOODPRESSURE.getUnit();
		}
		String vitalSigns = pulse + temp + breathing + bloodPressure;
		parameters.put("vitalSigns", vitalSigns != null && !vitalSigns.isEmpty() ? vitalSigns : null);
	    } else
		parameters.put("vitalSigns", null);

	    String patientName = "", dob = "", gender = "", mobileNumber = "", refferedBy = "", pid = "", date = "", resourceId = "", logoURL = "";
		if (patient.getReferredBy() != null) {
		    ReferencesCollection referencesCollection = referenceRepository.findOne(patient.getReferredBy());
		    if (referencesCollection != null)
			refferedBy = referencesCollection.getReference();
		}
		patientName = "Patient Name: " + (user != null ? user.getFirstName() : "--") + "<br>";
		String age = "--";
		if(patient != null && patient.getDob() != null){
			Age ageObj = patient.getDob().getAge();
			if(ageObj.getYears() > 14)age = ageObj.getYears()+" years";
			else {
				int months = 0, days = ageObj.getDays();
				if(ageObj.getMonths() > 0){
					months = ageObj.getMonths();
					if(ageObj.getYears() > 0)months = months + 12 * ageObj.getYears();
				}
				if(months == 0)age = days +" days";
				else age = months +" months "+days +" days";
			}
		}
		dob = "Patient Age: " + age + "<br>";
		gender = "Patient Gender: " + (patient != null ? patient.getGender() : "--") + "<br>";
		mobileNumber = "Mobile Number: " + (user != null ? user.getMobileNumber() : "--") + "<br>";
		pid = "Patient Id: " + (patient != null ? patient.getPID() : "--") + "<br>";
		refferedBy = "Referred By: " + (refferedBy != "" ? refferedBy : "--") + "<br>";
		date = "Date: " + new SimpleDateFormat("dd-MM-yyyy").format(new Date()) + "<br>";
		resourceId = "ClinicalNotesId: " + (clinicalNotesCollection.getUniqueEmrId() != null ? clinicalNotesCollection.getUniqueEmrId() : "--") + "<br>";
		PrintSettingsCollection printSettings = printSettingsRepository.getSettings(clinicalNotesCollection.getDoctorId(), clinicalNotesCollection.getLocationId(), clinicalNotesCollection.getHospitalId(),
			ComponentType.CLINICAL_NOTES.getType());

		if (printSettings == null) {
		    printSettings = printSettingsRepository.getSettings(clinicalNotesCollection.getDoctorId(), clinicalNotesCollection.getLocationId(), clinicalNotesCollection.getHospitalId(), ComponentType.ALL.getType());
		}

		parameters.put("printSettingsId", printSettings != null ? printSettings.getId() : "");
		String headerLeftText = "", headerRightText = "", footerBottomText = "";
		if (printSettings != null) {
		    if (printSettings.getHeaderSetup() != null) {
			for (PrintSettingsText str : printSettings.getHeaderSetup().getTopLeftText()) {

			    if ((str.getFontSize() != null) && (!str.getFontSize().equalsIgnoreCase("10pt") || !str.getFontSize().equalsIgnoreCase("11pt")
				    || !str.getFontSize().equalsIgnoreCase("12pt") || !str.getFontSize().equalsIgnoreCase("13pt")
				    || !str.getFontSize().equalsIgnoreCase("14pt") || !str.getFontSize().equalsIgnoreCase("15pt")))
				str.setFontSize("10pt");

			    boolean isBold = containsIgnoreCase(FONTSTYLE.BOLD.getStyle(), str.getFontStyle());
			    boolean isItalic = containsIgnoreCase(FONTSTYLE.ITALIC.getStyle(), str.getFontStyle());
			    String text = str.getText();
			    if (isItalic)
				text = "<i>" + text + "</i>";
			    if (isBold)
				text = "<b>" + text + "</b>";

			    if (headerLeftText.isEmpty())
				headerLeftText = "<span style='font-size:" + str.getFontSize() + ";'>" + text + "</span>";
			    else
				headerLeftText = headerLeftText + "<br/>" + "<span style='font-size:" + str.getFontSize() + "'>" + text + "</span>";
			}

			for (PrintSettingsText str : printSettings.getHeaderSetup().getTopRightText()) {
			    if ((str.getFontSize() != null) && (!str.getFontSize().equalsIgnoreCase("10pt") || !str.getFontSize().equalsIgnoreCase("11pt")
				    || !str.getFontSize().equalsIgnoreCase("12pt") || !str.getFontSize().equalsIgnoreCase("13pt")
				    || !str.getFontSize().equalsIgnoreCase("14pt") || !str.getFontSize().equalsIgnoreCase("15pt")))
				str.setFontSize("10pt");
			    boolean isBold = containsIgnoreCase(FONTSTYLE.BOLD.getStyle(), str.getFontStyle());
			    boolean isItalic = containsIgnoreCase(FONTSTYLE.ITALIC.getStyle(), str.getFontStyle());
			    String text = str.getText();
			    if (isItalic)
				text = "<i>" + text + "</i>";
			    if (isBold)
				text = "<b>" + text + "</b>";

			    if (headerRightText.isEmpty())
				headerRightText = "<span style='font-size:" + str.getFontSize() + "'>" + text + "</span>";
			    else
				headerRightText = headerRightText + "<br/>" + "<span style='font-size:" + str.getFontSize() + "'>" + text + "</span>";
			}

		    }
		    if (printSettings.getFooterSetup() != null) {
			if (printSettings.getFooterSetup().getCustomFooter())
			    for (PrintSettingsText str : printSettings.getFooterSetup().getBottomText()) {
				if ((str.getFontSize() != null) && (!str.getFontSize().equalsIgnoreCase("10pt") || !str.getFontSize().equalsIgnoreCase("11pt")
					|| !str.getFontSize().equalsIgnoreCase("12pt") || !str.getFontSize().equalsIgnoreCase("13pt")
					|| !str.getFontSize().equalsIgnoreCase("14pt") || !str.getFontSize().equalsIgnoreCase("15pt")))
				    str.setFontSize("10pt");

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
				    footerBottomText = footerBottomText + "" + "<span style='font-size:" + str.getFontSize() + "'>" + text + "</span>";
			    }
			if(printSettings.getClinicLogoUrl() != null)logoURL = getFinalImageURL(printSettings.getClinicLogoUrl());

			if (printSettings.getHeaderSetup() != null && printSettings.getHeaderSetup().getPatientDetails() != null
				&& printSettings.getHeaderSetup().getPatientDetails().getStyle() != null) {
			    PatientDetails patientDetails = printSettings.getHeaderSetup().getPatientDetails();
			    boolean isBold = containsIgnoreCase(FONTSTYLE.BOLD.getStyle(), patientDetails.getStyle().getFontStyle());
			    boolean isItalic = containsIgnoreCase(FONTSTYLE.ITALIC.getStyle(), patientDetails.getStyle().getFontStyle());
			    String fontSize = patientDetails.getStyle().getFontSize();
			    if ((fontSize != null)
				    && (!fontSize.equalsIgnoreCase("10pt") || !fontSize.equalsIgnoreCase("11pt") || !fontSize.equalsIgnoreCase("12pt")
					    || !fontSize.equalsIgnoreCase("13pt") || !fontSize.equalsIgnoreCase("14pt") || !fontSize.equalsIgnoreCase("15pt")))
				fontSize = "10pt";

			    if (isItalic) {
				patientName = "<i>" + patientName + "</i>";
				pid = "<i>" + pid + "</i>";
				dob = "<i>" + dob + "</i>";
				gender = "<i>" + gender + "</i>";
				mobileNumber = "<i>" + mobileNumber + "</i>";
				refferedBy = "<i>" + refferedBy + "</i>";
				date = "<i>" + date + "</i>";
				resourceId = "<i>" + resourceId + "</i>";
			    }
			    if (isBold) {
				patientName = "<b>" + patientName + "</b>";
				pid = "<b>" + pid + "</b>";
				dob = "<b>" + dob + "</b>";
				gender = "<b>" + gender + "</b>";
				mobileNumber = "<b>" + mobileNumber + "</b>";
				refferedBy = "<b>" + refferedBy + "</b>";
				date = "<b>" + date + "</b>";
				resourceId = "<b>" + resourceId + "</b>";
			    }
			    patientName = "<span style='font-size:" + fontSize + "'>" + patientName + "</span>";
			    pid = "<span style='font-size:" + fontSize + "'>" + pid + "</span>";
			    dob = "<span style='font-size:" + fontSize + "'>" + dob + "</span>";
			    gender = "<span style='font-size:" + fontSize + "'>" + gender + "</span>";
			    mobileNumber = "<span style='font-size:" + fontSize + "'>" + mobileNumber + "</span>";
			    refferedBy = "<span style='font-size:" + fontSize + "'>" + refferedBy + "</span>";
			    date = "<span style='font-size:" + fontSize + "'>" + date + "</span>";
			    resourceId = "<span style='font-size:" + fontSize + "'>" + resourceId + "</span>";
			}
		    }
		}

		UserCollection doctorUser = userRepository.findOne(clinicalNotesCollection.getDoctorId());
		if (doctorUser != null)
		    parameters.put("footerSignature", doctorUser.getTitle() + " " + doctorUser.getFirstName());

		parameters.put("patientLeftText", patientName + pid + dob + gender);
		parameters.put("patientRightText", mobileNumber + refferedBy + date + resourceId);
		parameters.put("headerLeftText", headerLeftText);
		parameters.put("headerRightText", headerRightText);
		parameters.put("footerBottomText", footerBottomText);
		parameters.put("logoURL", logoURL);
		String layout = printSettings != null ? (printSettings.getPageSetup() != null ? printSettings.getPageSetup().getLayout() : "PORTRAIT")
			: "PORTRAIT";
		String pageSize = printSettings != null ? (printSettings.getPageSetup() != null ? printSettings.getPageSetup().getPageSize() : "A4") : "A4";
		String margins = printSettings != null ? (printSettings.getPageSetup() != null ? printSettings.getPageSetup().getMargins() : null) : null;

		String pdfName = (user != null ? user.getFirstName() : "") + new SimpleDateFormat("dd-MM-yyyy").format(new Date()) + "CLINICALNOTES";
		response = jasperReportService.createPDF(parameters, "mongo-clinical-notes", layout, pageSize, margins, pdfName.replaceAll("\\s+", ""));

		return response;
	}
}
