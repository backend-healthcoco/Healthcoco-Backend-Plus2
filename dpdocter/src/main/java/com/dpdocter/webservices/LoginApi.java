package com.dpdocter.webservices;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.dpdocter.beans.Hospital;
import com.dpdocter.beans.LoginResponse;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.request.LoginRequest;
import com.dpdocter.services.LoginService;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;

/**
 * @author veeraj
 */
@Component
@Path(PathProxy.LOGIN_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LoginApi {

    private static Logger logger = Logger.getLogger(LoginApi.class.getName());

    @Autowired
    private LoginService loginService;

    @Context
    private UriInfo uriInfo;

    @Value(value = "${IMAGE_URL_ROOT_PATH}")
    private String imageUrlRootPath;

    @Path(value = PathProxy.LoginUrls.LOGIN_USER)
    @POST
    public Response<LoginResponse> login(LoginRequest request) {
	if (request == null) {
	    logger.warn("Invalid Input");
	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
	}
	LoginResponse loginResponse = loginService.login(request, uriInfo);
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

    private String getFinalImageURL(String imageURL) {
	if (imageURL != null) {
	    String finalImageURL = uriInfo.getBaseUri().toString().replace(uriInfo.getBaseUri().getPath(), imageUrlRootPath);
	    return finalImageURL + imageURL;
	} else
	    return null;

    }
}
