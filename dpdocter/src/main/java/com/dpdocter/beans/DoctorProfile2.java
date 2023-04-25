package com.dpdocter.beans;

import java.util.List;

import org.codehaus.jackson.map.annotate.JsonSerialize;
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class DoctorProfile2 {
    private String id;

    private String userId;

    private String doctorId;
    
    private String title;

    private String firstName;

    private String emailAddress;

    private String mobileNumber;

    private String gender;

    private String imageUrl;

    private String thumbnailUrl;

    private DOB dob;

    private String colorCode;

    private String coverImageUrl;

    private String coverThumbnailImageUrl;

    private List<String> additionalNumbers;

    private List<String> otherEmailAddresses;

    private DoctorExperience experience;

    private List<Education> education;

    private List<String> specialities;

    private List<Achievement> achievements;

    private String professionalStatement;

    private List<DoctorRegistrationDetail> registrationDetails;

    private List<DoctorExperienceDetail> experienceDetails;

    private List<String> professionalMemberships;

    private List<DoctorClinicProfile> clinicProfile;

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public String getUserId() {
	return userId;
    }

    public void setUserId(String userId) {
	this.userId = userId;
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

    public String getEmailAddress() {
	return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
	this.emailAddress = emailAddress;
    }

    public String getMobileNumber() {
	return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
	this.mobileNumber = mobileNumber;
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

    public DOB getDob() {
	return dob;
    }

    public void setDob(DOB dob) {
	this.dob = dob;
    }

    public List<String> getAdditionalNumbers() {
	return additionalNumbers;
    }

    public void setAdditionalNumbers(List<String> additionalNumbers) {
	this.additionalNumbers = additionalNumbers;
    }

    public List<String> getOtherEmailAddresses() {
	return otherEmailAddresses;
    }

    public void setOtherEmailAddresses(List<String> otherEmailAddresses) {
	this.otherEmailAddresses = otherEmailAddresses;
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

    public String getThumbnailUrl() {
	return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
	this.thumbnailUrl = thumbnailUrl;
    }

    public List<DoctorClinicProfile> getClinicProfile() {
	return clinicProfile;
    }

    public void setClinicProfile(List<DoctorClinicProfile> clinicProfile) {
	this.clinicProfile = clinicProfile;
    }

    public String getCoverImageUrl() {
	return coverImageUrl;
    }

    public void setCoverImageUrl(String coverImageUrl) {
	this.coverImageUrl = coverImageUrl;
    }

    public String getCoverThumbnailImageUrl() {
	return coverThumbnailImageUrl;
    }

    public void setCoverThumbnailImageUrl(String coverThumbnailImageUrl) {
	this.coverThumbnailImageUrl = coverThumbnailImageUrl;
    }

    public String getColorCode() {
	return colorCode;
    }

    public void setColorCode(String colorCode) {
	this.colorCode = colorCode;
    }

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	@Override
	public String toString() {
		return "DoctorProfile [id=" + id + ", userId=" + userId + ", doctorId=" + doctorId + ", title=" + title
				+ ", firstName=" + firstName + ", emailAddress=" + emailAddress + ", mobileNumber=" + mobileNumber
				+ ", gender=" + gender + ", imageUrl=" + imageUrl + ", thumbnailUrl=" + thumbnailUrl + ", dob=" + dob
				+ ", colorCode=" + colorCode + ", coverImageUrl=" + coverImageUrl + ", coverThumbnailImageUrl="
				+ coverThumbnailImageUrl + ", additionalNumbers=" + additionalNumbers + ", otherEmailAddresses="
				+ otherEmailAddresses + ", experience=" + experience + ", education=" + education + ", specialities="
				+ specialities + ", achievements=" + achievements + ", professionalStatement=" + professionalStatement
				+ ", registrationDetails=" + registrationDetails + ", experienceDetails=" + experienceDetails
				+ ", professionalMemberships=" + professionalMemberships + ", clinicProfile=" + clinicProfile + "]";
	}
}
