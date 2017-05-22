package com.dpdocter.response;

import com.dpdocter.beans.Location;

public class LabAssociationLookupResponse {

	private String parentLabId;
	private String daughterLabId;
	private Location location;

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

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	@Override
	public String toString() {
		return "LabAssociationLookupResponse [parentLabId=" + parentLabId + ", daughterLabId=" + daughterLabId
				+ ", location=" + location + "]";
	}

}
