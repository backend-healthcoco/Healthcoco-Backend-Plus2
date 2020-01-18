package com.dpdocter.webservices;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dpdocter.beans.AcadamicProfile;
import com.dpdocter.beans.DentalAssessment;
import com.dpdocter.beans.ENTAssessment;
import com.dpdocter.beans.EyeAssessment;
import com.dpdocter.beans.GrowthAssessmentAndGeneralBioMetrics;
import com.dpdocter.beans.NutritionAssessment;
import com.dpdocter.beans.PhysicalAssessment;
import com.dpdocter.beans.RegistrationDetails;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.response.AcadamicClassResponse;
import com.dpdocter.response.NutritionSchoolAssociationResponse;
import com.dpdocter.services.CampVisitService;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Component
@Path(PathProxy.CAMP_VISIT_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.CAMP_VISIT_BASE_URL, description = "Endpoint for camp visit")
public class CampVisitAPI {

	private static Logger logger = LogManager.getLogger(CampVisitAPI.class.getName());

	@Autowired
	private CampVisitService campVisitService;

	@Path(value = PathProxy.CampVisitUrls.GET_GROWTH_ASSESSMENT_AND_GENERAL_BIO_METRICS_BY_ID)
	@ApiOperation(value = PathProxy.CampVisitUrls.GET_GROWTH_ASSESSMENT_AND_GENERAL_BIO_METRICS_BY_ID, notes = PathProxy.CampVisitUrls.GET_GROWTH_ASSESSMENT_AND_GENERAL_BIO_METRICS_BY_ID)
	@GET
	public Response<GrowthAssessmentAndGeneralBioMetrics> getGrowthAssessmentAndGeneralBioMetrics(
			@PathParam("id") String id) {
		Response<GrowthAssessmentAndGeneralBioMetrics> response = new Response<GrowthAssessmentAndGeneralBioMetrics>();
		if (DPDoctorUtils.anyStringEmpty(id)) {
			throw new BusinessException(ServiceError.InvalidInput, "Id should not null or Empty");
		}
		response.setData(campVisitService.getGrowthAssessmentAndGeneralBioMetricsById(id));
		return response;
	}

	@Path(value = PathProxy.CampVisitUrls.GET_GROWTH_ASSESSMENT_AND_GENERAL_BIO_METRICS_LIST)
	@ApiOperation(value = PathProxy.CampVisitUrls.GET_GROWTH_ASSESSMENT_AND_GENERAL_BIO_METRICS_LIST, notes = PathProxy.CampVisitUrls.GET_GROWTH_ASSESSMENT_AND_GENERAL_BIO_METRICS_LIST)
	@GET
	public Response<GrowthAssessmentAndGeneralBioMetrics> getGrowthAssessmentAndGeneralBioMetricsList(
			@QueryParam(value = "branchId") String branchId, @QueryParam(value = "schoolId") String schoolId,
			@QueryParam(value = "academicProfileId") String academicProfileId,
			@QueryParam(value = "doctorId") String doctorId, @QueryParam(value = "page") int page,
			@QueryParam(value = "size") int size,
			@QueryParam(value = "updatedTime") @DefaultValue("0") String updatedTime,
			@QueryParam(value = "discarded") Boolean discarded) {
		Response<GrowthAssessmentAndGeneralBioMetrics> response = new Response<GrowthAssessmentAndGeneralBioMetrics>();
		response.setDataList(campVisitService.getGrowthAssessmentAndGeneralBioMetricsList(academicProfileId, schoolId,
				branchId, doctorId, updatedTime, page, size, discarded));
		response.setCount(campVisitService.getGrowthAssessmentAndGeneralBioMetricsListCount(academicProfileId, schoolId,
				branchId, doctorId, updatedTime, page, size, discarded));
		return response;
	}

