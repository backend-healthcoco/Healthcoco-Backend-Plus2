package com.dpdocter.collections;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "kiosk_dynamic_ui_cl")
public class KioskDynamicUiCollection extends GenericCollection {
	@Field
	private ObjectId id;
	@Field
	private ObjectId doctorId;
	@Field
	private List<String> kioskPermission;

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

	public List<String> getKioskPermission() {
		return kioskPermission;
	}

	public void setKioskPermission(List<String> kioskPermission) {
		this.kioskPermission = kioskPermission;
	}

}
