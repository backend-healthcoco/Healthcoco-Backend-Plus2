package com.dpdocter.webservices.v2;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.response.v2.PatientVisitResponse;
import com.dpdocter.services.OTPService;
import com.dpdocter.services.v2.PatientVisitService;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController(value = "PatientVisitApiV2")
@RequestMapping(value=PathProxy.PATIENT_VISIT_BASE_URL,produces = MediaType.APPLICATION_JSON ,consumes = MediaType.APPLICATION_JSON)
@Api(value = PathProxy.PATIENT_VISIT_BASE_URL, description = "Endpoint for patient visit")
public class PatientVisitApi {

	private static Logger logger = LogManager.getLogger(PatientVisitApi.class.getName());

	@Autowired
	private PatientVisitService patientVisitService;

	@Autowired
	private OTPService otpService;

	@Value(value = "${image.path}")
	private String imagePath;


	
	@GetMapping(value = PathProxy.PatientVisitUrls.GET_VISITS)
	@ApiOperation(value = PathProxy.PatientVisitUrls.GET_VISITS, notes = PathProxy.PatientVisitUrls.GET_VISITS)
	public Response<PatientVisitResponse> getVisit(@PathVariable(value = "doctorId") String doctorId,
			@PathVariable(value = "locationId") String locationId, @PathVariable(value = "hospitalId") String hospitalId,
			@PathVariable(value = "patientId") String patientId, @RequestParam(value = "page") int page,
			@RequestParam(value = "size") int size, @DefaultValue("0") @RequestParam("updatedTime") String updatedTime,
			@RequestParam("visitFor") String visitFor, @RequestParam("from") String from,@RequestParam("to") String to,  @RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded) {

		if (DPDoctorUtils.anyStringEmpty(patientId, hospitalId, locationId)) {
			logger.warn("Patient Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Patient Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
		}
		if (doctorId.equalsIgnoreCase("null")) {
			doctorId = null;
		}
		List<PatientVisitResponse> patienVisitResponse = patientVisitService.getVisit(doctorId, locationId, hospitalId,
				patientId, page, size, otpService.checkOTPVerified(doctorId, locationId, hospitalId, patientId),
				updatedTime, visitFor, from,to,discarded);

		Response<PatientVisitResponse> response = new Response<PatientVisitResponse>();
		response.setDataList(patienVisitResponse);
		return response;
	}

	
	@GetMapping(value = PathProxy.PatientVisitUrls.GET_VISITS_FOR_WEB)
	@ApiOperation(value = PathProxy.PatientVisitUrls.GET_VISITS_FOR_WEB, notes = PathProxy.PatientVisitUrls.GET_VISITS_FOR_WEB)
	public Response<PatientVisitResponse> getVisitForWEB(@RequestParam(value = "doctorId") String doctorId,
			@RequestParam(value = "locationId") String locationId, @RequestParam(value = "hospitalId") String hospitalId,
			@RequestParam(value = "patientId") String patientId, @RequestParam(value = "page") int page,
			@RequestParam(value = "size") int size, @DefaultValue("0") @RequestParam("updatedTime") String updatedTime,
			@RequestParam("visitFor") String visitFor,@RequestParam("from") String from,@RequestParam("to") String to,   @RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded) {

		if (DPDoctorUtils.anyStringEmpty(patientId, hospitalId, locationId)) {
			logger.warn("Patient Id, Hospital Id, Location Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Patient Id, Hospital Id, Location Id Cannot Be Empty");
		}
		List<PatientVisitResponse> patienVisitResponse = patientVisitService.getVisit(doctorId, locationId, hospitalId,
				patientId, page, size, otpService.checkOTPVerified(doctorId, locationId, hospitalId, patientId),
				updatedTime, visitFor, from,to,discarded);

		Response<PatientVisitResponse> response = new Response<PatientVisitResponse>();
		response.setDataList(patienVisitResponse);
		return response;
	}

	

}
