package com.dpdocter.services;

import com.dpdocter.beans.User;
import com.dpdocter.request.DoctorSignupRequest;
import com.dpdocter.request.PatientSignUpRequest;

/**
 * @author veeraj
 */
public interface SignUpService {
	Boolean activateUser(String userId);
	User doctorSignUp(DoctorSignupRequest request);
	User patientSignUp(PatientSignUpRequest request);
	
}
