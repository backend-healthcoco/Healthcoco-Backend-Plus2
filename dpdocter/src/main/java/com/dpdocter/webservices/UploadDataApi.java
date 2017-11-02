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
	public Response<Boolean> uploadPatientData(@PathParam("doctorId") String doctorId, @PathParam("locationId") String locationId, 
			@PathParam("hospitalId") String hospitalId) {
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
	public Response<Boolean> uploadPrescriptionData(@PathParam("doctorId") String doctorId, @PathParam("locationId") String locationId, 
			@PathParam("hospitalId") String hospitalId) {
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
	public Response<Boolean> uploadAppointmentData(@PathParam("doctorId") String doctorId, @PathParam("locationId") String locationId, 
			@PathParam("hospitalId") String hospitalId) {
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
	public Response<Boolean> uploadTreatmentPlansData(@PathParam("doctorId") String doctorId, @PathParam("locationId") String locationId, 
			@PathParam("hospitalId") String hospitalId) {
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
	public Response<Boolean> uploadTreatmentData(@PathParam("doctorId") String doctorId, @PathParam("locationId") String locationId, 
			@PathParam("hospitalId") String hospitalId) {
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
	public Response<Boolean> assignPNUMToPatientsHavingPNUMAsNull(@PathParam("doctorId") String doctorId, @PathParam("locationId") String locationId, 
			@PathParam("hospitalId") String hospitalId) {
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
	public Response<Boolean> deletePatients(@PathParam("doctorId") String doctorId, @PathParam("locationId") String locationId, 
			@PathParam("hospitalId") String hospitalId) {
		if (DPDoctorUtils.anyStringEmpty(doctorId, locationId, hospitalId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
	
		Response<Boolean> response = new Response<Boolean>();
		response.setData(uploadDataService.deletePatients(doctorId, locationId, hospitalId));
		return response;
	}
}
