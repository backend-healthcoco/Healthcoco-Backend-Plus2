package com.dpdocter.collections;

import org.bson.types.ObjectId;

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

	@Override
	public String toString() {
		return "RateCardLabAssociationCollection [id=" + id + ", daughterLabId=" + daughterLabId + ", parentLabId="
				+ parentLabId + ", rateCardId=" + rateCardId + ", discarded=" + discarded + "]";
	}

}
