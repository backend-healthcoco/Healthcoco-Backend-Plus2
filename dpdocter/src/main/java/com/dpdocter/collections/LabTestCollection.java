package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "lab_test_cl")
public class LabTestCollection extends GenericCollection {

    @Id
    private ObjectId id;

    @Field
    private ObjectId testId;

    @Field
    private ObjectId locationId;

    @Field
    private ObjectId hospitalId;

    @Field
    private int cost = 0;

    @Field
    private Boolean discarded = false;

    public ObjectId getId() {
	return id;
    }

    public void setId(ObjectId id) {
	this.id = id;
    }

    public ObjectId getTestId() {
	return testId;
    }

    public void setTestId(ObjectId testId) {
	this.testId = testId;
    }

    public ObjectId getLocationId() {
	return locationId;
    }

    public void setLocationId(ObjectId locationId) {
	this.locationId = locationId;
    }

    public ObjectId getHospitalId() {
	return hospitalId;
    }

    public void setHospitalId(ObjectId hospitalId) {
	this.hospitalId = hospitalId;
    }

    public int getCost() {
	return cost;
    }

    public void setCost(int cost) {
	this.cost = cost;
    }

    public Boolean getDiscarded() {
	return discarded;
    }

    public void setDiscarded(Boolean discarded) {
	this.discarded = discarded;
    }

    @Override
    public String toString() {
	return "LabTestCollection [id=" + id + ", testId=" + testId + ", locationId=" + locationId + ", hospitalId=" + hospitalId + ", cost=" + cost
		+ ", discarded=" + discarded + "]";
    }

}
