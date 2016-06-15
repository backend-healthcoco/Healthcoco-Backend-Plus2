package com.dpdocter.response;

public class PateientSignUpCheckResponse {

	private Boolean isPatientExistWithMobileNumber = false;
	
	private Boolean canAddNewPatient = true;

	public Boolean getIsPatientExistWithMobileNumber() {
		return isPatientExistWithMobileNumber;
	}

	public void setIsPatientExistWithMobileNumber(Boolean isPatientExistWithMobileNumber) {
		this.isPatientExistWithMobileNumber = isPatientExistWithMobileNumber;
	}

	public Boolean getCanAddNewPatient() {
		return canAddNewPatient;
	}

	public void setCanAddNewPatient(Boolean canAddNewPatient) {
		this.canAddNewPatient = canAddNewPatient;
	}

	@Override
	public String toString() {
		return "PateientSignUpCheckResponse [isPatientExistWithMobileNumber=" + isPatientExistWithMobileNumber
				+ ", canAddNewPatient=" + canAddNewPatient + "]";
	}
}
