package com.dpdocter.webservices;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.dpdocter.beans.Activity;
import com.dpdocter.beans.Community;
import com.dpdocter.beans.ContactUs;
import com.dpdocter.beans.FoodPreferences;
import com.dpdocter.beans.GeographicalArea;
import com.dpdocter.beans.LaptopUsage;
import com.dpdocter.beans.Meal;
import com.dpdocter.beans.MobilePhoneUsage;
import com.dpdocter.beans.PatientInfo;
import com.dpdocter.beans.PrimaryDetail;
import com.dpdocter.beans.Resume;
import com.dpdocter.beans.SendAppLink;
import com.dpdocter.beans.Sleep;
import com.dpdocter.beans.SubscriptionDetail;
import com.dpdocter.beans.TvUsage;
import com.dpdocter.beans.WorkHistory;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.services.AdminServices;
import com.dpdocter.services.BirthdaySMSServices;
import com.dpdocter.services.PatientInfoServices;
import com.dpdocter.services.SubscriptionService;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Component
@Path(PathProxy.NUTRITION_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.NUTRITION_URL, description = "Endpoint for patient profile")
public class PatientRegisterApi {

	private static Logger logger = Logger.getLogger(PatientRegisterApi.class.getName());

	@Autowired
	private PatientInfoServices patientInfoServices;

	@Path(value = PathProxy.NutritionUrls.ADD_PATIENT)
	@POST
	@ApiOperation(value = PathProxy.NutritionUrls.ADD_PATIENT, notes = PathProxy.NutritionUrls.ADD_PATIENT, response = Response.class)
	public Response<PatientInfo> addPatient(PatientInfo request) {
		if (request == null) {
			logger.warn("invalidInput");
			throw new BusinessException(ServiceError.InvalidInput, "invalidInput");
		}
		PatientInfo addPatientResponse = patientInfoServices.addPatient(request);
		Response<PatientInfo> response = new Response<PatientInfo>();
		response.setData(addPatientResponse);
		return response;
	}

	@Path(value = PathProxy.NutritionUrls.ADD_EDIT_PATIENT_ACTIVITY)
	@POST
	@ApiOperation(value = PathProxy.NutritionUrls.ADD_EDIT_PATIENT_ACTIVITY, notes = PathProxy.NutritionUrls.ADD_EDIT_PATIENT_ACTIVITY, response = Response.class)
	public Response<Activity> updateActivity(PatientInfo request) {
		if (request == null) {
			logger.warn("invalidInput");
			throw new BusinessException(ServiceError.InvalidInput, "invalidInput");
		}
		Activity addEditPatientActivityResponse = patientInfoServices.updateActivity(request);
		// addEditPatientActivityResponse
		Response<Activity> response = new Response<Activity>();
		response.setData(addEditPatientActivityResponse);
		return response;
	}

	@Path(value = PathProxy.NutritionUrls.ADD_EDIT_PATIENT_COMMUNITY)
	@POST
	@ApiOperation(value = PathProxy.NutritionUrls.ADD_EDIT_PATIENT_COMMUNITY, notes = PathProxy.NutritionUrls.ADD_EDIT_PATIENT_COMMUNITY, response = Response.class)
	public Response<Community> updateCommunity(PatientInfo request) {
		if (request == null) {
			logger.warn("invalidInput");
			throw new BusinessException(ServiceError.InvalidInput, "invalidInput");
		}
		Community addEditPatientCommunityResponse = patientInfoServices.updateCommunity(request);
		Response<Community> response = new Response<Community>();
		response.setData(addEditPatientCommunityResponse);
		return response;
	}

	@Path(value = PathProxy.NutritionUrls.ADD_EDIT_PATIENT_FOOD_PREFERENCES)
	@POST
	@ApiOperation(value = PathProxy.NutritionUrls.ADD_EDIT_PATIENT_FOOD_PREFERENCES, notes = PathProxy.NutritionUrls.ADD_EDIT_PATIENT_FOOD_PREFERENCES, response = Response.class)
	public Response<FoodPreferences> updateFoodPreferences(PatientInfo request) {
		if (request == null) {
			logger.warn("invalidInput");
			throw new BusinessException(ServiceError.InvalidInput, "invalidInput");
		}
		FoodPreferences addEditPatientFoodPreferencesResponse = patientInfoServices.updateFoodPreferences(request);
		Response<FoodPreferences> response = new Response<FoodPreferences>();
		response.setData(addEditPatientFoodPreferencesResponse);
		return response;

	}

