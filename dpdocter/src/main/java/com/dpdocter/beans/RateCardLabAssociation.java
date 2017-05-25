package com.dpdocter.beans;

import com.dpdocter.collections.GenericCollection;

public class RateCardLabAssociation extends GenericCollection {

	private String id;
	private String locationId;
	private String rateCardId;
	private Boolean discarded = false;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLocationId() {
		return locationId;
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}

	public String getRateCardId() {
		return rateCardId;
	}

	public void setRateCardId(String rateCardId) {
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
		return "RateCardLabAssociation [id=" + id + ", locationId=" + locationId + ", rateCardId=" + rateCardId
				+ ", discarded=" + discarded + "]";
	}

}
