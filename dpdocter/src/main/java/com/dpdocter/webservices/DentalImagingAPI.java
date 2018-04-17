
package com.dpdocter.webservices;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dpdocter.beans.DentalDiagnosticService;
import com.dpdocter.beans.DentalImaging;
import com.dpdocter.beans.DentalImagingRequest;
import com.dpdocter.beans.Location;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.services.DentalImagingService;

import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;


@Component
@Path(PathProxy.DENTAL_IMAGING_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.DENTAL_IMAGING_URL, description = "Endpoint for dental imaging")
public class DentalImagingAPI {
	
	private static Logger logger = Logger.getLogger(DentalImagingAPI.class.getName());

	
	@Autowired
	DentalImagingService dentalImagingService;
	

	@Path(value = PathProxy.DentalImagingUrl.ADD_EDIT_DENTAL_IMAGING_REQUEST)
	@POST
	@ApiOperation(value = PathProxy.DentalImagingUrl.ADD_EDIT_DENTAL_IMAGING_REQUEST, notes = PathProxy.DentalImagingUrl.ADD_EDIT_DENTAL_IMAGING_REQUEST)
	public Response<DentalImaging> addEditDentalRequest(DentalImagingRequest request) {
		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<DentalImaging> response = new Response<DentalImaging>();
		response.setData(dentalImagingService.addEditDentalImagingRequest(request));
		return response;
	}
	

	@Path(value = PathProxy.DentalImagingUrl.GET_REQUESTS)
	@GET
	@ApiOperation(value = PathProxy.DentalImagingUrl.GET_REQUESTS, notes = PathProxy.DentalImagingUrl.GET_REQUESTS)
	public Response<DentalImaging> getPickupRequests(@QueryParam("locationId") String locationId,@QueryParam("hospitalId") String hospitalId,
			@QueryParam("doctorId") String doctorId, @DefaultValue("0") @QueryParam("from") Long from,
			@QueryParam("to") Long to, @QueryParam("searchTerm") String searchTerm, @QueryParam("size") int size,
			@QueryParam("page") int page) {


		Response<DentalImaging> response = new Response<DentalImaging>();
		response.setDataList(dentalImagingService.getRequests(locationId, hospitalId, doctorId, from, to, searchTerm, size, page));
		return response;
	}

	
	@Path(value = PathProxy.DentalImagingUrl.GET_SERVICE_LOCATION)
	@GET
	@ApiOperation(value = PathProxy.DentalImagingUrl.GET_SERVICE_LOCATION, notes = PathProxy.DentalImagingUrl.GET_SERVICE_LOCATION)
	public Response<Location> getPickupRequests(@QueryParam("dentalImagingServiceId") String dentalImagingServiceId,
			@QueryParam("doctorId") String doctorId,@QueryParam("searchTerm") String searchTerm, @QueryParam("size") int size,
			@QueryParam("page") int page) {

		Response<Location> response = new Response<Location>();
		//response.setDataList(dentalImagingService.getRequests(locationId, hospitalId, doctorId, from, to, searchTerm, size, page));
		return response;
	}
	
	@Path(value = PathProxy.DentalImagingUrl.GET_SERVICES)
	@GET
	@ApiOperation(value = PathProxy.DentalImagingUrl.GET_SERVICES, notes = PathProxy.DentalImagingUrl.GET_SERVICES)
	public Response<DentalDiagnosticService> getPickupRequests(@QueryParam("searchTerm") String searchTerm, @QueryParam("size") int size,
			@QueryParam("page") int page) {

		Response<DentalDiagnosticService> response = new Response<DentalDiagnosticService>();
		//response.setDataList(dentalImagingService.getRequests(locationId, hospitalId, doctorId, from, to, searchTerm, size, page));
		return response;
	}
	
}

