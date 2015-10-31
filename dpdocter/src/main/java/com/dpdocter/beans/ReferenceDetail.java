package com.dpdocter.beans;

public class ReferenceDetail {
    private String id;

    private String reference;

    private String description;
    
    private String doctorId;

    private String locationId;

    private String hospitalId;


    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public String getReference() {
	return reference;
    }

    public void setReference(String reference) {
	this.reference = reference;
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

	@Override
	public String toString() {
		return "ReferenceDetail [id=" + id + ", reference=" + reference + ", description=" + description + ", doctorId="
				+ doctorId + ", locationId=" + locationId + ", hospitalId=" + hospitalId + "]";
	}
}
