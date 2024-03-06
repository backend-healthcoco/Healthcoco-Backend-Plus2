package com.dpdocter.beans;

import java.util.List;

import com.dpdocter.enums.NdhmAuthMethods;

public class HealthIdSearch {
	
	private List<NdhmAuthMethods> authMethods;

	private String healthId;
	
	private String healthIdNumber;
	
	private String name;
	
	private NdhmTags tags;

	public String getHealthId() {
		return healthId;
	}

	public void setHealthId(String healthId) {
		this.healthId = healthId;
	}

	public String getHealthIdNumber() {
		return healthIdNumber;
	}

	public void setHealthIdNumber(String healthIdNumber) {
		this.healthIdNumber = healthIdNumber;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public NdhmTags getTags() {
		return tags;
	}

	public void setTags(NdhmTags tags) {
		this.tags = tags;
	}

	public List<NdhmAuthMethods> getAuthMethods() {
		return authMethods;
	}

	public void setAuthMethods(List<NdhmAuthMethods> authMethods) {
		this.authMethods = authMethods;
	}

	
	
	
	
}
