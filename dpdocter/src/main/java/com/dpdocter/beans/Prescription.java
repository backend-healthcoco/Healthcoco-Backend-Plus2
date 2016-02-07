package com.dpdocter.beans;

import java.util.List;

import com.dpdocter.collections.GenericCollection;
import com.dpdocter.response.TestAndRecordDataResponse;

public class Prescription extends GenericCollection {
    private String id;

    private String uniqueId;

    private String name;

    private String doctorId;

    private String locationId;

    private String hospitalId;

    private List<PrescriptionItemDetail> items;

    private boolean inHistory = false;

    private Boolean discarded;

    private List<TestAndRecordDataResponse> tests;

    private String advice;

    private String visitId;

    private String patientId;

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public String getUniqueId() {
	return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
	this.uniqueId = uniqueId;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public List<PrescriptionItemDetail> getItems() {
	return items;
    }

    public void setItems(List<PrescriptionItemDetail> items) {
	this.items = items;
    }

    public boolean isInHistory() {
	return inHistory;
    }

    public void setInHistory(boolean inHistory) {
	this.inHistory = inHistory;
    }

    public Boolean getDiscarded() {
	return discarded;
    }

    public void setDiscarded(Boolean discarded) {
	this.discarded = discarded;
    }

    public String getDoctorId() {
	return doctorId;
    }

    public void setDoctorId(String doctorId) {
	this.doctorId = doctorId;
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

    public String getAdvice() {
	return advice;
    }

    public void setAdvice(String advice) {
	this.advice = advice;
    }

    public String getVisitId() {
	return visitId;
    }

    public void setVisitId(String visitId) {
	this.visitId = visitId;
    }

    public String getPatientId() {
	return patientId;
    }

    public void setPatientId(String patientId) {
	this.patientId = patientId;
    }

    public List<TestAndRecordDataResponse> getTests() {
	return tests;
    }

    public void setTests(List<TestAndRecordDataResponse> tests) {
	this.tests = tests;
    }

    @Override
    public String toString() {
	return "Prescription [id=" + id + ", uniqueId=" + uniqueId + ", name=" + name + ", doctorId=" + doctorId + ", locationId=" + locationId
		+ ", hospitalId=" + hospitalId + ", items=" + items + ", inHistory=" + inHistory + ", discarded=" + discarded + ", tests=" + tests
		+ ", advice=" + advice + ", visitId=" + visitId + ", patientId=" + patientId + "]";
    }
}
