package com.dpdocter.response.v2;

import java.util.List;

import com.dpdocter.beans.Role;
import com.dpdocter.enums.UserState;

public class ClinicDoctorResponse {

	private String title;

	private String userId;

	private String firstName;

	private List<Role> role;

	private Boolean isActivate;

	private UserState userState = UserState.USERSTATECOMPLETE;

	private String emailAddress;

	private String mobileNumber;

	private String colorCode;

	private Boolean isDefault=false;
	
	private Boolean isShowDoctorInCalender = false;

	private Boolean isShowPatientNumber = false;

	public Boolean getIsDefault() {
		return isDefault;
	}

	public void setIsDefault(Boolean isDefault) {
		this.isDefault = isDefault;
	}

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

	public List<Role> getRole() {
		return role;
	}

	public void setRole(List<Role> role) {
		this.role = role;
	}

	public Boolean getIsShowDoctorInCalender() {
		return isShowDoctorInCalender;
	}

	public void setIsShowDoctorInCalender(Boolean isShowDoctorInCalender) {
		this.isShowDoctorInCalender = isShowDoctorInCalender;
	}

	public Boolean getIsShowPatientNumber() {
		return isShowPatientNumber;
	}

	public void setIsShowPatientNumber(Boolean isShowPatientNumber) {
		this.isShowPatientNumber = isShowPatientNumber;
	}

	@Override
	public String toString() {
		return "ClinicDoctorResponse [title=" + title + ", userId=" + userId + ", firstName=" + firstName
				+ ", isActivate=" + isActivate + ", userState=" + userState + ", emailAddress=" + emailAddress
				+ ", mobileNumber=" + mobileNumber + ", colorCode=" + colorCode + ", getClass()=" + getClass()
				+ ", hashCode()=" + hashCode() + ", toString()=" + super.toString() + "]";
	}

}
