package com.dpdocter.response;

import java.util.List;

import com.dpdocter.beans.PatientTreatment;
import com.dpdocter.collections.GenericCollection;

public class PatientTreatmentResponse extends GenericCollection {
    private String id;

    private List<PatientTreatment> patientTreatments;

    private String patientId;

    private String locationId;

    private String hospitalId;

    private String doctorId;

    private double totalCost = 0.0;

    private boolean discarded = false;

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

    @Override
    public String toString() {
	return "PatientTreatmentResponse [id=" + id + ", patientTreatments=" + patientTreatments + ", patientId=" + patientId + ", locationId=" + locationId
		+ ", hospitalId=" + hospitalId + ", doctorId=" + doctorId + ", totalCost=" + totalCost + ", discarded=" + discarded + "]";
    }

}
