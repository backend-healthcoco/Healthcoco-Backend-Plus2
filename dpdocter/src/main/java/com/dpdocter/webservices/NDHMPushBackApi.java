package com.dpdocter.webservices;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import com.dpdocter.beans.FetchModesRequest;
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
	public Response<Boolean> fetchModes(@RequestBody OnFetchModesRequest request) {

		Boolean mobile = ndhmService.onFetchModes(request);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(mobile);
		return response;
	}
	

}
