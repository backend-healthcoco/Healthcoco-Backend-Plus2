package com.dpdocter.solr.services.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.stereotype.Service;

import com.dpdocter.enums.Resource;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.services.TransactionalManagementService;
import com.dpdocter.solr.beans.AdvancedSearch;
import com.dpdocter.solr.beans.AdvancedSearchParameter;
import com.dpdocter.solr.document.SolrPatientDocument;
import com.dpdocter.solr.repository.SolrPatientRepository;
import com.dpdocter.solr.response.SolrPatientResponse;
import com.dpdocter.solr.response.SolrPatientResponseDetails;
import com.dpdocter.solr.services.SolrRegistrationService;

import common.util.web.DPDoctorUtils;

@Service
public class SolrRegistrationServiceImpl implements SolrRegistrationService {

    private static Logger logger = Logger.getLogger(SolrRegistrationServiceImpl.class.getName());

    @Autowired
    private SolrPatientRepository solrPatientRepository;

    @Autowired
    private SolrTemplate solrTemplate;

    @Autowired
    private TransactionalManagementService transnationalService;

    @Override
    public boolean addPatient(SolrPatientDocument request) {
	boolean response = false;
	try {
	    solrPatientRepository.save(request);
	    response = true;
	    transnationalService.addResource(request.getUserId(), Resource.PATIENT, true);
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Occurred While Saving Patient");
	    // throw new BusinessException(ServiceError.Unknown,
	    // "Error Occurred While Saving Patient");
	}
	return response;
    }

    @Override
    public boolean editPatient(SolrPatientDocument request) {
	boolean response = false;
	try {
	    solrPatientRepository.save(request);
	    response = true;
	    transnationalService.addResource(request.getUserId(), Resource.PATIENT, true);
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Occurred While Editing Patient");
	    // throw new BusinessException(ServiceError.Unknown,
	    // "Error Occurred While Editing Patient");
	}
	return response;
    }

    @Override
    public boolean deletePatient(String id) {
	boolean response = false;
	try {
	    solrPatientRepository.delete(id);
	    response = true;
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Occurred While Deleting Patient");
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Deleting Patient");
	}
	return response;
    }

