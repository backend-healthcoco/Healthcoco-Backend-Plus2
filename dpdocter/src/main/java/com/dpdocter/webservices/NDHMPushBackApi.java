package com.dpdocter.webservices;

import java.io.IOException;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import com.dpdocter.beans.CareContextDiscoverRequest;
import com.dpdocter.beans.FetchModesRequest;
import com.dpdocter.beans.LinkConfirm;
import com.dpdocter.beans.LinkRequest;
import com.dpdocter.beans.NdhmOnPatientFindRequest;
import com.dpdocter.beans.NdhmPatientRequest;
import com.dpdocter.beans.NotifyRequest;
import com.dpdocter.beans.OnAuthConfirmRequest;
import com.dpdocter.beans.OnAuthInitRequest;
import com.dpdocter.beans.OnCareContext;
import com.dpdocter.beans.OnConsentFetchRequest;
import com.dpdocter.beans.OnConsentRequestStatus;
import com.dpdocter.beans.OnFetchModesRequest;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.request.ConsentOnInitRequest;
import com.dpdocter.request.DataFlowRequest;
import com.dpdocter.request.DataTransferRequest;
import com.dpdocter.services.NDHMservices;

import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Component
@Path(value = PathProxy.NDHM_PUSH_BACK_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.NDHM_PUSH_BACK_BASE_URL, description = "Endpoint for ndhm push back url")
public class NDHMPushBackApi {
	
	private static Logger logger = LogManager.getLogger(NDHMPushBackApi.class.getName());
	
	@Autowired
	private NDHMservices ndhmService;
	
	@Path(value = PathProxy.NdhmPushUrls.ON_FETCH_MODES)
	@POST
	@ApiOperation(value =PathProxy.NdhmPushUrls.ON_FETCH_MODES, notes = PathProxy.NdhmPushUrls.ON_FETCH_MODES)
	public Response<Boolean> fetchModes(String request) throws JsonParseException, JsonMappingException, IOException {

		System.out.println("request"+request); 
		ObjectMapper mapper = new ObjectMapper();
		OnFetchModesRequest request1= mapper.readValue(request,OnFetchModesRequest.class);
		Boolean mobile = ndhmService.onFetchModes(request1);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(mobile);
		return response;
	}
	
	@Path(value = PathProxy.NdhmPushUrls.ON_AUTH_INIT)
	@POST
	@ApiOperation(value =PathProxy.NdhmPushUrls.ON_AUTH_INIT, notes = PathProxy.NdhmPushUrls.ON_AUTH_INIT)
	public Response<Boolean> authInit(String request) throws JsonParseException, JsonMappingException, IOException {

		System.out.println("request"+request); 
		ObjectMapper mapper = new ObjectMapper();
		OnAuthInitRequest request1= mapper.readValue(request,OnAuthInitRequest.class);
		Boolean mobile = ndhmService.onAuthinit(request1);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(mobile);
		return response;
	}
	
	@Path(value = PathProxy.NdhmPushUrls.ON_AUTH_CONFIRM)
	@POST
	@ApiOperation(value =PathProxy.NdhmPushUrls.ON_AUTH_CONFIRM, notes = PathProxy.NdhmPushUrls.ON_AUTH_CONFIRM)
	public Response<Boolean> authConfirm(String request) throws JsonParseException, JsonMappingException, IOException {

		System.out.println("request"+request); 
		ObjectMapper mapper = new ObjectMapper();
		OnAuthConfirmRequest request1= mapper.readValue(request,OnAuthConfirmRequest.class);
		Boolean mobile = ndhmService.onAuthConfirm(request1);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(mobile);
		return response;
	}
	
	
	@Path(value = PathProxy.NdhmPushUrls.ON_CARE_CONTEXT)
	@POST
	@ApiOperation(value=PathProxy.NdhmPushUrls.ON_CARE_CONTEXT, notes = PathProxy.NdhmPushUrls.ON_CARE_CONTEXT)
	public Response<Boolean> onCareContext(String request) throws JsonParseException, JsonMappingException, IOException {

		System.out.println("request"+request); 
		ObjectMapper mapper = new ObjectMapper();
		OnCareContext request1= mapper.readValue(request,OnCareContext.class);
		Boolean mobile = ndhmService.onCareContext(request1);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(mobile);
		return response;
	}
	
	
	@Path(value = PathProxy.NdhmPushUrls.DISCOVER)
	@POST
	@ApiOperation(value=PathProxy.NdhmPushUrls.DISCOVER, notes = PathProxy.NdhmPushUrls.DISCOVER)
	public Response<Boolean> discover(String request) throws JsonParseException, JsonMappingException, IOException {

		System.out.println("request"+request); 
		ObjectMapper mapper = new ObjectMapper();
		CareContextDiscoverRequest request1= mapper.readValue(request,CareContextDiscoverRequest.class);
		Boolean mobile = ndhmService.discover(request1);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(mobile);
		return response;
	}
	

	
	@Path(value = PathProxy.NdhmPushUrls.LINK_INIT)
	@POST
	@ApiOperation(value=PathProxy.NdhmPushUrls.LINK_INIT, notes = PathProxy.NdhmPushUrls.LINK_INIT)
	public Response<Boolean> linkInit(String request) throws JsonParseException, JsonMappingException, IOException {

		System.out.println("request"+request); 
		ObjectMapper mapper = new ObjectMapper();
		LinkRequest request1= mapper.readValue(request,LinkRequest.class);
		Boolean mobile = ndhmService.linkInit(request1);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(mobile);
		return response;
	}
	
	
	@Path(value = PathProxy.NdhmPushUrls.LINK_CONFIRM)
	@POST
	@ApiOperation(value=PathProxy.NdhmPushUrls.LINK_CONFIRM, notes = PathProxy.NdhmPushUrls.LINK_CONFIRM)
	public Response<Boolean> linkConfirm(String request) throws JsonParseException, JsonMappingException, IOException {

		System.out.println("request"+request); 
		ObjectMapper mapper = new ObjectMapper();
		LinkConfirm request1= mapper.readValue(request,LinkConfirm.class);
		Boolean mobile = ndhmService.linkConfirm(request1);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(mobile);
		return response;
	}

