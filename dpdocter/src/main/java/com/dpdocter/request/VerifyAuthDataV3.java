package com.dpdocter.request;

import java.util.List;

import com.dpdocter.beans.AuthDataOtp;

public class VerifyAuthDataV3 {
	private List<String> authMethods;

	private AuthDataOtp otp;

	public List<String> getAuthMethods() {
		return authMethods;
	}

	public void setAuthMethods(List<String> authMethods) {
		this.authMethods = authMethods;
	}

	public AuthDataOtp getOtp() {
		return otp;
	}

	public void setOtp(AuthDataOtp otp) {
		this.otp = otp;
	}

}
