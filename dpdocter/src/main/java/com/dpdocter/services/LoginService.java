package com.dpdocter.services;

import com.dpdocter.beans.LoginResponse;
import com.dpdocter.beans.User;
import com.dpdocter.request.LoginPatientRequest;
import com.dpdocter.request.LoginRequest;

public interface LoginService {
    LoginResponse login(LoginRequest request);

    LoginResponse loginPatient(LoginPatientRequest request);

    User adminLogin(LoginPatientRequest request);
}