	@Path(value = PathProxy.NutritionUrls.ADD_EDIT_PATIENT_GEOGRAPHICAL_AREA)
	@POST
	@ApiOperation(value = PathProxy.NutritionUrls.ADD_EDIT_PATIENT_GEOGRAPHICAL_AREA, notes = PathProxy.NutritionUrls.ADD_EDIT_PATIENT_GEOGRAPHICAL_AREA, response = Response.class)
	public Response<GeographicalArea> updateGeographicalArea(PatientInfo request) {
		if (request == null ) {
			logger.warn("invalidInput");
			throw new BusinessException(ServiceError.InvalidInput, "invalidInput");
		}
		GeographicalArea addEditPatientGeographicalAreaResponse = patientInfoServices.updateGeographicalArea(request);
		Response<GeographicalArea> response = new Response<GeographicalArea>();
		response.setData(addEditPatientGeographicalAreaResponse);
		return response;
	}

	@Path(value = PathProxy.NutritionUrls.ADD_EDIT_PATIENT_LAPTOP_USAGE)
	@POST
	@ApiOperation(value = PathProxy.NutritionUrls.ADD_EDIT_PATIENT_LAPTOP_USAGE, notes = PathProxy.NutritionUrls.ADD_EDIT_PATIENT_LAPTOP_USAGE, response = Response.class)
	public Response<LaptopUsage> updateLaptopUsage(PatientInfo request) {
		if (request == null ) {
			logger.warn("invalidInput");
			throw new BusinessException(ServiceError.InvalidInput, "invalidInput");
		}
		LaptopUsage addEditPatientLaptopUsageResponse = patientInfoServices.updateLaptopUsage(request);
		Response<LaptopUsage> response = new Response<LaptopUsage>();
		response.setData(addEditPatientLaptopUsageResponse);
		return response;
	}

	@Path(value = PathProxy.NutritionUrls.ADD_EDIT_PATIENT_MEAL)
	@POST
	@ApiOperation(value = PathProxy.NutritionUrls.ADD_EDIT_PATIENT_MEAL, notes = PathProxy.NutritionUrls.ADD_EDIT_PATIENT_MEAL, response = Response.class)
	public Response<Meal> updateMeal(PatientInfo request) {
		if (request == null ) {
			logger.warn("invalidInput");
			throw new BusinessException(ServiceError.InvalidInput, "invalidInput");
		}
		Meal addEditPatientMealResponse = patientInfoServices.updateMeal(request);
		Response<Meal> response = new Response<Meal>();
		response.setData(addEditPatientMealResponse);
		return response;
	}

	@Path(value = PathProxy.NutritionUrls.ADD_EDIT_PATIENT_MOBILE_PHONE_USAGE)
	@POST
	@ApiOperation(value = PathProxy.NutritionUrls.ADD_EDIT_PATIENT_MOBILE_PHONE_USAGE, notes = PathProxy.NutritionUrls.ADD_EDIT_PATIENT_MOBILE_PHONE_USAGE, response = Response.class)
	public Response<MobilePhoneUsage> updateMobilePhoneUsage(PatientInfo request) {
		if (request == null) {
			logger.warn("invalidInput");
			throw new BusinessException(ServiceError.InvalidInput, "invalidInput");
		}
		MobilePhoneUsage addEditPatientMobilePhoneUsageResponse = patientInfoServices.updateMobilePhoneUsage(request);
		Response<MobilePhoneUsage> response = new Response<MobilePhoneUsage>();
		response.setData(addEditPatientMobilePhoneUsageResponse);
		return response;
	}

	@Path(value = PathProxy.NutritionUrls.ADD_EDIT_PATIENT_PRIMARY_DETAIL)
	@POST
	@ApiOperation(value = PathProxy.NutritionUrls.ADD_EDIT_PATIENT_PRIMARY_DETAIL, notes = PathProxy.NutritionUrls.ADD_EDIT_PATIENT_PRIMARY_DETAIL, response = Response.class)
	public Response<PrimaryDetail> updatePrimaryDetail(PatientInfo request) {
		if (request == null ) {
			logger.warn("invalidInput");
			throw new BusinessException(ServiceError.InvalidInput, "invalidInput");
		}
		PrimaryDetail addEditPatientPrimaryDetailResponse = patientInfoServices.updatePrimaryDetail(request);
		Response<PrimaryDetail> response = new Response<PrimaryDetail>();
		response.setData(addEditPatientPrimaryDetailResponse);
		return response;
	}

