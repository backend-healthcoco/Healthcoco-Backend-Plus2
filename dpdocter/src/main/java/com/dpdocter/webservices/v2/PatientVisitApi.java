package com.dpdocter.webservices.v2;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.response.v2.PatientVisitResponse;
import com.dpdocter.services.OTPService;
import com.dpdocter.services.v2.PatientVisitService;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Component(value = "PatientVisitApiV2")
@Path(PathProxy.PATIENT_VISIT_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.PATIENT_VISIT_BASE_URL, description = "Endpoint for patient visit")
public class PatientVisitApi {

	private static Logger logger = Logger.getLogger(PatientVisitApi.class.getName());

	@Autowired
	private PatientVisitService patientVisitService;

	@Autowired
	private OTPService otpService;

	@Value(value = "${image.path}")
	private String imagePath;


	@Path(value = PathProxy.PatientVisitUrls.GET_VISITS)
	@GET
	@ApiOperation(value = PathProxy.PatientVisitUrls.GET_VISITS, notes = PathProxy.PatientVisitUrls.GET_VISITS)
	public Response<PatientVisitResponse> getVisit(@PathParam(value = "doctorId") String doctorId,
			@PathParam(value = "locationId") String locationId, @PathParam(value = "hospitalId") String hospitalId,
			@PathParam(value = "patientId") String patientId, @QueryParam(value = "page") int page,
			@QueryParam(value = "size") int size, @DefaultValue("0") @QueryParam("updatedTime") String updatedTime,
			@QueryParam("visitFor") String visitFor, @DefaultValue("true") @QueryParam("discarded") Boolean discarded) {

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
				updatedTime, visitFor, discarded);

		Response<PatientVisitResponse> response = new Response<PatientVisitResponse>();
		response.setDataList(patienVisitResponse);
		return response;
	}

	@Path(value = PathProxy.PatientVisitUrls.GET_VISITS_FOR_WEB)
	@GET
	@ApiOperation(value = PathProxy.PatientVisitUrls.GET_VISITS_FOR_WEB, notes = PathProxy.PatientVisitUrls.GET_VISITS_FOR_WEB)
	public Response<PatientVisitResponse> getVisitForWEB(@QueryParam(value = "doctorId") String doctorId,
			@QueryParam(value = "locationId") String locationId, @QueryParam(value = "hospitalId") String hospitalId,
			@QueryParam(value = "patientId") String patientId, @QueryParam(value = "page") int page,
			@QueryParam(value = "size") int size, @DefaultValue("0") @QueryParam("updatedTime") String updatedTime,
			@QueryParam("visitFor") String visitFor, @DefaultValue("true") @QueryParam("discarded") Boolean discarded) {

		if (DPDoctorUtils.anyStringEmpty(patientId, hospitalId, locationId)) {
			logger.warn("Patient Id, Hospital Id, Location Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Patient Id, Hospital Id, Location Id Cannot Be Empty");
		}
		List<PatientVisitResponse> patienVisitResponse = patientVisitService.getVisit(doctorId, locationId, hospitalId,
				patientId, page, size, otpService.checkOTPVerified(doctorId, locationId, hospitalId, patientId),
				updatedTime, visitFor, true);

		Response<PatientVisitResponse> response = new Response<PatientVisitResponse>();
		response.setDataList(patienVisitResponse);
		return response;
	}

	

}
