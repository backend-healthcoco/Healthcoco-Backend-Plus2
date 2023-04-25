package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "form_content_cl")
public class FormContentCollection extends GenericCollection {
	@Id
	private ObjectId id;
	@Field
	private ObjectId locationId;
	@Field
	private ObjectId doctorId;
	@Field
	private ObjectId hospitalId;
	@Field
	private String declaration;
	@Field
	private String title;
	@Field
	private String type;
	@Field
	private boolean discarded = false;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDeclaration() {
		return declaration;
	}

	public String getTitle() {
		return title;
	}

	public boolean isDiscarded() {
		return discarded;
	}

	public void setDeclaration(String declaration) {
		this.declaration = declaration;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setDiscarded(boolean discarded) {
		this.discarded = discarded;
	}

	public ObjectId getId() {
		return id;
	}

	public ObjectId getLocationId() {
		return locationId;
	}

	public ObjectId getDoctorId() {
		return doctorId;
	}

	public ObjectId getHospitalId() {
		return hospitalId;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public void setLocationId(ObjectId locationId) {
		this.locationId = locationId;
	}

	public void setDoctorId(ObjectId doctorId) {
		this.doctorId = doctorId;
	}

	public void setHospitalId(ObjectId hospitalId) {
		this.hospitalId = hospitalId;
	}

}