	@Path(value = PathProxy.CampVisitUrls.GET_PHYSICAL_ASSESSMENT_BY_ID)
	@ApiOperation(value = PathProxy.CampVisitUrls.GET_PHYSICAL_ASSESSMENT_BY_ID, notes = PathProxy.CampVisitUrls.GET_PHYSICAL_ASSESSMENT_BY_ID)
	@GET
	public Response<PhysicalAssessment> getPhysicalAssessment(@PathParam("id") String id) {
		Response<PhysicalAssessment> response = new Response<PhysicalAssessment>();
		if (DPDoctorUtils.anyStringEmpty(id)) {
			throw new BusinessException(ServiceError.InvalidInput, "Id should not null or Empty");
		}
		response.setData(campVisitService.getPhysicalAssessmentById(id));
		return response;
	}

	@Path(value = PathProxy.CampVisitUrls.GET_PHYSICAL_ASSESSMENT_LIST)
	@ApiOperation(value = PathProxy.CampVisitUrls.GET_PHYSICAL_ASSESSMENT_LIST, notes = PathProxy.CampVisitUrls.GET_PHYSICAL_ASSESSMENT_LIST)
	@GET
	public Response<PhysicalAssessment> getPhysicalAssessmentList(@QueryParam(value = "branchId") String branchId,
			@QueryParam(value = "schoolId") String schoolId,
			@QueryParam(value = "academicProfileId") String academicProfileId,
			@QueryParam(value = "doctorId") String doctorId, @QueryParam(value = "page") int page,
			@QueryParam(value = "size") int size,
			@QueryParam(value = "updatedTime") @DefaultValue("0") String updatedTime,
			@QueryParam(value = "discarded") Boolean discarded) {
		Response<PhysicalAssessment> response = new Response<PhysicalAssessment>();
		response.setDataList(campVisitService.getPhysicalAssessmentList(academicProfileId, schoolId, branchId, doctorId,
				updatedTime, page, size, discarded));
		response.setCount(campVisitService.getPhysicalAssessmentCount(academicProfileId, schoolId, branchId, doctorId,
				updatedTime, page, size, discarded));
		return response;
	}

	@Path(value = PathProxy.CampVisitUrls.GET_ENT_ASSESSMENT_BY_ID)
	@ApiOperation(value = PathProxy.CampVisitUrls.GET_ENT_ASSESSMENT_BY_ID, notes = PathProxy.CampVisitUrls.GET_ENT_ASSESSMENT_BY_ID)
	@GET
	public Response<ENTAssessment> getENTAssessment(@PathParam("id") String id) {
		Response<ENTAssessment> response = new Response<ENTAssessment>();
		if (DPDoctorUtils.anyStringEmpty(id)) {
			throw new BusinessException(ServiceError.InvalidInput, "Id should not null or Empty");
		}
		response.setData(campVisitService.getENTAssessmentById(id));
		return response;
	}

	@Path(value = PathProxy.CampVisitUrls.GET_ENT_ASSESSMENT_LIST)
	@ApiOperation(value = PathProxy.CampVisitUrls.GET_ENT_ASSESSMENT_LIST, notes = PathProxy.CampVisitUrls.GET_ENT_ASSESSMENT_LIST)
	@GET
	public Response<ENTAssessment> getENTAssessmentList(@QueryParam(value = "branchId") String branchId,
			@QueryParam(value = "schoolId") String schoolId,
			@QueryParam(value = "academicProfileId") String academicProfileId,
			@QueryParam(value = "doctorId") String doctorId, @QueryParam(value = "page") int page,
			@QueryParam(value = "size") int size,
			@QueryParam(value = "updatedTime") @DefaultValue("0") String updatedTime,
			@QueryParam(value = "discarded") Boolean discarded) {
		Response<ENTAssessment> response = new Response<ENTAssessment>();
		response.setDataList(campVisitService.getENTAssessmentList(academicProfileId, schoolId, branchId, doctorId,
				updatedTime, page, size, discarded));
		response.setCount(campVisitService.getENTAssessmentListCount(academicProfileId, schoolId, branchId, doctorId,
				updatedTime, page, size, discarded));
		return response;
	}