	@Path(value = PathProxy.NdhmPushUrls.HEALTH_INFORMATION_REQUEST)
	@POST
	@ApiOperation(value = PathProxy.NdhmPushUrls.HEALTH_INFORMATION_REQUEST, notes = PathProxy.NdhmPushUrls.HEALTH_INFORMATION_REQUEST)
	public Response<Boolean> onDataFlowRequest(String request) throws JsonParseException, JsonMappingException, IOException {
		if (request == null) {
			throw new BusinessException(ServiceError.InvalidInput, " request Required");
		}
		System.out.println("request"+request); 
		ObjectMapper mapper = new ObjectMapper();
		DataFlowRequest request1=mapper.readValue(request,DataFlowRequest.class);
		Boolean mobile = ndhmService.onDataFlowRequest(request1);

		Response<Boolean> response = new Response<Boolean>();
		response.setData(mobile);
		return response;
	}
	
	
	@Path(value = PathProxy.NdhmPushUrls.NOTIFY)
	@POST
	@ApiOperation(value=PathProxy.NdhmPushUrls.NOTIFY, notes = PathProxy.NdhmPushUrls.NOTIFY)
	public Response<Boolean> notify(String request) throws JsonParseException, JsonMappingException, IOException {

		System.out.println("request"+request); 
		ObjectMapper mapper = new ObjectMapper();
		NotifyRequest request1= mapper.readValue(request,NotifyRequest.class);
		Boolean mobile = ndhmService.ndhmNotify(request1);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(mobile);
		return response;
	}
	
	/**
	 * consent request hiu swagger  on-init api
	 * @param request
	 * @return
	 */
	@Path(value = PathProxy.NdhmPushUrls.CONSENT_REQUEST_ON_INIT)
	@POST
	@ApiOperation(value = PathProxy.NdhmPushUrls.CONSENT_REQUEST_ON_INIT, notes = PathProxy.NdhmPushUrls.CONSENT_REQUEST_ON_INIT)
	public Response<Boolean> onConsentRequestOnInitApi(String request)throws JsonParseException, JsonMappingException, IOException {
		if (request == null) {
			throw new BusinessException(ServiceError.InvalidInput, " request Required");
		}
		System.out.println("request"+request); 
		ObjectMapper mapper = new ObjectMapper();
		ConsentOnInitRequest request1= mapper.readValue(request,ConsentOnInitRequest.class);
		Boolean mobile = ndhmService.onConsentRequestOnInitApi(request1);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(mobile);
		return response;
	}
	
	
	@Path(value = PathProxy.NdhmPushUrls.CONSENT_REQUEST_ON_STATUS)
	@POST
	@ApiOperation(value = PathProxy.NdhmPushUrls.CONSENT_REQUEST_ON_STATUS, notes = PathProxy.NdhmPushUrls.CONSENT_REQUEST_ON_STATUS)
	public Response<Boolean> onConsentRequestOnStatus(String request)throws JsonParseException, JsonMappingException, IOException {
		if (request == null) {
			throw new BusinessException(ServiceError.InvalidInput, " request Required");
		}
		System.out.println("request"+request); 
		ObjectMapper mapper = new ObjectMapper();
		OnConsentRequestStatus request1= mapper.readValue(request,OnConsentRequestStatus.class);
		Boolean mobile = ndhmService.onConsentRequestStatus(request1);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(mobile);
		return response;
	}
	
