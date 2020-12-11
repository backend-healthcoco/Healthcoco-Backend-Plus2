package com.dpdocter.beans;

import java.util.List;

public class ConsentDetail {

	private String schemaVersion;
	
	private String consentId;
	
	private String createdAt;
	
	private NdhmNotifyPatient patient;
	
	private List<ConsentCareContext> careContexts;
	
	private ConsentPurpose purpose;
	
	private HipConsent hip;
	
	private HipConsent consentManager;
	
	private List<String> hiTypes;
	
	private ConsentPermission permission;
	
	private String signature;

	public String getSchemaVersion() {
		return schemaVersion;
	}

	public void setSchemaVersion(String schemaVersion) {
		this.schemaVersion = schemaVersion;
	}

	public String getConsentId() {
		return consentId;
	}

	public void setConsentId(String consentId) {
		this.consentId = consentId;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}

	public NdhmNotifyPatient getPatient() {
		return patient;
	}

	public void setPatient(NdhmNotifyPatient patient) {
		this.patient = patient;
	}

	public List<ConsentCareContext> getCareContexts() {
		return careContexts;
	}

	public void setCareContexts(List<ConsentCareContext> careContexts) {
		this.careContexts = careContexts;
	}

	public ConsentPurpose getPurpose() {
		return purpose;
	}

	public void setPurpose(ConsentPurpose purpose) {
		this.purpose = purpose;
	}

	public HipConsent getHip() {
		return hip;
	}

	public void setHip(HipConsent hip) {
		this.hip = hip;
	}

	public HipConsent getConsentManager() {
		return consentManager;
	}

	public void setConsentManager(HipConsent consentManager) {
		this.consentManager = consentManager;
	}

	public List<String> getHiTypes() {
		return hiTypes;
	}

	public void setHiTypes(List<String> hiTypes) {
		this.hiTypes = hiTypes;
	}

	public ConsentPermission getPermission() {
		return permission;
	}

	public void setPermission(ConsentPermission permission) {
		this.permission = permission;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}
	
	
	
}
