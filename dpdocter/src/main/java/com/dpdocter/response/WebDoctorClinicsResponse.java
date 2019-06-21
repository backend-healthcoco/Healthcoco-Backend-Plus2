package com.dpdocter.response;

import java.util.List;

import com.dpdocter.beans.DoctorExperience;

public class WebDoctorClinicsResponse {

	private String doctorId;
	
	private String doctorSlugURL;
	
	private String firstName;
	
	private List<String> specialities;
	
	private List<String> parentSpecialities;
	
	private List<WebClinicResponse> clinics;

	private DoctorExperience experience;

	private String thumbnailUrl;
	
	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	public String getDoctorSlugURL() {
		return doctorSlugURL;
	}

	public void setDoctorSlugURL(String doctorSlugURL) {
		this.doctorSlugURL = doctorSlugURL;
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

	public DoctorExperience getExperience() {
		return experience;
	}

	public void setExperience(DoctorExperience experience) {
		this.experience = experience;
	}

	public String getThumbnailUrl() {
		return thumbnailUrl;
	}

	public void setThumbnailUrl(String thumbnailUrl) {
		this.thumbnailUrl = thumbnailUrl;
	}

	@Override
	public String toString() {
		return "WebDoctorClinicsResponse [doctorId=" + doctorId + ", doctorSlugURL=" + doctorSlugURL + ", firstName="
				+ firstName + ", specialities=" + specialities + ", parentSpecialities=" + parentSpecialities
				+ ", clinics=" + clinics + ", experience=" + experience + ", thumbnailUrl=" + thumbnailUrl + "]";
	}
}
