package com.dpdocter.webservices;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.dpdocter.beans.ClinicContactUs;
import com.dpdocter.beans.CollectionBoy;
import com.dpdocter.beans.DoctorContactUs;
import com.dpdocter.beans.DoctorSignUp;
import com.dpdocter.beans.InternalPromoCode;
import com.dpdocter.beans.InternalPromotionGroup;
import com.dpdocter.beans.RegisteredPatientDetails;
import com.dpdocter.beans.User;
import com.dpdocter.enums.Resource;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.request.DoctorSignupRequest;
import com.dpdocter.request.PatientProfilePicChangeRequest;
import com.dpdocter.request.PatientSignupRequestMobile;
import com.dpdocter.request.VerifyUnlockPatientRequest;
import com.dpdocter.request.VerifyUnlockPatientRequest.FlagEnum;
import com.dpdocter.response.CollectionBoyResponse;
import com.dpdocter.response.PateientSignUpCheckResponse;
import com.dpdocter.services.ClinicContactUsService;
import com.dpdocter.services.DoctorContactUsService;
import com.dpdocter.services.PromotionService;
import com.dpdocter.services.SignUpService;
import com.dpdocter.services.TransactionalManagementService;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Component
@Path(PathProxy.SIGNUP_BASE_URL)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.SIGNUP_BASE_URL, description = "Endpoint for signup")
public class SignUpApi {

	private static Logger logger = LogManager.getLogger(SignUpApi.class.getName());

	@Autowired
	private SignUpService signUpService;

	@Autowired
	private TransactionalManagementService transnationalService;

	@Autowired
	private DoctorContactUsService doctorContactUsService;

	@Autowired
	private ClinicContactUsService clinicContactUsService;

	@Autowired
	private PromotionService promotionService;

	@Value(value = "${image.path}")
	private String imagePath;

	@Value(value = "${register.first.name.validation}")
	private String firstNameValidaton;

	/**
	 * This API signup patient into DB. It contains a flag
	 * isNewPatientNeedToBeCreated which indicates that a new patient signup
	 * need to be done or not. When new patient is created then unlock only that
	 * new patient only.Rest of the patients will be locked. When patient signup
	 * is done (for already registered from doc.) with 80% match then unlock all
	 * the patients with that mobile number.
	 * 
	 * @param PatientSignupRequestMobile
	 * @return User List
	 */
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
			InternalPromotionGroup promotionGroup = promotionService
					.getPromotionGroup(request.getInternalPromoCode().trim());
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

	/**
	 * This API will take name and mobile num and flag (to verify or unlock) as
	 * i/p and return true or false based on 80 % match of name.POST API.In case
	 * of unlock it will unlock the user.In case of verify only return true or
	 * false ,no unlock in this case.Also while unlock check 80% match for only
	 * lock patients.
	 */
	@Produces(MediaType.APPLICATION_JSON)
	@Path(value = PathProxy.SignUpUrls.VERIFY_UNLOCK_PATIENT)
	@POST
	@ApiOperation(value = PathProxy.SignUpUrls.VERIFY_UNLOCK_PATIENT, notes = PathProxy.SignUpUrls.VERIFY_UNLOCK_PATIENT)
	public Response<Boolean> verifyOrUnlockPatient(VerifyUnlockPatientRequest request) {
		boolean flag = false;
		if ((request.getVerifyOrUnlock().getFlag()).equals(FlagEnum.VERIFY.getFlag())) {
			flag = signUpService.verifyPatientBasedOn80PercentMatchOfName(request.getName(), request.getMobileNumber());
		} else if ((request.getVerifyOrUnlock().getFlag()).equals(FlagEnum.UNLOCK.getFlag())) {
			flag = signUpService.unlockPatientBasedOn80PercentMatch(request.getName(), request.getMobileNumber());
		}
		Response<Boolean> flagResponse = new Response<Boolean>();
		flagResponse.setData(flag);
		return flagResponse;

	}

