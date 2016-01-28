package com.dpdocter.services.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dpdocter.collections.AddressCollection;
import com.dpdocter.collections.CityCollection;
import com.dpdocter.collections.ComplaintCollection;
import com.dpdocter.collections.CountryCollection;
import com.dpdocter.collections.DiagnosisCollection;
import com.dpdocter.collections.DiagramsCollection;
import com.dpdocter.collections.DoctorClinicProfileCollection;
import com.dpdocter.collections.DoctorCollection;
import com.dpdocter.collections.DrugCollection;
import com.dpdocter.collections.InvestigationCollection;
import com.dpdocter.collections.LabTestCollection;
import com.dpdocter.collections.LandmarkLocalityCollection;
import com.dpdocter.collections.LocationCollection;
import com.dpdocter.collections.NotesCollection;
import com.dpdocter.collections.ObservationCollection;
import com.dpdocter.collections.PatientAdmissionCollection;
import com.dpdocter.collections.PatientCollection;
import com.dpdocter.collections.ReferencesCollection;
import com.dpdocter.collections.StateCollection;
import com.dpdocter.collections.TransactionalCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.collections.UserLocationCollection;
import com.dpdocter.enums.Resource;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.AddressRepository;
import com.dpdocter.repository.CityRepository;
import com.dpdocter.repository.ComplaintRepository;
import com.dpdocter.repository.CountryRepository;
import com.dpdocter.repository.DiagnosisRepository;
import com.dpdocter.repository.DiagramsRepository;
import com.dpdocter.repository.DoctorClinicProfileRepository;
import com.dpdocter.repository.DoctorRepository;
import com.dpdocter.repository.DrugRepository;
import com.dpdocter.repository.InvestigationRepository;
import com.dpdocter.repository.LabTestRepository;
import com.dpdocter.repository.LandmarkLocalityRepository;
import com.dpdocter.repository.LocationRepository;
import com.dpdocter.repository.NotesRepository;
import com.dpdocter.repository.ObservationRepository;
import com.dpdocter.repository.PatientAdmissionRepository;
import com.dpdocter.repository.PatientRepository;
import com.dpdocter.repository.ReferenceRepository;
import com.dpdocter.repository.StateRepository;
import com.dpdocter.repository.TransnationalRepositiory;
import com.dpdocter.repository.UserLocationRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.services.TransactionalManagementService;
import com.dpdocter.solr.beans.DoctorLocation;
import com.dpdocter.solr.document.SolrCityDocument;
import com.dpdocter.solr.document.SolrComplaintsDocument;
import com.dpdocter.solr.document.SolrCountryDocument;
import com.dpdocter.solr.document.SolrDiagnosesDocument;
import com.dpdocter.solr.document.SolrDiagramsDocument;
import com.dpdocter.solr.document.SolrDoctorDocument;
import com.dpdocter.solr.document.SolrDrugDocument;
import com.dpdocter.solr.document.SolrInvestigationsDocument;
import com.dpdocter.solr.document.SolrLabTestDocument;
import com.dpdocter.solr.document.SolrLocalityLandmarkDocument;
import com.dpdocter.solr.document.SolrNotesDocument;
import com.dpdocter.solr.document.SolrObservationsDocument;
import com.dpdocter.solr.document.SolrPatientDocument;
import com.dpdocter.solr.document.SolrStateDocument;
import com.dpdocter.solr.services.SolrCityService;
import com.dpdocter.solr.services.SolrClinicalNotesService;
import com.dpdocter.solr.services.SolrPrescriptionService;
import com.dpdocter.solr.services.SolrRegistrationService;

@Service
public class TransactionalManagementServiceImpl implements TransactionalManagementService {

    private static Logger logger = Logger.getLogger(TransactionalManagementServiceImpl.class.getName());

    @Autowired
    private TransnationalRepositiory transnationalRepositiory;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private PatientAdmissionRepository patientAdmissionRepository;

    @Autowired
    private SolrRegistrationService solrRegistrationService;

    @Autowired
    private DrugRepository drugRepository;

    @Autowired
    private LabTestRepository labTestRepository;

    @Autowired
    private SolrPrescriptionService solrPrescriptionService;

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
    private SolrCityService solrCityService;

    @Autowired
    private CountryRepository countryRepository;
    
    @Autowired
    private StateRepository stateRepository;
    
