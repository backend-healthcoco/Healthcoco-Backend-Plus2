package com.dpdocter.request;

public class PatientSignupRequestMobile {
    private String name;

    private String password;


    private String mobileNumber;
    
    
    private boolean isNewPatientNeedToBeCreated;

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public String getPassword() {
	return password;
    }

    public void setPassword(String password) {
	this.password = password;
    }


    public String getMobileNumber() {
	return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
	this.mobileNumber = mobileNumber;
    }
    
    

	public boolean isNewPatientNeedToBeCreated() {
		return isNewPatientNeedToBeCreated;
	}

	public void setNewPatientNeedToBeCreated(boolean isNewPatientNeedToBeCreated) {
		this.isNewPatientNeedToBeCreated = isNewPatientNeedToBeCreated;
	}

	@Override
	public String toString() {
		return "PatientSignupRequestMobile [name=" + name + ", password="
				+ password + ", mobileNumber=" + mobileNumber
				+ ", isNewPatientNeedToBeCreated="
				+ isNewPatientNeedToBeCreated + "]";
	}

	

    

}
