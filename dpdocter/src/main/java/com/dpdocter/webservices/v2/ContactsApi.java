package com.dpdocter.webservices.v2;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.Produces;
import org.springframework.http.MediaType;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dpdocter.beans.v2.DoctorContactsResponse;
import com.dpdocter.beans.v2.PatientCard;
import com.dpdocter.beans.v2.RegisteredPatientDetails;
import com.dpdocter.enums.ContactsSearchType;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.request.GetDoctorContactsRequest;
import com.dpdocter.services.v2.ContactsService;
import com.dpdocter.services.v2.PatientVisitService;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController(value = "ContactsApiV2")
@RequestMapping(value=PathProxy.CONTACTS_BASE_URL,produces = MediaType.APPLICATION_JSON_VALUE ,consumes = MediaType.APPLICATION_JSON_VALUE)
@Api(value = PathProxy.CONTACTS_BASE_URL, description = "Endpoint for version 2 contacts")
public class ContactsApi {

	private static Logger logger = LogManager.getLogger(ContactsApi.class.getName());

	@Autowired
	private ContactsService contactsService;

	@Autowired
	private PatientVisitService patientTrackService;

	@Value(value = "${image.path}")
	private String imagePath;

	@PostMapping
	@ApiOperation(value = "GET_DOCTOR_CONTACTS", notes = "GET_DOCTOR_CONTACTS")
	public Response<DoctorContactsResponse> doctorContacts(GetDoctorContactsRequest request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getLocationId()) || request.getGroups() == null
				|| request.getGroups().isEmpty()) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		DoctorContactsResponse doctorContactsResponse = contactsService.getDoctorContacts(request);
		if (doctorContactsResponse != null && doctorContactsResponse.getPatientCards() != null
				&& !doctorContactsResponse.getPatientCards().isEmpty()) {
			for (PatientCard patientCard : doctorContactsResponse.getPatientCards()) {
				//patientCard.setImageUrl(getFinalImageURL(patientCard.getImageUrl()));
				patientCard.setThumbnailUrl(getFinalImageURL(patientCard.getThumbnailUrl()));
			}
		}
		Response<DoctorContactsResponse> response = new Response<DoctorContactsResponse>();
		response.setData(doctorContactsResponse);
		return response;
	}

	
	@GetMapping(value = PathProxy.ContactsUrls.DOCTOR_CONTACTS_DOCTOR_SPECIFIC)
	@ApiOperation(value = PathProxy.ContactsUrls.DOCTOR_CONTACTS_DOCTOR_SPECIFIC, notes = PathProxy.ContactsUrls.DOCTOR_CONTACTS_DOCTOR_SPECIFIC)
	public Response<DoctorContactsResponse> getDoctorContacts(@PathVariable("type") String type,
			@RequestParam("page") int page, @RequestParam("size") int size, @RequestParam(value = "doctorId") String doctorId,
			@RequestParam("locationId") String locationId, @RequestParam("hospitalId") String hospitalId,
			@DefaultValue("0") @RequestParam("updatedTime") String updatedTime,
			  @RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded, @RequestParam("role") String role) {

		DoctorContactsResponse doctorContactsResponse = null;

		if (DPDoctorUtils.anyStringEmpty(type)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		switch (ContactsSearchType.valueOf(type.toUpperCase())) {
		case DOCTORCONTACTS:
			doctorContactsResponse = contactsService.getDoctorContactsSortedByName(doctorId, locationId, hospitalId,
					updatedTime, discarded, page, size, role);
			break;
		case RECENTLYADDED:
			doctorContactsResponse = contactsService.getDoctorContacts(doctorId, locationId, hospitalId, updatedTime,
					discarded, page, size, role);
			break;
		case RECENTLYVISITED:
			doctorContactsResponse = patientTrackService.recentlyVisited(doctorId, locationId, hospitalId, page, size,
					role);
			break;
		case MOSTVISITED:
			doctorContactsResponse = patientTrackService.mostVisited(doctorId, locationId, hospitalId, page, size,
					role);
			break;
		default:
			break;
		}

		if (doctorContactsResponse != null && doctorContactsResponse.getPatientCards() != null
				&& !doctorContactsResponse.getPatientCards().isEmpty()) {
			for (PatientCard patientCard : doctorContactsResponse.getPatientCards()) {
				//patientCard.setImageUrl(getFinalImageURL(patientCard.getImageUrl()));
				patientCard.setThumbnailUrl(getFinalImageURL(patientCard.getThumbnailUrl()));
			}
		}

		Response<DoctorContactsResponse> response = new Response<DoctorContactsResponse>();
		response.setData(doctorContactsResponse);

		return response;

	}

	
	@GetMapping(value = PathProxy.ContactsUrls.DOCTOR_CONTACTS_HANDHELD)
	@ApiOperation(value = PathProxy.ContactsUrls.DOCTOR_CONTACTS_HANDHELD, notes = PathProxy.ContactsUrls.DOCTOR_CONTACTS_HANDHELD)
	public Response<Object> getDoctorContactsHandheld(@RequestParam(value = "doctorId") String doctorId,
			@RequestParam(value = "locationId") String locationId, @RequestParam(value = "hospitalId") String hospitalId,
			@DefaultValue("0") @RequestParam(value = "updatedTime") String updatedTime,
			  @RequestParam(value = "discarded") Boolean discarded, @RequestParam("role") String role,
			@RequestParam("page") int page, @RequestParam("size") int size, @RequestParam("searchTerm") String searchTerm) {

		List<RegisteredPatientDetails> registeredPatientDetails = contactsService.getDoctorContactsHandheld(doctorId,
				locationId, hospitalId, updatedTime, discarded, role, page, size, searchTerm);
		
		if (registeredPatientDetails != null && !registeredPatientDetails.isEmpty()) {
			for (RegisteredPatientDetails registeredPatientDetail : registeredPatientDetails) {
				registeredPatientDetail.setImageUrl(getFinalImageURL(registeredPatientDetail.getImageUrl()));
				registeredPatientDetail.setThumbnailUrl(getFinalImageURL(registeredPatientDetail.getThumbnailUrl()));
			}
		}

		Response<Object> response = new Response<Object>();
		response.setDataList(registeredPatientDetails);
		response.setData(contactsService.getDoctorContactsHandheldCount(doctorId, locationId, hospitalId, discarded,
				role, searchTerm));
		return response;
	}
/*
	(value = PathProxy.ContactsUrls.IMPORT_CONTACTS)
	@PostMapping
	@ApiOperation(value = PathProxy.ContactsUrls.IMPORT_CONTACTS, notes = PathProxy.ContactsUrls.IMPORT_CONTACTS)
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

	(value = PathProxy.ContactsUrls.BLOCK_CONTACT)
	@GetMapping
	@ApiOperation(value = PathProxy.ContactsUrls.BLOCK_CONTACT, notes = PathProxy.ContactsUrls.BLOCK_CONTACT)
	public Response<Boolean> blockPatient(@PathVariable("doctorId") String doctorId,
			@PathVariable("patientId") String patientId) {
		contactsService.blockPatient(patientId, doctorId);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(true);
		return response;
	}

	(value = PathProxy.ContactsUrls.ADD_GROUP)
	@PostMapping
	@ApiOperation(value = PathProxy.ContactsUrls.ADD_GROUP, notes = PathProxy.ContactsUrls.ADD_GROUP)
	public Response<Group> addGroup(Group group) {
		if (group == null || DPDoctorUtils.anyStringEmpty(group.getDoctorId())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Group responseGroup = contactsService.addEditGroup(group);
		Response<Group> response = new Response<Group>();
		response.setData(responseGroup);
		return response;
	}

	(value = PathProxy.ContactsUrls.EDIT_GROUP)
	@PutMapping
	@ApiOperation(value = PathProxy.ContactsUrls.EDIT_GROUP, notes = PathProxy.ContactsUrls.EDIT_GROUP)
	public Response<Group> editGroup(@PathVariable("groupId") String groupId, Group group) {
		if (group == null || DPDoctorUtils.anyStringEmpty(group.getDoctorId(), groupId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		group.setId(groupId);
		Group responseGroup = contactsService.addEditGroup(group);
		Response<Group> response = new Response<Group>();
		response.setData(responseGroup);
		return response;
	}

	(value = PathProxy.ContactsUrls.DELETE_GROUP)
	@DeleteMapping
	@ApiOperation(value = PathProxy.ContactsUrls.DELETE_GROUP, notes = PathProxy.ContactsUrls.DELETE_GROUP)
	public Response<Group> deleteGroup(@PathVariable("groupId") String groupId,
			  @RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(groupId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Group groupDeleteResponse = contactsService.deleteGroup(groupId, discarded);
		Response<Group> response = new Response<Group>();
		response.setData(groupDeleteResponse);
		return response;
	}

	(value = PathProxy.ContactsUrls.TOTAL_COUNT)
	@PostMapping
	@ApiOperation(value = PathProxy.ContactsUrls.TOTAL_COUNT, notes = PathProxy.ContactsUrls.TOTAL_COUNT)
	public Response<Integer> doctorContactsCount(GetDoctorContactsRequest request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		int ttlCount = contactsService.getContactsTotalSize(request);
		Response<Integer> response = new Response<Integer>();
		response.setData(ttlCount);
		return response;
	}

	(value = PathProxy.ContactsUrls.GET_ALL_GROUPS)
	@GetMapping
	@ApiOperation(value = PathProxy.ContactsUrls.GET_ALL_GROUPS, notes = PathProxy.ContactsUrls.GET_ALL_GROUPS)
	public Response<Object> getAllGroups(@RequestParam("page") int page, @RequestParam("size") int size,
			@RequestParam("doctorId") String doctorId, @RequestParam("locationId") String locationId,
			@RequestParam("hospitalId") String hospitalId,
			@DefaultValue("0") @RequestParam("updatedTime") String updatedTime,
			  @RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded) {
		String packageType = "ADVANCE";
		if (DPDoctorUtils.anyStringEmpty(locationId)&&DPDoctorUtils.anyStringEmpty(doctorId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<Object> response = new Response<Object>();
		List<Group> groups = contactsService.getAllGroups(page, size, doctorId, locationId, hospitalId, updatedTime,
				discarded);
		if (groups != null) {
			for (Group group : groups) {
				GetDoctorContactsRequest getDoctorContactsRequest = new GetDoctorContactsRequest();
				getDoctorContactsRequest.setDoctorId(doctorId);
				List<String> groupList = new ArrayList<String>();
				groupList.add(group.getId());

				if (!DPDoctorUtils.anyStringEmpty(group.getPackageType())) {
					packageType = group.getPackageType();

				}
				getDoctorContactsRequest.setGroups(groupList);
				int ttlCount = contactsService.getContactsTotalSize(getDoctorContactsRequest);
				group.setCount(ttlCount);
			}
		} else {
			response.setData(PackageType.ADVANCE.getType());
		}

		response.setData(packageType);

		response.setDataList(groups);

		return response;
	}

	(value = PathProxy.ContactsUrls.ADD_GROUP_TO_PATIENT)
	@PostMapping
	@ApiOperation(value = PathProxy.ContactsUrls.ADD_GROUP_TO_PATIENT, notes = PathProxy.ContactsUrls.ADD_GROUP_TO_PATIENT)
	public Response<PatientGroupAddEditRequest> addGroupToPatient(PatientGroupAddEditRequest request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getHospitalId(),
				request.getLocationId(), request.getPatientId())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		PatientGroupAddEditRequest groups = contactsService.addGroupToPatient(request);
		Response<PatientGroupAddEditRequest> response = new Response<PatientGroupAddEditRequest>();
		response.setData(groups);
		return response;
	}*/

	private String getFinalImageURL(String imageURL) {
		if (imageURL != null) {
			return imagePath + imageURL;
		} else
			return null;
	}
/*
	(value = PathProxy.ContactsUrls.SEND_SMS_TO_GROUP)
	@PostMapping
	@ApiOperation(value = PathProxy.ContactsUrls.SEND_SMS_TO_GROUP, notes = PathProxy.ContactsUrls.SEND_SMS_TO_GROUP)
	public Response<Boolean> sendSMSToGroup(BulkSMSRequest request) {
		Boolean status = null;
		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		status = contactsService.sendSMSToGroup(request);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(status);
		return response;
	}*/

}
