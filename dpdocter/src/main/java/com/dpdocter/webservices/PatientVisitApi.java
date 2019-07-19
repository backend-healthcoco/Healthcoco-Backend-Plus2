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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.dpdocter.beans.PatientVisit;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.request.AddMultipleDataRequest;
import com.dpdocter.response.PatientVisitResponse;
import com.dpdocter.services.OTPService;
import com.dpdocter.services.PatientVisitService;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Component
@Path(PathProxy.PATIENT_VISIT_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.PATIENT_VISIT_BASE_URL, description = "Endpoint for patient visit")
public class PatientVisitApi {

	private static Logger logger = Logger.getLogger(PatientVisitApi.class.getName());

	@Autowired
	private PatientVisitService patientVisitService;

	@Autowired
	private OTPService otpService;

	@Value(value = "${image.path}")
	private String imagePath;

	@Path(value = PathProxy.PatientVisitUrls.ADD_MULTIPLE_DATA)
	@POST
	@ApiOperation(value = PathProxy.PatientVisitUrls.ADD_MULTIPLE_DATA, notes = PathProxy.PatientVisitUrls.ADD_MULTIPLE_DATA)
	public Response<PatientVisitResponse> addMultipleData(AddMultipleDataRequest request) {

		if (request == null || DPDoctorUtils.allStringsEmpty(request.getDoctorId(), request.getLocationId(),
				request.getHospitalId(), request.getPatientId())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		PatientVisitResponse patienVisitResponse = patientVisitService.addMultipleData(request);
		Response<PatientVisitResponse> response = new Response<PatientVisitResponse>();
		response.setData(patienVisitResponse);
		return response;
	}

	@Path(value = PathProxy.PatientVisitUrls.EMAIL)
	@GET
	@ApiOperation(value = PathProxy.PatientVisitUrls.EMAIL, notes = PathProxy.PatientVisitUrls.EMAIL)
	public Response<Boolean> email(@PathParam(value = "visitId") String visitId,
			@PathParam(value = "emailAddress") String emailAddress) {

		if (DPDoctorUtils.anyStringEmpty(visitId, emailAddress)) {
			logger.warn("Visit Id Or Email AddressIs NULL");
			throw new BusinessException(ServiceError.InvalidInput, "Visit Id Or Email Address Is NULL");
		}
		Boolean isSend = patientVisitService.email(visitId, emailAddress);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(isSend);
		return response;
	}

	@Path(value = PathProxy.PatientVisitUrls.GET_VISIT)
	@GET
	@ApiOperation(value = PathProxy.PatientVisitUrls.GET_VISIT, notes = PathProxy.PatientVisitUrls.GET_VISIT)
	public Response<PatientVisitResponse> getVisit(@PathParam("visitId") String visitId) {
		if (DPDoctorUtils.anyStringEmpty(visitId)) {
			throw new BusinessException(ServiceError.InvalidInput, "Patient Visit Id Cannot Be Empty!");
		}

		PatientVisitResponse patientVisitResponse = patientVisitService.getVisit(visitId);

		Response<PatientVisitResponse> response = new Response<PatientVisitResponse>();
		response.setData(patientVisitResponse);
		return response;
	}

	@Path(value = PathProxy.PatientVisitUrls.GET_VISITS)
	@GET
	@ApiOperation(value = PathProxy.PatientVisitUrls.GET_VISITS, notes = PathProxy.PatientVisitUrls.GET_VISITS)
	public Response<PatientVisitResponse> getVisit(@PathParam(value = "doctorId") String doctorId,
			@PathParam(value = "locationId") String locationId, @PathParam(value = "hospitalId") String hospitalId,
			@PathParam(value = "patientId") String patientId, @QueryParam(value = "page") long page,
			@QueryParam(value = "size") int size, @DefaultValue("0") @QueryParam("updatedTime") String updatedTime,
			@QueryParam("visitFor") String visitFor) {

		if (DPDoctorUtils.anyStringEmpty(patientId, hospitalId, locationId)) {
			logger.warn("Patient Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Patient Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
		}
		if (doctorId.equalsIgnoreCase("null")) {
			doctorId = null;
		}
		List<PatientVisitResponse> patienVisitResponse = patientVisitService.getVisit(doctorId, locationId, hospitalId,
				patientId, page, size, otpService.checkOTPVerified(doctorId, locationId, hospitalId, patientId),
				updatedTime, visitFor);

		Response<PatientVisitResponse> response = new Response<PatientVisitResponse>();
		response.setDataList(patienVisitResponse);
		return response;
	}

	@Path(value = PathProxy.PatientVisitUrls.GET_VISITS_FOR_WEB)
	@GET
	@ApiOperation(value = PathProxy.PatientVisitUrls.GET_VISITS_FOR_WEB, notes = PathProxy.PatientVisitUrls.GET_VISITS_FOR_WEB)
	public Response<PatientVisitResponse> getVisitForWEB(@QueryParam(value = "doctorId") String doctorId,
			@QueryParam(value = "locationId") String locationId, @QueryParam(value = "hospitalId") String hospitalId,
			@QueryParam(value = "patientId") String patientId, @QueryParam(value = "page") long page,
			@QueryParam(value = "size") int size, @DefaultValue("0") @QueryParam("updatedTime") String updatedTime,
			@QueryParam("visitFor") String visitFor) {

		if (DPDoctorUtils.anyStringEmpty(patientId, hospitalId, locationId)) {
			logger.warn("Patient Id, Hospital Id, Location Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Patient Id, Hospital Id, Location Id Cannot Be Empty");
		}
		List<PatientVisitResponse> patienVisitResponse = patientVisitService.getVisit(doctorId, locationId, hospitalId,
				patientId, page, size, otpService.checkOTPVerified(doctorId, locationId, hospitalId, patientId),
				updatedTime, visitFor);

		Response<PatientVisitResponse> response = new Response<PatientVisitResponse>();
		response.setDataList(patienVisitResponse);
		return response;
	}

	@Path(value = PathProxy.PatientVisitUrls.GET_VISITS_HANDHELD)
	@GET
	@ApiOperation(value = PathProxy.PatientVisitUrls.GET_VISITS_HANDHELD, notes = PathProxy.PatientVisitUrls.GET_VISITS_HANDHELD)
	public Response<PatientVisit> getVisitsHandheld(@PathParam(value = "doctorId") String doctorId,
			@PathParam(value = "locationId") String locationId, @PathParam(value = "hospitalId") String hospitalId,
			@PathParam(value = "patientId") String patientId, @QueryParam(value = "page") long page,
			@QueryParam(value = "size") int size, @DefaultValue("0") @QueryParam("updatedTime") String updatedTime) {

		if (DPDoctorUtils.anyStringEmpty(patientId, doctorId, hospitalId, locationId)) {
			logger.warn("Patient Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Patient Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
		}
		List<PatientVisit> patienVisitResponse = patientVisitService.getVisitsHandheld(doctorId, locationId, hospitalId,
				patientId, page, size, otpService.checkOTPVerified(doctorId, locationId, hospitalId, patientId),
				updatedTime);

		Response<PatientVisit> response = new Response<PatientVisit>();
		response.setDataList(patienVisitResponse);
		return response;
	}

	@Path(value = PathProxy.PatientVisitUrls.DELETE_VISITS)
	@DELETE
	@ApiOperation(value = PathProxy.PatientVisitUrls.DELETE_VISITS, notes = PathProxy.PatientVisitUrls.DELETE_VISITS)
	public Response<PatientVisitResponse> deleteVisit(@PathParam(value = "visitId") String visitId,
			@DefaultValue("true") @QueryParam("discarded") Boolean discarded) {

		if (StringUtils.isEmpty(visitId)) {
			logger.warn("Visit Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput, "Visit Id Cannot Be Empty");
		}
		PatientVisitResponse patienVisitResponse = patientVisitService.deleteVisit(visitId, discarded);

		Response<PatientVisitResponse> response = new Response<PatientVisitResponse>();
		response.setData(patienVisitResponse);
		return response;
	}

	@Path(value = PathProxy.PatientVisitUrls.SMS_VISITS)
	@GET
	@ApiOperation(value = PathProxy.PatientVisitUrls.SMS_VISITS, notes = PathProxy.PatientVisitUrls.SMS_VISITS)
	public Response<Boolean> smsPrescription(@PathParam(value = "visitId") String visitId,
			@PathParam(value = "doctorId") String doctorId, @PathParam(value = "locationId") String locationId,
			@PathParam(value = "hospitalId") String hospitalId,
			@PathParam(value = "mobileNumber") String mobileNumber) {

		if (DPDoctorUtils.anyStringEmpty(visitId, doctorId, locationId, hospitalId, mobileNumber)) {
			logger.warn("Invalid Input. Visit Id, Doctor Id, Location Id, Hospital Id, Mobile Number Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Invalid Input. Visit Id, Doctor Id, Location Id, Hospital Id, Mobile Number Cannot Be Empty");
		}

		Response<Boolean> response = new Response<Boolean>();
		response.setData(patientVisitService.smsVisit(visitId, doctorId, locationId, hospitalId, mobileNumber));
		return response;
	}

	@Path(value = PathProxy.PatientVisitUrls.SMS_VISITS_WEB)
	@GET
	@ApiOperation(value = PathProxy.PatientVisitUrls.SMS_VISITS_WEB, notes = PathProxy.PatientVisitUrls.SMS_VISITS_WEB)
	public Response<Boolean> smsPrescriptionForWeb(@PathParam(value = "visitId") String visitId,
			@QueryParam(value = "doctorId") String doctorId, @QueryParam(value = "locationId") String locationId,
			@QueryParam(value = "hospitalId") String hospitalId,
			@PathParam(value = "mobileNumber") String mobileNumber) {

		if (DPDoctorUtils.anyStringEmpty(visitId, mobileNumber)) {
			logger.warn("Invalid Input. Visit Id, Doctor Id, Location Id, Hospital Id, Mobile Number Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Invalid Input. Visit Id, Doctor Id, Location Id, Hospital Id, Mobile Number Cannot Be Empty");
		}

		Response<Boolean> response = new Response<Boolean>();
		response.setData(patientVisitService.smsVisit(visitId, doctorId, locationId, hospitalId, mobileNumber));
		return response;
	}

	@Path(value = PathProxy.PatientVisitUrls.DOWNLOAD_PATIENT_VISIT)
	@GET
	@ApiOperation(value = PathProxy.PatientVisitUrls.DOWNLOAD_PATIENT_VISIT, notes = PathProxy.PatientVisitUrls.DOWNLOAD_PATIENT_VISIT)
	public Response<String> downloadPatientVisit(@PathParam("visitId") String visitId,
			@DefaultValue("false") @QueryParam("showPH") Boolean showPH,
			@DefaultValue("false") @QueryParam("showPLH") Boolean showPLH,
			@DefaultValue("false") @QueryParam("showFH") Boolean showFH,
			@DefaultValue("false") @QueryParam("showDA") Boolean showDA,
			@DefaultValue("false") @QueryParam("isLabPrint") Boolean isLabPrint,
			@DefaultValue("false") @QueryParam("showUSG") Boolean showUSG,
			@DefaultValue("false") @QueryParam("isCustomPDF") Boolean isCustomPDF,
			@DefaultValue("false") @QueryParam("showLMP") Boolean showLMP,
			@DefaultValue("false") @QueryParam("showEDD") Boolean showEDD,
			@DefaultValue("false") @QueryParam("showNoOfChildren") Boolean showNoOfChildren,
			@DefaultValue("true") @QueryParam("showPrescription") Boolean showPrescription,
			@DefaultValue("true") @QueryParam("showTreatment") Boolean showTreatment,
			@DefaultValue("true") @QueryParam("showclinicalNotes") Boolean showclinicalNotes,
			@DefaultValue("false") @QueryParam("showVitalSign") Boolean showVitalSign) {
		if (DPDoctorUtils.allStringsEmpty(visitId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<String> response = new Response<String>();
		response.setData(patientVisitService.getPatientVisitFile(visitId, showPH, showPLH, showFH, showDA, showUSG,
				isLabPrint, isCustomPDF, showLMP, showEDD, showNoOfChildren, showPrescription, showTreatment,
				showclinicalNotes, showVitalSign));
		return response;
	}

	@Path(value = PathProxy.PatientVisitUrls.GET_PATIENT_LAST_VISIT)
	@GET
	@ApiOperation(value = PathProxy.PatientVisitUrls.GET_PATIENT_LAST_VISIT, notes = PathProxy.PatientVisitUrls.GET_PATIENT_LAST_VISIT)
	public Response<PatientVisitResponse> getPatientLastVisit(@PathParam(value = "doctorId") String doctorId,
			@PathParam(value = "locationId") String locationId, @PathParam(value = "hospitalId") String hospitalId,
			@PathParam(value = "patientId") String patientId) {
		if (DPDoctorUtils.anyStringEmpty(doctorId, locationId, hospitalId, patientId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		PatientVisitResponse patientVisit = patientVisitService.getPatientLastVisit(doctorId, locationId, hospitalId,
				patientId);
		Response<PatientVisitResponse> response = new Response<PatientVisitResponse>();
		response.setData(patientVisit);
		return response;
	}

}
