package com.dpdocter.webservices;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dpdocter.beans.Districts;
import com.dpdocter.beans.DoctorLoginPin;
import com.dpdocter.beans.HealthIdRequest;
import com.dpdocter.beans.HealthIdResponse;
import com.dpdocter.beans.HealthIdSearch;
import com.dpdocter.beans.HealthIdSearchRequest;
import com.dpdocter.beans.NDHMStates;
import com.dpdocter.beans.NdhmOauthResponse;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.services.LoginService;
import com.dpdocter.services.NDHMservices;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@Path(value=PathProxy.NDHM_BASE_URL)
@Produces(MediaType.APPLICATION_JSON_VALUE)
@Consumes(MediaType.APPLICATION_JSON_VALUE)
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
	public Response<String> generateMobileOtp(@QueryParam("mobileNumber")String mobileNumber) {
		
		String mobile=ndhmService.generateOtp(mobileNumber);
		Response<String> response = new Response<String>();
		response.setData(mobile);
		return response;
	}
	
	
	@Path(value = PathProxy.NdhmUrls.GET_VERIFY_MOBILE_OTP)
	@GET
	@ApiOperation(value = PathProxy.NdhmUrls.GET_VERIFY_MOBILE_OTP, notes = PathProxy.NdhmUrls.GET_VERIFY_MOBILE_OTP)
	public Response<String> verifyMobileOtp(@QueryParam("otp")String otp,@QueryParam("txnId")String txnId) {
		
		String mobile=ndhmService.verifyOtp(otp, txnId);
		Response<String> response = new Response<String>();
		response.setData(mobile);
		return response;
	}
	
	@Path(value = PathProxy.NdhmUrls.GET_RESEND_MOBILE_OTP)
	@GET
	@ApiOperation(value = PathProxy.NdhmUrls.GET_RESEND_MOBILE_OTP, notes = PathProxy.NdhmUrls.GET_RESEND_MOBILE_OTP)
	public Response<Boolean> resendMobileOtp(@QueryParam("txnId")String txnId) {
		
		Boolean mobile=ndhmService.resendOtp(txnId);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(mobile);
		return response;
	}
	
	@Path(value = PathProxy.NdhmUrls.CREATE_HEALTH_ID)
	@POST
	@ApiOperation(value = PathProxy.NdhmUrls.CREATE_HEALTH_ID, notes = PathProxy.NdhmUrls.CREATE_HEALTH_ID)
	public Response<HealthIdResponse> generateHealthId(@RequestBody HealthIdRequest request) {
		
		HealthIdResponse mobile=ndhmService.createHealthId(request);
		Response<HealthIdResponse> response = new Response<HealthIdResponse>();
		response.setData(mobile);
		return response;
	}
	
	@Path(value = PathProxy.NdhmUrls.GET_LIST_STATES)
	@GET
	@ApiOperation(value = PathProxy.NdhmUrls.GET_LIST_STATES, notes = PathProxy.NdhmUrls.GET_LIST_STATES)
	public Response<NDHMStates> getlistOfStates() {
		
		List<NDHMStates> mobile=ndhmService.getListforStates();
		Response<NDHMStates> response = new Response<NDHMStates>();
		response.setDataList(mobile);
		return response;
	}
	
	
	@Path(value = PathProxy.NdhmUrls.GET_LIST_DISTRICTS)
	@GET
	@ApiOperation(value = PathProxy.NdhmUrls.GET_LIST_DISTRICTS, notes = PathProxy.NdhmUrls.GET_LIST_DISTRICTS)
	public Response<Districts> getlistOfStates(@QueryParam(value = "stateCode")String statecode) {
		
		List<Districts> mobile=ndhmService.getListforDistricts(statecode);
		Response<Districts> response = new Response<Districts>();
		response.setDataList(mobile);
		return response;
	}
	
	@Path(value = PathProxy.NdhmUrls.GET_SEARCH_BY_HEALTH_ID)
	@GET
	@ApiOperation(value = PathProxy.NdhmUrls.GET_SEARCH_BY_HEALTH_ID, notes = PathProxy.NdhmUrls.GET_SEARCH_BY_HEALTH_ID)
	public Response<HealthIdSearch> searchByMobileNumber(@QueryParam(value = "healthId") String healthId) {
		
		HealthIdSearch mobile=ndhmService.searchByHealthId(healthId);
		Response<HealthIdSearch> response = new Response<HealthIdSearch>();
		response.setData(mobile);
		return response;
	}
	
	
	@Path(value = PathProxy.NdhmUrls.GET_EXISTS_BY_HEALTH_ID)
	@GET
	@ApiOperation(value = PathProxy.NdhmUrls.GET_EXISTS_BY_HEALTH_ID, notes = PathProxy.NdhmUrls.GET_EXISTS_BY_HEALTH_ID)
	public Response<String> existsByHealthId(@QueryParam(value = "healthId") String healthId) {
		
		String mobile=ndhmService.existsByHealthId(healthId);
		Response<String> response = new Response<String>();
		response.setData(mobile);
		return response;
	}
	
	@Path(value = PathProxy.NdhmUrls.GET_SEARCH_BY_MOBILE_NUMBER)
	@POST
	@ApiOperation(value = PathProxy.NdhmUrls.GET_SEARCH_BY_MOBILE_NUMBER, notes = PathProxy.NdhmUrls.GET_SEARCH_BY_MOBILE_NUMBER)
	public Response<HealthIdSearch> searchByMobileNumber(@RequestBody HealthIdSearchRequest  request) {
		
		HealthIdSearch mobile=ndhmService.searchBymobileNumber(request);
		Response<HealthIdSearch> response = new Response<HealthIdSearch>();
		response.setData(mobile);
		return response;
	}

}
