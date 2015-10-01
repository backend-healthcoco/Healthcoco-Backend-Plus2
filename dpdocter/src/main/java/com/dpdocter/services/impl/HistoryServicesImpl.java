package com.dpdocter.services.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.IteratorUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import com.dpdocter.beans.ClinicalNotes;
import com.dpdocter.beans.GeneralData;
import com.dpdocter.beans.MailAttachment;
import com.dpdocter.beans.MailData;
import com.dpdocter.beans.MedicalData;
import com.dpdocter.beans.MedicalHistoryHandler;
import com.dpdocter.beans.Prescription;
import com.dpdocter.beans.Records;
import com.dpdocter.collections.ClinicalNotesCollection;
import com.dpdocter.collections.DiseasesCollection;
import com.dpdocter.collections.HistoryCollection;
import com.dpdocter.collections.NotesCollection;
import com.dpdocter.collections.PrescriptionCollection;
import com.dpdocter.collections.RecordsCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.enums.HistoryFilter;
import com.dpdocter.enums.Range;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.ClinicalNotesRepository;
import com.dpdocter.repository.DiseasesRepository;
import com.dpdocter.repository.HistoryRepository;
import com.dpdocter.repository.NotesRepository;
import com.dpdocter.repository.PrescriptionRepository;
import com.dpdocter.repository.RecordsRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.request.DiseaseAddEditRequest;
import com.dpdocter.response.DiseaseAddEditResponse;
import com.dpdocter.response.DiseaseListResponse;
import com.dpdocter.response.HistoryDetailsResponse;
import com.dpdocter.services.ClinicalNotesService;
import com.dpdocter.services.HistoryServices;
import com.dpdocter.services.MailService;
import com.dpdocter.services.PrescriptionServices;
import com.dpdocter.services.RecordsService;
import common.util.web.DPDoctorUtils;

@Service
public class HistoryServicesImpl implements HistoryServices {

    private static Logger logger = Logger.getLogger(HistoryServicesImpl.class.getName());

    @Autowired
    private DiseasesRepository diseasesRepository;

    @Autowired
    private HistoryRepository historyRepository;

    @Autowired
    private RecordsService recordsService;

    @Autowired
    private PrescriptionServices prescriptionServices;

    @Autowired
    private ClinicalNotesService clinicalNotesService;

    @Autowired
    private RecordsRepository recordsRepository;

    @Autowired
    private ClinicalNotesRepository clinicalNotesRepository;

    @Autowired
    private PrescriptionRepository prescriptionRepository;

    @Autowired
    private NotesRepository notesRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private MongoOperations mongoOperations;

    @Autowired
    private MailService mailService;
    
    @Autowired
    private UserRepository userRepository;

    @Override
    public List<DiseaseAddEditResponse> addDiseases(List<DiseaseAddEditRequest> request) {
	List<DiseaseAddEditResponse> response = null;
	List<DiseasesCollection> diseases = new ArrayList<DiseasesCollection>();
	try {
	    for (Iterator<DiseaseAddEditRequest> iterator = request.iterator(); iterator.hasNext();) {
		DiseaseAddEditRequest diseaseaddEditRequest = iterator.next();
		diseaseaddEditRequest.setCreatedTime(new Date());
	    }
	    BeanUtil.map(request, diseases);
	    diseases = diseasesRepository.save(diseases);
	    response = new ArrayList<DiseaseAddEditResponse>();
	    BeanUtil.map(diseases, response);
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Occurred While Saving Disease(s)");
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Saving Disease(s)");
	}
	return response;
    }

    @Override
    public DiseaseAddEditResponse editDiseases(DiseaseAddEditRequest request) {
	DiseaseAddEditResponse response = null;
	DiseasesCollection disease = new DiseasesCollection();
	BeanUtil.map(request, disease);
	try {
	    DiseasesCollection oldDisease = diseasesRepository.findOne(request.getId());
	    disease.setCreatedBy(oldDisease.getCreatedBy());
	    disease.setCreatedTime(oldDisease.getCreatedTime());
	    disease.setDiscarded(oldDisease.getDiscarded());
	    disease = diseasesRepository.save(disease);
	    response = new DiseaseAddEditResponse();
	    BeanUtil.map(disease, response);
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Occurred While Editing Disease");
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Editing Disease");
	}
	return response;
    }

