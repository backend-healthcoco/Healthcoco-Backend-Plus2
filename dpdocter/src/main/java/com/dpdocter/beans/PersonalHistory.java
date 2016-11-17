package com.dpdocter.beans;

public class PersonalHistory {

	private String id;
	private String diet;
	private String addictions;
	private String bowelHabit;
	private String bladderHabit;
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

	public String getDiet() {
		return diet;
	}

	public void setDiet(String diet) {
		this.diet = diet;
	}

	public String getAddictions() {
		return addictions;
	}

	public void setAddictions(String addictions) {
		this.addictions = addictions;
	}

	public String getBowelHabit() {
		return bowelHabit;
	}

	public void setBowelHabit(String bowelHabit) {
		this.bowelHabit = bowelHabit;
	}

	public String getBladderHabit() {
		return bladderHabit;
	}

	public void setBladderHabit(String bladderHabit) {
		this.bladderHabit = bladderHabit;
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

	@Override
	public String toString() {
		return "PersonalHistory [id=" + id + ", diet=" + diet + ", addictions=" + addictions + ", bowelHabit="
				+ bowelHabit + ", bladderHabit=" + bladderHabit + ", doctorId=" + doctorId + ", locationId="
				+ locationId + ", hospitalId=" + hospitalId + ", patientId=" + patientId + "]";
	}

}
