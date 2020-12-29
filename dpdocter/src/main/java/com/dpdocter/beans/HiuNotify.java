package com.dpdocter.beans;

import java.util.List;

public class HiuNotify {

	private String consentRequestId;
	
	private String status;
	
	private List<NdhmNotifyPatient> consentArtefacts;

	public String getConsentRequestId() {
		return consentRequestId;
	}

	public void setConsentRequestId(String consentRequestId) {
		this.consentRequestId = consentRequestId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public List<NdhmNotifyPatient> getConsentArtefacts() {
		return consentArtefacts;
	}

	public void setConsentArtefacts(List<NdhmNotifyPatient> consentArtefacts) {
		this.consentArtefacts = consentArtefacts;
	}
	
	
}
