package com.dpdocter.solr.document;

import java.util.Date;

import org.apache.solr.client.solrj.beans.Field;
import org.springframework.data.annotation.Id;
import org.springframework.data.solr.core.mapping.SolrDocument;

import com.dpdocter.beans.DOB;
import com.dpdocter.collections.GenericCollection;

@SolrDocument(solrCoreName = "patients")
public class SolrPatientDocument {

    @Id
    @Field
    private String id;

    @Field
    private String userId;

    @Field
    private String PID;

    @Field
    private String userName;

    @Field
    private String firstName;

    @Field
    private String middleName;

    @Field
    private String lastName;

    @Field
    private String gender;

    @Field
    private String bloodGroup;

    @Field
    private String emailAddress;

    @Field
    private DOB dob;

    @Field
    private String city;

    @Field
    private String locality;

    @Field
    private String postalCode;

    @Field
    private String mobileNumber;

    @Field
    private String profession;

    @Field
    private String doctorId;

    @Field
    private String locationId;

    @Field
    private String hospitalId;

    @Field
    private String referredBy;

    @Field
    private Date createdTime;

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

    public String getPID() {
	return PID;
    }

    public void setPID(String pID) {
	PID = pID;
    }

    public String getUserName() {
	return userName;
    }

    public void setUserName(String userName) {
	this.userName = userName;
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

    public String getBloodGroup() {
	return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
	this.bloodGroup = bloodGroup;
    }

    public String getEmailAddress() {
	return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
	this.emailAddress = emailAddress;
    }

    public DOB getDob() {
	return dob;
    }

    public void setDob(DOB dob) {
	this.dob = dob;
    }

    public String getCity() {
	return city;
    }

    public void setCity(String city) {
	this.city = city;
    }

    public String getLocality() {
	return locality;
    }

    public void setLocality(String locality) {
	this.locality = locality;
    }

    public String getPostalCode() {
	return postalCode;
    }

    public void setPostalCode(String postalCode) {
	this.postalCode = postalCode;
    }

    public String getMobileNumber() {
	return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
	this.mobileNumber = mobileNumber;
    }

    public String getProfession() {
	return profession;
    }

    public void setProfession(String profession) {
	this.profession = profession;
    }

    public String getDoctorId() {
	return doctorId;
    }

    public void setDoctorId(String doctorId) {
	this.doctorId = doctorId;
    }

    public String getLocationId() {
	return locationId;
    }

    public void setLocationId(String locationId) {
	this.locationId = locationId;
    }

    public String getHospitalId() {
	return hospitalId;
    }

    public void setHospitalId(String hospitalId) {
	this.hospitalId = hospitalId;
    }

    public String getReferredBy() {
	return referredBy;
    }

    public void setReferredBy(String referredBy) {
	this.referredBy = referredBy;
    }

    public Date getCreatedTime() {
	return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
	this.createdTime = createdTime;
    }

    @Override
    public String toString() {
	return "SolrPatientDocument [id=" + id + ", userId=" + userId + ", PID=" + PID + ", userName=" + userName + ", firstName=" + firstName
		+ ", middleName=" + middleName + ", lastName=" + lastName + ", gender=" + gender + ", bloodGroup=" + bloodGroup + ", emailAddress="
		+ emailAddress + ", dob=" + dob + ", city=" + city + ", locality=" + locality + ", postalCode=" + postalCode + ", mobileNumber=" + mobileNumber
		+ ", profession=" + profession + ", doctorId=" + doctorId + ", locationId=" + locationId + ", hospitalId=" + hospitalId + ", referredBy="
		+ referredBy + ", createdTime=" + createdTime + "]";
    }

}
