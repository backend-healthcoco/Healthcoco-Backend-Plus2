package com.dpdocter.webservices;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.dpdocter.beans.DoctorSignUp;
import com.dpdocter.beans.LocationAndAccessControl;
import com.dpdocter.beans.User;
import com.dpdocter.elasticsearch.document.ESDoctorDocument;
import com.dpdocter.elasticsearch.services.ESRegistrationService;
import com.dpdocter.enums.Resource;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.request.DoctorSignupHandheldContinueRequest;
import com.dpdocter.request.DoctorSignupHandheldRequest;
import com.dpdocter.request.DoctorSignupRequest;
import com.dpdocter.request.PatientProfilePicChangeRequest;
import com.dpdocter.request.PatientSignUpRequest;
import com.dpdocter.request.PatientSignupRequestMobile;
import com.dpdocter.request.VerifyUnlockPatientRequest;
import com.dpdocter.request.VerifyUnlockPatientRequest.FlagEnum;
import com.dpdocter.response.PateientSignUpCheckResponse;
import com.dpdocter.services.SignUpService;
import com.dpdocter.services.TransactionalManagementService;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Component
@Path(PathProxy.SIGNUP_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.ADMIN_BASE_URL, description = "Endpoint for signup")
public class SignUpApi {

    private static Logger logger = Logger.getLogger(SignUpApi.class.getName());

    @Autowired
    private SignUpService signUpService;

    @Autowired
    private ESRegistrationService esRegistrationService;

    @Autowired
    private TransactionalManagementService transnationalService;

    @Context
    private UriInfo uriInfo;

    @Value(value = "${image.path}")
    private String imagePath;

    @Path(value = PathProxy.SignUpUrls.ADMIN_SIGNUP)
    @POST
    @ApiOperation(value = PathProxy.SignUpUrls.ADMIN_SIGNUP, notes = PathProxy.SignUpUrls.ADMIN_SIGNUP)
    public Response<User> adminSignup(PatientSignUpRequest request) {
	if (request == null) {
	    logger.warn("Request send  is NULL");
	    throw new BusinessException(ServiceError.InvalidInput, "Request send  is NULL");
	}

	User user = signUpService.adminSignUp(request);
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

    @Path(value = PathProxy.SignUpUrls.DOCTOR_SIGNUP)
    @POST
    @ApiOperation(value = PathProxy.SignUpUrls.DOCTOR_SIGNUP, notes = PathProxy.SignUpUrls.DOCTOR_SIGNUP)
    public Response<DoctorSignUp> doctorSignup(DoctorSignupRequest request) {
	if (request == null) {
	    logger.warn("Request send  is NULL");
	    throw new BusinessException(ServiceError.InvalidInput, "Request send is NULL");
	}

	DoctorSignUp doctorSignUp = signUpService.doctorSignUp(request, uriInfo);
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
		    doctorSignUp.getHospital().setHospitalImageUrl(getFinalImageURL(doctorSignUp.getHospital().getHospitalImageUrl()));
		}
	    }
	    transnationalService.addResource(doctorSignUp.getUser().getId(), Resource.DOCTOR, false);
	    esRegistrationService.addDoctor(getESDoctorDocument(doctorSignUp));
	    }

	Response<DoctorSignUp> response = new Response<DoctorSignUp>();
	response.setData(doctorSignUp);
	return response;
    }

    @Path(value = PathProxy.SignUpUrls.DOCTOR_SIGNUP_HANDHELD)
    @POST
    @ApiOperation(value = PathProxy.SignUpUrls.DOCTOR_SIGNUP_HANDHELD, notes = PathProxy.SignUpUrls.DOCTOR_SIGNUP_HANDHELD)
    public Response<DoctorSignUp> doctorHandheld(DoctorSignupHandheldRequest request) {
	if (request == null) {
	    logger.warn("Request send  is NULL");
	    throw new BusinessException(ServiceError.InvalidInput, "Request send  is NULL");
	}
	DoctorSignUp doctorSignUp = signUpService.doctorHandheld(request);
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
		    doctorSignUp.getHospital().setHospitalImageUrl(getFinalImageURL(doctorSignUp.getHospital().getHospitalImageUrl()));
		}
	    }
	    // transnationalService.addResource(doctorSignUp.getUser().getId(),
	    // Resource.DOCTOR, false);
	    // esRegistrationService.addDoctor(getSolrDoctorDocument(doctorSignUp));
	}
	Response<DoctorSignUp> response = new Response<DoctorSignUp>();
	response.setData(doctorSignUp);
	return response;
    }

    @Path(value = PathProxy.SignUpUrls.DOCTOR_SIGNUP_HANDHELD_CONTINUE)
    @POST
    @ApiOperation(value = PathProxy.SignUpUrls.DOCTOR_SIGNUP_HANDHELD_CONTINUE, notes = PathProxy.SignUpUrls.DOCTOR_SIGNUP_HANDHELD_CONTINUE)
    public Response<DoctorSignUp> doctorHandheldContinue(DoctorSignupHandheldContinueRequest request) {
	if (request == null) {
	    logger.warn("Request send  is NULL");
	    throw new BusinessException(ServiceError.InvalidInput, "Request send  is NULL");
	}
	DoctorSignUp doctorSignUp = signUpService.doctorHandheldContinue(request, uriInfo);
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
		    doctorSignUp.getHospital().setHospitalImageUrl(getFinalImageURL(doctorSignUp.getHospital().getHospitalImageUrl()));
		}
	    }
	    transnationalService.addResource(doctorSignUp.getUser().getId(), Resource.DOCTOR, false);
	    esRegistrationService.addDoctor(getESDoctorDocument(doctorSignUp));
	}

	Response<DoctorSignUp> response = new Response<DoctorSignUp>();
	response.setData(doctorSignUp);
	return response;
    }

