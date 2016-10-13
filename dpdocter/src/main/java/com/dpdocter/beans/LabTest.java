package com.dpdocter.beans;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.dpdocter.collections.GenericCollection;
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class LabTest extends GenericCollection {

    private String id;

    private DiagnosticTest test;

    private String locationId;

    private String hospitalId;

    private int cost = 0;

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
	return "LabTest [id=" + id + ", test=" + test + ", locationId=" + locationId + ", hospitalId=" + hospitalId + ", cost=" + cost + ", discarded="
		+ discarded + "]";
    }
}
