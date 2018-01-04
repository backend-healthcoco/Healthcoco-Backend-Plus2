package com.dpdocter.beans;

import com.dpdocter.collections.GenericCollection;

public class InventorySettings extends GenericCollection{

	private String id;
	private String doctorId;
	private String locationId;
	private String hospitalId;
	private Boolean saveToInventory;
	private Boolean showInventoryCount;

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

	public String getLocationId() {
		return locationId;
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}

	public String getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(String hospitalId) {
		this.hospitalId = hospitalId;
	}

	public Boolean getSaveToInventory() {
		return saveToInventory;
	}

	public void setSaveToInventory(Boolean saveToInventory) {
		this.saveToInventory = saveToInventory;
	}

	public Boolean getShowInventoryCount() {
		return showInventoryCount;
	}

	public void setShowInventoryCount(Boolean showInventoryCount) {
		this.showInventoryCount = showInventoryCount;
	}

	@Override
	public String toString() {
		return "InventorySettings [id=" + id + ", doctorId=" + doctorId + ", locationId=" + locationId + ", hospitalId="
				+ hospitalId + ", saveToInventory=" + saveToInventory + ", showInventoryCount=" + showInventoryCount
				+ "]";
	}

}
