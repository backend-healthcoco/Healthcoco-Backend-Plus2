package com.dpdocter.webservices;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import com.dpdocter.beans.AuthConfirmRequest;
import com.dpdocter.beans.CareContextDiscoverRequest;
import com.dpdocter.beans.CareContextRequest;
import com.dpdocter.beans.ConsentFetchRequest;
import com.dpdocter.beans.Districts;
import com.dpdocter.beans.FetchModesRequest;
import com.dpdocter.beans.HealthIdRequest;
import com.dpdocter.beans.HealthIdResponse;
import com.dpdocter.beans.HealthIdSearch;
import com.dpdocter.beans.HealthIdSearchRequest;
import com.dpdocter.beans.HealthInfoNotify;
import com.dpdocter.beans.HiuDataRequest;
import com.dpdocter.beans.HiuDataResponse;
import com.dpdocter.beans.HiuOnNotify;
import com.dpdocter.beans.LinkConfirm;
import com.dpdocter.beans.LinkRequest;
import com.dpdocter.beans.MobileTokenRequest;
import com.dpdocter.beans.NDHMStates;
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
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.request.ConsentOnInitRequest;
import com.dpdocter.request.CreateAadhaarRequest;
import com.dpdocter.request.CreateProfileRequest;
import com.dpdocter.request.DataFlowRequest;
import com.dpdocter.request.DataTransferRequest;
import com.dpdocter.request.GatewayConsentInitRequest;
import com.dpdocter.request.GatewayConsentStatusRequest;
import com.dpdocter.services.NDHMservices;

import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Component
@Path(value = PathProxy.NDHM_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.NDHM_BASE_URL, description = "Endpoint for ndhm")
public class NdhmApi {

	private static Logger logger = LogManager.getLogger(NdhmApi.class.getName());

	@Autowired
	private NDHMservices ndhmService;

//	@Path(value = PathProxy.NdhmUrls.GET_SESSION)
//	@GET
//	@ApiOperation(value = PathProxy.NdhmUrls.GET_SESSION, notes = PathProxy.NdhmUrls.GET_SESSION)
//	public Response<NdhmOauthResponse> getSe() {
//		
//		NdhmOauthResponse ndhmOauth = ndhmService.session();
//		Response<NdhmOauthResponse> response = new Response<NdhmOauthResponse>();
//		response.setData(ndhmOauth);
//		return response;
//	}

	@Path(value = PathProxy.NdhmUrls.GET_GENERATE_MOBILE_OTP)
	@GET
	@ApiOperation(value = PathProxy.NdhmUrls.GET_GENERATE_MOBILE_OTP, notes = PathProxy.NdhmUrls.GET_GENERATE_MOBILE_OTP)
	public Response<NdhmOtp> generateMobileOtp(@QueryParam("mobileNumber") String mobileNumber) {

		NdhmOtp mobile = ndhmService.generateOtp(mobileNumber);

		Response<NdhmOtp> response = new Response<NdhmOtp>();
		response.setData(mobile);
		return response;
	}

	@Path(value = PathProxy.NdhmUrls.GET_VERIFY_MOBILE_OTP)
	@GET
	@ApiOperation(value = PathProxy.NdhmUrls.GET_VERIFY_MOBILE_OTP, notes = PathProxy.NdhmUrls.GET_VERIFY_MOBILE_OTP)
	public Response<NdhmOtp> verifyMobileOtp(@QueryParam("otp") String otp, @QueryParam("txnId") String txnId) {

		NdhmOtp mobile = ndhmService.verifyOtp(otp, txnId);

		Response<NdhmOtp> response = new Response<NdhmOtp>();
		response.setData(mobile);
		return response;

	}

	/// authorization

	@Path(value = PathProxy.NdhmUrls.GET_AUTH_INIT)
	@GET
	@ApiOperation(value = PathProxy.NdhmUrls.GET_AUTH_INIT, notes = PathProxy.NdhmUrls.GET_AUTH_INIT)
	public Response<NdhmOtp> sendAuthPassword(@QueryParam(value = "healthId") String healthId,
			@DefaultValue("AADHAAR_OTP") @QueryParam(value = "authMethod") String authMethod) {

		if (healthId == null) {
			throw new BusinessException(ServiceError.InvalidInput, " healthId Required");
		}
		Response<NdhmOtp> response = new Response<NdhmOtp>();
		response.setData(ndhmService.sendAuthInit(healthId, authMethod));

		return response;
	}

	@Path(value = PathProxy.NdhmUrls.GET_AUTH_WITH_MOBILE)
	@GET
	@ApiOperation(value = PathProxy.NdhmUrls.GET_AUTH_WITH_MOBILE, notes = PathProxy.NdhmUrls.GET_AUTH_WITH_MOBILE)
	public Response<NdhmOtp> sendAuthWithMobile(@QueryParam(value = "healthid") String healthid) {

		if (healthid == null) {
			throw new BusinessException(ServiceError.InvalidInput, " healthId Required");
		}
		Response<NdhmOtp> response = new Response<NdhmOtp>();
		response.setData(ndhmService.sendAuthWithMobile(healthid));

		return response;
	}

