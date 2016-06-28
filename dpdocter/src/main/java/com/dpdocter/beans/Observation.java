package com.dpdocter.beans;

import com.dpdocter.collections.GenericCollection;

public class Observation extends GenericCollection {

    private String id;

    private String observation;

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

    public String getObservation() {
	return observation;
    }

    public void setObservation(String observation) {
	this.observation = observation;
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
		return "Observation [id=" + id + ", observation=" + observation + ", doctorId=" + doctorId + ", locationId="
				+ locationId + ", hospitalId=" + hospitalId + ", discarded=" + discarded + "]";
	}

}
