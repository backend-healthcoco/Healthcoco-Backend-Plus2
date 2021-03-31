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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
@RequestMapping(value=PathProxy.ADMIT_CARD_URL,produces = MediaType.APPLICATION_JSON ,consumes = MediaType.APPLICATION_JSON)
@Api(value = PathProxy.ADMIT_CARD_URL, description = "")
public class AdmitCardAPI {
	private Logger logger = LogManager.getLogger(AdmitCardAPI.class);

	@Autowired
	private AdmitCardService admitCardService;

	@PostMapping(value = PathProxy.AdmitCardUrls.ADD_ADMIT_CARD)
	@ApiOperation(value = PathProxy.AdmitCardUrls.ADD_ADMIT_CARD, notes = PathProxy.AdmitCardUrls.ADD_ADMIT_CARD)
	public Response<AdmitCardResponse> addEditAdmitCard(@RequestBody AdmitCardRequest request) {
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

	@GetMapping(value = PathProxy.AdmitCardUrls.GET_ADMIT_CARDS)
	@ApiOperation(value = PathProxy.AdmitCardUrls.GET_ADMIT_CARDS, notes = PathProxy.AdmitCardUrls.GET_ADMIT_CARDS)
	public Response<AdmitCardResponse> getAdmitCards(@RequestParam(value = "page") long page,
			@RequestParam(value = "size") int size, @RequestParam(value = "doctorId") String doctorId,
			@RequestParam(value = "locationId") String locationId, @RequestParam(value = "hospitalId") String hospitalId,
			@RequestParam(value = "patientId") String patientId, @RequestParam("updatedTime") long updatedTime,
			@DefaultValue("false") @RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded) {
		Response<AdmitCardResponse> response = null;
		List<AdmitCardResponse> admitCardResponses = null;

		admitCardResponses = admitCardService.getAdmitCards(doctorId, locationId, hospitalId, patientId, page, size,
				updatedTime, discarded);
		response = new Response<AdmitCardResponse>();
		response.setDataList(admitCardResponses);

		return response;

	}

	@GetMapping(value = PathProxy.AdmitCardUrls.VIEW_ADMIT_CARD)
	@ApiOperation(value = PathProxy.AdmitCardUrls.VIEW_ADMIT_CARD, notes = PathProxy.AdmitCardUrls.VIEW_ADMIT_CARD)
	public Response<AdmitCardResponse> viewAdmitCard(@PathVariable("admitCardId") String admitCardId) {
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

	@DeleteMapping(value = PathProxy.AdmitCardUrls.DELETE_ADMIT_CARD)
	@ApiOperation(value = PathProxy.AdmitCardUrls.DELETE_ADMIT_CARD, notes = PathProxy.AdmitCardUrls.DELETE_ADMIT_CARD)
	public Response<AdmitCardResponse> deleteAdmitCard(@PathVariable(value = "admitCardId") String admitCardId,
			@PathVariable(value = "doctorId") String doctorId, @PathVariable(value = "locationId") String locationId,
			@PathVariable(value = "hospitalId") String hospitalId,
			  @RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded) {
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

	@GetMapping(value = PathProxy.AdmitCardUrls.DOWNLOAD_ADMIT_CARD)
	@ApiOperation(value = PathProxy.AdmitCardUrls.DOWNLOAD_ADMIT_CARD, notes = PathProxy.AdmitCardUrls.DOWNLOAD_ADMIT_CARD)
	public Response<String> downloadAdmitCard(@PathVariable("admitCardId") String admitCardId) {
		Response<String> response = new Response<String>();
		response.setData(admitCardService.downloadDischargeSummary(admitCardId));
		return response;
	}

	@GetMapping(value = PathProxy.AdmitCardUrls.EMAIL_ADMIT_CARD)
	@ApiOperation(value = PathProxy.AdmitCardUrls.EMAIL_ADMIT_CARD, notes = PathProxy.AdmitCardUrls.EMAIL_ADMIT_CARD)
	public Response<Boolean> emailAdmitCard(@PathVariable(value = "admitCardId") String admitCardId,
			@PathVariable(value = "doctorId") String doctorId, @PathVariable(value = "locationId") String locationId,
			@PathVariable(value = "hospitalId") String hospitalId,
			@PathVariable(value = "emailAddress") String emailAddress) {
		admitCardService.emailAdmitCard(admitCardId, doctorId, locationId, hospitalId, emailAddress);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(true);
		return response;
	}
	
	@GetMapping(value = PathProxy.AdmitCardUrls.EMAIL_ADMIT_CARD_WEB)
	@ApiOperation(value = PathProxy.AdmitCardUrls.EMAIL_ADMIT_CARD_WEB, notes = PathProxy.AdmitCardUrls.EMAIL_ADMIT_CARD_WEB)
	public Response<Boolean> emailAdmitCardForWeb(@PathVariable(value = "admitCardId") String admitCardId,
			@RequestParam(value = "doctorId") String doctorId, @RequestParam(value = "locationId") String locationId,
			@RequestParam(value = "hospitalId") String hospitalId,
			@PathVariable(value = "emailAddress") String emailAddress) {
		admitCardService.emailAdmitCardForWeb(admitCardId, doctorId, locationId, hospitalId, emailAddress);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(true);
		return response;
	}

}
