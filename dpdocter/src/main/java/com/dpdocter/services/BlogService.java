package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.Blog;
import com.dpdocter.enums.BlogCategoryType;
import com.dpdocter.response.BlogResponse;

public interface BlogService {
	public BlogResponse getBlogs(int size, int page, String category, String userId, String title);

	public Blog getBlog(String id, String userId);

	public Blog updateLikes(String id, String userId);

	public List<Blog> getMostLikedOrViewedBlogs(int size, int page, String category, String title, String userId,
			Boolean forMostLiked);

	public Boolean addFevouriteBlog(String blogId, String userId);

	public List<Blog> getFevouriteBlogs(int size, int page, String category, String userId, String title);

	public BlogCategoryType[] getBlogCategory();
}
