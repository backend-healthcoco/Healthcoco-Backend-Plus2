package com.dpdocter.webservices;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.dpdocter.beans.BloodGroup;
import com.dpdocter.beans.ClinicAddress;
import com.dpdocter.beans.ClinicImage;
import com.dpdocter.beans.ClinicLabProperties;
import com.dpdocter.beans.ClinicLogo;
import com.dpdocter.beans.ClinicProfile;
import com.dpdocter.beans.ClinicSpecialization;
import com.dpdocter.beans.ClinicTiming;
import com.dpdocter.beans.Feedback;
import com.dpdocter.beans.Location;
import com.dpdocter.beans.Profession;
import com.dpdocter.beans.Reference;
import com.dpdocter.beans.ReferenceDetail;
import com.dpdocter.beans.RegisteredPatientDetails;
import com.dpdocter.beans.Role;
import com.dpdocter.elasticsearch.document.ESPatientDocument;
import com.dpdocter.elasticsearch.document.ESReferenceDocument;
import com.dpdocter.elasticsearch.services.ESRegistrationService;
import com.dpdocter.enums.Resource;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.request.ClinicImageAddRequest;
import com.dpdocter.request.ClinicLogoAddRequest;
import com.dpdocter.request.ClinicProfileHandheld;
import com.dpdocter.request.DoctorRegisterRequest;
import com.dpdocter.request.PatientRegistrationRequest;
import com.dpdocter.response.ClinicDoctorResponse;
import com.dpdocter.response.PatientInitialAndCounter;
import com.dpdocter.response.PatientStatusResponse;
import com.dpdocter.response.RegisterDoctorResponse;
import com.dpdocter.services.RegistrationService;
import com.dpdocter.services.TransactionalManagementService;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @author veeraj
 */
@Component
@Path(PathProxy.REGISTRATION_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.REGISTRATION_BASE_URL, description = "Endpoint for register")
public class RegistrationApi {

	private static Logger logger = Logger.getLogger(RegistrationApi.class.getName());

	@Autowired
	private RegistrationService registrationService;

	@Autowired
	private ESRegistrationService esRegistrationService;

	@Autowired
	private TransactionalManagementService transnationalService;

	@Context
	private UriInfo uriInfo;

	@Value(value = "${image.path}")
	private String imagePath;

	@Value(value = "${register.first.name.validation}")
	private String firstNameValidaton;

	@Value(value = "${register.mobile.number.validation}")
	private String mobileNumberValidaton;

	@Value(value = "${invalid.input}")
	private String invalidInput;

