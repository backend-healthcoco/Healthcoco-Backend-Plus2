package com.dpdocter.beans;

import com.dpdocter.collections.GenericCollection;

public class RateCardDoctorAssociation extends GenericCollection {

	private String id;
	private String dentalLabId;
	private String doctorId;
	private String rateCardId;
	private Boolean discarded = false;
	private RateCard rateCard;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDentalLabId() {
		return dentalLabId;
	}

	public void setDentalLabId(String dentalLabId) {
		this.dentalLabId = dentalLabId;
	}

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
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
	

	public RateCard getRateCard() {
		return rateCard;
	}

	public void setRateCard(RateCard rateCard) {
		this.rateCard = rateCard;
	}

	@Override
	public String toString() {
		return "RateCardDentalLabAssociation [id=" + id + ", dentalLabId=" + dentalLabId + ", doctorId=" + doctorId
				+ ", rateCardId=" + rateCardId + ", discarded=" + discarded + "]";
	}

}
