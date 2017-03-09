package com.dpdocter.services;

import com.dpdocter.request.LoginPatientRequest;
import com.dpdocter.request.LoginRequest;
import com.dpdocter.response.LoginResponse;
import com.dpdocter.response.OAuth2TokenResponse;
import com.dpdocter.response.OauthRefreshTokenRequest;
import com.dpdocter.response.PatientLoginResponse;

public interface LoginService {

	LoginResponse login(LoginRequest request, Boolean isMobileApp);

	PatientLoginResponse loginPatient(LoginPatientRequest request);

	String refreshToken(OauthRefreshTokenRequest request);

	Boolean adminLogin(String mobileNumber);
}
