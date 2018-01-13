package com.dpdocter.services.impl;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dpdocter.beans.DentalWork;
import com.dpdocter.enums.LabType;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.request.AddEditCustomWorkRequest;
import com.dpdocter.services.DentalLabService;
import com.dpdocter.webservices.PathProxy;

import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Component
@Path(PathProxy.DENTAL_LAB_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.DENTAL_LAB_BASE_URL, description = "Endpoint for dental lab")
public class DentalLabAPI {

	private static Logger logger = Logger.getLogger(DentalLabAPI.class.getName());
	
	@Autowired
	private DentalLabService dentalLabService;
	
	@Path(value = PathProxy.DentalLabUrls.ADD_EDIT_DENTAL_WORKS)
	@POST
	@ApiOperation(value = PathProxy.DentalLabUrls.ADD_EDIT_DENTAL_WORKS, notes = PathProxy.DentalLabUrls.ADD_EDIT_DENTAL_WORKS)
	public Response<DentalWork> addEditPickupRequest(AddEditCustomWorkRequest request) {
		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<DentalWork> response = new Response<DentalWork>();
		response.setData(dentalLabService.addEditCustomWork(request));

		return response;
	}

	@Path(value = PathProxy.DentalLabUrls.GET_DENTAL_WORKS)
	@GET
	@ApiOperation(value = PathProxy.DentalLabUrls.GET_DENTAL_WORKS, notes = PathProxy.DentalLabUrls.GET_DENTAL_WORKS)
	public Response<DentalWork> getDentalWorks(@QueryParam("locationId") String locationId,
			@QueryParam("page") int page, @QueryParam("size") int size, @QueryParam("searchTerm") String searchTerm) {
		if (locationId == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<DentalWork> response = new Response<DentalWork>();
		response.setDataList(dentalLabService.getCustomWorks(page, size, searchTerm));
		return response;
	}
	
	@Path(value = PathProxy.DentalLabUrls.DELETE_DENTAL_WORKS)
	@DELETE
	@ApiOperation(value = PathProxy.DentalLabUrls.DELETE_DENTAL_WORKS, notes = PathProxy.DentalLabUrls.DELETE_DENTAL_WORKS)
	public Response<Object> deleteDentalWork(@QueryParam("id") String id,
			@QueryParam("discarded") boolean discarded) {
		if (id == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<Object> response = new Response<Object>();
		response.setData(dentalLabService.deleteCustomWork(id, discarded));
		return response;
	}
	
	@Path(value = PathProxy.DentalLabUrls.CHANGE_LAB_TYPE)
	@GET
	@ApiOperation(value = PathProxy.DentalLabUrls.CHANGE_LAB_TYPE, notes = PathProxy.DentalLabUrls.CHANGE_LAB_TYPE)
	public Response<Boolean> changeLabType(@QueryParam("doctorId") String doctorId,@QueryParam("locationId") String locationId,
			@QueryParam("labType") LabType labType) {
		if (locationId == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<Boolean> response = new Response<Boolean>();
		response.setData(dentalLabService.changeLabType(doctorId, locationId, labType));
		return response;
	}
	/*
	@Path(value = PathProxy.DentalLabUrls.ADD_EDIT_DENTAL_LAB_DOCTOR_ASSOCIATION)
	@POST
	@ApiOperation(value = PathProxy.DentalLabUrls.ADD_EDIT_DENTAL_LAB_DOCTOR_ASSOCIATION, notes = PathProxy.DentalLabUrls.ADD_EDIT_DENTAL_LAB_DOCTOR_ASSOCIATION)
	public Response<DentalLabDoctorAssociation> addEditDentalLabDoctorAssociation(DentalLabDoctorAssociation request) {
		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<DentalLabDoctorAssociation> response = new Response<DentalLabDoctorAssociation>();
		response.setData(dentalLabService.addEditDentalLabDoctorAssociation(request));
		return response;
	}
	

	@Path(value = PathProxy.DentalLabUrls.GET_DENTAL_LAB_DOCTOR_ASSOCIATION)
	@POST
	@ApiOperation(value = PathProxy.DentalLabUrls.GET_DENTAL_LAB_DOCTOR_ASSOCIATION, notes = PathProxy.DentalLabUrls.GET_DENTAL_LAB_DOCTOR_ASSOCIATION)
	public Response<DentalLabDoctorAssociation> getDentalLabDoctorAssociation(@QueryParam("locationId") String locationId,
			@QueryParam("page") int page, @QueryParam("size") int size, @QueryParam("searchTerm") String searchTerm) {
		if (locationId != null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<DentalLabDoctorAssociation> response = new Response<DentalLabDoctorAssociation>();
		response.setDataList(dentalLabService.getDentalLabDoctorAssociations(locationId, page, size, searchTerm));
		return response;
	}
*/
	
}
