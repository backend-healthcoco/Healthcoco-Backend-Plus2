package com.dpdocter.request;

import java.util.List;

public class GatewayConsentInitRequestBody {

	private ConsentPurposeRequest purpose;
	private ConsentDataFlowRequest patient;
	private ConsentDataFlowRequest hip;
	private ConsentDataFlowRequest hiu;
	private List<String> hiTypes;
	private List<CareContextsRequest> careContexts;
	private ConsentRequester requester;
	private ConsentPermissionRequest permission;
	public ConsentPurposeRequest getPurpose() {
		return purpose;
	}
	public void setPurpose(ConsentPurposeRequest purpose) {
		this.purpose = purpose;
	}
	
	public ConsentDataFlowRequest getPatient() {
		return patient;
	}
	public void setPatient(ConsentDataFlowRequest patient) {
		this.patient = patient;
	}
	public ConsentDataFlowRequest getHip() {
		return hip;
	}
	public void setHip(ConsentDataFlowRequest hip) {
		this.hip = hip;
	}
	public ConsentDataFlowRequest getHiu() {
		return hiu;
	}
	public void setHiu(ConsentDataFlowRequest hiu) {
		this.hiu = hiu;
	}
	public List<String> getHiTypes() {
		return hiTypes;
	}
	public void setHiTypes(List<String> hiTypes) {
		this.hiTypes = hiTypes;
	}
	public List<CareContextsRequest> getCareContexts() {
		return careContexts;
	}
	public void setCareContexts(List<CareContextsRequest> careContexts) {
		this.careContexts = careContexts;
	}
	public ConsentRequester getRequester() {
		return requester;
	}
	public void setRequester(ConsentRequester requester) {
		this.requester = requester;
	}
	public ConsentPermissionRequest getPermission() {
		return permission;
	}
	public void setPermission(ConsentPermissionRequest permission) {
		this.permission = permission;
	}
	
	
	
	
}
