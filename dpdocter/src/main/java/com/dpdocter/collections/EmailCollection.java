package com.dpdocter.collections;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "email_cl")
public class EmailCollection {

    @Id
    private String id;

    @Field
    private String senderId;

    @Field
    private String parentEmailId;

    @Field
    private Boolean isDraft;

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public String getSenderId() {
	return senderId;
    }

    public void setSenderId(String senderId) {
	this.senderId = senderId;
    }

    public String getParentEmailId() {
	return parentEmailId;
    }

    public void setParentEmailId(String parentEmailId) {
	this.parentEmailId = parentEmailId;
    }

    public Boolean getIsDraft() {
	return isDraft;
    }

    public void setIsDraft(Boolean isDraft) {
	this.isDraft = isDraft;
    }

    @Override
    public String toString() {
	return "EmailCollection [id=" + id + ", senderId=" + senderId + ", parentEmailId=" + parentEmailId + ", isDraft=" + isDraft + "]";
    }

}
