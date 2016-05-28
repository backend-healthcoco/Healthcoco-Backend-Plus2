package com.dpdocter.response;

import com.dpdocter.enums.UserState;

public class DoctorResponse {

	private String userId;
	
	private String firstName;
	
	private Boolean isActive = false;
	
	private String emailAddress;
	
	private UserState userState = UserState.USERSTATECOMPLETE;

    private String userUId;

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

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public UserState getUserState() {
		return userState;
	}

	public void setUserState(UserState userState) {
		this.userState = userState;
	}

	public String getUserUId() {
		return userUId;
	}

	public void setUserUId(String userUId) {
		this.userUId = userUId;
	}

	@Override
	public String toString() {
		return "DoctorResponse [userId=" + userId + ", firstName=" + firstName + ", isActive=" + isActive
				+ ", emailAddress=" + emailAddress + ", userState=" + userState + ", userUId=" + userUId + "]";
	}
}
