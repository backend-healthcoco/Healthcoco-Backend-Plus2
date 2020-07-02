package com.dpdocter.webservices.v2;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dpdocter.beans.PackageDetailObject;
import com.dpdocter.beans.Subscription;
import com.dpdocter.enums.PackageType;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.services.SubscriptionService;
import com.dpdocter.webservices.PathProxy;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;


@RestController
@Component(value = "SubscriptionApi")
@Path(PathProxy.SUBSCRIPTION_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.SUBSCRIPTION_BASE_URL, description = "")
public class SubscriptionApi {


	private Logger logger = Logger.getLogger(SubscriptionApi.class);

	@Autowired
	private SubscriptionService subscriptionService;
	
	
	
	@Produces(MediaType.APPLICATION_JSON)
	@Path(value = PathProxy.SubscriptionUrls.GET_SUBSCRIPTION_BY_DOCTORID)
	@GET
	@ApiOperation(value = PathProxy.SubscriptionUrls.GET_SUBSCRIPTION_BY_DOCTORID, notes = PathProxy.SubscriptionUrls.GET_SUBSCRIPTION_BY_DOCTORID)
	public Response<Subscription> getSubscriptionByDoctorId(@PathVariable("doctorId") String doctorId,
			@RequestParam(required = false, value = "packageName") PackageType packageName) {
		if (doctorId == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		Response<Subscription> response = new Response<Subscription>();
		response.setData(subscriptionService.getSubscriptionByDoctorId(doctorId,packageName));
		return response;

	}
	
	

	@Produces(MediaType.APPLICATION_JSON)
	@Path(value = PathProxy.SubscriptionUrls.GET_PACKAGES_BY_NAME)
	@GET
	@ApiOperation(value = PathProxy.SubscriptionUrls.GET_PACKAGES_BY_NAME, notes = PathProxy.SubscriptionUrls.GET_PACKAGES_BY_NAME)
	public Response<PackageDetailObject> getPackageDetailByPackageName(@RequestParam(required = true, value = "packageName") PackageType packageName) {
		if (packageName == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		Response<PackageDetailObject> response = new Response<PackageDetailObject>();
		response.setData(subscriptionService.getPackageDetailByPackageName(packageName));
		return response;

	}
	
	@Produces(MediaType.APPLICATION_JSON)
	@Path(value = PathProxy.SubscriptionUrls.ADD_EDIT_SUBSCRIPTION)
	@POST
	@ApiOperation(value = PathProxy.SubscriptionUrls.ADD_EDIT_SUBSCRIPTION, notes = PathProxy.SubscriptionUrls.ADD_EDIT_SUBSCRIPTION)
	public Response<Subscription> addEditSubscription(@RequestBody Subscription request) {

		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId())) {
			logger.warn("Invalid Input");			
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
	
		Response<Subscription> response = new Response<Subscription>();
		response.setData(subscriptionService.addEditSubscription(request));
		return response;
	}
}
