package com.dpdocter.beans;

import java.util.List;

import com.dpdocter.collections.GenericCollection;

public class Role extends GenericCollection {

    private String id;

    private String role;

    private String description;

    private String locationId;

    private String hospitalId;

    private List<AccessModule> accessModules;

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

    public List<AccessModule> getAccessModules() {
	return accessModules;
    }

    public void setAccessModules(List<AccessModule> accessModules) {
	this.accessModules = accessModules;
    }

    @Override
    public String toString() {
	return "Role [id=" + id + ", role=" + role + ", description=" + description + ", locationId=" + locationId + ", hospitalId=" + hospitalId
		+ ", accessModules=" + accessModules + "]";
    }
}
