package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.City;
import com.dpdocter.beans.Clinic;
import com.dpdocter.beans.DoctorInfo;
import com.dpdocter.beans.Landmark;
import com.dpdocter.beans.Locality;

public interface AppointmentService {

    City addCity(City city);

    Boolean activateDeactivateCity(String cityId, boolean activate);

    List<City> getCities();

    City getCity(String cityId);

    Locality addLocality(Locality locality);

    Landmark addLandmark(Landmark landmark);

    List<Object> getLandmarkLocality(String cityId, String type);

    Clinic getClinic(String locationId);

    List<DoctorInfo> getDoctors(String spetiality, String city, String localityOrLandmark);

}
