package com.dpdocter.services;

import java.util.List;

import com.dpdocter.request.ForgotUsernamePasswordRequest;
import com.dpdocter.request.ResetPasswordRequest;
import com.dpdocter.response.ForgotPasswordResponse;

public interface ForgotPasswordService {
	ForgotPasswordResponse forgotPasswordForDoctor(ForgotUsernamePasswordRequest request);
	
	ForgotPasswordResponse forgotPasswordForPatient(ForgotUsernamePasswordRequest request);

	void resetPassword(ResetPasswordRequest request);

	void forgotUsername(ForgotUsernamePasswordRequest request);
	
	List<ForgotPasswordResponse> getEmailAndMobNumberOfPatient(String username);
	
	
}
