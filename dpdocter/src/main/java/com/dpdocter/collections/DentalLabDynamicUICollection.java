package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.DentalLabDynamicField;

@Document(collection = "dental_lab_dynamic_ui_cl")
public class DentalLabDynamicUICollection extends GenericCollection {

	@Id
	private ObjectId id;
	@Field
	private DentalLabDynamicField dentalLabDynamicField;
	@Field
	private ObjectId dentalLabId;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public DentalLabDynamicField getDentalLabDynamicField() {
		return dentalLabDynamicField;
	}

	public void setDentalLabDynamicField(DentalLabDynamicField dentalLabDynamicField) {
		this.dentalLabDynamicField = dentalLabDynamicField;
	}

	public ObjectId getDentalLabId() {
		return dentalLabId;
	}

	public void setDentalLabId(ObjectId dentalLabId) {
		this.dentalLabId = dentalLabId;
	}

	@Override
	public String toString() {
		return "DentalLabDynamicUICollection [id=" + id + ", dentalLabDynamicField=" + dentalLabDynamicField
				+ ", dentalLabId=" + dentalLabId + "]";
	}

}
