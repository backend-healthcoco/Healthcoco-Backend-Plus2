package com.dpdocter.beans;

import java.util.List;

import com.dpdocter.enums.UserState;

public class User {
    private String id;

    private String title;

    private String firstName;

    private String userName;

    private String emailAddress;

    private String mobileNumber;

    private String gender;

    private DOB dob;

    private String bloodGroup;

    private String secPhoneNumber;

    private Boolean isPartOfClinic;

    private String imageUrl;

    private String thumbnailUrl;

    private String colorCode;

    private UserState userState;

    private String userUId;

    private List<String> specialities;
    
    public User(String firstName, String mobileNumber) {
		this.firstName = firstName;
		this.mobileNumber = mobileNumber;
	}

	public User() {
	}

	public String getImageUrl() {
	return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
	this.imageUrl = imageUrl;
    }

    public Boolean getIsPartOfClinic() {
	return isPartOfClinic;
    }

    public void setIsPartOfClinic(Boolean isPartOfClinic) {
	this.isPartOfClinic = isPartOfClinic;
    }

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

    public String getUserName() {
	return userName;
    }

    public void setUserName(String userName) {
	this.userName = userName;
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

    public DOB getDob() {
	return dob;
    }

    public void setDob(DOB dob) {
	this.dob = dob;
    }

    public String getSecPhoneNumber() {
	return secPhoneNumber;
    }

    public void setSecPhoneNumber(String secPhoneNumber) {
	this.secPhoneNumber = secPhoneNumber;
    }

    public String getColorCode() {
	return colorCode;
    }

    public void setColorCode(String colorCode) {
	this.colorCode = colorCode;
    }

    public String getThumbnailUrl() {
	return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
	this.thumbnailUrl = thumbnailUrl;
    }

    public UserState getUserState() {
	return userState;
    }

    public void setUserState(UserState userState) {
	this.userState = userState;
    }

    public String getTitle() {
	return title;
    }

    public void setTitle(String title) {
	this.title = title;
    }

    public String getBloodGroup() {
	return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
	this.bloodGroup = bloodGroup;
    }

	public String getUserUId() {
		return userUId;
	}

	public void setUserUId(String userUId) {
		this.userUId = userUId;
	}

	public List<String> getSpecialities() {
		return specialities;
	}

	public void setSpecialities(List<String> specialities) {
		this.specialities = specialities;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", title=" + title + ", firstName=" + firstName + ", userName=" + userName
				+ ", emailAddress=" + emailAddress + ", mobileNumber=" + mobileNumber + ", gender=" + gender + ", dob="
				+ dob + ", bloodGroup=" + bloodGroup + ", secPhoneNumber=" + secPhoneNumber + ", isPartOfClinic="
				+ isPartOfClinic + ", imageUrl=" + imageUrl + ", thumbnailUrl=" + thumbnailUrl + ", colorCode="
				+ colorCode + ", userState=" + userState + ", userUId=" + userUId + ", specialities=" + specialities
				+ "]";
	}

}
