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
	Response<LoginResponse> response = new Response<LoginResponse>();
	if(response != null)
	response.setData(loginResponse);
	return response;
    }

    @Path(value = PathProxy.LoginUrls.VERIFY_USER)
    @GET
    public Response<Boolean> verifyUser(@PathParam("userId") String userId) {
	if (DPDoctorUtils.anyStringEmpty(userId)) {
	    logger.warn("Invalid Input. User Id Cannot Be Empty");
	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input. User Id Cannot Be Empty");
	}
	Boolean verifyUserResponse = loginService.verifyUser(userId);
	Response<Boolean> response = new Response<Boolean>();
	response.setData(verifyUserResponse);
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
