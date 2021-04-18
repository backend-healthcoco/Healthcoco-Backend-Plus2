package com.dpdocter.webservices;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.Produces;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.dpdocter.beans.FlexibleCounts;
import com.dpdocter.beans.Records;
import com.dpdocter.beans.RecordsFile;
import com.dpdocter.beans.Tags;
import com.dpdocter.beans.UserAllowanceDetails;
import com.dpdocter.beans.UserRecords;
import com.dpdocter.enums.RecordsState;
import com.dpdocter.enums.VisitedFor;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.request.ChangeRecordLabelDescriptionRequest;
import com.dpdocter.request.MyFiileRequest;
import com.dpdocter.request.RecordsAddRequest;
import com.dpdocter.request.RecordsAddRequestMultipart;
import com.dpdocter.request.RecordsEditRequest;
import com.dpdocter.request.RecordsSearchRequest;
import com.dpdocter.request.TagRecordRequest;
import com.dpdocter.services.OTPService;
import com.dpdocter.services.PatientVisitService;
import com.dpdocter.services.RecordsService;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
(PathProxy.RECORDS_BASE_URL)
@Produces(MediaType.APPLICATION_JSON_VALUE)
@Consumes(MediaType.APPLICATION_JSON_VALUE)
@Api(value = PathProxy.RECORDS_BASE_URL, description = "Endpoint for records")
public class RecordsApi {

	private static Logger logger = LogManager.getLogger(RecordsApi.class.getName());

	@Autowired
	private RecordsService recordsService;

	@Autowired
	private PatientVisitService patientTrackService;

	@Value(value = "${image.path}")
	private String imagePath;

	@Autowired
	private OTPService otpService;

	@PostMapping(value = PathProxy.RecordsUrls.ADD_RECORDS)
	@Consumes({ MediaType.MULTIPART_FORM_DATA_VALUE })
	@ApiOperation(value = PathProxy.RecordsUrls.ADD_RECORDS, notes = PathProxy.RecordsUrls.ADD_RECORDS)
	public Response<Records> addRecords(@RequestParam("file") MultipartFile file, RecordsAddRequest request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId(),
				request.getHospitalId(), request.getPatientId()) || file == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		Records records = recordsService.addRecord(file, request, null);

		// patient track
		if (records != null) {
			Records visitRecord = new Records();
			BeanUtil.map(records, visitRecord);
			visitRecord.setPrescriptionId(null);
			records.setRecordsUrl(getFinalImageURL(records.getRecordsUrl()));
			String visitId = patientTrackService.addRecord(visitRecord, VisitedFor.REPORTS, request.getVisitId());
			records.setVisitId(visitId);
		}

