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
import com.dpdocter.services.PatientTrackService;
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
    public Response<Patient> recentlyVisited(@PathParam(value = "page") int page, @PathParam(value = "size") int size) {
	List<Patient> recentlyVisitedPatients = patientTrackService.recentlyVisited(page, size);
	Response<Patient> response = new Response<Patient>();
	response.setDataList(recentlyVisitedPatients);
	return response;
    }

    @Path(value = PathProxy.PatientTrackUrls.MOST_VISITED)
    @GET
    public Response<Patient> mostVisited(@PathParam(value = "page") int page, @PathParam(value = "size") int size) {
	List<Patient> mostVisitedPatients = patientTrackService.mostVisited(page, size);
	Response<Patient> response = new Response<Patient>();
	response.setDataList(mostVisitedPatients);
	return response;
    }
}
