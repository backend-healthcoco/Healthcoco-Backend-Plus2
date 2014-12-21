package com.dpdocter.services;

import com.dpdocter.beans.User;
import com.dpdocter.request.PatientRegistrationRequest;

public interface RegistrationService {
	User checkIfPatientExist(PatientRegistrationRequest request) ;
	User registerNewPatient(PatientRegistrationRequest request);
	boolean registerExistingPatient(PatientRegistrationRequest request,String patientId);
}
