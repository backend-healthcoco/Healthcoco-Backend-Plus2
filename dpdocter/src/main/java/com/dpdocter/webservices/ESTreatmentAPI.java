package com.dpdocter.webservices;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.Produces;
import org.springframework.http.MediaType;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dpdocter.elasticsearch.services.ESTreatmentService;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
(PathProxy.SOLR_PATIENT_TREATMENT_BASE_URL)
@Produces(MediaType.APPLICATION_JSON_VALUE)
@Consumes(MediaType.APPLICATION_JSON_VALUE)
@Api(value = PathProxy.SOLR_PATIENT_TREATMENT_BASE_URL, description = "Endpoint for es clinical notes")
public class ESTreatmentAPI {

	private static Logger logger = LogManager.getLogger(ESTreatmentAPI.class.getName());
	
    @Autowired
    private ESTreatmentService esTreatmentService;

    
    @GetMapping(value = PathProxy.SolrPatientTreatmentUrls.SEARCH)
    @ApiOperation(value = PathProxy.SolrPatientTreatmentUrls.SEARCH, notes = PathProxy.SolrPatientTreatmentUrls.SEARCH)
    public Response<Object> search(@PathVariable("type") String type, @PathVariable("range") String range, @RequestParam("page") int page, @RequestParam("size") int size,
	    @RequestParam(value = "doctorId") String doctorId, @RequestParam(value = "locationId") String locationId,
	    @RequestParam(value = "hospitalId") String hospitalId, @DefaultValue("0") @RequestParam(value = "updatedTime") String updatedTime,
	      @RequestParam(value = "discarded") Boolean discarded, @RequestParam(value = "searchTerm") String searchTerm) {
    	if (DPDoctorUtils.anyStringEmpty(type, range, locationId, hospitalId)) {
    	    logger.warn("Invalid Input");
    	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
    	}
    	Response<Object> response = esTreatmentService.search(type, range, page, size, doctorId, locationId, hospitalId, updatedTime, discarded, searchTerm);
		return response;
    }

}
