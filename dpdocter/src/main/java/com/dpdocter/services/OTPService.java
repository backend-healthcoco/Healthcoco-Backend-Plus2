package com.dpdocter.services;

public interface OTPService {

    String otpGenerator(String doctorId, String locationId, String hospitalId, String patientId, String mobileNumber);

    String otpGenerator(String mobileNumber);

    Boolean verifyOTP(String doctorId, String locationId, String hospitalId, String patientId, String otpNumber);

    boolean verifyOTP(String mobileNumber, String otpNumber);

    Boolean checkOTPVerified(String doctorId, String locationId, String hospitalId, String patientId);

    boolean checkOTPVerified(String mobileNumber, String otpNumber);

}
