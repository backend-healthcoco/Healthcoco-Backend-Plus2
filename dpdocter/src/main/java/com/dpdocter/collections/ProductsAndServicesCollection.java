package com.dpdocter.collections;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "products_and_services_cl")
public class ProductsAndServicesCollection extends GenericCollection {
    @Id
    private String id;

    @Field
    private String name;

    @Field
    private List<String> specialityIds;

    @Field
    private String locationId;

    @Field
    private String hospitalId;

    @Field
    private String doctorId;

    @Field
    private boolean discarded = false;

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public List<String> getSpecialityIds() {
	return specialityIds;
    }

    public void setSpecialityIds(List<String> specialityIds) {
	this.specialityIds = specialityIds;
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
