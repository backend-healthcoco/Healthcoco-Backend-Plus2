package com.dpdocter.beans;

public class PatientQuery {

	private NdhmNotifyPatient patient;
	
	private FetchModesRequester requester;

	public NdhmNotifyPatient getPatient() {
		return patient;
	}

	public void setPatient(NdhmNotifyPatient patient) {
		this.patient = patient;
	}

	public FetchModesRequester getRequester() {
		return requester;
	}

	public void setRequester(FetchModesRequester requester) {
		this.requester = requester;
	}
	
	
	
}
