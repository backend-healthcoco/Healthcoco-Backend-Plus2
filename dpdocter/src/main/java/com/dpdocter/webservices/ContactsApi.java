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
import com.dpdocter.beans.RegisteredPatientDetails;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.request.ExportContactsRequest;
import com.dpdocter.request.GetDoctorContactsRequest;
import com.dpdocter.request.ImportContactsRequest;
import com.dpdocter.services.ContactsService;
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
    public Response<DoctorContactsResponse> getDoctorContacts(@PathParam("doctorId") String doctorId) {
	return doctorContacts(doctorId, null, true);
    }

    @Path(value = PathProxy.ContactsUrls.DOCTOR_CONTACTS_DOCTOR_SPECIFIC_CREATED_TIME)
    @GET
    public Response<DoctorContactsResponse> getDoctorContacts(@PathParam("doctorId") String doctorId, @PathParam("createdTime") String createdTime) {
	return doctorContacts(doctorId, createdTime, true);
    }

    @Path(value = PathProxy.ContactsUrls.DOCTOR_CONTACTS_DOCTOR_SPECIFIC_CREATED_TIME_ISDELETED)
    @GET
    public Response<DoctorContactsResponse> getDoctorContacts(@PathParam("doctorId") String doctorId, @PathParam("createdTime") String createdTime,
    		@PathParam("isDeleted") boolean isDeleted) {
	return doctorContacts(doctorId, createdTime, isDeleted);
    }
    
    private Response<DoctorContactsResponse> doctorContacts(@PathParam("doctorId") String doctorId, @PathParam("createdTime") String createdTime,
    		@PathParam("isDeleted")  boolean isDeleted) {
	if (DPDoctorUtils.anyStringEmpty(doctorId)) {
	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input. Doctor Id Cannot Be Empty");
	}
	List<PatientCard> patientCards = contactsService.getDoctorContacts(doctorId, createdTime, isDeleted);
	int ttlCount = patientCards !=null ?patientCards.size():0;
	DoctorContactsResponse doctorContactsResponse = new DoctorContactsResponse();
	doctorContactsResponse.setPatientCards(patientCards);
	doctorContactsResponse.setTotalSize(ttlCount);
	Response<DoctorContactsResponse> response = new Response<DoctorContactsResponse>();
	response.setData(doctorContactsResponse);
	return response;
    }

    @Path(value = PathProxy.ContactsUrls.DOCTOR_CONTACTS_HANDHELD_DOCTOR_SPECIFIC)
    @GET
    public Response<RegisteredPatientDetails> getDoctorContactsHandheld(@PathParam("doctorId") String doctorId, @PathParam("createdTime") String createdTime) {
	if (DPDoctorUtils.anyStringEmpty(doctorId, createdTime)) {
	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input. Doctor Id, Created Time Cannot Be Empty");
	}
	return doctorContactsHandheld(doctorId, null, null, createdTime, true);
    }

    @Path(value = PathProxy.ContactsUrls.DOCTOR_CONTACTS_HANDHELD_DOCTOR_SPECIFIC_ISDELETED)
    @GET
    public Response<RegisteredPatientDetails> getDoctorContactsHandheld(@PathParam("doctorId") String doctorId, @PathParam("createdTime") String createdTime,
    		@PathParam("isDeleted") boolean isDeleted) {
	if (DPDoctorUtils.anyStringEmpty(doctorId, createdTime)) {
	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input. Doctor Id, Created Time Cannot Be Empty");
	}
	return doctorContactsHandheld(doctorId, null, null, createdTime, isDeleted);
    }
    
    @Path(value = PathProxy.ContactsUrls.DOCTOR_CONTACTS_HANDHELD)
    @GET
    public Response<RegisteredPatientDetails> getDoctorContactsHandheld(@PathParam("doctorId") String doctorId, @PathParam("locationId") String locationId,
	    @PathParam("hospitalId") String hospitalId, @PathParam("createdTime") String createdTime) {
	if (DPDoctorUtils.anyStringEmpty(doctorId, locationId, hospitalId, createdTime)) {
	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input. Doctor Id, Location Id, Hospital Id, Created Time Cannot Be Empty");
	}
	return doctorContactsHandheld(doctorId, locationId, hospitalId, createdTime, true);
    }
    
    @Path(value = PathProxy.ContactsUrls.DOCTOR_CONTACTS_HANDHELD_ISDELETED)
    @GET
    public Response<RegisteredPatientDetails> getDoctorContactsHandheld(@PathParam("doctorId") String doctorId, @PathParam("locationId") String locationId,
	    @PathParam("hospitalId") String hospitalId, @PathParam("createdTime") String createdTime, @PathParam("isDeleted") boolean isDeleted) {
	if (DPDoctorUtils.anyStringEmpty(doctorId, locationId, hospitalId, createdTime)) {
	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input. Doctor Id, Location Id, Hospital Id, Created Time Cannot Be Empty");
	}
	return doctorContactsHandheld(doctorId, locationId, hospitalId, createdTime, isDeleted);
    }

    private Response<RegisteredPatientDetails> doctorContactsHandheld(String doctorId, String locationId, String hospitalId, String createdTime, boolean isDeleted) {
	List<RegisteredPatientDetails> registeredPatientDetails = contactsService.getDoctorContactsHandheld(doctorId, locationId, hospitalId, createdTime, isDeleted);
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
	group = contactsService.addEditGroup(group);
	Response<Group> response = new Response<Group>();
	response.setData(group);
	return response;
    }

    @Path(value = PathProxy.ContactsUrls.EDIT_GROUP)
    @POST
    public Response<Group> editGroup(Group group) {
	group = contactsService.addEditGroup(group);
	Response<Group> response = new Response<Group>();
	response.setData(group);
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
    public Response<Group> getAllGroups(@PathParam("doctorId") String doctorId, @PathParam("locationId") String locationId,
	    @PathParam("hospitalId") String hospitalId) {
	return getGroups(doctorId, locationId, hospitalId, null, true);
    }

    @Path(value = PathProxy.ContactsUrls.GET_ALL_GROUPS_CREATED_TIME)
    @GET
    public Response<Group> getAllGroups(@PathParam("doctorId") String doctorId, @PathParam("locationId") String locationId,
	    @PathParam("hospitalId") String hospitalId, @PathParam("createdTime") String createdTime) {
	return getGroups(doctorId, locationId, hospitalId, createdTime, true);
    }
    
    @Path(value = PathProxy.ContactsUrls.GET_ALL_GROUPS_CREATED_TIME_ISDELETED)
    @GET
    public Response<Group> getAllGroups(@PathParam("doctorId") String doctorId, @PathParam("locationId") String locationId,
	    @PathParam("hospitalId") String hospitalId, @PathParam("createdTime") String createdTime, @PathParam("isDeleted") boolean isDeleted) {
	return getGroups(doctorId, locationId, hospitalId, createdTime, isDeleted);
    }

    private Response<Group> getGroups(String doctorId, String locationId, String hospitalId, String createdTime, boolean isDeleted) {
	List<Group> groups = contactsService.getAllGroups(doctorId, locationId, hospitalId, createdTime, isDeleted);
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

    @Path(value = PathProxy.ContactsUrls.GET_ALL_DOCTOR_SPECIFIC_GROUPS)
    @GET
    public Response<Group> getAllGroups(@PathParam("doctorId") String doctorId) {
	return getGroups(doctorId, null, true);
    }

    @Path(value = PathProxy.ContactsUrls.GET_ALL_DOCTOR_SPECIFIC_GROUPS_CREATED_TIME)
    @GET
    public Response<Group> getAllGroups(@PathParam("doctorId") String doctorId, @PathParam("createdTime") String createdTime) {
	return getGroups(doctorId, createdTime, true);
    }
    
    @Path(value = PathProxy.ContactsUrls.GET_ALL_DOCTOR_SPECIFIC_GROUPS_CREATED_TIME_ISDELETED)
    @GET
    public Response<Group> getAllGroups(@PathParam("doctorId") String doctorId, @PathParam("createdTime") String createdTime,
    		@PathParam("isDeleted") boolean isDeleted) {
	return getGroups(doctorId, createdTime, isDeleted);
    }

    private Response<Group> getGroups(String doctorId, String createdTime, boolean isDeleted) {
	if (DPDoctorUtils.anyStringEmpty(doctorId)) {
	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input. Doctor Id Cannot Be Empty");
	}
	List<Group> groups = contactsService.getAllGroups(doctorId, createdTime, isDeleted);
	Response<Group> response = new Response<Group>();
	response.setDataList(groups);
	return response;
    }

}
