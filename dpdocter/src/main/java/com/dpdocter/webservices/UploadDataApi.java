package com.dpdocter.webservices;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import org.springframework.http.MediaType;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.services.UploadDateService;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
(PathProxy.UPLOAD_DATA_BASE_URL)
@Produces(MediaType.APPLICATION_JSON_VALUE)
@Consumes(MediaType.APPLICATION_JSON_VALUE)
@Api(value = PathProxy.UPLOAD_DATA_BASE_URL, description = "Endpoint for upload data")
public class UploadDataApi {

	private static Logger logger = LogManager.getLogger(UploadDataApi.class.getName());

	@Autowired
	private UploadDateService uploadDataService;

	
	@GetMapping(value = PathProxy.UploadDataUrls.PATIENTS)
	@ApiOperation(value = PathProxy.UploadDataUrls.PATIENTS, notes = PathProxy.UploadDataUrls.PATIENTS)
	public Response<Boolean> uploadPatientData(@PathVariable("doctorId") String doctorId,
			@PathVariable("locationId") String locationId, @PathVariable("hospitalId") String hospitalId) {
		if (DPDoctorUtils.anyStringEmpty(doctorId, locationId, hospitalId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<Boolean> response = new Response<Boolean>();
		response.setData(uploadDataService.uploadPatientData(doctorId, locationId, hospitalId));
		return response;
	}

	
	@GetMapping(value = PathProxy.UploadDataUrls.PRESCRIPTIONS)
	@ApiOperation(value = PathProxy.UploadDataUrls.PRESCRIPTIONS, notes = PathProxy.UploadDataUrls.PRESCRIPTIONS)
	public Response<Boolean> uploadPrescriptionData(@PathVariable("doctorId") String doctorId,
			@PathVariable("locationId") String locationId, @PathVariable("hospitalId") String hospitalId) {
		if (DPDoctorUtils.anyStringEmpty(doctorId, locationId, hospitalId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		Response<Boolean> response = new Response<Boolean>();
		response.setData(uploadDataService.uploadPrescriptionData(doctorId, locationId, hospitalId));
		return response;
	}

	
	@GetMapping(value = PathProxy.UploadDataUrls.APPOINTMENTS)
	@ApiOperation(value = PathProxy.UploadDataUrls.APPOINTMENTS, notes = PathProxy.UploadDataUrls.APPOINTMENTS)
	public Response<Boolean> uploadAppointmentData(@PathVariable("doctorId") String doctorId,
			@PathVariable("locationId") String locationId, @PathVariable("hospitalId") String hospitalId) {
		if (DPDoctorUtils.anyStringEmpty(doctorId, locationId, hospitalId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		Response<Boolean> response = new Response<Boolean>();
		response.setData(uploadDataService.uploadAppointmentData(doctorId, locationId, hospitalId));
		return response;
	}

	
	@GetMapping(value = PathProxy.UploadDataUrls.TREATMENT_PLANS)
	@ApiOperation(value = PathProxy.UploadDataUrls.TREATMENT_PLANS, notes = PathProxy.UploadDataUrls.TREATMENT_PLANS)
	public Response<Boolean> uploadTreatmentPlansData(@PathVariable("doctorId") String doctorId,
			@PathVariable("locationId") String locationId, @PathVariable("hospitalId") String hospitalId) {
		if (DPDoctorUtils.anyStringEmpty(doctorId, locationId, hospitalId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		Response<Boolean> response = new Response<Boolean>();
		response.setData(uploadDataService.uploadTreatmentPlansData(doctorId, locationId, hospitalId));
		return response;
	}

	
	@GetMapping(value = PathProxy.UploadDataUrls.TREATMENTS)
	@ApiOperation(value = PathProxy.UploadDataUrls.TREATMENTS, notes = PathProxy.UploadDataUrls.TREATMENTS)
	public Response<Boolean> uploadTreatmentData(@PathVariable("doctorId") String doctorId,
			@PathVariable("locationId") String locationId, @PathVariable("hospitalId") String hospitalId) {
		if (DPDoctorUtils.anyStringEmpty(doctorId, locationId, hospitalId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		Response<Boolean> response = new Response<Boolean>();
		response.setData(uploadDataService.uploadTreatmentData(doctorId, locationId, hospitalId));
		return response;
	}

	
	@GetMapping(value = PathProxy.UploadDataUrls.ASSIGN_PNUM_TO_PATIENTS)
	@ApiOperation(value = PathProxy.UploadDataUrls.ASSIGN_PNUM_TO_PATIENTS, notes = PathProxy.UploadDataUrls.ASSIGN_PNUM_TO_PATIENTS)
	public Response<Boolean> assignPNUMToPatientsHavingPNUMAsNull(@PathVariable("doctorId") String doctorId,
			@PathVariable("locationId") String locationId, @PathVariable("hospitalId") String hospitalId) {
		if (DPDoctorUtils.anyStringEmpty(doctorId, locationId, hospitalId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		Response<Boolean> response = new Response<Boolean>();
		response.setData(uploadDataService.assignPNUMToPatientsHavingPNUMAsNull(doctorId, locationId, hospitalId));
		return response;
	}

	
	@GetMapping(value = PathProxy.UploadDataUrls.DELETE_PATIENTS)
	@ApiOperation(value = PathProxy.UploadDataUrls.DELETE_PATIENTS, notes = PathProxy.UploadDataUrls.DELETE_PATIENTS)
	public Response<Boolean> deletePatients(@PathVariable("doctorId") String doctorId,
			@PathVariable("locationId") String locationId, @PathVariable("hospitalId") String hospitalId) {
		if (DPDoctorUtils.anyStringEmpty(doctorId, locationId, hospitalId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		Response<Boolean> response = new Response<Boolean>();
		response.setData(uploadDataService.deletePatients(doctorId, locationId, hospitalId));
		return response;
	}

	
	@GetMapping(value = PathProxy.UploadDataUrls.UPDATE_EMR)
	@ApiOperation(value = PathProxy.UploadDataUrls.UPDATE_EMR, notes = PathProxy.UploadDataUrls.UPDATE_EMR)
	public Response<Boolean> updateEMR() {
		Response<Boolean> response = new Response<Boolean>();
		response.setData(uploadDataService.updateEMR());
		return response;
	}

	
	@GetMapping(value = PathProxy.UploadDataUrls.TREATMENT_SERVICES)
	@ApiOperation(value = PathProxy.UploadDataUrls.TREATMENT_SERVICES, notes = PathProxy.UploadDataUrls.TREATMENT_SERVICES)
	public Response<Boolean> uploadTreatmentServicesData(@PathVariable("doctorId") String doctorId,
			@PathVariable("locationId") String locationId, @PathVariable("hospitalId") String hospitalId) {
		if (DPDoctorUtils.anyStringEmpty(doctorId, locationId, hospitalId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		Response<Boolean> response = new Response<Boolean>();
		response.setData(uploadDataService.uploadTreatmentServicesData(doctorId, locationId, hospitalId));
		return response;
	}

	
	@GetMapping(value = PathProxy.UploadDataUrls.CLINICAL_NOTES)
	@ApiOperation(value = PathProxy.UploadDataUrls.CLINICAL_NOTES, notes = PathProxy.UploadDataUrls.CLINICAL_NOTES)
	public Response<Boolean> uploadClinicalNotesData(@PathVariable("doctorId") String doctorId,
			@PathVariable("locationId") String locationId, @PathVariable("hospitalId") String hospitalId) {
		if (DPDoctorUtils.anyStringEmpty(doctorId, locationId, hospitalId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		Response<Boolean> response = new Response<Boolean>();
		response.setData(uploadDataService.uploadClinicalNotesData(doctorId, locationId, hospitalId));
		return response;
	}
	
	
	@GetMapping(value = PathProxy.UploadDataUrls.INVOICES)
	@ApiOperation(value = PathProxy.UploadDataUrls.INVOICES, notes = PathProxy.UploadDataUrls.INVOICES)
	public Response<Boolean> uploadInvoicesData(@PathVariable("doctorId") String doctorId,
			@PathVariable("locationId") String locationId, @PathVariable("hospitalId") String hospitalId) {
		if (DPDoctorUtils.anyStringEmpty(doctorId, locationId, hospitalId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		Response<Boolean> response = new Response<Boolean>();
		response.setData(uploadDataService.uploadInvoicesData(doctorId, locationId, hospitalId));
		return response;
	}
	
	
	@GetMapping(value = PathProxy.UploadDataUrls.PAYMENTS)
	@ApiOperation(value = PathProxy.UploadDataUrls.PAYMENTS, notes = PathProxy.UploadDataUrls.PAYMENTS)
	public Response<Boolean> uploadPaymentsData(@PathVariable("doctorId") String doctorId,
			@PathVariable("locationId") String locationId, @PathVariable("hospitalId") String hospitalId) {
		if (DPDoctorUtils.anyStringEmpty(doctorId, locationId, hospitalId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		Response<Boolean> response = new Response<Boolean>();
		response.setData(uploadDataService.uploadPaymentsData(doctorId, locationId, hospitalId));
		return response;
	}
	
	
	@GetMapping(value = PathProxy.UploadDataUrls.UPDATE_TREATMENTS)
	@ApiOperation(value = PathProxy.UploadDataUrls.UPDATE_TREATMENTS, notes = PathProxy.UploadDataUrls.UPDATE_TREATMENTS)
	public Response<Boolean> updateTreatmentsData(@PathVariable("doctorId") String doctorId,
			@PathVariable("locationId") String locationId, @PathVariable("hospitalId") String hospitalId) {
		if (DPDoctorUtils.anyStringEmpty(doctorId, locationId, hospitalId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		Response<Boolean> response = new Response<Boolean>();
		response.setData(uploadDataService.updateTreatmentsData(doctorId, locationId, hospitalId));
		return response;
	}
	
	
	@GetMapping(value = PathProxy.UploadDataUrls.UPDATE_BILLING)
	@ApiOperation(value = PathProxy.UploadDataUrls.UPDATE_BILLING, notes = PathProxy.UploadDataUrls.UPDATE_BILLING)
	public Response<Boolean> updateBillingData(@PathVariable("locationId") String locationId, @PathVariable("hospitalId") String hospitalId) {
		if (DPDoctorUtils.anyStringEmpty(locationId, hospitalId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		Response<Boolean> response = new Response<Boolean>();
		response.setData(uploadDataService.updateBillingData(locationId, hospitalId));
		return response;
	}
	
	
	@GetMapping(value = PathProxy.UploadDataUrls.UPLOAD_IMAGES)
	@ApiOperation(value = PathProxy.UploadDataUrls.UPLOAD_IMAGES, notes = PathProxy.UploadDataUrls.UPLOAD_IMAGES)
	public Response<Boolean> upploadImages(@PathVariable("doctorId") String doctorId,
			@PathVariable("locationId") String locationId, @PathVariable("hospitalId") String hospitalId) {
		if (DPDoctorUtils.anyStringEmpty(doctorId, locationId, hospitalId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		Response<Boolean> response = new Response<Boolean>();
		response.setData(uploadDataService.uploadImages(doctorId, locationId, hospitalId));
		return response;
	}
	
	
	@GetMapping(value = PathProxy.UploadDataUrls.UPDATE_TREATMENT_SERVICES)
	@ApiOperation(value = PathProxy.UploadDataUrls.UPDATE_TREATMENT_SERVICES, notes = PathProxy.UploadDataUrls.UPDATE_TREATMENT_SERVICES)
	public Response<Boolean> updateTreatmentServices() {
		
		Response<Boolean> response = new Response<Boolean>();
		response.setData(uploadDataService.updateTreatmentServices());
		return response;
	}
	
	
	@GetMapping(value = PathProxy.UploadDataUrls.UPLOAD_REPORTS)
	@ApiOperation(value = PathProxy.UploadDataUrls.UPLOAD_REPORTS, notes = PathProxy.UploadDataUrls.UPLOAD_REPORTS)
	public Response<Boolean> upploadReports(@PathVariable("doctorId") String doctorId,
			@PathVariable("locationId") String locationId, @PathVariable("hospitalId") String hospitalId) {
		if (DPDoctorUtils.anyStringEmpty(doctorId, locationId, hospitalId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		Response<Boolean> response = new Response<Boolean>();
		response.setData(uploadDataService.upploadReports(doctorId, locationId, hospitalId));
		return response;
	}
}
