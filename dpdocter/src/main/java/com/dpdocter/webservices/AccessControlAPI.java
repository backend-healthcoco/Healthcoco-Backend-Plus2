package com.dpdocter.webservices;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dpdocter.beans.AccessControl;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.services.AccessControlServices;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Component
@Path(PathProxy.ACCESS_CONTROL_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.ACCESS_CONTROL_BASE_URL, description = "Endpoint for access control")
public class AccessControlAPI {
    @Autowired
    private AccessControlServices accessControlServices;

//    @Value(value = "${AccessControlAPI.getAccessControls}")
//    private String getAccessControls;
//
//    @Value(value = "${AccessControlAPI.setAccessControls}")
//    private String setAccessControls;

    @Path(value = PathProxy.AccessControlUrls.GET_ACCESS_CONTROLS)
    @GET
    @ApiOperation(value = PathProxy.AccessControlUrls.GET_ACCESS_CONTROLS, notes = PathProxy.AccessControlUrls.GET_ACCESS_CONTROLS)
    public Response<AccessControl> getAccessControls(@PathParam(value = "roleOrUserId") String roleOrUserId, @PathParam(value = "locationId") String locationId,
	    @PathParam(value = "hospitalId") String hospitalId) {
	if (DPDoctorUtils.anyStringEmpty(roleOrUserId, locationId, hospitalId)) {
	    throw new BusinessException(ServiceError.InvalidInput, "Role Or User Id, Location Id and Hospital Id Cannot Be Empty!");
	}

	AccessControl accessControl = accessControlServices.getAccessControls(roleOrUserId, locationId, hospitalId);

	Response<AccessControl> response = new Response<AccessControl>();
	response.setData(accessControl);
	return response;
    }

    @Path(value = PathProxy.AccessControlUrls.SET_ACCESS_CONTROLS)
    @POST
    @ApiOperation(value = PathProxy.AccessControlUrls.SET_ACCESS_CONTROLS, notes = PathProxy.AccessControlUrls.SET_ACCESS_CONTROLS)
    public Response<AccessControl> setAccessControls(AccessControl accessControl) {
	if (accessControl == null) {
	    throw new BusinessException(ServiceError.InvalidInput, "Access Control Request Cannot Be Empty!");
	}

	AccessControl setAccessControlResponse = accessControlServices.setAccessControls(accessControl);

	Response<AccessControl> response = new Response<AccessControl>();
	response.setData(setAccessControlResponse);
	return response;
    }
}
