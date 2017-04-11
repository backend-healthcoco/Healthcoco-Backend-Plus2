package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.GeocodedLocation;
import com.dpdocter.beans.Location;

public interface LocationServices {
	public List<GeocodedLocation> geocodeLocation(String address);

	List<GeocodedLocation> geocodeTimeZone(Double latitude, Double longitude);

	public Location addEditRecommedation(String locationId, String patientId);

	Boolean setDefaultLab(String locationId, String defaultLabId);
}
