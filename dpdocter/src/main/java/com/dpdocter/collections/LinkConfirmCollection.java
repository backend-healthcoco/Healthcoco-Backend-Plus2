package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.ConfirmationObject;

@Document(collection = "link_confirm_cl")
public class LinkConfirmCollection extends GenericCollection{

	@Id
	private ObjectId id;
	
	@Field
	private String requestId;
	@Field
	private String timestamp;
	@Field
	private ConfirmationObject confirmation;
	public ObjectId getId() {
		return id;
	}
	public void setId(ObjectId id) {
		this.id = id;
	}
	public String getRequestId() {
		return requestId;
	}
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}
	public String getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	public ConfirmationObject getConfirmation() {
		return confirmation;
	}
	public void setConfirmation(ConfirmationObject confirmation) {
		this.confirmation = confirmation;
	}
	
	
	

	
}
