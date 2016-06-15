package com.dpdocter.webservices;

import java.util.Date;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.geo.GeoLocation;
import org.springframework.stereotype.Component;

import com.dpdocter.beans.Appointment;
import com.dpdocter.beans.City;
import com.dpdocter.beans.Clinic;
import com.dpdocter.beans.Lab;
import com.dpdocter.beans.LandmarkLocality;
import com.dpdocter.beans.PatientQueue;
import com.dpdocter.enums.Resource;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.request.AppointmentRequest;
import com.dpdocter.request.EventRequest;
import com.dpdocter.request.PatientQueueAddEditRequest;
import com.dpdocter.response.SlotDataResponse;
import com.dpdocter.services.AppointmentService;
import com.dpdocter.services.TransactionalManagementService;
import com.dpdocter.solr.document.SolrLocalityLandmarkDocument;
import com.dpdocter.solr.services.SolrCityService;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Component
@Path(PathProxy.APPOINTMENT_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.APPOINTMENT_BASE_URL, description = "Endpoint for appointment")
public class AppointmentApi {

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private SolrCityService solrCityService;

    @Autowired
    private TransactionalManagementService transnationalService;

    @Path(value = PathProxy.AppointmentUrls.ACTIVATE_DEACTIVATE_CITY)
    @GET
    @ApiOperation(value = PathProxy.AppointmentUrls.ACTIVATE_DEACTIVATE_CITY, notes = PathProxy.AppointmentUrls.ACTIVATE_DEACTIVATE_CITY)
    public Response<Boolean> activateCity(@PathParam(value = "cityId") String cityId, @DefaultValue("true") @QueryParam("activate") Boolean activate) {
	if (cityId == null) {
	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
	}
	Boolean isActivated = false;
	isActivated = appointmentService.activateDeactivateCity(cityId, activate);
	transnationalService.addResource(cityId, Resource.CITY, false);
	solrCityService.activateDeactivateCity(cityId, activate);
	Response<Boolean> response = new Response<Boolean>();
	response.setData(isActivated);
	return response;

    }

    @Path(value = PathProxy.AppointmentUrls.GET_COUNTRIES)
    @GET
    @ApiOperation(value = PathProxy.AppointmentUrls.GET_COUNTRIES, notes = PathProxy.AppointmentUrls.GET_COUNTRIES)
    public Response<City> getCountries() {
	List<City> countries = appointmentService.getCountries();
	Response<City> response = new Response<City>();
	response.setDataList(countries);
	return response;
    }

    @Path(value = PathProxy.AppointmentUrls.GET_STATES)
    @GET
    @ApiOperation(value = PathProxy.AppointmentUrls.GET_STATES, notes = PathProxy.AppointmentUrls.GET_STATES)
    public Response<City> getStates(@QueryParam(value = "country") String country) {
	List<City> states = appointmentService.getStates(country);
	Response<City> response = new Response<City>();
	response.setDataList(states);
	return response;
    }

    @Path(value = PathProxy.AppointmentUrls.GET_CITY)
    @GET
    @ApiOperation(value = PathProxy.AppointmentUrls.GET_CITY, notes = PathProxy.AppointmentUrls.GET_CITY)
    public Response<City> getCities(@QueryParam(value = "state") String state) {
	List<City> cities = appointmentService.getCities(state);
	Response<City> response = new Response<City>();
	response.setDataList(cities);
	return response;
    }

    @Path(value = PathProxy.AppointmentUrls.GET_CITY_ID)
    @GET
    @ApiOperation(value = PathProxy.AppointmentUrls.GET_CITY_ID, notes = PathProxy.AppointmentUrls.GET_CITY_ID)
    public Response<City> getCityById(@PathParam(value = "cityId") String cityId) {
	if (cityId == null) {
	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
	}

	City cities = appointmentService.getCity(cityId);
	Response<City> response = new Response<City>();
	response.setData(cities);
	return response;

    }

    @Path(value = PathProxy.AppointmentUrls.ADD_LANDMARK_LOCALITY)
    @POST
    @ApiOperation(value = PathProxy.AppointmentUrls.ADD_LANDMARK_LOCALITY, notes = PathProxy.AppointmentUrls.ADD_LANDMARK_LOCALITY)
    public Response<LandmarkLocality> addLandmaklLocality(LandmarkLocality request) {
	if (request == null) {
	    throw new BusinessException(ServiceError.InvalidInput, "Request sent is NULL");
	}
	LandmarkLocality locality = appointmentService.addLandmaklLocality(request);
	transnationalService.addResource(request.getId(), Resource.LANDMARKLOCALITY, false);
	SolrLocalityLandmarkDocument solrLocalityLandmark = new SolrLocalityLandmarkDocument();
	BeanUtil.map(locality, solrLocalityLandmark);
	solrLocalityLandmark.setGeoLocation(new GeoLocation(locality.getLatitude(), locality.getLongitude()));
	solrCityService.addLocalityLandmark(solrLocalityLandmark);

	Response<LandmarkLocality> response = new Response<LandmarkLocality>();
	response.setData(locality);
	return response;
    }

