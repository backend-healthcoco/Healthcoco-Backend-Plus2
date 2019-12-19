package com.dpdocter.webservices;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dpdocter.beans.Recipe;
import com.dpdocter.enums.MealTimeEnum;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.services.NutritionEngineService;

import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Component
@Path(PathProxy.NUTRITION_ENGINE_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.NUTRITION_ENGINE_BASE_URL)
public class NutritionEngineAPI {

	private static Logger logger = Logger.getLogger(NutritionEngineAPI.class.getName());

	@Autowired
	private NutritionEngineService nutritionEngineService;

	@GET
	@Path(PathProxy.NutritionEngineUrl.GET_RECIPES)
	@ApiOperation(value = PathProxy.NutritionEngineUrl.GET_RECIPES)
	public Response<Recipe> getRecipes(@PathParam("userId") String userId, @QueryParam("countryId") String countryId,
			@QueryParam("country") String country,
			@MatrixParam(value = "mealTime") List<MealTimeEnum> mealTime,
			@QueryParam("doctorId") String doctorId, @QueryParam("locationId") String locationId,
			@QueryParam("hospitalId") String hospitalId) {
		if (userId == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		
		Response<Recipe> response = new Response<Recipe>();
		response.setDataList(nutritionEngineService.getRecipes(userId, countryId, country, mealTime, doctorId, locationId, hospitalId));
		return response;
	}

}
