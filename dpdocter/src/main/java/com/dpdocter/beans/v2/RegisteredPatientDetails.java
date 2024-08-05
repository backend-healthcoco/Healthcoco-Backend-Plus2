package com.dpdocter.beans.v2;

import java.util.List;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.dpdocter.beans.DOB;
import com.dpdocter.collections.GenericCollection;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class RegisteredPatientDetails extends GenericCollection {

	private String firstName;

	private String localPatientName;

	private String userId;

	private String userName;

	private String countryCode;

	private String mobileNumber;

	private String gender;

	private String doctorId;

	private String locationId;

	private String hospitalId;

	private String PID;
	
	private DOB dob;

	private String colorCode;

	private String backendPatientId;

	private Boolean isPatientDiscarded = false;

	private String PNUM;

	private Boolean isChild = false;

	private List<String> groupIds;
	
	private String imageUrl;

	private String thumbnailUrl;
	private List<String> healthId;
	
	private String ndhmToken;
	
	private String linkToken;
    private String abhaNumber;

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

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
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

	public String getPID() {
		return PID;
	}

	public void setPID(String pID) {
		PID = pID;
	}

	public String getColorCode() {
		return colorCode;
	}

	public void setColorCode(String colorCode) {
		this.colorCode = colorCode;
	}

	public String getBackendPatientId() {
		return backendPatientId;
	}

	public void setBackendPatientId(String backendPatientId) {
		this.backendPatientId = backendPatientId;
	}

	public Boolean getIsPatientDiscarded() {
		return isPatientDiscarded;
	}

	public void setIsPatientDiscarded(Boolean isPatientDiscarded) {
		this.isPatientDiscarded = isPatientDiscarded;
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

	public List<String> getGroupIds() {
		return groupIds;
	}

	public void setGroupIds(List<String> groupIds) {
		this.groupIds = groupIds;
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

	public List<String> getHealthId() {
		return healthId;
	}

	public void setHealthId(List<String> healthId) {
		this.healthId = healthId;
	}

	public String getNdhmToken() {
		return ndhmToken;
	}

	public void setNdhmToken(String ndhmToken) {
		this.ndhmToken = ndhmToken;
	}

	public String getLinkToken() {
		return linkToken;
	}

	public void setLinkToken(String linkToken) {
		this.linkToken = linkToken;
	}

	public String getAbhaNumber() {
		return abhaNumber;
	}

	public void setAbhaNumber(String abhaNumber) {
		this.abhaNumber = abhaNumber;
	}

	public DOB getDob() {
		return dob;
	}

	public void setDob(DOB dob) {
		this.dob = dob;
	}

	
}
