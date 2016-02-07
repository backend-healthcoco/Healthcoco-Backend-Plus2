package com.dpdocter.collections;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "role_cl")
public class RoleCollection extends GenericCollection {

    @Id
    private String id;

    @Field
    private String role;

    @Field
    private String description;

    @Field
    private String locationId;

    @Field
    private String hospitalId;

    @Field
    private Boolean discarded = false;

    public RoleCollection(String role, String locationId, String hospitalId) {
	this.role = role;
	this.locationId = locationId;
	this.hospitalId = hospitalId;
    }

    public RoleCollection() {
	// TODO Auto-generated constructor stub
    }

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public String getRole() {
	return role;
    }

    public void setRole(String role) {
	this.role = role;
    }

    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
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

    public Boolean getDiscarded() {
	return discarded;
    }

    public void setDiscarded(Boolean discarded) {
	this.discarded = discarded;
    }

    @Override
    public String toString() {
	return "RoleCollection [id=" + id + ", role=" + role + ", description=" + description + ", locationId=" + locationId + ", hospitalId=" + hospitalId
		+ ", discarded=" + discarded + "]";
    }

}
