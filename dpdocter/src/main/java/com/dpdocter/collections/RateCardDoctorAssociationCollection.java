package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "rate_card_doctor_association_cl")
public class RateCardDoctorAssociationCollection extends GenericCollection {

	private ObjectId id;
	private ObjectId dentalLabId;
	private ObjectId doctorId;
	private ObjectId rateCardId;
	private Boolean discarded = false;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public ObjectId getDentalLabId() {
		return dentalLabId;
	}

	public void setDentalLabId(ObjectId dentalLabId) {
		this.dentalLabId = dentalLabId;
	}

	public ObjectId getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(ObjectId doctorId) {
		this.doctorId = doctorId;
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
		return "RateCardDentalLabAssociationCollection [id=" + id + ", dentalLabId=" + dentalLabId + ", doctorId="
				+ doctorId + ", rateCardId=" + rateCardId + ", discarded=" + discarded + "]";
	}

}
