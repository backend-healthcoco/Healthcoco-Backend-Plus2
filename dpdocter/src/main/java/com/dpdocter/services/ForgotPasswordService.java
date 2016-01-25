package com.dpdocter.services;

import javax.ws.rs.core.UriInfo;

import com.dpdocter.request.ForgotUsernamePasswordRequest;
import com.dpdocter.request.ResetPasswordRequest;
import com.dpdocter.response.ForgotPasswordResponse;

public interface ForgotPasswordService {
    ForgotPasswordResponse forgotPasswordForDoctor(ForgotUsernamePasswordRequest request, UriInfo uriInfo);

    Boolean forgotPasswordForPatient(ForgotUsernamePasswordRequest request, UriInfo uriInfo);

    String resetPassword(ResetPasswordRequest request);

    Boolean forgotUsername(ForgotUsernamePasswordRequest request);

    ForgotPasswordResponse getEmailAndMobNumberOfPatient(String username);

    String resetPassword(String userId, String password);

	String checkLinkIsAlreadyUsed(String userId);

}
