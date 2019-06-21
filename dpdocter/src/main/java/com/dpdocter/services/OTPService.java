package com.dpdocter.services;

import java.util.Date;

public interface OTPService {

	String otpGenerator(String doctorId, String locationId, String hospitalId, String patientId);

	Boolean otpGenerator(String mobileNumber, Boolean isPatientOTP);

	Boolean verifyOTP(String doctorId, String locationId, String hospitalId, String patientId, String otpNumber);

	boolean verifyOTP(String mobileNumber, String otpNumber);

	Boolean checkOTPVerified(String doctorId, String locationId, String hospitalId, String patientId);

	boolean isNonVerifiedOTPValid(Date createdTime);

	boolean isOTPValid(Date createdTime);

	Boolean checkOTPVerifiedForPatient(String mobileNumber, String otpNumber);

}
