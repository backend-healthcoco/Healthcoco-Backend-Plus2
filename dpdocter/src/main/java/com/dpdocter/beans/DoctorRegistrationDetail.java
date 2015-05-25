package com.dpdocter.beans;

public class DoctorRegistrationDetail {
	private String medicalCouncilId;
	private String registrationId;
	private int yearOfPassing;

	public String getMedicalCouncilId() {
		return medicalCouncilId;
	}

	public void setMedicalCouncilId(String medicalCouncilId) {
		this.medicalCouncilId = medicalCouncilId;
	}

	public String getRegistrationId() {
		return registrationId;
	}

	public void setRegistrationId(String registrationId) {
		this.registrationId = registrationId;
	}

	public int getYearOfPassing() {
		return yearOfPassing;
	}

	public void setYearOfPassing(int yearOfPassing) {
		this.yearOfPassing = yearOfPassing;
	}

	@Override
	public String toString() {
		return "DoctorRegistrationDetail [medicalCouncilId=" + medicalCouncilId + ", registrationId=" + registrationId + ", yearOfPassing=" + yearOfPassing
				+ "]";
	}

}
