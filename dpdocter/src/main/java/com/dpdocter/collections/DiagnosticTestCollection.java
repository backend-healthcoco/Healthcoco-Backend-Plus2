package com.dpdocter.collections;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "diagnostic_test_cl")
public class DiagnosticTestCollection extends GenericCollection {

    @Id
    private String id;

    @Field
    private String testName;

    @Field
    private String description;

    @Field
    private String locationId;

    @Field
    private String hospitalId;

    @Field
    private Boolean discarded = false;

    @Field
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
		return "DiagnosticTestCollection [id=" + id + ", testName=" + testName + ", description=" + description
				+ ", locationId=" + locationId + ", hospitalId=" + hospitalId + ", discarded=" + discarded + ", code="
				+ code + "]";
	}
}
