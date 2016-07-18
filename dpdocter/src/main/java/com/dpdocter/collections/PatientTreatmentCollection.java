package com.dpdocter.collections;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.PatientTreatment;

@Document(collection = "patient_treatment_cl")
public class PatientTreatmentCollection extends GenericCollection {
    @Id
    private ObjectId id;

    @Field
    private List<PatientTreatment> patientTreatments;

    @Field
    private ObjectId patientId;

    @Field
    private ObjectId locationId;

    @Field
    private ObjectId hospitalId;

    @Field
    private ObjectId doctorId;

    @Field
    private double totalCost = 0.0;

    @Field
    private boolean discarded = false;

    @Field
    private boolean inHistory = false;

    public ObjectId getId() {
	return id;
    }

    public void setId(ObjectId id) {
	this.id = id;
    }

    public List<PatientTreatment> getPatientTreatments() {
	return patientTreatments;
    }

    public void setPatientTreatments(List<PatientTreatment> patientTreatments) {
	this.patientTreatments = patientTreatments;
    }

    public ObjectId getPatientId() {
	return patientId;
    }

    public void setPatientId(ObjectId patientId) {
	this.patientId = patientId;
    }

    public ObjectId getLocationId() {
	return locationId;
    }

    public void setLocationId(ObjectId locationId) {
	this.locationId = locationId;
    }

    public ObjectId getHospitalId() {
	return hospitalId;
    }

    public void setHospitalId(ObjectId hospitalId) {
	this.hospitalId = hospitalId;
    }

    public ObjectId getDoctorId() {
	return doctorId;
    }

    public void setDoctorId(ObjectId doctorId) {
	this.doctorId = doctorId;
    }

    public double getTotalCost() {
	return totalCost;
    }

    public void setTotalCost(double totalCost) {
	this.totalCost = totalCost;
    }

    public boolean isDiscarded() {
	return discarded;
    }

    public void setDiscarded(boolean discarded) {
	this.discarded = discarded;
    }

    public boolean isInHistory() {
	return inHistory;
    }

    public void setInHistory(boolean inHistory) {
	this.inHistory = inHistory;
    }

    @Override
    public String toString() {
	return "PatientTreatmentCollection [id=" + id + ", patientTreatments=" + patientTreatments + ", patientId=" + patientId + ", locationId=" + locationId
		+ ", hospitalId=" + hospitalId + ", doctorId=" + doctorId + ", totalCost=" + totalCost + ", discarded=" + discarded + ", inHistory=" + inHistory
		+ "]";
    }

}
