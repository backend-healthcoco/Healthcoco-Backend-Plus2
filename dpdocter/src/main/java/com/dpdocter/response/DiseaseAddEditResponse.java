package com.dpdocter.response;

import com.dpdocter.collections.GenericCollection;

public class DiseaseAddEditResponse extends GenericCollection {
    private String id;

    private String doctorId;

    private String locationId;

    private String hospitalId;

    private String disease;

    private String description;

    private Boolean discarded = false;

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
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

    public String getDisease() {
	return disease;
    }

    public void setDisease(String disease) {
	this.disease = disease;
    }

    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
    }

   public Boolean getDiscarded() {
	return discarded;
    }

    public void setDiscarded(Boolean discarded) {
	this.discarded = discarded;
    }

	@Override
	public String toString() {
		return "DiseaseAddEditResponse [id=" + id + ", doctorId=" + doctorId + ", locationId=" + locationId
				+ ", hospitalId=" + hospitalId + ", disease=" + disease + ", description=" + description
				+ ", discarded=" + discarded + "]";
	}
}
