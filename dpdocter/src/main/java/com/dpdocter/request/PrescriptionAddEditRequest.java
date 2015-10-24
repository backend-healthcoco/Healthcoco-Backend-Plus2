package com.dpdocter.request;

import java.util.List;

import com.dpdocter.beans.PrescriptionItem;

public class PrescriptionAddEditRequest {
    private String id;

    private String name;

    private String doctorId;

    private String locationId;

    private String hospitalId;

    private List<PrescriptionItem> items;

    private String patientId;

    private String prescriptionCode;

    private String createdBy;

    private String visitId;

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
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

    public List<PrescriptionItem> getItems() {
	return items;
    }

    public void setItems(List<PrescriptionItem> items) {
	this.items = items;
    }

    public String getPatientId() {
	return patientId;
    }

    public void setPatientId(String patientId) {
	this.patientId = patientId;
    }

    public String getPrescriptionCode() {
	return prescriptionCode;
    }

    public void setPrescriptionCode(String prescriptionCode) {
	this.prescriptionCode = prescriptionCode;
    }

    public String getCreatedBy() {
	return createdBy;
    }

    public void setCreatedBy(String createdBy) {
	this.createdBy = createdBy;
    }

    public String getVisitId() {
	return visitId;
    }

    public void setVisitId(String visitId) {
	this.visitId = visitId;
    }

    @Override
    public String toString() {
	return "PrescriptionAddEditRequest [id=" + id + ", name=" + name + ", doctorId=" + doctorId + ", locationId=" + locationId + ", hospitalId="
		+ hospitalId + ", items=" + items + ", patientId=" + patientId + ", prescriptionCode=" + prescriptionCode + ", createdBy=" + createdBy
		+ ", visitId=" + visitId + "]";
    }
}
