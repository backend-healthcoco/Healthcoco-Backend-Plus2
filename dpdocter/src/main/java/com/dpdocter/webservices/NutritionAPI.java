package com.dpdocter.webservices;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
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
import com.dpdocter.services.NutritionService;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Component
@Path(PathProxy.NUTRITION_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.NUTRITION_BASE_URL, description = "Endpoint for nutrition api's")
public class NutritionAPI {

	private static Logger logger = Logger.getLogger(NutritionAPI.class.getName());
	
	@Autowired
	NutritionService nutritionService;

	@POST
	@Path(PathProxy.NutritionUrl.ADD_EDIT_NUTRITION_REFERENCE)
	@ApiOperation(value = PathProxy.NutritionUrl.ADD_EDIT_NUTRITION_REFERENCE)
	public Response<NutritionReferenceResponse> addEditNutritionResponse(AddEditNutritionReferenceRequest request) {
		if(request == null)
		{
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<NutritionReferenceResponse> response = new Response<>();
		NutritionReferenceResponse nutritionReferenceResponse = null;

		nutritionReferenceResponse = nutritionService.addEditNutritionReference(request);
		response.setData(nutritionReferenceResponse);
		return response;
	}
	
	
	@Path(value = PathProxy.NutritionUrl.GET_NUTRITION_REFERENCES)
	@GET
	@ApiOperation(value = PathProxy.NutritionUrl.GET_NUTRITION_REFERENCES, notes = PathProxy.NutritionUrl.GET_NUTRITION_REFERENCES)
	public Response<NutritionReferenceResponse> getNutritionReference(@QueryParam("doctorId") String doctorId,
			@QueryParam("locationId") String locationId, @QueryParam("page") int page, @QueryParam("size") int size,
			@QueryParam("searchTerm") String searchTerm , @QueryParam("role") String role  ) {
		if (DPDoctorUtils.anyStringEmpty(doctorId, locationId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<NutritionReferenceResponse> response = new Response<NutritionReferenceResponse>();
		response.setDataList(nutritionService.getNutritionReferenceList(doctorId, locationId, role, page, size));
		return response;
	}
	
	
	@Path(value = PathProxy.NutritionUrl.GET_NUTRITION_ANALYTICS)
	@GET
	@ApiOperation(value = PathProxy.NutritionUrl.GET_NUTRITION_ANALYTICS, notes = PathProxy.NutritionUrl.GET_NUTRITION_ANALYTICS)
	public Response<NutritionGoalAnalytics> getNutritionAnalytics(@QueryParam("doctorId") String doctorId,
			@QueryParam("locationId") String locationId , @QueryParam("role") String role ,@DefaultValue("0") @QueryParam("fromDate") Long fromDate ,@QueryParam("toDate") Long toDate ) {
		if (DPDoctorUtils.anyStringEmpty(doctorId, locationId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<NutritionGoalAnalytics> response = new Response<NutritionGoalAnalytics>();
		response.setData(nutritionService.getGoalAnalytics(doctorId, locationId, role, fromDate, toDate));
		return response;
	}


}
