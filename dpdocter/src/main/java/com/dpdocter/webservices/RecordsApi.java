package com.dpdocter.webservices;

import java.io.File;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.dpdocter.beans.FlexibleCounts;
import com.dpdocter.beans.Records;
import com.dpdocter.beans.RecordsDescription;
import com.dpdocter.beans.Tags;
import com.dpdocter.enums.VisitedFor;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.request.ChangeRecordLabelRequest;
import com.dpdocter.request.RecordsAddRequest;
import com.dpdocter.request.RecordsEditRequest;
import com.dpdocter.request.RecordsSearchRequest;
import com.dpdocter.request.TagRecordRequest;
import com.dpdocter.services.PatientVisitService;
import com.dpdocter.services.RecordsService;
import common.util.web.DPDoctorUtils;
import common.util.web.Response;

@Component
@Path(PathProxy.RECORDS_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RecordsApi {
    @Autowired
    private RecordsService recordsService;

    @Autowired
    private PatientVisitService patientTrackService;

    @Context
    private UriInfo uriInfo;

    @Value(value = "${IMAGE_URL_ROOT_PATH}")
    private String imageUrlRootPath;

    @POST
    @Path(value = PathProxy.RecordsUrls.ADD_RECORDS)
    public Response<Records> addRecords(RecordsAddRequest request) {
	if (request == null) {
	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
	}

	Records records = recordsService.addRecord(request);

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

    @Path(value = PathProxy.RecordsUrls.TAG_RECORD)
    @POST
    public Response<Boolean> tagRecord(TagRecordRequest request) {
	if (request == null) {
	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
	}
	recordsService.tagRecord(request);
	Response<Boolean> response = new Response<Boolean>();
	response.setData(true);
	return response;
    }

    @Path(value = PathProxy.RecordsUrls.SEARCH_RECORD)
    @POST
    public Response<Records> searchRecords(RecordsSearchRequest request) {
	if (request == null) {
	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
	}
	List<Records> records = recordsService.searchRecords(request);
	if (records != null && !records.isEmpty()) {
	    for (Records record : records) {
		record.setRecordsUrl(getFinalImageURL(record.getRecordsUrl()));
	    }
	}
	Response<Records> response = new Response<Records>();
	response.setDataList(records);
	return response;
    }

    @Path(value = PathProxy.RecordsUrls.GET_RECORD_COUNT)
    @GET
    public Response<Integer> getRecordCount(@PathParam("doctorId") String doctorId, @PathParam("patientId") String patientId,
	    @PathParam("locationId") String locationId, @PathParam("hospitalId") String hospitalId) {
	Integer recordCount = recordsService.getRecordCount(doctorId, patientId, locationId, hospitalId);
	Response<Integer> response = new Response<Integer>();
	response.setData(recordCount);
	return response;
    }

    @Path(value = PathProxy.RecordsUrls.CHANGE_LABEL_RECORD)
    @POST
    public Response<Boolean> changeLabel(ChangeRecordLabelRequest request) {
	if (request == null) {
	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
	}
	recordsService.changeReportLabel(request.getRecordId(), request.getLabel());
	Response<Boolean> response = new Response<Boolean>();
	response.setData(true);
	return response;
    }

    @Path(value = PathProxy.RecordsUrls.GET_ALL_TAGS)
    @GET
    public Response<Tags> getAllTags(@PathParam("doctorId") String doctorId, @PathParam("locationId") String locationId,
	    @PathParam("hospitalId") String hospitalId) {
	List<Tags> tags = recordsService.getAllTags(doctorId, locationId, hospitalId);
	Response<Tags> response = new Response<Tags>();
	response.setDataList(tags);
	return response;
    }

    @Path(value = PathProxy.RecordsUrls.CREATE_TAG)
    @POST
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
    public Response<String> getPatientEmailId(@PathParam("patientId") String patientId) {
	String emailAdd = recordsService.getPatientEmailAddress(patientId);
	Response<String> response = new Response<String>();
	response.setData(emailAdd);
	return response;
    }

    @Path(value = PathProxy.RecordsUrls.EMAIL_RECORD)
    @GET
    public Response<Boolean> emailRecords(@PathParam("recordId") String recordId, @PathParam("emailAddress") String emailAddress) {
	recordsService.emailRecordToPatient(recordId, emailAddress);
	Response<Boolean> response = new Response<Boolean>();
	response.setData(true);
	return response;
    }

    @Path(value = PathProxy.RecordsUrls.DELETE_RECORD)
    @DELETE
    public Response<Boolean> deleteRecords(@PathParam("recordId") String recordId) {
	recordsService.deleteRecord(recordId);
	Response<Boolean> response = new Response<Boolean>();
	response.setData(true);
	return response;
    }

    @Path(value = PathProxy.RecordsUrls.DELETE_TAG)
    @DELETE
    public Response<Boolean> deleteTag(@PathParam("tagid") String tagid) {
	recordsService.deleteTag(tagid);
	Response<Boolean> response = new Response<Boolean>();
	response.setData(true);
	return response;
    }

    @Path(value = PathProxy.RecordsUrls.DOWNLOAD_RECORD)
    @GET
    public javax.ws.rs.core.Response downloadRecords(@PathParam("recordId") String recordId) {
	File file = recordsService.getRecordFile(recordId);
	if (file == null) {
	    ResponseBuilder response = javax.ws.rs.core.Response.status(Status.BAD_REQUEST);
	    return response.build();
	}
	ResponseBuilder response = javax.ws.rs.core.Response.ok(file);
	response.header("Content-Disposition", "attachment; filename=" + file.getName());
	return response.build();
    }

    @Path(value = PathProxy.RecordsUrls.EDIT_DESCRIPTION)
    @POST
    public Response<Boolean> editDescription(RecordsDescription recordsDescription) {
	if (DPDoctorUtils.anyStringEmpty(recordsDescription.getId(), recordsDescription.getDescription())) {
	    throw new BusinessException(ServiceError.InvalidInput, "Record Id, and Description Cannot Be Empty");
	}
	boolean editDescriptionResponse = recordsService.editDescription(recordsDescription);
	Response<Boolean> response = new Response<Boolean>();
	response.setData(editDescriptionResponse);
	return response;
    }

    @Path(value = PathProxy.RecordsUrls.GET_FLEXIBLE_COUNTS)
    @POST
    public Response<FlexibleCounts> getCounts(FlexibleCounts flexibleCounts) {
	FlexibleCounts flexibleCountsResponse = recordsService.getFlexibleCounts(flexibleCounts);
	Response<FlexibleCounts> response = new Response<FlexibleCounts>();
	response.setData(flexibleCountsResponse);
	return response;
    }

    @Path(value = PathProxy.RecordsUrls.EDIT_RECORD)
    @PUT
    public Response<Records> editRecords(@PathParam(value = "recordId") String recordId, RecordsEditRequest request) {
	if (DPDoctorUtils.anyStringEmpty(recordId) || request == null) {
	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
	}
	request.setId(recordId);
	Records records = recordsService.editRecord(request);

	// patient track
	// if (records != null) {
	// records.setRecordsUrl(getFinalImageURL(records.getRecordsUrl()));
	// patientTrackService.addRecord(request, VisitedFor.REPORTS,
	// request.getVisitId());
	// }

	Response<Records> response = new Response<Records>();
	response.setData(records);
	return response;

    }

    private String getFinalImageURL(String imageURL) {
	if (imageURL != null) {
	    String finalImageURL = uriInfo.getBaseUri().toString().replace(uriInfo.getBaseUri().getPath(), imageUrlRootPath);
	    return finalImageURL + imageURL;
	} else
	    return null;

    }
}
