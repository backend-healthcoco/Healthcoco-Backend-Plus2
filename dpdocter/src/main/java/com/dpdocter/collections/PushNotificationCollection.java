package com.dpdocter.collections;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.enums.DeviceType;
import com.dpdocter.enums.PushNotificationType;

@Document(collection = "push_notification_cl")
public class PushNotificationCollection {

	@Id
	private ObjectId id;

	@Field
	private List<String> deviceIds;

	@Field
	private String message;

	@Field
	private DeviceType deviceType;

	@Field
	private String imageURL;

	@Field
	private PushNotificationType notificationType = PushNotificationType.INDIVIDUAL;

	public PushNotificationCollection() {
	}

	public PushNotificationCollection(ObjectId id, List<String> deviceIds, String message, DeviceType deviceType,
			String imageURL, PushNotificationType notificationType) {
		this.id = id;
		this.deviceIds = deviceIds;
		this.message = message;
		this.deviceType = deviceType;
		this.imageURL = imageURL;
		this.notificationType = notificationType;
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public List<String> getDeviceIds() {
		return deviceIds;
	}

	public void setDeviceIds(List<String> deviceIds) {
		this.deviceIds = deviceIds;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public DeviceType getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(DeviceType deviceType) {
		this.deviceType = deviceType;
	}

	public String getImageURL() {
		return imageURL;
	}

	public void setImageURL(String imageURL) {
		this.imageURL = imageURL;
	}

	public PushNotificationType getNotificationType() {
		return notificationType;
	}

	public void setNotificationType(PushNotificationType notificationType) {
		this.notificationType = notificationType;
	}

	@Override
	public String toString() {
		return "PushNotificationCollection [id=" + id + ", deviceIds=" + deviceIds + ", message=" + message
				+ ", deviceType=" + deviceType + ", imageURL=" + imageURL + ", notificationType=" + notificationType
				+ "]";
	}
}
