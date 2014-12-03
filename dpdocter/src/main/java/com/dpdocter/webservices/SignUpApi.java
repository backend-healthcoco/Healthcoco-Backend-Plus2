package com.dpdocter.webservices;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dpdocter.beans.User;
import com.dpdocter.beans.UserActivation;
import com.dpdocter.enums.RoleEnum;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.services.SignUpService;

import common.util.web.Response;

@Component
@Path(PathProxy.SIGNUP_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SignUpApi {
	@Autowired
	private SignUpService signUpService;
	
	@Path(value=PathProxy.SignUpUrls.DOCTER_SIGNUP)
	@POST
	public Response<User> docterSignup(User user){
		if(user == null){
			throw new BusinessException(ServiceError.InvalidInput, "User to be saved is NULL");
		}
		user =  signUpService.signUp(user, RoleEnum.DOCTER.getRole());
		Response<User> response = new Response<User>();
		response.setData(user);
		return response;
	}
	
	@Path(value=PathProxy.SignUpUrls.PATIENT_SIGNUP)
	@POST
	public Response<User> patientSignup(User user){
		if(user == null){
			throw new BusinessException(ServiceError.InvalidInput, "User to be saved is NULL");
		}
		user =  signUpService.signUp(user, RoleEnum.PATIENT.getRole());
		Response<User> response = new Response<User>();
		response.setData(user);
		return response;
	}
	
	@Path(value=PathProxy.SignUpUrls.ACTIVATE_USER)
	@GET
	public Response<UserActivation> activateUser(@PathParam(value="userId")String userId){
		if(userId == null){
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Boolean isActivated = false;
		isActivated =  signUpService.activateUser(userId);
		UserActivation userActivation = new UserActivation();
		userActivation.setActivated(isActivated);
		Response<UserActivation> response = new Response<UserActivation>();
		response.setData(userActivation);
		
		return response;
	}
	
	
}
