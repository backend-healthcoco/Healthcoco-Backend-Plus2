package com.dpdocter.webservices;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.services.UploadDateService;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Component
@Path(PathProxy.UPLOAD_DATA_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.UPLOAD_DATA_BASE_URL, description = "Endpoint for upload data")
public class UploadDataApi {

	private static Logger logger = Logger.getLogger(UploadDataApi.class.getName());

	@Autowired
	private UploadDateService uploadDataService;

	@Path(value = PathProxy.UploadDataUrls.PATIENTS)
	@GET
	@ApiOperation(value = PathProxy.UploadDataUrls.PATIENTS, notes = PathProxy.UploadDataUrls.PATIENTS)
	public Response<Boolean> uploadPatientData(@PathParam("doctorId") String doctorId,
			@PathParam("locationId") String locationId, @PathParam("hospitalId") String hospitalId) {
		if (DPDoctorUtils.anyStringEmpty(doctorId, locationId, hospitalId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<Boolean> response = new Response<Boolean>();
		response.setData(uploadDataService.uploadPatientData(doctorId, locationId, hospitalId));
		return response;
	}

	@Path(value = PathProxy.UploadDataUrls.PRESCRIPTIONS)
	@GET
	@ApiOperation(value = PathProxy.UploadDataUrls.PRESCRIPTIONS, notes = PathProxy.UploadDataUrls.PRESCRIPTIONS)
	public Response<Boolean> uploadPrescriptionData(@PathParam("doctorId") String doctorId,
			@PathParam("locationId") String locationId, @PathParam("hospitalId") String hospitalId) {
		if (DPDoctorUtils.anyStringEmpty(doctorId, locationId, hospitalId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		Response<Boolean> response = new Response<Boolean>();
		response.setData(uploadDataService.uploadPrescriptionData(doctorId, locationId, hospitalId));
		return response;
	}

	@Path(value = PathProxy.UploadDataUrls.APPOINTMENTS)
	@GET
	@ApiOperation(value = PathProxy.UploadDataUrls.APPOINTMENTS, notes = PathProxy.UploadDataUrls.APPOINTMENTS)
	public Response<Boolean> uploadAppointmentData(@PathParam("doctorId") String doctorId,
			@PathParam("locationId") String locationId, @PathParam("hospitalId") String hospitalId) {
		if (DPDoctorUtils.anyStringEmpty(doctorId, locationId, hospitalId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		Response<Boolean> response = new Response<Boolean>();
		response.setData(uploadDataService.uploadAppointmentData(doctorId, locationId, hospitalId));
		return response;
	}

	@Path(value = PathProxy.UploadDataUrls.TREATMENT_PLANS)
	@GET
	@ApiOperation(value = PathProxy.UploadDataUrls.TREATMENT_PLANS, notes = PathProxy.UploadDataUrls.TREATMENT_PLANS)
	public Response<Boolean> uploadTreatmentPlansData(@PathParam("doctorId") String doctorId,
			@PathParam("locationId") String locationId, @PathParam("hospitalId") String hospitalId) {
		if (DPDoctorUtils.anyStringEmpty(doctorId, locationId, hospitalId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		Response<Boolean> response = new Response<Boolean>();
		response.setData(uploadDataService.uploadTreatmentPlansData(doctorId, locationId, hospitalId));
		return response;
	}

	@Path(value = PathProxy.UploadDataUrls.TREATMENTS)
	@GET
	@ApiOperation(value = PathProxy.UploadDataUrls.TREATMENTS, notes = PathProxy.UploadDataUrls.TREATMENTS)
	public Response<Boolean> uploadTreatmentData(@PathParam("doctorId") String doctorId,
			@PathParam("locationId") String locationId, @PathParam("hospitalId") String hospitalId) {
		if (DPDoctorUtils.anyStringEmpty(doctorId, locationId, hospitalId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		Response<Boolean> response = new Response<Boolean>();
		response.setData(uploadDataService.uploadTreatmentData(doctorId, locationId, hospitalId));
		return response;
	}

	@Path(value = PathProxy.UploadDataUrls.ASSIGN_PNUM_TO_PATIENTS)
	@GET
	@ApiOperation(value = PathProxy.UploadDataUrls.ASSIGN_PNUM_TO_PATIENTS, notes = PathProxy.UploadDataUrls.ASSIGN_PNUM_TO_PATIENTS)
	public Response<Boolean> assignPNUMToPatientsHavingPNUMAsNull(@PathParam("doctorId") String doctorId,
			@PathParam("locationId") String locationId, @PathParam("hospitalId") String hospitalId) {
		if (DPDoctorUtils.anyStringEmpty(doctorId, locationId, hospitalId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		Response<Boolean> response = new Response<Boolean>();
		response.setData(uploadDataService.assignPNUMToPatientsHavingPNUMAsNull(doctorId, locationId, hospitalId));
		return response;
	}

	@Path(value = PathProxy.UploadDataUrls.DELETE_PATIENTS)
	@GET
	@ApiOperation(value = PathProxy.UploadDataUrls.DELETE_PATIENTS, notes = PathProxy.UploadDataUrls.DELETE_PATIENTS)
	public Response<Boolean> deletePatients(@PathParam("doctorId") String doctorId,
			@PathParam("locationId") String locationId, @PathParam("hospitalId") String hospitalId) {
		if (DPDoctorUtils.anyStringEmpty(doctorId, locationId, hospitalId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		Response<Boolean> response = new Response<Boolean>();
		response.setData(uploadDataService.deletePatients(doctorId, locationId, hospitalId));
		return response;
	}

	@Path(value = PathProxy.UploadDataUrls.UPDATE_EMR)
	@GET
	@ApiOperation(value = PathProxy.UploadDataUrls.UPDATE_EMR, notes = PathProxy.UploadDataUrls.UPDATE_EMR)
	public Response<Boolean> updateEMR() {
		Response<Boolean> response = new Response<Boolean>();
		response.setData(uploadDataService.updateEMR());
		return response;
	}

	@Path(value = PathProxy.UploadDataUrls.TREATMENT_SERVICES)
	@GET
	@ApiOperation(value = PathProxy.UploadDataUrls.TREATMENT_SERVICES, notes = PathProxy.UploadDataUrls.TREATMENT_SERVICES)
	public Response<Boolean> uploadTreatmentServicesData(@PathParam("doctorId") String doctorId,
			@PathParam("locationId") String locationId, @PathParam("hospitalId") String hospitalId) {
		if (DPDoctorUtils.anyStringEmpty(doctorId, locationId, hospitalId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		Response<Boolean> response = new Response<Boolean>();
		response.setData(uploadDataService.uploadTreatmentServicesData(doctorId, locationId, hospitalId));
		return response;
	}

	@Path(value = PathProxy.UploadDataUrls.CLINICAL_NOTES)
	@GET
	@ApiOperation(value = PathProxy.UploadDataUrls.CLINICAL_NOTES, notes = PathProxy.UploadDataUrls.CLINICAL_NOTES)
	public Response<Boolean> uploadClinicalNotesData(@PathParam("doctorId") String doctorId,
			@PathParam("locationId") String locationId, @PathParam("hospitalId") String hospitalId) {
		if (DPDoctorUtils.anyStringEmpty(doctorId, locationId, hospitalId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		Response<Boolean> response = new Response<Boolean>();
		response.setData(uploadDataService.uploadClinicalNotesData(doctorId, locationId, hospitalId));
		return response;
	}

	@Path(value = PathProxy.UploadDataUrls.INVOICES)
	@GET
	@ApiOperation(value = PathProxy.UploadDataUrls.INVOICES, notes = PathProxy.UploadDataUrls.INVOICES)
	public Response<Boolean> uploadInvoicesData(@PathParam("doctorId") String doctorId,
			@PathParam("locationId") String locationId, @PathParam("hospitalId") String hospitalId) {
		if (DPDoctorUtils.anyStringEmpty(doctorId, locationId, hospitalId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		Response<Boolean> response = new Response<Boolean>();
		response.setData(uploadDataService.uploadInvoicesData(doctorId, locationId, hospitalId));
		return response;
	}

	@Path(value = PathProxy.UploadDataUrls.PAYMENTS)
	@GET
	@ApiOperation(value = PathProxy.UploadDataUrls.PAYMENTS, notes = PathProxy.UploadDataUrls.PAYMENTS)
	public Response<Boolean> uploadPaymentsData(@PathParam("doctorId") String doctorId,
			@PathParam("locationId") String locationId, @PathParam("hospitalId") String hospitalId) {
		if (DPDoctorUtils.anyStringEmpty(doctorId, locationId, hospitalId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		Response<Boolean> response = new Response<Boolean>();
		response.setData(uploadDataService.uploadPaymentsData(doctorId, locationId, hospitalId));
		return response;
	}

	@Path(value = PathProxy.UploadDataUrls.UPDATE_TREATMENTS)
	@GET
	@ApiOperation(value = PathProxy.UploadDataUrls.UPDATE_TREATMENTS, notes = PathProxy.UploadDataUrls.UPDATE_TREATMENTS)
	public Response<Boolean> updateTreatmentsData(@PathParam("doctorId") String doctorId,
			@PathParam("locationId") String locationId, @PathParam("hospitalId") String hospitalId) {
		if (DPDoctorUtils.anyStringEmpty(doctorId, locationId, hospitalId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		Response<Boolean> response = new Response<Boolean>();
		response.setData(uploadDataService.updateTreatmentsData(doctorId, locationId, hospitalId));
		return response;
	}

	@Path(value = PathProxy.UploadDataUrls.UPDATE_BILLING)
	@GET
	@ApiOperation(value = PathProxy.UploadDataUrls.UPDATE_BILLING, notes = PathProxy.UploadDataUrls.UPDATE_BILLING)
	public Response<Boolean> updateBillingData(@PathParam("locationId") String locationId,
			@PathParam("hospitalId") String hospitalId) {
		if (DPDoctorUtils.anyStringEmpty(locationId, hospitalId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		Response<Boolean> response = new Response<Boolean>();
		response.setData(uploadDataService.updateBillingData(locationId, hospitalId));
		return response;
	}

	@Path(value = PathProxy.UploadDataUrls.UPLOAD_IMAGES)
	@GET
	@ApiOperation(value = PathProxy.UploadDataUrls.UPLOAD_IMAGES, notes = PathProxy.UploadDataUrls.UPLOAD_IMAGES)
	public Response<Boolean> upploadImages(@PathParam("doctorId") String doctorId,
			@PathParam("locationId") String locationId, @PathParam("hospitalId") String hospitalId) {
		if (DPDoctorUtils.anyStringEmpty(doctorId, locationId, hospitalId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		Response<Boolean> response = new Response<Boolean>();
		response.setData(uploadDataService.uploadImages(doctorId, locationId, hospitalId));
		return response;
	}

	@Path(value = PathProxy.UploadDataUrls.UPDATE_TREATMENT_SERVICES)
	@GET
	@ApiOperation(value = PathProxy.UploadDataUrls.UPDATE_TREATMENT_SERVICES, notes = PathProxy.UploadDataUrls.UPDATE_TREATMENT_SERVICES)
	public Response<Boolean> updateTreatmentServices() {

		Response<Boolean> response = new Response<Boolean>();
		response.setData(uploadDataService.updateTreatmentServices());
		return response;
	}

	@Path(value = PathProxy.UploadDataUrls.UPLOAD_REPORTS)
	@GET
	@ApiOperation(value = PathProxy.UploadDataUrls.UPLOAD_REPORTS, notes = PathProxy.UploadDataUrls.UPLOAD_REPORTS)
	public Response<Boolean> upploadReports(@PathParam("doctorId") String doctorId,
			@PathParam("locationId") String locationId, @PathParam("hospitalId") String hospitalId) {
		if (DPDoctorUtils.anyStringEmpty(doctorId, locationId, hospitalId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		Response<Boolean> response = new Response<Boolean>();
		response.setData(uploadDataService.upploadReports(doctorId, locationId, hospitalId));
		return response;
	}
}
