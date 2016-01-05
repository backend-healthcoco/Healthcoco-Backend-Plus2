package com.dpdocter.services;

import java.util.Date;
import java.util.List;

import com.dpdocter.beans.Appointment;
import com.dpdocter.beans.City;
import com.dpdocter.beans.Clinic;
import com.dpdocter.beans.DoctorInfo;
import com.dpdocter.beans.Event;
import com.dpdocter.beans.Lab;
import com.dpdocter.beans.LandmarkLocality;
import com.dpdocter.beans.Slot;
import com.dpdocter.request.AppointmentRequest;
import com.dpdocter.request.EventRequest;
import com.dpdocter.response.ClinicAppointmentsResponse;
import com.dpdocter.solr.beans.Country;
import com.dpdocter.solr.beans.State;

public interface AppointmentService {

    City addCity(City city);

    Boolean activateDeactivateCity(String cityId, boolean activate);

    List<City> getCities();

    City getCity(String cityId);

    LandmarkLocality addLandmaklLocality(LandmarkLocality locality);

    Clinic getClinic(String locationId);

    List<DoctorInfo> getDoctors(String spetiality, String city, String localityOrLandmark);

    List<ClinicAppointmentsResponse> getClinicAppointments(String locationId, String doctorId, String patientId, String date,	String filterBy);

    List<Appointment> getDoctorAppointments(String locationId, String doctorId, String date, String patientId, List<String> filterBy, int page, int size);

    List<Appointment> getPatientAppointments(String locationId, String doctorId, String patientId, List<String> filterBy, int page, int size);

    Country addCountry(Country request);

	State addState(State request);

	Lab getLab(String locationId);

	List<Country> getCountries();

	List<State> getStates();

    List<Slot> getTimeSlots(String doctorId, String locationId, Date date);

	Appointment addAppointment(AppointmentRequest request);

	Appointment updateAppointment(AppointmentRequest request);

	Event addEditEvent(EventRequest request);

	Boolean cancelEvent(String eventId, String doctorId, String locationId);

	Boolean sendReminder(String appointmentId);

}
