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

import com.dpdocter.beans.AcademicProfile;
import com.dpdocter.beans.DentalAssessment;
import com.dpdocter.beans.DoctorSchoolAssociation;
import com.dpdocter.beans.ENTAssessment;
import com.dpdocter.beans.EyeAssessment;
import com.dpdocter.beans.GrowthAssessmentAndGeneralBioMetrics;
import com.dpdocter.beans.NutritionAssessment;
import com.dpdocter.beans.NutritionRDA;
import com.dpdocter.beans.PhysicalAssessment;
import com.dpdocter.beans.RegistrationDetails;
import com.dpdocter.beans.UserTreatment;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.response.AcadamicClassResponse;
import com.dpdocter.response.NutritionSchoolAssociationResponse;
import com.dpdocter.response.UserAssessment;
import com.dpdocter.services.CampVisitService;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value=PathProxy.CAMP_VISIT_BASE_URL,produces = MediaType.APPLICATION_JSON_VALUE ,consumes = MediaType.APPLICATION_JSON_VALUE)
@Api(value = PathProxy.CAMP_VISIT_BASE_URL, description = "Endpoint for camp visit")
public class CampVisitAPI {

	private static Logger logger = LogManager.getLogger(CampVisitAPI.class.getName());

	@Autowired
	private CampVisitService campVisitService;

	@GetMapping(value = PathProxy.CampVisitUrls.GET_GROWTH_ASSESSMENT_AND_GENERAL_BIO_METRICS_BY_ID)
	@ApiOperation(value = PathProxy.CampVisitUrls.GET_GROWTH_ASSESSMENT_AND_GENERAL_BIO_METRICS_BY_ID, notes = PathProxy.CampVisitUrls.GET_GROWTH_ASSESSMENT_AND_GENERAL_BIO_METRICS_BY_ID)
	public Response<GrowthAssessmentAndGeneralBioMetrics> getGrowthAssessmentAndGeneralBioMetrics(
			@PathVariable("id") String id) {
		Response<GrowthAssessmentAndGeneralBioMetrics> response = new Response<GrowthAssessmentAndGeneralBioMetrics>();
		if (DPDoctorUtils.anyStringEmpty(id)) {
			throw new BusinessException(ServiceError.InvalidInput, "Id should not null or Empty");
		}
		response.setData(campVisitService.getGrowthAssessmentAndGeneralBioMetricsById(id));
		return response;
	}

	@GetMapping(value = PathProxy.CampVisitUrls.GET_GROWTH_ASSESSMENT_AND_GENERAL_BIO_METRICS_LIST)
	@ApiOperation(value = PathProxy.CampVisitUrls.GET_GROWTH_ASSESSMENT_AND_GENERAL_BIO_METRICS_LIST, notes = PathProxy.CampVisitUrls.GET_GROWTH_ASSESSMENT_AND_GENERAL_BIO_METRICS_LIST)
	public Response<GrowthAssessmentAndGeneralBioMetrics> getGrowthAssessmentAndGeneralBioMetricsList(
			@RequestParam(value = "branchId") String branchId, @RequestParam(value = "schoolId") String schoolId,
			@RequestParam(value = "academicProfileId") String academicProfileId,
			@RequestParam(value = "doctorId") String doctorId, @RequestParam(value = "page") int page,
			@RequestParam(value = "size") int size,
			@RequestParam(value = "updatedTime") @DefaultValue("0") String updatedTime,
			@RequestParam(value = "discarded") Boolean discarded) {
		Response<GrowthAssessmentAndGeneralBioMetrics> response = new Response<GrowthAssessmentAndGeneralBioMetrics>();
		response.setDataList(campVisitService.getGrowthAssessmentAndGeneralBioMetricsList(academicProfileId, schoolId,
				branchId, doctorId, updatedTime, page, size, discarded));
		response.setCount(campVisitService.getGrowthAssessmentAndGeneralBioMetricsListCount(academicProfileId, schoolId,
				branchId, doctorId, updatedTime, page, size, discarded));
		return response;
	}

