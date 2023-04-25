package com.dpdocter.beans;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.dpdocter.enums.AccessPermissionType;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class AccessPermission {
	private AccessPermissionType accessPermissionType;

	private boolean accessPermissionValue = false;

	public AccessPermission() {
	}

	public AccessPermission(AccessPermissionType accessPermissionType, boolean accessPermissionValue) {
		this.accessPermissionType = accessPermissionType;
		this.accessPermissionValue = accessPermissionValue;
	}

	public AccessPermissionType getAccessPermissionType() {
		return accessPermissionType;
	}

	public void setAccessPermissionType(AccessPermissionType accessPermissionType) {
		this.accessPermissionType = accessPermissionType;
	}

	public boolean isAccessPermissionValue() {
		return accessPermissionValue;
	}

	public void setAccessPermissionValue(boolean accessPermissionValue) {
		this.accessPermissionValue = accessPermissionValue;
	}

	@Override
	public String toString() {
		return "AccessPermission [accessPermissionType=" + accessPermissionType + ", accessPermissionValue="
				+ accessPermissionValue + "]";
	}

}
