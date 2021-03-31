package com.dpdocter.webservices;

import javax.mail.MessagingException;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dpdocter.services.UnifiedCommunicationServices;

import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
(PathProxy.UNIFIED_COMMUNICATION_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.UNIFIED_COMMUNICATION_BASE_URL, description = "Endpoint for unified communication")
public class UnifiedCommunicationApi {

	private static Logger logger = LogManager.getLogger(UnifiedCommunicationApi.class.getName());
	
	@Autowired
	UnifiedCommunicationServices unifiedCommunicationServices;

	
	@GetMapping(value = PathProxy.ChatUrls.CREATE_CHAT_ACCESS_TOKEN)
	@ApiOperation(value = PathProxy.ChatUrls.CREATE_CHAT_ACCESS_TOKEN, notes = PathProxy.ChatUrls.CREATE_CHAT_ACCESS_TOKEN)
	public Response<String> createChatAccessToken(@PathVariable("userId") String userId)
			throws MessagingException {
	    
		Response<String> response = new Response<String>();
		response.setData(unifiedCommunicationServices.createChatAccessToken(userId));
		return response;

	}
	
	
	
	@GetMapping(value = PathProxy.ChatUrls.CREATE_USER)
	@ApiOperation(value = PathProxy.ChatUrls.CREATE_USER, notes = PathProxy.ChatUrls.CREATE_USER)
	public Response<String> createUser(@PathVariable("identity") String identity)
			throws MessagingException {
	    
		Response<String> response = new Response<String>();
		response.setData(unifiedCommunicationServices.createUser(identity));
		return response;

	}
	
	
	@GetMapping(value = PathProxy.ChatUrls.CREATE_VIDEO_ACCESS_TOKEN)
	@ApiOperation(value = PathProxy.ChatUrls.CREATE_VIDEO_ACCESS_TOKEN, notes = PathProxy.ChatUrls.CREATE_VIDEO_ACCESS_TOKEN)
	public Response<String> createVideoAccessToken(@PathVariable("userId") String userId, @PathVariable("room") String room)
			throws MessagingException {
	    
		Response<String> response = new Response<String>();
		response.setData(unifiedCommunicationServices.createVideoAccessToken(userId, room));
		return response;

	}
	

	
	@GetMapping(value = PathProxy.ChatUrls.CREATE_TWILIO_NOTIFICATION)
	@ApiOperation(value = PathProxy.ChatUrls.CREATE_TWILIO_NOTIFICATION, notes = PathProxy.ChatUrls.CREATE_TWILIO_NOTIFICATION)
	public Response<Boolean> createTwilioPushNotifition(@RequestParam(value = "serviceSID")String serviceSID)
			throws MessagingException {
	    
		Response<Boolean> response = new Response<Boolean>();
		Boolean push=unifiedCommunicationServices.twilioPushNotification(serviceSID);
		response.setData(push);
		return response;

	}
	

	
	@GetMapping(value = PathProxy.ChatUrls.CREATE_PUSH_NOTIFICATION)
	@ApiOperation(value = PathProxy.ChatUrls.CREATE_PUSH_NOTIFICATION, notes = PathProxy.ChatUrls.CREATE_PUSH_NOTIFICATION)
	public Response<Boolean> createPushNotifition(@RequestParam(value = "userId")String userId,@RequestParam(value = "room")String room,
			@RequestParam(value = "title")String title,@RequestParam(value = "callType")String callType)

			throws MessagingException {
	    
		Response<Boolean> response = new Response<Boolean>();
		Boolean push=unifiedCommunicationServices.createpushNotification(userId, room, title,callType);
		response.setData(push);
		return response;

	}
	
	
	@GetMapping(value = PathProxy.ChatUrls.CREATE_CHAT_ACCESS_TOKEN_ANDROID)
	@ApiOperation(value = PathProxy.ChatUrls.CREATE_CHAT_ACCESS_TOKEN_ANDROID, notes = PathProxy.ChatUrls.CREATE_CHAT_ACCESS_TOKEN_ANDROID)
	public Response<String> createChatAccessToken(@PathVariable("userId") String userId,@RequestParam(value = "pushCredentialSID")String pushCredentialSID)
			throws MessagingException {
	    
		Response<String> response = new Response<String>();
		response.setData(unifiedCommunicationServices.createChatAccessTokenAndroid(userId, pushCredentialSID));
		return response;

	}
	

	


}
