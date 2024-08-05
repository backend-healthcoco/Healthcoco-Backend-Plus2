package com.dpdocter.beans;

public class NdhmValidity {
	
	private String purpose;

	private NdhmRequester requester;

	private String expiry;

	private String limit;

	public String getPurpose() {
		return purpose;
	}

	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}

	public NdhmRequester getRequester() {
		return requester;
	}

	public void setRequester(NdhmRequester requester) {
		this.requester = requester;
	}

	public String getExpiry() {
		return expiry;
	}

	public void setExpiry(String expiry) {
		this.expiry = expiry;
	}

	public String getLimit() {
		return limit;
	}

	public void setLimit(String limit) {
		this.limit = limit;
	}

   
}
