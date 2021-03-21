package com.dpdocter.webservices;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import com.dpdocter.beans.Country;
import com.dpdocter.beans.PackageDetailObject;
import com.dpdocter.beans.Subscription;
import com.dpdocter.enums.PackageType;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.request.SubscriptionPaymentSignatureRequest;
import com.dpdocter.request.SubscriptionRequest;
import com.dpdocter.response.SubscriptionResponse;
import com.dpdocter.services.SubscriptionService;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;



@Component
@Path(PathProxy.SUBSCRIPTION_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.SUBSCRIPTION_BASE_URL, description = "")
public class SubscriptionApi {


	private Logger logger = LogManager.getLogger(SubscriptionApi.class);

	@Autowired
	private SubscriptionService subscriptionService;
	
	
	@Path(value = PathProxy.SubscriptionUrls.GET_SUBSCRIPTION_BY_DOCTORID)
	@GET
	@ApiOperation(value = PathProxy.SubscriptionUrls.GET_SUBSCRIPTION_BY_DOCTORID, notes = PathProxy.SubscriptionUrls.GET_SUBSCRIPTION_BY_DOCTORID)
	public Response<Subscription> getSubscriptionByDoctorId(@PathParam("doctorId") String doctorId,
			@QueryParam(value = "packageName") PackageType packageName,@QueryParam(value = "duration") int duration,
			@QueryParam(value = "newAmount") int newAmount ) {
		if (doctorId == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		Response<Subscription> response = new Response<Subscription>();
		response.setData(subscriptionService.getSubscriptionByDoctorId(doctorId,packageName,duration,newAmount));
		return response;

	}
		

	@Path(value = PathProxy.SubscriptionUrls.GET_PACKAGES_BY_NAME)
	@GET
	@ApiOperation(value = PathProxy.SubscriptionUrls.GET_PACKAGES_BY_NAME, notes = PathProxy.SubscriptionUrls.GET_PACKAGES_BY_NAME)
	public Response<PackageDetailObject> getPackageDetailByPackageName(@QueryParam( value = "packageName") PackageType packageName) {
		if (packageName == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		Response<PackageDetailObject> response = new Response<PackageDetailObject>();
		response.setData(subscriptionService.getPackageDetailByPackageName(packageName));
		return response;

	}
	
	@Path(value = PathProxy.SubscriptionUrls.ADD_EDIT_SUBSCRIPTION)
	@POST
	@ApiOperation(value = PathProxy.SubscriptionUrls.ADD_EDIT_SUBSCRIPTION, notes = PathProxy.SubscriptionUrls.ADD_EDIT_SUBSCRIPTION)
	public Response<SubscriptionResponse> addEditSubscription(@RequestBody SubscriptionRequest request) {

		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId())) {
			logger.warn("Invalid Input");			
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
	
		Response<SubscriptionResponse> response = new Response<SubscriptionResponse>();
		response.setData(subscriptionService.addEditSubscription(request));
		return response;
	}
	
	
	
	@Path(value = PathProxy.SubscriptionUrls.VERIFY_SIGNATURE)
	@POST
	@ApiOperation(value = PathProxy.SubscriptionUrls.VERIFY_SIGNATURE, notes = PathProxy.SubscriptionUrls.VERIFY_SIGNATURE)
	public Response<Boolean> verifySignature(@RequestBody SubscriptionPaymentSignatureRequest request) {
		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		if (DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getOrderId(),
				request.getSignature(), request.getPaymentId())) {
			logger.warn("doctorId,orderId,signature,paymentId should not be Null or empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"doctorId,orderId,signature,paymentId should not be Null or empty");
		}
		Response<Boolean> response = new Response<Boolean>();
		response.setData(subscriptionService.verifySignature(request));
		return response;
	}
	
	@Path(value = PathProxy.SubscriptionUrls.GET_COUNTRYLIST)
	@GET
	@ApiOperation(value = PathProxy.SubscriptionUrls.GET_COUNTRYLIST, notes = PathProxy.SubscriptionUrls.GET_COUNTRYLIST)
	public Response<Country> getCountryList(@QueryParam("page") int page, @QueryParam("size") int size,			
			@DefaultValue("false") @QueryParam(value = "isDiscarded") Boolean isDiscarded,
			@DefaultValue("")@QueryParam(value = "searchTerm") String searchTerm) {

		Integer count = subscriptionService.countCountry(isDiscarded, searchTerm);
		Response<Country> response = new Response<Country>();
		if (count > 0)
			response.setDataList(subscriptionService.getCountry(size, page, isDiscarded, searchTerm));
		response.setCount(count);
		return response;
	}
	
	@Path(value = PathProxy.SubscriptionUrls.GET_PACKAGES)
	@GET
	@ApiOperation(value = PathProxy.SubscriptionUrls.GET_PACKAGES, notes = PathProxy.SubscriptionUrls.GET_PACKAGES)
	public Response<PackageDetailObject> getPackagesList(@QueryParam("page") int page, @QueryParam("size") int size,			
			@DefaultValue("false") @QueryParam(value = "isDiscarded") Boolean isDiscarded,
			@DefaultValue("")@QueryParam(value = "searchTerm") String searchTerm) {
		Integer count = subscriptionService.countPackages(isDiscarded, searchTerm);
		Response<PackageDetailObject> response = new Response<PackageDetailObject>();
		if (count > 0)
			response.setDataList(subscriptionService.getPackages(size, page, isDiscarded, searchTerm));
		response.setCount(count);
		return response;
	}	
	
	@Path(value = PathProxy.SubscriptionUrls.GET_SUBSCRIPTIONHISTORY_BY_DOCTORID)
	@GET
	@ApiOperation(value = PathProxy.SubscriptionUrls.GET_SUBSCRIPTIONHISTORY_BY_DOCTORID, notes = PathProxy.SubscriptionUrls.GET_SUBSCRIPTIONHISTORY_BY_DOCTORID)
	public Response<Subscription> getSubscriptionHistoryByDoctorId(@PathParam("doctorId") String doctorId,
			@QueryParam("page") int page, @QueryParam("size") int size,			
			@DefaultValue("false") @QueryParam(value = "isDiscarded") Boolean isDiscarded,
			@DefaultValue("")@QueryParam(value = "searchTerm") String searchTerm) {
		if (doctorId == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		Integer count = subscriptionService.countSubscriptionHistory(doctorId,isDiscarded, searchTerm);
		Response<Subscription> response = new Response<Subscription>();
		if (count > 0)
			response.setDataList(subscriptionService.getSubscriptionHistory(doctorId,size, page, isDiscarded, searchTerm));
		response.setCount(count);
		return response;

	}
}
