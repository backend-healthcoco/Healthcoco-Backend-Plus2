package com.dpdocter.beans;

import com.dpdocter.collections.GenericCollection;

public class RateCardLabAssociation extends GenericCollection {

	private String id;
	private String daughterLabId;
	private String parentLabId;
	private String rateCardId;
	private Boolean discarded = false;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDaughterLabId() {
		return daughterLabId;
	}

	public void setDaughterLabId(String daughterLabId) {
		this.daughterLabId = daughterLabId;
	}

	public String getParentLabId() {
		return parentLabId;
	}

	public void setParentLabId(String parentLabId) {
		this.parentLabId = parentLabId;
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
		return "RateCardLabAssociation [id=" + id + ", duaghterLabId=" + daughterLabId + ", parentLabId=" + parentLabId
				+ ", rateCardId=" + rateCardId + ", discarded=" + discarded + "]";
	}

}
