package com.dpdocter.beans;

import com.dpdocter.collections.GenericCollection;

public class SessionQuestion extends GenericCollection {
	private String id;
	private String type = "CONFERENCE_SESSION";
	private String sessionId;
	private String questionerId;
	private Integer noOfLikes = 0;
	private String question;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getQuestionerId() {
		return questionerId;
	}

	public void setQuestionerId(String questionerId) {
		this.questionerId = questionerId;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public Integer getNoOfLikes() {
		return noOfLikes;
	}

	public void setNoOfLikes(Integer noOfLikes) {
		this.noOfLikes = noOfLikes;
	}

}