    @Path(value = PathProxy.AppointmentUrls.GET_CLINIC)
    @GET
    @ApiOperation(value = PathProxy.AppointmentUrls.GET_CLINIC, notes = PathProxy.AppointmentUrls.GET_CLINIC)
    public Response<Clinic> getClinic(@PathParam(value = "locationId") String locationId) {

	if (DPDoctorUtils.anyStringEmpty(locationId)) {
	    throw new BusinessException(ServiceError.InvalidInput, "Location Id cannot be empty");
	}
	Clinic clinic = appointmentService.getClinic(locationId);
	Response<Clinic> response = new Response<Clinic>();
	response.setData(clinic);
	return response;

    }

    @Path(value = PathProxy.AppointmentUrls.GET_LAB)
    @GET
    @ApiOperation(value = PathProxy.AppointmentUrls.GET_LAB, notes = PathProxy.AppointmentUrls.GET_LAB)
    public Response<Lab> getLabs(@PathParam("locationId") String locationId) {
	if (DPDoctorUtils.anyStringEmpty(locationId)) {
	    throw new BusinessException(ServiceError.InvalidInput, "Location Id cannot be empty");
	}
	Lab lab = appointmentService.getLab(locationId);
	Response<Lab> response = new Response<Lab>();
	response.setData(lab);
	return response;
    }

    @POST
    @ApiOperation(value = "ADD_APPOINTMENT", notes = "ADD_APPOINTMENT")
    public Response<Appointment> BookAppoinment(AppointmentRequest request) {
	if (request == null) {
	    throw new BusinessException(ServiceError.InvalidInput, "request cannot be null");
	}
	Appointment appointment = null;
	if (request.getAppointmentId() == null) {
	    appointment = appointmentService.addAppointment(request);
	} else {
	    appointment = appointmentService.updateAppointment(request);
	}

	Response<Appointment> response = new Response<Appointment>();
	response.setData(appointment);
	return response;

    }

    @GET
    @ApiOperation(value = "GET_APPOINTMENTS", notes = "GET_APPOINTMENTS")
    public Response<Appointment> getDoctorAppointments(@QueryParam(value = "locationId") String locationId,
	    @MatrixParam(value = "doctorId") List<String> doctorId, @QueryParam(value = "patientId") String patientId, @QueryParam(value = "from") String from,
	    @QueryParam(value = "to") String to, @QueryParam(value = "page") int page, @QueryParam(value = "size") int size, @DefaultValue(value="0") @QueryParam(value = "updatedTime") String updatedTime) {

	List<Appointment> appointment = appointmentService.getAppointments(locationId, doctorId, patientId, from, to, page, size, updatedTime);
	Response<Appointment> response = new Response<Appointment>();
	response.setDataList(appointment);
	return response;
    }

    @Path(value = PathProxy.AppointmentUrls.GET_PATIENT_APPOINTMENTS)
    @GET
    @ApiOperation(value = PathProxy.AppointmentUrls.GET_PATIENT_APPOINTMENTS, notes = PathProxy.AppointmentUrls.GET_PATIENT_APPOINTMENTS)
    public Response<Appointment> getPatientAppointments(@QueryParam(value = "locationId") String locationId, @QueryParam(value = "doctorId") String doctorId,
	    @QueryParam(value = "patientId") String patientId, @QueryParam(value = "from") String from, @QueryParam(value = "to") String to,
	    @QueryParam(value = "page") int page, @QueryParam(value = "size") int size, @DefaultValue(value="0") @QueryParam(value = "updatedTime") String updatedTime) {

	List<Appointment> appointment = appointmentService.getPatientAppointments(locationId, doctorId, patientId, from, to, page, size, updatedTime);
	Response<Appointment> response = new Response<Appointment>();
	response.setDataList(appointment);
	return response;
    }

    @Path(value = PathProxy.AppointmentUrls.GET_TIME_SLOTS)
    @GET
    @ApiOperation(value = PathProxy.AppointmentUrls.GET_TIME_SLOTS, notes = PathProxy.AppointmentUrls.GET_TIME_SLOTS)
    public Response<SlotDataResponse> getTimeSlots(@PathParam("doctorId") String doctorId, @PathParam("locationId") String locationId, @PathParam("date") String date) {
	
	if (DPDoctorUtils.anyStringEmpty(doctorId)) {
	    throw new BusinessException(ServiceError.InvalidInput, "Doctor Id Cannot Be Empty");
	}
	Date dateObj = new Date(Long.parseLong(date));
	SlotDataResponse slotDataResponse = appointmentService.getTimeSlots(doctorId, locationId, dateObj);
	Response<SlotDataResponse> response = new Response<SlotDataResponse>();
	response.setData(slotDataResponse);
	return response;
    }

