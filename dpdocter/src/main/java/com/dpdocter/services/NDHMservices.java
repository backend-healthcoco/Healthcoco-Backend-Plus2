package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.Districts;
import com.dpdocter.beans.HealthIdRequest;
import com.dpdocter.beans.HealthIdResponse;
import com.dpdocter.beans.NDHMStates;
import com.dpdocter.beans.NdhmOauthResponse;
import com.dpdocter.beans.HealthIdSearch;
import com.dpdocter.beans.HealthIdSearchRequest;

public interface NDHMservices {

	//NdhmOauthResponse session();

	String generateOtp(String mobileNumber);

	String verifyOtp(String otp, String txnId);

	Boolean resendOtp(String txnId);

	HealthIdResponse createHealthId(HealthIdRequest request);

	List<NDHMStates> getListforStates();

	List<Districts> getListforDistricts(String statecode);

	String existsByHealthId(String mobileNumber);

	HealthIdSearch searchByHealthId(String healthId);

	HealthIdSearch searchBymobileNumber(HealthIdSearchRequest request);
}
