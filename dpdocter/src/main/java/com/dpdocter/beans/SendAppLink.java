package com.dpdocter.beans;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.dpdocter.enums.AppType;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class SendAppLink {

	private String countryCode;

	private String mobileNumber;

	private String emailAddress;

	private AppType appType;

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public AppType getAppType() {
		return appType;
	}

	public void setAppType(AppType appType) {
		this.appType = appType;
	}

	@Override
	public String toString() {
		return "SendAppLink [mobileNumber=" + mobileNumber + ", emailAddress=" + emailAddress + ", appType=" + appType
				+ "]";
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
}
