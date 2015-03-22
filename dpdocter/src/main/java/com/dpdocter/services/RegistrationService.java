package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.Referrence;
import com.dpdocter.beans.RegisteredPatientDetails;
import com.dpdocter.beans.User;
import com.dpdocter.request.PatientRegistrationRequest;

public interface RegistrationService {
	User checkIfPatientExist(PatientRegistrationRequest request) ;
	RegisteredPatientDetails registerNewPatient(PatientRegistrationRequest request);
	RegisteredPatientDetails registerExistingPatient(PatientRegistrationRequest request);
	
	List<User> getUsersByPhoneNumber(String phoneNumber,String locationId,String hospitalId);
	
	RegisteredPatientDetails getPatientProfileByUserId(String userId,String doctorId,String locationId,String hospitalId);
	
	Referrence addEditReferrence(Referrence referrence);
	void deleteReferrence(String referrenceId);
	List<Referrence> getReferrences(String doctorId,String locationId,String hospitalId);
	
	
}
