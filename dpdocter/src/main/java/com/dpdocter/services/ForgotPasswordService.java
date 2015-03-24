package com.dpdocter.services;

import com.dpdocter.request.ForgotPasswordRequest;
import com.dpdocter.request.ResetPasswordRequest;
import com.dpdocter.response.ForgotPasswordResponse;

public interface ForgotPasswordService {
	ForgotPasswordResponse forgotPassword(ForgotPasswordRequest request);

	void resetPassword(ResetPasswordRequest request);

	void forgotUsername(ForgotPasswordRequest request);
}
