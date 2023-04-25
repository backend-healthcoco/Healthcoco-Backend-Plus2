package com.dpdocter.request;

import java.util.List;

public class DoctorContactAddEditRequest {
	private String doctorId;

	private String mobileNumber;

	private List<String> additionalNumbers;

	private List<String> otherEmailAddresses;

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public List<String> getAdditionalNumbers() {
		return additionalNumbers;
	}

	public void setAdditionalNumbers(List<String> additionalNumbers) {
		this.additionalNumbers = additionalNumbers;
	}

	public List<String> getOtherEmailAddresses() {
		return otherEmailAddresses;
	}

	public void setOtherEmailAddresses(List<String> otherEmailAddresses) {
		this.otherEmailAddresses = otherEmailAddresses;
	}

	@Override
	public String toString() {
		return "DoctorContactAddEditRequest [doctorId=" + doctorId + ", mobileNumber=" + mobileNumber
				+ ", additionalNumbers=" + additionalNumbers + ", otherEmailAddresses=" + otherEmailAddresses + "]";
	}

}
