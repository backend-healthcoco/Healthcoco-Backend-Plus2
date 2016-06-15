package com.dpdocter.solr.document;

import java.util.Date;

import org.apache.solr.client.solrj.beans.Field;
import org.springframework.data.annotation.Id;
import org.springframework.data.solr.core.mapping.SolrDocument;

import com.dpdocter.beans.DOB;

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
    private String firstNameWithoutSpace;

    @Field
    private String gender;

    @Field
    private String bloodGroup;

    @Field
    private String emailAddress;

    @Field
    private String days = "1";

    @Field
    private String months = "1";

    @Field
    private String years = "1";

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

    @Field
    private String imageUrl;

    @Field
    private String thumbnailUrl;

    @Field
    private String colorCode;

    @Field
    private Long registrationDate;

    @Field
    private String userUId;

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
	if (firstName != null)
	    setFirstNameWithoutSpace(firstName);
    }

    public String getFirstNameWithoutSpace() {
	return firstNameWithoutSpace;
    }

    public void setFirstNameWithoutSpace(String firstNameWithoutSpace) {
	this.firstNameWithoutSpace = firstNameWithoutSpace.replaceAll("\\s+", "");
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

    public String getDays() {
	return days;
    }

    public void setDays(String days) {
	this.days = days;
    }

    public String getMonths() {
	return months;
    }

    public void setMonths(String months) {
	this.months = months;
    }

    public String getYears() {
	return years;
    }

    public void setYears(String years) {
	this.years = years;
    }

    public DOB getDob() {
	return new DOB(Integer.parseInt(days), Integer.parseInt(months), Integer.parseInt(years));
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

    public String getImageUrl() {
	return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
	this.imageUrl = imageUrl;
    }

    public String getColorCode() {
	return colorCode;
    }

    public void setColorCode(String colorCode) {
	this.colorCode = colorCode;
    }

    public Long getRegistrationDate() {
	return registrationDate;
    }

    public void setRegistrationDate(Long registrationDate) {
	this.registrationDate = registrationDate;
    }

    public String getThumbnailUrl() {
	return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
	this.thumbnailUrl = thumbnailUrl;
    }

	public String getUserUId() {
		return userUId;
	}

	public void setUserUId(String userUId) {
		this.userUId = userUId;
	}

	@Override
	public String toString() {
		return "SolrPatientDocument [id=" + id + ", userId=" + userId + ", PID=" + PID + ", userName=" + userName
				+ ", firstName=" + firstName + ", firstNameWithoutSpace=" + firstNameWithoutSpace + ", gender=" + gender
				+ ", bloodGroup=" + bloodGroup + ", emailAddress=" + emailAddress + ", days=" + days + ", months="
				+ months + ", years=" + years + ", dob=" + dob + ", city=" + city + ", locality=" + locality
				+ ", postalCode=" + postalCode + ", mobileNumber=" + mobileNumber + ", profession=" + profession
				+ ", doctorId=" + doctorId + ", locationId=" + locationId + ", hospitalId=" + hospitalId
				+ ", referredBy=" + referredBy + ", createdTime=" + createdTime + ", imageUrl=" + imageUrl
				+ ", thumbnailUrl=" + thumbnailUrl + ", colorCode=" + colorCode + ", registrationDate="
				+ registrationDate + ", userUId=" + userUId + "]";
	}
}
