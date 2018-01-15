package com.dpdocter.collections;

import org.bson.types.ObjectId;

public class RateCardDentalWorkAssociation extends GenericCollection {

	private ObjectId id;
	private ObjectId locationId;
	private ObjectId hospitalId;
	private ObjectId dentalWorkId;
	private ObjectId rateCardId;
	private Integer turnaroundTime;
	private Double cost;
	private Boolean isAvailable = true;

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

	public ObjectId getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(ObjectId hospitalId) {
		this.hospitalId = hospitalId;
	}

	public ObjectId getDentalWorkId() {
		return dentalWorkId;
	}

	public void setDentalWorkId(ObjectId dentalWorkId) {
		this.dentalWorkId = dentalWorkId;
	}

	public ObjectId getRateCardId() {
		return rateCardId;
	}

	public void setRateCardId(ObjectId rateCardId) {
		this.rateCardId = rateCardId;
	}

	public Integer getTurnaroundTime() {
		return turnaroundTime;
	}

	public void setTurnaroundTime(Integer turnaroundTime) {
		this.turnaroundTime = turnaroundTime;
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

}
