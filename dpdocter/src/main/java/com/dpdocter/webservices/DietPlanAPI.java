package com.dpdocter.webservices;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
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

import com.dpdocter.beans.DentalWork;
import com.dpdocter.beans.DietPlan;
import com.dpdocter.elasticsearch.document.ESDentalWorksDocument;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.request.AddEditCustomWorkRequest;
import com.dpdocter.services.DietPlansService;

import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Component
@Path(PathProxy.DIET_PLAN_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.DIET_PLAN_BASE_URL, description = "Endpoint for Diet Plan")
public class DietPlanAPI {

	private static Logger logger = Logger.getLogger(DietPlanAPI.class.getName());

	@Autowired
	private DietPlansService dietPlansService;

	@Path(value = PathProxy.DietPlanUrls.ADD_EDIT_DIET_PLAN)
	@POST
	@ApiOperation(value = PathProxy.DietPlanUrls.ADD_EDIT_DIET_PLAN, notes = PathProxy.DietPlanUrls.ADD_EDIT_DIET_PLAN)
	public Response<DietPlan> addEditDietPlan(DietPlan request) {

		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		Response<DietPlan> response = new Response<DietPlan>();

		return response;
	}

	@Path(value = PathProxy.DietPlanUrls.GET_DIET_PLANS)
	@GET
	@ApiOperation(value = PathProxy.DietPlanUrls.GET_DIET_PLANS, notes = PathProxy.DietPlanUrls.GET_DIET_PLANS)
	public Response<DietPlan> getDietPlans(@QueryParam("locationId") String locationId, @QueryParam("page") int page,
			@QueryParam("size") int size, @QueryParam("doctorId") String doctorId,
			@QueryParam("hospitalId") String hospitalId, @QueryParam("updatedTime") String updatedTime,
			@QueryParam("discarded") boolean discarded) {
		Response<DietPlan> response = new Response<DietPlan>();
		return response;
	}

	@Path(value = PathProxy.DietPlanUrls.DELETE_DIET_PLAN)
	@DELETE
	@ApiOperation(value = PathProxy.DietPlanUrls.DELETE_DIET_PLAN, notes = PathProxy.DietPlanUrls.DELETE_DIET_PLAN)
	public Response<DietPlan> deleteDietPlan(@PathParam("planId") String planId,
			@QueryParam("discarded") boolean discarded) {

		if (planId == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		Response<DietPlan> response = new Response<DietPlan>();

		return response;
	}

	@Path(value = PathProxy.DietPlanUrls.GET_DIET_PLAN)
	@GET
	@ApiOperation(value = PathProxy.DietPlanUrls.GET_DIET_PLAN, notes = PathProxy.DietPlanUrls.GET_DIET_PLAN)
	public Response<DietPlan> getDietPlan(@QueryParam("planId") String planId) {

		if (planId == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		Response<DietPlan> response = new Response<DietPlan>();

		return response;
	}

}
