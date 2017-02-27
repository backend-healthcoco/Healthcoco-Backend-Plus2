package com.dpdocter.request;

public class OrderDrugsRequest {

	private String localeId;
	
	private String patientId;
	
	private String uniqueRequestId;

	private String uniqueResponseId;
	

	public String getLocaleId() {
		return localeId;
	}

	public void setLocaleId(String localeId) {
		this.localeId = localeId;
	}

	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}

	public String getUniqueRequestId() {
		return uniqueRequestId;
	}

	public void setUniqueRequestId(String uniqueRequestId) {
		this.uniqueRequestId = uniqueRequestId;
	}

	public String getUniqueResponseId() {
		return uniqueResponseId;
	}

	public void setUniqueResponseId(String uniqueResponseId) {
		this.uniqueResponseId = uniqueResponseId;
	}

	@Override
	public String toString() {
		return "OrderDrugsRequest [localeId=" + localeId + ", patientId=" + patientId + ", uniqueRequestId="
				+ uniqueRequestId + ", uniqueResponseId=" + uniqueResponseId + "]";
	}

}
