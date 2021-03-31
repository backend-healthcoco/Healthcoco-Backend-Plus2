package com.dpdocter.webservices;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dpdocter.elasticsearch.services.ESExpenseTypeService;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
(PathProxy.SOLR_BILLING_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.SOLR_BILLING_BASE_URL, description = "")
public class ESBillingApi {
	private static Logger logger = LogManager.getLogger(ESBillingApi.class.getName());

	@Autowired
	private ESExpenseTypeService esExpenseService;

	
	@GetMapping(value = PathProxy.SolrBillingUrls.SEARCH_EXPENSE_TYPES)
	@ApiOperation(value = PathProxy.SolrBillingUrls.SEARCH_EXPENSE_TYPES, notes = PathProxy.SolrBillingUrls.SEARCH_EXPENSE_TYPES)
	public Response<Object> searchExpenseType(@PathVariable("range") String range, @RequestParam("page") int page,
			@RequestParam("size") int size, @RequestParam(value = "doctorId") String doctorId,
			@RequestParam(value = "locationId") String locationId, @RequestParam(value = "hospitalId") String hospitalId,
			@DefaultValue("0") @RequestParam(value = "updatedTime") String updatedTime,
			  @RequestParam(value = "discarded") Boolean discarded,
			@RequestParam(value = "searchTerm") String searchTerm) {

		if (DPDoctorUtils.anyStringEmpty(range)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		List<?> documents = esExpenseService.search(range, page, size, doctorId, locationId, hospitalId, updatedTime,
				discarded, searchTerm);
		Response<Object> response = new Response<Object>();
		response.setDataList(documents);
		return response;
	}

}
