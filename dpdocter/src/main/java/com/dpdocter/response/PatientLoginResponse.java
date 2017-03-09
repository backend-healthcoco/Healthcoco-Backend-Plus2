package com.dpdocter.response;

import java.util.List;

import com.dpdocter.beans.RegisteredPatientDetails;

public class PatientLoginResponse {
	private List<RegisteredPatientDetails> detail;
	private OAuth2TokenResponse tokens;

	public List<RegisteredPatientDetails> getDetail() {
		return detail;
	}

	public void setDetail(List<RegisteredPatientDetails> detail) {
		this.detail = detail;
	}

	public OAuth2TokenResponse getTokens() {
		return tokens;
	}

	public void setTokens(OAuth2TokenResponse tokens) {
		this.tokens = tokens;
	}

}
