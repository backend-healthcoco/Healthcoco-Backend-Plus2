package com.dpdocter.webservices;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dpdocter.beans.DoctorLoginPin;
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
@RequestMapping(value=PathProxy.NDHM_BASE_URL, consumes =MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
@Api(value = PathProxy.NDHM_BASE_URL, description = "Endpoint for ndhm")
public class NdhmApi {

	private static Logger logger = LogManager.getLogger(NdhmApi.class.getName());

	@Autowired
	private NDHMservices ndhmService;
	
	@GetMapping(value = PathProxy.NdhmUrls.GET_SESSION)
	@ApiOperation(value = PathProxy.NdhmUrls.GET_SESSION, notes = PathProxy.NdhmUrls.GET_SESSION)
	public Response<NdhmOauthResponse> getLoginPin() {
		
		NdhmOauthResponse ndhmOauth = ndhmService.session();
		Response<NdhmOauthResponse> response = new Response<NdhmOauthResponse>();
		response.setData(ndhmOauth);
		return response;
	}

}
