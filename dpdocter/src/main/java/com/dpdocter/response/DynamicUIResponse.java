package com.dpdocter.response;

import com.dpdocter.beans.DynamicUI;
import com.dpdocter.beans.UIPermissions;

public class DynamicUIResponse {
	private DynamicUI dynamicUI ;
	
	private UIPermissions uiPermissions;

	public DynamicUI getDynamicUI() {
		return dynamicUI;
	}

	public void setDynamicUI(DynamicUI dynamicUI) {
		this.dynamicUI = dynamicUI;
	}

	public UIPermissions getUiPermissions() {
		return uiPermissions;
	}

	public void setUiPermissions(UIPermissions uiPermissions) {
		this.uiPermissions = uiPermissions;
	}
	
	
	
}
