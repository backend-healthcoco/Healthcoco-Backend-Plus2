package com.dpdocter.webservices;

import java.io.File;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dpdocter.beans.Records;
import com.dpdocter.beans.Tags;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.request.ChangeRecordLabelRequest;
import com.dpdocter.request.RecordsAddRequest;
import com.dpdocter.request.RecordsSearchRequest;
import com.dpdocter.request.TagRecordRequest;
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

	@POST
	@Path(value = PathProxy.RecordsUrls.ADD_RECORDS)
	// public Response<Records> addRecords(RecordsAddRequest
	// request,@FormDataParam("file") InputStream
	// fileInputStream,@FormDataParam("file") FormDataContentDisposition
	// contentDispositionHeader){
	public Response<Records> addRecords(RecordsAddRequest request) {
		if (request == null) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		Records records = recordsService.addRecord(request);
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
		Response<Records> response = new Response<Records>();
		response.setDataList(records);
		return response;
	}

	@Path(value = PathProxy.RecordsUrls.SEARCH_RECORD_DOCTOR_ID)
	@GET
	public Response<Records> getRecords(@PathParam("doctorId") String doctorId) {
		if (DPDoctorUtils.anyStringEmpty(doctorId)) {
			throw new BusinessException(ServiceError.InvalidInput, "Doctor Id Cannot Be Empty");
		}
		return searchRecords(doctorId, null, null, null);
	}

	@Path(value = PathProxy.RecordsUrls.SEARCH_RECORD_DOCTOR_ID_CT)
	@GET
	public Response<Records> getRecords(@PathParam("doctorId") String doctorId, @PathParam("createdTime") String createdTime) {
		if (DPDoctorUtils.anyStringEmpty(doctorId)) {
			throw new BusinessException(ServiceError.InvalidInput, "Doctor Id, Created Time Cannot Be Empty");
		}
		return searchRecords(doctorId, null, null, createdTime);
	}

	@Path(value = PathProxy.RecordsUrls.SEARCH_RECORD_ALL_FIELDS)
	@GET
	public Response<Records> getRecords(@PathParam("doctorId") String doctorId, @PathParam("locationId") String locationId,
			@PathParam("hospitalId") String hospitalId) {
		if (DPDoctorUtils.anyStringEmpty(doctorId)) {
			throw new BusinessException(ServiceError.InvalidInput, "Doctor Id, Location Id, Hospital Id Cannot Be Empty");
		}
		return searchRecords(doctorId, locationId, hospitalId, null);
	}

	@Path(value = PathProxy.RecordsUrls.SEARCH_RECORD_ALL_FIELDS_CT)
	@GET
	public Response<Records> getRecords(@PathParam("doctorId") String doctorId, @PathParam("locationId") String locationId,
			@PathParam("hospitalId") String hospitalId, @PathParam("createdTime") String createdTime) {
		if (DPDoctorUtils.anyStringEmpty(doctorId)) {
			throw new BusinessException(ServiceError.InvalidInput, "Doctor Id, Location Id, Hospital Id, and Created Time Cannot Be Empty");
		}
		return searchRecords(doctorId, locationId, hospitalId, createdTime);
	}

	private Response<Records> searchRecords(String doctorId, String locationId, String hospitalId, String createdTime) {
		List<Records> records = recordsService.searchRecords(doctorId, locationId, hospitalId, createdTime);
		Response<Records> response = new Response<Records>();
		response.setDataList(records);
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
	@GET
	public Response<Boolean> deleteRecords(@PathParam("recordId") String recordId) {
		recordsService.deleteRecord(recordId);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(true);
		return response;
	}

	@Path(value = PathProxy.RecordsUrls.DELETE_TAG)
	@GET
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
		ResponseBuilder response = javax.ws.rs.core.Response.ok((Object) file);
		response.header("Content-Disposition", "attachment; filename=" + file.getName());
		return response.build();
	}

}
