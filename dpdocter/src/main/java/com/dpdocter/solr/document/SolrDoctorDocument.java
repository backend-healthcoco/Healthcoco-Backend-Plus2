package com.dpdocter.solr.document;

import java.util.List;

import org.apache.solr.client.solrj.beans.Field;
import org.springframework.data.annotation.Id;
import org.springframework.data.solr.core.mapping.SolrDocument;

import com.dpdocter.beans.ConsultationFee;
import com.dpdocter.beans.DoctorExperience;
import com.dpdocter.solr.beans.DoctorLocation;
import com.dpdocter.solr.beans.SolrWorkingSchedule;

@SolrDocument(solrCoreName = "doctors")
public class SolrDoctorDocument extends DoctorLocation {
    @Id
    @Field
    private String id;

    @Field
    private String userId;

    @Field
    private String firstName;

    @Field
    private String gender;

    @Field
    private String emailAddress;

    @Field
    private String phoneNumber;

    @Field
    private String imageUrl;

    @Field
    private ConsultationFee consultationFee;

    @Field
    private List<SolrWorkingSchedule> workingSchedules;

    @Field
    private List<String> specialities;

    @Field
    private DoctorExperience experience;

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
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

    public String getEmailAddress() {
	return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
	this.emailAddress = emailAddress;
    }

    public String getPhoneNumber() {
	return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
	this.phoneNumber = phoneNumber;
    }

    public String getImageUrl() {
	return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
	this.imageUrl = imageUrl;
    }

    public ConsultationFee getConsultationFee() {
	return consultationFee;
    }

    public void setConsultationFee(ConsultationFee consultationFee) {
	this.consultationFee = consultationFee;
    }

    public List<SolrWorkingSchedule> getWorkingSchedules() {
	return workingSchedules;
    }

    public void setWorkingSchedules(List<SolrWorkingSchedule> workingSchedules) {
	this.workingSchedules = workingSchedules;
    }

    public String getUserId() {
	return userId;
    }

    public void setUserId(String userId) {
	this.userId = userId;
    }

    public List<String> getSpecialities() {
	return specialities;
    }

    public void setSpecialities(List<String> specialities) {
	this.specialities = specialities;
    }

    public DoctorExperience getExperience() {
	return experience;
    }

    public void setExperience(DoctorExperience experience) {
	this.experience = experience;
    }

    @Override
    public String toString() {
	return "SolrDoctorDocument [id=" + id + ", userId=" + userId + ", firstName=" + firstName + ", gender=" + gender + ", emailAddress=" + emailAddress
		+ ", phoneNumber=" + phoneNumber + ", imageUrl=" + imageUrl + ", consultationFee=" + consultationFee + ", workingSchedules=" + workingSchedules
		+ ", specialities=" + specialities + ", experience=" + experience + "]";
    }
}
