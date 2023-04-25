package com.dpdocter.response;

import java.util.List;

import com.dpdocter.beans.Blog;
import com.dpdocter.enums.BlogSuperCategoryType;

public class BlogResponse {

	private List<Blog> blogs;

	private int totalsize;

	private BlogSuperCategoryType superCategory;

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

	public BlogSuperCategoryType getSuperCategory() {
		return superCategory;
	}

	public void setSuperCategory(BlogSuperCategoryType superCategory) {
		this.superCategory = superCategory;
	}

	@Override
	public String toString() {
		return "BlogResponse [blogs=" + blogs + ", totalsize=" + totalsize + ", superCategory=" + superCategory + "]";
	}
}
