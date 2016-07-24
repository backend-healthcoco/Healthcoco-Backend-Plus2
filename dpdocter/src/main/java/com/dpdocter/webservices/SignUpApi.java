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

import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.dpdocter.beans.DoctorContactUs;
import com.dpdocter.beans.DoctorSignUp;
import com.dpdocter.beans.LocationAndAccessControl;
import com.dpdocter.beans.User;
import com.dpdocter.elasticsearch.document.ESDoctorDocument;
import com.dpdocter.enums.Resource;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.request.PatientProfilePicChangeRequest;
import com.dpdocter.request.PatientSignupRequestMobile;
import com.dpdocter.request.VerifyUnlockPatientRequest;
import com.dpdocter.request.VerifyUnlockPatientRequest.FlagEnum;
import com.dpdocter.response.PateientSignUpCheckResponse;
import com.dpdocter.services.DoctorContactUsService;
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
@Api(value = PathProxy.SIGNUP_BASE_URL, description = "Endpoint for signup")
public class SignUpApi {

    private static Logger logger = Logger.getLogger(SignUpApi.class.getName());

    @Autowired
    private SignUpService signUpService;

    @Autowired
    private TransactionalManagementService transnationalService;
    
    @Autowired
    private DoctorContactUsService doctorContactUsService;

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
    @Path(value = PathProxy.SignUpUrls.PATIENT_SIGNUP_MOBILE)
    @POST
    @ApiOperation(value = PathProxy.SignUpUrls.PATIENT_SIGNUP_MOBILE, notes = PathProxy.SignUpUrls.PATIENT_SIGNUP_MOBILE)
    public Response<User> patientSignupMobile(PatientSignupRequestMobile request) {
    	if (request == null || DPDoctorUtils.anyStringEmpty(request.getMobileNumber()) || request.getPassword() == null || request.getPassword().length == 0) {
    	    logger.warn("Inavlid Input");
    	    throw new BusinessException(ServiceError.InvalidInput, "Inavlid Input");
    	}else if (request.getName().length() < 2) {
    		logger.warn(firstNameValidaton);
    		throw new BusinessException(ServiceError.InvalidInput, firstNameValidaton);
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

    @Path(value = PathProxy.SignUpUrls.SUBMIT_DOCTOR_CONTACT)
    @POST
    @ApiOperation(value = PathProxy.SignUpUrls.SUBMIT_DOCTOR_CONTACT, notes = PathProxy.SignUpUrls.SUBMIT_DOCTOR_CONTACT)
    public Response<String> submitDoctorContactUsInfo(DoctorContactUs doctorContactUs)
    {
    	if(doctorContactUs == null){
    		logger.warn("Doctor contact data is null");
    	    throw new BusinessException(ServiceError.InvalidInput, "Doctor Contact data is null");
    	}else if(DPDoctorUtils.anyStringEmpty(doctorContactUs.getFirstName(), doctorContactUs.getEmailAddress(), doctorContactUs.getTitle(),doctorContactUs.getCity(), doctorContactUs.getMobileNumber()) || doctorContactUs.getGender() == null || doctorContactUs.getSpecialities() == null || doctorContactUs.getSpecialities().isEmpty()){
    		logger.warn("Invalid Input");
    		throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
    	}else if (doctorContactUs.getFirstName().length() < 2) {
    		logger.warn(firstNameValidaton);
    		throw new BusinessException(ServiceError.InvalidInput, firstNameValidaton);
    	 }
    	
    	Response<String> response = new Response<String>();
    	response.setData(doctorContactUsService.submitDoctorContactUSInfo(doctorContactUs));
    	return response;
    }
    
}
