package com.dpdocter.response;

import com.dpdocter.beans.UIPermissions;

public class DynamicUIResponse {
	private UIPermissions doctorPermissions;

	private UIPermissions allPermissions;

	private String doctorId;

	public UIPermissions getDoctorPermissions() {
		return doctorPermissions;
	}

	public void setDoctorPermissions(UIPermissions doctorPermissions) {
		this.doctorPermissions = doctorPermissions;
	}

	public UIPermissions getAllPermissions() {
		return allPermissions;
	}

	public void setAllPermissions(UIPermissions allPermissions) {
		this.allPermissions = allPermissions;
	}

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	@Override
	public String toString() {
		return "DynamicUIResponse [doctorPermissions=" + doctorPermissions + ", allPermissions=" + allPermissions
				+ ", doctorId=" + doctorId + "]";
	}

}
