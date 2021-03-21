package com.dpdocter.webservices.v2;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.dpdocter.elasticsearch.beans.AdvancedSearch;
import com.dpdocter.elasticsearch.response.v2.ESPatientResponseDetails;
import com.dpdocter.elasticsearch.services.v2.ESRegistrationService;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Component(value = "ESRegistrationApiV2")
@Path(PathProxy.SOLR_REGISTRATION_BASEURL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.SOLR_REGISTRATION_BASEURL, description = "Endpoint for solr register")
public class ESRegistrationApi {

    private static Logger logger = LogManager.getLogger(ESRegistrationApi.class.getName());

    @Autowired
    private ESRegistrationService solrRegistrationService;

    @Value(value = "${image.path}")
    private String imagePath;

    @Path(value = PathProxy.SolrRegistrationUrls.SEARCH_PATIENT)
    @GET
    @ApiOperation(value = PathProxy.SolrRegistrationUrls.SEARCH_PATIENT, notes = PathProxy.SolrRegistrationUrls.SEARCH_PATIENT)
    public Response<ESPatientResponseDetails> searchPatient(@PathParam(value = "locationId") String locationId, @PathParam(value = "hospitalId") String hospitalId,
	    @PathParam(value = "searchTerm") String searchTerm, @QueryParam("page") int page, @QueryParam("size") int size,
	    @QueryParam("doctorId") String doctorId, @QueryParam("role") String role) {
	if (DPDoctorUtils.anyStringEmpty(locationId, hospitalId, searchTerm)) {
	    logger.warn("Location Id, Hospital Id and Search Term Cannot Be Empty");
	    throw new BusinessException(ServiceError.InvalidInput, "Location Id, Hospital Id and Search Term Cannot Be Empty");
	}

	ESPatientResponseDetails patients = solrRegistrationService.searchPatient(locationId, hospitalId, searchTerm, page, size, doctorId, role);

	Response<ESPatientResponseDetails> response = new Response<ESPatientResponseDetails>();
	response.setData(patients);
	return response;
    }

    @Path(value = PathProxy.SolrRegistrationUrls.SEARCH_PATIENT_ADV)
    @POST
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
}
