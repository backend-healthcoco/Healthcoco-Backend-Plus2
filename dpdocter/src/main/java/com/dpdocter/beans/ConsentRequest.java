package com.dpdocter.beans;

public class ConsentRequest {

	private String id;
	
	private String status;
	
	private ConsentArtifact consentArtefacts;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public ConsentArtifact getConsentArtefacts() {
		return consentArtefacts;
	}

	public void setConsentArtefacts(ConsentArtifact consentArtefacts) {
		this.consentArtefacts = consentArtefacts;
	}
	
	
}
