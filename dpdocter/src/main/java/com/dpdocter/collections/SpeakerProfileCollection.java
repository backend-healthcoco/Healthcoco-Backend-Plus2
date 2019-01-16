package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "speaker_profile_cl")
public class SpeakerProfileCollection extends GenericCollection {
	@Id
	private ObjectId id;
	@Field
	private String profileImage;
	@Field
	private String firstName;
	@Field
	private String description;
	@Field
	private String mobileNumber;
	@Field
	private String emailAddress;
	@Field
	private String role;
	@Field
	private Boolean discarded = false;

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
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

	@Override
	public String toString() {
		return "SpeakerProfileCollection [id=" + id + ", profileImage=" + profileImage + ", firstName=" + firstName
				+ ", description=" + description + ", getId()=" + getId() + ", getProfileImage()=" + getProfileImage()
				+ ", getFirstName()=" + getFirstName() + ", getDescription()=" + getDescription()
				+ ", getCreatedTime()=" + getCreatedTime() + ", getUpdatedTime()=" + getUpdatedTime()
				+ ", getCreatedBy()=" + getCreatedBy() + ", getAdminCreatedTime()=" + getAdminCreatedTime()
				+ ", getClass()=" + getClass() + ", hashCode()=" + hashCode() + ", toString()=" + super.toString()
				+ "]";
	}

}
