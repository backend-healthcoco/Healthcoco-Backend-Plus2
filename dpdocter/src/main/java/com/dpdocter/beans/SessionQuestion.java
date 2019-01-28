package com.dpdocter.beans;

import com.dpdocter.collections.GenericCollection;

public class SessionQuestion extends GenericCollection {
	private String id;
	private String type = "CONFERENCE_SESSION";
	private String sessionId;
	private String questionerId;
	private String userId;
	private Integer noOfLikes;
	private Boolean discarded = false;
	private Boolean isLiked = false;

	public Boolean getIsLiked() {
		return isLiked;
	}

	public void setIsLiked(Boolean isLiked) {
		this.isLiked = isLiked;
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

	public Integer getNoOfLikes() {
		return noOfLikes;
	}

	public void setNoOfLikes(Integer noOfLikes) {
		this.noOfLikes = noOfLikes;
	}

	public String getQuestionerId() {
		return questionerId;
	}

	public void setQuestionerId(String questionerId) {
		this.questionerId = questionerId;
	}

}
