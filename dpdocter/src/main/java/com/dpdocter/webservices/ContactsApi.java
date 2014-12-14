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

import com.dpdocter.beans.PatientCard;
import com.dpdocter.services.ContactsService;
import common.util.web.Response;

/**
 * @author veeraj
 */

@Component
@Path(PathProxy.LOGIN_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ContactsApi {
	
	@Autowired
	private ContactsService contactsService;
	
	@Path(value=PathProxy.ContactsUrls.DOCTOR_CONTACTS)
	@GET
	public Response<PatientCard> docterContacts(@PathParam(value="doctorId")String doctorId,@PathParam(value="page")int page,@PathParam(value="size")int size){
		List<PatientCard> patientCards = contactsService.getDoctorContacts(doctorId, false,page,size);
		Response<PatientCard> response = new Response<PatientCard>();
		response.setDataList(patientCards);
		return response;
	}
	
	@Path(value=PathProxy.ContactsUrls.BLOCK_CONTACT)
	@GET
	public Response<Boolean> blockPatient(@PathParam(value="doctorId")String doctorId,@PathParam(value="patientId")String patientId){
		contactsService.blockPatient(patientId, doctorId);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(true);
		return response;
	}
	
	

}
