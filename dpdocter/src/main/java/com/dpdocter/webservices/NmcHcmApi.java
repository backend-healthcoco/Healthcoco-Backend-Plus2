package com.dpdocter.webservices;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dpdocter.beans.NmcHcm;
import com.dpdocter.beans.UserSymptom;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.services.NmcHcmServices;

import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@Path(value=PathProxy.NMC_HCM_BASE_URL)
@Produces(MediaType.APPLICATION_JSON_VALUE)
@Consumes(MediaType.APPLICATION_JSON_VALUE)
@Api(value = PathProxy.NMC_HCM_BASE_URL, description = "Endpoint for NmcHcm")

public class NmcHcmApi {

	private static Logger logger = LogManager.getLogger(NmcHcmApi.class.getName());
	
	@Autowired
	private NmcHcmServices nmcHcmServices;
	
//	@PostMapping(value=PathProxy.NmcHcmUrls.ADD_NMC_HCM_DETAILS)
//	@ApiOperation(value = PathProxy.NmcHcmUrls.ADD_NMC_HCM_DETAILS, notes = PathProxy.NmcHcmUrls.ADD_NMC_HCM_DETAILS)
//	public Response<NmcHcm> addEditNmcHcmDetails(@RequestBody NmcHcm request)
//	{
//		
//	if (request == null) {
//		logger.warn("Invalid Input");
//		throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
//	} 
//	Response<NmcHcm> response = new Response<NmcHcm>();
//	response.setData(nmcHcmServices.addDetails(request));
//	return response;
//	}
	
	@Path(value = PathProxy.NmcHcmUrls.GET_NMC_HCM_DETAILS)
	@ApiOperation(value = PathProxy.NmcHcmUrls.GET_NMC_HCM_DETAILS, notes =PathProxy.NmcHcmUrls.GET_NMC_HCM_DETAILS)
	@GET
	public Response<NmcHcm> getNmcHcmDetails(@DefaultValue("0") @QueryParam(value ="size") int size, 
			@DefaultValue("0") @QueryParam( value ="page") int page, 
			@QueryParam( value ="searchTerm") String searchTerm,
			@QueryParam( value ="type") String type,@DefaultValue("false")@QueryParam( value ="discarded") Boolean discarded) {
		
		Response<NmcHcm> response = new Response<NmcHcm>();
			response.setCount(nmcHcmServices.countNmcData(type, searchTerm,discarded));
			response.setDataList(nmcHcmServices.getDetails(page, size, searchTerm,type));
		
		return response;
	}
}
