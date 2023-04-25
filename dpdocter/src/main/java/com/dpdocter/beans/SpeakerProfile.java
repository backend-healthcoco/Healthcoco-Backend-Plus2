package com.dpdocter.beans;

import com.dpdocter.collections.GenericCollection;

public class SpeakerProfile extends GenericCollection {

	private String id;

	private String profileImage;

	private String firstName;

	private String description;

	private String mobileNumber;

	private String emailAddress;

	private String role;

	private Boolean discarded = false;

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getProfileImage() {
		return profileImage;
	}

	public void setProfileImage(String profileImage) {
		this.profileImage = profileImage;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return "SpeakerProfile [id=" + id + ", profileImage=" + profileImage + ", firstName=" + firstName
				+ ", description=" + description + ", getId()=" + getId() + ", getProfileImage()=" + getProfileImage()
				+ ", getFirstName()=" + getFirstName() + ", getDescription()=" + getDescription()
				+ ", getCreatedTime()=" + getCreatedTime() + ", getUpdatedTime()=" + getUpdatedTime()
				+ ", getCreatedBy()=" + getCreatedBy() + ", getAdminCreatedTime()=" + getAdminCreatedTime()
				+ ", getClass()=" + getClass() + ", hashCode()=" + hashCode() + ", toString()=" + super.toString()
				+ "]";
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

}
