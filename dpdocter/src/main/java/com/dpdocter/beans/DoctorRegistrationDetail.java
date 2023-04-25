package com.dpdocter.beans;

import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class DoctorRegistrationDetail {
	private String medicalCouncil;

	private String registrationId;

	private int yearOfPassing;

	public String getMedicalCouncil() {
		return medicalCouncil;
	}

	public void setMedicalCouncil(String medicalCouncil) {
		this.medicalCouncil = medicalCouncil;
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
		return "DoctorRegistrationDetail [medicalCouncil=" + medicalCouncil + ", registrationId=" + registrationId
				+ ", yearOfPassing=" + yearOfPassing + "]";
	}

}