    @Path(value = PathProxy.AppointmentUrls.ADD_EDIT_EVENT)
    @POST
    @ApiOperation(value = PathProxy.AppointmentUrls.ADD_EDIT_EVENT, notes = PathProxy.AppointmentUrls.ADD_EDIT_EVENT)
    public Response<Appointment> addEditEvent(EventRequest request) {
	if (request == null) {
	    throw new BusinessException(ServiceError.InvalidInput, "request cannot be null");
	}
	Appointment event = null;
	if (request.getId() == null)
	    event = appointmentService.addEvent(request);
	else
	    event = appointmentService.updateEvent(request);

	Response<Appointment> response = new Response<Appointment>();
	response.setData(event);
	return response;
    }

    @Path(value = PathProxy.AppointmentUrls.SEND_REMINDER_TO_PATIENT)
    @GET
    @ApiOperation(value = PathProxy.AppointmentUrls.SEND_REMINDER_TO_PATIENT, notes = PathProxy.AppointmentUrls.SEND_REMINDER_TO_PATIENT)
    public Response<Boolean> sendReminderToPatient(@PathParam(value = "appointmentId") String appointmentId) {
	if (DPDoctorUtils.anyStringEmpty(appointmentId)) {
	    throw new BusinessException(ServiceError.InvalidInput, "Appointment Id cannot be null");
	}
	Boolean sendReminder = appointmentService.sendReminderToPatient(appointmentId);

	Response<Boolean> response = new Response<Boolean>();
	response.setData(sendReminder);
	return response;
    }

    @Path(value = PathProxy.AppointmentUrls.ADD_PATIENT_IN_QUEUE)
    @POST
    @ApiOperation(value = PathProxy.AppointmentUrls.ADD_PATIENT_IN_QUEUE, notes = PathProxy.AppointmentUrls.ADD_PATIENT_IN_QUEUE)
    public Response<PatientQueue> addPatientInQueue(PatientQueueAddEditRequest request) {
	if (request == null) {
	    throw new BusinessException(ServiceError.InvalidInput, "request cannot be null");
	}
	List<PatientQueue> patientQueues = appointmentService.addPatientInQueue(request);

	Response<PatientQueue> response = new Response<PatientQueue>();
	response.setDataList(patientQueues);
	return response;
    }

    @Path(value = PathProxy.AppointmentUrls.REARRANGE_PATIENT_IN_QUEUE)
    @GET
    @ApiOperation(value = PathProxy.AppointmentUrls.REARRANGE_PATIENT_IN_QUEUE, notes = PathProxy.AppointmentUrls.REARRANGE_PATIENT_IN_QUEUE)
    public Response<PatientQueue> rearrangePatientInQueue(@PathParam(value = "doctorId") String doctorId, @PathParam(value = "locationId") String locationId,
	    @PathParam(value = "hospitalId") String hospitalId, @PathParam(value = "patientId") String patientId,
	    @PathParam(value = "appointmentId") String appointmentId, @PathParam(value = "sequenceNo") int sequenceNo) {
	if (DPDoctorUtils.anyStringEmpty(doctorId, locationId, hospitalId, patientId, appointmentId)) {
	    throw new BusinessException(ServiceError.InvalidInput, "DoctorId, LocationId, HospitalId, PatientId cannot be null");
	}
	List<PatientQueue> patientQueues = appointmentService.rearrangePatientInQueue(doctorId, locationId, hospitalId, patientId, appointmentId, sequenceNo);

	Response<PatientQueue> response = new Response<PatientQueue>();
	response.setDataList(patientQueues);
	return response;
    }

    @Path(value = PathProxy.AppointmentUrls.GET_PATIENT_QUEUE)
    @GET
    @ApiOperation(value = PathProxy.AppointmentUrls.GET_PATIENT_QUEUE, notes = PathProxy.AppointmentUrls.GET_PATIENT_QUEUE)
    public Response<PatientQueue> getPatientQueue(@PathParam(value = "doctorId") String doctorId, @PathParam(value = "locationId") String locationId,
	    @PathParam(value = "hospitalId") String hospitalId) {
	if (DPDoctorUtils.anyStringEmpty(doctorId, locationId, hospitalId)) {
	    throw new BusinessException(ServiceError.InvalidInput, "DoctorId, LocationId, HospitalId cannot be null");
	}
	List<PatientQueue> patientQueues = appointmentService.getPatientQueue(doctorId, locationId, hospitalId);

	Response<PatientQueue> response = new Response<PatientQueue>();
	response.setDataList(patientQueues);
	return response;
    }

}