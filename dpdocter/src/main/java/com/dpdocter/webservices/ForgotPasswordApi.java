package com.dpdocter.webservices;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.request.ForgotUsernamePasswordRequest;
import com.dpdocter.request.ResetPasswordRequest;
import com.dpdocter.services.ForgotPasswordService;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;

@Component
@Path(PathProxy.FORGOT_PASSWORD_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ForgotPasswordApi {

    private static Logger logger = Logger.getLogger(ForgotPasswordApi.class.getName());

    @Autowired
    private ForgotPasswordService forgotPasswordService;

    @Context
    private UriInfo uriInfo;

    @Path(value = PathProxy.ForgotPasswordUrls.FORGOT_PASSWORD_DOCTOR)
    @POST
    public Response<String> forgotPassword(ForgotUsernamePasswordRequest request) {
	if (request == null) {
	    logger.warn("Invalid Input");
	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
	}
	forgotPasswordService.forgotPasswordForDoctor(request, uriInfo);
	Response<String> response = new Response<String>();
	response.setData("RESET YOUR PASSWORD FROM EMAIL ADDRESS");
	return response;
    }

    @Path(value = PathProxy.ForgotPasswordUrls.FORGOT_PASSWORD_PATIENT)
    @POST
    public Response<Boolean> forgotPasswordForPatient(ForgotUsernamePasswordRequest request) {
	if (request == null) {
	    logger.warn("Invalid Input");
	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
	}
	boolean flag = forgotPasswordService.forgotPasswordForPatient(request, uriInfo);
	Response<Boolean> response = new Response<Boolean>();
	response.setData(flag);
	return response;
    }

    @Path(value = PathProxy.ForgotPasswordUrls.RESET_PASSWORD_PATIENT)
    @GET
    public Response<Boolean> resetPasswordPatient(@PathParam(value = "mobileNumber") String mobileNumber, @PathParam(value = "password") String password) {

	Boolean isReset = forgotPasswordService.resetPasswordPatient(mobileNumber, password);
	Response<Boolean> response = new Response<Boolean>();
	response.setData(isReset);
	return response;
    }

    @Path(value = PathProxy.ForgotPasswordUrls.RESET_PASSWORD)
    @POST
    public Response<String> resetPassword(ResetPasswordRequest request) {
	if (request == null) {
	    logger.warn("Invalid Input");
	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
	}
	String string = forgotPasswordService.resetPassword(request, uriInfo);
	Response<String> response = new Response<String>();
	response.setData(string);
	return response;
    }

    @Path(value = PathProxy.ForgotPasswordUrls.CHECK_LINK_IS_ALREADY_USED)
    @GET
    public Response<String> checkLinkIsAlreadyUsed(@PathParam(value = "userId") String userId) {
	if (DPDoctorUtils.anyStringEmpty(userId)) {
	    logger.warn("Invalid Input");
	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
	}
	String string = forgotPasswordService.checkLinkIsAlreadyUsed(userId);
	Response<String> response = new Response<String>();
	response.setData(string);
	return response;
    }

    @Path(value = PathProxy.ForgotPasswordUrls.FORGOT_USERNAME)
    @POST
    public Response<Boolean> forgotUsername(ForgotUsernamePasswordRequest request) {
	if (request == null) {
	    logger.warn("Invalid Input");
	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
	}
	boolean flag = forgotPasswordService.forgotUsername(request);
	Response<Boolean> response = new Response<Boolean>();
	response.setData(flag);
	return response;
    }

}
