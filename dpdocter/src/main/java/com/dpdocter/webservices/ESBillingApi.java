package com.dpdocter.webservices;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dpdocter.elasticsearch.services.ESExpenseTypeService;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Component
@Path(PathProxy.SOLR_BILLING_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.SOLR_BILLING_BASE_URL, description = "")
public class ESBillingApi {
	private static Logger logger = LogManager.getLogger(ESBillingApi.class.getName());

	@Autowired
	private ESExpenseTypeService esExpenseService;

	@Path(value = PathProxy.SolrBillingUrls.SEARCH_EXPENSE_TYPES)
	@GET
	@ApiOperation(value = PathProxy.SolrBillingUrls.SEARCH_EXPENSE_TYPES, notes = PathProxy.SolrBillingUrls.SEARCH_EXPENSE_TYPES)
	public Response<Object> searchExpenseType(@PathParam("range") String range, @QueryParam("page") int page,
			@QueryParam("size") int size, @QueryParam(value = "doctorId") String doctorId,
			@QueryParam(value = "locationId") String locationId, @QueryParam(value = "hospitalId") String hospitalId,
			@DefaultValue("0") @QueryParam(value = "updatedTime") String updatedTime,
			@DefaultValue("true") @QueryParam(value = "discarded") Boolean discarded,
			@QueryParam(value = "searchTerm") String searchTerm) {

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
