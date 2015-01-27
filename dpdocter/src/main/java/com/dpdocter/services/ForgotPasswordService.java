package com.dpdocter.services;

import com.dpdocter.request.ForgotPasswordRequest;
import com.dpdocter.request.ResetPasswordRequest;

public interface ForgotPasswordService {
	void forgotPassword(ForgotPasswordRequest request);
	void resetPassword(ResetPasswordRequest request);
}
