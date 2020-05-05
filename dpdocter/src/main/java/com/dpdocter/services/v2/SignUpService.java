package com.dpdocter.services.v2;

import com.dpdocter.beans.DoctorSignUp;
import com.dpdocter.beans.v2.DoctorSignupRequest;


public interface SignUpService {

	Boolean DoctorRegister(String mobileNumber);
	
	DoctorSignUp doctorSignUp(DoctorSignupRequest request);
	
	String verifyUser(String tokenId);
}
