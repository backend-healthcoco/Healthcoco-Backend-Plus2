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
import com.dpdocter.request.InitialAssessmentRequest;
import com.dpdocter.response.InitialAssessmentResponse;
import com.dpdocter.services.InitialAssessmentService;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
/**
<<<<<<< HEAD
 * This api used for Initial assessment done by Doctor after admission  of patient(NABH)
=======
 * This api used for Initial assessment done by Doctor after admission  of patient
>>>>>>> f7852ab834d95ffaf379923fd546d7157fec6a12
 * 
 * @author Nikita
 *
 */
@Component
@Path(PathProxy.INITIAL_ASSESSMENT_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.INITIAL_ASSESSMENT_BASE_URL, description = "Endpoint for form")
public class InitialAssessmentApi {

	private static Logger logger = Logger.getLogger(InitialAssessmentApi.class.getName());
	
	@Autowired
	private InitialAssessmentService initialAssessmentService;

	@Path(value = PathProxy.InitialAssessmentsUrls.ADD_EDIT_ASSESSMENT_FORM)
	@POST
	@ApiOperation(value = PathProxy.InitialAssessmentsUrls.ADD_EDIT_ASSESSMENT_FORM, notes = PathProxy.InitialAssessmentsUrls.ADD_EDIT_ASSESSMENT_FORM)
	public Response<InitialAssessmentResponse> addEditInitialAssessmentForm(InitialAssessmentRequest request) {
		if (request == null || DPDoctorUtils.allStringsEmpty(request.getDoctorId(), request.getLocationId(),
				request.getHospitalId(), request.getPatientId())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input Patient Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
		}
		InitialAssessmentResponse initialAssessmentResponse = initialAssessmentService.addEditInitialAssessmentForm(request);
		Response<InitialAssessmentResponse> response = new Response<InitialAssessmentResponse>();
		response.setData(initialAssessmentResponse);
		return response;
	}
	
	@Path(value = PathProxy.InitialAssessmentsUrls.GET_ASSESSMENT_FORM)
	@GET
	@ApiOperation(value = PathProxy.InitialAssessmentsUrls.GET_ASSESSMENT_FORM,notes = PathProxy.InitialAssessmentsUrls.GET_ASSESSMENT_FORM)
	public Response<InitialAssessmentResponse> getInitialAssessmentForms(@PathParam(value = "patientId") String patientId,
			@QueryParam(value = "locationId") String locationId, @QueryParam(value = "hospitalId") String hospitalId,
			@QueryParam(value = "doctorId") String doctorId,@DefaultValue("0") @QueryParam(value = "page") int page,
			@DefaultValue("0") @QueryParam(value = "size") int size,
			@DefaultValue("false") @QueryParam("discarded") Boolean discarded){
		if (DPDoctorUtils.anyStringEmpty(patientId, hospitalId, locationId,doctorId)) {
			logger.warn("Patient Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Patient Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
		}
		List<InitialAssessmentResponse> initialAssessmentResponse = initialAssessmentService.getInitialAssessmentForm(doctorId,locationId,hospitalId,patientId,page,size,discarded);
		Response<InitialAssessmentResponse> response = new Response<InitialAssessmentResponse>();
		response.setDataList(initialAssessmentResponse);
		return response;
		
	}
	
	@Path(value = PathProxy.InitialAssessmentsUrls.GET_ASSESSMENT_FORM_BY_ID)
	@GET
	@ApiOperation(value = PathProxy.InitialAssessmentsUrls.GET_ASSESSMENT_FORM_BY_ID, notes = PathProxy.InitialAssessmentsUrls.GET_ASSESSMENT_FORM_BY_ID)
	public Response<InitialAssessmentResponse> getById(@PathParam("initialAssessmentId") String initialAssessmentId) {
		if (initialAssessmentId == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		Response<InitialAssessmentResponse> response = new Response<InitialAssessmentResponse>();
		response.setData(initialAssessmentService.getInitialAssessmentFormById(initialAssessmentId));
		return response;

	}
	@Path(value = PathProxy.InitialAssessmentsUrls.DELETE_ASSESSMENT_FORM)
	@DELETE
	@ApiOperation(value = PathProxy.InitialAssessmentsUrls.DELETE_ASSESSMENT_FORM, notes = PathProxy.InitialAssessmentsUrls.DELETE_ASSESSMENT_FORM)
	public Response<Boolean> deleteInitialAssessment(@PathParam(value = "initialAssessmentId") String initialAssessmentId,
			@PathParam(value = "doctorId") String doctorId, @PathParam(value = "locationId") String locationId,
			@PathParam(value = "hospitalId") String hospitalId,
			@DefaultValue("true") @QueryParam("discarded") Boolean discarded) {
		if (StringUtils.isEmpty(initialAssessmentId) || StringUtils.isEmpty(doctorId) || StringUtils.isEmpty(hospitalId)
				|| StringUtils.isEmpty(locationId)) {
			logger.warn("initialAssessmentId, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"initialAssessmentId, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
		}
		Boolean formResponse = initialAssessmentService.deleteInitialAssessment(initialAssessmentId, doctorId, hospitalId,
				locationId, discarded);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(formResponse);
		return response;
	}
}
