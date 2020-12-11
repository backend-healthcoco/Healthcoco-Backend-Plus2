package com.dpdocter.beans;

public class LinkPatientRequest {

	private String id;
	
	private String referenceNumber;
	
	private CareContext careContexts;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getReferenceNumber() {
		return referenceNumber;
	}

	public void setReferenceNumber(String referenceNumber) {
		this.referenceNumber = referenceNumber;
	}

	public CareContext getCareContexts() {
		return careContexts;
	}

	public void setCareContexts(CareContext careContexts) {
		this.careContexts = careContexts;
	}
	
	
	
	
}
