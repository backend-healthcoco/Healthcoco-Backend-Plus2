package com.dpdocter.webservices;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.Produces;
import org.springframework.http.MediaType;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.dpdocter.beans.UserDevice;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.request.BroadcastNotificationRequest;
import com.dpdocter.services.PushNotificationServices;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value=PathProxy.PUSH_NOTIFICATION_BASE_URL,consumes =MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
@Api(value = PathProxy.PUSH_NOTIFICATION_BASE_URL, description = "Endpoint for push notification")
public class PushNotificationApi {

    private static Logger logger = LogManager.getLogger(PushNotificationApi.class.getName());
	
	@Autowired
	PushNotificationServices pushNotificationServices;
	
	
	@PostMapping(value = PathProxy.PushNotificationUrls.ADD_DEVICE)
	@ApiOperation(value = PathProxy.PushNotificationUrls.ADD_DEVICE, notes = PathProxy.PushNotificationUrls.ADD_DEVICE)
	public Response<UserDevice> addDevice(UserDevice request){
		if(request == null || DPDoctorUtils.anyStringEmpty(request.getDeviceId(), request.getPushToken()) || request.getDeviceType() == null || request.getRole() == null){
			    logger.warn("Invalid Input");
			    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		UserDevice userDevice = pushNotificationServices.addDevice(request);
		Response<UserDevice> response = new Response<UserDevice>();
		response.setData(userDevice);
		return response;
	}
	
	
	@PostMapping(value = PathProxy.PushNotificationUrls.BROADCAST_NOTIFICATION)
	@Consumes({ MediaType.MULTIPART_FORM_DATA_VALUE })
	@ApiOperation(value = PathProxy.PushNotificationUrls.BROADCAST_NOTIFICATION, notes = PathProxy.PushNotificationUrls.BROADCAST_NOTIFICATION)
	public Response<Boolean> broadcastNotification(@RequestParam("file") MultipartFile file, BroadcastNotificationRequest request){
		if(request == null){
			    logger.warn("Invalid Input");
			    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		pushNotificationServices.broadcastNotification(file, request);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(true);
		return response;
	}
	
	
	@PostMapping(value = PathProxy.PushNotificationUrls.READ_NOTIFICATION)
	@ApiOperation(value = PathProxy.PushNotificationUrls.READ_NOTIFICATION, notes = PathProxy.PushNotificationUrls.READ_NOTIFICATION)
	public Response<Boolean> readNotification(@PathVariable(value = "deviceId") String deviceId,
		    @DefaultValue("0") @RequestParam("count") Integer count){
		if(DPDoctorUtils.anyStringEmpty(deviceId)){
			    logger.warn("Invalid Input");
			    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		pushNotificationServices.readNotification(deviceId, count);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(true);
		return response;
	}
}
