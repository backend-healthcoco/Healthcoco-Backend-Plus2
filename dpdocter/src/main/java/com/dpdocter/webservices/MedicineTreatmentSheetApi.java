package com.dpdocter.webservices;

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

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.request.MedicineTreatmentSheetRequest;
import com.dpdocter.response.MedicineTreatmentSheetResponse;
import com.dpdocter.services.MedicineTreatmentService;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * This API used for daily medicine record where nurses put drug name and time
 * of given with her name & if medicine is high risk then enter two nurses name.
 * 
 * @author Nikita Refer Jira Id 6168
 */

@Component
@Path(PathProxy.MEDICINE_SHEET_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.MEDICINE_SHEET_BASE_URL, description = "End")
public class MedicineTreatmentSheetApi {

	private static Logger logger = Logger.getLogger(MedicineTreatmentSheetApi.class.getName());

	@Autowired
	private MedicineTreatmentService medicineTreatmentService;

	@Path(value = PathProxy.MedicineTreatmentUrls.ADD_EDIT_MEDICINE_SHEET)
	@POST
	@ApiOperation(value = PathProxy.MedicineTreatmentUrls.ADD_EDIT_MEDICINE_SHEET, notes = PathProxy.MedicineTreatmentUrls.ADD_EDIT_MEDICINE_SHEET)
	public Response<MedicineTreatmentSheetResponse> addEditMedicineTreatmentSheet(
			MedicineTreatmentSheetRequest request) {

		if (request == null || DPDoctorUtils.allStringsEmpty(request.getDoctorId(), request.getLocationId(),
				request.getHospitalId(), request.getPatientId())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput,
					"Invalid Input Patient Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
		}
		MedicineTreatmentSheetResponse medicineTreatmentSheetResponse = medicineTreatmentService
				.addEditMedicinetreatmentSheet(request);
		Response<MedicineTreatmentSheetResponse> response = new Response<MedicineTreatmentSheetResponse>();
		response.setData(medicineTreatmentSheetResponse);
		return response;
	}

	@Path(value = PathProxy.MedicineTreatmentUrls.GET_MEDICINE_SHEET)
	@GET
	@ApiOperation(value = PathProxy.MedicineTreatmentUrls.GET_MEDICINE_SHEET, notes = PathProxy.MedicineTreatmentUrls.GET_MEDICINE_SHEET)
	public Response<MedicineTreatmentSheetResponse> getMedicineSheet(@PathParam(value = "patientId") String patientId,
			@QueryParam(value = "locationId") String locationId, @QueryParam(value = "hospitalId") String hospitalId,
			@QueryParam(value = "doctorId") String doctorId, @DefaultValue("0") @QueryParam(value = "page") int page,
			@DefaultValue("0") @QueryParam(value = "size") int size,
			@DefaultValue("false") @QueryParam("discarded") Boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(patientId, hospitalId, locationId, doctorId)) {
			logger.warn("Patient Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Patient Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
		}
		List<MedicineTreatmentSheetResponse> medicineTreatmentSheetResponse = medicineTreatmentService
				.getMedicineSheet(doctorId, locationId, hospitalId, patientId, page, size, discarded);
		Response<MedicineTreatmentSheetResponse> response = new Response<MedicineTreatmentSheetResponse>();
		response.setDataList(medicineTreatmentSheetResponse);
		return response;

	}

	@Path(value = PathProxy.MedicineTreatmentUrls.GET_MEDICINE_SHEET_BY_ID)
	@GET
	@ApiOperation(value = PathProxy.MedicineTreatmentUrls.GET_MEDICINE_SHEET_BY_ID, notes = PathProxy.MedicineTreatmentUrls.GET_MEDICINE_SHEET_BY_ID)
	public Response<MedicineTreatmentSheetResponse> getById(@PathParam("medicineSheetId") String medicineSheetId) {
		if (medicineSheetId == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		Response<MedicineTreatmentSheetResponse> response = new Response<MedicineTreatmentSheetResponse>();
		response.setData(medicineTreatmentService.getMedicineSheetById(medicineSheetId));
		return response;

	}

	@Path(value = PathProxy.MedicineTreatmentUrls.DELETE_MEDICINE_SHEET)
	@DELETE
	@ApiOperation(value = PathProxy.MedicineTreatmentUrls.DELETE_MEDICINE_SHEET, notes = PathProxy.MedicineTreatmentUrls.DELETE_MEDICINE_SHEET)
	public Response<Boolean> deleteMedicineSheet(@PathParam(value = "medicineSheetId") String medicineSheetId,
			@PathParam(value = "doctorId") String doctorId, @PathParam(value = "locationId") String locationId,
			@PathParam(value = "hospitalId") String hospitalId,
			@DefaultValue("true") @QueryParam("discarded") Boolean discarded) {
		if (StringUtils.isEmpty(medicineSheetId) || StringUtils.isEmpty(doctorId) || StringUtils.isEmpty(hospitalId)
				|| StringUtils.isEmpty(locationId)) {
			logger.warn("initialAssessmentId, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"initialAssessmentId, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
		}
		Boolean formResponse = medicineTreatmentService.deleteMedicineSheet(medicineSheetId, doctorId, hospitalId,
				locationId, discarded);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(formResponse);
		return response;
	}

}
