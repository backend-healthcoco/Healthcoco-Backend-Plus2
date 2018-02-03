package com.dpdocter.beans;

import com.dpdocter.collections.GenericCollection;

public class RateCardDentalWorkAssociation extends GenericCollection {

	private String id;
	private String locationId;
	private String hospitalId;
	private String dentalWorkId;
	private String rateCardId;
	private Integer eta;
	private Double cost;
	private Boolean isAvailable = true;
	private DentalWork dentalWork;
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

	public String getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(String hospitalId) {
		this.hospitalId = hospitalId;
	}

	public String getDentalWorkId() {
		return dentalWorkId;
	}

	public void setDentalWorkId(String dentalWorkId) {
		this.dentalWorkId = dentalWorkId;
	}

	public String getRateCardId() {
		return rateCardId;
	}

	public void setRateCardId(String rateCardId) {
		this.rateCardId = rateCardId;
	}

	public Integer getEta() {
		return eta;
	}

	public void setEta(Integer eta) {
		this.eta = eta;
	}

	public Double getCost() {
		return cost;
	}

	public void setCost(Double cost) {
		this.cost = cost;
	}

	public Boolean getIsAvailable() {
		return isAvailable;
	}

	public void setIsAvailable(Boolean isAvailable) {
		this.isAvailable = isAvailable;
	}

	public DentalWork getDentalWork() {
		return dentalWork;
	}

	public void setDentalWork(DentalWork dentalWork) {
		this.dentalWork = dentalWork;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	@Override
	public String toString() {
		return "RateCardDentalWorkAssociation [id=" + id + ", locationId=" + locationId + ", hospitalId=" + hospitalId
				+ ", dentalWorkId=" + dentalWorkId + ", rateCardId=" + rateCardId + ", eta=" + eta + ", cost=" + cost
				+ ", isAvailable=" + isAvailable + ", dentalWork=" + dentalWork + "]";
	}

}