    @Autowired
    private CityRepository cityRepository;
    
    @Autowired
    private LandmarkLocalityRepository landmarkLocalityRepository;
    
    @Autowired
    private DoctorRepository doctorRepository;
    
    @Autowired
    private LocationRepository locationRepository;
    
    @Autowired
    private UserLocationRepository userLocationRepository;
    
    @Autowired
    private DoctorClinicProfileRepository doctorClinicProfileRepository;
    
    @Autowired
    private SolrClinicalNotesService solrClinicalNotesService;
    
    @Autowired
    private ReferenceRepository referenceRepository;
    
//    @Scheduled(fixedRate = 900000)
    public void checkResources() {
	System.out.println(">>> Scheduled test service <<<");
	List<TransactionalCollection> transactionalCollections = null;
	try {
	    transactionalCollections = transnationalRepositiory.findByIsCached(false);
	    if (transactionalCollections != null) {
		for (TransactionalCollection transactionalCollection : transactionalCollections) {
			if(transactionalCollection.getResourceId() != null)
		    switch (transactionalCollection.getResource()) {

		    case PATIENT:
			checkPatient(transactionalCollection.getResourceId());
			break;
		    case DRUG:
			checkDrug(transactionalCollection.getResourceId());
			break;
		    case LABTEST:
			checkLabTest(transactionalCollection.getResourceId());
			break;
		    case COMPLAINT:
			checkComplaint(transactionalCollection.getResourceId());
			break;
		    case DIAGNOSIS:
			checkDiagnosis(transactionalCollection.getResourceId());
			break;
		    case DIAGRAM:
			checkDiagrams(transactionalCollection.getResourceId());
			break;
		    case INVESTIGATION:
			checkInvestigation(transactionalCollection.getResourceId());
			break;
		    case NOTES:
			checkNotes(transactionalCollection.getResourceId());
			break;
		    case OBSERVATION:
			checkObservation(transactionalCollection.getResourceId());
			break;
		    case COUNTRY:
				checkCountry(transactionalCollection.getResourceId());
			break;
		    case STATE:
				checkState(transactionalCollection.getResourceId());
			break;
		    case CITY:
				checkCity(transactionalCollection.getResourceId());
			break;
		    case LANDMARKLOCALITY:
				checkLandmarkLocality(transactionalCollection.getResourceId());
			break;
		    case DOCTOR:
				checkDoctor(transactionalCollection.getResourceId());
			break;
		    case LOCATION:
				checkLocation(transactionalCollection.getResourceId());
			break;
				
		    default:
			break;
		    }
		}
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	}
    }
    
	@Override
    public void addResource(String resourceId, Resource resource, boolean isCached) {
	TransactionalCollection transactionalCollection = null;
	try {
	    transactionalCollection = transnationalRepositiory.findByResourceIdAndResource(resourceId, resource.getType());
	    if (transactionalCollection == null || !isCached) {
		transactionalCollection = new TransactionalCollection();
		transactionalCollection.setResourceId(resourceId);
		transactionalCollection.setResource(resource);
		transactionalCollection.setIsCached(isCached);
		transnationalRepositiory.save(transactionalCollection);
	    }
	    else if (transactionalCollection != null) {
		transactionalCollection.setIsCached(isCached);
		transnationalRepositiory.save(transactionalCollection);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Forbidden, e.getMessage());
	}
    }

    @Override
    public void checkPatient(String id) {
	try {
	    UserCollection userCollection = userRepository.findOne(id);
	    List<PatientCollection> patientCollections = patientRepository.findByUserId(id);
	    if (userCollection != null && patientCollections != null) {
	    	for(PatientCollection patientCollection : patientCollections){
	    		PatientAdmissionCollection patientAdmissionCollection = patientAdmissionRepository.findByUserIdAndDoctorId(id, patientCollection.getDoctorId());
	    		AddressCollection addressCollection = null;
	    		if (patientCollection.getAddressId() != null)
	    		    addressCollection = addressRepository.findOne(patientCollection.getAddressId());
	    		SolrPatientDocument patientDocument = new SolrPatientDocument();

	    		if (patientCollection.getDob() != null) {
	    		    patientDocument.setDays(patientCollection.getDob().getDays() + "");
	    		    patientDocument.setMonths(patientCollection.getDob().getMonths() + "");
	    		    patientDocument.setYears(patientCollection.getDob().getYears() + "");
	    		}
	    		BeanUtil.map(userCollection, patientDocument);
	    		if (patientCollection != null)BeanUtil.map(patientCollection, patientDocument);
	    		if (patientAdmissionCollection != null)BeanUtil.map(patientAdmissionCollection, patientDocument);
	    		if (addressCollection != null)BeanUtil.map(addressCollection, patientDocument);

	    		if (patientCollection != null)patientDocument.setId(patientCollection.getId());
	    		if (patientAdmissionCollection != null){
	    			if(patientAdmissionCollection.getReferredBy() != null){
	    				ReferencesCollection referencesCollection = referenceRepository.findOne(patientAdmissionCollection.getReferredBy());
	    				if(referencesCollection != null)patientDocument.setReferredBy(referencesCollection.getReference());
	    			}
	    			}

	    		if (patientCollection != null)solrRegistrationService.editPatient(patientDocument);
	    	}
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	}
    }

    @Override
    public void checkDrug(String id) {
	try {
	    DrugCollection drugCollection = drugRepository.findOne(id);
	    if (drugCollection != null) {
		SolrDrugDocument solrDrugDocument = new SolrDrugDocument();
		BeanUtil.map(drugCollection, solrDrugDocument);
		if (drugCollection.getDrugType() != null) {
		    solrDrugDocument.setDrugTypeId(drugCollection.getDrugType().getId());
		    solrDrugDocument.setDrugType(drugCollection.getDrugType().getType());
		}
		solrPrescriptionService.addDrug(solrDrugDocument);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	}
    }

    @Override
    public void checkLabTest(String id) {
	try {
	    LabTestCollection labTestCollection = labTestRepository.findOne(id);
	    if (labTestCollection != null) {
		SolrLabTestDocument solrLabTestDocument = new SolrLabTestDocument();
		BeanUtil.map(labTestCollection, solrLabTestDocument);
		solrPrescriptionService.addLabTest(solrLabTestDocument);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	}
    }

    @Override
    public void checkComplaint(String id) {
	try {
	    ComplaintCollection complaintCollection = complaintRepository.findOne(id);
	    if (complaintCollection != null) {
		SolrComplaintsDocument solrComplaintsDocument = new SolrComplaintsDocument();
		BeanUtil.map(complaintCollection, solrComplaintsDocument);
		solrClinicalNotesService.addComplaints(solrComplaintsDocument);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	}
    }

    @Override
    public void checkObservation(String id) {
	try {
	    ObservationCollection observationCollection = observationRepository.findOne(id);
	    if (observationCollection != null) {
		SolrObservationsDocument solrObservationsDocument = new SolrObservationsDocument();
		BeanUtil.map(observationCollection, solrObservationsDocument);
		solrClinicalNotesService.addObservations(solrObservationsDocument);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	}
    }

    @Override
    public void checkInvestigation(String id) {
	try {
	    InvestigationCollection investigationCollection = investigationRepository.findOne(id);
	    if (investigationCollection != null) {
		SolrInvestigationsDocument solrInvestigationsDocument = new SolrInvestigationsDocument();
		BeanUtil.map(investigationCollection, solrInvestigationsDocument);
		solrClinicalNotesService.addInvestigations(solrInvestigationsDocument);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	}
    }

    @Override
    public void checkDiagnosis(String id) {
	try {
	    DiagnosisCollection diagnosisCollection = diagnosisRepository.findOne(id);
	    if (diagnosisCollection != null) {
		SolrDiagnosesDocument solrDiagnosesDocument = new SolrDiagnosesDocument();
		BeanUtil.map(diagnosisCollection, solrDiagnosesDocument);
		solrClinicalNotesService.addDiagnoses(solrDiagnosesDocument);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	}
    }

    @Override
    public void checkNotes(String id) {
	try {
	    NotesCollection notesCollection = notesRepository.findOne(id);
	    if (notesCollection != null) {
		SolrNotesDocument solrNotesDocument = new SolrNotesDocument();
		BeanUtil.map(notesCollection, solrNotesDocument);
		solrClinicalNotesService.addNotes(solrNotesDocument);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	}
    }

    @Override
    public void checkDiagrams(String id) {
	try {
	    DiagramsCollection diagramsCollection = diagramsRepository.findOne(id);
	    if (diagramsCollection != null) {
		SolrDiagramsDocument solrDiagramsDocument = new SolrDiagramsDocument();
		BeanUtil.map(diagramsCollection, solrDiagramsDocument);
		solrClinicalNotesService.addDiagrams(solrDiagramsDocument);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	}
    }
    
    private void checkLocation(String resourceId) {
    	try {
    	    LocationCollection locationCollection = locationRepository.findOne(resourceId);
    	    if (locationCollection != null) {
    		DoctorLocation doctorLocation = new DoctorLocation();
    		BeanUtil.map(locationCollection, doctorLocation);
    		doctorLocation.setLocationId(locationCollection.getId());
    		doctorLocation.setLocationPhoneNumber(locationCollection.getMobileNumber());
    		solrRegistrationService.editLocation(doctorLocation);
    	    }
    	} catch (Exception e) {
    	    e.printStackTrace();
    	    logger.error(e);
    	}	
	}

	private void checkDoctor(String resourceId) {
		try {
		    DoctorCollection doctorCollection = doctorRepository.findByUserId(resourceId);
		    UserCollection userCollection = userRepository.findOne(resourceId);
		    if(doctorCollection != null && userCollection != null){
		    	List<UserLocationCollection> userLocationCollections = userLocationRepository.findByUserId(resourceId);
			    for(UserLocationCollection collection : userLocationCollections){
			    	LocationCollection locationCollection = locationRepository.findOne(collection.getLocationId());
			    	DoctorClinicProfileCollection clinicProfileCollection = doctorClinicProfileRepository.findByLocationId(collection.getId());
			    	SolrDoctorDocument doctorDocument = new SolrDoctorDocument();
			    	if(locationCollection != null)BeanUtil.map(locationCollection, doctorDocument);
			    	if(userCollection != null)BeanUtil.map(userCollection, doctorDocument);
			    	if(doctorCollection != null)BeanUtil.map(doctorCollection, doctorDocument);
			    	if(clinicProfileCollection != null)BeanUtil.map(clinicProfileCollection, doctorDocument);
			    	else {
			    		doctorDocument.setWorkingSchedules(null);
			    	}
			    	if(locationCollection != null)doctorDocument.setLocationId(locationCollection.getId());
			    	solrRegistrationService.addDoctor(doctorDocument);
			    }
		    }
		} catch (Exception e) {
		    e.printStackTrace();
		    logger.error(e);
		}
	}

	private void checkLandmarkLocality(String resourceId) {
		try {
		    LandmarkLocalityCollection landmarkLocalityCollection = landmarkLocalityRepository.findOne(resourceId);
		    if (landmarkLocalityCollection != null) {
			SolrLocalityLandmarkDocument solrLocalityLandmarkDocument = new SolrLocalityLandmarkDocument();
			BeanUtil.map(landmarkLocalityCollection, solrLocalityLandmarkDocument);
			solrCityService.addLocalityLandmark(solrLocalityLandmarkDocument);
		    }
		} catch (Exception e) {
		    e.printStackTrace();
		    logger.error(e);
		}
	}

	private void checkCity(String resourceId) {
		try {
		    CityCollection cityCollection = cityRepository.findOne(resourceId);
		    if (cityCollection != null) {
			SolrCityDocument solrCityDocument = new SolrCityDocument();
			BeanUtil.map(cityCollection, solrCityDocument);
			solrCityService.addCities(solrCityDocument);
		    }
		} catch (Exception e) {
		    e.printStackTrace();
		    logger.error(e);
		}
	}

	private void checkState(String resourceId) {
		try {
		    StateCollection stateCollection = stateRepository.findOne(resourceId);
		    if (stateCollection != null) {
			SolrStateDocument solrStateDocument = new SolrStateDocument();
			BeanUtil.map(stateCollection, solrStateDocument);
			solrCityService.addState(solrStateDocument);
		    }
		} catch (Exception e) {
		    e.printStackTrace();
		    logger.error(e);
		}
	}

	private void checkCountry(String resourceId) {
		try {
		    CountryCollection countryCollection = countryRepository.findOne(resourceId);
		    if (countryCollection != null) {
			SolrCountryDocument solrCountryDocument = new SolrCountryDocument();
			BeanUtil.map(countryCollection, solrCountryDocument);
			solrCityService.addCountry(solrCountryDocument);
		    }
		} catch (Exception e) {
		    e.printStackTrace();
		    logger.error(e);
		}
	}
}
