package com.dpdocter.beans;

import com.dpdocter.collections.GenericCollection;

public class SessionQuestion extends GenericCollection {
	private String id;
	private String type = "CONFERENCE_SESSION";
	private String sessionId;
	private String questionerId;
	private Integer noOfLikes = 0;
	private String question;
<<<<<<< HEAD
<<<<<<< HEAD

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
package com.dpdocter.beans;

import com.dpdocter.collections.GenericCollection;

public class SessionQuestion extends GenericCollection {
	private String id;
	private String type = "CONFERENCE_SESSION";
	private String sessionId;
	private String userId;
	private Integer noOfLikes;

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

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Integer getNoOfLikes() {
		return noOfLikes;
	}

	public void setNoOfLikes(Integer noOfLikes) {
		this.noOfLikes = noOfLikes;
	}

}
package com.dpdocter.beans;

import com.dpdocter.collections.GenericCollection;

public class SessionQuestion extends GenericCollection {
	private String id;
	private String type = "CONFERENCE_SESSION";
	private String sessionId;
	private String userId;
	private Integer noOfLikes;
=======
>>>>>>> 813314eae... HAPPY-4143
=======
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
>>>>>>> ef5ce8210... HAPPY-4143

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
