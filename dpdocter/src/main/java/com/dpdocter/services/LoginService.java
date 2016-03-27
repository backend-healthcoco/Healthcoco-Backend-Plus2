package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.LoginResponse;
import com.dpdocter.beans.User;
import com.dpdocter.request.LoginPatientRequest;
import com.dpdocter.request.LoginRequest;

public interface LoginService {
    LoginResponse login(LoginRequest request);

    List<User> loginPatient(LoginPatientRequest request);

    User adminLogin(LoginPatientRequest request);
}
