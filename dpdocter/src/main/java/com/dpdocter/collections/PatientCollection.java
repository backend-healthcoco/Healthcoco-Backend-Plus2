package com.dpdocter.collections;

import java.util.List;

import org.apache.commons.lang.WordUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.DOB;
import com.dpdocter.beans.Relations;

@Document(collection = "patient_cl")
public class PatientCollection extends GenericCollection {

    @Id
    private String id;

    @Field
    private String firstName;

    @Field
    private String bloodGroup;

    @Field
    private String profession;

    @Field
    private List<Relations> relations;

    @Field
    private String emailAddress;

    @Field
    private String doctorId;

    @Field
    private String locationId;

    @Field
    private String hospitalId;

    @Field
    private String addressId;

    @Field
    private String secMobile;

    @Field
    private String adhaarId;

    @Field
    private String panCardNumber;

    @Field
    private String drivingLicenseId;

    @Field
    private String insuranceId;

    @Field
    private String insuranceName;

    @Field
    private String userId;

    @Field
    private List<String> notes;

    @Field
    private String PID;

    @Field
    private Long registrationDate;

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

    public String getBloodGroup() {
	return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
	this.bloodGroup = bloodGroup;
    }

    public String getProfession() {
	return profession;
    }

    public void setProfession(String profession) {
	this.profession = profession;
    }

    public List<Relations> getRelations() {
	return relations;
    }

    public void setRelations(List<Relations> relations) {
	this.relations = relations;
    }

    public String getEmailAddress() {
	return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
	this.emailAddress = emailAddress;
    }

    public String getDoctorId() {
	return doctorId;
    }

    public void setDoctorId(String doctorId) {
	this.doctorId = doctorId;
    }

    public String getAddressId() {
	return addressId;
    }

    public void setAddressId(String addressId) {
	this.addressId = addressId;
    }

    public String getSecMobile() {
	return secMobile;
    }

    public void setSecMobile(String secMobile) {
	this.secMobile = secMobile;
    }

    public String getAdhaarId() {
	return adhaarId;
    }

    public void setAdhaarId(String adhaarId) {
	this.adhaarId = adhaarId;
    }

    public String getPanCardNumber() {
	return panCardNumber;
    }

    public void setPanCardNumber(String panCardNumber) {
	this.panCardNumber = panCardNumber;
    }

    public String getDrivingLicenseId() {
	return drivingLicenseId;
    }

    public void setDrivingLicenseId(String drivingLicenseId) {
	this.drivingLicenseId = drivingLicenseId;
    }

    public String getInsuranceId() {
	return insuranceId;
    }

    public void setInsuranceId(String insuranceId) {
	this.insuranceId = insuranceId;
    }

    public String getInsuranceName() {
	return insuranceName;
    }

    public void setInsuranceName(String insuranceName) {
	this.insuranceName = insuranceName;
    }

    public String getUserId() {
	return userId;
    }

    public void setUserId(String userId) {
	this.userId = userId;
    }

    public List<String> getNotes() {
	return notes;
    }

    public void setNotes(List<String> notes) {
	this.notes = notes;
    }

    public String getPID() {
	return PID;
    }

    public void setPID(String pID) {
	PID = pID;
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

    public Long getRegistrationDate() {
	return registrationDate;
    }

    public void setRegistrationDate(Long registrationDate) {
	this.registrationDate = registrationDate;
    }

    public String getFirstName() {
	return firstName;
    }

    public void setFirstName(String firstName) {
    if(firstName!=null)	this.firstName = WordUtils.capitalize(firstName.toLowerCase());
    else this.firstName = firstName;
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
	return "PatientCollection [id=" + id + ", firstName=" + firstName + ", bloodGroup=" + bloodGroup + ", profession=" + profession + ", relations="
		+ relations + ", emailAddress=" + emailAddress + ", doctorId=" + doctorId + ", locationId=" + locationId + ", hospitalId=" + hospitalId
		+ ", addressId=" + addressId + ", secMobile=" + secMobile + ", adhaarId=" + adhaarId + ", panCardNumber=" + panCardNumber
		+ ", drivingLicenseId=" + drivingLicenseId + ", insuranceId=" + insuranceId + ", insuranceName=" + insuranceName + ", userId=" + userId
		+ ", notes=" + notes + ", PID=" + PID + ", registrationDate=" + registrationDate + ", gender=" + gender + ", dob=" + dob + "]";
    }

}
