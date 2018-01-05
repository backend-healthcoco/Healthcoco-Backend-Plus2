package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "inventory_setting_cl")
public class InventorySettingsCollection extends GenericCollection{

	@Id
	private ObjectId id;
	@Field
	private ObjectId doctorId;
	@Field
	private ObjectId locationId;
	@Field
	private ObjectId hospitalId;
	@Field
	private Boolean saveToInventory;
	@Field
	private Boolean showInventoryCount;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public ObjectId getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(ObjectId doctorId) {
		this.doctorId = doctorId;
	}

	public ObjectId getLocationId() {
		return locationId;
	}

	public void setLocationId(ObjectId locationId) {
		this.locationId = locationId;
	}

	public ObjectId getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(ObjectId hospitalId) {
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
		return "InventorySettingsCollection [id=" + id + ", doctorId=" + doctorId + ", locationId=" + locationId
				+ ", hospitalId=" + hospitalId + ", saveToInventory=" + saveToInventory + ", showInventoryCount="
				+ showInventoryCount + "]";
	}

}
