package com.dpdocter.webservices;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dpdocter.beans.DoctorContactsResponse;
import com.dpdocter.beans.Group;
import com.dpdocter.beans.PatientCard;
import com.dpdocter.request.GetDoctorContactsRequest;
import com.dpdocter.services.ContactsService;

import common.util.web.Response;

/**
 * @author veeraj
 */

@Component
@Path(PathProxy.CONTACTS_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ContactsApi {
	
	@Autowired
	private ContactsService contactsService;
	
	@Path(value=PathProxy.ContactsUrls.DOCTOR_CONTACTS)
	@POST
	public Response<DoctorContactsResponse> docterContacts(GetDoctorContactsRequest request){
		List<PatientCard> patientCards = contactsService.getDoctorContacts(request);
		int ttlCount = contactsService.getcontactsTotalSize(request);
		DoctorContactsResponse doctorContactsResponse = new DoctorContactsResponse();
		doctorContactsResponse.setPatientCards(patientCards);
		doctorContactsResponse.setTotalSize(ttlCount);
		Response<DoctorContactsResponse> response = new Response<DoctorContactsResponse>();
		response.setData(doctorContactsResponse);
		return response;
	}
	
	@Path(value=PathProxy.ContactsUrls.BLOCK_CONTACT)
	@GET
	public Response<Boolean> blockPatient(@PathParam("doctorId")String doctorId,@PathParam("patientId")String patientId){
		contactsService.blockPatient(patientId, doctorId);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(true);
		return response;
	}
	@Path(value=PathProxy.ContactsUrls.ADD_GROUP)
	@POST
	public Response<Group> addGroup(Group group){
		group = contactsService.addEditGroup(group);
		Response<Group> response = new Response<Group>();
		response.setData(group);
		return response;
	}
	
	@Path(value=PathProxy.ContactsUrls.EDIT_GROUP)
	@POST
	public Response<Group> editGroup(Group group){
		group = contactsService.addEditGroup(group);
		Response<Group> response = new Response<Group>();
		response.setData(group);
		return response;
	}
	
	
	@Path(value=PathProxy.ContactsUrls.DELETE_GROUP)
	@GET
	public Response<Boolean> deleteGroup(@PathParam("groupId") String groupId){
		contactsService.deleteGroup(groupId);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(true);
		return response;
	}
	
	@Path(value=PathProxy.ContactsUrls.TOTAL_COUNT)
	@POST
	public Response<Integer> docterContactsCount(GetDoctorContactsRequest request){
		int ttlCount = contactsService.getcontactsTotalSize(request);
		Response<Integer> response = new Response<Integer>();
		response.setData(ttlCount);
		return response;
	}
	
	@Path(value=PathProxy.ContactsUrls.GET_ALL_GROUPS)
	@GET
	public Response<Group> getAllGroups(@PathParam("doctorId") String doctorId,
			@PathParam("locationId") String locationId,
			@PathParam("hospitalId") String hospitalId){
		List<Group> groups = contactsService.getAllGroups(doctorId,locationId,hospitalId);
		if(groups != null){
			for(Group group : groups){
				GetDoctorContactsRequest getDoctorContactsRequest = new GetDoctorContactsRequest();
				getDoctorContactsRequest.setDoctorId(doctorId);
				List<String> groupList = new ArrayList<String>();
				groupList.add(group.getId());
				getDoctorContactsRequest.setGroups(groupList);
				int ttlCount = contactsService.getcontactsTotalSize(getDoctorContactsRequest);
				group.setCount(ttlCount);
			}
		}
		
		
		
		Response<Group> response = new Response<Group>();
		response.setDataList(groups);
		return response;
	}
	
	
	

}
