package com.dpdocter.webservices;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import org.springframework.http.MediaType;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dpdocter.beans.CaloriesCounter;
import com.dpdocter.beans.ExerciseCounter;
import com.dpdocter.beans.MealCounter;
import com.dpdocter.beans.WaterCounter;
import com.dpdocter.beans.WaterCounterSetting;
import com.dpdocter.beans.WeightCounter;
import com.dpdocter.beans.WeightCounterSetting;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.services.CounterService;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value=PathProxy.COUNTER_BASE_URL,produces = MediaType.APPLICATION_JSON_VALUE ,consumes = MediaType.APPLICATION_JSON_VALUE)
@Api(value = PathProxy.COUNTER_BASE_URL, description = "Endpoint for CounterAPI")
public class CounterApi {
	private static Logger logger = LogManager.getLogger(CounterApi.class.getName());

	@Autowired
	private CounterService counterService;

	
	@PostMapping(value = PathProxy.CounterUrls.ADD_EDIT_WATER_COUNTER)
	@ApiOperation(value = PathProxy.CounterUrls.ADD_EDIT_WATER_COUNTER, notes = PathProxy.CounterUrls.ADD_EDIT_WATER_COUNTER)
	public Response<WaterCounter> addWaterCounter(@RequestBody WaterCounter request) {

		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		if (DPDoctorUtils.anyStringEmpty(request.getUserId())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "userId should not be null or empty");

		}
		WaterCounter counter = counterService.addEditWaterCounter(request);

