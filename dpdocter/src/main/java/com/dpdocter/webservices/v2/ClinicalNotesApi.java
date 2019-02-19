package com.dpdocter.webservices.v2;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.dpdocter.beans.v2.ClinicalNotes;
import com.dpdocter.beans.v2.Diagram;
import com.dpdocter.services.OTPService;
import com.dpdocter.services.v2.ClinicalNotesService;

import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Component(value = "ClinicalNotesApiV2")
@Path(PathProxy.CLINICAL_NOTES_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.CLINICAL_NOTES_BASE_URL, description = "Endpoint for clinical notes")
public class ClinicalNotesApi {

	private static Logger logger = Logger.getLogger(ClinicalNotesApi.class.getName());

	@Autowired
	private ClinicalNotesService clinicalNotesService;

	@Autowired
	private OTPService otpService;

	@Value(value = "${image.path}")
	private String imagePath;

	
	@GET
	@ApiOperation(value = "GET_CLINICAL_NOTES", notes = "GET_CLINICAL_NOTES")
	public Response<ClinicalNotes> getNotes(@QueryParam("page") int page, @QueryParam("size") int size,
			@QueryParam(value = "doctorId") String doctorId, @QueryParam(value = "locationId") String locationId,
			@QueryParam(value = "hospitalId") String hospitalId, @QueryParam(value = "patientId") String patientId,
			@DefaultValue("0") @QueryParam("updatedTime") String updatedTime,
			@DefaultValue("true") @QueryParam(value = "discarded") Boolean discarded) {

		List<ClinicalNotes> clinicalNotes = clinicalNotesService.getClinicalNotes(page, size, doctorId, locationId,
				hospitalId, patientId, updatedTime,
				otpService.checkOTPVerified(doctorId, locationId, hospitalId, patientId), discarded, false);

		if (clinicalNotes != null && !clinicalNotes.isEmpty()) {
			for (ClinicalNotes clinicalNote : clinicalNotes) {
				if (clinicalNote.getDiagrams() != null && !clinicalNote.getDiagrams().isEmpty()) {
					clinicalNote.setDiagrams(getFinalDiagrams(clinicalNote.getDiagrams()));
				}
			}
		}
		Response<ClinicalNotes> response = new Response<ClinicalNotes>();
		response.setDataList(clinicalNotes);
		return response;
	}
	

	private List<Diagram> getFinalDiagrams(List<Diagram> diagrams) {
		for (Diagram diagram : diagrams) {
			if (diagram.getDiagramUrl() != null) {
				diagram.setDiagramUrl(getFinalImageURL(diagram.getDiagramUrl()));
			}
		}
		return diagrams;
	}

	private String getFinalImageURL(String imageURL) {
		if (imageURL != null) {
			return imagePath + imageURL;
		} else
			return null;
	}
}
