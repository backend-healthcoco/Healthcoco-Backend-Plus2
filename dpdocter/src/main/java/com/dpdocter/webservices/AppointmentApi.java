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
import com.dpdocter.beans.Slot;
import com.dpdocter.enums.Resource;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.request.AppointmentRequest;
import com.dpdocter.request.EventRequest;
import com.dpdocter.request.PatientQueueAddEditRequest;
import com.dpdocter.services.AppointmentService;
import com.dpdocter.services.TransactionalManagementService;
import com.dpdocter.solr.beans.Country;
import com.dpdocter.solr.beans.State;
import com.dpdocter.solr.document.SolrCityDocument;
import com.dpdocter.solr.document.SolrCountryDocument;
import com.dpdocter.solr.document.SolrLocalityLandmarkDocument;
import com.dpdocter.solr.document.SolrStateDocument;
import com.dpdocter.solr.services.SolrCityService;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;

@Component
@Path(PathProxy.APPOINTMENT_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AppointmentApi {

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private SolrCityService solrCityService;

    @Autowired
    private TransactionalManagementService transnationalService;

    @Path(value = PathProxy.AppointmentUrls.ADD_COUNTRY)
    @POST
    public Response<Country> addCountry(Country request) {
	if (request == null) {
	    throw new BusinessException(ServiceError.InvalidInput, "Request sent is NULL");
	} else if (request.getCountry() == null) {
	    throw new BusinessException(ServiceError.InvalidInput, "Country cannot be NULL");
	}
	Country country = appointmentService.addCountry(request);

	transnationalService.addResource(country.getId(), Resource.COUNTRY, false);
	SolrCountryDocument solrCountry = new SolrCountryDocument();
	BeanUtil.map(country, solrCountry);
	solrCountry.setGeoLocation(new GeoLocation(country.getLatitude(), country.getLongitude()));
	solrCityService.addCountry(solrCountry);

	Response<Country> response = new Response<Country>();
	response.setData(country);
	return response;
    }

    @Path(value = PathProxy.AppointmentUrls.ADD_STATE)
    @POST
    public Response<State> addState(State request) {
	if (request == null) {
	    throw new BusinessException(ServiceError.InvalidInput, "Request sent is NULL");
	} else if (request.getState() == null) {
	    throw new BusinessException(ServiceError.InvalidInput, "Country cannot be NULL");
	}
	State state = appointmentService.addState(request);

	transnationalService.addResource(state.getId(), Resource.STATE, false);
	SolrStateDocument solrState = new SolrStateDocument();
	BeanUtil.map(state, solrState);
	solrState.setGeoLocation(new GeoLocation(state.getLatitude(), state.getLongitude()));
	solrCityService.addState(solrState);

	Response<State> response = new Response<State>();
	response.setData(state);
	return response;
    }

    @Path(value = PathProxy.AppointmentUrls.ADD_CITY)
    @POST
    public Response<City> addCity(City request) {
	if (request == null) {
	    throw new BusinessException(ServiceError.InvalidInput, "Request sent is NULL");
	} else if (request.getCity() == null) {
	    throw new BusinessException(ServiceError.InvalidInput, "City cannot be NULL");
	}
	City city = appointmentService.addCity(request);

	transnationalService.addResource(city.getId(), Resource.CITY, false);
	SolrCityDocument solrCities = new SolrCityDocument();
	BeanUtil.map(city, solrCities);
	solrCities.setGeoLocation(new GeoLocation(city.getLatitude(), city.getLongitude()));
	solrCityService.addCities(solrCities);

	Response<City> response = new Response<City>();
	response.setData(city);
	return response;
    }

    @Path(value = PathProxy.AppointmentUrls.ACTIVATE_DEACTIVATE_CITY)
    @GET
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

    @Path(value = PathProxy.AppointmentUrls.GET_CITY)
    @GET
    public Response<City> getCities() {
	List<City> cities = appointmentService.getCities();
	Response<City> response = new Response<City>();
	response.setDataList(cities);
	return response;

    }

    @Path(value = PathProxy.AppointmentUrls.GET_CITY_ID)
    @GET
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
    public Response<Appointment> getDoctorAppointments(@QueryParam(value = "locationId") String locationId,
	    @MatrixParam(value = "doctorId") List<String> doctorId, @QueryParam(value = "patientId") String patientId, @QueryParam(value = "from") String from,
	    @QueryParam(value = "to") String to, @QueryParam(value = "page") int page, @QueryParam(value = "size") int size) {

	List<Appointment> appointment = appointmentService.getAppointments(locationId, doctorId, patientId, from, to, page, size);
	Response<Appointment> response = new Response<Appointment>();
	response.setDataList(appointment);
	return response;
    }

    @Path(value = PathProxy.AppointmentUrls.GET_PATIENT_APPOINTMENTS)
    @GET
    public Response<Appointment> getPatientAppointments(@QueryParam(value = "locationId") String locationId, @QueryParam(value = "doctorId") String doctorId,
	    @QueryParam(value = "patientId") String patientId, @QueryParam(value = "from") String from, @QueryParam(value = "to") String to,
	    @QueryParam(value = "page") int page, @QueryParam(value = "size") int size) {

	List<Appointment> appointment = appointmentService.getPatientAppointments(locationId, doctorId, patientId, from, to, page, size);
	Response<Appointment> response = new Response<Appointment>();
	response.setDataList(appointment);
	return response;
    }

    @Path(value = PathProxy.AppointmentUrls.GET_TIME_SLOTS)
    @GET
    public Response<Slot> getTimeSlots(@PathParam("doctorId") String doctorId, @PathParam("locationId") String locationId, @PathParam("date") String date) {
	Response<Slot> response = new Response<Slot>();
	if (DPDoctorUtils.anyStringEmpty(doctorId)) {
	    throw new BusinessException(ServiceError.InvalidInput, "Doctor Id Cannot Be Empty");
	}
	Date dateObj = new Date(Long.parseLong(date));
	List<Slot> timeSlots = appointmentService.getTimeSlots(doctorId, locationId, dateObj);
	response.setDataList(timeSlots);
	return response;
    }

    @Path(value = PathProxy.AppointmentUrls.ADD_EDIT_EVENT)
    @POST
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

    @Path(value = PathProxy.AppointmentUrls.SEND_REMINDER)
    @GET
    public Response<Boolean> sendReminder(@PathParam(value = "appointmentId") String appointmentId) {
	if (DPDoctorUtils.anyStringEmpty(appointmentId)) {
	    throw new BusinessException(ServiceError.InvalidInput, "Appointment Id cannot be null");
	}
	Boolean sendReminder = appointmentService.sendReminder(appointmentId);

	Response<Boolean> response = new Response<Boolean>();
	response.setData(sendReminder);
	return response;
    }

    @Path(value = PathProxy.AppointmentUrls.IMPORT)
    @GET
    public Response<Boolean> importMaster() {
	appointmentService.importMaster();

	Response<Boolean> response = new Response<Boolean>();
	response.setData(true);
	return response;
    }

    @Path(value = PathProxy.AppointmentUrls.ADD_PATIENT_IN_QUEUE)
    @POST
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