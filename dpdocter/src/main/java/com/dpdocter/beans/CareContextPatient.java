package com.dpdocter.beans;

public class CareContextPatient {

	private String referenceNumber;
	
	private String display;
	
	private CareContext careContexts;

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

	public CareContext getCareContexts() {
		return careContexts;
	}

	public void setCareContexts(CareContext careContexts) {
		this.careContexts = careContexts;
	}
	
	
}
