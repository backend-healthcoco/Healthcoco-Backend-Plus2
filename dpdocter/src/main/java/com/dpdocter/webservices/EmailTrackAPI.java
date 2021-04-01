package com.dpdocter.webservices;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dpdocter.beans.EmailTrack;
import com.dpdocter.services.EmailTackService;

import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value = PathProxy.EMAIL_TRACK_BASE_URL,produces = MediaType.APPLICATION_JSON ,consumes = MediaType.APPLICATION_JSON)
@Api(value = PathProxy.EMAIL_TRACK_BASE_URL, description = "Endpoint for email track")
public class EmailTrackAPI {

//    private static Logger logger = LogManager.getLogger(EmailTrackAPI.class.getName());

    @Autowired
    private EmailTackService emailTackService;

    @GetMapping
    @ApiOperation(value = "GET_EMAIL_DETAILS", notes = "GET_EMAIL_DETAILS")
    public Response<EmailTrack> getEmailDetails(@RequestParam(value = "patientId") String patientId, @RequestParam(value = "doctorId") String doctorId,
	    @RequestParam(value = "locationId") String locationId, @RequestParam(value = "hospitalId") String hospitalId, @RequestParam("page") long page,
	    @RequestParam("size") int size) {

	List<EmailTrack> emailTrackList = emailTackService.getEmailDetails(patientId, doctorId, locationId, hospitalId, page, size);
	Response<EmailTrack> response = new Response<EmailTrack>();
	response.setDataList(emailTrackList);
	return response;
    }
    

}
