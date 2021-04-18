package com.dpdocter.webservices;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import org.springframework.http.MediaType;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.services.DownloadDataService;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value = PathProxy.DOWNLOAD_DATA_BASE_URL,produces = MediaType.APPLICATION_JSON_VALUE ,consumes = MediaType.APPLICATION_JSON_VALUE)
@Api(value = PathProxy.DOWNLOAD_DATA_BASE_URL, description = "Endpoint for upload data")
public class DownloadDataApi {

	private static Logger logger = LogManager.getLogger(DownloadDataApi.class.getName());
	
	@Autowired
	private DownloadDataService downloadDataService;

	
	@GetMapping(value = PathProxy.UploadDataUrls.PATIENTS)
	@ApiOperation(value = PathProxy.UploadDataUrls.PATIENTS, notes = PathProxy.UploadDataUrls.PATIENTS)
	public Response<Boolean> downloadPatientData(@PathVariable("doctorId") String doctorId,
			@PathVariable("locationId") String locationId, @PathVariable("hospitalId") String hospitalId) {
		if (DPDoctorUtils.anyStringEmpty(doctorId, locationId, hospitalId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		downloadDataService.generatePatientData(new ObjectId(doctorId), new ObjectId(locationId), new ObjectId(hospitalId));
		Response<Boolean> response = new Response<Boolean>();
		response.setData(true);
		return response;
	}

	
	@GetMapping(value = PathProxy.UploadDataUrls.PRESCRIPTIONS)
	@ApiOperation(value = PathProxy.UploadDataUrls.PRESCRIPTIONS, notes = PathProxy.UploadDataUrls.PRESCRIPTIONS)
	public Response<Boolean> downloadPrescriptionData(@PathVariable("doctorId") String doctorId,
			@PathVariable("locationId") String locationId, @PathVariable("hospitalId") String hospitalId) {
		if (DPDoctorUtils.anyStringEmpty(doctorId, locationId, hospitalId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		downloadDataService.downloadPrescriptionData(new ObjectId(doctorId), new ObjectId(locationId), new ObjectId(hospitalId));
		Response<Boolean> response = new Response<Boolean>();
		response.setData(true);
		return response;
	}

	
	@GetMapping(value = PathProxy.UploadDataUrls.APPOINTMENTS)
	@ApiOperation(value = PathProxy.UploadDataUrls.APPOINTMENTS, notes = PathProxy.UploadDataUrls.APPOINTMENTS)
	public Response<Boolean> downloadAppointmentData(@PathVariable("doctorId") String doctorId,
			@PathVariable("locationId") String locationId, @PathVariable("hospitalId") String hospitalId) {
		if (DPDoctorUtils.anyStringEmpty(doctorId, locationId, hospitalId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		downloadDataService.downloadAppointmentData(new ObjectId(doctorId), new ObjectId(locationId), new ObjectId(hospitalId));
		Response<Boolean> response = new Response<Boolean>();
		response.setData(true);
		return response;
	}

	
	@GetMapping(value = PathProxy.UploadDataUrls.TREATMENTS)
	@ApiOperation(value = PathProxy.UploadDataUrls.TREATMENTS, notes = PathProxy.UploadDataUrls.TREATMENTS)
	public Response<Boolean> downloadTreatmentData(@PathVariable("doctorId") String doctorId,
			@PathVariable("locationId") String locationId, @PathVariable("hospitalId") String hospitalId) {
		if (DPDoctorUtils.anyStringEmpty(doctorId, locationId, hospitalId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		downloadDataService.downloadTreatmentData(new ObjectId(doctorId), new ObjectId(locationId), new ObjectId(hospitalId));
		Response<Boolean> response = new Response<Boolean>();
		response.setData(true);
		return response;
	}

	
	@GetMapping(value = PathProxy.UploadDataUrls.CLINICAL_NOTES)
	@ApiOperation(value = PathProxy.UploadDataUrls.CLINICAL_NOTES, notes = PathProxy.UploadDataUrls.CLINICAL_NOTES)
	public Response<Boolean> downloadClinicalNotesData(@PathVariable("doctorId") String doctorId,
			@PathVariable("locationId") String locationId, @PathVariable("hospitalId") String hospitalId) {
		if (DPDoctorUtils.anyStringEmpty(doctorId, locationId, hospitalId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		downloadDataService.downloadClinicalNotesData(new ObjectId(doctorId), new ObjectId(locationId), new ObjectId(hospitalId));
		Response<Boolean> response = new Response<Boolean>();
		response.setData(true);
		return response;
	}

	
	@GetMapping(value = PathProxy.UploadDataUrls.INVOICES)
	@ApiOperation(value = PathProxy.UploadDataUrls.INVOICES, notes = PathProxy.UploadDataUrls.INVOICES)
	public Response<Boolean> downloadInvoicesData(@PathVariable("doctorId") String doctorId,
			@PathVariable("locationId") String locationId, @PathVariable("hospitalId") String hospitalId) {
		if (DPDoctorUtils.anyStringEmpty(doctorId, locationId, hospitalId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		downloadDataService.downloadInvoicesData(new ObjectId(doctorId), new ObjectId(locationId), new ObjectId(hospitalId));
		Response<Boolean> response = new Response<Boolean>();
		response.setData(true);
		return response;
	}

	
	@GetMapping(value = PathProxy.UploadDataUrls.PAYMENTS)
	@ApiOperation(value = PathProxy.UploadDataUrls.PAYMENTS, notes = PathProxy.UploadDataUrls.PAYMENTS)
	public Response<Boolean> downloadPaymentsData(@PathVariable("doctorId") String doctorId,
			@PathVariable("locationId") String locationId, @PathVariable("hospitalId") String hospitalId) {
		if (DPDoctorUtils.anyStringEmpty(doctorId, locationId, hospitalId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		downloadDataService.downloadPaymentsData(new ObjectId(doctorId), new ObjectId(locationId), new ObjectId(hospitalId));
		Response<Boolean> response = new Response<Boolean>();
		response.setData(true);
		return response;
	}
	
	
	@GetMapping(value = PathProxy.DownloadDataUrls.CLINICAL_ITEMS)
	@ApiOperation(value = PathProxy.DownloadDataUrls.CLINICAL_ITEMS, notes = PathProxy.DownloadDataUrls.CLINICAL_ITEMS)
	public Response<Boolean> downloadClinicalItems(@PathVariable("doctorId") String doctorId,
			@PathVariable("locationId") String locationId, @PathVariable("hospitalId") String hospitalId) {
		if (DPDoctorUtils.anyStringEmpty(doctorId, locationId, hospitalId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<Boolean> response = new Response<Boolean>();
		response.setData(downloadDataService.downloadClinicalItems(doctorId, locationId, hospitalId));
		return response;
	}

	
	@GetMapping(value = PathProxy.UploadDataUrls.UPDATE_EMR)
	@ApiOperation(value = PathProxy.UploadDataUrls.UPDATE_EMR, notes = PathProxy.UploadDataUrls.UPDATE_EMR)
	public Response<Boolean> update() {
		
		Response<Boolean> response = new Response<Boolean>();
		response.setData(downloadDataService.update("5927cdc6e4b098e7a0b9dd90", "5927cdc7e4b098e7a0b9dd93", "5927cdc6e4b098e7a0b9dd92"));
		return response;
	}
}
