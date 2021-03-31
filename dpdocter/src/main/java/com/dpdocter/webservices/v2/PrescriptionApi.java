package com.dpdocter.webservices.v2;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dpdocter.beans.v2.Drug;
import com.dpdocter.beans.v2.Prescription;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.services.OTPService;
import com.dpdocter.services.v2.PrescriptionServices;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController(value = "PrescriptionApiV2")
@RequestMapping(value=PathProxy.PRESCRIPTION_BASE_URL,produces = MediaType.APPLICATION_JSON ,consumes = MediaType.APPLICATION_JSON)
@Api(value = PathProxy.PRESCRIPTION_BASE_URL, description = "Endpoint for prescription")
public class PrescriptionApi {

	private static Logger logger = LogManager.getLogger(PrescriptionApi.class.getName());

	@Autowired
	private PrescriptionServices prescriptionServices;

	@Autowired
	private OTPService otpService;

	@GetMapping
	@ApiOperation(value = "GET_PRESCRIPTIONS", notes = "GET_PRESCRIPTIONS")
	public Response<Prescription> getPrescription(@RequestParam("page") int page, @RequestParam("size") int size,
			@RequestParam("doctorId") String doctorId, @RequestParam("locationId") String locationId,
			@RequestParam("hospitalId") String hospitalId, @RequestParam("patientId") String patientId,
			@DefaultValue("0") @RequestParam("updatedTime") String updatedTime,
			  @RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(locationId)) {
			throw new BusinessException(ServiceError.InvalidInput, "Doctor Id Cannot Be Empty");
		}
		List<Prescription> prescriptions = null;

		prescriptions = prescriptionServices.getPrescriptions(page, size, doctorId, hospitalId, locationId, patientId,
				updatedTime, otpService.checkOTPVerified(doctorId, locationId, hospitalId, patientId), discarded,
				false);

		Response<Prescription> response = new Response<Prescription>();
		response.setDataList(prescriptions);
		return response;
	}
	
	@GetMapping(value = PathProxy.PrescriptionUrls.GET_PRESCRIPTIONS_FOR_EMR)
	@ApiOperation(value = PathProxy.PrescriptionUrls.GET_PRESCRIPTIONS_FOR_EMR, notes = PathProxy.PrescriptionUrls.GET_PRESCRIPTIONS_FOR_EMR)
	
	public Response<Prescription> getPrescriptionForEMR(@RequestParam("page") int page, @RequestParam("size") int size,
			@RequestParam("doctorId") String doctorId, @RequestParam("locationId") String locationId,
			@RequestParam("hospitalId") String hospitalId, @RequestParam("patientId") String patientId,
			@DefaultValue("0") @RequestParam("updatedTime") String updatedTime,@RequestParam("from") String from,@RequestParam("to") String to,
			  @RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(locationId)) {
			throw new BusinessException(ServiceError.InvalidInput, "Doctor Id Cannot Be Empty");
		}
		List<Prescription> prescriptions = null;

		prescriptions = prescriptionServices.getPrescriptionsForEMR(page, size, doctorId, hospitalId, locationId, patientId, updatedTime, otpService.checkOTPVerified(doctorId, locationId, hospitalId, patientId),from,to, discarded, false);

		Response<Prescription> response = new Response<Prescription>();
		response.setDataList(prescriptions);
		return response;
	}

	
	@GetMapping(value = PathProxy.PrescriptionUrls.SEARCH_DRUGS)
	@ApiOperation(value = PathProxy.PrescriptionUrls.SEARCH_DRUGS, notes = PathProxy.PrescriptionUrls.SEARCH_DRUGS)
	public Response<Object> searchDrug(@RequestParam("page") int page, @RequestParam("size") int size,
			@RequestParam(value = "doctorId") String doctorId, @RequestParam(value = "locationId") String locationId,
			@RequestParam(value = "hospitalId") String hospitalId,
			@DefaultValue("0") @RequestParam(value = "updatedTime") String updatedTime,
			  @RequestParam(value = "discarded") Boolean discarded,
			@RequestParam(value = "searchTerm") String searchTerm) {

		if (DPDoctorUtils.anyStringEmpty(doctorId, locationId, hospitalId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		List<Drug> drugDocuments = prescriptionServices.getCustomGlobalDrugs(page, size, doctorId, locationId,
				hospitalId, updatedTime, discarded, searchTerm);
		Response<Object> response = new Response<Object>();
		response.setData(prescriptionServices.countCustomGlobalDrugs(doctorId, locationId, hospitalId, updatedTime,
				discarded, searchTerm));
		response.setDataList(drugDocuments);
		return response;
	}
	
	
	@GetMapping(value = PathProxy.PrescriptionUrls.GET_DRUGS_BY_CODE)
	@ApiOperation(value = PathProxy.PrescriptionUrls.GET_DRUGS_BY_CODE, notes = PathProxy.PrescriptionUrls.GET_DRUGS_BY_CODE)
	public Response<Drug> getDrugDetails(@PathVariable("drugCode") String drugCode) {
		if (drugCode == null) {
			logger.error("DrugId Is NULL");
			throw new BusinessException(ServiceError.InvalidInput, "DrugId Is NULL");
		}
		Drug drugAddEditResponse = prescriptionServices.getDrugByDrugCode(drugCode);
		Response<Drug> response = new Response<Drug>();
		response.setData(drugAddEditResponse);
		return response;
	}
	
}
