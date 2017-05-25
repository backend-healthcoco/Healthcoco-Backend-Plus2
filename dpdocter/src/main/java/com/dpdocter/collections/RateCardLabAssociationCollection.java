package com.dpdocter.collections;

import org.bson.types.ObjectId;

public class RateCardLabAssociationCollection extends GenericCollection {

	private ObjectId id;
	private ObjectId locationId;
	private ObjectId rateCardId;
	private Boolean discarded = false;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public ObjectId getLocationId() {
		return locationId;
	}

	public void setLocationId(ObjectId locationId) {
		this.locationId = locationId;
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
		return "RateCardLabAssociationCollection [id=" + id + ", locationId=" + locationId + ", rateCardId="
				+ rateCardId + ", discarded=" + discarded + "]";
	}

}
