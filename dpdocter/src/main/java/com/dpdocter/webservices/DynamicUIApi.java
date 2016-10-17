package com.dpdocter.webservices;

import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.springframework.stereotype.Component;

import com.dpdocter.beans.DynamicUIRequest;
import com.dpdocter.enums.SpecialityTypeEnum;

import io.swagger.annotations.Api;

@Component
@Path(PathProxy.DYNAMIC_UI_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.DYNAMIC_UI_BASE_URL, description = "Endpoint for Dynamic UI")
public class DynamicUIApi {
	
	
	public void getAllPermissionForDoctor(@PathParam("doctorId") String doctorId)
	{
		
	}
	
	public void getPermissionForDoctor(@PathParam("doctorId") String doctorId)
	{
		
	}
	
	public void postPermissions(DynamicUIRequest dynamicUIRequest)
	{
		
	}

}
