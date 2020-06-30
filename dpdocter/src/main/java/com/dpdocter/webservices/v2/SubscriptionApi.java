package com.dpdocter.webservices.v2;

import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
@Api(value = PathProxy.SUBSCRIPTION_BASE_URL, description = "Endpoint for Subscription")
@RequestMapping(value = PathProxy.SUBSCRIPTION_BASE_URL, produces = MediaType.APPLICATION_JSON, consumes = MediaType.APPLICATION_JSON)
public class SubscriptionApi {

	private static Logger logger = LogManager.getLogger(SubscriptionApi.class.getName());

	@Autowired
	private SubscriptionService subscriptionService;
	
	@GetMapping(value = PathProxy.SubscriptionUrls.GET_SUBSCRIPTION_BY_DOCTORID)
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
	
	@GetMapping(value = PathProxy.SubscriptionUrls.GET_PACKAGES_BY_NAME)
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
	
	@PostMapping(value = PathProxy.SubscriptionUrls.ADD_EDIT_SUBSCRIPTION)
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