	@Path(value = PathProxy.NdhmUrls.GET_AUTH_WITH_MOBILE_TOKEN)
	@GET
	@ApiOperation(value = PathProxy.NdhmUrls.GET_AUTH_WITH_MOBILE_TOKEN, notes = PathProxy.NdhmUrls.GET_AUTH_WITH_MOBILE_TOKEN)
	public Response<NdhmOtp> sendAuthWithMobileToken(@RequestBody MobileTokenRequest request) {

		if (request == null) {
			throw new BusinessException(ServiceError.InvalidInput, " aadhaar Required");
		}
		Response<NdhmOtp> response = new Response<NdhmOtp>();
		response.setData(ndhmService.sendAuthWithMobileToken(request));

		return response;
	}

	@Path(value = PathProxy.NdhmUrls.CONFIRM_AUTH_WITH_MOBILE_OTP)
	@GET
	@ApiOperation(value = PathProxy.NdhmUrls.CONFIRM_AUTH_WITH_MOBILE_OTP, notes = PathProxy.NdhmUrls.CONFIRM_AUTH_WITH_MOBILE_OTP)
	public Response<NdhmOtp> confirmWithMobileOTP(@QueryParam(value = "otp") String otp,
			@QueryParam(value = "txnId") String txnId) {

		if (otp == null) {
			throw new BusinessException(ServiceError.InvalidInput, " otp Required");
		}
		Response<NdhmOtp> response = new Response<NdhmOtp>();
		response.setData(ndhmService.confirmWithMobileOTP(otp, txnId));

		return response;
	}

	@Path(value = PathProxy.NdhmUrls.CONFIRM_AUTH_WITH_AADHAAR_OTP)
	@GET
	@ApiOperation(value = PathProxy.NdhmUrls.CONFIRM_AUTH_WITH_AADHAAR_OTP, notes = PathProxy.NdhmUrls.CONFIRM_AUTH_WITH_AADHAAR_OTP)
	public Response<NdhmOtp> confirmWithAadhaarOtp(@QueryParam(value = "otp") String otp,
			@QueryParam(value = "txnId") String txnId) {

		if (otp == null) {
			throw new BusinessException(ServiceError.InvalidInput, " otp Required");
		}
		Response<NdhmOtp> response = new Response<NdhmOtp>();
		response.setData(ndhmService.confirmWithAadhaarOtp(otp, txnId));

		return response;
	}

	// aadhar API

	@Path(value = PathProxy.NdhmUrls.GET_AADHAR_GENERATE_OTP)
	@GET
	@ApiOperation(value = PathProxy.NdhmUrls.GET_AADHAR_GENERATE_OTP, notes = PathProxy.NdhmUrls.GET_AADHAR_GENERATE_OTP)
	public Response<NdhmOtp> AadharGenerateOtp(@QueryParam(value = "aadhaar") String aadhaar) {

		if (aadhaar == null) {
			throw new BusinessException(ServiceError.InvalidInput, " aadhaar Required");
		}
		Response<NdhmOtp> response = new Response<NdhmOtp>();
		response.setData(ndhmService.aadharGenerateOtp(aadhaar));

		return response;
	}

	@Path(value = PathProxy.NdhmUrls.GET_AADHAR_GENERATE_MOBILE_OTP)
	@GET
	@ApiOperation(value = PathProxy.NdhmUrls.GET_AADHAR_GENERATE_MOBILE_OTP, notes = PathProxy.NdhmUrls.GET_AADHAR_GENERATE_MOBILE_OTP)
	public Response<Object> AadharGenerateMobileOtp(@QueryParam(value = "mobile") String mobile,
			@QueryParam(value = "txnId") String txnId) {

		if (mobile == null) {
			throw new BusinessException(ServiceError.InvalidInput, " mobile Required");
		}
		Response<Object> response = ndhmService.aadharGenerateMobileOtp(mobile, txnId);

		return response;
	}

	@Path(value = PathProxy.NdhmUrls.GET_AADHAR_VERIFY_OTP)
	@GET
	@ApiOperation(value = PathProxy.NdhmUrls.GET_AADHAR_VERIFY_OTP, notes = PathProxy.NdhmUrls.GET_AADHAR_VERIFY_OTP)
	public Response<Object> AadharVerifyOtp(@QueryParam(value = "otp") String otp,
			@QueryParam(value = "restrictions") String restrictions, @QueryParam(value = "txnId") String txnId) {

		if (txnId == null) {
			throw new BusinessException(ServiceError.InvalidInput, " txnId Required");
		}
		Response<Object> response = ndhmService.aadharVerifyOtp(otp, restrictions, txnId);

		return response;
	}

	@Path(value = PathProxy.NdhmUrls.GET_AADHAR_VERIFY_MOBILE_OTP)
	@GET
	@ApiOperation(value = PathProxy.NdhmUrls.GET_AADHAR_VERIFY_MOBILE_OTP, notes = PathProxy.NdhmUrls.GET_AADHAR_VERIFY_MOBILE_OTP)
	public Response<Object> AadharVerifyMobileOtp(@QueryParam(value = "otp") String otp,
			@QueryParam(value = "txnId") String txnId) {

		if (txnId == null) {
			throw new BusinessException(ServiceError.InvalidInput, " txnId Required");
		}
		Response<Object> response = ndhmService.aadharVerifyMobileOtp(otp, txnId);

		return response;
	}