	@Path(value = PathProxy.CampVisitUrls.GET_DENTAL_ASSESSMENT_BY_ID)
	@ApiOperation(value = PathProxy.CampVisitUrls.GET_DENTAL_ASSESSMENT_BY_ID, notes = PathProxy.CampVisitUrls.GET_DENTAL_ASSESSMENT_BY_ID)
	@GET
	public Response<DentalAssessment> getDentalAssessment(@PathParam("id") String id) {
		Response<DentalAssessment> response = new Response<DentalAssessment>();
		if (DPDoctorUtils.anyStringEmpty(id)) {
			throw new BusinessException(ServiceError.InvalidInput, "Id should not null or Empty");
		}
		response.setData(campVisitService.getDentalAssessmentById(id));
		return response;
	}

	@Path(value = PathProxy.CampVisitUrls.GET_DENTAL_ASSESSMENT_LIST)
	@ApiOperation(value = PathProxy.CampVisitUrls.GET_DENTAL_ASSESSMENT_LIST, notes = PathProxy.CampVisitUrls.GET_DENTAL_ASSESSMENT_LIST)
	@GET
	public Response<DentalAssessment> getDentalAssessmentList(@QueryParam(value = "branchId") String branchId,
			@QueryParam(value = "schoolId") String schoolId,
			@QueryParam(value = "academicProfileId") String academicProfileId,
			@QueryParam(value = "doctorId") String doctorId, @QueryParam(value = "page") int page,
			@QueryParam(value = "size") int size,
			@QueryParam(value = "updatedTime") @DefaultValue("0") String updatedTime,
			@QueryParam(value = "discarded") Boolean discarded) {
		Response<DentalAssessment> response = new Response<DentalAssessment>();
		response.setDataList(campVisitService.getDentalAssessmentList(academicProfileId, schoolId, branchId, doctorId,
				updatedTime, page, size, discarded));
		response.setCount(campVisitService.getDentalAssessmentListCount(academicProfileId, schoolId, branchId, doctorId,
				updatedTime, page, size, discarded));
		return response;
	}

	@Path(value = PathProxy.CampVisitUrls.GET_EYE_ASSESSMENT_BY_ID)
	@ApiOperation(value = PathProxy.CampVisitUrls.GET_EYE_ASSESSMENT_BY_ID, notes = PathProxy.CampVisitUrls.GET_EYE_ASSESSMENT_BY_ID)
	@GET
	public Response<EyeAssessment> getEyeAssessment(@PathParam("id") String id) {
		Response<EyeAssessment> response = new Response<EyeAssessment>();
		if (DPDoctorUtils.anyStringEmpty(id)) {
			throw new BusinessException(ServiceError.InvalidInput, "Id should not null or Empty");
		}
		response.setData(campVisitService.getEyeAssessmentById(id));
		return response;
	}

	@Path(value = PathProxy.CampVisitUrls.GET_EYE_ASSESSMENT_LIST)
	@ApiOperation(value = PathProxy.CampVisitUrls.GET_EYE_ASSESSMENT_LIST, notes = PathProxy.CampVisitUrls.GET_EYE_ASSESSMENT_LIST)
	@GET
	public Response<EyeAssessment> getEyeAssessmentList(@QueryParam(value = "branchId") String branchId,
			@QueryParam(value = "schoolId") String schoolId,
			@QueryParam(value = "academicProfileId") String academicProfileId,
			@QueryParam(value = "doctorId") String doctorId, @QueryParam(value = "page") int page,
			@QueryParam(value = "size") int size,
			@QueryParam(value = "updatedTime") @DefaultValue("0") String updatedTime,
			@QueryParam(value = "discarded") Boolean discarded) {
		Response<EyeAssessment> response = new Response<EyeAssessment>();
		response.setDataList(campVisitService.getEyeAssessmentList(academicProfileId, schoolId, branchId, doctorId,
				updatedTime, page, size, discarded));
		response.setCount(campVisitService.getEyeAssessmentListCount(academicProfileId, schoolId, branchId, doctorId,
				updatedTime, page, size, discarded));
		return response;
	}

