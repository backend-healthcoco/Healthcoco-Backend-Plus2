package com.dpdocter.request;

import java.util.List;

import com.dpdocter.beans.NutritionUIPermission;

public class NutrirtionUIRequest {

	private String id;

	private List<NutritionUIPermission> uiPermission;

	private String userId;

	private String adminId;

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

	public String getAdminId() {
		return adminId;
	}

	public void setAdminId(String adminId) {
		this.adminId = adminId;
	}

}
