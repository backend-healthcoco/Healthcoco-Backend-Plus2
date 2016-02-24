package com.dpdocter.beans;

import com.dpdocter.collections.GenericCollection;

public class DiagnosticTest extends GenericCollection {

    private String id;

    private String testName;

    private String description;

    private String locationId;

    private String hospitalId;

    private Boolean discarded = false;

    private String code;

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

    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
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

    public String getCode() {
	return code;
    }

    public void setCode(String code) {
	this.code = code;
    }

    @Override
    public String toString() {
	return "DiagnosticTest [id=" + id + ", testName=" + testName + ", description=" + description + ", locationId=" + locationId + ", hospitalId="
		+ hospitalId + ", discarded=" + discarded + ", code=" + code + "]";
    }
}
