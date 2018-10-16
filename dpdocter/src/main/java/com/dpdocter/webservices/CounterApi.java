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

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dpdocter.beans.Exercise;
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

@Component
@Path(PathProxy.COUNTER_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.COUNTER_BASE_URL, description = "Endpoint for CounterAPI")
public class CounterApi {
	private static Logger logger = Logger.getLogger(CounterApi.class.getName());

	@Autowired
	private CounterService counterService;

	@Path(value = PathProxy.CounterUrls.ADD_EDIT_WATER_COUNTER)
	@POST
	@ApiOperation(value = PathProxy.CounterUrls.ADD_EDIT_WATER_COUNTER, notes = PathProxy.CounterUrls.ADD_EDIT_WATER_COUNTER)
	public Response<WaterCounter> addWaterCounter(WaterCounter request) {

		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		if (DPDoctorUtils.anyStringEmpty(request.getUserId())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "userId should not be null or empty");

		}
		WaterCounter tracker = counterService.addEditWaterCounter(request);

		Response<WaterCounter> response = new Response<WaterCounter>();
		response.setData(tracker);
		return response;
	}

	@Path(value = PathProxy.CounterUrls.GET_WATER_COUNTER)
	@GET
	@ApiOperation(value = PathProxy.CounterUrls.GET_WATER_COUNTER, notes = PathProxy.CounterUrls.GET_WATER_COUNTER)
	public Response<WaterCounter> getWaterTrackerById(@PathParam("counterId") String counterId) {

		if (DPDoctorUtils.anyStringEmpty(counterId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		WaterCounter counter = counterService.getWaterCounterById(counterId);
		Response<WaterCounter> response = new Response<WaterCounter>();
		response.setData(counter);
		return response;
	}

	@Path(value = PathProxy.CounterUrls.GET_WATER_COUNTERS)
	@GET
	@ApiOperation(value = PathProxy.CounterUrls.GET_WATER_COUNTERS, notes = PathProxy.CounterUrls.GET_WATER_COUNTERS)
	public Response<WaterCounter> getWaterCounters(@PathParam("userId") String userId, @QueryParam("size") int size,
			@QueryParam("page") int page, @QueryParam("fromDate") String fromDate,
			@QueryParam("toDate") String toDate) {
		if (DPDoctorUtils.anyStringEmpty(userId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		Response<WaterCounter> response = new Response<WaterCounter>();
		response.setDataList(counterService.getWaterCounters(page, size, userId, fromDate, toDate));
		return response;
	}

	@Path(value = PathProxy.CounterUrls.DELETE_WATER_COUNTER)
	@DELETE
	@ApiOperation(value = PathProxy.CounterUrls.DELETE_WATER_COUNTER, notes = PathProxy.CounterUrls.DELETE_WATER_COUNTER)
	public Response<WaterCounter> deleteWaterCounter(@PathParam("counterId") String counterId,
			@QueryParam("discarded") @DefaultValue("true") Boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(counterId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		Response<WaterCounter> response = new Response<WaterCounter>();
		response.setData(counterService.deleteWaterCounter(counterId, discarded));
		return response;
	}

	@Path(value = PathProxy.CounterUrls.ADD_EDIT_WEIGHT_COUNTER)
	@POST
	@ApiOperation(value = PathProxy.CounterUrls.ADD_EDIT_WEIGHT_COUNTER, notes = PathProxy.CounterUrls.ADD_EDIT_WEIGHT_COUNTER)
	public Response<WeightCounter> addWeightCounter(WeightCounter request) {

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

	@Path(value = PathProxy.CounterUrls.GET_WEIGHT_COUNTER)
	@GET
	@ApiOperation(value = PathProxy.CounterUrls.GET_WEIGHT_COUNTER, notes = PathProxy.CounterUrls.GET_WEIGHT_COUNTER)
	public Response<WeightCounter> getWeightCounterById(@PathParam("counterId") String counterId) {

		if (DPDoctorUtils.anyStringEmpty(counterId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		WeightCounter counter = counterService.getWeightCounterById(counterId);
		Response<WeightCounter> response = new Response<WeightCounter>();
		response.setData(counter);
		return response;
	}

	@Path(value = PathProxy.CounterUrls.GET_WEIGHT_COUNTER)
	@GET
	@ApiOperation(value = PathProxy.CounterUrls.GET_WEIGHT_COUNTER, notes = PathProxy.CounterUrls.GET_WEIGHT_COUNTER)
	public Response<WeightCounter> getWeightCounter(@PathParam("userId") String userId, @QueryParam("size") int size,
			@QueryParam("page") int page, @QueryParam("fromDate") String fromDate,
			@QueryParam("toDate") String toDate) {
		if (DPDoctorUtils.anyStringEmpty(userId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		Response<WeightCounter> response = new Response<WeightCounter>();
		response.setDataList(counterService.getWeightCounters(page, size, userId, fromDate, toDate));
		return response;
	}

	@Path(value = PathProxy.CounterUrls.DELETE_WEIGHT_COUNTER)
	@DELETE
	@ApiOperation(value = PathProxy.CounterUrls.DELETE_WEIGHT_COUNTER, notes = PathProxy.CounterUrls.DELETE_WEIGHT_COUNTER)
	public Response<WeightCounter> deleteWeightCounter(@PathParam("counterId") String counterId,
			@QueryParam("discarded") @DefaultValue("true") Boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(counterId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		Response<WeightCounter> response = new Response<WeightCounter>();
		response.setData(counterService.deleteWeightCounter(counterId, discarded));
		return response;
	}

	@Path(value = PathProxy.CounterUrls.ADD_EDIT_WEIGHT_COUNTER_SETTING)
	@POST
	@ApiOperation(value = PathProxy.CounterUrls.ADD_EDIT_WEIGHT_COUNTER_SETTING, notes = PathProxy.CounterUrls.ADD_EDIT_WEIGHT_COUNTER_SETTING)
	public Response<WeightCounterSetting> addWeightCounterSetting(WeightCounterSetting request) {

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

	@Path(value = PathProxy.CounterUrls.GET_WEIGHT_COUNTER_SETTING)
	@GET
	@ApiOperation(value = PathProxy.CounterUrls.GET_WEIGHT_COUNTER_SETTING, notes = PathProxy.CounterUrls.GET_WEIGHT_COUNTER_SETTING)
	public Response<WeightCounterSetting> getWeightCounterSetting(@PathParam("userId") String userId) {
		if (DPDoctorUtils.anyStringEmpty(userId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		Response<WeightCounterSetting> response = new Response<WeightCounterSetting>();
		response.setData(counterService.getWeightCounterSetting(userId));
		return response;
	}

	@Path(value = PathProxy.CounterUrls.ADD_EDIT_WATER_COUNTER_SETTING)
	@POST
	@ApiOperation(value = PathProxy.CounterUrls.ADD_EDIT_WATER_COUNTER_SETTING, notes = PathProxy.CounterUrls.ADD_EDIT_WATER_COUNTER_SETTING)
	public Response<WaterCounterSetting> addWaterCountererSetting(WaterCounterSetting request) {

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

	@Path(value = PathProxy.CounterUrls.GET_WATER_COUNTER_SETTING)
	@GET
	@ApiOperation(value = PathProxy.CounterUrls.GET_WATER_COUNTER_SETTING, notes = PathProxy.CounterUrls.GET_WATER_COUNTER_SETTING)
	public Response<WaterCounterSetting> getWaterCounterSetting(@PathParam("userId") String userId) {
		if (DPDoctorUtils.anyStringEmpty(userId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		Response<WaterCounterSetting> response = new Response<WaterCounterSetting>();
		response.setData(counterService.getWaterCounterSetting(userId));
		return response;
	}

}
