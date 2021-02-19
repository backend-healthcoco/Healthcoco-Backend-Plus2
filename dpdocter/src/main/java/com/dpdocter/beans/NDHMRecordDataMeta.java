package com.dpdocter.beans;

import java.util.List;

public class NDHMRecordDataMeta {

	
	private String versionId;
	private List<String> profile;
	private String lastUpdated;
	private List<BundleSecurity> security;

	public List<String> getProfile() {
		return profile;
	}

	public void setProfile(List<String> profile) {
		this.profile = profile;
	}
	
	

	public String getVersionId() {
		return versionId;
	}

	public void setVersionId(String versionId) {
		this.versionId = versionId;
	}

	public String getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(String lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	public List<BundleSecurity> getSecurity() {
		return security;
	}

	public void setSecurity(List<BundleSecurity> security) {
		this.security = security;
	}

	@Override
	public String toString() {
		return "NDHMRecordDataMeta [profile=" + profile + "]";
	}
}
