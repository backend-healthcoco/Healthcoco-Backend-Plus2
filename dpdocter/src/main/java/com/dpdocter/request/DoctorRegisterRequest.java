package com.dpdocter.request;

public class DoctorRegisterRequest {

    private String userId;

    private String firstName;

    private String emailAddress;

    private String mobileNumber;

    private String locationId;

    private String hospitalId;

    private String registerNumber;

    private String roleId;
    
    private Boolean isActivate;

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

	public String getRegisterNumber() {
		return registerNumber;
	}

	public void setRegisterNumber(String registerNumber) {
		this.registerNumber = registerNumber;
	}

	public String getRoleId() {
		return roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	public Boolean getIsActivate() {
		return isActivate;
	}

	public void setIsActivate(Boolean isActivate) {
		this.isActivate = isActivate;
	}

	@Override
	public String toString() {
		return "DoctorRegisterRequest [userId=" + userId + ", firstName=" + firstName + ", emailAddress=" + emailAddress
				+ ", mobileNumber=" + mobileNumber + ", locationId=" + locationId + ", hospitalId=" + hospitalId
				+ ", registerNumber=" + registerNumber + ", roleId=" + roleId + ", isActivate=" + isActivate + "]";
	}
}
