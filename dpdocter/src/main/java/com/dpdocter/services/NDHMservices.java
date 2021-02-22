package com.dpdocter.services;

import java.io.InputStream;
import java.util.List;

import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

import com.dpdocter.beans.AuthConfirmRequest;
import com.dpdocter.beans.CareContextDiscoverRequest;
import com.dpdocter.beans.CareContextRequest;
import com.dpdocter.beans.ConsentFetchRequest;
import com.dpdocter.beans.Districts;
import com.dpdocter.beans.FetchModesRequest;
import com.dpdocter.beans.HealthIdRequest;
import com.dpdocter.beans.HealthIdResponse;
import com.dpdocter.beans.NDHMStates;
import com.dpdocter.beans.NdhmOauthResponse;
import com.dpdocter.beans.NdhmOnPatientFindRequest;
import com.dpdocter.beans.NdhmOtp;
import com.dpdocter.beans.NdhmOtpStatus;
import com.dpdocter.beans.NdhmPatientRequest;
import com.dpdocter.beans.NdhmStatus;
import com.dpdocter.beans.NotifyHiuRequest;
import com.dpdocter.beans.NotifyPatientrequest;
import com.dpdocter.beans.NotifyRequest;
import com.dpdocter.beans.OnAuthConfirmRequest;
import com.dpdocter.beans.OnAuthInitRequest;
import com.dpdocter.beans.OnCareContext;
import com.dpdocter.beans.OnConsentFetchRequest;
import com.dpdocter.beans.OnConsentRequestStatus;
import com.dpdocter.beans.OnDiscoverRequest;
import com.dpdocter.beans.OnFetchModesRequest;
import com.dpdocter.beans.OnLinkConfirm;
import com.dpdocter.beans.OnLinkRequest;
import com.dpdocter.beans.OnNotifyRequest;
import com.dpdocter.beans.OnNotifySmsRequest;
import com.dpdocter.beans.OnPatientShare;
import com.dpdocter.beans.OnSharePatientrequest;
import com.dpdocter.beans.PatientShareProfile;
import com.dpdocter.beans.HealthIdSearch;
import com.dpdocter.beans.HealthIdSearchRequest;
import com.dpdocter.beans.HealthInfoNotify;
import com.dpdocter.beans.HiuDataRequest;
import com.dpdocter.beans.HiuDataResponse;
import com.dpdocter.beans.HiuOnNotify;
import com.dpdocter.beans.LinkConfirm;
import com.dpdocter.beans.LinkRequest;
import com.dpdocter.beans.MobileTokenRequest;
import com.dpdocter.beans.NdhmOauthResponse;
import com.dpdocter.request.ConsentOnInitRequest;
import com.dpdocter.request.CreateAadhaarRequest;
import com.dpdocter.request.CreateProfileRequest;
import com.dpdocter.request.DataFlowRequest;
import com.dpdocter.request.DataTransferRequest;
import com.dpdocter.request.GatewayConsentInitRequest;
import com.dpdocter.request.GatewayConsentStatusRequest;
import com.dpdocter.webservices.GateWayOnRequest;

import common.util.web.Response;
public interface NDHMservices {

	//NdhmOauthResponse session();

	NdhmOtp generateOtp(String mobileNumber);

	NdhmOtp verifyOtp(String otp, String txnId);

	NdhmOtpStatus resendOtp(String txnId);

	HealthIdResponse createHealthId(HealthIdRequest request);

	List<NDHMStates> getListforStates();

	List<Districts> getListforDistricts(String statecode);

	NdhmStatus existsByHealthId(String mobileNumber);

	HealthIdSearch searchByHealthId(String healthId);

	HealthIdSearch searchBymobileNumber(HealthIdSearchRequest request);

	//auth
	NdhmOtp sendAuthPassword(String healthId, String password);

	NdhmOtp sendAuthWithMobile(String healthid);

	NdhmOtp sendAuthWithMobileToken(MobileTokenRequest request);

	NdhmOtp sendAuthInit(String healthId, String authMethod);
	
	NdhmOtp confirmWithMobileOTP(String otp, String txnId);

	NdhmOtp confirmWithAadhaarOtp(String otp, String txnId);
	
	//aadhar
	NdhmOtp aadharGenerateOtp(String aadhaar);

	Response<Object> aadharGenerateMobileOtp(String mobile, String txnId);

