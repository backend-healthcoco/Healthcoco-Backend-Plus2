package com.dpdocter.webservices;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dpdocter.beans.AppointmentGeneralFeedback;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.services.FeedbackService;

import common.util.web.Response;

@Component
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path(PathProxy.FEEDBACK_BASE_URL)
public class FeedbackAPI {
	
	@Autowired
	FeedbackService feedbackService;
	
	@POST
	@Path(PathProxy.FeedbackUrls.ADD_EDIT_GENERAL_APPOINTMENT_FEEDBACK)
	public AppointmentGeneralFeedback addEditAppointmentGeneralFeedback(AppointmentGeneralFeedback feedback)
	{
		Response<AppointmentGeneralFeedback> response = new Response<>();
		AppointmentGeneralFeedback appointmentGeneralFeedback = null;
		if(feedback == null){
			throw new BusinessException(ServiceError.InvalidInput , "Invalid input");
		}
		
		//appointmentGeneralFeedback = feedbackService.
		
		return null;
	}

}
