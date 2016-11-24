package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.enums.BlogCategoryType;

@Document(collection = "blog_cl")
public class BlogCollection extends GenericCollection {

	@Id
	private ObjectId id;

	@Field
	private String title;

	@Field
	private String titleImage;

	@Field
	private Boolean isActive = true;

	@Field
	private BlogCategoryType category;

	@Field
	private ObjectId articleId;

	@Field
	private ObjectId userId;

	private Integer noOfLikes = 0;

	@Field
	private Integer views = 0;

	@Field
	private Boolean discarded = false;

	public ObjectId getUserId() {
		return userId;
	}

	public void setUserId(ObjectId userId) {
		this.userId = userId;
	}

	public String getTitleImage() {
		return titleImage;
	}

	public void setTitleImage(String titleImage) {
		this.titleImage = titleImage;
	}

	public void setArticleId(ObjectId articleId) {
		this.articleId = articleId;
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public BlogCategoryType getCategory() {
		return category;
	}

	public void setCategory(BlogCategoryType category) {
		this.category = category;
	}

	public ObjectId getArticleId() {
		return articleId;
	}

	public void setArticleiId(ObjectId articleId) {
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

	@Override
	public String toString() {
		return "BlogCollection [id=" + id + ", title=" + title + "isActive=" + isActive + ", category=" + category
				+ ", articleiId=" + articleId + "]";
	}

	public Integer getNoOfLikes() {
		return noOfLikes;
	}

	public void setNoOfLikes(Integer noOfLikes) {
		this.noOfLikes = noOfLikes;
	}

}