	@Path(value = PathProxy.NdhmUrls.CREATE_HEALTHID_AADHAAR_OTP)
	@POST
	@ApiOperation(value = PathProxy.NdhmUrls.CREATE_HEALTHID_AADHAAR_OTP, notes = PathProxy.NdhmUrls.CREATE_HEALTHID_AADHAAR_OTP)
	public Response<Object> CreateHealthIdWithAadhaarOtp(@RequestBody CreateAadhaarRequest request) {

		if (request == null) {
			throw new BusinessException(ServiceError.InvalidInput, " request Required");
		}
		Response<Object> response = ndhmService.createHealthIdWithAadhaarOtp(request);

		return response;
	}

	@Path(value = PathProxy.NdhmUrls.GET_RESEND_MOBILE_OTP)
	@GET
	@ApiOperation(value = PathProxy.NdhmUrls.GET_RESEND_MOBILE_OTP, notes = PathProxy.NdhmUrls.GET_RESEND_MOBILE_OTP)
	public Response<NdhmOtpStatus> resendMobileOtp(@QueryParam("txnId") String txnId) {

		NdhmOtpStatus mobile = ndhmService.resendOtp(txnId);
		Response<NdhmOtpStatus> response = new Response<NdhmOtpStatus>();
		response.setData(mobile);
		return response;
	}

	@Path(value = PathProxy.NdhmUrls.CREATE_HEALTH_ID)
	@POST
	@ApiOperation(value = PathProxy.NdhmUrls.CREATE_HEALTH_ID, notes = PathProxy.NdhmUrls.CREATE_HEALTH_ID)
	public Response<HealthIdResponse> generateHealthId(@RequestBody HealthIdRequest request) {

		HealthIdResponse mobile = ndhmService.createHealthId(request);
		Response<HealthIdResponse> response = new Response<HealthIdResponse>();
		response.setData(mobile);
		return response;
	}
	
	
	

	@Path(value = PathProxy.NdhmUrls.GET_LIST_STATES)
	@GET
	@ApiOperation(value = PathProxy.NdhmUrls.GET_LIST_STATES, notes = PathProxy.NdhmUrls.GET_LIST_STATES)
	public Response<NDHMStates> getlistOfStates() {

		List<NDHMStates> mobile = ndhmService.getListforStates();
		Response<NDHMStates> response = new Response<NDHMStates>();
		response.setDataList(mobile);
		return response;
	}

	@Path(value = PathProxy.NdhmUrls.GET_LIST_DISTRICTS)
	@GET
	@ApiOperation(value = PathProxy.NdhmUrls.GET_LIST_DISTRICTS, notes = PathProxy.NdhmUrls.GET_LIST_DISTRICTS)
	public Response<Districts> getlistOfStates(@QueryParam(value = "stateCode") String statecode) {

		List<Districts> mobile = ndhmService.getListforDistricts(statecode);
		Response<Districts> response = new Response<Districts>();
		response.setDataList(mobile);
		return response;
	}

	@Path(value = PathProxy.NdhmUrls.GET_SEARCH_BY_HEALTH_ID)
	@GET
	@ApiOperation(value = PathProxy.NdhmUrls.GET_SEARCH_BY_HEALTH_ID, notes = PathProxy.NdhmUrls.GET_SEARCH_BY_HEALTH_ID)
	public Response<HealthIdSearch> searchByMobileNumber(@QueryParam(value = "healthId") String healthId) {

		HealthIdSearch mobile = ndhmService.searchByHealthId(healthId);
		Response<HealthIdSearch> response = new Response<HealthIdSearch>();
		response.setData(mobile);
		return response;
	}

	@Path(value = PathProxy.NdhmUrls.GET_EXISTS_BY_HEALTH_ID)
	@GET
	@ApiOperation(value = PathProxy.NdhmUrls.GET_EXISTS_BY_HEALTH_ID, notes = PathProxy.NdhmUrls.GET_EXISTS_BY_HEALTH_ID)
	public Response<NdhmStatus> existsByHealthId(@QueryParam(value = "healthId") String healthId) {

		NdhmStatus mobile = ndhmService.existsByHealthId(healthId);
		Response<NdhmStatus> response = new Response<NdhmStatus>();
		response.setData(mobile);
		return response;
	}

	@Path(value = PathProxy.NdhmUrls.GET_SEARCH_BY_MOBILE_NUMBER)
	@POST
	@ApiOperation(value = PathProxy.NdhmUrls.GET_SEARCH_BY_MOBILE_NUMBER, notes = PathProxy.NdhmUrls.GET_SEARCH_BY_MOBILE_NUMBER)
	public Response<HealthIdSearch> searchByMobileNumber(@RequestBody HealthIdSearchRequest request) {

		HealthIdSearch mobile = ndhmService.searchBymobileNumber(request);
		Response<HealthIdSearch> response = new Response<HealthIdSearch>();
		response.setData(mobile);
		return response;
	}

