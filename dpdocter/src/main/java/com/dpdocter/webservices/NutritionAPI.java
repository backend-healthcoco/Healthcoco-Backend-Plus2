package com.dpdocter.webservices;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.Produces;
import org.springframework.http.MediaType;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.dpdocter.beans.AssessmentPersonalDetail;
import com.dpdocter.beans.BloodGlucose;
import com.dpdocter.beans.NutritionGoalAnalytics;
import com.dpdocter.beans.NutritionPlan;
import com.dpdocter.beans.NutritionRDA;
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
import com.dpdocter.response.NutritionistReport;
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

@RestController
@RequestMapping(value=PathProxy.NUTRITION_BASE_URL,consumes =MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
@Api(value = PathProxy.NUTRITION_BASE_URL, description = "Endpoint for nutrition api's")
public class NutritionAPI {

	private static Logger logger = LogManager.getLogger(NutritionAPI.class.getName());

	@Autowired
	private NutritionService nutritionService;

	@Autowired
	private NutritionRecordService nutritionRecordService;

	@Autowired
	private AssessmentFormService assessmentFormService;

	@Autowired
	private NutritionReferenceService nutritionReferenceService;

	@PostMapping
	(PathProxy.NutritionUrl.ADD_EDIT_NUTRITION_REFERENCE)
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

	
	@GetMapping(value = PathProxy.NutritionUrl.GET_NUTRITION_REFERENCES)
	@ApiOperation(value = PathProxy.NutritionUrl.GET_NUTRITION_REFERENCES)
	public Response<NutritionReferenceResponse> getNutritionReference(@RequestParam("patientId") String patientId,
			@RequestParam("doctorId") String doctorId, @RequestParam("locationId") String locationId,
			@RequestParam("hospitalId") String hospitalId, @RequestParam("page") int page, @RequestParam("size") int size,
			@RequestParam("searchTerm") String searchTerm, @RequestParam("updatedTime") String updatedTime) {
		if (DPDoctorUtils.anyStringEmpty(doctorId, locationId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<NutritionReferenceResponse> response = new Response<NutritionReferenceResponse>();
		response.setDataList(nutritionReferenceService.getNutritionReferenceList(page, size, doctorId, locationId,
				hospitalId, patientId, searchTerm, updatedTime));
		return response;
	}

	
	@GetMapping(value = PathProxy.NutritionUrl.GET_NUTRITION_ANALYTICS)
	@ApiOperation(value = PathProxy.NutritionUrl.GET_NUTRITION_ANALYTICS)
	public Response<NutritionGoalAnalytics> getNutritionAnalytics(@RequestParam("doctorId") String doctorId,
			@RequestParam("locationId") String locationId, @DefaultValue("0") @RequestParam("fromDate") Long fromDate,
			@DefaultValue("0") @RequestParam("toDate") Long toDate) {
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

	
	@GetMapping(PathProxy.NutritionUrl.GET_NUTRITION_PLAN_BY_ID)
	@ApiOperation(value = PathProxy.NutritionUrl.GET_NUTRITION_PLAN_BY_ID, notes = PathProxy.NutritionUrl.GET_NUTRITION_PLAN_BY_ID)
	public Response<NutritionPlanResponse> getPlanById(@PathVariable("id") String id) {

		Response<NutritionPlanResponse> response = null;

		if (DPDoctorUtils.anyStringEmpty(id)) {
			throw new BusinessException(ServiceError.InvalidInput, " Invalid input");
		}

		response = new Response<NutritionPlanResponse>();
		response.setData(nutritionService.getNutritionPlan(id));

		return response;
	}

	
	@GetMapping(PathProxy.NutritionUrl.GET_ALL_PLAN_CATEGORY)
	@ApiOperation(value = PathProxy.NutritionUrl.GET_ALL_PLAN_CATEGORY, notes = PathProxy.NutritionUrl.GET_ALL_PLAN_CATEGORY)
	public Response<NutritionPlanType> getPlanCategory() {

		Response<NutritionPlanType> response = new Response<NutritionPlanType>();
		response.setDataList(nutritionService.getPlanType());

		return response;
	}

	
	@GetMapping(PathProxy.NutritionUrl.GET_SUBSCRIPTION_PLAN_BY_ID)
	@ApiOperation(value = PathProxy.NutritionUrl.GET_SUBSCRIPTION_PLAN_BY_ID, notes = PathProxy.NutritionUrl.GET_SUBSCRIPTION_PLAN_BY_ID)
	public Response<SubscriptionNutritionPlan> getSubscriptionPlan(@PathVariable("id") String id) {
		if (DPDoctorUtils.anyStringEmpty(id)) {
			throw new BusinessException(ServiceError.InvalidInput, " Invalid input");
		}
		Response<SubscriptionNutritionPlan> response = new Response<SubscriptionNutritionPlan>();
		response.setData(nutritionService.getSubscritionPlan(id));

		return response;
	}

	
	@GetMapping(PathProxy.NutritionUrl.GET_SUBSCRIPTION_PLANS)
	@ApiOperation(value = PathProxy.NutritionUrl.GET_SUBSCRIPTION_PLANS, notes = PathProxy.NutritionUrl.GET_SUBSCRIPTION_PLANS)
	public Response<SubscriptionNutritionPlan> getSubscriptionPlans(@RequestParam("page") int page,
			@RequestParam("size") int size, @RequestParam("nutritionplanId") String nutritionplanId,
			@RequestParam("discarded") @DefaultValue("false") boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(nutritionplanId)) {
			throw new BusinessException(ServiceError.InvalidInput, " NutritionplanId must not be null ");
		}
		Response<SubscriptionNutritionPlan> response = new Response<SubscriptionNutritionPlan>();
		response.setDataList(nutritionService.getSubscritionPlans(page, size, nutritionplanId, discarded));
		return response;
	}

	
	@GetMapping(PathProxy.NutritionUrl.GENERATE_ID)
	@ApiOperation(value = PathProxy.NutritionUrl.GENERATE_ID, notes = PathProxy.NutritionUrl.GENERATE_ID)
	public Response<String> getGenerateId() {
		Response<String> response = new Response<String>();
		response.setData(DPDoctorUtils.generateRandomId());

		return response;
	}

	
	@PostMapping(PathProxy.NutritionUrl.ADD_USER_PLAN_SUBSCRIPTION)
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

	
	@DeleteMapping(PathProxy.NutritionUrl.DELETE_USER_PLAN_SUBSCRIPTION)
	@ApiOperation(value = PathProxy.NutritionUrl.DELETE_USER_PLAN_SUBSCRIPTION, notes = PathProxy.NutritionUrl.DELETE_USER_PLAN_SUBSCRIPTION)
	public Response<UserNutritionSubscription> deleteUserPlanSubscription(@PathVariable("id") String id) {
		if (DPDoctorUtils.anyStringEmpty(id)) {
			throw new BusinessException(ServiceError.InvalidInput, " Invalid input");
		}
		Response<UserNutritionSubscription> response = new Response<UserNutritionSubscription>();
		response.setData(nutritionService.deleteUserSubscritionPlan(id));
		return response;
	}

	
	@GetMapping(PathProxy.NutritionUrl.GET_USER_PLAN_SUBSCRIPTION)
	@ApiOperation(value = PathProxy.NutritionUrl.GET_USER_PLAN_SUBSCRIPTION, notes = PathProxy.NutritionUrl.GET_USER_PLAN_SUBSCRIPTION)
	public Response<UserNutritionSubscriptionResponse> getUserPlanSubscription(@PathVariable("id") String id) {

		if (DPDoctorUtils.anyStringEmpty(id)) {
			throw new BusinessException(ServiceError.InvalidInput, " Invalid input");
		}
		Response<UserNutritionSubscriptionResponse> response = new Response<UserNutritionSubscriptionResponse>();
		response.setData(nutritionService.getUserSubscritionPlan(id));
		return response;
	}

	
	@GetMapping(PathProxy.NutritionUrl.GET_USER_PLAN_SUBSCRIPTIONS)
	@ApiOperation(value = PathProxy.NutritionUrl.GET_USER_PLAN_SUBSCRIPTIONS, notes = PathProxy.NutritionUrl.GET_USER_PLAN_SUBSCRIPTIONS)
	public Response<UserNutritionSubscriptionResponse> getUserPlanSubscriptions(@PathVariable("userId") String userId,
			@RequestParam("page") int page, @RequestParam("size") int size, @RequestParam("updatedTime") long updatedTime,
			@RequestParam("discarded") @DefaultValue("false") boolean discarded) {
		Response<UserNutritionSubscriptionResponse> response = new Response<UserNutritionSubscriptionResponse>();
		if (DPDoctorUtils.allStringsEmpty(userId)) {
			throw new BusinessException(ServiceError.InvalidInput, " Invalid input");
		}
		response.setDataList(nutritionService.getUserSubscritionPlans(page, size, updatedTime, discarded, userId));
		return response;
	}

	
	@GetMapping(PathProxy.NutritionUrl.GET_NUTRITION_PLAN)
	@ApiOperation(value = PathProxy.NutritionUrl.GET_NUTRITION_PLAN, notes = PathProxy.NutritionUrl.GET_NUTRITION_PLAN)
	public Response<NutritionPlan> getPlan(@RequestParam("page") int page, @RequestParam("size") int size,
			@RequestParam("type") String type, @RequestParam("updatedTime") long updatedTime,
			@RequestParam("discarded")   boolean discarded) {

		Response<NutritionPlan> response = new Response<NutritionPlan>();
		response.setDataList(nutritionService.getNutritionPlans(page, size, type, updatedTime, discarded));

		return response;
	}

	
	@PostMapping(PathProxy.NutritionUrl.GET_NUTRITION_PLAN_CATEGORY)
	@ApiOperation(value = PathProxy.NutritionUrl.GET_NUTRITION_PLAN_CATEGORY, notes = PathProxy.NutritionUrl.GET_NUTRITION_PLAN_CATEGORY)
	public Response<NutritionPlanWithCategoryResponse> getPlanByCategory(NutritionPlanRequest request) {

		Response<NutritionPlanWithCategoryResponse> response = new Response<NutritionPlanWithCategoryResponse>();
		response.setDataList(nutritionService.getNutritionPlanByCategory(request));

		return response;
	}

	
	@PostMapping(PathProxy.NutritionUrl.ADD_EDIT_ASSESSMENT_PATIENT_DETAIL)
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
		response.setData(assessmentFormService.addEditAssessmentPersonalDetail(request));

		return response;
	}

	
	@PostMapping(PathProxy.NutritionUrl.ADD_EDIT_ASSESSMENT_LIFE_STYLE)
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
		response.setData(assessmentFormService.addEditAssessmentLifeStyle(request));

		return response;
	}

	
	@PostMapping(PathProxy.NutritionUrl.ADD_EDIT_ASSESSMENT_FOOD_AND_EXCERCISE)
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
		response.setData(assessmentFormService.addEditFoodAndExcercise(request));

		return response;
	}

	
	@PostMapping(PathProxy.NutritionUrl.ADD_EDIT_ASSESSMENT_PATIENT_HISTORY)
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
		response.setData(assessmentFormService.addEditAssessmentHistory(request));

		return response;
	}

	
	@PostMapping(PathProxy.NutritionUrl.ADD_EDIT_ASSESSMENT_PATIENT_MEASUREMENT)
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
		response.setData(assessmentFormService.addEditPatientMeasurementInfo(request));

		return response;
	}

	
	@GetMapping(PathProxy.NutritionUrl.GET_ASSESSMENT_PATIENT_DETAIL)
	@ApiOperation(value = PathProxy.NutritionUrl.GET_ASSESSMENT_PATIENT_DETAIL, notes = PathProxy.NutritionUrl.GET_ASSESSMENT_PATIENT_DETAIL)
	public Response<AssessmentPersonalDetail> getAssessmentPatientDetail(@RequestParam("page") int page,
			@RequestParam("size") int size, @RequestParam("updateTime") long updateTime,
			@RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded, @RequestParam("doctorId") String doctorId,
			@RequestParam("patientId") String patientId, @RequestParam("locationId") String locationId,
			@RequestParam("hospitalId") String hospitalId) {

		if (DPDoctorUtils.allStringsEmpty(locationId, hospitalId)) {
			throw new BusinessException(ServiceError.InvalidInput, " hospitalId ,locationId should not null");
		}
		Response<AssessmentPersonalDetail> response = new Response<AssessmentPersonalDetail>();
		response.setCount(assessmentFormService.getAssessmentPatientDetailCount(page, size, discarded, updateTime,
				patientId, doctorId, locationId, hospitalId));
		if(response.getCount()>0) {
			response.setDataList(assessmentFormService.getAssessmentPatientDetail(page, size, discarded, updateTime,
					patientId, doctorId, locationId, hospitalId));	
		}

		return response;
	}

	
	@GetMapping(PathProxy.NutritionUrl.GET_ASSESSMENT_LIFE_STYLE)
	@ApiOperation(value = PathProxy.NutritionUrl.GET_ASSESSMENT_LIFE_STYLE, notes = PathProxy.NutritionUrl.GET_ASSESSMENT_LIFE_STYLE)
	public Response<PatientLifeStyle> getPatientLifeStyle(@PathVariable("assessmentId") String assessmentId) {

		if (DPDoctorUtils.allStringsEmpty(assessmentId)) {
			throw new BusinessException(ServiceError.InvalidInput, "assessmentId should not be null");
		}
		Response<PatientLifeStyle> response = new Response<PatientLifeStyle>();
		response.setData(assessmentFormService.getAssessmentLifeStyle(assessmentId));

		return response;
	}

	
	@GetMapping(PathProxy.NutritionUrl.GET_ASSESSMENT_FOOD_AND_EXCERCISE)
	@ApiOperation(value = PathProxy.NutritionUrl.GET_ASSESSMENT_FOOD_AND_EXCERCISE, notes = PathProxy.NutritionUrl.GET_ASSESSMENT_FOOD_AND_EXCERCISE)
	public Response<PatientFoodAndExcercise> getAssessmentFoodAndExcercise(
			@PathVariable("assessmentId") String assessmentId) {

		if (DPDoctorUtils.allStringsEmpty(assessmentId)) {
			throw new BusinessException(ServiceError.InvalidInput, "assessmentId should not be null");
		}
		Response<PatientFoodAndExcercise> response = new Response<PatientFoodAndExcercise>();
		response.setData(assessmentFormService.getPatientFoodAndExcercise(assessmentId));

		return response;
	}

	
	@GetMapping(PathProxy.NutritionUrl.GET_ASSESSMENT_PATIENT_HISTORY)
	@ApiOperation(value = PathProxy.NutritionUrl.GET_ASSESSMENT_PATIENT_HISTORY, notes = PathProxy.NutritionUrl.GET_ASSESSMENT_FOOD_AND_EXCERCISE)
	public Response<AssessmentFormHistoryResponse> getAssessmentHistory(
			@PathVariable("assessmentId") String assessmentId) {

		if (DPDoctorUtils.allStringsEmpty(assessmentId)) {
			throw new BusinessException(ServiceError.InvalidInput, "assessmentId should not be null");
		}
		Response<AssessmentFormHistoryResponse> response = new Response<AssessmentFormHistoryResponse>();
		response.setData(assessmentFormService.getAssessmentHistory(assessmentId));

		return response;
	}

	
	@GetMapping(PathProxy.NutritionUrl.GET_ASSESSMENT_PATIENT_MEASUREMENT)
	@ApiOperation(value = PathProxy.NutritionUrl.GET_ASSESSMENT_PATIENT_MEASUREMENT, notes = PathProxy.NutritionUrl.GET_ASSESSMENT_PATIENT_MEASUREMENT)
	public Response<PatientMeasurementInfo> getAssessmentMeasureInfo(@PathVariable("assessmentId") String assessmentId) {

		if (DPDoctorUtils.allStringsEmpty(assessmentId)) {
			throw new BusinessException(ServiceError.InvalidInput, "assessmentId should not be null");
		}
		Response<PatientMeasurementInfo> response = new Response<PatientMeasurementInfo>();

		response.setData(assessmentFormService.getPatientMeasurementInfo(assessmentId));
		return response;
	}

	
	@GetMapping(value = PathProxy.NutritionUrl.UPDATE_IS_SHARE_WITH_PATIENT)
	@ApiOperation(value = PathProxy.NutritionUrl.UPDATE_IS_SHARE_WITH_PATIENT, notes = PathProxy.NutritionUrl.UPDATE_IS_SHARE_WITH_PATIENT)
	public Response<Boolean> updateShareWithPatent(@PathVariable("recordId") String recordId) {
		if (DPDoctorUtils.anyStringEmpty(recordId)) {
			logger.warn("Record Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput, "Record Id Cannot Be Empty");
		}
		Response<Boolean> response = new Response<Boolean>();
		response.setData(nutritionRecordService.updateShareWithPatient(recordId));
		return response;

	}

	
	@PostMapping(value = PathProxy.NutritionUrl.ADD_NUTRITION_RECORD)
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

	@PostMapping(value = PathProxy.NutritionUrl.UPLOAD_NUTRITION_RECORD_MULTIPART_FILE)
	@Consumes({ MediaType.MULTIPART_FORM_DATA_VALUE})
	@ApiOperation(value = PathProxy.NutritionUrl.UPLOAD_NUTRITION_RECORD_MULTIPART_FILE, notes = PathProxy.NutritionUrl.UPLOAD_NUTRITION_RECORD_MULTIPART_FILE)
	public Response<RecordsFile> uploadNutritionRecordMultipart(@RequestParam("file") MultipartFile file, @RequestBody MyFiileRequest request) {

		if (request == null || DPDoctorUtils.anyStringEmpty(file.getOriginalFilename())) {
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

	@PostMapping(value = PathProxy.NutritionUrl.UPLOAD_NUTRITION_RECORD)
	@Consumes({ MediaType.MULTIPART_FORM_DATA_VALUE})
	@ApiOperation(value = PathProxy.NutritionUrl.UPLOAD_NUTRITION_RECORD, notes = PathProxy.NutritionUrl.UPLOAD_NUTRITION_RECORD)
	public Response<RecordsFile> uploadNutritionRecord(@RequestParam("file") MultipartFile file, DoctorLabReportUploadRequest request) {
		if (request == null || file == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		RecordsFile recordsFiles = nutritionRecordService.uploadNutritionRecord(file, request);

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

	
	@GetMapping(value = PathProxy.NutritionUrl.GET_NUTRITION_RECORD_BY_ID)
	@ApiOperation(value = PathProxy.NutritionUrl.GET_NUTRITION_RECORD_BY_ID, notes = PathProxy.NutritionUrl.GET_NUTRITION_RECORD_BY_ID)
	public Response<NutritionRecord> getRecordById(@PathVariable("recordId") String recordId) {
		if (DPDoctorUtils.anyStringEmpty(recordId)) {
			logger.warn("Record Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput, "report Id Cannot Be Empty");
		}

		NutritionRecord nutritionRecord = nutritionRecordService.getNutritionRecord(recordId);

		Response<NutritionRecord> response = new Response<NutritionRecord>();
		response.setData(nutritionRecord);
		return response;

	}

	
	@GetMapping(value = PathProxy.NutritionUrl.GET_NUTRITION_RECORDS)
	@ApiOperation(value = PathProxy.NutritionUrl.GET_NUTRITION_RECORDS, notes = PathProxy.NutritionUrl.GET_NUTRITION_RECORDS)
	public Response<NutritionRecord> getDoctorLabReports(@RequestParam("page") int page, @RequestParam("size") int size,
			@RequestParam("patientId") String patientId, @RequestParam("doctorId") String doctorId,
			@RequestParam("locationId") String locationId, @RequestParam("hospitalId") String hospitalId,
			@RequestParam("searchTerm") String searchTerm, @RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded,
			@RequestParam("isnutrition") @DefaultValue("isNutrition") Boolean isNutrition) {

		List<NutritionRecord> records = nutritionRecordService.getNutritionRecord(page, size, patientId, doctorId,
				locationId, hospitalId, searchTerm, discarded, isNutrition);
		Response<NutritionRecord> response = new Response<NutritionRecord>();
		response.setDataList(records);
		return response;

	}

	
	@DeleteMapping(PathProxy.NutritionUrl.DELETE_NUTRITION_RECORD)
	@ApiOperation(value = PathProxy.NutritionUrl.DELETE_NUTRITION_RECORD, notes = PathProxy.NutritionUrl.DELETE_NUTRITION_RECORD)
	public Response<NutritionRecord> deleteUserPlanSubscription(@PathVariable("recordId") String recordId,
			@RequestParam("discarded")   Boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(recordId)) {
			throw new BusinessException(ServiceError.InvalidInput, " Invalid input");
		}
		Response<NutritionRecord> response = new Response<NutritionRecord>();
		response.setData(nutritionRecordService.deleteNutritionRecord(recordId, discarded));
		return response;
	}
	

	
	@PostMapping(PathProxy.NutritionUrl.GET_USER_NUTRITION_PLAN)
	@ApiOperation(value = PathProxy.NutritionUrl.GET_USER_NUTRITION_PLAN, notes = PathProxy.NutritionUrl.GET_USER_NUTRITION_PLAN)
	public Response<NutritionPlanWithCategoryShortResponse> getPlanDetailsByCategory(NutritionPlanRequest request) {

		Response<NutritionPlanWithCategoryShortResponse> response = new Response<NutritionPlanWithCategoryShortResponse>();
		response.setDataList(nutritionService.getNutritionPlanDetailsByCategory(request));

		return response;
	}
	
	
	@GetMapping(PathProxy.NutritionUrl.GET_USER_NUTRITION_PLAN_BY_ID)
	@ApiOperation(value = PathProxy.NutritionUrl.GET_USER_NUTRITION_PLAN_BY_ID, notes = PathProxy.NutritionUrl.GET_USER_NUTRITION_PLAN_BY_ID)
	public Response<NutritionPlan> getNutritionPlanById(@PathVariable("id") String id) {

		Response<NutritionPlan> response = null;

		if (DPDoctorUtils.anyStringEmpty(id)) {
			throw new BusinessException(ServiceError.InvalidInput, " Invalid input");
		}

		response = new Response<NutritionPlan>();
		response.setData(nutritionService.getNutritionPlanById(id));

		return response;
	}
	
	
	@GetMapping(value = PathProxy.NutritionUrl.GET_TESTIMONIALS_BY_PLAN_ID)
	@ApiOperation(value = PathProxy.NutritionUrl.GET_TESTIMONIALS_BY_PLAN_ID, notes = PathProxy.NutritionUrl.GET_TESTIMONIALS_BY_PLAN_ID)
	public Response<Testimonial> getTestimonialsByPlanId(@PathVariable("id") String planId , @RequestParam("page") int page ,  @RequestParam("size") int size ) {
		if (planId == null) {
			throw new BusinessException(ServiceError.InvalidInput, " Invalid input");
		}

		Response<Testimonial> response = new Response<Testimonial>();

		response.setDataList(nutritionService.getTestimonialsByPlanId(planId, size, page));

		return response;
	}
	

	
	@PostMapping(value = PathProxy.NutritionUrl.ADD_EDIT_SUGAR_SETTINGS)
	@ApiOperation(value = PathProxy.NutritionUrl.ADD_EDIT_SUGAR_SETTINGS, notes = PathProxy.NutritionUrl.ADD_EDIT_SUGAR_SETTINGS)
	public Response<SugarSetting> addEditSugarSetting(SugarSetting request) {
		if (request == null) {
			throw new BusinessException(ServiceError.InvalidInput, " Invalid input");
		}

		Response<SugarSetting> response = new Response<SugarSetting>();

		response.setData(nutritionService.addEditSugarSetting(request));

		return response;
	}
	
	
	@GetMapping(value = PathProxy.NutritionUrl.GET_SUGAR_SETTINGS_BY_ID)
	@ApiOperation(value = PathProxy.NutritionUrl.GET_SUGAR_SETTINGS_BY_ID, notes = PathProxy.NutritionUrl.GET_SUGAR_SETTINGS_BY_ID)
	public Response<SugarSetting> getSugarSettingById(@PathVariable("id") String id) {
		if (id == null) {
			throw new BusinessException(ServiceError.InvalidInput, " Invalid input");
		}

		Response<SugarSetting> response = new Response<SugarSetting>();

		response.setData(nutritionService.getSugarSettingById(id));

		return response;
	}
	
	

	
	@PostMapping(value = PathProxy.NutritionUrl.ADD_EDIT_BLOOD_GLUCOSE)
	@ApiOperation(value = PathProxy.NutritionUrl.ADD_EDIT_BLOOD_GLUCOSE, notes = PathProxy.NutritionUrl.ADD_EDIT_BLOOD_GLUCOSE)
	public Response<BloodGlucose> addEditBloodGlucose(BloodGlucose request) {
		if (request == null) {
			throw new BusinessException(ServiceError.InvalidInput, " Invalid input");
		}

		Response<BloodGlucose> response = new Response<BloodGlucose>();

		response.setData(nutritionService.addEditBloodGlucose(request));

		return response;
	}
	
	
	@GetMapping(value = PathProxy.NutritionUrl.GET_BLOOD_GLUCOSE_BY_ID)
	@ApiOperation(value = PathProxy.NutritionUrl.GET_BLOOD_GLUCOSE_BY_ID, notes = PathProxy.NutritionUrl.GET_BLOOD_GLUCOSE_BY_ID)
	public Response<BloodGlucose> getBloodGlucoseById(@PathVariable("id") String id) {
		if (id == null) {
			throw new BusinessException(ServiceError.InvalidInput, " Invalid input");
		}

		Response<BloodGlucose> response = new Response<BloodGlucose>();
		response.setData(nutritionService.getBloodGlucoseById(id));
		return response;
	}
	
	
	@GetMapping(value = PathProxy.NutritionUrl.GET_BLOOD_GLUCOSE_LIST_BY_PATIENT_ID)
	@ApiOperation(value = PathProxy.NutritionUrl.GET_BLOOD_GLUCOSE_LIST_BY_PATIENT_ID, notes = PathProxy.NutritionUrl.GET_BLOOD_GLUCOSE_LIST_BY_PATIENT_ID)
	public Response<BloodGlucose> getBloodGlucoseListByPatientId(@RequestParam("patientId") String patientId, @RequestParam("page") int page, @RequestParam("size") int size) {
		if (patientId == null) {
			throw new BusinessException(ServiceError.InvalidInput, " Invalid input");
		}

		Response<BloodGlucose> response = new Response<BloodGlucose>();
		response.setDataList(nutritionService.getBloodGlucoseList(patientId, size, page, null, null));
		return response;
	}
	

	
	@PostMapping(value = PathProxy.NutritionUrl.ADD_EDIT_SUGAR_MEDICINE_REMINDER)
	@ApiOperation(value = PathProxy.NutritionUrl.ADD_EDIT_SUGAR_MEDICINE_REMINDER, notes = PathProxy.NutritionUrl.ADD_EDIT_SUGAR_MEDICINE_REMINDER)
	public Response<SugarMedicineReminder> addEditSugarMedicineReminder(SugarMedicineReminder request) {
		if (request == null) {
			throw new BusinessException(ServiceError.InvalidInput, " Invalid input");
		}

		Response<SugarMedicineReminder> response = new Response<SugarMedicineReminder>();
		response.setData(nutritionService.addEditSugarMedicineReminder(request));

		return response;
	}
	
	
	@GetMapping(value = PathProxy.NutritionUrl.GET_SUGAR_MEDICINE_REMINDER_BY_ID)
	@ApiOperation(value = PathProxy.NutritionUrl.GET_SUGAR_MEDICINE_REMINDER_BY_ID, notes = PathProxy.NutritionUrl.GET_SUGAR_MEDICINE_REMINDER_BY_ID)
	public Response<SugarMedicineReminder> getSugarMedicineReminderById(@PathVariable("id") String id) {
		if (id == null) {
			throw new BusinessException(ServiceError.InvalidInput, " Invalid input");
		}

		Response<SugarMedicineReminder> response = new Response<SugarMedicineReminder>();
		response.setData(nutritionService.getSugarMedicineReminderById(id));
		return response;
	}
	
	
	@GetMapping(value = PathProxy.NutritionUrl.GET_SUGAR_MEDICINE_REMINDER_LIST_BY_PATIENT_ID)
	@ApiOperation(value = PathProxy.NutritionUrl.GET_SUGAR_MEDICINE_REMINDER_LIST_BY_PATIENT_ID, notes = PathProxy.NutritionUrl.GET_SUGAR_MEDICINE_REMINDER_LIST_BY_PATIENT_ID)
	public Response<SugarMedicineReminder> getSugarReminderListByPatientId(@RequestParam("patientId") String patientId, @RequestParam("page") int page, @RequestParam("size") int size) {
		if (patientId == null) {
			throw new BusinessException(ServiceError.InvalidInput, " Invalid input");
		}

		Response<SugarMedicineReminder> response = new Response<SugarMedicineReminder>();
		response.setDataList(nutritionService.getSugarMedicineReminders(patientId, size, page, null, null));
		return response;
	}
	
	
	@GetMapping(value = PathProxy.NutritionUrl.GET_RDA_FOR_PATIENT)
	@ApiOperation(value = PathProxy.NutritionUrl.GET_RDA_FOR_PATIENT, notes = PathProxy.NutritionUrl.GET_RDA_FOR_PATIENT)
	public Response<NutritionRDA> getRDAForPatient(@PathVariable("patientId") String patientId, 
			@RequestParam("doctorId") String doctorId, @RequestParam("locationId") String locationId,
			@RequestParam("hospitalId") String hospitalId) {
		if (patientId == null) {
			throw new BusinessException(ServiceError.InvalidInput, " Invalid input");
		}

		Response<NutritionRDA> response = new Response<NutritionRDA>();
		response.setData(nutritionService.getRDAForPatient(patientId, doctorId, locationId, hospitalId));
		return response;
	}
	
	
	@GetMapping(value = PathProxy.NutritionUrl.GET_NUTRITIONIST_REPORT_OF_DIET_PLAN)
	@ApiOperation(value = PathProxy.NutritionUrl.GET_NUTRITIONIST_REPORT_OF_DIET_PLAN, notes = PathProxy.NutritionUrl.GET_NUTRITIONIST_REPORT_OF_DIET_PLAN)
	public Response<NutritionistReport> getNutrionistReportOfDietPlan(@PathVariable("nutritionistId") String nutritionistId, 
			@RequestParam("fromDate") String fromDate, @RequestParam("toDate") String toDate, 
			@RequestParam("size") int size, @RequestParam("page") int page,
			@RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded){ 
//			@RequestParam("searchTerm") String searchTerm) {
		if (nutritionistId == null) {
			throw new BusinessException(ServiceError.InvalidInput, " Invalid input");
		}

		Response<NutritionistReport> response = nutritionService.getNutrionistReportOfDietPlan(nutritionistId, fromDate, toDate, size, page, discarded, null);
		return response;
	}
	
	
	@GetMapping(value = PathProxy.NutritionUrl.GET_CLUSTERS_OF_STUDENTS)
	@ApiOperation(value = PathProxy.NutritionUrl.GET_CLUSTERS_OF_STUDENTS, notes = PathProxy.NutritionUrl.GET_CLUSTERS_OF_STUDENTS)
	public Response<String> getClusterOfStudents(@PathVariable("schoolId") String schoolId, 
			@RequestParam("branchId") String branchId, @RequestParam("size") int size, @RequestParam("page") int page) {
		if (schoolId == null) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid input");
		}

		Response<String> response = new Response<String>();
		response.setData(nutritionService.getClusterOfStudents(schoolId, branchId, size, page));
		return response;
	}
}
