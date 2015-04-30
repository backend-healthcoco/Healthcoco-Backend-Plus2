package com.dpdocter.services.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dpdocter.collections.DiseasesCollection;
import com.dpdocter.collections.HistoryCollection;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.DiseasesRepository;
import com.dpdocter.repository.HistoryRepository;
import com.dpdocter.request.DiseaseAddEditRequest;
import com.dpdocter.response.DiseaseAddEditResponse;
import com.dpdocter.response.DiseaseListResponse;
import com.dpdocter.services.HistoryServices;

@Service
public class HistoryServicesImpl implements HistoryServices {
	@Autowired
	private DiseasesRepository diseasesRepository;

	@Autowired
	private HistoryRepository historyRepository;

	public List<DiseaseAddEditResponse> addDiseases(List<DiseaseAddEditRequest> request) {
		List<DiseaseAddEditResponse> response = null;
		List<DiseasesCollection> diseases = new ArrayList<DiseasesCollection>();
		BeanUtil.map(request, diseases);
		try {
			diseases = diseasesRepository.save(diseases);
			response = new ArrayList<DiseaseAddEditResponse>();
			BeanUtil.map(diseases, response);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Saving Disease(s)");
		}
		return response;
	}

	public DiseaseAddEditResponse editDiseases(DiseaseAddEditRequest request) {
		DiseaseAddEditResponse response = null;
		DiseasesCollection disease = new DiseasesCollection();
		BeanUtil.map(request, disease);
		try {
			disease = diseasesRepository.save(disease);
			response = new DiseaseAddEditResponse();
			BeanUtil.map(disease, response);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Editing Disease");
		}
		return response;
	}

	public Boolean deleteDisease(String diseaseId, String doctorId, String hospitalId, String locationId) {
		Boolean response = false;
		DiseasesCollection disease = null;
		try {
			disease = diseasesRepository.findOne(diseaseId);
			if (disease != null) {
				if (disease.getDoctorId() != null && disease.getHospitalId() != null && disease.getLocationId() != null) {
					if (disease.getDoctorId().equals(doctorId) && disease.getHospitalId().equals(hospitalId) && disease.getLocationId().equals(locationId)) {
						disease.setDeleted(true);
						disease = diseasesRepository.save(disease);
						response = true;
					} else {
						throw new BusinessException(ServiceError.NotAuthorized, "Invalid Doctor Id, Hospital Id, Or Location Id");
					}
				} else {
					throw new BusinessException(ServiceError.NotAuthorized, "Cannot Delete Global Disease");
				}
			} else {
				throw new BusinessException(ServiceError.NotFound, "Disease Not Found");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Deleting Disease");
		}
		return response;
	}

	public boolean addReportToHistory(String reportId, String patientId, String doctorId, String hospitalId, String locationId) {
		HistoryCollection historyCollection = null;
		try {
			// check if history for this patient is already added .
			historyCollection = historyRepository.findByDoctorIdLocationIdHospitalIdAndPatientId(doctorId, locationId, hospitalId, patientId);
			if (historyCollection != null) {
				// check if reports are there in history.
				List<String> reports = historyCollection.getReports();
				if (reports != null) {
					// check if this report id is already added into history.
					if (!reports.contains(reportId)) {
						reports.add(reportId);
					} else {
						throw new BusinessException(ServiceError.Unknown, "This report is already added into history.");
					}
					// if no report is added into history then add it .
				} else {
					reports = new ArrayList<String>();
					reports.add(reportId);
					historyCollection.setReports(reports);
				}
			} else {// if history not added for this patient.Create new history.
				historyCollection = new HistoryCollection(doctorId, locationId, hospitalId, patientId);
				List<String> reports = new ArrayList<String>();
				reports.add(reportId);
				historyCollection.setReports(reports);
			}
			// finally add history into db.
			historyRepository.save(historyCollection);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());

		}
		return true;
	}

