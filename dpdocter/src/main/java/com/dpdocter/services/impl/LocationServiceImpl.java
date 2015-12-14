package com.dpdocter.services.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.dpdocter.beans.GeocodedLocation;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.services.LocationServices;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.GeocodingResult;

@Service
public class LocationServiceImpl implements LocationServices {
    @Value("${GEOCODING_SERVICES_API_KEY}")
    private String GEOCODING_SERVICES_API_KEY;

    @Override
    public List<GeocodedLocation> geocodeLocation(String address) {
	List<GeocodedLocation> response = null;
	GeoApiContext context = new GeoApiContext().setApiKey(GEOCODING_SERVICES_API_KEY);
	GeocodingResult[] results = null;
	try {
	    results = GeocodingApi.geocode(context, address).await();
	    if (results != null && results.length != 0) {
		response = new ArrayList<GeocodedLocation>();
		for (GeocodingResult result : results) {
		    GeocodedLocation geocodedLocation = new GeocodedLocation();
		    geocodedLocation.setFormattedAddress(result.formattedAddress);
		    geocodedLocation.setLatitude(result.geometry.location.lat);
		    geocodedLocation.setLongitude(result.geometry.location.lng);
		    response.add(geocodedLocation);
		}
	    }
	} catch (Exception e) {
	    throw new BusinessException(ServiceError.Unknown, "Couldn't Geocode the location");
	}
	return response;
    }

}
