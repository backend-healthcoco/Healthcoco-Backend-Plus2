package com.dpdocter.beans;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.dpdocter.collections.GenericCollection;
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class DrugDurationUnit extends GenericCollection{
    private String id;

    private String unit;

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

    public String getUnit() {
	return unit;
    }

    public void setUnit(String unit) {
	this.unit = unit;
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
		return "DrugDurationUnit [id=" + id + ", unit=" + unit + ", doctorId=" + doctorId + ", locationId=" + locationId
				+ ", hospitalId=" + hospitalId + ", discarded=" + discarded + "]";
	}

}
