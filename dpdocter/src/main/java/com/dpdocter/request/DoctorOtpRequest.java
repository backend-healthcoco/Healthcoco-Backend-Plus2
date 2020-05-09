package com.dpdocter.request;

import com.dpdocter.collections.GenericCollection;

public class DoctorOtpRequest extends GenericCollection {

	private String mobileNumber;
	
	private String countryCode;

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
	
	
}
