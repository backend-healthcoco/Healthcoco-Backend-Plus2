package com.dpdocter.beans;

import java.util.List;

import com.dpdocter.collections.GenericCollection;

public class KioskDynamicUi extends GenericCollection {
	private String id;
	private String doctorId;
	private List<String> kioskPermission;
	private List<String> allkioskPermission;

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

	public List<String> getKioskPermission() {
		return kioskPermission;
	}

	public void setKioskPermission(List<String> kioskPermission) {
		this.kioskPermission = kioskPermission;
	}

	public List<String> getAllkioskPermission() {
		return allkioskPermission;
	}

	public void setAllkioskPermission(List<String> allkioskPermission) {
		this.allkioskPermission = allkioskPermission;
	}

}