	@GetMapping(value = PathProxy.CampVisitUrls.GET_PHYSICAL_ASSESSMENT_BY_ID)
	@ApiOperation(value = PathProxy.CampVisitUrls.GET_PHYSICAL_ASSESSMENT_BY_ID, notes = PathProxy.CampVisitUrls.GET_PHYSICAL_ASSESSMENT_BY_ID)
	public Response<PhysicalAssessment> getPhysicalAssessment(@PathVariable("id") String id) {
		Response<PhysicalAssessment> response = new Response<PhysicalAssessment>();
		if (DPDoctorUtils.anyStringEmpty(id)) {
			throw new BusinessException(ServiceError.InvalidInput, "Id should not null or Empty");
		}
		response.setData(campVisitService.getPhysicalAssessmentById(id));
		return response;
	}

	@GetMapping(value = PathProxy.CampVisitUrls.GET_PHYSICAL_ASSESSMENT_LIST)
	@ApiOperation(value = PathProxy.CampVisitUrls.GET_PHYSICAL_ASSESSMENT_LIST, notes = PathProxy.CampVisitUrls.GET_PHYSICAL_ASSESSMENT_LIST)
	public Response<PhysicalAssessment> getPhysicalAssessmentList(@RequestParam(value = "branchId") String branchId,
			@RequestParam(value = "schoolId") String schoolId,
			@RequestParam(value = "academicProfileId") String academicProfileId,
			@RequestParam(value = "doctorId") String doctorId, @RequestParam(value = "page") int page,
			@RequestParam(value = "size") int size,
			@RequestParam(value = "updatedTime") @DefaultValue("0") String updatedTime,
			@RequestParam(value = "discarded") Boolean discarded) {
		Response<PhysicalAssessment> response = new Response<PhysicalAssessment>();
		response.setDataList(campVisitService.getPhysicalAssessmentList(academicProfileId, schoolId, branchId, doctorId,
				updatedTime, page, size, discarded));
		response.setCount(campVisitService.getPhysicalAssessmentCount(academicProfileId, schoolId, branchId, doctorId,
				updatedTime, page, size, discarded));
		return response;
	}

	@GetMapping(value = PathProxy.CampVisitUrls.GET_ENT_ASSESSMENT_BY_ID)
	@ApiOperation(value = PathProxy.CampVisitUrls.GET_ENT_ASSESSMENT_BY_ID, notes = PathProxy.CampVisitUrls.GET_ENT_ASSESSMENT_BY_ID)
	public Response<ENTAssessment> getENTAssessment(@PathVariable("id") String id) {
		Response<ENTAssessment> response = new Response<ENTAssessment>();
		if (DPDoctorUtils.anyStringEmpty(id)) {
			throw new BusinessException(ServiceError.InvalidInput, "Id should not null or Empty");
		}
		response.setData(campVisitService.getENTAssessmentById(id));
		return response;
	}

	@GetMapping(value = PathProxy.CampVisitUrls.GET_ENT_ASSESSMENT_LIST)
	@ApiOperation(value = PathProxy.CampVisitUrls.GET_ENT_ASSESSMENT_LIST, notes = PathProxy.CampVisitUrls.GET_ENT_ASSESSMENT_LIST)
	public Response<ENTAssessment> getENTAssessmentList(@RequestParam(value = "branchId") String branchId,
			@RequestParam(value = "schoolId") String schoolId,
			@RequestParam(value = "academicProfileId") String academicProfileId,
			@RequestParam(value = "doctorId") String doctorId, @RequestParam(value = "page") int page,
			@RequestParam(value = "size") int size,
			@RequestParam(value = "updatedTime") @DefaultValue("0") String updatedTime,
			@RequestParam(value = "discarded") Boolean discarded) {
		Response<ENTAssessment> response = new Response<ENTAssessment>();
		response.setDataList(campVisitService.getENTAssessmentList(academicProfileId, schoolId, branchId, doctorId,
				updatedTime, page, size, discarded));
		response.setCount(campVisitService.getENTAssessmentListCount(academicProfileId, schoolId, branchId, doctorId,
				updatedTime, page, size, discarded));
		return response;
	}

