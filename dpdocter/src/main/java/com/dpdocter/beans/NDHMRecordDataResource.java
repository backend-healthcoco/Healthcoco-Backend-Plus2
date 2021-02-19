package com.dpdocter.beans;

import java.util.List;

public class NDHMRecordDataResource {
	private String resourceType;
	private String id;
	private NDHMRecordDataMeta meta;
	
	
	private BundleEntryIdentifiers identifiers;
	
	private List<BundleEntryIdentifiers> identifier;
	
	private EntryType type;
    
    private NDHMRecordDataText text; 
    private String status;
    private String intent;
    private NDHMRecordDataCode medicationCodeableConcept;
    private NDHMRecordDataSubject subject;
    private String  authoredOn;
    private String date;
    private List<EntryAuthor> author;
    private String title;
    private List<EntrySection> section;
    
    private List<EntryName> name;
    
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
	
	
	
	public BundleEntryIdentifiers getIdentifiers() {
		return identifiers;
	}
	public void setIdentifiers(BundleEntryIdentifiers identifiers) {
		this.identifiers = identifiers;
	}
	public List<BundleEntryIdentifiers> getIdentifier() {
		return identifier;
	}
	public void setIdentifier(List<BundleEntryIdentifiers> identifier) {
		this.identifier = identifier;
	}
	public EntryType getType() {
		return type;
	}
	public void setType(EntryType type) {
		this.type = type;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public List<EntryAuthor> getAuthor() {
		return author;
	}
	public void setAuthor(List<EntryAuthor> author) {
		this.author = author;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public List<EntrySection> getSection() {
		return section;
	}
	public void setSection(List<EntrySection> section) {
		this.section = section;
	}
	public List<EntryName> getName() {
		return name;
	}
	public void setName(List<EntryName> name) {
		this.name = name;
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
