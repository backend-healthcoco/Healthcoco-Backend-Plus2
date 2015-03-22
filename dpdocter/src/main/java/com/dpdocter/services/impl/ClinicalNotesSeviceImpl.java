package com.dpdocter.services.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.dpdocter.beans.ClinicalNotes;
import com.dpdocter.beans.Complaint;
import com.dpdocter.beans.Diagnosis;
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
import com.dpdocter.request.ClinicalNotesAddRequest;
import com.dpdocter.request.ClinicalNotesEditRequest;
import com.dpdocter.services.ClinicalNotesService;
import com.dpdocter.services.FileManager;

@Service
public class ClinicalNotesSeviceImpl implements ClinicalNotesService {

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

	@Override
	public ClinicalNotes addNotes(ClinicalNotesAddRequest request) {
		ClinicalNotes clinicalNotes = null;
		try {
			// save clinical notes.
			ClinicalNotesCollection clinicalNotesCollection = new ClinicalNotesCollection();
			BeanUtil.map(request, clinicalNotesCollection);
			clinicalNotesCollection = clinicalNotesRepository
					.save(clinicalNotesCollection);
			if (clinicalNotesCollection != null) {
				// map the clinical notes with patient
				PatientClinicalNotesCollection patientClinicalNotesCollection = new PatientClinicalNotesCollection();
				patientClinicalNotesCollection
						.setClinicalNotesId(clinicalNotesCollection.getId());
				patientClinicalNotesCollection.setPatientId(request
						.getPatientId());
				patientClinicalNotesRepository
						.save(patientClinicalNotesCollection);
				clinicalNotes = new ClinicalNotes();
				BeanUtil.map(clinicalNotesCollection, clinicalNotes);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}

		return clinicalNotes;
	}

	@Override
	public ClinicalNotes getNotesById(String id) {
		ClinicalNotes clinicalNotes = null;
		try {
			ClinicalNotesCollection clinicalNotesCollection = clinicalNotesRepository
					.findOne(id);
			if (clinicalNotesCollection != null) {
				clinicalNotes = new ClinicalNotes();
				BeanUtil.map(clinicalNotesCollection, clinicalNotes);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return clinicalNotes;
	}

	@Override
	public ClinicalNotes editNotes(ClinicalNotesEditRequest request) {
		ClinicalNotes clinicalNotes = null;
		try {
			ClinicalNotesCollection clinicalNotesCollection = new ClinicalNotesCollection();
			BeanUtil.map(request, clinicalNotesCollection);
			clinicalNotesCollection = clinicalNotesRepository.save(clinicalNotesCollection);
			clinicalNotes = new ClinicalNotes();
			BeanUtil.map(clinicalNotesCollection, clinicalNotes);
			return clinicalNotes;
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}

	}

	@Override
	public void deleteNote(String id) {
		try {
			List<PatientClinicalNotesCollection> patientClinicalNotesCollections = 
					patientClinicalNotesRepository.findByClinicalNotesId(id);
			if(patientClinicalNotesCollections != null){
				patientClinicalNotesRepository.delete(patientClinicalNotesCollections);
			}
			clinicalNotesRepository.delete(id);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}

	}

	@Override
	public List<ClinicalNotes> getPatientsClinicalNotesWithVarifiedOTP(
			String patientId) {
		List<ClinicalNotes> clinicalNotes = null;
		try {
			List<PatientClinicalNotesCollection> patientClinicalNotesCollections = patientClinicalNotesRepository
					.findByPatientId(patientId);
			@SuppressWarnings("unchecked")
			Collection<String> clinicalNotesId =  CollectionUtils.collect(patientClinicalNotesCollections, new BeanToPropertyValueTransformer("clinicalNotesId"));
			
			Query queryForGettingAllClinicalNotesFromPatient = new Query();
			queryForGettingAllClinicalNotesFromPatient.addCriteria(Criteria.where("id").in(clinicalNotesId));
			List<ClinicalNotesCollection> clinicalNotesCollections = mongoTemplate.find(queryForGettingAllClinicalNotesFromPatient, ClinicalNotesCollection.class);
			clinicalNotes = new ArrayList<ClinicalNotes>();
			BeanUtil.map(clinicalNotesCollections, clinicalNotes);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return clinicalNotes;
	}

	@Override
	public List<ClinicalNotes> getPatientsClinicalNotesWithoutVarifiedOTP(
			String patientId,String doctorId) {
		List<ClinicalNotes> clinicalNotes = null;
		try {
			List<PatientClinicalNotesCollection> patientClinicalNotesCollections = patientClinicalNotesRepository
					.findByPatientId(patientId);
			@SuppressWarnings("unchecked")
			Collection<String> clinicalNotesId =  CollectionUtils.collect(patientClinicalNotesCollections, new BeanToPropertyValueTransformer("clinicalNotesId"));
			
			Query queryForGettingAllClinicalNotesFromPatient = new Query();
			queryForGettingAllClinicalNotesFromPatient.addCriteria(Criteria.where("id").in(clinicalNotesId));
			queryForGettingAllClinicalNotesFromPatient.addCriteria(Criteria.where("doctorId").is(doctorId));
			List<ClinicalNotesCollection> clinicalNotesCollections = mongoTemplate.find(queryForGettingAllClinicalNotesFromPatient, ClinicalNotesCollection.class);
			clinicalNotes = new ArrayList<ClinicalNotes>();
			BeanUtil.map(clinicalNotesCollections, clinicalNotes);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return clinicalNotes;
	}

	@Override
	public Complaint addEditComplaint(Complaint complaint) {
		try {
			ComplaintCollection complaintCollection = new ComplaintCollection();
			BeanUtil.map(complaint, complaintCollection);
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
	public Diagnosis addEditDiagnosis(Diagnosis diagnosis) {
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
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return diagram;
	}

	@Override
	public void deleteComplaint(String id,String doctorId) {
		try {
			ComplaintCollection complaintCollection = complaintRepository.findOne(id);
			if(complaintCollection != null){
				if(complaintCollection.getDoctorId() != null){
					if(complaintCollection.getDoctorId().equals(doctorId)){
						complaintCollection.setDeleted(true);
						complaintRepository.save(complaintCollection);
					}else{
						throw new BusinessException(ServiceError.Unknown, "Invalid DoctorId.");
					}
				}else{
					throw new BusinessException(ServiceError.Unknown, "Cant delete Global Complain.");
				}
				
			}else{
				throw new BusinessException(ServiceError.Unknown, "Complaint not found!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		
	}

	@Override
	public void deleteObservation(String id,String doctorId) {
		try {
			ObservationCollection observationCollection = observationRepository.findOne(id);
			if(observationCollection != null){
				if(observationCollection.getDoctorId() != null){
					if(observationCollection.getDoctorId().equals(doctorId)){
						observationCollection.setDeleted(true);
						observationRepository.save(observationCollection);
					}else{
						throw new BusinessException(ServiceError.Unknown, "Invalid DoctorId.");
					}
				}else{
					throw new BusinessException(ServiceError.Unknown, "Cant delete Global Observation.");
				}
			}else{
				throw new BusinessException(ServiceError.Unknown, "Observation not found!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
	}

	@Override
	public void deleteInvestigation(String id,String doctorId) {
		try {
			InvestigationCollection investigationCollection = investigationRepository.findOne(id);
			if(investigationCollection != null){
				if(investigationCollection.getDoctorId()!=null){
					if(investigationCollection.getDoctorId().equals(doctorId)){
						investigationCollection.setDeleted(true);
						investigationRepository.save(investigationCollection);
					}else{
						throw new BusinessException(ServiceError.Unknown, "Invalid DoctorId.");
					}
				}else{
					throw new BusinessException(ServiceError.Unknown, "Cant delete Global Investigation.");
				}
				
			}else{
				throw new BusinessException(ServiceError.Unknown, "Investigation not found!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
	}

	@Override
	public void deleteDiagnosis(String id,String doctorId) {
		try {
			DiagnosisCollection diagnosisCollection = diagnosisRepository.findOne(id);
			if(diagnosisCollection != null){
				if(diagnosisCollection.getDoctorId()!=null){
					if(diagnosisCollection.getDoctorId().equals(doctorId)){
						diagnosisCollection.setDeleted(true);
						diagnosisRepository.save(diagnosisCollection);
					}else{
						throw new BusinessException(ServiceError.Unknown, "Invalid DoctorId.");
					}
				}else{
					throw new BusinessException(ServiceError.Unknown, "Cant delete Global Diagnosis.");
				}
			}else{
				throw new BusinessException(ServiceError.Unknown, "Diagnosis not found!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
	}

	@Override
	public void deleteNotes(String id,String doctorId) {
		try {
			NotesCollection notesCollection = notesRepository.findOne(id);
			if(notesCollection != null){
				if(notesCollection.getDoctorId()!=null){
					if(notesCollection.getDoctorId().equals(doctorId)){
						notesCollection.setDeleted(true);
						notesRepository.save(notesCollection);
					}else{
						throw new BusinessException(ServiceError.Unknown, "Invalid DoctorId.");
					}
				}else{
					throw new BusinessException(ServiceError.Unknown, "Cant delete Global Notes.");
				}
				
			}else{
				throw new BusinessException(ServiceError.Unknown, "Notes not found!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
	}

	@Override
	public void deleteDiagram(String id,String doctorId) {
		try {
			DiagramsCollection diagramsCollection = diagramsRepository.findOne(id);
			if(diagramsCollection != null){
				if(diagramsCollection.getDoctorId()!=null){
					if(diagramsCollection.getDoctorId().equals(doctorId)){
						diagramsCollection.setDeleted(true);
						diagramsRepository.save(diagramsCollection);
					}else{
						throw new BusinessException(ServiceError.Unknown, "Invalid DoctorId.");
					}
				}else{
					throw new BusinessException(ServiceError.Unknown, "Cant delete Global Diagram.");
				}
				
			}else{
				throw new BusinessException(ServiceError.Unknown, "Diagram not found!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
	}
	
	

}