	@GetMapping(value = PathProxy.CampVisitUrls.GET_DENTAL_ASSESSMENT_BY_ID)
	@ApiOperation(value = PathProxy.CampVisitUrls.GET_DENTAL_ASSESSMENT_BY_ID, notes = PathProxy.CampVisitUrls.GET_DENTAL_ASSESSMENT_BY_ID)
	public Response<DentalAssessment> getDentalAssessment(@PathVariable("id") String id) {
		Response<DentalAssessment> response = new Response<DentalAssessment>();
		if (DPDoctorUtils.anyStringEmpty(id)) {
			throw new BusinessException(ServiceError.InvalidInput, "Id should not null or Empty");
		}
		response.setData(campVisitService.getDentalAssessmentById(id));
		return response;
	}

	@GetMapping(value = PathProxy.CampVisitUrls.GET_DENTAL_ASSESSMENT_LIST)
	@ApiOperation(value = PathProxy.CampVisitUrls.GET_DENTAL_ASSESSMENT_LIST, notes = PathProxy.CampVisitUrls.GET_DENTAL_ASSESSMENT_LIST)
	public Response<DentalAssessment> getDentalAssessmentList(@RequestParam(value = "branchId") String branchId,
			@RequestParam(value = "schoolId") String schoolId,
			@RequestParam(value = "academicProfileId") String academicProfileId,
			@RequestParam(value = "doctorId") String doctorId, @RequestParam(value = "page") int page,
			@RequestParam(value = "size") int size,
			@RequestParam(value = "updatedTime") @DefaultValue("0") String updatedTime,
			@RequestParam(value = "discarded") Boolean discarded) {
		Response<DentalAssessment> response = new Response<DentalAssessment>();
		response.setDataList(campVisitService.getDentalAssessmentList(academicProfileId, schoolId, branchId, doctorId,
				updatedTime, page, size, discarded));
		response.setCount(campVisitService.getDentalAssessmentListCount(academicProfileId, schoolId, branchId, doctorId,
				updatedTime, page, size, discarded));
		return response;
	}

	@GetMapping(value = PathProxy.CampVisitUrls.GET_EYE_ASSESSMENT_BY_ID)
	@ApiOperation(value = PathProxy.CampVisitUrls.GET_EYE_ASSESSMENT_BY_ID, notes = PathProxy.CampVisitUrls.GET_EYE_ASSESSMENT_BY_ID)
	public Response<EyeAssessment> getEyeAssessment(@PathVariable("id") String id) {
		Response<EyeAssessment> response = new Response<EyeAssessment>();
		if (DPDoctorUtils.anyStringEmpty(id)) {
			throw new BusinessException(ServiceError.InvalidInput, "Id should not null or Empty");
		}
		response.setData(campVisitService.getEyeAssessmentById(id));
		return response;
	}

	@GetMapping(value = PathProxy.CampVisitUrls.GET_EYE_ASSESSMENT_LIST)
	@ApiOperation(value = PathProxy.CampVisitUrls.GET_EYE_ASSESSMENT_LIST, notes = PathProxy.CampVisitUrls.GET_EYE_ASSESSMENT_LIST)
	public Response<EyeAssessment> getEyeAssessmentList(@RequestParam(value = "branchId") String branchId,
			@RequestParam(value = "schoolId") String schoolId,
			@RequestParam(value = "academicProfileId") String academicProfileId,
			@RequestParam(value = "doctorId") String doctorId, @RequestParam(value = "page") int page,
			@RequestParam(value = "size") int size,
			@RequestParam(value = "updatedTime") @DefaultValue("0") String updatedTime,
			@RequestParam(value = "discarded") Boolean discarded) {
		Response<EyeAssessment> response = new Response<EyeAssessment>();
		response.setDataList(campVisitService.getEyeAssessmentList(academicProfileId, schoolId, branchId, doctorId,
				updatedTime, page, size, discarded));
		response.setCount(campVisitService.getEyeAssessmentListCount(academicProfileId, schoolId, branchId, doctorId,
				updatedTime, page, size, discarded));
		return response;
	}

