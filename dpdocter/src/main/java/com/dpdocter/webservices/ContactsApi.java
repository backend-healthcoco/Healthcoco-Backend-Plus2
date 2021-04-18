package com.dpdocter.webservices;

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
import org.springframework.http.MediaType;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dpdocter.beans.Branch;
import com.dpdocter.beans.BulKMessage;
import com.dpdocter.beans.DoctorContactsResponse;
import com.dpdocter.beans.Group;
import com.dpdocter.beans.PatientCard;
import com.dpdocter.beans.RegisteredPatientDetails;
import com.dpdocter.enums.ContactsSearchType;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.request.BulkSMSRequest;
import com.dpdocter.request.ExportRequest;
import com.dpdocter.request.GetDoctorContactsRequest;
import com.dpdocter.request.ImportContactsRequest;
import com.dpdocter.request.PatientGroupAddEditRequest;
import com.dpdocter.services.ContactsService;
import com.dpdocter.services.PatientVisitService;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value=PathProxy.CONTACTS_BASE_URL,produces = MediaType.APPLICATION_JSON_VALUE ,consumes = MediaType.APPLICATION_JSON_VALUE)
@Api(value = PathProxy.CONTACTS_BASE_URL, description = "Endpoint for contacts")
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
				patientCard.setImageUrl(getFinalImageURL(patientCard.getImageUrl()));
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
			@RequestParam("page") long page, @RequestParam("size") int size, @RequestParam(value = "doctorId") String doctorId,
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
				patientCard.setImageUrl(getFinalImageURL(patientCard.getImageUrl()));
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
			@RequestParam("page") int page, @RequestParam("size") int size, @RequestParam("searchTerm") String searchTerm,@RequestParam("userId") String userId) {

		List<RegisteredPatientDetails> registeredPatientDetails = contactsService.getDoctorContactsHandheld(doctorId,
				locationId, hospitalId, updatedTime, discarded, role, page, size, searchTerm,userId);
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

	
	@PostMapping(value = PathProxy.ContactsUrls.IMPORT_CONTACTS)
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

	
	@PostMapping(value = PathProxy.ContactsUrls.EXPORT_CONTACTS)
	@ApiOperation(value = PathProxy.ContactsUrls.EXPORT_CONTACTS, notes = PathProxy.ContactsUrls.EXPORT_CONTACTS)
	public Response<Boolean> exportContacts(ExportRequest request) {
		if (request == null) {
			logger.warn("Invalid Input. Export Request Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input. Export Request Cannot Be Empty");
		}
		Boolean exportContactsResponse = contactsService.exportContacts(request);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(exportContactsResponse);
		return response;
	}

	
	@GetMapping(value = PathProxy.ContactsUrls.BLOCK_CONTACT)
	@ApiOperation(value = PathProxy.ContactsUrls.BLOCK_CONTACT, notes = PathProxy.ContactsUrls.BLOCK_CONTACT)
	public Response<Boolean> blockPatient(@PathVariable("doctorId") String doctorId,
			@PathVariable("patientId") String patientId) {
		contactsService.blockPatient(patientId, doctorId);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(true);
		return response;
	}

	
	@PostMapping(value = PathProxy.ContactsUrls.ADD_GROUP)
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

	
	@PutMapping(value = PathProxy.ContactsUrls.EDIT_GROUP)
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

	
	@DeleteMapping(value = PathProxy.ContactsUrls.DELETE_GROUP)
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

	
	@PostMapping(value = PathProxy.ContactsUrls.TOTAL_COUNT)
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

	
	@GetMapping(value = PathProxy.ContactsUrls.GET_ALL_GROUPS)
	@ApiOperation(value = PathProxy.ContactsUrls.GET_ALL_GROUPS, notes = PathProxy.ContactsUrls.GET_ALL_GROUPS)
	public Response<Object> getAllGroups(@RequestParam("page") int page, @RequestParam("size") int size,
			@RequestParam("doctorId") String doctorId, @RequestParam("locationId") String locationId,
			@RequestParam("hospitalId") String hospitalId,
			@DefaultValue("0") @RequestParam("updatedTime") String updatedTime,
			  @RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(locationId)&&DPDoctorUtils.anyStringEmpty(doctorId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<Object> response = contactsService.getAllGroups(page, size, doctorId, locationId, hospitalId, updatedTime,
				discarded);
		return response;
	}
	
	
	@PostMapping(value = PathProxy.ContactsUrls.ADD_GROUP_TO_PATIENT)
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
	}

	private String getFinalImageURL(String imageURL) {
		if (imageURL != null) {
			return imagePath + imageURL;
		} else
			return null;
	}

	
	@PostMapping(value = PathProxy.ContactsUrls.SEND_SMS_TO_GROUP)
	@ApiOperation(value = PathProxy.ContactsUrls.SEND_SMS_TO_GROUP, notes = PathProxy.ContactsUrls.SEND_SMS_TO_GROUP)
	public Response<String> sendSMSToGroup(BulkSMSRequest request) {
		String status = null;
		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		status = contactsService.sendSMSToGroup(request);
		Response<String> response = new Response<String>();
		response.setData(status);
		return response;
	}

	
	@PostMapping(value = PathProxy.ContactsUrls.ADD_BRANCH)
	@ApiOperation(value = PathProxy.ContactsUrls.ADD_BRANCH, notes = PathProxy.ContactsUrls.ADD_BRANCH)
	public Response<Branch> addEditBranch(Branch branch) {
		if (branch == null || DPDoctorUtils.anyStringEmpty(branch.getDoctorId())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Branch branchResponse = contactsService.addEditBranch(branch);
		Response<Branch> response = new Response<Branch>();
		response.setData(branchResponse);
		return response;
	}

	
	@GetMapping(value = PathProxy.ContactsUrls.GET_BRANCH_BY_ID)
	@ApiOperation(value = PathProxy.ContactsUrls.GET_BRANCH_BY_ID, notes = PathProxy.ContactsUrls.GET_BRANCH_BY_ID)
	public Response<Branch> getBranchById(@PathVariable("branchId") String branchId) {
		if (DPDoctorUtils.anyStringEmpty(branchId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Branch branchResponse = contactsService.getBranchById(branchId);
		Response<Branch> response = new Response<Branch>();
		response.setData(branchResponse);
		return response;
	}

	
	@DeleteMapping(value = PathProxy.ContactsUrls.DELETE_BRANCH)
	@ApiOperation(value = PathProxy.ContactsUrls.DELETE_BRANCH, notes = PathProxy.ContactsUrls.DELETE_BRANCH)
	public Response<Branch> deleteBranch(@PathVariable("branchId") String branchId,
			  @RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(branchId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Branch branchResponse = contactsService.deleteBranch(branchId, discarded);
		Response<Branch> response = new Response<Branch>();
		response.setData(branchResponse);
		return response;
	}

	
	@GetMapping(value = PathProxy.ContactsUrls.GET_BRANCHES)
	@ApiOperation(value = PathProxy.ContactsUrls.GET_BRANCHES, notes = PathProxy.ContactsUrls.GET_BRANCHES)
	public Response<Object> getBranches(@RequestParam("page") int page, @RequestParam("size") int size,
			@RequestParam("doctorId") String doctorId, @RequestParam("locationId") String locationId,
			@RequestParam("hospitalId") String hospitalId,
			@DefaultValue("0") @RequestParam("updatedTime") String updatedTime,
			  @RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded, @RequestParam("searchTerm") String searchTerm) {
		if (DPDoctorUtils.anyStringEmpty(locationId)&&DPDoctorUtils.anyStringEmpty(doctorId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<Object> response = contactsService.getBranches(page, size, doctorId, locationId, hospitalId, updatedTime,
				discarded, searchTerm);
		return response;
	}
	
	
	@PostMapping(value = PathProxy.ContactsUrls.GENERATE_DELIVERY_REPORT)
	@ApiOperation(value = PathProxy.ContactsUrls.GENERATE_DELIVERY_REPORT, notes = PathProxy.ContactsUrls.GENERATE_DELIVERY_REPORT)
	public Response<BulKMessage> generateDeliverReport(BulKMessage request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getRequestId())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		BulKMessage branchResponse = contactsService.generateDeliveryReport(request);
		Response<BulKMessage> response = new Response<BulKMessage>();
		response.setData(branchResponse);
		return response;
	}

	
	@GetMapping(value = PathProxy.ContactsUrls.GET_DELIVERY_REPORT)
	@ApiOperation(value = PathProxy.ContactsUrls.GET_DELIVERY_REPORT, notes = PathProxy.ContactsUrls.GET_DELIVERY_REPORT)
	public Response<Object> getDeliveryReports(@RequestParam("page") int page, @RequestParam("size") int size,
			@RequestParam("doctorId") String doctorId,
			@DefaultValue("0") @RequestParam("updatedTime") String updatedTime) {
		if (DPDoctorUtils.anyStringEmpty(doctorId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<Object> response = contactsService.getDeliveryReport(page, size, doctorId,updatedTime);
		return response;
	}
}
