package com.dpdocter.webservices;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dpdocter.beans.DietPlan;
import com.dpdocter.enums.MealTimeEnum;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.services.NutritionEngineService;

import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
(PathProxy.NUTRITION_ENGINE_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.NUTRITION_ENGINE_BASE_URL)
public class NutritionEngineAPI {

	private static Logger logger = LogManager.getLogger(NutritionEngineAPI.class.getName());

	@Autowired
	private NutritionEngineService nutritionEngineService;

	@GetMapping
	(PathProxy.NutritionEngineUrl.GET_RECIPES)
	@ApiOperation(value = PathProxy.NutritionEngineUrl.GET_RECIPES)
	public Response<DietPlan> getRecipes(@PathVariable("userId") String userId,
			@MatrixParam(value = "mealTime") List<MealTimeEnum> mealTime,
			@RequestParam("doctorId") String doctorId, @RequestParam("locationId") String locationId,
			@RequestParam("hospitalId") String hospitalId) {
		if (userId == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		
		Response<DietPlan> response = new Response<DietPlan>();
		response.setData(nutritionEngineService.getRecipes(userId, mealTime, doctorId, locationId, hospitalId));
		return response;
	}

}
