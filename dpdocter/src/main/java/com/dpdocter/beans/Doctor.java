package com.dpdocter.beans;

import java.util.List;

import com.dpdocter.enums.UserState;

/**
 * @author veeraj
 */

public class Doctor {
	private String id;

	private String firstName;

	private String lastName;
	
	private String countryCode;
	
	private String mobileNumber;

	private String emailAddress;

	private String imageUrl;

	private String thumbnailUrl;

	private String colorCode;

	private String coverImageUrl;

	private String coverThumbnailImageUrl;

	private List<String> specialities;

	private DoctorClinicProfile doctorClinicProfile;

	private UserState userState = UserState.USERSTATECOMPLETE;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public DoctorClinicProfile getDoctorClinicProfile() {
		return doctorClinicProfile;
	}

	public void setDoctorClinicProfile(DoctorClinicProfile doctorClinicProfile) {
		this.doctorClinicProfile = doctorClinicProfile;
	}

	public List<String> getSpecialities() {
		return specialities;
	}

	public void setSpecialities(List<String> specialities) {
		this.specialities = specialities;
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

	public String getCoverImageUrl() {
		return coverImageUrl;
	}

	public void setCoverImageUrl(String coverImageUrl) {
		this.coverImageUrl = coverImageUrl;
	}

	public String getCoverThumbnailImageUrl() {
		return coverThumbnailImageUrl;
	}

	public void setCoverThumbnailImageUrl(String coverThumbnailImageUrl) {
		this.coverThumbnailImageUrl = coverThumbnailImageUrl;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public UserState getUserState() {
		return userState;
	}

	public void setUserState(UserState userState) {
		this.userState = userState;
	}

	@Override
	public String toString() {
		return "Doctor [id=" + id + ", firstName=" + firstName + ", lastName=" + lastName + ", countryCode="
				+ countryCode + ", mobileNumber=" + mobileNumber + ", emailAddress=" + emailAddress + ", imageUrl="
				+ imageUrl + ", thumbnailUrl=" + thumbnailUrl + ", colorCode=" + colorCode + ", coverImageUrl="
				+ coverImageUrl + ", coverThumbnailImageUrl=" + coverThumbnailImageUrl + ", specialities="
				+ specialities + ", doctorClinicProfile=" + doctorClinicProfile + ", userState=" + userState + "]";
	}

}
