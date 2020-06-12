package com.dpdocter.webservices.v2;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.dpdocter.beans.DoctorLoginPin;
import com.dpdocter.beans.Hospital;
import com.dpdocter.beans.LoginResponse;
import com.dpdocter.beans.RegisteredPatientDetails;
import com.dpdocter.beans.UserAddress;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.request.LoginPatientRequest;
import com.dpdocter.request.LoginRequest;
import com.dpdocter.response.DoctorLoginPinRequest;
import com.dpdocter.response.UserAddressResponse;
import com.dpdocter.services.v2.LoginService;
import com.dpdocter.services.RegistrationService;

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

	@Autowired
	private RegistrationService registrationService;

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
//
//	@Path(value = PathProxy.LoginUrls.LOGIN_PATIENT)
//	@POST
//	@ApiOperation(value = PathProxy.LoginUrls.LOGIN_PATIENT, notes = PathProxy.LoginUrls.LOGIN_PATIENT)
//	public Response<Object> loginPatient(LoginPatientRequest request,
//			@DefaultValue("true") @QueryParam("discardedAddress") Boolean discardedAddress) {
//		if (request == null || DPDoctorUtils.anyStringEmpty(request.getMobileNumber())) {
//			logger.warn("Invalid Input");
//			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
//		}
//		if (DPDoctorUtils.anyStringEmpty(request.getOtpNumber())
//				&& (request.getPassword() == null || request.getPassword().length == 0)) {
//			logger.warn("Invalid Input");
//			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
//
//		}
//		List<RegisteredPatientDetails> users = null;
//		if (!DPDoctorUtils.anyStringEmpty(request.getOtpNumber())) {
//			users = loginService.loginPatientByOtp(request);
//		} else {
//			users = loginService.loginPatient(request);
//		}
//
//		if (users != null && !users.isEmpty()) {
//			for (RegisteredPatientDetails user : users) {
//				user.setImageUrl(getFinalImageURL(user.getImageUrl()));
//				user.setThumbnailUrl(getFinalImageURL(user.getThumbnailUrl()));
//			}
//		}
//		Response<Object> response = new Response<Object>();
//		response.setDataList(users);
//		if (users != null && !users.isEmpty()) {
//			List<UserAddress> userAddress = registrationService.getUserAddress(null, request.getMobileNumber(),
//					discardedAddress);
//			if (userAddress != null && !userAddress.isEmpty()) {
//				UserAddressResponse userAddressResponse = new UserAddressResponse();
//				userAddressResponse.setUserAddress(userAddress);
//				response.setData(userAddressResponse);
//			}
//		}
//		return response;
//	}
//
	private String getFinalImageURL(String imageURL) {
		if (imageURL != null) {
			return imagePath + imageURL;
		} else
			return null;

	}
//
//	@Path(value = PathProxy.LoginUrls.IS_LOCATION_ADMIN)
//	@POST
//	@ApiOperation(value = PathProxy.LoginUrls.IS_LOCATION_ADMIN, notes = PathProxy.LoginUrls.IS_LOCATION_ADMIN)
//	public Response<Boolean> isLocationAdmin(LoginRequest request) {
//		if (request == null || DPDoctorUtils.anyStringEmpty(request.getUsername()) || request.getPassword() == null
//				|| request.getPassword().length == 0) {
//			logger.warn("Invalid Input");
//			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
//		}
//		Boolean isLocationAdmin = loginService.isLocationAdmin(request);
//		Response<Boolean> response = new Response<Boolean>();
//		response.setData(isLocationAdmin);
//		return response;
//	}
//
//	@Path(value = PathProxy.LoginUrls.ADD_EDIT_DOCTOR_LOGIN_PIN)
//	@POST
//	@ApiOperation(value = PathProxy.LoginUrls.ADD_EDIT_DOCTOR_LOGIN_PIN, notes = PathProxy.LoginUrls.ADD_EDIT_DOCTOR_LOGIN_PIN)
//	public Response<DoctorLoginPin> addEditLoginPin(DoctorLoginPin request) {
//		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId()) || request.getPin() == null
//				|| request.getPin().length() == 0) {
//			logger.warn("Invalid Input");
//			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
//		}
//		DoctorLoginPin loginPin = loginService.AddEditLoginPin(request);
//		Response<DoctorLoginPin> response = new Response<DoctorLoginPin>();
//		response.setData(loginPin);
//		return response;
//	}
//
//	@Path(value = PathProxy.LoginUrls.GET_DOCTOR_LOGIN_PIN)
//	@GET
//	@ApiOperation(value = PathProxy.LoginUrls.GET_DOCTOR_LOGIN_PIN, notes = PathProxy.LoginUrls.GET_DOCTOR_LOGIN_PIN)
//	public Response<DoctorLoginPin> getLoginPin(@PathParam("doctorId") String doctorId) {
//		if (DPDoctorUtils.anyStringEmpty(doctorId)) {
//			logger.warn("Invalid Input");
//			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
//		}
//		DoctorLoginPin pinResponse = loginService.getLoginPin(doctorId);
//		Response<DoctorLoginPin> response = new Response<DoctorLoginPin>();
//		response.setData(pinResponse);
//		return response;
//	}
//
//	@Path(value = PathProxy.LoginUrls.CHECK_DOCTOR_LOGIN_PIN)
//	@POST
//	@ApiOperation(value = PathProxy.LoginUrls.CHECK_DOCTOR_LOGIN_PIN, notes = PathProxy.LoginUrls.CHECK_DOCTOR_LOGIN_PIN)
//	public Response<Boolean> checkLoginPin(DoctorLoginPinRequest request) {
//		if (DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getPin())) {
//			logger.warn("Invalid Input");
//			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
//
//		}
//		Boolean isLocationAdmin = loginService.checkLoginPin(request);
//		Response<Boolean> response = new Response<Boolean>();
//		response.setData(isLocationAdmin);
//		return response;
//	}

}
