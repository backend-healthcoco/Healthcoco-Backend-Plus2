package com.dpdocter.services.v2;

import com.dpdocter.beans.LoginResponse;
import com.dpdocter.request.LoginRequest;

public interface LoginService {
	LoginResponse login(LoginRequest request, Boolean isMobileApp, Boolean isNutritionist);
}
