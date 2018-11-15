package com.dpdocter.response;

import java.util.List;

public class WebDoctorClinicsResponse {

	private String doctorId;
	
	private String firstName;
	
	private List<String> specialities;

	private List<String> parentSpecialities;
	
	private List<WebClinicResponse> clinics;
	
	private String doctorSlugURL;

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public List<String> getSpecialities() {
		return specialities;
	}

	public void setSpecialities(List<String> specialities) {
		this.specialities = specialities;
	}

	public List<String> getParentSpecialities() {
		return parentSpecialities;
	}

	public void setParentSpecialities(List<String> parentSpecialities) {
		this.parentSpecialities = parentSpecialities;
	}

	public List<WebClinicResponse> getClinics() {
		return clinics;
	}

	public void setClinics(List<WebClinicResponse> clinics) {
		this.clinics = clinics;
	}

	public String getDoctorSlugURL() {
		return doctorSlugURL;
	}

	public void setDoctorSlugURL(String doctorSlugURL) {
		this.doctorSlugURL = doctorSlugURL;
	}

	@Override
	public String toString() {
		return "WebDoctorClinicsResponse [doctorId=" + doctorId + ", firstName=" + firstName + ", specialities="
				+ specialities + ", parentSpecialities=" + parentSpecialities + ", clinics=" + clinics
				+ ", doctorSlugURL=" + doctorSlugURL + "]";
	}
}
