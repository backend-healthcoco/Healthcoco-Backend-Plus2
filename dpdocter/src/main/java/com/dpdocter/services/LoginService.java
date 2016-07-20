package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.LoginResponse;
import com.dpdocter.beans.RegisteredPatientDetails;
import com.dpdocter.request.LoginPatientRequest;
import com.dpdocter.request.LoginRequest;

public interface LoginService {
    LoginResponse login(LoginRequest request);

    List<RegisteredPatientDetails> loginPatient(LoginPatientRequest request);

	Boolean adminLogin(String mobileNumber);
}
