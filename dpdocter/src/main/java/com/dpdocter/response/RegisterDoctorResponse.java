package com.dpdocter.response;

import java.util.Arrays;
import java.util.List;

import com.dpdocter.beans.DOB;
import com.dpdocter.beans.Role;
import com.dpdocter.enums.UserState;

public class RegisterDoctorResponse {

	private String title;

	private String userId;

	private String firstName;

	private String lastName;

	private String middleName;

	private String userName;

	private char[] password;

	private String emailAddress;

	private String mobileNumber;

	private String gender;

	private DOB dob;

	private String phoneNumber;

	private String imageUrl;

	private List<String> specialization;

	private String locationId;

	private String hospitalId;

	private String registerNumber;

	private List<Role> role;

	private UserState userState = UserState.USERSTATECOMPLETE;

	private String colorCode;

	private Boolean hasLoginAccess = true;

	private Boolean hasBillingAccess = true;

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

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public char[] getPassword() {
		return password;
	}

	public void setPassword(char[] password) {
		this.password = password;
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

	public List<String> getSpecialization() {
		return specialization;
	}

	public void setSpecialization(List<String> specialization) {
		this.specialization = specialization;
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

	public String getRegisterNumber() {
		return registerNumber;
	}

	public void setRegisterNumber(String registerNumber) {
		this.registerNumber = registerNumber;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<Role> getRole() {
		return role;
	}

	public void setRole(List<Role> role) {
		this.role = role;
	}

	public UserState getUserState() {
		return userState;
	}

	public void setUserState(UserState userState) {
		this.userState = userState;
	}

	public String getColorCode() {
		return colorCode;
	}

	public void setColorCode(String colorCode) {
		this.colorCode = colorCode;
	}

	public Boolean getHasLoginAccess() {
		return hasLoginAccess;
	}

	public void setHasLoginAccess(Boolean hasLoginAccess) {
		this.hasLoginAccess = hasLoginAccess;
	}

	public Boolean getHasBillingAccess() {
		return hasBillingAccess;
	}

	public void setHasBillingAccess(Boolean hasBillingAccess) {
		this.hasBillingAccess = hasBillingAccess;
	}

	@Override
	public String toString() {
		return "RegisterDoctorResponse [title=" + title + ", userId=" + userId + ", firstName=" + firstName
				+ ", lastName=" + lastName + ", middleName=" + middleName + ", userName=" + userName + ", password="
				+ Arrays.toString(password) + ", emailAddress=" + emailAddress + ", mobileNumber=" + mobileNumber
				+ ", gender=" + gender + ", dob=" + dob + ", phoneNumber=" + phoneNumber + ", imageUrl=" + imageUrl
				+ ", specialization=" + specialization + ", locationId=" + locationId + ", hospitalId=" + hospitalId
				+ ", registerNumber=" + registerNumber + ", role=" + role + ", userState=" + userState + ", colorCode="
				+ colorCode + ", hasLoginAccess=" + hasLoginAccess + ", hasBillingAccess=" + hasBillingAccess + "]";
	}
}
