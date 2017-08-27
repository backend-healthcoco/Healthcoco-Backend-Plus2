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

import com.dpdocter.beans.ConsentForm;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.request.AdmitCardRequest;
import com.dpdocter.request.DischargeSummaryRequest;
import com.dpdocter.response.AdmitCardResponse;
import com.dpdocter.response.DischargeSummaryResponse;
import com.dpdocter.services.AdmitCardService;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataParam;

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
	@Consumes({ MediaType.MULTIPART_FORM_DATA })
	@ApiOperation(value = PathProxy.AdmitCardUrls.ADD_ADMIT_CARD, notes = PathProxy.AdmitCardUrls.ADD_ADMIT_CARD)
	public Response<AdmitCardResponse> addEditAdmitCard(@FormDataParam("file") FormDataBodyPart file,
			@FormDataParam("data") FormDataBodyPart data) {
		Response<AdmitCardResponse> response = null;
		AdmitCardResponse admitCardResponse = null;
		data.setMediaType(MediaType.APPLICATION_JSON_TYPE);
		AdmitCardRequest request = data.getValueAs(AdmitCardRequest.class);
		if (request == null) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid input");
		}
		admitCardResponse = admitCardService.addEditAdmitcard(file, request);
		if (admitCardResponse != null) {
			response = new Response<AdmitCardResponse>();
			response.setData(admitCardResponse);
		}

		return response;
	}

	@Path(value = PathProxy.AdmitCardUrls.GET_ADMIT_CARDS)
	@GET
	@ApiOperation(value = PathProxy.AdmitCardUrls.GET_ADMIT_CARDS, notes = PathProxy.AdmitCardUrls.GET_ADMIT_CARDS)
	public Response<AdmitCardResponse> getAdmitCards(@QueryParam(value = "page") int page,
			@QueryParam(value = "size") int size, @QueryParam(value = "doctorId") String doctorId,
			@QueryParam(value = "locationId") String locationId, @QueryParam(value = "hospitalId") String hospitalId,
			@QueryParam(value = "patientId") String patientId, @QueryParam("updatedTime") long updatedTime,
			@DefaultValue("true") @QueryParam("discarded") Boolean discarded) {
		Response<AdmitCardResponse> response = null;
		List<AdmitCardResponse> admitCardResponses = null;

		if (DPDoctorUtils.anyStringEmpty(patientId, doctorId, locationId, hospitalId)) {
			throw new BusinessException(ServiceError.InvalidInput,
					"Doctor or patient id or locationId or hospitalId is null");
		}
		admitCardResponses = admitCardService.getAdmitCards(doctorId, locationId, hospitalId, patientId, page, size,
				updatedTime);
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
	public Response<AdmitCardResponse> deleteAdmitCard(@PathParam(value = "admitCardId") String admitCardId,
			@PathParam(value = "doctorId") String doctorId, @PathParam(value = "locationId") String locationId,
			@PathParam(value = "hospitalId") String hospitalId,
			@DefaultValue("true") @QueryParam("discarded") Boolean discarded) {
		if (StringUtils.isEmpty(admitCardId) || StringUtils.isEmpty(doctorId) || StringUtils.isEmpty(hospitalId)
				|| StringUtils.isEmpty(locationId)) {
			logger.warn("Admit card  Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Discharge Summery  Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
		}
		AdmitCardResponse admitCardResponse = admitCardService.deleteAdmitCard(admitCardId, doctorId, hospitalId,
				locationId, discarded);
		Response<AdmitCardResponse> response = new Response<AdmitCardResponse>();
		response.setData(admitCardResponse);
		return response;
	}

	@Path(value = PathProxy.AdmitCardUrls.DOWNLOAD_ADMIT_CARD)
	@GET
	@ApiOperation(value = PathProxy.AdmitCardUrls.DOWNLOAD_ADMIT_CARD, notes = PathProxy.AdmitCardUrls.DOWNLOAD_ADMIT_CARD)
	public Response<String> downloadDischargeSummary(@PathParam("admitCardId") String admitCardId) {
		Response<String> response = new Response<String>();

		return response;
	}

}
