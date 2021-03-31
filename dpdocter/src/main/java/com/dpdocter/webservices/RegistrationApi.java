package com.dpdocter.webservices;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dpdocter.beans.BloodGroup;
import com.dpdocter.beans.ClinicAddress;
import com.dpdocter.beans.ClinicImage;
import com.dpdocter.beans.ClinicLabProperties;
import com.dpdocter.beans.ClinicLogo;
import com.dpdocter.beans.ClinicProfile;
import com.dpdocter.beans.ClinicSpecialization;
import com.dpdocter.beans.ClinicTiming;
import com.dpdocter.beans.ConsentForm;
import com.dpdocter.beans.DoctorCalendarView;
import com.dpdocter.beans.Feedback;
import com.dpdocter.beans.FormContent;
import com.dpdocter.beans.Location;
import com.dpdocter.beans.PatientShortCard;
import com.dpdocter.beans.Profession;
import com.dpdocter.beans.Reference;
import com.dpdocter.beans.ReferenceDetail;
import com.dpdocter.beans.RegisteredPatientDetails;
import com.dpdocter.beans.Role;
import com.dpdocter.beans.Suggestion;
import com.dpdocter.beans.UserAddress;
import com.dpdocter.beans.UserReminders;
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
import com.dpdocter.response.UserAddressResponse;
import com.dpdocter.services.HistoryServices;
import com.dpdocter.services.RegistrationService;
import com.dpdocter.services.SuggestionService;
import com.dpdocter.services.TransactionalManagementService;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataParam;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @author parag
 */
@RestController
(PathProxy.REGISTRATION_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.REGISTRATION_BASE_URL, description = "Endpoint for register")
public class RegistrationApi {

	private static Logger logger = LogManager.getLogger(RegistrationApi.class.getName());

	@Autowired
	private RegistrationService registrationService;

	@Autowired
	private SuggestionService suggestionService;

	@Autowired
	private ESRegistrationService esRegistrationService;

	@Autowired
	private TransactionalManagementService transnationalService;

