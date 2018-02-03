package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "rate_card_dental_work_association_cl")
public class RateCardDentalWorkAssociationCollection extends GenericCollection {

	@Id
	private ObjectId id;
	@Field
	private ObjectId locationId;
	@Field
	private ObjectId hospitalId;
	@Field
	private ObjectId dentalWorkId;
	@Field
	private ObjectId rateCardId;
	@Field
	private Integer eta;
	@Field
	private Double cost;
	@Field
	private Boolean isAvailable = true;
	@Field
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

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

}
