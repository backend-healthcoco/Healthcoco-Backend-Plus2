package com.dpdocter.beans.v2;

import java.util.List;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.dpdocter.beans.DOB;
import com.dpdocter.beans.User;
import com.dpdocter.collections.GenericCollection;
import com.dpdocter.collections.PatientGroupCollection;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)

public class PatientCard extends GenericCollection {

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

	private String doctorId;

	private String doctorSepecificPatientId;

	private Boolean isPatientOTPVerified = false;

	private String locationId;

	private String hospitalId;

	private List<PatientGroupCollection> patientGroupCollections;

	private Boolean isPatientDiscarded = false;
	
	private String imageUrl;

	private String adhaarId;	
	
	private List<String> healthId;
	
	private String ndhmToken;
	
	private String linkToken;


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

	public List<PatientGroupCollection> getPatientGroupCollections() {
		return patientGroupCollections;
	}

	public void setPatientGroupCollections(List<PatientGroupCollection> patientGroupCollections) {
		this.patientGroupCollections = patientGroupCollections;
	}

	public Boolean getIsPatientDiscarded() {
		return isPatientDiscarded;
	}

	public void setIsPatientDiscarded(Boolean isPatientDiscarded) {
		this.isPatientDiscarded = isPatientDiscarded;
	}

	
	
	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	
	

	public String getAdhaarId() {
		return adhaarId;
	}

	public void setAdhaarId(String adhaarId) {
		this.adhaarId = adhaarId;
	}
	
	

	public String getNdhmToken() {
		return ndhmToken;
	}

	public void setNdhmToken(String ndhmToken) {
		this.ndhmToken = ndhmToken;
	}

	public List<String> getHealthId() {
		return healthId;
	}

	public void setHealthId(List<String> healthId) {
		this.healthId = healthId;
	}
	
	

	

	public String getLinkToken() {
		return linkToken;
	}

	public void setLinkToken(String linkToken) {
		this.linkToken = linkToken;
	}

	@Override
	public String toString() {
		return "PatientCard [id=" + id + ", userId=" + userId + ", firstName=" + firstName + ", localPatientName="
				+ localPatientName + ", emailAddress=" + emailAddress + ", thumbnailUrl=" + thumbnailUrl + ", PID="
				+ PID + ", gender=" + gender + ", mobileNumber=" + mobileNumber + ", dob=" + dob + ", colorCode="
				+ colorCode + ", PNUM=" + PNUM + ", user=" + user + ", doctorId=" + doctorId
				+ ", doctorSepecificPatientId=" + doctorSepecificPatientId + ", isPatientOTPVerified="
				+ isPatientOTPVerified + "]";
	}

}
