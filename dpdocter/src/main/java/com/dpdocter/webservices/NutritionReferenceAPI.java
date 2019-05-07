package com.dpdocter.webservices;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

@Component
@Path(PathProxy.NUTRITION_REFERENCE_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.NUTRITION_REFERENCE_BASE_URL, description = "Endpoint for nutrition Referenceapi's")
public class NutritionReferenceAPI {
	private static Logger logger = Logger.getLogger(NutritionReferenceAPI.class.getName());

	@Autowired
	private NutritionReferenceService nutritionReferenceService;

	@POST
	@Path(PathProxy.NutritionReferenceUrl.ADD_EDIT_NUTRITION_REFERENCE)
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

	@Path(value = PathProxy.NutritionReferenceUrl.GET_NUTRITION_REFERENCES)
	@GET
	@ApiOperation(value = PathProxy.NutritionReferenceUrl.GET_NUTRITION_REFERENCES)
	public Response<NutritionReferenceResponse> getNutritionReference(@QueryParam("patientId") String patientId,
			@QueryParam("doctorId") String doctorId, @QueryParam("locationId") String locationId,
			@QueryParam("hospitalId") String hospitalId, @QueryParam("page") int page, @QueryParam("size") int size,
			@QueryParam("searchTerm") String searchTerm, @QueryParam("updatedTime") String updatedTime) {
		if (DPDoctorUtils.anyStringEmpty(doctorId, locationId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<NutritionReferenceResponse> response = new Response<NutritionReferenceResponse>();
		response.setDataList(nutritionReferenceService.getNutritionReferenceList(page, size, doctorId, locationId,
				hospitalId, patientId, searchTerm, updatedTime));
		return response;
	}

	@Path(value = PathProxy.NutritionReferenceUrl.GET_NUTRITION_ANALYTICS)
	@GET
	@ApiOperation(value = PathProxy.NutritionReferenceUrl.GET_NUTRITION_ANALYTICS)
	public Response<NutritionGoalAnalytics> getNutritionAnalytics(@QueryParam("doctorId") String doctorId,
			@QueryParam("locationId") String locationId, @DefaultValue("0") @QueryParam("fromDate") Long fromDate,
			@DefaultValue("0") @QueryParam("toDate") Long toDate) {
		if (DPDoctorUtils.anyStringEmpty(doctorId, locationId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<NutritionGoalAnalytics> response = new Response<NutritionGoalAnalytics>();
		response.setData(nutritionReferenceService.getGoalAnalytics(doctorId, locationId, fromDate, toDate));
		return response;
	}

	@Path(value = PathProxy.NutritionReferenceUrl.GRT_NUTRITION_REFERNCE)
	@GET
	@ApiOperation(value = PathProxy.NutritionReferenceUrl.GRT_NUTRITION_REFERNCE)
	public Response<NutritionReferenceResponse> getNutritionReference(@PathParam("id") String id) {
		if (DPDoctorUtils.anyStringEmpty(id)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<NutritionReferenceResponse> response = new Response<NutritionReferenceResponse>();
		response.setData(nutritionReferenceService.getNutritionReferenceById(id));
		return response;
	}

	@Path(value = PathProxy.NutritionReferenceUrl.CHANGE_REFERENCE_STATUS)
	@GET
	@ApiOperation(value = PathProxy.NutritionReferenceUrl.CHANGE_REFERENCE_STATUS)
	public Response<Boolean> changeNutritionReference(@PathParam("id") String id,
			@QueryParam("regularityStatus") String regularityStatus, @QueryParam("goalStatus") String goalStatus) {
		if (DPDoctorUtils.anyStringEmpty(id)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<Boolean> response = new Response<Boolean>();
		response.setData(nutritionReferenceService.changeStatus(id, regularityStatus, goalStatus));
		return response;
	}
}
