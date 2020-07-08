package com.dpdocter.webservices;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import javax.ws.rs.core.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;


import com.dpdocter.beans.BulkSmsCredits;
import com.dpdocter.beans.BulkSmsPackage;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.request.OrderRequest;
import com.dpdocter.request.PaymentSignatureRequest;
import com.dpdocter.response.BulkSmsPaymentResponse;
import com.dpdocter.services.BulkSmsServices;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Component
@Path(PathProxy.BULK_SMS_PACKAGE_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.BULK_SMS_PACKAGE_BASE_URL, description = "Endpoint for records")
public class BulkSmsApi {
	
	private static Logger logger = LogManager.getLogger(BulkSmsApi.class.getName());


	@Autowired
	private BulkSmsServices bulkSmsServices;
	
	@Path(value = PathProxy.BulkSmsPackageUrls.GET_SMS_PACKAGE)
	@ApiOperation(value = PathProxy.BulkSmsPackageUrls.GET_SMS_PACKAGE, notes = PathProxy.BulkSmsPackageUrls.GET_SMS_PACKAGE)
	@GET
	public Response<BulkSmsPackage> getBulkSmsPackages(@DefaultValue("0")@QueryParam(value ="size") int size, 
			@DefaultValue("0")	@QueryParam( value ="page") int page,
			@QueryParam(value ="discarded") Boolean discarded, 
			@QueryParam(value ="searchTerm") String searchTerm) {

		Response<BulkSmsPackage> response = new Response<BulkSmsPackage>();
			response.setCount(bulkSmsServices.CountBulkSmsPackage(searchTerm, discarded));
			response.setDataList(bulkSmsServices.getBulkSmsPackage(page, size, searchTerm, discarded));
	
		return response;
	}
	
	
	@Path(value = PathProxy.BulkSmsPackageUrls.GET_BULK_SMS_CREDITS)
	@ApiOperation(value = PathProxy.BulkSmsPackageUrls.GET_BULK_SMS_CREDITS, notes = PathProxy.BulkSmsPackageUrls.GET_BULK_SMS_CREDITS)
	@GET	
	public Response<BulkSmsCredits> getBulkSmsCredits(@QueryParam(value ="doctorId") String doctorId,
			@QueryParam(value ="locationId") String locationId) {

		Response<BulkSmsCredits> response = new Response<BulkSmsCredits>();
		if (doctorId == null) {
			logger.warn("doctorId send  is NULL");
			throw new BusinessException(ServiceError.InvalidInput, "doctorId send  is NULL");
		}
			response.setData(bulkSmsServices.getCreditsByDoctorIdAndLocationId(doctorId, locationId));
	
		return response;
	}
	
	@Path(value = PathProxy.BulkSmsPackageUrls.GET_SMS_HISTORY)
	@ApiOperation(value = PathProxy.BulkSmsPackageUrls.GET_SMS_HISTORY, notes = PathProxy.BulkSmsPackageUrls.GET_SMS_HISTORY)
	@GET
	public Response<BulkSmsCredits> getBulkSmsHistory(@DefaultValue("0")@QueryParam(value ="size") int size, 
			@DefaultValue("0")	@QueryParam(value ="page") int page,
			@QueryParam(value ="doctorId") String doctorId,
			@QueryParam(value ="locationIdId") String locationId,
			@QueryParam(value ="searchTerm") String searchTerm) {

		Response<BulkSmsCredits> response = new Response<BulkSmsCredits>();
		if (doctorId == null) {
			logger.warn("doctorId send  is NULL");
			throw new BusinessException(ServiceError.InvalidInput, "doctorId send  is NULL");
		}
			response.setDataList(bulkSmsServices.getBulkSmsHistory(page, size, searchTerm, doctorId,locationId));
	
		return response;
	}
	
	
	@Path(value = PathProxy.BulkSmsPackageUrls.CREATE_PAYMENT)
	@ApiOperation(value = PathProxy.BulkSmsPackageUrls.CREATE_PAYMENT, notes = PathProxy.BulkSmsPackageUrls.CREATE_PAYMENT)
	@POST
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

	@Path(value = PathProxy.BulkSmsPackageUrls.VERIFY_SIGNATURE)
	@ApiOperation(value = PathProxy.BulkSmsPackageUrls.VERIFY_SIGNATURE, notes = PathProxy.BulkSmsPackageUrls.VERIFY_SIGNATURE)
	@POST
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

	

}
