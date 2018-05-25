package com.dpdocter.webservices;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dpdocter.beans.LabPrintSetting;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.request.LabPrintContentRequest;
import com.dpdocter.services.LabPrintServices;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Component
@Path(PathProxy.Lab_PRINT_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.Lab_PRINT_BASE_URL, description = "")
public class LabPrintAPI {

	private static Logger logger = Logger.getLogger(LabPrintAPI.class.getName());
	@Autowired
	private LabPrintServices labprintservices;

	@Path(value = PathProxy.LabPrintUrls.ADD_EDIT_LAB_PRINT_SETTING)
	@POST
	@ApiOperation(value = PathProxy.LabPrintUrls.ADD_EDIT_LAB_PRINT_SETTING, notes = PathProxy.LabPrintUrls.ADD_EDIT_LAB_PRINT_SETTING)
	public Response<LabPrintSetting> addEditPrintSetting(LabPrintSetting request) {
		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		} else if (DPDoctorUtils.anyStringEmpty(request.getLocationId(), request.getDoctorId(),
				request.getHospitalId())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<LabPrintSetting> response = new Response<LabPrintSetting>();
		response.setData(labprintservices.addEditPrintSetting(request));

		return response;
	}

	@Path(value = PathProxy.LabPrintUrls.GET_Lab_PRINT_SETTING)
	@GET
	@ApiOperation(value = PathProxy.LabPrintUrls.GET_Lab_PRINT_SETTING, notes = PathProxy.LabPrintUrls.GET_Lab_PRINT_SETTING)
	public Response<LabPrintSetting> addEditPrintSetting(@PathParam("locationId") String locationId,
			@PathParam("hospitalId") String hospitalId) {
		if (DPDoctorUtils.anyStringEmpty(locationId, hospitalId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<LabPrintSetting> response = new Response<LabPrintSetting>();
		response.setData(labprintservices.getLabPrintSetting(locationId, hospitalId));

		return response;
	}
	
	@Path(value = PathProxy.LabPrintUrls.ADD_EDIT_LAB_PRINT_HEADER)
	@POST
	@ApiOperation(value = PathProxy.LabPrintUrls.ADD_EDIT_LAB_PRINT_HEADER, notes = PathProxy.LabPrintUrls.ADD_EDIT_LAB_PRINT_HEADER)
	public Response<LabPrintSetting> addEditPrintHeader(LabPrintContentRequest request) {
		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		} else if (DPDoctorUtils.anyStringEmpty(request.getLocationId(), request.getDoctorId(),
				request.getHospitalId())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<LabPrintSetting> response = new Response<LabPrintSetting>();
		response.setData(labprintservices.setHeaderAndFooterSetup(request, "HEADER"));

		return response;
	}
	
	@Path(value = PathProxy.LabPrintUrls.ADD_EDIT_LAB_PRINT_FOOTER)
	@POST
	@ApiOperation(value = PathProxy.LabPrintUrls.ADD_EDIT_LAB_PRINT_FOOTER, notes = PathProxy.LabPrintUrls.ADD_EDIT_LAB_PRINT_FOOTER)
	public Response<LabPrintSetting> addEditPrintFooter(LabPrintContentRequest request) {
		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		} else if (DPDoctorUtils.anyStringEmpty(request.getLocationId(), request.getDoctorId(),
				request.getHospitalId())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<LabPrintSetting> response = new Response<LabPrintSetting>();
		response.setData(labprintservices.setHeaderAndFooterSetup(request, "FOOTER"));

		return response;
	}

}
