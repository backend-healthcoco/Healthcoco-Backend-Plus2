package com.dpdocter.webservices;

import javax.mail.MessagingException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dpdocter.services.UnifiedCommunicationServices;

import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Component
@Path(PathProxy.UNIFIED_COMMUNICATION_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.UNIFIED_COMMUNICATION_BASE_URL, description = "Endpoint for unified communication")
public class UnifiedCommunicationApi {

	private static Logger logger = Logger.getLogger(UnifiedCommunicationApi.class.getName());
	
	@Autowired
	UnifiedCommunicationServices unifiedCommunicationServices;

	@Path(value = PathProxy.ChatUrls.CREATE_CHAT_ACCESS_TOKEN)
	@GET
	@ApiOperation(value = PathProxy.ChatUrls.CREATE_CHAT_ACCESS_TOKEN, notes = PathProxy.ChatUrls.CREATE_CHAT_ACCESS_TOKEN)
	public Response<String> createChatAccessToken(@PathParam("userId") String userId)
			throws MessagingException {
	    
		Response<String> response = new Response<String>();
		response.setData(unifiedCommunicationServices.createChatAccessToken(userId));
		return response;

	}
	
	
	@Path(value = PathProxy.ChatUrls.CREATE_USER)
	@GET
	@ApiOperation(value = PathProxy.ChatUrls.CREATE_USER, notes = PathProxy.ChatUrls.CREATE_USER)
	public Response<String> createUser(@PathParam("identity") String identity)
			throws MessagingException {
	    
		Response<String> response = new Response<String>();
		response.setData(unifiedCommunicationServices.createUser(identity));
		return response;

	}
	
	@Path(value = PathProxy.ChatUrls.CREATE_VIDEO_ACCESS_TOKEN)
	@GET
	@ApiOperation(value = PathProxy.ChatUrls.CREATE_VIDEO_ACCESS_TOKEN, notes = PathProxy.ChatUrls.CREATE_VIDEO_ACCESS_TOKEN)
	public Response<String> createVideoAccessToken(@PathParam("userId") String userId, @PathParam("room") String room)
			throws MessagingException {
	    
		Response<String> response = new Response<String>();
		response.setData(unifiedCommunicationServices.createVideoAccessToken(userId, room));
		return response;

	}
	

	@Path(value = PathProxy.ChatUrls.CREATE_TWILIO_NOTIFICATION)
	@GET
	@ApiOperation(value = PathProxy.ChatUrls.CREATE_TWILIO_NOTIFICATION, notes = PathProxy.ChatUrls.CREATE_TWILIO_NOTIFICATION)
	public Response<Boolean> createTwilioPushNotifition(@QueryParam(value = "serviceSID")String serviceSID)
			throws MessagingException {
	    
		Response<Boolean> response = new Response<Boolean>();
		Boolean push=unifiedCommunicationServices.twilioPushNotification(serviceSID);
		response.setData(push);
		return response;

	}
	

	@Path(value = PathProxy.ChatUrls.CREATE_PUSH_NOTIFICATION)
	@GET
	@ApiOperation(value = PathProxy.ChatUrls.CREATE_PUSH_NOTIFICATION, notes = PathProxy.ChatUrls.CREATE_PUSH_NOTIFICATION)
	public Response<Boolean> createPushNotifition(@QueryParam(value = "userId")String userId,@QueryParam(value = "room")String room,
			@QueryParam(value = "title")String title,@QueryParam(value = "callType")String callType)

			throws MessagingException {
	    
		Response<Boolean> response = new Response<Boolean>();
		Boolean push=unifiedCommunicationServices.createpushNotification(userId, room, title,callType);
		response.setData(push);
		return response;

	}
	
	@Path(value = PathProxy.ChatUrls.CREATE_CHAT_ACCESS_TOKEN_ANDROID)
	@GET
	@ApiOperation(value = PathProxy.ChatUrls.CREATE_CHAT_ACCESS_TOKEN_ANDROID, notes = PathProxy.ChatUrls.CREATE_CHAT_ACCESS_TOKEN_ANDROID)
	public Response<String> createChatAccessToken(@PathParam("userId") String userId,@QueryParam(value = "pushCredentialSID")String pushCredentialSID)
			throws MessagingException {
	    
		Response<String> response = new Response<String>();
		response.setData(unifiedCommunicationServices.createChatAccessTokenAndroid(userId, pushCredentialSID));
		return response;

	}
	

	


}
