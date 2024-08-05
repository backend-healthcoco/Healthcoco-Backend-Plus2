package com.dpdocter.request;

import java.util.List;

public class EnrollmentV3Request {
	private List<String> scope;

	private String loginHint;
	private String loginId;

	private String otpSystem;

	public List<String> getScope() {
		return scope;
	}

	public void setScope(List<String> scope) {
		this.scope = scope;
	}

	public String getLoginHint() {
		return loginHint;
	}

	public void setLoginHint(String loginHint) {
		this.loginHint = loginHint;
	}

	public String getLoginId() {
		return loginId;
	}

	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}

	public String getOtpSystem() {
		return otpSystem;
	}

	public void setOtpSystem(String otpSystem) {
		this.otpSystem = otpSystem;
	}

}
