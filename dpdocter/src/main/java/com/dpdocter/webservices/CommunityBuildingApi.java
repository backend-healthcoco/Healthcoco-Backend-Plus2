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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.dpdocter.beans.Comment;
import com.dpdocter.beans.CommentRequest;
import com.dpdocter.beans.ForumRequest;
import com.dpdocter.beans.ForumResponse;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.services.CommunityBuildingService;


import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;


@Component
@Path(PathProxy.COMMUNITY_BUILDING_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.COMMUNITY_BUILDING_BASE_URL, description = "Endpoint for records")
public class CommunityBuildingApi {
	
private static Logger logger = LogManager.getLogger(CommunityBuildingApi.class.getName());
	
	@Autowired
	private CommunityBuildingService communityBuildingServices;
	

	@POST
	@Path(value = PathProxy.CommunityBuildingUrls.ADD_EDIT_FORUM)
	@ApiOperation(value = PathProxy.CommunityBuildingUrls.ADD_EDIT_FORUM, notes = PathProxy.CommunityBuildingUrls.ADD_EDIT_FORUM)
	public Response<ForumResponse> addEditDietPlan(@RequestBody ForumRequest request) {

		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		

		Response<ForumResponse> response = new Response<ForumResponse>();
		response.setData(communityBuildingServices.addEditForumResponse(request));
		return response;
	}
	
	@GET
	@Path(value = PathProxy.CommunityBuildingUrls.GET_FORUM_RESPONSES)
	@ApiOperation(value = PathProxy.CommunityBuildingUrls.GET_FORUM_RESPONSES, notes = PathProxy.CommunityBuildingUrls.GET_FORUM_RESPONSES)
	public Response<ForumResponse> getForumResponse(@DefaultValue("0")@QueryParam(value ="size") int size, 
			@DefaultValue("0")	@QueryParam( value ="page") int page,
			@QueryParam(value ="discarded") Boolean discarded, 
			@QueryParam(value ="searchTerm") String searchTerm) {
		Response<ForumResponse> response = new Response<ForumResponse>();
		response.setDataList(communityBuildingServices.getForumResponse(page, size, searchTerm, discarded));
		return response;
	}
	
	@GET
	@Path(value = PathProxy.CommunityBuildingUrls.GET_FORUM_RESPONSE_BY_ID)
	@ApiOperation(value = PathProxy.CommunityBuildingUrls.GET_FORUM_RESPONSE_BY_ID, notes = PathProxy.CommunityBuildingUrls.GET_FORUM_RESPONSE_BY_ID)
	public Response<ForumResponse> getArticles(
			@PathParam(value = "id") String id) {
		Response<ForumResponse> response = new Response<ForumResponse>();
		response.setData(communityBuildingServices.getForumResponseById(id));
		return response;
	}
	
	@POST
	@Path(value = PathProxy.CommunityBuildingUrls.ADD_EDIT_COMMENTS)
	@ApiOperation(value = PathProxy.CommunityBuildingUrls.ADD_EDIT_COMMENTS, notes = PathProxy.CommunityBuildingUrls.ADD_EDIT_COMMENTS)
	public Response<Comment> addeditLikes(@RequestBody CommentRequest request) {
		Response<Comment> response = new Response<Comment>();
		Comment feeds=communityBuildingServices.addEditComment(request);
		response.setData(feeds);
		return response;
	}
	
}
