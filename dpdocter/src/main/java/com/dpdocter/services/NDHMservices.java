package com.dpdocter.services;

import com.dpdocter.beans.NdhmOauthResponse;

public interface NDHMservices {

	//NdhmOauthResponse session();

	String generateOtp(String mobileNumber);

	String verifyOtp(String otp, String txnId);
}
