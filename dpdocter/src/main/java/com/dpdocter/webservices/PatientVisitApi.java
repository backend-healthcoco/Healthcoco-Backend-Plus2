package com.dpdocter.webservices;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.Produces;
import org.springframework.http.MediaType;


import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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

@RestController
(PathProxy.PATIENT_VISIT_BASE_URL)
@Produces(MediaType.APPLICATION_JSON_VALUE)
@Consumes(MediaType.APPLICATION_JSON_VALUE)
@Api(value = PathProxy.PATIENT_VISIT_BASE_URL, description = "Endpoint for patient visit")
public class PatientVisitApi {

	private static Logger logger = LogManager.getLogger(PatientVisitApi.class.getName());

	@Autowired
	private PatientVisitService patientVisitService;

	@Autowired
	private OTPService otpService;

	@Value(value = "${image.path}")
	private String imagePath;

	
	@PostMapping(value = PathProxy.PatientVisitUrls.ADD_MULTIPLE_DATA)
	@Consumes({ MediaType.MULTIPART_FORM_DATA_VALUE})
	@ApiOperation(value = PathProxy.PatientVisitUrls.ADD_MULTIPLE_DATA, notes = PathProxy.PatientVisitUrls.ADD_MULTIPLE_DATA)
	public Response<PatientVisitResponse> addMultipleData(AddMultipleDataRequest request, @RequestParam("file") MultipartFile file) {

		if (request == null || DPDoctorUtils.allStringsEmpty(request.getDoctorId(), request.getLocationId(),
				request.getHospitalId(), request.getPatientId())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		PatientVisitResponse patienVisitResponse = patientVisitService.addMultipleData(request, file);
		Response<PatientVisitResponse> response = new Response<PatientVisitResponse>();
		response.setData(patienVisitResponse);
		return response;
	}

	
	@GetMapping(value = PathProxy.PatientVisitUrls.EMAIL)
	@ApiOperation(value = PathProxy.PatientVisitUrls.EMAIL, notes = PathProxy.PatientVisitUrls.EMAIL)
	public Response<Boolean> email(@PathVariable(value = "visitId") String visitId,
			@PathVariable(value = "emailAddress") String emailAddress) {

		if (DPDoctorUtils.anyStringEmpty(visitId, emailAddress)) {
			logger.warn("Visit Id Or Email AddressIs NULL");
			throw new BusinessException(ServiceError.InvalidInput, "Visit Id Or Email Address Is NULL");
		}
		Boolean isSend = patientVisitService.email(visitId, emailAddress);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(isSend);
		return response;
	}

	
	@GetMapping(value = PathProxy.PatientVisitUrls.GET_VISIT)
	@ApiOperation(value = PathProxy.PatientVisitUrls.GET_VISIT, notes = PathProxy.PatientVisitUrls.GET_VISIT)
	public Response<PatientVisitResponse> getVisit(@PathVariable("visitId") String visitId) {
		if (DPDoctorUtils.anyStringEmpty(visitId)) {
			throw new BusinessException(ServiceError.InvalidInput, "Patient Visit Id Cannot Be Empty!");
		}

		PatientVisitResponse patientVisitResponse = patientVisitService.getVisit(visitId);

		Response<PatientVisitResponse> response = new Response<PatientVisitResponse>();
		response.setData(patientVisitResponse);
		return response;
	}

	
	@GetMapping(value = PathProxy.PatientVisitUrls.GET_VISITS)
	@ApiOperation(value = PathProxy.PatientVisitUrls.GET_VISITS, notes = PathProxy.PatientVisitUrls.GET_VISITS)
	public Response<PatientVisitResponse> getVisit(@PathVariable(value = "doctorId") String doctorId,
			@PathVariable(value = "locationId") String locationId, @PathVariable(value = "hospitalId") String hospitalId,
			@PathVariable(value = "patientId") String patientId, @RequestParam(value = "page") long page,
			@RequestParam(value = "size") int size, @DefaultValue("0") @RequestParam("updatedTime") String updatedTime,
			@RequestParam("visitFor") String visitFor) {

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

	
	@GetMapping(value = PathProxy.PatientVisitUrls.GET_VISITS_FOR_WEB)
	@ApiOperation(value = PathProxy.PatientVisitUrls.GET_VISITS_FOR_WEB, notes = PathProxy.PatientVisitUrls.GET_VISITS_FOR_WEB)
	public Response<PatientVisitResponse> getVisitForWEB(@RequestParam(value = "doctorId") String doctorId,
			@RequestParam(value = "locationId") String locationId, @RequestParam(value = "hospitalId") String hospitalId,
			@RequestParam(value = "patientId") String patientId, @RequestParam(value = "page") long page,
			@RequestParam(value = "size") int size, @DefaultValue("0") @RequestParam("updatedTime") String updatedTime,
			@RequestParam("visitFor") String visitFor) {

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

	
	@GetMapping(value = PathProxy.PatientVisitUrls.GET_VISITS_HANDHELD)
	@ApiOperation(value = PathProxy.PatientVisitUrls.GET_VISITS_HANDHELD, notes = PathProxy.PatientVisitUrls.GET_VISITS_HANDHELD)
	public Response<PatientVisit> getVisitsHandheld(@PathVariable(value = "doctorId") String doctorId,
			@PathVariable(value = "locationId") String locationId, @PathVariable(value = "hospitalId") String hospitalId,
			@PathVariable(value = "patientId") String patientId, @RequestParam(value = "page") long page,
			@RequestParam(value = "size") int size, @DefaultValue("0") @RequestParam("updatedTime") String updatedTime) {

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

	
	@DeleteMapping(value = PathProxy.PatientVisitUrls.DELETE_VISITS)
	@ApiOperation(value = PathProxy.PatientVisitUrls.DELETE_VISITS, notes = PathProxy.PatientVisitUrls.DELETE_VISITS)
	public Response<PatientVisitResponse> deleteVisit(@PathVariable(value = "visitId") String visitId,
			  @RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded) {

		if (StringUtils.isEmpty(visitId)) {
			logger.warn("Visit Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput, "Visit Id Cannot Be Empty");
		}
		PatientVisitResponse patienVisitResponse = patientVisitService.deleteVisit(visitId, discarded);

		Response<PatientVisitResponse> response = new Response<PatientVisitResponse>();
		response.setData(patienVisitResponse);
		return response;
	}

	
	@GetMapping(value = PathProxy.PatientVisitUrls.SMS_VISITS)
	@ApiOperation(value = PathProxy.PatientVisitUrls.SMS_VISITS, notes = PathProxy.PatientVisitUrls.SMS_VISITS)
	public Response<Boolean> smsPrescription(@PathVariable(value = "visitId") String visitId,
			@PathVariable(value = "doctorId") String doctorId, @PathVariable(value = "locationId") String locationId,
			@PathVariable(value = "hospitalId") String hospitalId,
			@PathVariable(value = "mobileNumber") String mobileNumber) {

		if (DPDoctorUtils.anyStringEmpty(visitId, doctorId, locationId, hospitalId, mobileNumber)) {
			logger.warn("Invalid Input. Visit Id, Doctor Id, Location Id, Hospital Id, Mobile Number Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Invalid Input. Visit Id, Doctor Id, Location Id, Hospital Id, Mobile Number Cannot Be Empty");
		}

		Response<Boolean> response = new Response<Boolean>();
		response.setData(patientVisitService.smsVisit(visitId, doctorId, locationId, hospitalId, mobileNumber));
		return response;
	}

	
	@GetMapping(value = PathProxy.PatientVisitUrls.SMS_VISITS_WEB)
	@ApiOperation(value = PathProxy.PatientVisitUrls.SMS_VISITS_WEB, notes = PathProxy.PatientVisitUrls.SMS_VISITS_WEB)
	public Response<Boolean> smsPrescriptionForWeb(@PathVariable(value = "visitId") String visitId,
			@RequestParam(value = "doctorId") String doctorId, @RequestParam(value = "locationId") String locationId,
			@RequestParam(value = "hospitalId") String hospitalId,
			@PathVariable(value = "mobileNumber") String mobileNumber) {

		if (DPDoctorUtils.anyStringEmpty(visitId, mobileNumber)) {
			logger.warn("Invalid Input. Visit Id, Doctor Id, Location Id, Hospital Id, Mobile Number Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Invalid Input. Visit Id, Doctor Id, Location Id, Hospital Id, Mobile Number Cannot Be Empty");
		}

		Response<Boolean> response = new Response<Boolean>();
		response.setData(patientVisitService.smsVisit(visitId, doctorId, locationId, hospitalId, mobileNumber));
		return response;
	}

	
	@GetMapping(value = PathProxy.PatientVisitUrls.DOWNLOAD_PATIENT_VISIT)
	@ApiOperation(value = PathProxy.PatientVisitUrls.DOWNLOAD_PATIENT_VISIT, notes = PathProxy.PatientVisitUrls.DOWNLOAD_PATIENT_VISIT)
	public Response<String> downloadPatientVisit(@PathVariable("visitId") String visitId,
			@DefaultValue("false") @RequestParam("showPH") Boolean showPH,
			@DefaultValue("false") @RequestParam("showPLH") Boolean showPLH,
			@DefaultValue("false") @RequestParam("showFH") Boolean showFH,
			@DefaultValue("false") @RequestParam("showDA") Boolean showDA,
			@DefaultValue("false") @RequestParam("isLabPrint") Boolean isLabPrint,
			@DefaultValue("false") @RequestParam("showUSG") Boolean showUSG,
			@DefaultValue("false") @RequestParam("isCustomPDF") Boolean isCustomPDF,
			@DefaultValue("false") @RequestParam("showLMP") Boolean showLMP,
			@DefaultValue("false") @RequestParam("showEDD") Boolean showEDD,
			@DefaultValue("false") @RequestParam("showNoOfChildren") Boolean showNoOfChildren,
			  @RequestParam("showPrescription") Boolean showPrescription,
			  @RequestParam("showTreatment") Boolean showTreatment,
			  @RequestParam("showclinicalNotes") Boolean showclinicalNotes,
			@DefaultValue("false") @RequestParam("showVitalSign") Boolean showVitalSign) {
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

	
	@GetMapping(value = PathProxy.PatientVisitUrls.GET_PATIENT_LAST_VISIT)
	@ApiOperation(value = PathProxy.PatientVisitUrls.GET_PATIENT_LAST_VISIT, notes = PathProxy.PatientVisitUrls.GET_PATIENT_LAST_VISIT)
	public Response<PatientVisitResponse> getPatientLastVisit(@PathVariable(value = "doctorId") String doctorId,
			@PathVariable(value = "locationId") String locationId, @PathVariable(value = "hospitalId") String hospitalId,
			@PathVariable(value = "patientId") String patientId) {
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