	@Path(value = PathProxy.NutritionUrls.ADD_EDIT_PATIENT_SLEEP)
	@POST
	@ApiOperation(value = PathProxy.NutritionUrls.ADD_EDIT_PATIENT_SLEEP, notes = PathProxy.NutritionUrls.ADD_EDIT_PATIENT_SLEEP, response = Response.class)
	public Response<Sleep> updateSleep(PatientInfo request) {
		if (request == null ) {
			logger.warn("invalidInput");
			throw new BusinessException(ServiceError.InvalidInput, "invalidInput");
		}
		Sleep addEditPatientSleepResponse = patientInfoServices.updateSleep(request);
		Response<Sleep> response = new Response<Sleep>();
		response.setData(addEditPatientSleepResponse);
		return response;
	}

	@Path(value = PathProxy.NutritionUrls.ADD_EDIT_PATIENT_TV_USAGE)
	@POST
	@ApiOperation(value = PathProxy.NutritionUrls.ADD_EDIT_PATIENT_TV_USAGE, notes = PathProxy.NutritionUrls.ADD_EDIT_PATIENT_TV_USAGE, response = Response.class)
	public Response<TvUsage> updateTvUsage(PatientInfo request) {
		if (request == null ) {
			logger.warn("invalidInput");
			throw new BusinessException(ServiceError.InvalidInput, "invalidInput");
		}
		TvUsage addEditPatientTvUsageResponse = patientInfoServices.updateTvUsage(request);
		Response<TvUsage> response = new Response<TvUsage>();
		response.setData(addEditPatientTvUsageResponse);
		return response;
	}

	@Path(value = PathProxy.NutritionUrls.ADD_EDIT_PATIENT_WORK_HISTORY)
	@POST
	@ApiOperation(value = PathProxy.NutritionUrls.ADD_EDIT_PATIENT_WORK_HISTORY, notes = PathProxy.NutritionUrls.ADD_EDIT_PATIENT_WORK_HISTORY, response = Response.class)
	public Response<WorkHistory> updateWorkHistory(PatientInfo request) {
		if (request == null ) {
			logger.warn("invalidInput");
			throw new BusinessException(ServiceError.InvalidInput, "invalidInput");
		}
		WorkHistory addEditPatientWorkHistoryResponse = patientInfoServices.updateWorkHistory(request);
		Response<WorkHistory> response = new Response<WorkHistory>();
		response.setData(addEditPatientWorkHistoryResponse);
		return response;
	}

	@Path(value = PathProxy.NutritionUrls.GET_PATIENT_ACTIVITY)
	@GET
	@ApiOperation(value = PathProxy.NutritionUrls.GET_PATIENT_ACTIVITY, notes = PathProxy.NutritionUrls.GET_PATIENT_ACTIVITY, response = Response.class)
	public Response<Activity> getActivity(@PathParam(value = "patientInfoId") String patientInfoId) {
		if (DPDoctorUtils.anyStringEmpty(patientInfoId)) {
			logger.warn("invalidInput");
			throw new BusinessException(ServiceError.InvalidInput, "invalidInput");
		}
		Activity getPatientActivityResponse = patientInfoServices.getActivity(patientInfoId);
		// addEditPatientActivityResponse
		Response<Activity> response = new Response<Activity>();
		response.setData(getPatientActivityResponse);
		return response;
	}

	@Path(value = PathProxy.NutritionUrls.GET_PATIENT_COMMUNITY)
	@GET
	@ApiOperation(value = PathProxy.NutritionUrls.GET_PATIENT_COMMUNITY, notes = PathProxy.NutritionUrls.GET_PATIENT_COMMUNITY, response = Response.class)
	public Response<Community> getCommunity(@PathParam(value = "patientInfoId") String patientInfoId) {
		if (DPDoctorUtils.anyStringEmpty(patientInfoId)) {
			logger.warn("invalidInput");
			throw new BusinessException(ServiceError.InvalidInput, "invalidInput");
		}
		Community getPatientCommunityResponse = patientInfoServices.getCommunity(patientInfoId);
		Response<Community> response = new Response<Community>();
		response.setData(getPatientCommunityResponse);
		return response;
	}

	@Path(value = PathProxy.NutritionUrls.GET_PATIENT_FOOD_PREFERENCES)
	@GET
	@ApiOperation(value = PathProxy.NutritionUrls.GET_PATIENT_FOOD_PREFERENCES, notes = PathProxy.NutritionUrls.GET_PATIENT_FOOD_PREFERENCES, response = Response.class)
	public Response<FoodPreferences> getFoodPreferences(@PathParam(value = "patientInfoId") String patientInfoId) {
		if (DPDoctorUtils.anyStringEmpty(patientInfoId)) {
			logger.warn("invalidInput");
			throw new BusinessException(ServiceError.InvalidInput, "invalidInput");
		}
		FoodPreferences getPatientFoodPreferencesResponse = patientInfoServices.getFoodPreferences(patientInfoId);
		Response<FoodPreferences> response = new Response<FoodPreferences>();
		response.setData(getPatientFoodPreferencesResponse);
		return response;
	}

