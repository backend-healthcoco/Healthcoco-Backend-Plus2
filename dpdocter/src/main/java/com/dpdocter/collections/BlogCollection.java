package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.enums.BlogCategoryType;
import com.dpdocter.enums.BlogSuperCategoryType;

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
	private BlogSuperCategoryType superCategory;

	@Field
	private BlogCategoryType category;

	@Field
	private ObjectId articleId;

	@Field
	private ObjectId userId;

	@Field
	private Integer noOfLikes = 0;

	@Field
	private Integer views = 0;

	@Field
	private Boolean discarded = false;

	@Field
	private String postBy;

	@Field
	private String shortDesc;

	@Field
	private String metaKeyword;

	@Field
	private String metaDesc;

	@Field
	@Indexed(unique = true)

	private String slugURL;

	@Field
	private Boolean isSmilebirdBlog = false;

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

	public BlogSuperCategoryType getSuperCategory() {
		return superCategory;
	}

	public void setSuperCategory(BlogSuperCategoryType superCategory) {
		this.superCategory = superCategory;
	}

	public Integer getViews() {
		return views;
	}

	public void setViews(Integer views) {
		this.views = views;
	}

	@Override
	public String toString() {
		return "BlogCollection [id=" + id + ", title=" + title + ", titleImage=" + titleImage + ", isActive=" + isActive
				+ ", superCategory=" + superCategory + ", category=" + category + ", articleId=" + articleId
				+ ", userId=" + userId + ", noOfLikes=" + noOfLikes + ", views=" + views + ", discarded=" + discarded
				+ ", postBy=" + postBy + ", shortDesc=" + shortDesc + ", metaKeyword=" + metaKeyword + ", metaDesc="
				+ metaDesc + ", slugURL=" + slugURL + "]";
	}

	public Integer getNoOfLikes() {
		return noOfLikes;
	}

	public void setNoOfLikes(Integer noOfLikes) {
		this.noOfLikes = noOfLikes;
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

	public Boolean getIsSmilebirdBlog() {
		return isSmilebirdBlog;
	}

	public void setIsSmilebirdBlog(Boolean isSmilebirdBlog) {
		this.isSmilebirdBlog = isSmilebirdBlog;
	}

}
