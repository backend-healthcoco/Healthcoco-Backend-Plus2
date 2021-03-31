package com.dpdocter.webservices.v2;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dpdocter.elasticsearch.services.v2.ESPrescriptionService;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController(value = "ESPrescriptionApiV2")
@RequestMapping(value=PathProxy.SOLR_PRESCRIPTION_BASEURL,produces = MediaType.APPLICATION_JSON ,consumes = MediaType.APPLICATION_JSON)
@Api(value = PathProxy.SOLR_PRESCRIPTION_BASEURL, description = "Endpoint for solr prescription")
public class ESPrescriptionApi {

	private static Logger logger = LogManager.getLogger(ESPrescriptionApi.class.getName());

	@Autowired
	private ESPrescriptionService esPrescriptionService;

	
	@GetMapping(value = PathProxy.SolrPrescriptionUrls.SEARCH_DRUG)
	@ApiOperation(value = PathProxy.SolrPrescriptionUrls.SEARCH_DRUG, notes = PathProxy.SolrPrescriptionUrls.SEARCH_DRUG)
	public Response<Object> searchDrug(@PathVariable("range") String range, @RequestParam("page") int page,
			@RequestParam("size") int size, @RequestParam(value = "doctorId") String doctorId,
			@RequestParam(value = "locationId") String locationId, @RequestParam(value = "hospitalId") String hospitalId,
			@DefaultValue("0") @RequestParam(value = "updatedTime") String updatedTime,
			  @RequestParam(value = "discarded") Boolean discarded,
			@RequestParam(value = "searchTerm") String searchTerm, @RequestParam(value = "category") String category,
			@DefaultValue("false") @RequestParam(value = "searchByGenericName") Boolean searchByGenericName) {

		if (DPDoctorUtils.anyStringEmpty(range)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		// Hack for ios
		searchByGenericName = false;
		//
		Response<Object> response = esPrescriptionService.searchDrug(range, page, size, doctorId, locationId, hospitalId,
				updatedTime, discarded, searchTerm, category, searchByGenericName);
		return response;
	}
}
