package com.dpdocter.beans;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

@JsonSerialize(include = Inclusion.NON_NULL)
public class DynamicUIRequest {

	private String DoctorId;
	private UIPermissions uiPermissions;

	public String getDoctorId() {
		return DoctorId;
	}

	public void setDoctorId(String doctorId) {
		DoctorId = doctorId;
	}

	public UIPermissions getUiPermissions() {
		return uiPermissions;
	}

	public void setUiPermissions(UIPermissions uiPermissions) {
		this.uiPermissions = uiPermissions;
	}

	@Override
	public String toString() {
		return "DynamicUIRequest [DoctorId=" + DoctorId + ", uiPermissions=" + uiPermissions + "]";
	}

}
