package com.dpdocter.request;

public class CareContextsRequest {
	
	private String patientReference;
    private String careContextReference;
	public String getPatientReference() {
		return patientReference;
	}
	public void setPatientReference(String patientReference) {
		this.patientReference = patientReference;
	}
	public String getCareContextReference() {
		return careContextReference;
	}
	public void setCareContextReference(String careContextReference) {
		this.careContextReference = careContextReference;
	}
    
    
}
