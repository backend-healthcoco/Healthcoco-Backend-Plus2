package com.dpdocter.webservices;

import java.io.IOException;
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

import com.dpdocter.beans.DoctorLabReport;
import com.dpdocter.beans.RecordsFile;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.request.DoctorLabDoctorReferenceRequest;
import com.dpdocter.request.DoctorLabFavouriteDoctorRequest;
import com.dpdocter.request.DoctorLabReportUploadRequest;
import com.dpdocter.request.MyFiileRequest;
import com.dpdocter.response.DoctorLabFavouriteDoctorResponse;
import com.dpdocter.response.DoctorLabReportResponse;
import com.dpdocter.response.DoctorLabSearchDoctorResponse;
import com.dpdocter.services.DoctorLabService;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataParam;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Component
@Path(PathProxy.DOCTOR_LAB_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.DOCTOR_LAB_URL, description = "Endpoint for doctor lab")
public class DoctorLabApi {
	private static Logger logger = Logger.getLogger(DoctorLabApi.class.getName());
	@Autowired
	private DoctorLabService doctorLabService;

	@Value(value = "${image.path}")
	private String imagePath;

	@Path(value = PathProxy.DoctorLabUrls.ADD_DOCTOR_LAB_REPORT)
	@POST
	@ApiOperation(value = PathProxy.DoctorLabUrls.ADD_DOCTOR_LAB_REPORT, notes = PathProxy.DoctorLabUrls.ADD_DOCTOR_LAB_REPORT)
	public Response<DoctorLabReport> addDoctorLabReport(DoctorLabReport request) {
		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		if (DPDoctorUtils.anyStringEmpty(request.getPatientName(), request.getUploadedByDoctorId(),
				request.getUploadedByHospitalId(), request.getUploadedByLocationId())
				|| request.getRecordsFiles().isEmpty() || request.getRecordsFiles() == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		DoctorLabReport doctorLabReport = doctorLabService.addDoctorLabReport(request);

		Response<DoctorLabReport> response = new Response<DoctorLabReport>();
		response.setData(doctorLabReport);
		return response;
	}

	private String getFinalImageURL(String imageURL) {
		if (imageURL != null) {
			return imagePath + imageURL;
		} else
			return null;
	}

	@POST
	@Path(value = PathProxy.DoctorLabUrls.UPLOAD_DOCTOR_LAB_MULTIPART_FILE)
	@Consumes({ MediaType.MULTIPART_FORM_DATA })
	@ApiOperation(value = PathProxy.DoctorLabUrls.UPLOAD_DOCTOR_LAB_MULTIPART_FILE, notes = PathProxy.DoctorLabUrls.UPLOAD_DOCTOR_LAB_MULTIPART_FILE)
	public Response<RecordsFile> uploadDoctorLabReportMultipart(@FormDataParam("file") FormDataBodyPart file,
			@FormDataParam("data") FormDataBodyPart data) {

		data.setMediaType(MediaType.APPLICATION_JSON_TYPE);
		MyFiileRequest request = data.getValueAs(MyFiileRequest.class);

		if (request == null || DPDoctorUtils.anyStringEmpty(file.getContentDisposition().getFileName())) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		RecordsFile recordsFile = doctorLabService.uploadDoctorLabReportMultipart(file, request);
		if (recordsFile != null) {
			recordsFile.setRecordsUrl(getFinalImageURL(recordsFile.getRecordsUrl()));
			recordsFile.setThumbnailUrl(getFinalImageURL(recordsFile.getThumbnailUrl()));
		}

		Response<RecordsFile> response = new Response<RecordsFile>();
		response.setData(recordsFile);
		return response;
	}

	@POST
	@Path(value = PathProxy.DoctorLabUrls.UPLOAD_DOCTOR_LAB_FILE)
	@ApiOperation(value = PathProxy.DoctorLabUrls.UPLOAD_DOCTOR_LAB_FILE, notes = PathProxy.DoctorLabUrls.UPLOAD_DOCTOR_LAB_FILE)
	public Response<RecordsFile> uploadDoctorLabReport(DoctorLabReportUploadRequest request) {
		if (request == null || request.getFileDetails() == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		RecordsFile recordsFiles = doctorLabService.uploadDoctorLabReport(request);

		Response<RecordsFile> response = new Response<RecordsFile>();
		response.setData(recordsFiles);
		return response;
	}

	@Path(value = PathProxy.DoctorLabUrls.GET_DOCTOR_LAB_REPORT_BY_ID)
	@GET
	@ApiOperation(value = PathProxy.DoctorLabUrls.GET_DOCTOR_LAB_REPORT_BY_ID, notes = PathProxy.DoctorLabUrls.GET_DOCTOR_LAB_REPORT_BY_ID)
	public Response<DoctorLabReportResponse> getRecordById(@PathParam("reportId") String reportId) {
		if (DPDoctorUtils.anyStringEmpty(reportId)) {
			logger.warn("Record Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput, "report Id Cannot Be Empty");
		}

		DoctorLabReportResponse doctorLabReport = doctorLabService.getDoctorLabReportById(reportId);

		Response<DoctorLabReportResponse> response = new Response<DoctorLabReportResponse>();
		response.setData(doctorLabReport);
		return response;

	}

	@Path(value = PathProxy.DoctorLabUrls.GET_DOCTOR_LAB_REPORTS)
	@GET
	@ApiOperation(value = PathProxy.DoctorLabUrls.GET_DOCTOR_LAB_REPORTS, notes = PathProxy.DoctorLabUrls.GET_DOCTOR_LAB_REPORTS)
	public Response<DoctorLabReportResponse> getDoctorLabReports(@QueryParam("page") long page,
			@QueryParam("size") int size, @QueryParam("patientId") String patientId,
			@QueryParam("doctorId") String doctorId, @QueryParam("locationId") String locationId,
			@QueryParam("hospitalId") String hospitalId, @QueryParam("searchTerm") String searchTerm,
			@QueryParam("discarded") Boolean discarded,
			@QueryParam("isdoctorLab") @DefaultValue("true") Boolean isdoctorLab) {
		if (DPDoctorUtils.anyStringEmpty(hospitalId, locationId)) {
			logger.warn("Record Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput, "hospitalId,locationId Cannot Be Empty");
		}

		List<DoctorLabReportResponse> doctorLabReport = doctorLabService.getDoctorLabReport(page, size, patientId,
				doctorId, locationId, hospitalId, searchTerm, discarded, isdoctorLab);

		Response<DoctorLabReportResponse> response = new Response<DoctorLabReportResponse>();
		response.setDataList(doctorLabReport);
		return response;

	}

	@POST
	@Path(value = PathProxy.DoctorLabUrls.ADD_TO_FAVOURITE_DOCTOR_LIST)
	@ApiOperation(value = PathProxy.DoctorLabUrls.ADD_TO_FAVOURITE_DOCTOR_LIST, notes = PathProxy.DoctorLabUrls.ADD_TO_FAVOURITE_DOCTOR_LIST)
	public Response<Boolean> addDoctorToFavouriteList(DoctorLabFavouriteDoctorRequest request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId(),
				request.getLocationId(), request.getFavouriteDoctorId(), request.getFavouriteHospitalId(),
				request.getFavouriteLocationId())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<Boolean> response = new Response<Boolean>();
		response.setData(doctorLabService.addDoctorToFavouriteList(request));
		return response;
	}

	@Path(value = PathProxy.DoctorLabUrls.GET_FAVOURITE_DOCTOR)
	@GET
	@ApiOperation(value = PathProxy.DoctorLabUrls.GET_FAVOURITE_DOCTOR, notes = PathProxy.DoctorLabUrls.GET_FAVOURITE_DOCTOR)
	public Response<DoctorLabFavouriteDoctorResponse> getFavouriteDoctors(@QueryParam("page") long page,
			@QueryParam("size") int size, @QueryParam("doctorId") String doctorId,
			@QueryParam("locationId") String locationId, @QueryParam("hospitalId") String hospitalId,
			@QueryParam("searchTerm") String searchTerm, @QueryParam("city") String city) {
		if (DPDoctorUtils.anyStringEmpty(hospitalId, locationId)) {
			logger.warn("Record Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput, "hospitalId,locationId Cannot Be Empty");
		}

		List<DoctorLabFavouriteDoctorResponse> favouriteDoctorResponses = doctorLabService.getFavouriteList(size, page,
				searchTerm, doctorId, locationId, hospitalId, city);

		Response<DoctorLabFavouriteDoctorResponse> response = new Response<DoctorLabFavouriteDoctorResponse>();
		response.setDataList(favouriteDoctorResponses);
		return response;

	}

	@Path(value = PathProxy.DoctorLabUrls.SEARCH_DOCTOR)
	@GET
	@ApiOperation(value = PathProxy.DoctorLabUrls.SEARCH_DOCTOR, notes = PathProxy.DoctorLabUrls.SEARCH_DOCTOR)
	public Response<DoctorLabSearchDoctorResponse> searchDoctors(@QueryParam("page") int page,
			@QueryParam("size") int size, @QueryParam("doctorId") String doctorId,
			@QueryParam("locationId") String locationId, @QueryParam("hospitalId") String hospitalId,
			@QueryParam("searchTerm") String searchTerm, @QueryParam("speciality") String speciality,
			@QueryParam("city") String city) {
		if (DPDoctorUtils.anyStringEmpty(doctorId, hospitalId, locationId)) {
			logger.warn("Record Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput, "hospitalId,locationId Cannot Be Empty");
		}
		List<DoctorLabSearchDoctorResponse> doctorLabSearchDoctorResponses = doctorLabService.searchDoctor(size, page,
				searchTerm, doctorId, locationId, hospitalId, speciality, city);
		Response<DoctorLabSearchDoctorResponse> response = new Response<DoctorLabSearchDoctorResponse>();
		response.setDataList(doctorLabSearchDoctorResponses);
		return response;

	}

	@Path(value = PathProxy.DoctorLabUrls.UPDATE_IS_SHARE_WITH_DOCTOR)
	@GET
	@ApiOperation(value = PathProxy.DoctorLabUrls.UPDATE_IS_SHARE_WITH_DOCTOR, notes = PathProxy.DoctorLabUrls.UPDATE_IS_SHARE_WITH_DOCTOR)
	public Response<Boolean> updateShareWithDoctor(@PathParam("reportId") String reportId) {
		if (DPDoctorUtils.anyStringEmpty(reportId)) {
			logger.warn("Record Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput, "Record Id Cannot Be Empty");
		}
		Response<Boolean> response = new Response<Boolean>();
		response.setData(doctorLabService.updateShareWithDoctor(reportId));
		return response;

	}

	@Path(value = PathProxy.DoctorLabUrls.UPDATE_IS_SHARE_WITH_PATIENT)
	@GET
	@ApiOperation(value = PathProxy.DoctorLabUrls.UPDATE_IS_SHARE_WITH_PATIENT, notes = PathProxy.DoctorLabUrls.UPDATE_IS_SHARE_WITH_PATIENT)
	public Response<Boolean> updateShareWithPatent(@PathParam("reportId") String reportId) {
		if (DPDoctorUtils.anyStringEmpty(reportId)) {
			logger.warn("Record Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput, "Record Id Cannot Be Empty");
		}
		Response<Boolean> response = new Response<Boolean>();
		response.setData(doctorLabService.updateShareWithPatient(reportId));
		return response;

	}

	@Path(value = PathProxy.DoctorLabUrls.DELETE_FAVOURITE_DOCTOR)
	@DELETE
	@ApiOperation(value = PathProxy.DoctorLabUrls.DELETE_FAVOURITE_DOCTOR, notes = PathProxy.DoctorLabUrls.DELETE_FAVOURITE_DOCTOR)
	public Response<Boolean> DiscardFavouriteDoctor(@PathParam("id") String id) {
		if (DPDoctorUtils.anyStringEmpty(id)) {
			logger.warn("Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput, "Id Cannot Be Empty");
		}
		Response<Boolean> response = new Response<Boolean>();
		response.setData(doctorLabService.DiscardFavouriteDoctor(id));
		return response;

	}

	@Path(value = PathProxy.DoctorLabUrls.DELETE_DOCTOR_LAB_REPORTS)
	@DELETE
	@ApiOperation(value = PathProxy.DoctorLabUrls.DELETE_DOCTOR_LAB_REPORTS, notes = PathProxy.DoctorLabUrls.DELETE_DOCTOR_LAB_REPORTS)
	public Response<Boolean> DiscardDoctorLabReports(@PathParam("reportId") String reportId) {
		if (DPDoctorUtils.anyStringEmpty(reportId)) {
			logger.warn("Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput, "report Id Cannot Be Empty");
		}
		Response<Boolean> response = new Response<Boolean>();
		response.setData(doctorLabService.DiscardDoctorLabReports(reportId));
		return response;

	}

	@POST
	@Path(value = PathProxy.DoctorLabUrls.ADD_DOCTOR_REFERENCE)
	@ApiOperation(value = PathProxy.DoctorLabUrls.ADD_DOCTOR_REFERENCE, notes = PathProxy.DoctorLabUrls.ADD_DOCTOR_REFERENCE)
	public Response<Boolean> addDoctorReference(DoctorLabDoctorReferenceRequest request) {
		if (request == null
				|| DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId(), request.getHospitalId(),
						request.getLocationName(), request.getFirstName(), request.getMobileNumber())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<Boolean> response = new Response<Boolean>();
		response.setData(doctorLabService.addDoctorReference(request));
		return response;
	}

	@Path(value = PathProxy.DoctorLabUrls.DOWNLOAD_REPORT)
	@GET
	@ApiOperation(value = PathProxy.DoctorLabUrls.DOWNLOAD_REPORT, notes = PathProxy.DoctorLabUrls.DOWNLOAD_REPORT)
	public Response<String> downloadReport() throws IOException {
		Response<String> response = new Response<String>();
		response.setData(doctorLabService.downloadReport());
		return response;

	}

}
