package com.dpdocter.collections;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.enums.DeviceType;
import com.dpdocter.enums.RoleEnum;

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

    @Field
    private String pushToken;

    @Field
    private RoleEnum role;
    
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

	public RoleEnum getRole() {
		return role;
	}

	public void setRole(RoleEnum role) {
		this.role = role;
	}

	public String getPushToken() {
		return pushToken;
	}

	public void setPushToken(String pushToken) {
		this.pushToken = pushToken;
	}

	@Override
	public String toString() {
		return "UserDeviceCollection [id=" + id + ", userId=" + userId + ", deviceType=" + deviceType + ", deviceId="
				+ deviceId + ", pushToken=" + pushToken + ", role=" + role + "]";
	}

}
