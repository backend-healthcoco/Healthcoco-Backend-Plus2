package com.dpdocter.webservices;

import javax.mail.MessagingException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.dpdocter.elasticsearch.document.ESProfessionDocument;
import com.dpdocter.elasticsearch.repository.ESProfessionRepository;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.services.MailService;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;

import common.util.web.FCMSender;
import common.util.web.Response;

@Component
@Path("testing")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TestExceptionAPI {

	@Autowired
    ESProfessionRepository esProfessionRepository;
	
	@Autowired
	MailService mailService;
	
	@Value("${doctor.android.google.services.api.key}")
    private String DOCTOR_GEOCODING_SERVICES_API_KEY;

	@Value("${patient.android.google.services.api.key}")
    private String PATIENT_GEOCODING_SERVICES_API_KEY;
	
    @GET
    @Path("/exception/{id}")
    public String exceptionTest(@PathParam("id") String id) throws BusinessException {
	throw new BusinessException(ServiceError.InvalidInput, "Request send  is NULL");
    }
    
    @GET
    @Path("/get")
    public Response<ESProfessionDocument> get(@PathParam("id") String id) {
	
    	ESProfessionDocument documents = esProfessionRepository.findById("55f40520e4b0cb1d08c1700d").orElse(null);
    	
    	Response<ESProfessionDocument> response = new Response<ESProfessionDocument>();
    	response.setData(documents);
    	return response;
    }
    
	@GET
	@Path("/mail")
	public Response<Boolean> testMail() throws MessagingException {
		/*Boolean status = mailService.sendExceptionMail("Testing Business Exception mail");
		Response<Boolean> response = new Response<>();
		response.setData(status);
		return response;*/
		throw new MessagingException("testing advice");
	}

	@GET
	@Path("/notification/{id}")
	public Boolean testNotification(@PathParam("id") final Integer id) {
		final String patientToken = "ex_B-ybBKNo:APA91bFuB36OhqqC4DpLfk77qirsPDW-x4-W-ePHikTPa0iTAV0_TV3H9Y07S42N9Yo4FBMt3EMN-iJtBhxuct840KTuRRoGReajh8L2WRx55yNnW45-arSeco2zsYEZ9aIsaYvuM9V3";
		final String doctorToken = "dQm3y7MATVI:APA91bEiyFic8EZqhMPpDQ9o2mxl-37BfxoSzLJKyw-rBwsZbPNHMr4NgymsBuQhsZpQCsHBd90vlZ6snoBCCWGEKvAEOUzZ1D3uQrlsPcn6-_KsLx9k-fXoEWOFtSWlQRHmxEH2SKZM";
		Thread t = new Thread() {
			public void run() {
				try {
					if(id == 1)
					{
					Sender sender = new FCMSender(PATIENT_GEOCODING_SERVICES_API_KEY.trim());
					Message message = new Message.Builder().collapseKey("message").timeToLive(3).delayWhileIdle(true)
							.addData("message", "FCM Notification from Java application").build();

					// Use the same token(or registration id) that was earlier
					// used to send the message to the client directly from
					// Firebase Console's Notification tab.
					Result result = sender.send(message, patientToken, 1);
					}
					else
					{
						Sender sender = new FCMSender(DOCTOR_GEOCODING_SERVICES_API_KEY.trim());
						Message message = new Message.Builder().collapseKey("message").timeToLive(3).delayWhileIdle(true)
								.addData("message", "FCM Notification from Java application").build();

						// Use the same token(or registration id) that was earlier
						// used to send the message to the client directly from
						// Firebase Console's Notification tab.
						Result result = sender.send(message, doctorToken, 1);
					}
						
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		t.start();
		try {
			t.join();
		} catch (InterruptedException iex) {
			iex.printStackTrace();
		}
		return true;
	}
	
}
