package com.dpdocter.services;

import java.util.Date;
import java.util.List;

import com.dpdocter.beans.Appointment;
import com.dpdocter.beans.City;
import com.dpdocter.beans.Clinic;
import com.dpdocter.beans.Lab;
import com.dpdocter.beans.LandmarkLocality;
import com.dpdocter.beans.PatientQueue;
import com.dpdocter.request.AppointmentRequest;
import com.dpdocter.request.EventRequest;
import com.dpdocter.request.PatientQueueAddEditRequest;
import com.dpdocter.response.SlotDataResponse;

public interface AppointmentService {

    City addCity(City city);

    Boolean activateDeactivateCity(String cityId, boolean activate);

    List<City> getCities(String state);

    City getCity(String cityId);

    LandmarkLocality addLandmaklLocality(LandmarkLocality locality);

    Clinic getClinic(String locationId);

    List<Appointment> getAppointments(String locationId, List<String> doctorId, String patientId, String from, String to, int page, int size, String updatedTime);

    List<Appointment> getPatientAppointments(String locationId, String doctorId, String patientId, String from, String to, int page, int size, String updatedTime);

    Lab getLab(String locationId);

    List<City> getCountries();

    List<City> getStates(String country);

    SlotDataResponse getTimeSlots(String doctorId, String locationId, Date date);

    Appointment addAppointment(AppointmentRequest request);

    Appointment updateAppointment(AppointmentRequest request);

    Appointment addEvent(EventRequest request);

    Appointment updateEvent(EventRequest request);

    Boolean sendReminderToPatient(String appointmentId);

    List<PatientQueue> addPatientInQueue(PatientQueueAddEditRequest request);

    List<PatientQueue> rearrangePatientInQueue(String doctorId, String locationId, String hospitalId, String patientId, String appointmentId, int sequenceNo);

    List<PatientQueue> getPatientQueue(String doctorId, String locationId, String hospitalId);

	Boolean sendReminderToDoctor(String appointmentId);

}
