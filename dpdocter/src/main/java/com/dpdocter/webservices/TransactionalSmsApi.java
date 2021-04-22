package com.dpdocter.webservices;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.Produces;
import org.springframework.http.MediaType;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.response.MessageResponse;
import com.dpdocter.services.TransactionSmsServices;

import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;


@RestController
@RequestMapping(value=PathProxy.TRANSACTION_SMS_BASE_URL,consumes =MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
@Api(value = PathProxy.TRANSACTION_SMS_BASE_URL, description = "Endpoint for records")
public class TransactionalSmsApi {

	
	private static Logger logger = LogManager.getLogger(TransactionalSmsApi.class.getName());

	@Autowired
	private TransactionSmsServices transactionSmsServices;
	
	@GetMapping(value = PathProxy.TransactionSmsUrls.GET_TRANSACTION_SMS_REPORT)
	@ApiOperation(value = PathProxy.TransactionSmsUrls.GET_TRANSACTION_SMS_REPORT, notes = PathProxy.TransactionSmsUrls.GET_TRANSACTION_SMS_REPORT)
	public Response<MessageResponse> getTransactionSmsReport(@DefaultValue("0")@RequestParam(value ="size") int size, 
			@DefaultValue("0")	@RequestParam(value ="page") int page,
			@RequestParam("fromDate") String fromDate, @RequestParam("toDate") String toDate,
			@RequestParam(value ="doctorId") String doctorId,
			@RequestParam(value ="locationIdId") String locationId) {

		Response<MessageResponse> response = new Response<MessageResponse>();
		if (doctorId == null && locationId==null) {
			logger.warn("doctorId or locationid  is NULL");
			throw new BusinessException(ServiceError.InvalidInput, "doctorId send  is NULL");
		}
			response.setDataList(transactionSmsServices.getSmsReport(page, size, doctorId, locationId,fromDate,toDate));
	
		return response;
	}
}
