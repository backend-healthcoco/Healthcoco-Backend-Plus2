package com.dpdocter.services.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.IteratorUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import com.dpdocter.beans.ClinicalNotes;
import com.dpdocter.beans.Complaint;
import com.dpdocter.beans.Diagnosis;
import com.dpdocter.beans.Diagram;
import com.dpdocter.beans.FileDetails;
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

	@Value(value = "${IMAGE_RESOURCE}")
	private String imageResource;

	public ClinicalNotes addNotes(ClinicalNotesAddRequest request) {
		ClinicalNotes clinicalNotes = null;
		try {
			// save clinical notes.
			ClinicalNotesCollection clinicalNotesCollection = new ClinicalNotesCollection();
			BeanUtil.map(request, clinicalNotesCollection);
			/*	if(request.getDiagrams() != null){
					List<String> diagramUrls = new ArrayList<String>();
					List<String> diagramPaths = new ArrayList<String>();;
					for(FileDetails diagram : request.getDiagrams()){
						String path = request.getPatientId() + File.separator + "clinical-notes-diagrams";
						//save image
						String diagramUrl = fileManager.saveImageAndReturnImageUrl(diagram,path);
						String fileName = diagram.getFileName()
								+ "." + diagram.getFileExtension();
						String diagramPath = imageResource + File.separator + path + File.separator + fileName;
						diagramUrls.add(diagramUrl);
						diagramPaths.add(diagramPath);
					}
					
					clinicalNotesCollection.setDiagrams(diagramUrls);
					clinicalNotesCollection.setDiagramsPaths(diagramPaths);
				}*/

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
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}

		return clinicalNotes;
	}

	public ClinicalNotes getNotesById(String id) {
		ClinicalNotes clinicalNote = null;
		try {
			ClinicalNotesCollection clinicalNotesCollection = clinicalNotesRepository.findOne(id);
			if (clinicalNotesCollection != null) {
				clinicalNote = new ClinicalNotes();
				clinicalNote.setDoctorId(clinicalNotesCollection.getDoctorId());
				clinicalNote.setHospitalId(clinicalNotesCollection.getHospitalId());
				clinicalNote.setLocationId(clinicalNotesCollection.getLocationId());
				clinicalNote.setId(id);
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
						clinicalNotesCollection.getObservation()).iterator());
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
						clinicalNotesCollection.getInvestigation()).iterator());
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
					List<Diagnosis> diagnosisList = new ArrayList<Diagnosis>();
					for (DiagnosisCollection diagnosisCollection : diagnosisCollections) {
						Diagnosis diagnosis = new Diagnosis();
						BeanUtil.map(diagnosisCollection, diagnosis);
						diagnosis.setDoctorId(null);
						diagnosis.setHospitalId(null);
						diagnosis.setLocationId(null);
						diagnosisList.add(diagnosis);
					}
					clinicalNote.setDiagnoses(diagnosisList);
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

	public ClinicalNotes editNotes(ClinicalNotesEditRequest request) {
		ClinicalNotes clinicalNotes = null;
		try {
			ClinicalNotesCollection clinicalNotesCollection = new ClinicalNotesCollection();
			BeanUtil.map(request, clinicalNotesCollection);
			if (request.getDiagrams() != null) {
				List<String> diagramUrls = new ArrayList<String>();
				List<String> diagramPaths = new ArrayList<String>();
				for (FileDetails diagram : request.getDiagrams()) {
					String path = request.getPatientId() + File.separator + "clinical-notes-diagrams";
					// save image
					String diagramUrl = fileManager.saveImageAndReturnImageUrl(diagram, path);
					String fileName = diagram.getFileName() + "." + diagram.getFileExtension();
					String diagramPath = imageResource + File.separator + path + File.separator + fileName;
					diagramUrls.add(diagramUrl);
					diagramPaths.add(diagramPath);
				}

				clinicalNotesCollection.setDiagrams(diagramUrls);
				clinicalNotesCollection.setDiagramsPaths(diagramPaths);
			}

			clinicalNotesCollection = clinicalNotesRepository.save(clinicalNotesCollection);
			BeanUtil.map(clinicalNotesCollection, clinicalNotes);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return clinicalNotes;
	}

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

	public List<ClinicalNotes> getPatientsClinicalNotesWithVarifiedOTP(String patientId) {
		List<ClinicalNotes> clinicalNotesList = null;
		try {
			List<PatientClinicalNotesCollection> patientClinicalNotesCollections = patientClinicalNotesRepository.findByPatientId(patientId);
			if (patientClinicalNotesCollections != null) {
				@SuppressWarnings("unchecked")
				Collection<String> clinicalNotesIds = CollectionUtils.collect(patientClinicalNotesCollections, new BeanToPropertyValueTransformer(
						"clinicalNotesId"));
				clinicalNotesList = new ArrayList<ClinicalNotes>();
				for (String clinicalNotesId : clinicalNotesIds) {
					ClinicalNotes clinicalNotes = getNotesById(clinicalNotesId);
					if (clinicalNotes != null) {
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

	public List<ClinicalNotes> getPatientsClinicalNotesWithoutVarifiedOTP(String patientId, String doctorId, String locationId, String hospitalId) {
		List<ClinicalNotes> clinicalNotesList = null;
		try {
			List<PatientClinicalNotesCollection> patientClinicalNotesCollections = patientClinicalNotesRepository.findByPatientId(patientId);
			if (patientClinicalNotesCollections != null) {
				@SuppressWarnings("unchecked")
				Collection<String> clinicalNotesIds = CollectionUtils.collect(patientClinicalNotesCollections, new BeanToPropertyValueTransformer(
						"clinicalNotesId"));
				clinicalNotesList = new ArrayList<ClinicalNotes>();
				for (String clinicalNotesId : clinicalNotesIds) {
					ClinicalNotes clinicalNotes = getNotesById(clinicalNotesId);
					if (clinicalNotes != null) {
						if (clinicalNotes.getDoctorId().equals(doctorId) && clinicalNotes.getLocationId().equals(locationId)
								&& clinicalNotes.getHospitalId().equals(hospitalId)) {
							clinicalNotesList.add(clinicalNotes);
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

	public Diagram addEditDiagram(Diagram diagram) {
		try {
			String path = "clinicalNotes" + File.separator + "diagrams";
			String diagramUrl = fileManager.saveImageAndReturnImageUrl(diagram.getDiagram(), path);
			diagram.setDiagramUrl(diagramUrl);
			DiagramsCollection diagramsCollection = new DiagramsCollection();
			BeanUtil.map(diagram, diagramsCollection);
			diagramsCollection = diagramsRepository.save(diagramsCollection);
			BeanUtil.map(diagramsCollection, diagram);
			diagram.setDiagram(null);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return diagram;
	}

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
	public List<Diagnosis> getCustomDiagnosis(String doctorId, String locationId, String hospitalId, int page, int size) {
		List<DiagnosisCollection> diagnosisCollections = null;
		List<Diagnosis> diagnosis = null;
		try {
			diagnosisCollections = diagnosisRepository.findCustomDiagnosis(doctorId, locationId, hospitalId, false, new PageRequest(page, size));
			if (diagnosisCollections != null) {
				diagnosis = new ArrayList<Diagnosis>();
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

}
