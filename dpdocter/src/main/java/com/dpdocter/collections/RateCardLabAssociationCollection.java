package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "rate_card_lab_association_cl")
public class RateCardLabAssociationCollection extends GenericCollection {

	private ObjectId id;
	private ObjectId daughterLabId;
	private ObjectId parentLabId;
	private ObjectId rateCardId;
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
