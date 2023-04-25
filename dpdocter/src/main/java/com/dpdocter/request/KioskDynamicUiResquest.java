package com.dpdocter.request;

import java.util.List;

public class KioskDynamicUiResquest {
	public List<String> getKioskPermission() {
		return kioskPermission;
	}

	public void setKioskPermission(List<String> kioskPermission) {
		this.kioskPermission = kioskPermission;
	}

	private String doctorId;
	private List<String> kioskPermission;

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

}