	@Path(value = PathProxy.CampVisitUrls.GET_NUTRITION_ASSESSMENT_BY_ID)
	@ApiOperation(value = PathProxy.CampVisitUrls.GET_NUTRITION_ASSESSMENT_BY_ID, notes = PathProxy.CampVisitUrls.GET_NUTRITION_ASSESSMENT_BY_ID)
	@GET
	public Response<NutritionAssessment> getNutritionAssessment(@PathParam("id") String id) {
		Response<NutritionAssessment> response = new Response<NutritionAssessment>();
		if (DPDoctorUtils.anyStringEmpty(id)) {
			throw new BusinessException(ServiceError.InvalidInput, "Id should not null or Empty");
		}
		response.setData(campVisitService.getNutritionAssessmentById(id));
		return response;
	}

	@Path(value = PathProxy.CampVisitUrls.GET_NUTRITION_ASSESSMENT_LIST)
	@ApiOperation(value = PathProxy.CampVisitUrls.GET_NUTRITION_ASSESSMENT_LIST, notes = PathProxy.CampVisitUrls.GET_NUTRITION_ASSESSMENT_LIST)
	@GET
	public Response<NutritionAssessment> getNutritionAssessmentList(@QueryParam(value = "branchId") String branchId,
			@QueryParam(value = "schoolId") String schoolId,
			@QueryParam(value = "academicProfileId") String academicProfileId,
			@QueryParam(value = "doctorId") String doctorId, @QueryParam(value = "page") int page,
			@QueryParam(value = "size") int size,
			@QueryParam(value = "updatedTime") @DefaultValue("0") String updatedTime,
			@QueryParam(value = "discarded") Boolean discarded,
			@QueryParam(value = "recipe") String recipe) {
		Response<NutritionAssessment> response = new Response<NutritionAssessment>();
		response.setDataList(campVisitService.getNutritionAssessmentList(academicProfileId, schoolId, branchId,
				doctorId, updatedTime, page, size, discarded, recipe));
		response.setCount(campVisitService.getNutritionAssessmentListCount(academicProfileId, schoolId, branchId,
				doctorId, updatedTime, page, size, discarded, recipe));
		return response;
	}

