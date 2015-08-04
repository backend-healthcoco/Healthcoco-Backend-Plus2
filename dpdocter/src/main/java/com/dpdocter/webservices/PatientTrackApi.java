package com.dpdocter.webservices;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dpdocter.beans.Patient;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.services.PatientTrackService;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;

@Component
@Path(PathProxy.PATIENT_TRACK_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PatientTrackApi {
    @Autowired
    private PatientTrackService patientTrackService;

    @Path(value = PathProxy.PatientTrackUrls.RECENTLY_VISITED)
    @GET
    public Response<Patient> recentlyVisited(@PathParam(value = "doctorId") String doctorId, @PathParam(value = "locationId") String locationId,
	    @PathParam(value = "hospitalId") String hospitalId, @PathParam(value = "page") int page, @PathParam(value = "size") int size) {
	if (DPDoctorUtils.anyStringEmpty(doctorId, locationId, hospitalId, String.valueOf(page), String.valueOf(size))) {
	    throw new BusinessException(ServiceError.InvalidInput, "Doctor Id, Location Id, Hospital Id, Page Size Or Limit Cannot be Empty");
	}
	List<Patient> recentlyVisitedPatients = patientTrackService.recentlyVisited(doctorId, locationId, hospitalId, page, size);
	Response<Patient> response = new Response<Patient>();
	response.setDataList(recentlyVisitedPatients);
	return response;
    }

    @Path(value = PathProxy.PatientTrackUrls.MOST_VISITED)
    @GET
    public Response<Patient> mostVisited(@PathParam(value = "doctorId") String doctorId, @PathParam(value = "locationId") String locationId,
	    @PathParam(value = "hospitalId") String hospitalId, @PathParam(value = "page") int page, @PathParam(value = "size") int size) {
	if (DPDoctorUtils.anyStringEmpty(doctorId, locationId, hospitalId, String.valueOf(page), String.valueOf(size))) {
	    throw new BusinessException(ServiceError.InvalidInput, "Doctor Id, Location Id, Hospital Id, Page Size Or Limit Cannot be Empty");
	}
	List<Patient> mostVisitedPatients = patientTrackService.mostVisited(doctorId, locationId, hospitalId, page, size);
	Response<Patient> response = new Response<Patient>();
	response.setDataList(mostVisitedPatients);
	return response;
    }
}
