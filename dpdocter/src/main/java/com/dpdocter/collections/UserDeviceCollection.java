package com.dpdocter.collections;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.enums.DeviceType;

@Document(collection = "user_device_cl")
public class UserDeviceCollection extends GenericCollection{
	
	@Id
    private String id;

    @Field
    private String userId;

    @Field
    private DeviceType deviceType;

    @Field
    private String deviceId;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public DeviceType getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(DeviceType deviceType) {
		this.deviceType = deviceType;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	@Override
	public String toString() {
		return "UserDeviceCollection [id=" + id + ", userId=" + userId + ", deviceType=" + deviceType + ", deviceId="
				+ deviceId + "]";
	}	
}
