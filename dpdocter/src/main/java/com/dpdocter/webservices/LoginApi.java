package com.dpdocter.webservices;

import java.io.IOException;
import java.util.List;

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
import com.dpdocter.beans.RegisteredPatientDetails;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.request.LoginPatientRequest;
import com.dpdocter.request.LoginRequest;
import com.dpdocter.response.LoginResponse;
import com.dpdocter.response.OAuth2TokenResponse;
import com.dpdocter.response.OauthRefreshTokenRequest;
import com.dpdocter.response.PatientLoginResponse;
import com.dpdocter.services.LoginService;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Component
@Path(PathProxy.LOGIN_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.LOGIN_BASE_URL, description = "Endpoint for login")
public class LoginApi {

	private static Logger logger = Logger.getLogger(LoginApi.class.getName());

	@Autowired
	private LoginService loginService;

	@Value(value = "${image.path}")
	private String imagePath;

	@Path(value = PathProxy.LoginUrls.LOGIN_USER)
	@POST
	@ApiOperation(value = PathProxy.LoginUrls.LOGIN_USER, notes = PathProxy.LoginUrls.LOGIN_USER)
	public Response<LoginResponse> login(LoginRequest request,
			@DefaultValue(value = "false") @QueryParam(value = "isMobileApp") Boolean isMobileApp) {
		if (request == null
				|| DPDoctorUtils.anyStringEmpty(request.getClientId(), request.getGrantType(),
						request.getClientSecret(), request.getUsername())
				|| request.getPassword() == null || request.getPassword().length == 0) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		LoginResponse loginResponse = loginService.login(request, isMobileApp);
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

	@Path(value = PathProxy.LoginUrls.LOGIN_PATIENT)
	@POST
	@ApiOperation(value = PathProxy.LoginUrls.LOGIN_PATIENT, notes = PathProxy.LoginUrls.LOGIN_PATIENT)
	public Response<PatientLoginResponse> loginPatient(LoginPatientRequest request) {
		if (request == null
				|| DPDoctorUtils.anyStringEmpty(request.getClientId(), request.getGrantType(),
						request.getClientSecret(), request.getMobileNumber())
				|| request.getPassword() == null || request.getPassword().length == 0) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		PatientLoginResponse loginResponse = loginService.loginPatient(request);
		List<RegisteredPatientDetails> users = loginResponse.getDetail();
		if (users != null && !users.isEmpty()) {
			for (RegisteredPatientDetails user : users) {
				user.setImageUrl(getFinalImageURL(user.getImageUrl()));
				user.setThumbnailUrl(getFinalImageURL(user.getThumbnailUrl()));
			}
		}
		Response<PatientLoginResponse> response = new Response<PatientLoginResponse>();
		response.setData(loginResponse);
		return response;
	}

	@Path(value = PathProxy.LoginUrls.REFRESH_TOKEN)
	@POST
	@ApiOperation(value = PathProxy.LoginUrls.REFRESH_TOKEN, notes = PathProxy.LoginUrls.REFRESH_TOKEN)
	public Response<OAuth2TokenResponse> refreshToken(OauthRefreshTokenRequest request)
			throws JsonParseException, JsonMappingException, IOException {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getClientId(), request.getGrantType(),
				request.getClientSecret(), request.getRefresh_token())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		String tokens = loginService.refreshToken(request);
		OAuth2TokenResponse oauth2TokenResponse = new OAuth2TokenResponse();
		if (tokens.equalsIgnoreCase("Failed")) {
			logger.warn("refresh");
			throw new BusinessException(ServiceError.InvalidInput, "refresh");
		}
		ObjectMapper mapper = new ObjectMapper();
		oauth2TokenResponse = mapper.readValue(tokens, OAuth2TokenResponse.class);
		Response<OAuth2TokenResponse> response = new Response<OAuth2TokenResponse>();
		response.setData(oauth2TokenResponse);
		return response;
	}

	private String getFinalImageURL(String imageURL) {
		if (imageURL != null) {
			return imagePath + imageURL;
		} else
			return null;

	}
}
