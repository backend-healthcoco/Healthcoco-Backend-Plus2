package com.dpdocter.request;

import java.util.List;

public class DoctorRegisterRequest {

	private String title;

	private String userId;

	private String firstName;

	private String emailAddress;

	private String mobileNumber;

	private Boolean hasLoginAccess = true;

	private Boolean hasBillingAccess = true;

	private String locationId;

	private String hospitalId;

	private String registerNumber;

	private String roleId;

	private Boolean isActivate;

	private String colorCode;

	private List<String> speciality;

	private String addedBy;

	public List<String> getSpeciality() {
		return speciality;
	}

	public void setSpeciality(List<String> speciality) {
		this.speciality = speciality;
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

	public String getEmailAddress() {
		return emailAddress != null ? emailAddress.toLowerCase() : emailAddress;
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

	public String getRoleId() {
		return roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	public Boolean getIsActivate() {
		return isActivate;
	}

	public void setIsActivate(Boolean isActivate) {
		this.isActivate = isActivate;
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

	public String getAddedBy() {
		return addedBy;
	}

	public void setAddedBy(String addedBy) {
		this.addedBy = addedBy;
	}

	@Override
	public String toString() {
		return "DoctorRegisterRequest [title=" + title + ", userId=" + userId + ", firstName=" + firstName
				+ ", emailAddress=" + emailAddress + ", mobileNumber=" + mobileNumber + ", hasLoginAccess="
				+ hasLoginAccess + ", hasBillingAccess=" + hasBillingAccess + ", locationId=" + locationId
				+ ", hospitalId=" + hospitalId + ", registerNumber=" + registerNumber + ", roleId=" + roleId
				+ ", isActivate=" + isActivate + ", colorCode=" + colorCode + ", speciality=" + speciality + "]";
	}

}
