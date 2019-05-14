package com.dpdocter.response;

import java.util.List;

import com.dpdocter.beans.Achievement;
import com.dpdocter.beans.DOB;
import com.dpdocter.beans.DoctorExperience;
import com.dpdocter.beans.DoctorExperienceDetail;
import com.dpdocter.beans.DoctorRegistrationDetail;
import com.dpdocter.beans.Education;

public class DoctorProfileBySlugUrlResponse {

	private String doctorId;

	private String title;

	private String firstName;

	private String gender;

	private String imageUrl;

	private String thumbnailUrl;

	private DOB dob;

	private String colorCode;

	private DoctorExperience experience;

	private List<Education> education;

	private List<String> specialities;

	private List<String> services;

	private List<String> parentSpecialities;

	private List<Achievement> achievements;

	private String professionalStatement;

	private List<DoctorRegistrationDetail> registrationDetails;

	private List<DoctorExperienceDetail> experienceDetails;

	private List<String> professionalMemberships;

	private List<DoctorClinicProfileBySlugUrlResponse> clinicProfile;

	private String metaTitle;

	private String metaDesccription;

	private String metaKeyword;

	private String doctorSlugURL;

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getThumbnailUrl() {
		return thumbnailUrl;
	}

	public void setThumbnailUrl(String thumbnailUrl) {
		this.thumbnailUrl = thumbnailUrl;
	}

	public DOB getDob() {
		return dob;
	}

	public void setDob(DOB dob) {
		this.dob = dob;
	}

	public String getColorCode() {
		return colorCode;
	}

	public void setColorCode(String colorCode) {
		this.colorCode = colorCode;
	}

	public DoctorExperience getExperience() {
		return experience;
	}

	public void setExperience(DoctorExperience experience) {
		this.experience = experience;
	}

	public List<Education> getEducation() {
		return education;
	}

	public void setEducation(List<Education> education) {
		this.education = education;
	}

	public List<String> getSpecialities() {
		return specialities;
	}

	public void setSpecialities(List<String> specialities) {
		this.specialities = specialities;
	}

	public List<String> getServices() {
		return services;
	}

	public void setServices(List<String> services) {
		this.services = services;
	}

	public List<String> getParentSpecialities() {
		return parentSpecialities;
	}

	public void setParentSpecialities(List<String> parentSpecialities) {
		this.parentSpecialities = parentSpecialities;
	}

	public List<Achievement> getAchievements() {
		return achievements;
	}

	public void setAchievements(List<Achievement> achievements) {
		this.achievements = achievements;
	}

	public String getProfessionalStatement() {
		return professionalStatement;
	}

	public void setProfessionalStatement(String professionalStatement) {
		this.professionalStatement = professionalStatement;
	}

	public List<DoctorRegistrationDetail> getRegistrationDetails() {
		return registrationDetails;
	}

	public void setRegistrationDetails(List<DoctorRegistrationDetail> registrationDetails) {
		this.registrationDetails = registrationDetails;
	}

	public List<DoctorExperienceDetail> getExperienceDetails() {
		return experienceDetails;
	}

	public void setExperienceDetails(List<DoctorExperienceDetail> experienceDetails) {
		this.experienceDetails = experienceDetails;
	}

	public List<String> getProfessionalMemberships() {
		return professionalMemberships;
	}

	public void setProfessionalMemberships(List<String> professionalMemberships) {
		this.professionalMemberships = professionalMemberships;
	}

	public List<DoctorClinicProfileBySlugUrlResponse> getClinicProfile() {
		return clinicProfile;
	}

	public void setClinicProfile(List<DoctorClinicProfileBySlugUrlResponse> clinicProfile) {
		this.clinicProfile = clinicProfile;
	}

	public String getMetaTitle() {
		return metaTitle;
	}

	public void setMetaTitle(String metaTitle) {
		this.metaTitle = metaTitle;
	}

	public String getMetaDesccription() {
		return metaDesccription;
	}

	public void setMetaDesccription(String metaDesccription) {
		this.metaDesccription = metaDesccription;
	}

	public String getMetaKeyword() {
		return metaKeyword;
	}

	public void setMetaKeyword(String metaKeyword) {
		this.metaKeyword = metaKeyword;
	}

	public String getDoctorSlugURL() {
		return doctorSlugURL;
	}

	public void setDoctorSlugURL(String doctorSlugURL) {
		this.doctorSlugURL = doctorSlugURL;
	}

	@Override
	public String toString() {
		return "DoctorProfileBySlugUrlResponse [doctorId=" + doctorId + ", title=" + title + ", firstName=" + firstName
				+ ", gender=" + gender + ", imageUrl=" + imageUrl + ", thumbnailUrl=" + thumbnailUrl + ", dob=" + dob
				+ ", colorCode=" + colorCode + ", experience=" + experience + ", education=" + education
				+ ", specialities=" + specialities + ", services=" + services + ", parentSpecialities="
				+ parentSpecialities + ", achievements=" + achievements + ", professionalStatement="
				+ professionalStatement + ", registrationDetails=" + registrationDetails + ", experienceDetails="
				+ experienceDetails + ", professionalMemberships=" + professionalMemberships + ", clinicProfile="
				+ clinicProfile + ", metaTitle=" + metaTitle + ", metaDesccription=" + metaDesccription
				+ ", metaKeyword=" + metaKeyword + ", doctorSlugURL=" + doctorSlugURL + "]";
	}
}
