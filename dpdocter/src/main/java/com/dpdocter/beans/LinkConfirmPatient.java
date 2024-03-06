package com.dpdocter.beans;

import java.util.List;

public class LinkConfirmPatient {

	private String referenceNumber;
	
	private String display;
	
	private List<CareContext> careContexts;
	
	public String getReferenceNumber() {
		return referenceNumber;
	}

	public void setReferenceNumber(String referenceNumber) {
		this.referenceNumber = referenceNumber;
	}

	public String getDisplay() {
		return display;
	}

	public void setDisplay(String display) {
		this.display = display;
	}

	public List<CareContext> getCareContexts() {
		return careContexts;
	}

	public void setCareContexts(List<CareContext> careContexts) {
		this.careContexts = careContexts;
	}

	

}
