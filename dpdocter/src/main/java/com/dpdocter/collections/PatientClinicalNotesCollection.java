package com.dpdocter.collections;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "patient_clinical_notes_cl")
public class PatientClinicalNotesCollection extends GenericCollection {
    @Id
    private String id;

    @Field
    private String patientId;

    @Field
    private String clinicalNotesId;

    @Field
    private Boolean discarded;

    @Field
    private Long createdDate;

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public String getPatientId() {
	return patientId;
    }

    public void setPatientId(String patientId) {
	this.patientId = patientId;
    }

    public String getClinicalNotesId() {
	return clinicalNotesId;
    }

    public void setClinicalNotesId(String clinicalNotesId) {
	this.clinicalNotesId = clinicalNotesId;
    }

    public Boolean getDiscarded() {
	return discarded;
    }

    public void setDiscarded(Boolean discarded) {
	this.discarded = discarded;
    }

    @Override
    public String toString() {
	return "PatientClinicalNotesCollection [id=" + id + ", patientId=" + patientId + ", clinicalNotesId=" + clinicalNotesId + ", discarded=" + discarded
		+ "]";
    }

}
