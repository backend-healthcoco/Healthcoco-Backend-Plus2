package com.dpdocter.beans;

public class PatientShortCard {

	private String firstName;

	private String localPatientName;

	private String userName;

	private String emailAddress;

	private String imageUrl;

	private String thumbnailUrl;

	private String bloodGroup;

	private String PID;

	private String gender;

	private String mobileNumber;

	private String secPhoneNumber;

	private DOB dob;

	private String userId;
	
	private String PNUM;
	
	private String backendPatientId;

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLocalPatientName() {
		return localPatientName;
	}

	public void setLocalPatientName(String localPatientName) {
		this.localPatientName = localPatientName;
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

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getThumbnailUrl() {
		return thumbnailUrl;
	}

	public void setThumbnailUrl(String thumbnailUrl) {
		this.thumbnailUrl = thumbnailUrl;
	}

	public String getBloodGroup() {
		return bloodGroup;
	}

	public void setBloodGroup(String bloodGroup) {
		this.bloodGroup = bloodGroup;
	}

	public String getPID() {
		return PID;
	}

	public void setPID(String pID) {
		PID = pID;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getSecPhoneNumber() {
		return secPhoneNumber;
	}

	public void setSecPhoneNumber(String secPhoneNumber) {
		this.secPhoneNumber = secPhoneNumber;
	}

	public DOB getDob() {
		return dob;
	}

	public void setDob(DOB dob) {
		this.dob = dob;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getPNUM() {
		return PNUM;
	}

	public void setPNUM(String pNUM) {
		PNUM = pNUM;
	}

	public String getBackendPatientId() {
		return backendPatientId;
	}

	public void setBackendPatientId(String backendPatientId) {
		this.backendPatientId = backendPatientId;
	}

	@Override
	public String toString() {
		return "PatientShortCard [firstName=" + firstName + ", localPatientName=" + localPatientName + ", userName="
				+ userName + ", emailAddress=" + emailAddress + ", imageUrl=" + imageUrl + ", thumbnailUrl="
				+ thumbnailUrl + ", bloodGroup=" + bloodGroup + ", PID=" + PID + ", gender=" + gender
				+ ", mobileNumber=" + mobileNumber + ", secPhoneNumber=" + secPhoneNumber + ", dob=" + dob + ", userId="
				+ userId + ", PNUM=" + PNUM + ", backendPatientId=" + backendPatientId + "]";
	}
}
