package com.dpdocter.response;

import java.util.List;

import com.dpdocter.beans.Vaccine;

public class BabyVaccineReminderResponse {

	private String patientName;
	private String doctorName;
	private String locationName;
	private List<Vaccine> vaccines;
	private String mobileNumber;
	private String clinicNumber;
	private String googleMapShortUrl;

	public String getPatientName() {
		return patientName;
	}

	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}

	public String getDoctorName() {
		return doctorName;
	}

	public void setDoctorName(String doctorName) {
		this.doctorName = doctorName;
	}

	public String getLocationName() {
		return locationName;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}

	public List<Vaccine> getVaccines() {
		return vaccines;
	}

	public void setVaccines(List<Vaccine> vaccines) {
		this.vaccines = vaccines;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getClinicNumber() {
		return clinicNumber;
	}

	public void setClinicNumber(String clinicNumber) {
		this.clinicNumber = clinicNumber;
	}

	public String getGoogleMapShortUrl() {
		return googleMapShortUrl;
	}

	public void setGoogleMapShortUrl(String googleMapShortUrl) {
		this.googleMapShortUrl = googleMapShortUrl;
	}

	@Override
	public String toString() {
		return "BabyVaccineReminderResponse [patientName=" + patientName + ", doctorName=" + doctorName
				+ ", locationName=" + locationName + ", vaccines=" + vaccines + ", mobileNumber=" + mobileNumber
				+ ", clinicNumber=" + clinicNumber + ", googleMapShortUrl=" + googleMapShortUrl + "]";
	}
}
