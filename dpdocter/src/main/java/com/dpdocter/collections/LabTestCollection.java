package com.dpdocter.collections;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.DiagnosticTest;

@Document(collection = "lab_test_cl")
public class LabTestCollection extends GenericCollection {

    @Id
    private String id;

    @Field
    private DiagnosticTest test;

    @Field
    private String locationId;

    @Field
    private String hospitalId;

    @Field
    private int cost = 0;

    @Field
    private Boolean discarded = false;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public DiagnosticTest getTest() {
		return test;
	}

	public void setTest(DiagnosticTest test) {
		this.test = test;
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
		return "LabTestCollection [id=" + id + ", test=" + test + ", locationId=" + locationId + ", hospitalId="
				+ hospitalId + ", cost=" + cost + ", discarded=" + discarded + "]";
	}

}
