package com.dpdocter.webservices;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.dpdocter.beans.Hospital;
import com.dpdocter.beans.LoginResponse;
import com.dpdocter.beans.User;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.request.LoginPatientRequest;
import com.dpdocter.request.LoginRequest;
import com.dpdocter.services.LoginService;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;

/**
 * @author veeraj
 */
@Component
@Path(PathProxy.LOGIN_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.LOGIN_BASE_URL, description = "Endpoint for login")
public class LoginApi {

    private static Logger logger = Logger.getLogger(LoginApi.class.getName());

    @Autowired
    private LoginService loginService;

    @Value(value = "${image.path}")
    private String imagePath;

    @Path(value = PathProxy.LoginUrls.LOGIN_ADMIN)
    @POST
    @ApiOperation(value = PathProxy.LoginUrls.LOGIN_ADMIN, notes = PathProxy.LoginUrls.LOGIN_ADMIN)
    public Response<User> adminLogin(LoginPatientRequest request) {
	if (request == null) {
	    logger.warn("Invalid Input");
	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
	}
	User loginResponse = loginService.adminLogin(request);
	if (loginResponse != null) {
	    if (!DPDoctorUtils.anyStringEmpty(loginResponse.getImageUrl())) {
		loginResponse.setImageUrl(getFinalImageURL(loginResponse.getImageUrl()));
	    }
	    if (!DPDoctorUtils.anyStringEmpty(loginResponse.getThumbnailUrl())) {
		loginResponse.setThumbnailUrl(getFinalImageURL(loginResponse.getThumbnailUrl()));
	    }
	}
	Response<User> response = new Response<User>();
	if (response != null)
	    response.setData(loginResponse);
	return response;
    }

    @Path(value = PathProxy.LoginUrls.LOGIN_USER)
    @POST
    @ApiOperation(value = PathProxy.LoginUrls.LOGIN_USER, notes = PathProxy.LoginUrls.LOGIN_USER)
    public Response<LoginResponse> login(LoginRequest request) {
	if (request == null) {
	    logger.warn("Invalid Input");
	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
	}
	LoginResponse loginResponse = loginService.login(request);
	if (loginResponse != null) {
	    if (!DPDoctorUtils.anyStringEmpty(loginResponse.getUser().getImageUrl())) {
		loginResponse.getUser().setImageUrl(getFinalImageURL(loginResponse.getUser().getImageUrl()));
	    }
	    if (!DPDoctorUtils.anyStringEmpty(loginResponse.getUser().getThumbnailUrl())) {
		loginResponse.getUser().setThumbnailUrl(getFinalImageURL(loginResponse.getUser().getThumbnailUrl()));
	    }
	    if (loginResponse.getHospitals() != null && !loginResponse.getHospitals().isEmpty()) {
		for (Hospital hospital : loginResponse.getHospitals()) {
		    if (!DPDoctorUtils.anyStringEmpty(hospital.getHospitalImageUrl())) {
			hospital.setHospitalImageUrl(getFinalImageURL(hospital.getHospitalImageUrl()));
		    }
		}
	    }
	}
	Response<LoginResponse> response = new Response<LoginResponse>();
	if (response != null)
	    response.setData(loginResponse);
	return response;
    }

    @Path(value = PathProxy.LoginUrls.LOGIN_PATIENT)
    @POST
    @ApiOperation(value = PathProxy.LoginUrls.LOGIN_PATIENT, notes = PathProxy.LoginUrls.LOGIN_PATIENT)
    public Response<User> loginPatient(LoginPatientRequest request) {
	if (request == null) {
	    logger.warn("Invalid Input");
	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
	}
	List<User> users = loginService.loginPatient(request);
	if(users != null && !users.isEmpty()){
		for(User user : users){
			user.setImageUrl(getFinalImageURL(user.getImageUrl()));
			user.setThumbnailUrl(getFinalImageURL(user.getThumbnailUrl()));
		}
	}
	Response<User> response = new Response<User>();
	response.setDataList(users);
	return response;
    }

    private String getFinalImageURL(String imageURL) {
	if (imageURL != null) {
	    return imagePath + imageURL;
	} else
	    return null;

    }
}
