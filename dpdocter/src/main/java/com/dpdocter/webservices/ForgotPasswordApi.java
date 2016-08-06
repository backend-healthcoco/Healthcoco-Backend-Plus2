package com.dpdocter.webservices;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

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
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Component
@Path(PathProxy.FORGOT_PASSWORD_BASE_URL)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.FORGOT_PASSWORD_BASE_URL, description = "Endpoint for forgot password")
public class ForgotPasswordApi {

    private static Logger logger = Logger.getLogger(ForgotPasswordApi.class.getName());

    @Autowired
    private ForgotPasswordService forgotPasswordService;

    @Path(value = PathProxy.ForgotPasswordUrls.FORGOT_PASSWORD_DOCTOR)
    @Produces(MediaType.APPLICATION_JSON)
    @POST
    @ApiOperation(value = PathProxy.ForgotPasswordUrls.FORGOT_PASSWORD_DOCTOR, notes = PathProxy.ForgotPasswordUrls.FORGOT_PASSWORD_DOCTOR)
    public Response<String> forgotPassword(ForgotUsernamePasswordRequest request) {
	if (request == null || DPDoctorUtils.anyStringEmpty(request.getEmailAddress())) {
	    logger.warn("Invalid Input");
	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
	}
	forgotPasswordService.forgotPasswordForDoctor(request);
	Response<String> response = new Response<String>();
	response.setData("RESET YOUR PASSWORD FROM EMAIL ADDRESS");
	return response;
    }

    @Path(value = PathProxy.ForgotPasswordUrls.FORGOT_PASSWORD_PATIENT)
    @Produces(MediaType.APPLICATION_JSON)
    @POST
    @ApiOperation(value = PathProxy.ForgotPasswordUrls.FORGOT_PASSWORD_PATIENT, notes = PathProxy.ForgotPasswordUrls.FORGOT_PASSWORD_PATIENT)
    public Response<Boolean> forgotPasswordForPatient(ForgotUsernamePasswordRequest request) {
	if (request == null || DPDoctorUtils.anyStringEmpty(request.getMobileNumber())) {
	    logger.warn("Invalid Input");
	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
	}
	boolean flag = forgotPasswordService.forgotPasswordForPatient(request);
	Response<Boolean> response = new Response<Boolean>();
	response.setData(flag);
	return response;
    }

    @Produces(MediaType.APPLICATION_JSON)
    @Path(value = PathProxy.ForgotPasswordUrls.RESET_PASSWORD_PATIENT)
    @POST
    @ApiOperation(value = PathProxy.ForgotPasswordUrls.RESET_PASSWORD_PATIENT, notes = PathProxy.ForgotPasswordUrls.RESET_PASSWORD_PATIENT)
    public Response<Boolean> resetPasswordPatient(ResetPasswordRequest request) {
    	if (request == null || DPDoctorUtils.anyStringEmpty(request.getMobileNumber())) {
    	    logger.warn("Invalid Input");
    	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
    	}
	Boolean isReset = forgotPasswordService.resetPasswordPatient(request);
	Response<Boolean> response = new Response<Boolean>();
	response.setData(isReset);
	return response;
    }

    @Produces(MediaType.TEXT_HTML)
    @Path(value = PathProxy.ForgotPasswordUrls.RESET_PASSWORD)
    @POST
    @ApiOperation(value = PathProxy.ForgotPasswordUrls.RESET_PASSWORD, notes = PathProxy.ForgotPasswordUrls.RESET_PASSWORD)
    public String resetPassword(ResetPasswordRequest request) {
    	if (request == null || DPDoctorUtils.anyStringEmpty(request.getUserId())) {
    	    logger.warn("Invalid Input");
    	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
    	}
	String string = forgotPasswordService.resetPassword(request);
	return string;
    }

    @Produces(MediaType.TEXT_HTML)
    @Path(value = PathProxy.ForgotPasswordUrls.CHECK_LINK_IS_ALREADY_USED)
    @GET
    @ApiOperation(value = PathProxy.ForgotPasswordUrls.CHECK_LINK_IS_ALREADY_USED, notes = PathProxy.ForgotPasswordUrls.CHECK_LINK_IS_ALREADY_USED)
    public String checkLinkIsAlreadyUsed(@PathParam(value = "userId") String userId) {
	if (DPDoctorUtils.anyStringEmpty(userId)) {
	    logger.warn("Invalid Input");
	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
	}
	String string = forgotPasswordService.checkLinkIsAlreadyUsed(userId);
	return string;
    }

    @Produces(MediaType.APPLICATION_JSON)
    @Path(value = PathProxy.ForgotPasswordUrls.FORGOT_USERNAME)
    @POST
    @ApiOperation(value = PathProxy.ForgotPasswordUrls.FORGOT_USERNAME, notes = PathProxy.ForgotPasswordUrls.FORGOT_USERNAME)
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
