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

import com.dpdocter.beans.DietPlan;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.services.DietPlansService;

import common.util.web.DPDoctorUtils;
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
		if (DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getPatientId(), request.getLocationId(),
				request.getHospitalId())) {
			logger.warn("patientId,doctorId,locationId and hospitalId should not be null or empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"patientId,doctorId,locationId and hospitalId should not be null or empty");
		}

		Response<DietPlan> response = new Response<DietPlan>();
		response.setData(dietPlansService.addEditDietPlan(request));
		return response;
	}

	@Path(value = PathProxy.DietPlanUrls.GET_DIET_PLANS)
	@GET
	@ApiOperation(value = PathProxy.DietPlanUrls.GET_DIET_PLANS, notes = PathProxy.DietPlanUrls.GET_DIET_PLANS)
	public Response<DietPlan> getDietPlans(@QueryParam("locationId") String locationId, @QueryParam("page") int page,
			@QueryParam("size") int size, @QueryParam("patientId") String patientId,
			@QueryParam("doctorId") String doctorId, @QueryParam("hospitalId") String hospitalId,
			@QueryParam("updatedTime") long updatedTime, @QueryParam("discarded") boolean discarded) {
		Response<DietPlan> response = new Response<DietPlan>();
		response.setDataList(dietPlansService.getDietPlans(page, size, patientId, doctorId, hospitalId, locationId,
				updatedTime, discarded));
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
		response.setData(dietPlansService.discardDietPlan(planId, discarded));
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
		response.setData(dietPlansService.getDietPlanById(planId));

		return response;
	}

	@Path(value = PathProxy.DietPlanUrls.DOWNLOAD_DIET_PLAN)
	@GET
	@ApiOperation(value = PathProxy.DietPlanUrls.DOWNLOAD_DIET_PLAN, notes = PathProxy.DietPlanUrls.DOWNLOAD_DIET_PLAN)
	public Response<String> downloadDietPlan(@QueryParam("planId") String planId) {

		if (planId == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		Response<String> response = new Response<String>();
		response.setData(dietPlansService.downloadDietPlan(planId));
		return response;
	}

	@Path(value = PathProxy.DietPlanUrls.SEND_DIET_PLAN_EMAIL)
	@GET
	@ApiOperation(value = PathProxy.DietPlanUrls.SEND_DIET_PLAN_EMAIL, notes = PathProxy.DietPlanUrls.SEND_DIET_PLAN_EMAIL)
	public Response<Boolean> emailDietPlan(@PathParam("planId") String planId,
			@QueryParam("emailAddress") String emailAddress) {

		if (emailAddress == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		Response<Boolean> response = new Response<Boolean>();
		response.setData(dietPlansService.emailDietPlan(emailAddress, planId));
		return response;
	}

}
