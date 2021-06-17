package com.dpdocter.beans;

import java.util.List;

import com.dpdocter.collections.GenericCollection;
import com.dpdocter.enums.CommunityType;

public class FeedsResponse extends GenericCollection{
	
	private String id;
	
	private UserPost user;                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          
	
	private ArticleDetails multilingual;

	private CommunityType type;
	
	private String postByAdminId;
	
	private String postByAdminName;
	
	private String postByDoctorId;
	
	private String postByDoctorName;
	
	private String postByUserId;

	private String postByUserName;
	
	private String postByExpertName;
	
	private String postByExpertId;
	
	private List<Comment>comments;
	
	private List<String>userIds;
	
	private Boolean isSaved;
	
	private Long likes;
	
	private Boolean discarded=false;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public UserPost getUser() {
		return user;
	}

	public void setUser(UserPost user) {
		this.user = user;
	}

	

	

	public ArticleDetails getMultilingual() {
		return multilingual;
	}

	public void setMultilingual(ArticleDetails multilingual) {
		this.multilingual = multilingual;
	}

	public CommunityType getType() {
		return type;
	}

	public void setType(CommunityType type) {
		this.type = type;
	}

	public String getPostByAdminId() {
		return postByAdminId;
	}

	public void setPostByAdminId(String postByAdminId) {
		this.postByAdminId = postByAdminId;
	}

	public String getPostByAdminName() {
		return postByAdminName;
	}

	public void setPostByAdminName(String postByAdminName) {
		this.postByAdminName = postByAdminName;
	}

	public String getPostByDoctorId() {
		return postByDoctorId;
	}

	public void setPostByDoctorId(String postByDoctorId) {
		this.postByDoctorId = postByDoctorId;
	}

	

	public String getPostByExpertName() {
		return postByExpertName;
	}

	public void setPostByExpertName(String postByExpertName) {
		this.postByExpertName = postByExpertName;
	}

	public String getPostByExpertId() {
		return postByExpertId;
	}

	public void setPostByExpertId(String postByExpertId) {
		this.postByExpertId = postByExpertId;
	}

	public List<Comment> getComments() {
		return comments;
	}

	public void setComments(List<Comment> comments) {
		this.comments = comments;
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

	public String getPostByDoctorName() {
		return postByDoctorName;
	}

	public void setPostByDoctorName(String postByDoctorName) {
		this.postByDoctorName = postByDoctorName;
	}

	public String getPostByUserId() {
		return postByUserId;
	}

	public void setPostByUserId(String postByUserId) {
		this.postByUserId = postByUserId;
	}

	public String getPostByUserName() {
		return postByUserName;
	}

	public void setPostByUserName(String postByUserName) {
		this.postByUserName = postByUserName;
	}
	
	
	


}
