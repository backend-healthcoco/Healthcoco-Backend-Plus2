package com.dpdocter.webservices.v2;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.dpdocter.beans.Hospital;
import com.dpdocter.beans.LoginResponse;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.request.LoginRequest;
import com.dpdocter.services.v2.LoginService;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Component(value = "LoginApiV2")
@Path(PathProxy.LOGIN_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.LOGIN_BASE_URL, description = "Endpoint for login")
public class LoginApiV2 {

	private static Logger logger = Logger.getLogger(LoginApiV2.class.getName());

	@Autowired
	private LoginService loginService;

	@Value(value = "${image.path}")
	private String imagePath;

	@Path(value = PathProxy.LoginUrls.LOGIN_USER)
	@POST
	@ApiOperation(value = PathProxy.LoginUrls.LOGIN_USER, notes = PathProxy.LoginUrls.LOGIN_USER)
	public Response<LoginResponse> login(LoginRequest request,
			@DefaultValue(value = "false") @QueryParam(value = "isMobileApp") Boolean isMobileApp,
			@DefaultValue(value = "false") @QueryParam(value = "isNutritionist") Boolean isNutritionist) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getUsername()) || request.getPassword() == null
				|| request.getPassword().length == 0) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		LoginResponse loginResponse = loginService.login(request, isMobileApp, isNutritionist);
		if (loginResponse != null) {
			if (!DPDoctorUtils.anyStringEmpty(loginResponse.getUser().getImageUrl())) {
				loginResponse.getUser().setImageUrl(getFinalImageURL(loginResponse.getUser().getImageUrl()));
			}
			if (!DPDoctorUtils.anyStringEmpty(loginResponse.getUser().getThumbnailUrl())) {
				loginResponse.getUser().setThumbnailUrl(getFinalImageURL(loginResponse.getUser().getThumbnailUrl()));
			}
			if (loginResponse.getHospitals() != null && !loginResponse.getHospitals().isEmpty()) {
				for (Hospital hospital : loginResponse.getHospitals()) {
					if (!DPDoctorUtils.anyStringEmpty(hospital.getHospitalImageUrl())) {
						hospital.setHospitalImageUrl(getFinalImageURL(hospital.getHospitalImageUrl()));
					}
				}
			}
		}

		Response<LoginResponse> response = new Response<LoginResponse>();
		if (response != null)
			response.setData(loginResponse);
		return response;
	}

	private String getFinalImageURL(String imageURL) {
		if (imageURL != null) {
			return imagePath + imageURL;
		} else
			return null;

	}

}