	@Autowired
	private HistoryServices historyServices;

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

	
	@PostMapping(value = PathProxy.RegistrationUrls.PATIENT_REGISTER)
	@ApiOperation(value = PathProxy.RegistrationUrls.PATIENT_REGISTER, notes = PathProxy.RegistrationUrls.PATIENT_REGISTER, response = Response.class)
	public Response<RegisteredPatientDetails> patientRegister(PatientRegistrationRequest request,
			@MatrixParam(value = "infoType") List<String> infoType) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getLocalPatientName())) {
			logger.warn(invalidInput);
			throw new BusinessException(ServiceError.InvalidInput, invalidInput);
		} else if (request.getLocalPatientName().length() < 2) {
			logger.warn(firstNameValidaton);
			throw new BusinessException(ServiceError.InvalidInput, firstNameValidaton);
		}

		Response<RegisteredPatientDetails> response = new Response<RegisteredPatientDetails>();
		RegisteredPatientDetails registeredPatientDetails = null;

		if (request.getUserId() == null) {
			if (!DPDoctorUtils.anyStringEmpty(request.getMobileNumber()))
				registrationService.checkPatientCount(request.getMobileNumber());
			registeredPatientDetails = registrationService.registerNewPatient(request);
			transnationalService.addResource(new ObjectId(registeredPatientDetails.getUserId()), Resource.PATIENT,
					false);
			esRegistrationService.addPatient(registrationService.getESPatientDocument(registeredPatientDetails));

		} else {
			registeredPatientDetails = registrationService.registerExistingPatient(request, infoType);
			transnationalService.addResource(new ObjectId(registeredPatientDetails.getUserId()), Resource.PATIENT,
					false);
			esRegistrationService.addPatient(registrationService.getESPatientDocument(registeredPatientDetails));
		}

		if (request.getFamilyMedicalHistoryHandler() != null) {
			request.getFamilyMedicalHistoryHandler().setPatientId(registeredPatientDetails.getPatient().getPatientId());
			historyServices.handleFamilyHistory(request.getFamilyMedicalHistoryHandler());
		}

		if (request.getPastMedicalHistoryHandler() != null) {
			request.getPastMedicalHistoryHandler().setPatientId(registeredPatientDetails.getPatient().getPatientId());
			historyServices.handleMedicalHistory(request.getPastMedicalHistoryHandler());
		}

		if (request.getPersonalHistoryAddRequest() != null) {
			request.getPersonalHistoryAddRequest().setPatientId(registeredPatientDetails.getPatient().getPatientId());

			historyServices.assignPersonalHistory(request.getPersonalHistoryAddRequest());
		}

		registeredPatientDetails.setImageUrl(getFinalImageURL(registeredPatientDetails.getImageUrl()));
		registeredPatientDetails.setThumbnailUrl(getFinalImageURL(registeredPatientDetails.getThumbnailUrl()));
		response.setData(registeredPatientDetails);

		return response;
	}

	
	@PutMapping(value = PathProxy.RegistrationUrls.EDIT_PATIENT_PROFILE)
	@ApiOperation(value = PathProxy.RegistrationUrls.EDIT_PATIENT_PROFILE, notes = PathProxy.RegistrationUrls.EDIT_PATIENT_PROFILE, response = Response.class)
	public Response<RegisteredPatientDetails> editPatientRegister(PatientRegistrationRequest request,
			@MatrixParam(value = "infoType") List<String> infoType) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getUserId())) {
			logger.warn(invalidInput);
			throw new BusinessException(ServiceError.InvalidInput, invalidInput);
		} else if (!DPDoctorUtils.allStringsEmpty(request.getHospitalId(), request.getDoctorId(),
				request.getLocationId())) {
			if (DPDoctorUtils.anyStringEmpty(request.getLocalPatientName())) {
				logger.warn(invalidInput);
				throw new BusinessException(ServiceError.InvalidInput, invalidInput);
			} else if (request.getLocalPatientName().length() < 2) {
				logger.warn(firstNameValidaton);
				throw new BusinessException(ServiceError.InvalidInput, firstNameValidaton);
			}
		}
		Response<RegisteredPatientDetails> response = new Response<RegisteredPatientDetails>();
		RegisteredPatientDetails registeredPatientDetails = registrationService.registerExistingPatient(request,
				infoType);
		transnationalService.addResource(new ObjectId(registeredPatientDetails.getUserId()), Resource.PATIENT, false);
		esRegistrationService.addPatient(registrationService.getESPatientDocument(registeredPatientDetails));

		registeredPatientDetails.setImageUrl(getFinalImageURL(registeredPatientDetails.getImageUrl()));
		registeredPatientDetails.setThumbnailUrl(getFinalImageURL(registeredPatientDetails.getThumbnailUrl()));
		response.setData(registeredPatientDetails);
		return response;
	}

	
	@GetMapping(value = PathProxy.RegistrationUrls.EXISTING_PATIENTS_BY_PHONE_NUM)
	@ApiOperation(value = PathProxy.RegistrationUrls.EXISTING_PATIENTS_BY_PHONE_NUM, notes = PathProxy.RegistrationUrls.EXISTING_PATIENTS_BY_PHONE_NUM, response = Response.class)
	public Response<RegisteredPatientDetails> getExistingPatients(@PathVariable("mobileNumber") String mobileNumber,
			@PathVariable("doctorId") String doctorId, @PathVariable("locationId") String locationId,
			@PathVariable("hospitalId") String hospitalId, @RequestParam("role") String role,
			@DefaultValue("false") @RequestParam("forChangeNumber") Boolean forChangeNumber) {
		if (DPDoctorUtils.anyStringEmpty(mobileNumber)) {
			logger.warn(mobileNumberValidaton);
			throw new BusinessException(ServiceError.InvalidInput, mobileNumberValidaton);
		}

		Response<RegisteredPatientDetails> response = new Response<RegisteredPatientDetails>();

		List<RegisteredPatientDetails> users = registrationService.getUsersByPhoneNumber(mobileNumber, doctorId,
				locationId, hospitalId, role, forChangeNumber);
		if (users != null && !users.isEmpty()) {
			for (RegisteredPatientDetails user : users) {
				user.setImageUrl(getFinalImageURL(user.getImageUrl()));
				user.setThumbnailUrl(getFinalImageURL(user.getThumbnailUrl()));
			}
		}
		response.setDataList(users);
		return response;
	}

	
	@GetMapping(value = PathProxy.RegistrationUrls.PATIENTS_BY_PHONE_NUM)
	@ApiOperation(value = PathProxy.RegistrationUrls.PATIENTS_BY_PHONE_NUM, notes = PathProxy.RegistrationUrls.PATIENTS_BY_PHONE_NUM, response = Response.class)
	public Response<Object> getExistingPatients(@PathVariable("mobileNumber") String mobileNumber,
			@DefaultValue("false") @RequestParam("getAddress") Boolean getAddress,
			  @RequestParam("discardedAddress") Boolean discardedAddress) {
		if (DPDoctorUtils.anyStringEmpty(mobileNumber)) {
			logger.warn(mobileNumberValidaton);
			throw new BusinessException(ServiceError.InvalidInput, mobileNumberValidaton);
		}
		Response<Object> response = new Response<Object>();

		List<RegisteredPatientDetails> users = registrationService.getPatientsByPhoneNumber(mobileNumber);
		if (users != null && !users.isEmpty()) {
			for (RegisteredPatientDetails user : users) {
				user.setImageUrl(getFinalImageURL(user.getImageUrl()));
				user.setThumbnailUrl(getFinalImageURL(user.getThumbnailUrl()));
			}
			if (getAddress) {
				List<UserAddress> userAddress = registrationService.getUserAddress(null, mobileNumber,
						discardedAddress);
				if (userAddress != null && !userAddress.isEmpty()) {
					UserAddressResponse userAddressResponse = new UserAddressResponse();
					userAddressResponse.setUserAddress(userAddress);
					response.setData(userAddressResponse);
				}
			}
		}
		response.setDataList(users);
		return response;
	}

	
	@GetMapping(value = PathProxy.RegistrationUrls.EXISTING_PATIENTS_BY_PHONE_NUM_COUNT)
	@ApiOperation(value = PathProxy.RegistrationUrls.EXISTING_PATIENTS_BY_PHONE_NUM_COUNT, notes = PathProxy.RegistrationUrls.EXISTING_PATIENTS_BY_PHONE_NUM_COUNT, response = Response.class)
	public Response<Integer> getExistingPatientsCount(@PathVariable("mobileNumber") String mobileNumber) {
		if (DPDoctorUtils.anyStringEmpty(mobileNumber)) {
			logger.warn(invalidInput);
			throw new BusinessException(ServiceError.InvalidInput, invalidInput);
		}
		Response<Integer> response = new Response<Integer>();
		Integer patientCountByMobNum = 0;
		List<RegisteredPatientDetails> users = registrationService.getUsersByPhoneNumber(mobileNumber, null, null, null,
				null, null);
		if (users != null) {
			patientCountByMobNum = users.size();
		}
		response.setData(patientCountByMobNum);
		return response;
	}

	
	@GetMapping(value = PathProxy.RegistrationUrls.GET_PATIENT_PROFILE)
	@ApiOperation(value = PathProxy.RegistrationUrls.GET_PATIENT_PROFILE, notes = PathProxy.RegistrationUrls.GET_PATIENT_PROFILE, response = Response.class)
	public Response<RegisteredPatientDetails> getPatientProfile(@PathVariable("userId") String userId,
			@RequestParam("doctorId") String doctorId, @RequestParam("locationId") String locationId,
			@RequestParam("hospitalId") String hospitalId) {
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

	
	@PostMapping(value = PathProxy.RegistrationUrls.ADD_REFERRENCE)
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

	
	@DeleteMapping(value = PathProxy.RegistrationUrls.DELETE_REFERRENCE)
	@ApiOperation(value = PathProxy.RegistrationUrls.DELETE_REFERRENCE, notes = PathProxy.RegistrationUrls.DELETE_REFERRENCE, response = Response.class)
	public Response<Reference> deleteReferrence(@PathVariable("referrenceId") String referrenceId,
			  @RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded) {
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

	
	@GetMapping(value = PathProxy.RegistrationUrls.GET_REFERRENCES)
	@ApiOperation(value = PathProxy.RegistrationUrls.GET_REFERRENCES, notes = PathProxy.RegistrationUrls.GET_REFERRENCES, response = Response.class)
	public Response<ReferenceDetail> getReferences(@PathVariable("range") String range, @RequestParam("page") long page,
			@RequestParam("size") int size, @RequestParam(value = "doctorId") String doctorId,
			@RequestParam(value = "locationId") String locationId, @RequestParam(value = "hospitalId") String hospitalId,
			@DefaultValue("0") @RequestParam(value = "updatedTime") String updatedTime,
			  @RequestParam(value = "discarded") Boolean discarded) {

		List<ReferenceDetail> references = registrationService.getReferences(range, page, size, doctorId, locationId,
				hospitalId, updatedTime, discarded);
		Response<ReferenceDetail> response = new Response<ReferenceDetail>();
		response.setDataList(references);
		return response;
	}

	
	@GetMapping(value = PathProxy.RegistrationUrls.GET_PATIENT_INITIAL_COUNTER)
	@ApiOperation(value = PathProxy.RegistrationUrls.GET_PATIENT_INITIAL_COUNTER, notes = PathProxy.RegistrationUrls.GET_PATIENT_INITIAL_COUNTER, response = Response.class)
	public Response<PatientInitialAndCounter> getPatientInitialAndCounter(@PathVariable("locationId") String locationId) {

		if (DPDoctorUtils.anyStringEmpty(locationId)) {
			logger.warn(invalidInput);
			throw new BusinessException(ServiceError.InvalidInput, invalidInput);
		}
		Response<PatientInitialAndCounter> response = new Response<PatientInitialAndCounter>();
		PatientInitialAndCounter patientInitialAndCounter = registrationService.getPatientInitialAndCounter(locationId);
		response.setData(patientInitialAndCounter);
		return response;
	}

	
	@GetMapping(value = PathProxy.RegistrationUrls.UPDATE_PATIENT_INITIAL_AND_COUNTER)
	@ApiOperation(value = PathProxy.RegistrationUrls.UPDATE_PATIENT_INITIAL_AND_COUNTER, notes = PathProxy.RegistrationUrls.UPDATE_PATIENT_INITIAL_AND_COUNTER, response = Response.class)
	public Response<Boolean> updatePatientInitialAndCounter(@PathVariable("locationId") String locationId,
			@PathVariable("patientInitial") String patientInitial, @PathVariable("patientCounter") int patientCounter,
			@DefaultValue(value = "true") @RequestParam("isPidHasDate") Boolean isPidHasDate) {
		if (DPDoctorUtils.anyStringEmpty(locationId, patientInitial, new Integer(patientCounter).toString())) {
			logger.warn(invalidInput);
			throw new BusinessException(ServiceError.InvalidInput, invalidInput);
		} else if (patientInitial.matches(".*\\d+.*")) {
			logger.warn("Invalid Patient Initial");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Patient Initial");
		}
		Boolean updateResponse = registrationService.updatePatientInitialAndCounter(locationId, patientInitial,
				patientCounter, isPidHasDate);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(updateResponse);
		return response;
	}

	
	@GetMapping(value = PathProxy.RegistrationUrls.GET_CLINIC_DETAILS)
	@ApiOperation(value = PathProxy.RegistrationUrls.GET_CLINIC_DETAILS, notes = PathProxy.RegistrationUrls.GET_CLINIC_DETAILS, response = Response.class)
	public Response<Location> getClinicDetails(@PathVariable("clinicId") String clinicId) {
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

	
	@PostMapping(value = PathProxy.RegistrationUrls.UPDATE_CLINIC_PROFILE)
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

	
	@PostMapping(value = PathProxy.RegistrationUrls.UPDATE_CLINIC_PROFILE_HANDHELD)
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

	
	@PostMapping(value = PathProxy.RegistrationUrls.UPDATE_CLINIC_ADDRESS)
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

	
	@PostMapping(value = PathProxy.RegistrationUrls.UPDATE_CLINIC_TIMING)
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

	
	@PostMapping(value = PathProxy.RegistrationUrls.UPDATE_CLINIC_SPECIALIZATION)
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

	
	@PostMapping(value = PathProxy.RegistrationUrls.UPDATE_CLINIC_LAB_PROPERTIES)
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

	
	@PostMapping(value = PathProxy.RegistrationUrls.CHANGE_CLINIC_LOGO)
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

	
	@PostMapping(value = PathProxy.RegistrationUrls.ADD_CLINIC_IMAGE)
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

	
	@DeleteMapping(value = PathProxy.RegistrationUrls.DELETE_CLINIC_IMAGE)
	@ApiOperation(value = PathProxy.RegistrationUrls.DELETE_CLINIC_IMAGE, notes = PathProxy.RegistrationUrls.DELETE_CLINIC_IMAGE)
	public Response<Boolean> deleteClinicImage(@PathVariable(value = "locationId") String locationId,
			@PathVariable(value = "counter") int counter) {
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

	
	@GetMapping(value = PathProxy.RegistrationUrls.GET_BLOOD_GROUP)
	@ApiOperation(value = PathProxy.RegistrationUrls.GET_BLOOD_GROUP, notes = PathProxy.RegistrationUrls.GET_BLOOD_GROUP)
	public Response<BloodGroup> getBloodGroup() {
		List<BloodGroup> bloodGroupResponse = registrationService.getBloodGroup();
		Response<BloodGroup> response = new Response<BloodGroup>();
		response.setDataList(bloodGroupResponse);
		return response;
	}

	// (value = PathProxy.RegistrationUrls.ADD_PROFESSION)
	// @PostMapping
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

	
	@GetMapping(value = PathProxy.RegistrationUrls.GET_PROFESSION)
	@ApiOperation(value = PathProxy.RegistrationUrls.GET_PROFESSION, notes = PathProxy.RegistrationUrls.GET_PROFESSION)
	public Response<Profession> getProfession(@RequestParam("page") long page, @RequestParam("size") int size,
			@DefaultValue("0") @RequestParam(value = "updatedTime") String updatedTime) {

		List<Profession> professionResponse = registrationService.getProfession(page, size, updatedTime);
		Response<Profession> response = new Response<Profession>();
		response.setDataList(professionResponse);
		return response;
	}

	
	@PostMapping(value = PathProxy.RegistrationUrls.USER_REGISTER_IN_CLINIC)
	@ApiOperation(value = PathProxy.RegistrationUrls.USER_REGISTER_IN_CLINIC, notes = PathProxy.RegistrationUrls.USER_REGISTER_IN_CLINIC)
	public Response<RegisterDoctorResponse> userRegister(DoctorRegisterRequest request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getEmailAddress(), request.getFirstName(),
				request.getLocationId(), request.getHospitalId())) {
			logger.warn(invalidInput);
			throw new BusinessException(ServiceError.InvalidInput, invalidInput);
		}
		RegisterDoctorResponse doctorResponse = null;
		if (!registrationService.checktDoctorExistByEmailAddress(request.getEmailAddress())) {
			doctorResponse = registrationService.registerNewUser(request);
		} else {
			doctorResponse = registrationService.registerExisitingUser(request);
		}

		transnationalService.addResource(new ObjectId(doctorResponse.getUserId()), Resource.DOCTOR, false);
		if (doctorResponse != null)
			esRegistrationService.addDoctor(registrationService.getESDoctorDocument(doctorResponse));
		Response<RegisterDoctorResponse> response = new Response<RegisterDoctorResponse>();
		response.setData(doctorResponse);
		return response;
	}

	
	@PutMapping(value = PathProxy.RegistrationUrls.EDIT_USER_IN_CLINIC)
	@ApiOperation(value = PathProxy.RegistrationUrls.EDIT_USER_IN_CLINIC, notes = PathProxy.RegistrationUrls.EDIT_USER_IN_CLINIC)
	public Response<RegisterDoctorResponse> editUserInClinic(@PathVariable("userId") String userId,
			@PathVariable("locationId") String locationId, DoctorRegisterRequest request) {
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

	private String getFinalImageURL(String imageURL) {
		if (imageURL != null) {
			return imagePath + imageURL;
		} else
			return null;
	}

	
	@PostMapping(value = PathProxy.RegistrationUrls.ADD_EDIT_ROLE)
	@ApiOperation(value = PathProxy.RegistrationUrls.ADD_EDIT_ROLE, notes = PathProxy.RegistrationUrls.ADD_EDIT_ROLE)
	public Response<Role> addRole(Role request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getRole())// ,
																				// request.getLocationId(),
																				// request.getHospitalId()
				|| request.getAccessModules() == null || request.getAccessModules().isEmpty()) {
			logger.warn(invalidInput);
			throw new BusinessException(ServiceError.InvalidInput, invalidInput);
		}
		Role professionResponse = registrationService.addRole(request);
		Response<Role> response = new Response<Role>();
		response.setData(professionResponse);
		return response;
	}

	
	@PutMapping(value = PathProxy.RegistrationUrls.UPDATE_STAFF_ROLE)
	@ApiOperation(value = PathProxy.RegistrationUrls.UPDATE_STAFF_ROLE, notes = PathProxy.RegistrationUrls.UPDATE_STAFF_ROLE)
	public Response<RegisterDoctorResponse> updateStaffRole(@PathVariable("userId") String userId,
			@PathVariable("locationId") String locationId, DoctorRegisterRequest request) {
		if (request == null
				|| DPDoctorUtils.anyStringEmpty(userId, locationId, request.getFirstName(), request.getHospitalId())) {
			logger.warn(invalidInput);
			throw new BusinessException(ServiceError.InvalidInput, invalidInput);
		}
		request.setUserId(userId);
		request.setLocationId(locationId);
		RegisterDoctorResponse doctorResponse = registrationService.updateStaffRole(request);

		transnationalService.checkDoctor(new ObjectId(request.getUserId()), null);
		Response<RegisterDoctorResponse> response = new Response<RegisterDoctorResponse>();
		response.setData(doctorResponse);
		return response;
	}

	
	@GetMapping(value = PathProxy.RegistrationUrls.GET_ROLE)
	@ApiOperation(value = PathProxy.RegistrationUrls.GET_ROLE, notes = PathProxy.RegistrationUrls.GET_ROLE)
	public Response<Role> getRole(@PathVariable(value = "range") String range,
			@PathVariable(value = "locationId") String locationId, @PathVariable(value = "hospitalId") String hospitalId,
			@RequestParam(value = "page") long page, @RequestParam(value = "size") int size,
			@DefaultValue("0") @RequestParam(value = "updatedTime") String updatedTime,
			@RequestParam(value = "role") String role) {
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

	
	@GetMapping(value = PathProxy.RegistrationUrls.GET_USERS)
	@ApiOperation(value = PathProxy.RegistrationUrls.GET_USERS, notes = PathProxy.RegistrationUrls.GET_USERS)
	public Response<ClinicDoctorResponse> getUsers(@PathVariable(value = "locationId") String locationId,
			@PathVariable(value = "hospitalId") String hospitalId, @RequestParam(value = "page") long page,
			@RequestParam(value = "size") int size,
			@DefaultValue("0") @RequestParam(value = "updatedTime") String updatedTime,
			@RequestParam(value = "role") String role,
			@DefaultValue("false") @RequestParam(value = "active") Boolean active,
			@DefaultValue("false") @RequestParam(value = "access") Boolean access,
			@RequestParam(value = "userState") String userState) {
		if (DPDoctorUtils.anyStringEmpty(locationId, hospitalId)) {
			logger.warn(invalidInput);
			throw new BusinessException(ServiceError.InvalidInput, invalidInput);
		}
		List<ClinicDoctorResponse> professionResponse = registrationService.getUsers(page, size, locationId, hospitalId,
				updatedTime, role, active, access, userState);
		Response<ClinicDoctorResponse> response = new Response<ClinicDoctorResponse>();
		response.setDataList(professionResponse);
		return response;
	}

	
	@DeleteMapping(value = PathProxy.RegistrationUrls.DELETE_ROLE)
	@ApiOperation(value = PathProxy.RegistrationUrls.DELETE_ROLE, notes = PathProxy.RegistrationUrls.DELETE_ROLE)
	public Response<Role> deleteRole(@PathVariable(value = "roleId") String roleId,
			  @RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(roleId)) {
			logger.warn(invalidInput);
			throw new BusinessException(ServiceError.InvalidInput, invalidInput);
		}
		Role role = registrationService.deleteRole(roleId, discarded);
		Response<Role> response = new Response<Role>();
		response.setData(role);
		return response;
	}

	
	@DeleteMapping(value = PathProxy.RegistrationUrls.ACTIVATE_DEACTIVATE_USER)
	@ApiOperation(value = PathProxy.RegistrationUrls.ACTIVATE_DEACTIVATE_USER, notes = PathProxy.RegistrationUrls.ACTIVATE_DEACTIVATE_USER)
	public Response<Boolean> activateDeactivateUser(@PathVariable(value = "userId") String userId,
			@PathVariable(value = "locationId") String locationId,
			  @RequestParam("isActivate") Boolean isActivate) {
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

	
	@DeleteMapping(value = PathProxy.RegistrationUrls.ACCESS_USER)
	@ApiOperation(value = PathProxy.RegistrationUrls.ACCESS_USER, notes = PathProxy.RegistrationUrls.ACCESS_USER)
	public Response<Boolean> LoginAccessUser(@PathVariable(value = "userId") String userId,
			@PathVariable(value = "locationId") String locationId,
			  @RequestParam("hasLoginAccess") Boolean hasLoginAccess) {
		if (DPDoctorUtils.anyStringEmpty(userId, locationId)) {
			logger.warn(invalidInput);
			throw new BusinessException(ServiceError.InvalidInput, invalidInput);
		}
		registrationService.loginAccessUser(userId, locationId, hasLoginAccess);
		transnationalService.checkDoctor(new ObjectId(userId), null);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(true);
		return response;
	}

	
	@PostMapping(value = PathProxy.RegistrationUrls.ADD_FEEDBACK)
	@ApiOperation(value = PathProxy.RegistrationUrls.ADD_FEEDBACK, notes = PathProxy.RegistrationUrls.ADD_FEEDBACK)
	public Response<Feedback> addFeedback(Feedback request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getExplanation())) {
			logger.warn(invalidInput);
			throw new BusinessException(ServiceError.InvalidInput, invalidInput);
		}
		Feedback feedback = registrationService.addFeedback(request);

		Response<Feedback> response = new Response<Feedback>();
		response.setData(feedback);
		return response;
	}

	
	@GetMapping(value = PathProxy.RegistrationUrls.VISIBLE_FEEDBACK)
	@ApiOperation(value = PathProxy.RegistrationUrls.VISIBLE_FEEDBACK, notes = PathProxy.RegistrationUrls.VISIBLE_FEEDBACK)
	public Response<Feedback> visibleFeedback(@PathVariable("feedbackId") String feedbackId,
			  @RequestParam("isVisible") Boolean isVisible) {

		Feedback feedback = registrationService.visibleFeedback(feedbackId, isVisible);
		transnationalService.checkDoctor(new ObjectId(feedback.getDoctorId()), new ObjectId(feedback.getLocationId()));
		Response<Feedback> response = new Response<Feedback>();
		response.setData(feedback);
		return response;
	}

	
	@GetMapping(value = PathProxy.RegistrationUrls.GET_PATIENT_STATUS)
	@ApiOperation(value = PathProxy.RegistrationUrls.GET_PATIENT_STATUS, notes = PathProxy.RegistrationUrls.GET_PATIENT_STATUS)
	public Response<PatientStatusResponse> getPatientStatus(@PathVariable("patientId") String patientId,
			@PathVariable("doctorId") String doctorId, @PathVariable("locationId") String locationId,
			@PathVariable("hospitalId") String hospitalId) {

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

	
	@GetMapping(value = PathProxy.RegistrationUrls.GET_DOCTOR_FEEDBACK)
	@ApiOperation(value = PathProxy.RegistrationUrls.GET_DOCTOR_FEEDBACK, notes = PathProxy.RegistrationUrls.GET_DOCTOR_FEEDBACK)
	public Response<Feedback> getFeedback(@RequestParam("page") long page, @RequestParam("size") int size,
			@RequestParam("doctorId") String doctorId, @RequestParam("locationId") String locationId,
			@RequestParam("hospitalId") String hospitalId,
			@DefaultValue("0") @RequestParam(value = "updatedTime") String updatedTime,
			@RequestParam(value = "type") String type) {

		List<Feedback> feedbacks = registrationService.getFeedback(page, size, doctorId, locationId, hospitalId,
				updatedTime, type);
		Response<Feedback> response = new Response<Feedback>();
		response.setDataList(feedbacks);
		return response;
	}

	
	@GetMapping(value = PathProxy.RegistrationUrls.CHECK_PATIENT_NUMBER)
	@ApiOperation(value = PathProxy.RegistrationUrls.CHECK_PATIENT_NUMBER, notes = PathProxy.RegistrationUrls.CHECK_PATIENT_NUMBER)
	public Response<Boolean> checkPatientNumber(@PathVariable("oldMobileNumber") String oldMobileNumber,
			@PathVariable("newMobileNumber") String newMobileNumber) {
		if (DPDoctorUtils.anyStringEmpty(oldMobileNumber, newMobileNumber)) {
			logger.warn(invalidInput);
			throw new BusinessException(ServiceError.InvalidInput, invalidInput);
		}

		Boolean checkPatientNumberResponse = registrationService.checkPatientNumber(oldMobileNumber, newMobileNumber);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(checkPatientNumberResponse);
		return response;
	}

	
	@GetMapping(value = PathProxy.RegistrationUrls.CHANGE_PATIENT_NUMBER)
	@ApiOperation(value = PathProxy.RegistrationUrls.CHANGE_PATIENT_NUMBER, notes = PathProxy.RegistrationUrls.CHANGE_PATIENT_NUMBER)
	public Response<Boolean> changePatientNumber(@PathVariable("oldMobileNumber") String oldMobileNumber,
			@PathVariable("newMobileNumber") String newMobileNumber, @PathVariable("otpNumber") String otpNumber) {
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

	// (value =
	// PathProxy.RegistrationUrls.UPDATE_PATIENT_INITIAL_COUNTER_ON_CLINIC_LEVEL)
	// @GetMapping
	// public Response<Boolean> updatePIDOnClinicLevel() {
	//
	// Boolean changePatientNumberResponse =
	// registrationService.updatePIDOnClinicLevel();
	// Response<Boolean> response = new Response<Boolean>();
	// response.setData(changePatientNumberResponse);
	// return response;
	// }

	
	@GetMapping(value = PathProxy.RegistrationUrls.UPDATE_DOCTOR_CLINIC_PROFILE)
	public Response<Boolean> updateDoctorClinicProfile() {

		Boolean changePatientNumberResponse = registrationService.updateDoctorClinicProfile();
		Response<Boolean> response = new Response<Boolean>();
		response.setData(changePatientNumberResponse);
		return response;
	}

	
	@PostMapping(value = PathProxy.RegistrationUrls.ADD_SUGGESTION)
	@ApiOperation(value = PathProxy.RegistrationUrls.ADD_SUGGESTION, notes = PathProxy.RegistrationUrls.ADD_SUGGESTION)
	public Response<Suggestion> addSuggestion(Suggestion request) {
		if (request == null) {
			logger.warn(invalidInput);
			throw new BusinessException(ServiceError.InvalidInput, invalidInput);

		}
		Suggestion suggestion = suggestionService.AddEditSuggestion(request);
		Response<Suggestion> response = new Response<Suggestion>();
		response.setData(suggestion);
		return response;
	}

	
	@GetMapping(value = PathProxy.RegistrationUrls.GET_SUGGESTION)
	@ApiOperation(value = PathProxy.RegistrationUrls.GET_SUGGESTION, notes = PathProxy.RegistrationUrls.GET_SUGGESTION)
	public Response<Object> getSuggestion(@RequestParam("page") long page, @RequestParam("size") int size,
			@PathVariable("userId") String userId, @RequestParam("suggetionTypesuggetionType") String suggetionType,
			@RequestParam("state") String state, @RequestParam("searchTerm") String searchTerm) {

		List<Suggestion> suggestions = suggestionService.getSuggestion(page, size, userId, suggetionType, state,
				searchTerm);
		Response<Object> response = new Response<Object>();
		response.setDataList(suggestions);
		return response;
	}

	
	@GetMapping(value = PathProxy.RegistrationUrls.UPDATE_ROLE_COLLECTION_DATA)
	public Response<Boolean> updateRoleCollectionData() {

		Boolean updateRoleCollectionDataResponse = registrationService.updateRoleCollectionData();
		Response<Boolean> response = new Response<Boolean>();
		response.setData(updateRoleCollectionDataResponse);
		return response;
	}

	@PostMapping
	(value = PathProxy.RegistrationUrls.ADD_CONSENT_FORM)
	@Consumes({ MediaType.MULTIPART_FORM_DATA })
	@ApiOperation(value = PathProxy.RegistrationUrls.ADD_CONSENT_FORM, notes = PathProxy.RegistrationUrls.ADD_CONSENT_FORM)
	public Response<ConsentForm> addConsentForm(@FormDataParam("file") FormDataBodyPart file,
			@FormDataParam("data") FormDataBodyPart data) {
		data.setMediaType(MediaType.APPLICATION_JSON_TYPE);
		ConsentForm request = data.getValueAs(ConsentForm.class);

		if (file == null) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		if (file == null || request == null) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		if (DPDoctorUtils.anyStringEmpty(request.getPatientId(), request.getDoctorId(), request.getLocationId(),
				request.getHospitalId())) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		ConsentForm consentForm = registrationService.addConcentForm(file, request);

		Response<ConsentForm> response = new Response<ConsentForm>();
		response.setData(consentForm);
		return response;
	}

	@PostMapping
	(value = PathProxy.RegistrationUrls.ADD_CONSENT_FORM_DATA)
	@ApiOperation(value = PathProxy.RegistrationUrls.ADD_CONSENT_FORM_DATA, notes = PathProxy.RegistrationUrls.ADD_CONSENT_FORM_DATA)
	public Response<ConsentForm> addConsentForm(ConsentForm request) {
		if (request == null) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		if (DPDoctorUtils.anyStringEmpty(request.getPatientId(), request.getDoctorId(), request.getLocationId(),
				request.getHospitalId())) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		ConsentForm consentForm = registrationService.addConcentForm(null, request);
		Response<ConsentForm> response = new Response<ConsentForm>();
		response.setData(consentForm);
		return response;
	}

	
	@GetMapping(value = PathProxy.RegistrationUrls.GET_CONSENT_FORM)
	@ApiOperation(value = PathProxy.RegistrationUrls.GET_CONSENT_FORM, notes = PathProxy.RegistrationUrls.GET_CONSENT_FORM)
	public Response<ConsentForm> getConsentForm(@RequestParam("page") long page, @RequestParam("size") int size,
			@RequestParam("patientId") String patientId, @RequestParam("doctorId") String doctorId,
			@RequestParam("locationId") String locationId, @RequestParam("hospitalId") String hospitalId,
			@RequestParam("PID") String PID, @RequestParam("searchTerm") String searchTerm,
			  @RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded,
			@DefaultValue("0") @RequestParam("updatedTime") long updatedTime) {

		Response<ConsentForm> response = new Response<ConsentForm>();
		List<ConsentForm> consentForms = registrationService.getConcentForm(page, size, patientId, doctorId, locationId,
				hospitalId, PID, searchTerm, discarded, updatedTime);
		response.setDataList(consentForms);
		return response;
	}

	
	@DeleteMapping(value = PathProxy.RegistrationUrls.DELETE_CONSENT_FORM)
	@ApiOperation(value = PathProxy.RegistrationUrls.DELETE_CONSENT_FORM, notes = PathProxy.RegistrationUrls.DELETE_CONSENT_FORM)
	public Response<ConsentForm> deleteConsentForm(@PathVariable("consentFormId") String consentFormId,
			  @RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded) {
		Response<ConsentForm> response = new Response<ConsentForm>();
		response.setData(registrationService.deleteConcentForm(consentFormId, discarded));
		return response;
	}

	
	@GetMapping(value = PathProxy.RegistrationUrls.DOWNLOAD_CONSENT_FORM)
	@ApiOperation(value = PathProxy.RegistrationUrls.DOWNLOAD_CONSENT_FORM, notes = PathProxy.RegistrationUrls.DOWNLOAD_CONSENT_FORM)
	public Response<String> downloadConsentForm(@PathVariable("consentFormId") String consentFormId) {
		Response<String> response = new Response<String>();
		response.setData(registrationService.downloadConcentForm(consentFormId));
		return response;
	}

	
	@GetMapping(value = PathProxy.RegistrationUrls.EMAIL_CONSENT_FORM)
	@ApiOperation(value = PathProxy.RegistrationUrls.EMAIL_CONSENT_FORM, notes = PathProxy.RegistrationUrls.EMAIL_CONSENT_FORM)
	public Response<Boolean> emailConsentForm(@PathVariable(value = "consentFormId") String consentFormId,
			@PathVariable(value = "doctorId") String doctorId, @PathVariable(value = "locationId") String locationId,
			@PathVariable(value = "hospitalId") String hospitalId,
			@PathVariable(value = "emailAddress") String emailAddress) {

		if (DPDoctorUtils.anyStringEmpty(consentFormId, doctorId, locationId, hospitalId, emailAddress)) {
			logger.warn(
					"Invalid Input. consentFormId , Doctor Id, Location Id, Hospital Id, EmailAddress Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Invalid Input. consentFormId, Doctor Id, Location Id, Hospital Id, EmailAddress Cannot Be Empty");
		}
		registrationService.emailConsentForm(consentFormId, doctorId, locationId, hospitalId, emailAddress);

		Response<Boolean> response = new Response<Boolean>();
		response.setData(true);
		return response;
	}

	
	@GetMapping(value = PathProxy.RegistrationUrls.UPDATE_PID)
	@ApiOperation(value = PathProxy.RegistrationUrls.UPDATE_PID, notes = PathProxy.RegistrationUrls.UPDATE_PID)
	public Response<Integer> updateRegisterPID(@RequestParam(value = "createdTime") long createdTime) {

		Response<Integer> response = new Response<Integer>();
		response.setData(registrationService.updateRegisterPID(createdTime));
		return response;
	}

	
	@PostMapping(value = PathProxy.RegistrationUrls.ADD_FORM_CONTENT)
	@ApiOperation(value = PathProxy.RegistrationUrls.ADD_FORM_CONTENT, notes = PathProxy.RegistrationUrls.ADD_FORM_CONTENT)
	public Response<FormContent> addEditFormContent(FormContent request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId(),
				request.getHospitalId())) {
			throw new BusinessException(ServiceError.InvalidInput,
					"doctorId,locationId and hospitalId could not have null value");

		}
		Response<FormContent> response = new Response<FormContent>();
		response.setData(registrationService.addeditFromContent(request));
		return response;
	}

	
	@GetMapping(value = PathProxy.RegistrationUrls.GET_FORM_CONTENT)
	@ApiOperation(value = PathProxy.RegistrationUrls.GET_FORM_CONTENT, notes = PathProxy.RegistrationUrls.GET_FORM_CONTENT)
	public Response<FormContent> getFormContents(@PathVariable("doctorId") String doctorId,
			@PathVariable("locationId") String locationId, @PathVariable("hospitalId") String hospitalId,
			@RequestParam("page") long page, @RequestParam("size") int size, @RequestParam("type") String type,
			@RequestParam("title") String title, @RequestParam("updatedTime") String updatedTime,
			@RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(doctorId, locationId, hospitalId)) {
			throw new BusinessException(ServiceError.InvalidInput,
					"doctorId,locationId and hospitalId could not have null value");

		}
		Response<FormContent> response = new Response<FormContent>();
		response.setDataList(registrationService.getFormContents(page, size, doctorId, locationId, hospitalId, type,
				title, updatedTime, discarded));
		return response;
	}

	
	@DeleteMapping(value = PathProxy.RegistrationUrls.DELETE_FORM_CONTENT)
	@ApiOperation(value = PathProxy.RegistrationUrls.DELETE_FORM_CONTENT, notes = PathProxy.RegistrationUrls.DELETE_FORM_CONTENT)
	public Response<FormContent> deleteFormContents(@PathVariable("contentId") String contentId,
			  @RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(contentId)) {
			throw new BusinessException(ServiceError.InvalidInput, "Content Id could not null");

		}
		Response<FormContent> response = new Response<FormContent>();
		response.setData(registrationService.deleteFormContent(contentId, discarded));
		return response;
	}

	
	@PostMapping(value = PathProxy.RegistrationUrls.ADD_EDIT_USER_REMINDERS)
	@ApiOperation(value = PathProxy.RegistrationUrls.ADD_EDIT_USER_REMINDERS, notes = PathProxy.RegistrationUrls.ADD_EDIT_USER_REMINDERS)
	public Response<UserReminders> addEditPatientReminders(UserReminders request,
			@RequestParam("reminderType") String reminderType) {
		if (request == null) {
			throw new BusinessException(ServiceError.InvalidInput, "Request cannot be null");

		} else if (DPDoctorUtils.anyStringEmpty(request.getUserId())) {
			throw new BusinessException(ServiceError.InvalidInput, "User Id could not null");

		}
		Response<UserReminders> response = new Response<UserReminders>();
		response.setData(registrationService.addEditPatientReminders(request, reminderType));
		return response;
	}

	
	@GetMapping(value = PathProxy.RegistrationUrls.GET_USER_REMINDERS)
	@ApiOperation(value = PathProxy.RegistrationUrls.GET_USER_REMINDERS, notes = PathProxy.RegistrationUrls.GET_USER_REMINDERS)
	public Response<UserReminders> getPatientReminders(@PathVariable("userId") String userId,
			@RequestParam("reminderType") String reminderType) {
		if (DPDoctorUtils.anyStringEmpty(userId)) {
			throw new BusinessException(ServiceError.InvalidInput, "User Id could not null");

		}
		Response<UserReminders> response = new Response<UserReminders>();
		response.setData(registrationService.getPatientReminders(userId, reminderType));
		return response;
	}

	
	@PostMapping(value = PathProxy.RegistrationUrls.ADD_EDIT_USER_ADDRESS)
	@ApiOperation(value = PathProxy.RegistrationUrls.ADD_EDIT_USER_ADDRESS, notes = PathProxy.RegistrationUrls.ADD_EDIT_USER_ADDRESS)
	public Response<UserAddress> addEditUserAddress(UserAddress request) {
		if (request == null) {
			throw new BusinessException(ServiceError.InvalidInput, "Request cannot be null");

		} else if (DPDoctorUtils.anyStringEmpty(request.getMobileNumber())
				&& (request.getUserIds() == null || !request.getUserIds().isEmpty())) {
			throw new BusinessException(ServiceError.InvalidInput, "User Id & Mobile Number could not null");

		}
		Response<UserAddress> response = new Response<UserAddress>();
		response.setData(registrationService.addEditUserAddress(request));
		return response;
	}

	
	@GetMapping(value = PathProxy.RegistrationUrls.GET_USER_ADDRESS)
	@ApiOperation(value = PathProxy.RegistrationUrls.GET_USER_ADDRESS, notes = PathProxy.RegistrationUrls.GET_USER_ADDRESS)
	public Response<UserAddress> getUserAddress(@RequestParam("userId") String userId,
			@RequestParam("mobileNumber") String mobileNumber,
			  @RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded) {
		if (DPDoctorUtils.allStringsEmpty(userId, mobileNumber)) {
			throw new BusinessException(ServiceError.InvalidInput, "User Id & MobileNumber could not null");
		}
		Response<UserAddress> response = new Response<UserAddress>();
		response.setDataList(registrationService.getUserAddress(userId, mobileNumber, discarded));
		return response;
	}

	
	@DeleteMapping(value = PathProxy.RegistrationUrls.DELETE_USER_ADDRESS)
	@ApiOperation(value = PathProxy.RegistrationUrls.DELETE_USER_ADDRESS, notes = PathProxy.RegistrationUrls.DELETE_USER_ADDRESS)
	public Response<UserAddress> deleteUserAddress(@PathVariable("addressId") String addressId,
			@RequestParam("userId") String userId, @RequestParam("mobileNumber") String mobileNumber,
			@DefaultValue("false") @RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(userId, addressId)) {
			throw new BusinessException(ServiceError.InvalidInput, "User Id  & Address Id could not null");

		}
		Response<UserAddress> response = new Response<UserAddress>();
		response.setData(registrationService.deleteUserAddress(addressId, userId, mobileNumber, discarded));
		return response;
	}

	
	@DeleteMapping(value = PathProxy.RegistrationUrls.DELETE_PATIENT)
	@ApiOperation(value = PathProxy.RegistrationUrls.DELETE_PATIENT, notes = PathProxy.RegistrationUrls.DELETE_PATIENT)
	public Response<Object> deletePatient(@PathVariable("doctorId") String doctorId,
			@PathVariable("locationId") String locationId, @PathVariable("hospitalId") String hospitalId,
			@PathVariable("patientId") String patientId,   @RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded,
			@DefaultValue("false") @RequestParam("isMobileApp") Boolean isMobileApp) {
		if (DPDoctorUtils.anyStringEmpty(doctorId, locationId, hospitalId, patientId)) {
			throw new BusinessException(ServiceError.InvalidInput,
					"Doctor Id, locationId, hospitalId & patientId could not null");

		}
		Response<Object> response = registrationService.deletePatient(doctorId, locationId, hospitalId, patientId,
				discarded, isMobileApp);
		return response;
	}

	
	@GetMapping(value = PathProxy.RegistrationUrls.GET_DELETED_PATIENT)
	@ApiOperation(value = PathProxy.RegistrationUrls.GET_DELETED_PATIENT, notes = PathProxy.RegistrationUrls.GET_DELETED_PATIENT)
	public Response<PatientShortCard> getDeletedPatient(@PathVariable("doctorId") String doctorId,
			@PathVariable("locationId") String locationId, @PathVariable("hospitalId") String hospitalId,
			@RequestParam("page") int page, @RequestParam("size") int size, @RequestParam("searchTerm") String searchTerm,
			@RequestParam("sortBy") String sortBy) {
		if (DPDoctorUtils.anyStringEmpty(doctorId, locationId, hospitalId)) {
			throw new BusinessException(ServiceError.InvalidInput, "Doctor Id, locationId, hospitalId could not null");

		}
		Response<PatientShortCard> response = new Response<PatientShortCard>();
		response.setDataList(registrationService.getDeletedPatient(doctorId, locationId, hospitalId, page, size,
				searchTerm, sortBy));
		return response;
	}

	
	@GetMapping(value = PathProxy.RegistrationUrls.UPDATE_PATIENT_NUMBER)
	@ApiOperation(value = PathProxy.RegistrationUrls.UPDATE_PATIENT_NUMBER, notes = PathProxy.RegistrationUrls.UPDATE_PATIENT_NUMBER)
	public Response<Boolean> updatePatientNumber(@PathVariable("doctorId") String doctorId,
			@PathVariable("locationId") String locationId, @PathVariable("hospitalId") String hospitalId,
			@PathVariable("oldPatientId") String patientId, @RequestParam("newPatientId") String newPatientId,
			@RequestParam("mobileNumber") String mobileNumber) {
		if (DPDoctorUtils.anyStringEmpty(doctorId, locationId, hospitalId)) {
			throw new BusinessException(ServiceError.InvalidInput, "Doctor Id, locationId, hospitalId could not null");

		}

		if (DPDoctorUtils.allStringsEmpty(newPatientId, mobileNumber)) {
			throw new BusinessException(ServiceError.InvalidInput,
					"New PatientId and mobileNumber both cannot be null");

		}

		Response<Boolean> response = new Response<Boolean>();
		response.setData(registrationService.updatePatientNumber(doctorId, locationId, hospitalId, patientId,
				newPatientId, mobileNumber));
		return response;
	}

	
	@GetMapping(value = PathProxy.RegistrationUrls.SET_DEFAULT_DOCTOR_IN_LIST)
	@ApiOperation(value = PathProxy.RegistrationUrls.SET_DEFAULT_DOCTOR_IN_LIST, notes = PathProxy.RegistrationUrls.SET_DEFAULT_DOCTOR_IN_LIST)
	public Response<Boolean> setDefaultDoctor(@PathVariable("doctorId") String doctorId,
			@PathVariable("locationId") String locationId, @PathVariable("hospitalId") String hospitalId,
			@RequestParam("defaultDoctorId") String defaultDoctorId) {
		if (DPDoctorUtils.anyStringEmpty(doctorId, locationId, hospitalId, defaultDoctorId)) {
			logger.warn(invalidInput);
			throw new BusinessException(ServiceError.InvalidInput, invalidInput);
		}

		Response<Boolean> response = new Response<Boolean>();
		response.setData(registrationService.setDefaultDocter(doctorId, locationId, hospitalId, defaultDoctorId));
		return response;
	}

	
	@GetMapping(value = PathProxy.RegistrationUrls.SET_DEFAULT_CLINIC_IN_LIST)
	@ApiOperation(value = PathProxy.RegistrationUrls.SET_DEFAULT_CLINIC_IN_LIST, notes = PathProxy.RegistrationUrls.SET_DEFAULT_CLINIC_IN_LIST)
	public Response<Boolean> setDefaultDoctor(@PathVariable("locationId") String locationId,
			@PathVariable("hospitalId") String hospitalId, @RequestParam("defaultLocationId") String defaultLocationId) {
		if (DPDoctorUtils.anyStringEmpty(locationId, hospitalId)) {
			logger.warn(invalidInput);
			throw new BusinessException(ServiceError.InvalidInput, invalidInput);
		}

		Response<Boolean> response = new Response<Boolean>();
		response.setData(registrationService.setDefaultClinic(locationId, hospitalId, defaultLocationId));
		return response;
	}

	
	@GetMapping(value = PathProxy.RegistrationUrls.GET_CLINICS)
	@ApiOperation(value = PathProxy.RegistrationUrls.GET_CLINICS, notes = PathProxy.RegistrationUrls.GET_CLINICS)
	public Response<Location> getUsers(@PathVariable(value = "locationId") String locationId,
			@PathVariable(value = "hospitalId") String hospitalId) {
		if (DPDoctorUtils.anyStringEmpty(locationId, hospitalId)) {
			logger.warn(invalidInput);
			throw new BusinessException(ServiceError.InvalidInput, invalidInput);
		}
		Location location = registrationService.getClinics(locationId, hospitalId);
		Response<Location> response = new Response<Location>();
		response.setData(location);
		return response;
	}