	@Path(value = PathProxy.NdhmUrls.RESENT_AADHAAR_OTP)
	@GET
	@ApiOperation(value = PathProxy.NdhmUrls.RESENT_AADHAAR_OTP, notes = PathProxy.NdhmUrls.RESENT_AADHAAR_OTP)
	public Response<Object> ResendAadhaarOtp(@QueryParam(value = "txnId") String txnId) {

		if (txnId == null) {
			throw new BusinessException(ServiceError.InvalidInput, " txnId Required");
		}
		Response<Object> response = ndhmService.resendAadhaarOtp(txnId);

		return response;
	}

	// profile API
//@RequestMapping(method = RequestMethod.GET, produces = "application/pdf")
	@Path(value = PathProxy.NdhmUrls.GET_PROFILE_CARD)
	@GET
//	@Produces("application/pdf")
//	@RequestMapping(value = PathProxy.NdhmUrls.GET_PROFILE_CARD, method = RequestMethod.GET)
	@ApiOperation(value = PathProxy.NdhmUrls.GET_PROFILE_CARD, notes = PathProxy.NdhmUrls.GET_PROFILE_CARD)
	public ResponseEntity<byte[]> ProfileGetCard(@QueryParam(value = "authToken") String authToken, HttpServletResponse response) throws IOException {

		if (authToken == null) {
			throw new BusinessException(ServiceError.InvalidInput, " authToken Required");
		}

		return ndhmService.profileGetCard(authToken);
	}

	@Path(value = PathProxy.NdhmUrls.GET_PROFILE_PNGCARD)
	@GET
	@ApiOperation(value = PathProxy.NdhmUrls.GET_PROFILE_PNGCARD, notes = PathProxy.NdhmUrls.GET_PROFILE_PNGCARD)
	public Response<Object> ProfileGetPngCard(@QueryParam(value = "authToken") String authToken) {

		if (authToken == null) {
			throw new BusinessException(ServiceError.InvalidInput, " authToken Required");
		}
		Response<Object> response = ndhmService.profileGetPngCard(authToken);

		return response;
	}

	@Path(value = PathProxy.NdhmUrls.GET_PROFILE)
	@GET
	@ApiOperation(value = PathProxy.NdhmUrls.GET_PROFILE, notes = PathProxy.NdhmUrls.GET_PROFILE)
	public Response<Object> GetProfileDetail(@QueryParam(value = "authToken") String authToken) {

		if (authToken == null) {
			throw new BusinessException(ServiceError.InvalidInput, " authToken Required");
		}
		Response<Object> response = ndhmService.getProfileDetail(authToken);

		return response;
	}

	@Path(value = PathProxy.NdhmUrls.CREATE_PROFILE)
	@POST
	@ApiOperation(value = PathProxy.NdhmUrls.CREATE_PROFILE, notes = PathProxy.NdhmUrls.CREATE_PROFILE)
	public Response<Object> CreateProfile(@RequestBody CreateProfileRequest request,
			@QueryParam(value = "authToken") String authToken) {

		if (request == null) {
			throw new BusinessException(ServiceError.InvalidInput, " request Required");
		}
		Response<Object> response = ndhmService.createProfile(request, authToken);

		return response;
	}

	@Path(value = PathProxy.NdhmUrls.DELETE_PROFILE)
	@DELETE
	@ApiOperation(value = PathProxy.NdhmUrls.DELETE_PROFILE, notes = PathProxy.NdhmUrls.DELETE_PROFILE)
	public Response<Object> DeleteProfileDetail(@QueryParam(value = "authToken") String authToken) {

		if (authToken == null) {
			throw new BusinessException(ServiceError.InvalidInput, " authToken Required");
		}
		Response<Object> response = ndhmService.DeleteProfileDetail(authToken);

		return response;
	}
	
	@Path(value = PathProxy.NdhmUrls.FETCH_MODES)
	@POST
	@ApiOperation(value = PathProxy.NdhmUrls.FETCH_MODES, notes = PathProxy.NdhmUrls.FETCH_MODES)
	public Response<Boolean> fetchModes(@RequestBody FetchModesRequest request) {

		Boolean mobile = ndhmService.fetchModes(request);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(mobile);
		return response;
	}
	
	@Path(value = PathProxy.NdhmUrls.GET_FETCH_MODES)
	@GET
	@ApiOperation(value = PathProxy.NdhmUrls.GET_FETCH_MODES, notes = PathProxy.NdhmUrls.GET_FETCH_MODES)
	public Response<OnFetchModesRequest> getfetchModes(@QueryParam ("requestId")String requestId) {

		OnFetchModesRequest mobile = ndhmService.getFetchModes(requestId);
		Response<OnFetchModesRequest> response = new Response<OnFetchModesRequest>();
		response.setData(mobile);
		return response;
	}

	@Path(value = PathProxy.NdhmUrls.AUTH_INIT)
	@POST
	@ApiOperation(value = PathProxy.NdhmUrls.AUTH_INIT, notes = PathProxy.NdhmUrls.AUTH_INIT)
	public Response<Boolean> authInit(@RequestBody FetchModesRequest request) {

		Boolean mobile = ndhmService.authInit(request);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(mobile);
		return response;
	}
	
