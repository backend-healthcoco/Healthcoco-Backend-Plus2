package com.dpdocter.webservices;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dpdocter.beans.Blog;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.request.BlogRequest;
import com.dpdocter.response.BlogResponse;
import com.dpdocter.services.BlogService;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Component
@Path(PathProxy.BLOGS_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.BLOGS_BASE_URL, description = "Endpoint for blog")

public class BlogApi {
//	private static Logger logger = LogManager.getLogger(BlogApi.class.getName());

	@Autowired
	BlogService blogService;

	@Path(value = PathProxy.BlogsUrls.GET_BLOGS)
	@GET
	@ApiOperation(value = PathProxy.BlogsUrls.GET_BLOGS, notes = PathProxy.BlogsUrls.GET_BLOGS)
	public Response<BlogResponse> getBlogs(@QueryParam(value = "size") int size, @QueryParam(value = "page") long page,
			@QueryParam(value = "userId") String userId, @QueryParam(value = "category") String category,
			@QueryParam(value = "title") String title) {
		BlogResponse blogresponse = blogService.getBlogs(size, page, category, userId, title);
		Response<BlogResponse> response = new Response<BlogResponse>();
		response.setData(blogresponse);
		return response;
	}

	@Path(value = PathProxy.BlogsUrls.GET_BLOG_LIST)
	@GET
	@ApiOperation(value = PathProxy.BlogsUrls.GET_BLOG_LIST, notes = PathProxy.BlogsUrls.GET_BLOG_LIST)
	public Response<Object> getBlogList(@QueryParam(value = "size") int size, @QueryParam(value = "page") long page,
			@QueryParam(value = "userId") String userId, @QueryParam(value = "category") String category,
			@QueryParam(value = "title") String title) {
		BlogResponse blogresponse = blogService.getBlogs(size, page, category, userId, title);
		Response<Object> response = new Response<Object>();
		response.setData(blogresponse.getTotalsize());
		response.setDataList(blogresponse.getBlogs());
		return response;
	}

	@Path(value = PathProxy.BlogsUrls.GET_BLOG_BY_SLUG_URL)
	@GET
	@ApiOperation(value = PathProxy.BlogsUrls.GET_BLOG_BY_SLUG_URL, notes = PathProxy.BlogsUrls.GET_BLOG_BY_SLUG_URL)
	public Response<Blog> getBlogBySlugURL(@PathParam("slugURL") String slugURL, @QueryParam("userId") String userId) {
		Blog blogresponse = blogService.getBlog(null, slugURL, userId);
		if (DPDoctorUtils.anyStringEmpty(slugURL)) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid input");

		}
		Response<Blog> response = new Response<Blog>();
		response.setData(blogresponse);
		return response;
	}

	@Path(value = PathProxy.BlogsUrls.GET_BLOG_BY_ID)
	@GET
	@ApiOperation(value = PathProxy.BlogsUrls.GET_BLOG_BY_ID, notes = PathProxy.BlogsUrls.GET_BLOG_BY_ID)
	public Response<Blog> getBlog(@PathParam("blogId") String blogId, @QueryParam("userId") String userId) {
		Blog blogresponse = blogService.getBlog(blogId, null, userId);
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

	@Path(value = PathProxy.BlogsUrls.GET__MOST_LIKES_OR_VIEWED_BLOGS)
	@GET
	@ApiOperation(value = PathProxy.BlogsUrls.GET__MOST_LIKES_OR_VIEWED_BLOGS, notes = PathProxy.BlogsUrls.GET__MOST_LIKES_OR_VIEWED_BLOGS)
	public Response<Blog> getBlogs(@QueryParam(value = "size") int size, @QueryParam(value = "page") long page,
			@QueryParam(value = "userId") String userId, @QueryParam(value = "category") String category,
			@QueryParam(value = "title") String title, @QueryParam(value = "forMostLike") boolean forMostLike) {
		List<Blog> blogresponse = blogService.getMostLikedOrViewedBlogs(size, page, category, title, userId,
				forMostLike);
		Response<Blog> response = new Response<Blog>();
		response.setDataList(blogresponse);
		return response;
	}

	@Path(value = PathProxy.BlogsUrls.ADD_EDIT_FEVOURITE_BLOGS)
	@GET
	@ApiOperation(value = PathProxy.BlogsUrls.ADD_EDIT_FEVOURITE_BLOGS, notes = PathProxy.BlogsUrls.ADD_EDIT_FEVOURITE_BLOGS)
	public Response<Boolean> addFevouriteBlogs(@PathParam("blogId") String blogId, @PathParam("userId") String userId) {
		Boolean added = blogService.addFevouriteBlog(blogId, userId);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(added);
		return response;
	}

	@Path(value = PathProxy.BlogsUrls.GET_FEVOURITE_BLOGS)
	@GET
	@ApiOperation(value = PathProxy.BlogsUrls.GET_FEVOURITE_BLOGS, notes = PathProxy.BlogsUrls.GET_FEVOURITE_BLOGS)
	public Response<Object> getFevouriteBlogs(@QueryParam(value = "size") int size,
			@QueryParam(value = "page") long page, @QueryParam(value = "userId") String userId,
			@QueryParam(value = "category") String category, @QueryParam(value = "title") String title) {
		List<Blog> blogList = blogService.getFevouriteBlogs(size, page, category, userId, title);
		Response<Object> response = new Response<Object>();
		response.setDataList(blogList);
		return response;
	}

	@Path(value = PathProxy.BlogsUrls.GET_BLOGS_CATEGORY)
	@GET
	@ApiOperation(value = PathProxy.BlogsUrls.GET_BLOGS_CATEGORY, notes = PathProxy.BlogsUrls.GET_BLOGS_CATEGORY)
	public Response<Object> getBlogCategory() {

		Response<Object> response = new Response<Object>();
		response.setData(blogService.getBlogCategory());
		return response;
	}

	@Path(value = PathProxy.BlogsUrls.GET_BLOGS_BY_CATEGORY)
	@POST
	@ApiOperation(value = PathProxy.BlogsUrls.GET_BLOGS_BY_CATEGORY, notes = PathProxy.BlogsUrls.GET_BLOGS_BY_CATEGORY)
	public Response<BlogResponse> getBlogs(BlogRequest request) {
		List<BlogResponse> blogresponses = blogService.getBlogs(request);
		Response<BlogResponse> response = new Response<BlogResponse>();
		response.setDataList(blogresponses);
		return response;
	}
}
