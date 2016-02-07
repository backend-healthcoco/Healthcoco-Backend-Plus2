package com.dpdocter.webservices;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dpdocter.beans.EmailTrack;
import com.dpdocter.services.EmailTackService;
import common.util.web.Response;

@Component
@Path(PathProxy.EMAIL_TRACK_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class EmailTrackAPI {

    private static Logger logger = Logger.getLogger(EmailTrackAPI.class.getName());

    @Autowired
    private EmailTackService emailTackService;

    @GET
    public Response<EmailTrack> getEmailDetails(@QueryParam(value = "patientId") String patientId, @QueryParam(value = "doctorId") String doctorId,
	    @QueryParam(value = "locationId") String locationId, @QueryParam(value = "hospitalId") String hospitalId, @QueryParam("page") int page,
	    @QueryParam("size") int size) {

	List<EmailTrack> emailTrackList = emailTackService.getEmailDetails(patientId, doctorId, locationId, hospitalId, page, size);
	Response<EmailTrack> response = new Response<EmailTrack>();
	response.setDataList(emailTrackList);
	return response;
    }

}
