package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

public class GenericCodeWithStrengthCollection extends GenericCollection {
	@Id
	private ObjectId id;

	@Field
	private ObjectId genericId;

	@Field
	private String strength;

	@Field
	private Boolean discarded = false;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public ObjectId getGenericId() {
		return genericId;
	}

	public void setGenericId(ObjectId genericId) {
		this.genericId = genericId;
	}

	public String getStrength() {
		return strength;
	}

	public void setStrength(String strength) {
		this.strength = strength;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	@Override
	public String toString() {
		return "GenericCodeWithStrengthCollection [id=" + id + ", genericId=" + genericId + ", strength=" + strength
				+ ", discarded=" + discarded + "]";
	}

}
