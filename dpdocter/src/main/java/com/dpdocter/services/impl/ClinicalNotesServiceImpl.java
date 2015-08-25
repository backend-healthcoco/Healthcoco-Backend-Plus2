package com.dpdocter.services.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.IteratorUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
import com.dpdocter.beans.Notes;
import com.dpdocter.beans.Observation;
import com.dpdocter.collections.ClinicalNotesCollection;
import com.dpdocter.collections.ComplaintCollection;
import com.dpdocter.collections.DiagnosisCollection;
import com.dpdocter.collections.DiagramsCollection;
import com.dpdocter.collections.InvestigationCollection;
import com.dpdocter.collections.NotesCollection;
import com.dpdocter.collections.ObservationCollection;
import com.dpdocter.collections.PatientClinicalNotesCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.ClinicalNotesRepository;
import com.dpdocter.repository.ComplaintRepository;
import com.dpdocter.repository.DiagnosisRepository;
import com.dpdocter.repository.DiagramsRepository;
import com.dpdocter.repository.InvestigationRepository;
import com.dpdocter.repository.NotesRepository;
import com.dpdocter.repository.ObservationRepository;
import com.dpdocter.repository.PatientClinicalNotesRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.request.ClinicalNotesAddRequest;
import com.dpdocter.request.ClinicalNotesEditRequest;
import com.dpdocter.services.ClinicalNotesService;
import com.dpdocter.services.FileManager;

import common.util.web.DPDoctorUtils;

