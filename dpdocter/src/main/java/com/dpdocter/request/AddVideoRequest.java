package com.dpdocter.request;

import java.util.List;

import common.util.web.JacksonUtil;

public class AddVideoRequest {

	private String name;

	private String speciality;

	private String description;

	private String doctorId;

	private String locationId;

	private String hospitalId;
	
	private String type;

	private List<String> tags;


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSpeciality() {
		return speciality;
	}

	public void setSpeciality(String speciality) {
		this.speciality = speciality;
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
		return "AddVideoRequest [name=" + name + ", speciality=" + speciality + ", description=" + description + "]";
	}

	public static void main(String[] args) {
		AddVideoRequest request = new AddVideoRequest();
		System.err.println(JacksonUtil.obj2Json(request));
	}

}
