package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.Districts;
import com.dpdocter.beans.HealthIdRequest;
import com.dpdocter.beans.HealthIdResponse;
import com.dpdocter.beans.NDHMStates;
import com.dpdocter.beans.NdhmOauthResponse;
import com.dpdocter.beans.HealthIdSearch;
import com.dpdocter.beans.HealthIdSearchRequest;
import com.dpdocter.beans.MobileTokenRequest;
import com.dpdocter.beans.NdhmOauthResponse;
import com.dpdocter.request.CreateAadhaarRequest;
import com.dpdocter.request.CreateProfileRequest;

import common.util.web.Response;
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

	//auth
	Response<Object> sendAuthPassword(String healthId, String password);

	Response<Object> sendAuthWithMobile(String healthId);

	Response<Object> sendAuthWithMobileToken(MobileTokenRequest request);

	Response<Object> sendAuthInit(String healthId, String authMethod);
	
	Response<Object> confirmWithMobileOTP(String otp, String txnId);

	Response<Object> confirmWithAadhaarOtp(String otp, String txnId);
	//aadhar
	Response<Object> aadharGenerateOtp(String aadhaar);

	Response<Object> aadharGenerateMobileOtp(String mobile, String txnId);

	Response<Object> aadharVerifyOtp(String otp, String restrictions, String txnId);

	Response<Object> aadharVerifyMobileOtp(String otp, String txnId);

	Response<Object> createHealthIdWithAadhaarOtp(CreateAadhaarRequest request);

	Response<Object> resendAadhaarOtp(String txnId);

	//profile
	Response<Object> profileGetCard(String authToken);

	Response<Object> profileGetPngCard(String authToken);

	Response<Object> getProfileDetail(String authToken);

	Response<Object> createProfile(CreateProfileRequest request, String authToken);

	Response<Object> DeleteProfileDetail(String authToken);

}
