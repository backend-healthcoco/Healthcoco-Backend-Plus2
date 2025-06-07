package com.dpdocter.webservices;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.services.DownloadDataService;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Component
@Path(PathProxy.DOWNLOAD_DATA_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.DOWNLOAD_DATA_BASE_URL, description = "Endpoint for upload data")
public class DownloadDataApi {

	private static Logger logger = Logger.getLogger(DownloadDataApi.class.getName());

	@Autowired
	private DownloadDataService downloadDataService;

	@Path(value = PathProxy.UploadDataUrls.PATIENTS)
	@GET
	@ApiOperation(value = PathProxy.UploadDataUrls.PATIENTS, notes = PathProxy.UploadDataUrls.PATIENTS)
	public Response<Boolean> downloadPatientData(@PathParam("doctorId") String doctorId,
			@PathParam("locationId") String locationId, @PathParam("hospitalId") String hospitalId) {
		if (DPDoctorUtils.anyStringEmpty(doctorId, locationId, hospitalId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		downloadDataService.generatePatientData(new ObjectId(doctorId), new ObjectId(locationId),
				new ObjectId(hospitalId));
		Response<Boolean> response = new Response<Boolean>();
		response.setData(true);
		return response;
	}

	@Path(value = PathProxy.UploadDataUrls.PRESCRIPTIONS)
	@GET
	@ApiOperation(value = PathProxy.UploadDataUrls.PRESCRIPTIONS, notes = PathProxy.UploadDataUrls.PRESCRIPTIONS)
	public Response<Boolean> downloadPrescriptionData(@PathParam("doctorId") String doctorId,
			@PathParam("locationId") String locationId, @PathParam("hospitalId") String hospitalId) {
		if (DPDoctorUtils.anyStringEmpty(doctorId, locationId, hospitalId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		downloadDataService.downloadPrescriptionData(new ObjectId(doctorId), new ObjectId(locationId),
				new ObjectId(hospitalId));
		Response<Boolean> response = new Response<Boolean>();
		response.setData(true);
		return response;
	}

	@Path(value = PathProxy.UploadDataUrls.APPOINTMENTS)
	@GET
	@ApiOperation(value = PathProxy.UploadDataUrls.APPOINTMENTS, notes = PathProxy.UploadDataUrls.APPOINTMENTS)
	public Response<Boolean> downloadAppointmentData(@PathParam("doctorId") String doctorId,
			@PathParam("locationId") String locationId, @PathParam("hospitalId") String hospitalId) {
		if (DPDoctorUtils.anyStringEmpty(doctorId, locationId, hospitalId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		downloadDataService.downloadAppointmentData(new ObjectId(doctorId), new ObjectId(locationId),
				new ObjectId(hospitalId));
		Response<Boolean> response = new Response<Boolean>();
		response.setData(true);
		return response;
	}

	@Path(value = PathProxy.UploadDataUrls.TREATMENTS)
	@GET
	@ApiOperation(value = PathProxy.UploadDataUrls.TREATMENTS, notes = PathProxy.UploadDataUrls.TREATMENTS)
	public Response<Boolean> downloadTreatmentData(@PathParam("doctorId") String doctorId,
			@PathParam("locationId") String locationId, @PathParam("hospitalId") String hospitalId) {
		if (DPDoctorUtils.anyStringEmpty(doctorId, locationId, hospitalId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		downloadDataService.downloadTreatmentData(new ObjectId(doctorId), new ObjectId(locationId),
				new ObjectId(hospitalId));
		Response<Boolean> response = new Response<Boolean>();
		response.setData(true);
		return response;
	}

	@Path(value = PathProxy.UploadDataUrls.CLINICAL_NOTES)
	@GET
	@ApiOperation(value = PathProxy.UploadDataUrls.CLINICAL_NOTES, notes = PathProxy.UploadDataUrls.CLINICAL_NOTES)
	public Response<Boolean> downloadClinicalNotesData(@PathParam("doctorId") String doctorId,
			@PathParam("locationId") String locationId, @PathParam("hospitalId") String hospitalId) {
		if (DPDoctorUtils.anyStringEmpty(doctorId, locationId, hospitalId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		downloadDataService.downloadClinicalNotesData(new ObjectId(doctorId), new ObjectId(locationId),
				new ObjectId(hospitalId));
		Response<Boolean> response = new Response<Boolean>();
		response.setData(true);
		return response;
	}

	@Path(value = PathProxy.UploadDataUrls.INVOICES)
	@GET
	@ApiOperation(value = PathProxy.UploadDataUrls.INVOICES, notes = PathProxy.UploadDataUrls.INVOICES)
	public Response<Boolean> downloadInvoicesData(@PathParam("doctorId") String doctorId,
			@PathParam("locationId") String locationId, @PathParam("hospitalId") String hospitalId) {
		if (DPDoctorUtils.anyStringEmpty(doctorId, locationId, hospitalId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		downloadDataService.downloadInvoicesData(new ObjectId(doctorId), new ObjectId(locationId),
				new ObjectId(hospitalId));
		Response<Boolean> response = new Response<Boolean>();
		response.setData(true);
		return response;
	}

	@Path(value = PathProxy.UploadDataUrls.PAYMENTS)
	@GET
	@ApiOperation(value = PathProxy.UploadDataUrls.PAYMENTS, notes = PathProxy.UploadDataUrls.PAYMENTS)
	public Response<Boolean> downloadPaymentsData(@PathParam("doctorId") String doctorId,
			@PathParam("locationId") String locationId, @PathParam("hospitalId") String hospitalId) {
		if (DPDoctorUtils.anyStringEmpty(doctorId, locationId, hospitalId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		downloadDataService.downloadPaymentsData(new ObjectId(doctorId), new ObjectId(locationId),
				new ObjectId(hospitalId));
		Response<Boolean> response = new Response<Boolean>();
		response.setData(true);
		return response;
	}

	@Path(value = PathProxy.DownloadDataUrls.CLINICAL_ITEMS)
	@GET
	@ApiOperation(value = PathProxy.DownloadDataUrls.CLINICAL_ITEMS, notes = PathProxy.DownloadDataUrls.CLINICAL_ITEMS)
	public Response<Boolean> downloadClinicalItems(@PathParam("doctorId") String doctorId,
			@PathParam("locationId") String locationId, @PathParam("hospitalId") String hospitalId) {
		if (DPDoctorUtils.anyStringEmpty(doctorId, locationId, hospitalId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<Boolean> response = new Response<Boolean>();
		response.setData(downloadDataService.downloadClinicalItems(doctorId, locationId, hospitalId));
		return response;
	}

	@Path(value = PathProxy.UploadDataUrls.UPDATE_EMR)
	@GET
	@ApiOperation(value = PathProxy.UploadDataUrls.UPDATE_EMR, notes = PathProxy.UploadDataUrls.UPDATE_EMR)
	public Response<Boolean> update() {

		Response<Boolean> response = new Response<Boolean>();
		response.setData(downloadDataService.update("5927cdc6e4b098e7a0b9dd90", "5927cdc7e4b098e7a0b9dd93",
				"5927cdc6e4b098e7a0b9dd92"));
		return response;
	}

	@Path(value = PathProxy.DownloadDataUrls.GET_FILES)
	@GET
	@ApiOperation(value = PathProxy.DownloadDataUrls.GET_FILES, notes = PathProxy.DownloadDataUrls.GET_FILES)
	public Response<Boolean> downloadfiles(@PathParam("doctorId") String doctorId,
			@PathParam("locationId") String locationId, @PathParam("hospitalId") String hospitalId,
			@QueryParam("page") int page, @QueryParam("size") @DefaultValue("0") int size) {
		if (DPDoctorUtils.anyStringEmpty(doctorId, locationId, hospitalId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<Boolean> response = new Response<Boolean>();
		response.setData(downloadDataService.downloadfiles(doctorId, locationId, hospitalId, page, size));
		return response;
	}
	

	@Path(value = PathProxy.DownloadDataUrls.BACKUP_ALL_DATA)
	@GET
	@ApiOperation(value = PathProxy.DownloadDataUrls.BACKUP_ALL_DATA, notes = PathProxy.DownloadDataUrls.BACKUP_ALL_DATA)
	public Response<Boolean> backupAllDataAnEmail(@PathParam("doctorId") String doctorId,
			@PathParam("locationId") String locationId, @PathParam("hospitalId") String hospitalId) {
		if (DPDoctorUtils.anyStringEmpty(doctorId, locationId, hospitalId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<Boolean> response = new Response<Boolean>();
		response.setData(downloadDataService.backupAllDataAnEmail(doctorId, locationId, hospitalId));
		return response;
	}
	
}
