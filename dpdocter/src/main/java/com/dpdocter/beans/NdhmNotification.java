package com.dpdocter.beans;

public class NdhmNotification {

	private String status;

	private String consentId;

	private ConsentDetail consentDetail;

	private Boolean grantAcknowledgement;
	private String signature;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getConsentId() {
		return consentId;
	}

	public void setConsentId(String consentId) {
		this.consentId = consentId;
	}

	public ConsentDetail getConsentDetail() {
		return consentDetail;
	}

	public void setConsentDetail(ConsentDetail consentDetail) {
		this.consentDetail = consentDetail;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public Boolean getGrantAcknowledgement() {
		return grantAcknowledgement;
	}

	public void setGrantAcknowledgement(Boolean grantAcknowledgement) {
		this.grantAcknowledgement = grantAcknowledgement;
	}

}
