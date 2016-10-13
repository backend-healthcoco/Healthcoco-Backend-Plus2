package com.dpdocter.beans;

import java.util.List;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.dpdocter.collections.GenericCollection;
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Role extends GenericCollection {

    private String id;

    private String role;

    private String explanation;

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

    public String getExplanation() {
		return explanation;
	}

	public void setExplanation(String explanation) {
		this.explanation = explanation;
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
		return "Role [id=" + id + ", role=" + role + ", explanation=" + explanation + ", locationId=" + locationId
				+ ", hospitalId=" + hospitalId + ", accessModules=" + accessModules + "]";
	}

}
