package com.dpdocter.webservices;

import java.util.List;

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

import com.dpdocter.beans.Blog;
import com.dpdocter.services.BlogService;

import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Component
@Path(PathProxy.BLOGS_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.BLOGS_BASE_URL, description = "Endpoint for blog")

public class BlogApi {
	private static Logger logger = Logger.getLogger(BlogApi.class.getName());

	@Autowired
	BlogService blogService;

	@Path(value = PathProxy.BlogsUrls.GET_BLOGS)
	@GET
	@ApiOperation(value = PathProxy.BlogsUrls.GET_BLOGS, notes = PathProxy.BlogsUrls.GET_BLOGS)
	public Response<Blog> getBlogs(@QueryParam(value = "size") int size, @QueryParam(value = "page") int page,
			@QueryParam(value = "userId") String userId, @QueryParam(value = "category") String category,
			@QueryParam(value = "title") String title) {
		List<Blog> blogresponse = blogService.getBlogs(size, page, category, userId, title);
		Response<Blog> response = new Response<Blog>();
		response.setDataList(blogresponse);
		return response;
	}

	@Path(value = PathProxy.BlogsUrls.GET_BLOG_BY_ID)
	@GET
	@ApiOperation(value = PathProxy.BlogsUrls.GET_BLOG_BY_ID, notes = PathProxy.BlogsUrls.GET_BLOG_BY_ID)
	public Response<Blog> getBlog(@PathParam("blogId") String blogId, @QueryParam("userId") String userId) {
		Blog blogresponse = blogService.getBlog(blogId, userId);
		Response<Blog> response = new Response<Blog>();
		response.setData(blogresponse);
		return response;
	}

	@Path(value = PathProxy.BlogsUrls.LIKE_THE_BLOG)
	@GET
	@ApiOperation(value = PathProxy.BlogsUrls.LIKE_THE_BLOG, notes = PathProxy.BlogsUrls.LIKE_THE_BLOG)
	public Response<Blog> likeTheBlog(@PathParam("blogId") String blogId, @PathParam("userId") String userId) {
		Blog blogresponse = blogService.updateLikes(blogId, userId);
		Response<Blog> response = new Response<Blog>();
		response.setData(blogresponse);
		return response;
	}

}
