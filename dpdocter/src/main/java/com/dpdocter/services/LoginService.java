package com.dpdocter.services;

import com.dpdocter.beans.LoginResponse;
import com.dpdocter.request.LoginRequest;

public interface LoginService {
    LoginResponse login(LoginRequest request);

    Boolean verifyUser(String userId);

    String otpGenerator(String mobileNumber);
}
