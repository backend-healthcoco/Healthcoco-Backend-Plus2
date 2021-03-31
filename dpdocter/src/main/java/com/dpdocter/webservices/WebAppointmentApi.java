package com.dpdocter.webservices;

import java.util.ArrayList;
import java.util.List;

import javax.mail.MessagingException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dpdocter.beans.Appointment;
import com.dpdocter.beans.InternalPromoCode;
import com.dpdocter.beans.InternalPromotionGroup;
import com.dpdocter.beans.RegisteredPatientDetails;
import com.dpdocter.beans.UserAddress;
import com.dpdocter.enums.AppointmentCreatedBy;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.request.AppointmentRequest;
import com.dpdocter.request.LoginPatientRequest;
import com.dpdocter.request.PatientSignupRequestMobile;
import com.dpdocter.response.UserAddressResponse;
import com.dpdocter.response.WebAppointmentSlotDataResponse;
import com.dpdocter.response.WebDoctorClinicsResponse;
import com.dpdocter.services.AppointmentService;
import com.dpdocter.services.LoginService;
import com.dpdocter.services.MailService;
import com.dpdocter.services.OTPService;
import com.dpdocter.services.PromotionService;
import com.dpdocter.services.RegistrationService;
import com.dpdocter.services.SignUpService;
import com.dpdocter.services.WebAppointmentService;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
(PathProxy.WEB_APPOINTMENT_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.WEB_APPOINTMENT_BASE_URL, description = "Endpoint for appointment")
public class WebAppointmentApi {

	private static Logger logger = LogManager.getLogger(WebAppointmentApi.class.getName());

	@Autowired
	private WebAppointmentService webAppointmentService;
	
	@Autowired
	MailService mailService;
	
	@Autowired
	private AppointmentService appointmentService;

	@Autowired
	private SignUpService signUpService;

	@Value(value = "${register.first.name.validation}")
	private String firstNameValidaton;
	
	@Autowired
	private LoginService loginService;

	@Autowired
	private RegistrationService registrationService;
	
	@Autowired
	private PromotionService promotionService;

	@Value(value = "${image.path}")
	private String imagePath;
	
