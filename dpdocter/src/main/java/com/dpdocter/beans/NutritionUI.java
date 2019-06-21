package com.dpdocter.beans;

import java.util.List;

import com.dpdocter.collections.GenericCollection;

public class NutritionUI extends GenericCollection {

	private String id;

	private List<NutritionUIPermission> uiPermission;

	private String userId;

	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<NutritionUIPermission> getUiPermission() {
		return uiPermission;
	}

	public void setUiPermission(List<NutritionUIPermission> uiPermission) {
		this.uiPermission = uiPermission;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

}