	@Path(value = PathProxy.NdhmUrls.GET_AUTH_INIT_HIP)
	@GET
	@ApiOperation(value = PathProxy.NdhmUrls.GET_AUTH_INIT_HIP, notes = PathProxy.NdhmUrls.GET_AUTH_INIT_HIP)
	public Response<OnAuthInitRequest> getAuthInit(@QueryParam ("requestId")String requestId) {

		OnAuthInitRequest mobile = ndhmService.getOnAuthInit(requestId);
		Response<OnAuthInitRequest> response = new Response<OnAuthInitRequest>();
		response.setData(mobile);
		return response;
	}

	@Path(value = PathProxy.NdhmUrls.AUTH_CONFIRM)
	@POST
	@ApiOperation(value = PathProxy.NdhmUrls.AUTH_CONFIRM, notes = PathProxy.NdhmUrls.AUTH_CONFIRM)
	public Response<Boolean> authConfirm(@RequestBody AuthConfirmRequest request) {

		Boolean mobile = ndhmService.authConfirm(request);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(mobile);
		return response;
	}
	
	@Path(value = PathProxy.NdhmUrls.GET_AUTH_CONFIRM_HIP)
	@GET
	@ApiOperation(value = PathProxy.NdhmUrls.GET_AUTH_CONFIRM_HIP, notes = PathProxy.NdhmUrls.GET_AUTH_CONFIRM_HIP)
	public Response<OnAuthConfirmRequest> getAuthConfirm(@QueryParam ("requestId")String requestId) {

		OnAuthConfirmRequest mobile = ndhmService.getOnAuthConfirm(requestId);
		Response<OnAuthConfirmRequest> response = new Response<OnAuthConfirmRequest>();
		response.setData(mobile);
		return response;
	}
	
	@Path(value = PathProxy.NdhmUrls.ADD_CARE_CONTEXT)
	@POST
	@ApiOperation(value = PathProxy.NdhmUrls.ADD_CARE_CONTEXT, notes = PathProxy.NdhmUrls.ADD_CARE_CONTEXT)
	public Response<Boolean> addContext(@RequestBody CareContextRequest request) {

		Boolean mobile = ndhmService.addCareContext(request);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(mobile);
		return response;
	}
	
	@Path(value = PathProxy.NdhmUrls.GET_CARE_CONTEXT)
	@GET
	@ApiOperation(value = PathProxy.NdhmUrls.GET_CARE_CONTEXT, notes = PathProxy.NdhmUrls.GET_CARE_CONTEXT)
	public Response<OnCareContext> getCareContext(@QueryParam("requestId")String requestId) {

		OnCareContext mobile = ndhmService.getCareContext(requestId);
		Response<OnCareContext> response = new Response<OnCareContext>();
		response.setData(mobile);
		return response;
	}
	
	
	@Path(value = PathProxy.NdhmUrls.ON_DISCOVER)
	@POST
	@ApiOperation(value = PathProxy.NdhmUrls.ON_DISCOVER, notes = PathProxy.NdhmUrls.ON_DISCOVER)
	public Response<Boolean> onDiscover(@RequestBody OnDiscoverRequest request) {

		Boolean mobile = ndhmService.onDiscover(request);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(mobile);
		return response;
	}
	
	@Path(value = PathProxy.NdhmUrls.GET_DISCOVER)
	@GET
	@ApiOperation(value = PathProxy.NdhmUrls.GET_DISCOVER, notes = PathProxy.NdhmUrls.GET_DISCOVER)
	public Response<CareContextDiscoverRequest> getDiscover(@QueryParam("requestId")String requestId) {

		CareContextDiscoverRequest mobile = ndhmService.getCareContextDiscover(requestId);
		Response<CareContextDiscoverRequest> response = new Response<CareContextDiscoverRequest>();
		response.setData(mobile);
		return response;
	}
	

	@Path(value = PathProxy.NdhmUrls.ON_LINK_INIT)
	@POST
	@ApiOperation(value = PathProxy.NdhmUrls.ON_LINK_INIT, notes = PathProxy.NdhmUrls.ON_LINK_INIT)
	public Response<Boolean> onLinkInit(@RequestBody OnLinkRequest request) {

		Boolean mobile = ndhmService.onLinkInit(request);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(mobile);
		return response;
	}
	
	@Path(value = PathProxy.NdhmUrls.GET_LINK_INIT)
	@GET
	@ApiOperation(value = PathProxy.NdhmUrls.GET_LINK_INIT, notes = PathProxy.NdhmUrls.GET_LINK_INIT)
	public Response<LinkRequest> getLinkInit(@QueryParam("requestId")String requestId) {

		LinkRequest mobile = ndhmService.getLinkInit(requestId);
		Response<LinkRequest> response = new Response<LinkRequest>();
		response.setData(mobile);
		return response;
	}
	
