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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.response.v2.PatientTreatmentResponse;
import com.dpdocter.services.OTPService;
import com.dpdocter.services.v2.PatientTreatmentServices;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController(value = "PatientTreamentAPIV2")
@RequestMapping(value=PathProxy.PATIENT_TREATMENT_BASE_URL,produces = MediaType.APPLICATION_JSON ,consumes = MediaType.APPLICATION_JSON)
@Api(value = PathProxy.PATIENT_TREATMENT_BASE_URL, description = "Endpoint for patient treatment")
public class PatientTreamentAPI {

	private static Logger logger = LogManager.getLogger(PatientTreamentAPI.class.getName());

	@Value(value = "${invalid.input}")
	private String invalidInput;

	@Autowired
	private PatientTreatmentServices patientTreatmentServices;

	@Autowired
	private OTPService otpService;
	
	@GetMapping
	@ApiOperation(value = "GET_PATIENT_TREATMENTS", notes = "GET_PATIENT_TREATMENTS")
	public Response<PatientTreatmentResponse> getPatientTreatments(@RequestParam("page") int page,
			@RequestParam("size") int size, @RequestParam("locationId") String locationId,
			@RequestParam("hospitalId") String hospitalId, @RequestParam("doctorId") String doctorId,
			@RequestParam("patientId") String patientId, @DefaultValue("0") @RequestParam("updatedTime") String updatedTime,
			  @RequestParam(value = "discarded") Boolean discarded,@RequestParam("from") String from,@RequestParam("to") String to,
			@RequestParam(value = "status") String status) {
		if (DPDoctorUtils.anyStringEmpty(locationId)) {
			logger.warn(invalidInput);
			throw new BusinessException(ServiceError.InvalidInput, invalidInput);
		}

		List<PatientTreatmentResponse> patientTreatmentResponses = patientTreatmentServices.getPatientTreatments(page,
				size, doctorId, locationId, hospitalId, patientId, updatedTime,
				otpService.checkOTPVerified(doctorId, locationId, hospitalId, patientId), from,to,discarded, false, status);

		Response<PatientTreatmentResponse> response = new Response<PatientTreatmentResponse>();
		response.setDataList(patientTreatmentResponses);
		return response;
	}

	
}
