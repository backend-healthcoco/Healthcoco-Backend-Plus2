package com.dpdocter.webservices;

import java.util.List;

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

import com.dpdocter.elasticsearch.document.ESDentalWorksDocument;
import com.dpdocter.elasticsearch.services.impl.ESDentalLabServiceImpl;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
(PathProxy.SOLR_DENTAL_WORKS_BASE_URL)
@Produces(MediaType.APPLICATION_JSON_VALUE)
@Consumes(MediaType.APPLICATION_JSON_VALUE)
@Api(value = PathProxy.SOLR_DENTAL_WORKS_BASE_URL, description = "Endpoint for es dental works")
public class ESDentalLabAPI {

	private static Logger logger = LogManager.getLogger(ESDentalLabAPI.class.getName());
	
	@Autowired
	ESDentalLabServiceImpl esDentalLabServiceImpl;
	
	
	@GetMapping(value = PathProxy.ESDentalLabsUrl.SEARCH_DENTAL_WORKS)
	@ApiOperation(value = PathProxy.SolrClinicalNotesUrls.SEARCH_COMPLAINTS, notes = PathProxy.SolrClinicalNotesUrls.SEARCH_COMPLAINTS)
	public Response<ESDentalWorksDocument> searchComplaints(@PathVariable("range") String range,
			@RequestParam("page") long page, @RequestParam("size") int size, @RequestParam(value = "doctorId") String doctorId,
			@RequestParam(value = "locationId") String locationId, @RequestParam(value = "hospitalId") String hospitalId,
			@DefaultValue("0") @RequestParam(value = "updatedTime") String updatedTime,
			  @RequestParam(value = "discarded") Boolean discarded,
			@RequestParam(value = "searchTerm") String searchTerm) {
		if (DPDoctorUtils.anyStringEmpty(range, locationId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		List<ESDentalWorksDocument> dentalWorks = esDentalLabServiceImpl.searchDentalworks(range, page, size, doctorId, locationId, hospitalId, updatedTime, discarded, searchTerm);
		Response<ESDentalWorksDocument> response = new Response<ESDentalWorksDocument>();
		response.setDataList(dentalWorks);
		return response;
	}
	
}
