package com.dpdocter.webservices;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
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

@Component
@Path(PathProxy.FORGOT_PASSWORD_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ForgotPasswordApi {

    private static Logger logger = Logger.getLogger(ForgotPasswordApi.class.getName());

    @Autowired
    private ForgotPasswordService forgotPasswordService;

    @Path(value = PathProxy.ForgotPasswordUrls.FORGOT_PASSWORD_DOCTOR)
    @POST
    public Response<String> forgotPassword(ForgotUsernamePasswordRequest request) {
	if (request == null) {
	    logger.warn("Invalid Input");
	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
	}
	forgotPasswordService.forgotPasswordForDoctor(request);
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
	boolean flag = forgotPasswordService.forgotPasswordForPatient(request);
	Response<Boolean> response = new Response<Boolean>();
	response.setData(flag);
	return response;
    }

    @Produces(MediaType.TEXT_HTML)
    @Path(value = PathProxy.ForgotPasswordUrls.RESET_PASSWORD)
    @POST
    public String resetPassword(ResetPasswordRequest request) {
	if (request == null) {
	    logger.warn("Invalid Input");
	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
	}
	String response = forgotPasswordService.resetPassword(request);
	return "<html><body>" + response + "</body></html>";
    }

    @Produces(MediaType.TEXT_HTML)
    @Consumes({ MediaType.APPLICATION_FORM_URLENCODED })
    @Path(value = PathProxy.ForgotPasswordUrls.RESET_PASSWORD_GET)
    @POST
    public String resetPassword(@FormParam(value = "userId") String userId, @FormParam(value = "password") String password) {
	if (DPDoctorUtils.anyStringEmpty(userId, password)) {
	    logger.warn("Invalid Input");
	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
	}
	String response = forgotPasswordService.resetPassword(userId, password);
	return "<html><body>" + response + "</body></html>";
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
