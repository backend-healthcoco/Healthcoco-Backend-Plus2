package com.dpdocter.services;

import java.util.Date;

import com.dpdocter.request.ForgotUsernamePasswordRequest;
import com.dpdocter.request.ResetPasswordRequest;
import com.dpdocter.response.ForgotPasswordResponse;

public interface ForgotPasswordService {
	ForgotPasswordResponse forgotPasswordForDoctor(ForgotUsernamePasswordRequest request);

	Boolean forgotPasswordForPatient(ForgotUsernamePasswordRequest request);

	Boolean forgotUsername(ForgotUsernamePasswordRequest request);

	ForgotPasswordResponse getEmailAndMobNumberOfPatient(String username);

	String resetPassword(ResetPasswordRequest request);

	String checkLinkIsAlreadyUsed(String userId);

	Boolean resetPasswordPatient(ResetPasswordRequest request);

	boolean isLinkValid(Date createdTime);

	String resetPasswordPharmacy(ResetPasswordRequest request);

	String resetPasswordCB(ResetPasswordRequest request);

	public String resetPasswordForConference(ResetPasswordRequest request);

}