	@Autowired
    private OTPService otpService;
	
	
	@GetMapping(value = PathProxy.WebAppointmentUrls.GET_CLINICS_BY_DOCTOR_SLUG_URL)
	@ApiOperation(value = PathProxy.WebAppointmentUrls.GET_CLINICS_BY_DOCTOR_SLUG_URL, notes = PathProxy.WebAppointmentUrls.GET_CLINICS_BY_DOCTOR_SLUG_URL)
	public Response<WebDoctorClinicsResponse> getClinicsByDoctorSlugURL(@PathVariable("doctorSlugUrl") String doctorSlugUrl) {		
		WebDoctorClinicsResponse webDoctorClinicsResponse = webAppointmentService.getClinicsByDoctorSlugURL(doctorSlugUrl);
		Response<WebDoctorClinicsResponse> response = new Response<WebDoctorClinicsResponse>();
		response.setData(webDoctorClinicsResponse);
		return response;
	}
	
	
	@GetMapping(value = PathProxy.WebAppointmentUrls.GET_TIME_SLOTS)
	@ApiOperation(value = PathProxy.WebAppointmentUrls.GET_TIME_SLOTS, notes = PathProxy.WebAppointmentUrls.GET_TIME_SLOTS)
	public Response<WebAppointmentSlotDataResponse> getTimeSlots(@PathVariable("doctorId") String doctorId,
			@PathVariable("locationId") String locationId, @PathVariable("hospitalId") String hospitalId, @PathVariable("date") String date) {
		
		WebAppointmentSlotDataResponse slots = webAppointmentService.getTimeSlots(doctorId, locationId, hospitalId, date);
		Response<WebAppointmentSlotDataResponse> response = new Response<WebAppointmentSlotDataResponse>();
		response.setData(slots);
		return response;
	}
	
	
	@PostMapping(value = PathProxy.WebAppointmentUrls.ADD_APPOINTMENT)
	@ApiOperation(value = PathProxy.WebAppointmentUrls.ADD_APPOINTMENT, notes = PathProxy.WebAppointmentUrls.ADD_APPOINTMENT)
	public Response<Appointment> BookAppoinment(AppointmentRequest request)
			throws MessagingException {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId(),
				request.getHospitalId())) {
			logger.warn("Invalid Input");
			mailService.sendExceptionMail("Invalid input :: Doctor Id ,Location Id or Hostipal Id cannot be empty");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		} else if (request.getTime() != null && (request.getTime().getFromTime() > request.getTime().getToTime())) {
			logger.warn("Invalid Time");
			mailService.sendExceptionMail("Invalid input :: Time");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Time");
		} else if (request.getTime() != null
				&& ((request.getTime().getToTime() - request.getTime().getFromTime()) > 120)) {
			logger.warn("Invalid Time");
			mailService.sendExceptionMail("Invalid input : Appointment duration cannot be greater than 120 mins");
			throw new BusinessException(ServiceError.InvalidInput,
					"Invalid Time : Appointment duration cannot be greater than 120 mins");
		}

		request.setCreatedBy(AppointmentCreatedBy.PATIENT);
				
		Appointment appointment = null;
		if (request.getAppointmentId() == null) {
			appointment = appointmentService.addAppointment(request, true);
		} else {
			appointment = appointmentService.updateAppointment(request, true, false);
		}

		Response<Appointment> response = new Response<Appointment>();
		response.setData(appointment);
		return response;

	}
	
	
	@PostMapping(value = PathProxy.WebAppointmentUrls.LOGIN_PATIENT)
	@ApiOperation(value = PathProxy.LoginUrls.LOGIN_PATIENT, notes = PathProxy.LoginUrls.LOGIN_PATIENT)
	public Response<Object> loginPatient(LoginPatientRequest request,
			  @RequestParam("discardedAddress") Boolean discardedAddress) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getMobileNumber()) || request.getPassword() == null
				|| request.getPassword().length == 0) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		List<RegisteredPatientDetails> users = loginService.loginPatient(request);
		if (users != null && !users.isEmpty()) {
			for (RegisteredPatientDetails user : users) {
				user.setImageUrl(getFinalImageURL(user.getImageUrl()));
				user.setThumbnailUrl(getFinalImageURL(user.getThumbnailUrl()));
			}
		}
		Response<Object> response = new Response<Object>();
		response.setDataList(users);
		if (users != null && !users.isEmpty()) {
			List<UserAddress> userAddress = registrationService.getUserAddress(null, request.getMobileNumber(),
					discardedAddress);
			if (userAddress != null && !userAddress.isEmpty()) {
				UserAddressResponse userAddressResponse = new UserAddressResponse();
				userAddressResponse.setUserAddress(userAddress);
				response.setData(userAddressResponse);
			}
		}
		return response;
	}

	private String getFinalImageURL(String imageURL) {
		if (imageURL != null) {
			return imagePath + imageURL;
		} else
			return null;

	}
	
	@Produces(MediaType.APPLICATION_JSON)
	@PostMapping(value = PathProxy.WebAppointmentUrls.PATIENT_SIGNUP_MOBILE)
	@ApiOperation(value = PathProxy.SignUpUrls.PATIENT_SIGNUP_MOBILE, notes = PathProxy.SignUpUrls.PATIENT_SIGNUP_MOBILE)
	public Response<RegisteredPatientDetails> patientSignupMobile(PatientSignupRequestMobile request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getMobileNumber()) || request.getPassword() == null
				|| request.getPassword().length == 0) {
			logger.warn("Inavlid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Inavlid Input");
		}
		if (request.getInternalPromoCode() != null) {
			InternalPromotionGroup promotionGroup = promotionService.getPromotionGroup(request.getInternalPromoCode().trim());
			if (promotionGroup != null) {
				InternalPromoCode internalPromoCode = new InternalPromoCode();
				internalPromoCode.setMobileNumber(request.getMobileNumber());
				internalPromoCode.setPromoCode(request.getInternalPromoCode().toUpperCase());
				promotionService.addInternalPromoCode(internalPromoCode);
			} else {
				logger.warn("Promo Code not Found");
				throw new BusinessException(ServiceError.InvalidInput, "Promo code not found");
			}
		}
		
		List<RegisteredPatientDetails> users = new ArrayList<RegisteredPatientDetails>();

		if (request.isNewPatientNeedToBeCreated()) {
			if (request.getName().length() < 2) {
				logger.warn(firstNameValidaton);
				throw new BusinessException(ServiceError.InvalidInput, firstNameValidaton);
			}
			RegisteredPatientDetails user = signUpService.signupNewPatient(request);
			users.add(user);
		} else {
			users = signUpService.signupAlreadyRegisteredPatient(request);
		}
		for (RegisteredPatientDetails user : users) {
			if (user.getImageUrl() != null) {
				user.setImageUrl(getFinalImageURL(user.getImageUrl()));
			}
			if (user.getThumbnailUrl() != null) {
				user.setThumbnailUrl(getFinalImageURL(user.getThumbnailUrl()));
			}
		}
		Response<RegisteredPatientDetails> response = new Response<RegisteredPatientDetails>();
		response.setDataList(users);
		return response;
	}

	 
	    @GetMapping(value = PathProxy.WebAppointmentUrls.OTP_GENERATOR_MOBILE)
	    @ApiOperation(value = PathProxy.OTPUrls.OTP_GENERATOR_MOBILE, notes = PathProxy.OTPUrls.OTP_GENERATOR_MOBILE)
	    public Response<Boolean> otpGenerator(@PathVariable("mobileNumber") String mobileNumber, @DefaultValue("false") @RequestParam(value = "isPatientOTP") Boolean isPatientOTP) {
		if (DPDoctorUtils.anyStringEmpty(mobileNumber)) {
		    logger.warn("Invalid Input. Mobile Number Cannot Be Empty");
		    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input. Mobile Number Cannot Be Empty");
		}
		Boolean OTP = otpService.otpGenerator(mobileNumber, isPatientOTP);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(OTP);
		return response;
	    }

	    
	    @GetMapping(value = PathProxy.WebAppointmentUrls.VERIFY_OTP_MOBILE)
	    @ApiOperation(value = PathProxy.OTPUrls.VERIFY_OTP_MOBILE, notes = PathProxy.OTPUrls.VERIFY_OTP_MOBILE)
	    public Response<Boolean> verifyOTP(@PathVariable("mobileNumber") String mobileNumber, @PathVariable("otpNumber") String otpNumber) {
		if (DPDoctorUtils.anyStringEmpty(otpNumber, mobileNumber)) {
		    logger.warn("Invalid Input. DoctorId, LocationId, HospitalId, PatientId, OTP Number Cannot Be Empty");
		    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input. DoctorId, LocationId, HospitalId, PatientId, OTP Number Cannot Be Empty");
		}
		Boolean verifyOTPResponse = otpService.verifyOTP(mobileNumber, otpNumber);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(verifyOTPResponse);
		return response;
	    }
}
