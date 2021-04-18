package com.dpdocter.webservices;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import org.springframework.http.MediaType;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dpdocter.beans.BankDetails;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.services.BankDetailsService;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value=PathProxy.BANK_DETAILS_BASE_URL,produces = MediaType.APPLICATION_JSON_VALUE ,consumes = MediaType.APPLICATION_JSON_VALUE)
@Api(value = PathProxy.BANK_DETAILS_BASE_URL, description = "Endpoint for records")
public class BankDetailsApi {

	private static Logger logger = LogManager.getLogger(BankDetailsApi.class.getName());
	
	@Autowired
	private BankDetailsService bankDetailsService;
	
	
	@PostMapping(value = PathProxy.BankDetailsUrls.ADD_EDIT_BANK_DETAILS)
	@ApiOperation(value = PathProxy.BankDetailsUrls.ADD_EDIT_BANK_DETAILS, notes = PathProxy.BankDetailsUrls.ADD_EDIT_BANK_DETAILS)
	public Response<Boolean> saveBankDetails(@RequestBody BankDetails request) {
	
		if (request == null) {
			logger.warn("Request send  is NULL");
			throw new BusinessException(ServiceError.InvalidInput, "Request send  is NULL");
		}
		
		if (DPDoctorUtils.anyStringEmpty(request.getAccountholderName())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "AccountholderName cannot be null");
		}
		
		if (DPDoctorUtils.anyStringEmpty(request.getAccountNumber(),request.getBankName(),request.getIfscNumber(),request.getDoctorId())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "AccountNumber,BankName,IfscNumber,DoctorId cannot be null");
		}
			
		
		Response<Boolean> response = new Response<Boolean>();
		bankDetailsService.addEditBankDetails(request);
		
		response.setData(true);
		return response;
	}
	
	@GetMapping(value = PathProxy.BankDetailsUrls.GET_BANK_DETAILS_BY_DOCTORID)
	@ApiOperation(value = PathProxy.BankDetailsUrls.GET_BANK_DETAILS_BY_DOCTORID, notes = PathProxy.BankDetailsUrls.GET_BANK_DETAILS_BY_DOCTORID)
	public Response<BankDetails> getBankDetails(@PathVariable("doctorId") String doctorId) {
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
