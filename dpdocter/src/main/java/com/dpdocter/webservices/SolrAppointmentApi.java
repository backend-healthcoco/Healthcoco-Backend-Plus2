package com.dpdocter.webservices;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.solr.beans.AppointmentSearchResponse;
import com.dpdocter.solr.document.SolrDoctorDocument;
import com.dpdocter.solr.document.SolrSpecialityDocument;
import com.dpdocter.solr.response.LabResponse;
import com.dpdocter.solr.services.SolrAppointmentService;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import common.util.web.Response;

@Component
@Path(PathProxy.SOLR_APPOINTMENT_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.SOLR_APPOINTMENT_BASE_URL, description = "Endpoint for solr appointment")
public class SolrAppointmentApi {

    @Autowired
    private SolrAppointmentService solrAppointmentService;

    @Value(value = "${image.path}")
    private String imagePath;

    @Path(value = PathProxy.SolrAppointmentUrls.SEARCH)
    @GET
    @ApiOperation(value = PathProxy.SolrAppointmentUrls.SEARCH, notes = PathProxy.SolrAppointmentUrls.SEARCH)
    public Response<AppointmentSearchResponse> search(@QueryParam("city") String city, @QueryParam("location") String location,
	    @QueryParam(value = "latitude") String latitude, @QueryParam(value = "longitude") String longitude, @QueryParam("searchTerm") String searchTerm) {

	List<AppointmentSearchResponse> appointmentSearchResponses = solrAppointmentService.search(city, location, latitude, longitude, searchTerm);

	Response<AppointmentSearchResponse> response = new Response<AppointmentSearchResponse>();
	response.setDataList(appointmentSearchResponses);
	return response;
    }

    @Path(value = PathProxy.SolrAppointmentUrls.GET_DOCTORS)
    @GET
    @ApiOperation(value = PathProxy.SolrAppointmentUrls.GET_DOCTORS, notes = PathProxy.SolrAppointmentUrls.GET_DOCTORS)
    public Response<SolrDoctorDocument> getDoctors(@QueryParam("page") int page, @QueryParam("size") int size, @QueryParam("city") String city,
	    @QueryParam("location") String location, @QueryParam(value = "latitude") String latitude, @QueryParam(value = "longitude") String longitude,
	    @QueryParam("speciality") String speciality, @QueryParam("symptom") String symptom, @QueryParam("booking") Boolean booking,
	    @QueryParam("calling") Boolean calling, @QueryParam("minFee") String minFee, @QueryParam("maxFee") String maxFee,
	    @QueryParam("minTime") String minTime, @QueryParam("maxTime") String maxTime, @MatrixParam("days") List<String> days,
	    @QueryParam("gender") String gender, @QueryParam("minExperience") String minExperience, @QueryParam("maxExperience") String maxExperience) {

	List<SolrDoctorDocument> doctors = solrAppointmentService.getDoctors(page, size, city, location, latitude, longitude, speciality, symptom, booking,
		calling, minFee, maxFee, minTime, maxTime, days, gender, minExperience, maxExperience);

	Response<SolrDoctorDocument> response = new Response<SolrDoctorDocument>();
	response.setDataList(doctors);
	return response;
    }

    @Path(value = PathProxy.SolrAppointmentUrls.GET_LABS)
    @GET
    @ApiOperation(value = PathProxy.SolrAppointmentUrls.GET_LABS, notes = PathProxy.SolrAppointmentUrls.GET_LABS)
    public Response<LabResponse> getLabs(@QueryParam("page") int page, @QueryParam("size") int size, @QueryParam("city") String city, @QueryParam("location") String location,
	    @QueryParam(value = "latitude") String latitude, @QueryParam(value = "longitude") String longitude, @QueryParam("test") String test,
	    @QueryParam("booking") Boolean booking, @QueryParam("calling") Boolean calling) {

	List<LabResponse> doctors = solrAppointmentService.getLabs(page, size, city, location, latitude, longitude, test, booking, calling);

	if (doctors != null && !doctors.isEmpty()) {
	    for (LabResponse doctorDocument : doctors) {
		if (doctorDocument.getImages() != null && !doctorDocument.getImages().isEmpty()) {
			List<String> images = new ArrayList<String>();
			for (String clinicImage : doctorDocument.getImages()) {
			    images.add(clinicImage);
			}
			doctorDocument.setImages(images);
		}
		if (doctorDocument.getLogoUrl() != null)
		    doctorDocument.setLogoUrl(getFinalImageURL(doctorDocument.getLogoUrl()));
	    }
	}
	Response<LabResponse> response = new Response<LabResponse>();
	response.setDataList(doctors);
	return response;
    }

    @Path(value = PathProxy.SolrAppointmentUrls.ADD_SPECIALITY)
    @POST
    @ApiOperation(value = PathProxy.SolrAppointmentUrls.ADD_SPECIALITY, notes = PathProxy.SolrAppointmentUrls.ADD_SPECIALITY)
    public Response<Boolean> addSpeciality(List<SolrSpecialityDocument> request) {
	if (request == null || request.isEmpty()) {
	    throw new BusinessException(ServiceError.InvalidInput, "Specialities Cannot Be Empty");
	}

	boolean addSpecializationResponse = solrAppointmentService.addSpeciality(request);

	Response<Boolean> response = new Response<Boolean>();
	response.setData(addSpecializationResponse);
	return response;
    }

    private String getFinalImageURL(String imageURL) {
	if (imageURL != null) {
	    return imagePath + imageURL;
	} else
	    return null;

    }
}
