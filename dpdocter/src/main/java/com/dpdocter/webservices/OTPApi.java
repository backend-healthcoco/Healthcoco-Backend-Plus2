package com.dpdocter.webservices;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.services.OTPService;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
(PathProxy.OTP_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.OTP_BASE_URL, description = "Endpoint for otp")
public class OTPApi {

	private static Logger logger = LogManager.getLogger(OTPApi.class.getName());

	@Autowired
	private OTPService otpService;

	
	@GetMapping(value = PathProxy.OTPUrls.OTP_GENERATOR)
	@ApiOperation(value = PathProxy.OTPUrls.OTP_GENERATOR, notes = PathProxy.OTPUrls.OTP_GENERATOR)
	public Response<String> otpGenerator(@PathVariable("doctorId") String doctorId,
			@PathVariable("locationId") String locationId, @PathVariable("hospitalId") String hospitalId,
			@PathVariable("patientId") String patientId) {
		if (DPDoctorUtils.anyStringEmpty(doctorId, locationId, hospitalId, patientId)) {
			logger.warn("Invalid Input. DoctorId, LocationId, HospitalId, PatientId Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Invalid Input. DoctorId, LocationId, HospitalId, PatientId, Cannot Be Empty");
		}
		String OTP = otpService.otpGenerator(doctorId, locationId, hospitalId, patientId);
		Response<String> response = new Response<String>();
		response.setData(OTP);
		return response;
	}

	
    @GetMapping(value = PathProxy.OTPUrls.OTP_GENERATOR_MOBILE)
    @ApiOperation(value = PathProxy.OTPUrls.OTP_GENERATOR_MOBILE, notes = PathProxy.OTPUrls.OTP_GENERATOR_MOBILE)
    public Response<Boolean> otpGenerator(@PathVariable("mobileNumber") String mobileNumber, @DefaultValue("false") @RequestParam(value = "isPatientOTP") Boolean isPatientOTP,@DefaultValue("+91") @RequestParam(value = "countryCode") String countryCode) {
	if (DPDoctorUtils.anyStringEmpty(mobileNumber)) {
	    logger.warn("Invalid Input. Mobile Number Cannot Be Empty");
	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input. Mobile Number Cannot Be Empty");
	}
	//mobileNumber=countryCode+mobileNumber;
	Boolean OTP = otpService.otpGenerator(mobileNumber, isPatientOTP);
	Response<Boolean> response = new Response<Boolean>();
	response.setData(OTP);
	return response;
    }

	
	@GetMapping(value = PathProxy.OTPUrls.VERIFY_OTP)
	@ApiOperation(value = PathProxy.OTPUrls.VERIFY_OTP, notes = PathProxy.OTPUrls.VERIFY_OTP)
	public Response<Boolean> verifyOTP(@PathVariable("doctorId") String doctorId,
			@PathVariable("locationId") String locationId, @PathVariable("hospitalId") String hospitalId,
			@PathVariable("patientId") String patientId, @PathVariable("otpNumber") String otpNumber) {
		if (DPDoctorUtils.anyStringEmpty(otpNumber, doctorId, locationId, hospitalId, patientId)) {
			logger.warn("Invalid Input. DoctorId, LocationId, HospitalId, PatientId, OTP Number Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Invalid Input. DoctorId, LocationId, HospitalId, PatientId, OTP Number Cannot Be Empty");
		}
		Boolean verifyOTPResponse = otpService.verifyOTP(doctorId, locationId, hospitalId, patientId, otpNumber);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(verifyOTPResponse);
		return response;
	}

	
	@GetMapping(value = PathProxy.OTPUrls.VERIFY_OTP_MOBILE)
	@ApiOperation(value = PathProxy.OTPUrls.VERIFY_OTP_MOBILE, notes = PathProxy.OTPUrls.VERIFY_OTP_MOBILE)
	public Response<Boolean> verifyOTP(@PathVariable("mobileNumber") String mobileNumber,
			@PathVariable("otpNumber") String otpNumber) {
		if (DPDoctorUtils.anyStringEmpty(otpNumber, mobileNumber)) {
			logger.warn("Invalid Input. DoctorId, LocationId, HospitalId, PatientId, OTP Number Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Invalid Input. DoctorId, LocationId, HospitalId, PatientId, OTP Number Cannot Be Empty");
		}
		Boolean verifyOTPResponse = otpService.verifyOTP(mobileNumber, otpNumber);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(verifyOTPResponse);
		return response;
	}
	
	
	
	@GetMapping(value = PathProxy.OTPUrls.VERIFY_OTP_SIGNUP)
	@ApiOperation(value = PathProxy.OTPUrls.VERIFY_OTP_SIGNUP, notes = PathProxy.OTPUrls.VERIFY_OTP_SIGNUP)
	public Response<Boolean> verifySignUpOTP(@PathVariable("mobileNumber") String mobileNumber,
			@PathVariable("otpNumber") String otpNumber,@PathVariable("countryCode") String countryCode) {
		if (DPDoctorUtils.anyStringEmpty(otpNumber, mobileNumber,countryCode)) {
			logger.warn("Invalid Input. mobileNumber,countryCode, OTP Number Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Invalid Input. mobileNumber,countryCode, OTP Number Cannot Be Empty");
		}
		Boolean verifyOTPResponse = otpService.verifyOTP(mobileNumber, otpNumber,countryCode);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(verifyOTPResponse);
		return response;
	}
}
