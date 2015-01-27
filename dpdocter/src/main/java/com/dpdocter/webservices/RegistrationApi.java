package com.dpdocter.webservices;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dpdocter.beans.User;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.request.PatientRegistrationRequest;
import com.dpdocter.services.RegistrationService;
import common.util.web.Response;

/**
 * @author veeraj
 */
@Component
@Path(PathProxy.REGISTRATION_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RegistrationApi {
	@Autowired
	private RegistrationService registrationService;

	@Path(value = PathProxy.RegistrationUrls.PATIENT_REGISTER)
	@POST
	public Response<User> patientRegister(PatientRegistrationRequest request) {
		if (request == null) {
			throw new BusinessException(ServiceError.InvalidInput,
					"Invalid Input");
		}
		Response<User> response = new Response<User>();

		User user = registrationService.checkIfPatientExist(request);
		if (user == null) {
			user = registrationService.registerNewPatient(request);
		} else {
			user = registrationService.registerExistingPatient(request, user.getId());
		}
		response.setData(user);
		return response;
	}
}
