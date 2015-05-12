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

import com.dpdocter.beans.ClinicAddress;
import com.dpdocter.beans.ClinicProfile;
import com.dpdocter.beans.ClinicTiming;
import com.dpdocter.beans.Location;
import com.dpdocter.beans.Reference;
import com.dpdocter.beans.RegisteredPatientDetails;
import com.dpdocter.beans.User;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.request.PatientRegistrationRequest;
import com.dpdocter.response.ReferenceResponse;
import com.dpdocter.services.RegistrationService;
import common.util.web.DPDoctorUtils;
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
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<RegisteredPatientDetails> response = new Response<RegisteredPatientDetails>();
		RegisteredPatientDetails registeredPatientDetails = null;
		// User user = registrationService.checkIfPatientExist(request);
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
	public Response<User> getExistingPatients(@PathParam("mobileNumber") String mobileNumber, @PathParam("locationId") String locationId,
			@PathParam("hospitalId") String hospitalId) {
		if (mobileNumber == null) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input.Mobile Number is null");
		}
		if (locationId == null) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input.locationId is null");
		}
		if (hospitalId == null) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input.hospitalId is null");
		}
		Response<User> response = new Response<User>();

		List<User> users = registrationService.getUsersByPhoneNumber(mobileNumber, locationId, hospitalId);
		response.setDataList(users);
		return response;
	}

	@Path(value = PathProxy.RegistrationUrls.EXISTING_PATIENTS_BY_PHONE_NUM_COUNT)
	@GET
	public Response<Integer> getExistingPatientsCount(@PathParam("mobileNumber") String mobileNumber) {
		if (mobileNumber == null) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input.Phone Number is null");
		}
		Response<Integer> response = new Response<Integer>();
		Integer patientCountByMobNum = 0;
		List<User> users = registrationService.getUsersByPhoneNumber(mobileNumber, null, null);
		if (users != null) {
			patientCountByMobNum = users.size();
		}
		response.setData(patientCountByMobNum);
		return response;
	}

	@Path(value = PathProxy.RegistrationUrls.GET_PATIENT_PROFILE)
	@GET
	public Response<RegisteredPatientDetails> getPatientProfile(@PathParam("userId") String userId, @PathParam("doctorId") String doctorId,
			@PathParam("locationId") String locationId, @PathParam("hospitalId") String hospitalId) {
		if (userId == null) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input.userId is null");
		}
		if (doctorId == null) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input.doctorId is null");
		}
		if (locationId == null) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input.locationId is null");
		}
		if (hospitalId == null) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input.hospitalId is null");
		}
		Response<RegisteredPatientDetails> response = new Response<RegisteredPatientDetails>();

		RegisteredPatientDetails registeredPatientDetails = registrationService.getPatientProfileByUserId(userId, doctorId, locationId, hospitalId);
		response.setData(registeredPatientDetails);
		return response;
	}

	@Path(value = PathProxy.RegistrationUrls.ADD_REFERRENCE)
	@POST
	public Response<Reference> addReference(Reference request) {
		if (request == null) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<Reference> response = new Response<Reference>();
		response.setData(registrationService.addEditReference(request));
		return response;
	}

	@Path(value = PathProxy.RegistrationUrls.DELETE_REFERRENCE)
	@GET
	public Response<Boolean> deleteReferrence(@PathParam("referrenceId") String referrenceId) {
		if (referrenceId == null) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input.referrenceId is null");
		}
		Response<Boolean> response = new Response<Boolean>();
		registrationService.deleteReferrence(referrenceId);
		response.setData(true);
		return response;
	}

	@Path(value = PathProxy.RegistrationUrls.GET_REFERRENCES)
	@GET
	public Response<ReferenceResponse> getReferences(@PathParam("doctorId") String doctorId, @PathParam("locationId") String locationId,
			@PathParam("hospitalId") String hospitalId) {
		if (doctorId == null) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input.doctorId is null");
		}
		if (locationId == null) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input.locationId is null");
		}
		if (hospitalId == null) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input.hospitalId is null");
		}
		ReferenceResponse references = registrationService.getReferences(doctorId, locationId, hospitalId);
		Response<ReferenceResponse> response = new Response<ReferenceResponse>();
		response.setData(references);
		return response;
	}

	@Path(value = PathProxy.RegistrationUrls.GET_CUSTOM_REFERENCES)
	@GET
	public Response<ReferenceResponse> getCustomReferences(@PathParam("doctorId") String doctorId, @PathParam("locationId") String locationId,
			@PathParam("hospitalId") String hospitalId) {
		if (doctorId == null) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input.doctorId is null");
		}
		if (locationId == null) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input.locationId is null");
		}
		if (hospitalId == null) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input.hospitalId is null");
		}
		ReferenceResponse references = registrationService.getCustomReferences(doctorId, locationId, hospitalId);
		Response<ReferenceResponse> response = new Response<ReferenceResponse>();
		response.setData(references);
		return response;
	}

	@Path(value = PathProxy.RegistrationUrls.PATIENT_ID_GENERATOR)
	@GET
	public Response<String> patientIDGenerator(@PathParam("doctorId") String doctorId, @PathParam("locationId") String locationId,
			@PathParam("hospitalId") String hospitalId) {

		if (doctorId == null) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input.doctorId is null");
		}
		if (locationId == null) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input.locationId is null");
		}
		if (hospitalId == null) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input.hospitalId is null");
		}

		Response<String> response = new Response<String>();
		String generatedId = registrationService.patientIdGenerator(doctorId, locationId, hospitalId);
		response.setData(generatedId);
		return response;
	}

	@Path(value = PathProxy.RegistrationUrls.UPDATE_PATIENT_ID_GENERATOR_LOGIC)
	@GET
	public Response<Boolean> updatePatientInitialAndCounter(@PathParam("doctorId") String doctorId, @PathParam("patientInitial") String patientInitial,
			@PathParam("patientCounter") int patientCounter) {
		if (DPDoctorUtils.anyStringEmpty(doctorId, patientInitial, new Integer(patientCounter).toString())) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input. Dcotor Id, Patient Initial, Patient Counter Cannot Be Empty");
		}
		Boolean updateResponse = registrationService.updatePatientInitialAndCounter(doctorId, patientInitial, patientCounter);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(updateResponse);
		return response;
	}

	@Path(value = PathProxy.RegistrationUrls.GET_CLINIC_DETAILS)
	@GET
	public Response<Location> getClinicDetails(@PathParam("clinicId") String clinicId) {
		if (DPDoctorUtils.anyStringEmpty(clinicId)) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input. Clinic Id Cannot Be Empty");
		}
		Location clinicDetails = registrationService.getClinicDetails(clinicId);
		Response<Location> response = new Response<Location>();
		response.setData(clinicDetails);
		return response;
	}

	@Path(value = PathProxy.RegistrationUrls.UPDATE_CLINIC_PROFILE)
	@POST
	public Response<ClinicProfile> updateClinicProfile(ClinicProfile request) {
		if (request == null) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input. Request Sent Is Empty");
		}
		ClinicProfile clinicProfileUpdateResponse = registrationService.updateClinicProfile(request);
		Response<ClinicProfile> response = new Response<ClinicProfile>();
		response.setData(clinicProfileUpdateResponse);
		return response;
	}

	@Path(value = PathProxy.RegistrationUrls.UPDATE_CLINIC_ADDRESS)
	@POST
	public Response<ClinicAddress> updateClinicAddress(ClinicAddress request) {
		if (request == null) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input. Request Sent Is Empty");
		}
		ClinicAddress clinicAddressUpdateResponse = registrationService.updateClinicAddress(request);
		Response<ClinicAddress> response = new Response<ClinicAddress>();
		response.setData(clinicAddressUpdateResponse);
		return response;
	}

	@Path(value = PathProxy.RegistrationUrls.UPDATE_CLINIC_TIMING)
	@POST
	public Response<ClinicTiming> updateClinicTiming(ClinicTiming request) {
		if (request == null) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input. Request Sent Is Empty");
		}
		ClinicTiming clinicTimingUpdateResponse = registrationService.updateClinicTiming(request);
		Response<ClinicTiming> response = new Response<ClinicTiming>();
		response.setData(clinicTimingUpdateResponse);
		return response;
	}
}