	@Path(value = PathProxy.NutritionUrls.GET_PATIENT_LAPTOP_USAGE)
	@GET
	@ApiOperation(value = PathProxy.NutritionUrls.GET_PATIENT_LAPTOP_USAGE, notes = PathProxy.NutritionUrls.GET_PATIENT_LAPTOP_USAGE, response = Response.class)
	public Response<LaptopUsage> getLaptopUsage(@PathParam(value = "patientInfoId") String patientInfoId) {
		if (DPDoctorUtils.anyStringEmpty(patientInfoId)) {
			logger.warn("invalidInput");
			throw new BusinessException(ServiceError.InvalidInput, "invalidInput");
		}
		LaptopUsage getPatientLaptopUsageResponse = patientInfoServices.getLaptopUsage(patientInfoId);
		Response<LaptopUsage> response = new Response<LaptopUsage>();
		response.setData(getPatientLaptopUsageResponse);
		return response;
	}

	@Path(value = PathProxy.NutritionUrls.GET_PATIENT_MEAL)
	@GET
	@ApiOperation(value = PathProxy.NutritionUrls.GET_PATIENT_MEAL, notes = PathProxy.NutritionUrls.GET_PATIENT_MEAL, response = Response.class)
	public Response<Meal> getMeal(@PathParam(value = "patientInfoId") String patientInfoId) {
		if (DPDoctorUtils.anyStringEmpty(patientInfoId)) {
			logger.warn("invalidInput");
			throw new BusinessException(ServiceError.InvalidInput, "invalidInput");
		}
		Meal getPatientMealResponse = patientInfoServices.getMeal(patientInfoId);
		Response<Meal> response = new Response<Meal>();
		response.setData(getPatientMealResponse);
		return response;
	}

	@Path(value = PathProxy.NutritionUrls.GET_PATIENT_MOBILE_PHONE_USAGE)
	@GET
	@ApiOperation(value = PathProxy.NutritionUrls.GET_PATIENT_MOBILE_PHONE_USAGE, notes = PathProxy.NutritionUrls.GET_PATIENT_MOBILE_PHONE_USAGE, response = Response.class)
	public Response<MobilePhoneUsage> getMobilePhoneUsage(@PathParam(value = "patientInfoId") String patientInfoId) {
		if (DPDoctorUtils.anyStringEmpty(patientInfoId)) {
			logger.warn("invalidInput");
			throw new BusinessException(ServiceError.InvalidInput, "invalidInput");
		}
		MobilePhoneUsage getPatientMobilePhoneUsageResponse = patientInfoServices.getMobilePhoneUsage(patientInfoId);
		Response<MobilePhoneUsage> response = new Response<MobilePhoneUsage>();
		response.setData(getPatientMobilePhoneUsageResponse);
		return response;
	}
	
	@Path(value = PathProxy.NutritionUrls.GET_PATIENT_GEOGRAPHICAL_AREA)
	@GET
	@ApiOperation(value = PathProxy.NutritionUrls.GET_PATIENT_GEOGRAPHICAL_AREA, notes = PathProxy.NutritionUrls.GET_PATIENT_GEOGRAPHICAL_AREA, response = Response.class)
	public Response<GeographicalArea> getGeographicalArea(@PathParam(value = "patientInfoId") String patientInfoId) {
		if (DPDoctorUtils.anyStringEmpty(patientInfoId)) {
			logger.warn("invalidInput");
			throw new BusinessException(ServiceError.InvalidInput, "invalidInput");
		}
		GeographicalArea getPatientGeographicalAreaResponse = patientInfoServices.getGeographicalArea(patientInfoId);
		Response<GeographicalArea> response = new Response<GeographicalArea>();
		response.setData(getPatientGeographicalAreaResponse);
		return response;
	}

	@Path(value = PathProxy.NutritionUrls.GET_PATIENT_PRIMARY_DETAIL)
	@GET
	@ApiOperation(value = PathProxy.NutritionUrls.GET_PATIENT_PRIMARY_DETAIL, notes = PathProxy.NutritionUrls.GET_PATIENT_PRIMARY_DETAIL, response = Response.class)
	public Response<PrimaryDetail> getPrimaryDetail(@PathParam(value = "patientInfoId") String patientInfoId) {
		if (DPDoctorUtils.anyStringEmpty(patientInfoId)) {
			logger.warn("invalidInput");
			throw new BusinessException(ServiceError.InvalidInput, "invalidInput");
		}
		PrimaryDetail getPatientPrimaryDetailResponse = patientInfoServices.getPrimaryDetail(patientInfoId);
		Response<PrimaryDetail> response = new Response<PrimaryDetail>();
		response.setData(getPatientPrimaryDetailResponse);
		return response;
	}

