package com.dpdocter.webservices;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dpdocter.beans.DynamicUI;
import com.dpdocter.beans.UIPermissions;
import com.dpdocter.enums.SpecialityTypeEnum;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.request.DynamicUIRequest;
import com.dpdocter.services.DynamicUIService;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Component
@Path(PathProxy.DYNAMIC_UI_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.DYNAMIC_UI_BASE_URL, description = "Endpoint for Dynamic UI")
public class DynamicUIApi {
	
	@Autowired
	DynamicUIService dynamicUIService;
	
	@Path(value = PathProxy.DynamicUIUrls.GET_ALL_PERMISSIONS_FOR_DOCTOR)
	@GET
    @ApiOperation(value = PathProxy.DynamicUIUrls.GET_ALL_PERMISSIONS_FOR_DOCTOR, notes = PathProxy.DynamicUIUrls.GET_ALL_PERMISSIONS_FOR_DOCTOR)
	public Response<UIPermissions> getAllPermissionForDoctor(@PathParam("doctorId") String doctorId)
	{
		if(DPDoctorUtils.anyStringEmpty(doctorId)){
			throw new BusinessException(ServiceError.InvalidInput, "Doctor Id is null");
		}
		UIPermissions uiPermissions = dynamicUIService.getAllPermissionForDoctor(doctorId);
		Response<UIPermissions> response = new Response<UIPermissions>();
		response.setData(uiPermissions);
		return response;
	}
	
	@Path(value = PathProxy.DynamicUIUrls.GET_PERMISSIONS_FOR_DOCTOR)
	@GET
    @ApiOperation(value = PathProxy.DynamicUIUrls.GET_PERMISSIONS_FOR_DOCTOR, notes = PathProxy.DynamicUIUrls.GET_PERMISSIONS_FOR_DOCTOR)
	public Response<DynamicUI> getPermissionForDoctor(@PathParam("doctorId") String doctorId)
	{
		if(DPDoctorUtils.anyStringEmpty(doctorId)){
			throw new BusinessException(ServiceError.InvalidInput, "Doctor Id is null");
		}
		DynamicUI dynamicUI = dynamicUIService.getPermissionForDoctor(doctorId);
		Response<DynamicUI> response = new Response<DynamicUI>();
		response.setData(dynamicUI);
		return response;
	}
	
	@POST
    @ApiOperation(value = "SUBMIT_DYNAMIC_UI_PERMISSION", notes = "SUBMIT_DYNAMIC_UI_PERMISSION")
	public DynamicUI postPermissions(DynamicUIRequest dynamicUIRequest)
	{
		return null;
	}

}
