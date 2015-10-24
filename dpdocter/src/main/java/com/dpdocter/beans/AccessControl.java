package com.dpdocter.beans;

import java.util.List;

public class AccessControl {
    private String id;

    private String roleOrUserId;

    private String hospitalId;

    private String locationId;

    private String roleType;

    private List<AccessModule> accessModules;

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

    public List<AccessModule> getAccessModules() {
	return accessModules;
    }

    public void setAccessModules(List<AccessModule> accessModules) {
	this.accessModules = accessModules;
    }

    @Override
    public String toString() {
	return "AccessControl [id=" + id + ", roleOrUserId=" + roleOrUserId + ", hospitalId=" + hospitalId + ", locationId=" + locationId + ", roleType="
		+ roleType + ", accessModules=" + accessModules + "]";
    }

}
