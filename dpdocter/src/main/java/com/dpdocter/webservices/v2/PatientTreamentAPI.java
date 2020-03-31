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

import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.response.v2.PatientTreatmentResponse;
import com.dpdocter.services.OTPService;
import com.dpdocter.services.v2.PatientTreatmentServices;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Component(value = "PatientTreamentAPIV2")
@Path(PathProxy.PATIENT_TREATMENT_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.PATIENT_TREATMENT_BASE_URL, description = "Endpoint for patient treatment")
public class PatientTreamentAPI {

	private static Logger logger = Logger.getLogger(PatientTreamentAPI.class.getName());

	@Value(value = "${invalid.input}")
	private String invalidInput;

	@Autowired
	private PatientTreatmentServices patientTreatmentServices;

	@Autowired
	private OTPService otpService;
	
	@GET
	@ApiOperation(value = "GET_PATIENT_TREATMENTS", notes = "GET_PATIENT_TREATMENTS")
	public Response<PatientTreatmentResponse> getPatientTreatments(@QueryParam("page") int page,
			@QueryParam("size") int size, @QueryParam("locationId") String locationId,
			@QueryParam("hospitalId") String hospitalId, @QueryParam("doctorId") String doctorId,
			@QueryParam("patientId") String patientId, @DefaultValue("0") @QueryParam("updatedTime") String updatedTime,
			@DefaultValue("true") @QueryParam(value = "discarded") Boolean discarded,@QueryParam("from") String from,@QueryParam("to") String to,
			@QueryParam(value = "status") String status) {
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
