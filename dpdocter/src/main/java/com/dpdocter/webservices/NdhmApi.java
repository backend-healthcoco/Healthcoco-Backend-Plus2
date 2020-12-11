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
import com.dpdocter.beans.Districts;
import com.dpdocter.beans.FetchModesRequest;
import com.dpdocter.beans.HealthIdRequest;
import com.dpdocter.beans.HealthIdResponse;
import com.dpdocter.beans.HealthIdSearch;
import com.dpdocter.beans.HealthIdSearchRequest;
import com.dpdocter.beans.MobileTokenRequest;
import com.dpdocter.beans.NDHMStates;
import com.dpdocter.beans.NdhmOtp;
import com.dpdocter.beans.NdhmOtpStatus;
import com.dpdocter.beans.NdhmStatus;
import com.dpdocter.beans.OnAuthConfirmRequest;
import com.dpdocter.beans.OnAuthInitRequest;
import com.dpdocter.beans.OnDiscoverRequest;
import com.dpdocter.beans.OnFetchModesRequest;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.request.CreateAadhaarRequest;
import com.dpdocter.request.CreateProfileRequest;
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

	@Path(value = PathProxy.NdhmUrls.GET_PROFILE)
	@DELETE
	@ApiOperation(value = PathProxy.NdhmUrls.GET_PROFILE, notes = PathProxy.NdhmUrls.GET_PROFILE)
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
	
	
	@Path(value = PathProxy.NdhmUrls.GET_DISCOVER)
	@GET
	@ApiOperation(value = PathProxy.NdhmUrls.GET_DISCOVER, notes = PathProxy.NdhmUrls.GET_DISCOVER)
	public Response<CareContextDiscoverRequest> getDiscover(@QueryParam("requestId")String requestId) {

		CareContextDiscoverRequest mobile = ndhmService.getCareContextDiscover(requestId);
		Response<CareContextDiscoverRequest> response = new Response<CareContextDiscoverRequest>();
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
	
	
	
	

	
}
