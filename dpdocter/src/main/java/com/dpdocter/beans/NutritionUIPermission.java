package com.dpdocter.beans;

import java.util.List;

public class NutritionUIPermission {
	private String ui;
	private List<String> accessTypes;

	public String getUi() {
		return ui;
	}

	public void setUi(String ui) {
		this.ui = ui;
	}

	public List<String> getAccessTypes() {
		return accessTypes;
	}

	public void setAccessTypes(List<String> accessTypes) {
		this.accessTypes = accessTypes;
	}

}
