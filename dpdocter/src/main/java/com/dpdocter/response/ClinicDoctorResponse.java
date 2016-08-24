package com.dpdocter.response;

import java.util.Date;
import java.util.List;

import com.dpdocter.beans.Role;
import com.dpdocter.enums.UserState;

public class ClinicDoctorResponse {

	private String title;
    
	private String userId;

    private String firstName;

    private List<Role> role;

    private Boolean isActivate;

    private Date lastSession;

    private Boolean discarded = false;

    private UserState userState = UserState.USERSTATECOMPLETE;
    
    private String emailAddress;

    private String mobileNumber;

    private String registerNumber;

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

    public List<Role> getRole() {
		return role;
	}

	public void setRole(List<Role> role) {
		this.role = role;
	}

	public Boolean getIsActivate() {
	return isActivate;
    }

    public void setIsActivate(Boolean isActivate) {
	this.isActivate = isActivate;
    }

    public Date getLastSession() {
	return lastSession;
    }

    public void setLastSession(Date lastSession) {
	this.lastSession = lastSession;
    }

    public Boolean getDiscarded() {
	return discarded;
    }

    public void setDiscarded(Boolean discarded) {
	this.discarded = discarded;
    }

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public UserState getUserState() {
		return userState;
	}

	public void setUserState(UserState userState) {
		this.userState = userState;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getRegisterNumber() {
		return registerNumber;
	}

	public void setRegisterNumber(String registerNumber) {
		this.registerNumber = registerNumber;
	}

	@Override
	public String toString() {
		return "ClinicDoctorResponse [title=" + title + ", userId=" + userId + ", firstName=" + firstName + ", role="
				+ role + ", isActivate=" + isActivate + ", lastSession=" + lastSession + ", discarded=" + discarded
				+ ", userState=" + userState + ", emailAddress=" + emailAddress + ", mobileNumber=" + mobileNumber
				+ ", registerNumber=" + registerNumber + "]";
	}
}
