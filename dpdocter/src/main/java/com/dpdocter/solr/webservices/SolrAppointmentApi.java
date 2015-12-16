package com.dpdocter.solr.webservices;

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
import org.springframework.stereotype.Component;

import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.solr.beans.AppointmentSearchResponse;
import com.dpdocter.solr.document.SolrDoctorDocument;
import com.dpdocter.solr.document.SolrSpecialityDocument;
import com.dpdocter.solr.services.SolrAppointmentService;
import com.dpdocter.webservices.PathProxy;
import common.util.web.DPDoctorUtils;
import common.util.web.Response;

@Component
@Path(PathProxy.SOLR_APPOINTMENT_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SolrAppointmentApi {
    
	@Autowired
    private SolrAppointmentService solrAppointmentService;


    @Path(value = PathProxy.SolrAppointmentUrls.GET_DOCTORS)
    @GET
    public Response<SolrDoctorDocument> getDoctors(@QueryParam("city") String city, @QueryParam("location") String location,
	    @QueryParam("speciality") String speciality, @QueryParam("symptom") String symptom, @QueryParam("booking") Boolean booking,
	    @QueryParam("calling") Boolean calling, @QueryParam("minFee") String minFee, @QueryParam("maxFee") String maxFee, 
	    @QueryParam("minTime") String minTime, @QueryParam("maxTime") String maxTime,
	    @MatrixParam("days") List<String> days, @QueryParam("gender") String gender, @QueryParam("minExperience") String minExperience,
	    @QueryParam("maxExperience") String maxExperience) {
	if (DPDoctorUtils.anyStringEmpty(city)) {
	    throw new BusinessException(ServiceError.InvalidInput, "City Cannot Be Empty");
	}

	List<SolrDoctorDocument> doctors = solrAppointmentService.getDoctors(city, location, speciality, symptom, booking, calling, minFee, maxFee,
			minTime, maxTime, days, gender, minExperience, maxExperience);

	Response<SolrDoctorDocument> response = new Response<SolrDoctorDocument>();
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
}
