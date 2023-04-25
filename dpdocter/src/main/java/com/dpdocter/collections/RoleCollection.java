package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "role_cl")
public class RoleCollection extends GenericCollection {

	@Id
	private ObjectId id;

	@Field
	private String role;

	@Field
	private String explanation;

	@Indexed
	private ObjectId locationId;

	@Indexed
	private ObjectId hospitalId;

	@Field
	private Boolean discarded = false;

	public RoleCollection(String role, ObjectId locationId, ObjectId hospitalId) {
		this.id = null;
		this.role = role;
		this.locationId = locationId;
		this.hospitalId = hospitalId;
	}

	public RoleCollection() {
		// TODO Auto-generated constructor stub
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getExplanation() {
		return explanation;
	}

	public void setExplanation(String explanation) {
		this.explanation = explanation;
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

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	@Override
	public String toString() {
		return "RoleCollection [id=" + id + ", role=" + role + ", explanation=" + explanation + ", locationId="
				+ locationId + ", hospitalId=" + hospitalId + ", discarded=" + discarded + "]";
	}

}
