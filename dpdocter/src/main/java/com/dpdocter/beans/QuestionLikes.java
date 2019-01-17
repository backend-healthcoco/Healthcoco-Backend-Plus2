package com.dpdocter.beans;

import com.dpdocter.collections.GenericCollection;

public class QuestionLikes extends GenericCollection {
	
	private String id;

	
	private String questionId;

	
	private String userId;

	
	private Boolean discarded=false;


	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public String getQuestionId() {
		return questionId;
	}


	public void setQuestionId(String questionId) {
		this.questionId = questionId;
	}


	public String getUserId() {
		return userId;
	}


	public void setUserId(String userId) {
		this.userId = userId;
	}


	public Boolean getDiscarded() {
		return discarded;
	}


	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	
}
