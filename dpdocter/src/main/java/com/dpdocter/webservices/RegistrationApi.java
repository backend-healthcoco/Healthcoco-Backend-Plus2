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

import com.dpdocter.beans.Referrence;
import com.dpdocter.beans.RegisteredPatientDetails;
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
	public Response<RegisteredPatientDetails> patientRegister(PatientRegistrationRequest request) {
		if (request == null) {
			throw new BusinessException(ServiceError.InvalidInput,"Invalid Input");
		}
		Response<RegisteredPatientDetails> response = new Response<RegisteredPatientDetails>();
		RegisteredPatientDetails registeredPatientDetails = null;
		//User user = registrationService.checkIfPatientExist(request);
		if (request.getUserId() == null) {
			registeredPatientDetails = registrationService.registerNewPatient(request);
		} else {
			registeredPatientDetails = registrationService.registerExistingPatient(request);
		}
		response.setData(registeredPatientDetails);
		return response;
	}
	
	@Path(value = PathProxy.RegistrationUrls.EXISTING_PATIENTS_BY_PHONE_NUM)
	@GET
	public Response<User> getExistingPatients(@PathParam("mobileNumber")String mobileNumber,
			@PathParam("locationId")String locationId,
			@PathParam("hospitalId")String hospitalId) {
		if (mobileNumber == null) {
			throw new BusinessException(ServiceError.InvalidInput,
					"Invalid Input.Mobile Number is null");
		}
		if (locationId == null) {
			throw new BusinessException(ServiceError.InvalidInput,
					"Invalid Input.locationId is null");
		}
		if (hospitalId == null) {
			throw new BusinessException(ServiceError.InvalidInput,
					"Invalid Input.hospitalId is null");
		}
		Response<User> response = new Response<User>();

		List<User> users = registrationService.getUsersByPhoneNumber(mobileNumber,locationId,hospitalId);
		response.setDataList(users);
		return response;
	}
	
	@Path(value = PathProxy.RegistrationUrls.EXISTING_PATIENTS_BY_PHONE_NUM_COUNT)
	@GET
	public Response<Integer> getExistingPatientsCount(@PathParam("mobileNumber")String mobileNumber) {
		if (mobileNumber == null) {
			throw new BusinessException(ServiceError.InvalidInput,
					"Invalid Input.Phone Number is null");
		}
		Response<Integer> response = new Response<Integer>();
		Integer patientCountByMobNum = 0;
		List<User> users = registrationService.getUsersByPhoneNumber(mobileNumber,null,null);
		if(users != null){
			patientCountByMobNum = users.size();
		}
		response.setData(patientCountByMobNum);
		return response;
	}
	
	@Path(value = PathProxy.RegistrationUrls.GET_PATIENT_PROFILE)
	@GET
	public Response<RegisteredPatientDetails> getPatientProfile(@PathParam("userId")String userId,@PathParam("doctorId")String doctorId,
			@PathParam("locationId")String locationId,@PathParam("hospitalId")String hospitalId) {
		if (userId == null) {
			throw new BusinessException(ServiceError.InvalidInput,
					"Invalid Input.userId is null");
		}
		if (doctorId == null) {
			throw new BusinessException(ServiceError.InvalidInput,
					"Invalid Input.doctorId is null");
		}
		if (locationId == null) {
			throw new BusinessException(ServiceError.InvalidInput,
					"Invalid Input.locationId is null");
		}
		if (hospitalId == null) {
			throw new BusinessException(ServiceError.InvalidInput,
					"Invalid Input.hospitalId is null");
		}
		Response<RegisteredPatientDetails> response = new Response<RegisteredPatientDetails>();

		RegisteredPatientDetails registeredPatientDetails = registrationService.getPatientProfileByUserId(userId, doctorId, locationId, hospitalId);
		response.setData(registeredPatientDetails);
		return response;
	}
	
	@Path(value = PathProxy.RegistrationUrls.ADD_REFERRENCE)
	@POST
	public Response<Referrence> addReferrence(Referrence request) {
		if (request == null) {
			throw new BusinessException(ServiceError.InvalidInput,"Invalid Input");
		}
		Response<Referrence> response = new Response<Referrence>();
		response.setData(registrationService.addEditReferrence(request));
		return response;
	}
	
	@Path(value = PathProxy.RegistrationUrls.DELETE_REFERRENCE)
	@GET
	public Response<Boolean> deleteReferrence(@PathParam("referrenceId")String referrenceId) {
		if (referrenceId == null) {
			throw new BusinessException(ServiceError.InvalidInput,
					"Invalid Input.referrenceId is null");
		}
		Response<Boolean> response = new Response<Boolean>();
		registrationService.deleteReferrence(referrenceId);
		response.setData(true);
		return response;
	}
	
	@Path(value = PathProxy.RegistrationUrls.GET_REFERRENCES)
	@GET
	public Response<Referrence> getReferrences(@PathParam("doctorId")String doctorId,@PathParam("locationId")String locationId,@PathParam("hospitalId")String hospitalId) {
		if (doctorId == null) {
			throw new BusinessException(ServiceError.InvalidInput,
					"Invalid Input.doctorId is null");
		}
		if (locationId == null) {
			throw new BusinessException(ServiceError.InvalidInput,
					"Invalid Input.locationId is null");
		}
		if (hospitalId == null) {
			throw new BusinessException(ServiceError.InvalidInput,
					"Invalid Input.hospitalId is null");
		}
		Response<Referrence> response = new Response<Referrence>();
		List<Referrence> referrences = registrationService.getReferrences(doctorId, locationId, hospitalId);
		response.setDataList(referrences);
		return response;
	}
	
	@Path(value = PathProxy.RegistrationUrls.PATIENT_ID_GENERATOR)
	@GET
	public Response<String> patientIDGenerator(@PathParam("doctorId")String doctorId,
			@PathParam("locationId")String locationId,@PathParam("hospitalId")String hospitalId) {
		
		if (doctorId == null) {
			throw new BusinessException(ServiceError.InvalidInput,
					"Invalid Input.doctorId is null");
		}
		if (locationId == null) {
			throw new BusinessException(ServiceError.InvalidInput,
					"Invalid Input.locationId is null");
		}
		if (hospitalId == null) {
			throw new BusinessException(ServiceError.InvalidInput,
					"Invalid Input.hospitalId is null");
		}
		
		Response<String> response = new Response<String>();
		String generatedId = registrationService.patientIdGenerator(doctorId, locationId, hospitalId);
		response.setData(generatedId);
		return response;
	}
	
}
