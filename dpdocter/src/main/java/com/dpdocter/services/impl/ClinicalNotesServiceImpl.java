package com.dpdocter.services.impl;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.IteratorUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

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
import com.dpdocter.beans.PrintSettingsText;
import com.dpdocter.collections.ClinicalNotesCollection;
import com.dpdocter.collections.ComplaintCollection;
import com.dpdocter.collections.DiagnosisCollection;
import com.dpdocter.collections.DiagramsCollection;
import com.dpdocter.collections.EmailTrackCollection;
import com.dpdocter.collections.InvestigationCollection;
import com.dpdocter.collections.LocationCollection;
import com.dpdocter.collections.NotesCollection;
import com.dpdocter.collections.ObservationCollection;
import com.dpdocter.collections.PatientAdmissionCollection;
import com.dpdocter.collections.PatientClinicalNotesCollection;
import com.dpdocter.collections.PatientCollection;
import com.dpdocter.collections.PrintSettingsCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.enums.ClinicalItems;
import com.dpdocter.enums.ComponentType;
import com.dpdocter.enums.Range;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.ClinicalNotesRepository;
import com.dpdocter.repository.ComplaintRepository;
import com.dpdocter.repository.DiagnosisRepository;
import com.dpdocter.repository.DiagramsRepository;
import com.dpdocter.repository.InvestigationRepository;
import com.dpdocter.repository.LocationRepository;
import com.dpdocter.repository.NotesRepository;
import com.dpdocter.repository.ObservationRepository;
import com.dpdocter.repository.PatientAdmissionRepository;
import com.dpdocter.repository.PatientClinicalNotesRepository;
import com.dpdocter.repository.PatientRepository;
import com.dpdocter.repository.PrintSettingsRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.request.ClinicalNotesAddRequest;
import com.dpdocter.request.ClinicalNotesEditRequest;
import com.dpdocter.services.ClinicalNotesService;
import com.dpdocter.services.EmailTackService;
import com.dpdocter.services.FileManager;
import com.dpdocter.services.JasperReportService;
import com.dpdocter.services.MailService;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import common.util.web.DPDoctorUtils;

@Service
public class ClinicalNotesServiceImpl implements ClinicalNotesService {

    private static Logger logger = Logger.getLogger(ClinicalNotesServiceImpl.class.getName());

    @Autowired
    private ClinicalNotesRepository clinicalNotesRepository;

    @Autowired
    private PatientClinicalNotesRepository patientClinicalNotesRepository;

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
    private MongoTemplate mongoTemplate;

    @Autowired
    private UserRepository userRepository;

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
    private PatientAdmissionRepository patientAdmissionRepository;
    
    @Context
    private UriInfo uriInfo;

    @Value(value = "${IMAGE_URL_ROOT_PATH}")
    private String imageUrlRootPath;

    @Value(value = "${IMAGE_RESOURCE}")
    private String imageResource;

    @Override
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
			complaintCollection = complaintRepository.save(complaintCollection);
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
			observationCollection = observationRepository.save(observationCollection);
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
			investigationCollection = investigationRepository.save(investigationCollection);
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
			notesCollection = notesRepository.save(notesCollection);
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
			diagnosisCollection = diagnosisRepository.save(diagnosisCollection);
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

	    clinicalNotesCollection.setCreatedTime(createdTime);
	    clinicalNotesCollection = clinicalNotesRepository.save(clinicalNotesCollection);

	    clinicalNotes = new ClinicalNotes();
	    BeanUtil.map(clinicalNotesCollection, clinicalNotes);

	    if (clinicalNotesCollection != null) {
		if (request.getId() == null) {
		    // map the clinical notes with patient
		    PatientClinicalNotesCollection patientClinicalNotesCollection = new PatientClinicalNotesCollection();
		    patientClinicalNotesCollection.setClinicalNotesId(clinicalNotesCollection.getId());
		    patientClinicalNotesCollection.setPatientId(request.getPatientId());
		    patientClinicalNotesCollection.setCreatedTime(createdTime);
		    patientClinicalNotesRepository.save(patientClinicalNotesCollection);
		    clinicalNotes.setPatientId(patientClinicalNotesCollection.getPatientId());
		}

	    }