	@GetMapping(value = PathProxy.CampVisitUrls.GET_NUTRITION_ASSESSMENT_BY_ID)
	@ApiOperation(value = PathProxy.CampVisitUrls.GET_NUTRITION_ASSESSMENT_BY_ID, notes = PathProxy.CampVisitUrls.GET_NUTRITION_ASSESSMENT_BY_ID)
	public Response<NutritionAssessment> getNutritionAssessment(@PathVariable("id") String id) {
		Response<NutritionAssessment> response = new Response<NutritionAssessment>();
		if (DPDoctorUtils.anyStringEmpty(id)) {
			throw new BusinessException(ServiceError.InvalidInput, "Id should not null or Empty");
		}
		response.setData(campVisitService.getNutritionAssessmentById(id));
		return response;
	}

	@GetMapping(value = PathProxy.CampVisitUrls.GET_NUTRITION_ASSESSMENT_LIST)
	@ApiOperation(value = PathProxy.CampVisitUrls.GET_NUTRITION_ASSESSMENT_LIST, notes = PathProxy.CampVisitUrls.GET_NUTRITION_ASSESSMENT_LIST)
	public Response<NutritionAssessment> getNutritionAssessmentList(@RequestParam(value = "branchId") String branchId,
			@RequestParam(value = "schoolId") String schoolId,
			@RequestParam(value = "academicProfileId") String academicProfileId,
			@RequestParam(value = "doctorId") String doctorId, @RequestParam(value = "page") int page,
			@RequestParam(value = "size") int size,
			@RequestParam(value = "updatedTime") @DefaultValue("0") String updatedTime,
			@RequestParam(value = "discarded") Boolean discarded,
			@RequestParam(value = "recipe") String recipe) {
		Response<NutritionAssessment> response = new Response<NutritionAssessment>();
		response.setDataList(campVisitService.getNutritionAssessmentList(academicProfileId, schoolId, branchId,
				doctorId, updatedTime, page, size, discarded, recipe));
		response.setCount(campVisitService.getNutritionAssessmentListCount(academicProfileId, schoolId, branchId,
				doctorId, updatedTime, page, size, discarded, recipe));
		return response;
	}

	@GetMapping(PathProxy.CampVisitUrls.GET_ACADAMIC_PROFILE)
	@ApiOperation(value = PathProxy.CampVisitUrls.GET_ACADAMIC_PROFILE, notes = PathProxy.CampVisitUrls.GET_ACADAMIC_PROFILE)
	public Response<AcademicProfile> getAcadamicProfile(@PathVariable("profileType") String profileType,
			@PathVariable("branchId") String branchId, @PathVariable("schoolId") String schoolId,
			@RequestParam("page") int page, @RequestParam("size") int size, @RequestParam("classId") String classId,
			@RequestParam("sectionId") String sectionId, @RequestParam("searchTerm") String searchTerm,
			@RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded, @RequestParam("userId") String userId,
			@RequestParam("updatedTime") String updatedTime, @RequestParam("assesmentType") String assesmentType, @RequestParam("department") String department,
			@RequestParam("departmentValue") String departmentValue) {

		if (DPDoctorUtils.anyStringEmpty(branchId, schoolId, profileType)) {
			throw new BusinessException(ServiceError.InvalidInput,
					"schoolId,branchId,profileType should not null or Empty");
		}
		Response<AcademicProfile> response = new Response<AcademicProfile>();
		if (profileType.equalsIgnoreCase("STUDENT")) {
			response.setDataList(campVisitService.getStudentProfile(page, size, branchId, schoolId, classId, sectionId,
					searchTerm, discarded, profileType, userId, updatedTime, assesmentType, department, departmentValue));
			response.setCount(campVisitService.countStudentProfile(branchId, schoolId, classId, sectionId, searchTerm,
					discarded, profileType, userId, updatedTime));
		} else if (profileType.equalsIgnoreCase("TEACHER")) {
			response.setDataList(campVisitService.getTeacherProfile(page, size, branchId, schoolId, searchTerm,
					discarded, profileType, userId, updatedTime));
			response.setCount(campVisitService.countTeacherProfile(branchId, schoolId, searchTerm, discarded,
					profileType, userId, updatedTime));

		}
		return response;
	}