    @Override
    public Boolean deleteDisease(String diseaseId, String doctorId, String hospitalId, String locationId) {
	Boolean response = false;
	DiseasesCollection disease = null;
	try {
	    disease = diseasesRepository.findOne(diseaseId);
	    if (disease != null) {
		if (disease.getDoctorId() != null && disease.getHospitalId() != null && disease.getLocationId() != null) {
		    if (disease.getDoctorId().equals(doctorId) && disease.getHospitalId().equals(hospitalId) && disease.getLocationId().equals(locationId)) {
			disease.setDiscarded(true);
			disease = diseasesRepository.save(disease);
			response = true;
		    } else {
			logger.warn("Invalid Doctor Id, Hospital Id, Or Location Id");
			throw new BusinessException(ServiceError.NotAuthorized, "Invalid Doctor Id, Hospital Id, Or Location Id");
		    }
		} else {
		    logger.warn("Cannot Delete Global Disease");
		    throw new BusinessException(ServiceError.NotAuthorized, "Cannot Delete Global Disease");
		}
	    } else {
		logger.warn("Disease Not Found");
		throw new BusinessException(ServiceError.NotFound, "Disease Not Found");
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Occurred While Deleting Disease");
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Deleting Disease");
	}
	return response;
    }

    @Override
    public boolean addReportToHistory(String reportId, String patientId, String doctorId, String hospitalId, String locationId) {
	HistoryCollection historyCollection = null;
	RecordsCollection recordsCollection = null;
	try {
	    GeneralData report = new GeneralData();
	    report.setData(reportId);
	    report.setDataType(HistoryFilter.REPORTS);
	    // check if history for this patient is already added .
	    historyCollection = historyRepository.findOne(doctorId, locationId, hospitalId, patientId);
	    if (historyCollection != null) {

		// check if reports are there in history.

		Collection<String> reports = null;
		if (historyCollection.getGeneralRecords() != null)
		    reports = CollectionUtils.collect(historyCollection.getGeneralRecords(), new BeanToPropertyValueTransformer("data"));
		if (reports != null) {
		    // check if this report id is already added into history.
		    if (!reports.contains(reportId)) {
			historyCollection.getGeneralRecords().add(0, report);
		    } else {
			return true;
		    }
		    // if no report is added into history then add it .
		} else {
		    if (historyCollection.getGeneralRecords() == null) {
			List<GeneralData> generalRecords = historyCollection.getGeneralRecords();
			generalRecords = new ArrayList<GeneralData>();
			generalRecords.add(0, report);
			historyCollection.setGeneralRecords(generalRecords);
		    } else
			historyCollection.getGeneralRecords().add(0, report);
		}
	    } else {
		// if history not added for this patient.Create new history.
		historyCollection = new HistoryCollection(doctorId, locationId, hospitalId, patientId);
		historyCollection.setGeneralRecords(Arrays.asList(report));
		historyCollection.setCreatedTime(new Date());
	    }
	    // finally add history into db.
	    historyRepository.save(historyCollection);

	    // modify record that it has been added to history.
	    recordsCollection = recordsRepository.findOne(reportId);
	    if (recordsCollection != null) {
		recordsCollection.setInHistory(true);
		recordsRepository.save(recordsCollection);
	    }

	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
	return true;
    }

    @Override
    public boolean addClinicalNotesToHistory(String clinicalNotesId, String patientId, String doctorId, String hospitalId, String locationId) {
	HistoryCollection historyCollection = null;
	ClinicalNotesCollection clinicalNotesCollection = null;
	try {
	    GeneralData clinicalNote = new GeneralData();
	    clinicalNote.setData(clinicalNotesId);
	    clinicalNote.setDataType(HistoryFilter.CLINICAL_NOTES);
	    // check if history for this patient is already added .
	    historyCollection = historyRepository.findOne(doctorId, locationId, hospitalId, patientId);
	    if (historyCollection != null) {
		// check if clinical notes are there in history.
		Collection<String> clinicalNotes = null;
		if (historyCollection.getGeneralRecords() != null)
		    clinicalNotes = CollectionUtils.collect(historyCollection.getGeneralRecords(), new BeanToPropertyValueTransformer("data"));
		if (clinicalNotes != null) {
		    // check if this clinicalNotes id is already added into
		    // history.
		    if (!clinicalNotes.contains(clinicalNotesId)) {
			historyCollection.getGeneralRecords().add(0, clinicalNote);
		    } else {
			return true;
		    }
		    // if no clinicalNote is added into history then add it .
		} else {
		    if (historyCollection.getGeneralRecords() == null) {
			List<GeneralData> generalRecords = historyCollection.getGeneralRecords();
			generalRecords = new ArrayList<GeneralData>();
			generalRecords.add(0, clinicalNote);
			historyCollection.setGeneralRecords(generalRecords);
		    } else
			historyCollection.getGeneralRecords().add(0, clinicalNote);
		}
	    } else {// if history not added for this patient.Create new history.
		historyCollection = new HistoryCollection(doctorId, locationId, hospitalId, patientId);
		historyCollection.setGeneralRecords(Arrays.asList(clinicalNote));
		historyCollection.setCreatedTime(new Date());
	    }
	    // finally add history into db.
	    historyRepository.save(historyCollection);

	    // modify clinical notes that it has been added to history.
	    clinicalNotesCollection = clinicalNotesRepository.findOne(clinicalNotesId);
	    if (clinicalNotesCollection != null) {
		clinicalNotesCollection.setInHistory(true);
		clinicalNotesRepository.save(clinicalNotesCollection);
	    }

	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());

	}
	return true;
    }

    @Override
    public boolean addPrescriptionToHistory(String prescriptionId, String patientId, String doctorId, String hospitalId, String locationId) {
	HistoryCollection historyCollection = null;
	PrescriptionCollection prescriptionCollection = null;
	try {
	    GeneralData prescription = new GeneralData();
	    prescription.setData(prescriptionId);
	    prescription.setDataType(HistoryFilter.PRESCRIPTIONS);
	    // check if history for this patient is already added .
	    historyCollection = historyRepository.findOne(doctorId, locationId, hospitalId, patientId);
	    if (historyCollection != null) {
		// check if prescription are there in history.
		Collection<String> prescriptions = null;
		if (historyCollection.getGeneralRecords() != null)
		    prescriptions = CollectionUtils.collect(historyCollection.getGeneralRecords(), new BeanToPropertyValueTransformer("data"));
		if (prescriptions != null) {
		    // check if this prescription id is already added into
		    // history.
		    if (!prescriptions.contains(prescriptionId)) {
			historyCollection.getGeneralRecords().add(0, prescription);
		    } else {
			return true;
		    }
		    // if no prescription is added into history then add it .
		} else {
		    if (historyCollection.getGeneralRecords() == null) {
			List<GeneralData> generalRecords = historyCollection.getGeneralRecords();
			generalRecords = new ArrayList<GeneralData>();
			generalRecords.add(0, prescription);
			historyCollection.setGeneralRecords(generalRecords);
		    } else
			historyCollection.getGeneralRecords().add(0, prescription);
		}
	    } else {// if history not added for this patient.Create new history.
		historyCollection = new HistoryCollection(doctorId, locationId, hospitalId, patientId);
		historyCollection.setGeneralRecords(Arrays.asList(prescription));
		historyCollection.setCreatedTime(new Date());
	    }
	    // finally add history into db.
	    historyRepository.save(historyCollection);

	    // modify prescription that it has been added to history.
	    prescriptionCollection = prescriptionRepository.findOne(prescriptionId);
	    if (prescriptionCollection != null) {
		prescriptionCollection.setInHistory(true);
		prescriptionRepository.save(prescriptionCollection);
	    }

	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());

	}
	return true;
    }

