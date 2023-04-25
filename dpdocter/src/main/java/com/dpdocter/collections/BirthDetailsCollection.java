package com.dpdocter.collections;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.BirthAchievement;
import com.dpdocter.beans.GrowthChart;

public class BirthDetailsCollection extends GenericCollection {
	@Id
	private ObjectId id;
	@Field
	private List<BirthAchievement> birthAchievements;
	@Field
	private GrowthChart growthChart;
	@Field
	private ObjectId doctorId;
	@Field
	private ObjectId locationId;
	@Field
	private ObjectId hospitalId;
	@Field
	private ObjectId patientId;
	@Field
	private Boolean isPatientDiscarded = false;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public List<BirthAchievement> getBirthAchievements() {
		return birthAchievements;
	}

	public void setBirthAchievements(List<BirthAchievement> birthAchievements) {
		this.birthAchievements = birthAchievements;
	}

	public GrowthChart getGrowthChart() {
		return growthChart;
	}

	public void setGrowthChart(GrowthChart growthChart) {
		this.growthChart = growthChart;
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

	public ObjectId getPatientId() {
		return patientId;
	}

	public void setPatientId(ObjectId patientId) {
		this.patientId = patientId;
	}

	public Boolean getIsPatientDiscarded() {
		return isPatientDiscarded;
	}

	public void setIsPatientDiscarded(Boolean isPatientDiscarded) {
		this.isPatientDiscarded = isPatientDiscarded;
	}

	@Override
	public String toString() {
		return "BirthDetailsCollection [id=" + id + ", birthAchievements=" + birthAchievements + ", growthChart="
				+ growthChart + ", doctorId=" + doctorId + ", locationId=" + locationId + ", hospitalId=" + hospitalId
				+ ", patientId=" + patientId + ", isPatientDiscarded=" + isPatientDiscarded + "]";
	}

}
