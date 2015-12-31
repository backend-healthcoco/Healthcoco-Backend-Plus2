package com.dpdocter.collections;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "records_cl")
public class RecordsCollection extends GenericCollection {

    @Id
    private String id;

    @Field
    private String recordsUrl;

    @Field
    private String recordsPath;

    @Field
    private String recordsLable;

    @Field
    private String recordsType;

    @Field
    private String description;

    @Field
    private String patientId;

    @Field
    private String doctorId;

    @Field
    private String locationId;

    @Field
    private String hospitalId;

    @Field
    private Boolean discarded = false;

    @Field
    private boolean inHistory = false;

    @Field
    private String uploadedByLocation;
    
    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public String getRecordsUrl() {
	return recordsUrl;
    }

    public void setRecordsUrl(String recordsUrl) {
	this.recordsUrl = recordsUrl;
    }

    public String getRecordsPath() {
	return recordsPath;
    }

    public void setRecordsPath(String recordsPath) {
	this.recordsPath = recordsPath;
    }

    public String getRecordsLable() {
	return recordsLable;
    }

    public void setRecordsLable(String recordsLable) {
	this.recordsLable = recordsLable;
    }

    public String getRecordsType() {
	return recordsType;
    }

    public void setRecordsType(String recordsType) {
	this.recordsType = recordsType;
    }

    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
    }

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

    public Boolean getDiscarded() {
	return discarded;
    }

    public void setDiscarded(Boolean discarded) {
	this.discarded = discarded;
    }

    public boolean isInHistory() {
	return inHistory;
    }

    public void setInHistory(boolean inHistory) {
	this.inHistory = inHistory;
    }

	public String getUploadedByLocation() {
		return uploadedByLocation;
	}

	public void setUploadedByLocation(String uploadedByLocation) {
		this.uploadedByLocation = uploadedByLocation;
	}

	@Override
	public String toString() {
		return "RecordsCollection [id=" + id + ", recordsUrl=" + recordsUrl + ", recordsPath=" + recordsPath
				+ ", recordsLable=" + recordsLable + ", recordsType=" + recordsType + ", description=" + description
				+ ", patientId=" + patientId + ", doctorId=" + doctorId + ", locationId=" + locationId + ", hospitalId="
				+ hospitalId + ", discarded=" + discarded + ", inHistory=" + inHistory + ", uploadedByLocation="
				+ uploadedByLocation + "]";
	}
}
