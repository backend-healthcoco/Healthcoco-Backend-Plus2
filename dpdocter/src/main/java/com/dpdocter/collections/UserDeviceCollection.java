package com.dpdocter.collections;

import java.util.List;

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
    private List<String> userIds;

    @Field
    private DeviceType deviceType;

    @Field
    private String deviceId;

    @Field
    private String pushToken;

    @Field
    private RoleEnum role;

    @Field
    private int batchCount = 0;
    
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<String> getUserIds() {
		return userIds;
	}

	public void setUserIds(List<String> userIds) {
		this.userIds = userIds;
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

	public String getPushToken() {
		return pushToken;
	}

	public void setPushToken(String pushToken) {
		this.pushToken = pushToken;
	}

	public RoleEnum getRole() {
		return role;
	}

	public void setRole(RoleEnum role) {
		this.role = role;
	}

	public int getBatchCount() {
		return batchCount;
	}

	public void setBatchCount(int batchCount) {
		this.batchCount = batchCount;
	}

	@Override
	public String toString() {
		return "UserDeviceCollection [id=" + id + ", userIds=" + userIds + ", deviceType=" + deviceType + ", deviceId="
				+ deviceId + ", pushToken=" + pushToken + ", role=" + role + ", batchCount=" + batchCount + "]";
	}
}
