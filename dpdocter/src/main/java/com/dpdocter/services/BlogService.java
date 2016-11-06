package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.Blog;

public interface BlogService {
	public List<Blog> getBlogs(int size, int page, String category, String userId, String title);

	public Blog getBlog(String id,String userId);

	public Blog updateLikes(String id, String userId);
}
