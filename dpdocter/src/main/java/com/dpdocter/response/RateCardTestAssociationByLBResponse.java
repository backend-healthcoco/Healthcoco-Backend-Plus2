package com.dpdocter.response;

import java.util.List;

import com.dpdocter.beans.RateCardTestAssociation;

public class RateCardTestAssociationByLBResponse {
	private String specimen;
	private List<RateCardTestAssociation> ratecards;
	public String getSpecimen() {
		return specimen;
	}
	public void setSpecimen(String specimen) {
		this.specimen = specimen;
	}
	public List<RateCardTestAssociation> getRatecards() {
		return ratecards;
	}
	public void setRatecards(List<RateCardTestAssociation> ratecards) {
		this.ratecards = ratecards;
	}
	
}
