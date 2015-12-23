package com.dpdocter.services;

import javax.ws.rs.core.UriInfo;

import com.dpdocter.beans.LoginResponse;
import com.dpdocter.request.LoginRequest;

public interface LoginService {
    LoginResponse login(LoginRequest request, UriInfo uriInfo);
}
