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

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dpdocter.elasticsearch.document.ESDentalWorksDocument;
import com.dpdocter.elasticsearch.services.impl.ESDentalLabServiceImpl;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Component
@Path(PathProxy.SOLR_DENTAL_WORKS_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.SOLR_DENTAL_WORKS_BASE_URL, description = "Endpoint for es dental works")
public class ESDentalLabAPI {

	private static Logger logger = Logger.getLogger(ESDentalLabAPI.class.getName());
	
	@Autowired
	ESDentalLabServiceImpl esDentalLabServiceImpl;
	
	@Path(value = PathProxy.ESDentalLabsUrl.SEARCH_DENTAL_WORKS)
	@GET
	@ApiOperation(value = PathProxy.SolrClinicalNotesUrls.SEARCH_COMPLAINTS, notes = PathProxy.SolrClinicalNotesUrls.SEARCH_COMPLAINTS)
	public Response<ESDentalWorksDocument> searchComplaints(@PathParam("range") String range,
			@QueryParam("page") int page, @QueryParam("size") int size, @QueryParam(value = "doctorId") String doctorId,
			@QueryParam(value = "locationId") String locationId, @QueryParam(value = "hospitalId") String hospitalId,
			@DefaultValue("0") @QueryParam(value = "updatedTime") String updatedTime,
			@DefaultValue("true") @QueryParam(value = "discarded") Boolean discarded,
			@QueryParam(value = "searchTerm") String searchTerm) {
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