	/**
	 * API called by CM to request Health information from HIP against a validated consent artefact
	 * Data Flow health information request
	 * @param request
	 * @return
	 */
//	@Path(value = PathProxy.NdhmUrls.GET_HEALTH_INFORMATION_REQUEST)
//	@POST
//	@ApiOperation(value = PathProxy.NdhmUrls.GET_HEALTH_INFORMATION_REQUEST, notes = PathProxy.NdhmUrls.GET_HEALTH_INFORMATION_REQUEST)
//	public Response<Boolean> onDataFlowRequest(@RequestBody DataFlowRequest request) {
//		if (request == null) {
//			throw new BusinessException(ServiceError.InvalidInput, " request Required");
//		}
//		Boolean mobile = ndhmService.onDataFlowRequest(request);
//
//		Response<Boolean> response = new Response<Boolean>();
//		response.setData(mobile);
//		return response;
//	}

	
	@Path(value = PathProxy.NdhmUrls.ON_LINK_CONFIRM)
	@POST
	@ApiOperation(value = PathProxy.NdhmUrls.ON_LINK_CONFIRM, notes = PathProxy.NdhmUrls.ON_LINK_CONFIRM)
	public Response<Boolean> onLinkConfirm(@RequestBody OnLinkConfirm request) {

		Boolean mobile = ndhmService.onLinkConfirm(request);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(mobile);
		return response;
	}

	@Path(value = PathProxy.NdhmUrls.GET_LINK_CONFIRM)
	@GET
	@ApiOperation(value = PathProxy.NdhmUrls.GET_LINK_CONFIRM, notes = PathProxy.NdhmUrls.GET_LINK_CONFIRM)
	public Response<LinkConfirm> getLinkConfirm(@QueryParam("requestId")String requestId) {

		LinkConfirm mobile = ndhmService.getLinkConfim(requestId);
		Response<LinkConfirm> response = new Response<LinkConfirm>();
		response.setData(mobile);
		return response;
	}
	

	
	/**
	 * API called by HIP to acknowledge Health information request receipt. Either the hiRequest or error must be specified. hiRequest element returns the same transactionId as before with a status indicating that the request is acknowledged.
	 * Gateway health information on-request
	 * @param request
	 * @return
	 */
	@Path(value = PathProxy.NdhmUrls.HEALTH_INFORMATION_ON_REQUEST)
	@POST
	@ApiOperation(value = PathProxy.NdhmUrls.HEALTH_INFORMATION_ON_REQUEST, notes = PathProxy.NdhmUrls.HEALTH_INFORMATION_ON_REQUEST)
	public Response<Boolean> onGateWayOnRequest(@RequestBody GateWayOnRequest request) {
		if (request == null) {
			throw new BusinessException(ServiceError.InvalidInput, " request Required");
		}
		Boolean mobile = ndhmService.onGateWayOnRequest(request);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(mobile);
		return response;
	}
	

	
	@Path(value = PathProxy.NdhmUrls.ON_NOTIFY)
	@POST
	@ApiOperation(value = PathProxy.NdhmUrls.ON_NOTIFY, notes = PathProxy.NdhmUrls.ON_NOTIFY)
	public Response<Boolean> onNotify(@RequestBody OnNotifyRequest request) {

	Boolean mobile = ndhmService.onNotify(request);
	Response<Boolean> response = new Response<Boolean>();
	response.setData(mobile);
	return response;
}
	
	
	
	/**
	 * gateway consent-requests/init hiu swagger  init api
	 * @param request
	 * @return
	 */
	@Path(value = PathProxy.NdhmUrls.GATEWAY_CONSENT_REQUEST_INIT)
	@POST
	@ApiOperation(value = PathProxy.NdhmUrls.GATEWAY_CONSENT_REQUEST_INIT, notes = PathProxy.NdhmUrls.GATEWAY_CONSENT_REQUEST_INIT)
	public Response<Boolean> onGatewayConsentRequestInitApi(@RequestBody GatewayConsentInitRequest request) {
		if (request == null) {
			throw new BusinessException(ServiceError.InvalidInput, " request Required");
		}
		System.out.println("request " + request);
		Boolean mobile = ndhmService.onGatewayConsentRequestInitApi(request);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(mobile);
		return response;
	}
	
	/**
	 * gateway consent-requests/status hiu swagger  init api
	 * @param request
	 * @return
	 */
	@Path(value = PathProxy.NdhmUrls.GATWAY_CONSENT_REQUEST_STATUS)
	@POST
	@ApiOperation(value = PathProxy.NdhmUrls.GATWAY_CONSENT_REQUEST_STATUS, notes = PathProxy.NdhmUrls.GATWAY_CONSENT_REQUEST_STATUS)
	public Response<Boolean> onGatewayConsentRequestStatusApi(@RequestBody GatewayConsentStatusRequest request) {
		if (request == null) {
			throw new BusinessException(ServiceError.InvalidInput, " request Required");
		}
		Boolean mobile = ndhmService.onGatewayConsentRequestStatusApi(request);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(mobile);
		return response;
	}

	
	
