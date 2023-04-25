package com.dpdocter.response;

public class PatientStatusResponse {

	private Boolean isDataAvailableWithOtherDoctor = false;

	private Boolean isPatientOTPVerified = false;

	public Boolean getIsDataAvailableWithOtherDoctor() {
		return isDataAvailableWithOtherDoctor;
	}

	public void setIsDataAvailableWithOtherDoctor(Boolean isDataAvailableWithOtherDoctor) {
		this.isDataAvailableWithOtherDoctor = isDataAvailableWithOtherDoctor;
	}

	public Boolean getIsPatientOTPVerified() {
		return isPatientOTPVerified;
	}

	public void setIsPatientOTPVerified(Boolean isPatientOTPVerified) {
		this.isPatientOTPVerified = isPatientOTPVerified;
	}

	@Override
	public String toString() {
		return "PatientStatusResponse [isDataAvailableWithOtherDoctor=" + isDataAvailableWithOtherDoctor
				+ ", isPatientOTPVerified=" + isPatientOTPVerified + "]";
	}
}
