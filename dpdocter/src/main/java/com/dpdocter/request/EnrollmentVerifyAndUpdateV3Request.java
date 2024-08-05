package com.dpdocter.request;

import java.util.List;

public class EnrollmentVerifyAndUpdateV3Request {
	private List<String> scope;

	private VerifyAuthDataV3 authData;

	public List<String> getScope() {
		return scope;
	}

	public void setScope(List<String> scope) {
		this.scope = scope;
	}

	public VerifyAuthDataV3 getAuthData() {
		return authData;
	}

	public void setAuthData(VerifyAuthDataV3 authData) {
		this.authData = authData;
	}

}
