package com.dpdocter.services;

import java.util.Date;
import java.util.List;

import com.dpdocter.beans.Appointment;
import com.dpdocter.beans.City;
import com.dpdocter.beans.Clinic;
import com.dpdocter.beans.Lab;
import com.dpdocter.beans.LandmarkLocality;
import com.dpdocter.beans.PatientQueue;
import com.dpdocter.beans.Slot;
import com.dpdocter.request.AppointmentRequest;
import com.dpdocter.request.EventRequest;
import com.dpdocter.request.PatientQueueAddEditRequest;
import com.dpdocter.solr.beans.Country;
import com.dpdocter.solr.beans.State;

public interface AppointmentService {

    City addCity(City city);

    Boolean activateDeactivateCity(String cityId, boolean activate);

    List<City> getCities();

    City getCity(String cityId);

    LandmarkLocality addLandmaklLocality(LandmarkLocality locality);

    Clinic getClinic(String locationId);

    List<Appointment> getAppointments(String locationId, List<String> doctorId, String patientId, String from, String to, int page, int size);

    List<Appointment> getPatientAppointments(String locationId, String doctorId, String patientId, String from, String to, int page, int size);

    Country addCountry(Country request);

    State addState(State request);

    Lab getLab(String locationId);

    List<Country> getCountries();

    List<State> getStates();

    List<Slot> getTimeSlots(String doctorId, String locationId, Date date);

    Appointment addAppointment(AppointmentRequest request);

    Appointment updateAppointment(AppointmentRequest request);

    Appointment addEvent(EventRequest request);

    Appointment updateEvent(EventRequest request);

    Boolean sendReminder(String appointmentId);

    void importMaster();

    List<PatientQueue> addPatientInQueue(PatientQueueAddEditRequest request);

    List<PatientQueue> rearrangePatientInQueue(String doctorId, String locationId, String hospitalId, String patientId, String appointmentId, int sequenceNo);

    List<PatientQueue> getPatientQueue(String doctorId, String locationId, String hospitalId);

}
