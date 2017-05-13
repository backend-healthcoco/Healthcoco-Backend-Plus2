package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.GeocodedLocation;
import com.dpdocter.beans.LabTestPickup;
import com.dpdocter.beans.Location;
import com.dpdocter.request.AddEditLabTestPickupRequest;

public interface LocationServices {
	public List<GeocodedLocation> geocodeLocation(String address);

	List<GeocodedLocation> geocodeTimeZone(Double latitude, Double longitude);

	public Location addEditRecommedation(String locationId, String patientId);

	Boolean setDefaultLab(String locationId, String defaultLabId);

	LabTestPickup addEditLabTestPickupRequest(AddEditLabTestPickupRequest request);

	Boolean verifyCRN(String locationId, String crn, String requestId);
}
