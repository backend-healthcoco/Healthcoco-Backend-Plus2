package com.dpdocter.beans;

import com.dpdocter.collections.GenericCollection;
import com.dpdocter.enums.BlogCategoryType;
import com.dpdocter.enums.BlogSuperCategoryType;

public class Blog extends GenericCollection {
	private String id;

	private String title;

	private String titleImage;

	private BlogSuperCategoryType superCategory;
	
	private BlogCategoryType category;

	private String articleId;

	private Boolean isActive = true;

	private String article;

	private Integer noOfLikes = 0;

	private Boolean isliked = false;

	private Integer views = 0;

	private String userId;

	private String postBy;

	private Boolean discarded = false;

	private Boolean isFavourite = false;

	private String shortDesc;

	private String metaKeyword;

	private String metaDesc;	
	
	private String slugURL; 

	public String getSlugURL() {
		return slugURL;
	}

	public void setSlugURL(String slugURL) {
		this.slugURL = slugURL;
	}

	public String getPostBy() {
		return postBy;
	}

	public void setPostBy(String postBy) {
		this.postBy = postBy;
	}

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

	public String getShortDesc() {
		return shortDesc;
	}

	public void setShortDesc(String shortDesc) {
		this.shortDesc = shortDesc;
	}

	public String getMetaKeyword() {
		return metaKeyword;
	}

	public void setMetaKeyword(String metaKeyword) {
		this.metaKeyword = metaKeyword;
	}

	public String getMetaDesc() {
		return metaDesc;
	}

	public void setMetaDesc(String metaDesc) {
		this.metaDesc = metaDesc;
	}

	public BlogSuperCategoryType getSuperCategory() {
		return superCategory;
	}

	public void setSuperCategory(BlogSuperCategoryType superCategory) {
		this.superCategory = superCategory;
	}

	@Override
	public String toString() {
		return "Blog [id=" + id + ", title=" + title + ", titleImage=" + titleImage + ", superCategory=" + superCategory
				+ ", category=" + category + ", articleId=" + articleId + ", isActive=" + isActive + ", article="
				+ article + ", noOfLikes=" + noOfLikes + ", isliked=" + isliked + ", views=" + views + ", userId="
				+ userId + ", postBy=" + postBy + ", discarded=" + discarded + ", isFavourite=" + isFavourite
				+ ", shortDesc=" + shortDesc + ", metaKeyword=" + metaKeyword + ", metaDesc=" + metaDesc + ", slugURL="
				+ slugURL + "]";
	}

}
