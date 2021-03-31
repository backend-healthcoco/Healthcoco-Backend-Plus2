package com.dpdocter.webservices.v2;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
@RestController(value = "RegistrationApiV2")
@RequestMapping(value=PathProxy.REGISTRATION_BASE_URL,produces = MediaType.APPLICATION_JSON ,consumes = MediaType.APPLICATION_JSON)
@Api(value = PathProxy.REGISTRATION_BASE_URL, description = "Endpoint for register")
public class RegistrationApi {

	private static Logger logger = LogManager.getLogger(RegistrationApi.class.getName());

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

	private String getFinalImageURL(String imageURL) {
		if (imageURL != null) {
			return imagePath + imageURL;
		} else
			return null;
	}

	
	@GetMapping(value = PathProxy.RegistrationUrls.GET_USERS)
	@ApiOperation(value = PathProxy.RegistrationUrls.GET_USERS, notes = PathProxy.RegistrationUrls.GET_USERS)
	public Response<ClinicDoctorResponse> getUsers(@RequestParam("doctorId") String doctorId,
			@PathVariable(value = "locationId") String locationId, @PathVariable(value = "hospitalId") String hospitalId,
			@RequestParam(value = "page") int page, @RequestParam(value = "size") int size,
			@DefaultValue("0") @RequestParam(value = "updatedTime") String updatedTime,
			@RequestParam(value = "role") String role,
			@DefaultValue("false") @RequestParam(value = "active") Boolean active,
			@RequestParam(value = "userState") String userState) {
		if (DPDoctorUtils.anyStringEmpty(locationId, hospitalId)) {
			logger.warn(invalidInput);
			throw new BusinessException(ServiceError.InvalidInput, invalidInput);
		}
		List<ClinicDoctorResponse> professionResponse = registrationService.getUsers(page, size, doctorId, locationId, hospitalId,
				updatedTime, role, active, userState);
		Response<ClinicDoctorResponse> response = new Response<ClinicDoctorResponse>();
		response.setDataList(professionResponse);
		return response;
	}

}
