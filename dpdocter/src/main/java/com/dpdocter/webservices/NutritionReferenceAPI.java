package com.dpdocter.webservices;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.Produces;
import org.springframework.http.MediaType;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dpdocter.beans.NutritionGoalAnalytics;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.request.AddEditNutritionReferenceRequest;
import com.dpdocter.response.NutritionReferenceResponse;
import com.dpdocter.services.NutritionReferenceService;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
(PathProxy.NUTRITION_REFERENCE_BASE_URL)
@Produces(MediaType.APPLICATION_JSON_VALUE)
@Consumes(MediaType.APPLICATION_JSON_VALUE)
@Api(value = PathProxy.NUTRITION_REFERENCE_BASE_URL, description = "Endpoint for nutrition Referenceapi's")
public class NutritionReferenceAPI {
	private static Logger logger = LogManager.getLogger(NutritionReferenceAPI.class.getName());

	@Autowired
	private NutritionReferenceService nutritionReferenceService;

	@PostMapping
	(PathProxy.NutritionReferenceUrl.ADD_EDIT_NUTRITION_REFERENCE)
	@ApiOperation(value = PathProxy.NutritionReferenceUrl.ADD_EDIT_NUTRITION_REFERENCE)
	public Response<NutritionReferenceResponse> addEditNutritionResponse(AddEditNutritionReferenceRequest request) {
		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		if (DPDoctorUtils.anyStringEmpty(request.getMobileNumber(), request.getLocalPatientName(),
				request.getDoctorId(), request.getPatientId(), request.getLocationId(), request.getHospitalId())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput,
					"mobileNumber,localPatientName,doctorId,PatientId should not empty or null ");
		}
		Response<NutritionReferenceResponse> response = new Response<>();
		NutritionReferenceResponse nutritionReferenceResponse = null;

		nutritionReferenceResponse = nutritionReferenceService.addEditNutritionReference(request);
		response.setData(nutritionReferenceResponse);
		return response;
	}

	
	@GetMapping(value = PathProxy.NutritionReferenceUrl.GET_NUTRITION_REFERENCES)
	@ApiOperation(value = PathProxy.NutritionReferenceUrl.GET_NUTRITION_REFERENCES)
	public Response<NutritionReferenceResponse> getNutritionReference(@RequestParam("patientId") String patientId,
			@RequestParam("doctorId") String doctorId, @RequestParam("locationId") String locationId,
			@RequestParam("hospitalId") String hospitalId, @RequestParam("page") int page, @RequestParam("size") int size,
			@RequestParam("searchTerm") String searchTerm, @RequestParam("updatedTime") String updatedTime) {
		if (DPDoctorUtils.anyStringEmpty(doctorId, locationId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<NutritionReferenceResponse> response = new Response<NutritionReferenceResponse>();
		response.setDataList(nutritionReferenceService.getNutritionReferenceList(page, size, doctorId, locationId,
				hospitalId, patientId, searchTerm, updatedTime));
		return response;
	}

	
	@GetMapping(value = PathProxy.NutritionReferenceUrl.GET_NUTRITION_ANALYTICS)
	@ApiOperation(value = PathProxy.NutritionReferenceUrl.GET_NUTRITION_ANALYTICS)
	public Response<NutritionGoalAnalytics> getNutritionAnalytics(@RequestParam("doctorId") String doctorId,
			@RequestParam("locationId") String locationId, @DefaultValue("0") @RequestParam("fromDate") Long fromDate,
			@DefaultValue("0") @RequestParam("toDate") Long toDate) {
		if (DPDoctorUtils.anyStringEmpty(doctorId, locationId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<NutritionGoalAnalytics> response = new Response<NutritionGoalAnalytics>();
		response.setData(nutritionReferenceService.getGoalAnalytics(doctorId, locationId, fromDate, toDate));
		return response;
	}

	
	@GetMapping(value = PathProxy.NutritionReferenceUrl.GRT_NUTRITION_REFERNCE)
	@ApiOperation(value = PathProxy.NutritionReferenceUrl.GRT_NUTRITION_REFERNCE)
	public Response<NutritionReferenceResponse> getNutritionReference(@PathVariable("id") String id) {
		if (DPDoctorUtils.anyStringEmpty(id)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<NutritionReferenceResponse> response = new Response<NutritionReferenceResponse>();
		response.setData(nutritionReferenceService.getNutritionReferenceById(id));
		return response;
	}

	
	@GetMapping(value = PathProxy.NutritionReferenceUrl.CHANGE_REFERENCE_STATUS)
	@ApiOperation(value = PathProxy.NutritionReferenceUrl.CHANGE_REFERENCE_STATUS)
	public Response<Boolean> changeNutritionReference(@PathVariable("id") String id,
			@RequestParam("regularityStatus") String regularityStatus, @RequestParam("goalStatus") String goalStatus) {
		if (DPDoctorUtils.anyStringEmpty(id)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<Boolean> response = new Response<Boolean>();
		response.setData(nutritionReferenceService.changeStatus(id, regularityStatus, goalStatus));
		return response;
	}
}
