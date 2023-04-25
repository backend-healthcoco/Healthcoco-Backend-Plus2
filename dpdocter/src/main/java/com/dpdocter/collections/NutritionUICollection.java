package com.dpdocter.collections;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.NutritionUIPermission;

@Document(collection = "nutrition_ui_cl")

public class NutritionUICollection extends GenericCollection {
	@Id
	private ObjectId id;
	@Field
	private List<NutritionUIPermission> uiPermission;
	@Field
	private ObjectId userId;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public List<NutritionUIPermission> getUiPermission() {
		return uiPermission;
	}

	public void setUiPermission(List<NutritionUIPermission> uiPermission) {
		this.uiPermission = uiPermission;
	}

	public ObjectId getUserId() {
		return userId;
	}

	public void setUserId(ObjectId userId) {
		this.userId = userId;
	}

}
