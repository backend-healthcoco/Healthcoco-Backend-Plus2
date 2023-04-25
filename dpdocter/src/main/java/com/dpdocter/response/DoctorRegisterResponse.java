package com.dpdocter.response;

import com.dpdocter.collections.GenericCollection;

public class DoctorRegisterResponse extends GenericCollection {

	private String id;
	private String mobileNumber;
	private String countryCode;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

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

	@Override
	public String toString() {
		return "DoctorRegisterResponse [id=" + id + ", mobileNumber=" + mobileNumber + ", countryCode=" + countryCode
				+ "]";
	}

}
