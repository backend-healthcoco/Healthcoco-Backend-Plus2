package com.dpdocter.services.v2;

import java.util.List;

import com.dpdocter.beans.DoctorLoginPin;
import com.dpdocter.beans.LoginResponse;
import com.dpdocter.beans.RegisteredPatientDetails;
import com.dpdocter.request.LoginPatientRequest;
import com.dpdocter.request.LoginRequest;
import com.dpdocter.response.DoctorLoginPinRequest;

public interface LoginService {
	LoginResponse login(LoginRequest request, Boolean isMobileApp, Boolean isNutritionist);
}
