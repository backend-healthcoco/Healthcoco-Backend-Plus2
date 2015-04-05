package com.dpdocter.services;

import java.util.List;

import com.dpdocter.request.ForgotUsernamePasswordRequest;
import com.dpdocter.request.ResetPasswordRequest;
import com.dpdocter.response.ForgotPasswordResponse;

import common.util.web.Response;

public interface ForgotPasswordService {
	ForgotPasswordResponse forgotPasswordForDoctor(ForgotUsernamePasswordRequest request);

	Response<Boolean> forgotPasswordForPatient(ForgotUsernamePasswordRequest request);

	void resetPassword(ResetPasswordRequest request);

	Response<Boolean> forgotUsername(ForgotUsernamePasswordRequest request);

	ForgotPasswordResponse getEmailAndMobNumberOfPatient(String username);

}
