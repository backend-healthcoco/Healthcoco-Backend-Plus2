package com.dpdocter.webservices;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dpdocter.beans.VersionControl;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.services.VersionControlService;

import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Component
@Path(PathProxy.VERSION_CONTROL_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.VERSION_CONTROL_BASE_URL, description = "Endpoint for version controliing")
public class VersionControlAPI {

	private static Logger logger = Logger.getLogger(SignUpApi.class.getName());

	@Autowired
	VersionControlService versionControlService;

	@Path(value = PathProxy.VersionControlUrls.CHECK_VERSION)
	@POST
	@ApiOperation(value = PathProxy.VersionControlUrls.CHECK_VERSION, notes = PathProxy.VersionControlUrls.CHECK_VERSION)
	public Response<Integer> checkVersion(VersionControl versionControl) {

		if (versionControl == null) {
			logger.warn("Request send  is NULL");
			throw new BusinessException(ServiceError.InvalidInput, "Request send  is NULL");
		}
		Integer versionControlCode = versionControlService.checkVersion(versionControl);
		Response<Integer> response = new Response<Integer>();
		response.setData(versionControlCode);
		return response;
	}

	@Path(value = PathProxy.VersionControlUrls.CHANGE_VERSION)
	@POST
	@ApiOperation(value = PathProxy.VersionControlUrls.CHANGE_VERSION, notes = PathProxy.VersionControlUrls.CHANGE_VERSION)
	public Response<VersionControl> changeVersion(VersionControl versionControl) {

		if (versionControl == null) {
			logger.warn("Request send  is NULL");
			throw new BusinessException(ServiceError.InvalidInput, "Request send  is NULL");
		}
		versionControl = versionControlService.changeVersion(versionControl);
		Response<VersionControl> response = new Response<VersionControl>();
		response.setData(versionControl);
		return response;
	}

}
