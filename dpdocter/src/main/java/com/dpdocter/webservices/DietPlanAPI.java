package com.dpdocter.webservices;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

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

import com.dpdocter.beans.DietPlan;
import com.dpdocter.beans.DietPlanTemplate;
import com.dpdocter.beans.Language;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.services.DietPlansService;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value = PathProxy.DIET_PLAN_BASE_URL,produces = MediaType.APPLICATION_JSON ,consumes = MediaType.APPLICATION_JSON)
@Api(value = PathProxy.DIET_PLAN_BASE_URL, description = "Endpoint for Diet Plan")
public class DietPlanAPI {

	private static Logger logger = LogManager.getLogger(DietPlanAPI.class.getName());

	@Autowired
	private DietPlansService dietPlansService;

	
	@PostMapping(value = PathProxy.DietPlanUrls.ADD_EDIT_DIET_PLAN)
	@ApiOperation(value = PathProxy.DietPlanUrls.ADD_EDIT_DIET_PLAN, notes = PathProxy.DietPlanUrls.ADD_EDIT_DIET_PLAN)
	public Response<DietPlan> addEditDietPlan(@RequestBody DietPlan request) {

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

	
	@GetMapping(value = PathProxy.DietPlanUrls.GET_DIET_PLANS)
	@ApiOperation(value = PathProxy.DietPlanUrls.GET_DIET_PLANS, notes = PathProxy.DietPlanUrls.GET_DIET_PLANS)
	public Response<DietPlan> getDietPlans(@RequestParam("locationId") String locationId, @RequestParam("page") int page,
			@RequestParam("size") int size, @RequestParam("patientId") String patientId,
			@RequestParam("doctorId") String doctorId, @RequestParam("hospitalId") String hospitalId,
			@RequestParam("updatedTime") long updatedTime, @RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded) {
		Response<DietPlan> response = new Response<DietPlan>();
		response.setDataList(dietPlansService.getDietPlans(page, size, patientId, doctorId, hospitalId, locationId,
				updatedTime, discarded));
		return response;
	}
	
	
	@GetMapping(value = PathProxy.DietPlanUrls.GET_DIET_PLANS_FOR_PATIENT)
	@ApiOperation(value = PathProxy.DietPlanUrls.GET_DIET_PLANS_FOR_PATIENT, notes = PathProxy.DietPlanUrls.GET_DIET_PLANS_FOR_PATIENT)
	public Response<DietPlan> getDietPlans( @RequestParam("page") int page,
			@RequestParam("size") int size, @RequestParam("patientId") String patientId,
			@RequestParam("updatedTime") long updatedTime, @RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded) {
		Response<DietPlan> response = new Response<DietPlan>();
		response.setDataList(dietPlansService.getDietPlansForPatient(page, size, patientId, updatedTime, discarded));
		return response;
	}

	
	@DeleteMapping(value = PathProxy.DietPlanUrls.DELETE_DIET_PLAN)
	@ApiOperation(value = PathProxy.DietPlanUrls.DELETE_DIET_PLAN, notes = PathProxy.DietPlanUrls.DELETE_DIET_PLAN)
	public Response<DietPlan> deleteDietPlan(@PathVariable("planId") String planId,
			@RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded) {

		if (planId == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		Response<DietPlan> response = new Response<DietPlan>();
		response.setData(dietPlansService.discardDietPlan(planId, discarded));
		return response;
	}

	
	@GetMapping(value = PathProxy.DietPlanUrls.GET_DIET_PLAN)
	@ApiOperation(value = PathProxy.DietPlanUrls.GET_DIET_PLAN, notes = PathProxy.DietPlanUrls.GET_DIET_PLAN)
	public Response<DietPlan> getDietPlan(@PathVariable("planId") String planId, @RequestParam("languageId") String languageId) {

		if (planId == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		Response<DietPlan> response = new Response<DietPlan>();
		response.setData(dietPlansService.getDietPlanById(planId, languageId));

		return response;
	}

	
	@GetMapping(value = PathProxy.DietPlanUrls.DOWNLOAD_DIET_PLAN)
	@ApiOperation(value = PathProxy.DietPlanUrls.DOWNLOAD_DIET_PLAN, notes = PathProxy.DietPlanUrls.DOWNLOAD_DIET_PLAN)
	public Response<String> downloadDietPlan(@PathVariable("planId") String planId) {

		if (planId == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		Response<String> response = new Response<String>();
		response.setData(dietPlansService.downloadDietPlan(planId));
		return response;
	}

	
	@GetMapping(value = PathProxy.DietPlanUrls.SEND_DIET_PLAN_EMAIL)
	@ApiOperation(value = PathProxy.DietPlanUrls.SEND_DIET_PLAN_EMAIL, notes = PathProxy.DietPlanUrls.SEND_DIET_PLAN_EMAIL)
	public Response<Boolean> emailDietPlan(@PathVariable("planId") String planId,
			@RequestParam("emailAddress") String emailAddress) {

		if (emailAddress == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		Response<Boolean> response = new Response<Boolean>();
		response.setData(dietPlansService.emailDietPlan(emailAddress, planId));
		return response;
	}

	
	@PostMapping(value = PathProxy.DietPlanUrls.ADD_EDIT_DIET_PLAN_TEMPLATE)
	@ApiOperation(value = PathProxy.DietPlanUrls.ADD_EDIT_DIET_PLAN_TEMPLATE, notes = PathProxy.DietPlanUrls.ADD_EDIT_DIET_PLAN_TEMPLATE)
	public Response<DietPlanTemplate> addEditDietPlanTemplate(@RequestBody DietPlanTemplate request) {

		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		if (DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId(),
				request.getHospitalId())) {
			logger.warn("doctorId,locationId and hospitalId should not be null or empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"doctorId,locationId and hospitalId should not be null or empty");
		}

		Response<DietPlanTemplate> response = new Response<DietPlanTemplate>();
		response.setData(dietPlansService.addEditDietPlanTemplate(request));
		return response;
	}

	
	@GetMapping(value = PathProxy.DietPlanUrls.GET_DIET_PLAN_TEMPLATES)
	@ApiOperation(value = PathProxy.DietPlanUrls.GET_DIET_PLAN_TEMPLATES, notes = PathProxy.DietPlanUrls.GET_DIET_PLAN_TEMPLATES)
	public Response<DietPlanTemplate> getDietPlanTemplates(@RequestParam("locationId") String locationId, @RequestParam("page") int page,
			@RequestParam("size") int size, 
			@RequestParam("doctorId") String doctorId, @RequestParam("hospitalId") String hospitalId,
			@RequestParam("updatedTime") long updatedTime, @RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded,
			@RequestParam("gender") String gender, @RequestParam("country") String country, @RequestParam("fromAge") Double fromAge,
			@RequestParam("toAge") Double toAge, @RequestParam("community") String community,
			@RequestParam("type") String type, @RequestParam("pregnancyCategory") String pregnancyCategory,
			@RequestParam("searchTerm") String searchTerm, @RequestParam("foodPreference") String foodPreference,
			@MatrixParam("disease") List<String> disease, @RequestParam("bmiFrom") Double bmiFrom, @RequestParam("bmiTo") Double bmiTo,
			@RequestParam("languageId") String languageId, @RequestParam("age") Double age, @RequestParam("bmi") Double bmi,
			@RequestParam("allDisease") @DefaultValue("false") boolean allDisease) {
		
		Response<DietPlanTemplate> response = dietPlansService.getDietPlanTemplates(page, size, doctorId, hospitalId, locationId,
				updatedTime, discarded, gender, country, fromAge, toAge, community, type, pregnancyCategory, searchTerm, 
				foodPreference, disease, bmiFrom, bmiTo, languageId, age, bmi, allDisease);
		return response;
	}

	
	@DeleteMapping(value = PathProxy.DietPlanUrls.DELETE_DIET_PLAN_TEMPLATE)
	@ApiOperation(value = PathProxy.DietPlanUrls.DELETE_DIET_PLAN_TEMPLATE, notes = PathProxy.DietPlanUrls.DELETE_DIET_PLAN_TEMPLATE)
	public Response<DietPlanTemplate> deleteDietPlanTemplate(@PathVariable("planId") String planId,
			@RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded) {

		if (planId == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		Response<DietPlanTemplate> response = new Response<DietPlanTemplate>();
		response.setData(dietPlansService.deleteDietPlanTemplate(planId, discarded));
		return response;
	}

	
	@GetMapping(value = PathProxy.DietPlanUrls.GET_DIET_PLAN_TEMPLATE)
	@ApiOperation(value = PathProxy.DietPlanUrls.GET_DIET_PLAN_TEMPLATE, notes = PathProxy.DietPlanUrls.GET_DIET_PLAN_TEMPLATE)
	public Response<DietPlanTemplate> getDietPlanTemplateById(@PathVariable("planId") String planId, @RequestParam("languageId") String languageId) {

		if (planId == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		Response<DietPlanTemplate> response = new Response<DietPlanTemplate>();
		response.setData(dietPlansService.getDietPlanTemplateById(planId, languageId));

		return response;
	}
	
	
	@GetMapping(value = PathProxy.DietPlanUrls.UPDATE_DIET_PLAN_TEMPLATE)
	public Response<DietPlanTemplate> updateDietPlanTemplate() {
		Response<DietPlanTemplate> response = new Response<DietPlanTemplate>();
		response.setData(dietPlansService.updateDietPlanTemplate());

		return response;
	}
	
	
	@GetMapping(value = PathProxy.DietPlanUrls.GET_LANGUAGES)
	@ApiOperation(value = PathProxy.DietPlanUrls.GET_LANGUAGES, notes = PathProxy.DietPlanUrls.GET_LANGUAGES)
	public Response<Language> getLanguages(@RequestParam("size") int size, @RequestParam("page") int page,
			@RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded, @RequestParam("searchTerm") String searchTerm) {
		Integer count = dietPlansService.countLanguage(discarded, searchTerm);
		Response<Language> response = new Response<Language>();
		if (count > 0)
			response.setDataList(dietPlansService.getLanguages(size, page, discarded, searchTerm));
		response.setCount(count);
		return response;
	}
}
