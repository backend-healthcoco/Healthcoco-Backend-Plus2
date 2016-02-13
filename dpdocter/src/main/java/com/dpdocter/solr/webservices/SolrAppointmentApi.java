package com.dpdocter.solr.webservices;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.dpdocter.beans.ClinicImage;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.solr.beans.AppointmentSearchResponse;
import com.dpdocter.solr.beans.SolrDoctor;
import com.dpdocter.solr.document.SolrDoctorDocument;
import com.dpdocter.solr.document.SolrSpecialityDocument;
import com.dpdocter.solr.response.LabResponse;
import com.dpdocter.solr.services.SolrAppointmentService;
import com.dpdocter.webservices.PathProxy;

import common.util.web.Response;

@Component
@Path(PathProxy.SOLR_APPOINTMENT_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SolrAppointmentApi {

    @Autowired
    private SolrAppointmentService solrAppointmentService;

    @Context
    private UriInfo uriInfo;

    @Value(value = "${IMAGE_URL_ROOT_PATH}")
    private String imageUrlRootPath;

    @Path(value = PathProxy.SolrAppointmentUrls.SEARCH)
    @GET
    public Response<AppointmentSearchResponse> search(@QueryParam("city") String city, @QueryParam("location") String location,
	    @QueryParam(value = "latitude") String latitude, @QueryParam(value = "longitude") String longitude, @QueryParam("searchTerm") String searchTerm) {

	List<AppointmentSearchResponse> appointmentSearchResponses = solrAppointmentService.search(city, location, latitude, longitude, searchTerm);

	Response<AppointmentSearchResponse> response = new Response<AppointmentSearchResponse>();
	response.setDataList(appointmentSearchResponses);
	return response;
    }

    @Path(value = PathProxy.SolrAppointmentUrls.GET_DOCTORS)
    @GET
    public Response<SolrDoctor> getDoctors(@QueryParam("city") String city, @QueryParam("location") String location,
    	@QueryParam(value = "latitude") String latitude, @QueryParam(value = "longitude") String longitude,
	    @QueryParam("speciality") String speciality, @QueryParam("symptom") String symptom, @QueryParam("booking") Boolean booking,
	    @QueryParam("calling") Boolean calling, @QueryParam("minFee") String minFee, @QueryParam("maxFee") String maxFee,
	    @QueryParam("minTime") String minTime, @QueryParam("maxTime") String maxTime, @MatrixParam("days") List<String> days,
	    @QueryParam("gender") String gender, @QueryParam("minExperience") String minExperience, @QueryParam("maxExperience") String maxExperience) {

	List<SolrDoctor> doctors = solrAppointmentService.getDoctors(city, location, latitude, longitude, speciality, symptom, booking, calling, minFee, maxFee, minTime,
		maxTime, days, gender, minExperience, maxExperience);

	Response<SolrDoctor> response = new Response<SolrDoctor>();
	response.setDataList(doctors);
	return response;
    }

    @Path(value = PathProxy.SolrAppointmentUrls.GET_LABS)
    @GET
    public Response<LabResponse> getLabs(@QueryParam("city") String city, @QueryParam("location") String location,
	    @QueryParam(value = "latitude") String latitude, @QueryParam(value = "longitude") String longitude, @QueryParam("testId") String testId) {

	List<LabResponse> doctors = solrAppointmentService.getLabs(city, location, latitude, longitude, testId);

	if(doctors != null && !doctors.isEmpty()){
		for(LabResponse doctorDocument : doctors){
			if (doctorDocument.getImages() != null && !doctorDocument.getImages().isEmpty()) {
				for (ClinicImage clinicImage : doctorDocument.getImages()) {
				    if (clinicImage.getImageUrl() != null) {
					clinicImage.setImageUrl(getFinalImageURL(clinicImage.getImageUrl()));
				    }
				    if (clinicImage.getThumbnailUrl() != null) {
					clinicImage.setThumbnailUrl(getFinalImageURL(clinicImage.getThumbnailUrl()));
				    }
				}
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
	    String finalImageURL = uriInfo.getBaseUri().toString().replace(uriInfo.getBaseUri().getPath(), imageUrlRootPath);
	    return finalImageURL + imageURL;
	} else
	    return null;

    }
}
