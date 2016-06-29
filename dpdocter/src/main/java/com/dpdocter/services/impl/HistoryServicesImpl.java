package com.dpdocter.services.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
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
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.ClinicalNotes;
import com.dpdocter.beans.DiagnosticTest;
import com.dpdocter.beans.Drug;
import com.dpdocter.beans.GeneralData;
import com.dpdocter.beans.History;
import com.dpdocter.beans.MailAttachment;
import com.dpdocter.beans.MailData;
import com.dpdocter.beans.MedicalData;
import com.dpdocter.beans.MedicalHistoryHandler;
import com.dpdocter.beans.PatientTreatment;
import com.dpdocter.beans.Prescription;
import com.dpdocter.beans.PrescriptionItem;
import com.dpdocter.beans.PrescriptionItemDetail;
import com.dpdocter.beans.Records;
import com.dpdocter.beans.TestAndRecordData;
import com.dpdocter.collections.ClinicalNotesCollection;
import com.dpdocter.collections.DiagnosticTestCollection;
import com.dpdocter.collections.DiseasesCollection;
import com.dpdocter.collections.DrugCollection;
import com.dpdocter.collections.HistoryCollection;
import com.dpdocter.collections.NotesCollection;
import com.dpdocter.collections.PatientTreatmentCollection;
import com.dpdocter.collections.PatientVisitCollection;
import com.dpdocter.collections.PrescriptionCollection;
import com.dpdocter.collections.RecordsCollection;
import com.dpdocter.enums.HistoryFilter;
import com.dpdocter.enums.Range;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.ClinicalNotesRepository;
import com.dpdocter.repository.DiagnosticTestRepository;
import com.dpdocter.repository.DiseasesRepository;
import com.dpdocter.repository.DrugRepository;
import com.dpdocter.repository.HistoryRepository;
import com.dpdocter.repository.LocationRepository;
import com.dpdocter.repository.NotesRepository;
import com.dpdocter.repository.PatientTreamentRepository;
import com.dpdocter.repository.PatientVisitRepository;
import com.dpdocter.repository.PrescriptionRepository;
import com.dpdocter.repository.RecordsRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.request.DiseaseAddEditRequest;
import com.dpdocter.response.DiseaseAddEditResponse;
import com.dpdocter.response.DiseaseListResponse;
import com.dpdocter.response.HistoryDetailsResponse;
import com.dpdocter.response.MailResponse;
import com.dpdocter.response.PatientTreatmentResponse;
import com.dpdocter.response.TestAndRecordDataResponse;
import com.dpdocter.services.ClinicalNotesService;
import com.dpdocter.services.HistoryServices;
import com.dpdocter.services.MailBodyGenerator;
import com.dpdocter.services.MailService;
import com.dpdocter.services.OTPService;
import com.dpdocter.services.PatientTreatmentServices;
import com.dpdocter.services.PrescriptionServices;
import com.dpdocter.services.RecordsService;

@Service
public class HistoryServicesImpl implements HistoryServices {

    private static Logger logger = Logger.getLogger(HistoryServicesImpl.class.getName());

    @Autowired
    private OTPService otpService;

    @Autowired
    private DiseasesRepository diseasesRepository;

    @Autowired
    private HistoryRepository historyRepository;

    @Autowired
    private RecordsService recordsService;

    @Autowired
    private PrescriptionServices prescriptionServices;

    @Autowired
    private DrugRepository drugRepository;

    @Autowired
    private PatientTreatmentServices patientTreatmentServices;

    @Autowired
    private ClinicalNotesService clinicalNotesService;

    @Autowired
    private RecordsRepository recordsRepository;

    @Autowired
    private ClinicalNotesRepository clinicalNotesRepository;

    @Autowired
    private PrescriptionRepository prescriptionRepository;

    @Autowired
    private DiagnosticTestRepository diagnosticTestRepository;

    @Autowired
    private PatientTreamentRepository patientTreamentRepository;

    @Autowired
    private NotesRepository notesRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private MailService mailService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private PatientVisitRepository patientVisitRepository;

    @Autowired
    private MailBodyGenerator mailBodyGenerator;