	@Path(value = PathProxy.NdhmUrls.GET_NOTIFY)
	@GET
	@ApiOperation(value = PathProxy.NdhmUrls.GET_NOTIFY, notes = PathProxy.NdhmUrls.GET_NOTIFY)
	public Response<NotifyRequest> getNotify(@QueryParam("requestId")String requestId) {

		NotifyRequest mobile = ndhmService.getNotify(requestId);
		Response<NotifyRequest> response = new Response<NotifyRequest>();
		response.setData(mobile);
		return response;
	}
	
	
	@Path(value = PathProxy.NdhmUrls.GET_DATAFLOW)
	@GET
	@ApiOperation(value = PathProxy.NdhmUrls.GET_DATAFLOW, notes = PathProxy.NdhmUrls.GET_DATAFLOW)
	public Response<DataFlowRequest> getHealthDataFlow(@QueryParam("transactionId")String transactionId) {

		DataFlowRequest mobile = ndhmService.getDataFlow(transactionId);
		Response<DataFlowRequest> response = new Response<DataFlowRequest>();
		response.setData(mobile);
		return response;
	}
	
	@Path(value = PathProxy.NdhmUrls.GET_CONSENT_INIT)
	@GET
	@ApiOperation(value = PathProxy.NdhmUrls.GET_CONSENT_INIT, notes = PathProxy.NdhmUrls.GET_CONSENT_INIT)
	public Response<ConsentOnInitRequest> getConsentInit(@QueryParam("requestId")String requestId) {

		ConsentOnInitRequest mobile = ndhmService.getConsentInitRequest(requestId);
		Response<ConsentOnInitRequest> response = new Response<ConsentOnInitRequest>();
		response.setData(mobile);
		return response;
	}
	
	@Path(value = PathProxy.NdhmUrls.HEALTH_INFORMATION_NOTIFY)
	@POST
	@ApiOperation(value = PathProxy.NdhmUrls.HEALTH_INFORMATION_NOTIFY, notes = PathProxy.NdhmUrls.HEALTH_INFORMATION_NOTIFY)
	public Response<Boolean> healthInfoNotify(@RequestBody HealthInfoNotify request) {
		if (request == null) {
			throw new BusinessException(ServiceError.InvalidInput, " request Required");
		}
		Boolean mobile = ndhmService.healthInformationNotify(request);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(mobile);
		return response;
	}
	
	@Path(value = PathProxy.NdhmUrls.GET_CONSENT_STATUS)
	@GET
	@ApiOperation(value = PathProxy.NdhmUrls.GET_CONSENT_STATUS, notes = PathProxy.NdhmUrls.GET_CONSENT_STATUS)
	public Response<OnConsentRequestStatus> getConsentStatus(@QueryParam("requestId")String requestId) {

		OnConsentRequestStatus mobile = ndhmService.getConsentStatus(requestId);
		Response<OnConsentRequestStatus> response = new Response<OnConsentRequestStatus>();
		response.setData(mobile);
		return response;
	}

	@Path(value = PathProxy.NdhmUrls.NDHM_PATIENT)
	@POST
	@ApiOperation(value = PathProxy.NdhmUrls.NDHM_PATIENT, notes = PathProxy.NdhmUrls.NDHM_PATIENT)
	public Response<Boolean> findPatient(@RequestBody NdhmPatientRequest request) {
		if (request == null) {
			throw new BusinessException(ServiceError.InvalidInput, " request Required");
		}
		Boolean mobile = ndhmService.findPatient(request);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(mobile);
		return response;
	}
	
	@Path(value = PathProxy.NdhmUrls.GET_NDHM_PATIENT)
	@GET
	@ApiOperation(value = PathProxy.NdhmUrls.GET_NDHM_PATIENT, notes = PathProxy.NdhmUrls.GET_NDHM_PATIENT)
	public Response<NdhmOnPatientFindRequest> getNdhmPatient(@QueryParam("requestId")String requestId) {

		NdhmOnPatientFindRequest mobile = ndhmService.getNdhmPatient(requestId);
		Response<NdhmOnPatientFindRequest> response = new Response<NdhmOnPatientFindRequest>();
		response.setData(mobile);
		return response;
	}
	
	@Path(value = PathProxy.NdhmUrls.HIU_ON_NOTIFY)
	@POST
	@ApiOperation(value = PathProxy.NdhmUrls.HIU_ON_NOTIFY, notes = PathProxy.NdhmUrls.HIU_ON_NOTIFY)
	public Response<Boolean> hiuOnNotify(@RequestBody HiuOnNotify request) {
		if (request == null) {
			throw new BusinessException(ServiceError.InvalidInput, " request Required");
		}
		Boolean mobile = ndhmService.onNotifyHiu(request);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(mobile);
		return response;
	}
	
	
	@Path(value = PathProxy.NdhmUrls.GET_HIU_NOTIFY)
	@GET
	@ApiOperation(value = PathProxy.NdhmUrls.GET_HIU_NOTIFY, notes = PathProxy.NdhmUrls.GET_HIU_NOTIFY)
	public Response<NotifyHiuRequest> getHiuNotify(@QueryParam("requestId")String requestId) {

		NotifyHiuRequest mobile = ndhmService.getHiuNotify(requestId);
		Response<NotifyHiuRequest> response = new Response<NotifyHiuRequest>();
		response.setData(mobile);
		return response;
	}
	
