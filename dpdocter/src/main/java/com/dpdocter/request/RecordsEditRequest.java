package com.dpdocter.request;

import com.dpdocter.beans.FileDetails;

public class RecordsEditRequest {

    private String patientId;

    private String doctorId;

    private Long createdDate;

    private String description;

    private FileDetails fileDetails;

    private String locationId;

    private String hospitalId;

    public String getPatientId() {
	return patientId;
    }

    public void setPatientId(String patientId) {
	this.patientId = patientId;
    }

    public String getDoctorId() {
	return doctorId;
    }

    public void setDoctorId(String doctorId) {
	this.doctorId = doctorId;
    }

    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
    }

    public Long getCreatedDate() {
	return createdDate;
    }

    public void setCreatedDate(Long createdDate) {
	this.createdDate = createdDate;
    }

    public FileDetails getFileDetails() {
	return fileDetails;
    }

    public void setFileDetails(FileDetails fileDetails) {
	this.fileDetails = fileDetails;
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

    @Override
    public String toString() {
	return "RecordsEditRequest [patientId=" + patientId + ", doctorId=" + doctorId + ", createdDate=" + createdDate + ", description=" + description
		+ ", fileDetails=" + fileDetails + ", locationId=" + locationId + ", hospitalId=" + hospitalId + "]";
    }

}
