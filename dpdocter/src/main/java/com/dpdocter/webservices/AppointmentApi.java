package com.dpdocter.webservices;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
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
import com.dpdocter.beans.LandmarkLocality;
import com.dpdocter.enums.Resource;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.request.AppoinmentRequest;
import com.dpdocter.services.AppointmentService;
import com.dpdocter.services.TransactionalManagementService;
import com.dpdocter.solr.beans.Country;
import com.dpdocter.solr.document.SolrCityDocument;
import com.dpdocter.solr.document.SolrCountryDocument;
import com.dpdocter.solr.document.SolrLocalityLandmarkDocument;
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
    
    @POST
    public Response<Appointment> BookAppoinment(AppoinmentRequest request) {

	if (request == null) {
	    throw new BusinessException(ServiceError.InvalidInput, "request cannot be null");
	}
	Appointment appointment = appointmentService.appointment(request);
	Response<Appointment> response = new Response<Appointment>();
	response.setData(appointment);
	return response;

    }
    
    @Path(value = PathProxy.AppointmentUrls.GET_CLINIC_APPOINTMENTS)
    @GET
    public Response<Appointment> getClinicAppointments(@QueryParam(value = "locationId") String locationId, @QueryParam(value = "doctorId") String doctorId,
    		@QueryParam(value = "day") int day, @QueryParam(value = "month") int month, @QueryParam(value = "week") int week,
    		@QueryParam("page") int page, @QueryParam("size") int size, @DefaultValue("0") @QueryParam("updatedTime") String updatedTime) {

    List<Appointment> appointment = appointmentService.getClinicAppointments(locationId, doctorId, day, month, week, page, size, updatedTime);
	Response<Appointment> response = new Response<Appointment>();
	response.setDataList(appointment);
	return response;
    }

    @Path(value = PathProxy.AppointmentUrls.GET_DOCTOR_APPOINTMENTS)
    @GET
    public Response<Appointment> getDoctorAppointments(@QueryParam(value = "locationId") String locationId, @QueryParam(value = "doctorId") String doctorId,
    		@QueryParam(value = "day") int day, @QueryParam(value = "month") int month, @QueryParam(value = "week") int week,
    		@QueryParam("page") int page, @QueryParam("size") int size, @DefaultValue("0") @QueryParam("updatedTime") String updatedTime) {

    List<Appointment> appointment = appointmentService.getDoctorAppointments(locationId, doctorId, day, month, week, page, size, updatedTime);
	Response<Appointment> response = new Response<Appointment>();
	response.setDataList(appointment);
	return response;
    }

    @Path(value = PathProxy.AppointmentUrls.GET_PATIENT_APPOINTMENTS)
    @GET
    public Response<Appointment> getPatientAppointments(@QueryParam(value = "locationId") String locationId, @QueryParam(value = "doctorId") String doctorId,
    		@QueryParam(value = "patientId") String patientId, @QueryParam(value = "day") int day, @QueryParam(value = "month") int month, @QueryParam(value = "week") int week,
    		@QueryParam("page") int page, @QueryParam("size") int size, @DefaultValue("0") @QueryParam("updatedTime") String updatedTime) {

    List<Appointment> appointment = appointmentService.getPatientAppointments(locationId, doctorId, patientId, day, month, week, page, size, updatedTime);
	Response<Appointment> response = new Response<Appointment>();
	response.setDataList(appointment);
	return response;
    }

}