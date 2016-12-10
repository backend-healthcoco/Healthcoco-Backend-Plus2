package com.dpdocter.response;

import java.util.Date;
import java.util.List;

import com.dpdocter.beans.PatientCard;
import com.dpdocter.collections.GenericCollection;
import com.dpdocter.enums.UserState;

public class UserLookupResponse extends GenericCollection {

	private String id;

	private String title;

	private String firstName;

	private String lastName;

	private String middleName;

	private String userName;

	private String emailAddress;

	private String countryCode;

	private String mobileNumber;

	private String imageUrl;

	private String thumbnailUrl;

	private Boolean isActive = false;

	private Boolean isVerified = false;

	private String coverImageUrl;

	private String coverThumbnailImageUrl;

	private String colorCode;

	private UserState userState = UserState.USERSTATECOMPLETE;

	private Date lastSession;

	private boolean signedUp = false;

	private String userUId;

	private List<PatientCard> patients;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
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

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
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

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public Boolean getIsVerified() {
		return isVerified;
	}

	public void setIsVerified(Boolean isVerified) {
		this.isVerified = isVerified;
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

	public String getColorCode() {
		return colorCode;
	}

	public void setColorCode(String colorCode) {
		this.colorCode = colorCode;
	}

	public UserState getUserState() {
		return userState;
	}

	public void setUserState(UserState userState) {
		this.userState = userState;
	}

	public Date getLastSession() {
		return lastSession;
	}

	public void setLastSession(Date lastSession) {
		this.lastSession = lastSession;
	}

	public boolean isSignedUp() {
		return signedUp;
	}

	public void setSignedUp(boolean signedUp) {
		this.signedUp = signedUp;
	}

	public String getUserUId() {
		return userUId;
	}

	public void setUserUId(String userUId) {
		this.userUId = userUId;
	}

	public List<PatientCard> getPatients() {
		return patients;
	}

	public void setPatients(List<PatientCard> patients) {
		this.patients = patients;
	}

	@Override
	public String toString() {
		return "UserLookupResponse [id=" + id + ", title=" + title + ", firstName=" + firstName + ", lastName="
				+ lastName + ", middleName=" + middleName + ", userName=" + userName + ", emailAddress=" + emailAddress
				+ ", countryCode=" + countryCode + ", mobileNumber=" + mobileNumber + ", imageUrl=" + imageUrl
				+ ", thumbnailUrl=" + thumbnailUrl + ", isActive=" + isActive + ", isVerified=" + isVerified
				+ ", coverImageUrl=" + coverImageUrl + ", coverThumbnailImageUrl=" + coverThumbnailImageUrl
				+ ", colorCode=" + colorCode + ", userState=" + userState + ", lastSession=" + lastSession
				+ ", signedUp=" + signedUp + ", userUId=" + userUId + "]";
	}

}
