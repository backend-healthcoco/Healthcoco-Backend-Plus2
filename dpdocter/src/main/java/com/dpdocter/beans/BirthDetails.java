package com.dpdocter.beans;

public class BirthDetails {

	private String id;
	
	private BirthHistory birthHistory;

	private BirthAchievement birthAchievement;

	private GrowthChart growthChart;

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

	public BirthHistory getBirthHistory() {
		return birthHistory;
	}

	public void setBirthHistory(BirthHistory birthHistory) {
		this.birthHistory = birthHistory;
	}

	public BirthAchievement getBirthAchievement() {
		return birthAchievement;
	}

	public void setBirthAchievement(BirthAchievement birthAchievement) {
		this.birthAchievement = birthAchievement;
	}

	public GrowthChart getGrowthChart() {
		return growthChart;
	}

	public void setGrowthChart(GrowthChart growthChart) {
		this.growthChart = growthChart;
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
		return "BirthDetails [id=" + id + ", birthHistory=" + birthHistory + ", birthAchievement=" + birthAchievement
				+ ", growthChart=" + growthChart + ", doctorId=" + doctorId + ", locationId=" + locationId
				+ ", hospitalId=" + hospitalId + ", patientId=" + patientId + "]";
	}

}
