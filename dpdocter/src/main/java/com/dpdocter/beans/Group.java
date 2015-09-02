package com.dpdocter.beans;

import com.dpdocter.collections.GenericCollection;

public class Group extends GenericCollection {
    private String id;

    private String name;

    private String description;

    private String doctorId;

    private String locationId;

    private String hospitalId;

    private int count = 0;

    private Boolean discarded = false;

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
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

    public int getCount() {
	return count;
    }

    public void setCount(int count) {
	this.count = count;
    }
    
	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	@Override
    public String toString() {
	return "Group [id=" + id + ", name=" + name + ", description=" + description + ", doctorId=" + doctorId + ", locationId=" + locationId
		+ ", hospitalId=" + hospitalId + ", count=" + count + ", discarded=" + discarded + "]";
    }

}
