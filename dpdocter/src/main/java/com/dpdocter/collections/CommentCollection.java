package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "comment_cl")
public class CommentCollection extends GenericCollection {
	@Id
	private ObjectId id;
	@Field
	private String userName;
	@Field
	private ObjectId userId;
	@Field
	private ObjectId postId;
	
	@Field
	private String comment;
	@Field
	private Boolean discarded=false;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	

	public ObjectId getUserId() {
		return userId;
	}

	public void setUserId(ObjectId userId) {
		this.userId = userId;
	}

	public ObjectId getPostId() {
		return postId;
	}

	public void setPostId(ObjectId postId) {
		this.postId = postId;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}
	
	
	
}
