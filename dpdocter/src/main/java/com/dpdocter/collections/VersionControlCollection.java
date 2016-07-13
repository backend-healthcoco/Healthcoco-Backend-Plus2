package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.enums.AppType;
import com.dpdocter.enums.ApplicationType;
import com.dpdocter.enums.DeviceType;

@Document(collection = "version_control_cl")
public class VersionControlCollection {
	@Id
	private ObjectId id;
	@Field
	private AppType appType;
	@Field
	private DeviceType deviceType;
	@Field
	private Integer majorVersion;
	@Field
	private Integer minorVersion;

	@Field
	private Integer patchVersion;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public Integer getMajorVersion() {
		return majorVersion;
	}

	public void setMajorVersion(Integer majorVersion) {
		this.majorVersion = majorVersion;
	}

	public Integer getMinorVersion() {
		return minorVersion;
	}

	public void setMinorVersion(Integer minorVersion) {
		this.minorVersion = minorVersion;
	}

	public Integer getPatchVersion() {
		return patchVersion;
	}

	public void setPatchVersion(Integer patchVersion) {
		this.patchVersion = patchVersion;
	}

	public AppType getAppType() {
		return appType;
	}

	public void setAppType(AppType appType) {
		this.appType = appType;
	}

	public DeviceType getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(DeviceType deviceType) {
		this.deviceType = deviceType;
	}

	@Override
	public String toString() {
		return "VersionControlCollection [id=" + id + ", appType=" + appType + ", deviceType=" + deviceType
				+ ", majorVersion=" + majorVersion + ", minorVersion=" + minorVersion + ", patchVersion=" + patchVersion
				+ "]";
	}
	
	

}