	@GetMapping(value = PathProxy.CampVisitUrls.GET_ACADAMIC_PROFILE_BY_ID)
	@ApiOperation(value = PathProxy.CampVisitUrls.GET_ACADAMIC_PROFILE_BY_ID, notes = PathProxy.CampVisitUrls.GET_ACADAMIC_PROFILE_BY_ID)
	public Response<RegistrationDetails> getAcadamicProfile(@PathVariable("id") String id) {

		if (DPDoctorUtils.anyStringEmpty(id)) {
			throw new BusinessException(ServiceError.InvalidInput, "id,profileType should not null or Empty");
		}
		Response<RegistrationDetails> response = new Response<RegistrationDetails>();
		response.setData(campVisitService.getAcadamicProfile(id));
		return response;
	}

	@GetMapping(value = PathProxy.CampVisitUrls.GET_ASSOCIATIONS_FOR_NUTRITION)
	@ApiOperation(value = PathProxy.CampVisitUrls.GET_ASSOCIATIONS_FOR_NUTRITION, notes = PathProxy.CampVisitUrls.GET_ASSOCIATIONS_FOR_NUTRITION)
	public Response<NutritionSchoolAssociationResponse> getNutritionAssociations(@RequestParam("page") int page,
			@RequestParam("size") int size, @RequestParam("classId") String classId,
			@RequestParam("doctorId") String doctorId, @RequestParam("searchTerm") String searchTerm,
			@RequestParam("updatedTime") String updatedTime) {

		if (DPDoctorUtils.anyStringEmpty(doctorId)) {
			throw new BusinessException(ServiceError.InvalidInput, "id should not null or Empty");
		}
		Response<NutritionSchoolAssociationResponse> response = new Response<NutritionSchoolAssociationResponse>();
		response.setDataList(campVisitService.getNutritionAssociations(page, size, doctorId, searchTerm, updatedTime));
		return response;
	}

	@GetMapping(value = PathProxy.CampVisitUrls.GET_ACADAMIC_CLASSES)
	@ApiOperation(value = PathProxy.CampVisitUrls.GET_ACADAMIC_CLASSES, notes = PathProxy.CampVisitUrls.GET_ACADAMIC_CLASSES)
	public Response<AcadamicClassResponse> getAcadamicClass(@PathVariable("branchId") String branchId,
			@PathVariable("schoolId") String schoolId, @RequestParam("page") int page, @RequestParam("size") int size,
			@RequestParam("searchTerm") String searchTerm, @RequestParam(required = false, value ="discarded",defaultValue = "true" )boolean discarded) {
		Response<AcadamicClassResponse> response = new Response<AcadamicClassResponse>();
		response.setDataList(campVisitService.getAcadamicClass(page, size, branchId, schoolId, searchTerm, discarded));
		response.setCount(campVisitService.countAcadamicClass(branchId, schoolId, searchTerm, discarded));
		return response;
	}
	
