package com.dpdocter.webservices;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dpdocter.beans.AccessControl;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.services.AccessControlServices;

import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value=PathProxy.ACCESS_CONTROL_BASE_URL,produces = MediaType.APPLICATION_JSON ,consumes = MediaType.APPLICATION_JSON)
@Api(value = PathProxy.ACCESS_CONTROL_BASE_URL, description = "Endpoint for access control")
public class AccessControlAPI {

	@Autowired
    private AccessControlServices accessControlServices;

    @PostMapping(value = PathProxy.AccessControlUrls.SET_ACCESS_CONTROLS)
    @ApiOperation(value = PathProxy.AccessControlUrls.SET_ACCESS_CONTROLS, notes = PathProxy.AccessControlUrls.SET_ACCESS_CONTROLS)
    public Response<AccessControl> setAccessControls(@RequestBody AccessControl accessControl) {
	if (accessControl == null) {
	    throw new BusinessException(ServiceError.InvalidInput, "Access Control Request Cannot Be Empty!");
	}

	AccessControl setAccessControlResponse = accessControlServices.setAccessControls(accessControl);

	Response<AccessControl> response = new Response<AccessControl>();
	response.setData(setAccessControlResponse);
	return response;
    }
}
