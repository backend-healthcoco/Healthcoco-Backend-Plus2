package com.dpdocter.beans;

import java.util.Date;

import com.dpdocter.collections.GenericCollection;
import com.dpdocter.enums.ProfileType;
import com.dpdocter.response.AcadamicClassResponse;

public class AcadamicProfile extends GenericCollection {

	private String id;

	private String userId;

	private String firstName;

	private String localPatientName;

	private String mobileNumber;

	private String uniqueId;

	private String rollNo;

	private AcadamicClassResponse acadamicClass;

	private String emailAddress;

	private String acadamicSection;

	private Date admissionDate;

	private ProfileType type = ProfileType.STUDENT;

	private String imageUrl;

	private String thumbnailUrl;

	private Boolean isSuperStar = false;

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

	public Boolean getIsSuperStar() {
		return isSuperStar;
	}

	public void setIsSuperStar(Boolean isSuperStar) {
		this.isSuperStar = isSuperStar;
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

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getUniqueId() {
		return uniqueId;
	}

	public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}

	public ProfileType getType() {
		return type;
	}

	public void setType(ProfileType type) {
		this.type = type;
	}

	public String getRollNo() {
		return rollNo;
	}

	public void setRollNo(String rollNo) {
		this.rollNo = rollNo;
	}

	public AcadamicClassResponse getAcadamicClass() {
		return acadamicClass;
	}

	public void setAcadamicClass(AcadamicClassResponse acadamicClass) {
		this.acadamicClass = acadamicClass;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getAcadamicSection() {
		return acadamicSection;
	}

	public void setAcadamicSection(String acadamicSection) {
		this.acadamicSection = acadamicSection;
	}

	public Date getAdmissionDate() {
		return admissionDate;
	}

	public void setAdmissionDate(Date admissionDate) {
		this.admissionDate = admissionDate;
	}

	@Override
	public String toString() {
		return "AcadamicProfile [id=" + id + ", userId=" + userId + ", firstName=" + firstName + ", localPatientName="
				+ localPatientName + ", mobileNumber=" + mobileNumber + ", uniqueId=" + uniqueId + ", type=" + type
				+ "]";
	}

}
