package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.DoctorLoginPin;
import com.dpdocter.beans.LoginResponse;
import com.dpdocter.beans.RegisteredPatientDetails;
import com.dpdocter.request.LoginPatientRequest;
import com.dpdocter.request.LoginRequest;
import com.dpdocter.response.DoctorLoginPinRequest;

public interface LoginService {
	LoginResponse login(LoginRequest request, Boolean isMobileApp, Boolean isNutritionist);

	List<RegisteredPatientDetails> loginPatient(LoginPatientRequest request);

	Boolean adminLogin(String mobileNumber);

	Boolean isLocationAdmin(LoginRequest request);

	public DoctorLoginPin getLoginPin(String doctorId);

	public Boolean checkLoginPin(DoctorLoginPinRequest request);

	public DoctorLoginPin AddEditLoginPin(DoctorLoginPin request);

	public List<RegisteredPatientDetails> loginPatientByOtp(LoginPatientRequest request);

}
