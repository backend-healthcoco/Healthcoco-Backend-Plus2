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
import org.springframework.stereotype.Component;

import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.request.PreOperationAssessmentRequest;
import com.dpdocter.response.InitialAdmissionResponse;
import com.dpdocter.response.PreOperationAssessmentResponse;
import com.dpdocter.services.InitialAssessmentService;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * This api used for get patient health details before operation
 * @author dell
 *
 */

@Component
@Path(PathProxy.PREOPERATION_ASSESSMENT_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.PREOPERATION_ASSESSMENT_BASE_URL, description = "Endpoint for form")
public class PreOperationAssessmentApi {

private static Logger logger = Logger.getLogger(PreOperationAssessmentApi.class.getName());
	
	@Autowired
	private InitialAssessmentService initialAssessmentService;

	@Path(value = PathProxy.PreOprationAssessmentsUrls.ADD_EDIT_PREOPERATION_FORM)
	@POST
	@ApiOperation(value = PathProxy.PreOprationAssessmentsUrls.ADD_EDIT_PREOPERATION_FORM, notes = PathProxy.PreOprationAssessmentsUrls.ADD_EDIT_PREOPERATION_FORM)
	public Response<PreOperationAssessmentResponse> addEditPreOperationAssessmentForm(PreOperationAssessmentRequest request) {
		if (request == null || DPDoctorUtils.allStringsEmpty(request.getDoctorId(), request.getLocationId(),
				request.getHospitalId(), request.getPatientId())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input Patient Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
		}
		PreOperationAssessmentResponse preOperationAssessmentResponse = initialAssessmentService.addEditPreOperationAssessmentForm(request);
		Response<PreOperationAssessmentResponse> response = new Response<PreOperationAssessmentResponse>();
		response.setData(preOperationAssessmentResponse);
		return response;
	}
	
	@Path(value = PathProxy.PreOprationAssessmentsUrls.GET_PREOPERATION_FORM)
	@GET
	@ApiOperation(value = PathProxy.PreOprationAssessmentsUrls.GET_PREOPERATION_FORM,notes = PathProxy.PreOprationAssessmentsUrls.GET_PREOPERATION_FORM)
	public Response<PreOperationAssessmentResponse> getAdmissionAssessmentForms(@PathParam(value = "patientId") String patientId,
			@QueryParam(value = "locationId") String locationId, @QueryParam(value = "hospitalId") String hospitalId,
			@QueryParam(value = "doctorId") String doctorId,@DefaultValue("0") @QueryParam(value = "page") int page,
			@DefaultValue("0") @QueryParam(value = "size") int size,
			@DefaultValue("false") @QueryParam("discarded") Boolean discarded){
		if (DPDoctorUtils.anyStringEmpty(patientId, hospitalId, locationId,doctorId)) {
			logger.warn("Patient Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Patient Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
		}
		List<PreOperationAssessmentResponse> preOperationAssessmentResponse = initialAssessmentService.getPreOperationAssessmentForms(doctorId,locationId,hospitalId,patientId,page,size,discarded);
		Response<PreOperationAssessmentResponse> response = new Response<PreOperationAssessmentResponse>();
		response.setDataList(preOperationAssessmentResponse);
		return response;
	}
	@Path(value = PathProxy.PreOprationAssessmentsUrls.GET_PREOPERATION_FORM_BY_ID)
	@GET
	@ApiOperation(value = PathProxy.PreOprationAssessmentsUrls.GET_PREOPERATION_FORM_BY_ID, notes = PathProxy.PreOprationAssessmentsUrls.GET_PREOPERATION_FORM_BY_ID)
	public Response<PreOperationAssessmentResponse> getById(@PathParam("preOperationFormId") String preOperationFormId) {
		if (preOperationFormId == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		Response<PreOperationAssessmentResponse> response = new Response<PreOperationAssessmentResponse>();
		response.setData(initialAssessmentService.getPreOprationFormById(preOperationFormId));
		return response;

	}
	@Path(value = PathProxy.PreOprationAssessmentsUrls.DELETE_PREOPERATION_FORM)
	@DELETE
	@ApiOperation(value = PathProxy.PreOprationAssessmentsUrls.DELETE_PREOPERATION_FORM, notes = PathProxy.PreOprationAssessmentsUrls.DELETE_PREOPERATION_FORM)
	public Response<Boolean> deletePreOperationForm(@PathParam(value = "preOperationFormId") String preOperationFormId,
			@PathParam(value = "doctorId") String doctorId, @PathParam(value = "locationId") String locationId,
			@PathParam(value = "hospitalId") String hospitalId,
			@DefaultValue("true") @QueryParam("discarded") Boolean discarded) {
		if (StringUtils.isEmpty(preOperationFormId) || StringUtils.isEmpty(doctorId) || StringUtils.isEmpty(hospitalId)
				|| StringUtils.isEmpty(locationId)) {
			logger.warn("preOperationFormId, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"preOperationFormId, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
		}
		Boolean formResponse = initialAssessmentService.deletePreOperationForm(preOperationFormId, doctorId, hospitalId,
				locationId, discarded);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(formResponse);
		return response;
	}
	
	@Path(value = PathProxy.PreOprationAssessmentsUrls.DOWNLOAD_PREOPERATION_FORM_BY_ID)
	@GET
	@ApiOperation(value = PathProxy.PreOprationAssessmentsUrls.DOWNLOAD_PREOPERATION_FORM_BY_ID, notes = PathProxy.PreOprationAssessmentsUrls.DOWNLOAD_PREOPERATION_FORM_BY_ID)
	public Response<String> downloadPreOprationFormById(@PathParam("preOperationFormId") String preOperationFormId) {
		if (preOperationFormId == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		Response<String> response = new Response<String>();
		response.setData(initialAssessmentService.downloadPreOprationFormById(preOperationFormId));
		return response;

	}
}
