package com.dpdocter.webservices;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import org.springframework.http.MediaType;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dpdocter.beans.BulkSmsCredits;
import com.dpdocter.beans.BulkSmsPackage;
import com.dpdocter.beans.MessageStatus;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.request.OrderRequest;
import com.dpdocter.request.PaymentSignatureRequest;
import com.dpdocter.response.BulkSmsPaymentResponse;
import com.dpdocter.response.MessageResponse;
import com.dpdocter.services.BulkSmsServices;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value=PathProxy.BULK_SMS_PACKAGE_BASE_URL,produces = MediaType.APPLICATION_JSON_VALUE ,consumes = MediaType.APPLICATION_JSON_VALUE)
@Api(value = PathProxy.BULK_SMS_PACKAGE_BASE_URL, description = "Endpoint for records")
public class BulkSmsApi {
	
	private static Logger logger = LogManager.getLogger(BulkSmsApi.class.getName());


	@Autowired
	private BulkSmsServices bulkSmsServices;
	
	@GetMapping(value = PathProxy.BulkSmsPackageUrls.GET_SMS_PACKAGE)
	@ApiOperation(value = PathProxy.BulkSmsPackageUrls.GET_SMS_PACKAGE, notes = PathProxy.BulkSmsPackageUrls.GET_SMS_PACKAGE)
	public Response<BulkSmsPackage> getBulkSmsPackages(@DefaultValue("0")@RequestParam(value ="size") int size, 
			@DefaultValue("0")	@RequestParam( value ="page") int page,
			@RequestParam(value ="discarded") Boolean discarded, 
			@RequestParam(value ="searchTerm") String searchTerm) {

		Response<BulkSmsPackage> response = new Response<BulkSmsPackage>();
			response.setCount(bulkSmsServices.CountBulkSmsPackage(searchTerm, discarded));
			response.setDataList(bulkSmsServices.getBulkSmsPackage(page, size, searchTerm, discarded));
	
		return response;
	}
	
	
	@GetMapping(value = PathProxy.BulkSmsPackageUrls.GET_BULK_SMS_CREDITS)
	@ApiOperation(value = PathProxy.BulkSmsPackageUrls.GET_BULK_SMS_CREDITS, notes = PathProxy.BulkSmsPackageUrls.GET_BULK_SMS_CREDITS)	
	public Response<BulkSmsCredits> getBulkSmsCredits(@DefaultValue("0")@RequestParam(value ="size") int size, 
			@DefaultValue("0")	@RequestParam(value ="page") int page,
			@RequestParam(value ="doctorId") String doctorId,
			@RequestParam(value ="locationIdId") String locationId,
			@RequestParam(value ="searchTerm") String searchTerm) {

		Response<BulkSmsCredits> response = new Response<BulkSmsCredits>();
		if (doctorId == null && locationId==null) {
			logger.warn("doctorId or locationid  is NULL");
			throw new BusinessException(ServiceError.InvalidInput, "doctorId send  is NULL");
		}
			response.setDataList(bulkSmsServices.getCreditsByDoctorIdAndLocationId(size, page, searchTerm, doctorId, locationId));
	
		return response;
	}
	
	@GetMapping(value = PathProxy.BulkSmsPackageUrls.GET_SMS_HISTORY)
	@ApiOperation(value = PathProxy.BulkSmsPackageUrls.GET_SMS_HISTORY, notes = PathProxy.BulkSmsPackageUrls.GET_SMS_HISTORY)
	public Response<BulkSmsCredits> getBulkSmsHistory(@DefaultValue("0")@RequestParam(value ="size") int size, 
			@DefaultValue("0")	@RequestParam(value ="page") int page,
			@RequestParam(value ="doctorId") String doctorId,
			@RequestParam(value ="locationIdId") String locationId,
			@RequestParam(value ="searchTerm") String searchTerm) {

		Response<BulkSmsCredits> response = new Response<BulkSmsCredits>();
		if (doctorId == null && locationId==null) {
			logger.warn("doctorId or locationid  is NULL");
			throw new BusinessException(ServiceError.InvalidInput, "doctorId send  is NULL");
		}
			response.setDataList(bulkSmsServices.getBulkSmsHistory(page, size, searchTerm, doctorId,locationId));
	
		return response;
	}
	
