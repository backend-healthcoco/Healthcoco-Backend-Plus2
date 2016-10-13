package com.dpdocter.beans;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.dpdocter.collections.GenericCollection;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class DynamicUI extends GenericCollection {

	private UIPermissions uiPermissions;

	public UIPermissions getUiPermissions() {
		return uiPermissions;
	}

	public void setUiPermissions(UIPermissions uiPermissions) {
		this.uiPermissions = uiPermissions;
	}

	@Override
	public String toString() {
		return "DynamicUI [uiPermissions=" + uiPermissions + "]";
	}

}
