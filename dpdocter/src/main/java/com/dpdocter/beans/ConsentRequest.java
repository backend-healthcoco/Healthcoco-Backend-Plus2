package com.dpdocter.beans;

import java.util.List;

public class ConsentRequest {

	private String id;
	
	private String status;
	
	private List<ConsentArtifact> consentArtefacts;

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

	public List<ConsentArtifact> getConsentArtefacts() {
		return consentArtefacts;
	}

	public void setConsentArtefacts(List<ConsentArtifact> consentArtefacts) {
		this.consentArtefacts = consentArtefacts;
	}

	
	
	
}
