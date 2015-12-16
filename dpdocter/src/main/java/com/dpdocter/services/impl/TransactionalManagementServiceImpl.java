package com.dpdocter.services.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dpdocter.collections.AddressCollection;
import com.dpdocter.collections.ComplaintCollection;
import com.dpdocter.collections.DiagnosisCollection;
import com.dpdocter.collections.DiagramsCollection;
import com.dpdocter.collections.DrugCollection;
import com.dpdocter.collections.InvestigationCollection;
import com.dpdocter.collections.LabTestCollection;
import com.dpdocter.collections.NotesCollection;
import com.dpdocter.collections.ObservationCollection;
import com.dpdocter.collections.PatientAdmissionCollection;
import com.dpdocter.collections.PatientCollection;
import com.dpdocter.collections.TransactionalCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.enums.Resource;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.AddressRepository;
import com.dpdocter.repository.ComplaintRepository;
import com.dpdocter.repository.DiagnosisRepository;
import com.dpdocter.repository.DiagramsRepository;
import com.dpdocter.repository.DrugRepository;
import com.dpdocter.repository.InvestigationRepository;
import com.dpdocter.repository.LabTestRepository;
import com.dpdocter.repository.NotesRepository;
import com.dpdocter.repository.ObservationRepository;
import com.dpdocter.repository.PatientAdmissionRepository;
import com.dpdocter.repository.PatientRepository;
import com.dpdocter.repository.TransnationalRepositiory;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.services.TransactionalManagementService;
import com.dpdocter.solr.document.SolrComplaintsDocument;
import com.dpdocter.solr.document.SolrDiagnosesDocument;
import com.dpdocter.solr.document.SolrDiagramsDocument;
import com.dpdocter.solr.document.SolrDrugDocument;
import com.dpdocter.solr.document.SolrInvestigationsDocument;
import com.dpdocter.solr.document.SolrLabTestDocument;
import com.dpdocter.solr.document.SolrNotesDocument;
import com.dpdocter.solr.document.SolrObservationsDocument;
import com.dpdocter.solr.document.SolrPatientDocument;
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
    private SolrClinicalNotesService solrClinicalNotesService;

    // @Scheduled(fixedRate = 10000)
    public void checkResources() {
	System.out.println(">>> Scheduled test service <<<");
	List<TransactionalCollection> transactionalCollections = null;
	try {
	    transactionalCollections = transnationalRepositiory.findByIsCached(false);
	    if (transactionalCollections != null) {
		for (TransactionalCollection transactionalCollection : transactionalCollections) {
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
	    if (transactionalCollection == null) {
		transactionalCollection = new TransactionalCollection();
		transactionalCollection.setResourceId(resourceId);
		transactionalCollection.setResource(resource);
		transactionalCollection.setIsCached(isCached);
		transnationalRepositiory.save(transactionalCollection);
	    }
	    if (transactionalCollection != null) {
		transactionalCollection.setIsCached(isCached);
		transnationalRepositiory.save(transactionalCollection);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
    }

    @Override
    public void checkPatient(String id) {
	try {
	    UserCollection userCollection = userRepository.findOne(id);
	    PatientCollection patientCollection = patientRepository.findByUserId(id);
	    if (userCollection != null && patientCollection != null) {
		PatientAdmissionCollection patientAdmissionCollection = patientAdmissionRepository.findByUserIdAndDoctorId(id, patientCollection.getDoctorId());
		AddressCollection addressCollection = addressRepository.findOne(patientCollection.getAddressId());
		SolrPatientDocument patientDocument = new SolrPatientDocument();

		if(patientCollection.getDob() != null){
			patientDocument.setDays(patientCollection.getDob().getDays() + "");
			patientDocument.setMonths(patientCollection.getDob().getMonths() + "");
			patientDocument.setYears(patientCollection.getDob().getYears() + "");
		}
		BeanUtil.map(userCollection, patientDocument);
		BeanUtil.map(patientCollection, patientDocument);
		BeanUtil.map(patientAdmissionCollection, patientDocument);
		BeanUtil.map(addressCollection, patientDocument);

		solrRegistrationService.editPatient(patientDocument);
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
		if(drugCollection.getDrugType() != null){
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
}
