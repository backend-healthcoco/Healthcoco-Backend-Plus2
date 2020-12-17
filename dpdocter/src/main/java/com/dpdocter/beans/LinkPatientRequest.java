package com.dpdocter.beans;

import java.util.List;

public class LinkPatientRequest {

	private String id;
	
	private String referenceNumber;
	
	private List<CareContext> careContexts;

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

	public List<CareContext> getCareContexts() {
		return careContexts;
	}

	public void setCareContexts(List<CareContext> careContexts) {
		this.careContexts = careContexts;
	}

	
	
	
	
	
}
