package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "push_notification_cl")
public class PushNotificationCollection extends GenericCollection{

	@Id
    private ObjectId id;
	
	@Field
    private ObjectId senderId;
	
	@Field
    private ObjectId senderLocationId;
	
	@Field
    private ObjectId senderHospitalId;
	
	@Field
    private ObjectId receiverId;
	
	@Field
    private ObjectId receiverLocationId;
	
	@Field
    private ObjectId receiverHospitalId;
	
	@Field
    private String title;
	
	@Field
    private String imageURL;

	@Field
    private String text;
	
	@Field
    private String type;
	
	@Field
    private String typeId;

	public PushNotificationCollection() {
		super();
	}

	public PushNotificationCollection(ObjectId id, ObjectId senderId, ObjectId senderLocationId, ObjectId senderHospitalId,
			ObjectId receiverId, ObjectId receiverLocationId, ObjectId receiverHospitalId, String title,
			String imageURL, String text, String type, String typeId) {
		super();
		this.id = id;
		this.senderId = senderId;
		this.senderLocationId = senderLocationId;
		this.senderHospitalId = senderHospitalId;
		this.receiverId = receiverId;
		this.receiverLocationId = receiverLocationId;
		this.receiverHospitalId = receiverHospitalId;
		this.title = title;
		this.imageURL = imageURL;
		this.text = text;
		this.type = type;
		this.typeId = typeId;
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public ObjectId getSenderId() {
		return senderId;
	}

	public void setSenderId(ObjectId senderId) {
		this.senderId = senderId;
	}

	public ObjectId getSenderLocationId() {
		return senderLocationId;
	}

	public void setSenderLocationId(ObjectId senderLocationId) {
		this.senderLocationId = senderLocationId;
	}

	public ObjectId getSenderHospitalId() {
		return senderHospitalId;
	}

	public void setSenderHospitalId(ObjectId senderHospitalId) {
		this.senderHospitalId = senderHospitalId;
	}

	public ObjectId getReceiverId() {
		return receiverId;
	}

	public void setReceiverId(ObjectId receiverId) {
		this.receiverId = receiverId;
	}

	public ObjectId getReceiverLocationId() {
		return receiverLocationId;
	}

	public void setReceiverLocationId(ObjectId receiverLocationId) {
		this.receiverLocationId = receiverLocationId;
	}

	public ObjectId getReceiverHospitalId() {
		return receiverHospitalId;
	}

	public void setReceiverHospitalId(ObjectId receiverHospitalId) {
		this.receiverHospitalId = receiverHospitalId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getImageURL() {
		return imageURL;
	}

	public void setImageURL(String imageURL) {
		this.imageURL = imageURL;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTypeId() {
		return typeId;
	}

	public void setTypeId(String typeId) {
		this.typeId = typeId;
	}

	@Override
	public String toString() {
		return "PushNotificationCollection [id=" + id + ", senderId=" + senderId + ", senderLocationId="
				+ senderLocationId + ", senderHospitalId=" + senderHospitalId + ", receiverId=" + receiverId
				+ ", receiverLocationId=" + receiverLocationId + ", receiverHospitalId=" + receiverHospitalId
				+ ", title=" + title + ", imageURL=" + imageURL + ", text=" + text + ", type=" + type + ", typeId="
				+ typeId + "]";
	}	
}
