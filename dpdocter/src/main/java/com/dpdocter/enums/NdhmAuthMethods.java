package com.dpdocter.enums;

public enum NdhmAuthMethods {

	AADHAAR_OTP("AADHAAR_OTP"), MOBILE_OTP("MOBILE_OTP"), PASSWORD("PASSWORD"), DEMOGRAPHICS("DEMOGRAPHICS"), AADHAAR_BIO("AADHAAR_BIO");
	
	private String type;

	private NdhmAuthMethods(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}
	
	
}
