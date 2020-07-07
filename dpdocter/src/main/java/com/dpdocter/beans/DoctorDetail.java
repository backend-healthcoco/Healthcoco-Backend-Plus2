package com.dpdocter.beans;

import java.util.List;

public class DoctorDetail {
	private String doctorId;
	private String locationId;
	private String city;
	private DoctorExperience experience;
	private Integer noOfRecommenations = 0;
	private String responseTime;
	private String doctorName;
	private List<String> specialities;

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

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public DoctorExperience getExperience() {
		return experience;
	}

	public void setExperience(DoctorExperience experience) {
		this.experience = experience;
	}

	public String getResponseTime() {
		return responseTime;
	}

	public void setResponseTime(String responseTime) {
		this.responseTime = responseTime;
	}

	public String getDoctorName() {
		return doctorName;
	}

	public void setDoctorName(String doctorName) {
		this.doctorName = doctorName;
	}
	
	public List<String> getSpecialities() {
		return specialities;
	}

	public void setSpecialities(List<String> specialities) {
		this.specialities = specialities;
	}

	public Integer getNoOfRecommenations() {
		return noOfRecommenations;
	}

	public void setNoOfRecommenations(Integer noOfRecommenations) {
		this.noOfRecommenations = noOfRecommenations;
	}

	@Override
	public String toString() {
		return "DoctorDetail [ doctorId=" + doctorId + ", locationId=" + locationId + ", city=" + city
				+ ", experience=" + experience + ", noOfRecommenations=" + noOfRecommenations + ", responseTime="
				+ responseTime + ", doctorName=" + doctorName + ", specialities=" + specialities + "]";
	}

}