	Response<Object> aadharVerifyOtp(String otp, String restrictions, String txnId);

	Response<Object> aadharVerifyMobileOtp(String otp, String txnId);

	Response<Object> createHealthIdWithAadhaarOtp(CreateAadhaarRequest request);

	Response<Object> resendAadhaarOtp(String txnId);

	//profile
	ResponseEntity<byte[]> profileGetCard(String authToken);

	Response<Object> profileGetPngCard(String authToken);

	Response<Object> getProfileDetail(String authToken);

	Response<Object> createProfile(CreateProfileRequest request, String authToken);

	Response<Object> DeleteProfileDetail(String authToken);
	
	Boolean fetchModes(FetchModesRequest request);

	Boolean onFetchModes(OnFetchModesRequest request);

	OnFetchModesRequest getFetchModes(String requestId);

	Boolean authInit(FetchModesRequest request);

	Boolean authConfirm(AuthConfirmRequest request);

	Boolean onAuthinit(OnAuthInitRequest request);

	OnAuthInitRequest getOnAuthInit(String requestId);

	Boolean onAuthConfirm(OnAuthConfirmRequest request);

	OnAuthConfirmRequest getOnAuthConfirm(String requestId);
	
	Boolean addCareContext(CareContextRequest request);
	
	Boolean onCareContext(OnCareContext request); 
	
	OnCareContext getCareContext(String requestId); 
	
	Boolean discover(CareContextDiscoverRequest  request);
	
	CareContextDiscoverRequest getCareContextDiscover(String requestId);
	
	Boolean onDiscover(OnDiscoverRequest request);
	
	Boolean linkInit(LinkRequest request);
	
	Boolean onLinkInit(OnLinkRequest request);
	
	LinkRequest getLinkInit(String requestId);
	
	Boolean linkConfirm(LinkConfirm request);
	
	Boolean onLinkConfirm(OnLinkConfirm request);

	LinkConfirm getLinkConfim(String requestId);
	
	Boolean onDataFlowRequest(DataFlowRequest request);

	Boolean onGateWayOnRequest(GateWayOnRequest request);
	
	DataFlowRequest getDataFlow(String transactionId);

	

	Boolean onConsentRequestOnInitApi(ConsentOnInitRequest request);

	Boolean onGatewayConsentRequestInitApi(GatewayConsentInitRequest request);

	Boolean onGatewayConsentRequestStatusApi(GatewayConsentStatusRequest request);
	
	ConsentOnInitRequest getConsentInitRequest(String requestId);
	
	Boolean ndhmNotify(NotifyRequest request);
	
	Boolean onNotify(OnNotifyRequest request);
	
	NotifyRequest getNotify(String requestId);
	
	Boolean healthInformationNotify(HealthInfoNotify request);
	
	Boolean onConsentRequestStatus(OnConsentRequestStatus request);
	
	OnConsentRequestStatus getConsentStatus(String requestId);

	Boolean onDataTransfer(DataTransferRequest request);
	
	Boolean findPatient(NdhmPatientRequest request);
	
	Boolean onFindPatient(NdhmOnPatientFindRequest request);

	NdhmOnPatientFindRequest getNdhmPatient(String requestId);
	
	Boolean notifyHiu(NotifyHiuRequest request);
	
	
	
	NotifyHiuRequest getHiuNotify(String requestId);

	Boolean onNotifyHiu(HiuOnNotify request);
	
	Boolean consentFetch(ConsentFetchRequest request);
	
	Boolean onConsentFetch(OnConsentFetchRequest request);

	OnConsentFetchRequest getConsentFetch(String requestId);
	
	Boolean hiuDataRequest(HiuDataRequest request);

	Boolean onHiuDatarequest(GateWayOnRequest request);

	GateWayOnRequest getHiuDataRequest(String requestId,String doctorId,String healthId);

	Boolean onHiuDataTransferApi(DataTransferRequest request);
	
	HiuDataResponse getHiuData(String transactionId);

	Boolean shareProfile(PatientShareProfile request);

	Boolean onShareProfile(OnSharePatientrequest request);

	PatientShareProfile getPatientShare(String requestId);

	Boolean notifyPatientSms(NotifyPatientrequest request);

	
	OnNotifySmsRequest getNotifySms(String requestId);

	Boolean onNotifySms(OnNotifySmsRequest request);


	

}