	@Path(PathProxy.CampVisitUrls.GET_ACADAMIC_PROFILE)
	@ApiOperation(value = PathProxy.CampVisitUrls.GET_ACADAMIC_PROFILE, notes = PathProxy.CampVisitUrls.GET_ACADAMIC_PROFILE)
	@GET
	public Response<AcadamicProfile> getAcadamicProfile(@PathParam("profileType") String profileType,
			@PathParam("branchId") String branchId, @PathParam("schoolId") String schoolId,
			@QueryParam("page") int page, @QueryParam("size") int size, @QueryParam("classId") String classId,
			@QueryParam("sectionId") String sectionId, @QueryParam("searchTerm") String searchTerm,
			@QueryParam("discarded") Boolean discarded, @QueryParam("userId") String userId,
			@QueryParam("updatedTime") String updatedTime) {

		if (DPDoctorUtils.anyStringEmpty(branchId, schoolId, profileType)) {
			throw new BusinessException(ServiceError.InvalidInput,
					"schoolId,branchId,profileType should not null or Empty");
		}
		Response<AcadamicProfile> response = new Response<AcadamicProfile>();
		if (profileType.equalsIgnoreCase("STUDENT")) {
			response.setDataList(campVisitService.getStudentProfile(page, size, branchId, schoolId, classId, sectionId,
					searchTerm, discarded, profileType, userId, updatedTime));
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

	@Path(value = PathProxy.CampVisitUrls.GET_ACADAMIC_PROFILE_BY_ID)
	@ApiOperation(value = PathProxy.CampVisitUrls.GET_ACADAMIC_PROFILE_BY_ID, notes = PathProxy.CampVisitUrls.GET_ACADAMIC_PROFILE_BY_ID)
	@GET
	public Response<RegistrationDetails> getAcadamicProfile(@PathParam("id") String id) {

		if (DPDoctorUtils.anyStringEmpty(id)) {
			throw new BusinessException(ServiceError.InvalidInput, "id,profileType should not null or Empty");
		}
		Response<RegistrationDetails> response = new Response<RegistrationDetails>();
		response.setData(campVisitService.getAcadamicProfile(id));
		return response;
	}

	@Path(value = PathProxy.CampVisitUrls.GET_ASSOCIATIONS_FOR_DOCTOR)
	@ApiOperation(value = PathProxy.CampVisitUrls.GET_ASSOCIATIONS_FOR_DOCTOR, notes = PathProxy.CampVisitUrls.GET_ASSOCIATIONS_FOR_DOCTOR)
	@GET
	public Response<NutritionSchoolAssociationResponse> getAssociations(@QueryParam("page") int page,
			@QueryParam("size") int size, @QueryParam("classId") String classId,
			@QueryParam("doctorId") String doctorId, @QueryParam("searchTerm") String searchTerm,
			@QueryParam("updatedTime") String updatedTime) {

		if (DPDoctorUtils.anyStringEmpty(doctorId)) {
			throw new BusinessException(ServiceError.InvalidInput, "id should not null or Empty");
		}
		Response<NutritionSchoolAssociationResponse> response = new Response<NutritionSchoolAssociationResponse>();
		response.setDataList(campVisitService.getAssociations(page, size, doctorId, searchTerm, updatedTime));
		return response;
	}

	@Path(value = PathProxy.CampVisitUrls.GET_ACADAMIC_CLASSES)
	@ApiOperation(value = PathProxy.CampVisitUrls.GET_ACADAMIC_CLASSES, notes = PathProxy.CampVisitUrls.GET_ACADAMIC_CLASSES)
	@GET
	public Response<AcadamicClassResponse> getAcadamicClass(@PathParam("branchId") String branchId,
			@PathParam("schoolId") String schoolId, @QueryParam("page") int page, @QueryParam("size") int size,
			@QueryParam("searchTerm") String searchTerm, @QueryParam("discarded") Boolean discarded) {
		Response<AcadamicClassResponse> response = new Response<AcadamicClassResponse>();
		response.setDataList(campVisitService.getAcadamicClass(page, size, branchId, schoolId, searchTerm, discarded));
		response.setCount(campVisitService.countAcadamicClass(branchId, schoolId, searchTerm, discarded));
		return response;
	}
	
	@Path(value = PathProxy.CampVisitUrls.GET_DOCTOR_ACADAMIC_PROFILE)
	@ApiOperation(value = PathProxy.CampVisitUrls.GET_DOCTOR_ACADAMIC_PROFILE, notes = PathProxy.CampVisitUrls.GET_DOCTOR_ACADAMIC_PROFILE)
	@GET
	
	public Response<AcadamicProfile> getAcadamicProfile(
			@PathParam("userId") String userId, @QueryParam("page") int page, @QueryParam("size") int size,
			@QueryParam("searchTerm") String searchTerm, @QueryParam("discarded") Boolean discarded) {
		Response<AcadamicProfile> response = new Response<AcadamicProfile>();
		response.setDataList(campVisitService.getProfile(page, size, userId, discarded, searchTerm));
		response.setCount(campVisitService.countProfile(userId, discarded, searchTerm));
		return response;
	}


}
