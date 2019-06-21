package com.dpdocter.webservices;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dpdocter.elasticsearch.services.ESTreatmentService;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Component
@Path(PathProxy.SOLR_PATIENT_TREATMENT_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.SOLR_PATIENT_TREATMENT_BASE_URL, description = "Endpoint for es clinical notes")
public class ESTreatmentAPI {

	private static Logger logger = Logger.getLogger(ESTreatmentAPI.class.getName());
	
    @Autowired
    private ESTreatmentService esTreatmentService;

    @Path(value = PathProxy.SolrPatientTreatmentUrls.SEARCH)
    @GET
    @ApiOperation(value = PathProxy.SolrPatientTreatmentUrls.SEARCH, notes = PathProxy.SolrPatientTreatmentUrls.SEARCH)
    public Response<Object> search(@PathParam("type") String type, @PathParam("range") String range, @QueryParam("page") int page, @QueryParam("size") int size,
	    @QueryParam(value = "doctorId") String doctorId, @QueryParam(value = "locationId") String locationId,
	    @QueryParam(value = "hospitalId") String hospitalId, @DefaultValue("0") @QueryParam(value = "updatedTime") String updatedTime,
	    @DefaultValue("true") @QueryParam(value = "discarded") Boolean discarded, @QueryParam(value = "searchTerm") String searchTerm) {
    	if (DPDoctorUtils.anyStringEmpty(type, range, locationId, hospitalId)) {
    	    logger.warn("Invalid Input");
    	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
    	}
    	Response<Object> response = esTreatmentService.search(type, range, page, size, doctorId, locationId, hospitalId, updatedTime, discarded, searchTerm);
		return response;
    }

}
