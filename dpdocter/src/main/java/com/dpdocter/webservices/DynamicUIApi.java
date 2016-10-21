package com.dpdocter.webservices;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.springframework.stereotype.Component;

import com.dpdocter.beans.DynamicUI;
import com.dpdocter.enums.SpecialityTypeEnum;
import com.dpdocter.request.DynamicUIRequest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Component
@Path(PathProxy.DYNAMIC_UI_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.DYNAMIC_UI_BASE_URL, description = "Endpoint for Dynamic UI")
public class DynamicUIApi {
	
	
	@Path(value = PathProxy.DynamicUIUrls.GET_ALL_PERMISSIONS_FOR_DOCTOR)
	@GET
    @ApiOperation(value = PathProxy.DynamicUIUrls.GET_ALL_PERMISSIONS_FOR_DOCTOR, notes = PathProxy.DynamicUIUrls.GET_ALL_PERMISSIONS_FOR_DOCTOR)
	public DynamicUI getAllPermissionForDoctor(@PathParam("doctorId") String doctorId)
	{
		return null;
	}
	
	@Path(value = PathProxy.DynamicUIUrls.GET_PERMISSIONS_FOR_DOCTOR)
	@GET
    @ApiOperation(value = PathProxy.DynamicUIUrls.GET_PERMISSIONS_FOR_DOCTOR, notes = PathProxy.DynamicUIUrls.GET_PERMISSIONS_FOR_DOCTOR)
	public DynamicUI getPermissionForDoctor(@PathParam("doctorId") String doctorId)
	{
		return null;
	}
	
	@POST
    @ApiOperation(value = "SUBMIT_DYNAMIC_UI_PERMISSION", notes = "SUBMIT_DYNAMIC_UI_PERMISSION")
	public DynamicUI postPermissions(DynamicUIRequest dynamicUIRequest)
	{
		return null;
	}

}
