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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.dpdocter.beans.AssessmentPersonalDetail;
import com.dpdocter.beans.BloodGlucose;
import com.dpdocter.beans.NutritionGoalAnalytics;
import com.dpdocter.beans.NutritionPlan;
import com.dpdocter.beans.NutritionRecord;
import com.dpdocter.beans.PatientAssesentmentHistoryRequest;
import com.dpdocter.beans.PatientFoodAndExcercise;
import com.dpdocter.beans.PatientLifeStyle;
import com.dpdocter.beans.PatientMeasurementInfo;
import com.dpdocter.beans.RecordsFile;
import com.dpdocter.beans.SubscriptionNutritionPlan;
import com.dpdocter.beans.SugarMedicineReminder;
import com.dpdocter.beans.SugarSetting;
import com.dpdocter.beans.Testimonial;
import com.dpdocter.beans.UserNutritionSubscription;
import com.dpdocter.elasticsearch.response.NutritionPlanWithCategoryShortResponse;
import com.dpdocter.enums.NutritionPlanType;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.request.AddEditNutritionReferenceRequest;
import com.dpdocter.request.DoctorLabReportUploadRequest;
import com.dpdocter.request.MyFiileRequest;
import com.dpdocter.request.NutritionPlanRequest;
import com.dpdocter.response.AssessmentFormHistoryResponse;
import com.dpdocter.response.NutritionPlanResponse;
import com.dpdocter.response.NutritionPlanWithCategoryResponse;
import com.dpdocter.response.NutritionReferenceResponse;
import com.dpdocter.response.UserNutritionSubscriptionResponse;
import com.dpdocter.services.AssessmentFormService;
import com.dpdocter.services.NutritionRecordService;
import com.dpdocter.services.NutritionReferenceService;
import com.dpdocter.services.NutritionService;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataParam;

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
	private NutritionService nutritionService;

	@Autowired
	private NutritionRecordService nutritionRecordService;

	@Autowired
	private AssessmentFormService AssessmentFormService;

	@Autowired
	private NutritionReferenceService nutritionReferenceService;

	@POST
	@Path(PathProxy.NutritionUrl.ADD_EDIT_NUTRITION_REFERENCE)
	@ApiOperation(value = PathProxy.NutritionUrl.ADD_EDIT_NUTRITION_REFERENCE)
	public Response<NutritionReferenceResponse> addEditNutritionResponse(AddEditNutritionReferenceRequest request) {
		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		if (DPDoctorUtils.anyStringEmpty(request.getMobileNumber(), request.getLocalPatientName(),
				request.getDoctorId(), request.getPatientId(), request.getLocationId(), request.getHospitalId())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput,
					"mobileNumber,localPatientName,doctorId,PatientId should not empty or null ");
		}
		Response<NutritionReferenceResponse> response = new Response<>();
		NutritionReferenceResponse nutritionReferenceResponse = null;

		nutritionReferenceResponse = nutritionReferenceService.addEditNutritionReference(request);
		response.setData(nutritionReferenceResponse);
		return response;
	}

	@Path(value = PathProxy.NutritionUrl.GET_NUTRITION_REFERENCES)
	@GET
	@ApiOperation(value = PathProxy.NutritionUrl.GET_NUTRITION_REFERENCES)
	public Response<NutritionReferenceResponse> getNutritionReference(@QueryParam("patientId") String patientId,
			@QueryParam("doctorId") String doctorId, @QueryParam("locationId") String locationId,
			@QueryParam("hospitalId") String hospitalId, @QueryParam("page") int page, @QueryParam("size") int size,
			@QueryParam("searchTerm") String searchTerm, @QueryParam("updatedTime") String updatedTime) {
		if (DPDoctorUtils.anyStringEmpty(doctorId, locationId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<NutritionReferenceResponse> response = new Response<NutritionReferenceResponse>();
		response.setDataList(nutritionReferenceService.getNutritionReferenceList(page, size, doctorId, locationId,
				hospitalId, patientId, searchTerm, updatedTime));
		return response;
	}

	@Path(value = PathProxy.NutritionUrl.GET_NUTRITION_ANALYTICS)
	@GET
	@ApiOperation(value = PathProxy.NutritionUrl.GET_NUTRITION_ANALYTICS)
	public Response<NutritionGoalAnalytics> getNutritionAnalytics(@QueryParam("doctorId") String doctorId,
			@QueryParam("locationId") String locationId, @DefaultValue("0") @QueryParam("fromDate") Long fromDate,
			@DefaultValue("0") @QueryParam("toDate") Long toDate) {
		if (DPDoctorUtils.anyStringEmpty(doctorId, locationId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<NutritionGoalAnalytics> response = new Response<NutritionGoalAnalytics>();
		response.setData(nutritionReferenceService.getGoalAnalytics(doctorId, locationId, fromDate, toDate));
		return response;
	}

	@Value(value = "${image.path}")
	private String imagePath;

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

	@Path(PathProxy.NutritionUrl.ADD_EDIT_ASSESSMENT_PATIENT_DETAIL)
	@POST
	@ApiOperation(value = PathProxy.NutritionUrl.ADD_EDIT_ASSESSMENT_PATIENT_DETAIL, notes = PathProxy.NutritionUrl.ADD_EDIT_ASSESSMENT_PATIENT_DETAIL)
	public Response<AssessmentPersonalDetail> addEditAssessmentPatientDetail(AssessmentPersonalDetail request) {
		if (request == null) {
			throw new BusinessException(ServiceError.InvalidInput, " Invalid input");
		}
		if (DPDoctorUtils.allStringsEmpty(request.getDoctorId(), request.getLocationId(), request.getHospitalId())) {
			throw new BusinessException(ServiceError.InvalidInput,
					" doctorId , hospitalId ,locationId should not null");
		}

		Response<AssessmentPersonalDetail> response = new Response<AssessmentPersonalDetail>();
		response.setData(AssessmentFormService.addEditAssessmentPersonalDetail(request));

		return response;
	}

	@Path(PathProxy.NutritionUrl.ADD_EDIT_ASSESSMENT_LIFE_STYLE)
	@POST
	@ApiOperation(value = PathProxy.NutritionUrl.ADD_EDIT_ASSESSMENT_LIFE_STYLE, notes = PathProxy.NutritionUrl.ADD_EDIT_ASSESSMENT_LIFE_STYLE)
	public Response<PatientLifeStyle> addEditAssessmentLifeStyle(PatientLifeStyle request) {
		if (request == null) {
			throw new BusinessException(ServiceError.InvalidInput, " Invalid input");
		}
		if (DPDoctorUtils.allStringsEmpty(request.getDoctorId(), request.getLocationId(), request.getHospitalId())) {
			throw new BusinessException(ServiceError.InvalidInput,
					" doctorId , hospitalId ,locationId should not null");
		}
		Response<PatientLifeStyle> response = new Response<PatientLifeStyle>();
		response.setData(AssessmentFormService.addEditAssessmentLifeStyle(request));

		return response;
	}

	@Path(PathProxy.NutritionUrl.ADD_EDIT_ASSESSMENT_FOOD_AND_EXCERCISE)
	@POST
	@ApiOperation(value = PathProxy.NutritionUrl.ADD_EDIT_ASSESSMENT_FOOD_AND_EXCERCISE, notes = PathProxy.NutritionUrl.ADD_EDIT_ASSESSMENT_FOOD_AND_EXCERCISE)
	public Response<PatientFoodAndExcercise> addEditAssessmentFoodAndExcercise(PatientFoodAndExcercise request) {
		if (request == null) {
			throw new BusinessException(ServiceError.InvalidInput, " Invalid input");
		}
		if (DPDoctorUtils.allStringsEmpty(request.getDoctorId(), request.getLocationId(), request.getHospitalId())) {
			throw new BusinessException(ServiceError.InvalidInput,
					" doctorId , hospitalId ,locationId should not null");
		}
		Response<PatientFoodAndExcercise> response = new Response<PatientFoodAndExcercise>();
		response.setData(AssessmentFormService.addEditFoodAndExcercise(request));

		return response;
	}

	@Path(PathProxy.NutritionUrl.ADD_EDIT_ASSESSMENT_PATIENT_HISTORY)
	@POST
	@ApiOperation(value = PathProxy.NutritionUrl.ADD_EDIT_ASSESSMENT_PATIENT_HISTORY, notes = PathProxy.NutritionUrl.ADD_EDIT_ASSESSMENT_PATIENT_HISTORY)
	public Response<AssessmentFormHistoryResponse> addEditAssessmentPatientHistory(
			PatientAssesentmentHistoryRequest request) {
		if (request == null) {
			throw new BusinessException(ServiceError.InvalidInput, " Invalid input");
		}
		if (DPDoctorUtils.allStringsEmpty(request.getDoctorId(), request.getLocationId(), request.getHospitalId())) {
			throw new BusinessException(ServiceError.InvalidInput,
					" doctorId , hospitalId ,locationId should not null");
		}
		Response<AssessmentFormHistoryResponse> response = new Response<AssessmentFormHistoryResponse>();
		response.setData(AssessmentFormService.addEditAssessmentHistory(request));

		return response;
	}

	@Path(PathProxy.NutritionUrl.ADD_EDIT_ASSESSMENT_PATIENT_MEASUREMENT)
	@POST
	@ApiOperation(value = PathProxy.NutritionUrl.ADD_EDIT_ASSESSMENT_PATIENT_MEASUREMENT, notes = PathProxy.NutritionUrl.ADD_EDIT_ASSESSMENT_PATIENT_MEASUREMENT)
	public Response<PatientMeasurementInfo> addEditAssessmentPatientMeasurement(PatientMeasurementInfo request) {
		if (request == null) {
			throw new BusinessException(ServiceError.InvalidInput, " Invalid input");
		}
		if (DPDoctorUtils.allStringsEmpty(request.getDoctorId(), request.getLocationId(), request.getHospitalId())) {
			throw new BusinessException(ServiceError.InvalidInput,
					" doctorId , hospitalId ,locationId should not null");
		}
		Response<PatientMeasurementInfo> response = new Response<PatientMeasurementInfo>();
		response.setData(AssessmentFormService.addEditPatientMeasurementInfo(request));

		return response;
	}

	@Path(PathProxy.NutritionUrl.GET_ASSESSMENT_PATIENT_DETAIL)
	@GET
	@ApiOperation(value = PathProxy.NutritionUrl.GET_ASSESSMENT_PATIENT_DETAIL, notes = PathProxy.NutritionUrl.GET_ASSESSMENT_PATIENT_DETAIL)
	public Response<AssessmentPersonalDetail> getAssessmentPatientDetail(@QueryParam("page") int page,
			@QueryParam("size") int size, @QueryParam("updateTime") long updateTime,
			@QueryParam("discarded") boolean discarded, @QueryParam("doctorId") String doctorId,
			@QueryParam("patientId") String patientId, @QueryParam("locationId") String locationId,
			@QueryParam("hospitalId") String hospitalId) {

		if (DPDoctorUtils.allStringsEmpty(locationId, hospitalId)) {
			throw new BusinessException(ServiceError.InvalidInput, " hospitalId ,locationId should not null");
		}
		Response<AssessmentPersonalDetail> response = new Response<AssessmentPersonalDetail>();
		response.setDataList(AssessmentFormService.getAssessmentPatientDetail(page, size, discarded, updateTime,
				patientId, doctorId, locationId, hospitalId));

		return response;
	}

	@Path(PathProxy.NutritionUrl.GET_ASSESSMENT_LIFE_STYLE)
	@GET
	@ApiOperation(value = PathProxy.NutritionUrl.GET_ASSESSMENT_LIFE_STYLE, notes = PathProxy.NutritionUrl.GET_ASSESSMENT_LIFE_STYLE)
	public Response<PatientLifeStyle> getPatientLifeStyle(@PathParam("assessmentId") String assessmentId) {

		if (DPDoctorUtils.allStringsEmpty(assessmentId)) {
			throw new BusinessException(ServiceError.InvalidInput, "assessmentId should not be null");
		}
		Response<PatientLifeStyle> response = new Response<PatientLifeStyle>();
		response.setData(AssessmentFormService.getAssessmentLifeStyle(assessmentId));

		return response;
	}

	@Path(PathProxy.NutritionUrl.GET_ASSESSMENT_FOOD_AND_EXCERCISE)
	@GET
	@ApiOperation(value = PathProxy.NutritionUrl.GET_ASSESSMENT_FOOD_AND_EXCERCISE, notes = PathProxy.NutritionUrl.GET_ASSESSMENT_FOOD_AND_EXCERCISE)
	public Response<PatientFoodAndExcercise> getAssessmentFoodAndExcercise(
			@PathParam("assessmentId") String assessmentId) {

		if (DPDoctorUtils.allStringsEmpty(assessmentId)) {
			throw new BusinessException(ServiceError.InvalidInput, "assessmentId should not be null");
		}
		Response<PatientFoodAndExcercise> response = new Response<PatientFoodAndExcercise>();
		response.setData(AssessmentFormService.getPatientFoodAndExcercise(assessmentId));

		return response;
	}

	@Path(PathProxy.NutritionUrl.GET_ASSESSMENT_PATIENT_HISTORY)
	@GET
	@ApiOperation(value = PathProxy.NutritionUrl.GET_ASSESSMENT_PATIENT_HISTORY, notes = PathProxy.NutritionUrl.GET_ASSESSMENT_FOOD_AND_EXCERCISE)
	public Response<AssessmentFormHistoryResponse> getAssessmentHistory(
			@PathParam("assessmentId") String assessmentId) {

		if (DPDoctorUtils.allStringsEmpty(assessmentId)) {
			throw new BusinessException(ServiceError.InvalidInput, "assessmentId should not be null");
		}
		Response<AssessmentFormHistoryResponse> response = new Response<AssessmentFormHistoryResponse>();
		response.setData(AssessmentFormService.getAssessmentHistory(assessmentId));

		return response;
	}

	@Path(PathProxy.NutritionUrl.GET_ASSESSMENT_PATIENT_MEASUREMENT)
	@GET
	@ApiOperation(value = PathProxy.NutritionUrl.GET_ASSESSMENT_PATIENT_MEASUREMENT, notes = PathProxy.NutritionUrl.GET_ASSESSMENT_PATIENT_MEASUREMENT)
	public Response<PatientMeasurementInfo> getAssessmentMeasureInfo(@PathParam("assessmentId") String assessmentId) {

		if (DPDoctorUtils.allStringsEmpty(assessmentId)) {
			throw new BusinessException(ServiceError.InvalidInput, "assessmentId should not be null");
		}
		Response<PatientMeasurementInfo> response = new Response<PatientMeasurementInfo>();

		response.setData(AssessmentFormService.getPatientMeasurementInfo(assessmentId));
		return response;
	}

	@Path(value = PathProxy.NutritionUrl.UPDATE_IS_SHARE_WITH_PATIENT)
	@GET
	@ApiOperation(value = PathProxy.NutritionUrl.UPDATE_IS_SHARE_WITH_PATIENT, notes = PathProxy.NutritionUrl.UPDATE_IS_SHARE_WITH_PATIENT)
	public Response<Boolean> updateShareWithPatent(@PathParam("recordId") String recordId) {
		if (DPDoctorUtils.anyStringEmpty(recordId)) {
			logger.warn("Record Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput, "Record Id Cannot Be Empty");
		}
		Response<Boolean> response = new Response<Boolean>();
		response.setData(nutritionRecordService.updateShareWithPatient(recordId));
		return response;

	}

	@Path(value = PathProxy.NutritionUrl.ADD_NUTRITION_RECORD)
	@POST
	@ApiOperation(value = PathProxy.NutritionUrl.ADD_NUTRITION_RECORD, notes = PathProxy.NutritionUrl.ADD_NUTRITION_RECORD)
	public Response<NutritionRecord> addNutritionRecord(NutritionRecord request) {
		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		if (DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId(), request.getHospitalId(),
				request.getPatientId()) || request.getRecordsFiles() == null || request.getRecordsFiles().isEmpty()) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		NutritionRecord nutritionRecord = nutritionRecordService.addNutritionRecord(request);

		Response<NutritionRecord> response = new Response<NutritionRecord>();
		response.setData(nutritionRecord);
		return response;
	}

	@POST
	@Path(value = PathProxy.NutritionUrl.UPLOAD_NUTRITION_RECORD_MULTIPART_FILE)
	@Consumes({ MediaType.MULTIPART_FORM_DATA })
	@ApiOperation(value = PathProxy.NutritionUrl.UPLOAD_NUTRITION_RECORD_MULTIPART_FILE, notes = PathProxy.NutritionUrl.UPLOAD_NUTRITION_RECORD_MULTIPART_FILE)
	public Response<RecordsFile> uploadNutritionRecordMultipart(@FormDataParam("file") FormDataBodyPart file,
			@FormDataParam("data") FormDataBodyPart data) {

		data.setMediaType(MediaType.APPLICATION_JSON_TYPE);
		MyFiileRequest request = data.getValueAs(MyFiileRequest.class);

		if (request == null || DPDoctorUtils.anyStringEmpty(file.getContentDisposition().getFileName())) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		RecordsFile recordsFile = nutritionRecordService.uploadNutritionRecord(file, request);
		if (recordsFile != null) {
			recordsFile.setRecordsUrl(getFinalImageURL(recordsFile.getRecordsUrl()));
			recordsFile.setThumbnailUrl(getFinalImageURL(recordsFile.getThumbnailUrl()));
		}

		Response<RecordsFile> response = new Response<RecordsFile>();
		response.setData(recordsFile);
		return response;
	}

	@POST
	@Path(value = PathProxy.NutritionUrl.UPLOAD_NUTRITION_RECORD)
	@ApiOperation(value = PathProxy.NutritionUrl.UPLOAD_NUTRITION_RECORD, notes = PathProxy.NutritionUrl.UPLOAD_NUTRITION_RECORD)
	public Response<RecordsFile> uploadNutritionRecord(DoctorLabReportUploadRequest request) {
		if (request == null || request.getFileDetails() == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		RecordsFile recordsFiles = nutritionRecordService.uploadNutritionRecord(request);

		Response<RecordsFile> response = new Response<RecordsFile>();
		response.setData(recordsFiles);
		return response;
	}

	private String getFinalImageURL(String imageURL) {
		if (imageURL != null) {
			return imagePath + imageURL;
		} else
			return null;
	}

	@Path(value = PathProxy.NutritionUrl.GET_NUTRITION_RECORD_BY_ID)
	@GET
	@ApiOperation(value = PathProxy.NutritionUrl.GET_NUTRITION_RECORD_BY_ID, notes = PathProxy.NutritionUrl.GET_NUTRITION_RECORD_BY_ID)
	public Response<NutritionRecord> getRecordById(@PathParam("recordId") String recordId) {
		if (DPDoctorUtils.anyStringEmpty(recordId)) {
			logger.warn("Record Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput, "report Id Cannot Be Empty");
		}

		NutritionRecord nutritionRecord = nutritionRecordService.getNutritionRecord(recordId);

		Response<NutritionRecord> response = new Response<NutritionRecord>();
		response.setData(nutritionRecord);
		return response;

	}

	@Path(value = PathProxy.NutritionUrl.GET_NUTRITION_RECORDS)
	@GET
	@ApiOperation(value = PathProxy.NutritionUrl.GET_NUTRITION_RECORDS, notes = PathProxy.NutritionUrl.GET_NUTRITION_RECORDS)
	public Response<NutritionRecord> getDoctorLabReports(@QueryParam("page") int page, @QueryParam("size") int size,
			@QueryParam("patientId") String patientId, @QueryParam("doctorId") String doctorId,
			@QueryParam("locationId") String locationId, @QueryParam("hospitalId") String hospitalId,
			@QueryParam("searchTerm") String searchTerm, @QueryParam("discarded") Boolean discarded,
			@QueryParam("isnutrition") @DefaultValue("isNutrition") Boolean isNutrition) {

		List<NutritionRecord> records = nutritionRecordService.getNutritionRecord(page, size, patientId, doctorId,
				locationId, hospitalId, searchTerm, discarded, isNutrition);
		Response<NutritionRecord> response = new Response<NutritionRecord>();
		response.setDataList(records);
		return response;

	}

	@Path(PathProxy.NutritionUrl.DELETE_NUTRITION_RECORD)
	@DELETE
	@ApiOperation(value = PathProxy.NutritionUrl.DELETE_NUTRITION_RECORD, notes = PathProxy.NutritionUrl.DELETE_NUTRITION_RECORD)
	public Response<NutritionRecord> deleteUserPlanSubscription(@PathParam("recordId") String recordId,
			@QueryParam("discarded") @DefaultValue("true") Boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(recordId)) {
			throw new BusinessException(ServiceError.InvalidInput, " Invalid input");
		}
		Response<NutritionRecord> response = new Response<NutritionRecord>();
		response.setData(nutritionRecordService.deleteNutritionRecord(recordId, discarded));
		return response;
	}
	

	@Path(PathProxy.NutritionUrl.GET_USER_NUTRITION_PLAN)
	@POST
	@ApiOperation(value = PathProxy.NutritionUrl.GET_USER_NUTRITION_PLAN, notes = PathProxy.NutritionUrl.GET_USER_NUTRITION_PLAN)
	public Response<NutritionPlanWithCategoryShortResponse> getPlanDetailsByCategory(NutritionPlanRequest request) {

		Response<NutritionPlanWithCategoryShortResponse> response = new Response<NutritionPlanWithCategoryShortResponse>();
		response.setDataList(nutritionService.getNutritionPlanDetailsByCategory(request));

		return response;
	}
	
	@Path(PathProxy.NutritionUrl.GET_USER_NUTRITION_PLAN_BY_ID)
	@GET
	@ApiOperation(value = PathProxy.NutritionUrl.GET_USER_NUTRITION_PLAN_BY_ID, notes = PathProxy.NutritionUrl.GET_USER_NUTRITION_PLAN_BY_ID)
	public Response<NutritionPlan> getNutritionPlanById(@PathParam("id") String id) {

		Response<NutritionPlan> response = null;

		if (DPDoctorUtils.anyStringEmpty(id)) {
			throw new BusinessException(ServiceError.InvalidInput, " Invalid input");
		}

		response = new Response<NutritionPlan>();
		response.setData(nutritionService.getNutritionPlanById(id));

		return response;
	}
	
	@Path(value = PathProxy.NutritionUrl.GET_TESTIMONIALS_BY_PLAN_ID)
	@GET
	@ApiOperation(value = PathProxy.NutritionUrl.GET_TESTIMONIALS_BY_PLAN_ID, notes = PathProxy.NutritionUrl.GET_TESTIMONIALS_BY_PLAN_ID)
	public Response<Testimonial> getTestimonialsByPlanId(@PathParam("id") String planId , @QueryParam("page") int page ,  @QueryParam("size") int size ) {
		if (planId == null) {
			throw new BusinessException(ServiceError.InvalidInput, " Invalid input");
		}

		Response<Testimonial> response = new Response<Testimonial>();

		response.setDataList(nutritionService.getTestimonialsByPlanId(planId, size, page));

		return response;
	}
	

	@Path(value = PathProxy.NutritionUrl.ADD_EDIT_SUGAR_SETTINGS)
	@POST
	@ApiOperation(value = PathProxy.NutritionUrl.ADD_EDIT_SUGAR_SETTINGS, notes = PathProxy.NutritionUrl.ADD_EDIT_SUGAR_SETTINGS)
	public Response<SugarSetting> addEditSugarSetting(SugarSetting request) {
		if (request == null) {
			throw new BusinessException(ServiceError.InvalidInput, " Invalid input");
		}

		Response<SugarSetting> response = new Response<SugarSetting>();

		response.setData(nutritionService.addEditSugarSetting(request));

		return response;
	}
	
	@Path(value = PathProxy.NutritionUrl.GET_SUGAR_SETTINGS_BY_ID)
	@GET
	@ApiOperation(value = PathProxy.NutritionUrl.GET_SUGAR_SETTINGS_BY_ID, notes = PathProxy.NutritionUrl.GET_SUGAR_SETTINGS_BY_ID)
	public Response<SugarSetting> getSugarSettingById(@PathParam("id") String id) {
		if (id == null) {
			throw new BusinessException(ServiceError.InvalidInput, " Invalid input");
		}

		Response<SugarSetting> response = new Response<SugarSetting>();

		response.setData(nutritionService.getSugarSettingById(id));

		return response;
	}
	
	

	@Path(value = PathProxy.NutritionUrl.ADD_EDIT_BLOOD_GLUCOSE)
	@POST
	@ApiOperation(value = PathProxy.NutritionUrl.ADD_EDIT_BLOOD_GLUCOSE, notes = PathProxy.NutritionUrl.ADD_EDIT_BLOOD_GLUCOSE)
	public Response<BloodGlucose> addEditBloodGlucose(BloodGlucose request) {
		if (request == null) {
			throw new BusinessException(ServiceError.InvalidInput, " Invalid input");
		}

		Response<BloodGlucose> response = new Response<BloodGlucose>();

		response.setData(nutritionService.addEditBloodGlucose(request));

		return response;
	}
	
	@Path(value = PathProxy.NutritionUrl.GET_BLOOD_GLUCOSE_BY_ID)
	@GET
	@ApiOperation(value = PathProxy.NutritionUrl.GET_BLOOD_GLUCOSE_BY_ID, notes = PathProxy.NutritionUrl.GET_BLOOD_GLUCOSE_BY_ID)
	public Response<BloodGlucose> getBloodGlucoseById(@PathParam("id") String id) {
		if (id == null) {
			throw new BusinessException(ServiceError.InvalidInput, " Invalid input");
		}

		Response<BloodGlucose> response = new Response<BloodGlucose>();
		response.setData(nutritionService.getBloodGlucoseById(id));
		return response;
	}
	
	@Path(value = PathProxy.NutritionUrl.GET_BLOOD_GLUCOSE_LIST_BY_PATIENT_ID)
	@GET
	@ApiOperation(value = PathProxy.NutritionUrl.GET_BLOOD_GLUCOSE_LIST_BY_PATIENT_ID, notes = PathProxy.NutritionUrl.GET_BLOOD_GLUCOSE_LIST_BY_PATIENT_ID)
	public Response<BloodGlucose> getBloodGlucoseListByPatientId(@QueryParam("patientId") String patientId, @QueryParam("page") int page, @QueryParam("size") int size) {
		if (patientId == null) {
			throw new BusinessException(ServiceError.InvalidInput, " Invalid input");
		}

		Response<BloodGlucose> response = new Response<BloodGlucose>();
		response.setDataList(nutritionService.getBloodGlucoseList(patientId, size, page, null, null));
		return response;
	}
	

	@Path(value = PathProxy.NutritionUrl.ADD_EDIT_SUGAR_MEDICINE_REMINDER)
	@POST
	@ApiOperation(value = PathProxy.NutritionUrl.ADD_EDIT_SUGAR_MEDICINE_REMINDER, notes = PathProxy.NutritionUrl.ADD_EDIT_SUGAR_MEDICINE_REMINDER)
	public Response<SugarMedicineReminder> addEditSugarMedicineReminder(SugarMedicineReminder request) {
		if (request == null) {
			throw new BusinessException(ServiceError.InvalidInput, " Invalid input");
		}

		Response<SugarMedicineReminder> response = new Response<SugarMedicineReminder>();
		response.setData(nutritionService.addEditSugarMedicineReminder(request));

		return response;
	}
	
	@Path(value = PathProxy.NutritionUrl.GET_SUGAR_MEDICINE_REMINDER_BY_ID)
	@GET
	@ApiOperation(value = PathProxy.NutritionUrl.GET_SUGAR_MEDICINE_REMINDER_BY_ID, notes = PathProxy.NutritionUrl.GET_SUGAR_MEDICINE_REMINDER_BY_ID)
	public Response<SugarMedicineReminder> getSugarMedicineReminderById(@PathParam("id") String id) {
		if (id == null) {
			throw new BusinessException(ServiceError.InvalidInput, " Invalid input");
		}

		Response<SugarMedicineReminder> response = new Response<SugarMedicineReminder>();
		response.setData(nutritionService.getSugarMedicineReminderById(id));
		return response;
	}
	
	@Path(value = PathProxy.NutritionUrl.GET_SUGAR_MEDICINE_REMINDER_LIST_BY_PATIENT_ID)
	@GET
	@ApiOperation(value = PathProxy.NutritionUrl.GET_SUGAR_MEDICINE_REMINDER_LIST_BY_PATIENT_ID, notes = PathProxy.NutritionUrl.GET_SUGAR_MEDICINE_REMINDER_LIST_BY_PATIENT_ID)
	public Response<SugarMedicineReminder> getSugarReminderListByPatientId(@QueryParam("patientId") String patientId, @QueryParam("page") int page, @QueryParam("size") int size) {
		if (patientId == null) {
			throw new BusinessException(ServiceError.InvalidInput, " Invalid input");
		}

		Response<SugarMedicineReminder> response = new Response<SugarMedicineReminder>();
		response.setDataList(nutritionService.getSugarMedicineReminders(patientId, size, page, null, null));
		return response;
	}
	
	
	
}
