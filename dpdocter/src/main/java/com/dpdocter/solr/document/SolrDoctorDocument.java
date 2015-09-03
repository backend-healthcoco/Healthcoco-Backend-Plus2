package com.dpdocter.solr.document;

import java.util.List;

import org.apache.solr.client.solrj.beans.Field;
import org.springframework.data.annotation.Id;
import org.springframework.data.solr.core.mapping.SolrDocument;

import com.dpdocter.beans.ConsultationFee;
import com.dpdocter.beans.WorkingSchedule;
import com.dpdocter.solr.beans.DoctorLocation;

@SolrDocument(solrCoreName = "doctors")
public class SolrDoctorDocument extends DoctorLocation {
    @Id
    @Field
    private String id;

    @Field
    private String firstName;

    @Field
    private String middleName;

    @Field
    private String lastName;

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
    private List<WorkingSchedule> workingSchedules;

    @Field
    private List<String> specialization;

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

    public String getMiddleName() {
	return middleName;
    }

    public void setMiddleName(String middleName) {
	this.middleName = middleName;
    }

    public String getLastName() {
	return lastName;
    }

    public void setLastName(String lastName) {
	this.lastName = lastName;
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

    public List<WorkingSchedule> getWorkingSchedules() {
	return workingSchedules;
    }

    public void setWorkingSchedules(List<WorkingSchedule> workingSchedules) {
	this.workingSchedules = workingSchedules;
    }

    public List<String> getSpecialization() {
	return specialization;
    }

    public void setSpecialization(List<String> specialization) {
	this.specialization = specialization;
    }

    @Override
    public String toString() {
	return "SolrDoctorDocument [id=" + id + ", firstName=" + firstName + ", middleName=" + middleName + ", lastName=" + lastName + ", gender=" + gender
		+ ", emailAddress=" + emailAddress + ", phoneNumber=" + phoneNumber + ", imageUrl=" + imageUrl + ", consultationFee=" + consultationFee
		+ ", workingSchedules=" + workingSchedules + ", specialization=" + specialization + "]";
    }

}
