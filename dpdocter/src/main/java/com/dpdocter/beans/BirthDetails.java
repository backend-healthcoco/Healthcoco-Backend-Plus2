package com.dpdocter.beans;

import java.util.List;

import com.dpdocter.collections.GenericCollection;

public class BirthDetails extends GenericCollection{
	
	private String id;
	private List<BirthAchievement> birthAchievements;
	private List<GrowthChart> growthCharts;
	private String doctorId;
	private String locationId;
	private String hospitalId;
	private String patientId;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<BirthAchievement> getBirthAchievements() {
		return birthAchievements;
	}

	public void setBirthAchievements(List<BirthAchievement> birthAchievements) {
		this.birthAchievements = birthAchievements;
	}

	public List<GrowthChart> getGrowthCharts() {
		return growthCharts;
	}

	public void setGrowthCharts(List<GrowthChart> growthCharts) {
		this.growthCharts = growthCharts;
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

	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}

	@Override
	public String toString() {
		return "BirthDetails [id=" + id + ", birthAchievements=" + birthAchievements + ", growthCharts=" + growthCharts
				+ ", doctorId=" + doctorId + ", locationId=" + locationId + ", hospitalId=" + hospitalId
				+ ", patientId=" + patientId + "]";
	}

}
