package com.dpdocter.collections;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "lab_test_cl")
public class LabTestCollection extends GenericCollection {

    @Id
    private String id;

    @Field
    private String testName;

    @Field
    private String doctorId;

    @Field
    private String locationId;

    @Field
    private String hospitalId;

    @Field
    private Boolean discarded = false;

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
	return "LabTestCollection [id=" + id + ", testName=" + testName + ", doctorId=" + doctorId + ", locationId=" + locationId + ", hospitalId="
		+ hospitalId + ", discarded=" + discarded + "]";
    }
}
