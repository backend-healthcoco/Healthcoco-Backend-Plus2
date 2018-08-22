package com.dpdocter.response.v2;

import com.dpdocter.enums.UserState;

public class ClinicDoctorResponse {

	private String title;

	private String userId;

	private String firstName;

	private Boolean isActivate;

	private UserState userState = UserState.USERSTATECOMPLETE;

	private String emailAddress;

	private String mobileNumber;

	private String colorCode;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public Boolean getIsActivate() {
		return isActivate;
	}

	public void setIsActivate(Boolean isActivate) {
		this.isActivate = isActivate;
	}

	public UserState getUserState() {
		return userState;
	}

	public void setUserState(UserState userState) {
		this.userState = userState;
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

	public String getColorCode() {
		return colorCode;
	}

	public void setColorCode(String colorCode) {
		this.colorCode = colorCode;
	}

	@Override
	public String toString() {
		return "ClinicDoctorResponse [title=" + title + ", userId=" + userId + ", firstName=" + firstName
				+ ", isActivate=" + isActivate + ", userState=" + userState + ", emailAddress=" + emailAddress
				+ ", mobileNumber=" + mobileNumber + ", colorCode=" + colorCode + ", getClass()=" + getClass()
				+ ", hashCode()=" + hashCode() + ", toString()=" + super.toString() + "]";
	}

}
