package com.dpdocter.collections;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.ArticleDetails;
import com.dpdocter.beans.Comment;
import com.dpdocter.beans.UserPost;
import com.dpdocter.enums.CommunityType;
import com.dpdocter.enums.UserType;

@Document(collection = "feeds_cl")
public class FeedsCollection extends GenericCollection {

	@Id
	private ObjectId id;
	@Field
	private UserPost user;
	@Field
	private List<ArticleDetails> multilingual;
	@Field
	private CommunityType type;
	@Field
	private ObjectId userId;
	@Field
	private String userName;
	@Field
	private UserType userType;
	@Field
	private String speciality;
	@Field
	private String userImageUrl;
	@Field
	private List<String> userIds;
	@Field
	private Boolean isSaved;
	@Field
	private Long likes;
	@Field
	private Boolean discarded = false;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public UserPost getUser() {
		return user;
	}

	public void setUser(UserPost user) {
		this.user = user;
	}

	public List<ArticleDetails> getMultilingual() {
		return multilingual;
	}

	public void setMultilingual(List<ArticleDetails> multilingual) {
		this.multilingual = multilingual;
	}

	public CommunityType getType() {
		return type;
	}

	public void setType(CommunityType type) {
		this.type = type;
	}

	
	

	public ObjectId getUserId() {
		return userId;
	}

	public void setUserId(ObjectId userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public UserType getUserType() {
		return userType;
	}

	public void setUserType(UserType userType) {
		this.userType = userType;
	}

	public String getSpeciality() {
		return speciality;
	}

	public void setSpeciality(String speciality) {
		this.speciality = speciality;
	}

	public List<String> getUserIds() {
		return userIds;
	}

	public void setUserIds(List<String> userIds) {
		this.userIds = userIds;
	}

	public Boolean getIsSaved() {
		return isSaved;
	}

	public void setIsSaved(Boolean isSaved) {
		this.isSaved = isSaved;
	}

	public Long getLikes() {
		return likes;
	}

	public void setLikes(Long likes) {
		this.likes = likes;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	public String getUserImageUrl() {
		return userImageUrl;
	}

	public void setUserImageUrl(String userImageUrl) {
		this.userImageUrl = userImageUrl;
	}

}
