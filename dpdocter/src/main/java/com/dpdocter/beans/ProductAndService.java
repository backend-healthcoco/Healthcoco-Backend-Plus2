package com.dpdocter.beans;

import java.util.List;

public class ProductAndService {
    private String id;

    private String name;

    private double cost = 0.0;

    private String locationId;

    private String hospitalId;

    private String doctorId;

    private List<String> specialityIds;

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

    public double getCost() {
	return cost;
    }

    public void setCost(double cost) {
	this.cost = cost;
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

    public String getDoctorId() {
	return doctorId;
    }

    public void setDoctorId(String doctorId) {
	this.doctorId = doctorId;
    }

    public List<String> getSpecialityIds() {
	return specialityIds;
    }

    public void setSpecialityIds(List<String> specialityIds) {
	this.specialityIds = specialityIds;
    }

    @Override
    public String toString() {
	return "ProductAndService [id=" + id + ", name=" + name + ", cost=" + cost + ", locationId=" + locationId + ", hospitalId=" + hospitalId + ", doctorId="
		+ doctorId + ", specialityIds=" + specialityIds + "]";
    }

}
