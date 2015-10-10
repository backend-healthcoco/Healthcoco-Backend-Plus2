package com.dpdocter.webservices;

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
import com.dpdocter.beans.User;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.request.DoctorSignupRequest;
import com.dpdocter.request.PatientProfilePicChangeRequest;
import com.dpdocter.request.PatientSignUpRequest;
import com.dpdocter.services.SignUpService;
import com.dpdocter.solr.services.SolrRegistrationService;
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
	DoctorSignUp doctorSignUp = signUpService.doctorSignUp(request);
	if (doctorSignUp != null) {
	    if (doctorSignUp.getUser() != null) {
		if (doctorSignUp.getUser().getImageUrl() != null) {
		    doctorSignUp.getUser().setImageUrl(getFinalImageURL(doctorSignUp.getUser().getImageUrl()));
		}
	    }
	    if (doctorSignUp.getHospital() != null) {
		if (doctorSignUp.getHospital().getHospitalImageUrl() != null) {
		    doctorSignUp.getHospital().setHospitalImageUrl(getFinalImageURL(doctorSignUp.getHospital().getHospitalImageUrl()));
		}
	    }
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
	}
	Response<User> response = new Response<User>();
	response.setData(user);
	return response;
    }

    @Path(value = PathProxy.SignUpUrls.PATIENT_PROFILE_PIC_CHANGE)
    @POST
    public Response<User> patientProfilePicChange(PatientProfilePicChangeRequest request) {
	if (request == null) {
	    logger.warn("Request send  is NULL");
	    throw new BusinessException(ServiceError.InvalidInput, "Request sent is NULL");
	}
	User user = signUpService.patientProfilePicChange(request);

	solrRegistrationService.patientProfilePicChange(request.getUsername(), user.getImageUrl());
	if (user != null) {
	    if (user.getImageUrl() != null) {
		user.setImageUrl(getFinalImageURL(user.getImageUrl()));
	    }
	}
	Response<User> response = new Response<User>();
	response.setData(user);
	return response;
    }

    @Produces(MediaType.TEXT_HTML)
    @Path(value = PathProxy.SignUpUrls.ACTIVATE_USER)
    @GET
    public String activateUser(@PathParam(value = "tokenId") String tokenId) {
	if (tokenId == null) {
	    logger.warn("Invalid Input");
	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
	}
	String response = signUpService.activateUser(tokenId);
	return "<html><body>" + response + "</body></html>";
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
}
