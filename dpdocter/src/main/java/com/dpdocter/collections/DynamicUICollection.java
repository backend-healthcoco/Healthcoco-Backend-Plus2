package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.UIPermissions;

@Document(collection = "dynamic_ui_cl")
public class DynamicUICollection extends GenericCollection {

	@Id
	private ObjectId id;
	@Field
	private UIPermissions uiPermissions;
	@Field
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
		if (uiPermissions != null) {
			this.uiPermissions.setClinicalNotesPermissions(uiPermissions.getClinicalNotesPermissions());
			this.uiPermissions.setPatientVisitPermissions(uiPermissions.getPatientVisitPermissions());
			this.uiPermissions.setPrescriptionPermissions(uiPermissions.getPrescriptionPermissions());
			this.uiPermissions.setProfilePermissions(uiPermissions.getProfilePermissions());
			this.uiPermissions.setTabPermissions(uiPermissions.getTabPermissions());
		}
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
