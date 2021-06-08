package com.dpdocter.beans;

import java.util.List;

import com.dpdocter.enums.CommunityType;

public class CommentRequest {
	
	private String id;
	
	private String postId;

	private String userName;

	private String userId;

	private String comment;
	
	private CommunityType type;
	
	private Boolean discarded=false;

	public String getPostId() {
		return postId;
	}

	public void setPostId(String postId) {
		this.postId = postId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	

	public CommunityType getType() {
		return type;
	}

	public void setType(CommunityType type) {
		this.type = type;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	
}
