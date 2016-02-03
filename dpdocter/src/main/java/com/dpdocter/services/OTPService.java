package com.dpdocter.services;

import java.util.Date;

import javax.ws.rs.core.UriInfo;

public interface OTPService {

    String otpGenerator(String doctorId, String locationId, String hospitalId, String patientId, UriInfo uriInfo);

    String otpGenerator(String mobileNumber);

    Boolean verifyOTP(String doctorId, String locationId, String hospitalId, String patientId, String otpNumber, UriInfo uriInfo);

    boolean verifyOTP(String mobileNumber, String otpNumber);

    Boolean checkOTPVerified(String doctorId, String locationId, String hospitalId, String patientId);

	boolean isNonVerifiedOTPValid(Date createdTime);

	boolean isOTPValid(Date createdTime);

}
