package com.dpdocter.webservices;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.dpdocter.beans.FileDownloadResponse;
import com.dpdocter.beans.FlexibleCounts;
import com.dpdocter.beans.Records;
import com.dpdocter.beans.Tags;
import com.dpdocter.enums.RecordsState;
import com.dpdocter.enums.VisitedFor;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.request.ChangeRecordLabelDescriptionRequest;
import com.dpdocter.request.RecordsAddRequest;
import com.dpdocter.request.RecordsAddRequestMultipart;
import com.dpdocter.request.RecordsEditRequest;
import com.dpdocter.request.RecordsSearchRequest;
import com.dpdocter.request.TagRecordRequest;
import com.dpdocter.services.OTPService;
import com.dpdocter.services.PatientVisitService;
import com.dpdocter.services.RecordsService;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataParam;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Component
@Path(PathProxy.RECORDS_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.RECORDS_BASE_URL, description = "Endpoint for records")
public class RecordsApi {
	
	private static Logger logger = Logger.getLogger(RecordsApi.class.getName());
	
    @Autowired
    private RecordsService recordsService;

    @Autowired
    private PatientVisitService patientTrackService;

    @Value(value = "${image.path}")
    private String imagePath;

    @Autowired
    private OTPService otpService;

    @POST
    @Path(value = PathProxy.RecordsUrls.ADD_RECORDS)
    @ApiOperation(value = PathProxy.RecordsUrls.ADD_RECORDS, notes = PathProxy.RecordsUrls.ADD_RECORDS)
    public Response<Records> addRecords(RecordsAddRequest request) {
	if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId(), request.getHospitalId(), request.getPatientId()) || request.getFileDetails() == null) {
	    logger.warn("Invalid Input");
		throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
	}

	Records records = recordsService.addRecord(request);

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

    @Path(value = PathProxy.RecordsUrls.TAG_RECORD)
    @POST
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

    @Path(value = PathProxy.RecordsUrls.SEARCH_RECORD)
    @POST
    @ApiOperation(value = PathProxy.RecordsUrls.SEARCH_RECORD, notes = PathProxy.RecordsUrls.SEARCH_RECORD)
    public Response<Records> searchRecords(RecordsSearchRequest request) {
	if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId())) {
		logger.warn("Invalid Input");
	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
	}
	request.setIsOTPVerified(otpService.checkOTPVerified(request.getDoctorId(), request.getLocationId(), request.getHospitalId(), request.getPatientId()));
	List<Records> records = recordsService.searchRecords(request);

	Response<Records> response = new Response<Records>();
	response.setDataList(records);
	return response;
    }

    @Path(value = PathProxy.RecordsUrls.GET_RECORD_BY_ID)
    @GET
    @ApiOperation(value = "GET_RECORDS_BY_ID", notes = "GET_RECORDS_BY_ID")
    public Response<Records> getRecordById(@PathParam("recordId") String recordId) {
	if (DPDoctorUtils.anyStringEmpty(recordId)) {
		logger.warn("Record Id Cannot Be Empty");
	    throw new BusinessException(ServiceError.InvalidInput, "Record Id Cannot Be Empty");
	}

	Records record = recordsService.getRecordById(recordId);

	Response<Records> response = new Response<Records>();
	response.setData(record);
	return response;

    }
    
    @Path(value = PathProxy.RecordsUrls.GET_RECORDS_PATIENT_ID)
    @GET
    @ApiOperation(value = PathProxy.RecordsUrls.GET_RECORDS_PATIENT_ID, notes = PathProxy.RecordsUrls.GET_RECORDS_PATIENT_ID)
    public Response<Records> getRecordsByPatientId(@PathParam("patientId") String patientId, @QueryParam("page") int page, @QueryParam("size") int size,
	    @DefaultValue("0") @QueryParam("updatedTime") String updatedTime, @DefaultValue("true") @QueryParam("discarded") Boolean discarded,
	    @DefaultValue("false") @QueryParam("isDoctorApp") Boolean isDoctorApp) {
	if (DPDoctorUtils.anyStringEmpty(patientId)) {
		logger.warn("Patient Id Cannot Be Empty");
	    throw new BusinessException(ServiceError.InvalidInput, "Patient Id Cannot Be Empty");
	}

	List<Records> records = recordsService.getRecordsByPatientId(patientId, page, size, updatedTime, discarded, isDoctorApp);

	Response<Records> response = new Response<Records>();
	response.setDataList(records);
	return response;

    }

    @Path(value = PathProxy.RecordsUrls.GET_RECORD_COUNT)
    @GET
    @ApiOperation(value = PathProxy.RecordsUrls.GET_RECORD_COUNT, notes = PathProxy.RecordsUrls.GET_RECORD_COUNT)
    public Response<Integer> getRecordCount(@PathParam("doctorId") String doctorId, @PathParam("patientId") String patientId,
	    @PathParam("locationId") String locationId, @PathParam("hospitalId") String hospitalId) {
    	if (DPDoctorUtils.anyStringEmpty(doctorId, patientId, locationId, hospitalId)) {
    		logger.warn("Invalid Input");
    	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
    	}
		Boolean isOTPVerified = otpService.checkOTPVerified(doctorId, locationId, hospitalId, patientId);
		Integer recordCount = recordsService.getRecordCount(doctorId, patientId, locationId, hospitalId, isOTPVerified);
		Response<Integer> response = new Response<Integer>();
		response.setData(recordCount);
	return response;
    }

    @Path(value = PathProxy.RecordsUrls.GET_ALL_TAGS)
    @GET
    @ApiOperation(value = PathProxy.RecordsUrls.GET_ALL_TAGS, notes = PathProxy.RecordsUrls.GET_ALL_TAGS)
    public Response<Tags> getAllTags(@PathParam("doctorId") String doctorId, @PathParam("locationId") String locationId, @PathParam("hospitalId") String hospitalId) {
    	if (DPDoctorUtils.anyStringEmpty(doctorId, locationId, hospitalId)) {
    		logger.warn("Invalid Input");
    	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
    	}
	List<Tags> tags = recordsService.getAllTags(doctorId, locationId, hospitalId);
	Response<Tags> response = new Response<Tags>();
	response.setDataList(tags);
	return response;
    }

    @Path(value = PathProxy.RecordsUrls.CREATE_TAG)
    @POST
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

    @Path(value = PathProxy.RecordsUrls.GET_PATIENT_EMAIL_ADD)
    @GET
    @ApiOperation(value = PathProxy.RecordsUrls.GET_PATIENT_EMAIL_ADD, notes = PathProxy.RecordsUrls.GET_PATIENT_EMAIL_ADD)
    public Response<String> getPatientEmailId(@PathParam("patientId") String patientId) {
	String emailAdd = recordsService.getPatientEmailAddress(patientId);
	Response<String> response = new Response<String>();
	response.setData(emailAdd);
	return response;
    }

    @Path(value = PathProxy.RecordsUrls.EMAIL_RECORD)
    @GET
    @ApiOperation(value = PathProxy.RecordsUrls.EMAIL_RECORD, notes = PathProxy.RecordsUrls.EMAIL_RECORD)
    public Response<Boolean> emailRecords(@PathParam("recordId") String recordId, @PathParam(value = "doctorId") String doctorId,
	    @PathParam(value = "locationId") String locationId, @PathParam(value = "hospitalId") String hospitalId,
	    @PathParam("emailAddress") String emailAddress) {
    	if (DPDoctorUtils.anyStringEmpty(recordId, emailAddress, doctorId, locationId, hospitalId)) {
    		logger.warn("Invalid Input");
    	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
    	}
		recordsService.emailRecordToPatient(recordId, doctorId, locationId, hospitalId, emailAddress);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(true);
	return response;
    }

    @Path(value = PathProxy.RecordsUrls.DELETE_RECORD)
    @DELETE
    @ApiOperation(value = PathProxy.RecordsUrls.DELETE_RECORD, notes = PathProxy.RecordsUrls.DELETE_RECORD)
    public Response<Records> deleteRecords(@PathParam("recordId") String recordId, @DefaultValue("true") @QueryParam("discarded") Boolean discarded) {
    	if (DPDoctorUtils.anyStringEmpty(recordId)) {
    		logger.warn("Invalid Input");
    	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
    	}
    	Records records = recordsService.deleteRecord(recordId, discarded);
		Response<Records> response = new Response<Records>();
		response.setData(records);
	return response;
    }

    @Path(value = PathProxy.RecordsUrls.DELETE_TAG)
    @DELETE
    @ApiOperation(value = PathProxy.RecordsUrls.DELETE_TAG, notes = PathProxy.RecordsUrls.DELETE_TAG)
    public Response<Tags> deleteTag(@PathParam("tagid") String tagid, @DefaultValue("true") @QueryParam("discarded") Boolean discarded) {
    	if (DPDoctorUtils.anyStringEmpty(tagid)) {
    		logger.warn("Invalid Input");
    	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
    	}
    	Tags tags = recordsService.deleteTag(tagid, discarded);
	Response<Tags> response = new Response<Tags>();
	response.setData(tags);
	return response;
    }

    @Path(value = PathProxy.RecordsUrls.DOWNLOAD_RECORD)
    @GET
    @ApiOperation(value = PathProxy.RecordsUrls.DOWNLOAD_RECORD, notes = PathProxy.RecordsUrls.DOWNLOAD_RECORD)
    public javax.ws.rs.core.Response downloadRecords(@PathParam("recordId") String recordId) {
    	FileDownloadResponse file = recordsService.getRecordFile(recordId);
	if (file == null) {
	    ResponseBuilder response = javax.ws.rs.core.Response.status(Status.BAD_REQUEST);
	    return response.build();
	}
	ResponseBuilder response = javax.ws.rs.core.Response.ok(file.getInputStream());
	response.header("Content-Disposition", "attachment; filename=" + file.getFileName());
	return response.build();
    }

    @Path(value = PathProxy.RecordsUrls.GET_FLEXIBLE_COUNTS)
    @POST
    @ApiOperation(value = PathProxy.RecordsUrls.GET_FLEXIBLE_COUNTS, notes = PathProxy.RecordsUrls.GET_FLEXIBLE_COUNTS)
    public Response<FlexibleCounts> getCounts(FlexibleCounts flexibleCounts) {
    	if (flexibleCounts == null || DPDoctorUtils.anyStringEmpty(flexibleCounts.getDoctorId(), flexibleCounts.getPatientId(), flexibleCounts.getLocationId(), flexibleCounts.getHospitalId())) {
    		logger.warn("Invalid Input");
    	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
    	}
		FlexibleCounts flexibleCountsResponse = recordsService.getFlexibleCounts(flexibleCounts);
		Response<FlexibleCounts> response = new Response<FlexibleCounts>();
		response.setData(flexibleCountsResponse);
	return response;
    }

    @Path(value = PathProxy.RecordsUrls.EDIT_RECORD)
    @PUT
    @ApiOperation(value = PathProxy.RecordsUrls.EDIT_RECORD, notes = PathProxy.RecordsUrls.EDIT_RECORD)
    public Response<Records> editRecords(@PathParam(value = "recordId") String recordId, RecordsEditRequest request) {
	if (DPDoctorUtils.anyStringEmpty(recordId) || request == null) {
	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
	}
	request.setId(recordId);
	Records records = recordsService.editRecord(request);
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

    @Path(value = PathProxy.RecordsUrls.CHANGE_LABEL_AND_DESCRIPTION_RECORD)
    @POST
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

    @POST
    @Path(value = PathProxy.RecordsUrls.ADD_RECORDS_MULTIPART)
    @Consumes({ MediaType.MULTIPART_FORM_DATA })
    @ApiOperation(value = PathProxy.RecordsUrls.ADD_RECORDS_MULTIPART, notes = PathProxy.RecordsUrls.ADD_RECORDS_MULTIPART)
    public Response<Records> addRecordsMultipart(@FormDataParam("file") FormDataBodyPart file, @FormDataParam("data") FormDataBodyPart data) {
    	data.setMediaType(MediaType.APPLICATION_JSON_TYPE);
    	RecordsAddRequestMultipart request = data.getValueAs(RecordsAddRequestMultipart.class);

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
    
    @POST
    @Path(value = PathProxy.RecordsUrls.SAVE_RECORDS_IMAGE)
    @Consumes({ MediaType.MULTIPART_FORM_DATA })
    @ApiOperation(value = PathProxy.RecordsUrls.SAVE_RECORDS_IMAGE, notes = PathProxy.RecordsUrls.SAVE_RECORDS_IMAGE)
    public Response<String> saveRecordsImage(@FormDataParam("file") FormDataBodyPart file, @FormDataParam("patientId") FormDataBodyPart patientId) {
    	patientId.setMediaType(MediaType.APPLICATION_JSON_TYPE);
    	String patientIdString = patientId.getValueAs(String.class);

    String	imageURL = recordsService.saveRecordsImage(file, patientIdString);
    imageURL = getFinalImageURL(imageURL);
	Response<String> response = new Response<String>();
	response.setData(imageURL);
	return response;
    }

    @Path(value = PathProxy.RecordsUrls.CHANGE_RECORD_STATE)
    @GET
    @ApiOperation(value = PathProxy.RecordsUrls.CHANGE_RECORD_STATE, notes = PathProxy.RecordsUrls.CHANGE_RECORD_STATE)
    public Response<Records> changeRecordState(@PathParam("recordId") String recordId, @PathParam("recordsState") String recordsState) {
    	if (DPDoctorUtils.anyStringEmpty(recordId, recordsState)) {
    		logger.warn("Invalid Input");
    	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
    	}
    	if (!recordsState.equalsIgnoreCase(RecordsState.APPROVED_BY_DOCTOR.toString()) && !recordsState.equalsIgnoreCase(RecordsState.DECLINED_BY_DOCTOR.toString())) {
    		logger.warn("Invalid Input");
    	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
    	}
    	Records records = recordsService.changeRecordState(recordId, recordsState);
		Response<Records> response = new Response<Records>();
		response.setData(records);
	return response;
    }

}
