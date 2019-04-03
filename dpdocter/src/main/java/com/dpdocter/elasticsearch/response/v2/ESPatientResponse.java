package com.dpdocter.elasticsearch.response.v2;

import com.dpdocter.beans.DOB;

/*Expected Structure: 
id,thumbnailurl, localPatientName, firstName,
 Pid,Pnum,userId, dob,gender,mobilenumber,
*/

public class ESPatientResponse {

	private String id;

	private String userId;

	private String PID;

	private String firstName;

	private String localPatientName;

	private String gender;

	private String emailAddress;

	private DOB dob;

	private String mobileNumber;

	private String thumbnailUrl;

	private String colorCode;

	private String PNUM;

	private Boolean isChild = false;

	private String fatherName;

	private String motherName;

	private String doctorId;

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

	public DOB getDob() {
		return dob;
	}

	public void setDob(DOB dob) {
		this.dob = dob;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getThumbnailUrl() {
		return thumbnailUrl;
	}

	public void setThumbnailUrl(String thumbnailUrl) {
		this.thumbnailUrl = thumbnailUrl;
	}

	public String getColorCode() {
		return colorCode;
	}

	public void setColorCode(String colorCode) {
		this.colorCode = colorCode;
	}

	public String getPNUM() {
		return PNUM;
	}

	public void setPNUM(String pNUM) {
		PNUM = pNUM;
	}

	public Boolean getIsChild() {
		return isChild;
	}

	public void setIsChild(Boolean isChild) {
		this.isChild = isChild;
	}

	public String getFatherName() {
		return fatherName;
	}

	public void setFatherName(String fatherName) {
		this.fatherName = fatherName;
	}

	public String getMotherName() {
		return motherName;
	}

	public void setMotherName(String motherName) {
		this.motherName = motherName;
	}

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	@Override
	public String toString() {
		return "ESPatientResponse [id=" + id + ", userId=" + userId + ", PID=" + PID + ", firstName=" + firstName
				+ ", localPatientName=" + localPatientName + ", gender=" + gender + ", emailAddress=" + emailAddress
				+ ", dob=" + dob + ", mobileNumber=" + mobileNumber + ", thumbnailUrl=" + thumbnailUrl + ", colorCode="
				+ colorCode + ", PNUM=" + PNUM + ", isChild=" + isChild + ", fatherName=" + fatherName + ", motherName="
				+ motherName + ", doctorId=" + doctorId + "]";
	}
}