	/**
	 * Data Transfer health information transfer api
	 * @param request
	 * @return
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonParseException 
	 */
//	@Path(value = PathProxy.NdhmPushUrls.HEALTH_INFORMATION_TRANSFER)
//	@POST
//	@ApiOperation(value = PathProxy.NdhmPushUrls.HEALTH_INFORMATION_TRANSFER, notes = PathProxy.NdhmPushUrls.HEALTH_INFORMATION_TRANSFER)
//	public Response<Boolean> onDataTransferApi(String request)throws JsonParseException, JsonMappingException, IOException {
//		if (request == null) {
//			throw new BusinessException(ServiceError.InvalidInput, " request Required");
//		}
//		ObjectMapper mapper = new ObjectMapper();
//		DataTransferRequest request1= mapper.readValue(request,DataTransferRequest.class);
//		Boolean mobile = ndhmService.onDataTransferApi(request1);
//		Response<Boolean> response = new Response<Boolean>();
//		response.setData(mobile);
//		return response;
//	}
	
	@Path(value = PathProxy.NdhmPushUrls.NDHM_ON_PATIENT)
	@POST
	@ApiOperation(value = PathProxy.NdhmPushUrls.NDHM_ON_PATIENT, notes = PathProxy.NdhmPushUrls.NDHM_ON_PATIENT)
	public Response<Boolean> findPatient(String request) throws JsonParseException, JsonMappingException, IOException {
		if (request == null) {
			throw new BusinessException(ServiceError.InvalidInput, " request Required");
		}
		System.out.println("request"+request); 
		ObjectMapper mapper = new ObjectMapper();
		NdhmOnPatientFindRequest request1= mapper.readValue(request,NdhmOnPatientFindRequest.class);
		Boolean mobile = ndhmService.onFindPatient(request1);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(mobile);
		return response;
	}
	
	@Path(value = PathProxy.NdhmPushUrls.HIU_NOTIFY)
	@POST
	@ApiOperation(value = PathProxy.NdhmPushUrls.HIU_NOTIFY, notes = PathProxy.NdhmPushUrls.HIU_NOTIFY)
	public Response<Boolean> hiuNotify(String request) throws JsonParseException, JsonMappingException, IOException {
		if (request == null) {
			throw new BusinessException(ServiceError.InvalidInput, " request Required");
		}
		System.out.println("request"+request); 
		ObjectMapper mapper = new ObjectMapper();
		NdhmOnPatientFindRequest request1= mapper.readValue(request,NdhmOnPatientFindRequest.class);

		Boolean mobile = ndhmService.onFindPatient(request1);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(mobile);
		return response;
	}
	
	@Path(value = PathProxy.NdhmPushUrls.ON_CONSENT_FETCH)
	@POST
	@ApiOperation(value = PathProxy.NdhmPushUrls.ON_CONSENT_FETCH, notes = PathProxy.NdhmPushUrls.ON_CONSENT_FETCH)
	public Response<Boolean> hiuOnConsentFetch(String request) throws JsonParseException, JsonMappingException, IOException {
		if (request == null) {
			throw new BusinessException(ServiceError.InvalidInput, " request Required");
		}
		System.out.println("request"+request); 
		ObjectMapper mapper = new ObjectMapper();
		OnConsentFetchRequest request1= mapper.readValue(request,OnConsentFetchRequest.class);

		Boolean mobile = ndhmService.onConsentFetch(request1);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(mobile);
		return response;
	}
	
	@Path(value = PathProxy.NdhmPushUrls.ON_HIU_DATA_REQUEST)
	@POST
	@ApiOperation(value = PathProxy.NdhmPushUrls.ON_HIU_DATA_REQUEST, notes = PathProxy.NdhmPushUrls.ON_HIU_DATA_REQUEST)
	public Response<Boolean> hiuOnConsentFe(String request) throws JsonParseException, JsonMappingException, IOException {
		if (request == null) {
			throw new BusinessException(ServiceError.InvalidInput, " request Required");
		}
		System.out.println("request"+request); 
		ObjectMapper mapper = new ObjectMapper();
		GateWayOnRequest request1= mapper.readValue(request,GateWayOnRequest.class);

		Boolean mobile = ndhmService.onHiuDatarequest(request1);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(mobile);
		return response;
	}
	
	@Path(value = PathProxy.NdhmPushUrls.HIU_DATA_TRANSFER)
	@POST
	@ApiOperation(value = PathProxy.NdhmPushUrls.HIU_DATA_TRANSFER, notes = PathProxy.NdhmPushUrls.HIU_DATA_TRANSFER)
	public Response<Boolean> hiuDataTransfer(String request) throws JsonParseException, JsonMappingException, IOException {
		if (request == null) {
			throw new BusinessException(ServiceError.InvalidInput, " request Required");
		}
		System.out.println("request"+request); 
		ObjectMapper mapper = new ObjectMapper();
		DataTransferRequest request1= mapper.readValue(request,DataTransferRequest.class);

		Boolean mobile = ndhmService.onHiuDataTransferApi(request1);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(mobile);
		return response;
	}

}