	@Path(value = PathProxy.NdhmUrls.HIU_CONSENT_FETCH)
	@POST
	@ApiOperation(value = PathProxy.NdhmUrls.HIU_CONSENT_FETCH, notes = PathProxy.NdhmUrls.HIU_CONSENT_FETCH)
	public Response<Boolean> hiuConsentFetch(@RequestBody ConsentFetchRequest request) {
		if (request == null) {
			throw new BusinessException(ServiceError.InvalidInput, " request Required");
		}
		Boolean mobile = ndhmService.consentFetch(request);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(mobile);
		return response;
	}
	
	
	@Path(value = PathProxy.NdhmUrls.GET_CONSENT_FETCH)
	@GET
	@ApiOperation(value = PathProxy.NdhmUrls.GET_CONSENT_FETCH, notes = PathProxy.NdhmUrls.GET_CONSENT_FETCH)
	public Response<OnConsentFetchRequest> getHiuConsentArtifact(@QueryParam("requestId")String requestId) {

		OnConsentFetchRequest mobile = ndhmService.getConsentFetch(requestId);
		Response<OnConsentFetchRequest> response = new Response<OnConsentFetchRequest>();
		response.setData(mobile);
		return response;
	}

	
	@Path(value = PathProxy.NdhmUrls.HIU_DATA_REQUEST)
	@POST
	@ApiOperation(value = PathProxy.NdhmUrls.HIU_DATA_REQUEST, notes = PathProxy.NdhmUrls.HIU_DATA_REQUEST)
	public Response<Boolean> hiuDataRequest(@RequestBody HiuDataRequest request) {
		if (request == null) {
			throw new BusinessException(ServiceError.InvalidInput, " request Required");
		}
		Boolean mobile = ndhmService.hiuDataRequest(request);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(mobile);
		return response;
	}
	
	@Path(value = PathProxy.NdhmUrls.GET_HIU_DATA_REQUEST)
	@GET
	@ApiOperation(value = PathProxy.NdhmUrls.GET_HIU_DATA_REQUEST, notes = PathProxy.NdhmUrls.GET_HIU_DATA_REQUEST)
	public Response<GateWayOnRequest> getHiuDataRequest(@QueryParam("requestId")String requestId) {

		GateWayOnRequest mobile = ndhmService.getHiuDataRequest(requestId);
		Response<GateWayOnRequest> response = new Response<GateWayOnRequest>();
		response.setData(mobile);
		return response;
	}
	
	
	@Path(value = PathProxy.NdhmUrls.GET_HIU_DATA)
	@GET
	@ApiOperation(value = PathProxy.NdhmUrls.GET_HIU_DATA, notes = PathProxy.NdhmUrls.GET_HIU_DATA)
	public Response<HiuDataResponse> getHiuData(@QueryParam("transactionId")String transactionId) {

		HiuDataResponse mobile = ndhmService.getHiuData(transactionId);
		Response<HiuDataResponse> response = new Response<HiuDataResponse>();
		response.setData(mobile);
		return response;
	}
	
	@Path(value = PathProxy.NdhmUrls.SHARE_PATIENT)
	@POST
	@ApiOperation(value = PathProxy.NdhmUrls.SHARE_PATIENT, notes = PathProxy.NdhmUrls.SHARE_PATIENT)
	public Response<Boolean> sharePatientRequest(@RequestBody OnSharePatientrequest request) {
		if (request == null) {
			throw new BusinessException(ServiceError.InvalidInput, " request Required");
		}
		Boolean mobile = ndhmService.onShareProfile(request);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(mobile);
		return response;
	}
	
	@Path(value = PathProxy.NdhmUrls.GET_SHARE_PATIENT)
	@GET
	@ApiOperation(value = PathProxy.NdhmUrls.GET_SHARE_PATIENT, notes = PathProxy.NdhmUrls.GET_SHARE_PATIENT)
	public Response<PatientShareProfile> getPatientShare(@QueryParam("healthId")String healthId) {

		PatientShareProfile mobile = ndhmService.getPatientShare(healthId);
		Response<PatientShareProfile> response = new Response<PatientShareProfile>();
		response.setData(mobile);
		return response;
	}
	
	@Path(value = PathProxy.NdhmUrls.PATIENT_NOTIFY_SMS)
	@POST
	@ApiOperation(value = PathProxy.NdhmUrls.PATIENT_NOTIFY_SMS, notes = PathProxy.NdhmUrls.PATIENT_NOTIFY_SMS)
	public Response<Boolean> sharePatientRequest(@RequestBody NotifyPatientrequest request) {
		if (request == null) {
			throw new BusinessException(ServiceError.InvalidInput, " request Required");
		}
		Boolean mobile = ndhmService.notifyPatientSms(request);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(mobile);
		return response;
	}
	
	@Path(value = PathProxy.NdhmUrls.GET_PATIENT_NOTIFY_SMS)
	@GET
	@ApiOperation(value = PathProxy.NdhmUrls.GET_PATIENT_NOTIFY_SMS, notes = PathProxy.NdhmUrls.GET_PATIENT_NOTIFY_SMS)
	public Response<OnNotifySmsRequest> getNotifySms(@QueryParam("requestId")String requestId) {

		OnNotifySmsRequest mobile = ndhmService.getNotifySms(requestId);
		Response<OnNotifySmsRequest> response = new Response<OnNotifySmsRequest>();
		response.setData(mobile);
		return response;
	}
	
	
	
	
}