		Response<Records> response = new Response<Records>();
		response.setData(records);
		return response;
	}

	
	@PostMapping(value = PathProxy.RecordsUrls.TAG_RECORD)
	@ApiOperation(value = PathProxy.RecordsUrls.TAG_RECORD, notes = PathProxy.RecordsUrls.TAG_RECORD)
	public Response<Boolean> tagRecord(TagRecordRequest request) {
		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		recordsService.tagRecord(request);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(true);
		return response;
	}

	
	@PostMapping(value = PathProxy.RecordsUrls.SEARCH_RECORD)
	@ApiOperation(value = PathProxy.RecordsUrls.SEARCH_RECORD, notes = PathProxy.RecordsUrls.SEARCH_RECORD)
	public Response<Records> searchRecords(RecordsSearchRequest request) {
//		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId())) {
//			logger.warn("Invalid Input");
//			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
//		}
		request.setIsOTPVerified(otpService.checkOTPVerified(request.getDoctorId(), request.getLocationId(),
				request.getHospitalId(), request.getPatientId()));
		List<Records> records = recordsService.searchRecords(request);

		Response<Records> response = new Response<Records>();
		response.setDataList(records);
		return response;
	}

	
	@GetMapping(value = PathProxy.RecordsUrls.GET_RECORD_BY_ID)
	@ApiOperation(value = "GET_RECORDS_BY_ID", notes = "GET_RECORDS_BY_ID")
	public Response<Records> getRecordById(@PathVariable("recordId") String recordId) {
		if (DPDoctorUtils.anyStringEmpty(recordId)) {
			logger.warn("Record Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput, "Record Id Cannot Be Empty");
		}

		Records record = recordsService.getRecordById(recordId);

		Response<Records> response = new Response<Records>();
		response.setData(record);
		return response;

	}

	
	@GetMapping(value = PathProxy.RecordsUrls.GET_RECORDS_PATIENT_ID)
	@ApiOperation(value = PathProxy.RecordsUrls.GET_RECORDS_PATIENT_ID, notes = PathProxy.RecordsUrls.GET_RECORDS_PATIENT_ID)
	public Response<Object> getRecordsByPatientId(@PathVariable("patientId") String patientId,
			@RequestParam("page") int page, @RequestParam("size") int size,
			@DefaultValue("0") @RequestParam("updatedTime") String updatedTime,
			  @RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded,
			@DefaultValue("false") @RequestParam("isDoctorApp") Boolean isDoctorApp, @RequestParam(value = "sortBy") String sortBy) {
		if (DPDoctorUtils.anyStringEmpty(patientId)) {
			logger.warn("Patient Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput, "Patient Id Cannot Be Empty");
		}

		Response<Object> response = recordsService.getRecordsByPatientId(patientId, page, size, updatedTime, discarded,
				isDoctorApp, sortBy);
		return response;

	}

	
	@GetMapping(value = PathProxy.RecordsUrls.GET_RECORDS_DOCTOR_ID)
	@ApiOperation(value = PathProxy.RecordsUrls.GET_RECORDS_DOCTOR_ID, notes = PathProxy.RecordsUrls.GET_RECORDS_DOCTOR_ID)
	public Response<Records> getRecordsByDoctorId(@PathVariable("doctorId") String doctorId, @RequestParam("page") long page,
			@RequestParam("size") int size, @DefaultValue("0") @RequestParam("updatedTime") String updatedTime,
			  @RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(doctorId)) {
			logger.warn("Doctor Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput, "Doctor Id Cannot Be Empty");
		}

		List<Records> records = recordsService.getRecordsByDoctorId(doctorId, page, size, updatedTime, discarded);

		Response<Records> response = new Response<Records>();
		response.setDataList(records);
		return response;

	}

	
	@GetMapping(value = PathProxy.RecordsUrls.GET_RECORD_COUNT)
	@ApiOperation(value = PathProxy.RecordsUrls.GET_RECORD_COUNT, notes = PathProxy.RecordsUrls.GET_RECORD_COUNT)
	public Response<Integer> getRecordCount(@PathVariable("doctorId") String doctorId,
			@PathVariable("patientId") String patientId, @PathVariable("locationId") String locationId,
			@PathVariable("hospitalId") String hospitalId) {
		if (DPDoctorUtils.anyStringEmpty(doctorId, patientId, locationId, hospitalId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Boolean isOTPVerified = otpService.checkOTPVerified(doctorId, locationId, hospitalId, patientId);
		Integer recordCount = recordsService.getRecordCount(new ObjectId(doctorId), new ObjectId(patientId),
				new ObjectId(locationId), new ObjectId(hospitalId), isOTPVerified);
		Response<Integer> response = new Response<Integer>();
		response.setData(recordCount);
		return response;
	}

	
	@GetMapping(value = PathProxy.RecordsUrls.GET_ALL_TAGS)
	@ApiOperation(value = PathProxy.RecordsUrls.GET_ALL_TAGS, notes = PathProxy.RecordsUrls.GET_ALL_TAGS)
	public Response<Tags> getAllTags(@PathVariable("doctorId") String doctorId, @PathVariable("locationId") String locationId,
			@PathVariable("hospitalId") String hospitalId) {
		if (DPDoctorUtils.anyStringEmpty(doctorId, locationId, hospitalId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		List<Tags> tags = recordsService.getAllTags(doctorId, locationId, hospitalId);
		Response<Tags> response = new Response<Tags>();
		response.setDataList(tags);
		return response;
	}

	
	@PostMapping(value = PathProxy.RecordsUrls.CREATE_TAG)
	@ApiOperation(value = PathProxy.RecordsUrls.CREATE_TAG, notes = PathProxy.RecordsUrls.CREATE_TAG)
	public Response<Tags> createTag(Tags tags) {
		if (tags == null) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		tags = recordsService.addEditTag(tags);
		Response<Tags> response = new Response<Tags>();
		response.setData(tags);
		return response;
	}

	
	@GetMapping(value = PathProxy.RecordsUrls.GET_PATIENT_EMAIL_ADD)
	@ApiOperation(value = PathProxy.RecordsUrls.GET_PATIENT_EMAIL_ADD, notes = PathProxy.RecordsUrls.GET_PATIENT_EMAIL_ADD)
	public Response<String> getPatientEmailId(@PathVariable("patientId") String patientId) {
		String emailAdd = recordsService.getPatientEmailAddress(patientId);
		Response<String> response = new Response<String>();
		response.setData(emailAdd);
		return response;
	}

	
	@GetMapping(value = PathProxy.RecordsUrls.EMAIL_RECORD)
	@ApiOperation(value = PathProxy.RecordsUrls.EMAIL_RECORD, notes = PathProxy.RecordsUrls.EMAIL_RECORD)
	public Response<Boolean> emailRecords(@PathVariable("recordId") String recordId,
			@PathVariable(value = "doctorId") String doctorId, @PathVariable(value = "locationId") String locationId,
			@PathVariable(value = "hospitalId") String hospitalId, @PathVariable("emailAddress") String emailAddress,
			@MatrixParam("fileIds") List<String> fileIds) {
		if (DPDoctorUtils.anyStringEmpty(recordId, emailAddress, doctorId, locationId, hospitalId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		recordsService.emailRecordToPatient(recordId, doctorId, locationId, hospitalId, emailAddress);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(true);
		return response;
	}

	
	@DeleteMapping(value = PathProxy.RecordsUrls.DELETE_RECORD)
	@ApiOperation(value = PathProxy.RecordsUrls.DELETE_RECORD, notes = PathProxy.RecordsUrls.DELETE_RECORD)
	public Response<Records> deleteRecords(@PathVariable("recordId") String recordId,
			  @RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(recordId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Records records = recordsService.deleteRecord(recordId, discarded);
		Response<Records> response = new Response<Records>();
		response.setData(records);
		return response;
	}

	
	@DeleteMapping(value = PathProxy.RecordsUrls.DELETE_TAG)
	@ApiOperation(value = PathProxy.RecordsUrls.DELETE_TAG, notes = PathProxy.RecordsUrls.DELETE_TAG)
	public Response<Tags> deleteTag(@PathVariable("tagid") String tagid,
			  @RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(tagid)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Tags tags = recordsService.deleteTag(tagid, discarded);
		Response<Tags> response = new Response<Tags>();
		response.setData(tags);
		return response;
	}

	
	@PostMapping(value = PathProxy.RecordsUrls.GET_FLEXIBLE_COUNTS)
	@ApiOperation(value = PathProxy.RecordsUrls.GET_FLEXIBLE_COUNTS, notes = PathProxy.RecordsUrls.GET_FLEXIBLE_COUNTS)
	public Response<FlexibleCounts> getCounts(FlexibleCounts flexibleCounts) {
		if (flexibleCounts == null || DPDoctorUtils.anyStringEmpty(flexibleCounts.getDoctorId(),
				flexibleCounts.getPatientId(), flexibleCounts.getLocationId(), flexibleCounts.getHospitalId())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		FlexibleCounts flexibleCountsResponse = recordsService.getFlexibleCounts(flexibleCounts);
		Response<FlexibleCounts> response = new Response<FlexibleCounts>();
		response.setData(flexibleCountsResponse);
		return response;
	}

	
	@PutMapping(value = PathProxy.RecordsUrls.EDIT_RECORD)
	@ApiOperation(value = PathProxy.RecordsUrls.EDIT_RECORD, notes = PathProxy.RecordsUrls.EDIT_RECORD)
	public Response<Records> editRecords(@PathVariable(value = "recordId") String recordId, @RequestParam("file") MultipartFile file, 
			RecordsEditRequest request) {
		if (DPDoctorUtils.anyStringEmpty(recordId) || request == null) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		request.setId(recordId);
		Records records = recordsService.editRecord(file, request);
		if (records != null) {
			records.setRecordsUrl(getFinalImageURL(records.getRecordsUrl()));
			String visitId = patientTrackService.editRecord(records.getId(), VisitedFor.REPORTS);
			records.setVisitId(visitId);
		}
		Response<Records> response = new Response<Records>();
		response.setData(records);
		return response;

	}

	private String getFinalImageURL(String imageURL) {
		if (imageURL != null) {
			return imagePath + imageURL;
		} else
			return null;
	}

	
	@PostMapping(value = PathProxy.RecordsUrls.CHANGE_LABEL_AND_DESCRIPTION_RECORD)
	@ApiOperation(value = PathProxy.RecordsUrls.CHANGE_LABEL_AND_DESCRIPTION_RECORD, notes = PathProxy.RecordsUrls.CHANGE_LABEL_AND_DESCRIPTION_RECORD)
	public Response<Boolean> changeLabelAndDescription(ChangeRecordLabelDescriptionRequest request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getRecordId())) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		recordsService.changeLabelAndDescription(request.getRecordId(), request.getLabel(), request.getExplanation());
		Response<Boolean> response = new Response<Boolean>();
		response.setData(true);
		return response;
	}

	@PostMapping(value = PathProxy.RecordsUrls.ADD_RECORDS_MULTIPART)
	@Consumes({ MediaType.MULTIPART_FORM_DATA_VALUE})
	@ApiOperation(value = PathProxy.RecordsUrls.ADD_RECORDS_MULTIPART, notes = PathProxy.RecordsUrls.ADD_RECORDS_MULTIPART)
	public Response<Records> addRecordsMultipart(@RequestParam("file") MultipartFile file, RecordsAddRequestMultipart request) {
		if (request == null) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		Records records = recordsService.addRecordsMultipart(file, request);

		// patient track
		if (records != null) {
			records.setRecordsUrl(getFinalImageURL(records.getRecordsUrl()));
			String visitId = patientTrackService.addRecord(records, VisitedFor.REPORTS, request.getVisitId());
			records.setVisitId(visitId);
		}

		Response<Records> response = new Response<Records>();
		response.setData(records);
		return response;
	}

	@PostMapping(value = PathProxy.RecordsUrls.SAVE_RECORDS_IMAGE)
	@Consumes({ MediaType.MULTIPART_FORM_DATA_VALUE})
	@ApiOperation(value = PathProxy.RecordsUrls.SAVE_RECORDS_IMAGE, notes = PathProxy.RecordsUrls.SAVE_RECORDS_IMAGE)
	public Response<String> saveRecordsImage(@RequestParam("file") MultipartFile file, @PathVariable("patientId")  String patientId) {
	
		String imageURL = recordsService.saveRecordsImage(file, patientId);
		imageURL = getFinalImageURL(imageURL);
		Response<String> response = new Response<String>();
		response.setData(imageURL);
		return response;
	}

	
	@GetMapping(value = PathProxy.RecordsUrls.CHANGE_RECORD_STATE)
	@ApiOperation(value = PathProxy.RecordsUrls.CHANGE_RECORD_STATE, notes = PathProxy.RecordsUrls.CHANGE_RECORD_STATE)
	public Response<Records> changeRecordState(@PathVariable("recordId") String recordId,
			@PathVariable("recordsState") String recordsState) {
		if (DPDoctorUtils.anyStringEmpty(recordId, recordsState)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		if (!recordsState.equalsIgnoreCase(RecordsState.APPROVED_BY_DOCTOR.toString())
				&& !recordsState.equalsIgnoreCase(RecordsState.DECLINED_BY_DOCTOR.toString())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Records records = recordsService.changeRecordState(recordId, recordsState);
		Response<Records> response = new Response<Records>();
		response.setData(records);
		return response;
	}

	
	@PostMapping(value = PathProxy.RecordsUrls.ADD_USER_RECORDS)
	@ApiOperation(value = PathProxy.RecordsUrls.ADD_USER_RECORDS, notes = PathProxy.RecordsUrls.ADD_USER_RECORDS)
	public Response<UserRecords> addUserRecords(UserRecords request) {

		if (request == null) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		UserRecords records = recordsService.addUserRecords(request);

		if (records != null) {
			for (RecordsFile recordsFile : records.getRecordsFiles()) {
				recordsFile.setRecordsUrl(getFinalImageURL(recordsFile.getRecordsUrl()));
				recordsFile.setThumbnailUrl(getFinalImageURL(recordsFile.getThumbnailUrl()));
			}
		}

		Response<UserRecords> response = new Response<UserRecords>();
		response.setData(records);
		return response;
	}

	@PostMapping(value = PathProxy.RecordsUrls.UPLOAD_USER_RECORD_FILE)
	@Consumes({ MediaType.MULTIPART_FORM_DATA_VALUE})
	@ApiOperation(value = PathProxy.RecordsUrls.UPLOAD_USER_RECORD_FILE, notes = PathProxy.RecordsUrls.UPLOAD_USER_RECORD_FILE)
	public Response<RecordsFile> uploadUserRecord(@RequestParam("file") MultipartFile file, MyFiileRequest request) {

		if (request == null || DPDoctorUtils.anyStringEmpty(file.getOriginalFilename())) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		RecordsFile recordsFile = recordsService.uploadUserRecord(file, request);
		if (recordsFile != null) {
			recordsFile.setRecordsUrl(getFinalImageURL(recordsFile.getRecordsUrl()));
			recordsFile.setThumbnailUrl(getFinalImageURL(recordsFile.getThumbnailUrl()));
		}

		Response<RecordsFile> response = new Response<RecordsFile>();
		response.setData(recordsFile);
		return response;
	}

	
	@GetMapping(value = PathProxy.RecordsUrls.GET_USER_RECORD_BY_ID)
	@ApiOperation(value = "GET_USER_RECORD_BY_ID", notes = "GET_USER_RECORD_BY_ID")
	public Response<UserRecords> getUserRecordById(@PathVariable("recordId") String recordId) {
		if (DPDoctorUtils.anyStringEmpty(recordId)) {
			logger.warn("Record Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput, "Record Id Cannot Be Empty");
		}

		UserRecords record = recordsService.getUserRecordById(recordId);

		Response<UserRecords> response = new Response<UserRecords>();
		response.setData(record);
		return response;

	}

	
	@GetMapping(value = PathProxy.RecordsUrls.GET_USER_RECORDS)
	@ApiOperation(value = PathProxy.RecordsUrls.GET_USER_RECORDS, notes = PathProxy.RecordsUrls.GET_USER_RECORDS)
	public Response<Object> getUserRecords(@RequestParam("patientId") String patientId, @RequestParam("page") long page,
			@RequestParam("size") int size, @RequestParam("doctorId") String doctorId,
			@RequestParam("locationId") String locationId, @RequestParam("hospitalId") String hospitalId,
			@DefaultValue("0") @RequestParam("updatedTime") String updatedTime,
			  @RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(patientId) && DPDoctorUtils.anyStringEmpty(hospitalId, doctorId, locationId)) {
			logger.warn("Patient Id or hospitalId ,doctorId ,locationId Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Patient Id or hospitalId ,doctorId ,locationId Cannot Be Empty");
		}
		Response<Object> response = recordsService.getUserRecordsByuserId(patientId, doctorId, locationId, hospitalId,
				page, size, updatedTime, discarded);
		
		return response;

	}

	
	@GetMapping(value = PathProxy.RecordsUrls.GET_USER_RECORDS_ALLOWANCE)
	@ApiOperation(value = "GET_USER_RECORDS_ALLOWANCE", notes = "GET_USER_RECORDS_ALLOWANCE")
	public Response<UserAllowanceDetails> getUserRecordAllowance(@RequestParam("userId") String userId,
			@RequestParam("mobileNumber") String mobileNumber) {
		if (DPDoctorUtils.anyStringEmpty(userId) && DPDoctorUtils.anyStringEmpty(mobileNumber)) {
			logger.warn("Record Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput, "Record Id Cannot Be Empty");
		}

		UserAllowanceDetails record = recordsService.getUserRecordAllowance(userId, mobileNumber);

		Response<UserAllowanceDetails> response = new Response<UserAllowanceDetails>();
		response.setData(record);
		return response;

	}

	
	@DeleteMapping(value = PathProxy.RecordsUrls.DELETE_OR_HIDE_USER_RECORD)
	@ApiOperation(value = PathProxy.RecordsUrls.DELETE_OR_HIDE_USER_RECORD, notes = PathProxy.RecordsUrls.DELETE_OR_HIDE_USER_RECORD)
	public Response<UserRecords> deleteUserRecord(@PathVariable("recordId") String recordId,
			  @RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded,
			@DefaultValue("false") @RequestParam("isVisible") Boolean isVisible) {
		if (DPDoctorUtils.anyStringEmpty(recordId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		UserRecords records = recordsService.deleteUserRecord(recordId, discarded, isVisible);
		Response<UserRecords> response = new Response<UserRecords>();
		response.setData(records);
		return response;
	}

	
	@DeleteMapping(value = PathProxy.RecordsUrls.DELETE_USER_RECORDS_FILE)
	@ApiOperation(value = PathProxy.RecordsUrls.DELETE_USER_RECORDS_FILE, notes = PathProxy.RecordsUrls.DELETE_USER_RECORDS_FILE)
	public Response<UserRecords> deleteUserRecordFile(@PathVariable("recordId") String recordId,
			@MatrixParam("fileIds") List<String> fileIds) {
		if (DPDoctorUtils.anyStringEmpty(recordId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		UserRecords records = recordsService.deleteUserRecordsFile(recordId, fileIds);
		Response<UserRecords> response = new Response<UserRecords>();
		response.setData(records);
		return response;
	}

	
	@GetMapping(value = PathProxy.RecordsUrls.SHARE_RECORD_WITH_PATIENT)
	@ApiOperation(value = PathProxy.RecordsUrls.SHARE_RECORD_WITH_PATIENT, notes = PathProxy.RecordsUrls.SHARE_RECORD_WITH_PATIENT)
	public Response<Boolean> shareUserRecords(@PathVariable("recordId") String recordId) {
		if (DPDoctorUtils.anyStringEmpty(recordId)) {
			logger.warn("Record Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput, "Record Id cannot Be Empty");
		}
		Response<Boolean> response = new Response<Boolean>();
		response.setData(recordsService.updateShareWithPatient(recordId));
		return response;

	}

}