    @Override
    @Transactional
    public List<DiseasesCollection> addDiseases(List<DiseaseAddEditRequest> request) {
	List<DiseasesCollection> response = new ArrayList<DiseasesCollection>();
	try {
	    for (DiseaseAddEditRequest addEditRequest : request) {
	    	addEditRequest.setCreatedTime(new Date());
	    	DiseasesCollection diseasesCollection = new DiseasesCollection();
	    	BeanUtil.map(addEditRequest, diseasesCollection);
	    	diseasesCollection = diseasesRepository.save(diseasesCollection);
	    	response.add(diseasesCollection);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Occurred While Saving Disease(s)");
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Saving Disease(s)");
	}
	return response;
    }

    @Override
    @Transactional
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
    @Transactional
    public DiseaseAddEditResponse deleteDisease(String diseaseId, String doctorId, String hospitalId, String locationId, Boolean discarded) {
    	DiseaseAddEditResponse response = null;
	DiseasesCollection disease = null;
	try {
	    disease = diseasesRepository.findOne(diseaseId);
	    if (disease != null) {
		if (disease.getDoctorId() != null && disease.getHospitalId() != null && disease.getLocationId() != null) {
		    if (disease.getDoctorId().equals(doctorId) && disease.getHospitalId().equals(hospitalId) && disease.getLocationId().equals(locationId)) {
			disease.setDiscarded(discarded);
			disease.setUpdatedTime(new Date());
			disease = diseasesRepository.save(disease);
			response = new DiseaseAddEditResponse();
			BeanUtil.map(disease, response);
		    } else {
			logger.warn("Invalid Doctor Id, Hospital Id, Or Location Id");
			throw new BusinessException(ServiceError.NotAuthorized, "Invalid Doctor Id, Hospital Id, Or Location Id");
		    }
		} else {
			disease.setDiscarded(discarded);
			disease.setUpdatedTime(new Date());
			disease = diseasesRepository.save(disease);
			response = new DiseaseAddEditResponse();
			BeanUtil.map(disease, response);
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

    @SuppressWarnings("unchecked")
    @Override
    @Transactional
    public Records addReportToHistory(String reportId, String patientId, String doctorId, String hospitalId, String locationId) {
    	Records response = null;
    	HistoryCollection historyCollection = null;
    	RecordsCollection recordsCollection = null;
	try {
	    GeneralData report = new GeneralData();
	    report.setData(reportId);
	    report.setDataType(HistoryFilter.REPORTS);
	    // check if history for this patient is already added .
	    historyCollection = historyRepository.findHistory(doctorId, locationId, hospitalId, patientId);
	    if (historyCollection != null) {

		// check if reports are there in history.

		Collection<String> reports = null;
		if (historyCollection.getGeneralRecords() != null)
		    reports = CollectionUtils.collect(historyCollection.getGeneralRecords(), new BeanToPropertyValueTransformer("data"));
		if (reports != null) {
		    // check if this report id is already added into history.
		    if (!reports.contains(reportId)) {
			historyCollection.getGeneralRecords().add(0, report);
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
		historyCollection.setUpdatedTime(new Date());
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
		recordsCollection.setUpdatedTime(new Date());
		recordsRepository.save(recordsCollection);
		response = new Records();
		BeanUtil.map(recordsCollection, response);
	    }

	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
	return response;
    }

    @SuppressWarnings("unchecked")
    @Override
    @Transactional
    public ClinicalNotes addClinicalNotesToHistory(String clinicalNotesId, String patientId, String doctorId, String hospitalId, String locationId) {
    	ClinicalNotes response = null;
    	HistoryCollection historyCollection = null;
	ClinicalNotesCollection clinicalNotesCollection = null;
	try {
	    GeneralData clinicalNote = new GeneralData();
	    clinicalNote.setData(clinicalNotesId);
	    clinicalNote.setDataType(HistoryFilter.CLINICAL_NOTES);
	    // check if history for this patient is already added .
	    historyCollection = historyRepository.findHistory(doctorId, locationId, hospitalId, patientId);
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
		historyCollection.setUpdatedTime(new Date());
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
		clinicalNotesCollection.setUpdatedTime(new Date());
		clinicalNotesRepository.save(clinicalNotesCollection);
		response = new ClinicalNotes();
		BeanUtil.map(clinicalNotesCollection, response);
	    }

	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());

	}
	return response;
    }

    @SuppressWarnings("unchecked")
    @Override
    @Transactional
    public Prescription addPrescriptionToHistory(String prescriptionId, String patientId, String doctorId, String hospitalId, String locationId) {
    	Prescription response = null;
    	HistoryCollection historyCollection = null;
	PrescriptionCollection prescriptionCollection = null;
	try {
	    GeneralData prescription = new GeneralData();
	    prescription.setData(prescriptionId);
	    prescription.setDataType(HistoryFilter.PRESCRIPTIONS);
	    // check if history for this patient is already added .
	    historyCollection = historyRepository.findHistory(doctorId, locationId, hospitalId, patientId);
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
		historyCollection.setUpdatedTime(new Date());
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
		prescriptionCollection.setUpdatedTime(new Date());
		prescriptionCollection.setInHistory(true);
		prescriptionRepository.save(prescriptionCollection);
		response = new Prescription();
		List<TestAndRecordData> tests = prescriptionCollection.getDiagnosticTests();
		prescriptionCollection.setDiagnosticTests(null);
		BeanUtil.map(prescriptionCollection, response);
		if (prescriptionCollection.getItems() != null) {
		List<PrescriptionItemDetail> prescriptionItemDetailsList = new ArrayList<PrescriptionItemDetail>();
		for (PrescriptionItem prescriptionItem : prescriptionCollection.getItems()) {
		    PrescriptionItemDetail prescriptionItemDetails = new PrescriptionItemDetail();
		    BeanUtil.map(prescriptionItem, prescriptionItemDetails);
		    if (prescriptionItem.getDrugId() != null) {
			DrugCollection drugCollection = drugRepository.findOne(prescriptionItem.getDrugId());
			Drug drug = new Drug();
			if (drugCollection != null)
			    BeanUtil.map(drugCollection, drug);
			prescriptionItemDetails.setDrug(drug);
		    }
		    prescriptionItemDetailsList.add(prescriptionItemDetails);
		}
		response.setItems(prescriptionItemDetailsList);
	    }
		PatientVisitCollection patientVisitCollection = patientVisitRepository.findByPrescriptionId(response.getId());
		if (patientVisitCollection != null) response.setVisitId(patientVisitCollection.getId());
		
		if(tests != null && !tests.isEmpty()){
			List<TestAndRecordDataResponse> diagnosticTests = new ArrayList<TestAndRecordDataResponse>();
		    	for(TestAndRecordData data : tests){
		    		if(data.getTestId() != null){
		    			DiagnosticTestCollection diagnosticTestCollection = diagnosticTestRepository.findOne(data.getTestId());
			    		DiagnosticTest diagnosticTest = new DiagnosticTest();
			    		if(diagnosticTestCollection !=null){
			    			BeanUtil.map(diagnosticTestCollection, diagnosticTest);
			    		}
			    		diagnosticTests.add(new TestAndRecordDataResponse(diagnosticTest, data.getRecordId()));
		    		}
		    }
		    	response.setDiagnosticTests(diagnosticTests);
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
    @Transactional
    public PatientTreatment addPatientTreatmentToHistory(String treatmentId, String patientId, String doctorId, String hospitalId, String locationId) {
    	PatientTreatment response = null;
    	HistoryCollection historyCollection = null;
	PatientTreatmentCollection patientTreatmentCollection;
	try {
	    GeneralData patientTreatment = new GeneralData();
	    patientTreatment.setData(treatmentId);
	    patientTreatment.setDataType(HistoryFilter.PATIENT_TREATMENTS);
	    // check if history for this patient is already added .
	    historyCollection = historyRepository.findHistory(doctorId, locationId, hospitalId, patientId);
	    if (historyCollection != null) {
		// check if patient treatments are there in history.
		Collection<String> patientTreatments = null;
		if (historyCollection.getGeneralRecords() != null)
		    patientTreatments = CollectionUtils.collect(historyCollection.getGeneralRecords(), new BeanToPropertyValueTransformer("data"));
		if (patientTreatments != null) {
		    // check if this patient treatments id is already added into
		    // history.
		    if (!patientTreatments.contains(treatmentId)) {
			historyCollection.getGeneralRecords().add(0, patientTreatment);
		    }
		    // if no patient treatments is added into history then add
		    // it .
		} else {
		    if (historyCollection.getGeneralRecords() == null) {
			List<GeneralData> generalRecords = historyCollection.getGeneralRecords();
			generalRecords = new ArrayList<GeneralData>();
			generalRecords.add(0, patientTreatment);
			historyCollection.setGeneralRecords(generalRecords);
		    } else
			historyCollection.getGeneralRecords().add(0, patientTreatment);
		}
		historyCollection.setUpdatedTime(new Date());
	    } else {
		// if history not added for this patient.Create new history.
		historyCollection = new HistoryCollection(doctorId, locationId, hospitalId, patientId);
		historyCollection.setGeneralRecords(Arrays.asList(patientTreatment));
		historyCollection.setCreatedTime(new Date());
	    }
	    // finally add history into db.
	    historyRepository.save(historyCollection);

	    // modify patient treatment that it has been added to history.
	    patientTreatmentCollection = patientTreamentRepository.findOne(treatmentId);
	    if (patientTreatmentCollection != null) {
		patientTreatmentCollection.setUpdatedTime(new Date());
		patientTreatmentCollection.setInHistory(true);
		patientTreamentRepository.save(patientTreatmentCollection);
		response = new PatientTreatment();
		BeanUtil.map(patientTreatmentCollection, response);
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
    public HistoryDetailsResponse assignMedicalHistory(String diseaseId, String patientId, String doctorId, String hospitalId, String locationId) {
    	HistoryDetailsResponse response = null;
    	HistoryCollection historyCollection = null;
	try {
	    // check if history for this patient is already added .
	    historyCollection = historyRepository.findHistory(doctorId, locationId, hospitalId, patientId);
	    if (historyCollection != null) {
		// check if medicalHistory are there in history.
		List<String> medicalHistoryList = historyCollection.getMedicalhistory();
		if (medicalHistoryList != null) {
		    // check if this diseaseId id is already added into history.
		    if (!medicalHistoryList.contains(diseaseId)) {
			medicalHistoryList.add(diseaseId);
		    }
		    // if no medicalHistory is added into history then add it .
		} else {
		    medicalHistoryList = new ArrayList<String>();
		    medicalHistoryList.add(diseaseId);
		    historyCollection.setMedicalhistory(medicalHistoryList);
		}
		historyCollection.setUpdatedTime(new Date());
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
	    BeanUtil.map(historyCollection, response);
		List<String> medicalHistoryIds = historyCollection.getMedicalhistory();
		if (medicalHistoryIds != null && !medicalHistoryIds.isEmpty()) {
		    List<DiseaseListResponse> medicalHistory = getDiseasesByIds(medicalHistoryIds);
		    response.setMedicalhistory(medicalHistory);
		}

		List<String> familyHistoryIds = historyCollection.getFamilyhistory();
		if (familyHistoryIds != null && !familyHistoryIds.isEmpty()) {
		    List<DiseaseListResponse> familyHistory = getDiseasesByIds(familyHistoryIds);
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
    @Transactional
    public HistoryDetailsResponse assignFamilyHistory(String diseaseId, String patientId, String doctorId, String hospitalId, String locationId) {
    	HistoryDetailsResponse response = null;
    	HistoryCollection historyCollection = null;
	try {
	    // check if history for this patient is already added .
	    historyCollection = historyRepository.findHistory(doctorId, locationId, hospitalId, patientId);
	    if (historyCollection != null) {
		// check if familyHistory are there in history.
		List<String> familyHistoryList = historyCollection.getFamilyhistory();
		if (familyHistoryList != null) {
		    // check if this diseaseId id is already added into history.
		    if (!familyHistoryList.contains(diseaseId)) {
			familyHistoryList.add(diseaseId);
		    }
		    // if no familyHistory is added into history then add it .
		} else {
		    familyHistoryList = new ArrayList<String>();
		    familyHistoryList.add(diseaseId);
		    historyCollection.setFamilyhistory(familyHistoryList);
		}
		historyCollection.setUpdatedTime(new Date());
	    } else {// if history not added for this patient.Create new history.
		historyCollection = new HistoryCollection(doctorId, locationId, hospitalId, patientId);
		historyCollection.setCreatedTime(new Date());
		List<String> familyHistoryList = new ArrayList<String>();
		familyHistoryList.add(diseaseId);
		historyCollection.setFamilyhistory(familyHistoryList);
	    }
	    // finally add history into db.
	    historyRepository.save(historyCollection);
	    BeanUtil.map(historyCollection, response);
		List<String> medicalHistoryIds = historyCollection.getMedicalhistory();
		if (medicalHistoryIds != null && !medicalHistoryIds.isEmpty()) {
		    List<DiseaseListResponse> medicalHistory = getDiseasesByIds(medicalHistoryIds);
		    response.setMedicalhistory(medicalHistory);
		}

		List<String> familyHistoryIds = historyCollection.getFamilyhistory();
		if (familyHistoryIds != null && !familyHistoryIds.isEmpty()) {
		    List<DiseaseListResponse> familyHistory = getDiseasesByIds(familyHistoryIds);
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
    @Transactional
    public boolean addSpecialNotes(List<String> specialNotes, String patientId, String doctorId, String hospitalId, String locationId) {
	HistoryCollection historyCollection = null;
	List<NotesCollection> notesCollections = null;
	try {
	    // check if history for this patient is already added .
	    historyCollection = historyRepository.findHistory(doctorId, locationId, hospitalId, patientId);
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
    @Transactional
    public Records removeReports(String reportId, String patientId, String doctorId, String hospitalId, String locationId) {
    	Records response = null;
    	HistoryCollection historyCollection = null;
	RecordsCollection recordsCollection = null;
	try {
	    historyCollection = historyRepository.findHistory(doctorId, locationId, hospitalId, patientId);
	    if (historyCollection != null) {
		@SuppressWarnings("unchecked")
		List<String> reports = (List<String>) CollectionUtils.collect(historyCollection.getGeneralRecords(),
			new BeanToPropertyValueTransformer("data"));
		if (reports != null) {
		    if (reports.contains(reportId)) {
			historyCollection.getGeneralRecords().remove(reports.indexOf(reportId));
			if (checkIfHistoryRemovedCompletely(historyCollection)) {
			    historyRepository.delete(historyCollection.getId());
			} else {
			    historyCollection.setUpdatedTime(new Date());
			    historyRepository.save(historyCollection);
			}
			// modify records that it has been removed from history
			recordsCollection = recordsRepository.findOne(reportId);
			if (recordsCollection != null) {
			    recordsCollection.setInHistory(false);
			    recordsCollection.setUpdatedTime(new Date());
			    recordsRepository.save(recordsCollection);
			    response = new Records();
				BeanUtil.map(recordsCollection, response);
			}
		    } else {
			logger.warn("This reports is not found for this patient to remove.");
			throw new BusinessException(ServiceError.NoRecord, "This reports is not found for this patient to remove.");
		    }
		} else {
		    logger.warn("No reports found for this patient to remove.");
		    throw new BusinessException(ServiceError.NoRecord, "No reports found for this patient to remove.");
		}
	    } else {
		logger.warn("No History found for this patient.");
		throw new BusinessException(ServiceError.NoRecord, "No History found for this patient.");
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
    public ClinicalNotes removeClinicalNotes(String clinicalNotesId, String patientId, String doctorId, String hospitalId, String locationId) {
    	ClinicalNotes response = null;
    	HistoryCollection historyCollection = null;
	ClinicalNotesCollection clinicalNotesCollection = null;
	try {
	    historyCollection = historyRepository.findHistory(doctorId, locationId, hospitalId, patientId);
	    if (historyCollection != null) {
		@SuppressWarnings("unchecked")
		List<String> clinicalNotes = (List<String>) CollectionUtils.collect(historyCollection.getGeneralRecords(),
			new BeanToPropertyValueTransformer("data"));
		if (clinicalNotes != null) {
		    if (clinicalNotes.contains(clinicalNotesId)) {
			historyCollection.getGeneralRecords().remove(clinicalNotes.indexOf(clinicalNotesId));
			if (checkIfHistoryRemovedCompletely(historyCollection)) {
			    historyRepository.delete(historyCollection.getId());
			} else {
			    historyCollection.setUpdatedTime(new Date());
			    historyRepository.save(historyCollection);
			}
			// modify clinical notes that it has been removed from
			// history.
			clinicalNotesCollection = clinicalNotesRepository.findOne(clinicalNotesId);
			if (clinicalNotesCollection != null) {
			    clinicalNotesCollection.setInHistory(false);
			    clinicalNotesCollection.setUpdatedTime(new Date());
			    clinicalNotesRepository.save(clinicalNotesCollection);
			    response = new ClinicalNotes();
				BeanUtil.map(clinicalNotesCollection, response);

			}
		    } else {
			logger.warn("This clinicalNote is not found for this patient to remove.");
			throw new BusinessException(ServiceError.NoRecord, "This clinicalNote is not found for this patient to remove.");
		    }
		} else {
		    logger.warn("No clinicalNote found for this patient to remove.");
		    throw new BusinessException(ServiceError.NoRecord, "No clinicalNote found for this patient to remove.");
		}
	    } else {
		logger.warn("No History found for this patient.");
		throw new BusinessException(ServiceError.NoRecord, "No History found for this patient.");
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
    public Prescription removePrescription(String prescriptionId, String patientId, String doctorId, String hospitalId, String locationId) {
    	Prescription response = null;
    	HistoryCollection historyCollection = null;
	PrescriptionCollection prescriptionCollection = null;
	try {
	    historyCollection = historyRepository.findHistory(doctorId, locationId, hospitalId, patientId);
	    if (historyCollection != null) {
		@SuppressWarnings("unchecked")
		List<String> prescriptions = (List<String>) CollectionUtils.collect(historyCollection.getGeneralRecords(),
			new BeanToPropertyValueTransformer("data"));
		if (prescriptions != null) {
		    if (prescriptions.contains(prescriptionId)) {
			historyCollection.getGeneralRecords().remove(prescriptions.indexOf(prescriptionId));
			if (checkIfHistoryRemovedCompletely(historyCollection)) {
			    historyRepository.delete(historyCollection.getId());
			} else {
			    historyCollection.setUpdatedTime(new Date());
			    historyRepository.save(historyCollection);
			}
			// modify prescription that it has been removed from
			// history.
			prescriptionCollection = prescriptionRepository.findOne(prescriptionId);
			if (prescriptionCollection != null) {
			    prescriptionCollection.setInHistory(false);
			    prescriptionCollection.setUpdatedTime(new Date());
			    prescriptionRepository.save(prescriptionCollection);
			    response = new Prescription();
				List<TestAndRecordData> tests = prescriptionCollection.getDiagnosticTests();
				prescriptionCollection.setDiagnosticTests(null);
				BeanUtil.map(prescriptionCollection, response);
				if (prescriptionCollection.getItems() != null) {
				List<PrescriptionItemDetail> prescriptionItemDetailsList = new ArrayList<PrescriptionItemDetail>();
				for (PrescriptionItem prescriptionItem : prescriptionCollection.getItems()) {
				    PrescriptionItemDetail prescriptionItemDetails = new PrescriptionItemDetail();
				    BeanUtil.map(prescriptionItem, prescriptionItemDetails);
				    if (prescriptionItem.getDrugId() != null) {
					DrugCollection drugCollection = drugRepository.findOne(prescriptionItem.getDrugId());
					Drug drug = new Drug();
					if (drugCollection != null)
					    BeanUtil.map(drugCollection, drug);
					prescriptionItemDetails.setDrug(drug);
				    }
				    prescriptionItemDetailsList.add(prescriptionItemDetails);
				}
				response.setItems(prescriptionItemDetailsList);
			    }
				PatientVisitCollection patientVisitCollection = patientVisitRepository.findByPrescriptionId(response.getId());
				if (patientVisitCollection != null) response.setVisitId(patientVisitCollection.getId());
				
				if(tests != null && !tests.isEmpty()){
					List<TestAndRecordDataResponse> diagnosticTests = new ArrayList<TestAndRecordDataResponse>();
				    	for(TestAndRecordData data : tests){
				    		if(data.getTestId() != null){
				    			DiagnosticTestCollection diagnosticTestCollection = diagnosticTestRepository.findOne(data.getTestId());
					    		DiagnosticTest diagnosticTest = new DiagnosticTest();
					    		if(diagnosticTestCollection !=null){
					    			BeanUtil.map(diagnosticTestCollection, diagnosticTest);
					    		}
					    		diagnosticTests.add(new TestAndRecordDataResponse(diagnosticTest, data.getRecordId()));
				    		}
				    }
				    	response.setDiagnosticTests(diagnosticTests);
			    }
			   }
		    } else {
			logger.warn("This prescription is not found for this patient to remove.");
			throw new BusinessException(ServiceError.NoRecord, "This prescription is not found for this patient to remove.");
		    }
		} else {
		    logger.warn("No prescription found for this patient to remove.");
		    throw new BusinessException(ServiceError.NoRecord, "No prescription found for this patient to remove.");
		}
	    } else {
		logger.warn("No History found for this patient. ");
		throw new BusinessException(ServiceError.NoRecord, "No History found for this patient.");
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
    public HistoryDetailsResponse removeMedicalHistory(String diseaseId, String patientId, String doctorId, String hospitalId, String locationId) {
    	HistoryDetailsResponse response = null;
    	HistoryCollection historyCollection = null;
	try {
	    historyCollection = historyRepository.findHistory(doctorId, locationId, hospitalId, patientId);
	    if (historyCollection != null) {
		List<String> medicalHistory = historyCollection.getMedicalhistory();
		if (medicalHistory != null) {
		    if (medicalHistory.contains(diseaseId)) {
			medicalHistory.remove(diseaseId);
			if (checkIfHistoryRemovedCompletely(historyCollection)) {
			    historyRepository.delete(historyCollection.getId());
			} else {
			    historyCollection.setUpdatedTime(new Date());
			    historyRepository.save(historyCollection);
			    BeanUtil.map(historyCollection, response);
				List<String> medicalHistoryIds = historyCollection.getMedicalhistory();
				if (medicalHistoryIds != null && !medicalHistoryIds.isEmpty()) {
				    List<DiseaseListResponse> medicalHistoryList = getDiseasesByIds(medicalHistoryIds);
				    response.setMedicalhistory(medicalHistoryList);
				}

				List<String> familyHistoryIds = historyCollection.getFamilyhistory();
				if (familyHistoryIds != null && !familyHistoryIds.isEmpty()) {
				    List<DiseaseListResponse> familyHistory = getDiseasesByIds(familyHistoryIds);
				    response.setFamilyhistory(familyHistory);
				}
			}
		    } else {
			logger.warn("This disease is not found for this patient to remove.");
			throw new BusinessException(ServiceError.NoRecord, "This disease is not found for this patient to remove.");
		    }
		} else {
		    logger.warn("No disease found for this patient to remove.");
		    throw new BusinessException(ServiceError.NoRecord, "No disease found for this patient to remove.");
		}
	    } else {
		logger.warn("No History found for this patient.");
		throw new BusinessException(ServiceError.NoRecord, "No History found for this patient.");
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
    public HistoryDetailsResponse removeFamilyHistory(String diseaseId, String patientId, String doctorId, String hospitalId, String locationId) {
    	HistoryDetailsResponse response = null;
    	HistoryCollection historyCollection = null;
	try {
	    historyCollection = historyRepository.findHistory(doctorId, locationId, hospitalId, patientId);
	    if (historyCollection != null) {
		List<String> familyHistory = historyCollection.getFamilyhistory();
		if (familyHistory != null) {
		    if (familyHistory.contains(diseaseId)) {
			familyHistory.remove(diseaseId);
			if (checkIfHistoryRemovedCompletely(historyCollection)) {
			    historyRepository.delete(historyCollection.getId());
			} else {
			    historyCollection.setUpdatedTime(new Date());
			    historyRepository.save(historyCollection);
			    BeanUtil.map(historyCollection, response);
				List<String> medicalHistoryIds = historyCollection.getMedicalhistory();
				if (medicalHistoryIds != null && !medicalHistoryIds.isEmpty()) {
				    List<DiseaseListResponse> medicalHistory = getDiseasesByIds(medicalHistoryIds);
				    response.setMedicalhistory(medicalHistory);
				}

				List<String> familyHistoryIds = historyCollection.getFamilyhistory();
				if (familyHistoryIds != null && !familyHistoryIds.isEmpty()) {
				    List<DiseaseListResponse> familyHistoryList = getDiseasesByIds(familyHistoryIds);
				    response.setFamilyhistory(familyHistoryList);
				}
			}
		    } else {
			logger.warn("This disease is not found for this patient to remove.");
			throw new BusinessException(ServiceError.NoRecord, "This disease is not found for this patient to remove.");
		    }
		} else {
		    logger.warn("No disease found for this patient to remove.");
		    throw new BusinessException(ServiceError.NoRecord, "No disease found for this patient to remove.");
		}
	    } else {
		logger.warn("No History found for this patient.");
		throw new BusinessException(ServiceError.NoRecord, "No History found for this patient.");
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
	return response;
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
    @Transactional
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
	boolean[] discards = new boolean[2];
	discards[0] = false;
	try {
	    if (discarded)
		discards[1] = true;
	    long createdTimeStamp = Long.parseLong(updatedTime);

	    if (doctorId == null)
		diseasesCollections = new ArrayList<DiseasesCollection>();
	    else {
		if (locationId == null && hospitalId == null) {
		    if (size > 0)
			diseasesCollections = diseasesRepository.findCustomDiseases(doctorId, new Date(createdTimeStamp), discards,
				new PageRequest(page, size, Direction.ASC, "disease"));
		    else
			diseasesCollections = diseasesRepository.findCustomDiseases(doctorId, new Date(createdTimeStamp), discards,
				new Sort(Sort.Direction.ASC, "disease"));
		} else {
		    if (size > 0)
			diseasesCollections = diseasesRepository.findCustomDiseases(doctorId, locationId, hospitalId, new Date(createdTimeStamp), discards,
				new PageRequest(page, size, Direction.ASC, "disease"));
		    else
			diseasesCollections = diseasesRepository.findCustomDiseases(doctorId, locationId, hospitalId, new Date(createdTimeStamp), discards,
				new Sort(Sort.Direction.ASC, "disease"));
		}
	    }
	    if (diseasesCollections != null) {
		diseaseListResponses = new ArrayList<DiseaseListResponse>();
		for (DiseasesCollection diseasesCollection : diseasesCollections) {
		    DiseaseListResponse diseaseListResponse = new DiseaseListResponse(diseasesCollection.getId(), diseasesCollection.getDisease(),
			    diseasesCollection.getExplanation(), diseasesCollection.getDoctorId(), diseasesCollection.getLocationId(),
			    diseasesCollection.getHospitalId(), diseasesCollection.getDiscarded(), diseasesCollection.getCreatedTime(),
			    diseasesCollection.getUpdatedTime(), diseasesCollection.getCreatedBy());
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
	boolean[] discards = new boolean[2];
	discards[0] = false;
	try {
	    if (discarded)
		discards[1] = true;
	    long createdTimeStamp = Long.parseLong(updatedTime);

	    if (size > 0)
		diseasesCollections = diseasesRepository.findGlobalDiseases(new Date(createdTimeStamp), discards,
			new PageRequest(page, size, Direction.ASC, "disease"));
	    else
		diseasesCollections = diseasesRepository.findGlobalDiseases(new Date(createdTimeStamp), discards, new Sort(Sort.Direction.ASC, "disease"));

	    if (diseasesCollections != null) {
		diseaseListResponses = new ArrayList<DiseaseListResponse>();
		for (DiseasesCollection diseasesCollection : diseasesCollections) {
		    DiseaseListResponse diseaseListResponse = new DiseaseListResponse(diseasesCollection.getId(), diseasesCollection.getDisease(),
			    diseasesCollection.getExplanation(), diseasesCollection.getDoctorId(), diseasesCollection.getLocationId(),
			    diseasesCollection.getHospitalId(), diseasesCollection.getDiscarded(), diseasesCollection.getCreatedTime(),
			    diseasesCollection.getUpdatedTime(), diseasesCollection.getCreatedBy());
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
	boolean[] discards = new boolean[2];
	discards[0] = false;
	try {
	    if (discarded)
		discards[1] = true;
	    long createdTimeStamp = Long.parseLong(updatedTime);

	    if (doctorId == null) {
		if (size > 0)
		    diseasesCollections = diseasesRepository.findCustomGlobalDiseases(new Date(createdTimeStamp), discards,
			    new PageRequest(page, size, Direction.ASC, "disease"));
		else
		    diseasesCollections = diseasesRepository.findCustomGlobalDiseases(new Date(createdTimeStamp), discards,
			    new Sort(Sort.Direction.ASC, "disease"));
	    } else {
		if (locationId == null && hospitalId == null) {
		    if (size > 0)
			diseasesCollections = diseasesRepository.findCustomGlobalDiseases(doctorId, new Date(createdTimeStamp), discards,
				new PageRequest(page, size, Direction.ASC, "disease"));
		    else
			diseasesCollections = diseasesRepository.findCustomGlobalDiseases(doctorId, new Date(createdTimeStamp), discards,
				new Sort(Sort.Direction.ASC, "disease"));
		} else {
		    if (size > 0)
			diseasesCollections = diseasesRepository.findCustomGlobalDiseases(doctorId, locationId, hospitalId, new Date(createdTimeStamp),
				discards, new PageRequest(page, size, Direction.ASC, "disease"));
		    else
			diseasesCollections = diseasesRepository.findCustomGlobalDiseases(doctorId, locationId, hospitalId, new Date(createdTimeStamp),
				discards, new Sort(Sort.Direction.ASC, "disease"));
		}
	    }
	    if (diseasesCollections != null) {
		diseaseListResponses = new ArrayList<DiseaseListResponse>();
		for (DiseasesCollection diseasesCollection : diseasesCollections) {
		    DiseaseListResponse diseaseListResponse = new DiseaseListResponse(diseasesCollection.getId(), diseasesCollection.getDisease(),
			    diseasesCollection.getExplanation(), diseasesCollection.getDoctorId(), diseasesCollection.getLocationId(),
			    diseasesCollection.getHospitalId(), diseasesCollection.getDiscarded(), diseasesCollection.getCreatedTime(),
			    diseasesCollection.getUpdatedTime(), diseasesCollection.getCreatedBy());
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
    @Transactional
    public List<HistoryDetailsResponse> getPatientHistoryDetailsWithoutVerifiedOTP(String patientId, String doctorId, String hospitalId, String locationId,
	    List<String> historyFilter, int page, int size, String updatedTime) {
	List<HistoryDetailsResponse> response = null;
	try {
	    for (int i = 0; i < historyFilter.size(); i++) {
		historyFilter.set(i, historyFilter.get(i).toUpperCase());
	    }
	    long createdTime = Long.parseLong(updatedTime);
	    AggregationOperation matchForFilter = null;
	    Aggregation aggregation = null;
	    if (!historyFilter.contains(HistoryFilter.ALL.getFilter())) {
		matchForFilter = Aggregation.match(Criteria.where("generalRecords.dataType").in(historyFilter));
		if (size > 0)
		    aggregation = Aggregation.newAggregation(
			    Aggregation.match(Criteria.where("patientId").is(patientId).andOperator(Criteria.where("doctorId").is(doctorId),
				    Criteria.where("locationId").is(locationId), Criteria.where("hospitalId").is(hospitalId),
				    Criteria.where("updatedTime").gte(new Date(createdTime)))),
			    Aggregation.unwind("generalRecords"), matchForFilter, Aggregation.skip(page * size), Aggregation.limit(size),
			    Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));
		else
		    aggregation = Aggregation.newAggregation(
			    Aggregation.match(Criteria.where("patientId").is(patientId).andOperator(Criteria.where("doctorId").is(doctorId),
				    Criteria.where("locationId").is(locationId), Criteria.where("hospitalId").is(hospitalId),
				    Criteria.where("updatedTime").gte(new Date(createdTime)))),
			    Aggregation.unwind("generalRecords"), matchForFilter, Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));

	    } else {
		if (size > 0)
		    aggregation = Aggregation.newAggregation(
			    Aggregation.match(Criteria.where("patientId").is(patientId).andOperator(Criteria.where("doctorId").is(doctorId),
				    Criteria.where("locationId").is(locationId), Criteria.where("hospitalId").is(hospitalId),
				    Criteria.where("updatedTime").gte(new Date(createdTime)))),
			    Aggregation.unwind("generalRecords"), Aggregation.skip(page * size), Aggregation.limit(size),
			    Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));
		else
		    aggregation = Aggregation.newAggregation(
			    Aggregation.match(Criteria.where("patientId").is(patientId).andOperator(Criteria.where("doctorId").is(doctorId),
				    Criteria.where("locationId").is(locationId), Criteria.where("hospitalId").is(hospitalId),
				    Criteria.where("updatedTime").gte(new Date(createdTime)))),
			    Aggregation.unwind("generalRecords"), Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));

	    }
	    AggregationResults<History> groupResults = mongoTemplate.aggregate(aggregation, HistoryCollection.class, History.class);
	    List<History> general = groupResults.getMappedResults();
	    if (general != null) {

		response = new ArrayList<HistoryDetailsResponse>();
		for (History historyCollection : general) {
		    HistoryDetailsResponse historyDetailsResponse = new HistoryDetailsResponse();
		    BeanUtil.map(historyCollection, historyDetailsResponse);
		    if (historyCollection.getGeneralRecords() != null) {
			List<GeneralData> generalRecords = new ArrayList<GeneralData>();
			generalRecords.add(getGeneralData(historyCollection.getGeneralRecords()));
			historyDetailsResponse.setGeneralRecords(generalRecords);
		    }

		    List<String> medicalHistoryIds = historyCollection.getMedicalhistory();
		    if (medicalHistoryIds != null && !medicalHistoryIds.isEmpty()) {
			List<DiseaseListResponse> medicalHistory = getDiseasesByIds(medicalHistoryIds);
			historyDetailsResponse.setMedicalhistory(medicalHistory);
		    }

		    List<String> familyHistoryIds = historyCollection.getFamilyhistory();
		    if (familyHistoryIds != null && !familyHistoryIds.isEmpty()) {
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
			/*UserCollection userCollection = userRepository.findOne(clinicalNote.getDoctorId());
			if (userCollection != null)
			    clinicalNote.setDoctorName(userCollection.getFirstName());*/
			generalData.setData(clinicalNote);
			generalData.setDataType(HistoryFilter.CLINICAL_NOTES);
		    }
		    break;
		case PRESCRIPTIONS:
		    Prescription prescription = prescriptionServices.getPrescriptionById(id.getData().toString());
		    if (prescription != null) {
			/*UserCollection userCollection = userRepository.findOne(prescription.getDoctorId());
			if (userCollection != null)
			    prescription.setDoctorName(userCollection.getFirstName());*/
			generalData.setData(prescription);
			generalData.setDataType(HistoryFilter.PRESCRIPTIONS);
		    }
		    break;
		case REPORTS:
		    Records record = recordsService.getRecordById(id.getData().toString());
		    if (record != null) {
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
    @Transactional
    public List<HistoryDetailsResponse> getPatientHistoryDetailsWithVerifiedOTP(String patientId, String doctorId, String hospitalId, String locationId,
	    List<String> historyFilter, int page, int size, String updatedTime) {
	List<HistoryDetailsResponse> response = null;
	try {
	    for (int i = 0; i < historyFilter.size(); i++) {
		historyFilter.set(i, historyFilter.get(i).toUpperCase());
	    }
	    long createdTime = Long.parseLong(updatedTime);
	    AggregationOperation matchForFilter = null;
	    Aggregation aggregation = null;
	    if (!historyFilter.contains(HistoryFilter.ALL.getFilter())) {
		matchForFilter = Aggregation.match(Criteria.where("generalRecords.dataType").in(historyFilter));
		if (size > 0)
		    aggregation = Aggregation.newAggregation(
			    Aggregation.match(Criteria.where("patientId").is(patientId).and("updatedTime").gte(new Date(createdTime))),
			    Aggregation.unwind("generalRecords"), matchForFilter, Aggregation.skip(page * size), Aggregation.limit(size),
			    Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));
		else
		    aggregation = Aggregation.newAggregation(
			    Aggregation.match(Criteria.where("patientId").is(patientId).and("updatedTime").gte(new Date(createdTime))),
			    Aggregation.unwind("generalRecords"), matchForFilter, Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));
	    } else {
		if (size > 0)
		    aggregation = Aggregation.newAggregation(
			    Aggregation.match(Criteria.where("patientId").is(patientId).and("updatedTime").gte(new Date(createdTime))),
			    Aggregation.unwind("generalRecords"), Aggregation.skip(page * size), Aggregation.limit(size));
		else
		    aggregation = Aggregation.newAggregation(
			    Aggregation.match(Criteria.where("patientId").is(patientId).and("updatedTime").gte(new Date(createdTime))),
			    Aggregation.unwind("generalRecords"));
	    }

	    AggregationResults<History> groupResults = mongoTemplate.aggregate(aggregation, HistoryCollection.class, History.class);
	    List<History> general = groupResults.getMappedResults();
	    if (general != null) {
		response = new ArrayList<HistoryDetailsResponse>();
		for (History historyCollection : general) {
		    HistoryDetailsResponse historyDetailsResponse = new HistoryDetailsResponse();
		    BeanUtil.map(historyCollection, historyDetailsResponse);

		    if (historyCollection.getGeneralRecords() != null) {
			List<GeneralData> generalRecords = new ArrayList<GeneralData>();
			generalRecords.add(getGeneralData(historyCollection.getGeneralRecords()));
			historyDetailsResponse.setGeneralRecords(generalRecords);
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

    private GeneralData getGeneralData(GeneralData generalRecords) {
	GeneralData generalData = null;
	try {
	    switch (generalRecords.getDataType()) {
	    case CLINICAL_NOTES:
		ClinicalNotes clinicalNote = clinicalNotesService.getNotesById(generalRecords.getData().toString());
		if (clinicalNote != null) {
		    generalData = new GeneralData();
		    generalData.setData(clinicalNote);
		    generalData.setDataType(HistoryFilter.CLINICAL_NOTES);
		}
		break;
	    case PRESCRIPTIONS:
		Prescription prescription = prescriptionServices.getPrescriptionById(generalRecords.getData().toString());
		if (prescription != null) {
		    generalData = new GeneralData();
		    generalData.setData(prescription);
		    generalData.setDataType(HistoryFilter.PRESCRIPTIONS);
		}
		break;
	    case REPORTS:
		Records record = recordsService.getRecordById(generalRecords.getData().toString());
		if (record != null) {
		    generalData = new GeneralData();
		    generalData.setData(record);
		    generalData.setDataType(HistoryFilter.REPORTS);
		}
		break;
	    case PATIENT_TREATMENTS:
		PatientTreatmentResponse patientTreatment = patientTreatmentServices.getPatientTreatmentById(generalRecords.getData().toString());
		if (patientTreatment != null) {
		    generalData = new GeneralData();
		    generalData.setData(patientTreatment);
		    generalData.setDataType(HistoryFilter.PATIENT_TREATMENTS);
		}
	    default:
		break;
	    }

	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
	return generalData;
    }

    @Override
    @Transactional
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
    @Transactional
    public Integer getHistoryCount(String doctorId, String patientId, String locationId, String hospitalId, boolean isOTPVerified) {
	Integer historyCount = 0;
	try {
	    List<HistoryCollection> historyCollections = null;
	    if (isOTPVerified)
		historyCollections = historyRepository.findHistory(patientId);
	    else {
		HistoryCollection historyCollection = historyRepository.findHistory(doctorId, locationId, hospitalId, patientId);
		if (historyCollection != null) {
		    historyCollections = new ArrayList<HistoryCollection>();
		    historyCollections.add(historyCollection);
		}
	    }
	    if (historyCollections != null) {
		for (HistoryCollection historyCollection : historyCollections) {
		    if (historyCollection.getGeneralRecords() != null && !historyCollection.getGeneralRecords().isEmpty()) {
			historyCount = historyCount + (historyCollection.getGeneralRecords().isEmpty() ? 0 : historyCollection.getGeneralRecords().size());
		    }
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
    @Transactional
    public boolean handleMedicalHistory(MedicalHistoryHandler request) {
	HistoryCollection historyCollection = null;
	boolean response = false;
	try {
	    historyCollection = historyRepository.findHistory(request.getDoctorId(), request.getLocationId(), request.getHospitalId(), request.getPatientId());
	    if (historyCollection != null) {
		List<String> medicalHistoryList = historyCollection.getMedicalhistory();
		if (medicalHistoryList != null && !medicalHistoryList.isEmpty()) {
		    if (request.getAddDiseases() != null)
			medicalHistoryList.addAll(request.getAddDiseases());
		    medicalHistoryList = new ArrayList<String>(new LinkedHashSet<String>(medicalHistoryList));
		    if (request.getRemoveDiseases() != null)
			medicalHistoryList.removeAll(request.getRemoveDiseases());
		    historyCollection.setMedicalhistory(medicalHistoryList);
		} else {
		    medicalHistoryList = new ArrayList<String>();
		    if (request.getAddDiseases() != null)
			medicalHistoryList.addAll(request.getAddDiseases());
		    if (request.getRemoveDiseases() != null)
			medicalHistoryList.removeAll(request.getRemoveDiseases());
		    historyCollection.setMedicalhistory(medicalHistoryList);
		}
	    } else {
		historyCollection = new HistoryCollection(request.getDoctorId(), request.getLocationId(), request.getHospitalId(), request.getPatientId());
		historyCollection.setCreatedTime(new Date());
		List<String> medicalHistoryList = new ArrayList<String>();
		if (request.getAddDiseases() != null)
		    medicalHistoryList.addAll(request.getAddDiseases());
		if (request.getRemoveDiseases() != null)
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
    @Transactional
    public boolean handleFamilyHistory(MedicalHistoryHandler request) {
	HistoryCollection historyCollection = null;
	boolean response = false;
	try {
	    historyCollection = historyRepository.findHistory(request.getDoctorId(), request.getLocationId(), request.getHospitalId(), request.getPatientId());
	    if (historyCollection != null) {
		List<String> familyHistoryList = historyCollection.getFamilyhistory();
		if (familyHistoryList != null && !familyHistoryList.isEmpty()) {
		    if (request.getAddDiseases() != null)
			familyHistoryList.addAll(request.getAddDiseases());
		    familyHistoryList = new ArrayList<String>(new LinkedHashSet<String>(familyHistoryList));
		    if (request.getRemoveDiseases() != null)
			familyHistoryList.removeAll(request.getRemoveDiseases());
		    historyCollection.setFamilyhistory(familyHistoryList);
		} else {
		    familyHistoryList = new ArrayList<String>();
		    if (request.getAddDiseases() != null)
			familyHistoryList.addAll(request.getAddDiseases());
		    if (request.getRemoveDiseases() != null)
			familyHistoryList.removeAll(request.getRemoveDiseases());
		    historyCollection.setFamilyhistory(familyHistoryList);
		}
	    } else {
		historyCollection = new HistoryCollection(request.getDoctorId(), request.getLocationId(), request.getHospitalId(), request.getPatientId());
		historyCollection.setCreatedTime(new Date());
		List<String> familyHistoryList = new ArrayList<String>();
		if (request.getAddDiseases() != null)
		    familyHistoryList.addAll(request.getAddDiseases());
		if (request.getRemoveDiseases() != null)
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
    @Transactional
    public HistoryDetailsResponse getMedicalAndFamilyHistory(String patientId, String doctorId, String hospitalId, String locationId) {
	HistoryDetailsResponse response = null;
	HistoryCollection historyCollection = null;

	try {
	    historyCollection = historyRepository.findHistory(doctorId, locationId, hospitalId, patientId);
	    if (historyCollection != null) {
		response = new HistoryDetailsResponse();
		BeanUtil.map(historyCollection, response);
		List<String> medicalHistoryIds = historyCollection.getMedicalhistory();
		if (medicalHistoryIds != null && !medicalHistoryIds.isEmpty()) {
		    List<DiseaseListResponse> medicalHistory = getDiseasesByIds(medicalHistoryIds);
		    response.setMedicalhistory(medicalHistory);
		}

		List<String> familyHistoryIds = historyCollection.getFamilyhistory();
		if (familyHistoryIds != null && !familyHistoryIds.isEmpty()) {
		    List<DiseaseListResponse> familyHistory = getDiseasesByIds(familyHistoryIds);
		    response.setFamilyhistory(familyHistory);
		}
		if ((medicalHistoryIds == null || medicalHistoryIds.isEmpty()) && (familyHistoryIds == null || familyHistoryIds.isEmpty()))
		    response = null;
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
    public boolean mailMedicalData(MedicalData medicalData) {
	boolean response = false;
	List<MailAttachment> mailAttachments = null;
	try {
	    String doctorId = medicalData.getDoctorId();
	    String locationId = medicalData.getLocationId();
	    String hospitalId = medicalData.getHospitalId();
	    String emailAddress = medicalData.getEmailAddress();
	    mailAttachments = new ArrayList<MailAttachment>();
	    MailResponse mailResponse = null;
	    for (MailData mailData : medicalData.getMailDataList()) {
		switch (mailData.getMailType()) {
		case CLINICAL_NOTE:
			mailResponse = clinicalNotesService.getClinicalNotesMailData(mailData.getId(), doctorId, locationId, hospitalId);
			mailAttachments.add(mailResponse.getMailAttachment());
		    break;
		case PRESCRIPTION:
			mailResponse = prescriptionServices.getPrescriptionMailData(mailData.getId(), doctorId, locationId, hospitalId);
			mailAttachments.add(mailResponse.getMailAttachment());
		    break;
		case REPORT:
			mailResponse = recordsService.getRecordMailData(mailData.getId(), doctorId, locationId, hospitalId);
			mailAttachments.add(mailResponse.getMailAttachment());
		    break;
		}
	    }
	    if(mailResponse != null){
	    	String body = mailBodyGenerator.generateEMREmailBody(mailResponse.getPatientName(), mailResponse.getDoctorName(), mailResponse.getClinicName(), mailResponse.getClinicAddress(), mailResponse.getMailRecordCreatedDate(), "Medical Data", "emrRecordTemplate.vm");
		    mailService.sendEmailMultiAttach(emailAddress, mailResponse.getDoctorName()+" sent you a Medical Data", body, mailAttachments);
		    response = true;
	    }
	} catch (Exception e) {
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
	return response;
    }

    @Override
    @Transactional
    public boolean addVisitsToHistory(String visitId, String patientId, String doctorId, String hospitalId, String locationId) {

	PatientVisitCollection patientVisitCollection = null;
	try {
	    patientVisitCollection = patientVisitRepository.findOne(visitId);
	    if (patientVisitCollection != null) {
		if (patientVisitCollection.getClinicalNotesId() != null) {
		    for (String clinicalNotesId : patientVisitCollection.getClinicalNotesId()) {
			addClinicalNotesToHistory(clinicalNotesId, patientId, doctorId, hospitalId, locationId);
		    }
		}
		if (patientVisitCollection.getPrescriptionId() != null) {
		    for (String prescriptionId : patientVisitCollection.getPrescriptionId()) {
			addPrescriptionToHistory(prescriptionId, patientId, doctorId, hospitalId, locationId);
		    }
		}
		if (patientVisitCollection.getRecordId() != null) {
		    for (String recordId : patientVisitCollection.getRecordId()) {
			addReportToHistory(recordId, patientId, doctorId, hospitalId, locationId);
		    }
		}
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());

	}
	return true;

    }

    @Override
    @Transactional
    public boolean removeVisits(String visitId, String patientId, String doctorId, String hospitalId, String locationId) {
	PatientVisitCollection patientVisitCollection = null;
	try {
	    patientVisitCollection = patientVisitRepository.findOne(visitId);
	    if (patientVisitCollection != null) {
		if (patientVisitCollection.getClinicalNotesId() != null) {
		    for (String clinicalNotesId : patientVisitCollection.getClinicalNotesId()) {
			removeClinicalNotes(clinicalNotesId, patientId, doctorId, hospitalId, locationId);
		    }
		}
		if (patientVisitCollection.getPrescriptionId() != null) {
		    for (String prescriptionId : patientVisitCollection.getPrescriptionId()) {
			removePrescription(prescriptionId, patientId, doctorId, hospitalId, locationId);
		    }
		}
		if (patientVisitCollection.getRecordId() != null) {
		    for (String recordId : patientVisitCollection.getRecordId()) {
			removeReports(recordId, patientId, doctorId, hospitalId, locationId);
		    }
		}
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());

	}
	return true;
    }

    @Override
    @Transactional
    public List<HistoryDetailsResponse> getMultipleData(String patientId, String doctorId, String hospitalId, String locationId, String updatedTime,
	    Boolean inHistory, Boolean discarded) {
	List<HistoryDetailsResponse> response = null;
	try {

		Boolean isOTPVerified = otpService.checkOTPVerified(doctorId, locationId, hospitalId, patientId);
	    List<Prescription> prescriptions = prescriptionServices.getPrescriptions(0, 0, doctorId, hospitalId, locationId, patientId, updatedTime, isOTPVerified,	discarded, inHistory);
	    
	    List<ClinicalNotes> clinicalNotes = clinicalNotesService.getClinicalNotes(0, 0, doctorId, locationId, hospitalId, patientId, updatedTime, isOTPVerified, discarded, inHistory);
	    
	    List<Records> records = recordsService.getRecords(0, 0, doctorId, hospitalId, locationId, patientId, updatedTime, isOTPVerified, discarded, inHistory);

	    if (prescriptions != null || clinicalNotes != null || records != null) {

		response = new ArrayList<HistoryDetailsResponse>();
		if (prescriptions != null)
		    for (Prescription prescription : prescriptions) {
			HistoryDetailsResponse historyDetailsResponse = new HistoryDetailsResponse();
			BeanUtil.map(prescription, historyDetailsResponse);
			GeneralData generalData = new GeneralData();
			generalData.setData(prescription);
			generalData.setDataType(HistoryFilter.PRESCRIPTIONS);
			List<GeneralData> generalRecords = new ArrayList<GeneralData>();
			generalRecords.add(generalData);
			historyDetailsResponse.setGeneralRecords(generalRecords);
			response.add(historyDetailsResponse);
		    }

		if (clinicalNotes != null)
		    for (ClinicalNotes clinicalNote : clinicalNotes) {
			HistoryDetailsResponse historyDetailsResponse = new HistoryDetailsResponse();
			BeanUtil.map(clinicalNote, historyDetailsResponse);
			GeneralData generalData = new GeneralData();
			generalData.setData(clinicalNote);
			generalData.setDataType(HistoryFilter.CLINICAL_NOTES);
			List<GeneralData> generalRecords = new ArrayList<GeneralData>();
			generalRecords.add(generalData);
			historyDetailsResponse.setGeneralRecords(generalRecords);
			response.add(historyDetailsResponse);
		    }

		if (records != null)
		    for (Records record : records) {
			HistoryDetailsResponse historyDetailsResponse = new HistoryDetailsResponse();
			BeanUtil.map(record, historyDetailsResponse);
			GeneralData generalData = new GeneralData();
			generalData.setData(record);
			generalData.setDataType(HistoryFilter.REPORTS);
			List<GeneralData> generalRecords = new ArrayList<GeneralData>();
			generalRecords.add(generalData);
			historyDetailsResponse.setGeneralRecords(generalRecords);
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
    @Transactional
    public List<HistoryDetailsResponse> getPatientHistory(String patientId, List<String> historyFilter, int page, int size, String updatedTime) {
	List<HistoryDetailsResponse> response = null;
	try {
	    for (int i = 0; i < historyFilter.size(); i++) {
		historyFilter.set(i, historyFilter.get(i).toUpperCase());
	    }
	    long createdTime = Long.parseLong(updatedTime);
	    AggregationOperation matchForFilter = null;
	    Aggregation aggregation = null;
	    if (!historyFilter.contains(HistoryFilter.ALL.getFilter())) {
		matchForFilter = Aggregation.match(Criteria.where("generalRecords.dataType").in(historyFilter));
		if (size > 0)
		    aggregation = Aggregation.newAggregation(
			    Aggregation.match(Criteria.where("patientId").is(patientId).andOperator(Criteria.where("updatedTime").gte(new Date(createdTime)))),
			    Aggregation.unwind("generalRecords"), matchForFilter, Aggregation.skip(page * size), Aggregation.limit(size),
			    Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));
		else
		    aggregation = Aggregation.newAggregation(
			    Aggregation.match(Criteria.where("patientId").is(patientId).andOperator(Criteria.where("updatedTime").gte(new Date(createdTime)))),
			    Aggregation.unwind("generalRecords"), matchForFilter, Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));

	    } else {
		if (size > 0)
		    aggregation = Aggregation.newAggregation(
			    Aggregation.match(Criteria.where("patientId").is(patientId).andOperator(Criteria.where("updatedTime").gte(new Date(createdTime)))),
			    Aggregation.unwind("generalRecords"), Aggregation.skip(page * size), Aggregation.limit(size),
			    Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));
		else
		    aggregation = Aggregation.newAggregation(
			    Aggregation.match(Criteria.where("patientId").is(patientId).andOperator(Criteria.where("updatedTime").gte(new Date(createdTime)))),
			    Aggregation.unwind("generalRecords"), Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));

	    }
	    AggregationResults<History> groupResults = mongoTemplate.aggregate(aggregation, HistoryCollection.class, History.class);
	    List<History> general = groupResults.getMappedResults();
	    if (general != null) {

		response = new ArrayList<HistoryDetailsResponse>();
		for (History historyCollection : general) {
		    HistoryDetailsResponse historyDetailsResponse = new HistoryDetailsResponse();
		    BeanUtil.map(historyCollection, historyDetailsResponse);
		    if (historyCollection.getGeneralRecords() != null) {
			List<GeneralData> generalRecords = new ArrayList<GeneralData>();
			generalRecords.add(getGeneralData(historyCollection.getGeneralRecords()));
			historyDetailsResponse.setGeneralRecords(generalRecords);
		    }

		    List<String> medicalHistoryIds = historyCollection.getMedicalhistory();
		    if (medicalHistoryIds != null && !medicalHistoryIds.isEmpty()) {
			List<DiseaseListResponse> medicalHistory = getDiseasesByIds(medicalHistoryIds);
			historyDetailsResponse.setMedicalhistory(medicalHistory);
		    }

		    List<String> familyHistoryIds = historyCollection.getFamilyhistory();
		    if (familyHistoryIds != null && !familyHistoryIds.isEmpty()) {
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
    @Transactional
    public PatientTreatment removePatientTreatment(String treatmentId, String patientId, String doctorId, String hospitalId, String locationId) {
    	PatientTreatment response = null;
    	HistoryCollection historyCollection = null;
	PatientTreatmentCollection patientTreatmentCollection;
	try {
	    historyCollection = historyRepository.findHistory(doctorId, locationId, hospitalId, patientId);
	    if (historyCollection != null) {
		@SuppressWarnings("unchecked")
		List<String> patientTreatments = (List<String>) CollectionUtils.collect(historyCollection.getGeneralRecords(),
			new BeanToPropertyValueTransformer("data"));
		if (patientTreatments != null) {
		    if (patientTreatments.contains(treatmentId)) {
			historyCollection.getGeneralRecords().remove(patientTreatments.indexOf(treatmentId));
			if (checkIfHistoryRemovedCompletely(historyCollection)) {
			    historyRepository.delete(historyCollection.getId());
			} else {
			    historyCollection.setUpdatedTime(new Date());
			    historyRepository.save(historyCollection);
			}
			// modify patient treatment that it has been removed
			// from
			// history.
			patientTreatmentCollection = patientTreamentRepository.findOne(treatmentId);
			if (patientTreatmentCollection != null) {
			    patientTreatmentCollection.setInHistory(false);
			    patientTreatmentCollection.setUpdatedTime(new Date());
			    patientTreamentRepository.save(patientTreatmentCollection);
			    response = new PatientTreatment();
			    BeanUtil.map(patientTreatmentCollection, response);
			}
		    } else {
			logger.warn("This patient treatment is not found for this patient to remove.");
			throw new BusinessException(ServiceError.NoRecord, "This patient treatment is not found for this patient to remove.");
		    }
		} else {
		    logger.warn("No patient treatment found for this patient to remove.");
		    throw new BusinessException(ServiceError.NoRecord, "No patient treatment found for this patient to remove.");
		}
	    } else {
		logger.warn("No History found for this patient. ");
		throw new BusinessException(ServiceError.NoRecord, "No History found for this patient.");
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
	return response;
    }
}
