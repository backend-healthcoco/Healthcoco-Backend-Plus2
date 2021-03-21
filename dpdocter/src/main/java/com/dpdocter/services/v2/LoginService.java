package com.dpdocter.services.v2;

import com.dpdocter.beans.LoginResponse;
import com.dpdocter.request.LoginRequest;

public interface LoginService {
	LoginResponse login(LoginRequest request, Boolean isMobileApp, Boolean isNutritionist);

//	List<RegisteredPatientDetails> loginPatient(LoginPatientRequest request);
//
//	Boolean adminLogin(String mobileNumber);
//
//	Boolean isLocationAdmin(LoginRequest request);
//
//	public DoctorLoginPin getLoginPin(String doctorId);
//
//	public Boolean checkLoginPin(DoctorLoginPinRequest request);
//
//	public DoctorLoginPin AddEditLoginPin(DoctorLoginPin request);
//
//	public List<RegisteredPatientDetails> loginPatientByOtp(LoginPatientRequest request);

	//LoginResponse login(LoginRequest request, Boolean isMobileApp, Boolean isNutritionist, Boolean isAdminNutritionist);

}
