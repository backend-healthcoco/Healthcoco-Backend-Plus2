package com.dpdocter.beans;

import com.dpdocter.collections.GenericCollection;

public class LabTest extends GenericCollection {

    private String id;

    private String testName;

    private String doctorId;

    private String locationId;

    private String hospitalId;

    private Boolean discarded;

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public String getTestName() {
	return testName;
    }

    public void setTestName(String testName) {
	this.testName = testName;
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

    @Override
    public String toString() {
	return "LabTest [id=" + id + ", testName=" + testName + ", doctorId=" + doctorId + ", locationId=" + locationId + ", hospitalId=" + hospitalId
		+ ", discarded=" + discarded + "]";
    }
}
