package com.dpdocter.webservices.v2;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.Produces;
import org.springframework.http.MediaType;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dpdocter.response.v2.AdmitCardResponse;
import com.dpdocter.services.v2.AdmitCardService;

import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;


@RestController(value = "AdmitCardAPIV2")
@RequestMapping(value=PathProxy.ADMIT_CARD_URL,produces = MediaType.APPLICATION_JSON_VALUE ,consumes = MediaType.APPLICATION_JSON_VALUE)
@Api(value = PathProxy.ADMIT_CARD_URL, description = "")
public class AdmitCardAPI {
	private Logger logger = LogManager.getLogger(AdmitCardAPI.class);

	@Autowired
	private AdmitCardService admitCardService;


	
	@GetMapping(value = PathProxy.AdmitCardUrls.GET_ADMIT_CARDS)
	@ApiOperation(value = PathProxy.AdmitCardUrls.GET_ADMIT_CARDS, notes = PathProxy.AdmitCardUrls.GET_ADMIT_CARDS)
	public Response<AdmitCardResponse> getAdmitCards(@RequestParam(value = "page") int page,
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
}
