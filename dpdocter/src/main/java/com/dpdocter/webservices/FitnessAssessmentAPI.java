package com.dpdocter.webservices;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import org.springframework.http.MediaType;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dpdocter.beans.FitnessAssessment;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.services.FitnessAssessmentService;
import com.dpdocter.services.TransactionalManagementService;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value=PathProxy.FITNESS_ASSESSMENT_BASE_URL,consumes =MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
@Api(value = PathProxy.FITNESS_ASSESSMENT_BASE_URL, description = "Endpoint for fitness Assessment")
public class FitnessAssessmentAPI {
	private static Logger logger = LogManager.getLogger(FitnessAssessmentAPI.class.getName());
	@Autowired
	private FitnessAssessmentService fitnessAssessmentService;

	@Autowired
	private TransactionalManagementService transnationalService;

	
	@DeleteMapping(value = PathProxy.FitnessUrls.DELETE_FITNESS_ASSESSMENT)
	@ApiOperation(value = PathProxy.FitnessUrls.DELETE_FITNESS_ASSESSMENT, notes = PathProxy.FitnessUrls.DELETE_FITNESS_ASSESSMENT)
	public Response<Boolean> deleteFitness(@PathVariable("id") String id,
			@RequestParam("discarded")   Boolean discarded) {

		if (DPDoctorUtils.anyStringEmpty(id)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		Response<Boolean> response = new Response<Boolean>();
		response.setData(fitnessAssessmentService.discardFitnessAssessment(id, discarded));
		return response;
	}

	
	@GetMapping(value = PathProxy.FitnessUrls.GET_FITNESS_ASSESSMENT)
	@ApiOperation(value = PathProxy.FitnessUrls.GET_FITNESS_ASSESSMENT, notes = PathProxy.FitnessUrls.GET_FITNESS_ASSESSMENT)
	public Response<FitnessAssessment> getFitnessAssessment(@PathVariable("doctorId") String doctorId,
			@PathVariable("locationId") String locationId, @PathVariable("hospitalId") String hospitalId,
			@PathVariable("patientId") String patientId, @RequestParam("size") int size, @RequestParam("page") int page,
			@RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded, @RequestParam("updatedTime") long updatedTime) {

		Response<FitnessAssessment> response = new Response<FitnessAssessment>();
		Integer count = fitnessAssessmentService.countFitnessAssessment(discarded, patientId);
		response.setDataList(fitnessAssessmentService.getFitnessAssessmentList(size, page, discarded, doctorId,
				locationId, hospitalId, patientId, updatedTime));
		response.setCount(count);
		return response;
	}

	
	@GetMapping(value = PathProxy.FitnessUrls.GET_FITNESS_ASSESSMENT_BY_ID)
	@ApiOperation(value = PathProxy.FitnessUrls.GET_FITNESS_ASSESSMENT_BY_ID, notes = PathProxy.FitnessUrls.GET_FITNESS_ASSESSMENT_BY_ID)
	public Response<FitnessAssessment> getFitnessAssessmentById(@PathVariable("id") String id) {
		if (DPDoctorUtils.anyStringEmpty(id)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		Response<FitnessAssessment> response = new Response<FitnessAssessment>();
		response.setData(fitnessAssessmentService.getFitnessAssessmentById(id));
		return response;
	}

	
	@PostMapping(value = PathProxy.FitnessUrls.ADD_EDIT_FITNESS_ASSESSMENT)
	@ApiOperation(value = PathProxy.FitnessUrls.ADD_EDIT_FITNESS_ASSESSMENT, notes = PathProxy.FitnessUrls.ADD_EDIT_FITNESS_ASSESSMENT)
	public Response<FitnessAssessment> addEditFitnessAssessment(@RequestBody FitnessAssessment request) {
		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		if (DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId(), request.getHospitalId(),
				request.getPatientId())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput,
					"doctorId,locationId,PatientIdor hospitalId should not be null or empty");

		}
		FitnessAssessment fitnessAssessment = fitnessAssessmentService.addEditFitnessAssessment(request);
		Response<FitnessAssessment> response = new Response<FitnessAssessment>();
		response.setData(fitnessAssessment);
		return response;
	}

	
	@GetMapping(value = PathProxy.FitnessUrls.DOWNLOAD_FITNESS_ASSESSMENT)
	@ApiOperation(value = PathProxy.FitnessUrls.DOWNLOAD_FITNESS_ASSESSMENT, notes = PathProxy.FitnessUrls.DOWNLOAD_FITNESS_ASSESSMENT)
	public Response<String> downloadFitnessAssessment(@PathVariable("fitnessId") String fitnessId) {
		if (DPDoctorUtils.anyStringEmpty(fitnessId)) {
			logger.error("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<String> response = new Response<String>();
		response.setData(fitnessAssessmentService.getFitnessAssessmentFile(fitnessId));
		return response;
	}
}