@Service
public class ClinicalNotesServiceImpl implements ClinicalNotesService {

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
	long createdDate = createdTime.getTime();
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
	    clinicalNotesCollection.setCreatedDate(createdDate);
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
			complaint.setDoctorId(null);
			complaint.setHospitalId(null);
			complaint.setLocationId(null);
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
			observation.setDoctorId(null);
			observation.setHospitalId(null);
			observation.setLocationId(null);
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
			investigation.setDoctorId(null);
			investigation.setHospitalId(null);
			investigation.setLocationId(null);
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
			diagnosis.setDoctorId(null);
			diagnosis.setHospitalId(null);
			diagnosis.setLocationId(null);
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
			note.setDoctorId(null);
			note.setLocationId(null);
			note.setHospitalId(null);
			notes.add(note);
		    }
		    clinicalNote.setNotes(notes);
		}

		@SuppressWarnings("unchecked")
		List<DiagramsCollection> diagramsCollections = IteratorUtils.toList(diagramsRepository.findAll(clinicalNotesCollection.getDiagrams())
			.iterator());
		if (diagramsCollections != null) {
		    List<Diagram> diagrams = new ArrayList<Diagram>();
		    for (DiagramsCollection diagramsCollection : diagramsCollections) {
			Diagram diagram = new Diagram();
			BeanUtil.map(diagramsCollection, diagrams);
			diagram.setDoctorId(null);
			diagram.setHospitalId(null);
			diagram.setLocationId(null);
			diagrams.add(diagram);
		    }
		    clinicalNote.setDiagrams(diagrams);
		}
	    }
	} catch (Exception e) {
	    e.printStackTrace();
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
	long createdDate = createdTime.getTime();
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
	    clinicalNotesCollection.setCreatedDate(createdDate);
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
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}

	return clinicalNotes;

    }

    @Override
    public void deleteNote(String id) {
	try {
	    List<PatientClinicalNotesCollection> patientClinicalNotesCollections = patientClinicalNotesRepository.findByClinicalNotesId(id);
	    if (patientClinicalNotesCollections != null) {
		patientClinicalNotesRepository.delete(patientClinicalNotesCollections);
	    }
	    clinicalNotesRepository.delete(id);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}

    }

    @Override
    public List<ClinicalNotes> getPatientsClinicalNotesWithVerifiedOTP(String patientId, String createdTime, boolean isDeleted) {
	List<ClinicalNotes> clinicalNotesList = null;
	List<PatientClinicalNotesCollection> patientClinicalNotesCollections = null;
	try {
	    if (DPDoctorUtils.anyStringEmpty(createdTime)) {
		if (isDeleted) {
		    patientClinicalNotesCollections = patientClinicalNotesRepository.findByPatientId(patientId, new Sort(Sort.Direction.DESC, "createdDate"));
		} else {
		    patientClinicalNotesCollections = patientClinicalNotesRepository.findByPatientId(patientId, isDeleted, new Sort(Sort.Direction.DESC,
			    "createdDate"));
		}
	    } else {
		long createdTimeStamp = Long.parseLong(createdTime);
		if (isDeleted) {
		    patientClinicalNotesCollections = patientClinicalNotesRepository.findByPatientId(patientId, new Date(createdTimeStamp), new Sort(
			    Sort.Direction.DESC, "createdDate"));
		} else {
		    patientClinicalNotesCollections = patientClinicalNotesRepository.findByPatientId(patientId, isDeleted, new Date(createdTimeStamp),
			    new Sort(Sort.Direction.DESC, "createdDate"));
		}
	    }

	    if (patientClinicalNotesCollections != null) {
		@SuppressWarnings("unchecked")
		Collection<String> clinicalNotesIds = CollectionUtils.collect(patientClinicalNotesCollections, new BeanToPropertyValueTransformer(
			"clinicalNotesId"));
		clinicalNotesList = new ArrayList<ClinicalNotes>();
		for (String clinicalNotesId : clinicalNotesIds) {
		    ClinicalNotes clinicalNotes = getNotesById(clinicalNotesId);
		    if (clinicalNotes != null) {
		    		UserCollection userCollection = userRepository.findOne(clinicalNotes.getDoctorId());
		    		if(userCollection != null){
		    			clinicalNotes.setDoctorName(userCollection.getFirstName() + userCollection.getLastName());
		    		}
		    	
			clinicalNotesList.add(clinicalNotes);
		    }
		}
	    } else {
		throw new BusinessException(ServiceError.Unknown, "No Clinical Notes found for patient Id : " + patientId);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
	return clinicalNotesList;
    }

    @Override
    public List<ClinicalNotes> getPatientsClinicalNotesWithoutVerifiedOTP(String patientId, String doctorId, String locationId, String hospitalId,
	    String createdTime, boolean isDeleted) {
	List<ClinicalNotes> clinicalNotesList = null;
	List<PatientClinicalNotesCollection> patientClinicalNotesCollections = null;
	try {
	    if (createdTime != null) {
		long createdTimeStamp = Long.parseLong(createdTime);
		patientClinicalNotesCollections = patientClinicalNotesRepository.findByPatientId(patientId, new Date(createdTimeStamp), new Sort(
			Sort.Direction.DESC, "createdDate"));
	    } else {
		patientClinicalNotesCollections = patientClinicalNotesRepository.findByPatientId(patientId, new Sort(Sort.Direction.DESC, "createdDate"));
	    }
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
		    		if(userCollection != null){
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
		    		if(userCollection != null){
		    			clinicalNotes.setDoctorName(userCollection.getFirstName() + userCollection.getLastName());
		    		}
				clinicalNotesList.add(clinicalNotes);
			    }
			}
		    }
		}
	    } else {
		throw new BusinessException(ServiceError.Unknown, "No Clinical Notes found for patient Id : " + patientId);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
	return clinicalNotesList;
    }

    @Override
    public Complaint addEditComplaint(Complaint complaint) {
	try {
	    ComplaintCollection complaintCollection = new ComplaintCollection();
	    BeanUtil.map(complaint, complaintCollection);
	    complaintCollection.setCreatedTime(new Date());
	    complaintCollection = complaintRepository.save(complaintCollection);
	    BeanUtil.map(complaintCollection, complaint);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
	return complaint;
    }

    @Override
    public Observation addEditObservation(Observation observation) {
	try {
	    ObservationCollection observationCollection = new ObservationCollection();
	    BeanUtil.map(observation, observationCollection);
	    observationCollection = observationRepository.save(observationCollection);
	    BeanUtil.map(observationCollection, observation);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
	return observation;
    }

    @Override
    public Investigation addEditInvestigation(Investigation investigation) {
	try {
	    InvestigationCollection investigationCollection = new InvestigationCollection();
	    BeanUtil.map(investigation, investigationCollection);
	    investigationCollection = investigationRepository.save(investigationCollection);
	    BeanUtil.map(investigationCollection, investigation);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
	return investigation;
    }

    @Override
    public Diagnoses addEditDiagnosis(Diagnoses diagnosis) {
	try {
	    DiagnosisCollection diagnosisCollection = new DiagnosisCollection();
	    BeanUtil.map(diagnosis, diagnosisCollection);
	    diagnosisCollection = diagnosisRepository.save(diagnosisCollection);
	    BeanUtil.map(diagnosisCollection, diagnosis);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
	return diagnosis;
    }

    @Override
    public Notes addEditNotes(Notes notes) {
	try {
	    NotesCollection notesCollection = new NotesCollection();
	    BeanUtil.map(notes, notesCollection);
	    notesCollection = notesRepository.save(notesCollection);
	    BeanUtil.map(notesCollection, notes);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
	return notes;
    }

    @Override
    public Diagram addEditDiagram(Diagram diagram) {
	try {
	    String path = "clinicalNotes" + File.separator + "diagrams";
	    String diagramUrl = fileManager.saveImageAndReturnImageUrl(diagram.getDiagram(), path);
	    diagram.setDiagramUrl(diagramUrl);
	    DiagramsCollection diagramsCollection = new DiagramsCollection();
	    BeanUtil.map(diagram, diagramsCollection);
	    diagramsCollection = diagramsRepository.save(diagramsCollection);
	    BeanUtil.map(diagramsCollection, diagram);
	    // diagram.setDiagram(null);

	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
	return diagram;
    }

    @Override
    public void deleteComplaint(String id, String doctorId, String locationId, String hospitalId) {
	try {
	    ComplaintCollection complaintCollection = complaintRepository.findOne(id);
	    if (complaintCollection != null) {
		if (complaintCollection.getDoctorId() != null && complaintCollection.getHospitalId() != null && complaintCollection.getLocationId() != null) {
		    if (complaintCollection.getDoctorId().equals(doctorId) && complaintCollection.getHospitalId().equals(hospitalId)
			    && complaintCollection.getLocationId().equals(locationId)) {
			complaintCollection.setDeleted(true);
			complaintRepository.save(complaintCollection);
		    } else {
			throw new BusinessException(ServiceError.Unknown, "Invalid Doctor Id, Hospital Id, Or Location Id");
		    }
		} else {
		    throw new BusinessException(ServiceError.Unknown, "Cant delete Global Complaint.");
		}

	    } else {
		throw new BusinessException(ServiceError.Unknown, "Complaint not found!");
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}

    }

    @Override
    public void deleteObservation(String id, String doctorId, String locationId, String hospitalId) {
	try {
	    ObservationCollection observationCollection = observationRepository.findOne(id);
	    if (observationCollection != null) {
		if (observationCollection.getDoctorId() != null && observationCollection.getHospitalId() != null
			&& observationCollection.getLocationId() != null) {
		    if (observationCollection.getDoctorId().equals(doctorId) && observationCollection.getHospitalId().equals(hospitalId)
			    && observationCollection.getLocationId().equals(locationId)) {
			observationCollection.setDeleted(true);
			observationRepository.save(observationCollection);
		    } else {
			throw new BusinessException(ServiceError.Unknown, "Invalid Doctor Id, Hospital Id, Or Location Id");
		    }
		} else {
		    throw new BusinessException(ServiceError.Unknown, "Cant delete Global Observation.");
		}
	    } else {
		throw new BusinessException(ServiceError.Unknown, "Observation not found!");
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
    }

    @Override
    public void deleteInvestigation(String id, String doctorId, String locationId, String hospitalId) {
	try {
	    InvestigationCollection investigationCollection = investigationRepository.findOne(id);
	    if (investigationCollection != null) {
		if (investigationCollection.getDoctorId() != null && investigationCollection.getHospitalId() != null
			&& investigationCollection.getLocationId() != null) {
		    if (investigationCollection.getDoctorId().equals(doctorId) && investigationCollection.getHospitalId().equals(hospitalId)
			    && investigationCollection.getLocationId().equals(locationId)) {
			investigationCollection.setDeleted(true);
			investigationRepository.save(investigationCollection);
		    } else {
			throw new BusinessException(ServiceError.Unknown, "Invalid Doctor Id, Hospital Id, Or Location Id");
		    }
		} else {
		    throw new BusinessException(ServiceError.Unknown, "Cant delete Global Investigation.");
		}
	    } else {
		throw new BusinessException(ServiceError.Unknown, "Investigation not found!");
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
    }

    @Override
    public void deleteDiagnosis(String id, String doctorId, String locationId, String hospitalId) {
	try {
	    DiagnosisCollection diagnosisCollection = diagnosisRepository.findOne(id);
	    if (diagnosisCollection != null) {
		if (diagnosisCollection.getDoctorId() != null && diagnosisCollection.getHospitalId() != null && diagnosisCollection.getLocationId() != null) {
		    if (diagnosisCollection.getDoctorId().equals(doctorId) && diagnosisCollection.getHospitalId().equals(hospitalId)
			    && diagnosisCollection.getLocationId().equals(locationId)) {
			diagnosisCollection.setDeleted(true);
			diagnosisRepository.save(diagnosisCollection);
		    } else {
			throw new BusinessException(ServiceError.Unknown, "Invalid Doctor Id, Hospital Id, Or Location Id");
		    }
		} else {
		    throw new BusinessException(ServiceError.Unknown, "Cant delete Global Diagnosis.");
		}
	    } else {
		throw new BusinessException(ServiceError.Unknown, "Diagnosis not found!");
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
    }

    @Override
    public void deleteNotes(String id, String doctorId, String locationId, String hospitalId) {
	try {
	    NotesCollection notesCollection = notesRepository.findOne(id);
	    if (notesCollection != null) {
		if (notesCollection.getDoctorId() != null && notesCollection.getHospitalId() != null && notesCollection.getLocationId() != null) {
		    if (notesCollection.getDoctorId().equals(doctorId) && notesCollection.getHospitalId().equals(hospitalId)
			    && notesCollection.getLocationId().equals(locationId)) {
			notesCollection.setDeleted(true);
			notesRepository.save(notesCollection);
		    } else {
			throw new BusinessException(ServiceError.Unknown, "Invalid Doctor Id, Hospital Id, Or Location Id");
		    }
		} else {
		    throw new BusinessException(ServiceError.Unknown, "Cant delete Global Notes.");
		}

	    } else {
		throw new BusinessException(ServiceError.Unknown, "Notes not found!");
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
    }

    @Override
    public void deleteDiagram(String id, String doctorId, String locationId, String hospitalId) {
	try {
	    DiagramsCollection diagramsCollection = diagramsRepository.findOne(id);
	    if (diagramsCollection != null) {
		if (diagramsCollection.getDoctorId() != null && diagramsCollection.getHospitalId() != null && diagramsCollection.getLocationId() != null) {
		    if (diagramsCollection.getDoctorId().equals(doctorId) && diagramsCollection.getHospitalId().equals(hospitalId)
			    && diagramsCollection.getLocationId().equals(locationId)) {
			diagramsCollection.setDeleted(true);
			diagramsRepository.save(diagramsCollection);
		    } else {
			throw new BusinessException(ServiceError.Unknown, "Invalid Doctor Id, Hospital Id, Or Location Id");
		    }
		} else {
		    throw new BusinessException(ServiceError.Unknown, "Cant delete Global Diagram.");
		}

	    } else {
		throw new BusinessException(ServiceError.Unknown, "Diagram not found!");
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
    }

    @Override
    public List<Complaint> getCustomComplaints(String doctorId, String locationId, String hospitalId, int page, int size) {
	List<ComplaintCollection> complaintCollections = null;
	List<Complaint> complaints = null;
	try {
	    complaintCollections = complaintRepository.findCustomComplaints(doctorId, locationId, hospitalId, false, new PageRequest(page, size));
	    if (complaintCollections != null) {
		complaints = new ArrayList<Complaint>();
		BeanUtil.map(complaintCollections, complaints);
	    } else {
		throw new BusinessException(ServiceError.NotFound, "No Complaints Found");
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Complaints");
	}
	return complaints;
    }

    @Override
    public List<Diagnoses> getCustomDiagnosis(String doctorId, String locationId, String hospitalId, int page, int size) {
	List<DiagnosisCollection> diagnosisCollections = null;
	List<Diagnoses> diagnosis = null;
	try {
	    diagnosisCollections = diagnosisRepository.findCustomDiagnosis(doctorId, locationId, hospitalId, false, new PageRequest(page, size));
	    if (diagnosisCollections != null) {
		diagnosis = new ArrayList<Diagnoses>();
		BeanUtil.map(diagnosisCollections, diagnosis);
	    } else {
		throw new BusinessException(ServiceError.NotFound, "No Diagnosis Found");
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Diagnosis");
	}
	return diagnosis;
    }

    @Override
    public List<Investigation> getCustomInvestigations(String doctorId, String locationId, String hospitalId, int page, int size) {
	List<InvestigationCollection> investigationCollections = null;
	List<Investigation> investigations = null;
	try {
	    investigationCollections = investigationRepository.findCustomInvestigations(doctorId, locationId, hospitalId, false, new PageRequest(page, size));
	    if (investigationCollections != null) {
		investigations = new ArrayList<Investigation>();
		BeanUtil.map(investigationCollections, investigations);
	    } else {
		throw new BusinessException(ServiceError.NotFound, "No Investigation Found");
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Investigations");
	}
	return investigations;
    }

    @Override
    public List<Observation> getCustomObservations(String doctorId, String locationId, String hospitalId, int page, int size) {
	List<ObservationCollection> observationCollections = null;
	List<Observation> observations = null;
	try {
	    observationCollections = observationRepository.findCustomObservations(doctorId, locationId, hospitalId, false, new PageRequest(page, size));
	    if (observationCollections != null) {
		observations = new ArrayList<Observation>();
		BeanUtil.map(observationCollections, observations);
	    } else {
		throw new BusinessException(ServiceError.NotFound, "No Observations Found");
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Observations");
	}
	return observations;
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
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Clinical Notes Count");
	}
	return clinicalNotesCount;
    }

    @Override
    public List<Complaint> getComplaints(String doctorId, String createdTime, boolean isDeleted) {
	List<Complaint> response = new ArrayList<Complaint>();
	List<ComplaintCollection> complaintCollections = null;
	try {
	    long createdTimeStamp = Long.parseLong(createdTime);
	    if (doctorId != null) {
		if (isDeleted)
		    complaintCollections = complaintRepository.findComplaints(doctorId, new Date(createdTimeStamp),
			    new Sort(Sort.Direction.DESC, "createdTime"));
		else
		    complaintCollections = complaintRepository.findComplaints(doctorId, new Date(createdTimeStamp), isDeleted, new Sort(Sort.Direction.DESC,
			    "createdTime"));

	    } else {
		if (isDeleted)
		    complaintCollections = complaintRepository.findComplaints(new Date(createdTimeStamp), new Sort(Sort.Direction.DESC, "createdTime"));
		else
		    complaintCollections = complaintRepository.findComplaints(new Date(createdTimeStamp), isDeleted, new Sort(Sort.Direction.DESC,
			    "createdTime"));

	    }
	    BeanUtil.map(complaintCollections, response);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Complaints");
	}
	return response;
    }

    @Override
    public List<Investigation> getInvestigations(String doctorId, String createdTime, boolean isDeleted) {
	List<Investigation> response = new ArrayList<Investigation>();
	List<InvestigationCollection> investigationCollections = null;
	try {
	    long createdTimeStamp = Long.parseLong(createdTime);
	    if (doctorId != null) {
		if (isDeleted)
		    investigationCollections = investigationRepository.findInvestigations(doctorId, new Date(createdTimeStamp), new Sort(Sort.Direction.DESC,
			    "createdTime"));
		else
		    investigationCollections = investigationRepository.findInvestigations(doctorId, new Date(createdTimeStamp), false, new Sort(
			    Sort.Direction.DESC, "createdTime"));
	    } else {
		if (isDeleted)
		    investigationCollections = investigationRepository.findInvestigations(new Date(createdTimeStamp), new Sort(Sort.Direction.DESC,
			    "createdTime"));
		else
		    investigationCollections = investigationRepository.findInvestigations(new Date(createdTimeStamp), false, new Sort(Sort.Direction.DESC,
			    "createdTime"));
	    }
	    BeanUtil.map(investigationCollections, response);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Investigations");
	}
	return response;
    }

    @Override
    public List<Observation> getObservations(String doctorId, String createdTime, boolean isDeleted) {
	List<Observation> response = new ArrayList<Observation>();
	List<ObservationCollection> observationCollections = null;
	try {
	    long createdTimeStamp = Long.parseLong(createdTime);
	    if (doctorId != null) {
		if (isDeleted)
		    observationCollections = observationRepository.findObservations(doctorId, new Date(createdTimeStamp), new Sort(Sort.Direction.DESC,
			    "createdTime"));
		else
		    observationCollections = observationRepository.findObservations(doctorId, new Date(createdTimeStamp), false, new Sort(Sort.Direction.DESC,
			    "createdTime"));
	    } else {
		if (isDeleted)
		    observationCollections = observationRepository.findObservations(new Date(createdTimeStamp), new Sort(Sort.Direction.DESC, "createdTime"));
		else
		    observationCollections = observationRepository.findObservations(new Date(createdTimeStamp), false, new Sort(Sort.Direction.DESC,
			    "createdTime"));
	    }
	    BeanUtil.map(observationCollections, response);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Observations");
	}
	return response;
    }

    @Override
    public List<Diagnoses> getDiagnosis(String doctorId, String createdTime, boolean isDeleted) {
	List<Diagnoses> response = new ArrayList<Diagnoses>();
	List<DiagnosisCollection> diagnosisCollections = null;
	try {
	    long createdTimeStamp = Long.parseLong(createdTime);
	    if (doctorId != null) {
		if (isDeleted)
		    diagnosisCollections = diagnosisRepository
			    .findDiagnosis(doctorId, new Date(createdTimeStamp), new Sort(Sort.Direction.DESC, "createdTime"));

		else
		    diagnosisCollections = diagnosisRepository.findDiagnosis(doctorId, new Date(createdTimeStamp), isDeleted, new Sort(Sort.Direction.DESC,
			    "createdTime"));
	    } else {
		if (isDeleted)
		    diagnosisCollections = diagnosisRepository.findDiagnosis(new Date(createdTimeStamp), new Sort(Sort.Direction.DESC, "createdTime"));

		else
		    diagnosisCollections = diagnosisRepository.findDiagnosis(new Date(createdTimeStamp), isDeleted,
			    new Sort(Sort.Direction.DESC, "createdTime"));

	    }
	    BeanUtil.map(diagnosisCollections, response);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Diagnosis");
	}
	return response;
    }

    @Override
    public List<Notes> getNotes(String doctorId, String createdTime, boolean isDeleted) {
	List<Notes> response = new ArrayList<Notes>();
	List<NotesCollection> notesCollections = null;
	try {
	    long createdTimeStamp = Long.parseLong(createdTime);
	    if (doctorId != null) {
		if (isDeleted)
		    notesCollections = notesRepository.findNotes(doctorId, new Date(createdTimeStamp), new Sort(Sort.Direction.DESC, "createdTime"));
		else
		    notesCollections = notesRepository.findNotes(doctorId, new Date(createdTimeStamp), false, new Sort(Sort.Direction.DESC, "createdTime"));
	    } else {
		if (isDeleted)
		    notesCollections = notesRepository.findNotes(new Date(createdTimeStamp), new Sort(Sort.Direction.DESC, "createdTime"));
		else
		    notesCollections = notesRepository.findNotes(new Date(createdTimeStamp), false, new Sort(Sort.Direction.DESC, "createdTime"));
	    }
	    BeanUtil.map(notesCollections, response);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Notes");
	}
	return response;
    }

    @Override
    public List<Diagram> getDiagrams(String doctorId, String createdTime, boolean isDeleted) {
	List<Diagram> response = new ArrayList<Diagram>();
	List<DiagramsCollection> diagramsCollections = null;
	try {
	    long createdTimeStamp = Long.parseLong(createdTime);

	    if (isDeleted)
		diagramsCollections = diagramsRepository.findDiagrams(doctorId, new Date(createdTimeStamp), new Sort(Sort.Direction.DESC, "createdTime"));
	    else
		diagramsCollections = diagramsRepository.findDiagrams(doctorId, new Date(createdTimeStamp), isDeleted, new Sort(Sort.Direction.DESC,
			"createdTime"));
	    BeanUtil.map(diagramsCollections, response);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Diagrams");
	}
	return response;
    }

    @Override
    public List<Diagram> getGlobalDiagrams(String createdTime) {
	List<Diagram> response = new ArrayList<Diagram>();
	List<DiagramsCollection> diagramsCollections = null;
	try {
	    long createdTimeStamp = Long.parseLong(createdTime);
	    diagramsCollections = diagramsRepository.findGlobalDiagrams(new Date(createdTimeStamp), new Sort(Sort.Direction.DESC, "createdTime"));
	    BeanUtil.map(diagramsCollections, response);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Global Diagrams");
	}
	return response;
    }

    @Override
    public List<Complaint> getCustomGlobalComplaints(String doctorId, String createdTime, boolean isDeleted) {
	List<Complaint> response = new ArrayList<Complaint>();
	List<ComplaintCollection> complaintCollections = null;
	try {
	    long createdTimeStamp = Long.parseLong(createdTime);
	    if (doctorId != null) {
		if (isDeleted)
		    complaintCollections = complaintRepository.findCustomGlobalComplaints(doctorId, new Date(createdTimeStamp), new Sort(Sort.Direction.DESC,
			    "createdTime"));
		else
		    complaintCollections = complaintRepository.findCustomGlobalComplaints(doctorId, new Date(createdTimeStamp), isDeleted, new Sort(
			    Sort.Direction.DESC, "createdTime"));

	    } else {
		if (isDeleted)
		    complaintCollections = complaintRepository.findComplaints(new Date(createdTimeStamp), new Sort(Sort.Direction.DESC, "createdTime"));
		else
		    complaintCollections = complaintRepository.findComplaints(new Date(createdTimeStamp), isDeleted, new Sort(Sort.Direction.DESC,
			    "createdTime"));

	    }
	    BeanUtil.map(complaintCollections, response);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Complaints");
	}
	return response;
    }

    @Override
    public List<Investigation> getCustomGlobalInvestigations(String doctorId, String createdTime, boolean isDeleted) {
	List<Investigation> response = new ArrayList<Investigation>();
	List<InvestigationCollection> investigationCollections = null;
	try {
	    long createdTimeStamp = Long.parseLong(createdTime);
	    if (doctorId != null) {
		if (isDeleted)
		    investigationCollections = investigationRepository.findCustomGlobalInvestigations(doctorId, new Date(createdTimeStamp), new Sort(
			    Sort.Direction.DESC, "createdTime"));
		else
		    investigationCollections = investigationRepository.findCustomGlobalInvestigations(doctorId, new Date(createdTimeStamp), false, new Sort(
			    Sort.Direction.DESC, "createdTime"));
	    } else {
		if (isDeleted)
		    investigationCollections = investigationRepository.findInvestigations(new Date(createdTimeStamp), new Sort(Sort.Direction.DESC,
			    "createdTime"));
		else
		    investigationCollections = investigationRepository.findInvestigations(new Date(createdTimeStamp), false, new Sort(Sort.Direction.DESC,
			    "createdTime"));
	    }
	    BeanUtil.map(investigationCollections, response);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Investigations");
	}
	return response;
    }

    @Override
    public List<Observation> getCustomGlobalObservations(String doctorId, String createdTime, boolean isDeleted) {
	List<Observation> response = new ArrayList<Observation>();
	List<ObservationCollection> observationCollections = null;
	try {
	    long createdTimeStamp = Long.parseLong(createdTime);
	    if (doctorId != null) {
		if (isDeleted)
		    observationCollections = observationRepository.findCustomGlobalObservations(doctorId, new Date(createdTimeStamp), new Sort(
			    Sort.Direction.DESC, "createdTime"));
		else
		    observationCollections = observationRepository.findCustomGlobalObservations(doctorId, new Date(createdTimeStamp), false, new Sort(
			    Sort.Direction.DESC, "createdTime"));
	    } else {
		if (isDeleted)
		    observationCollections = observationRepository.findObservations(new Date(createdTimeStamp), new Sort(Sort.Direction.DESC, "createdTime"));
		else
		    observationCollections = observationRepository.findObservations(new Date(createdTimeStamp), false, new Sort(Sort.Direction.DESC,
			    "createdTime"));
	    }
	    BeanUtil.map(observationCollections, response);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Observations");
	}
	return response;
    }

    @Override
    public List<Diagnoses> getCustomGlobalDiagnosis(String doctorId, String createdTime, boolean isDeleted) {
	List<Diagnoses> response = new ArrayList<Diagnoses>();
	List<DiagnosisCollection> diagnosisCollections = null;
	try {
	    long createdTimeStamp = Long.parseLong(createdTime);
	    if (doctorId != null) {
		if (isDeleted)
		    diagnosisCollections = diagnosisRepository.findCustomGlobalDiagnosis(doctorId, new Date(createdTimeStamp), new Sort(Sort.Direction.DESC,
			    "createdTime"));

		else
		    diagnosisCollections = diagnosisRepository.findCustomGlobalDiagnosis(doctorId, new Date(createdTimeStamp), isDeleted, new Sort(
			    Sort.Direction.DESC, "createdTime"));
	    } else {
		if (isDeleted)
		    diagnosisCollections = diagnosisRepository.findDiagnosis(new Date(createdTimeStamp), new Sort(Sort.Direction.DESC, "createdTime"));

		else
		    diagnosisCollections = diagnosisRepository.findDiagnosis(new Date(createdTimeStamp), isDeleted,
			    new Sort(Sort.Direction.DESC, "createdTime"));

	    }
	    BeanUtil.map(diagnosisCollections, response);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Diagnosis");
	}
	return response;
    }
}