	@Produces(MediaType.APPLICATION_JSON)
	@Path(value = PathProxy.SignUpUrls.VERIFY_LOCALE)
	@GET
	@ApiOperation(value = PathProxy.SignUpUrls.VERIFY_LOCALE, notes = PathProxy.SignUpUrls.VERIFY_LOCALE)
	public Response<String> verifyLocale(@PathParam(value = "tokenId") String tokenId) {
		if (tokenId == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		String string = signUpService.verifyLocale(tokenId);
		Response<String> response = new Response<String>();
		response.setData(string);
		return response;
	}

	@Produces(MediaType.APPLICATION_JSON)
	@Path(value = PathProxy.SignUpUrls.PATIENT_PROFILE_PIC_CHANGE)
	@POST
	@ApiOperation(value = PathProxy.SignUpUrls.PATIENT_PROFILE_PIC_CHANGE, notes = PathProxy.SignUpUrls.PATIENT_PROFILE_PIC_CHANGE)
	public Response<User> patientProfilePicChange(PatientProfilePicChangeRequest request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getUsername())) {
			logger.warn("Inavlid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Inavlid Input");
		}
		User user = signUpService.patientProfilePicChange(request);
		transnationalService.addResource(new ObjectId(user.getId()), Resource.PATIENT, false);
		transnationalService.checkPatient(new ObjectId(user.getId()));
		if (user != null) {
			if (user.getImageUrl() != null) {
				user.setImageUrl(getFinalImageURL(user.getImageUrl()));
			}
			if (user.getThumbnailUrl() != null) {
				user.setThumbnailUrl(getFinalImageURL(user.getThumbnailUrl()));
			}
		}
		Response<User> response = new Response<User>();
		response.setData(user);
		return response;
	}

	@Produces(MediaType.APPLICATION_JSON)
	@Path(value = PathProxy.SignUpUrls.VERIFY_USER)
	@GET
	@ApiOperation(value = PathProxy.SignUpUrls.VERIFY_USER, notes = PathProxy.SignUpUrls.VERIFY_USER)
	public Response<String> verifyUser(@PathParam(value = "tokenId") String tokenId) {
		if (tokenId == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		String string = signUpService.verifyUser(tokenId);
		Response<String> response = new Response<String>();
		response.setData(string);
		return response;
	}

	@Produces(MediaType.APPLICATION_JSON)
	@Path(value = PathProxy.SignUpUrls.CHECK_IF_USERNAME_EXIST)
	@GET
	@ApiOperation(value = PathProxy.SignUpUrls.CHECK_IF_USERNAME_EXIST, notes = PathProxy.SignUpUrls.CHECK_IF_USERNAME_EXIST)
	public Response<Boolean> checkUsernameExist(@PathParam(value = "username") String username) {
		if (username == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<Boolean> response = new Response<Boolean>();
		response.setData(signUpService.checkUserNameExist(username));
		return response;
	}

	@Produces(MediaType.APPLICATION_JSON)
	@Path(value = PathProxy.SignUpUrls.CHECK_IF_MOBNUM_EXIST)
	@GET
	@ApiOperation(value = PathProxy.SignUpUrls.CHECK_IF_MOBNUM_EXIST, notes = PathProxy.SignUpUrls.CHECK_IF_MOBNUM_EXIST)
	public Response<Boolean> checkMobileNumExist(@PathParam(value = "mobileNumber") String mobileNumber) {
		if (mobileNumber == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<Boolean> response = new Response<Boolean>();
		response.setData(signUpService.checkMobileNumExist(mobileNumber));
		return response;
	}

	@Produces(MediaType.APPLICATION_JSON)
	@Path(value = PathProxy.SignUpUrls.CHECK_MOBNUM_SIGNEDUP)
	@GET
	@ApiOperation(value = PathProxy.SignUpUrls.CHECK_MOBNUM_SIGNEDUP, notes = PathProxy.SignUpUrls.CHECK_MOBNUM_SIGNEDUP)
	public Response<PateientSignUpCheckResponse> checkMobileNumberSignedUp(
			@PathParam(value = "mobileNumber") String mobileNumber) {
		if (DPDoctorUtils.anyStringEmpty(mobileNumber)) {
			logger.warn("Invalid Input. Mobile Number Cannot Be Empty!");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input. Mobile Number Cannot Be Empty!");
		}

		PateientSignUpCheckResponse checkResponse = signUpService.checkMobileNumberSignedUp(mobileNumber);
		Response<PateientSignUpCheckResponse> response = new Response<PateientSignUpCheckResponse>();
		response.setData(checkResponse);
		return response;
	}

	@Produces(MediaType.APPLICATION_JSON)
	@Path(value = PathProxy.SignUpUrls.CHECK_IF_EMAIL_ADDR_EXIST)
	@GET
	@ApiOperation(value = PathProxy.SignUpUrls.CHECK_IF_EMAIL_ADDR_EXIST, notes = PathProxy.SignUpUrls.CHECK_IF_EMAIL_ADDR_EXIST)
	public Response<Boolean> checkEmailExist(@PathParam(value = "emailaddress") String emailaddress) {
		if (DPDoctorUtils.anyStringEmpty(emailaddress)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<Boolean> response = new Response<Boolean>();
		response.setData(signUpService.checkEmailAddressExist(emailaddress));
		return response;
	}

	private String getFinalImageURL(String imageURL) {
		return imagePath + imageURL;
	}

	@Produces(MediaType.APPLICATION_JSON)
	@Path(value = PathProxy.SignUpUrls.SUBMIT_DOCTOR_CONTACT)
	@POST
	@ApiOperation(value = PathProxy.SignUpUrls.SUBMIT_DOCTOR_CONTACT, notes = PathProxy.SignUpUrls.SUBMIT_DOCTOR_CONTACT)
	public Response<String> submitDoctorContactUsInfo(DoctorContactUs doctorContactUs) {
		if (doctorContactUs == null) {
			logger.warn("Doctor contact data is null");
			throw new BusinessException(ServiceError.InvalidInput, "Doctor Contact data is null");
		} else if (DPDoctorUtils.anyStringEmpty(doctorContactUs.getFirstName(), doctorContactUs.getEmailAddress(),
				doctorContactUs.getTitle(), doctorContactUs.getCity(), doctorContactUs.getMobileNumber())
				|| doctorContactUs.getGender() == null || doctorContactUs.getSpecialities() == null
				|| doctorContactUs.getSpecialities().isEmpty()) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		} else if (doctorContactUs.getFirstName().length() < 2) {
			logger.warn(firstNameValidaton);
			throw new BusinessException(ServiceError.InvalidInput, firstNameValidaton);
		}

		Response<String> response = new Response<String>();
		response.setData(doctorContactUsService.submitDoctorContactUSInfo(doctorContactUs));
		return response;
	}

	@Produces(MediaType.APPLICATION_JSON)
	@Path(value = PathProxy.SignUpUrls.SUBMIT_CLINIC_CONTACT)
	@POST
	@ApiOperation(value = PathProxy.SignUpUrls.SUBMIT_CLINIC_CONTACT, notes = PathProxy.SignUpUrls.SUBMIT_CLINIC_CONTACT)
	public Response<String> submitClinicContactUsInfo(ClinicContactUs clinicContactUs) {
		if (clinicContactUs == null) {
			logger.warn("Clinic contact data is null");
			throw new BusinessException(ServiceError.InvalidInput, "Clinic Contact data is null");
		} else if (DPDoctorUtils.anyStringEmpty(clinicContactUs.getEmailAddress(), clinicContactUs.getLocationName(),
				clinicContactUs.getDoctorId(), clinicContactUs.getCity())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		} else if (clinicContactUs.getLocationName().length() < 2) {
			logger.warn("LocationNameValidaton");
			throw new BusinessException(ServiceError.InvalidInput, "LocationNameValidaton");
		}

		Response<String> response = new Response<String>();
		response.setData(clinicContactUsService.submitClinicContactUSInfo(clinicContactUs));
		return response;
	}

	@Produces(MediaType.APPLICATION_JSON)
	@Path(value = PathProxy.SignUpUrls.SIGNUP_COLLECTION_BOY)
	@POST
	@ApiOperation(value = PathProxy.SignUpUrls.SIGNUP_COLLECTION_BOY, notes = PathProxy.SignUpUrls.SIGNUP_COLLECTION_BOY)
	public Response<CollectionBoyResponse> collectionBoySignup(CollectionBoy request) {
		Response<CollectionBoyResponse> response = null;

		if (request == null || request.getPassword() == null || request.getPassword().length == 0) {
			logger.warn("Request send  is NULL");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Request");
		}
		CollectionBoyResponse collectionBoy = signUpService.signupCollectionBoys(request);
		response = new Response<CollectionBoyResponse>();
		response.setData(collectionBoy);
		return response;
	}

	@Produces(MediaType.APPLICATION_JSON)
	@Path(value = PathProxy.SignUpUrls.WELCOME_USER)
	@GET
	@ApiOperation(value = PathProxy.SignUpUrls.WELCOME_USER, notes = PathProxy.SignUpUrls.WELCOME_USER)
	public Response<DoctorContactUs> welcomeUser(@PathParam(value = "tokenId") String tokenId) {
		if (tokenId == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		DoctorContactUs contactUs = signUpService.welcomeUser(tokenId);
		Response<DoctorContactUs> response = new Response<DoctorContactUs>();
		response.setData(contactUs);
		return response;
	}

	@Produces(MediaType.APPLICATION_JSON)
	@Path(value = PathProxy.SignUpUrls.DOCTOR_SIGNUP)
	@POST
	@ApiOperation(value = PathProxy.SignUpUrls.DOCTOR_SIGNUP, notes = PathProxy.SignUpUrls.DOCTOR_SIGNUP)
	public Response<DoctorSignUp> doctorSignup(DoctorSignupRequest request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getFirstName(), request.getEmailAddress(),
				request.getMobileNumber(), request.getCity())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		} else if (request.getFirstName().length() < 2) {
			logger.warn(firstNameValidaton);
			throw new BusinessException(ServiceError.InvalidInput, firstNameValidaton);
		}

		DoctorSignUp doctorSignUp = signUpService.doctorSignUp(request);
		if (doctorSignUp != null) {
			if (doctorSignUp.getUser() != null) {
				if (doctorSignUp.getUser().getImageUrl() != null) {
					doctorSignUp.getUser().setImageUrl(getFinalImageURL(doctorSignUp.getUser().getImageUrl()));
				}
				if (doctorSignUp.getUser().getThumbnailUrl() != null) {
					doctorSignUp.getUser().setThumbnailUrl(getFinalImageURL(doctorSignUp.getUser().getThumbnailUrl()));
				}
			}
			if (doctorSignUp.getHospital() != null) {
				if (doctorSignUp.getHospital().getHospitalImageUrl() != null) {
					doctorSignUp.getHospital()
							.setHospitalImageUrl(getFinalImageURL(doctorSignUp.getHospital().getHospitalImageUrl()));
				}
			}
			transnationalService.checkDoctor(new ObjectId(doctorSignUp.getUser().getId()), null);

		}

		Response<DoctorSignUp> response = new Response<DoctorSignUp>();
		response.setData(doctorSignUp);
		return response;
	}
	
	
//	@Produces(MediaType.APPLICATION_JSON)
//	@Path(value = PathProxy.SignUpUrls.DOCTOR_REGISTER)
//	@GET
//	@ApiOperation(value = PathProxy.SignUpUrls.DOCTOR_REGISTER, notes = PathProxy.SignUpUrls.DOCTOR_REGISTER)
//	 public Response<Boolean> DoctorRegister(@QueryParam(value = "mobileNumber") String mobileNumber) {
//			if (mobileNumber == null || mobileNumber.isEmpty()) {
//			    logger.warn("Mobile number is null");
//			    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
//			}
//			Boolean registerResponse = signUpService.DoctorRegister(mobileNumber);
//		
//			Response<Boolean> response = new Response<Boolean>();
//			if (response != null)
//			    response.setData(registerResponse);
//			
//		    
//			return response;
//		}
	
//	@Produces(MediaType.APPLICATION_JSON)
//	@Path(value = PathProxy.SignUpUrls.VERIFY_EMAIL_ADDRESS)
//	@GET
//	@ApiOperation(value = PathProxy.SignUpUrls.VERIFY_EMAIL_ADDRESS, notes = PathProxy.SignUpUrls.VERIFY_EMAIL_ADDRESS)
//	public Response<Boolean> verifyEmail(@PathParam(value = "emailaddress") String emailaddress) {
//		if (DPDoctorUtils.anyStringEmpty(emailaddress)) {
//			logger.warn("Invalid Input");
//			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
//		}
//		Response<Boolean> response = new Response<Boolean>();
//		response.setData(signUpService.verifyEmailAddress(emailaddress));
//		return response;
//	}
//
	
}