//	
//	

	
	@GetMapping(value = "update")
	public Response<Boolean> update() {

		Response<Boolean> response = new Response<Boolean>();
		response.setData(registrationService.update());
		return response;
	}

	
	@GetMapping(value = PathProxy.RegistrationUrls.CHECK_IF_PNUM_EXIST)
	@ApiOperation(value = PathProxy.RegistrationUrls.CHECK_IF_PNUM_EXIST, notes = PathProxy.RegistrationUrls.CHECK_IF_PNUM_EXIST)
	public Response<Boolean> checkIfPNUMExist(@PathVariable("locationId") String locationId,
			@PathVariable("hospitalId") String hospitalId, @PathVariable("PNUM") String PNUM) {
		if (DPDoctorUtils.anyStringEmpty(locationId, hospitalId)) {
			throw new BusinessException(ServiceError.InvalidInput, "LocationId, hospitalId could not null");

		}

		if (DPDoctorUtils.allStringsEmpty(PNUM)) {
			throw new BusinessException(ServiceError.InvalidInput, "PNUM cannot be null");
		}

		Response<Boolean> response = new Response<Boolean>();
		response.setData(registrationService.checkIfPNUMExist(locationId, hospitalId, PNUM));
		return response;
	}

	
	@PostMapping(value = PathProxy.RegistrationUrls.UPDATE_CALENDAR_VIEW)
	@ApiOperation(value = PathProxy.RegistrationUrls.UPDATE_CALENDAR_VIEW, notes = PathProxy.RegistrationUrls.UPDATE_CALENDAR_VIEW)
	public Response<DoctorCalendarView> updateDoctorCalendarView(@RequestBody DoctorCalendarView request) {

		if (request == null) {

			// request.getHospitalId()) {

			logger.warn(invalidInput);

			throw new BusinessException(ServiceError.InvalidInput, invalidInput);

		}

		DoctorCalendarView doctorCalendarView = registrationService.updateCalendarView(request);

		Response<DoctorCalendarView> response = new Response<DoctorCalendarView>();

		response.setData(doctorCalendarView);

		return response;

	}

	
	@GetMapping(value = PathProxy.RegistrationUrls.GET_DOCTOR_CALENDAR_VIEW)
	@ApiOperation(value = PathProxy.RegistrationUrls.GET_DOCTOR_CALENDAR_VIEW, notes = PathProxy.RegistrationUrls.GET_DOCTOR_CALENDAR_VIEW)
	public Response<DoctorCalendarView> getDoctorCalendarView(@RequestParam("doctorId") String doctorId,
			@RequestParam("locationId") String locationId) {

		if (DPDoctorUtils.anyStringEmpty(doctorId, locationId)) {

			throw new BusinessException(ServiceError.InvalidInput, "DoctorId,LocationId, could not null");

		}

		Response<DoctorCalendarView> response = new Response<DoctorCalendarView>();

		response.setData(registrationService.getDoctorCalendarView(doctorId, locationId));

		return response;

	}

}
