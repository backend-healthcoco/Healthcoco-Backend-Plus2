package com.dpdocter.collections;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "products_and_services_cl")
public class ProductsAndServicesCollection extends GenericCollection {
    @Id
    private ObjectId id;

    @Field
    private String name;

    @Field
    private List<ObjectId> specialityIds;

    @Field
    private ObjectId locationId;

    @Field
    private ObjectId hospitalId;

    @Field
    private ObjectId doctorId;

    @Field
    private boolean discarded = false;

    public ObjectId getId() {
	return id;
    }

    public void setId(ObjectId id) {
	this.id = id;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public List<ObjectId> getSpecialityIds() {
	return specialityIds;
    }

    public void setSpecialityIds(List<ObjectId> specialityIds) {
	this.specialityIds = specialityIds;
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

    public boolean isDiscarded() {
	return discarded;
    }

    public void setDiscarded(boolean discarded) {
	this.discarded = discarded;
    }

    @Override
    public String toString() {
	return "ProductsAndServicesCollection [id=" + id + ", name=" + name + ", specialityIds=" + specialityIds + ", locationId=" + locationId
		+ ", hospitalId=" + hospitalId + ", doctorId=" + doctorId + ", discarded=" + discarded + "]";
    }

}
