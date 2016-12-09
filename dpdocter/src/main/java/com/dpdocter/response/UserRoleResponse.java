package com.dpdocter.response;

import java.util.List;

import com.dpdocter.collections.GenericCollection;
import com.dpdocter.collections.UserRoleCollection;

public class UserRoleResponse extends GenericCollection{

	private String id;

    private String role;

    private String explanation;

    private String locationId;

    private String hospitalId;

    private Boolean discarded = false;

    List<UserRoleCollection> userRoleCollections;

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

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	public List<UserRoleCollection> getUserRoleCollections() {
		return userRoleCollections;
	}

	public void setUserRoleCollections(List<UserRoleCollection> userRoleCollections) {
		this.userRoleCollections = userRoleCollections;
	}

	@Override
	public String toString() {
		return "UserRoleResponse [id=" + id + ", role=" + role + ", explanation=" + explanation + ", locationId="
				+ locationId + ", hospitalId=" + hospitalId + ", discarded=" + discarded + ", userRoleCollections="
				+ userRoleCollections + "]";
	}
}
