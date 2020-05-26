package com.dpdocter.services.v2;


import com.dpdocter.beans.DoctorSignUp;
import com.dpdocter.beans.v2.DoctorSignupRequest;
import com.dpdocter.request.DoctorOtpRequest;
import com.dpdocter.response.DoctorRegisterResponse;


public interface SignUpService {

	DoctorRegisterResponse DoctorRegister(DoctorOtpRequest request);
	
	DoctorSignUp doctorSignUp(DoctorSignupRequest request);
	
	String verifyUser(String tokenId);
	
	Boolean resendVerificationEmail(String emailaddress);
}
