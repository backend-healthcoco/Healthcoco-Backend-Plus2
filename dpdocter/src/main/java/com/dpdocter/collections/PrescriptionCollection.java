package com.dpdocter.collections;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.PrescriptionItem;

@Document(collection = "prescription_cl")
public class PrescriptionCollection extends GenericCollection {
    @Field
    private String id;

    @Field
    private String name;

    @Field
    private String doctorId;

    @Field
    private String locationId;

    @Field
    private String hospitalId;

    @Field
    private Boolean isDeleted = false;

    @Field
    private List<PrescriptionItem> items;

    @Field
    private String patientId;

    @Field
    private String prescriptionCode;

    @Field
    private boolean inHistory = false;

    @Field
    private Long createdDate;

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

    public Boolean getIsDeleted() {
	return isDeleted;
    }

    public void setIsDeleted(Boolean isDeleted) {
	this.isDeleted = isDeleted;
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

    public boolean isInHistory() {
	return inHistory;
    }

    public void setInHistory(boolean inHistory) {
	this.inHistory = inHistory;
    }

    public Long getCreatedDate() {
	return createdDate;
    }

    public void setCreatedDate(Long createdDate) {
	this.createdDate = createdDate;
    }

    @Override
    public String toString() {
	return "PrescriptionCollection [id=" + id + ", name=" + name + ", doctorId=" + doctorId + ", locationId=" + locationId + ", hospitalId=" + hospitalId
		+ ", isDeleted=" + isDeleted + ", items=" + items + ", patientId=" + patientId + ", prescriptionCode=" + prescriptionCode + ", inHistory="
		+ inHistory + ", createdDate=" + createdDate + "]";
    }

}
