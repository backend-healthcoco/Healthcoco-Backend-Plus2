package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.NdhmNotification;

@Document(collection = "ndhm_notify_cl")
public class NdhmNotifyCollection extends GenericCollection{

	@Id
	private ObjectId id;
	@Field
	private String requestId;
	@Field
	private String timestamp;
	@Field
	private NdhmNotification notification;
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
	public NdhmNotification getNotification() {
		return notification;
	}
	public void setNotification(NdhmNotification notification) {
		this.notification = notification;
	}
	
	
}