//    @Path(value = PathProxy.SignUpUrls.PATIENT_SIGNUP)
//    @POST
//    @ApiOperation(value = PathProxy.SignUpUrls.PATIENT_SIGNUP, notes = PathProxy.SignUpUrls.PATIENT_SIGNUP)
//    public Response<User> patientSignup(PatientSignUpRequest request) {
//	if (request == null) {
//	    logger.warn("Request send  is NULL");
//	    throw new BusinessException(ServiceError.InvalidInput, "Request send is NULL");
//	}
//	User user = signUpService.patientSignUp(request);
//	if (user != null) {
//	    if (user.getImageUrl() != null) {
//		user.setImageUrl(getFinalImageURL(user.getImageUrl()));
//	    }
//	    if (user.getThumbnailUrl() != null) {
//		user.setThumbnailUrl(getFinalImageURL(user.getThumbnailUrl()));
//	    }
//	}
//	Response<User> response = new Response<User>();
//	response.setData(user);
//	return response;
//    }

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
    @Path(value = PathProxy.SignUpUrls.PATIENT_SIGNUP_MOBILE)
    @POST
    @ApiOperation(value = PathProxy.SignUpUrls.PATIENT_SIGNUP_MOBILE, notes = PathProxy.SignUpUrls.PATIENT_SIGNUP_MOBILE)
    public Response<User> patientSignupMobile(PatientSignupRequestMobile request) {
	if (request == null) {
	    logger.warn("Request send is NULL");
	    throw new BusinessException(ServiceError.InvalidInput, "Request send is NULL");
	}
	List<User> users = new ArrayList<>();

	if (request.isNewPatientNeedToBeCreated()) {
	    User user = signUpService.signupNewPatient(request);
	    users.add(user);
	} else {
	    users = signUpService.signupAlreadyRegisteredPatient(request);
	}
	for (User user : users) {
	    if (user.getImageUrl() != null) {
		user.setImageUrl(getFinalImageURL(user.getImageUrl()));
	    }
	    if (user.getThumbnailUrl() != null) {
		user.setThumbnailUrl(getFinalImageURL(user.getThumbnailUrl()));
	    }
	}
	Response<User> response = new Response<User>();
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

    @Path(value = PathProxy.SignUpUrls.PATIENT_PROFILE_PIC_CHANGE)
    @POST
    @ApiOperation(value = PathProxy.SignUpUrls.PATIENT_PROFILE_PIC_CHANGE, notes = PathProxy.SignUpUrls.PATIENT_PROFILE_PIC_CHANGE)
    public Response<User> patientProfilePicChange(PatientProfilePicChangeRequest request) {
	if (request == null) {
	    logger.warn("Request send  is NULL");
	    throw new BusinessException(ServiceError.InvalidInput, "Request sent is NULL");
	}
	User user = signUpService.patientProfilePicChange(request);
	transnationalService.addResource(user.getId(), Resource.PATIENT, false);
	transnationalService.checkPatient(user.getId());
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

    @Produces(MediaType.TEXT_HTML)
    @Path(value = PathProxy.SignUpUrls.VERIFY_USER)
    @GET
    @ApiOperation(value = PathProxy.SignUpUrls.VERIFY_USER, notes = PathProxy.SignUpUrls.VERIFY_USER)
    public String verifyUser(@PathParam(value = "tokenId") String tokenId) {
	if (tokenId == null) {
	    logger.warn("Invalid Input");
	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
	}
	String response = signUpService.verifyUser(tokenId);

	return response;
    }

    @Path(value = PathProxy.SignUpUrls.ACTIVATE_USER)
    @GET
    @ApiOperation(value = PathProxy.SignUpUrls.ACTIVATE_USER, notes = PathProxy.SignUpUrls.ACTIVATE_USER)
    public Response<Boolean> activateUser(@PathParam("userId") String userId, @DefaultValue(value = "true") @QueryParam("activate") Boolean activate) {
	if (DPDoctorUtils.anyStringEmpty(userId)) {
	    logger.warn("Invalid Input. User Id Cannot Be Empty");
	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input. User Id Cannot Be Empty");
	}
	Boolean verifyUserResponse = signUpService.activateUser(userId, activate);
	esRegistrationService.activateUser(userId);
	Response<Boolean> response = new Response<Boolean>();
	response.setData(verifyUserResponse);
	return response;
    }

    @Path(value = PathProxy.SignUpUrls.ACTIVATE_LOCATION)
    @GET
    @ApiOperation(value = PathProxy.SignUpUrls.ACTIVATE_LOCATION, notes = PathProxy.SignUpUrls.ACTIVATE_LOCATION)
    public Response<Boolean> activateLocation(@PathParam("locationId") String locationId, @DefaultValue(value = "true") @QueryParam("activate") Boolean activate) {
	if (DPDoctorUtils.anyStringEmpty(locationId)) {
	    logger.warn("Invalid Input. Location Id Cannot Be Empty");
	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input. Location Id Cannot Be Empty");
	}
	Boolean verifyUserResponse = signUpService.activateLocation(locationId, activate);
	Response<Boolean> response = new Response<Boolean>();
	response.setData(verifyUserResponse);
	return response;
    }

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

    @Path(value = PathProxy.SignUpUrls.CHECK_MOBNUM_SIGNEDUP)
    @GET
    @ApiOperation(value = PathProxy.SignUpUrls.CHECK_MOBNUM_SIGNEDUP, notes = PathProxy.SignUpUrls.CHECK_MOBNUM_SIGNEDUP)
    public Response<PateientSignUpCheckResponse> checkMobileNumberSignedUp(@PathParam(value = "mobileNumber") String mobileNumber) {
	if (DPDoctorUtils.anyStringEmpty(mobileNumber)) {
	    logger.warn("Invalid Input. Mobile Number Cannot Be Empty!");
	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input. Mobile Number Cannot Be Empty!");
	}

	PateientSignUpCheckResponse checkResponse = signUpService.checkMobileNumberSignedUp(mobileNumber);
	Response<PateientSignUpCheckResponse> response = new Response<PateientSignUpCheckResponse>();
	response.setData(checkResponse);
	return response;
    }

    @Path(value = PathProxy.SignUpUrls.CHECK_IF_EMAIL_ADDR_EXIST)
    @GET
    @ApiOperation(value = PathProxy.SignUpUrls.CHECK_IF_EMAIL_ADDR_EXIST, notes = PathProxy.SignUpUrls.CHECK_IF_EMAIL_ADDR_EXIST)
    public Response<Boolean> checkEmailExist(@PathParam(value = "emailaddress") String emailaddress) {
	if (emailaddress == null) {
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

    private ESDoctorDocument getESDoctorDocument(DoctorSignUp doctor) {
    	ESDoctorDocument esDoctorDocument = null;
	try {
		esDoctorDocument = new ESDoctorDocument();
	    BeanUtil.map(doctor.getUser(), esDoctorDocument);
	    esDoctorDocument.setUserId(doctor.getUser().getId());
	    if (doctor.getHospital() != null && doctor.getHospital().getLocationsAndAccessControl() != null) {
		for (LocationAndAccessControl locationAndAccessControl : doctor.getHospital().getLocationsAndAccessControl()) {
		    BeanUtil.map(locationAndAccessControl, esDoctorDocument);
		    esDoctorDocument.setLocationId(locationAndAccessControl.getId());
		}
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
	return esDoctorDocument;
    }

    @Path(value = PathProxy.SignUpUrls.RESEND_VERIFICATION_EMAIL_TO_DOCTOR)
    @GET
    @ApiOperation(value = PathProxy.SignUpUrls.RESEND_VERIFICATION_EMAIL_TO_DOCTOR, notes = PathProxy.SignUpUrls.RESEND_VERIFICATION_EMAIL_TO_DOCTOR)
    public Response<Boolean> resendVerificationEmail(@PathParam(value = "emailaddress") String emailaddress) {
	if (DPDoctorUtils.anyStringEmpty(emailaddress)) {
	    logger.warn("Invalid Input");
	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
	}
	Response<Boolean> response = new Response<Boolean>();
	response.setData(signUpService.resendVerificationEmail(emailaddress, uriInfo));
	return response;
    }
}