	public boolean addClinicalNotesToHistory(String clinicalNotesId, String patientId, String doctorId, String hospitalId, String locationId) {
		HistoryCollection historyCollection = null;
		try {
			// check if history for this patient is already added .
			historyCollection = historyRepository.findByDoctorIdLocationIdHospitalIdAndPatientId(doctorId, locationId, hospitalId, patientId);
			if (historyCollection != null) {
				// check if clinical notes are there in history.
				List<String> clinicalNotes = historyCollection.getClinicalNotes();
				if (clinicalNotes != null) {
					// check if this clinicalNotes id is already added into
					// history.
					if (!clinicalNotes.contains(clinicalNotesId)) {
						clinicalNotes.add(clinicalNotesId);
					} else {
						throw new BusinessException(ServiceError.Unknown, "This clinicalNote is already added into history.");
					}
					// if no clinicalNote is added into history then add it .
				} else {
					clinicalNotes = new ArrayList<String>();
					clinicalNotes.add(clinicalNotesId);
					historyCollection.setClinicalNotes(clinicalNotes);
				}
			} else {// if history not added for this patient.Create new history.
				historyCollection = new HistoryCollection(doctorId, locationId, hospitalId, patientId);
				List<String> clinicalNotes = new ArrayList<String>();
				clinicalNotes.add(clinicalNotesId);
				historyCollection.setClinicalNotes(clinicalNotes);
			}
			// finally add history into db.
			historyRepository.save(historyCollection);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());

		}
		return true;
	}

	public boolean addPrescriptionToHistory(String prescriptionId, String patientId, String doctorId, String hospitalId, String locationId) {
		HistoryCollection historyCollection = null;
		try {
			// check if history for this patient is already added .
			historyCollection = historyRepository.findByDoctorIdLocationIdHospitalIdAndPatientId(doctorId, locationId, hospitalId, patientId);
			if (historyCollection != null) {
				// check if prescription are there in history.
				List<String> prescriptions = historyCollection.getPrescriptions();
				if (prescriptions != null) {
					// check if this prescription id is already added into
					// history.
					if (!prescriptions.contains(prescriptionId)) {
						prescriptions.add(prescriptionId);
					} else {
						throw new BusinessException(ServiceError.Unknown, "This prescription is already added into history.");
					}
					// if no prescription is added into history then add it .
				} else {
					prescriptions = new ArrayList<String>();
					prescriptions.add(prescriptionId);
					historyCollection.setClinicalNotes(prescriptions);
				}
			} else {// if history not added for this patient.Create new history.
				historyCollection = new HistoryCollection(doctorId, locationId, hospitalId, patientId);
				List<String> prescriptions = new ArrayList<String>();
				prescriptions.add(prescriptionId);
				historyCollection.setClinicalNotes(prescriptions);
			}
			// finally add history into db.
			historyRepository.save(historyCollection);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());

		}
		return true;
	}

	public boolean assignMedicalHistory(String diseaseId, String patientId, String doctorId, String hospitalId, String locationId) {
		HistoryCollection historyCollection = null;
		try {
			// check if history for this patient is already added .
			historyCollection = historyRepository.findByDoctorIdLocationIdHospitalIdAndPatientId(doctorId, locationId, hospitalId, patientId);
			if (historyCollection != null) {
				// check if medicalHistory are there in history.
				List<String> medicalHistoryList = historyCollection.getMedicalhistory();
				if (medicalHistoryList != null) {
					// check if this diseaseId id is already added into history.
					if (!medicalHistoryList.contains(diseaseId)) {
						medicalHistoryList.add(diseaseId);
					} else {
						throw new BusinessException(ServiceError.Unknown, "This diseaseId is already added into history.");
					}
					// if no medicalHistory is added into history then add it .
				} else {
					medicalHistoryList = new ArrayList<String>();
					medicalHistoryList.add(diseaseId);
					historyCollection.setMedicalhistory(medicalHistoryList);
				}
			} else {// if history not added for this patient.Create new history.
				historyCollection = new HistoryCollection(doctorId, locationId, hospitalId, patientId);
				List<String> medicalHistoryList = new ArrayList<String>();
				medicalHistoryList.add(diseaseId);
				historyCollection.setMedicalhistory(medicalHistoryList);
			}
			// finally add history into db.
			historyRepository.save(historyCollection);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());

		}
		return true;
	}

	public boolean assignFamilyHistory(String diseaseId, String patientId, String doctorId, String hospitalId, String locationId) {
		HistoryCollection historyCollection = null;
		try {
			// check if history for this patient is already added .
			historyCollection = historyRepository.findByDoctorIdLocationIdHospitalIdAndPatientId(doctorId, locationId, hospitalId, patientId);
			if (historyCollection != null) {
				// check if familyHistory are there in history.
				List<String> familyHistoryList = historyCollection.getFamilyhistory();
				if (familyHistoryList != null) {
					// check if this diseaseId id is already added into history.
					if (!familyHistoryList.contains(diseaseId)) {
						familyHistoryList.add(diseaseId);
					} else {
						throw new BusinessException(ServiceError.Unknown, "This diseaseId is already added into history.");
					}
					// if no familyHistory is added into history then add it .
				} else {
					familyHistoryList = new ArrayList<String>();
					familyHistoryList.add(diseaseId);
					historyCollection.setFamilyhistory(familyHistoryList);
				}
			} else {// if history not added for this patient.Create new history.
				historyCollection = new HistoryCollection(doctorId, locationId, hospitalId, patientId);
				List<String> familyHistoryList = new ArrayList<String>();
				familyHistoryList.add(diseaseId);
				historyCollection.setFamilyhistory(familyHistoryList);
			}
			// finally add history into db.
			historyRepository.save(historyCollection);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());

		}
		return true;
	}

	public boolean addSpecialNotes(List<String> specialNotes, String patientId, String doctorId, String hospitalId, String locationId) {
		HistoryCollection historyCollection = null;
		try {
			// check if history for this patient is already added .
			historyCollection = historyRepository.findByDoctorIdLocationIdHospitalIdAndPatientId(doctorId, locationId, hospitalId, patientId);
			if (historyCollection != null) {
				// check if spetialNotes are there in history.
				List<String> spetialNotesInHistory = historyCollection.getSpetialNotes();
				if (spetialNotesInHistory != null) {
					spetialNotesInHistory.addAll(specialNotes);
					// if no spetialNotes is added into history then add it .
				} else {
					spetialNotesInHistory = new ArrayList<String>();
					spetialNotesInHistory.addAll(specialNotes);
					historyCollection.setSpetialNotes(spetialNotesInHistory);
				}
			} else {// if history not added for this patient.Create new history.
				historyCollection = new HistoryCollection(doctorId, locationId, hospitalId, patientId);
				historyCollection.setSpetialNotes(specialNotes);
			}
			// finally add history into db.
			historyRepository.save(historyCollection);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());

		}
		return true;
	}

	public boolean removeReports(String reportId, String patientId, String doctorId, String hospitalId, String locationId) {
		HistoryCollection historyCollection = null;
		try {
			historyCollection = historyRepository.findByDoctorIdLocationIdHospitalIdAndPatientId(doctorId, locationId, hospitalId, patientId);
			if (historyCollection != null) {
				List<String> reports = historyCollection.getReports();
				if (reports != null) {
					if (reports.contains(reportId)) {
						reports.remove(reportId);
						if (checkIfHistoryRemovedCompletly(historyCollection)) {
							historyRepository.delete(historyCollection.getId());
						} else {
							historyRepository.save(historyCollection);
						}
					} else {
						throw new BusinessException(ServiceError.Unknown, "This reports is not found for this patient to remove. ");
					}
				} else {
					throw new BusinessException(ServiceError.Unknown, "No reports found for this patient to remove. ");
				}
			} else {
				throw new BusinessException(ServiceError.Unknown, "No History found for this patient. ");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return true;
	}

	public boolean removeClinicalNotes(String clinicalNotesId, String patientId, String doctorId, String hospitalId, String locationId) {
		HistoryCollection historyCollection = null;
		try {
			historyCollection = historyRepository.findByDoctorIdLocationIdHospitalIdAndPatientId(doctorId, locationId, hospitalId, patientId);
			if (historyCollection != null) {
				List<String> clinicalNotes = historyCollection.getClinicalNotes();
				if (clinicalNotes != null) {
					if (clinicalNotes.contains(clinicalNotesId)) {
						clinicalNotes.remove(clinicalNotesId);
						if (checkIfHistoryRemovedCompletly(historyCollection)) {
							historyRepository.delete(historyCollection.getId());
						} else {
							historyRepository.save(historyCollection);
						}
					} else {
						throw new BusinessException(ServiceError.Unknown, "This clinicalNote is not found for this patient to remove. ");
					}
				} else {
					throw new BusinessException(ServiceError.Unknown, "No clinicalNote found for this patient to remove. ");
				}
			} else {
				throw new BusinessException(ServiceError.Unknown, "No History found for this patient. ");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return true;
	}

	public boolean removePrescription(String prescriptionId, String patientId, String doctorId, String hospitalId, String locationId) {
		HistoryCollection historyCollection = null;
		try {
			historyCollection = historyRepository.findByDoctorIdLocationIdHospitalIdAndPatientId(doctorId, locationId, hospitalId, patientId);
			if (historyCollection != null) {
				List<String> prescriptions = historyCollection.getPrescriptions();
				if (prescriptions != null) {
					if (prescriptions.contains(prescriptionId)) {
						prescriptions.remove(prescriptionId);
						if (checkIfHistoryRemovedCompletly(historyCollection)) {
							historyRepository.delete(historyCollection.getId());
						} else {
							historyRepository.save(historyCollection);
						}
					} else {
						throw new BusinessException(ServiceError.Unknown, "This prescription is not found for this patient to remove. ");
					}
				} else {
					throw new BusinessException(ServiceError.Unknown, "No prescription found for this patient to remove. ");
				}
			} else {
				throw new BusinessException(ServiceError.Unknown, "No History found for this patient. ");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return true;
	}

	public boolean removeMedicalHistory(String diseaseId, String patientId, String doctorId, String hospitalId, String locationId) {
		HistoryCollection historyCollection = null;
		try {
			historyCollection = historyRepository.findByDoctorIdLocationIdHospitalIdAndPatientId(doctorId, locationId, hospitalId, patientId);
			if (historyCollection != null) {
				List<String> medicalHistory = historyCollection.getMedicalhistory();
				if (medicalHistory != null) {
					if (medicalHistory.contains(diseaseId)) {
						medicalHistory.remove(diseaseId);
						if (checkIfHistoryRemovedCompletly(historyCollection)) {
							historyRepository.delete(historyCollection.getId());
						} else {
							historyRepository.save(historyCollection);
						}
					} else {
						throw new BusinessException(ServiceError.Unknown, "This disease is not found for this patient to remove. ");
					}
				} else {
					throw new BusinessException(ServiceError.Unknown, "No disease found for this patient to remove. ");
				}
			} else {
				throw new BusinessException(ServiceError.Unknown, "No History found for this patient. ");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return true;
	}

	public boolean removeFamilyHistory(String diseaseId, String patientId, String doctorId, String hospitalId, String locationId) {
		HistoryCollection historyCollection = null;
		try {
			historyCollection = historyRepository.findByDoctorIdLocationIdHospitalIdAndPatientId(doctorId, locationId, hospitalId, patientId);
			if (historyCollection != null) {
				List<String> familyHistory = historyCollection.getFamilyhistory();
				if (familyHistory != null) {
					if (familyHistory.contains(diseaseId)) {
						familyHistory.remove(diseaseId);
						if (checkIfHistoryRemovedCompletly(historyCollection)) {
							historyRepository.delete(historyCollection.getId());
						} else {
							historyRepository.save(historyCollection);
						}
					} else {
						throw new BusinessException(ServiceError.Unknown, "This disease is not found for this patient to remove. ");
					}
				} else {
					throw new BusinessException(ServiceError.Unknown, "No disease found for this patient to remove. ");
				}
			} else {
				throw new BusinessException(ServiceError.Unknown, "No History found for this patient. ");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return true;
	}

	private boolean checkIfHistoryRemovedCompletly(HistoryCollection historyCollection) {
		if (historyCollection != null) {
			if (historyCollection.getReports() == null && historyCollection.getClinicalNotes() == null && historyCollection.getPrescriptions() == null
					&& historyCollection.getMedicalhistory() == null && historyCollection.getFamilyhistory() == null
					&& historyCollection.getSpetialNotes() == null) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	public List<DiseaseListResponse> getDiseases(String doctorId, String hospitalId, String locationId) {
		List<DiseaseListResponse> diseaseListResponses = null;
		try {
			List<DiseasesCollection> diseaseCollections = diseasesRepository.findDiseases(doctorId, locationId, hospitalId);
			if (diseaseCollections != null) {
				diseaseListResponses = new ArrayList<DiseaseListResponse>();
				for (DiseasesCollection diseasesCollection : diseaseCollections) {
					DiseaseListResponse diseaseListResponse = new DiseaseListResponse(diseasesCollection.getId(), diseasesCollection.getDisease(),
							diseasesCollection.getDescription());
					diseaseListResponses.add(diseaseListResponse);

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return diseaseListResponses;
	}

}
