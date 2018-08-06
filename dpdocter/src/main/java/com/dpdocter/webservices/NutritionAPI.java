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
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dpdocter.beans.NutritionGoalAnalytics;
import com.dpdocter.beans.NutritionPlan;
import com.dpdocter.beans.SubscriptionNutritionPlan;
import com.dpdocter.beans.UserNutritionSubscription;
import com.dpdocter.enums.NutritionPlanType;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.request.AddEditNutritionReferenceRequest;
import com.dpdocter.request.NutritionPlanRequest;
import com.dpdocter.response.NutritionPlanResponse;
import com.dpdocter.response.NutritionPlanWithCategoryResponse;
import com.dpdocter.response.NutritionReferenceResponse;
import com.dpdocter.response.UserNutritionSubscriptionResponse;
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
		if (request == null) {
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
	@ApiOperation(value = PathProxy.NutritionUrl.GET_NUTRITION_REFERENCES)
	public Response<NutritionReferenceResponse> getNutritionReference(@QueryParam("doctorId") String doctorId,
			@QueryParam("locationId") String locationId, @QueryParam("page") int page, @QueryParam("size") int size,
			@QueryParam("searchTerm") String searchTerm, @QueryParam("role") String role) {
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
	@ApiOperation(value = PathProxy.NutritionUrl.GET_NUTRITION_ANALYTICS)
	public Response<NutritionGoalAnalytics> getNutritionAnalytics(@QueryParam("doctorId") String doctorId,
			@QueryParam("locationId") String locationId, @QueryParam("role") String role,
			@DefaultValue("0") @QueryParam("fromDate") Long fromDate, @QueryParam("toDate") Long toDate) {
		if (DPDoctorUtils.anyStringEmpty(doctorId, locationId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<NutritionGoalAnalytics> response = new Response<NutritionGoalAnalytics>();
		response.setData(nutritionService.getGoalAnalytics(doctorId, locationId, role, fromDate, toDate));
		return response;
	}

	@Path(PathProxy.NutritionUrl.GET_NUTRITION_PLAN_BY_ID)
	@GET
	@ApiOperation(value = PathProxy.NutritionUrl.GET_NUTRITION_PLAN_BY_ID, notes = PathProxy.NutritionUrl.GET_NUTRITION_PLAN_BY_ID)
	public Response<NutritionPlanResponse> getPlanById(@PathParam("id") String id) {

		Response<NutritionPlanResponse> response = null;

		if (DPDoctorUtils.anyStringEmpty(id)) {
			throw new BusinessException(ServiceError.InvalidInput, " Invalid input");
		}

		response = new Response<NutritionPlanResponse>();
		response.setData(nutritionService.getNutritionPlan(id));

		return response;
	}

	@Path(PathProxy.NutritionUrl.GET_ALL_PLAN_CATEGORY)
	@GET
	@ApiOperation(value = PathProxy.NutritionUrl.GET_ALL_PLAN_CATEGORY, notes = PathProxy.NutritionUrl.GET_ALL_PLAN_CATEGORY)
	public Response<NutritionPlanType> getPlanCategory() {

		Response<NutritionPlanType> response = new Response<NutritionPlanType>();
		response.setDataList(nutritionService.getPlanType());

		return response;
	}

	@Path(PathProxy.NutritionUrl.GET_SUBSCRIPTION_PLAN_BY_ID)
	@GET
	@ApiOperation(value = PathProxy.NutritionUrl.GET_SUBSCRIPTION_PLAN_BY_ID, notes = PathProxy.NutritionUrl.GET_SUBSCRIPTION_PLAN_BY_ID)
	public Response<SubscriptionNutritionPlan> getSubscriptionPlan(@PathParam("id") String id) {
		if (DPDoctorUtils.anyStringEmpty(id)) {
			throw new BusinessException(ServiceError.InvalidInput, " Invalid input");
		}
		Response<SubscriptionNutritionPlan> response = new Response<SubscriptionNutritionPlan>();
		response.setData(nutritionService.getSubscritionPlan(id));

		return response;
	}

	@Path(PathProxy.NutritionUrl.GET_SUBSCRIPTION_PLANS)
	@GET
	@ApiOperation(value = PathProxy.NutritionUrl.GET_SUBSCRIPTION_PLANS, notes = PathProxy.NutritionUrl.GET_SUBSCRIPTION_PLANS)
	public Response<SubscriptionNutritionPlan> getSubscriptionPlans(@QueryParam("page") int page,
			@QueryParam("size") int size, @QueryParam("nutritionplanId") String nutritionplanId,
			@QueryParam("discarded") @DefaultValue("false") boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(nutritionplanId)) {
			throw new BusinessException(ServiceError.InvalidInput, " NutritionplanId must not be null ");
		}
		Response<SubscriptionNutritionPlan> response = new Response<SubscriptionNutritionPlan>();
		response.setDataList(nutritionService.getSubscritionPlans(page, size, nutritionplanId, discarded));

		return response;
	}

	@Path(PathProxy.NutritionUrl.GENERATE_ID)
	@GET
	@ApiOperation(value = PathProxy.NutritionUrl.GENERATE_ID, notes = PathProxy.NutritionUrl.GENERATE_ID)
	public Response<String> getGenerateId() {
		Response<String> response = new Response<String>();
		response.setData(DPDoctorUtils.generateRandomId());

		return response;
	}

	@Path(PathProxy.NutritionUrl.ADD_USER_PLAN_SUBSCRIPTION)
	@POST
	@ApiOperation(value = PathProxy.NutritionUrl.ADD_USER_PLAN_SUBSCRIPTION, notes = PathProxy.NutritionUrl.ADD_USER_PLAN_SUBSCRIPTION)
	public Response<UserNutritionSubscriptionResponse> addEditUserPlanSubscription(UserNutritionSubscription request) {
		if (request == null) {
			throw new BusinessException(ServiceError.InvalidInput, " Invalid input");
		}
		if (DPDoctorUtils.anyStringEmpty(request.getNutritionPlanId(), request.getSubscriptionPlanId(),
				request.getUserId())) {
			throw new BusinessException(ServiceError.InvalidInput, " Invalid input");
		}

		Response<UserNutritionSubscriptionResponse> response = new Response<UserNutritionSubscriptionResponse>();
		response.setData(nutritionService.addEditUserSubscritionPlan(request));
		return response;
	}

	@Path(PathProxy.NutritionUrl.DELETE_USER_PLAN_SUBSCRIPTION)
	@DELETE
	@ApiOperation(value = PathProxy.NutritionUrl.DELETE_USER_PLAN_SUBSCRIPTION, notes = PathProxy.NutritionUrl.DELETE_USER_PLAN_SUBSCRIPTION)
	public Response<UserNutritionSubscription> deleteUserPlanSubscription(@PathParam("id") String id) {
		if (DPDoctorUtils.anyStringEmpty(id)) {
			throw new BusinessException(ServiceError.InvalidInput, " Invalid input");
		}
		Response<UserNutritionSubscription> response = new Response<UserNutritionSubscription>();
		response.setData(nutritionService.deleteUserSubscritionPlan(id));
		return response;
	}

	@Path(PathProxy.NutritionUrl.GET_USER_PLAN_SUBSCRIPTION)
	@GET
	@ApiOperation(value = PathProxy.NutritionUrl.GET_USER_PLAN_SUBSCRIPTION, notes = PathProxy.NutritionUrl.GET_USER_PLAN_SUBSCRIPTION)
	public Response<UserNutritionSubscriptionResponse> getUserPlanSubscription(@PathParam("id") String id) {

		if (DPDoctorUtils.anyStringEmpty(id)) {
			throw new BusinessException(ServiceError.InvalidInput, " Invalid input");
		}
		Response<UserNutritionSubscriptionResponse> response = new Response<UserNutritionSubscriptionResponse>();
		response.setData(nutritionService.getUserSubscritionPlan(id));
		return response;
	}

	@Path(PathProxy.NutritionUrl.GET_USER_PLAN_SUBSCRIPTIONS)
	@GET
	@ApiOperation(value = PathProxy.NutritionUrl.GET_USER_PLAN_SUBSCRIPTIONS, notes = PathProxy.NutritionUrl.GET_USER_PLAN_SUBSCRIPTIONS)
	public Response<UserNutritionSubscriptionResponse> getUserPlanSubscriptions(@PathParam("userId") String userId,
			@QueryParam("page") int page, @QueryParam("size") int size, @QueryParam("updatedTime") long updatedTime,
			@QueryParam("discarded") @DefaultValue("false") boolean discarded) {
		Response<UserNutritionSubscriptionResponse> response = new Response<UserNutritionSubscriptionResponse>();
		if (DPDoctorUtils.allStringsEmpty(userId)) {
			throw new BusinessException(ServiceError.InvalidInput, " Invalid input");
		}
		response.setDataList(nutritionService.getUserSubscritionPlans(page, size, updatedTime, discarded, userId));
		return response;
	}

	@Path(PathProxy.NutritionUrl.GET_NUTRITION_PLAN)
	@GET
	@ApiOperation(value = PathProxy.NutritionUrl.GET_NUTRITION_PLAN, notes = PathProxy.NutritionUrl.GET_NUTRITION_PLAN)
	public Response<NutritionPlan> getPlan(@QueryParam("page") int page, @QueryParam("size") int size,
			@QueryParam("type") String type, @QueryParam("updatedTime") long updatedTime,
			@QueryParam("discarded") @DefaultValue("true") boolean discarded) {

		Response<NutritionPlan> response = new Response<NutritionPlan>();
		response.setDataList(nutritionService.getNutritionPlans(page, size, type, updatedTime, discarded));

		return response;
	}

	@Path(PathProxy.NutritionUrl.GET_NUTRITION_PLAN_CATEGORY)
	@POST
	@ApiOperation(value = PathProxy.NutritionUrl.GET_NUTRITION_PLAN_CATEGORY, notes = PathProxy.NutritionUrl.GET_NUTRITION_PLAN_CATEGORY)
	public Response<NutritionPlanWithCategoryResponse> getPlanByCategory(NutritionPlanRequest request) {

		Response<NutritionPlanWithCategoryResponse> response = new Response<NutritionPlanWithCategoryResponse>();
		response.setDataList(nutritionService.getNutritionPlanByCategory(request));

		return response;
	}

}
