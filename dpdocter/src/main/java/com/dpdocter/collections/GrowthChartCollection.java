package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.Age;
import com.dpdocter.beans.BloodPressure;

@Document(collection = "growth_chart_cl")
public class GrowthChartCollection extends GenericCollection {

	@Id
	private ObjectId id;
	@Field
	private ObjectId patientId;
	@Field
	private ObjectId doctorId;
	@Field
	private ObjectId locationId;
	@Field
	private ObjectId hospitalId;
	@Field
	private Integer height;
	@Field
	private Double weight;
	@Field
	private Double bmi;
	@Field
	private Integer skullCircumference;
	@Field
	private String progress;
	@Field
	private Age age;
	@Field
	private String temperature;
	@Field
	private BloodPressure bloodPressure;
	@Field
	private String bloodSugarF;
	@Field
	private String bloodSugarPP;
	@Field
	private String bmd;
	@Field
	private Boolean discarded = false;
	@Field
	private Boolean isPatientDiscarded = false;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public ObjectId getPatientId() {
		return patientId;
	}

	public void setPatientId(ObjectId patientId) {
		this.patientId = patientId;
	}

	public ObjectId getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(ObjectId doctorId) {
		this.doctorId = doctorId;
	}

	public ObjectId getLocationId() {
		return locationId;
	}

	public void setLocationId(ObjectId locationId) {
		this.locationId = locationId;
	}

	public ObjectId getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(ObjectId hospitalId) {
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

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	public Boolean getIsPatientDiscarded() {
		return isPatientDiscarded;
	}

	public void setIsPatientDiscarded(Boolean isPatientDiscarded) {
		this.isPatientDiscarded = isPatientDiscarded;
	}

	@Override
	public String toString() {
		return "GrowthChartCollection [id=" + id + ", patientId=" + patientId + ", doctorId=" + doctorId
				+ ", locationId=" + locationId + ", hospitalId=" + hospitalId + ", height=" + height + ", weight="
				+ weight + ", bmi=" + bmi + ", skullCircumference=" + skullCircumference + ", progress=" + progress
				+ ", age=" + age + ", temperature=" + temperature + ", bloodPressure=" + bloodPressure
				+ ", bloodSugarF=" + bloodSugarF + ", bloodSugarPP=" + bloodSugarPP + ", bmd=" + bmd + ", discarded="
				+ discarded + ", isPatientDiscarded=" + isPatientDiscarded + "]";
	}

}
