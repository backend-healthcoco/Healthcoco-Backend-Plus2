package com.dpdocter.beans;

public class DrugDosage {

    String id;

    String value;

    String doctorId;

    String locationId;

    String hospitalId;

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public String getValue() {
	return value;
    }

    public void setValue(String value) {
	this.value = value;
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
	return "DrugDosage [id=" + id + ", value=" + value + ", doctorId=" + doctorId + ", locationId=" + locationId + ", hospitalId=" + hospitalId + "]";
    }

}
