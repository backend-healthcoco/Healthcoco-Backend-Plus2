package com.dpdocter.webservices;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dpdocter.beans.Video;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.request.AddVideoRequest;
import com.dpdocter.services.VideoService;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataParam;

import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Component
@Path(PathProxy.VIDEO_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.VIDEO_BASE_URL, description = "Endpoint for videos")
public class VideoApi {
	
	private static Logger logger = Logger.getLogger(VideoApi.class.getName());

	 
	 @Autowired
	 VideoService videoService;
	 

	    @Path(value = PathProxy.VideoUrls.ADD_VIDEO)
	    @POST
	    @Consumes({ MediaType.MULTIPART_FORM_DATA })
	    @ApiOperation(value =PathProxy.VideoUrls.ADD_VIDEO, notes = PathProxy.VideoUrls.ADD_VIDEO)
	    public Response<Video> addVideo(@FormDataParam("file") FormDataBodyPart file,
				@FormDataParam("data") FormDataBodyPart data)
	    {
	    	data.setMediaType(MediaType.APPLICATION_JSON_TYPE);
			AddVideoRequest request = data.getValueAs(AddVideoRequest.class);
	    	if (file == null) {
	    	    logger.warn("Request send  is NULL");
	    	    throw new BusinessException(ServiceError.InvalidInput, "Request send  is NULL");
	    	}
	    	Video video = videoService.addVideo(file, request);
	    	Response<Video> response = new Response<Video>();
	    	response.setData(video);
	    	return response;
	    }
	    
	    @Path(value = PathProxy.VideoUrls.GET_VIDEO)
	    @GET
	    @ApiOperation(value =PathProxy.VideoUrls.GET_VIDEO, notes = PathProxy.VideoUrls.GET_VIDEO)
	    public Response<Video> getVideoss(@QueryParam("doctorId") String doctorId , @QueryParam("searchTerm") String searchTerm, @QueryParam("page") long page , @QueryParam("size") int size)
	    {
	    	List<Video> videos = null;
	    	if (doctorId == null) {
	    	    logger.warn("Request send  is NULL");
	    	    throw new BusinessException(ServiceError.InvalidInput, "Request send  is NULL");
	    	}
	    	videos = videoService.getVideos(doctorId, searchTerm, page, size);
	    	Response<Video> response = new Response<Video>();
	    	response.setDataList(videos);
	    	return response;
	    }


}