    @Override
    public SolrPatientResponseDetails searchPatient(String doctorId, String locationId, String hospitalId, String searchTerm, int page, int size) {
	List<SolrPatientDocument> patients = new ArrayList<SolrPatientDocument>();
	List<SolrPatientResponse> response = null;
	SolrPatientResponseDetails responseDetails = null;
	try {
		List<SolrPatientDocument> patientDocuments =  new ArrayList<SolrPatientDocument>();
	    if (size > 0){
	    	patientDocuments = solrPatientRepository.findByFirstName(doctorId, locationId, hospitalId, searchTerm, new PageRequest(page, size, Direction.DESC, "createdTime"));
	    	if(patientDocuments != null)patients.addAll(patientDocuments);
	    	
	    	patientDocuments = solrPatientRepository.findByEmailAddress(doctorId, locationId, hospitalId, searchTerm, new PageRequest(page, size, Direction.DESC, "createdTime"));
	    	if(patientDocuments != null)patients.addAll(patientDocuments);
	    	
	    	patientDocuments = solrPatientRepository.findByMobileNumber(doctorId, locationId, hospitalId, searchTerm, new PageRequest(page, size, Direction.DESC, "createdTime"));
	    	if(patientDocuments != null)patients.addAll(patientDocuments);
	    	
	    	patientDocuments = solrPatientRepository.findByPID(doctorId, locationId, hospitalId, searchTerm, new PageRequest(page, size, Direction.DESC, "createdTime"));
	    	if(patientDocuments != null)patients.addAll(patientDocuments);
	    }
		
	    else{
	    	patientDocuments = solrPatientRepository.findByFirstName(doctorId, locationId, hospitalId, searchTerm, new Sort(Sort.Direction.DESC, "createdTime"));
	    	if(patientDocuments != null)patients.addAll(patientDocuments);
	    	
	    	patientDocuments = solrPatientRepository.findByEmailAddress(doctorId, locationId, hospitalId, searchTerm, new Sort(Sort.Direction.DESC, "createdTime"));
	    	if(patientDocuments != null)patients.addAll(patientDocuments);
	    	
	    	patientDocuments = solrPatientRepository.findByMobileNumber(doctorId, locationId, hospitalId, searchTerm, new Sort(Sort.Direction.DESC, "createdTime"));
	    	if(patientDocuments != null)patients.addAll(patientDocuments);
	    	
	    	patientDocuments = solrPatientRepository.findByPID(doctorId, locationId, hospitalId, searchTerm, new Sort(Sort.Direction.DESC, "createdTime"));
	    	if(patientDocuments != null)patients.addAll(patientDocuments);
	    }
	    if(patients != null && !patients.isEmpty()){
	    	response = new ArrayList<SolrPatientResponse>();
	    	BeanUtil.map(patients, response);
	    	responseDetails = new SolrPatientResponseDetails();
	    	responseDetails.setPatients(response);
	    	responseDetails.setTotalSize(solrPatientRepository.count(doctorId, locationId, hospitalId, searchTerm));
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Occurred While Searching Patient");
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Searching Patients");
	}
	return responseDetails;
    }

    @Override
    public SolrPatientResponseDetails searchPatient(AdvancedSearch request) {
	List<SolrPatientDocument> patients = null;
	List<SolrPatientResponse> response = new ArrayList<SolrPatientResponse>();
	SolrPatientResponseDetails responseDetails = null;
	try {
	    Criteria advancedCriteria = createAdvancedSearchCriteria(request);

	    SimpleQuery query = new SimpleQuery(advancedCriteria);

	    solrTemplate.setSolrCore("patients");
	    if (request.getSize() > 0)
		patients = solrTemplate.queryForPage(
			query.setPageRequest(new PageRequest(request.getSize(), request.getPage(), Direction.DESC, "createdTime")), SolrPatientDocument.class)
			.getContent();
	    else
		patients = solrTemplate.queryForPage(query.addSort(new Sort(Sort.Direction.DESC, "createdTime")), SolrPatientDocument.class).getContent();

	    if(patients!= null){
	    	BeanUtil.map(patients, response);
	    	responseDetails = new SolrPatientResponseDetails();
	    	responseDetails.setTotalSize(solrTemplate.count(query));
	    }
	    
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Occurred While Searching Patients");
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Searching Patients");
	}
	return responseDetails;
    }

    private Criteria createAdvancedSearchCriteria(AdvancedSearch request) {
	String doctorId = request.getDoctorId();
	String locationId = request.getLocationId();
	String hospitalId = request.getHospitalId();

	Criteria advancedCriteria = Criteria.where("doctorId").is(doctorId).and("locationId").is(locationId).and("hospitalId").is(hospitalId);

	if (request.getSearchParameters() != null && !request.getSearchParameters().isEmpty()) {
	    for (AdvancedSearchParameter searchParameter : request.getSearchParameters()) {
		String searchValue = searchParameter.getSearchValue();
		String searchType = searchParameter.getSearchType().getSearchType();
		if (!DPDoctorUtils.anyStringEmpty(searchValue, searchType)) {
		    if (searchType.equalsIgnoreCase("DOB")) {
			if (advancedCriteria == null) {
			    advancedCriteria = new Criteria("days").contains(searchValue).and("months").contains(searchValue).and("years")
				    .contains(searchValue);
			} else {
			    advancedCriteria = advancedCriteria.and("days").contains(searchValue).and("months").contains(searchValue).and("years")
				    .contains(searchValue);
			}
		    } else {
			if (advancedCriteria == null) {
			    advancedCriteria = new Criteria(searchType).contains(searchValue);
			} else {
			    advancedCriteria = advancedCriteria.and(searchType).contains(searchValue);
			}
		    }

		}
	    }
	}

	return advancedCriteria;
    }

    // @Override
    // public List<SolrPatientDocument> searchPatientByFirstName(String
    // doctorId, String locationId, String hospitalId, String searchValue) {
    // List<SolrPatientDocument> response = null;
    // try {
    // response = solrPatientRepository.findByFirstName(doctorId, locationId,
    // hospitalId, searchValue);
    // } catch (Exception e) {
    // e.printStackTrace();
    // logger.error(e + " Error Occurred While Searching Patients");
    // throw new BusinessException(ServiceError.Unknown,
    // "Error Occurred While Searching Patients");
    // }
    // return response;
    //
    // }
    //
    // @Override
    // public List<SolrPatientDocument> searchPatientByMiddleName(String
    // doctorId, String locationId, String hospitalId, String searchValue) {
    // List<SolrPatientDocument> response = null;
    // try {
    // response = solrPatientRepository.findByMiddleName(doctorId, locationId,
    // hospitalId, searchValue);
    // } catch (Exception e) {
    // e.printStackTrace();
    // logger.error(e + " Error Occurred While Searching Patients");
    // throw new BusinessException(ServiceError.Unknown,
    // "Error Occurred While Searching Patients");
    // }
    // return response;
    // }
    //
    // @Override
    // public List<SolrPatientDocument> searchPatientByLastName(String doctorId,
    // String locationId, String hospitalId, String searchValue) {
    // List<SolrPatientDocument> response = null;
    // try {
    // response = solrPatientRepository.findByLastName(doctorId, locationId,
    // hospitalId, searchValue);
    // } catch (Exception e) {
    // e.printStackTrace();
    // logger.error(e + " Error Occurred While Searching Patients");
    // throw new BusinessException(ServiceError.Unknown,
    // "Error Occurred While Searching Patients");
    // }
    // return response;
    //
    // }
    //
    // @Override
    // public List<SolrPatientDocument> searchPatientByPID(String doctorId,
    // String locationId, String hospitalId, String searchValue) {
    // List<SolrPatientDocument> response = null;
    // try {
    // response = solrPatientRepository.findByPID(doctorId, locationId,
    // hospitalId, searchValue);
    // } catch (Exception e) {
    // e.printStackTrace();
    // logger.error(e + " Error Occurred While Searching Patients");
    // throw new BusinessException(ServiceError.Unknown,
    // "Error Occurred While Searching Patients");
    // }
    // return response;
    // }
    //
    // @Override
    // public List<SolrPatientDocument> searchPatientByMobileNumber(String
    // doctorId, String locationId, String hospitalId, String searchValue) {
    // List<SolrPatientDocument> response = null;
    // try {
    // response = solrPatientRepository.findByMobileNumber(doctorId, locationId,
    // hospitalId, searchValue);
    // } catch (Exception e) {
    // e.printStackTrace();
    // logger.error(e + " Error Occurred While Searching Patients");
    // throw new BusinessException(ServiceError.Unknown,
    // "Error Occurred While Searching Patients");
    // }
    // return response;
    // }
    //
    // @Override
    // public List<SolrPatientDocument> searchPatientByEmailAddress(String
    // doctorId, String locationId, String hospitalId, String searchValue) {
    // List<SolrPatientDocument> response = null;
    // try {
    // response = solrPatientRepository.findByEmailAddress(doctorId, locationId,
    // hospitalId, searchValue);
    // } catch (Exception e) {
    // e.printStackTrace();
    // logger.error(e + " Error Occurred While Searching Patients");
    // throw new BusinessException(ServiceError.Unknown,
    // "Error Occurred While Searching Patients");
    // }
    // return response;
    //
    // }
    //
    // @Override
    // public List<SolrPatientDocument> searchPatientByUserName(String doctorId,
    // String locationId, String hospitalId, String searchValue) {
    // List<SolrPatientDocument> response = null;
    // try {
    // response = solrPatientRepository.findByUserName(doctorId, locationId,
    // hospitalId, searchValue);
    // } catch (Exception e) {
    // e.printStackTrace();
    // logger.error(e + " Error Occurred While Searching Patients");
    // throw new BusinessException(ServiceError.Unknown,
    // "Error Occurred While Searching Patients");
    // }
    // return response;
    //
    // }
    //
    // @Override
    // public List<SolrPatientDocument> searchPatientByCity(String doctorId,
    // String locationId, String hospitalId, String searchValue) {
    // List<SolrPatientDocument> response = null;
    // try {
    // response = solrPatientRepository.findByCity(doctorId, locationId,
    // hospitalId, searchValue);
    // } catch (Exception e) {
    // e.printStackTrace();
    // logger.error(e + " Error Occurred While Searching Patients");
    // throw new BusinessException(ServiceError.Unknown,
    // "Error Occurred While Searching Patients");
    // }
    // return response;
    //
    // }
    //
    // @Override
    // public List<SolrPatientDocument> searchPatientByLocality(String doctorId,
    // String locationId, String hospitalId, String searchValue) {
    // List<SolrPatientDocument> response = null;
    // try {
    // response = solrPatientRepository.findByLocality(doctorId, locationId,
    // hospitalId, searchValue);
    // } catch (Exception e) {
    // e.printStackTrace();
    // logger.error(e + " Error Occurred While Searching Patients");
    // throw new BusinessException(ServiceError.Unknown,
    // "Error Occurred While Searching Patients");
    // }
    // return response;
    //
    // }
    //
    // @Override
    // public List<SolrPatientDocument> searchPatientByBloodGroup(String
    // doctorId, String locationId, String hospitalId, String searchValue) {
    // List<SolrPatientDocument> response = null;
    // try {
    // response = solrPatientRepository.findByBloodGroup(doctorId, locationId,
    // hospitalId, searchValue);
    // } catch (Exception e) {
    // e.printStackTrace();
    // logger.error(e + " Error Occurred While Searching Patients");
    // throw new BusinessException(ServiceError.Unknown,
    // "Error Occurred While Searching Patients");
    // }
    // return response;
    //
    // }
    //
    // @Override
    // public List<SolrPatientDocument> searchPatientByReferredBy(String
    // doctorId, String locationId, String hospitalId, String searchValue) {
    // List<SolrPatientDocument> response = null;
    // try {
    // response = solrPatientRepository.findByReferredBy(doctorId, locationId,
    // hospitalId, searchValue);
    // } catch (Exception e) {
    // e.printStackTrace();
    // logger.error(e + " Error Occurred While Searching Patients");
    // throw new BusinessException(ServiceError.Unknown,
    // "Error Occurred While Searching Patients");
    // }
    // return response;
    //
    // }
    //
    // @Override
    // public List<SolrPatientDocument> searchPatientByProfession(String
    // doctorId, String locationId, String hospitalId, String searchValue) {
    // List<SolrPatientDocument> response = null;
    // try {
    // response = solrPatientRepository.findByProfession(doctorId, locationId,
    // hospitalId, searchValue);
    // } catch (Exception e) {
    // e.printStackTrace();
    // logger.error(e + " Error Occurred While Searching Patients");
    // throw new BusinessException(ServiceError.Unknown,
    // "Error Occurred While Searching Patients");
    // }
    // return response;
    //
    // }
    //
    // @Override
    // public List<SolrPatientDocument> searchPatientByPostalCode(String
    // doctorId, String locationId, String hospitalId, String searchValue) {
    // List<SolrPatientDocument> response = null;
    // try {
    // response = solrPatientRepository.findByPostalCode(doctorId, locationId,
    // hospitalId, searchValue);
    // } catch (Exception e) {
    // e.printStackTrace();
    // logger.error(e + " Error Occurred While Searching Patients");
    // throw new BusinessException(ServiceError.Unknown,
    // "Error Occurred While Searching Patients");
    // }
    // return response;
    //
    // }
    //
    // @Override
    // public List<SolrPatientDocument> searchPatientByGender(String doctorId,
    // String locationId, String hospitalId, String searchValue) {
    // List<SolrPatientDocument> response = null;
    // try {
    // response = solrPatientRepository.findByGender(doctorId, locationId,
    // hospitalId, searchValue);
    // } catch (Exception e) {
    // e.printStackTrace();
    // logger.error(e + " Error Occurred While Searching Patients");
    // throw new BusinessException(ServiceError.Unknown,
    // "Error Occurred While Searching Patients");
    // }
    // return response;
    //
    // }

    @Override
    public void patientProfilePicChange(String username, String imageUrl) {
	SolrPatientDocument document = null;
	try {
	    document = solrPatientRepository.findByUserName(username);
	    if (document != null) {
		document.setImageUrl(imageUrl);
		solrPatientRepository.save(document);
		transnationalService.addResource(document.getId(), Resource.PATIENT, true);
	    }

	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Occurred While Searching Patients");
	    // throw new BusinessException(ServiceError.Unknown,
	    // "Error Occurred While Searching Patients");
	}

    }

}
