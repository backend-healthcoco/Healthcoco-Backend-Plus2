package com.dpdocter.beans;

import java.util.List;

public class NDHMRecordDataResource {
	private String resourceType;
	private String id;
	private NDHMRecordDataMeta meta;
	
    
    private NDHMRecordDataText text; 
    private String status;
    private String intent;
    private NDHMRecordDataCode medicationCodeableConcept;
    private NDHMRecordDataSubject subject;
    private String  authoredOn;
    private NDHMRecordDataRequester requester;
    private NDHMRecordDataCode reasonCode; 
    private List<NDHMRecordDataReasonReference> reasonReference;
    private List<NDHMRecordDataDosageInstruction> dosageInstruction;
	public String getResourceType() {
		return resourceType;
	}
	public void setResourceType(String resourceType) {
		this.resourceType = resourceType;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public NDHMRecordDataMeta getMeta() {
		return meta;
	}
	public void setMeta(NDHMRecordDataMeta meta) {
		this.meta = meta;
	}
	public NDHMRecordDataText getText() {
		return text;
	}
	public void setText(NDHMRecordDataText text) {
		this.text = text;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getIntent() {
		return intent;
	}
	public void setIntent(String intent) {
		this.intent = intent;
	}
	public NDHMRecordDataCode getMedicationCodeableConcept() {
		return medicationCodeableConcept;
	}
	public void setMedicationCodeableConcept(NDHMRecordDataCode medicationCodeableConcept) {
		this.medicationCodeableConcept = medicationCodeableConcept;
	}
	public NDHMRecordDataSubject getSubject() {
		return subject;
	}
	public void setSubject(NDHMRecordDataSubject subject) {
		this.subject = subject;
	}
	public String getAuthoredOn() {
		return authoredOn;
	}
	public void setAuthoredOn(String authoredOn) {
		this.authoredOn = authoredOn;
	}
	public NDHMRecordDataRequester getRequester() {
		return requester;
	}
	public void setRequester(NDHMRecordDataRequester requester) {
		this.requester = requester;
	}
	public NDHMRecordDataCode getReasonCode() {
		return reasonCode;
	}
	public void setReasonCode(NDHMRecordDataCode reasonCode) {
		this.reasonCode = reasonCode;
	}
	public List<NDHMRecordDataReasonReference> getReasonReference() {
		return reasonReference;
	}
	public void setReasonReference(List<NDHMRecordDataReasonReference> reasonReference) {
		this.reasonReference = reasonReference;
	}
	public List<NDHMRecordDataDosageInstruction> getDosageInstruction() {
		return dosageInstruction;
	}
	public void setDosageInstruction(List<NDHMRecordDataDosageInstruction> dosageInstruction) {
		this.dosageInstruction = dosageInstruction;
	}
	@Override
	public String toString() {
		return "NDHMRecordDataResource [resourceType=" + resourceType + ", id=" + id + ", meta=" + meta + ", text="
				+ text + ", status=" + status + ", intent=" + intent + ", medicationCodeableConcept="
				+ medicationCodeableConcept + ", subject=" + subject + ", authoredOn=" + authoredOn + ", requester="
				+ requester + ", reasonCode=" + reasonCode + ", reasonReference=" + reasonReference
				+ ", dosageInstruction=" + dosageInstruction + "]";
	}
}
