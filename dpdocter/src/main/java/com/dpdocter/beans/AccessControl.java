package com.dpdocter.beans;

import java.util.List;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.dpdocter.enums.Type;
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class AccessControl {
    private String id;

    private String roleOrUserId;

    private String hospitalId;

    private String locationId;

    private Type type;

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

    public Type getType() {
	return type;
    }

    public void setType(Type type) {
	this.type = type;
    }

    public List<AccessModule> getAccessModules() {
	return accessModules;
    }

    public void setAccessModules(List<AccessModule> accessModules) {
	this.accessModules = accessModules;
    }

    @Override
    public String toString() {
	return "AccessControl [id=" + id + ", roleOrUserId=" + roleOrUserId + ", hospitalId=" + hospitalId + ", locationId=" + locationId + ", type=" + type
		+ ", accessModules=" + accessModules + "]";
    }

}
