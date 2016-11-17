package com.dpdocter.beans;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.dpdocter.collections.GenericCollection;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class DynamicUI extends GenericCollection {

	private String id;
	private UIPermissions uiPermissions;
	private String doctorId;

	public UIPermissions getUiPermissions() {
		return uiPermissions;
	}

	public void setUiPermissions(UIPermissions uiPermissions) {
		this.uiPermissions = uiPermissions;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	@Override
	public String toString() {
		return "DynamicUI [id=" + id + ", uiPermissions=" + uiPermissions + ", doctorId=" + doctorId + "]";
	}

	

}
