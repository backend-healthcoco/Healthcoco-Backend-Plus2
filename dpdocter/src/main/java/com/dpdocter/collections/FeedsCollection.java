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

@Document(collection = "feeds_cl")
public class FeedsCollection extends GenericCollection{

	@Id
	private ObjectId id;
	@Field
	private UserPost user;
	@Field
	private List<ArticleDetails> multilingual;
	@Field
	private CommunityType type;
	@Field
	private ObjectId postByAdminId;
	@Field
	private String postByAdminName;
	@Field
	private ObjectId postByDoctorId;
	@Field
	private String postByDoctorName;
	@Field
	private ObjectId postByUserId;
	@Field
	private String postByUserName;
	@Field
	private String postByExpertName;
	@Field
	private ObjectId postByExpertId;
	@Field
	private List<ObjectId>commentIds;
	@Field
	private List<ObjectId>userIds;
	@Field
	private Boolean isSaved;
	@Field
	private Long likes;
	@Field
	private Boolean discarded=false;
	
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
	public ObjectId getPostByAdminId() {
		return postByAdminId;
	}
	public void setPostByAdminId(ObjectId postByAdminId) {
		this.postByAdminId = postByAdminId;
	}
	public String getPostByAdminName() {
		return postByAdminName;
	}
	public void setPostByAdminName(String postByAdminName) {
		this.postByAdminName = postByAdminName;
	}
	public ObjectId getPostByDoctorId() {
		return postByDoctorId;
	}
	public void setPostByDoctorId(ObjectId postByDoctorId) {
		this.postByDoctorId = postByDoctorId;
	}
	
	public String getPostByDoctorName() {
		return postByDoctorName;
	}
	public void setPostByDoctorName(String postByDoctorName) {
		this.postByDoctorName = postByDoctorName;
	}
	public ObjectId getPostByUserId() {
		return postByUserId;
	}
	public void setPostByUserId(ObjectId postByUserId) {
		this.postByUserId = postByUserId;
	}
	public String getPostByUserName() {
		return postByUserName;
	}
	public void setPostByUserName(String postByUserName) {
		this.postByUserName = postByUserName;
	}
	public String getPostByExpertName() {
		return postByExpertName;
	}
	public void setPostByExpertName(String postByExpertName) {
		this.postByExpertName = postByExpertName;
	}
	public ObjectId getPostByExpertId() {
		return postByExpertId;
	}
	public void setPostByExpertId(ObjectId postByExpertId) {
		this.postByExpertId = postByExpertId;
	}
	
	public List<ObjectId> getCommentIds() {
		return commentIds;
	}
	public void setCommentIds(List<ObjectId> commentIds) {
		this.commentIds = commentIds;
	}
	public List<ObjectId> getUserIds() {
		return userIds;
	}
	public void setUserIds(List<ObjectId> userIds) {
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
	
	
	
}
