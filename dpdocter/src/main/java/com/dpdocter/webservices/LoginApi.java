package com.dpdocter.webservices;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dpdocter.beans.LoginResponse;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.request.LoginRequest;
import com.dpdocter.services.LoginService;
import common.util.web.Response;

/**
 * @author veeraj
 */
@Component
@Path(PathProxy.LOGIN_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LoginApi {
	@Autowired
	private LoginService loginService;

	@Path(value = PathProxy.LoginUrls.LOGIN_USER)
	@POST
	public Response<LoginResponse> login(LoginRequest request) {
		if (request == null) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		LoginResponse loginResponse = loginService.login(request);
		Response<LoginResponse> response = new Response<LoginResponse>();
		response.setData(loginResponse);
		return response;
	}
}
