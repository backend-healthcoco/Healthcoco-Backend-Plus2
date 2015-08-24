package com.dpdocter.webservices;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dpdocter.beans.City;
import com.dpdocter.beans.Clinic;
import com.dpdocter.beans.Landmark;
import com.dpdocter.beans.Locality;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.services.AppointmentService;
import com.dpdocter.solr.document.SolrCityDocument;
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

    @Path(value = PathProxy.CityUrls.ADD_CITY)
    @POST
    public Response<City> addCity(City request) {
	if (request == null) {
	    throw new BusinessException(ServiceError.InvalidInput, "Request sent is NULL");
	} else if (request.getCity() == null) {
	    throw new BusinessException(ServiceError.InvalidInput, "City cannot be NULL");
	}
	City city = appointmentService.addCity(request);

	SolrCityDocument solrCities = new SolrCityDocument();
	BeanUtil.map(city, solrCities);
	if (request.getId() == null) {
	    solrCityService.addCities(solrCities);
	}

	else {
	    solrCityService.editCities(solrCities);
	}

	Response<City> response = new Response<City>();
	response.setData(city);
	return response;
    }

    @Path(value = PathProxy.CityUrls.ACTIVATE_CITY)
    @GET
    public Response<Boolean> activateCity(@PathParam(value = "cityId") String cityId) {
	if (cityId == null) {
	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
	}
	Boolean isActivated = false;
	isActivated = appointmentService.activateDeactivateCity(cityId, true);
	solrCityService.activateDeactivateCity(cityId, true);
	Response<Boolean> response = new Response<Boolean>();
	response.setData(isActivated);
	return response;

    }

    @Path(value = PathProxy.CityUrls.DEACTIVATE_CITY)
    @GET
    public Response<Boolean> deactivateCity(@PathParam(value = "cityId") String cityId) {
	if (cityId == null) {
	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
	}
	Boolean isDeactivated = false;
	isDeactivated = appointmentService.activateDeactivateCity(cityId, false);
	solrCityService.activateDeactivateCity(cityId, false);
	Response<Boolean> response = new Response<Boolean>();
	response.setData(isDeactivated);
	return response;

    }

    @Path(value = PathProxy.CityUrls.GET_CITY)
    @GET
    public Response<City> getCities() {
	List<City> cities = appointmentService.getCities();
	Response<City> response = new Response<City>();
	response.setDataList(cities);
	return response;

    }

    @Path(value = PathProxy.CityUrls.GET_CITY_ID)
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

    @Path(value = PathProxy.CityUrls.ADD_LOCALITY)
    @POST
    public Response<Locality> addLocality(Locality request) {
	if (request == null) {
	    throw new BusinessException(ServiceError.InvalidInput, "Request sent is NULL");
	} else if (request.getCityId() == null || request.getLocality() == null) {
	    throw new BusinessException(ServiceError.InvalidInput, "CityId, Locality cannot be NULL");
	}
	Locality locality = appointmentService.addLocality(request);

	SolrLocalityLandmarkDocument solrLocalityLandmark = new SolrLocalityLandmarkDocument();
	BeanUtil.map(locality, solrLocalityLandmark);
	if (request.getId() == null) {
	    solrCityService.addLocalityLandmark(solrLocalityLandmark);
	}

	else {
	    solrCityService.editLocalityLandmark(solrLocalityLandmark);
	}

	Response<Locality> response = new Response<Locality>();
	response.setData(locality);
	return response;
    }

    @Path(value = PathProxy.CityUrls.ADD_LANDMARK)
    @POST
    public Response<Landmark> addLandmark(Landmark request) {
	if (request == null) {
	    throw new BusinessException(ServiceError.InvalidInput, "Request sent is NULL");
	} else if (request.getCityId() == null || request.getLandmark() == null) {
	    throw new BusinessException(ServiceError.InvalidInput, "CityId, Landmark cannot be NULL");
	}
	Landmark landmark = appointmentService.addLandmark(request);

	SolrLocalityLandmarkDocument solrLocalityLandmark = new SolrLocalityLandmarkDocument();
	BeanUtil.map(landmark, solrLocalityLandmark);
	if (request.getId() == null) {
	    solrCityService.addLocalityLandmark(solrLocalityLandmark);
	}

	else {
	    solrCityService.editLocalityLandmark(solrLocalityLandmark);
	}

	Response<Landmark> response = new Response<Landmark>();
	response.setData(landmark);
	return response;
    }

    @Path(value = PathProxy.CityUrls.GET_LANDMARK_LOCALITY)
    @GET
    public Response<Object> getLandmarkLocality(@PathParam(value = "cityId") String cityId, @QueryParam(value = "type") String type) {

	List<Object> landmarksLocalities = appointmentService.getLandmarkLocality(cityId, type);
	Response<Object> response = new Response<Object>();
	response.setDataList(landmarksLocalities);
	return response;

    }

    @Path(value = PathProxy.CityUrls.GET_CLINIC)
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
}