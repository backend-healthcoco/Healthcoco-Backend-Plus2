package com.dpdocter.beans;

import java.util.List;

public class DiscoverPatientResponse {

	private String referenceNumber;
	
	private String display;
	
	private List<CareContext> careContexts;
	
	private List<String>matchedBy;

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

	public List<String> getMatchedBy() {
		return matchedBy;
	}

	public void setMatchedBy(List<String> matchedBy) {
		this.matchedBy = matchedBy;
	}

	public List<CareContext> getCareContexts() {
		return careContexts;
	}

	public void setCareContexts(List<CareContext> careContexts) {
		this.careContexts = careContexts;
	}
	
	
	
	
}