	@GetMapping(value = PathProxy.BulkSmsPackageUrls.GET_SMS_REPORT)
	@ApiOperation(value = PathProxy.BulkSmsPackageUrls.GET_SMS_REPORT, notes = PathProxy.BulkSmsPackageUrls.GET_SMS_REPORT)
	public Response<MessageResponse> getBulkSmsReport(@DefaultValue("0")@RequestParam(value ="size") int size, 
			@DefaultValue("0")	@RequestParam(value ="page") int page,
			@RequestParam(value ="doctorId") String doctorId,
			@RequestParam(value ="locationIdId") String locationId) {

		Response<MessageResponse> response = new Response<MessageResponse>();
		if (doctorId == null && locationId==null) {
			logger.warn("doctorId or locationid  is NULL");
			throw new BusinessException(ServiceError.InvalidInput, "doctorId send  is NULL");
		}
			response.setDataList(bulkSmsServices.getSmsReport(page, size, doctorId, locationId));
	
		return response;
	}
	
	
	@PostMapping(value = PathProxy.BulkSmsPackageUrls.CREATE_PAYMENT)
	@ApiOperation(value = PathProxy.BulkSmsPackageUrls.CREATE_PAYMENT, notes = PathProxy.BulkSmsPackageUrls.CREATE_PAYMENT)
	public Response<BulkSmsPaymentResponse> createOrder(@RequestBody OrderRequest request) {
		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		if (DPDoctorUtils.anyStringEmpty(request.getBulkSmsPackageId(),request.getDoctorId())) {
			logger.warn(" doctorId should not be Null or empty");
			throw new BusinessException(ServiceError.InvalidInput, "userId,problemDetailId and doctorId should not be Null or empty");
		}

		Response<BulkSmsPaymentResponse> response = new Response<BulkSmsPaymentResponse>();
		response.setData(bulkSmsServices.addCredits(request));
		return response;
	}

	@PostMapping(value = PathProxy.BulkSmsPackageUrls.VERIFY_SIGNATURE)
	@ApiOperation(value = PathProxy.BulkSmsPackageUrls.VERIFY_SIGNATURE, notes = PathProxy.BulkSmsPackageUrls.VERIFY_SIGNATURE)
	public Response<Boolean> verifySignature(@RequestBody PaymentSignatureRequest request) {
		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		if (DPDoctorUtils.anyStringEmpty(request.getBulkSmsPackageId(), request.getDoctorId(), request.getOrderId(),
				request.getSignature(), request.getPaymentId())) {
			logger.warn("userId,doctorId,orderId,signature,paymentId should not be Null or empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"userId,orderId,signature,paymentId should not be Null or empty");
		}
		Response<Boolean> response = new Response<Boolean>();
		response.setData(bulkSmsServices.verifySignature(request));
		return response;
	}

	@GetMapping(value = PathProxy.BulkSmsPackageUrls.GET_SMS_STATUS)
	@ApiOperation(value = PathProxy.BulkSmsPackageUrls.GET_SMS_STATUS, notes = PathProxy.BulkSmsPackageUrls.GET_SMS_STATUS)
	public Response<MessageStatus> getSmsStatus(
			@RequestParam(value ="messageId") String messageId) {

		Response<MessageStatus> response = new Response<MessageStatus>();
		if (messageId == null && messageId==null) {
			logger.warn("messageId  is NULL");
			throw new BusinessException(ServiceError.InvalidInput, "doctorId send  is NULL");
		}
			response.setData(bulkSmsServices.getSmsStatus(messageId));
	
		return response;
	}
	

}
