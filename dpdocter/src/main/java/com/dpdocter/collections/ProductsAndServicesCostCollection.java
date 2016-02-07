package com.dpdocter.collections;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "products_and_services_costs_cl")
public class ProductsAndServicesCostCollection extends GenericCollection {
    @Id
    private String id;

    @Field
    private String locationId;

    @Field
    private String hospitalId;

    @Field
    private String doctorId;

    @Field
    private String productAndServiceId;

    @Field
    private double cost = 0.0;

    @Field
    private boolean discarded = false;

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

    public String getDoctorId() {
	return doctorId;
    }

    public void setDoctorId(String doctorId) {
	this.doctorId = doctorId;
    }

    public String getProductAndServiceId() {
	return productAndServiceId;
    }

    public void setProductAndServiceId(String productAndServiceId) {
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
