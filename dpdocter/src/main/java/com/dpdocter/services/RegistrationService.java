package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.RegisteredPatientDetails;
import com.dpdocter.beans.User;
import com.dpdocter.request.PatientRegistrationRequest;

public interface RegistrationService {
	User checkIfPatientExist(PatientRegistrationRequest request) ;
	RegisteredPatientDetails registerNewPatient(PatientRegistrationRequest request);
	RegisteredPatientDetails registerExistingPatient(PatientRegistrationRequest request);
	
	List<User> getUsersByPhoneNumber(String phoneNumber);
	
	RegisteredPatientDetails getPatientProfileByUserId(String userId,String doctorId);
}
