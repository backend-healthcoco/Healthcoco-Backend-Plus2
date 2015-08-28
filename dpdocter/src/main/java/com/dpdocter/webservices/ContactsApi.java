package com.dpdocter.webservices;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dpdocter.beans.DoctorContactsResponse;
import com.dpdocter.beans.Group;
import com.dpdocter.beans.PatientCard;
import com.dpdocter.beans.RegisteredPatientDetails;
import com.dpdocter.enums.ContactsSearchType;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.request.ExportContactsRequest;
import com.dpdocter.request.GetDoctorContactsRequest;
import com.dpdocter.request.ImportContactsRequest;
import com.dpdocter.request.PatientGroupAddEditRequest;
import com.dpdocter.services.ContactsService;
import com.dpdocter.services.PatientTrackService;

import common.util.web.DPDoctorUtils;
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
    
    @Autowired
    private PatientTrackService patientTrackService;

    @Path(value = PathProxy.ContactsUrls.DOCTOR_CONTACTS)
    @POST
    public Response<DoctorContactsResponse> doctorContacts(GetDoctorContactsRequest request) {
	List<PatientCard> patientCards = contactsService.getDoctorContacts(request);
	int ttlCount = contactsService.getContactsTotalSize(request);
	DoctorContactsResponse doctorContactsResponse = new DoctorContactsResponse();
	doctorContactsResponse.setPatientCards(patientCards);
	doctorContactsResponse.setTotalSize(ttlCount);
	Response<DoctorContactsResponse> response = new Response<DoctorContactsResponse>();
	response.setData(doctorContactsResponse);
	return response;
    }

    @Path(value = PathProxy.ContactsUrls.DOCTOR_CONTACTS_DOCTOR_SPECIFIC)
    @GET
    public Response<DoctorContactsResponse> getDoctorContacts(@PathParam("type") String type, @PathParam("page") int page, @PathParam("size") int size, @QueryParam("doctorId") String doctorId,
    	    @QueryParam("locationId") String locationId, @QueryParam("hospitalId") String hospitalId, 
    	    @QueryParam("createdTime") String createdTime, @QueryParam("isDeleted") Boolean isDeleted) {
    	
    	DoctorContactsResponse doctorContactsResponse = null;
    	
    	if (DPDoctorUtils.anyStringEmpty(type)) {
    	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input. Type Cannot Be Empty");
    	}
    	
    	switch(ContactsSearchType.valueOf(type.toUpperCase())){
    	case DOCTORCONTACTS : doctorContactsResponse = doctorContacts(doctorId, createdTime, isDeleted);break;
    	case RECENTLYVISITED :doctorContactsResponse = patientTrackService.recentlyVisited(doctorId, locationId, hospitalId, page, size);break;
    	case MOSTVISITED :    doctorContactsResponse = patientTrackService.mostVisited(doctorId, locationId, hospitalId, page, size); break;
    	default : break;
    	}
    	
    	Response<DoctorContactsResponse> response = new Response<DoctorContactsResponse>();
    	response.setData(doctorContactsResponse);
    	
	return response;
	
    }
    private DoctorContactsResponse doctorContacts(@QueryParam("doctorId") String doctorId, @QueryParam("createdTime") String createdTime,
    		@QueryParam("isDeleted") Boolean isDeleted) {
	if (DPDoctorUtils.anyStringEmpty(doctorId)) {
	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input. Doctor Id Cannot Be Empty");
	}
	List<PatientCard> patientCards = contactsService.getDoctorContacts(doctorId, createdTime, isDeleted != null ?isDeleted:true); 
		
	int ttlCount = patientCards != null ? patientCards.size() : 0;
	DoctorContactsResponse doctorContactsResponse = new DoctorContactsResponse();
	doctorContactsResponse.setPatientCards(patientCards);
	doctorContactsResponse.setTotalSize(ttlCount);
	return doctorContactsResponse;
    }

    @Path(value = PathProxy.ContactsUrls.DOCTOR_CONTACTS_HANDHELD)
    @GET
    public Response<RegisteredPatientDetails> getDoctorContactsHandheld(@QueryParam(value = "doctorId") String doctorId,
    	    @QueryParam(value = "locationId") String locationId, @QueryParam(value = "hospitalId") String hospitalId, 
    	    @QueryParam(value = "createdTime") String createdTime, @QueryParam(value = "isDeleted") Boolean isDeleted) {
	
    	if(isDeleted != null)return doctorContactsHandheld(doctorId, locationId, hospitalId, createdTime, isDeleted);
    	else return doctorContactsHandheld(doctorId, locationId, hospitalId, createdTime, true);
    }

    private Response<RegisteredPatientDetails> doctorContactsHandheld(String doctorId, String locationId, String hospitalId, String createdTime,
	    boolean isDeleted) {
	List<RegisteredPatientDetails> registeredPatientDetails = contactsService.getDoctorContactsHandheld(doctorId, locationId, hospitalId, createdTime,
		isDeleted);
	Response<RegisteredPatientDetails> response = new Response<RegisteredPatientDetails>();
	response.setDataList(registeredPatientDetails);
	return response;
    }

    @Path(value = PathProxy.ContactsUrls.IMPORT_CONTACTS)
    @POST
    public Response<Boolean> importContacts(ImportContactsRequest request) {
	if (request == null) {
	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input. Import Request Cannot Be Empty");
	}
	Boolean importContactsResponse = contactsService.importContacts(request);
	Response<Boolean> response = new Response<Boolean>();
	response.setData(importContactsResponse);
	return response;
    }

    @Path(value = PathProxy.ContactsUrls.EXPORT_CONTACTS)
    @POST
    public Response<Boolean> exportContacts(ExportContactsRequest request) {
	if (request == null) {
	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input. Export Request Cannot Be Empty");
	}
	Boolean exportContactsResponse = contactsService.exportContacts(request);
	Response<Boolean> response = new Response<Boolean>();
	response.setData(exportContactsResponse);
	return response;
    }

    @Path(value = PathProxy.ContactsUrls.BLOCK_CONTACT)
    @GET
    public Response<Boolean> blockPatient(@PathParam("doctorId") String doctorId, @PathParam("patientId") String patientId) {
	contactsService.blockPatient(patientId, doctorId);
	Response<Boolean> response = new Response<Boolean>();
	response.setData(true);
	return response;
    }

    @Path(value = PathProxy.ContactsUrls.ADD_GROUP)
    @POST
    public Response<Group> addGroup(Group group) {
	Group responseGroup = contactsService.addEditGroup(group);
	Response<Group> response = new Response<Group>();
	response.setData(responseGroup);
	return response;
    }

    @Path(value = PathProxy.ContactsUrls.EDIT_GROUP)
    @POST
    public Response<Group> editGroup(Group group) {
	Group responseGroup = contactsService.addEditGroup(group);
	Response<Group> response = new Response<Group>();
	response.setData(responseGroup);
	return response;
    }

    @Path(value = PathProxy.ContactsUrls.DELETE_GROUP)
    @GET
    public Response<Boolean> deleteGroup(@PathParam("groupId") String groupId) {
	Boolean groupDeleteResponse = contactsService.deleteGroup(groupId);
	Response<Boolean> response = new Response<Boolean>();
	response.setData(groupDeleteResponse);
	return response;
    }

    @Path(value = PathProxy.ContactsUrls.TOTAL_COUNT)
    @POST
    public Response<Integer> doctorContactsCount(GetDoctorContactsRequest request) {
	int ttlCount = contactsService.getContactsTotalSize(request);
	Response<Integer> response = new Response<Integer>();
	response.setData(ttlCount);
	return response;
    }

    @Path(value = PathProxy.ContactsUrls.GET_ALL_GROUPS)
    @GET
    public Response<Group> getAllGroups(@PathParam("page") int page, @PathParam("size") int size, @QueryParam("doctorId") String doctorId, @QueryParam("locationId") String locationId, @QueryParam("hospitalId") String hospitalId,
    		@QueryParam("createdTime") String createdTime, @QueryParam("isDeleted") Boolean isDeleted) {
    	
    	if(isDeleted != null)return getGroups(page, size, doctorId, locationId, hospitalId, createdTime, isDeleted);
    	else	return getGroups(page, size, doctorId, locationId, hospitalId, createdTime, true);
    }

    private Response<Group> getGroups(int page, int size, String doctorId, String locationId, String hospitalId, String createdTime, boolean isDeleted) {
	List<Group> groups = contactsService.getAllGroups(page, size, doctorId, locationId, hospitalId, createdTime, isDeleted);
	if (groups != null) {
	    for (Group group : groups) {
		GetDoctorContactsRequest getDoctorContactsRequest = new GetDoctorContactsRequest();
		getDoctorContactsRequest.setDoctorId(doctorId);
		List<String> groupList = new ArrayList<String>();
		groupList.add(group.getId());
		getDoctorContactsRequest.setGroups(groupList);
		int ttlCount = contactsService.getContactsTotalSize(getDoctorContactsRequest);
		group.setCount(ttlCount);
	    }
	}
	Response<Group> response = new Response<Group>();
	response.setDataList(groups);
	return response;
    }

    @Path(value = PathProxy.ContactsUrls.ADD_GROUP_TO_PATIENT)
    @POST
    public Response<PatientGroupAddEditRequest> addGroupToPatient(PatientGroupAddEditRequest request) {

	PatientGroupAddEditRequest groups = contactsService.addGroupToPatient(request);
	Response<PatientGroupAddEditRequest> response = new Response<PatientGroupAddEditRequest>();
	response.setData(groups);
	return response;
    }
}
