package com.dpdocter.collections;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.Achievement;
import com.dpdocter.beans.DOB;
import com.dpdocter.beans.DoctorExperience;
import com.dpdocter.beans.DoctorExperienceDetail;
import com.dpdocter.beans.DoctorRegistrationDetail;
import com.dpdocter.beans.Education;

@Document(collection = "docter_cl")
public class DoctorCollection extends GenericCollection {
    @Id
    private String id;

    @Field
    private List<String> additionalNumbers;

    @Field
    private List<String> otherEmailAddresses;

    @Field
    private String userId;

    @Field
    private DoctorExperience experience;

    @Field
    private List<Education> education;

    @Field
    private List<String> specialities;

    @Field
    private List<Achievement> achievements;

    @Field
    private String professionalStatement;

    @Field
    private List<DoctorRegistrationDetail> registrationDetails;

    @Field
    private List<DoctorExperienceDetail> experienceDetails;

    @Field
    private List<String> professionalMemberships;

    @Field
    private String registerNumber;

    @Field
    private String gender;

    @Field
    private DOB dob;

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
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

    public String getUserId() {
	return userId;
    }

    public void setUserId(String userId) {
	this.userId = userId;
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

    public String getRegisterNumber() {
	return registerNumber;
    }

    public void setRegisterNumber(String registerNumber) {
	this.registerNumber = registerNumber;
    }

    public String getGender() {
	return gender;
    }

    public void setGender(String gender) {
	this.gender = gender;
    }

    public DOB getDob() {
	return dob;
    }

    public void setDob(DOB dob) {
	this.dob = dob;
    }

    @Override
    public String toString() {
	return "DoctorCollection [id=" + id + ", additionalNumbers=" + additionalNumbers + ", otherEmailAddresses=" + otherEmailAddresses + ", userId="
		+ userId + ", experience=" + experience + ", education=" + education + ", specialities=" + specialities + ", achievements=" + achievements
		+ ", professionalStatement=" + professionalStatement + ", registrationDetails=" + registrationDetails + ", experienceDetails="
		+ experienceDetails + ", professionalMemberships=" + professionalMemberships + ", registerNumber=" + registerNumber + ", gender=" + gender
		+ ", dob=" + dob + "]";
    }
}
