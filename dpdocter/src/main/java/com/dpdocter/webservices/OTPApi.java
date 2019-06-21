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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.services.OTPService;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Component
@Path(PathProxy.OTP_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.OTP_BASE_URL, description = "Endpoint for otp")
public class OTPApi {

	private static Logger logger = Logger.getLogger(OTPApi.class.getName());

	@Autowired
	private OTPService otpService;

	@Path(value = PathProxy.OTPUrls.OTP_GENERATOR)
	@GET
	@ApiOperation(value = PathProxy.OTPUrls.OTP_GENERATOR, notes = PathProxy.OTPUrls.OTP_GENERATOR)
	public Response<String> otpGenerator(@PathParam("doctorId") String doctorId,
			@PathParam("locationId") String locationId, @PathParam("hospitalId") String hospitalId,
			@PathParam("patientId") String patientId) {
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

	@Path(value = PathProxy.OTPUrls.OTP_GENERATOR_MOBILE)
    @GET
    @ApiOperation(value = PathProxy.OTPUrls.OTP_GENERATOR_MOBILE, notes = PathProxy.OTPUrls.OTP_GENERATOR_MOBILE)
    public Response<Boolean> otpGenerator(@PathParam("mobileNumber") String mobileNumber, @DefaultValue("false") @QueryParam(value = "isPatientOTP") Boolean isPatientOTP,@DefaultValue("+91") @QueryParam(value = "countryCode") String countryCode) {
	if (DPDoctorUtils.anyStringEmpty(mobileNumber)) {
	    logger.warn("Invalid Input. Mobile Number Cannot Be Empty");
	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input. Mobile Number Cannot Be Empty");
	}
	mobileNumber=countryCode+mobileNumber;
	Boolean OTP = otpService.otpGenerator(mobileNumber, isPatientOTP);
	Response<Boolean> response = new Response<Boolean>();
	response.setData(OTP);
	return response;
    }

	@Path(value = PathProxy.OTPUrls.VERIFY_OTP)
	@GET
	@ApiOperation(value = PathProxy.OTPUrls.VERIFY_OTP, notes = PathProxy.OTPUrls.VERIFY_OTP)
	public Response<Boolean> verifyOTP(@PathParam("doctorId") String doctorId,
			@PathParam("locationId") String locationId, @PathParam("hospitalId") String hospitalId,
			@PathParam("patientId") String patientId, @PathParam("otpNumber") String otpNumber) {
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

	@Path(value = PathProxy.OTPUrls.VERIFY_OTP_MOBILE)
	@GET
	@ApiOperation(value = PathProxy.OTPUrls.VERIFY_OTP_MOBILE, notes = PathProxy.OTPUrls.VERIFY_OTP_MOBILE)
	public Response<Boolean> verifyOTP(@PathParam("mobileNumber") String mobileNumber,
			@PathParam("otpNumber") String otpNumber) {
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
}
