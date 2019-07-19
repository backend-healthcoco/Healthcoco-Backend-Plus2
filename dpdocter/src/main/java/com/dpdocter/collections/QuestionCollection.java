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
	private ObjectId questionerId;
	@Field
	private Integer noOfLikes = 0;
	@Field
	private Boolean discarded=false;
	


	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

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

	public ObjectId getQuestionerId() {
		return questionerId;
	}

	public void setQuestionerId(ObjectId questionerId) {
		this.questionerId = questionerId;
	}

	public Integer getNoOfLikes() {
		return noOfLikes;
	}

	public void setNoOfLikes(Integer noOfLikes) {
		this.noOfLikes = noOfLikes;
	}

	@Override
	public String toString() {
		return "QuestionCollection [id=" + id + ", type=" + type + ", sessionId=" + sessionId + ", questionerId="
				+ questionerId + ", noOfLikes=" + noOfLikes + ", discarded=" + discarded + "]";
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}
}
