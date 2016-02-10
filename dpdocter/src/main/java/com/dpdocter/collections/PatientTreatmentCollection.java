package com.dpdocter.collections;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.PatientTreatment;

@Document(collection = "patient_treatment_cl")
public class PatientTreatmentCollection extends GenericCollection {
    @Id
    private String id;

    @Field
    private List<PatientTreatment> patientTreatments;

    @Field
    private String patientId;

    @Field
    private String locationId;

    @Field
    private String hospitalId;

    @Field
    private String doctorId;

    @Field
    private double totalCost = 0.0;

    @Field
    private boolean discarded = false;

    @Field
    private boolean inHistory = false;

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public List<PatientTreatment> getPatientTreatments() {
	return patientTreatments;
    }

    public void setPatientTreatments(List<PatientTreatment> patientTreatments) {
	this.patientTreatments = patientTreatments;
    }

    public String getPatientId() {
	return patientId;
    }

    public void setPatientId(String patientId) {
	this.patientId = patientId;
    }

    public String getLocationId() {
	return locationId;
    }

    public void setLocationId(String locationId) {
	this.locationId = locationId;
    }

    public String getHospitalId() {
	return hospitalId;
    }

    public void setHospitalId(String hospitalId) {
	this.hospitalId = hospitalId;
    }

    public String getDoctorId() {
	return doctorId;
    }

    public void setDoctorId(String doctorId) {
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
