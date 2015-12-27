package com.dpdocter.solr.webservices;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.solr.beans.AdvancedSearch;
import com.dpdocter.solr.response.SolrPatientResponseDetails;
import com.dpdocter.solr.services.SolrRegistrationService;
import com.dpdocter.webservices.PathProxy;
import common.util.web.DPDoctorUtils;
import common.util.web.Response;

@Component
@Path(PathProxy.SOLR_REGISTRATION_BASEURL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SolrRegistrationApi {

    private static Logger logger = Logger.getLogger(SolrRegistrationApi.class.getName());

    @Autowired
    private SolrRegistrationService solrRegistrationService;

    @Context
    private UriInfo uriInfo;

    @Value(value = "${IMAGE_URL_ROOT_PATH}")
    private String imageUrlRootPath;

    @Path(value = PathProxy.SolrRegistrationUrls.SEARCH_PATIENT)
    @GET
    public Response<SolrPatientResponseDetails> searchPatient(@PathParam(value = "doctorId") String doctorId,
	    @PathParam(value = "locationId") String locationId, @PathParam(value = "hospitalId") String hospitalId,
	    @PathParam(value = "searchTerm") String searchTerm, @QueryParam("page") int page, @QueryParam("size") int size) {
	if (DPDoctorUtils.anyStringEmpty(doctorId, locationId, hospitalId, searchTerm)) {
	    logger.warn("Doctor Id, Location Id, Hospital Id and Search Term Cannot Be Empty");
	    throw new BusinessException(ServiceError.InvalidInput, "Doctor Id, Location Id, Hospital Id and Search Term Cannot Be Empty");
	}
	SolrPatientResponseDetails patients = solrRegistrationService.searchPatient(doctorId, locationId, hospitalId, searchTerm, page, size, uriInfo);
	Response<SolrPatientResponseDetails> response = new Response<SolrPatientResponseDetails>();
	response.setData(patients);
	return response;
    }

    @Path(value = PathProxy.SolrRegistrationUrls.SEARCH_PATIENT_ADV)
    @POST
    public Response<SolrPatientResponseDetails> searchPatient(AdvancedSearch request) {

	if (request == null) {
	    logger.warn("Search Request Cannot Be Empty");
	    throw new BusinessException(ServiceError.InvalidInput, "Search Request Cannot Be Empty");
	}

	SolrPatientResponseDetails patients = solrRegistrationService.searchPatient(request, uriInfo);

	Response<SolrPatientResponseDetails> response = new Response<SolrPatientResponseDetails>();
	response.setData(patients);
	return response;
    }
}
