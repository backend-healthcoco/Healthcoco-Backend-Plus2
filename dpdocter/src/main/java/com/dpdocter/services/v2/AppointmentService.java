package com.dpdocter.services.v2;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;

import com.dpdocter.beans.Event;
import com.dpdocter.beans.v2.Appointment;
import com.dpdocter.request.AppointmentRequest;
import com.dpdocter.request.EventRequest;
import com.dpdocter.response.SlotDataResponse;

import common.util.web.Response;

public interface AppointmentService {

	
	Response<Appointment> getAppointments(String locationId, List<String> doctorId, String patientId, String from,
			String to, int page, int size, String updatedTime, String status, String sortBy, String fromTime,
			String toTime, Boolean isRegisteredPatientRequired, Boolean isWeb, Boolean discarded,String branch);

	Response<Object> getPatientAppointments(String locationId, String doctorId, String patientId, String from,
			String to, int page, int size, String updatedTime, String type);


	SlotDataResponse getTimeSlots(String doctorId, String locationId, Date date, Boolean isPatient);

	Appointment addAppointment(AppointmentRequest request, Boolean isFormattedResponseRequired);

	Appointment updateAppointment(AppointmentRequest request, Boolean updateVisit, Boolean isStatusChange);

	Event addEvent(EventRequest request);

	Event updateEvent(EventRequest request);

	Appointment getAppointmentById(ObjectId appointmentId);

	Response<Appointment> getAppointmentsForWebNew(String locationId, List<String> doctorId, String patientId, String from,
			String to, int page, int size, String updatedTime, String status, String sortBy, String fromTime,
			String toTime, Boolean isRegisteredPatientRequired, Boolean isWeb, Boolean discarded, String branch);

	
}
