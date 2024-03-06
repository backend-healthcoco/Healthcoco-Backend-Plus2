package com.dpdocter.beans;

public class LinkResponse {

	private String referenceNumber;
	
	private String authenticationType;
	
	private LinkMeta meta;

	public String getReferenceNumber() {
		return referenceNumber;
	}

	public void setReferenceNumber(String referenceNumber) {
		this.referenceNumber = referenceNumber;
	}

	public String getAuthenticationType() {
		return authenticationType;
	}

	public void setAuthenticationType(String authenticationType) {
		this.authenticationType = authenticationType;
	}

	public LinkMeta getMeta() {
		return meta;
	}

	public void setMeta(LinkMeta meta) {
		this.meta = meta;
	}
	
	
}