	@GetMapping(value = PathProxy.CampVisitUrls.GET_DOCTOR_ACADAMIC_PROFILE)
	@ApiOperation(value = PathProxy.CampVisitUrls.GET_DOCTOR_ACADAMIC_PROFILE, notes = PathProxy.CampVisitUrls.GET_DOCTOR_ACADAMIC_PROFILE)
	public Response<AcademicProfile> getAcadamicProfile(
			@PathVariable("userId") String userId, @RequestParam("page") int page, @RequestParam("size") int size,
			@RequestParam("searchTerm") String searchTerm, @RequestParam(required = false, value ="discarded", defaultValue = "true" )boolean discarded) {
		Response<AcademicProfile> response = new Response<AcademicProfile>();
		response.setDataList(campVisitService.getProfile(page, size, userId, discarded, searchTerm));
		response.setCount(campVisitService.countProfile(userId, discarded, searchTerm));
		return response;
	}

	@GetMapping(value = PathProxy.CampVisitUrls.GET_RDA_FOR_USER)
	@ApiOperation(value = PathProxy.CampVisitUrls.GET_RDA_FOR_USER, notes = PathProxy.CampVisitUrls.GET_RDA_FOR_USER)
	public Response<NutritionRDA> getRDAForUser(@PathVariable("academicProfileId") String academicProfileId, 
			@RequestParam("doctorId") String doctorId, @RequestParam("locationId") String locationId,
			@RequestParam("hospitalId") String hospitalId) {
		if (academicProfileId == null) {
			throw new BusinessException(ServiceError.InvalidInput, " Invalid input");
		}

		Response<NutritionRDA> response = new Response<NutritionRDA>();
		response.setData(campVisitService.getRDAForUser(academicProfileId, doctorId, locationId, hospitalId));
		return response;
	}
	
	@GetMapping(value = PathProxy.CampVisitUrls.GET_USER_ASSESSMENT)
	@ApiOperation(value = PathProxy.CampVisitUrls.GET_USER_ASSESSMENT, notes = PathProxy.CampVisitUrls.GET_USER_ASSESSMENT)
	public Response<UserAssessment> getUserAssessment(@PathVariable("academicProfileId") String academicProfileId,
			@RequestParam(value = "doctorId") String doctorId) {
		Response<UserAssessment> response = new Response<UserAssessment>();
		if (DPDoctorUtils.anyStringEmpty(academicProfileId)) {
			throw new BusinessException(ServiceError.InvalidInput, "Id should not null or Empty");
		}
		response.setData(campVisitService.getUserAssessment(academicProfileId, doctorId));
		return response;
	}
	
	@GetMapping(value = PathProxy.CampVisitUrls.GET_ASSOCIATIONS_FOR_DOCTOR)
	@ApiOperation(value = PathProxy.CampVisitUrls.GET_ASSOCIATIONS_FOR_DOCTOR, notes = PathProxy.CampVisitUrls.GET_ASSOCIATIONS_FOR_DOCTOR)
	public Response<DoctorSchoolAssociation> getDoctorAssociations(@RequestParam("page") int page,
			@RequestParam("size") int size, @RequestParam("branchId") String branchId,
			@RequestParam("doctorId") String doctorId, @RequestParam("searchTerm") String searchTerm,
			@RequestParam("updatedTime") String updatedTime, @RequestParam("department") String department) {

		if (DPDoctorUtils.anyStringEmpty(doctorId)) {
			throw new BusinessException(ServiceError.InvalidInput, "id should not null or Empty");
		}
		Response<DoctorSchoolAssociation> response = new Response<DoctorSchoolAssociation>();
		response.setDataList(campVisitService.getDoctorAssociations(page, size, doctorId, searchTerm, updatedTime, branchId, department));
		return response;
	}
	
