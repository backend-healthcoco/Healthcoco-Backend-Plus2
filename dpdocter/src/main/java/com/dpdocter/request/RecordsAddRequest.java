package com.dpdocter.request;

import com.dpdocter.beans.FileDetails;

public class RecordsAddRequest {

    private String patientId;

    private String doctorId;

    private String description;

    private FileDetails fileDetails;

    private String locationId;

    private String hospitalId;

    private String recordsType;

    private String createdBy;

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

    public String getRecordsType() {
	return recordsType;
    }

    public void setRecordsType(String recordsType) {
	this.recordsType = recordsType;
    }

    public String getCreatedBy() {
	return createdBy;
    }

    public void setCreatedBy(String createdBy) {
	this.createdBy = createdBy;
    }

	@Override
	public String toString() {
		return "RecordsAddRequest [patientId=" + patientId + ", doctorId=" + doctorId + ", description=" + description
				+ ", fileDetails=" + fileDetails + ", locationId=" + locationId + ", hospitalId=" + hospitalId
				+ ", recordsType=" + recordsType + ", createdBy=" + createdBy + "]";
	}
}
