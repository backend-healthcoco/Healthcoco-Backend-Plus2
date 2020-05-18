package com.dpdocter.webservices;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import com.dpdocter.beans.BankDetails;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.services.BankDetailsService;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Component
@Path(PathProxy.BANK_DETAILS_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.BANK_DETAILS_BASE_URL, description = "Endpoint for records")
public class BankDetailsApi {

	private static Logger logger = Logger.getLogger(BankDetailsApi.class.getName());
	
	@Autowired
	private BankDetailsService bankDetailsService;
	
	
	@Path(value = PathProxy.BankDetailsUrls.ADD_EDIT_BANK_DETAILS)
	@ApiOperation(value = PathProxy.BankDetailsUrls.ADD_EDIT_BANK_DETAILS, notes = PathProxy.BankDetailsUrls.ADD_EDIT_BANK_DETAILS)
	@POST
	public Response<Boolean> saveBankDetails(@RequestBody BankDetails request) {
	
		if (request == null) {
			logger.warn("Request send  is NULL");
			throw new BusinessException(ServiceError.InvalidInput, "Request send  is NULL");
		}
		
		Response<Boolean> response = new Response<Boolean>();
		bankDetailsService.addEditBankDetails(request);
		
		response.setData(true);
		return response;
	}
	
	@Path(value = PathProxy.BankDetailsUrls.GET_BANK_DETAILS_BY_DOCTORID)
	@GET
	@ApiOperation(value = PathProxy.BankDetailsUrls.GET_BANK_DETAILS_BY_DOCTORID, notes = PathProxy.BankDetailsUrls.GET_BANK_DETAILS_BY_DOCTORID)
	public Response<BankDetails> getBankDetails(@PathParam("doctorId") String doctorId) {
		if (DPDoctorUtils.anyStringEmpty(doctorId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "id is NULL");
		}
		BankDetails bankDetails =bankDetailsService.getBankDetailsByDoctorId(doctorId);
		Response<BankDetails> response = new Response<BankDetails>();
		response.setData(bankDetails);
		return response;
	}

	
}
