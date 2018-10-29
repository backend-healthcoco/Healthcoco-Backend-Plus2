package com.dpdocter.beans;

import com.dpdocter.collections.GenericCollection;

public class GrowthChart extends GenericCollection {

	private String id;
	private String patientId;
	private String doctorId;
	private String locationId;
	private String hospitalId;
	private Integer height;
	private Double weight;
	private Double bmi;
	private Integer skullCircumference;
	private String progress;
	private Age age;
	private String temperature;
	private BloodPressure bloodPressure;
	private String bloodSugarF;
	private String bloodSugarPP;
	private String bmd;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
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

	public Integer getHeight() {
		return height;
	}

	public void setHeight(Integer height) {
		this.height = height;
	}

	public Double getWeight() {
		return weight;
	}

	public void setWeight(Double weight) {
		this.weight = weight;
	}

	public Double getBmi() {
		return bmi;
	}

	public void setBmi(Double bmi) {
		this.bmi = bmi;
	}

	public Integer getSkullCircumference() {
		return skullCircumference;
	}

	public void setSkullCircumference(Integer skullCircumference) {
		this.skullCircumference = skullCircumference;
	}

	public String getProgress() {
		return progress;
	}

	public void setProgress(String progress) {
		this.progress = progress;
	}

	public Age getAge() {
		return age;
	}

	public void setAge(Age age) {
		this.age = age;
	}

	public String getTemperature() {
		return temperature;
	}

	public void setTemperature(String temperature) {
		this.temperature = temperature;
	}

	public BloodPressure getBloodPressure() {
		return bloodPressure;
	}

	public void setBloodPressure(BloodPressure bloodPressure) {
		this.bloodPressure = bloodPressure;
	}

	public String getBloodSugarF() {
		return bloodSugarF;
	}

	public void setBloodSugarF(String bloodSugarF) {
		this.bloodSugarF = bloodSugarF;
	}

	public String getBloodSugarPP() {
		return bloodSugarPP;
	}

	public void setBloodSugarPP(String bloodSugarPP) {
		this.bloodSugarPP = bloodSugarPP;
	}

	public String getBmd() {
		return bmd;
	}

	public void setBmd(String bmd) {
		this.bmd = bmd;
	}

	@Override
	public String toString() {
		return "GrowthChart [height=" + height + ", weight=" + weight + ", bmi=" + bmi + ", skullCircumference="
				+ skullCircumference + ", progress=" + progress + "]";
	}

}
