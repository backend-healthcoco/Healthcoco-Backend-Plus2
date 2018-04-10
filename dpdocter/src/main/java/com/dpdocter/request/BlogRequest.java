package com.dpdocter.request;

import java.util.List;

public class BlogRequest {

	private List<BlogCategoryWithPageSize> blogSuperCategories;
	
	private String userId;
	
	private String category;
	
	private String title;

	public List<BlogCategoryWithPageSize> getBlogSuperCategories() {
		return blogSuperCategories;
	}

	public void setBlogSuperCategories(List<BlogCategoryWithPageSize> blogSuperCategories) {
		this.blogSuperCategories = blogSuperCategories;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public String toString() {
		return "BlogRequest [blogSuperCategories=" + blogSuperCategories + ", userId=" + userId + ", category="
				+ category + ", title=" + title + "]";
	}
}
