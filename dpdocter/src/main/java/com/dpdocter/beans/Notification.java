package com.dpdocter.beans;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.dpdocter.collections.GenericCollection;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Notification extends GenericCollection{
	
    private String id;
	
    private String senderId;
	
    private String senderLocationId;
	
    private String senderHospitalId;
	
    private String receiverId;
	
    private String receiverLocationId;
	
    private String receiverHospitalId;
	
    private String title;
	
    private String imageURL;

    private String text;
	
    private String type;
	
    private String typeId;

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

	public String getSenderLocationId() {
		return senderLocationId;
	}

	public void setSenderLocationId(String senderLocationId) {
		this.senderLocationId = senderLocationId;
	}

	public String getSenderHospitalId() {
		return senderHospitalId;
	}

	public void setSenderHospitalId(String senderHospitalId) {
		this.senderHospitalId = senderHospitalId;
	}

	public String getReceiverId() {
		return receiverId;
	}

	public void setReceiverId(String receiverId) {
		this.receiverId = receiverId;
	}

	public String getReceiverLocationId() {
		return receiverLocationId;
	}

	public void setReceiverLocationId(String receiverLocationId) {
		this.receiverLocationId = receiverLocationId;
	}

	public String getReceiverHospitalId() {
		return receiverHospitalId;
	}

	public void setReceiverHospitalId(String receiverHospitalId) {
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
		return "Notification [id=" + id + ", senderId=" + senderId + ", senderLocationId=" + senderLocationId
				+ ", senderHospitalId=" + senderHospitalId + ", receiverId=" + receiverId + ", receiverLocationId="
				+ receiverLocationId + ", receiverHospitalId=" + receiverHospitalId + ", title=" + title + ", imageURL="
				+ imageURL + ", text=" + text + ", type=" + type + ", typeId=" + typeId + "]";
	}	
}
