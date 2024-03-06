package com.dpdocter.beans;

import com.dpdocter.enums.NdhmAuthMethods;
import com.dpdocter.enums.NdhmPurpose;

public class FetchModeQuery {

	private String id;
	
	private NdhmPurpose purpose;
	
	private NdhmAuthMethods authMode;
	
	private FetchModesRequester requester;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public NdhmPurpose getPurpose() {
		return purpose;
	}

	public void setPurpose(NdhmPurpose purpose) {
		this.purpose = purpose;
	}

	public FetchModesRequester getRequester() {
		return requester;
	}

	public void setRequester(FetchModesRequester requester) {
		this.requester = requester;
	}

	public NdhmAuthMethods getAuthMode() {
		return authMode;
	}

	public void setAuthMode(NdhmAuthMethods authMode) {
		this.authMode = authMode;
	}
	
	
	
	
}
