package com.dpdocter.webservices;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.request.AdmitCardRequest;
import com.dpdocter.response.AdmitCardResponse;
import com.dpdocter.services.AdmitCardService;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@Path(PathProxy.ADMIT_CARD_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.ADMIT_CARD_URL, description = "")
public class AdmitCardAPI {
	private Logger logger = Logger.getLogger(AdmitCardAPI.class);

	@Autowired
	private AdmitCardService admitCardService;

	@POST
	@Path(value = PathProxy.AdmitCardUrls.ADD_ADMIT_CARD)
	@ApiOperation(value = PathProxy.AdmitCardUrls.ADD_ADMIT_CARD, notes = PathProxy.AdmitCardUrls.ADD_ADMIT_CARD)
	public Response<AdmitCardResponse> addEditAdmitCard(AdmitCardRequest request) {
		Response<AdmitCardResponse> response = null;
		AdmitCardResponse admitCardResponse = null;

		if (request == null) {

			throw new BusinessException(ServiceError.InvalidInput, "Invalid input");

		}
		if (DPDoctorUtils.allStringsEmpty(request.getDoctorId(), request.getLocationId(), request.getHospitalId(),
				request.getPatientId())) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid input");
		}
		admitCardResponse = admitCardService.addEditAdmitcard(request);
		if (admitCardResponse != null) {
			response = new Response<AdmitCardResponse>();
			response.setData(admitCardResponse);
		}

		return response;
	}

	@Path(value = PathProxy.AdmitCardUrls.GET_ADMIT_CARDS)
	@GET
	@ApiOperation(value = PathProxy.AdmitCardUrls.GET_ADMIT_CARDS, notes = PathProxy.AdmitCardUrls.GET_ADMIT_CARDS)
	public Response<AdmitCardResponse> getAdmitCards(@QueryParam(value = "page") long page,
			@QueryParam(value = "size") int size, @QueryParam(value = "doctorId") String doctorId,
			@QueryParam(value = "locationId") String locationId, @QueryParam(value = "hospitalId") String hospitalId,
			@QueryParam(value = "patientId") String patientId, @QueryParam("updatedTime") long updatedTime,
			@DefaultValue("false") @QueryParam("discarded") Boolean discarded) {
		Response<AdmitCardResponse> response = null;
		List<AdmitCardResponse> admitCardResponses = null;

		admitCardResponses = admitCardService.getAdmitCards(doctorId, locationId, hospitalId, patientId, page, size,
				updatedTime, discarded);
		response = new Response<AdmitCardResponse>();
		response.setDataList(admitCardResponses);

		return response;

	}

	@Path(value = PathProxy.AdmitCardUrls.VIEW_ADMIT_CARD)
	@GET
	@ApiOperation(value = PathProxy.AdmitCardUrls.VIEW_ADMIT_CARD, notes = PathProxy.AdmitCardUrls.VIEW_ADMIT_CARD)
	public Response<AdmitCardResponse> viewAdmitCard(@PathParam("admitCardId") String admitCardId) {
		Response<AdmitCardResponse> response = null;
		AdmitCardResponse admitCardResponse = null;

		if (admitCardId == null) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid input");
		}

		admitCardResponse = admitCardService.getAdmitCard(admitCardId);

		response = new Response<AdmitCardResponse>();
		response.setData(admitCardResponse);

		return response;
	}

	@Path(value = PathProxy.AdmitCardUrls.DELETE_ADMIT_CARD)
	@DELETE
	@ApiOperation(value = PathProxy.AdmitCardUrls.DELETE_ADMIT_CARD, notes = PathProxy.AdmitCardUrls.DELETE_ADMIT_CARD)
	public Response<Boolean> deleteAdmitCard(@PathParam(value = "admitCardId") String admitCardId,
			@PathParam(value = "doctorId") String doctorId, @PathParam(value = "locationId") String locationId,
			@PathParam(value = "hospitalId") String hospitalId,
			@DefaultValue("true") @QueryParam("discarded") Boolean discarded) {
		if (StringUtils.isEmpty(admitCardId) || StringUtils.isEmpty(doctorId) || StringUtils.isEmpty(hospitalId)
				|| StringUtils.isEmpty(locationId)) {
			logger.warn("Admit card  Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Discharge Summery  Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
		}
		Boolean admitCardResponse = admitCardService.deleteAdmitCard(admitCardId, doctorId, hospitalId,
				locationId, discarded);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(admitCardResponse);
		return response;
	}

	@Path(value = PathProxy.AdmitCardUrls.DOWNLOAD_ADMIT_CARD)
	@GET
	@ApiOperation(value = PathProxy.AdmitCardUrls.DOWNLOAD_ADMIT_CARD, notes = PathProxy.AdmitCardUrls.DOWNLOAD_ADMIT_CARD)
	public Response<String> downloadAdmitCard(@PathParam("admitCardId") String admitCardId) {
		Response<String> response = new Response<String>();
		response.setData(admitCardService.downloadDischargeSummary(admitCardId));
		return response;
	}

	@Path(value = PathProxy.AdmitCardUrls.EMAIL_ADMIT_CARD)
	@GET
	@ApiOperation(value = PathProxy.AdmitCardUrls.EMAIL_ADMIT_CARD, notes = PathProxy.AdmitCardUrls.EMAIL_ADMIT_CARD)
	public Response<Boolean> emailAdmitCard(@PathParam(value = "admitCardId") String admitCardId,
			@PathParam(value = "doctorId") String doctorId, @PathParam(value = "locationId") String locationId,
			@PathParam(value = "hospitalId") String hospitalId,
			@PathParam(value = "emailAddress") String emailAddress) {
		admitCardService.emailAdmitCard(admitCardId, doctorId, locationId, hospitalId, emailAddress);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(true);
		return response;
	}
	
	@Path(value = PathProxy.AdmitCardUrls.EMAIL_ADMIT_CARD_WEB)
	@GET
	@ApiOperation(value = PathProxy.AdmitCardUrls.EMAIL_ADMIT_CARD_WEB, notes = PathProxy.AdmitCardUrls.EMAIL_ADMIT_CARD_WEB)
	public Response<Boolean> emailAdmitCardForWeb(@PathParam(value = "admitCardId") String admitCardId,
			@QueryParam(value = "doctorId") String doctorId, @QueryParam(value = "locationId") String locationId,
			@QueryParam(value = "hospitalId") String hospitalId,
			@PathParam(value = "emailAddress") String emailAddress) {
		admitCardService.emailAdmitCardForWeb(admitCardId, doctorId, locationId, hospitalId, emailAddress);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(true);
		return response;
	}

}