	@Path(value = PathProxy.RegistrationUrls.PATIENT_REGISTER)
	@POST
	@ApiOperation(value = PathProxy.RegistrationUrls.PATIENT_REGISTER, notes = PathProxy.RegistrationUrls.PATIENT_REGISTER, response = Response.class)
	public Response<RegisteredPatientDetails> patientRegister(PatientRegistrationRequest request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getLocalPatientName())) {
			logger.warn(invalidInput);
			throw new BusinessException(ServiceError.InvalidInput, invalidInput);
		} else if (DPDoctorUtils.anyStringEmpty(request.getMobileNumber())) {
			logger.warn(mobileNumberValidaton);
			throw new BusinessException(ServiceError.InvalidInput, mobileNumberValidaton);
		} else if (request.getLocalPatientName().length() < 2) {
			logger.warn(firstNameValidaton);
			throw new BusinessException(ServiceError.InvalidInput, firstNameValidaton);
		}

		Response<RegisteredPatientDetails> response = new Response<RegisteredPatientDetails>();
		RegisteredPatientDetails registeredPatientDetails = null;

		if (request.getUserId() == null) {
			registrationService.checkPatientCount(request.getMobileNumber());
			registeredPatientDetails = registrationService.registerNewPatient(request);
			transnationalService.addResource(new ObjectId(registeredPatientDetails.getUserId()), Resource.PATIENT,
					false);
			esRegistrationService.addPatient(getESPatientDocument(registeredPatientDetails));

		} else {
			registeredPatientDetails = registrationService.registerExistingPatient(request);
			transnationalService.addResource(new ObjectId(registeredPatientDetails.getUserId()), Resource.PATIENT,
					false);
			esRegistrationService.addPatient(getESPatientDocument(registeredPatientDetails));
		}
		registeredPatientDetails.setImageUrl(getFinalImageURL(registeredPatientDetails.getImageUrl()));
		registeredPatientDetails.setThumbnailUrl(getFinalImageURL(registeredPatientDetails.getThumbnailUrl()));
		response.setData(registeredPatientDetails);
		return response;
	}

	@Path(value = PathProxy.RegistrationUrls.EDIT_PATIENT_PROFILE)
	@PUT
	@ApiOperation(value = PathProxy.RegistrationUrls.EDIT_PATIENT_PROFILE, notes = PathProxy.RegistrationUrls.EDIT_PATIENT_PROFILE, response = Response.class)
	public Response<RegisteredPatientDetails> editPatientRegister(PatientRegistrationRequest request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getUserId(), request.getLocalPatientName())) {
			logger.warn(invalidInput);
			throw new BusinessException(ServiceError.InvalidInput, invalidInput);
		} else if (DPDoctorUtils.anyStringEmpty(request.getMobileNumber())) {
			logger.warn(mobileNumberValidaton);
			throw new BusinessException(ServiceError.InvalidInput, mobileNumberValidaton);
		} else if (request.getLocalPatientName().length() < 2) {
			logger.warn(firstNameValidaton);
			throw new BusinessException(ServiceError.InvalidInput, firstNameValidaton);
		}
		Response<RegisteredPatientDetails> response = new Response<RegisteredPatientDetails>();
		RegisteredPatientDetails registeredPatientDetails = registrationService.registerExistingPatient(request);
		transnationalService.addResource(new ObjectId(registeredPatientDetails.getUserId()), Resource.PATIENT, false);
		esRegistrationService.addPatient(getESPatientDocument(registeredPatientDetails));

		registeredPatientDetails.setImageUrl(getFinalImageURL(registeredPatientDetails.getImageUrl()));
		registeredPatientDetails.setThumbnailUrl(getFinalImageURL(registeredPatientDetails.getThumbnailUrl()));
		response.setData(registeredPatientDetails);
		return response;
	}

	@Path(value = PathProxy.RegistrationUrls.EXISTING_PATIENTS_BY_PHONE_NUM)
	@GET
	@ApiOperation(value = PathProxy.RegistrationUrls.EXISTING_PATIENTS_BY_PHONE_NUM, notes = PathProxy.RegistrationUrls.EXISTING_PATIENTS_BY_PHONE_NUM, response = Response.class)
	public Response<RegisteredPatientDetails> getExistingPatients(@PathParam("mobileNumber") String mobileNumber,
			@PathParam("doctorId") String doctorId, @PathParam("locationId") String locationId,
			@PathParam("hospitalId") String hospitalId) {
		if (DPDoctorUtils.anyStringEmpty(mobileNumber)) {
			logger.warn(mobileNumberValidaton);
			throw new BusinessException(ServiceError.InvalidInput, mobileNumberValidaton);
		}

		Response<RegisteredPatientDetails> response = new Response<RegisteredPatientDetails>();

		List<RegisteredPatientDetails> users = registrationService.getUsersByPhoneNumber(mobileNumber, doctorId,
				locationId, hospitalId);
		if (users != null && !users.isEmpty()) {
			for (RegisteredPatientDetails user : users) {
				user.setImageUrl(getFinalImageURL(user.getImageUrl()));
				user.setThumbnailUrl(getFinalImageURL(user.getThumbnailUrl()));
			}
		}
		response.setDataList(users);
		return response;
	}

	@Path(value = PathProxy.RegistrationUrls.PATIENTS_BY_PHONE_NUM)
	@GET
	@ApiOperation(value = PathProxy.RegistrationUrls.PATIENTS_BY_PHONE_NUM, notes = PathProxy.RegistrationUrls.PATIENTS_BY_PHONE_NUM, response = Response.class)
	public Response<RegisteredPatientDetails> getExistingPatients(@PathParam("mobileNumber") String mobileNumber) {
		if (DPDoctorUtils.anyStringEmpty(mobileNumber)) {
			logger.warn(mobileNumberValidaton);
			throw new BusinessException(ServiceError.InvalidInput, mobileNumberValidaton);
		}

		Response<RegisteredPatientDetails> response = new Response<RegisteredPatientDetails>();

		List<RegisteredPatientDetails> users = registrationService.getPatientsByPhoneNumber(mobileNumber);
		if (users != null && !users.isEmpty()) {
			for (RegisteredPatientDetails user : users) {
				user.setImageUrl(getFinalImageURL(user.getImageUrl()));
				user.setThumbnailUrl(getFinalImageURL(user.getThumbnailUrl()));
			}
		}
		response.setDataList(users);
		return response;
	}

	@Path(value = PathProxy.RegistrationUrls.EXISTING_PATIENTS_BY_PHONE_NUM_COUNT)
	@GET
	@ApiOperation(value = PathProxy.RegistrationUrls.EXISTING_PATIENTS_BY_PHONE_NUM_COUNT, notes = PathProxy.RegistrationUrls.EXISTING_PATIENTS_BY_PHONE_NUM_COUNT, response = Response.class)
	public Response<Integer> getExistingPatientsCount(@PathParam("mobileNumber") String mobileNumber) {
		if (DPDoctorUtils.anyStringEmpty(mobileNumber)) {
			logger.warn(invalidInput);
			throw new BusinessException(ServiceError.InvalidInput, invalidInput);
		}
		Response<Integer> response = new Response<Integer>();
		Integer patientCountByMobNum = 0;
		List<RegisteredPatientDetails> users = registrationService.getUsersByPhoneNumber(mobileNumber, null, null,
				null);
		if (users != null) {
			patientCountByMobNum = users.size();
		}
		response.setData(patientCountByMobNum);
		return response;
	}

	@Path(value = PathProxy.RegistrationUrls.GET_PATIENT_PROFILE)
	@GET
	@ApiOperation(value = PathProxy.RegistrationUrls.GET_PATIENT_PROFILE, notes = PathProxy.RegistrationUrls.GET_PATIENT_PROFILE, response = Response.class)
	public Response<RegisteredPatientDetails> getPatientProfile(@PathParam("userId") String userId,
			@QueryParam("doctorId") String doctorId, @QueryParam("locationId") String locationId,
			@QueryParam("hospitalId") String hospitalId) {
		if (DPDoctorUtils.anyStringEmpty(userId)) {
			logger.warn(invalidInput);
			throw new BusinessException(ServiceError.InvalidInput, invalidInput);
		}

		Response<RegisteredPatientDetails> response = new Response<RegisteredPatientDetails>();

		RegisteredPatientDetails registeredPatientDetails = registrationService.getPatientProfileByUserId(userId,
				doctorId, locationId, hospitalId);
		if (registeredPatientDetails != null) {
			registeredPatientDetails.setImageUrl(getFinalImageURL(registeredPatientDetails.getImageUrl()));
			registeredPatientDetails.setThumbnailUrl(getFinalImageURL(registeredPatientDetails.getThumbnailUrl()));
		}
		response.setData(registeredPatientDetails);
		return response;
	}

	@Path(value = PathProxy.RegistrationUrls.ADD_REFERRENCE)
	@POST
	@ApiOperation(value = PathProxy.RegistrationUrls.ADD_REFERRENCE, notes = PathProxy.RegistrationUrls.ADD_REFERRENCE, response = Response.class)
	public Response<Reference> addReference(Reference request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getReference(), request.getDoctorId(),
				request.getLocationId(), request.getHospitalId())) {
			logger.warn(invalidInput);
			throw new BusinessException(ServiceError.InvalidInput, invalidInput);
		}
		Reference reference = registrationService.addEditReference(request);
		transnationalService.addResource(new ObjectId(reference.getId()), Resource.REFERENCE, false);
		ESReferenceDocument esReferenceDocument = new ESReferenceDocument();
		BeanUtil.map(reference, esReferenceDocument);
		esRegistrationService.addEditReference(esReferenceDocument);
		Response<Reference> response = new Response<Reference>();
		response.setData(reference);
		return response;
	}

	@Path(value = PathProxy.RegistrationUrls.DELETE_REFERRENCE)
	@DELETE
	@ApiOperation(value = PathProxy.RegistrationUrls.DELETE_REFERRENCE, notes = PathProxy.RegistrationUrls.DELETE_REFERRENCE, response = Response.class)
	public Response<Reference> deleteReferrence(@PathParam("referrenceId") String referrenceId,
			@DefaultValue("true") @QueryParam("discarded") Boolean discarded) {
		if (referrenceId == null) {
			logger.warn(invalidInput);
			throw new BusinessException(ServiceError.InvalidInput, invalidInput);
		}
		Reference reference = registrationService.deleteReferrence(referrenceId, discarded);
		transnationalService.addResource(new ObjectId(reference.getId()), Resource.REFERENCE, false);
		ESReferenceDocument esReferenceDocument = new ESReferenceDocument();
		BeanUtil.map(reference, esReferenceDocument);
		esRegistrationService.addEditReference(esReferenceDocument);
		Response<Reference> response = new Response<Reference>();
		response.setData(reference);
		return response;
	}

	@Path(value = PathProxy.RegistrationUrls.GET_REFERRENCES)
	@GET
	@ApiOperation(value = PathProxy.RegistrationUrls.GET_REFERRENCES, notes = PathProxy.RegistrationUrls.GET_REFERRENCES, response = Response.class)
	public Response<ReferenceDetail> getReferences(@PathParam("range") String range, @QueryParam("page") int page,
			@QueryParam("size") int size, @QueryParam(value = "doctorId") String doctorId,
			@QueryParam(value = "locationId") String locationId, @QueryParam(value = "hospitalId") String hospitalId,
			@DefaultValue("0") @QueryParam(value = "updatedTime") String updatedTime,
			@DefaultValue("true") @QueryParam(value = "discarded") Boolean discarded) {

		List<ReferenceDetail> references = registrationService.getReferences(range, page, size, doctorId, locationId,
				hospitalId, updatedTime, discarded);
		Response<ReferenceDetail> response = new Response<ReferenceDetail>();
		response.setDataList(references);
		return response;
	}

	// @Path(value = PathProxy.RegistrationUrls.PATIENT_ID_GENERATOR)
	// @GET
	// @ApiOperation(value = PathProxy.RegistrationUrls.PATIENT_ID_GENERATOR,
	// notes = PathProxy.RegistrationUrls.PATIENT_ID_GENERATOR, response =
	// Response.class)
	// public Response<String> patientIDGenerator(@PathParam("doctorId") String
	// doctorId, @PathParam("locationId") String locationId,
	// @PathParam("hospitalId") String hospitalId) {
	//
	// if (doctorId == null) {
	// throw new BusinessException(ServiceError.InvalidInput, "Invalid
	// Input.doctorId is null");
	// }
	// if (locationId == null) {
	// throw new BusinessException(ServiceError.InvalidInput, "Invalid
	// Input.locationId is null");
	// }
	// if (hospitalId == null) {
	// throw new BusinessException(ServiceError.InvalidInput, "Invalid
	// Input.hospitalId is null");
	// }
	//
	// Response<String> response = new Response<String>();
	// String generatedId = registrationService.patientIdGenerator(doctorId,
	// locationId, hospitalId);
	// response.setData(generatedId);
	// return response;
	// }

	@Path(value = PathProxy.RegistrationUrls.GET_PATIENT_INITIAL_COUNTER)
	@GET
	@ApiOperation(value = PathProxy.RegistrationUrls.GET_PATIENT_INITIAL_COUNTER, notes = PathProxy.RegistrationUrls.GET_PATIENT_INITIAL_COUNTER, response = Response.class)
	public Response<PatientInitialAndCounter> getPatientInitialAndCounter(@PathParam("doctorId") String doctorId,
			@PathParam("locationId") String locationId) {

		if (DPDoctorUtils.anyStringEmpty(doctorId, locationId)) {
			logger.warn(invalidInput);
			throw new BusinessException(ServiceError.InvalidInput, invalidInput);
		}
		Response<PatientInitialAndCounter> response = new Response<PatientInitialAndCounter>();
		PatientInitialAndCounter patientInitialAndCounter = registrationService.getPatientInitialAndCounter(doctorId, locationId);
		response.setData(patientInitialAndCounter);
		return response;
	}

	@Path(value = PathProxy.RegistrationUrls.UPDATE_PATIENT_ID_GENERATOR_LOGIC)
	@GET
	@ApiOperation(value = PathProxy.RegistrationUrls.UPDATE_PATIENT_ID_GENERATOR_LOGIC, notes = PathProxy.RegistrationUrls.UPDATE_PATIENT_ID_GENERATOR_LOGIC, response = Response.class)
	public Response<Boolean> updatePatientInitialAndCounter(@PathParam("doctorId") String doctorId,
			@PathParam("locationId") String locationId, @PathParam("patientInitial") String patientInitial,
			@PathParam("patientCounter") int patientCounter) {
		if (DPDoctorUtils.anyStringEmpty(doctorId, locationId, patientInitial,
				new Integer(patientCounter).toString())) {
			logger.warn(invalidInput);
			throw new BusinessException(ServiceError.InvalidInput, invalidInput);
		} else if (patientInitial.matches(".*\\d+.*")) {
			logger.warn("Invalid Patient Initial");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Patient Initial");
		}
		Boolean updateResponse = registrationService.updatePatientInitialAndCounter(doctorId, locationId, patientInitial, patientCounter);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(updateResponse);
		return response;
	}

	@Path(value = PathProxy.RegistrationUrls.GET_CLINIC_DETAILS)
	@GET
	@ApiOperation(value = PathProxy.RegistrationUrls.GET_CLINIC_DETAILS, notes = PathProxy.RegistrationUrls.GET_CLINIC_DETAILS, response = Response.class)
	public Response<Location> getClinicDetails(@PathParam("clinicId") String clinicId) {
		if (DPDoctorUtils.anyStringEmpty(clinicId)) {
			logger.warn(invalidInput);
			throw new BusinessException(ServiceError.InvalidInput, invalidInput);
		}
		Location clinicDetails = registrationService.getClinicDetails(clinicId);
		if (clinicDetails != null) {
			if (clinicDetails.getImages() != null && !clinicDetails.getImages().isEmpty()) {
				for (ClinicImage clinicImage : clinicDetails.getImages()) {
					if (clinicImage.getImageUrl() != null) {
						clinicImage.setImageUrl(getFinalImageURL(clinicImage.getImageUrl()));
					}
					if (clinicImage.getThumbnailUrl() != null) {
						clinicImage.setThumbnailUrl(getFinalImageURL(clinicImage.getThumbnailUrl()));
					}
				}
			}
			if (clinicDetails.getLogoUrl() != null)
				clinicDetails.setLogoUrl(getFinalImageURL(clinicDetails.getLogoUrl()));
			if (clinicDetails.getLogoThumbnailUrl() != null)
				clinicDetails.setLogoThumbnailUrl(getFinalImageURL(clinicDetails.getLogoThumbnailUrl()));
		}
		Response<Location> response = new Response<Location>();
		response.setData(clinicDetails);
		return response;
	}

	@Path(value = PathProxy.RegistrationUrls.UPDATE_CLINIC_PROFILE)
	@POST
	@ApiOperation(value = PathProxy.RegistrationUrls.UPDATE_CLINIC_PROFILE, notes = PathProxy.RegistrationUrls.UPDATE_CLINIC_PROFILE)
	public Response<ClinicProfile> updateClinicProfile(ClinicProfile request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getId())) {
			logger.warn(invalidInput);
			throw new BusinessException(ServiceError.InvalidInput, invalidInput);
		}
		ClinicProfile clinicProfileUpdateResponse = registrationService.updateClinicProfile(request);
		transnationalService.addResource(new ObjectId(clinicProfileUpdateResponse.getId()), Resource.LOCATION, false);

		if (clinicProfileUpdateResponse != null)
			transnationalService.checkLocation(new ObjectId(request.getId()));
		Response<ClinicProfile> response = new Response<ClinicProfile>();
		response.setData(clinicProfileUpdateResponse);
		return response;
	}

	@Path(value = PathProxy.RegistrationUrls.UPDATE_CLINIC_PROFILE_HANDHELD)
	@POST
	@ApiOperation(value = PathProxy.RegistrationUrls.UPDATE_CLINIC_PROFILE_HANDHELD, notes = PathProxy.RegistrationUrls.UPDATE_CLINIC_PROFILE_HANDHELD)
	public Response<ClinicProfile> updateClinicProfile(ClinicProfileHandheld request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getId())) {
			logger.warn(invalidInput);
			throw new BusinessException(ServiceError.InvalidInput, invalidInput);
		}
		ClinicProfile clinicProfileUpdateResponse = registrationService.updateClinicProfileHandheld(request);
		transnationalService.addResource(new ObjectId(clinicProfileUpdateResponse.getId()), Resource.LOCATION, false);
		if (clinicProfileUpdateResponse != null)
			transnationalService.checkLocation(new ObjectId(request.getId()));
		Response<ClinicProfile> response = new Response<ClinicProfile>();
		response.setData(clinicProfileUpdateResponse);
		return response;
	}

	@Path(value = PathProxy.RegistrationUrls.UPDATE_CLINIC_ADDRESS)
	@POST
	@ApiOperation(value = PathProxy.RegistrationUrls.UPDATE_CLINIC_ADDRESS, notes = PathProxy.RegistrationUrls.UPDATE_CLINIC_ADDRESS)
	public Response<ClinicAddress> updateClinicAddress(ClinicAddress request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getId())) {
			logger.warn(invalidInput);
			throw new BusinessException(ServiceError.InvalidInput, invalidInput);
		}
		ClinicAddress clinicAddressUpdateResponse = registrationService.updateClinicAddress(request);
		transnationalService.addResource(new ObjectId(clinicAddressUpdateResponse.getId()), Resource.LOCATION, false);
		if (clinicAddressUpdateResponse != null)
			transnationalService.checkLocation(new ObjectId(request.getId()));
		Response<ClinicAddress> response = new Response<ClinicAddress>();
		response.setData(clinicAddressUpdateResponse);
		return response;
	}

	@Path(value = PathProxy.RegistrationUrls.UPDATE_CLINIC_TIMING)
	@POST
	@ApiOperation(value = PathProxy.RegistrationUrls.UPDATE_CLINIC_TIMING, notes = PathProxy.RegistrationUrls.UPDATE_CLINIC_TIMING)
	public Response<ClinicTiming> updateClinicTiming(ClinicTiming request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getId())) {
			logger.warn(invalidInput);
			throw new BusinessException(ServiceError.InvalidInput, invalidInput);
		}
		ClinicTiming clinicTimingUpdateResponse = registrationService.updateClinicTiming(request);
		transnationalService.addResource(new ObjectId(request.getId()), Resource.LOCATION, false);
		if (clinicTimingUpdateResponse != null)
			transnationalService.checkLocation(new ObjectId(request.getId()));
		Response<ClinicTiming> response = new Response<ClinicTiming>();
		response.setData(clinicTimingUpdateResponse);
		return response;
	}

	@Path(value = PathProxy.RegistrationUrls.UPDATE_CLINIC_SPECIALIZATION)
	@POST
	@ApiOperation(value = PathProxy.RegistrationUrls.UPDATE_CLINIC_SPECIALIZATION, notes = PathProxy.RegistrationUrls.UPDATE_CLINIC_SPECIALIZATION)
	public Response<ClinicSpecialization> updateClinicSpecialization(ClinicSpecialization request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getId())) {
			logger.warn(invalidInput);
			throw new BusinessException(ServiceError.InvalidInput, invalidInput);
		}
		ClinicSpecialization clinicSpecializationUpdateResponse = registrationService
				.updateClinicSpecialization(request);
		transnationalService.addResource(new ObjectId(clinicSpecializationUpdateResponse.getId()), Resource.LOCATION,
				false);
		if (clinicSpecializationUpdateResponse != null)
			transnationalService.checkLocation(new ObjectId(request.getId()));
		Response<ClinicSpecialization> response = new Response<ClinicSpecialization>();
		response.setData(clinicSpecializationUpdateResponse);
		return response;
	}

	@Path(value = PathProxy.RegistrationUrls.UPDATE_CLINIC_LAB_PROPERTIES)
	@POST
	@ApiOperation(value = PathProxy.RegistrationUrls.UPDATE_CLINIC_LAB_PROPERTIES, notes = PathProxy.RegistrationUrls.UPDATE_CLINIC_LAB_PROPERTIES)
	public Response<ClinicLabProperties> updateLabProperties(ClinicLabProperties request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getId())) {
			logger.warn(invalidInput);
			throw new BusinessException(ServiceError.InvalidInput, invalidInput);
		}
		ClinicLabProperties clinicLabProperties = registrationService.updateLabProperties(request);
		if (clinicLabProperties != null) {
			transnationalService.addResource(new ObjectId(request.getId()), Resource.LOCATION, false);
			transnationalService.checkLocation(new ObjectId(request.getId()));
		}
		Response<ClinicLabProperties> response = new Response<ClinicLabProperties>();
		response.setData(clinicLabProperties);
		return response;
	}

	@Path(value = PathProxy.RegistrationUrls.CHANGE_CLINIC_LOGO)
	@POST
	@ApiOperation(value = PathProxy.RegistrationUrls.CHANGE_CLINIC_LOGO, notes = PathProxy.RegistrationUrls.CHANGE_CLINIC_LOGO)
	public Response<ClinicLogo> changeClinicLogo(ClinicLogoAddRequest request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getId())) {
			logger.warn(invalidInput);
			throw new BusinessException(ServiceError.InvalidInput, invalidInput);
		}
		ClinicLogo clinicLogoResponse = registrationService.changeClinicLogo(request);
		if (clinicLogoResponse != null) {
			if (clinicLogoResponse.getLogoURL() != null) {
				clinicLogoResponse.setLogoURL(getFinalImageURL(clinicLogoResponse.getLogoURL()));
			}
			if (clinicLogoResponse.getLogoThumbnailURL() != null) {
				clinicLogoResponse.setLogoThumbnailURL(getFinalImageURL(clinicLogoResponse.getLogoThumbnailURL()));
			}
		}
		transnationalService.addResource(new ObjectId(request.getId()), Resource.LOCATION, false);
		transnationalService.checkLocation(new ObjectId(request.getId()));
		Response<ClinicLogo> response = new Response<ClinicLogo>();
		response.setData(clinicLogoResponse);
		return response;
	}

	@Path(value = PathProxy.RegistrationUrls.ADD_CLINIC_IMAGE)
	@POST
	@ApiOperation(value = PathProxy.RegistrationUrls.ADD_CLINIC_IMAGE, notes = PathProxy.RegistrationUrls.ADD_CLINIC_IMAGE)
	public Response<ClinicImage> addClinicImage(ClinicImageAddRequest request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getId())) {
			logger.warn(invalidInput);
			throw new BusinessException(ServiceError.InvalidInput, invalidInput);
		} else if (request.getImages() == null) {
			logger.warn("Invalid Input. Request Image Is Null");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input. Request Image Is Null");
		} else if (request.getImages().size() > 5) {
			logger.warn("More than 5 images cannot be uploaded at a time");
			throw new BusinessException(ServiceError.NotAcceptable, "More than 5 images cannot be uploaded at a time");
		}
		List<ClinicImage> clinicImageResponse = registrationService.addClinicImage(request);
		if (clinicImageResponse != null && !clinicImageResponse.isEmpty()) {
			for (ClinicImage clinicalImage : clinicImageResponse) {
				if (clinicalImage.getImageUrl() != null) {
					clinicalImage.setImageUrl(getFinalImageURL(clinicalImage.getImageUrl()));
				}
				if (clinicalImage.getThumbnailUrl() != null) {
					clinicalImage.setThumbnailUrl(getFinalImageURL(clinicalImage.getThumbnailUrl()));
				}
			}
			transnationalService.addResource(new ObjectId(request.getId()), Resource.LOCATION, false);
			transnationalService.checkLocation(new ObjectId(request.getId()));
		}
		Response<ClinicImage> response = new Response<ClinicImage>();
		response.setDataList(clinicImageResponse);
		return response;
	}

	@Path(value = PathProxy.RegistrationUrls.DELETE_CLINIC_IMAGE)
	@DELETE
	@ApiOperation(value = PathProxy.RegistrationUrls.DELETE_CLINIC_IMAGE, notes = PathProxy.RegistrationUrls.DELETE_CLINIC_IMAGE)
	public Response<Boolean> deleteClinicImage(@PathParam(value = "locationId") String locationId,
			@PathParam(value = "counter") int counter) {
		if (DPDoctorUtils.anyStringEmpty(locationId)) {
			logger.warn("Invalid Input. Location Id is null");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input. Location Id is null");
		} else if (counter == 0) {
			logger.warn("Invalid Input. Counter cannot be 0");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input. Counter cannot be 0");
		}

		Boolean deleteImage = registrationService.deleteClinicImage(locationId, counter);
		transnationalService.addResource(new ObjectId(locationId), Resource.LOCATION, false);
		transnationalService.checkLocation(new ObjectId(locationId));
		Response<Boolean> response = new Response<Boolean>();
		response.setData(deleteImage);
		return response;
	}

	@Path(value = PathProxy.RegistrationUrls.GET_BLOOD_GROUP)
	@GET
	@ApiOperation(value = PathProxy.RegistrationUrls.GET_BLOOD_GROUP, notes = PathProxy.RegistrationUrls.GET_BLOOD_GROUP)
	public Response<BloodGroup> getBloodGroup() {
		List<BloodGroup> bloodGroupResponse = registrationService.getBloodGroup();
		Response<BloodGroup> response = new Response<BloodGroup>();
		response.setDataList(bloodGroupResponse);
		return response;
	}

	// @Path(value = PathProxy.RegistrationUrls.ADD_PROFESSION)
	// @POST
	// @ApiOperation(value = PathProxy.RegistrationUrls.ADD_PROFESSION, notes =
	// PathProxy.RegistrationUrls.ADD_PROFESSION)
	// public Response<Profession> addProfession(Profession request) {
	// if (request == null) {
	// throw new BusinessException(ServiceError.InvalidInput, "Invalid Input.
	// Request Sent Is Empty");
	// }
	// Profession professionResponse =
	// registrationService.addProfession(request);
	// Response<Profession> response = new Response<Profession>();
	// response.setData(professionResponse);
	// return response;
	// }

	@Path(value = PathProxy.RegistrationUrls.GET_PROFESSION)
	@GET
	@ApiOperation(value = PathProxy.RegistrationUrls.GET_PROFESSION, notes = PathProxy.RegistrationUrls.GET_PROFESSION)
	public Response<Profession> getProfession(@QueryParam("page") int page, @QueryParam("size") int size,
			@DefaultValue("0") @QueryParam(value = "updatedTime") String updatedTime) {

		List<Profession> professionResponse = registrationService.getProfession(page, size, updatedTime);
		Response<Profession> response = new Response<Profession>();
		response.setDataList(professionResponse);
		return response;
	}

	@Path(value = PathProxy.RegistrationUrls.USER_REGISTER_IN_CLINIC)
	@POST
	@ApiOperation(value = PathProxy.RegistrationUrls.USER_REGISTER_IN_CLINIC, notes = PathProxy.RegistrationUrls.USER_REGISTER_IN_CLINIC)
	public Response<RegisterDoctorResponse> userRegister(DoctorRegisterRequest request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getEmailAddress(), request.getFirstName(),
				request.getLocationId(), request.getHospitalId())) {
			logger.warn(invalidInput);
			throw new BusinessException(ServiceError.InvalidInput, invalidInput);
		}
		RegisterDoctorResponse doctorResponse = null;
		if (!registrationService.checktDoctorExistByEmailAddress(request.getEmailAddress()))
			doctorResponse = registrationService.registerNewUser(request);
		else
			doctorResponse = registrationService.registerExisitingUser(request);

		transnationalService.addResource(new ObjectId(doctorResponse.getUserId()), Resource.DOCTOR, false);
		if (doctorResponse != null)
			esRegistrationService.addDoctor(registrationService.getESDoctorDocument(doctorResponse));
		Response<RegisterDoctorResponse> response = new Response<RegisterDoctorResponse>();
		response.setData(doctorResponse);
		return response;
	}

	@Path(value = PathProxy.RegistrationUrls.EDIT_USER_IN_CLINIC)
	@PUT
	@ApiOperation(value = PathProxy.RegistrationUrls.EDIT_USER_IN_CLINIC, notes = PathProxy.RegistrationUrls.EDIT_USER_IN_CLINIC)
	public Response<RegisterDoctorResponse> editUserInClinic(@PathParam("userId") String userId,
			@PathParam("locationId") String locationId, DoctorRegisterRequest request) {
		if (request == null
				|| DPDoctorUtils.anyStringEmpty(userId, locationId, request.getFirstName(), request.getHospitalId())) {
			logger.warn(invalidInput);
			throw new BusinessException(ServiceError.InvalidInput, invalidInput);
		}
		request.setUserId(userId);
		request.setLocationId(locationId);
		RegisterDoctorResponse doctorResponse = registrationService.editUserInClinic(request);

		transnationalService.checkDoctor(new ObjectId(request.getUserId()), null);
		Response<RegisterDoctorResponse> response = new Response<RegisterDoctorResponse>();
		response.setData(doctorResponse);
		return response;
	}

	private ESPatientDocument getESPatientDocument(RegisteredPatientDetails patient) {
		ESPatientDocument esPatientDocument = null;
		try {
			esPatientDocument = new ESPatientDocument();
			if (patient.getAddress() != null) {
				BeanUtil.map(patient.getAddress(), esPatientDocument);
			}
			if (patient.getPatient() != null) {
				BeanUtil.map(patient.getPatient(), esPatientDocument);
			}
			BeanUtil.map(patient, esPatientDocument);
			if (patient.getBackendPatientId() != null)esPatientDocument.setId(patient.getBackendPatientId());
			if (patient.getReferredBy() != null)esPatientDocument.setReferredBy(patient.getReferredBy().getId());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return esPatientDocument;
	}

	private String getFinalImageURL(String imageURL) {
		if (imageURL != null) {
			return imagePath + imageURL;
		} else
			return null;
	}

	@Path(value = PathProxy.RegistrationUrls.ADD_EDIT_ROLE)
	@POST
	@ApiOperation(value = PathProxy.RegistrationUrls.ADD_EDIT_ROLE, notes = PathProxy.RegistrationUrls.ADD_EDIT_ROLE)
	public Response<Role> addRole(Role request) {
		if (request == null
				|| DPDoctorUtils.anyStringEmpty(request.getRole(), request.getLocationId(), request.getHospitalId())
				|| request.getAccessModules() == null || request.getAccessModules().isEmpty()) {
			logger.warn(invalidInput);
			throw new BusinessException(ServiceError.InvalidInput, invalidInput);
		}
		Role professionResponse = registrationService.addRole(request);
		Response<Role> response = new Response<Role>();
		response.setData(professionResponse);
		return response;
	}

	@Path(value = PathProxy.RegistrationUrls.GET_ROLE)
	@GET
	@ApiOperation(value = PathProxy.RegistrationUrls.GET_ROLE, notes = PathProxy.RegistrationUrls.GET_ROLE)
	public Response<Role> getRole(@PathParam(value = "range") String range,
			@PathParam(value = "locationId") String locationId, @PathParam(value = "hospitalId") String hospitalId,
			@QueryParam(value = "page") int page, @QueryParam(value = "size") int size,
			@DefaultValue("0") @QueryParam(value = "updatedTime") String updatedTime,
			@QueryParam(value = "role") String role) {
		if (DPDoctorUtils.anyStringEmpty(range, locationId, hospitalId)) {
			logger.warn(invalidInput);
			throw new BusinessException(ServiceError.InvalidInput, invalidInput);
		}
		List<Role> professionResponse = registrationService.getRole(range, page, size, locationId, hospitalId,
				updatedTime, role);
		Response<Role> response = new Response<Role>();
		response.setDataList(professionResponse);
		return response;
	}

	@Path(value = PathProxy.RegistrationUrls.GET_USERS)
	@GET
	@ApiOperation(value = PathProxy.RegistrationUrls.GET_USERS, notes = PathProxy.RegistrationUrls.GET_USERS)
	public Response<ClinicDoctorResponse> getUsers(@PathParam(value = "locationId") String locationId,
			@PathParam(value = "hospitalId") String hospitalId, @QueryParam(value = "page") int page,
			@QueryParam(value = "size") int size,
			@DefaultValue("0") @QueryParam(value = "updatedTime") String updatedTime,
			@QueryParam(value = "role") String role) {
		if (DPDoctorUtils.anyStringEmpty(locationId, hospitalId)) {
			logger.warn(invalidInput);
			throw new BusinessException(ServiceError.InvalidInput, invalidInput);
		}
		List<ClinicDoctorResponse> professionResponse = registrationService.getUsers(page, size, locationId, hospitalId,
				updatedTime, role);
		Response<ClinicDoctorResponse> response = new Response<ClinicDoctorResponse>();
		response.setDataList(professionResponse);
		return response;
	}

	@Path(value = PathProxy.RegistrationUrls.DELETE_ROLE)
	@DELETE
	@ApiOperation(value = PathProxy.RegistrationUrls.DELETE_ROLE, notes = PathProxy.RegistrationUrls.DELETE_ROLE)
	public Response<Role> deleteRole(@PathParam(value = "roleId") String roleId,
			@DefaultValue("true") @QueryParam("discarded") Boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(roleId)) {
			logger.warn(invalidInput);
			throw new BusinessException(ServiceError.InvalidInput, invalidInput);
		}
		Role role = registrationService.deleteRole(roleId, discarded);
		Response<Role> response = new Response<Role>();
		response.setData(role);
		return response;
	}

	@Path(value = PathProxy.RegistrationUrls.ACTIVATE_DEACTIVATE_USER)
	@DELETE
	@ApiOperation(value = PathProxy.RegistrationUrls.ACTIVATE_DEACTIVATE_USER, notes = PathProxy.RegistrationUrls.ACTIVATE_DEACTIVATE_USER)
	public Response<Boolean> activateDeactivateUser(@PathParam(value = "userId") String userId,
			@PathParam(value = "locationId") String locationId,
			@DefaultValue("true") @QueryParam("isActivate") Boolean isActivate) {
		if (DPDoctorUtils.anyStringEmpty(userId, locationId)) {
			logger.warn(invalidInput);
			throw new BusinessException(ServiceError.InvalidInput, invalidInput);
		}
		registrationService.activateDeactivateUser(userId, locationId, isActivate);
		transnationalService.checkDoctor(new ObjectId(userId), null);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(true);
		return response;
	}

	@Path(value = PathProxy.RegistrationUrls.ADD_FEEDBACK)
	@POST
	@ApiOperation(value = PathProxy.RegistrationUrls.ADD_FEEDBACK, notes = PathProxy.RegistrationUrls.ADD_FEEDBACK)
	public Response<Feedback> addFeedback(Feedback request) {
		if (request == null) {
			logger.warn(invalidInput);
			throw new BusinessException(ServiceError.InvalidInput, invalidInput);
		}
		Feedback feedback = registrationService.addFeedback(request);
		if (!DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId()))
			transnationalService.checkDoctor(new ObjectId(request.getDoctorId()),
					new ObjectId(feedback.getLocationId()));
		Response<Feedback> response = new Response<Feedback>();
		response.setData(feedback);
		return response;
	}

	@Path(value = PathProxy.RegistrationUrls.VISIBLE_FEEDBACK)
	@GET
	@ApiOperation(value = PathProxy.RegistrationUrls.VISIBLE_FEEDBACK, notes = PathProxy.RegistrationUrls.VISIBLE_FEEDBACK)
	public Response<Feedback> visibleFeedback(@PathParam("feedbackId") String feedbackId,
			@DefaultValue("true") @QueryParam("isVisible") Boolean isVisible) {

		Feedback feedback = registrationService.visibleFeedback(feedbackId, isVisible);
		transnationalService.checkDoctor(new ObjectId(feedback.getDoctorId()), new ObjectId(feedback.getLocationId()));
		Response<Feedback> response = new Response<Feedback>();
		response.setData(feedback);
		return response;
	}

	@Path(value = PathProxy.RegistrationUrls.GET_PATIENT_STATUS)
	@GET
	@ApiOperation(value = PathProxy.RegistrationUrls.GET_PATIENT_STATUS, notes = PathProxy.RegistrationUrls.GET_PATIENT_STATUS)
	public Response<PatientStatusResponse> getPatientStatus(@PathParam("patientId") String patientId,
			@PathParam("doctorId") String doctorId, @PathParam("locationId") String locationId,
			@PathParam("hospitalId") String hospitalId) {

		if (DPDoctorUtils.anyStringEmpty(patientId, doctorId, locationId, hospitalId)) {
			logger.warn(invalidInput);
			throw new BusinessException(ServiceError.InvalidInput, invalidInput);
		}
		Response<PatientStatusResponse> response = new Response<PatientStatusResponse>();

		PatientStatusResponse patientStatusResponse = registrationService.getPatientStatus(patientId, doctorId,
				locationId, hospitalId);
		response.setData(patientStatusResponse);
		return response;
	}

	@Path(value = PathProxy.RegistrationUrls.GET_DOCTOR_FEEDBACK)
	@GET
	@ApiOperation(value = PathProxy.RegistrationUrls.GET_DOCTOR_FEEDBACK, notes = PathProxy.RegistrationUrls.GET_DOCTOR_FEEDBACK)
	public Response<Feedback> getFeedback(@QueryParam("page") int page, @QueryParam("size") int size,
			@QueryParam("doctorId") String doctorId, @QueryParam("locationId") String locationId,
			@QueryParam("hospitalId") String hospitalId,
			@DefaultValue("0") @QueryParam(value = "updatedTime") String updatedTime,
			@QueryParam(value = "type") String type) {
		if (DPDoctorUtils.anyStringEmpty(doctorId)) {
			logger.warn(invalidInput);
			throw new BusinessException(ServiceError.InvalidInput, invalidInput);
		}
		List<Feedback> feedbacks = registrationService.getFeedback(page, size, doctorId, locationId, hospitalId,
				updatedTime, type);
		Response<Feedback> response = new Response<Feedback>();
		response.setDataList(feedbacks);
		return response;
	}

	@Path(value = PathProxy.RegistrationUrls.CHECK_PATIENT_NUMBER)
	@GET
	@ApiOperation(value = PathProxy.RegistrationUrls.CHECK_PATIENT_NUMBER, notes = PathProxy.RegistrationUrls.CHECK_PATIENT_NUMBER)
	public Response<Boolean> checkPatientNumber(@PathParam("oldMobileNumber") String oldMobileNumber,
			@PathParam("newMobileNumber") String newMobileNumber) {
		if (DPDoctorUtils.anyStringEmpty(oldMobileNumber, newMobileNumber)) {
			logger.warn(invalidInput);
			throw new BusinessException(ServiceError.InvalidInput, invalidInput);
		}

		Boolean checkPatientNumberResponse = registrationService.checkPatientNumber(oldMobileNumber, newMobileNumber);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(checkPatientNumberResponse);
		return response;
	}

	@Path(value = PathProxy.RegistrationUrls.CHANGE_PATIENT_NUMBER)
	@GET
	@ApiOperation(value = PathProxy.RegistrationUrls.CHANGE_PATIENT_NUMBER, notes = PathProxy.RegistrationUrls.CHANGE_PATIENT_NUMBER)
	public Response<Boolean> changePatientNumber(@PathParam("oldMobileNumber") String oldMobileNumber,
			@PathParam("newMobileNumber") String newMobileNumber, @PathParam("otpNumber") String otpNumber) {
		if (DPDoctorUtils.anyStringEmpty(oldMobileNumber, newMobileNumber)) {
			logger.warn(invalidInput);
			throw new BusinessException(ServiceError.InvalidInput, invalidInput);
		}
		Boolean changePatientNumberResponse = registrationService.changePatientNumber(oldMobileNumber, newMobileNumber,
				otpNumber);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(changePatientNumberResponse);
		return response;
	}

	@Path(value = PathProxy.RegistrationUrls.REGISTER_PATIENTS_IN_BULK)
	@GET
	public Response<Boolean> registerPatients(@PathParam("doctorId") String doctorId, @PathParam("locationId") String locationId,
			@PathParam("hospitalId") String hospitalId) {
		if (DPDoctorUtils.anyStringEmpty(doctorId, locationId, hospitalId)) {
			logger.warn(invalidInput);
			throw new BusinessException(ServiceError.InvalidInput, invalidInput);
		}
		Boolean changePatientNumberResponse = registrationService.registerPatients(doctorId, locationId, hospitalId);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(changePatientNumberResponse);
		return response;
	}

}
