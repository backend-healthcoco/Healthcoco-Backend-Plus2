package com.dpdocter.beans.v2;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.dpdocter.beans.DOB;
import com.dpdocter.beans.User;

/*Expected Structure: 
id,thumbnailurl, localPatientName, firstName, Pid,Pnum,userId, 
dob,gender,mobilenumber,colorCode

*/
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)

public class PatientCard {

	private String id;

	private String userId;

	private String firstName;

	private String localPatientName;

	private String emailAddress;

	private String thumbnailUrl;

	private String PID;

	private String gender;

	private String mobileNumber;

	private DOB dob;

	private String colorCode;

	private String PNUM;

	// @Transient
	private User user;

	private String doctorSepecificPatientId;
	private Boolean isPatientOTPVerified = false;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getDoctorSepecificPatientId() {
		return doctorSepecificPatientId;
	}

	public void setDoctorSepecificPatientId(String doctorSepecificPatientId) {
		this.doctorSepecificPatientId = doctorSepecificPatientId;
	}

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

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getThumbnailUrl() {
		return thumbnailUrl;
	}

	public void setThumbnailUrl(String thumbnailUrl) {
		this.thumbnailUrl = thumbnailUrl;
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

	public DOB getDob() {
		return dob;
	}

	public void setDob(DOB dob) {
		this.dob = dob;
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

	public Boolean getIsPatientOTPVerified() {
		return isPatientOTPVerified;
	}

	public void setIsPatientOTPVerified(Boolean isPatientOTPVerified) {
		this.isPatientOTPVerified = isPatientOTPVerified;
	}

	@Override
	public String toString() {
		return "PatientCard [id=" + id + ", userId=" + userId + ", firstName=" + firstName + ", localPatientName="
				+ localPatientName + ", emailAddress=" + emailAddress + ", thumbnailUrl=" + thumbnailUrl + ", PID="
				+ PID + ", gender=" + gender + ", mobileNumber=" + mobileNumber + ", dob=" + dob + ", colorCode="
				+ colorCode + ", PNUM=" + PNUM + "]";
	}

}
