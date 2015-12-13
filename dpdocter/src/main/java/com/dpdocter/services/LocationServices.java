package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.GeocodedLocation;

public interface LocationServices {
    public List<GeocodedLocation> geocodeLocation(String address);
}
