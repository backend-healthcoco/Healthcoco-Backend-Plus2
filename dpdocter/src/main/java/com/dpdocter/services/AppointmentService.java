package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.Appointment;
import com.dpdocter.beans.City;
import com.dpdocter.beans.Clinic;
import com.dpdocter.beans.DoctorInfo;
import com.dpdocter.beans.Landmark;
import com.dpdocter.beans.LandmarkLocality;
import com.dpdocter.request.AppoinmentRequest;
import com.dpdocter.solr.beans.Country;

public interface AppointmentService {

    City addCity(City city);

    Boolean activateDeactivateCity(String cityId, boolean activate);

    List<City> getCities();

    City getCity(String cityId);

    LandmarkLocality addLandmaklLocality(LandmarkLocality locality);

    List<Object> getLandmarkLocality(String cityId, String type);

    Clinic getClinic(String locationId);

    List<DoctorInfo> getDoctors(String spetiality, String city, String localityOrLandmark);

	Appointment appointment(AppoinmentRequest request);

	List<Appointment> getClinicAppointments(String locationId, String doctorId, int day, int month, int week, int page, int size, String updatedTime);

	List<Appointment> getDoctorAppointments(String locationId, String doctorId, int day, int month, int week, int page, int size, String updatedTime);

	List<Appointment> getPatientAppointments(String locationId, String doctorId, String patientId, int day,	int month, int week, int page, int size, String updatedTime);

	Country addCountry(Country request);

}
