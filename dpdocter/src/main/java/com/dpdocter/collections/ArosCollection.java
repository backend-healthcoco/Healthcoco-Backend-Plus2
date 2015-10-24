package com.dpdocter.collections;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "aros_cl")
public class ArosCollection {
    @Id
    private String id;

    @Field
    private String roleOrUserId;

    @Field
    private String hospitalId;

    @Field
    private String locationId;

    @Field
    private String roleType;

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public String getRoleOrUserId() {
	return roleOrUserId;
    }

    public void setRoleOrUserId(String roleOrUserId) {
	this.roleOrUserId = roleOrUserId;
    }

    public String getHospitalId() {
	return hospitalId;
    }

    public void setHospitalId(String hospitalId) {
	this.hospitalId = hospitalId;
    }

    public String getLocationId() {
	return locationId;
    }

    public void setLocationId(String locationId) {
	this.locationId = locationId;
    }

    public String getRoleType() {
	return roleType;
    }

    public void setRoleType(String roleType) {
	this.roleType = roleType;
    }

    @Override
    public String toString() {
	return "ArosCollection [id=" + id + ", roleOrUserId=" + roleOrUserId + ", hospitalId=" + hospitalId + ", locationId=" + locationId + ", roleType="
		+ roleType + "]";
    }

}
