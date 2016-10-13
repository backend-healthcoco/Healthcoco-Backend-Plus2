package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import com.dpdocter.beans.UIPermissions;

@Document(collection = "dynamic_ui_cl")
public class DynamicUICollection extends GenericCollection {

	private ObjectId id;
	private UIPermissions uiPermissions;
	private ObjectId doctorId;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public UIPermissions getUiPermissions() {
		return uiPermissions;
	}

	public void setUiPermissions(UIPermissions uiPermissions) {
		this.uiPermissions = uiPermissions;
	}

	public ObjectId getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(ObjectId doctorId) {
		this.doctorId = doctorId;
	}

	@Override
	public String toString() {
		return "DynamicUICollection [id=" + id + ", uiPermissions=" + uiPermissions + ", doctorId=" + doctorId + "]";
	}

}
