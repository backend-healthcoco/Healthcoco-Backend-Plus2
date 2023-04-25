package com.dpdocter.response;

public class PateientSignUpCheckResponse {

	private Boolean isPatientExistWithMobileNumber = false;

	public Boolean getIsPatientExistWithMobileNumber() {
		return isPatientExistWithMobileNumber;
	}

	public void setIsPatientExistWithMobileNumber(Boolean isPatientExistWithMobileNumber) {
		this.isPatientExistWithMobileNumber = isPatientExistWithMobileNumber;
	}

	@Override
	public String toString() {
		return "PateientSignUpCheckResponse [isPatientExistWithMobileNumber=" + isPatientExistWithMobileNumber + "]";
	}

}
