package com.dpdocter.webservices.v2;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;

import com.dpdocter.response.v2.AdmitCardResponse;
import com.dpdocter.services.v2.AdmitCardService;

import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@Component(value = "AdmitCardAPIV2")
@Path(PathProxy.ADMIT_CARD_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.ADMIT_CARD_URL, description = "")
public class AdmitCardAPI {

	@Autowired
	private AdmitCardService admitCardService;


	@Path(value = PathProxy.AdmitCardUrls.GET_ADMIT_CARDS)
	@GET
	@ApiOperation(value = PathProxy.AdmitCardUrls.GET_ADMIT_CARDS, notes = PathProxy.AdmitCardUrls.GET_ADMIT_CARDS)
	public Response<AdmitCardResponse> getAdmitCards(@QueryParam(value = "page") int page,
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
}
