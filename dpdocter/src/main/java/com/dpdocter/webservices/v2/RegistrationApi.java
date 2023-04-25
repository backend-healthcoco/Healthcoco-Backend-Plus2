package com.dpdocter.webservices.v2;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.response.v2.ClinicDoctorResponse;
import com.dpdocter.services.v2.RegistrationService;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @author parag
 */
@Component(value = "RegistrationApiV2")
@Path(PathProxy.REGISTRATION_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.REGISTRATION_BASE_URL, description = "Endpoint for register")
public class RegistrationApi {

	private static Logger logger = Logger.getLogger(RegistrationApi.class.getName());

	@Autowired
	private RegistrationService registrationService;

	@Context
	private UriInfo uriInfo;

	@Value(value = "${image.path}")
	private String imagePath;

	@Value(value = "${register.first.name.validation}")
	private String firstNameValidaton;

	@Value(value = "${register.mobile.number.validation}")
	private String mobileNumberValidaton;

	@Value(value = "${invalid.input}")
	private String invalidInput;

	@Path(value = PathProxy.RegistrationUrls.GET_USERS)
	@GET
	@ApiOperation(value = PathProxy.RegistrationUrls.GET_USERS, notes = PathProxy.RegistrationUrls.GET_USERS)
	public Response<ClinicDoctorResponse> getUsers(@QueryParam("doctorId") String doctorId,
			@PathParam(value = "locationId") String locationId, @PathParam(value = "hospitalId") String hospitalId,
			@QueryParam(value = "page") int page, @QueryParam(value = "size") int size,
			@DefaultValue("0") @QueryParam(value = "updatedTime") String updatedTime,
			@QueryParam(value = "role") String role,
			@DefaultValue("false") @QueryParam(value = "active") Boolean active,
			@QueryParam(value = "userState") String userState) {
		if (DPDoctorUtils.anyStringEmpty(locationId, hospitalId)) {
			logger.warn(invalidInput);
			throw new BusinessException(ServiceError.InvalidInput, invalidInput);
		}
		List<ClinicDoctorResponse> professionResponse = registrationService.getUsers(page, size, doctorId, locationId,
				hospitalId, updatedTime, role, active, userState);
		Response<ClinicDoctorResponse> response = new Response<ClinicDoctorResponse>();
		response.setDataList(professionResponse);
		return response;
	}

}
