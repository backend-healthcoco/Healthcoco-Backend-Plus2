package com.dpdocter.webservices;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dpdocter.beans.BloodGroup;
import com.dpdocter.beans.ClinicAddress;
import com.dpdocter.beans.ClinicImage;
import com.dpdocter.beans.ClinicLogo;
import com.dpdocter.beans.ClinicProfile;
import com.dpdocter.beans.ClinicSpecialization;
import com.dpdocter.beans.ClinicTiming;
import com.dpdocter.beans.Location;
import com.dpdocter.beans.Profession;
import com.dpdocter.beans.Reference;
import com.dpdocter.beans.RegisteredPatientDetails;
import com.dpdocter.beans.User;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.request.ClinicImageAddRequest;
import com.dpdocter.request.ClinicLogoAddRequest;
import com.dpdocter.request.PatientRegistrationRequest;
import com.dpdocter.response.PatientInitialAndCounter;
import com.dpdocter.response.ReferenceResponse;
import com.dpdocter.services.RegistrationService;
import com.dpdocter.solr.document.SolrPatientDocument;
import com.dpdocter.solr.services.SolrRegistrationService;
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

    @Autowired
    private SolrRegistrationService solrRegistrationService;

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
	    solrRegistrationService.addPatient(getSolrPatientDocument(registeredPatientDetails));
	} else {
	    registeredPatientDetails = registrationService.registerExistingPatient(request);
	    solrRegistrationService.editPatient(getSolrPatientDocument(registeredPatientDetails));
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
    @DELETE
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

    @Path(value = PathProxy.RegistrationUrls.GET_PATIENT_INITIAL_COUNTER)
    @GET
    public Response<PatientInitialAndCounter> getPatientInitialAndCounter(@PathParam("doctorId") String doctorId, @PathParam("locationId") String locationId) {

	if (DPDoctorUtils.anyStringEmpty(doctorId, locationId)) {
	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input. Doctor Id or Location Id cannot be null");
	}
	Response<PatientInitialAndCounter> response = new Response<PatientInitialAndCounter>();
	PatientInitialAndCounter patientInitialAndCounter = registrationService.getPatientInitialAndCounter(doctorId, locationId);
	response.setData(patientInitialAndCounter);
	return response;
    }

    @Path(value = PathProxy.RegistrationUrls.UPDATE_PATIENT_ID_GENERATOR_LOGIC)
    @GET
    public Response<Boolean> updatePatientInitialAndCounter(@PathParam("doctorId") String doctorId, @PathParam("locationId") String locationId,
	    @PathParam("patientInitial") String patientInitial, @PathParam("patientCounter") int patientCounter) {
	if (DPDoctorUtils.anyStringEmpty(doctorId, locationId, patientInitial, new Integer(patientCounter).toString())) {
	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input. Dcotor Id, ,Location Id, Patient Initial, Patient Counter Cannot Be Empty");
	}
	Boolean updateResponse = registrationService.updatePatientInitialAndCounter(doctorId, locationId, patientInitial, patientCounter);
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

    @Path(value = PathProxy.RegistrationUrls.UPDATE_CLINIC_SPECIALIZATION)
    @POST
    public Response<ClinicSpecialization> updateClinicSpecialization(ClinicSpecialization request) {
	if (request == null) {
	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input. Request Sent Is Empty");
	}
	ClinicSpecialization clinicSpecializationUpdateResponse = registrationService.updateClinicSpecialization(request);
	Response<ClinicSpecialization> response = new Response<ClinicSpecialization>();
	response.setData(clinicSpecializationUpdateResponse);
	return response;
    }

    @Path(value = PathProxy.RegistrationUrls.CHANGE_CLINIC_LOGO)
    @POST
    public Response<ClinicLogo> changeClinicLogo(ClinicLogoAddRequest request) {
	if (request == null) {
	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input. Request Sent Is Empty");
	}
	ClinicLogo clinicLogoResponse = registrationService.changeClinicLogo(request);
	Response<ClinicLogo> response = new Response<ClinicLogo>();
	response.setData(clinicLogoResponse);
	return response;
    }

    @Path(value = PathProxy.RegistrationUrls.ADD_CLINIC_IMAGE)
    @POST
    public Response<ClinicImage> addClinicImage(ClinicImageAddRequest request) {
	if (request == null) {
	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input. Request Sent Is Empty");
	} else if (request.getImages() == null) {
	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input. Request Image Is Null");
	} else if (request.getImages().size() > 5) {
	    throw new BusinessException(ServiceError.Unknown, "More than 5 images cannot be uploaded at a time");
	}
	List<ClinicImage> clinicImageResponse = registrationService.addClinicImage(request);
	Response<ClinicImage> response = new Response<ClinicImage>();
	response.setDataList(clinicImageResponse);
	return response;
    }

    @Path(value = PathProxy.RegistrationUrls.DELETE_CLINIC_IMAGE)
    @DELETE
    public Response<Boolean> deleteClinicImage(@PathParam(value = "locationId") String locationId, @PathParam(value = "counter") int counter) {
	if (DPDoctorUtils.anyStringEmpty(locationId)) {
	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input. Location Id is null");
	} else if (counter == 0) {
	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input. Counter cannot be 0");
	}

	Boolean deleteImage = registrationService.deleteClinicImage(locationId, counter);
	Response<Boolean> response = new Response<Boolean>();
	response.setData(deleteImage);
	return response;
    }

    @Path(value = PathProxy.RegistrationUrls.ADD_BLOOD_GROUP)
    @POST
    public Response<BloodGroup> addBloodGroup(BloodGroup request) {
	if (request == null) {
	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input. Request Sent Is Empty");
	}
	BloodGroup bloodGroupResponse = registrationService.addBloodGroup(request);
	Response<BloodGroup> response = new Response<BloodGroup>();
	response.setData(bloodGroupResponse);
	return response;
    }

    @Path(value = PathProxy.RegistrationUrls.GET_BLOOD_GROUP)
    @GET
    public Response<BloodGroup> getBloodGroup() {
	List<BloodGroup> bloodGroupResponse = registrationService.getBloodGroup();
	Response<BloodGroup> response = new Response<BloodGroup>();
	response.setDataList(bloodGroupResponse);
	return response;
    }

    @Path(value = PathProxy.RegistrationUrls.ADD_PROFESSION)
    @POST
    public Response<Profession> addProfession(Profession request) {
	if (request == null) {
	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input. Request Sent Is Empty");
	}
	Profession professionResponse = registrationService.addProfession(request);
	Response<Profession> response = new Response<Profession>();
	response.setData(professionResponse);
	return response;
    }

    @Path(value = PathProxy.RegistrationUrls.GET_PROFESSION)
    @GET
    public Response<Profession> getProfession() {

	List<Profession> professionResponse = registrationService.getProfession();
	Response<Profession> response = new Response<Profession>();
	response.setDataList(professionResponse);
	return response;
    }

    private SolrPatientDocument getSolrPatientDocument(RegisteredPatientDetails patient) {
	SolrPatientDocument solrPatientDocument = null;
	try {
	    solrPatientDocument = new SolrPatientDocument();
	    solrPatientDocument.setDays(patient.getDob().getDays());
	    solrPatientDocument.setMonths(patient.getDob().getMonths());
	    solrPatientDocument.setYears(patient.getDob().getYears());

	    if (patient.getAddress() != null) {
		BeanUtil.map(patient.getAddress(), solrPatientDocument);
	    }
	    if (patient.getPatient() != null) {
		BeanUtil.map(patient.getPatient(), solrPatientDocument);
	    }
	    BeanUtil.map(patient, solrPatientDocument);
	    solrPatientDocument.setId(patient.getPatient().getPatientId());

	} catch (Exception e) {
	    e.printStackTrace();
	}
	return solrPatientDocument;
    }
}