	@Path(value = PathProxy.NutritionUrls.GET_PATIENT_SLEEP)
	@GET
	@ApiOperation(value = PathProxy.NutritionUrls.GET_PATIENT_SLEEP, notes = PathProxy.NutritionUrls.GET_PATIENT_SLEEP, response = Response.class)
	public Response<Sleep> getSleep(@PathParam(value = "patientInfoId") String patientInfoId) {
		if (DPDoctorUtils.anyStringEmpty(patientInfoId)) {
			logger.warn("invalidInput");
			throw new BusinessException(ServiceError.InvalidInput, "invalidInput");
		}
		Sleep getPatientSleepResponse = patientInfoServices.getSleep(patientInfoId);
		Response<Sleep> response = new Response<Sleep>();
		response.setData(getPatientSleepResponse);
		return response;
	}

	@Path(value = PathProxy.NutritionUrls.GET_PATIENT_TV_USAGE)
	@GET
	@ApiOperation(value = PathProxy.NutritionUrls.GET_PATIENT_TV_USAGE, notes = PathProxy.NutritionUrls.GET_PATIENT_TV_USAGE, response = Response.class)
	public Response<TvUsage> getTvUsage(@PathParam(value = "patientInfoId") String patientInfoId) {
		if (DPDoctorUtils.anyStringEmpty(patientInfoId)) {
			logger.warn("invalidInput");
			throw new BusinessException(ServiceError.InvalidInput, "invalidInput");
		}
		TvUsage getPatientTvUsageResponse = patientInfoServices.getTvUsage(patientInfoId);
		Response<TvUsage> response = new Response<TvUsage>();
		response.setData(getPatientTvUsageResponse);
		return response;
	}

	@Path(value = PathProxy.NutritionUrls.GET_PATIENT_WORK_HISTORY)
	@GET
	@ApiOperation(value = PathProxy.NutritionUrls.GET_PATIENT_WORK_HISTORY, notes = PathProxy.NutritionUrls.GET_PATIENT_WORK_HISTORY, response = Response.class)
	public Response<WorkHistory> getWorkHistory(@PathParam(value = "patientInfoId") String patientInfoId) {
		if (DPDoctorUtils.anyStringEmpty(patientInfoId)) {
			logger.warn("invalidInput");
			throw new BusinessException(ServiceError.InvalidInput, "invalidInput");
		}
		WorkHistory getPatientWorkHistoryResponse = patientInfoServices.getWorkHistory(patientInfoId);
		Response<WorkHistory> response = new Response<WorkHistory>();
		response.setData(getPatientWorkHistoryResponse);
		return response;
	}

	@Path(value = PathProxy.NutritionUrls.GET_PATIENT)
	@GET
	@ApiOperation(value = PathProxy.NutritionUrls.GET_PATIENT, notes = PathProxy.NutritionUrls.GET_PATIENT, response = Response.class)
	public Response<PatientInfo> getPatient(@PathParam(value = "patientInfoId") String patientInfoId) {
		if (DPDoctorUtils.anyStringEmpty(patientInfoId)) {
			logger.warn("invalidInput");
			throw new BusinessException(ServiceError.InvalidInput, "invalidInput");
		}
		PatientInfo patientInfo = patientInfoServices.findById(patientInfoId);
		Response<PatientInfo> response = new Response<PatientInfo>();
		response.setData(patientInfo);
		return response;
	}
	
	@Path(value = PathProxy.NutritionUrls.GET_ALL_PATIENT)
	@GET
	@ApiOperation(value = PathProxy.NutritionUrls.GET_ALL_PATIENT, notes = PathProxy.NutritionUrls.GET_ALL_PATIENT, response = Response.class)
	public Response<List<PatientInfo>> getAllPatients(@PathParam(value = "doctorId") String doctorId) {
		if (DPDoctorUtils.anyStringEmpty(doctorId)) {
			logger.warn("invalidInput");
			throw new BusinessException(ServiceError.InvalidInput, "invalidInput");
		}
		List<PatientInfo> patientInfo = patientInfoServices.findAll(doctorId);
		Response<List<PatientInfo>> response = new Response<List<PatientInfo>>();
		response.setData(patientInfo);
		return response;
	}

}
