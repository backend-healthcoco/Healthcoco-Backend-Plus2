package com.dpdocter.collections;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "email_user_cl")
public class EmailUserCollection {

	@Id
	private String id;
	@Field
	private String subject;
	@Field
	private String receiverId;
	@Field
	private String emailId;
	@Field
	private Boolean isTrashed;
	@Field
	private Boolean isRead;
	@Field
	private Boolean isStarted;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getReceiverId() {
		return receiverId;
	}

	public void setReceiverId(String receiverId) {
		this.receiverId = receiverId;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public Boolean getIsTrashed() {
		return isTrashed;
	}

	public void setIsTrashed(Boolean isTrashed) {
		this.isTrashed = isTrashed;
	}

	public Boolean getIsRead() {
		return isRead;
	}

	public void setIsRead(Boolean isRead) {
		this.isRead = isRead;
	}

	public Boolean getIsStarted() {
		return isStarted;
	}

	public void setIsStarted(Boolean isStarted) {
		this.isStarted = isStarted;
	}

	@Override
	public String toString() {
		return "EmailUserCollection [id=" + id + ", subject=" + subject + ", receiverId=" + receiverId + ", emailId=" + emailId + ", isTrashed=" + isTrashed
				+ ", isRead=" + isRead + ", isStarted=" + isStarted + "]";
	}

}
