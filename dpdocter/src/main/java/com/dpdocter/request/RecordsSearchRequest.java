package com.dpdocter.request;

public class RecordsSearchRequest {
    private String patientId;

    private String doctorId;

    private String tagId;

    private String locationId;

    private String hospitalId;

    private String updatedTime = "0";

    private Boolean discarded = true;

    private long page;

    private int size;

    private Boolean isOTPVerified = false;

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

    public String getTagId() {
	return tagId;
    }

    public void setTagId(String tagId) {
	this.tagId = tagId;
    }

    public String getUpdatedTime() {
	return updatedTime;
    }

    public void setUpdatedTime(String updatedTime) {
	this.updatedTime = updatedTime;
    }

    public Boolean getDiscarded() {
	return discarded;
    }

    public void setDiscarded(Boolean discarded) {
	this.discarded = discarded;
    }

    public long getPage() {
	return page;
    }

    public void setPage(long page) {
	this.page = page;
    }

    public int getSize() {
	return size;
    }

    public void setSize(int size) {
	this.size = size;
    }

    public Boolean getIsOTPVerified() {
	return isOTPVerified;
    }

    public void setIsOTPVerified(Boolean isOTPVerified) {
	this.isOTPVerified = isOTPVerified;
    }

    @Override
    public String toString() {
	return "RecordsSearchRequest [patientId=" + patientId + ", doctorId=" + doctorId + ", tagId=" + tagId + ", locationId=" + locationId + ", hospitalId="
		+ hospitalId + ", updatedTime=" + updatedTime + ", discarded=" + discarded + ", page=" + page + ", size=" + size + ", isOTPVerified="
		+ isOTPVerified + "]";
    }
}
