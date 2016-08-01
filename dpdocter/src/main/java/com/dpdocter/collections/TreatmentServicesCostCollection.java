package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "treatment_services_costs_cl")
public class TreatmentServicesCostCollection extends GenericCollection {
    @Id
    private ObjectId id;

    @Field
    private ObjectId locationId;

    @Field
    private ObjectId hospitalId;

    @Field
    private ObjectId doctorId;

    @Field
    private ObjectId treatmentServiceId;

    @Field
    private double cost = 0.0;

    @Field
    private Boolean discarded = false;

    @Field
    private int ranking = 0;

    @Field
    private Boolean isFav = false;

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

    public ObjectId getDoctorId() {
	return doctorId;
    }

    public void setDoctorId(ObjectId doctorId) {
	this.doctorId = doctorId;
    }

    public ObjectId getTreatmentServiceId() {
		return treatmentServiceId;
	}

	public void setTreatmentServiceId(ObjectId treatmentServiceId) {
		this.treatmentServiceId = treatmentServiceId;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public double getCost() {
	return cost;
    }

    public void setCost(double cost) {
	this.cost = cost;
    }

    public Boolean isDiscarded() {
	return discarded;
    }

    public void setDiscarded(Boolean discarded) {
	this.discarded = discarded;
    }

	public int getRanking() {
		return ranking;
	}

	public void setRanking(int ranking) {
		this.ranking = ranking;
	}

	public Boolean getIsFav() {
		return isFav;
	}

	public void setIsFav(Boolean isFav) {
		this.isFav = isFav;
	}

	@Override
	public String toString() {
		return "TreatmentServicesCostCollection [id=" + id + ", locationId=" + locationId + ", hospitalId=" + hospitalId
				+ ", doctorId=" + doctorId + ", treatmentServiceId=" + treatmentServiceId + ", cost=" + cost
				+ ", discarded=" + discarded + ", ranking=" + ranking + ", isFav=" + isFav + "]";
	}

}
