package com.dpdocter.webservices;

import java.io.IOException;
import java.util.List;

import javax.ws.rs.Consumes;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value = PathProxy.DOCTOR_LAB_URL,produces = MediaType.APPLICATION_JSON_VALUE ,consumes = MediaType.APPLICATION_JSON_VALUE)
@Api(value = PathProxy.DOCTOR_LAB_URL, description = "Endpoint for doctor lab")
public class DoctorLabApi {
	private static Logger logger = LogManager.getLogger(DoctorLabApi.class.getName());
	@Autowired
	private DoctorLabService doctorLabService;

	@Value(value = "${image.path}")
	private String imagePath;

	
	@PostMapping(value = PathProxy.DoctorLabUrls.ADD_DOCTOR_LAB_REPORT)
	@ApiOperation(value = PathProxy.DoctorLabUrls.ADD_DOCTOR_LAB_REPORT, notes = PathProxy.DoctorLabUrls.ADD_DOCTOR_LAB_REPORT)
	public Response<DoctorLabReport> addDoctorLabReport(@RequestBody DoctorLabReport request) {
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

	@PostMapping(value = PathProxy.DoctorLabUrls.UPLOAD_DOCTOR_LAB_MULTIPART_FILE)
	@Consumes({ MediaType.MULTIPART_FORM_DATA_VALUE })
	@ApiOperation(value = PathProxy.DoctorLabUrls.UPLOAD_DOCTOR_LAB_MULTIPART_FILE, notes = PathProxy.DoctorLabUrls.UPLOAD_DOCTOR_LAB_MULTIPART_FILE)
	public Response<RecordsFile> uploadDoctorLabReportMultipart(@RequestParam("file") MultipartFile file,
			@RequestBody MyFiileRequest request) {

		if (request == null || DPDoctorUtils.anyStringEmpty(file.getOriginalFilename())) {
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

	@PostMapping(value = PathProxy.DoctorLabUrls.UPLOAD_DOCTOR_LAB_FILE)
	@Consumes({ MediaType.MULTIPART_FORM_DATA_VALUE })
	@ApiOperation(value = PathProxy.DoctorLabUrls.UPLOAD_DOCTOR_LAB_FILE, notes = PathProxy.DoctorLabUrls.UPLOAD_DOCTOR_LAB_FILE)
	public Response<RecordsFile> uploadDoctorLabReport(@RequestParam("file") MultipartFile file, @RequestBody DoctorLabReportUploadRequest request) {
		if (request == null || file == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		RecordsFile recordsFiles = doctorLabService.uploadDoctorLabReport(file, request);

		Response<RecordsFile> response = new Response<RecordsFile>();
		response.setData(recordsFiles);
		return response;
	}

	
	@GetMapping(value = PathProxy.DoctorLabUrls.GET_DOCTOR_LAB_REPORT_BY_ID)
	@ApiOperation(value = PathProxy.DoctorLabUrls.GET_DOCTOR_LAB_REPORT_BY_ID, notes = PathProxy.DoctorLabUrls.GET_DOCTOR_LAB_REPORT_BY_ID)
	public Response<DoctorLabReportResponse> getRecordById(@PathVariable("reportId") String reportId) {
		if (DPDoctorUtils.anyStringEmpty(reportId)) {
			logger.warn("Record Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput, "report Id Cannot Be Empty");
		}

		DoctorLabReportResponse doctorLabReport = doctorLabService.getDoctorLabReportById(reportId);

		Response<DoctorLabReportResponse> response = new Response<DoctorLabReportResponse>();
		response.setData(doctorLabReport);
		return response;

	}

	
	@GetMapping(value = PathProxy.DoctorLabUrls.GET_DOCTOR_LAB_REPORTS)
	@ApiOperation(value = PathProxy.DoctorLabUrls.GET_DOCTOR_LAB_REPORTS, notes = PathProxy.DoctorLabUrls.GET_DOCTOR_LAB_REPORTS)
	public Response<DoctorLabReportResponse> getDoctorLabReports(@RequestParam("page") long page,
			@RequestParam("size") int size, @RequestParam("patientId") String patientId,
			@RequestParam("doctorId") String doctorId, @RequestParam("locationId") String locationId,
			@RequestParam("hospitalId") String hospitalId, @RequestParam("searchTerm") String searchTerm,
			@RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded,
			@RequestParam("isdoctorLab")   Boolean isdoctorLab) {
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

	@PostMapping
	(value = PathProxy.DoctorLabUrls.ADD_TO_FAVOURITE_DOCTOR_LIST)
	@ApiOperation(value = PathProxy.DoctorLabUrls.ADD_TO_FAVOURITE_DOCTOR_LIST, notes = PathProxy.DoctorLabUrls.ADD_TO_FAVOURITE_DOCTOR_LIST)
	public Response<Boolean> addDoctorToFavouriteList(@RequestBody DoctorLabFavouriteDoctorRequest request) {
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


	@GetMapping	(value = PathProxy.DoctorLabUrls.GET_FAVOURITE_DOCTOR)
	@ApiOperation(value = PathProxy.DoctorLabUrls.GET_FAVOURITE_DOCTOR, notes = PathProxy.DoctorLabUrls.GET_FAVOURITE_DOCTOR)
	public Response<DoctorLabFavouriteDoctorResponse> getFavouriteDoctors(@RequestParam("page") long page,
			@RequestParam("size") int size, @RequestParam("doctorId") String doctorId,
			@RequestParam("locationId") String locationId, @RequestParam("hospitalId") String hospitalId,
			@RequestParam("searchTerm") String searchTerm, @RequestParam("city") String city) {
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

	
	@GetMapping(value = PathProxy.DoctorLabUrls.SEARCH_DOCTOR)
	@ApiOperation(value = PathProxy.DoctorLabUrls.SEARCH_DOCTOR, notes = PathProxy.DoctorLabUrls.SEARCH_DOCTOR)
	public Response<DoctorLabSearchDoctorResponse> searchDoctors(@RequestParam("page") int page,
			@RequestParam("size") int size, @RequestParam("doctorId") String doctorId,
			@RequestParam("locationId") String locationId, @RequestParam("hospitalId") String hospitalId,
			@RequestParam("searchTerm") String searchTerm, @RequestParam("speciality") String speciality,
			@RequestParam("city") String city) {
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

	
	@GetMapping(value = PathProxy.DoctorLabUrls.UPDATE_IS_SHARE_WITH_DOCTOR)
	@ApiOperation(value = PathProxy.DoctorLabUrls.UPDATE_IS_SHARE_WITH_DOCTOR, notes = PathProxy.DoctorLabUrls.UPDATE_IS_SHARE_WITH_DOCTOR)
	public Response<Boolean> updateShareWithDoctor(@PathVariable("reportId") String reportId) {
		if (DPDoctorUtils.anyStringEmpty(reportId)) {
			logger.warn("Record Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput, "Record Id Cannot Be Empty");
		}
		Response<Boolean> response = new Response<Boolean>();
		response.setData(doctorLabService.updateShareWithDoctor(reportId));
		return response;

	}

	
	@GetMapping(value = PathProxy.DoctorLabUrls.UPDATE_IS_SHARE_WITH_PATIENT)
	@ApiOperation(value = PathProxy.DoctorLabUrls.UPDATE_IS_SHARE_WITH_PATIENT, notes = PathProxy.DoctorLabUrls.UPDATE_IS_SHARE_WITH_PATIENT)
	public Response<Boolean> updateShareWithPatent(@PathVariable("reportId") String reportId) {
		if (DPDoctorUtils.anyStringEmpty(reportId)) {
			logger.warn("Record Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput, "Record Id Cannot Be Empty");
		}
		Response<Boolean> response = new Response<Boolean>();
		response.setData(doctorLabService.updateShareWithPatient(reportId));
		return response;

	}

	
	@DeleteMapping(value = PathProxy.DoctorLabUrls.DELETE_FAVOURITE_DOCTOR)
	@ApiOperation(value = PathProxy.DoctorLabUrls.DELETE_FAVOURITE_DOCTOR, notes = PathProxy.DoctorLabUrls.DELETE_FAVOURITE_DOCTOR)
	public Response<Boolean> DiscardFavouriteDoctor(@PathVariable("id") String id) {
		if (DPDoctorUtils.anyStringEmpty(id)) {
			logger.warn("Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput, "Id Cannot Be Empty");
		}
		Response<Boolean> response = new Response<Boolean>();
		response.setData(doctorLabService.DiscardFavouriteDoctor(id));
		return response;

	}

	
	@DeleteMapping(value = PathProxy.DoctorLabUrls.DELETE_DOCTOR_LAB_REPORTS)
	@ApiOperation(value = PathProxy.DoctorLabUrls.DELETE_DOCTOR_LAB_REPORTS, notes = PathProxy.DoctorLabUrls.DELETE_DOCTOR_LAB_REPORTS)
	public Response<Boolean> DiscardDoctorLabReports(@PathVariable("reportId") String reportId) {
		if (DPDoctorUtils.anyStringEmpty(reportId)) {
			logger.warn("Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput, "report Id Cannot Be Empty");
		}
		Response<Boolean> response = new Response<Boolean>();
		response.setData(doctorLabService.DiscardDoctorLabReports(reportId));
		return response;

	}

	@PostMapping
	(value = PathProxy.DoctorLabUrls.ADD_DOCTOR_REFERENCE)
	@ApiOperation(value = PathProxy.DoctorLabUrls.ADD_DOCTOR_REFERENCE, notes = PathProxy.DoctorLabUrls.ADD_DOCTOR_REFERENCE)
	public Response<Boolean> addDoctorReference(@RequestBody DoctorLabDoctorReferenceRequest request) {
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

	
	@GetMapping(value = PathProxy.DoctorLabUrls.DOWNLOAD_REPORT)
	@ApiOperation(value = PathProxy.DoctorLabUrls.DOWNLOAD_REPORT, notes = PathProxy.DoctorLabUrls.DOWNLOAD_REPORT)
	public Response<String> downloadReport() throws IOException {
		Response<String> response = new Response<String>();
		response.setData(doctorLabService.downloadReport());
		return response;

	}

}
