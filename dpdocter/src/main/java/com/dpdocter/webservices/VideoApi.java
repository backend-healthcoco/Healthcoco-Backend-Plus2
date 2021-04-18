package com.dpdocter.webservices;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.Produces;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.dpdocter.beans.MyVideo;
import com.dpdocter.beans.Video;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.request.AddMyVideoRequest;
import com.dpdocter.request.AddVideoRequest;
import com.dpdocter.services.VideoService;

import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
(PathProxy.VIDEO_BASE_URL)
@Produces(MediaType.APPLICATION_JSON_VALUE)
@Consumes(MediaType.APPLICATION_JSON_VALUE)
@Api(value = PathProxy.VIDEO_BASE_URL, description = "Endpoint for videos")
public class VideoApi {
	
	private static Logger logger = LogManager.getLogger(VideoApi.class.getName());

	 
	 @Autowired
	 VideoService videoService;
	 

	    
	    @PostMapping(value = PathProxy.VideoUrls.ADD_VIDEO)
	    @Consumes({ MediaType.MULTIPART_FORM_DATA_VALUE})
	    @ApiOperation(value =PathProxy.VideoUrls.ADD_VIDEO, notes = PathProxy.VideoUrls.ADD_VIDEO)
	    public Response<Video> addVideo(@RequestParam("file") MultipartFile file, @RequestBody AddVideoRequest request){
	    	
	    	if (file == null) {
	    	    logger.warn("Request send  is NULL");
	    	    throw new BusinessException(ServiceError.InvalidInput, "Request send  is NULL");
	    	}
	    	Video video = videoService.addVideo(file, request);
	    	Response<Video> response = new Response<Video>();
	    	response.setData(video);
	    	return response;
	    }
	    
	    
	    @GetMapping(value = PathProxy.VideoUrls.GET_VIDEO)
	    @ApiOperation(value =PathProxy.VideoUrls.GET_VIDEO, notes = PathProxy.VideoUrls.GET_VIDEO)
	    public Response<Video> getVideoss(@RequestParam(value = "doctorId") String doctorId , @RequestParam(value = "searchTerm") String searchTerm, @MatrixParam(value ="tags") List<String> tags, @RequestParam(value = "page") int page , @RequestParam(value = "size") int size)
	    {
	    	List<Video> videos = null;
	    	if (doctorId == null) {
	    	    logger.warn("Request send  is NULL");
	    	    throw new BusinessException(ServiceError.InvalidInput, "Request send  is NULL");
	    	}
	    	videos = videoService.getVideos(doctorId, searchTerm, tags, page, size);
	    	Response<Video> response = new Response<Video>();
	    	response.setDataList(videos);
	    	return response;
	    }
	    
	   
	    @PostMapping(value = PathProxy.VideoUrls.ADD_MY_VIDEO)
	    @Consumes({ MediaType.MULTIPART_FORM_DATA_VALUE})
	    @ApiOperation(value =PathProxy.VideoUrls.ADD_MY_VIDEO, notes = PathProxy.VideoUrls.ADD_MY_VIDEO)
	    public Response<MyVideo> addMyVideo(@RequestParam("file") MultipartFile file, @RequestBody AddMyVideoRequest request )
	    {
	    	if (file == null) {
	    	    logger.warn("Request send  is NULL");
	    	    throw new BusinessException(ServiceError.InvalidInput, "Request send  is NULL");
	    	}
	    	MyVideo video = videoService.addMyVideo(file, request);
	    	Response<MyVideo> response = new Response<MyVideo>();
	    	response.setData(video);
	    	return response;
	    }


}