	    // Setting detail of complaints, investigations, observations,
	    // diagnoses, notes and diagrams into response.
	    List<Complaint> complaints = IteratorUtils.toList(complaintRepository.findAll(complaintIds).iterator());
	    List<Investigation> investigations = IteratorUtils.toList(investigationRepository.findAll(investigationIds).iterator());
	    List<Observation> observations = IteratorUtils.toList(observationRepository.findAll(observationIds).iterator());
	    List<Diagnoses> diagnoses = IteratorUtils.toList(diagnosisRepository.findAll(diagnosisIds).iterator());
	    List<Notes> notes = IteratorUtils.toList(notesRepository.findAll(noteIds).iterator());
	    List<Diagram> diagrams = IteratorUtils.toList(diagramsRepository.findAll(diagramIds).iterator());
	    /*if (request.getDiagrams() != null && !request.getDiagrams().isEmpty()) {
	    diagrams = IteratorUtils.toList(diagramsRepository.findAll(request.getDiagrams()).iterator());
	    } else {
	    diagrams = new ArrayList<Diagram>();
	    }*/

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
    public ClinicalNotes getNotesById(String id) {
	ClinicalNotes clinicalNote = null;
	try {
	    ClinicalNotesCollection clinicalNotesCollection = clinicalNotesRepository.findOne(id);
	    if (clinicalNotesCollection != null) {
		clinicalNote = new ClinicalNotes();
		BeanUtil.map(clinicalNotesCollection, clinicalNote);
		@SuppressWarnings("unchecked")
		List<ComplaintCollection> complaintCollections = IteratorUtils.toList(complaintRepository.findAll(clinicalNotesCollection.getComplaints())
			.iterator());
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
		List<ObservationCollection> observationCollections = IteratorUtils.toList(observationRepository.findAll(
			clinicalNotesCollection.getObservations()).iterator());
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
		List<InvestigationCollection> investigationCollections = IteratorUtils.toList(investigationRepository.findAll(
			clinicalNotesCollection.getInvestigations()).iterator());
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
		List<DiagnosisCollection> diagnosisCollections = IteratorUtils.toList(diagnosisRepository.findAll(clinicalNotesCollection.getDiagnoses())
			.iterator());
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
		    List<DiagramsCollection> diagramsCollections = IteratorUtils.toList(diagramsRepository.findAll(clinicalNotesCollection.getDiagrams())
			    .iterator());
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
			complaintCollection.setCreatedTime(createdTime);
			complaintCollection = complaintRepository.save(complaintCollection);
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
			observationCollection = observationRepository.save(observationCollection);
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
			investigationCollection = investigationRepository.save(investigationCollection);
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
			notesCollection = notesRepository.save(notesCollection);
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
			diagnosisCollection = diagnosisRepository.save(diagnosisCollection);
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

	    clinicalNotesCollection = clinicalNotesRepository.save(clinicalNotesCollection);
	    if (clinicalNotesCollection != null) {
		if (request.getId() == null) {
		    // map the clinical notes with patient
		    PatientClinicalNotesCollection patientClinicalNotesCollection = new PatientClinicalNotesCollection();
		    patientClinicalNotesCollection.setClinicalNotesId(clinicalNotesCollection.getId());
		    patientClinicalNotesCollection.setPatientId(request.getPatientId());
		    patientClinicalNotesRepository.save(patientClinicalNotesCollection);
		}

	    }
	    clinicalNotes = new ClinicalNotes();
	    BeanUtil.map(clinicalNotesCollection, clinicalNotes);

	    // Setting detail of complaints, investigations, observations,
	    // diagnoses, notes and diagrams into response.
	    List<Complaint> complaints = IteratorUtils.toList(complaintRepository.findAll(complaintIds).iterator());
	    List<Investigation> investigations = IteratorUtils.toList(investigationRepository.findAll(investigationIds).iterator());
	    List<Observation> observations = IteratorUtils.toList(observationRepository.findAll(observationIds).iterator());
	    List<Diagnoses> diagnoses = IteratorUtils.toList(diagnosisRepository.findAll(diagnosisIds).iterator());
	    List<Notes> notes = IteratorUtils.toList(notesRepository.findAll(noteIds).iterator());
	    List<Diagram> diagrams = IteratorUtils.toList(diagramsRepository.findAll(diagramIds).iterator());
	    /*if (request.getDiagrams() != null && !request.getDiagrams().isEmpty()) {
	    diagrams = IteratorUtils.toList(diagramsRepository.findAll(request.getDiagrams()).iterator());
	    } else {
	    diagrams = new ArrayList<Diagram>();
	    }*/

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
    public void deleteNote(String id, Boolean discarded) {
	try {
	    List<PatientClinicalNotesCollection> patientClinicalNotesCollections = patientClinicalNotesRepository.findByClinicalNotesId(id);
	    if (patientClinicalNotesCollections != null) {
		for (PatientClinicalNotesCollection patientClinicalNotesCollection : patientClinicalNotesCollections) {
		    if (discarded == null)
			patientClinicalNotesCollection.setDiscarded(true);
		    else
			patientClinicalNotesCollection.setDiscarded(discarded);
		    patientClinicalNotesCollection.setUpdatedTime(new Date());
		    patientClinicalNotesRepository.save(patientClinicalNotesCollection);
		}

	    }
	    ClinicalNotesCollection clinicalNotes = clinicalNotesRepository.findOne(id);
	    if (discarded == null)
		clinicalNotes.setDiscarded(true);
	    else
		clinicalNotes.setDiscarded(discarded);
	    clinicalNotesRepository.save(clinicalNotes);
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}

    }

    @Override
    public List<ClinicalNotes> getPatientsClinicalNotesWithVerifiedOTP(int page, int size, String patientId, String updatedTime, boolean discarded) {
	List<ClinicalNotes> clinicalNotesList = null;
	List<PatientClinicalNotesCollection> patientClinicalNotesCollections = null;
	boolean[] discards = new boolean[2];
	discards[0] = false;

	try {
	    if (discarded)
		discards[1] = true;

	    long createdTimeStamp = Long.parseLong(updatedTime);
	    if (size > 0)
		patientClinicalNotesCollections = patientClinicalNotesRepository.findByPatientId(patientId, discards, new Date(createdTimeStamp),
			new PageRequest(page, size, Direction.DESC, "updatedTime"));
	    else
		patientClinicalNotesCollections = patientClinicalNotesRepository.findByPatientId(patientId, discards, new Date(createdTimeStamp), new Sort(
			Sort.Direction.DESC, "updatedTime"));

	    if (patientClinicalNotesCollections != null) {
		@SuppressWarnings("unchecked")
		Collection<String> clinicalNotesIds = CollectionUtils.collect(patientClinicalNotesCollections, new BeanToPropertyValueTransformer(
			"clinicalNotesId"));
		clinicalNotesList = new ArrayList<ClinicalNotes>();
		for (String clinicalNotesId : clinicalNotesIds) {
		    ClinicalNotes clinicalNotes = getNotesById(clinicalNotesId);
		    if (clinicalNotes != null) {
			UserCollection userCollection = userRepository.findOne(clinicalNotes.getDoctorId());
			if (userCollection != null) {
			    clinicalNotes.setDoctorName(userCollection.getFirstName());
			}
			clinicalNotesList.add(clinicalNotes);
		    }
		}
	    } else {
		logger.warn("No Clinical Notes found for patient Id : " + patientId);
		throw new BusinessException(ServiceError.Unknown, "No Clinical Notes found for patient Id : " + patientId);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
	return clinicalNotesList;
    }

    @Override
    public List<ClinicalNotes> getPatientsClinicalNotesWithoutVerifiedOTP(int page, int size, String patientId, String doctorId, String locationId,
	    String hospitalId, String updatedTime, boolean discarded) {
	List<ClinicalNotes> clinicalNotesList = null;
	List<PatientClinicalNotesCollection> patientClinicalNotesCollections = null;
	try {
	    long createdTimeStamp = Long.parseLong(updatedTime);
	    if (size > 0)
		patientClinicalNotesCollections = patientClinicalNotesRepository.findByPatientId(patientId, new Date(createdTimeStamp), new PageRequest(page,
			size, Direction.DESC, "updatedTime"));
	    else
		patientClinicalNotesCollections = patientClinicalNotesRepository.findByPatientId(patientId, new Date(createdTimeStamp), new Sort(
			Sort.Direction.DESC, "updatedTime"));
	    if (patientClinicalNotesCollections != null) {
		@SuppressWarnings("unchecked")
		Collection<String> clinicalNotesIds = CollectionUtils.collect(patientClinicalNotesCollections, new BeanToPropertyValueTransformer(
			"clinicalNotesId"));
		clinicalNotesList = new ArrayList<ClinicalNotes>();
		if (DPDoctorUtils.allStringsEmpty(locationId, hospitalId)) {
		    for (String clinicalNotesId : clinicalNotesIds) {
			ClinicalNotes clinicalNotes = getNotesById(clinicalNotesId);
			if (clinicalNotes != null) {
			    if (clinicalNotes.getDoctorId().equals(doctorId)) {
				UserCollection userCollection = userRepository.findOne(clinicalNotes.getDoctorId());
				if (userCollection != null) {
				    clinicalNotes.setDoctorName(userCollection.getFirstName() + userCollection.getLastName());
				}
				clinicalNotesList.add(clinicalNotes);
			    }
			}
		    }
		} else {
		    for (String clinicalNotesId : clinicalNotesIds) {
			ClinicalNotes clinicalNotes = getNotesById(clinicalNotesId);
			if (clinicalNotes != null) {
			    if (clinicalNotes.getDoctorId().equals(doctorId) && clinicalNotes.getLocationId().equals(locationId)
				    && clinicalNotes.getHospitalId().equals(hospitalId)) {
				UserCollection userCollection = userRepository.findOne(clinicalNotes.getDoctorId());
				if (userCollection != null) {
				    clinicalNotes.setDoctorName(userCollection.getFirstName() + userCollection.getLastName());
				}
				clinicalNotesList.add(clinicalNotes);
			    }
			}
		    }
		}
	    } else {
		logger.warn("No Clinical Notes found for patient Id : " + patientId);
		throw new BusinessException(ServiceError.Unknown, "No Clinical Notes found for patient Id : " + patientId);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
	return clinicalNotesList;
    }

    @Override
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
    public Diagram addEditDiagram(Diagram diagram) {
	try {
	    if (diagram.getDiagram() != null) {
		String path = "clinicalNotes" + File.separator + "diagrams";
		diagram.getDiagram().setFileName(diagram.getDiagram().getFileName() + new Date().getTime());
		String diagramUrl = fileManager.saveImageAndReturnImageUrl(diagram.getDiagram(), path);
		diagram.setDiagramUrl(diagramUrl);

	    }
	    DiagramsCollection diagramsCollection = new DiagramsCollection();
	    BeanUtil.map(diagram, diagramsCollection);
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
    public void deleteComplaint(String id, String doctorId, String locationId, String hospitalId, Boolean discarded) {
	try {
	    ComplaintCollection complaintCollection = complaintRepository.findOne(id);
	    if (complaintCollection != null) {
		if (complaintCollection.getDoctorId() != null && complaintCollection.getHospitalId() != null && complaintCollection.getLocationId() != null) {
		    if (complaintCollection.getDoctorId().equals(doctorId) && complaintCollection.getHospitalId().equals(hospitalId)
			    && complaintCollection.getLocationId().equals(locationId)) {
			if (discarded == null)
			    complaintCollection.setDiscarded(true);
			else
			    complaintCollection.setDiscarded(discarded);
			complaintCollection.setUpdatedTime(new Date());
			complaintRepository.save(complaintCollection);
		    } else {
			logger.warn("Invalid Doctor Id, Hospital Id, Or Location Id");
			throw new BusinessException(ServiceError.Unknown, "Invalid Doctor Id, Hospital Id, Or Location Id");
		    }
		} else {
		    logger.warn("Cant delete Global Complaint.");
		    throw new BusinessException(ServiceError.Unknown, "Cant delete Global Complaint.");
		}

	    } else {
		logger.warn("Complaint not found!");
		throw new BusinessException(ServiceError.Unknown, "Complaint not found!");
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}

    }

    @Override
    public void deleteObservation(String id, String doctorId, String locationId, String hospitalId, Boolean discarded) {
	try {
	    ObservationCollection observationCollection = observationRepository.findOne(id);
	    if (observationCollection != null) {
		if (observationCollection.getDoctorId() != null && observationCollection.getHospitalId() != null
			&& observationCollection.getLocationId() != null) {
		    if (observationCollection.getDoctorId().equals(doctorId) && observationCollection.getHospitalId().equals(hospitalId)
			    && observationCollection.getLocationId().equals(locationId)) {
			if (discarded == null)
			    observationCollection.setDiscarded(true);
			else
			    observationCollection.setDiscarded(discarded);
			observationCollection.setUpdatedTime(new Date());
			observationRepository.save(observationCollection);
		    } else {
			logger.warn("Invalid Doctor Id, Hospital Id, Or Location Id");
			throw new BusinessException(ServiceError.Unknown, "Invalid Doctor Id, Hospital Id, Or Location Id");
		    }
		} else {
		    logger.warn("Cant delete Global Observation.");
		    throw new BusinessException(ServiceError.Unknown, "Cant delete Global Observation.");
		}
	    } else {
		logger.warn("Observation not found!");
		throw new BusinessException(ServiceError.Unknown, "Observation not found!");
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
    }

    @Override
    public void deleteInvestigation(String id, String doctorId, String locationId, String hospitalId, Boolean discarded) {
	try {
	    InvestigationCollection investigationCollection = investigationRepository.findOne(id);
	    if (investigationCollection != null) {
		if (investigationCollection.getDoctorId() != null && investigationCollection.getHospitalId() != null
			&& investigationCollection.getLocationId() != null) {
		    if (investigationCollection.getDoctorId().equals(doctorId) && investigationCollection.getHospitalId().equals(hospitalId)
			    && investigationCollection.getLocationId().equals(locationId)) {
			if (discarded == null)
			    investigationCollection.setDiscarded(true);
			else
			    investigationCollection.setDiscarded(discarded);
			investigationCollection.setUpdatedTime(new Date());
			investigationRepository.save(investigationCollection);
		    } else {
			logger.warn("Invalid Doctor Id, Hospital Id, Or Location Id");
			throw new BusinessException(ServiceError.Unknown, "Invalid Doctor Id, Hospital Id, Or Location Id");
		    }
		} else {
		    logger.warn("Cant delete Global Investigation.");
		    throw new BusinessException(ServiceError.Unknown, "Cant delete Global Investigation.");
		}
	    } else {
		logger.warn("Investigation not found!");
		throw new BusinessException(ServiceError.Unknown, "Investigation not found!");
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
    }

    @Override
    public void deleteDiagnosis(String id, String doctorId, String locationId, String hospitalId, Boolean discarded) {
	try {
	    DiagnosisCollection diagnosisCollection = diagnosisRepository.findOne(id);
	    if (diagnosisCollection != null) {
		if (diagnosisCollection.getDoctorId() != null && diagnosisCollection.getHospitalId() != null && diagnosisCollection.getLocationId() != null) {
		    if (diagnosisCollection.getDoctorId().equals(doctorId) && diagnosisCollection.getHospitalId().equals(hospitalId)
			    && diagnosisCollection.getLocationId().equals(locationId)) {
			if (discarded == null)
			    diagnosisCollection.setDiscarded(true);
			else
			    diagnosisCollection.setDiscarded(discarded);
			diagnosisCollection.setUpdatedTime(new Date());
			diagnosisRepository.save(diagnosisCollection);
		    } else {
			logger.warn("Invalid Doctor Id, Hospital Id, Or Location Id");
			throw new BusinessException(ServiceError.Unknown, "Invalid Doctor Id, Hospital Id, Or Location Id");
		    }
		} else {
		    logger.warn("Cant delete Global Diagnosis.");
		    throw new BusinessException(ServiceError.Unknown, "Cant delete Global Diagnosis.");
		}
	    } else {
		logger.warn("Diagnosis not found!");
		throw new BusinessException(ServiceError.Unknown, "Diagnosis not found!");
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
    }

    @Override
    public void deleteNotes(String id, String doctorId, String locationId, String hospitalId, Boolean discarded) {
	try {
	    NotesCollection notesCollection = notesRepository.findOne(id);
	    if (notesCollection != null) {
		if (notesCollection.getDoctorId() != null && notesCollection.getHospitalId() != null && notesCollection.getLocationId() != null) {
		    if (notesCollection.getDoctorId().equals(doctorId) && notesCollection.getHospitalId().equals(hospitalId)
			    && notesCollection.getLocationId().equals(locationId)) {
			if (discarded == null)
			    notesCollection.setDiscarded(true);
			else
			    notesCollection.setDiscarded(discarded);
			notesCollection.setUpdatedTime(new Date());
			notesRepository.save(notesCollection);
		    } else {
			logger.warn("Invalid Doctor Id, Hospital Id, Or Location Id");
			throw new BusinessException(ServiceError.Unknown, "Invalid Doctor Id, Hospital Id, Or Location Id");
		    }
		} else {
		    logger.warn("Cant delete Global Notes.");
		    throw new BusinessException(ServiceError.Unknown, "Cant delete Global Notes.");
		}

	    } else {
		logger.warn("Notes not found!");
		throw new BusinessException(ServiceError.Unknown, "Notes not found!");
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
    }

    @Override
    public void deleteDiagram(String id, String doctorId, String locationId, String hospitalId, Boolean discarded) {
	try {
	    DiagramsCollection diagramsCollection = diagramsRepository.findOne(id);
	    if (diagramsCollection != null) {
		if (diagramsCollection.getDoctorId() != null && diagramsCollection.getHospitalId() != null && diagramsCollection.getLocationId() != null) {
		    if (diagramsCollection.getDoctorId().equals(doctorId) && diagramsCollection.getHospitalId().equals(hospitalId)
			    && diagramsCollection.getLocationId().equals(locationId)) {
			if (discarded == null)
			    diagramsCollection.setDiscarded(true);
			else
			    diagramsCollection.setDiscarded(discarded);
			diagramsCollection.setUpdatedTime(new Date());
			diagramsRepository.save(diagramsCollection);
		    } else {
			logger.warn("Invalid Doctor Id, Hospital Id, Or Location Id");
			throw new BusinessException(ServiceError.Unknown, "Invalid Doctor Id, Hospital Id, Or Location Id");
		    }
		} else {
		    logger.warn("Cant delete Global Diagram.");
		    throw new BusinessException(ServiceError.Unknown, "Cant delete Global Diagram.");
		}

	    } else {
		logger.warn("Diagram not found!");
		throw new BusinessException(ServiceError.Unknown, "Diagram not found!");
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
    }

    @Override
    public Integer getClinicalNotesCount(String doctorId, String patientId, String locationId, String hospitalId) {
	List<ClinicalNotesCollection> clinicalNotesCollections = null;
	Integer clinicalNotesCount = 0;
	try {
	    clinicalNotesCollections = clinicalNotesRepository.getClinicalNotes(doctorId, hospitalId, locationId);
	    @SuppressWarnings("unchecked")
	    List<String> clinicalNotesIds = (List<String>) CollectionUtils.collect(clinicalNotesCollections, new BeanToPropertyValueTransformer("id"));
	    clinicalNotesCount = patientClinicalNotesRepository.findCount(patientId, clinicalNotesIds);
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Clinical Notes Count");
	}
	return clinicalNotesCount;
    }

    @Override
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
		response = getGlobalDiagrams(page, size, updatedTime, discarded);
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
		    complaintCollections = complaintRepository.findCustomGlobalComplaints(new Date(createdTimeStamp), discards, new PageRequest(page, size,
			    Direction.DESC, "updatedTime"));
		else
		    complaintCollections = complaintRepository.findCustomGlobalComplaints(new Date(createdTimeStamp), discards, new Sort(Sort.Direction.DESC,
			    "updatedTime"));

	    } else {
		if (DPDoctorUtils.anyStringEmpty(locationId, hospitalId)) {
		    if (size > 0)
			complaintCollections = complaintRepository.findCustomGlobalComplaints(doctorId, new Date(createdTimeStamp), discards, new PageRequest(
				page, size, Direction.DESC, "updatedTime"));
		    else
			complaintCollections = complaintRepository.findCustomGlobalComplaints(doctorId, new Date(createdTimeStamp), discards, new Sort(
				Sort.Direction.DESC, "updatedTime"));
		} else {
		    if (size > 0)
			complaintCollections = complaintRepository.findCustomGlobalComplaints(doctorId, locationId, hospitalId, new Date(createdTimeStamp),
				discards, new PageRequest(page, size, Direction.DESC, "updatedTime"));
		    else
			complaintCollections = complaintRepository.findCustomGlobalComplaints(doctorId, locationId, hospitalId, new Date(createdTimeStamp),
				discards, new Sort(Sort.Direction.DESC, "updatedTime"));
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
		complaintCollections = complaintRepository.findGlobalComplaints(new Date(createdTimeStamp), discards, new PageRequest(page, size,
			Direction.DESC, "updatedTime"));
	    else
		complaintCollections = complaintRepository.findGlobalComplaints(new Date(createdTimeStamp), discards, new Sort(Sort.Direction.DESC,
			"updatedTime"));

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
			complaintCollections = complaintRepository.findCustomComplaints(doctorId, new Date(createdTimeStamp), discards, new PageRequest(page,
				size, Direction.DESC, "updatedTime"));
		    else
			complaintCollections = complaintRepository.findCustomComplaints(doctorId, new Date(createdTimeStamp), discards, new Sort(
				Sort.Direction.DESC, "updatedTime"));
		} else {
		    if (size > 0)
			complaintCollections = complaintRepository.findCustomComplaints(doctorId, locationId, hospitalId, new Date(createdTimeStamp), discards,
				new PageRequest(page, size, Direction.DESC, "updatedTime"));
		    else
			complaintCollections = complaintRepository.findCustomComplaints(doctorId, locationId, hospitalId, new Date(createdTimeStamp), discards,
				new Sort(Sort.Direction.DESC, "updatedTime"));

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
		    investigationsCollections = investigationRepository.findCustomGlobalInvestigations(new Date(createdTimeStamp), discards, new PageRequest(
			    page, size, Direction.DESC, "updatedTime"));
		else
		    investigationsCollections = investigationRepository.findCustomGlobalInvestigations(new Date(createdTimeStamp), discards, new Sort(
			    Sort.Direction.DESC, "updatedTime"));

	    } else {
		if (DPDoctorUtils.anyStringEmpty(locationId, hospitalId)) {
		    if (size > 0)
			investigationsCollections = investigationRepository.findCustomGlobalInvestigations(doctorId, new Date(createdTimeStamp), discards,
				new PageRequest(page, size, Direction.DESC, "updatedTime"));
		    else
			investigationsCollections = investigationRepository.findCustomGlobalInvestigations(doctorId, new Date(createdTimeStamp), discards,
				new Sort(Sort.Direction.DESC, "updatedTime"));
		} else {
		    if (size > 0)
			investigationsCollections = investigationRepository.findCustomGlobalInvestigations(doctorId, locationId, hospitalId, new Date(
				createdTimeStamp), discards, new PageRequest(page, size, Direction.DESC, "updatedTime"));
		    else
			investigationsCollections = investigationRepository.findCustomGlobalInvestigations(doctorId, locationId, hospitalId, new Date(
				createdTimeStamp), discards, new Sort(Sort.Direction.DESC, "updatedTime"));
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
		investigationsCollections = investigationRepository.findGlobalInvestigations(new Date(createdTimeStamp), discards, new PageRequest(page, size,
			Direction.DESC, "updatedTime"));
	    else
		investigationsCollections = investigationRepository.findGlobalInvestigations(new Date(createdTimeStamp), discards, new Sort(
			Sort.Direction.DESC, "updatedTime"));

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
				new PageRequest(page, size, Direction.DESC, "updatedTime"));
		    else
			investigationsCollections = investigationRepository.findCustomInvestigations(doctorId, new Date(createdTimeStamp), discards, new Sort(
				Sort.Direction.DESC, "updatedTime"));
		} else {
		    if (size > 0)
			investigationsCollections = investigationRepository.findCustomInvestigations(doctorId, locationId, hospitalId, new Date(
				createdTimeStamp), discards, new PageRequest(page, size, Direction.DESC, "updatedTime"));
		    else
			investigationsCollections = investigationRepository.findCustomInvestigations(doctorId, locationId, hospitalId, new Date(
				createdTimeStamp), discards, new Sort(Sort.Direction.DESC, "updatedTime"));
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
		    observationCollections = observationRepository.findCustomGlobalObservations(new Date(createdTimeStamp), discards, new PageRequest(page,
			    size, Direction.DESC, "updatedTime"));
		else
		    observationCollections = observationRepository.findCustomGlobalObservations(new Date(createdTimeStamp), discards, new Sort(
			    Sort.Direction.DESC, "updatedTime"));

	    } else {
		if (locationId == null && hospitalId == null) {
		    if (size > 0)
			observationCollections = observationRepository.findCustomGlobalObservations(doctorId, new Date(createdTimeStamp), discards,
				new PageRequest(page, size, Direction.DESC, "updatedTime"));
		    else
			observationCollections = observationRepository.findCustomGlobalObservations(doctorId, new Date(createdTimeStamp), discards, new Sort(
				Sort.Direction.DESC, "createdTime"));
		} else {
		    if (size > 0)
			observationCollections = observationRepository.findCustomGlobalObservations(doctorId, locationId, hospitalId,
				new Date(createdTimeStamp), discards, new PageRequest(page, size, Direction.DESC, "updatedTime"));
		    else
			observationCollections = observationRepository.findCustomGlobalObservations(doctorId, locationId, hospitalId,
				new Date(createdTimeStamp), discards, new Sort(Sort.Direction.DESC, "createdTime"));

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
		observationCollections = observationRepository.findGlobalObservations(new Date(createdTimeStamp), discards, new PageRequest(page, size,
			Direction.DESC, "updatedTime"));
	    else
		observationCollections = observationRepository.findGlobalObservations(new Date(createdTimeStamp), discards, new Sort(Sort.Direction.DESC,
			"updatedTime"));

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

    private List<Object> getCustomObservations(int page, int size, String doctorId, String locationId, String hospitalId, String updatedTime, Boolean discarded) {
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

	    if (DPDoctorUtils.anyStringEmpty(locationId, hospitalId)) {
		if (size > 0)
		    observationCollections = observationRepository.findCustomObservations(doctorId, new Date(createdTimeStamp), discards, new PageRequest(page,
			    size, Direction.DESC, "updatedTime"));
		else
		    observationCollections = observationRepository.findCustomObservations(doctorId, new Date(createdTimeStamp), discards, new Sort(
			    Sort.Direction.DESC, "updatedTime"));
	    } else {
		if (size > 0)
		    observationCollections = observationRepository.findCustomObservations(doctorId, locationId, hospitalId, new Date(createdTimeStamp),
			    discards, new PageRequest(page, size, Direction.DESC, "updatedTime"));
		else
		    observationCollections = observationRepository.findCustomObservations(doctorId, locationId, hospitalId, new Date(createdTimeStamp),
			    discards, new Sort(Sort.Direction.DESC, "updatedTime"));
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
		    diagnosisCollections = diagnosisRepository.findCustomGlobalDiagnosis(new Date(createdTimeStamp), discards, new PageRequest(page, size,
			    Direction.DESC, "updatedTime"));
		else
		    diagnosisCollections = diagnosisRepository.findCustomGlobalDiagnosis(new Date(createdTimeStamp), discards, new Sort(Sort.Direction.DESC,
			    "updatedTime"));
	    } else {

		if (locationId == null && hospitalId == null) {
		    if (size > 0)
			diagnosisCollections = diagnosisRepository.findCustomGlobalDiagnosis(doctorId, new Date(createdTimeStamp), discards, new PageRequest(
				page, size, Direction.DESC, "updatedTime"));
		    else
			diagnosisCollections = diagnosisRepository.findCustomGlobalDiagnosis(doctorId, new Date(createdTimeStamp), discards, new Sort(
				Sort.Direction.DESC, "createdTime"));

		} else {
		    if (size > 0)
			diagnosisCollections = diagnosisRepository.findCustomGlobalDiagnosis(doctorId, locationId, hospitalId, new Date(createdTimeStamp),
				discards, new PageRequest(page, size, Direction.DESC, "updatedTime"));
		    else
			diagnosisCollections = diagnosisRepository.findCustomGlobalDiagnosis(doctorId, locationId, hospitalId, new Date(createdTimeStamp),
				discards, new Sort(Sort.Direction.DESC, "updatedTime"));
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
		diagnosisCollections = diagnosisRepository.findGlobalDiagnosis(new Date(createdTimeStamp), discards, new PageRequest(page, size,
			Direction.DESC, "updatedTime"));
	    else
		diagnosisCollections = diagnosisRepository.findGlobalDiagnosis(new Date(createdTimeStamp), discards, new Sort(Sort.Direction.DESC,
			"updatedTime"));
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
	    if (DPDoctorUtils.anyStringEmpty(locationId, hospitalId)) {
		if (size > 0)
		    diagnosisCollections = diagnosisRepository.findCustomDiagnosis(doctorId, new Date(createdTimeStamp), discards, new PageRequest(page, size,
			    Direction.DESC, "updatedTime"));
		else
		    diagnosisCollections = diagnosisRepository.findCustomDiagnosis(doctorId, new Date(createdTimeStamp), discards, new Sort(
			    Sort.Direction.DESC, "updatedTime"));
	    } else {
		if (size > 0)
		    diagnosisCollections = diagnosisRepository.findCustomDiagnosis(doctorId, locationId, hospitalId, new Date(createdTimeStamp), discards,
			    new PageRequest(page, size, Direction.DESC, "updatedTime"));
		else
		    diagnosisCollections = diagnosisRepository.findCustomDiagnosis(doctorId, locationId, hospitalId, new Date(createdTimeStamp), discards,
			    new Sort(Sort.Direction.DESC, "updatedTime"));
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

    private List<Object> getCustomGlobalNotes(int page, int size, String doctorId, String locationId, String hospitalId, String updatedTime, Boolean discarded) {
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
		    notesCollections = notesRepository.findCustomGlobalNotes(new Date(createdTimeStamp), discards, new PageRequest(page, size, Direction.DESC,
			    "updatedTime"));
		else
		    notesCollections = notesRepository
			    .findCustomGlobalNotes(new Date(createdTimeStamp), discards, new Sort(Sort.Direction.DESC, "updatedTime"));
	    } else {
		if (DPDoctorUtils.anyStringEmpty(locationId, hospitalId)) {
		    if (size > 0)
			notesCollections = notesRepository.findCustomGlobalNotes(doctorId, new Date(createdTimeStamp), discards, new PageRequest(page, size,
				Direction.DESC, "updatedTime"));
		    else
			notesCollections = notesRepository.findCustomGlobalNotes(doctorId, new Date(createdTimeStamp), discards, new Sort(Sort.Direction.DESC,
				"updatedTime"));
		} else {
		    if (size > 0)
			notesCollections = notesRepository.findCustomGlobalNotes(doctorId, locationId, hospitalId, new Date(createdTimeStamp), discards,
				new PageRequest(page, size, Direction.DESC, "updatedTime"));
		    else
			notesCollections = notesRepository.findCustomGlobalNotes(doctorId, locationId, hospitalId, new Date(createdTimeStamp), discards,
				new Sort(Sort.Direction.DESC, "createdTime"));
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
		notesCollections = notesRepository.findGlobalNotes(new Date(createdTimeStamp), discards, new PageRequest(page, size, Direction.DESC,
			"updatedTime"));
	    else
		notesCollections = notesRepository.findGlobalNotes(new Date(createdTimeStamp), discards, new Sort(Sort.Direction.DESC, "updatedTime"));

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
			notesCollections = notesRepository.findCustomNotes(doctorId, new Date(createdTimeStamp), discards, new PageRequest(page, size,
				Direction.DESC, "updatedTime"));
		    else
			notesCollections = notesRepository.findCustomNotes(doctorId, new Date(createdTimeStamp), discards, new Sort(Sort.Direction.DESC,
				"updatedTime"));
		} else {
		    if (size > 0)
			notesCollections = notesRepository.findCustomNotes(doctorId, locationId, hospitalId, new Date(createdTimeStamp), discards,
				new PageRequest(page, size, Direction.DESC, "updatedTime"));
		    else
			notesCollections = notesRepository.findCustomNotes(doctorId, locationId, hospitalId, new Date(createdTimeStamp), discards, new Sort(
				Sort.Direction.DESC, "updatedTime"));
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
		    diagramCollections = diagramsRepository.findCustomGlobalDiagrams(new Date(createdTimeStamp), discards, new PageRequest(page, size,
			    Direction.DESC, "updatedTime"));
		else
		    diagramCollections = diagramsRepository.findCustomGlobalDiagrams(new Date(createdTimeStamp), discards, new Sort(Sort.Direction.DESC,
			    "updatedTime"));

	    } else {
		if (locationId == null && hospitalId == null) {
		    if (size > 0)
			diagramCollections = diagramsRepository.findCustomGlobalDiagrams(doctorId, new Date(createdTimeStamp), discards, new PageRequest(page,
				size, Direction.DESC, "updatedTime"));
		    else
			diagramCollections = diagramsRepository.findCustomGlobalDiagrams(doctorId, new Date(createdTimeStamp), discards, new Sort(
				Sort.Direction.DESC, "updatedTime"));
		} else {
		    if (size > 0)
			diagramCollections = diagramsRepository.findCustomGlobalDiagrams(doctorId, locationId, hospitalId, new Date(createdTimeStamp),
				discards, new PageRequest(page, size, Direction.DESC, "updatedTime"));
		    else
			diagramCollections = diagramsRepository.findCustomGlobalDiagrams(doctorId, locationId, hospitalId, new Date(createdTimeStamp),
				discards, new Sort(Sort.Direction.DESC, "updatedTime"));
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

    private List<Object> getGlobalDiagrams(int page, int size, String updatedTime, Boolean discarded) {
	List<DiagramsCollection> diagramCollections = null;
	List<Object> response = null;
	boolean[] discards = new boolean[2];
	discards[0] = false;
	try {
	    if (discarded)
		discards[1] = true;
	    long createdTimeStamp = Long.parseLong(updatedTime);

	    if (size > 0)
		diagramCollections = diagramsRepository.findGlobalDiagrams(new Date(createdTimeStamp), discards, new PageRequest(page, size, Direction.DESC,
			"updatedTime"));
	    else
		diagramCollections = diagramsRepository.findGlobalDiagrams(new Date(createdTimeStamp), discards, new Sort(Sort.Direction.DESC, "updatedTime"));

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
		if (locationId == null && hospitalId == null) {
		    if (size > 0)
			diagramCollections = diagramsRepository.findCustomDiagrams(doctorId, new Date(createdTimeStamp), discards, new PageRequest(page, size,
				Direction.DESC, "updatedTime"));
		    else
			diagramCollections = diagramsRepository.findCustomDiagrams(doctorId, new Date(createdTimeStamp), discards, new Sort(
				Sort.Direction.DESC, "updatedTime"));
		} else {
		    if (size > 0)
			diagramCollections = diagramsRepository.findCustomDiagrams(doctorId, locationId, hospitalId, new Date(createdTimeStamp), discards,
				new PageRequest(page, size, Direction.DESC, "updatedTime"));
		    else
			diagramCollections = diagramsRepository.findCustomDiagrams(doctorId, locationId, hospitalId, new Date(createdTimeStamp), discards,
				new Sort(Sort.Direction.DESC, "updatedTime"));
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
    public MailAttachment getClinicalNotesMailData(String clinicalNotesId, String doctorId, String locationId, String hospitalId) {
	return createMailData(clinicalNotesId, doctorId, locationId, hospitalId);
    }

    private MailAttachment createMailData(String clinicalNotesId, String doctorId, String locationId, String hospitalId) {
	ClinicalNotesCollection clinicalNotesCollection = null;
	Map<String, Object> parameters = new HashMap<String, Object>();
	MailAttachment mailAttachment = null;
	PatientCollection patient = null;
	PatientAdmissionCollection patientAdmission = null;
	UserCollection user = null;
	EmailTrackCollection emailTrackCollection = new EmailTrackCollection();
	String patientId = null;
	try {
	    clinicalNotesCollection = clinicalNotesRepository.findOne(clinicalNotesId);
	    if (clinicalNotesCollection != null) {
		if (clinicalNotesCollection.getDoctorId() != null && clinicalNotesCollection.getHospitalId() != null
			&& clinicalNotesCollection.getLocationId() != null) {
		    if (clinicalNotesCollection.getDoctorId().equals(doctorId) && clinicalNotesCollection.getHospitalId().equals(hospitalId)
			    && clinicalNotesCollection.getLocationId().equals(locationId)) {

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
			for (String diagramId : clinicalNotesCollection.getDiagrams()) {
				DBObject diagram = new BasicDBObject();
				DiagramsCollection diagramsCollection = diagramsRepository.findOne(diagramId);
				if(diagramsCollection != null){
					if (diagramsCollection.getDiagramUrl() != null) {
					    diagram.put("url", getFinalImageURL(diagramsCollection.getDiagramUrl()));
					}
					diagram.put("tags", diagramsCollection.getTags());
					diagramIds.add(diagram);
				}
			}
		
			parameters.put("diagramIds", diagramIds);
		    }
		    List<PatientClinicalNotesCollection> patientClinicalNotesCollection = patientClinicalNotesRepository.findByClinicalNotesId(clinicalNotesId);
		    if (patientClinicalNotesCollection != null && !patientClinicalNotesCollection.isEmpty()) {
			patientId = patientClinicalNotesCollection.get(0).getPatientId();
			patientAdmission = patientAdmissionRepository.findByPatientIdAndDoctorId(patientId, doctorId);
		    } else {
			logger.warn("No patient found");
			throw new BusinessException(ServiceError.NotFound, "No patient found");
		    }
		    user = userRepository.findOne(patientId);
		    patient = patientRepository.findByUserId(patientId);

		    emailTrackCollection.setDoctorId(doctorId);
		    emailTrackCollection.setHospitalId(hospitalId);
		    emailTrackCollection.setLocationId(locationId);
		    emailTrackCollection.setType(ComponentType.CLINICAL_NOTES.getType());
		    emailTrackCollection.setSubject("Prescription");
		    if (user != null) {
			emailTrackCollection.setPatientName(user.getFirstName());
			emailTrackCollection.setPatientId(user.getId());
		    }
		    parameters.put("clinicalNotesId", clinicalNotesId);

		} else {
		    logger.warn("Clinical Notes Id, doctorId, location Id, hospital Id does not match");
		    throw new BusinessException(ServiceError.NotFound, "Clinical Notes Id, doctorId, location Id, hospital Id does not match");
		}

		PrintSettingsCollection printSettings = printSettingsRepository.getSettings(doctorId, locationId, hospitalId,
			ComponentType.CLINICAL_NOTES.getType());
		DBObject printId = new BasicDBObject();
		if (printSettings == null) {
		    printSettings = printSettingsRepository.getSettings(doctorId, locationId, hospitalId, ComponentType.ALL.getType());
		    if (printSettings != null) {
			printId.put("$oid", printSettings.getId());

		    }
		} else
		    printId.put("$oid", printSettings.getId());

		parameters.put("printSettingsId", Arrays.asList(printId));
		String headerLeftText="",headerRightText="",footerBottomText ="";
		String patientName="", dob="", gender="", mobileNumber="";
		if(printSettings!=null){
			if(printSettings.getHeaderSetup() != null){
				for(PrintSettingsText str: printSettings.getHeaderSetup().getTopLeftText())headerLeftText=headerLeftText+"<br/>"+str.getText();
				for(PrintSettingsText str: printSettings.getHeaderSetup().getTopRightText())headerRightText=headerRightText+"<br/>"+str.getText();
				if(printSettings.getHeaderSetup().getPatientDetails() !=null && user!=null){
					patientName=printSettings.getHeaderSetup().getPatientDetails().getShowName()?"Patient Name: "+user.getFirstName()+"<br>":"";
					dob = printSettings.getHeaderSetup().getPatientDetails().getShowDOB()?"Patient Age: "+(user.getDob() != null ? (user.getDob().getAge())+"<br>":""):"";
					gender = printSettings.getHeaderSetup().getPatientDetails().getShowGender()?"Patient Gender: "+user.getGender()+"<br>":"";
					mobileNumber = printSettings.getHeaderSetup().getPatientDetails().getShowGender()?"Mobile Number: "+user.getMobileNumber()+"<br>":"";
				}
			}
		    if (printSettings.getFooterSetup() != null) {
			if (printSettings.getFooterSetup().getCustomFooter())
			    for (PrintSettingsText str : printSettings.getFooterSetup().getBottomText())
				footerBottomText = footerBottomText + "<br/>" + str.getText();
			if (printSettings.getFooterSetup().getShowSignature()) {
			    UserCollection doctorUser = userRepository.findOne(doctorId);
			    if (doctorUser != null)
				parameters.put("footerSignature", "Dr." + doctorUser.getFirstName());
			}
		    }
		}
		parameters.put("patientLeftText", patientName +  (patient!=null? "Patient Id: " +patient.getPID()+ "<br>":"")  + dob + gender);
		parameters.put("patientRightText", mobileNumber + (patientAdmission != null ? "Reffered By:" + patientAdmission.getReferredBy() + "<br>" : "")
			+ "Date:" + new SimpleDateFormat("dd-MM-yyyy").format(new Date()));
		parameters.put("headerLeftText", headerLeftText);
		parameters.put("headerRightText", headerRightText);
		parameters.put("footerBottomText", footerBottomText);

		LocationCollection location = locationRepository.findOne(locationId);
		if (location != null)
		    parameters.put("logoURL", getFinalImageURL(location.getLogoUrl()));

		String layout = printSettings != null ? (printSettings.getPageSetup() != null ? printSettings.getPageSetup().getLayout() : "PORTRAIT")
			: "PORTRAIT";
		String pageSize = printSettings != null ? (printSettings.getPageSetup() != null ? printSettings.getPageSetup().getPageSize() : "A4") : "A4";
		String margins = printSettings != null ? (printSettings.getPageSetup() != null ? printSettings.getPageSetup().getMargins() : null) : null;

		String path = jasperReportService.createPDF(parameters, "mongo-clinical-notes", layout, pageSize, margins);
		FileSystemResource file = new FileSystemResource(path);
		mailAttachment = new MailAttachment();
		mailAttachment.setAttachmentName(file.getFilename());
		mailAttachment.setFileSystemResource(file);

		emailTackService.saveEmailTrack(emailTrackCollection);

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
    	if (imageURL != null && uriInfo != null) {
    	    String finalImageURL = uriInfo.getBaseUri().toString().replace(uriInfo.getBaseUri().getPath(), imageUrlRootPath);
    	    return finalImageURL + imageURL;
    	} else
    	    return null;
}
}