	@PostMapping(value = PathProxy.CampVisitUrls.ADD_USER_TREATMENT)
	@ApiOperation(value = PathProxy.CampVisitUrls.ADD_USER_TREATMENT, notes = PathProxy.CampVisitUrls.ADD_USER_TREATMENT)
	public Response<UserTreatment> addUserTreatment(@RequestBody UserTreatment request) {
		if(request == null) {
			throw new BusinessException(ServiceError.InvalidInput, "Request should not null or Empty");
		}
		Response<UserTreatment> response = new Response<UserTreatment>();
		response.setData(campVisitService.addUserTreatment(request));
		return response;
	}

	@GetMapping(value = PathProxy.CampVisitUrls.GET_USER_TREATMENT_BY_ID)
	@ApiOperation(value = PathProxy.CampVisitUrls.GET_USER_TREATMENT_BY_ID, notes = PathProxy.CampVisitUrls.GET_USER_TREATMENT_BY_ID)
	public Response<UserTreatment> getUserTreatmentById(@PathVariable("id") String id) {
		if (id == null) {
			throw new BusinessException(ServiceError.InvalidInput, " Invalid input");
		}

		Response<UserTreatment> response = new Response<UserTreatment>();
		response.setData(campVisitService.getUserTreatmentById(id));
		return response;
	}
	
	@GetMapping(value = PathProxy.CampVisitUrls.GET_USER_TREATMENTS)
	@ApiOperation(value = PathProxy.CampVisitUrls.GET_USER_TREATMENTS, notes = PathProxy.CampVisitUrls.GET_USER_TREATMENTS)
	public Response<UserTreatment> getUserTreatments(@RequestParam("size") int size, @RequestParam("page") int page, @RequestParam("userId") String userId, 
			@RequestParam(value = "doctorId") String doctorId, @RequestParam("locationId") String locationId, 
			@RequestParam("hospitalId") String hospitalId, @RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded,
			@RequestParam("updatedTime") String updatedTime, @RequestParam("department") String department) {
		Response<UserTreatment> response = new Response<UserTreatment>();
		response.setDataList(campVisitService.getUserTreatments(size, page, userId, doctorId, locationId, hospitalId, discarded, updatedTime, department));
		return response;
	}
	
	@DeleteMapping(value = PathProxy.CampVisitUrls.DELETE_TREATMENT)
	@ApiOperation(value = PathProxy.CampVisitUrls.DELETE_TREATMENT, notes = PathProxy.CampVisitUrls.DELETE_TREATMENT)
	public Response<UserTreatment> deleteUserTreatment(@PathVariable("id") String id, @RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded) {
		if (id == null) {
			throw new BusinessException(ServiceError.InvalidInput, " Invalid input");
		}
		Response<UserTreatment> response = new Response<UserTreatment>();
		response.setData(campVisitService.deleteUserTreatment(id, discarded));
		return response;
	}
	
	@GetMapping(value = PathProxy.CampVisitUrls.GET_USER_TREATMENT_ANALYTICS_DATA)
	@ApiOperation(value = PathProxy.CampVisitUrls.GET_USER_TREATMENT_ANALYTICS_DATA, notes = PathProxy.CampVisitUrls.GET_USER_TREATMENT_ANALYTICS_DATA)
	public Response<Object> getUserTreatmentAnalyticsData(@RequestParam("doctorId") String doctorId,
			@PathVariable("locationId") String locationId, @PathVariable("hospitalId") String hospitalId,
			@RequestParam("fromDate") long fromDate, @RequestParam("toDate") long toDate,
			@RequestParam("department") String department, @RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded) {
		if (DPDoctorUtils.allStringsEmpty(locationId, hospitalId)) {
			throw new BusinessException(ServiceError.InvalidInput, " locationId, hospitalId should not be empty");
		}
		List<Object> analyticResponse = campVisitService.getUserTreatmentAnalyticsData(
				doctorId, locationId, hospitalId, fromDate, toDate, department, discarded);

		Response<Object> response = new Response<Object>();
		response.setDataList(analyticResponse);
		return response;
	}
}
