package com.dpdocter.webservices;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
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
import com.dpdocter.services.SignUpService;
import com.dpdocter.services.TransactionalManagementService;
import com.dpdocter.solr.document.SolrDoctorDocument;
import com.dpdocter.solr.services.SolrRegistrationService;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;

@Component
@Path(PathProxy.SIGNUP_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SignUpApi {

    private static Logger logger = Logger.getLogger(SignUpApi.class.getName());

    @Autowired
    private SignUpService signUpService;

    @Autowired
    private SolrRegistrationService solrRegistrationService;

    @Autowired
    private TransactionalManagementService transnationalService;

    @Context
    private UriInfo uriInfo;

    @Value(value = "${IMAGE_URL_ROOT_PATH}")
    private String imageUrlRootPath;

    @Path(value = PathProxy.SignUpUrls.DOCTOR_SIGNUP)
    @POST
    public Response<DoctorSignUp> doctorSignup(DoctorSignupRequest request) {
	if (request == null) {
	    logger.warn("Request send  is NULL");
	    throw new BusinessException(ServiceError.InvalidInput, "Request send  is NULL");
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
	    solrRegistrationService.addDoctor(getSolrDoctorDocument(doctorSignUp));
	    
//	    if(doctorSignUp.getHospital() != null){
//	    	if(doctorSignUp.getHospital().getLocationsAndAccessControl() != null && !doctorSignUp.getHospital().getLocationsAndAccessControl().isEmpty()){
//	    		for(LocationAndAccessControl location : doctorSignUp.getHospital().getLocationsAndAccessControl()){
//	    			transnationalService.addResource(location.getId(), Resource.LOCATION, false);
//	    		    solrRegistrationService.addLocation(getSolrLocationDocument(location));
//	    		}
//	    	}
//	    }
	    
	    
	}

	Response<DoctorSignUp> response = new Response<DoctorSignUp>();
	response.setData(doctorSignUp);
	return response;
    }

    @Path(value = PathProxy.SignUpUrls.DOCTOR_SIGNUP_HANDHELD)
    @POST
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
//	    transnationalService.addResource(doctorSignUp.getUser().getId(), Resource.DOCTOR, false);
//	    solrRegistrationService.addDoctor(getSolrDoctorDocument(doctorSignUp));
	}
	Response<DoctorSignUp> response = new Response<DoctorSignUp>();
	response.setData(doctorSignUp);
	return response;
    }

    @Path(value = PathProxy.SignUpUrls.DOCTOR_SIGNUP_HANDHELD_CONTINUE)
    @POST
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
	    solrRegistrationService.addDoctor(getSolrDoctorDocument(doctorSignUp));
	}

	Response<DoctorSignUp> response = new Response<DoctorSignUp>();
	response.setData(doctorSignUp);
	return response;
    }

    @Path(value = PathProxy.SignUpUrls.PATIENT_SIGNUP)
    @POST
    public Response<User> patientSignup(PatientSignUpRequest request) {
	if (request == null) {
	    logger.warn("Request send  is NULL");
	    throw new BusinessException(ServiceError.InvalidInput, "Request send is NULL");
	}
	User user = signUpService.patientSignUp(request);
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
    
    /**
     * This API signup patient into DB.
     * It contains a flag isNewPatientNeedToBeCreated which indicates that a new patient signup need to be done or not.
     * When new patient is created then unlock only that new patient only.Rest of the patients will be locked.
     * When patient signup is done (for already registered from doc.)
     *  with 80% match then unlock all the patients with that mobile number.
     *  
     * @param PatientSignupRequestMobile 
     * @return User List
     */
    @Path(value = PathProxy.SignUpUrls.PATIENT_SIGNUP_MOBILE)
    @POST
    public Response<User> patientSignupMobile(PatientSignupRequestMobile request) {
	if (request == null) {
	    logger.warn("Request send is NULL");
	    throw new BusinessException(ServiceError.InvalidInput, "Request send is NULL");
	}
	List<User> users = new ArrayList<>();
	
	if(request.isNewPatientNeedToBeCreated()){
		User user = signUpService.signupNewPatient(request);
		users.add(user);
	}else{
		users = signUpService.signupAlreadyRegisteredPatient(request);
	}
	for(User user : users){
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
     * This API will take name and mobile num and flag (to verify or unlock) as i/p 
     * and return true or false based on 80 % match of name.POST API.In case of unlock
     *  it will unlock the user.In case of verify only return true or false ,no unlock 
     *  in this case.Also while unlock check 80% match for only lock patients.
     */
    @Path(value = PathProxy.SignUpUrls.VERIFY_UNLOCK_PATIENT)
    @POST
    public Response<Boolean> verifyOrUnlockPatient(VerifyUnlockPatientRequest request) {
		boolean flag = false;
		if((request.getVerifyOrUnlock().getFlag()).equals(FlagEnum.VERIFY.getFlag())){
			flag = signUpService.verifyPatientBasedOn80PercentMatchOfName(request.getName(), request.getMobileNumber());
		}else if ((request.getVerifyOrUnlock().getFlag()).equals(FlagEnum.UNLOCK.getFlag())){
			flag = signUpService.unlockPatientBasedOn80PercentMatch(request.getName(), request.getMobileNumber());
		}
    	Response<Boolean> flagResponse = new Response<Boolean>();
    	flagResponse.setData(flag);
    	return flagResponse;
    	
    }
    	
    @Path(value = PathProxy.SignUpUrls.PATIENT_PROFILE_PIC_CHANGE)
    @POST
    public Response<User> patientProfilePicChange(PatientProfilePicChangeRequest request) {
	if (request == null) {
	    logger.warn("Request send  is NULL");
	    throw new BusinessException(ServiceError.InvalidInput, "Request sent is NULL");
	}
	User user = signUpService.patientProfilePicChange(request);
	transnationalService.addResource(user.getId(), Resource.PATIENT, false);
	solrRegistrationService.patientProfilePicChange(request.getUsername(), user.getImageUrl());
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
    public String verifyUser(@PathParam(value = "tokenId") String tokenId) {
	if (tokenId == null) {
	    logger.warn("Invalid Input");
	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
	}
	String response = signUpService.verifyUser(tokenId);
	
	return  response;
    }

    @Path(value = PathProxy.SignUpUrls.ACTIVATE_USER)
    @GET
    public Response<Boolean> activateUser(@PathParam("userId") String userId) {
	if (DPDoctorUtils.anyStringEmpty(userId)) {
	    logger.warn("Invalid Input. User Id Cannot Be Empty");
	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input. User Id Cannot Be Empty");
	}
	Boolean verifyUserResponse = signUpService.activateUser(userId);
	Response<Boolean> response = new Response<Boolean>();
	response.setData(verifyUserResponse);
	return response;
    }

    @Path(value = PathProxy.SignUpUrls.CHECK_IF_USERNAME_EXIST)
    @GET
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
    public Response<Boolean> checkMobileNumberSignedUp(@PathParam(value = "mobileNumber") String mobileNumber) {
	if (DPDoctorUtils.anyStringEmpty(mobileNumber)) {
	    logger.warn("Invalid Input. Mobile Number Cannot Be Empty!");
	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input. Mobile Number Cannot Be Empty!");
	}

	boolean mobileNumberSignedUp = signUpService.checkMobileNumberSignedUp(mobileNumber);
	Response<Boolean> response = new Response<Boolean>();
	response.setData(mobileNumberSignedUp);
	return response;
    }

    @Path(value = PathProxy.SignUpUrls.CHECK_IF_EMAIL_ADDR_EXIST)
    @GET
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
	String finalImageURL = uriInfo.getBaseUri().toString().replace(uriInfo.getBaseUri().getPath(), imageUrlRootPath);
	return finalImageURL + imageURL;
    }

    private SolrDoctorDocument getSolrDoctorDocument(DoctorSignUp doctor) {
	SolrDoctorDocument solrDoctorDocument = null;
	try {
	    solrDoctorDocument = new SolrDoctorDocument();
	    BeanUtil.map(doctor.getUser(), solrDoctorDocument);
	    solrDoctorDocument.setUserId(doctor.getUser().getId());
	    List<String> specialiazation = new ArrayList<String>();
	    if (doctor.getHospital() != null &&  doctor.getHospital().getLocationsAndAccessControl() != null) {
		for (LocationAndAccessControl locationAndAccessControl : doctor.getHospital().getLocationsAndAccessControl()) { 
			specialiazation.addAll(locationAndAccessControl.getSpecialization());
			BeanUtil.map(locationAndAccessControl, solrDoctorDocument);
			solrDoctorDocument.setLocationId(locationAndAccessControl.getId());
		}
	    }
	    solrDoctorDocument.setSpecialization(specialiazation);
	} catch (Exception e) {
	    e.printStackTrace();
	}
	return solrDoctorDocument;
    }

//     private SolrLocationDocument getSolrLocationDocument(LocationAndAccessControl location) {
//    	 SolrLocationDocument solrLocationDocument = new SolrLocationDocument();
//     try {
//    	 BeanUtil.map(location, solrLocationDocument);
//     } catch (Exception e) {
//     e.printStackTrace();
//     }
//     return solrLocationDocuments;
//     }
}
