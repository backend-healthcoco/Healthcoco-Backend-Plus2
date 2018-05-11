package com.dpdocter.response;

import com.dpdocter.beans.Location;

public class DentalImagingLocationResponse {

	private String locationId;
	private Location location;

	public String getLocationId() {
		return locationId;
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

}
