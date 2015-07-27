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

import com.dpdocter.beans.LoginResponse;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.request.LoginRequest;
import com.dpdocter.services.LoginService;
import common.util.web.DPDoctorUtils;
import common.util.web.Response;

/**
 * @author veeraj
 */
@Component
@Path(PathProxy.LOGIN_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LoginApi {
    @Autowired
    private LoginService loginService;

    @Path(value = PathProxy.LoginUrls.LOGIN_USER)
    @POST
    public Response<LoginResponse> login(LoginRequest request) {
	if (request == null) {
	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
	}
	LoginResponse loginResponse = loginService.login(request);
	Response<LoginResponse> response = new Response<LoginResponse>();
	response.setData(loginResponse);
	return response;
    }

    @Path(value = PathProxy.LoginUrls.VERIFY_USER)
    @GET
    public Response<Boolean> verifyUser(@PathParam("userId") String userId) {
	if (DPDoctorUtils.anyStringEmpty(userId)) {
	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input. User Id Cannot Be Empty");
	}
	Boolean verifyUserResponse = loginService.verifyUser(userId);
	Response<Boolean> response = new Response<Boolean>();
	response.setData(verifyUserResponse);
	return response;
    }

    @Path(value = PathProxy.LoginUrls.OTP_GENERATOR)
    @GET
    public Response<String> otpGenerator(@PathParam("mobileNumber") String mobileNumber) {
	if (DPDoctorUtils.anyStringEmpty(mobileNumber)) {
	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input. Mobile Number Cannot Be Empty");
	}
	String OTP = loginService.otpGenerator(mobileNumber);
	Response<String> response = new Response<String>();
	response.setData(OTP);
	return response;
    }
}
