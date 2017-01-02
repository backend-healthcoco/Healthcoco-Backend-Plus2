package com.dpdocter.beans;

import com.dpdocter.collections.GenericCollection;
import com.dpdocter.enums.BlogCategoryType;

public class Blog extends GenericCollection {
	private String id;

	private String title;

	private String titleImage;

	private BlogCategoryType category;

	private String articleId;

	private Boolean isActive = true;

	private String article;

	private Integer noOfLikes = 0;

	private Boolean isliked = false;

	private Integer views = 0;

	private String userId;

	private Boolean discarded = false;

	private Boolean isFavourite = false;

	public Boolean getIsFavourite() {
		return isFavourite;
	}

	public void setIsFavourite(Boolean isFavourite) {
		this.isFavourite = isFavourite;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getArticle() {
		return article;
	}

	public void setArticle(String article) {
		this.article = article;
	}

	public BlogCategoryType getCategory() {
		return category;
	}

	public void setCategory(BlogCategoryType category) {
		this.category = category;
	}

	public String getTitleImage() {
		return titleImage;
	}

	public void setTitleImage(String titleImage) {
		this.titleImage = titleImage;
	}

	public String getArticleId() {
		return articleId;
	}

	public void setArticleId(String articleId) {
		this.articleId = articleId;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	public Integer getViews() {
		return views;
	}

	public void setViews(Integer views) {
		this.views = views;
	}

	public Integer getNoOfLikes() {
		return noOfLikes;
	}

	public void setNoOfLikes(Integer noOfLikes) {
		this.noOfLikes = noOfLikes;
	}

	public Boolean getIsliked() {
		return isliked;
	}

	public void setIsliked(Boolean isliked) {
		this.isliked = isliked;
	}

}