		Response<WaterCounter> response = new Response<WaterCounter>();
		response.setData(counter);
		return response;
	}

	
	@GetMapping(value = PathProxy.CounterUrls.GET_WATER_COUNTER)
	@ApiOperation(value = PathProxy.CounterUrls.GET_WATER_COUNTER, notes = PathProxy.CounterUrls.GET_WATER_COUNTER)
	public Response<WaterCounter> getWaterCounterById(@PathVariable("counterId") String counterId) {

		if (DPDoctorUtils.anyStringEmpty(counterId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		WaterCounter counter = counterService.getWaterCounterById(counterId);
		Response<WaterCounter> response = new Response<WaterCounter>();
		response.setData(counter);
		return response;
	}

	
	@GetMapping(value = PathProxy.CounterUrls.GET_WATER_COUNTERS)
	@ApiOperation(value = PathProxy.CounterUrls.GET_WATER_COUNTERS, notes = PathProxy.CounterUrls.GET_WATER_COUNTERS)
	public Response<WaterCounter> getWaterCounters(@PathVariable("userId") String userId, @RequestParam("size") int size,
			@RequestParam("page") int page, @RequestParam("fromDate") String fromDate,
			@RequestParam("toDate") String toDate) {
		if (DPDoctorUtils.anyStringEmpty(userId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		Response<WaterCounter> response = new Response<WaterCounter>();
		response.setDataList(counterService.getWaterCounters(page, size, userId, fromDate, toDate));
		return response;
	}

	
	@DeleteMapping(value = PathProxy.CounterUrls.DELETE_WATER_COUNTER)
	@ApiOperation(value = PathProxy.CounterUrls.DELETE_WATER_COUNTER, notes = PathProxy.CounterUrls.DELETE_WATER_COUNTER)
	public Response<WaterCounter> deleteWaterCounter(@PathVariable("counterId") String counterId,
			@RequestParam("discarded")   Boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(counterId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		Response<WaterCounter> response = new Response<WaterCounter>();
		response.setData(counterService.deleteWaterCounter(counterId, discarded));
		return response;
	}

	
	@PostMapping(value = PathProxy.CounterUrls.ADD_EDIT_WEIGHT_COUNTER)
	@ApiOperation(value = PathProxy.CounterUrls.ADD_EDIT_WEIGHT_COUNTER, notes = PathProxy.CounterUrls.ADD_EDIT_WEIGHT_COUNTER)
	public Response<WeightCounter> addWeightCounter(@RequestBody WeightCounter request) {

		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		if (DPDoctorUtils.anyStringEmpty(request.getUserId())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "userId should not be null or empty");

		}
		WeightCounter counter = counterService.addEditWeightCounter(request);
		Response<WeightCounter> response = new Response<WeightCounter>();
		response.setData(counter);
		return response;
	}

	
	@GetMapping(value = PathProxy.CounterUrls.GET_WEIGHT_COUNTER)
	@ApiOperation(value = PathProxy.CounterUrls.GET_WEIGHT_COUNTER, notes = PathProxy.CounterUrls.GET_WEIGHT_COUNTER)
	public Response<WeightCounter> getWeightCounterById(@PathVariable("counterId") String counterId) {

		if (DPDoctorUtils.anyStringEmpty(counterId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		WeightCounter counter = counterService.getWeightCounterById(counterId);
		Response<WeightCounter> response = new Response<WeightCounter>();
		response.setData(counter);
		return response;
	}

	
	@GetMapping(value = PathProxy.CounterUrls.GET_WEIGHT_COUNTERS)
	@ApiOperation(value = PathProxy.CounterUrls.GET_WEIGHT_COUNTERS, notes = PathProxy.CounterUrls.GET_WEIGHT_COUNTERS)
	public Response<WeightCounter> getWeightCounter(@PathVariable("userId") String userId, @RequestParam("size") int size,
			@RequestParam("page") int page, @RequestParam("fromDate") String fromDate,
			@RequestParam("toDate") String toDate) {
		if (DPDoctorUtils.anyStringEmpty(userId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		Response<WeightCounter> response = new Response<WeightCounter>();
		response.setDataList(counterService.getWeightCounters(page, size, userId, fromDate, toDate));
		return response;
	}

	
	@DeleteMapping(value = PathProxy.CounterUrls.DELETE_WEIGHT_COUNTER)
	@ApiOperation(value = PathProxy.CounterUrls.DELETE_WEIGHT_COUNTER, notes = PathProxy.CounterUrls.DELETE_WEIGHT_COUNTER)
	public Response<WeightCounter> deleteWeightCounter(@PathVariable("counterId") String counterId,
			@RequestParam("discarded")   Boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(counterId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		Response<WeightCounter> response = new Response<WeightCounter>();
		response.setData(counterService.deleteWeightCounter(counterId, discarded));
		return response;
	}

	
	@PostMapping(value = PathProxy.CounterUrls.ADD_EDIT_WEIGHT_COUNTER_SETTING)
	@ApiOperation(value = PathProxy.CounterUrls.ADD_EDIT_WEIGHT_COUNTER_SETTING, notes = PathProxy.CounterUrls.ADD_EDIT_WEIGHT_COUNTER_SETTING)
	public Response<WeightCounterSetting> addWeightCounterSetting(@RequestBody WeightCounterSetting request) {

		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		if (DPDoctorUtils.anyStringEmpty(request.getUserId())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "userId should not be null or empty");

		}
		WeightCounterSetting counterSetting = counterService.addEditWeightCounterSetting(request);
		Response<WeightCounterSetting> response = new Response<WeightCounterSetting>();
		response.setData(counterSetting);
		return response;
	}

	
	@GetMapping(value = PathProxy.CounterUrls.GET_WEIGHT_COUNTER_SETTING)
	@ApiOperation(value = PathProxy.CounterUrls.GET_WEIGHT_COUNTER_SETTING, notes = PathProxy.CounterUrls.GET_WEIGHT_COUNTER_SETTING)
	public Response<WeightCounterSetting> getWeightCounterSetting(@PathVariable("userId") String userId) {
		if (DPDoctorUtils.anyStringEmpty(userId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		Response<WeightCounterSetting> response = new Response<WeightCounterSetting>();
		response.setData(counterService.getWeightCounterSetting(userId));
		return response;
	}

	
	@PostMapping(value = PathProxy.CounterUrls.ADD_EDIT_WATER_COUNTER_SETTING)
	@ApiOperation(value = PathProxy.CounterUrls.ADD_EDIT_WATER_COUNTER_SETTING, notes = PathProxy.CounterUrls.ADD_EDIT_WATER_COUNTER_SETTING)
	public Response<WaterCounterSetting> addWaterCountererSetting(@RequestBody WaterCounterSetting request) {

		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		if (DPDoctorUtils.anyStringEmpty(request.getUserId())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "userId should not be null or empty");

		}
		WaterCounterSetting counterSetting = counterService.addEditWaterCounterSetting(request);
		Response<WaterCounterSetting> response = new Response<WaterCounterSetting>();
		response.setData(counterSetting);
		return response;
	}

	
	@GetMapping(value = PathProxy.CounterUrls.GET_WATER_COUNTER_SETTING)
	@ApiOperation(value = PathProxy.CounterUrls.GET_WATER_COUNTER_SETTING, notes = PathProxy.CounterUrls.GET_WATER_COUNTER_SETTING)
	public Response<WaterCounterSetting> getWaterCounterSetting(@PathVariable("userId") String userId) {
		if (DPDoctorUtils.anyStringEmpty(userId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		Response<WaterCounterSetting> response = new Response<WaterCounterSetting>();
		response.setData(counterService.getWaterCounterSetting(userId));
		return response;
	}

	
	@PostMapping(value = PathProxy.CounterUrls.ADD_EDIT_MEAL_COUNTER)
	@ApiOperation(value = PathProxy.CounterUrls.ADD_EDIT_MEAL_COUNTER, notes = PathProxy.CounterUrls.ADD_EDIT_MEAL_COUNTER)
	public Response<MealCounter> addMealCounter(@RequestBody MealCounter request) {

		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		if (DPDoctorUtils.anyStringEmpty(request.getUserId())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "userId should not be null or empty");

		}
		MealCounter counter = counterService.addEditMealCounter(request);

		Response<MealCounter> response = new Response<MealCounter>();
		response.setData(counter);
		return response;
	}

	
	@GetMapping(value = PathProxy.CounterUrls.GET_MEAL_COUNTER)
	@ApiOperation(value = PathProxy.CounterUrls.GET_MEAL_COUNTER, notes = PathProxy.CounterUrls.GET_MEAL_COUNTER)
	public Response<MealCounter> getMealCounterById(@PathVariable("counterId") String counterId) {

		if (DPDoctorUtils.anyStringEmpty(counterId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		MealCounter counter = counterService.getMealCounterById(counterId);
		Response<MealCounter> response = new Response<MealCounter>();
		response.setData(counter);
		return response;
	}

	
	@GetMapping(value = PathProxy.CounterUrls.GET_MEAL_COUNTERS)
	@ApiOperation(value = PathProxy.CounterUrls.GET_MEAL_COUNTERS, notes = PathProxy.CounterUrls.GET_MEAL_COUNTERS)
	public Response<MealCounter> getMealCounters(@PathVariable("userId") String userId, @RequestParam("size") int size,
			@RequestParam("page") int page, @RequestParam("fromDate") String fromDate, @RequestParam("toDate") String toDate,
			@RequestParam("mealTime") String mealTime) {
		if (DPDoctorUtils.anyStringEmpty(userId, mealTime)) {
			logger.warn("userId,mealtime must not null or empty");
			throw new BusinessException(ServiceError.InvalidInput, "userId,mealtime must not null or empty");

		}
		Response<MealCounter> response = new Response<MealCounter>();
		response.setDataList(counterService.getMealCounters(page, size, userId, fromDate, toDate, mealTime));
		return response;
	}

	
	@DeleteMapping(value = PathProxy.CounterUrls.DELETE_MEAL_COUNTER)
	@ApiOperation(value = PathProxy.CounterUrls.DELETE_MEAL_COUNTER, notes = PathProxy.CounterUrls.DELETE_MEAL_COUNTER)
	public Response<MealCounter> deleteMealCounter(@PathVariable("counterId") String counterId,
			@RequestParam("discarded")   Boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(counterId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		Response<MealCounter> response = new Response<MealCounter>();
		response.setData(counterService.deleteMealCounter(counterId, discarded));
		return response;
	}

	
	@PostMapping(value = PathProxy.CounterUrls.ADD_EDIT_EXERCISE_COUNTER)
	@ApiOperation(value = PathProxy.CounterUrls.ADD_EDIT_EXERCISE_COUNTER, notes = PathProxy.CounterUrls.ADD_EDIT_EXERCISE_COUNTER)
	public Response<ExerciseCounter> addExerciseCounter(@RequestBody ExerciseCounter request) {

		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		if (DPDoctorUtils.anyStringEmpty(request.getUserId())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "userId should not be null or empty");

		}
		ExerciseCounter counter = counterService.addEditExerciseCounter(request);

		Response<ExerciseCounter> response = new Response<ExerciseCounter>();
		response.setData(counter);
		return response;
	}

	
	@GetMapping(value = PathProxy.CounterUrls.GET_EXERCISE_COUNTER)
	@ApiOperation(value = PathProxy.CounterUrls.GET_EXERCISE_COUNTER, notes = PathProxy.CounterUrls.GET_EXERCISE_COUNTER)
	public Response<ExerciseCounter> getExerciseCounterById(@PathVariable("counterId") String counterId) {

		if (DPDoctorUtils.anyStringEmpty(counterId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		ExerciseCounter counter = counterService.getExerciseCounterById(counterId);
		Response<ExerciseCounter> response = new Response<ExerciseCounter>();
		response.setData(counter);
		return response;
	}

	
	@GetMapping(value = PathProxy.CounterUrls.GET_EXERCISE_COUNTERS)
	@ApiOperation(value = PathProxy.CounterUrls.GET_EXERCISE_COUNTERS, notes = PathProxy.CounterUrls.GET_EXERCISE_COUNTERS)
	public Response<ExerciseCounter> getExerciseCounters(@PathVariable("userId") String userId,
			@RequestParam("size") int size, @RequestParam("page") int page, @RequestParam("fromDate") String fromDate,
			@RequestParam("toDate") String toDate) {
		if (DPDoctorUtils.anyStringEmpty(userId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		Response<ExerciseCounter> response = new Response<ExerciseCounter>();
		response.setDataList(counterService.getExerciseCounters(page, size, userId, fromDate, toDate));
		return response;
	}

	
	@DeleteMapping(value = PathProxy.CounterUrls.DELETE_EXERCISE_COUNTER)
	@ApiOperation(value = PathProxy.CounterUrls.DELETE_EXERCISE_COUNTER, notes = PathProxy.CounterUrls.DELETE_EXERCISE_COUNTER)
	public Response<ExerciseCounter> deleteExerciseCounter(@PathVariable("counterId") String counterId,
			@RequestParam("discarded")   Boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(counterId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		Response<ExerciseCounter> response = new Response<ExerciseCounter>();
		response.setData(counterService.deleteExerciseCounter(counterId, discarded));
		return response;
	}

	
	@PostMapping(value = PathProxy.CounterUrls.ADD_EDIT_CALORIES_COUNTER)
	@ApiOperation(value = PathProxy.CounterUrls.ADD_EDIT_CALORIES_COUNTER, notes = PathProxy.CounterUrls.ADD_EDIT_CALORIES_COUNTER)
	public Response<CaloriesCounter> addCaloriesCounter(@RequestBody CaloriesCounter request) {

		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		if (DPDoctorUtils.anyStringEmpty(request.getUserId())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "userId should not be null or empty");

		}
		CaloriesCounter counter = counterService.addEditCaloriesCounter(request);

		Response<CaloriesCounter> response = new Response<CaloriesCounter>();
		response.setData(counter);
		return response;
	}

	
	@GetMapping(value = PathProxy.CounterUrls.GET_CALORIES_COUNTER)
	@ApiOperation(value = PathProxy.CounterUrls.GET_CALORIES_COUNTER, notes = PathProxy.CounterUrls.GET_CALORIES_COUNTER)
	public Response<CaloriesCounter> getCaloriesCounterById(@PathVariable("counterId") String counterId) {

		if (DPDoctorUtils.anyStringEmpty(counterId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		CaloriesCounter counter = counterService.getCaloriesCounterById(counterId);
		Response<CaloriesCounter> response = new Response<CaloriesCounter>();
		response.setData(counter);
		return response;
	}

	
	@GetMapping(value = PathProxy.CounterUrls.GET_CALORIES_COUNTERS)
	@ApiOperation(value = PathProxy.CounterUrls.GET_CALORIES_COUNTERS, notes = PathProxy.CounterUrls.GET_CALORIES_COUNTERS)
	public Response<CaloriesCounter> getCaloriesCounters(@PathVariable("userId") String userId,
			@RequestParam("size") int size, @RequestParam("page") int page, @RequestParam("fromDate") String fromDate,
			@RequestParam("toDate") String toDate) {
		if (DPDoctorUtils.anyStringEmpty(userId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		Response<CaloriesCounter> response = new Response<CaloriesCounter>();
		response.setDataList(counterService.getCaloriesCounters(page, size, userId, fromDate, toDate));
		return response;
	}

	
	@DeleteMapping(value = PathProxy.CounterUrls.DELETE_CALORIES_COUNTER)
	@ApiOperation(value = PathProxy.CounterUrls.DELETE_CALORIES_COUNTER, notes = PathProxy.CounterUrls.DELETE_CALORIES_COUNTER)
	public Response<CaloriesCounter> deleteCaloriesCounter(@PathVariable("counterId") String counterId,
			@RequestParam("discarded")   Boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(counterId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		Response<CaloriesCounter> response = new Response<CaloriesCounter>();
		response.setData(counterService.deleteColariesCounter(counterId, discarded));
		return response;
	}

}
