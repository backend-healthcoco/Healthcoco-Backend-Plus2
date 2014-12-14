package com.dpdocter.services;

import com.dpdocter.beans.User;
import com.dpdocter.request.LoginRequest;

public interface LoginService {
	User login(LoginRequest request);
}