    @Override
    public boolean assignMedicalHistory(String diseaseId, String patientId, String doctorId, String hospitalId, String locationId) {
	HistoryCollection historyCollection = null;
	try {
	    // check if history for this patient is already added .
	    historyCollection = historyRepository.findOne(doctorId, locationId, hospitalId, patientId);
	    if (historyCollection != null) {
		// check if medicalHistory are there in history.
		List<String> medicalHistoryList = historyCollection.getMedicalhistory();
		if (medicalHistoryList != null) {
		    // check if this diseaseId id is already added into history.
		    if (!medicalHistoryList.contains(diseaseId)) {
			medicalHistoryList.add(diseaseId);
		    } else {
			return false;
		    }
		    // if no medicalHistory is added into history then add it .
		} else {
		    medicalHistoryList = new ArrayList<String>();
		    medicalHistoryList.add(diseaseId);
		    historyCollection.setMedicalhistory(medicalHistoryList);
		}
	    } else {
		// if history not added for this patient.Create new history.
		historyCollection = new HistoryCollection(doctorId, locationId, hospitalId, patientId);
		historyCollection.setCreatedTime(new Date());
		List<String> medicalHistoryList = new ArrayList<String>();
		medicalHistoryList.add(diseaseId);
		historyCollection.setMedicalhistory(medicalHistoryList);
	    }
	    // finally add history into db.
	    historyRepository.save(historyCollection);

	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());

	}
	return true;
    }

    @Override
    public boolean assignFamilyHistory(String diseaseId, String patientId, String doctorId, String hospitalId, String locationId) {
	HistoryCollection historyCollection = null;
	try {
	    // check if history for this patient is already added .
	    historyCollection = historyRepository.findOne(doctorId, locationId, hospitalId, patientId);
	    if (historyCollection != null) {
		// check if familyHistory are there in history.
		List<String> familyHistoryList = historyCollection.getFamilyhistory();
		if (familyHistoryList != null) {
		    // check if this diseaseId id is already added into history.
		    if (!familyHistoryList.contains(diseaseId)) {
			familyHistoryList.add(diseaseId);
		    } else {
			return false;
		    }
		    // if no familyHistory is added into history then add it .
		} else {
		    familyHistoryList = new ArrayList<String>();
		    familyHistoryList.add(diseaseId);
		    historyCollection.setFamilyhistory(familyHistoryList);
		}
	    } else {// if history not added for this patient.Create new history.
		historyCollection = new HistoryCollection(doctorId, locationId, hospitalId, patientId);
		historyCollection.setCreatedTime(new Date());
		List<String> familyHistoryList = new ArrayList<String>();
		familyHistoryList.add(diseaseId);
		historyCollection.setFamilyhistory(familyHistoryList);
	    }
	    // finally add history into db.
	    historyRepository.save(historyCollection);
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());

	}
	return true;
    }

    @Override
    public boolean addSpecialNotes(List<String> specialNotes, String patientId, String doctorId, String hospitalId, String locationId) {
	HistoryCollection historyCollection = null;
	List<NotesCollection> notesCollections = null;
	try {
	    // check if history for this patient is already added .
	    historyCollection = historyRepository.findOne(doctorId, locationId, hospitalId, patientId);
	    if (historyCollection != null) {
		// check if spetialNotes are there in history.
		List<String> spetialNotesInHistory = historyCollection.getSpecialNotes();
		if (spetialNotesInHistory != null) {
		    spetialNotesInHistory.addAll(specialNotes);
		    // if no spetialNotes is added into history then add it .
		} else {
		    spetialNotesInHistory = new ArrayList<String>();
		    spetialNotesInHistory.addAll(specialNotes);
		    historyCollection.setSpecialNotes(spetialNotesInHistory);
		}
	    } else {// if history not added for this patient.Create new history.
		historyCollection = new HistoryCollection(doctorId, locationId, hospitalId, patientId);
		historyCollection.setCreatedTime(new Date());
		historyCollection.setSpecialNotes(specialNotes);
	    }
	    // finally add history into db.
	    historyRepository.save(historyCollection);

	    // modify notes that they have been added to history.
	    notesCollections = (List<NotesCollection>) notesRepository.findAll(specialNotes);
	    if (notesCollections != null && !notesCollections.isEmpty()) {
		for (NotesCollection note : notesCollections) {
		    note.setInHistory(true);
		}
		notesRepository.save(notesCollections);
	    }

	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());

	}
	return true;
    }

    @Override
    public boolean removeReports(String reportId, String patientId, String doctorId, String hospitalId, String locationId) {
	HistoryCollection historyCollection = null;
	RecordsCollection recordsCollection = null;
	try {
	    historyCollection = historyRepository.findOne(doctorId, locationId, hospitalId, patientId);
	    if (historyCollection != null) {
		List<String> reports = (List<String>) CollectionUtils
			.collect(historyCollection.getGeneralRecords(), new BeanToPropertyValueTransformer("data"));
		if (reports != null) {
		    if (reports.contains(reportId)) {
			historyCollection.getGeneralRecords().remove(reports.indexOf(reportId));
			if (checkIfHistoryRemovedCompletely(historyCollection)) {
			    historyRepository.delete(historyCollection.getId());
			} else {
			    historyRepository.save(historyCollection);
			}
			// modify records that it has been removed from history
			recordsCollection = recordsRepository.findOne(reportId);
			if (recordsCollection != null) {
			    recordsCollection.setInHistory(false);
			    recordsRepository.save(recordsCollection);
			}
		    } else {
			logger.warn("This reports is not found for this patient to remove.");
			throw new BusinessException(ServiceError.Unknown, "This reports is not found for this patient to remove.");
		    }
		} else {
		    logger.warn("No reports found for this patient to remove.");
		    throw new BusinessException(ServiceError.Unknown, "No reports found for this patient to remove.");
		}
	    } else {
		logger.warn("No History found for this patient.");
		throw new BusinessException(ServiceError.Unknown, "No History found for this patient.");
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
	return true;
    }

    @Override
    public boolean removeClinicalNotes(String clinicalNotesId, String patientId, String doctorId, String hospitalId, String locationId) {
	HistoryCollection historyCollection = null;
	ClinicalNotesCollection clinicalNotesCollection = null;
	try {
	    historyCollection = historyRepository.findOne(doctorId, locationId, hospitalId, patientId);
	    if (historyCollection != null) {
		List<String> clinicalNotes = (List<String>) CollectionUtils.collect(historyCollection.getGeneralRecords(), new BeanToPropertyValueTransformer(
			"data"));
		if (clinicalNotes != null) {
		    if (clinicalNotes.contains(clinicalNotesId)) {
			historyCollection.getGeneralRecords().remove(clinicalNotes.indexOf(clinicalNotesId));
			if (checkIfHistoryRemovedCompletely(historyCollection)) {
			    historyRepository.delete(historyCollection.getId());
			} else {
			    historyRepository.save(historyCollection);
			}
			// modify clinical notes that it has been removed from
			// history.
			clinicalNotesCollection = clinicalNotesRepository.findOne(clinicalNotesId);
			if (clinicalNotesCollection != null) {
			    clinicalNotesCollection.setInHistory(false);
			    clinicalNotesRepository.save(clinicalNotesCollection);
			}
		    } else {
			logger.warn("This clinicalNote is not found for this patient to remove.");
			throw new BusinessException(ServiceError.Unknown, "This clinicalNote is not found for this patient to remove.");
		    }
		} else {
		    logger.warn("No clinicalNote found for this patient to remove.");
		    throw new BusinessException(ServiceError.Unknown, "No clinicalNote found for this patient to remove.");
		}
	    } else {
		logger.warn("No History found for this patient.");
		throw new BusinessException(ServiceError.Unknown, "No History found for this patient.");
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
	return true;
    }

    @Override
    public boolean removePrescription(String prescriptionId, String patientId, String doctorId, String hospitalId, String locationId) {
	HistoryCollection historyCollection = null;
	PrescriptionCollection prescriptionCollection = null;
	try {
	    historyCollection = historyRepository.findOne(doctorId, locationId, hospitalId, patientId);
	    if (historyCollection != null) {
		List<String> prescriptions = (List<String>) CollectionUtils.collect(historyCollection.getGeneralRecords(), new BeanToPropertyValueTransformer(
			"data"));
		if (prescriptions != null) {
		    if (prescriptions.contains(prescriptionId)) {
			historyCollection.getGeneralRecords().remove(prescriptions.indexOf(prescriptionId));
			if (checkIfHistoryRemovedCompletely(historyCollection)) {
			    historyRepository.delete(historyCollection.getId());
			} else {
			    historyRepository.save(historyCollection);
			}
			// modify prescription that it has been removed from
			// history.
			prescriptionCollection = prescriptionRepository.findOne(prescriptionId);
			if (prescriptionCollection != null) {
			    prescriptionCollection.setInHistory(false);
			    prescriptionRepository.save(prescriptionCollection);
			}
		    } else {
			logger.warn("This prescription is not found for this patient to remove.");
			throw new BusinessException(ServiceError.Unknown, "This prescription is not found for this patient to remove.");
		    }
		} else {
		    logger.warn("No prescription found for this patient to remove.");
		    throw new BusinessException(ServiceError.Unknown, "No prescription found for this patient to remove.");
		}
	    } else {
		logger.warn("No History found for this patient. ");
		throw new BusinessException(ServiceError.Unknown, "No History found for this patient.");
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
	return true;
    }

    @Override
    public boolean removeMedicalHistory(String diseaseId, String patientId, String doctorId, String hospitalId, String locationId) {
	HistoryCollection historyCollection = null;
	try {
	    historyCollection = historyRepository.findOne(doctorId, locationId, hospitalId, patientId);
	    if (historyCollection != null) {
		List<String> medicalHistory = historyCollection.getMedicalhistory();
		if (medicalHistory != null) {
		    if (medicalHistory.contains(diseaseId)) {
			medicalHistory.remove(diseaseId);
			if (checkIfHistoryRemovedCompletely(historyCollection)) {
			    historyRepository.delete(historyCollection.getId());
			} else {
			    historyRepository.save(historyCollection);
			}
		    } else {
			logger.warn("This disease is not found for this patient to remove.");
			throw new BusinessException(ServiceError.Unknown, "This disease is not found for this patient to remove.");
		    }
		} else {
		    logger.warn("No disease found for this patient to remove.");
		    throw new BusinessException(ServiceError.Unknown, "No disease found for this patient to remove.");
		}
	    } else {
		logger.warn("No History found for this patient.");
		throw new BusinessException(ServiceError.Unknown, "No History found for this patient.");
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
	return true;
    }

    @Override
    public boolean removeFamilyHistory(String diseaseId, String patientId, String doctorId, String hospitalId, String locationId) {
	HistoryCollection historyCollection = null;
	try {
	    historyCollection = historyRepository.findOne(doctorId, locationId, hospitalId, patientId);
	    if (historyCollection != null) {
		List<String> familyHistory = historyCollection.getFamilyhistory();
		if (familyHistory != null) {
		    if (familyHistory.contains(diseaseId)) {
			familyHistory.remove(diseaseId);
			if (checkIfHistoryRemovedCompletely(historyCollection)) {
			    historyRepository.delete(historyCollection.getId());
			} else {
			    historyRepository.save(historyCollection);
			}
		    } else {
			logger.warn("This disease is not found for this patient to remove.");
			throw new BusinessException(ServiceError.Unknown, "This disease is not found for this patient to remove.");
		    }
		} else {
		    logger.warn("No disease found for this patient to remove.");
		    throw new BusinessException(ServiceError.Unknown, "No disease found for this patient to remove.");
		}
	    } else {
		logger.warn("No History found for this patient.");
		throw new BusinessException(ServiceError.Unknown, "No History found for this patient.");
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
	return true;
    }

    private boolean checkIfHistoryRemovedCompletely(HistoryCollection historyCollection) {
	if (historyCollection != null) {
	    if (historyCollection.getGeneralRecords() == null && historyCollection.getMedicalhistory() == null && historyCollection.getFamilyhistory() == null
		    && historyCollection.getSpecialNotes() == null) {
		return true;
	    } else {
		return false;
	    }
	} else {
	    return false;
	}
    }

    @Override
    public List<DiseaseListResponse> getDiseases(String range, int page, int size, String doctorId, String hospitalId, String locationId, String updatedTime,
	    Boolean discarded) {
	List<DiseaseListResponse> diseaseListResponses = null;

	switch (Range.valueOf(range.toUpperCase())) {

	case GLOBAL:
	    diseaseListResponses = getGlobalDiseases(page, size, updatedTime, discarded);
	    break;
	case CUSTOM:
	    diseaseListResponses = getCustomDiseases(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
	    break;
	case BOTH:
	    diseaseListResponses = getCustomGlobalDiseases(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
	    break;
	}
	return diseaseListResponses;
    }

    private List<DiseaseListResponse> getCustomDiseases(int page, int size, String doctorId, String locationId, String hospitalId, String updatedTime,
	    Boolean discarded) {
	List<DiseaseListResponse> diseaseListResponses = null;
	List<DiseasesCollection> diseasesCollections = null;
	try {
	    if (doctorId == null)
		diseasesCollections = new ArrayList<DiseasesCollection>();
	    else if (!DPDoctorUtils.allStringsEmpty(updatedTime)) {
		long createdTimeStamp = Long.parseLong(updatedTime);
		if (locationId == null && hospitalId == null) {
		    if (discarded) {
			if (size > 0)
			    diseasesCollections = diseasesRepository.findCustomDiseases(doctorId, new Date(createdTimeStamp), new PageRequest(page, size,
				    Direction.DESC, "updatedTime"));
			else
			    diseasesCollections = diseasesRepository.findCustomDiseases(doctorId, new Date(createdTimeStamp), new Sort(Sort.Direction.DESC,
				    "updatedTime"));
		    } else {
			if (size > 0)
			    diseasesCollections = diseasesRepository.findCustomDiseases(doctorId, new Date(createdTimeStamp), discarded, new PageRequest(page,
				    size, Direction.DESC, "updatedTime"));
			else
			    diseasesCollections = diseasesRepository.findCustomDiseases(doctorId, new Date(createdTimeStamp), discarded, new Sort(
				    Sort.Direction.DESC, "updatedTime"));
		    }
		} else {
		    if (discarded) {
			if (size > 0)
			    diseasesCollections = diseasesRepository.findCustomDiseases(doctorId, locationId, hospitalId, new Date(createdTimeStamp),
				    new PageRequest(page, size, Direction.DESC, "updatedTime"));
			else
			    diseasesCollections = diseasesRepository.findCustomDiseases(doctorId, locationId, hospitalId, new Date(createdTimeStamp), new Sort(
				    Sort.Direction.DESC, "updatedTime"));
		    } else {
			if (size > 0)
			    diseasesCollections = diseasesRepository.findCustomDiseases(doctorId, locationId, hospitalId, new Date(createdTimeStamp),
				    discarded, new PageRequest(page, size, Direction.DESC, "updatedTime"));
			else
			    diseasesCollections = diseasesRepository.findCustomDiseases(doctorId, locationId, hospitalId, new Date(createdTimeStamp),
				    discarded, new Sort(Sort.Direction.DESC, "updatedTime"));
		    }
		}
	    } else {
		if (locationId == null && hospitalId == null) {
		    if (discarded) {
			if (size > 0)
			    diseasesCollections = diseasesRepository.findCustomDiseases(doctorId, new PageRequest(page, size, Direction.DESC, "updatedTime"));
			else
			    diseasesCollections = diseasesRepository.findCustomDiseases(doctorId, new Sort(Sort.Direction.DESC, "updatedTime"));
		    } else {
			if (size > 0)
			    diseasesCollections = diseasesRepository.findCustomDiseases(doctorId, discarded, new PageRequest(page, size, Direction.DESC,
				    "updatedTime"));
			else
			    diseasesCollections = diseasesRepository.findCustomDiseases(doctorId, discarded, new Sort(Sort.Direction.DESC, "updatedTime"));
		    }
		} else {
		    if (discarded) {
			if (size > 0)
			    diseasesCollections = diseasesRepository.findCustomDiseases(doctorId, locationId, hospitalId, new PageRequest(page, size,
				    Direction.DESC, "updatedTime"));
			else
			    diseasesCollections = diseasesRepository.findCustomDiseases(doctorId, locationId, hospitalId, new Sort(Sort.Direction.DESC,
				    "updatedTime"));
		    } else {
			if (size > 0)
			    diseasesCollections = diseasesRepository.findCustomDiseases(doctorId, locationId, hospitalId, discarded, new PageRequest(page,
				    size, Direction.DESC, "updatedTime"));
			else
			    diseasesCollections = diseasesRepository.findCustomDiseases(doctorId, locationId, hospitalId, discarded, new Sort(
				    Sort.Direction.DESC, "updatedTime"));
		    }
		}
	    }

	    if (diseasesCollections != null) {
		diseaseListResponses = new ArrayList<DiseaseListResponse>();
		for (DiseasesCollection diseasesCollection : diseasesCollections) {
		    DiseaseListResponse diseaseListResponse = new DiseaseListResponse(diseasesCollection.getId(), diseasesCollection.getDisease(),
			    diseasesCollection.getDescription(), diseasesCollection.getDoctorId(), diseasesCollection.getLocationId(),
			    diseasesCollection.getHospitalId(), diseasesCollection.getDiscarded(), diseasesCollection.getCreatedTime(),
			    diseasesCollection.getUpdatedTime());
		    diseaseListResponses.add(diseaseListResponse);

		}
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
	return diseaseListResponses;
    }

    private List<DiseaseListResponse> getGlobalDiseases(int page, int size, String updatedTime, Boolean discarded) {
	List<DiseaseListResponse> diseaseListResponses = null;
	List<DiseasesCollection> diseasesCollections = null;
	try {

	    if (!DPDoctorUtils.allStringsEmpty(updatedTime)) {
		long createdTimeStamp = Long.parseLong(updatedTime);
		if (discarded) {
		    if (size > 0)
			diseasesCollections = diseasesRepository.findGlobalDiseases(new Date(createdTimeStamp), new PageRequest(page, size, Direction.DESC,
				"updatedTime"));
		    else
			diseasesCollections = diseasesRepository.findGlobalDiseases(new Date(createdTimeStamp), new Sort(Sort.Direction.DESC, "updatedTime"));
		} else {
		    if (size > 0)
			diseasesCollections = diseasesRepository.findGlobalDiseases(new Date(createdTimeStamp), discarded, new PageRequest(page, size,
				Direction.DESC, "updatedTime"));
		    else
			diseasesCollections = diseasesRepository.findGlobalDiseases(new Date(createdTimeStamp), discarded, new Sort(Sort.Direction.DESC,
				"updatedTime"));
		}

	    } else {
		if (discarded) {
		    if (size > 0)
			diseasesCollections = diseasesRepository.findGlobalDiseases(new PageRequest(page, size, Direction.DESC, "updatedTime"));
		    else
			diseasesCollections = diseasesRepository.findGlobalDiseases(new Sort(Sort.Direction.DESC, "updatedTime"));
		} else {
		    if (size > 0)
			diseasesCollections = diseasesRepository.findGlobalDiseases(discarded, new PageRequest(page, size, Direction.DESC, "updatedTime"));
		    else
			diseasesCollections = diseasesRepository.findGlobalDiseases(discarded, new Sort(Sort.Direction.DESC, "updatedTime"));
		}
	    }
	    if (diseasesCollections != null) {
		diseaseListResponses = new ArrayList<DiseaseListResponse>();
		for (DiseasesCollection diseasesCollection : diseasesCollections) {
		    DiseaseListResponse diseaseListResponse = new DiseaseListResponse(diseasesCollection.getId(), diseasesCollection.getDisease(),
			    diseasesCollection.getDescription(), diseasesCollection.getDoctorId(), diseasesCollection.getLocationId(),
			    diseasesCollection.getHospitalId(), diseasesCollection.getDiscarded(), diseasesCollection.getCreatedTime(),
			    diseasesCollection.getUpdatedTime());
		    diseaseListResponses.add(diseaseListResponse);

		}
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
	return diseaseListResponses;
    }

    private List<DiseaseListResponse> getCustomGlobalDiseases(int page, int size, String doctorId, String locationId, String hospitalId, String updatedTime,
	    Boolean discarded) {
	List<DiseaseListResponse> diseaseListResponses = null;
	List<DiseasesCollection> diseasesCollections = null;
	try {
	    if (doctorId == null) {
		    	if (!DPDoctorUtils.allStringsEmpty(updatedTime)) {
		    		long createdTimeStamp = Long.parseLong(updatedTime);
		    		if(discarded){
		    			if (size > 0)
		    				diseasesCollections = diseasesRepository.findCustomGlobalDiseases(new Date(createdTimeStamp), new PageRequest(page, size, Direction.DESC, "updatedTime"));
		    			else
		    				diseasesCollections = diseasesRepository.findCustomGlobalDiseases(new Date(createdTimeStamp), new Sort(Sort.Direction.DESC, "updatedTime"));
		    		}
		    		else{
		    			if (size > 0)
		    				diseasesCollections = diseasesRepository.findCustomGlobalDiseases(new Date(createdTimeStamp),discarded, new PageRequest(page, size, Direction.DESC, "updatedTime"));
		    			else
		    				diseasesCollections = diseasesRepository.findCustomGlobalDiseases(new Date(createdTimeStamp),discarded, new Sort(Sort.Direction.DESC, "updatedTime"));
		    		}
		    	}
		    	else{
		    		if(discarded){
		    			if (size > 0)
		    				diseasesCollections = diseasesRepository.findAll(new PageRequest(page, size, Direction.DESC, "updatedTime")).getContent();
		    			else
		    				diseasesCollections = diseasesRepository.findAll(new Sort(Sort.Direction.DESC, "updatedTime"));
		    		}
		    		else{
		    			if (size > 0)
		    				diseasesCollections = diseasesRepository.findCustomGlobalDiseases(discarded, new PageRequest(page, size, Direction.DESC, "updatedTime"));
		    			else
		    				diseasesCollections = diseasesRepository.findCustomGlobalDiseases(discarded, new Sort(Sort.Direction.DESC, "updatedTime"));
		    		}
		    	}
	    }
	    else if (!DPDoctorUtils.allStringsEmpty(updatedTime)) {
		long createdTimeStamp = Long.parseLong(updatedTime);
		if (locationId == null && hospitalId == null) {
		    if (discarded) {
			if (size > 0)
			    diseasesCollections = diseasesRepository.findCustomGlobalDiseases(doctorId, new Date(createdTimeStamp), new PageRequest(page, size,
				    Direction.DESC, "updatedTime"));
			else
			    diseasesCollections = diseasesRepository.findCustomGlobalDiseases(doctorId, new Date(createdTimeStamp), new Sort(
				    Sort.Direction.DESC, "updatedTime"));
		    } else {
			if (size > 0)
			    diseasesCollections = diseasesRepository.findCustomGlobalDiseases(doctorId, new Date(createdTimeStamp), discarded, new PageRequest(
				    page, size, Direction.DESC, "updatedTime"));
			else
			    diseasesCollections = diseasesRepository.findCustomGlobalDiseases(doctorId, new Date(createdTimeStamp), discarded, new Sort(
				    Sort.Direction.DESC, "updatedTime"));
		    }
		} else {
		    if (discarded) {
			if (size > 0)
			    diseasesCollections = diseasesRepository.findCustomGlobalDiseases(doctorId, locationId, hospitalId, new Date(createdTimeStamp),
				    new PageRequest(page, size, Direction.DESC, "updatedTime"));
			else
			    diseasesCollections = diseasesRepository.findCustomGlobalDiseases(doctorId, locationId, hospitalId, new Date(createdTimeStamp),
				    new Sort(Sort.Direction.DESC, "updatedTime"));
		    } else {
			if (size > 0)
			    diseasesCollections = diseasesRepository.findCustomGlobalDiseases(doctorId, locationId, hospitalId, new Date(createdTimeStamp),
				    discarded, new PageRequest(page, size, Direction.DESC, "updatedTime"));
			else
			    diseasesCollections = diseasesRepository.findCustomGlobalDiseases(doctorId, locationId, hospitalId, new Date(createdTimeStamp),
				    discarded, new Sort(Sort.Direction.DESC, "updatedTime"));
		    }
		}
	    } else {
		if (locationId == null && hospitalId == null) {
		    if (discarded) {
			if (size > 0)
			    diseasesCollections = diseasesRepository.findCustomGlobalDiseases(doctorId, new PageRequest(page, size, Direction.DESC,
				    "updatedTime"));
			else
			    diseasesCollections = diseasesRepository.findCustomGlobalDiseases(doctorId, new Sort(Sort.Direction.DESC, "updatedTime"));
		    } else {
			if (size > 0)
			    diseasesCollections = diseasesRepository.findCustomGlobalDiseases(doctorId, discarded, new PageRequest(page, size, Direction.DESC,
				    "updatedTime"));
			else
			    diseasesCollections = diseasesRepository
				    .findCustomGlobalDiseases(doctorId, discarded, new Sort(Sort.Direction.DESC, "updatedTime"));
		    }
		} else {
		    if (discarded) {
			if (size > 0)
			    diseasesCollections = diseasesRepository.findCustomGlobalDiseases(doctorId, locationId, hospitalId, new PageRequest(page, size,
				    Direction.DESC, "updatedTime"));
			else
			    diseasesCollections = diseasesRepository.findCustomGlobalDiseases(doctorId, locationId, hospitalId, new Sort(Sort.Direction.DESC,
				    "updatedTime"));
		    } else {
			if (size > 0)
			    diseasesCollections = diseasesRepository.findCustomGlobalDiseases(doctorId, locationId, hospitalId, discarded, new PageRequest(
				    page, size, Direction.DESC, "updatedTime"));
			else
			    diseasesCollections = diseasesRepository.findCustomGlobalDiseases(doctorId, locationId, hospitalId, discarded, new Sort(
				    Sort.Direction.DESC, "updatedTime"));
		    }
		}
	    }

	    if (diseasesCollections != null) {
		diseaseListResponses = new ArrayList<DiseaseListResponse>();
		for (DiseasesCollection diseasesCollection : diseasesCollections) {
		    DiseaseListResponse diseaseListResponse = new DiseaseListResponse(diseasesCollection.getId(), diseasesCollection.getDisease(),
			    diseasesCollection.getDescription(), diseasesCollection.getDoctorId(), diseasesCollection.getLocationId(),
			    diseasesCollection.getHospitalId(), diseasesCollection.getDiscarded(), diseasesCollection.getCreatedTime(),
			    diseasesCollection.getUpdatedTime());
		    diseaseListResponses.add(diseaseListResponse);

		}
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
	return diseaseListResponses;
    }

    @Override
    public HistoryDetailsResponse getPatientHistoryDetailsWithoutVerifiedOTP(String patientId, String doctorId, String hospitalId, String locationId,
	    String historyFilter) {
	HistoryDetailsResponse response = null;
	try {
	    HistoryCollection historyCollection = null;

	    if (HistoryFilter.ALL.getFilter().equals(historyFilter)) {
		historyCollection = historyRepository.findOne(doctorId, locationId, hospitalId, patientId);
	    } else {
		historyCollection = historyRepository.findOne(doctorId, locationId, hospitalId, patientId);
		if (historyCollection.getGeneralRecords() != null && !historyCollection.getGeneralRecords().isEmpty()) {
		    List<GeneralData> generalRecords = historyCollection.getGeneralRecords();
		    for (Iterator<GeneralData> iterator = generalRecords.iterator(); iterator.hasNext();) {
			GeneralData generalRecord = iterator.next();
			if (!generalRecord.getDataType().toString().trim().equals(historyFilter.toString())) {
			    iterator.remove();
			}
		    }
		    historyCollection.setGeneralRecords(generalRecords);
		}
	    }

	    if (historyCollection != null) {
		response = new HistoryDetailsResponse();
		BeanUtil.map(historyCollection, response);

		if (historyCollection.getGeneralRecords() != null && !historyCollection.getGeneralRecords().isEmpty()) {
		    response.setGeneralRecords(fetchGeneralData(historyCollection.getGeneralRecords()));
		}

		List<String> medicalHistoryIds = historyCollection.getMedicalhistory();
		if (medicalHistoryIds != null) {
		    List<DiseaseListResponse> medicalHistory = getDiseasesByIds(medicalHistoryIds);
		    response.setMedicalhistory(medicalHistory);
		}

		List<String> familyHistoryIds = historyCollection.getFamilyhistory();
		if (familyHistoryIds != null) {
		    List<DiseaseListResponse> familyHistory = getDiseasesByIds(medicalHistoryIds);
		    response.setFamilyhistory(familyHistory);
		}

		response.setSpecialNotes(historyCollection.getSpecialNotes());
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}

	return response;
    }

    private List<GeneralData> fetchGeneralData(List<GeneralData> ids) {
	List<GeneralData> details = null;
	try {
	    details = new ArrayList<GeneralData>();
	    for (GeneralData id : ids) {
		GeneralData generalData = new GeneralData();
		switch (id.getDataType()) {
		case CLINICAL_NOTES:
		    ClinicalNotes clinicalNote = clinicalNotesService.getNotesById(id.getData().toString());
		    if (clinicalNote != null) {
		    UserCollection userCollection = userRepository.findOne(clinicalNote.getDoctorId());
			if (userCollection != null) clinicalNote.setDoctorName(userCollection.getFirstName());
			generalData.setData(clinicalNote);
			generalData.setDataType(HistoryFilter.CLINICAL_NOTES);
		    }
		    break;
		case PRESCRIPTIONS:
		    Prescription prescription = prescriptionServices.getPrescriptionById(id.getData().toString());
		    if (prescription != null) {
		    	UserCollection userCollection = userRepository.findOne(prescription.getDoctorId());
				if (userCollection != null) prescription.setDoctorName(userCollection.getFirstName());
			generalData.setData(prescription);
			generalData.setDataType(HistoryFilter.PRESCRIPTIONS);
		    }
		    break;
		case REPORTS:
		    Records record = recordsService.getRecordById(id.getData().toString());
		    if (record != null) {
		    UserCollection userCollection = userRepository.findOne(record.getDoctorId());
			if (userCollection != null) record.setDoctorName(userCollection.getFirstName());
			generalData.setData(record);
			generalData.setDataType(HistoryFilter.REPORTS);
		    }
		    break;
		default:
		    break;
		}
		if (generalData.getData() != null) {
		    details.add(generalData);
		}
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
	return details;
    }

    @Override
    public List<HistoryDetailsResponse> getPatientHistoryDetailsWithVerifiedOTP(String patientId, String doctorId, String hospitalId, String locationId,
	    String historyFilter) {
	List<HistoryDetailsResponse> response = null;
	try {
	    List<HistoryCollection> historyCollections = null;
	    if (HistoryFilter.ALL.getFilter().equals(historyFilter)) {
		historyCollections = historyRepository.findAll(patientId);
	    } else {
		historyCollections = historyRepository.findAll(patientId);
		if (historyCollections != null && !historyCollections.isEmpty()) {
		    for (Iterator<HistoryCollection> collectionIterator = historyCollections.iterator(); collectionIterator.hasNext();) {
			HistoryCollection historyCollection = collectionIterator.next();
			if (historyCollection.getGeneralRecords() != null && !historyCollection.getGeneralRecords().isEmpty()) {
			    List<GeneralData> generalRecords = historyCollection.getGeneralRecords();
			    for (Iterator<GeneralData> iterator = generalRecords.iterator(); iterator.hasNext();) {
				GeneralData generalRecord = iterator.next();
				if (!generalRecord.getDataType().toString().trim().equals(historyFilter)) {
				    iterator.remove();
				}
			    }
			    historyCollection.setGeneralRecords(generalRecords);
			}
		    }
		}
	    }

	    if (historyCollections != null) {
		response = new ArrayList<HistoryDetailsResponse>();
		for (HistoryCollection historyCollection : historyCollections) {
		    HistoryDetailsResponse historyDetailsResponse = new HistoryDetailsResponse();
		    BeanUtil.map(historyCollection, historyDetailsResponse);

		    if (historyCollection.getGeneralRecords() != null && !historyCollection.getGeneralRecords().isEmpty()) {
			historyDetailsResponse.setGeneralRecords(fetchGeneralData(historyCollection.getGeneralRecords()));
		    }

		    List<String> medicalHistoryIds = historyCollection.getMedicalhistory();
		    if (medicalHistoryIds != null) {
			List<DiseaseListResponse> medicalHistory = getDiseasesByIds(medicalHistoryIds);
			historyDetailsResponse.setMedicalhistory(medicalHistory);
		    }

		    List<String> familyHistoryIds = historyCollection.getFamilyhistory();
		    if (familyHistoryIds != null) {
			List<DiseaseListResponse> familyHistory = getDiseasesByIds(medicalHistoryIds);
			historyDetailsResponse.setFamilyhistory(familyHistory);
		    }

		    historyDetailsResponse.setSpecialNotes(historyCollection.getSpecialNotes());

		    response.add(historyDetailsResponse);
		}
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
	return response;
    }

    @Override
    public List<DiseaseListResponse> getDiseasesByIds(List<String> diseasesIds) {
	List<DiseaseListResponse> diseaseListResponses = null;
	try {
	    Iterable<DiseasesCollection> diseasesCollIterable = diseasesRepository.findAll(diseasesIds);

	    if (diseasesCollIterable != null) {
		@SuppressWarnings("unchecked")
		List<DiseasesCollection> diseasesCollections = IteratorUtils.toList(diseasesCollIterable.iterator());
		diseaseListResponses = new ArrayList<DiseaseListResponse>();
		BeanUtil.map(diseasesCollections, diseaseListResponses);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
	return diseaseListResponses;
    }

    @Override
    public Integer getHistoryCount(String doctorId, String patientId, String locationId, String hospitalId) {
	Integer historyCount = 0;
	try {
	    HistoryCollection historyCollection = historyRepository.findOne(doctorId, locationId, hospitalId, patientId);
	    if (historyCollection != null) {
		if (historyCollection.getGeneralRecords() != null && !historyCollection.getGeneralRecords().isEmpty()) {
		    List<GeneralData> generslData = fetchGeneralData(historyCollection.getGeneralRecords());
		    historyCount = generslData.isEmpty() ? 0 : generslData.size();
		}
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Occurred While Getting History Count");
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting History Count");
	}
	return historyCount;
    }

    @Override
    public boolean handleMedicalHistory(MedicalHistoryHandler request) {
	HistoryCollection historyCollection = null;
	boolean response = false;
	try {
	    historyCollection = historyRepository.findOne(request.getDoctorId(), request.getLocationId(), request.getHospitalId(), request.getPatientId());
	    if (historyCollection != null) {
		List<String> medicalHistoryList = historyCollection.getMedicalhistory();
		if (medicalHistoryList != null && !medicalHistoryList.isEmpty()) {
		    medicalHistoryList.addAll(request.getAddDiseases());
		    medicalHistoryList = new ArrayList<String>(new LinkedHashSet<String>(medicalHistoryList));
		    medicalHistoryList.removeAll(request.getRemoveDiseases());
		    historyCollection.setMedicalhistory(medicalHistoryList);
		} else {
		    medicalHistoryList = new ArrayList<String>();
		    medicalHistoryList.addAll(request.getAddDiseases());
		    medicalHistoryList.removeAll(request.getRemoveDiseases());
		    historyCollection.setMedicalhistory(medicalHistoryList);
		}
	    } else {
		historyCollection = new HistoryCollection(request.getDoctorId(), request.getLocationId(), request.getHospitalId(), request.getPatientId());
		historyCollection.setCreatedTime(new Date());
		List<String> medicalHistoryList = new ArrayList<String>();
		medicalHistoryList.addAll(request.getAddDiseases());
		medicalHistoryList.removeAll(request.getRemoveDiseases());
		historyCollection.setMedicalhistory(medicalHistoryList);
	    }
	    if (checkIfHistoryRemovedCompletely(historyCollection)) {
		historyRepository.delete(historyCollection.getId());
	    } else {
		historyRepository.save(historyCollection);
	    }

	    response = true;

	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());

	}
	return response;
    }

    @Override
    public boolean handleFamilyHistory(MedicalHistoryHandler request) {
	HistoryCollection historyCollection = null;
	boolean response = false;
	try {
	    historyCollection = historyRepository.findOne(request.getDoctorId(), request.getLocationId(), request.getHospitalId(), request.getPatientId());
	    if (historyCollection != null) {
		List<String> familyHistoryList = historyCollection.getFamilyhistory();
		if (familyHistoryList != null && !familyHistoryList.isEmpty()) {
		    familyHistoryList.addAll(request.getAddDiseases());
		    familyHistoryList = new ArrayList<String>(new LinkedHashSet<String>(familyHistoryList));
		    familyHistoryList.removeAll(request.getRemoveDiseases());
		    historyCollection.setFamilyhistory(familyHistoryList);
		} else {
		    familyHistoryList = new ArrayList<String>();
		    familyHistoryList.addAll(request.getAddDiseases());
		    familyHistoryList.removeAll(request.getRemoveDiseases());
		    historyCollection.setFamilyhistory(familyHistoryList);
		}
	    } else {
		historyCollection = new HistoryCollection(request.getDoctorId(), request.getLocationId(), request.getHospitalId(), request.getPatientId());
		historyCollection.setCreatedTime(new Date());
		List<String> familyHistoryList = new ArrayList<String>();
		familyHistoryList.addAll(request.getAddDiseases());
		familyHistoryList.removeAll(request.getRemoveDiseases());
		historyCollection.setFamilyhistory(familyHistoryList);
	    }
	    if (checkIfHistoryRemovedCompletely(historyCollection)) {
		historyRepository.delete(historyCollection.getId());
	    } else {
		historyRepository.save(historyCollection);
	    }

	    response = true;

	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());

	}
	return response;
    }

    @Override
    public HistoryDetailsResponse getMedicalAndFamilyHistory(String patientId, String doctorId, String hospitalId, String locationId) {
	HistoryDetailsResponse response = null;
	HistoryCollection historyCollection = null;

	try {
	    historyCollection = historyRepository.findOne(doctorId, locationId, hospitalId, patientId);
	    List<String> medicalHistoryIds = historyCollection.getMedicalhistory();
	    if (medicalHistoryIds != null) {
		if (response == null)
		    response = new HistoryDetailsResponse();
		List<DiseaseListResponse> medicalHistory = getDiseasesByIds(medicalHistoryIds);
		response.setMedicalhistory(medicalHistory);
	    }

	    List<String> familyHistoryIds = historyCollection.getFamilyhistory();
	    if (familyHistoryIds != null) {
		if (response == null)
		    response = new HistoryDetailsResponse();
		List<DiseaseListResponse> familyHistory = getDiseasesByIds(medicalHistoryIds);
		response.setFamilyhistory(familyHistory);
	    }

	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
	return response;
    }

    @Override
    public boolean mailMedicalData(MedicalData medicalData) {
	boolean response = false;
	List<MailAttachment> mailAttachments = null;
	try {
	    String doctorId = medicalData.getDoctorId();
	    String locationId = medicalData.getLocationId();
	    String hospitalId = medicalData.getHospitalId();
	    String emailAddress = medicalData.getEmailAddress();
	    mailAttachments = new ArrayList<MailAttachment>();
	    for (MailData mailData : medicalData.getMailDataList()) {
		switch (mailData.getMailType()) {
		case CLINICAL_NOTE:
		    mailAttachments.add(clinicalNotesService.getClinicalNotesMailData(mailData.getId(), doctorId, locationId, hospitalId));
		    break;
		case PRESCRIPTION:
		    mailAttachments.add(prescriptionServices.getPrescriptionMailData(mailData.getId(), doctorId, locationId, hospitalId));
		    break;
		case REPORT:
		    mailAttachments.add(recordsService.getRecordMailData(mailData.getId()));
		    break;
		}
	    }
	    mailService.sendEmailMultiAttach(emailAddress, "Medical Data", "PFA.", mailAttachments);
	    response = true;
	} catch (Exception e) {
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
	return response;
    }
}
