package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "question_cl")
public class QuestionCollection extends GenericCollection {

	@Id
	private ObjectId id;
	@Field
	private String type = "CONFERENCE_SESSION";	
	@Field
	private String question;
	@Field
	private ObjectId sessionId;
	@Field
	private ObjectId userId;
	@Field
	private Integer noOfLikes=0;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public ObjectId getSessionId() {
		return sessionId;
	}

	public void setSessionId(ObjectId sessionId) {
		this.sessionId = sessionId;
	}

	public ObjectId getUserId() {
		return userId;
	}

	public void setUserId(ObjectId userId) {
		this.userId = userId;
	}

	public Integer getNoOfLikes() {
		return noOfLikes;
	}

	public void setNoOfLikes(Integer noOfLikes) {
		this.noOfLikes = noOfLikes;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}
	

}
package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "question_cl")
public class QuestionCollection extends GenericCollection {

	@Id
	private ObjectId id;
	@Field
	private String type = "CONFERENCE_SESSION";
	@Field
	private ObjectId sessionId;
	@Field
	private ObjectId userId;
	@Field
	private Integer noOfLikes;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public ObjectId getSessionId() {
		return sessionId;
	}

	public void setSessionId(ObjectId sessionId) {
		this.sessionId = sessionId;
	}

	public ObjectId getUserId() {
		return userId;
	}

	public void setUserId(ObjectId userId) {
		this.userId = userId;
	}

	public Integer getNoOfLikes() {
		return noOfLikes;
	}

	public void setNoOfLikes(Integer noOfLikes) {
		this.noOfLikes = noOfLikes;
	}
	

}
