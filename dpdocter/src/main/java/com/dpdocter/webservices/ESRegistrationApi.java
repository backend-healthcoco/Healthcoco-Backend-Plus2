package com.dpdocter.webservices;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import org.springframework.http.MediaType;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dpdocter.elasticsearch.beans.AdvancedSearch;
import com.dpdocter.elasticsearch.document.ESPatientDocument;
import com.dpdocter.elasticsearch.response.ESPatientResponseDetails;
import com.dpdocter.elasticsearch.services.ESRegistrationService;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
(PathProxy.SOLR_REGISTRATION_BASEURL)
@Produces(MediaType.APPLICATION_JSON_VALUE)
@Consumes(MediaType.APPLICATION_JSON_VALUE)
@Api(value = PathProxy.SOLR_REGISTRATION_BASEURL, description = "Endpoint for solr register")
public class ESRegistrationApi {

    private static Logger logger = LogManager.getLogger(ESRegistrationApi.class.getName());

    @Autowired
    private ESRegistrationService solrRegistrationService;

    @Value(value = "${image.path}")
    private String imagePath;

    
    @GetMapping(value = PathProxy.SolrRegistrationUrls.SEARCH_PATIENT)
    @ApiOperation(value = PathProxy.SolrRegistrationUrls.SEARCH_PATIENT, notes = PathProxy.SolrRegistrationUrls.SEARCH_PATIENT)
    public Response<ESPatientResponseDetails> searchPatient(@PathVariable(value = "locationId") String locationId, @PathVariable(value = "hospitalId") String hospitalId,
	    @PathVariable(value = "searchTerm") String searchTerm, @RequestParam("page") long page, @RequestParam("size") int size,
	    @RequestParam("doctorId") String doctorId, @RequestParam("role") String role) {
	if (DPDoctorUtils.anyStringEmpty(locationId, hospitalId, searchTerm)) {
	    logger.warn("Location Id, Hospital Id and Search Term Cannot Be Empty");
	    throw new BusinessException(ServiceError.InvalidInput, "Location Id, Hospital Id and Search Term Cannot Be Empty");
	}

	ESPatientResponseDetails patients = solrRegistrationService.searchPatient(locationId, hospitalId, searchTerm, page, size, doctorId, role);

	Response<ESPatientResponseDetails> response = new Response<ESPatientResponseDetails>();
	response.setData(patients);
	return response;
    }

    
    @PostMapping(value = PathProxy.SolrRegistrationUrls.SEARCH_PATIENT_ADV)
    @ApiOperation(value = PathProxy.SolrRegistrationUrls.SEARCH_PATIENT_ADV, notes = PathProxy.SolrRegistrationUrls.SEARCH_PATIENT_ADV)
    public Response<ESPatientResponseDetails> searchPatient(AdvancedSearch request) {

	if (request == null || DPDoctorUtils.anyStringEmpty(request.getLocationId(), request.getHospitalId())) {
	    logger.warn("Invalid Input");
	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
	}

	ESPatientResponseDetails patients = solrRegistrationService.searchPatient(request);

	Response<ESPatientResponseDetails> response = new Response<ESPatientResponseDetails>();
	response.setData(patients);
	return response;
    }
    
    
	@GetMapping(value = PathProxy.SolrRegistrationUrls.SEARCH_DELETED_PATIENT)
	@ApiOperation(value = PathProxy.SolrRegistrationUrls.SEARCH_DELETED_PATIENT, notes = PathProxy.SolrRegistrationUrls.SEARCH_DELETED_PATIENT)
	public Response<ESPatientDocument> searchDeletedPatient(@PathVariable("doctorId") String doctorId,
			@PathVariable("locationId") String locationId, @PathVariable("hospitalId") String hospitalId,
			@RequestParam("page") int page, @RequestParam("size") int size, @RequestParam("searchTerm") String searchTerm,
			@RequestParam("sortBy") String sortBy) {
		if (DPDoctorUtils.anyStringEmpty(doctorId, locationId, hospitalId)) {
			throw new BusinessException(ServiceError.InvalidInput, "Doctor Id, locationId, hospitalId could not null");

		}
		Response<ESPatientDocument> response = new Response<ESPatientDocument>();
		response.setDataList(solrRegistrationService.searchDeletedPatient(doctorId, locationId, hospitalId, page, size, searchTerm, sortBy));
		return response;
	}
}
