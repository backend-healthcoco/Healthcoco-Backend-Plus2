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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dpdocter.beans.v2.ClinicalNotes;
import com.dpdocter.beans.v2.Diagram;
import com.dpdocter.services.OTPService;
import com.dpdocter.services.v2.ClinicalNotesService;

import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController(value = "ClinicalNotesApiV2")
@RequestMapping(value=PathProxy.CLINICAL_NOTES_BASE_URL,produces = MediaType.APPLICATION_JSON_VALUE ,consumes = MediaType.APPLICATION_JSON_VALUE)
@Api(value = PathProxy.CLINICAL_NOTES_BASE_URL, description = "Endpoint for clinical notes")
public class ClinicalNotesApi {

	private static Logger logger = LogManager.getLogger(ClinicalNotesApi.class.getName());

	@Autowired
	private ClinicalNotesService clinicalNotesService;

	@Autowired
	private OTPService otpService;

	@Value(value = "${image.path}")
	private String imagePath;

	
	@GetMapping
	@ApiOperation(value = "GET_CLINICAL_NOTES", notes = "GET_CLINICAL_NOTES")
	public Response<ClinicalNotes> getNotes(@RequestParam("page") int page, @RequestParam("size") int size,
			@RequestParam(value = "doctorId") String doctorId, @RequestParam(value = "locationId") String locationId,
			@RequestParam(value = "hospitalId") String hospitalId, @RequestParam(value = "patientId") String patientId,
			@DefaultValue("0") @RequestParam("updatedTime") String updatedTime,@RequestParam("from") String from,@RequestParam("to") String to,
			  @RequestParam(value = "discarded") Boolean discarded) {

		List<ClinicalNotes> clinicalNotes = clinicalNotesService.getClinicalNotes(page, size, doctorId, locationId,
				hospitalId, patientId, updatedTime,
				otpService.checkOTPVerified(doctorId, locationId, hospitalId, patientId), from,to,discarded, false);

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
