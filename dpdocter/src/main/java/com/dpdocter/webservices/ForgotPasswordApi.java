package com.dpdocter.webservices;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.request.ForgotPasswordRequest;
import com.dpdocter.request.ResetPasswordRequest;
import com.dpdocter.services.ForgotPasswordService;
import common.util.web.Response;

@Component
@Path(PathProxy.FORGOT_PASSWORD_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ForgotPasswordApi {

	@Autowired
	private ForgotPasswordService forgotPasswordService;

	@Path(value = PathProxy.ForgotPasswordUrls.FORGOT_PASSWORD)
	@POST
	public Response<String> forgotPassword(ForgotPasswordRequest request) {
		if (request == null) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		forgotPasswordService.forgotPassword(request);
		Response<String> response = new Response<String>();
		response.setData("RESET YOUR PASSWORD FROM EMAIL ADDRESS");
		return response;
	}

	@Path(value = PathProxy.ForgotPasswordUrls.RESET_PASSWORD)
	@POST
	public Response<String> resetPassword(ResetPasswordRequest request) {
		if (request == null) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		forgotPasswordService.resetPassword(request);
		Response<String> response = new Response<String>();
		response.setData("PASSWORD CHANGED SUCCESSFULLY.");
		return response;
	}

	@Path(value = PathProxy.ForgotPasswordUrls.FORGOT_USERNAME)
	@POST
	public void forgotUsername(ForgotPasswordRequest request) {
		if (request == null) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		forgotPasswordService.forgotUsername(request);
	}
}
