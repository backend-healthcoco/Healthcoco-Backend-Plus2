package com.dpdocter.webservices.v2;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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

import java.time.Period;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component(value = "ContactsApiV2")
@Path(PathProxy.CONTACTS_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.CONTACTS_BASE_URL, description = "Endpoint for version 2 contacts")
public class ContactsApi {

	private static Logger logger = Logger.getLogger(ContactsApi.class.getName());

	@Autowired
	private ContactsService contactsService;

	@Autowired
	private PatientVisitService patientTrackService;

	@Value(value = "${image.path}")
	private String imagePath;

	@POST
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
				// patientCard.setImageUrl(getFinalImageURL(patientCard.getImageUrl()));
				patientCard.setThumbnailUrl(getFinalImageURL(patientCard.getThumbnailUrl()));
			}
		}
		Response<DoctorContactsResponse> response = new Response<DoctorContactsResponse>();
		response.setData(doctorContactsResponse);
		return response;
	}

	@Path(value = PathProxy.ContactsUrls.DOCTOR_CONTACTS_DOCTOR_SPECIFIC)
	@GET
	@ApiOperation(value = PathProxy.ContactsUrls.DOCTOR_CONTACTS_DOCTOR_SPECIFIC, notes = PathProxy.ContactsUrls.DOCTOR_CONTACTS_DOCTOR_SPECIFIC)
	public Response<DoctorContactsResponse> getDoctorContacts(@PathParam("type") String type,
			@QueryParam("page") int page, @QueryParam("size") int size, @QueryParam(value = "doctorId") String doctorId,
			@QueryParam("locationId") String locationId, @QueryParam("hospitalId") String hospitalId,
			@DefaultValue("0") @QueryParam("updatedTime") String updatedTime,
			@DefaultValue("true") @QueryParam("discarded") Boolean discarded, @QueryParam("role") String role) {

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
				// patientCard.setImageUrl(getFinalImageURL(patientCard.getImageUrl()));
				patientCard.setThumbnailUrl(getFinalImageURL(patientCard.getThumbnailUrl()));

				// calculate age of patient upto today
				if (patientCard.getDob() != null) {

					if (patientCard.getDob().getDays() > 0 && patientCard.getDob().getMonths() > 0
							&& patientCard.getDob().getYears() > 0) {
						DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yyyy");
						LocalDate today = LocalDate.now();
						LocalDate birthday = LocalDate.parse(patientCard.getDob().getDays() + "/"
								+ patientCard.getDob().getMonths() + "/" + patientCard.getDob().getYears(), formatter);

						Period p = Period.between(birthday, today);

						patientCard.getDob().getAge().setDays(p.getDays());
						patientCard.getDob().getAge().setMonths(p.getMonths());
						patientCard.getDob().getAge().setYears(p.getYears());
					}
				}
			}
		}

		Response<DoctorContactsResponse> response = new Response<DoctorContactsResponse>();
		response.setData(doctorContactsResponse);

		return response;

	}

	@Path(value = PathProxy.ContactsUrls.DOCTOR_CONTACTS_HANDHELD)
	@GET
	@ApiOperation(value = PathProxy.ContactsUrls.DOCTOR_CONTACTS_HANDHELD, notes = PathProxy.ContactsUrls.DOCTOR_CONTACTS_HANDHELD)
	public Response<Object> getDoctorContactsHandheld(@QueryParam(value = "doctorId") String doctorId,
			@QueryParam(value = "locationId") String locationId, @QueryParam(value = "hospitalId") String hospitalId,
			@DefaultValue("0") @QueryParam(value = "updatedTime") String updatedTime,
			@DefaultValue("true") @QueryParam(value = "discarded") Boolean discarded, @QueryParam("role") String role,
			@QueryParam("page") int page, @QueryParam("size") int size, @QueryParam("searchTerm") String searchTerm) {

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

	private String getFinalImageURL(String imageURL) {
		if (imageURL != null) {
			return imagePath + imageURL;
		} else
			return null;
	}

	@Path(value = PathProxy.ContactsUrls.SEARCH_PATIENT)
	@GET
	@ApiOperation(value = PathProxy.ContactsUrls.SEARCH_PATIENT, notes = PathProxy.ContactsUrls.SEARCH_PATIENT)
	public Response<DoctorContactsResponse> searchPatient(@PathParam(value = "locationId") String locationId,
			@PathParam(value = "hospitalId") String hospitalId, @PathParam(value = "searchTerm") String searchTerm,
			@QueryParam("page") int page, @QueryParam("size") int size, @QueryParam("doctorId") String doctorId,
			@QueryParam("role") String role) {
		if (DPDoctorUtils.anyStringEmpty(locationId, hospitalId, searchTerm)) {
			logger.warn("Location Id, Hospital Id and Search Term Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Location Id, Hospital Id and Search Term Cannot Be Empty");
		}
		DoctorContactsResponse patients = contactsService.searchPatient(locationId, hospitalId, searchTerm, page, size,
				doctorId, role);

		Response<DoctorContactsResponse> response = new Response<DoctorContactsResponse>();
		response.setData(patients);
		return response;
	}
}
