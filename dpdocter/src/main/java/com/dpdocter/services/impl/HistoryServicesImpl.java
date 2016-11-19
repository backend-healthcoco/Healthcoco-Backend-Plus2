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
import org.bson.types.ObjectId;
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
import com.dpdocter.beans.DrugsAndAllergies;
import com.dpdocter.beans.GeneralData;
import com.dpdocter.beans.History;
import com.dpdocter.beans.MailAttachment;
import com.dpdocter.beans.MailData;
import com.dpdocter.beans.MedicalData;
import com.dpdocter.beans.MedicalHistoryHandler;
import com.dpdocter.beans.PersonalHistory;
import com.dpdocter.beans.Prescription;
import com.dpdocter.beans.PrescriptionItem;
import com.dpdocter.beans.PrescriptionItemDetail;
import com.dpdocter.beans.Records;
import com.dpdocter.beans.TestAndRecordData;
import com.dpdocter.beans.Treatment;
import com.dpdocter.beans.TreatmentService;
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
import com.dpdocter.collections.TreatmentServicesCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.enums.HistoryFilter;
import com.dpdocter.enums.HistoryType;
import com.dpdocter.enums.Range;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.ClinicalNotesRepository;
import com.dpdocter.repository.DiagnosticTestRepository;
import com.dpdocter.repository.DiseasesRepository;
import com.dpdocter.repository.DrugRepository;
import com.dpdocter.repository.HistoryRepository;
import com.dpdocter.repository.NotesRepository;
import com.dpdocter.repository.PatientTreamentRepository;
import com.dpdocter.repository.PatientVisitRepository;
import com.dpdocter.repository.PrescriptionRepository;
import com.dpdocter.repository.RecordsRepository;
import com.dpdocter.repository.TreatmentServicesRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.request.DiseaseAddEditRequest;
import com.dpdocter.request.DrugsAndAllergiesAddRequest;
import com.dpdocter.request.PersonalHistoryAddRequest;
import com.dpdocter.response.DiseaseAddEditResponse;
import com.dpdocter.response.DiseaseListResponse;
import com.dpdocter.response.HistoryDetailsResponse;
import com.dpdocter.response.MailResponse;
import com.dpdocter.response.PatientTreatmentResponse;
import com.dpdocter.response.TestAndRecordDataResponse;
import com.dpdocter.response.TreatmentResponse;
import com.dpdocter.services.ClinicalNotesService;
import com.dpdocter.services.HistoryServices;
import com.dpdocter.services.MailBodyGenerator;
import com.dpdocter.services.MailService;
import com.dpdocter.services.OTPService;
import com.dpdocter.services.PatientTreatmentServices;
import com.dpdocter.services.PrescriptionServices;
import com.dpdocter.services.RecordsService;
import com.squareup.okhttp.internal.spdy.ErrorCode;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;

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
    private PatientVisitRepository patientVisitRepository;

    @Autowired
    private MailBodyGenerator mailBodyGenerator;
    
    @Autowired
    private TreatmentServicesRepository treatmentServicesRepository;

    @Override
    @Transactional
    public List<DiseaseAddEditResponse> addDiseases(List<DiseaseAddEditRequest> request) {
	List<DiseaseAddEditResponse> response = new ArrayList<DiseaseAddEditResponse>();
	try {
	    for (DiseaseAddEditRequest addEditRequest : request) {
	    	addEditRequest.setCreatedTime(new Date());
	    	DiseasesCollection diseasesCollection = new DiseasesCollection();
	    	BeanUtil.map(addEditRequest, diseasesCollection);
	    	if(!DPDoctorUtils.anyStringEmpty(addEditRequest.getDoctorId())){
				UserCollection userCollection = userRepository.findOne(new ObjectId(addEditRequest.getDoctorId()));
			    if (userCollection != null) {
			    	diseasesCollection.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "") + userCollection.getFirstName());
			    }
			}else{
				diseasesCollection.setCreatedBy("ADMIN");
			}
	    	diseasesCollection = diseasesRepository.save(diseasesCollection);
	    	DiseaseAddEditResponse addEditResponse = new DiseaseAddEditResponse();
	    	BeanUtil.map(diseasesCollection, addEditResponse);
	    	response.add(addEditResponse);
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
	    DiseasesCollection oldDisease = diseasesRepository.findOne(new ObjectId(request.getId()));
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
	    disease = diseasesRepository.findOne(new ObjectId(diseaseId));
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
		ObjectId patientObjectId = null, doctorObjectId = null, locationObjectId = null , hospitalObjectId= null;
		if(!DPDoctorUtils.anyStringEmpty(patientId))patientObjectId = new ObjectId(patientId);
		if(!DPDoctorUtils.anyStringEmpty(doctorId))doctorObjectId = new ObjectId(doctorId);
    	if(!DPDoctorUtils.anyStringEmpty(locationId))locationObjectId = new ObjectId(locationId);
    	if(!DPDoctorUtils.anyStringEmpty(hospitalId))hospitalObjectId = new ObjectId(hospitalId);
    	
	    GeneralData report = new GeneralData();
	    report.setData(reportId);
	    report.setDataType(HistoryFilter.REPORTS);
	    historyCollection = historyRepository.findHistory(doctorObjectId, locationObjectId, hospitalObjectId, patientObjectId);
	    if (historyCollection != null) {

		Collection<String> reports = null;
		if (historyCollection.getGeneralRecords() != null)
		    reports = CollectionUtils.collect(historyCollection.getGeneralRecords(), new BeanToPropertyValueTransformer("data"));
		if (reports != null) {
		    if (!reports.contains(reportId)) {
			historyCollection.getGeneralRecords().add(0, report);
		    }
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
		historyCollection = new HistoryCollection(doctorObjectId, locationObjectId, hospitalObjectId, patientObjectId);
		historyCollection.setGeneralRecords(Arrays.asList(report));
		historyCollection.setCreatedTime(new Date());
	    }
	    historyRepository.save(historyCollection);

	    // modify record that it has been added to history.
	    recordsCollection = recordsRepository.findOne(new ObjectId(reportId));
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
		
		ObjectId patientObjectId = null, doctorObjectId = null, locationObjectId = null , hospitalObjectId= null;
		if(!DPDoctorUtils.anyStringEmpty(patientId))patientObjectId = new ObjectId(patientId);
		if(!DPDoctorUtils.anyStringEmpty(doctorId))doctorObjectId = new ObjectId(doctorId);
    	if(!DPDoctorUtils.anyStringEmpty(locationId))locationObjectId = new ObjectId(locationId);
    	if(!DPDoctorUtils.anyStringEmpty(hospitalId))hospitalObjectId = new ObjectId(hospitalId);
    	
	    GeneralData clinicalNote = new GeneralData();
	    clinicalNote.setData(clinicalNotesId);
	    clinicalNote.setDataType(HistoryFilter.CLINICAL_NOTES);
	    historyCollection = historyRepository.findHistory(doctorObjectId, locationObjectId, hospitalObjectId, patientObjectId);
	    if (historyCollection != null) {
		Collection<String> clinicalNotes = null;
		if (historyCollection.getGeneralRecords() != null)
		    clinicalNotes = CollectionUtils.collect(historyCollection.getGeneralRecords(), new BeanToPropertyValueTransformer("data"));
		if (clinicalNotes != null) {
		    if (!clinicalNotes.contains(clinicalNotesId)) {
			historyCollection.getGeneralRecords().add(0, clinicalNote);
		    }
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
	    } else {
	    	// if history not added for this patient.Create new history.
		historyCollection = new HistoryCollection(doctorObjectId, locationObjectId, hospitalObjectId, patientObjectId);
		historyCollection.setGeneralRecords(Arrays.asList(clinicalNote));
		historyCollection.setCreatedTime(new Date());
	    }
	    // finally add history into db.
	    historyRepository.save(historyCollection);

	    // modify clinical notes that it has been added to history.
	    clinicalNotesCollection = clinicalNotesRepository.findOne(new ObjectId(clinicalNotesId));
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
		ObjectId patientObjectId = null, doctorObjectId = null, locationObjectId = null , hospitalObjectId= null;
		if(!DPDoctorUtils.anyStringEmpty(patientId))patientObjectId = new ObjectId(patientId);
		if(!DPDoctorUtils.anyStringEmpty(doctorId))doctorObjectId = new ObjectId(doctorId);
    	if(!DPDoctorUtils.anyStringEmpty(locationId))locationObjectId = new ObjectId(locationId);
    	if(!DPDoctorUtils.anyStringEmpty(hospitalId))hospitalObjectId = new ObjectId(hospitalId);
    	
	    GeneralData prescription = new GeneralData();
	    prescription.setData(prescriptionId);
	    prescription.setDataType(HistoryFilter.PRESCRIPTIONS);
	    historyCollection = historyRepository.findHistory(doctorObjectId, locationObjectId, hospitalObjectId, patientObjectId);
	    if (historyCollection != null) {
		Collection<String> prescriptions = null;
		if (historyCollection.getGeneralRecords() != null)
		    prescriptions = CollectionUtils.collect(historyCollection.getGeneralRecords(), new BeanToPropertyValueTransformer("data"));
		if (prescriptions != null) {
		    if (!prescriptions.contains(prescriptionId)) {
			historyCollection.getGeneralRecords().add(0, prescription);
		    }
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
	    } else {
	    	// if history not added for this patient.Create new history.
	    	historyCollection = new HistoryCollection(doctorObjectId, locationObjectId, hospitalObjectId, patientObjectId);
			historyCollection.setGeneralRecords(Arrays.asList(prescription));
			historyCollection.setCreatedTime(new Date());
	    }
	    historyRepository.save(historyCollection);

	    prescriptionCollection = prescriptionRepository.findOne(new ObjectId(prescriptionId));
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
			DrugCollection drugCollection = drugRepository.findOne(new ObjectId(prescriptionItem.getDrugId()));
			Drug drug = new Drug();
			if (drugCollection != null)BeanUtil.map(drugCollection, drug);
			prescriptionItemDetails.setDrug(drug);
		    }
		    prescriptionItemDetailsList.add(prescriptionItemDetails);
		}
		response.setItems(prescriptionItemDetailsList);
	    }
		PatientVisitCollection patientVisitCollection = patientVisitRepository.findByPrescriptionId(prescriptionCollection.getId());
		if (patientVisitCollection != null) response.setVisitId(patientVisitCollection.getId().toString());
		
		if(tests != null && !tests.isEmpty()){
			List<TestAndRecordDataResponse> diagnosticTests = new ArrayList<TestAndRecordDataResponse>();
		    	for(TestAndRecordData data : tests){
		    		if(data.getTestId() != null){
		    			DiagnosticTestCollection diagnosticTestCollection = diagnosticTestRepository.findOne(new ObjectId(data.getTestId()));
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

    @SuppressWarnings("unchecked")
	@Override
    @Transactional
    public PatientTreatmentResponse addPatientTreatmentToHistory(String treatmentId, String patientId, String doctorId, String hospitalId, String locationId) {
    	PatientTreatmentResponse response = null;
    	HistoryCollection historyCollection = null;
	    PatientTreatmentCollection patientTreatmentCollection = null;
	try {
		ObjectId patientObjectId = null, doctorObjectId = null, locationObjectId = null , hospitalObjectId= null;
		if(!DPDoctorUtils.anyStringEmpty(patientId))patientObjectId = new ObjectId(patientId);
		if(!DPDoctorUtils.anyStringEmpty(doctorId))doctorObjectId = new ObjectId(doctorId);
    	if(!DPDoctorUtils.anyStringEmpty(locationId))locationObjectId = new ObjectId(locationId);
    	if(!DPDoctorUtils.anyStringEmpty(hospitalId))hospitalObjectId = new ObjectId(hospitalId);
    	
	    GeneralData patientTreatment = new GeneralData();
	    patientTreatment.setData(treatmentId);
	    patientTreatment.setDataType(HistoryFilter.PATIENT_TREATMENTS);
	    // check if history for this patient is already added .
	    historyCollection = historyRepository.findHistory(doctorObjectId, locationObjectId, hospitalObjectId, patientObjectId);
	    if (historyCollection != null) {
		// check if patient treatments are there in history.
		Collection<String> patientTreatments = null;
		if (historyCollection.getGeneralRecords() != null)
		    patientTreatments = CollectionUtils.collect(historyCollection.getGeneralRecords(), new BeanToPropertyValueTransformer("data"));
		if (patientTreatments != null) {
		    if (!patientTreatments.contains(treatmentId)) {
			historyCollection.getGeneralRecords().add(0, patientTreatment);
		    }
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
	    	historyCollection = new HistoryCollection(doctorObjectId, locationObjectId, hospitalObjectId, patientObjectId);
		historyCollection.setGeneralRecords(Arrays.asList(patientTreatment));
		historyCollection.setCreatedTime(new Date());
	    }
	    // finally add history into db.
	    historyRepository.save(historyCollection);

	    // modify patient treatment that it has been added to history.
	    patientTreatmentCollection = patientTreamentRepository.findOne(new ObjectId(treatmentId));
	    if (patientTreatmentCollection != null) {
			patientTreatmentCollection.setUpdatedTime(new Date());
			patientTreatmentCollection.setInHistory(true);
			patientTreamentRepository.save(patientTreatmentCollection);
			List<TreatmentResponse> treatmentResponses = new ArrayList<TreatmentResponse>();
		    for (Treatment treatment : patientTreatmentCollection.getTreatments()) {
		    
		    	TreatmentResponse treatmentResponse = new TreatmentResponse();
				BeanUtil.map(treatment, treatmentResponse);
				TreatmentServicesCollection treatmentServicesCollection = treatmentServicesRepository.findOne(treatment.getTreatmentServiceId());
				if (treatmentServicesCollection != null) {
				    TreatmentService treatmentService = new TreatmentService();
				    BeanUtil.map(treatmentServicesCollection, treatmentService);
				    treatmentResponse.setTreatmentService(treatmentService);
				}
				treatmentResponses.add(treatmentResponse);
		    }
			response = new PatientTreatmentResponse();	
			BeanUtil.map(patientTreatmentCollection, response);
			response.setTreatments(treatmentResponses);
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
		ObjectId patientObjectId = null, doctorObjectId = null, locationObjectId = null , hospitalObjectId= null;
		if(!DPDoctorUtils.anyStringEmpty(patientId))patientObjectId = new ObjectId(patientId);
		if(!DPDoctorUtils.anyStringEmpty(doctorId))doctorObjectId = new ObjectId(doctorId);
    	if(!DPDoctorUtils.anyStringEmpty(locationId))locationObjectId = new ObjectId(locationId);
    	if(!DPDoctorUtils.anyStringEmpty(hospitalId))hospitalObjectId = new ObjectId(hospitalId);
    	
	    historyCollection = historyRepository.findHistory(doctorObjectId, locationObjectId, hospitalObjectId, patientObjectId);
	    if (historyCollection != null) {
		List<ObjectId> medicalHistoryList = historyCollection.getMedicalhistory();
		if (medicalHistoryList != null) {
		    if (!medicalHistoryList.contains(new ObjectId(diseaseId))) {
			medicalHistoryList.add(new ObjectId(diseaseId));
		    }
		} else {
		    medicalHistoryList = new ArrayList<ObjectId>();
		    medicalHistoryList.add(new ObjectId(diseaseId));
		    historyCollection.setMedicalhistory(medicalHistoryList);
		}
		historyCollection.setUpdatedTime(new Date());
	    } else {
	    	historyCollection = new HistoryCollection(doctorObjectId, locationObjectId, hospitalObjectId, patientObjectId);
		historyCollection.setCreatedTime(new Date());
		List<ObjectId> medicalHistoryList = new ArrayList<ObjectId>();
		medicalHistoryList.add(new ObjectId(diseaseId));
		historyCollection.setMedicalhistory(medicalHistoryList);
	    }
	    // finally add history into db.
	    historyCollection = historyRepository.save(historyCollection);
	    if(historyCollection != null){
	    	response = new HistoryDetailsResponse();
	    	BeanUtil.map(historyCollection, response);
			List<ObjectId> medicalHistoryIds = historyCollection.getMedicalhistory();
			if (medicalHistoryIds != null && !medicalHistoryIds.isEmpty()) {
			    List<DiseaseListResponse> medicalHistory = getDiseasesByIds(medicalHistoryIds);
			    response.setMedicalhistory(medicalHistory);
			}

			List<ObjectId> familyHistoryIds = historyCollection.getFamilyhistory();
			if (familyHistoryIds != null && !familyHistoryIds.isEmpty()) {
			    List<DiseaseListResponse> familyHistory = getDiseasesByIds(familyHistoryIds);
			    response.setFamilyhistory(familyHistory);
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
    public HistoryDetailsResponse assignFamilyHistory(String diseaseId, String patientId, String doctorId, String hospitalId, String locationId) {
    	HistoryDetailsResponse response = null;
    	HistoryCollection historyCollection = null;
	try {
		ObjectId patientObjectId = null, doctorObjectId = null, locationObjectId = null , hospitalObjectId= null;
		if(!DPDoctorUtils.anyStringEmpty(patientId))patientObjectId = new ObjectId(patientId);
		if(!DPDoctorUtils.anyStringEmpty(doctorId))doctorObjectId = new ObjectId(doctorId);
    	if(!DPDoctorUtils.anyStringEmpty(locationId))locationObjectId = new ObjectId(locationId);
    	if(!DPDoctorUtils.anyStringEmpty(hospitalId))hospitalObjectId = new ObjectId(hospitalId);
    	
	    // check if history for this patient is already added .
	    historyCollection = historyRepository.findHistory(doctorObjectId, locationObjectId, hospitalObjectId, patientObjectId);
	    if (historyCollection != null) {
		// check if familyHistory are there in history.
		List<ObjectId> familyHistoryList = historyCollection.getFamilyhistory();
		if (familyHistoryList != null) {
		    // check if this diseaseId id is already added into history.
		    if (!familyHistoryList.contains(new ObjectId(diseaseId))) {
			familyHistoryList.add(new ObjectId(diseaseId));
		    }
		    // if no familyHistory is added into history then add it .
		} else {
		    familyHistoryList = new ArrayList<ObjectId>();
		    familyHistoryList.add(new ObjectId(diseaseId));
		    historyCollection.setFamilyhistory(familyHistoryList);
		}
		historyCollection.setUpdatedTime(new Date());
	    } else {// if history not added for this patient.Create new history.
	    	historyCollection = new HistoryCollection(doctorObjectId, locationObjectId, hospitalObjectId, patientObjectId);
		historyCollection.setCreatedTime(new Date());
		List<ObjectId> familyHistoryList = new ArrayList<ObjectId>();
		familyHistoryList.add(new ObjectId(diseaseId));
		historyCollection.setFamilyhistory(familyHistoryList);
	    }
	    // finally add history into db.
	    historyCollection = historyRepository.save(historyCollection);
	    if(historyCollection != null){
	    	response = new HistoryDetailsResponse();
	    	BeanUtil.map(historyCollection, response);
			List<ObjectId> medicalHistoryIds = historyCollection.getMedicalhistory();
			if (medicalHistoryIds != null && !medicalHistoryIds.isEmpty()) {
			    List<DiseaseListResponse> medicalHistory = getDiseasesByIds(medicalHistoryIds);
			    response.setMedicalhistory(medicalHistory);
			}

			List<ObjectId> familyHistoryIds = historyCollection.getFamilyhistory();
			if (familyHistoryIds != null && !familyHistoryIds.isEmpty()) {
			    List<DiseaseListResponse> familyHistory = getDiseasesByIds(familyHistoryIds);
			    response.setFamilyhistory(familyHistory);
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
    public boolean addSpecialNotes(List<String> specialNotes, String patientId, String doctorId, String hospitalId, String locationId) {
	HistoryCollection historyCollection = null;
	List<NotesCollection> notesCollections = null;
	try {
		ObjectId patientObjectId = null, doctorObjectId = null, locationObjectId = null , hospitalObjectId= null;
		if(!DPDoctorUtils.anyStringEmpty(patientId))patientObjectId = new ObjectId(patientId);
		if(!DPDoctorUtils.anyStringEmpty(doctorId))doctorObjectId = new ObjectId(doctorId);
    	if(!DPDoctorUtils.anyStringEmpty(locationId))locationObjectId = new ObjectId(locationId);
    	if(!DPDoctorUtils.anyStringEmpty(hospitalId))hospitalObjectId = new ObjectId(hospitalId);
    	
    	List<ObjectId> specialNotesObjectIds = null;
    	if(specialNotes != null){
    		specialNotesObjectIds = new ArrayList<ObjectId>();
    		for(String specialNotesId : specialNotes)specialNotesObjectIds.add(new ObjectId(specialNotesId));
    	}
	    historyCollection = historyRepository.findHistory(doctorObjectId, locationObjectId, hospitalObjectId, patientObjectId);
	    if (historyCollection != null) {
			List<ObjectId> specialNotesInHistory = historyCollection.getSpecialNotes();
			if (specialNotesInHistory == null) specialNotesInHistory = new ArrayList<ObjectId>();
			if(specialNotesObjectIds != null)specialNotesInHistory.addAll(specialNotesObjectIds);
			historyCollection.setSpecialNotes(specialNotesInHistory);
	    } else {// if history not added for this patient.Create new history.
	    	historyCollection = new HistoryCollection(doctorObjectId, locationObjectId, hospitalObjectId, patientObjectId);
	    	historyCollection.setCreatedTime(new Date());
	    	historyCollection.setSpecialNotes(specialNotesObjectIds);
	    }
	    historyRepository.save(historyCollection);

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
		ObjectId patientObjectId = null, doctorObjectId = null, locationObjectId = null , hospitalObjectId= null;
		if(!DPDoctorUtils.anyStringEmpty(patientId))patientObjectId = new ObjectId(patientId);
		if(!DPDoctorUtils.anyStringEmpty(doctorId))doctorObjectId = new ObjectId(doctorId);
    	if(!DPDoctorUtils.anyStringEmpty(locationId))locationObjectId = new ObjectId(locationId);
    	if(!DPDoctorUtils.anyStringEmpty(hospitalId))hospitalObjectId = new ObjectId(hospitalId);
    	
	    historyCollection = historyRepository.findHistory(doctorObjectId, locationObjectId, hospitalObjectId, patientObjectId);
	    if (historyCollection != null) {
		@SuppressWarnings("unchecked")
		List<String> reports = (List<String>) CollectionUtils.collect(historyCollection.getGeneralRecords(), new BeanToPropertyValueTransformer("data"));
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
			recordsCollection = recordsRepository.findOne(new ObjectId(reportId));
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
		ObjectId patientObjectId = null, doctorObjectId = null, locationObjectId = null , hospitalObjectId= null;
		if(!DPDoctorUtils.anyStringEmpty(patientId))patientObjectId = new ObjectId(patientId);
		if(!DPDoctorUtils.anyStringEmpty(doctorId))doctorObjectId = new ObjectId(doctorId);
    	if(!DPDoctorUtils.anyStringEmpty(locationId))locationObjectId = new ObjectId(locationId);
    	if(!DPDoctorUtils.anyStringEmpty(hospitalId))hospitalObjectId = new ObjectId(hospitalId);
    	
	    historyCollection = historyRepository.findHistory(doctorObjectId, locationObjectId, hospitalObjectId, patientObjectId);
	    if (historyCollection != null) {
		@SuppressWarnings("unchecked")
		List<String> clinicalNotes = (List<String>) CollectionUtils.collect(historyCollection.getGeneralRecords(), new BeanToPropertyValueTransformer("data"));
		if (clinicalNotes != null) {
		    if (clinicalNotes.contains(clinicalNotesId)) {
			historyCollection.getGeneralRecords().remove(clinicalNotes.indexOf(clinicalNotesId));
			if (checkIfHistoryRemovedCompletely(historyCollection)) {
			    historyRepository.delete(historyCollection.getId());
			} else {
			    historyCollection.setUpdatedTime(new Date());
			    historyRepository.save(historyCollection);
			}
			clinicalNotesCollection = clinicalNotesRepository.findOne(new ObjectId(clinicalNotesId));
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
		ObjectId patientObjectId = null, doctorObjectId = null, locationObjectId = null , hospitalObjectId= null;
		if(!DPDoctorUtils.anyStringEmpty(patientId))patientObjectId = new ObjectId(patientId);
		if(!DPDoctorUtils.anyStringEmpty(doctorId))doctorObjectId = new ObjectId(doctorId);
    	if(!DPDoctorUtils.anyStringEmpty(locationId))locationObjectId = new ObjectId(locationId);
    	if(!DPDoctorUtils.anyStringEmpty(hospitalId))hospitalObjectId = new ObjectId(hospitalId);
    	
	    historyCollection = historyRepository.findHistory(doctorObjectId, locationObjectId, hospitalObjectId, patientObjectId);
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
			prescriptionCollection = prescriptionRepository.findOne(new ObjectId(prescriptionId));
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
					DrugCollection drugCollection = drugRepository.findOne(new ObjectId(prescriptionItem.getDrugId()));
					Drug drug = new Drug();
					if (drugCollection != null)
					    BeanUtil.map(drugCollection, drug);
					prescriptionItemDetails.setDrug(drug);
				    }
				    prescriptionItemDetailsList.add(prescriptionItemDetails);
				}
				response.setItems(prescriptionItemDetailsList);
			    }
				PatientVisitCollection patientVisitCollection = patientVisitRepository.findByPrescriptionId(prescriptionCollection.getId());
				if (patientVisitCollection != null) response.setVisitId(patientVisitCollection.getId().toString());
				
				if(tests != null && !tests.isEmpty()){
					List<TestAndRecordDataResponse> diagnosticTests = new ArrayList<TestAndRecordDataResponse>();
				    	for(TestAndRecordData data : tests){
				    		if(data.getTestId() != null){
				    			DiagnosticTestCollection diagnosticTestCollection = diagnosticTestRepository.findOne(new ObjectId(data.getTestId()));
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
		ObjectId patientObjectId = null, doctorObjectId = null, locationObjectId = null , hospitalObjectId= null;
		if(!DPDoctorUtils.anyStringEmpty(patientId))patientObjectId = new ObjectId(patientId);
		if(!DPDoctorUtils.anyStringEmpty(doctorId))doctorObjectId = new ObjectId(doctorId);
    	if(!DPDoctorUtils.anyStringEmpty(locationId))locationObjectId = new ObjectId(locationId);
    	if(!DPDoctorUtils.anyStringEmpty(hospitalId))hospitalObjectId = new ObjectId(hospitalId);
    	
	    historyCollection = historyRepository.findHistory(doctorObjectId, locationObjectId, hospitalObjectId, patientObjectId);
	    if (historyCollection != null) {
		List<ObjectId> medicalHistory = historyCollection.getMedicalhistory();
		if (medicalHistory != null) {
		    if (medicalHistory.contains(diseaseId)) {
			medicalHistory.remove(diseaseId);
			if (checkIfHistoryRemovedCompletely(historyCollection)) {
			    historyRepository.delete(historyCollection.getId());
			} else {
			    historyCollection.setUpdatedTime(new Date());
			    historyCollection = historyRepository.save(historyCollection);
			    if(historyCollection != null){
			    	response = new HistoryDetailsResponse();
			    	BeanUtil.map(historyCollection, response);
					List<ObjectId> medicalHistoryIds = historyCollection.getMedicalhistory();
					if (medicalHistoryIds != null && !medicalHistoryIds.isEmpty()) {
					    List<DiseaseListResponse> medicalHistoryList = getDiseasesByIds(medicalHistoryIds);
					    response.setMedicalhistory(medicalHistoryList);
					}

					List<ObjectId> familyHistoryIds = historyCollection.getFamilyhistory();
					if (familyHistoryIds != null && !familyHistoryIds.isEmpty()) {
					    List<DiseaseListResponse> familyHistory = getDiseasesByIds(familyHistoryIds);
					    response.setFamilyhistory(familyHistory);
					}
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
		ObjectId patientObjectId = null, doctorObjectId = null, locationObjectId = null , hospitalObjectId= null;
		if(!DPDoctorUtils.anyStringEmpty(patientId))patientObjectId = new ObjectId(patientId);
		if(!DPDoctorUtils.anyStringEmpty(doctorId))doctorObjectId = new ObjectId(doctorId);
    	if(!DPDoctorUtils.anyStringEmpty(locationId))locationObjectId = new ObjectId(locationId);
    	if(!DPDoctorUtils.anyStringEmpty(hospitalId))hospitalObjectId = new ObjectId(hospitalId);
    	
	    historyCollection = historyRepository.findHistory(doctorObjectId, locationObjectId, hospitalObjectId, patientObjectId);
	    if (historyCollection != null) {
		List<ObjectId> familyHistory = historyCollection.getFamilyhistory();
		if (familyHistory != null) {
		    if (familyHistory.contains(diseaseId)) {
			familyHistory.remove(diseaseId);
			if (checkIfHistoryRemovedCompletely(historyCollection)) {
			    historyRepository.delete(historyCollection.getId());
			} else {
			    historyCollection.setUpdatedTime(new Date());
			    historyCollection = historyRepository.save(historyCollection);
			    if(historyCollection != null){
			    	response = new HistoryDetailsResponse();
			    	BeanUtil.map(historyCollection, response);
					List<ObjectId> medicalHistoryIds = historyCollection.getMedicalhistory();
					if (medicalHistoryIds != null && !medicalHistoryIds.isEmpty()) {
					    List<DiseaseListResponse> medicalHistory = getDiseasesByIds(medicalHistoryIds);
					    response.setMedicalhistory(medicalHistory);
					}

					List<ObjectId> familyHistoryIds = historyCollection.getFamilyhistory();
					if (familyHistoryIds != null && !familyHistoryIds.isEmpty()) {
					    List<DiseaseListResponse> familyHistoryList = getDiseasesByIds(familyHistoryIds);
					    response.setFamilyhistory(familyHistoryList);
					}
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
	    Boolean discarded, Boolean isAdmin, String searchTerm) {
	List<DiseaseListResponse> diseaseListResponses = null;

	switch (Range.valueOf(range.toUpperCase())) {

	case GLOBAL:
		if(isAdmin)diseaseListResponses = getGlobalDiseasesForAdmin(page, size, updatedTime, discarded, searchTerm);
		else diseaseListResponses = getGlobalDiseases(page, size, updatedTime, discarded);
	    break;
	case CUSTOM:
		if(isAdmin)diseaseListResponses = getCustomDiseasesForAdmin(page, size, updatedTime, discarded, searchTerm);
		else diseaseListResponses = getCustomDiseases(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
	    break;
	case BOTH:
		if(isAdmin)diseaseListResponses = getCustomGlobalDiseasesForAdmin(page, size, updatedTime, discarded, searchTerm);
		else diseaseListResponses = getCustomGlobalDiseases(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
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

	    ObjectId doctorObjectId = null, locationObjectId = null , hospitalObjectId= null;
		if(!DPDoctorUtils.anyStringEmpty(doctorId))doctorObjectId = new ObjectId(doctorId);
    	if(!DPDoctorUtils.anyStringEmpty(locationId))locationObjectId = new ObjectId(locationId);
    	if(!DPDoctorUtils.anyStringEmpty(hospitalId))hospitalObjectId = new ObjectId(hospitalId);
    	
		if (locationObjectId == null && hospitalObjectId == null) {
		    if (size > 0)
			diseasesCollections = diseasesRepository.findCustomDiseases(doctorObjectId, new Date(createdTimeStamp), discards,
				new PageRequest(page, size, Direction.DESC, "updatedTime"));
		    else
			diseasesCollections = diseasesRepository.findCustomDiseases(doctorObjectId, new Date(createdTimeStamp), discards,
				new Sort(Sort.Direction.DESC, "updatedTime"));
		} else {
		    if (size > 0)
			diseasesCollections = diseasesRepository.findCustomDiseases(doctorObjectId, locationObjectId, hospitalObjectId, new Date(createdTimeStamp), discards,
				new PageRequest(page, size, Direction.DESC, "updatedTime"));
		    else
			diseasesCollections = diseasesRepository.findCustomDiseases(doctorObjectId, locationObjectId, hospitalObjectId, new Date(createdTimeStamp), discards,
				new Sort(Sort.Direction.DESC, "updatedTime"));
		}
	    if (diseasesCollections != null) {
		diseaseListResponses = new ArrayList<DiseaseListResponse>();
		for (DiseasesCollection diseasesCollection : diseasesCollections) {
			DiseaseListResponse diseaseListResponse = new DiseaseListResponse(diseasesCollection.getId().toString(), diseasesCollection.getDisease(),
				    diseasesCollection.getExplanation(), 
				    DPDoctorUtils.anyStringEmpty(diseasesCollection.getDoctorId()) ? null : diseasesCollection.getDoctorId().toString(),
				    DPDoctorUtils.anyStringEmpty(diseasesCollection.getLocationId()) ? null : diseasesCollection.getLocationId().toString(),
				    DPDoctorUtils.anyStringEmpty(diseasesCollection.getHospitalId()) ? null : diseasesCollection.getHospitalId().toString(), 
				    diseasesCollection.getDiscarded(), diseasesCollection.getCreatedTime(),
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

	    if (size > 0)diseasesCollections = diseasesRepository.findGlobalDiseases(new Date(createdTimeStamp), discards, new PageRequest(page, size, Direction.DESC, "updatedTime"));
	    else
		diseasesCollections = diseasesRepository.findGlobalDiseases(new Date(createdTimeStamp), discards, new Sort(Sort.Direction.DESC, "updatedTime"));

	    if (diseasesCollections != null) {
		diseaseListResponses = new ArrayList<DiseaseListResponse>();
		for (DiseasesCollection diseasesCollection : diseasesCollections) {
			DiseaseListResponse diseaseListResponse = new DiseaseListResponse(diseasesCollection.getId().toString(), diseasesCollection.getDisease(),
				    diseasesCollection.getExplanation(), 
				    DPDoctorUtils.anyStringEmpty(diseasesCollection.getDoctorId()) ? null : diseasesCollection.getDoctorId().toString(),
				    DPDoctorUtils.anyStringEmpty(diseasesCollection.getLocationId()) ? null : diseasesCollection.getLocationId().toString(),
				    DPDoctorUtils.anyStringEmpty(diseasesCollection.getHospitalId()) ? null : diseasesCollection.getHospitalId().toString(), 
				    diseasesCollection.getDiscarded(), diseasesCollection.getCreatedTime(),
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

	    ObjectId doctorObjectId = null, locationObjectId = null , hospitalObjectId= null;
		if(!DPDoctorUtils.anyStringEmpty(doctorId))doctorObjectId = new ObjectId(doctorId);
    	if(!DPDoctorUtils.anyStringEmpty(locationId))locationObjectId = new ObjectId(locationId);
    	if(!DPDoctorUtils.anyStringEmpty(hospitalId))hospitalObjectId = new ObjectId(hospitalId);
    	
		if (locationObjectId == null && hospitalObjectId == null) {
		    if (size > 0)diseasesCollections = diseasesRepository.findCustomGlobalDiseases(doctorObjectId, new Date(createdTimeStamp), discards, new PageRequest(page, size, Direction.DESC, "updatedTime"));
		    else diseasesCollections = diseasesRepository.findCustomGlobalDiseases(doctorObjectId, new Date(createdTimeStamp), discards, new Sort(Sort.Direction.DESC, "updatedTime"));
		} else {
		    if (size > 0) diseasesCollections = diseasesRepository.findCustomGlobalDiseases(doctorObjectId, locationObjectId, hospitalObjectId, new Date(createdTimeStamp), discards, new PageRequest(page, size, Direction.DESC, "updatedTime"));
		    else diseasesCollections = diseasesRepository.findCustomGlobalDiseases(doctorObjectId, locationObjectId, hospitalObjectId, new Date(createdTimeStamp), discards, new Sort(Sort.Direction.DESC, "updatedTime"));
		}

		if (diseasesCollections != null) {
		diseaseListResponses = new ArrayList<DiseaseListResponse>();
		for (DiseasesCollection diseasesCollection : diseasesCollections) {
		    DiseaseListResponse diseaseListResponse = new DiseaseListResponse(diseasesCollection.getId().toString(), diseasesCollection.getDisease(),
			    diseasesCollection.getExplanation(), 
			    DPDoctorUtils.anyStringEmpty(diseasesCollection.getDoctorId()) ? null : diseasesCollection.getDoctorId().toString(),
			    DPDoctorUtils.anyStringEmpty(diseasesCollection.getLocationId()) ? null : diseasesCollection.getLocationId().toString(),
			    DPDoctorUtils.anyStringEmpty(diseasesCollection.getHospitalId()) ? null : diseasesCollection.getHospitalId().toString(), 
			    diseasesCollection.getDiscarded(), diseasesCollection.getCreatedTime(),
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

    private List<DiseaseListResponse> getCustomDiseasesForAdmin(int page, int size, String updatedTime, Boolean discarded, String searchTerm) {
    	List<DiseaseListResponse> diseaseListResponses = null;
    	List<DiseasesCollection> diseasesCollections = null;
    	boolean[] discards = new boolean[2];
    	discards[0] = false;
    	try {
    		if (discarded)
    			discards[1] = true;

    			long createdTimeStamp = Long.parseLong(updatedTime);
    			if(DPDoctorUtils.anyStringEmpty(searchTerm)){
    				if (size > 0)diseasesCollections = diseasesRepository.findCustomDiseasesForAdmin(new Date(createdTimeStamp), discards, new PageRequest(page, size, Direction.DESC, "updatedTime"));
    				else diseasesCollections = diseasesRepository.findCustomDiseasesForAdmin(new Date(createdTimeStamp), discards, new Sort(Sort.Direction.DESC, "updatedTime"));
    			}else{
    				if (size > 0)diseasesCollections = diseasesRepository.findCustomDiseasesForAdmin(new Date(createdTimeStamp), discards, searchTerm, new PageRequest(page, size, Direction.DESC, "updatedTime"));
    				else diseasesCollections = diseasesRepository.findCustomDiseasesForAdmin(new Date(createdTimeStamp), discards, searchTerm, new Sort(Sort.Direction.DESC, "updatedTime"));
    			}
    	    if (diseasesCollections != null) {
    		diseaseListResponses = new ArrayList<DiseaseListResponse>();
    		for (DiseasesCollection diseasesCollection : diseasesCollections) {
    			DiseaseListResponse diseaseListResponse = new DiseaseListResponse(diseasesCollection.getId().toString(), diseasesCollection.getDisease(),
    				    diseasesCollection.getExplanation(), 
    				    DPDoctorUtils.anyStringEmpty(diseasesCollection.getDoctorId()) ? null : diseasesCollection.getDoctorId().toString(),
    				    DPDoctorUtils.anyStringEmpty(diseasesCollection.getLocationId()) ? null : diseasesCollection.getLocationId().toString(),
    				    DPDoctorUtils.anyStringEmpty(diseasesCollection.getHospitalId()) ? null : diseasesCollection.getHospitalId().toString(), 
    				    diseasesCollection.getDiscarded(), diseasesCollection.getCreatedTime(),
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

        private List<DiseaseListResponse> getGlobalDiseasesForAdmin(int page, int size, String updatedTime, Boolean discarded, String searchTerm) {
    	List<DiseaseListResponse> diseaseListResponses = null;
    	List<DiseasesCollection> diseasesCollections = null;
    	boolean[] discards = new boolean[2];
    	discards[0] = false;
    	try {
    	    if (discarded)
    			discards[1] = true;
    		    long createdTimeStamp = Long.parseLong(updatedTime);
    		    
    		    if(DPDoctorUtils.anyStringEmpty(searchTerm)){
    		    	if (size > 0)diseasesCollections = diseasesRepository.findGlobalDiseasesForAdmin(new Date(createdTimeStamp), discards, new PageRequest(page, size, Direction.DESC, "updatedTime"));
    			    else diseasesCollections = diseasesRepository.findGlobalDiseasesForAdmin(new Date(createdTimeStamp), discards, new Sort(Sort.Direction.DESC, "updatedTime"));
    		    }else{
    		    	if (size > 0)diseasesCollections = diseasesRepository.findGlobalDiseasesForAdmin(new Date(createdTimeStamp), discards, searchTerm, new PageRequest(page, size, Direction.DESC, "updatedTime"));
    			    else diseasesCollections = diseasesRepository.findGlobalDiseasesForAdmin(new Date(createdTimeStamp), searchTerm, discards, new Sort(Sort.Direction.DESC, "updatedTime"));
    		    }

    	    if (diseasesCollections != null) {
    		diseaseListResponses = new ArrayList<DiseaseListResponse>();
    		for (DiseasesCollection diseasesCollection : diseasesCollections) {
    			DiseaseListResponse diseaseListResponse = new DiseaseListResponse(diseasesCollection.getId().toString(), diseasesCollection.getDisease(),
    				    diseasesCollection.getExplanation(), 
    				    DPDoctorUtils.anyStringEmpty(diseasesCollection.getDoctorId()) ? null : diseasesCollection.getDoctorId().toString(),
    				    DPDoctorUtils.anyStringEmpty(diseasesCollection.getLocationId()) ? null : diseasesCollection.getLocationId().toString(),
    				    DPDoctorUtils.anyStringEmpty(diseasesCollection.getHospitalId()) ? null : diseasesCollection.getHospitalId().toString(), 
    				    diseasesCollection.getDiscarded(), diseasesCollection.getCreatedTime(),
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

        private List<DiseaseListResponse> getCustomGlobalDiseasesForAdmin(int page, int size, String updatedTime, Boolean discarded, String searchTerm) {
    	List<DiseaseListResponse> diseaseListResponses = null;
    	List<DiseasesCollection> diseasesCollections = null;
    	boolean[] discards = new boolean[2];
    	discards[0] = false;
    	try {
    	    if (discarded)
    			discards[1] = true;

    			long createdTimeStamp = Long.parseLong(updatedTime);
    			if(DPDoctorUtils.anyStringEmpty(searchTerm)){
    				if (size > 0)diseasesCollections = diseasesRepository.findCustomGlobalDiseasesForAdmin(new Date(createdTimeStamp), discards, new PageRequest(page, size, Direction.DESC, "updatedTime"));
    				else diseasesCollections = diseasesRepository.findCustomGlobalDiseasesForAdmin(new Date(createdTimeStamp), discards, new Sort(Sort.Direction.DESC, "updatedTime"));
    			}else{
    				if (size > 0)diseasesCollections = diseasesRepository.findCustomGlobalDiseasesForAdmin(new Date(createdTimeStamp), discards, searchTerm, new PageRequest(page, size, Direction.DESC, "updatedTime"));
    				else diseasesCollections = diseasesRepository.findCustomGlobalDiseasesForAdmin(new Date(createdTimeStamp), discards, searchTerm, new Sort(Sort.Direction.DESC, "updatedTime"));
    			}
    	    if (diseasesCollections != null) {
    		diseaseListResponses = new ArrayList<DiseaseListResponse>();
    		for (DiseasesCollection diseasesCollection : diseasesCollections) {
    			DiseaseListResponse diseaseListResponse = new DiseaseListResponse(diseasesCollection.getId().toString(), diseasesCollection.getDisease(),
    				    diseasesCollection.getExplanation(), 
    				    DPDoctorUtils.anyStringEmpty(diseasesCollection.getDoctorId()) ? null : diseasesCollection.getDoctorId().toString(),
    				    DPDoctorUtils.anyStringEmpty(diseasesCollection.getLocationId()) ? null : diseasesCollection.getLocationId().toString(),
    				    DPDoctorUtils.anyStringEmpty(diseasesCollection.getHospitalId()) ? null : diseasesCollection.getHospitalId().toString(), 
    				    diseasesCollection.getDiscarded(), diseasesCollection.getCreatedTime(),
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
	    
	    ObjectId patientObjectId = null, doctorObjectId = null, locationObjectId = null , hospitalObjectId= null;
		if(!DPDoctorUtils.anyStringEmpty(patientId))patientObjectId = new ObjectId(patientId);
		if(!DPDoctorUtils.anyStringEmpty(doctorId))doctorObjectId = new ObjectId(doctorId);
    	if(!DPDoctorUtils.anyStringEmpty(locationId))locationObjectId = new ObjectId(locationId);
    	if(!DPDoctorUtils.anyStringEmpty(hospitalId))hospitalObjectId = new ObjectId(hospitalId);
    	
	    long createdTime = Long.parseLong(updatedTime);
	    AggregationOperation matchForFilter = null;
	    Aggregation aggregation = null;
	    if (!historyFilter.contains(HistoryFilter.ALL.getFilter())) {
		matchForFilter = Aggregation.match(Criteria.where("generalRecords.dataType").in(historyFilter));
		if (size > 0)
		    aggregation = Aggregation.newAggregation(
			    Aggregation.match(Criteria.where("patientId").is(patientObjectId).andOperator(Criteria.where("doctorId").is(doctorObjectId),
				    Criteria.where("locationId").is(locationObjectId), Criteria.where("hospitalId").is(hospitalObjectId),
				    Criteria.where("updatedTime").gte(new Date(createdTime)))),
			    Aggregation.unwind("generalRecords"), matchForFilter, Aggregation.skip(page * size), Aggregation.limit(size),
			    Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));
		else
		    aggregation = Aggregation.newAggregation(
			    Aggregation.match(Criteria.where("patientId").is(patientObjectId).andOperator(Criteria.where("doctorId").is(doctorObjectId),
				    Criteria.where("locationId").is(locationObjectId), Criteria.where("hospitalId").is(hospitalObjectId),
				    Criteria.where("updatedTime").gte(new Date(createdTime)))),
			    Aggregation.unwind("generalRecords"), matchForFilter, Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));

	    } else {
		if (size > 0)
		    aggregation = Aggregation.newAggregation(
			    Aggregation.match(Criteria.where("patientId").is(patientObjectId).andOperator(Criteria.where("doctorId").is(doctorObjectId),
				    Criteria.where("locationId").is(locationObjectId), Criteria.where("hospitalId").is(hospitalObjectId),
				    Criteria.where("updatedTime").gte(new Date(createdTime)))),
			    Aggregation.unwind("generalRecords"), Aggregation.skip(page * size), Aggregation.limit(size),
			    Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));
		else
		    aggregation = Aggregation.newAggregation(
			    Aggregation.match(Criteria.where("patientId").is(patientObjectId).andOperator(Criteria.where("doctorId").is(doctorObjectId),
				    Criteria.where("locationId").is(locationObjectId), Criteria.where("hospitalId").is(hospitalObjectId),
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

		    List<ObjectId> medicalHistoryIds = null;
		    if(historyCollection.getMedicalhistory() != null){
		    	medicalHistoryIds = new ArrayList<ObjectId>();
		    	for(String medicalHistoryId : historyCollection.getMedicalhistory())medicalHistoryIds.add(new ObjectId(medicalHistoryId));
		    }
		    if (medicalHistoryIds != null && !medicalHistoryIds.isEmpty()) {
			List<DiseaseListResponse> medicalHistory = getDiseasesByIds(medicalHistoryIds);
			historyDetailsResponse.setMedicalhistory(medicalHistory);
		    }

		    List<ObjectId> familyHistoryIds = null;
		    if(historyCollection.getFamilyhistory() != null){
		    	familyHistoryIds = new ArrayList<ObjectId>();
		    	for(String familyHistoryId : historyCollection.getFamilyhistory())familyHistoryIds.add(new ObjectId(familyHistoryId));
		    }
		    if (familyHistoryIds != null && !familyHistoryIds.isEmpty()) {
			List<DiseaseListResponse> familyHistory = getDiseasesByIds(familyHistoryIds);
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
    public List<HistoryDetailsResponse> getPatientHistoryDetailsWithVerifiedOTP(String patientId, String doctorId, String hospitalId, String locationId,
	    List<String> historyFilter, int page, int size, String updatedTime) {
	List<HistoryDetailsResponse> response = null;
	try {
	    for (int i = 0; i < historyFilter.size(); i++) {
		historyFilter.set(i, historyFilter.get(i).toUpperCase());
	    }
	    
	    ObjectId patientObjectId = null;
		if(!DPDoctorUtils.anyStringEmpty(patientId))patientObjectId = new ObjectId(patientId);
		
	    long createdTime = Long.parseLong(updatedTime);
	    AggregationOperation matchForFilter = null;
	    Aggregation aggregation = null;
	    if (!historyFilter.contains(HistoryFilter.ALL.getFilter())) {
		matchForFilter = Aggregation.match(Criteria.where("generalRecords.dataType").in(historyFilter));
		if (size > 0)
		    aggregation = Aggregation.newAggregation(
			    Aggregation.match(Criteria.where("patientId").is(patientObjectId).and("updatedTime").gte(new Date(createdTime))),
			    Aggregation.unwind("generalRecords"), matchForFilter, Aggregation.skip(page * size), Aggregation.limit(size),
			    Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));
		else
		    aggregation = Aggregation.newAggregation(
			    Aggregation.match(Criteria.where("patientId").is(patientObjectId).and("updatedTime").gte(new Date(createdTime))),
			    Aggregation.unwind("generalRecords"), matchForFilter, Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));
	    } else {
		if (size > 0)
		    aggregation = Aggregation.newAggregation(
			    Aggregation.match(Criteria.where("patientId").is(patientObjectId).and("updatedTime").gte(new Date(createdTime))),
			    Aggregation.unwind("generalRecords"), Aggregation.skip(page * size), Aggregation.limit(size));
		else
		    aggregation = Aggregation.newAggregation(
			    Aggregation.match(Criteria.where("patientId").is(patientObjectId).and("updatedTime").gte(new Date(createdTime))),
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

		    List<ObjectId> medicalHistoryIds = null;
		    if(historyCollection.getMedicalhistory() != null){
		    	medicalHistoryIds = new ArrayList<ObjectId>();
		    	for(String medicalHistoryId : historyCollection.getMedicalhistory())medicalHistoryIds.add(new ObjectId(medicalHistoryId));
		    }
		    if (medicalHistoryIds != null && !medicalHistoryIds.isEmpty()) {
			List<DiseaseListResponse> medicalHistory = getDiseasesByIds(medicalHistoryIds);
			historyDetailsResponse.setMedicalhistory(medicalHistory);
		    }

		    List<ObjectId> familyHistoryIds = null;
		    if(historyCollection.getFamilyhistory() != null){
		    	familyHistoryIds = new ArrayList<ObjectId>();
		    	for(String familyHistoryId : historyCollection.getFamilyhistory())familyHistoryIds.add(new ObjectId(familyHistoryId));
		    }
		    if (familyHistoryIds != null) {
			List<DiseaseListResponse> familyHistory = getDiseasesByIds(familyHistoryIds);
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

    @SuppressWarnings("unchecked")
	@Override
    @Transactional
    public List<DiseaseListResponse> getDiseasesByIds(List<ObjectId> medicalHistoryIds) {
	List<DiseaseListResponse> diseaseListResponses = null;
	try {
		List<DiseasesCollection> diseasesCollections =  IteratorUtils.toList(diseasesRepository.findAll(medicalHistoryIds).iterator());

	    if (diseasesCollections != null) {
		diseaseListResponses = new ArrayList<DiseaseListResponse>();
		for(DiseasesCollection diseasesCollection : diseasesCollections){
			DiseaseListResponse diseaseListResponse = new DiseaseListResponse();
			BeanUtil.map(diseasesCollection, diseaseListResponse);
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
    public Integer getHistoryCount(ObjectId doctorObjectId, ObjectId patientObjectId, ObjectId locationObjectId, ObjectId hospitalObjectId, boolean isOTPVerified) {
	Integer historyCount = 0;
	try {
	    List<HistoryCollection> historyCollections = null;
	    if (isOTPVerified)
		historyCollections = historyRepository.findHistory(patientObjectId);
	    else {
		HistoryCollection historyCollection = historyRepository.findHistory(doctorObjectId, locationObjectId, hospitalObjectId, patientObjectId);
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

	    List<ObjectId> diseaseObjectIds = null;
	    if(request.getAddDiseases() != null){
	    	diseaseObjectIds = new ArrayList<ObjectId>();
	    	for(String diseaseId : request.getAddDiseases())diseaseObjectIds.add(new ObjectId(diseaseId));
	    }

		historyCollection = historyRepository.findHistory(new ObjectId(request.getDoctorId()), new ObjectId(request.getLocationId()), new ObjectId(request.getHospitalId()), new ObjectId(request.getPatientId()));
	    if (historyCollection != null) {

		List<ObjectId> medicalHistoryList = historyCollection.getMedicalhistory();
		if (medicalHistoryList != null && !medicalHistoryList.isEmpty()) {
		    if (diseaseObjectIds != null)medicalHistoryList.addAll(diseaseObjectIds);
		    medicalHistoryList = new ArrayList<ObjectId>(new LinkedHashSet<ObjectId>(medicalHistoryList));
		    if (request.getRemoveDiseases() != null)
			medicalHistoryList.removeAll(request.getRemoveDiseases());
		    historyCollection.setMedicalhistory(medicalHistoryList);
		} else {
		    medicalHistoryList = new ArrayList<ObjectId>();
		    if (diseaseObjectIds != null)medicalHistoryList.addAll(diseaseObjectIds);
		    if (request.getRemoveDiseases() != null)
			medicalHistoryList.removeAll(request.getRemoveDiseases());
		    historyCollection.setMedicalhistory(medicalHistoryList);
		}
	    } else {
		historyCollection = new HistoryCollection(new ObjectId(request.getDoctorId()), new ObjectId(request.getLocationId()), new ObjectId(request.getHospitalId()), new ObjectId(request.getPatientId()));
		historyCollection.setCreatedTime(new Date());
		List<ObjectId> medicalHistoryList = new ArrayList<ObjectId>();
		if (diseaseObjectIds != null)medicalHistoryList.addAll(diseaseObjectIds);
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
		
		List<ObjectId> diseasesObjectIdList = null;
		if(request.getAddDiseases() != null){
			diseasesObjectIdList = new ArrayList<ObjectId>();
			for(String disesaseId : request.getAddDiseases())diseasesObjectIdList.add(new ObjectId(disesaseId));
		}
	    historyCollection = historyRepository.findHistory(new ObjectId(request.getDoctorId()), new ObjectId(request.getLocationId()), new ObjectId(request.getHospitalId()), new ObjectId(request.getPatientId()));
	    if (historyCollection != null) {
		List<ObjectId> familyHistoryList = historyCollection.getFamilyhistory();
		if (familyHistoryList != null && !familyHistoryList.isEmpty()) {
			if(diseasesObjectIdList != null)familyHistoryList.addAll(diseasesObjectIdList);
		    familyHistoryList = new ArrayList<ObjectId>(new LinkedHashSet<ObjectId>(familyHistoryList));
		    if (request.getRemoveDiseases() != null)
			familyHistoryList.removeAll(request.getRemoveDiseases());
		    historyCollection.setFamilyhistory(familyHistoryList);
		} else {
		    familyHistoryList = new ArrayList<ObjectId>();
		    if (diseasesObjectIdList != null) familyHistoryList.addAll(diseasesObjectIdList);
		    if (request.getRemoveDiseases() != null)
			familyHistoryList.removeAll(request.getRemoveDiseases());
		    historyCollection.setFamilyhistory(familyHistoryList);
		}
	    } else {
	    	historyCollection = new HistoryCollection(new ObjectId(request.getDoctorId()), new ObjectId(request.getLocationId()), new ObjectId(request.getHospitalId()), new ObjectId(request.getPatientId()));historyCollection.setCreatedTime(new Date());
		List<ObjectId> familyHistoryList = new ArrayList<ObjectId>();
		if (diseasesObjectIdList != null)familyHistoryList.addAll(diseasesObjectIdList);
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
	    historyCollection = historyRepository.findHistory(new ObjectId(doctorId), new ObjectId(locationId), new ObjectId(hospitalId), new ObjectId(patientId));
	    if (historyCollection != null) {
			response = new HistoryDetailsResponse();
			BeanUtil.map(historyCollection, response);
			List<ObjectId> medicalHistoryIds = historyCollection.getMedicalhistory();
		if (medicalHistoryIds != null && !medicalHistoryIds.isEmpty()) {
		    List<DiseaseListResponse> medicalHistory = getDiseasesByIds(medicalHistoryIds);
		    response.setMedicalhistory(medicalHistory);
		}

		List<ObjectId> familyHistoryIds = historyCollection.getFamilyhistory();
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
	    	String body = mailBodyGenerator.generateEMREmailBody(mailResponse.getPatientName(), mailResponse.getDoctorName(), mailResponse.getClinicName(), mailResponse.getClinicAddress(), mailResponse.getMailRecordCreatedDate(), "Medical Data", "emrMailTemplate.vm");
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
	    patientVisitCollection = patientVisitRepository.findOne(new ObjectId(visitId));
	    if (patientVisitCollection != null) {
		if (patientVisitCollection.getClinicalNotesId() != null) {
		    for (ObjectId clinicalNotesId : patientVisitCollection.getClinicalNotesId()) {
			addClinicalNotesToHistory(clinicalNotesId.toString(), patientId, doctorId, hospitalId, locationId);
		    }
		}
		if (patientVisitCollection.getPrescriptionId() != null) {
		    for (ObjectId prescriptionId : patientVisitCollection.getPrescriptionId()) {
			addPrescriptionToHistory(prescriptionId.toString(), patientId, doctorId, hospitalId, locationId);
		    }
		}
		if (patientVisitCollection.getRecordId() != null) {
		    for (ObjectId recordId : patientVisitCollection.getRecordId()) {
			addReportToHistory(recordId.toString(), patientId, doctorId, hospitalId, locationId);
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
	    patientVisitCollection = patientVisitRepository.findOne(new ObjectId(visitId));
	    if (patientVisitCollection != null) {
		if (patientVisitCollection.getClinicalNotesId() != null) {
		    for (ObjectId clinicalNotesId : patientVisitCollection.getClinicalNotesId()) {
			removeClinicalNotes(clinicalNotesId.toString(), patientId, doctorId, hospitalId, locationId);
		    }
		}
		if (patientVisitCollection.getPrescriptionId() != null) {
		    for (ObjectId prescriptionId : patientVisitCollection.getPrescriptionId()) {
			removePrescription(prescriptionId.toString(), patientId, doctorId, hospitalId, locationId);
		    }
		}
		if (patientVisitCollection.getRecordId() != null) {
		    for (ObjectId recordId : patientVisitCollection.getRecordId()) {
			removeReports(recordId.toString(), patientId, doctorId, hospitalId, locationId);
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
			    Aggregation.match(Criteria.where("patientId").is(new ObjectId(patientId)).andOperator(Criteria.where("updatedTime").gte(new Date(createdTime)))),
			    Aggregation.unwind("generalRecords"), matchForFilter, Aggregation.skip(page * size), Aggregation.limit(size),
			    Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));
		else
		    aggregation = Aggregation.newAggregation(
			    Aggregation.match(Criteria.where("patientId").is(new ObjectId(patientId)).andOperator(Criteria.where("updatedTime").gte(new Date(createdTime)))),
			    Aggregation.unwind("generalRecords"), matchForFilter, Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));

	    } else {
		if (size > 0)
		    aggregation = Aggregation.newAggregation(
			    Aggregation.match(Criteria.where("patientId").is(new ObjectId(patientId)).andOperator(Criteria.where("updatedTime").gte(new Date(createdTime)))),
			    Aggregation.unwind("generalRecords"), Aggregation.skip(page * size), Aggregation.limit(size),
			    Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));
		else
		    aggregation = Aggregation.newAggregation(
			    Aggregation.match(Criteria.where("patientId").is(new ObjectId(patientId)).andOperator(Criteria.where("updatedTime").gte(new Date(createdTime)))),
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

		    List<ObjectId> medicalHistoryIds = null;
		    if(historyCollection.getMedicalhistory() != null){
		    	medicalHistoryIds = new ArrayList<ObjectId>();
		    	for(String medicalHistoryId : historyCollection.getMedicalhistory())medicalHistoryIds.add(new ObjectId(medicalHistoryId));
		    }
		    if (medicalHistoryIds != null && !medicalHistoryIds.isEmpty()) {
			List<DiseaseListResponse> medicalHistory = getDiseasesByIds(medicalHistoryIds);
			historyDetailsResponse.setMedicalhistory(medicalHistory);
		    }

		    List<ObjectId> familyHistoryIds = null;
		    if(historyCollection.getFamilyhistory() != null){
		    	familyHistoryIds = new ArrayList<ObjectId>();
		    	for(String familyHistoryId : historyCollection.getFamilyhistory())familyHistoryIds.add(new ObjectId(familyHistoryId));
		    }
		    if (familyHistoryIds != null && !familyHistoryIds.isEmpty()) {
			List<DiseaseListResponse> familyHistory = getDiseasesByIds(familyHistoryIds);
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
    public PatientTreatmentResponse removePatientTreatment(String treatmentId, String patientId, String doctorId, String hospitalId, String locationId) {
    	PatientTreatmentResponse response = null;
    	HistoryCollection historyCollection = null;
	PatientTreatmentCollection patientTreatmentCollection;
	try {
	    historyCollection = historyRepository.findHistory(new ObjectId(doctorId), new ObjectId(locationId), new ObjectId(hospitalId), new ObjectId(patientId));
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
			patientTreatmentCollection = patientTreamentRepository.findOne(new ObjectId(treatmentId));
			if (patientTreatmentCollection != null) {
			    patientTreatmentCollection.setInHistory(false);
			    patientTreatmentCollection.setUpdatedTime(new Date());
			    patientTreamentRepository.save(patientTreatmentCollection);
			    List<TreatmentResponse> treatmentResponses = new ArrayList<TreatmentResponse>();
			    for (Treatment treatment : patientTreatmentCollection.getTreatments()) {
			    
			    	TreatmentResponse treatmentResponse = new TreatmentResponse();
					BeanUtil.map(treatment, treatmentResponse);
					TreatmentServicesCollection treatmentServicesCollection = treatmentServicesRepository.findOne(treatment.getTreatmentServiceId());
					if (treatmentServicesCollection != null) {
					    TreatmentService treatmentService = new TreatmentService();
					    BeanUtil.map(treatmentServicesCollection, treatmentService);
					    treatmentResponse.setTreatmentService(treatmentService);
					}
					treatmentResponses.add(treatmentResponse);
			    }
				response = new PatientTreatmentResponse();	
				BeanUtil.map(patientTreatmentCollection, response);
				response.setTreatments(treatmentResponses);
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

    @Override
    @Transactional
	public HistoryDetailsResponse assignPersonalHistory(PersonalHistoryAddRequest request) {
		HistoryDetailsResponse response = null;
		HistoryCollection historyCollection = null;
		PersonalHistory personalHistory = null;
		try {
			ObjectId patientObjectId = null, doctorObjectId = null, locationObjectId = null, hospitalObjectId = null;
			if (!DPDoctorUtils.anyStringEmpty(request.getPatientId()))
				patientObjectId = new ObjectId(request.getPatientId());
			if (!DPDoctorUtils.anyStringEmpty(request.getDoctorId()))
				doctorObjectId = new ObjectId(request.getDoctorId());
			if (!DPDoctorUtils.anyStringEmpty(request.getLocationId()))
				locationObjectId = new ObjectId(request.getLocationId());
			if (!DPDoctorUtils.anyStringEmpty(request.getHospitalId()))
				hospitalObjectId = new ObjectId(request.getHospitalId());

			historyCollection = historyRepository.findHistory(doctorObjectId, locationObjectId, hospitalObjectId,
					patientObjectId);
			if (historyCollection != null) {
				personalHistory = historyCollection.getPersonalHistory();
				if (personalHistory != null) {
					personalHistory.setAddictions(request.getAddictions());
					personalHistory.setBladderHabit(request.getBladderHabit());
					personalHistory.setBowelHabit(request.getBowelHabit());
					personalHistory.setDiet(request.getDiet());
					historyCollection.setPersonalHistory(personalHistory);
				} else {
					personalHistory = new PersonalHistory();
					personalHistory.setAddictions(request.getAddictions());
					personalHistory.setBladderHabit(request.getBladderHabit());
					personalHistory.setBowelHabit(request.getBowelHabit());
					personalHistory.setDiet(request.getDiet());
					historyCollection.setPersonalHistory(personalHistory);
				}
				historyCollection.setUpdatedTime(new Date());
			} else {
				historyCollection = new HistoryCollection(doctorObjectId, locationObjectId, hospitalObjectId,
						patientObjectId);
				historyCollection.setCreatedTime(new Date());
				personalHistory = new PersonalHistory();
				personalHistory.setAddictions(request.getAddictions());
				personalHistory.setBladderHabit(request.getBladderHabit());
				personalHistory.setBowelHabit(request.getBowelHabit());
				personalHistory.setDiet(request.getDiet());
				historyCollection.setPersonalHistory(personalHistory);
			}
			// finally add history into db.
			historyCollection = historyRepository.save(historyCollection);
			if (historyCollection != null) {
				response = new HistoryDetailsResponse();
				BeanUtil.map(historyCollection, response);
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
	public HistoryDetailsResponse assignDrugsAndAllergies(DrugsAndAllergiesAddRequest request) {
		HistoryDetailsResponse response = null;
		HistoryCollection historyCollection = null;
		DrugsAndAllergies drugsAndAllergies = null;
		try {
			ObjectId patientObjectId = null, doctorObjectId = null, locationObjectId = null, hospitalObjectId = null;
			if (!DPDoctorUtils.anyStringEmpty(request.getPatientId()))
				patientObjectId = new ObjectId(request.getPatientId());
			if (!DPDoctorUtils.anyStringEmpty(request.getDoctorId()))
				doctorObjectId = new ObjectId(request.getDoctorId());
			if (!DPDoctorUtils.anyStringEmpty(request.getLocationId()))
				locationObjectId = new ObjectId(request.getLocationId());
			if (!DPDoctorUtils.anyStringEmpty(request.getHospitalId()))
				hospitalObjectId = new ObjectId(request.getHospitalId());

			historyCollection = historyRepository.findHistory(doctorObjectId, locationObjectId, hospitalObjectId,
					patientObjectId);
			if (historyCollection != null) {
				drugsAndAllergies = historyCollection.getDrugsAndAllergies();
				if (drugsAndAllergies != null) {
					drugsAndAllergies.setDrugs(request.getDrugs());
					drugsAndAllergies.setAllergies(request.getAllergies());
					historyCollection.setDrugsAndAllergies(drugsAndAllergies);
				} else {
					drugsAndAllergies = new DrugsAndAllergies();
					drugsAndAllergies.setDrugs(request.getDrugs());
					drugsAndAllergies.setAllergies(request.getAllergies());
					historyCollection.setDrugsAndAllergies(drugsAndAllergies);
				}
				historyCollection.setUpdatedTime(new Date());
			} else {
				historyCollection = new HistoryCollection(doctorObjectId, locationObjectId, hospitalObjectId,
						patientObjectId);
				historyCollection.setCreatedTime(new Date());
				drugsAndAllergies = new DrugsAndAllergies();
				drugsAndAllergies.setDrugs(request.getDrugs());
				drugsAndAllergies.setAllergies(request.getAllergies());
				historyCollection.setDrugsAndAllergies(drugsAndAllergies);
			}
			// finally add history into db.
			historyCollection = historyRepository.save(historyCollection);
			if (historyCollection != null) {
				response = new HistoryDetailsResponse();
				BeanUtil.map(historyCollection, response);
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
    public HistoryDetailsResponse getHistory( String patientId, String doctorId, String hospitalId, String locationId , List<String> type)
    {
    	HistoryDetailsResponse response =null;
    	ObjectId patientObjectId = null, doctorObjectId = null, locationObjectId = null , hospitalObjectId= null;
		if(!DPDoctorUtils.anyStringEmpty(patientId))patientObjectId = new ObjectId(patientId);
		if(!DPDoctorUtils.anyStringEmpty(doctorId))doctorObjectId = new ObjectId(doctorId);
    	if(!DPDoctorUtils.anyStringEmpty(locationId))locationObjectId = new ObjectId(locationId);
    	if(!DPDoctorUtils.anyStringEmpty(hospitalId))hospitalObjectId = new ObjectId(hospitalId);
    	
    	HistoryCollection historyCollection = historyRepository.findHistory(doctorObjectId, locationObjectId, hospitalObjectId,
				patientObjectId);
    	
		if (historyCollection == null) {
			throw new BusinessException(ServiceError.NoRecord, "History record not found");
		} else {
			response = new HistoryDetailsResponse();
			response.setDoctorId(String.valueOf(historyCollection.getDoctorId()));
			response.setLocationId(String.valueOf(historyCollection.getLocationId()));
			response.setHospitalId(String.valueOf(historyCollection.getHospitalId()));
			response.setPatientId(String.valueOf(historyCollection.getPatientId()));
			if(type == null || type.isEmpty())
			{
				List<ObjectId> medicalHistoryIds = historyCollection.getMedicalhistory();
				if (medicalHistoryIds != null && !medicalHistoryIds.isEmpty()) {
				    List<DiseaseListResponse> medicalHistory = getDiseasesByIds(medicalHistoryIds);
				    response.setMedicalhistory(medicalHistory);
				}
				response.setDrugsAndAllergies(historyCollection.getDrugsAndAllergies());
				response.setPersonalHistory(historyCollection.getPersonalHistory());
				List<ObjectId> familyHistoryIds = historyCollection.getFamilyhistory();
				if (familyHistoryIds != null && !familyHistoryIds.isEmpty()) {
				    List<DiseaseListResponse> familyHistory = getDiseasesByIds(familyHistoryIds);
				    response.setFamilyhistory(familyHistory);
				}
			}
			if (type.contains(HistoryType.MEDICAL.getType())) {
				List<ObjectId> medicalHistoryIds = historyCollection.getMedicalhistory();
				if (medicalHistoryIds != null && !medicalHistoryIds.isEmpty()) {
				    List<DiseaseListResponse> medicalHistory = getDiseasesByIds(medicalHistoryIds);
				    response.setMedicalhistory(medicalHistory);
				}
			}
			if (type.contains(HistoryType.DRUG_ALLERGIES.getType())) {
				response.setDrugsAndAllergies(historyCollection.getDrugsAndAllergies());
			}
			if (type.contains(HistoryType.PERSONAL.getType())) {
				response.setPersonalHistory(historyCollection.getPersonalHistory());
			}
			if (type.contains(HistoryType.FAMILY.getType())) {
				List<ObjectId> familyHistoryIds = historyCollection.getFamilyhistory();
				if (familyHistoryIds != null && !familyHistoryIds.isEmpty()) {
				    List<DiseaseListResponse> familyHistory = getDiseasesByIds(familyHistoryIds);
				    response.setFamilyhistory(familyHistory);
				}
			}
		}
    	return response;
    }
}
