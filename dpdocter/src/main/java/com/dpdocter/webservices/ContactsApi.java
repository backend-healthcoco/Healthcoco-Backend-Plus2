package com.dpdocter.webservices;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import com.dpdocter.services.PatientVisitService;

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

    private static Logger logger = Logger.getLogger(ContactsApi.class.getName());

    @Autowired
    private ContactsService contactsService;

    @Autowired
    private PatientVisitService patientTrackService;

    @Value(value = "${image.path}")
    private String imagePath;

    @POST
    public Response<DoctorContactsResponse> doctorContacts(GetDoctorContactsRequest request) {
	List<PatientCard> patientCards = contactsService.getDoctorContacts(request);
	int ttlCount = contactsService.getContactsTotalSize(request);
	DoctorContactsResponse doctorContactsResponse = new DoctorContactsResponse();
	if (patientCards != null && !patientCards.isEmpty()) {
	    for (PatientCard patientCard : patientCards) {
		patientCard.setImageUrl(getFinalImageURL(patientCard.getImageUrl()));
		patientCard.setThumbnailUrl(getFinalImageURL(patientCard.getThumbnailUrl()));
	    }
	}
	doctorContactsResponse.setPatientCards(patientCards);
	doctorContactsResponse.setTotalSize(ttlCount);
	Response<DoctorContactsResponse> response = new Response<DoctorContactsResponse>();
	response.setData(doctorContactsResponse);
	return response;
    }

    @Path(value = PathProxy.ContactsUrls.DOCTOR_CONTACTS_DOCTOR_SPECIFIC)
    @GET
    public Response<DoctorContactsResponse> getDoctorContacts(@PathParam("type") String type, @QueryParam("page") int page, @QueryParam("size") int size,
	    @QueryParam("doctorId") String doctorId, @QueryParam("locationId") String locationId, @QueryParam("hospitalId") String hospitalId,
	    @DefaultValue("0") @QueryParam("updatedTime") String updatedTime, @DefaultValue("true") @QueryParam("discarded") Boolean discarded) {

	DoctorContactsResponse doctorContactsResponse = null;

	if (DPDoctorUtils.anyStringEmpty(type)) {
	    logger.warn("Invalid Input. Type Cannot Be Empty");
	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input. Type Cannot Be Empty");
	}

	switch (ContactsSearchType.valueOf(type.toUpperCase())) {
	case DOCTORCONTACTS:
	    doctorContactsResponse = contactsService.getDoctorContactsSortedByName(doctorId, locationId, hospitalId, updatedTime, discarded, page, size);
	    break;
	case RECENTLYADDED:
	    doctorContactsResponse = contactsService.getDoctorContacts(doctorId, locationId, hospitalId, updatedTime, discarded, page, size);
	    break;
	case RECENTLYVISITED:
	    doctorContactsResponse = patientTrackService.recentlyVisited(doctorId, locationId, hospitalId, page, size);
	    break;
	case MOSTVISITED:
	    doctorContactsResponse = patientTrackService.mostVisited(doctorId, locationId, hospitalId, page, size);
	    break;
	default:
	    break;
	}

	if (doctorContactsResponse != null && doctorContactsResponse.getPatientCards() != null && !doctorContactsResponse.getPatientCards().isEmpty()) {
	    for (PatientCard patientCard : doctorContactsResponse.getPatientCards()) {
		patientCard.setImageUrl(getFinalImageURL(patientCard.getImageUrl()));
		patientCard.setThumbnailUrl(getFinalImageURL(patientCard.getThumbnailUrl()));
	    }
	}

	Response<DoctorContactsResponse> response = new Response<DoctorContactsResponse>();
	response.setData(doctorContactsResponse);

	return response;

    }

    @Path(value = PathProxy.ContactsUrls.DOCTOR_CONTACTS_HANDHELD)
    @GET
    public Response<RegisteredPatientDetails> getDoctorContactsHandheld(@QueryParam(value = "doctorId") String doctorId,
	    @QueryParam(value = "locationId") String locationId, @QueryParam(value = "hospitalId") String hospitalId,
	    @DefaultValue("0") @QueryParam(value = "updatedTime") String updatedTime,
	    @DefaultValue("true") @QueryParam(value = "discarded") Boolean discarded) {

	return doctorContactsHandheld(doctorId, locationId, hospitalId, updatedTime, discarded);
    }

    private Response<RegisteredPatientDetails> doctorContactsHandheld(String doctorId, String locationId, String hospitalId, String updatedTime,
	    boolean discarded) {
	List<RegisteredPatientDetails> registeredPatientDetails = contactsService.getDoctorContactsHandheld(doctorId, locationId, hospitalId, updatedTime,
		discarded);
	if (registeredPatientDetails != null && !registeredPatientDetails.isEmpty()) {
	    for (RegisteredPatientDetails registeredPatientDetail : registeredPatientDetails) {
		registeredPatientDetail.setImageUrl(getFinalImageURL(registeredPatientDetail.getImageUrl()));
		registeredPatientDetail.setThumbnailUrl(getFinalImageURL(registeredPatientDetail.getThumbnailUrl()));
	    }
	}
	Response<RegisteredPatientDetails> response = new Response<RegisteredPatientDetails>();
	response.setDataList(registeredPatientDetails);
	return response;
    }

    @Path(value = PathProxy.ContactsUrls.IMPORT_CONTACTS)
    @POST
    public Response<Boolean> importContacts(ImportContactsRequest request) {
	if (request == null) {
	    logger.warn("Invalid Input. Import Request Cannot Be Empty");
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
	    logger.warn("Invalid Input. Export Request Cannot Be Empty");
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
    @PUT
    public Response<Group> editGroup(@PathParam("groupId") String groupId, Group group) {
	if (DPDoctorUtils.anyStringEmpty(groupId)) {
	    logger.warn("Invalid Input. GroupId Cannot Be Empty");
	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input. GroupId Cannot Be Empty");
	}
	group.setId(groupId);
	Group responseGroup = contactsService.addEditGroup(group);
	Response<Group> response = new Response<Group>();
	response.setData(responseGroup);
	return response;
    }

    @Path(value = PathProxy.ContactsUrls.DELETE_GROUP)
    @DELETE
    public Response<Group> deleteGroup(@PathParam("groupId") String groupId, @DefaultValue("true") @QueryParam("discarded") Boolean discarded) {
    	Group groupDeleteResponse = contactsService.deleteGroup(groupId, discarded);
	Response<Group> response = new Response<Group>();
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
    public Response<Group> getAllGroups(@QueryParam("page") int page, @QueryParam("size") int size, @QueryParam("doctorId") String doctorId,
	    @QueryParam("locationId") String locationId, @QueryParam("hospitalId") String hospitalId,
	    @DefaultValue("0") @QueryParam("updatedTime") String updatedTime, @DefaultValue("true") @QueryParam("discarded") Boolean discarded) {

	return getGroups(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
    }

    private Response<Group> getGroups(int page, int size, String doctorId, String locationId, String hospitalId, String updatedTime, boolean discarded) {
	List<Group> groups = contactsService.getAllGroups(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
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

    private String getFinalImageURL(String imageURL) {
	if (imageURL != null) {
	    return imagePath + imageURL;
	} else
	    return null;
    }
}
