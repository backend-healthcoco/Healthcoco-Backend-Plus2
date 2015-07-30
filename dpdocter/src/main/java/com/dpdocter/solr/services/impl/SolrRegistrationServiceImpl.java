package com.dpdocter.solr.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.solr.document.SolrPatientDocument;
import com.dpdocter.solr.repository.SolrPatientRepository;
import com.dpdocter.solr.services.SolrRegistrationService;

@Service
public class SolrRegistrationServiceImpl implements SolrRegistrationService {
    @Autowired
    private SolrPatientRepository solrPatientRepository;

    @Override
    public boolean addPatient(SolrPatientDocument request) {
	boolean response = false;
	try {
	    solrPatientRepository.save(request);
	    response = true;
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Saving Patient");
	}
	return response;
    }

    @Override
    public boolean editPatient(SolrPatientDocument request) {
	boolean response = false;
	try {
	    solrPatientRepository.save(request);
	    response = true;
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Editing Patient");
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
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Deleting Patient");
	}
	return response;
    }

    @Override
    public List<SolrPatientDocument> searchPatient(String doctorId, String locationId, String hospitalId, String searchTerm) {
	List<SolrPatientDocument> response = null;
	try {
	    response = solrPatientRepository.find(doctorId, locationId, hospitalId, searchTerm);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Searching Patients");
	}
	return response;
    }

}
