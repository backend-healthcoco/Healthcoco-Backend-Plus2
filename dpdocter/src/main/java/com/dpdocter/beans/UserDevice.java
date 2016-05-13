package com.dpdocter.beans;

import com.dpdocter.collections.GenericCollection;
import com.dpdocter.enums.DeviceType;
import com.dpdocter.enums.RoleEnum;

public class UserDevice extends GenericCollection{

    private String id;

    private String userId;

    private DeviceType deviceType;

    private String deviceId;

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

	@Override
	public String toString() {
		return "UserDevice [id=" + id + ", userId=" + userId + ", deviceType=" + deviceType + ", deviceId=" + deviceId
				+ ", role=" + role + "]";
	}
}
