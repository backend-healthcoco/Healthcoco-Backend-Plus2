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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dpdocter.beans.DoctorSignUp;
import com.dpdocter.beans.Location;
import com.dpdocter.beans.User;
import com.dpdocter.beans.UserActivation;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.request.DoctorSignupRequest;
import com.dpdocter.request.PatientProfilePicChangeRequest;
import com.dpdocter.request.PatientSignUpRequest;
import com.dpdocter.services.SignUpService;
import com.dpdocter.solr.document.SolrDoctorDocument;
import com.dpdocter.solr.document.SolrLocationDocument;
import com.dpdocter.solr.services.SolrAppointmentService;
import common.util.web.Response;

@Component
@Path(PathProxy.SIGNUP_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SignUpApi {
    @Autowired
    private SignUpService signUpService;

    @Autowired
    private SolrAppointmentService solrAppointmentService;

    @Path(value = PathProxy.SignUpUrls.DOCTOR_SIGNUP)
    @POST
    public Response<DoctorSignUp> doctorSignup(DoctorSignupRequest request) {
	if (request == null) {
	    throw new BusinessException(ServiceError.InvalidInput, "Request send  is NULL");
	}

	DoctorSignUp doctorSignUp = signUpService.doctorSignUp(request);

	if (doctorSignUp != null) {
	    solrAppointmentService.addDoctor(getSolrDoctorDocument(doctorSignUp));
	    solrAppointmentService.addLocation(getSolrLocationDocument(doctorSignUp));
	}

	Response<DoctorSignUp> response = new Response<DoctorSignUp>();
	response.setData(doctorSignUp);
	return response;
    }

    @Path(value = PathProxy.SignUpUrls.PATIENT_SIGNUP)
    @POST
    public Response<User> patientSignup(PatientSignUpRequest request) {
	if (request == null) {
	    throw new BusinessException(ServiceError.InvalidInput, "Request send is NULL");
	}
	User user = signUpService.patientSignUp(request);
	Response<User> response = new Response<User>();
	response.setData(user);
	return response;
    }

    @Path(value = PathProxy.SignUpUrls.PATIENT_PROFILE_PIC_CHANGE)
    @POST
    public Response<User> patientProfilePicChange(PatientProfilePicChangeRequest request) {
	if (request == null) {
	    throw new BusinessException(ServiceError.InvalidInput, "Request sent is NULL");
	}
	User user = signUpService.patientProfilePicChange(request);
	Response<User> response = new Response<User>();
	response.setData(user);
	return response;
    }

    @Path(value = PathProxy.SignUpUrls.ACTIVATE_USER)
    @GET
    public Response<UserActivation> activateUser(@PathParam(value = "userId") String userId) {
	if (userId == null) {
	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
	}
	Boolean isActivated = false;
	isActivated = signUpService.activateUser(userId);
	UserActivation userActivation = new UserActivation();
	userActivation.setActivated(isActivated);
	Response<UserActivation> response = new Response<UserActivation>();
	response.setData(userActivation);

	return response;
    }

    @Path(value = PathProxy.SignUpUrls.CHECK_IF_USERNAME_EXIST)
    @GET
    public Response<Boolean> checkUsernameExist(@PathParam(value = "username") String username) {
	if (username == null) {
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
	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
	}
	Response<Boolean> response = new Response<Boolean>();
	response.setData(signUpService.checkEmailAddressExist(emailaddress));
	return response;
    }

    private SolrDoctorDocument getSolrDoctorDocument(DoctorSignUp doctor) {
	SolrDoctorDocument solrDoctorDocument = null;
	try {
	    solrDoctorDocument = new SolrDoctorDocument();
	    BeanUtil.map(doctor.getHospital().getLocations().get(0), solrDoctorDocument);
	    BeanUtil.map(doctor.getUser(), solrDoctorDocument);
	    /*BeanUtil.map(doctor.getUser(), solrDoctorDocument);
	    List<String> specialiazation = new ArrayList<String>();
	    SolrLocationDocument solrLocationDocument = new SolrLocationDocument();
	    for (Location location : doctor.getHospital().getLocations()) {
	    if (location.getSpecialization() != null) {
	        specialiazation.addAll(location.getSpecialization());
	    }
	    BeanUtil.map(location, solrLocationDocument);
	    }
	    solrDoctorDocument.setSpecialization(specialiazation);
	    solrDoctorDocument.setLocation(solrLocationDocument);*/
	} catch (Exception e) {
	    e.printStackTrace();
	}
	return solrDoctorDocument;
    }

    private List<SolrLocationDocument> getSolrLocationDocument(DoctorSignUp doctor) {
	List<SolrLocationDocument> solrLocationDocuments = null;
	try {
	    solrLocationDocuments = new ArrayList<SolrLocationDocument>();
	    for (Location location : doctor.getHospital().getLocations()) {
		SolrLocationDocument solrLocationDocument = new SolrLocationDocument();
		BeanUtil.map(location, solrLocationDocument);
		solrLocationDocuments.add(solrLocationDocument);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
	return solrLocationDocuments;
    }

}
