package com.dpdocter.response;

import java.util.List;

import com.dpdocter.beans.Blog;

public class BlogResponse {
	private List<Blog> blogs;
	private int totalsize;
	public List<Blog> getBlogs() {
		return blogs;
	}
	public void setBlogs(List<Blog> blogs) {
		this.blogs = blogs;
	}
	public int getTotalsize() {
		return totalsize;
	}
	public void setTotalsize(int totalsize) {
		this.totalsize = totalsize;
	}
	
}
