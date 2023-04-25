package com.dpdocter.webservices.v2;

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

import com.dpdocter.elasticsearch.services.v2.ESPrescriptionService;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Component(value = "ESPrescriptionApiV2")
@Path(PathProxy.SOLR_PRESCRIPTION_BASEURL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.SOLR_PRESCRIPTION_BASEURL, description = "Endpoint for solr prescription")
public class ESPrescriptionApi {

	private static Logger logger = Logger.getLogger(ESPrescriptionApi.class.getName());

	@Autowired
	private ESPrescriptionService esPrescriptionService;

	@Path(value = PathProxy.SolrPrescriptionUrls.SEARCH_DRUG)
	@GET
	@ApiOperation(value = PathProxy.SolrPrescriptionUrls.SEARCH_DRUG, notes = PathProxy.SolrPrescriptionUrls.SEARCH_DRUG)
	public Response<Object> searchDrug(@PathParam("range") String range, @QueryParam("page") int page,
			@QueryParam("size") int size, @QueryParam(value = "doctorId") String doctorId,
			@QueryParam(value = "locationId") String locationId, @QueryParam(value = "hospitalId") String hospitalId,
			@DefaultValue("0") @QueryParam(value = "updatedTime") String updatedTime,
			@DefaultValue("true") @QueryParam(value = "discarded") Boolean discarded,
			@QueryParam(value = "searchTerm") String searchTerm, @QueryParam(value = "category") String category,
			@DefaultValue("false") @QueryParam(value = "searchByGenericName") Boolean searchByGenericName) {

		if (DPDoctorUtils.anyStringEmpty(range)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		// Hack for ios
		searchByGenericName = false;
		//
		Response<Object> response = esPrescriptionService.searchDrug(range, page, size, doctorId, locationId,
				hospitalId, updatedTime, discarded, searchTerm, category, searchByGenericName);
		return response;
	}
}
