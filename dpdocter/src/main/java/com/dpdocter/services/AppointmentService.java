package com.dpdocter.services;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;

import com.dpdocter.beans.Appointment;
import com.dpdocter.beans.City;
import com.dpdocter.beans.Clinic;
import com.dpdocter.beans.CustomAppointment;
import com.dpdocter.beans.Event;
import com.dpdocter.beans.Lab;
import com.dpdocter.beans.LandmarkLocality;
import com.dpdocter.beans.NutritionAppointment;
import com.dpdocter.beans.PatientQueue;
import com.dpdocter.request.AppointmentRequest;
import com.dpdocter.request.EventRequest;
import com.dpdocter.request.PatientQueueAddEditRequest;
import com.dpdocter.request.PrintPatientCardRequest;
import com.dpdocter.response.AVGTimeDetail;
import com.dpdocter.response.LocationWithAppointmentCount;
import com.dpdocter.response.LocationWithPatientQueueDetails;
import com.dpdocter.response.SlotDataResponse;

import common.util.web.Response;

public interface AppointmentService {

	City addCity(City city);

	Boolean activateDeactivateCity(String cityId, boolean activate);

	List<City> getCities(String state);

	City getCity(String cityId);

	LandmarkLocality addLandmaklLocality(LandmarkLocality locality);

	Clinic getClinic(String locationId, String role, Boolean active);

	Clinic getClinic(String slugUrl);

	Response<Appointment> getAppointments(String locationId, List<String> doctorId, String patientId, String from,
			String to, int page, int size, String updatedTime, String status, String sortBy, String fromTime,
			String toTime, Boolean isRegisteredPatientRequired, Boolean isWeb);

	Response<Object> getPatientAppointments(String locationId, String doctorId, String patientId, String from,
			String to, int page, int size, String updatedTime, String type);

	Lab getLab(String locationId, String patientId, Boolean active);

	Lab getLab(String slugUrl);

	List<City> getCountries();

	List<City> getStates(String country);

	SlotDataResponse getTimeSlots(String doctorId, String locationId, Date date, Boolean isPatient);

	Appointment addAppointment(AppointmentRequest request, Boolean isFormattedResponseRequired);

	Appointment updateAppointment(AppointmentRequest request, Boolean updateVisit, Boolean isStatusChange);

	Event addEvent(EventRequest request, Boolean forAllDoctors);

	Event updateEvent(EventRequest request, Boolean forAllDoctors);

	Boolean sendReminderToPatient(String appointmentId);

	List<PatientQueue> addPatientInQueue(PatientQueueAddEditRequest request);

	List<PatientQueue> rearrangePatientInQueue(String doctorId, String locationId, String hospitalId, String patientId,
			String appointmentId, int sequenceNo);

	List<PatientQueue> getPatientQueue(String doctorId, String locationId, String hospitalId, String status);

	Appointment getAppointmentById(ObjectId appointmentId);

	LocationWithPatientQueueDetails getNoOfPatientInQueue(String locationId, List<String> doctorId, String from,
			String to);

	LocationWithAppointmentCount getDoctorsWithAppointmentCount(String locationId, String role, Boolean active,
			String from, String to);

	public void updateQueue();

	public CustomAppointment addCustomAppointment(CustomAppointment request);

	public CustomAppointment deleteCustomAppointment(String appointmentId, String locationId, String hospitalId,
			String doctorId, Boolean discarded);

	public CustomAppointment getCustomAppointmentById(String appointmentId);

	public List<CustomAppointment> getCustomAppointments(long page, int size, String locationId, String hospitalId,
			String doctorId, String updatedTime, Boolean discarded);

	public AVGTimeDetail getCustomAppointmentAVGTimeDetail(String locationId, String hospitalId, String doctorId);

	Appointment getPatientLastAppointment(String locationId, String doctorId, String patientId);

	Appointment updateAppointmentDoctor(String appointmentId, String doctorId);

	String printPatientCard(PrintPatientCardRequest request);

	Object changeStatusInAppointment(String doctorId, String locationId, String hospitalId, String patientId,
			String appointmentId, String status, Boolean isObjectRequired);

	public String downloadCalender(List<String> doctorIds, String locationId, String hospitalId, String from, String to,
			Boolean isGroupByDoctor, Boolean showMobileNo, Boolean showAppointmentStatus, Boolean showNotes,
			Boolean showPatientGroups, Boolean showCategory,Boolean showTreatment);

	Response<Event> getEvents(String locationId, List<String> doctorId, String from, String to, int page, int size,
			String updatedTime, String sortBy, String fromTime, String toTime, Boolean isCalenderBlocked, String state);

	Event getEventById(String eventId);

	Response<Event> getEventsByMonth(String locationId, List<String> doctorId, String from, String to, int page, int size,
			String updatedTime, String sortBy, String fromTime, String toTime, Boolean isCalenderBlocked, String state);

	Boolean checkToday(int dayOfDate, int yearOfDate, String timeZone);

	Integer getMinutesOfDay(Date date);

	NutritionAppointment deleteNutritionAppointment(String appointmentId, Boolean discarded);

	NutritionAppointment getNutritionAppointmentById(String appointmentId);

	List<NutritionAppointment> getNutritionAppointments(int page, int size, String userId, String fromDate,
			String toDate);

	Boolean addEditNutritionAppointment(NutritionAppointment request);

	Boolean update();

	SlotDataResponse getOnlineConsultationTimeSlots(String doctorId, String consultationType, Date date, Boolean isPatient);

	//new
//	Appointment addAppointment(AppointmentRequest request,
	//		Boolean isFormattedResponseRequired);
}
