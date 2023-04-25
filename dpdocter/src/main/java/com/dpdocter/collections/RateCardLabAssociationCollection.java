package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "rate_card_lab_association_cl")
public class RateCardLabAssociationCollection extends GenericCollection {
	@Id
	private ObjectId id;
	@Field
	private ObjectId daughterLabId;
	@Field
	private ObjectId parentLabId;
	@Field
	private ObjectId rateCardId;
	@Field
	private Boolean discarded = false;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public ObjectId getDaughterLabId() {
		return daughterLabId;
	}

	public void setDaughterLabId(ObjectId daughterLabId) {
		this.daughterLabId = daughterLabId;
	}

	public ObjectId getParentLabId() {
		return parentLabId;
	}

	public void setParentLabId(ObjectId parentLabId) {
		this.parentLabId = parentLabId;
	}

	public ObjectId getRateCardId() {
		return rateCardId;
	}

	public void setRateCardId(ObjectId rateCardId) {
		this.rateCardId = rateCardId;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

}
