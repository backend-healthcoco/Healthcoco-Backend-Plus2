package com.dpdocter.webservices;

import java.util.ArrayList;
import java.util.List;

import javax.mail.MessagingException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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
import com.dpdocter.response.SlotDataResponse;
import com.dpdocter.response.UserAddressResponse;
import com.dpdocter.response.WebDoctorClinicsResponse;
import com.dpdocter.services.AppointmentService;
import com.dpdocter.services.LoginService;
import com.dpdocter.services.MailService;
import com.dpdocter.services.PromotionService;
import com.dpdocter.services.RegistrationService;
import com.dpdocter.services.SignUpService;
import com.dpdocter.services.WebAppointmentService;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Component
@Path(PathProxy.WEB_APPOINTMENT_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.WEB_APPOINTMENT_BASE_URL, description = "Endpoint for appointment")
public class WebAppointmentApi {

	private static Logger logger = Logger.getLogger(WebAppointmentApi.class.getName());

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
	
	@Path(value = PathProxy.WebAppointmentUrls.GET_CLINICS_BY_DOCTOR_SLUG_URL)
	@GET
	@ApiOperation(value = PathProxy.WebAppointmentUrls.GET_CLINICS_BY_DOCTOR_SLUG_URL, notes = PathProxy.WebAppointmentUrls.GET_CLINICS_BY_DOCTOR_SLUG_URL)
	public Response<WebDoctorClinicsResponse> getClinicsByDoctorSlugURL(@QueryParam("doctorSlugUrl") String doctorSlugUrl) {
		
		WebDoctorClinicsResponse webDoctorClinicsResponse = webAppointmentService.getClinicsByDoctorSlugURL(doctorSlugUrl);
		Response<WebDoctorClinicsResponse> response = new Response<WebDoctorClinicsResponse>();
		response.setData(webDoctorClinicsResponse);
		return response;
	}
	
	@Path(value = PathProxy.WebAppointmentUrls.GET_TIME_SLOTS)
	@GET
	@ApiOperation(value = PathProxy.WebAppointmentUrls.GET_TIME_SLOTS, notes = PathProxy.WebAppointmentUrls.GET_TIME_SLOTS)
	public Response<SlotDataResponse> getTimeSlots(@PathParam("doctorId") String doctorId,
			@PathParam("locationId") String locationId, @PathParam("date") String date) {
		
		SlotDataResponse slots = webAppointmentService.getTimeSlots(doctorId, locationId, date);
		Response<SlotDataResponse> response = new Response<SlotDataResponse>();
		response.setData(slots);
		return response;
	}
	
	@Path(value = PathProxy.WebAppointmentUrls.ADD_APPOINTMENT)
	@POST
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
	
	@Path(value = PathProxy.LoginUrls.LOGIN_PATIENT)
	@POST
	@ApiOperation(value = PathProxy.LoginUrls.LOGIN_PATIENT, notes = PathProxy.LoginUrls.LOGIN_PATIENT)
	public Response<Object> loginPatient(LoginPatientRequest request,
			@DefaultValue("true") @QueryParam("discardedAddress") Boolean discardedAddress) {
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
	@Path(value = PathProxy.SignUpUrls.PATIENT_SIGNUP_MOBILE)
	@POST
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

}
