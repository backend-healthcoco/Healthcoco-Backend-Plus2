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
import com.dpdocter.beans.OnAuthConfirmRequest;
import com.dpdocter.beans.OnAuthInitRequest;
import com.dpdocter.beans.OnCareContext;
import com.dpdocter.beans.OnFetchModesRequest;
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
	

}
