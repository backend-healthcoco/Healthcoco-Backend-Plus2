package com.dpdocter.beans;

import java.util.List;

public class NDHMRecordDataMeta {

	List<String> profile;

	public List<String> getProfile() {
		return profile;
	}

	public void setProfile(List<String> profile) {
		this.profile = profile;
	}

	@Override
	public String toString() {
		return "NDHMRecordDataMeta [profile=" + profile + "]";
	}
}
