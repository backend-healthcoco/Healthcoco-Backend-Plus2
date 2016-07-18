package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "products_and_services_costs_cl")
public class ProductsAndServicesCostCollection extends GenericCollection {
    @Id
    private ObjectId id;

    @Field
    private ObjectId locationId;

    @Field
    private ObjectId hospitalId;

    @Field
    private ObjectId doctorId;

    @Field
    private ObjectId productAndServiceId;

    @Field
    private double cost = 0.0;

    @Field
    private boolean discarded = false;

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

    public ObjectId getProductAndServiceId() {
	return productAndServiceId;
    }

    public void setProductAndServiceId(ObjectId productAndServiceId) {
	this.productAndServiceId = productAndServiceId;
    }

    public double getCost() {
	return cost;
    }

    public void setCost(double cost) {
	this.cost = cost;
    }

    public boolean isDiscarded() {
	return discarded;
    }

    public void setDiscarded(boolean discarded) {
	this.discarded = discarded;
    }

    @Override
    public String toString() {
	return "ProductsAndServicesCostCollection [id=" + id + ", locationId=" + locationId + ", hospitalId=" + hospitalId + ", doctorId=" + doctorId
		+ ", productAndServiceId=" + productAndServiceId + ", cost=" + cost + ", discarded=" + discarded + "]";
    }

}
