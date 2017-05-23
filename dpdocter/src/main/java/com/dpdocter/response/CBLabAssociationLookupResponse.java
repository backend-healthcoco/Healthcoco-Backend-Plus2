package com.dpdocter.response;

import com.dpdocter.beans.Location;

public class CBLabAssociationLookupResponse {

	private String id;
	private String collectionBoyId;
	private String parentLabId;
	private String daughterLabId;
	private Boolean isActive = true;
	private Location location;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCollectionBoyId() {
		return collectionBoyId;
	}

	public void setCollectionBoyId(String collectionBoyId) {
		this.collectionBoyId = collectionBoyId;
	}

	public String getParentLabId() {
		return parentLabId;
	}

	public void setParentLabId(String parentLabId) {
		this.parentLabId = parentLabId;
	}

	public String getDaughterLabId() {
		return daughterLabId;
	}

	public void setDaughterLabId(String daughterLabId) {
		this.daughterLabId = daughterLabId;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	@Override
	public String toString() {
		return "CBLabAssociationLookupResponse [id=" + id + ", collectionBoyId=" + collectionBoyId + ", parentLabId="
				+ parentLabId + ", daughterLabId=" + daughterLabId + ", isActive=" + isActive + ", location=" + location
				+ "]";
	}

}
