package com.dpdocter.beans;

import java.util.List;

public class Clinic {

    private String id;

    private Hospital hospital;

    private Location location;

    private List<Doctor> doctors;

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public Hospital getHospital() {
	return hospital;
    }

    public void setHospital(Hospital hospital) {
	this.hospital = hospital;
    }

    public Location getLocation() {
	return location;
    }

    public void setLocation(Location location) {
	this.location = location;
    }

    public List<Doctor> getDoctors() {
	return doctors;
    }

    public void setDoctors(List<Doctor> doctors) {
	this.doctors = doctors;
    }

    @Override
    public String toString() {
	return "Clinic [id=" + id + ", hospital=" + hospital + ", location=" + location + ", doctors=" + doctors + "]";
    }

}
