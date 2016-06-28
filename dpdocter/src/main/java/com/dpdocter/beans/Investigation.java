package com.dpdocter.beans;

import com.dpdocter.collections.GenericCollection;

public class Investigation extends GenericCollection {

    private String id;

    private String investigation;

    private String doctorId;

    private String locationId;

    private String hospitalId;

    private Boolean discarded = false;
    
    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public String getInvestigation() {
	return investigation;
    }

    public void setInvestigation(String investigation) {
	this.investigation = investigation;
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
		return "Investigation [id=" + id + ", investigation=" + investigation + ", doctorId=" + doctorId
				+ ", locationId=" + locationId + ", hospitalId=" + hospitalId + ", discarded=" + discarded + "]";
	}

}
