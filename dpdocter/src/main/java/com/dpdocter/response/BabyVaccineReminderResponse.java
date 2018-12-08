package com.dpdocter.response;

import java.util.List;

import com.dpdocter.beans.Vaccine;

public class BabyVaccineReminderResponse {

	private String patientName;
	private String doctorName;
	private String locationName;
	private List<Vaccine> vaccines;

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

	@Override
	public String toString() {
		return "BabyVaccineReminderResponse [patientName=" + patientName + ", doctorName=" + doctorName
				+ ", locationName=" + locationName + ", vaccines=" + vaccines + "]";
	}

}
