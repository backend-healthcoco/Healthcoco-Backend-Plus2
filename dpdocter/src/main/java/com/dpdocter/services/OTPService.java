package com.dpdocter.services;

public interface OTPService {

	String otpGenerator(String doctorId, String locationId, String hospitalId, String patientId, String mobileNumber);

	Boolean verifyOTP(String doctorId, String locationId, String hospitalId, String patientId, String otpNumber);
	
	Boolean checkOTPVerified(String doctorId, String locationId, String hospitalId, String patientId);

}
