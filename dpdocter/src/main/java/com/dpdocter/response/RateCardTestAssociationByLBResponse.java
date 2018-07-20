package com.dpdocter.response;

import java.util.List;

import com.dpdocter.beans.RateCardTestAssociation;

public class RateCardTestAssociationByLBResponse {
	private String specimen;
	private List<RateCardTestAssociation> rateCards;

	public String getSpecimen() {
		return specimen;
	}

	public void setSpecimen(String specimen) {
		this.specimen = specimen;
	}

	public List<RateCardTestAssociation> getRateCards() {
		return rateCards;
	}

	public void setRateCards(List<RateCardTestAssociation> rateCards) {
		this.rateCards = rateCards;
	}
}