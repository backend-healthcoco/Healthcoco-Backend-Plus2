package com.dpdocter.webservices;

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
			throw new BusinessException(ServiceError.InvalidInput,"Invalid Input");
		}
		Response<User> response = new Response<User>();
		User user = null;
		//User user = registrationService.checkIfPatientExist(request);
		if (request.getUserId() == null) {
			user = registrationService.registerNewPatient(request);
		} else {
			user = registrationService.registerExistingPatient(request);
		}
		response.setData(user);
		return response;
	}
	
	@Path(value = PathProxy.RegistrationUrls.EXISTING_PATIENTS_BY_PHONE_NUM)
	@GET
	public Response<User> getExistingPatients(@PathParam("phoneNumber")String phoneNumber) {
		if (phoneNumber == null) {
			throw new BusinessException(ServiceError.InvalidInput,
					"Invalid Input.Phone Number is null");
		}
		Response<User> response = new Response<User>();

		List<User> users = registrationService.getUsersByPhoneNumber(phoneNumber);
		response.setDataList(users);
		return response;
	}
	
	
}
