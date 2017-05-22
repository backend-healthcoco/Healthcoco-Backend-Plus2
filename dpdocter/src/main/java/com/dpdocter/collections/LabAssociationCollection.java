package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "lab_association_cl")
public class LabAssociationCollection extends GenericCollection {

	@Id
	private ObjectId id;
	@Field
	private ObjectId parentLabId;
	@Field
	private ObjectId daughterLabId;
	@Field
	private Boolean isActive;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public ObjectId getParentLabId() {
		return parentLabId;
	}

	public void setParentLabId(ObjectId parentLabId) {
		this.parentLabId = parentLabId;
	}

	public ObjectId getDaughterLabId() {
		return daughterLabId;
	}

	public void setDaughterLabId(ObjectId daughterLabId) {
		this.daughterLabId = daughterLabId;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	@Override
	public String toString() {
		return "LabAssociationCollection [id=" + id + ", parentLabId=" + parentLabId + ", daughterLabId="
				+ daughterLabId + ", isActive=" + isActive + "]";
	}

}
