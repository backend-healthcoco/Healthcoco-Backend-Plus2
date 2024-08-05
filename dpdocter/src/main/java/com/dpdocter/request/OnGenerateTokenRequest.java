package com.dpdocter.request;

import com.dpdocter.beans.FetchResponse;

public class OnGenerateTokenRequest {
	private String abhaAddress;

	private String linkToken;
	private FetchResponse response;

	public String getAbhaAddress() {
		return abhaAddress;
	}

	public void setAbhaAddress(String abhaAddress) {
		this.abhaAddress = abhaAddress;
	}

	public String getLinkToken() {
		return linkToken;
	}

	public void setLinkToken(String linkToken) {
		this.linkToken = linkToken;
	}

	public FetchResponse getResponse() {
		return response;
	}

	public void setResponse(FetchResponse response) {
		this.response = response;
	}

}
