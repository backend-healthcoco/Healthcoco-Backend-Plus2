package com.dpdocter.webservices;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dpdocter.beans.AddEditSEORequest;
import com.dpdocter.beans.Clinic;
import com.dpdocter.beans.ClinicImage;
import com.dpdocter.beans.DoctorClinicProfile;
import com.dpdocter.beans.DoctorContactsResponse;
import com.dpdocter.beans.DoctorGeneralInfo;
import com.dpdocter.beans.DoctorOnlineConsultationFees;
import com.dpdocter.beans.DoctorProfile;
import com.dpdocter.beans.DoctorRegistrationDetails;
import com.dpdocter.beans.EducationInstitute;
import com.dpdocter.beans.EducationQualification;
import com.dpdocter.beans.Location;
import com.dpdocter.beans.MedicalCouncil;
import com.dpdocter.beans.ProfessionalMembership;
import com.dpdocter.beans.Services;
import com.dpdocter.beans.Speciality;
import com.dpdocter.elasticsearch.services.ESMasterService;
import com.dpdocter.enums.PackageType;
import com.dpdocter.enums.Resource;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.request.DoctorAchievementAddEditRequest;
import com.dpdocter.request.DoctorAddEditFacilityRequest;
import com.dpdocter.request.DoctorAppointmentNumbersAddEditRequest;
import com.dpdocter.request.DoctorAppointmentSlotAddEditRequest;
import com.dpdocter.request.DoctorConsultationFeeAddEditRequest;
import com.dpdocter.request.DoctorContactAddEditRequest;
import com.dpdocter.request.DoctorDOBAddEditRequest;
import com.dpdocter.request.DoctorEducationAddEditRequest;
import com.dpdocter.request.DoctorExperienceAddEditRequest;
import com.dpdocter.request.DoctorExperienceDetailAddEditRequest;
import com.dpdocter.request.DoctorGenderAddEditRequest;
import com.dpdocter.request.DoctorMultipleDataAddEditRequest;
import com.dpdocter.request.DoctorNameAddEditRequest;
import com.dpdocter.request.DoctorOnlineWorkingTimeRequest;
import com.dpdocter.request.DoctorProfessionalAddEditRequest;
import com.dpdocter.request.DoctorProfessionalStatementAddEditRequest;
import com.dpdocter.request.DoctorProfilePictureAddEditRequest;
import com.dpdocter.request.DoctorRegistrationAddEditRequest;
import com.dpdocter.request.DoctorServicesAddEditRequest;
import com.dpdocter.request.DoctorSpecialityAddEditRequest;
import com.dpdocter.request.DoctorVisitingTimeAddEditRequest;
import com.dpdocter.response.DoctorMultipleDataAddEditResponse;
import com.dpdocter.response.DoctorStatisticsResponse;
import com.dpdocter.services.DoctorProfileService;
import com.dpdocter.services.DoctorStatsService;
import com.dpdocter.services.LabService;
import com.dpdocter.services.LocationServices;
import com.dpdocter.services.TransactionalManagementService;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value=PathProxy.DOCTOR_PROFILE_URL,produces = MediaType.APPLICATION_JSON ,consumes = MediaType.APPLICATION_JSON)
@Api(value = PathProxy.DOCTOR_PROFILE_URL, description = "Endpoint for doctor profile")
public class DoctorProfileApi {

	private static Logger logger = LogManager.getLogger(DoctorProfileApi.class.getName());

	@Autowired
	private DoctorProfileService doctorProfileService;

	@Autowired
	private TransactionalManagementService transnationalService;

	@Autowired
	private LocationServices locationService;

	@Autowired
	private LabService labService;

	@Autowired
	private DoctorStatsService doctorStatsService;
	
	@Autowired
	private ESMasterService esMasterService;

	@Value(value = "${image.path}")
	private String imagePath;

	
	@PostMapping(value = PathProxy.DoctorProfileUrls.ADD_EDIT_NAME)
	@ApiOperation(value = PathProxy.DoctorProfileUrls.ADD_EDIT_NAME, notes = PathProxy.DoctorProfileUrls.ADD_EDIT_NAME)
	public Response<DoctorNameAddEditRequest> addEditName(@RequestBody DoctorNameAddEditRequest request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getFirstName())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		DoctorNameAddEditRequest addEditNameResponse = doctorProfileService.addEditName(request);
		transnationalService.checkDoctor(new ObjectId(request.getDoctorId()), null);
		Response<DoctorNameAddEditRequest> response = new Response<DoctorNameAddEditRequest>();
		response.setData(addEditNameResponse);
		return response;
	}

	
	@PostMapping(value = PathProxy.DoctorProfileUrls.ADD_EDIT_EXPERIENCE)
	@ApiOperation(value = PathProxy.DoctorProfileUrls.ADD_EDIT_EXPERIENCE, notes = PathProxy.DoctorProfileUrls.ADD_EDIT_EXPERIENCE)
	public Response<DoctorExperienceAddEditRequest> addEditExperience(@RequestBody DoctorExperienceAddEditRequest request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		DoctorExperienceAddEditRequest experienceResponse = doctorProfileService.addEditExperience(request);
		if (experienceResponse != null)
			transnationalService.checkDoctor(new ObjectId(request.getDoctorId()), null);
		Response<DoctorExperienceAddEditRequest> response = new Response<DoctorExperienceAddEditRequest>();
		response.setData(experienceResponse);
		return response;
	}

	
	@PostMapping(value = PathProxy.DoctorProfileUrls.ADD_EDIT_CONTACT)
	@ApiOperation(value = PathProxy.DoctorProfileUrls.ADD_EDIT_CONTACT, notes = PathProxy.DoctorProfileUrls.ADD_EDIT_CONTACT)
	public Response<DoctorContactAddEditRequest> addEditContact(@RequestBody DoctorContactAddEditRequest request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		DoctorContactAddEditRequest addEditContactResponse = doctorProfileService.addEditContact(request);
		Response<DoctorContactAddEditRequest> response = new Response<DoctorContactAddEditRequest>();
		response.setData(addEditContactResponse);
		return response;
	}

	
	@PostMapping(value = PathProxy.DoctorProfileUrls.ADD_EDIT_EDUCATION)
	@ApiOperation(value = PathProxy.DoctorProfileUrls.ADD_EDIT_EDUCATION, notes = PathProxy.DoctorProfileUrls.ADD_EDIT_EDUCATION)
	public Response<DoctorEducationAddEditRequest> addEditEducation(@RequestBody DoctorEducationAddEditRequest request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		DoctorEducationAddEditRequest addEditEducationResponse = doctorProfileService.addEditEducation(request);
		Response<DoctorEducationAddEditRequest> response = new Response<DoctorEducationAddEditRequest>();
		response.setData(addEditEducationResponse);
		return response;
	}

	
	@GetMapping(value = PathProxy.DoctorProfileUrls.GET_MEDICAL_COUNCILS)
	@ApiOperation(value = PathProxy.DoctorProfileUrls.GET_MEDICAL_COUNCILS, notes = PathProxy.DoctorProfileUrls.GET_MEDICAL_COUNCILS)
	public Response<MedicalCouncil> getMedicalCouncils(@RequestParam("page") long page, @RequestParam("size") int size,
			@DefaultValue("0") @RequestParam(value = "updatedTime") String updatedTime) {
		List<MedicalCouncil> medicalCouncils = doctorProfileService.getMedicalCouncils(page, size, updatedTime);
		Response<MedicalCouncil> response = new Response<MedicalCouncil>();
		response.setDataList(medicalCouncils);
		return response;
	}

	
	@PostMapping(value = PathProxy.DoctorProfileUrls.ADD_EDIT_SPECIALITY)
	@ApiOperation(value = PathProxy.DoctorProfileUrls.ADD_EDIT_SPECIALITY, notes = PathProxy.DoctorProfileUrls.ADD_EDIT_SPECIALITY)
	public Response<DoctorSpecialityAddEditRequest> addEditSpeciality(@RequestBody DoctorSpecialityAddEditRequest request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		DoctorSpecialityAddEditRequest specialityResponse = doctorProfileService.addEditSpeciality(request);

		if (specialityResponse != null)
			transnationalService.checkDoctor(new ObjectId(request.getDoctorId()), null);
		Response<DoctorSpecialityAddEditRequest> response = new Response<DoctorSpecialityAddEditRequest>();
		response.setData(specialityResponse);
		return response;
	}

	
	@PostMapping(value = PathProxy.DoctorProfileUrls.ADD_EDIT_SERVICES)
	@ApiOperation(value = PathProxy.DoctorProfileUrls.ADD_EDIT_SERVICES, notes = PathProxy.DoctorProfileUrls.ADD_EDIT_SERVICES)
	public Response<DoctorServicesAddEditRequest> addEditServices(@RequestBody DoctorServicesAddEditRequest request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		DoctorServicesAddEditRequest specialityResponse = doctorProfileService.addEditServices(request);

		if (specialityResponse != null)
			transnationalService.checkDoctor(new ObjectId(request.getDoctorId()), null);
		Response<DoctorServicesAddEditRequest> response = new Response<DoctorServicesAddEditRequest>();
		response.setData(specialityResponse);
		return response;
	}
	
	//	public Response<DoctorServicesAddEditRequest> addEditServices(DoctorServicesAddEditRequest request) {
//		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId())) {
//			logger.warn("Invalid Input");
//			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
//		}
//		DoctorServicesAddEditRequest specialityResponse = doctorProfileService.addEditServices(request);
//
//		if (specialityResponse != null)
//			transnationalService.checkDoctor(new ObjectId(request.getDoctorId()), null);
//		Response<DoctorServicesAddEditRequest> response = new Response<DoctorServicesAddEditRequest>();
//		response.setData(specialityResponse);
//		return response;
//	}

	
	
	@PostMapping(value = PathProxy.DoctorProfileUrls.ADD_EDIT_ACHIEVEMENT)
	@ApiOperation(value = PathProxy.DoctorProfileUrls.ADD_EDIT_ACHIEVEMENT, notes = PathProxy.DoctorProfileUrls.ADD_EDIT_ACHIEVEMENT)
	public Response<DoctorAchievementAddEditRequest> addEditAchievement(@RequestBody DoctorAchievementAddEditRequest request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		DoctorAchievementAddEditRequest addEditSpecialityResponse = doctorProfileService.addEditAchievement(request);
		Response<DoctorAchievementAddEditRequest> response = new Response<DoctorAchievementAddEditRequest>();
		response.setData(addEditSpecialityResponse);
		return response;
	}

	
	@PostMapping(value = PathProxy.DoctorProfileUrls.ADD_EDIT_PROFESSIONAL_STATEMENT)
	@ApiOperation(value = PathProxy.DoctorProfileUrls.ADD_EDIT_PROFESSIONAL_STATEMENT, notes = PathProxy.DoctorProfileUrls.ADD_EDIT_PROFESSIONAL_STATEMENT)
	public Response<DoctorProfessionalStatementAddEditRequest> addEditProfessionalStatement(
			@RequestBody DoctorProfessionalStatementAddEditRequest request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		DoctorProfessionalStatementAddEditRequest addEditProfessionalStatementResponse = doctorProfileService
				.addEditProfessionalStatement(request);
		Response<DoctorProfessionalStatementAddEditRequest> response = new Response<DoctorProfessionalStatementAddEditRequest>();
		response.setData(addEditProfessionalStatementResponse);
		return response;
	}

	
	@PostMapping(value = PathProxy.DoctorProfileUrls.ADD_EDIT_REGISTRATION_DETAIL)
	@ApiOperation(value = PathProxy.DoctorProfileUrls.ADD_EDIT_REGISTRATION_DETAIL, notes = PathProxy.DoctorProfileUrls.ADD_EDIT_REGISTRATION_DETAIL)
	public Response<DoctorRegistrationAddEditRequest> addEditRegistrationDetail(
			@RequestBody DoctorRegistrationAddEditRequest request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		DoctorRegistrationAddEditRequest addEditRegistrationDetailResponse = doctorProfileService
				.addEditRegistrationDetail(request);
		Response<DoctorRegistrationAddEditRequest> response = new Response<DoctorRegistrationAddEditRequest>();
		response.setData(addEditRegistrationDetailResponse);
		return response;
	}

	
	@PostMapping(value = PathProxy.DoctorProfileUrls.ADD_EDIT_EXPERIENCE_DETAIL)
	@ApiOperation(value = PathProxy.DoctorProfileUrls.ADD_EDIT_EXPERIENCE_DETAIL, notes = PathProxy.DoctorProfileUrls.ADD_EDIT_EXPERIENCE_DETAIL)
	public Response<DoctorExperienceDetailAddEditRequest> addEditExperienceDetail(
			@RequestBody DoctorExperienceDetailAddEditRequest request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		DoctorExperienceDetailAddEditRequest addEditExperienceDetailResponse = doctorProfileService
				.addEditExperienceDetail(request);
		Response<DoctorExperienceDetailAddEditRequest> response = new Response<DoctorExperienceDetailAddEditRequest>();
		response.setData(addEditExperienceDetailResponse);
		return response;
	}

	
	@PostMapping(value = PathProxy.DoctorProfileUrls.ADD_EDIT_PROFILE_PICTURE)
	@ApiOperation(value = PathProxy.DoctorProfileUrls.ADD_EDIT_PROFILE_PICTURE, notes = PathProxy.DoctorProfileUrls.ADD_EDIT_PROFILE_PICTURE)
	public Response<String> addEditProfilePicture(@RequestBody DoctorProfilePictureAddEditRequest request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		String addEditProfilePictureResponse = doctorProfileService.addEditProfilePicture(request);
		if (addEditProfilePictureResponse != null)
			transnationalService.checkDoctor(new ObjectId(request.getDoctorId()), null);
		addEditProfilePictureResponse = getFinalImageURL(addEditProfilePictureResponse);
		Response<String> response = new Response<String>();
		response.setData(addEditProfilePictureResponse);
		return response;
	}

	
	@PostMapping(value = PathProxy.DoctorProfileUrls.ADD_EDIT_COVER_PICTURE)
	@ApiOperation(value = PathProxy.DoctorProfileUrls.ADD_EDIT_COVER_PICTURE, notes = PathProxy.DoctorProfileUrls.ADD_EDIT_COVER_PICTURE)
	public Response<String> addEditCoverPicture(@RequestBody DoctorProfilePictureAddEditRequest request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		String addEditCoverPictureResponse = doctorProfileService.addEditCoverPicture(request);
		if (addEditCoverPictureResponse != null)
			transnationalService.checkDoctor(new ObjectId(request.getDoctorId()), null);
		addEditCoverPictureResponse = getFinalImageURL(addEditCoverPictureResponse);
		Response<String> response = new Response<String>();
		response.setData(addEditCoverPictureResponse);
		return response;
	}

	
	@PostMapping(value = PathProxy.DoctorProfileUrls.ADD_EDIT_PROFESSIONAL_MEMBERSHIP)
	@ApiOperation(value = PathProxy.DoctorProfileUrls.ADD_EDIT_PROFESSIONAL_MEMBERSHIP, notes = PathProxy.DoctorProfileUrls.ADD_EDIT_PROFESSIONAL_MEMBERSHIP)
	public Response<DoctorProfessionalAddEditRequest> addEditProfessionalMembership(
			@RequestBody DoctorProfessionalAddEditRequest request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		DoctorProfessionalAddEditRequest addEditProfessionalMembershipResponse = doctorProfileService
				.addEditProfessionalMembership(request);
		Response<DoctorProfessionalAddEditRequest> response = new Response<DoctorProfessionalAddEditRequest>();
		response.setData(addEditProfessionalMembershipResponse);
		return response;
	}

	
	@GetMapping(value = PathProxy.DoctorProfileUrls.GET_DOCTOR_PROFILE)
	@ApiOperation(value = PathProxy.DoctorProfileUrls.GET_DOCTOR_PROFILE, notes = PathProxy.DoctorProfileUrls.GET_DOCTOR_PROFILE)
	public Response<DoctorProfile> getDoctorProfile(@PathVariable("doctorId") String doctorId,
			@RequestParam("locationId") String locationId, @RequestParam("hospitalId") String hospitalId,
			@DefaultValue(value = "false") @RequestParam(value = "isMobileApp") Boolean isMobileApp,
			@RequestParam(value = "patientId") String patientId,
			@DefaultValue(value = "true") @RequestParam(value = "isSearched") Boolean isSearched,
			@RequestParam(value = "userState") String userState) {
		if (DPDoctorUtils.anyStringEmpty(doctorId)) {
			logger.warn("Doctor Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput, "Doctor Id Cannot Be Empty");
		}
		DoctorProfile doctorProfile = doctorProfileService.getDoctorProfile(doctorId, locationId, hospitalId, patientId,
				isMobileApp, isSearched,userState);
		if (doctorProfile != null) {
			if (doctorProfile.getImageUrl() != null) {
				doctorProfile.setImageUrl(getFinalImageURL(doctorProfile.getImageUrl()));
			}
			if (doctorProfile.getThumbnailUrl() != null) {
				doctorProfile.setThumbnailUrl(getFinalImageURL(doctorProfile.getThumbnailUrl()));
			}
			if (doctorProfile.getCoverImageUrl() != null) {
				doctorProfile.setCoverImageUrl(getFinalImageURL(doctorProfile.getCoverImageUrl()));
			}
			if (doctorProfile.getCoverThumbnailImageUrl() != null) {
				doctorProfile.setCoverThumbnailImageUrl(getFinalImageURL(doctorProfile.getCoverThumbnailImageUrl()));
			}
			if (doctorProfile.getRegistrationImageUrl() != null) {
				doctorProfile.setRegistrationImageUrl(getFinalImageURL(doctorProfile.getRegistrationImageUrl()));
			}
			if (doctorProfile.getRegistrationThumbnailUrl() != null) {
				doctorProfile.setRegistrationThumbnailUrl(getFinalImageURL(doctorProfile.getRegistrationThumbnailUrl()));
			}
			
			if (doctorProfile.getPhotoIdImageUrl() != null) {
				doctorProfile.setPhotoIdImageUrl(getFinalImageURL(doctorProfile.getPhotoIdImageUrl()));
			}
			
			if (doctorProfile.getClinicProfile() != null & !doctorProfile.getClinicProfile().isEmpty()) {
				for (DoctorClinicProfile clinicProfile : doctorProfile.getClinicProfile()) {
					if (clinicProfile.getImages() != null) {
						for (ClinicImage clinicImage : clinicProfile.getImages()) {
							if (clinicImage.getImageUrl() != null)
								clinicImage.setImageUrl(getFinalImageURL(clinicImage.getImageUrl()));
							if (clinicImage.getThumbnailUrl() != null)
								clinicImage.setThumbnailUrl(getFinalImageURL(clinicImage.getThumbnailUrl()));
						}
					}
					if (clinicProfile.getLogoUrl() != null) {
						clinicProfile.setLogoUrl(getFinalImageURL(clinicProfile.getLogoUrl()));
					}
					
					if (clinicProfile.getClinicOwnershipImageUrl() != null) {
						clinicProfile.setClinicOwnershipImageUrl(getFinalImageURL(clinicProfile.getClinicOwnershipImageUrl()));
					}
					
					if (clinicProfile.getPackageType() == null) {
						clinicProfile.setPackageType(PackageType.ADVANCE.getType());
					}

					if (clinicProfile.getLogoThumbnailUrl() != null) {
						clinicProfile.setLogoThumbnailUrl(getFinalImageURL(clinicProfile.getLogoThumbnailUrl()));
					}
				}
			}

			if (patientId != null || isSearched == true) {
				doctorProfileService.updateDoctorProfileViews(doctorId);
			}
		}
		Response<DoctorProfile> response = new Response<DoctorProfile>();
		response.setData(doctorProfile);
		return response;
	}

	
	@GetMapping(value = PathProxy.DoctorProfileUrls.GET_PROFESSIONAL_MEMBERSHIPS)
	@ApiOperation(value = PathProxy.DoctorProfileUrls.GET_PROFESSIONAL_MEMBERSHIPS, notes = PathProxy.DoctorProfileUrls.GET_PROFESSIONAL_MEMBERSHIPS)
	public Response<ProfessionalMembership> getProfessionalMemberships(@RequestParam("page") long page,
			@RequestParam("size") int size, @DefaultValue("0") @RequestParam(value = "updatedTime") String updatedTime) {
		List<ProfessionalMembership> professionalMemberships = doctorProfileService.getProfessionalMemberships(page,
				size, updatedTime);
		Response<ProfessionalMembership> response = new Response<ProfessionalMembership>();
		response.setDataList(professionalMemberships);
		return response;
	}

	
	@PostMapping(value = PathProxy.DoctorProfileUrls.ADD_EDIT_APPOINTMENT_NUMBERS)
	@ApiOperation(value = PathProxy.DoctorProfileUrls.ADD_EDIT_APPOINTMENT_NUMBERS, notes = PathProxy.DoctorProfileUrls.ADD_EDIT_APPOINTMENT_NUMBERS)
	public Response<DoctorAppointmentNumbersAddEditRequest> addEditAppointmentNumbers(
		@RequestBody DoctorAppointmentNumbersAddEditRequest request) {
		
		if (request == null) {
			logger.warn("Doctor Contact Request Is Empty");
			throw new BusinessException(ServiceError.InvalidInput, "Doctor Contact Request Is Empty");
		} else if (DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId())) {
			logger.warn("Doctor Id, LocationId Is Empty");
			throw new BusinessException(ServiceError.InvalidInput, "Doctor Id, LocationId Is Empty");
		}
		DoctorAppointmentNumbersAddEditRequest addEditAppointmentNumbersResponse = doctorProfileService
				.addEditAppointmentNumbers(request);
		transnationalService.addResource(new ObjectId(request.getDoctorId()), Resource.DOCTOR, false);

		Response<DoctorAppointmentNumbersAddEditRequest> response = new Response<DoctorAppointmentNumbersAddEditRequest>();
		response.setData(addEditAppointmentNumbersResponse);
		return response;
	}

	
	@PostMapping(value = PathProxy.DoctorProfileUrls.ADD_EDIT_VISITING_TIME)
	@ApiOperation(value = PathProxy.DoctorProfileUrls.ADD_EDIT_VISITING_TIME, notes = PathProxy.DoctorProfileUrls.ADD_EDIT_VISITING_TIME)
	public Response<DoctorVisitingTimeAddEditRequest> addEditVisitingTime(@RequestBody DoctorVisitingTimeAddEditRequest request) {
		if (request == null) {
			logger.warn("Doctor Contact Request Is Empty");
			throw new BusinessException(ServiceError.InvalidInput, "Doctor Contact Request Is Empty");
		} else if (DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId())) {
			logger.warn("Doctor Id, LocationId Is Empty");
			throw new BusinessException(ServiceError.InvalidInput, "Doctor Id, LocationId Is Empty");
		}
		DoctorVisitingTimeAddEditRequest addEditVisitingTimeResponse = doctorProfileService
				.addEditVisitingTime(request);
		transnationalService.checkDoctor(new ObjectId(request.getDoctorId()), new ObjectId(request.getLocationId()));
		Response<DoctorVisitingTimeAddEditRequest> response = new Response<DoctorVisitingTimeAddEditRequest>();
		response.setData(addEditVisitingTimeResponse);
		return response;
	}

	
	@PostMapping(value = PathProxy.DoctorProfileUrls.ADD_EDIT_CONSULTATION_FEE)
	@ApiOperation(value = PathProxy.DoctorProfileUrls.ADD_EDIT_CONSULTATION_FEE, notes = PathProxy.DoctorProfileUrls.ADD_EDIT_CONSULTATION_FEE)
	public Response<DoctorConsultationFeeAddEditRequest> addEditConsultationFee(
			@RequestBody DoctorConsultationFeeAddEditRequest request) {
		if (request == null) {
			logger.warn("Doctor Contact Request Is Empty");
			throw new BusinessException(ServiceError.InvalidInput, "Doctor Contact Request Is Empty");
		} else if (DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId())) {
			logger.warn("Doctor Id, LocationId Is Empty");
			throw new BusinessException(ServiceError.InvalidInput, "Doctor Id, LocationId Is Empty");
		}
		DoctorConsultationFeeAddEditRequest addEditConsultationFeeResponse = doctorProfileService
				.addEditConsultationFee(request);
		transnationalService.addResource(new ObjectId(request.getDoctorId()), Resource.DOCTOR, false);
		Response<DoctorConsultationFeeAddEditRequest> response = new Response<DoctorConsultationFeeAddEditRequest>();
		response.setData(addEditConsultationFeeResponse);
		return response;
	}

	
	@PostMapping(value = PathProxy.DoctorProfileUrls.ADD_EDIT_APPOINTMENT_SLOT)
	@ApiOperation(value = PathProxy.DoctorProfileUrls.ADD_EDIT_APPOINTMENT_SLOT, notes = PathProxy.DoctorProfileUrls.ADD_EDIT_APPOINTMENT_SLOT)
	public Response<DoctorAppointmentSlotAddEditRequest> addEditAppointmentSlot(
			@RequestBody DoctorAppointmentSlotAddEditRequest request) {
		if (request == null) {
			logger.warn("Doctor Contact Request Is Empty");
			throw new BusinessException(ServiceError.InvalidInput, "Doctor Contact Request Is Empty");
		} else if (DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId())) {
			logger.warn("Doctor Id, LocationId Is Empty");
			throw new BusinessException(ServiceError.InvalidInput, "Doctor Id, LocationId Is Empty");
		}
		DoctorAppointmentSlotAddEditRequest addEditAppointmentSlotResponse = doctorProfileService
				.addEditAppointmentSlot(request);
		transnationalService.addResource(new ObjectId(request.getDoctorId()), Resource.DOCTOR, false);

		Response<DoctorAppointmentSlotAddEditRequest> response = new Response<DoctorAppointmentSlotAddEditRequest>();
		response.setData(addEditAppointmentSlotResponse);
		return response;
	}

	
	@PostMapping(value = PathProxy.DoctorProfileUrls.ADD_EDIT_GENERAL_INFO)
	@ApiOperation(value = PathProxy.DoctorProfileUrls.ADD_EDIT_GENERAL_INFO, notes = PathProxy.DoctorProfileUrls.ADD_EDIT_GENERAL_INFO)
	public Response<DoctorGeneralInfo> addEditGeneralInfo(@RequestBody DoctorGeneralInfo request) {
		if (request == null) {
			logger.warn("Doctor Contact Request Is Empty");
			throw new BusinessException(ServiceError.InvalidInput, "Doctor Contact Request Is Empty");
		} else if (DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId())) {
			logger.warn("Doctor Id, LocationId Is Empty");
			throw new BusinessException(ServiceError.InvalidInput, "Doctor Id, LocationId Is Empty");
		}

		DoctorGeneralInfo addEditGeneralInfoResponse = doctorProfileService.addEditGeneralInfo(request);
		transnationalService.checkDoctor(new ObjectId(request.getDoctorId()), new ObjectId(request.getLocationId()));

		Response<DoctorGeneralInfo> response = new Response<DoctorGeneralInfo>();
		response.setData(addEditGeneralInfoResponse);
		return response;
	}

	private String getFinalImageURL(String imageURL) {
		if (imageURL != null) {
			return imagePath + imageURL;
		} else
			return null;
	}

	
	@GetMapping(value = PathProxy.DoctorProfileUrls.GET_SPECIALITIES)
	@ApiOperation(value = PathProxy.DoctorProfileUrls.GET_SPECIALITIES, notes = PathProxy.DoctorProfileUrls.GET_SPECIALITIES)
	public Response<Speciality> getSpeciality(@RequestParam("page") long page, @RequestParam("size") int size,
			@DefaultValue("0") @RequestParam(value = "updatedTime") String updatedTime) {
		List<Speciality> specialities = doctorProfileService.getSpecialities(page, size, updatedTime);
		Response<Speciality> response = new Response<Speciality>();
		response.setDataList(specialities);
		return response;
	}

	
	@GetMapping(value = PathProxy.DoctorProfileUrls.GET_SERVICES)
	@ApiOperation(value = PathProxy.DoctorProfileUrls.GET_SERVICES, notes = PathProxy.DoctorProfileUrls.GET_SERVICES)
	public Response<Services> getServices(@RequestParam("page") int page, @RequestParam("size") int size,@RequestParam("searchTerm") String searchTerm,
			@DefaultValue("0") @RequestParam(value = "updatedTime") String updatedTime) {
		List<Services> specialities = doctorProfileService.getServices(page, size, updatedTime,searchTerm);
		Response<Services> response = new Response<Services>();
		response.setDataList(specialities);
		return response;
	}

	
	@GetMapping(value = PathProxy.DoctorProfileUrls.GET_EDUCATION_INSTITUTES)
	@ApiOperation(value = PathProxy.DoctorProfileUrls.GET_EDUCATION_INSTITUTES, notes = PathProxy.DoctorProfileUrls.GET_EDUCATION_INSTITUTES)
	public Response<EducationInstitute> getEducationInstitutes(@RequestParam("page") long page,
			@RequestParam("size") int size, @DefaultValue("0") @RequestParam(value = "updatedTime") String updatedTime) {
		List<EducationInstitute> educationInstitutes = doctorProfileService.getEducationInstitutes(page, size,
				updatedTime);
		Response<EducationInstitute> response = new Response<EducationInstitute>();
		response.setDataList(educationInstitutes);
		return response;
	}

	
	@GetMapping(value = PathProxy.DoctorProfileUrls.GET_EDUCATION_QUALIFICATIONS)
	@ApiOperation(value = PathProxy.DoctorProfileUrls.GET_EDUCATION_QUALIFICATIONS, notes = PathProxy.DoctorProfileUrls.GET_EDUCATION_QUALIFICATIONS)
	public Response<EducationQualification> getEducationQualifications(@RequestParam("page") long page,
			@RequestParam("size") int size, @DefaultValue("0") @RequestParam(value = "updatedTime") String updatedTime) {
		List<EducationQualification> qualifications = doctorProfileService.getEducationQualifications(page, size,
				updatedTime);
		Response<EducationQualification> response = new Response<EducationQualification>();
		response.setDataList(qualifications);
		return response;
	}

	
	@PostMapping(value = PathProxy.DoctorProfileUrls.ADD_EDIT_MULTIPLE_DATA)
	@ApiOperation(value = PathProxy.DoctorProfileUrls.ADD_EDIT_MULTIPLE_DATA, notes = PathProxy.DoctorProfileUrls.ADD_EDIT_MULTIPLE_DATA)
	public Response<DoctorMultipleDataAddEditResponse> addEditMultipleData(@RequestBody DoctorMultipleDataAddEditRequest request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId())) {
			logger.warn("Request Cannot Be null");
			throw new BusinessException(ServiceError.InvalidInput, "Request Cannot Be null");
		}
		DoctorMultipleDataAddEditResponse addEditNameResponse = doctorProfileService.addEditMultipleData(request);
		transnationalService.checkDoctor(new ObjectId(request.getDoctorId()), null);

		addEditNameResponse.setCoverImageUrl(getFinalImageURL(addEditNameResponse.getCoverImageUrl()));
		addEditNameResponse.setImageUrl(getFinalImageURL(addEditNameResponse.getImageUrl()));
		addEditNameResponse
				.setCoverThumbnailImageUrl(getFinalImageURL(addEditNameResponse.getCoverThumbnailImageUrl()));
		addEditNameResponse.setThumbnailUrl(getFinalImageURL(addEditNameResponse.getThumbnailUrl()));

		Response<DoctorMultipleDataAddEditResponse> response = new Response<DoctorMultipleDataAddEditResponse>();
		response.setData(addEditNameResponse);
		return response;
	}

	
	@PostMapping(value = PathProxy.DoctorProfileUrls.ADD_EDIT_FACILITY)
	@ApiOperation(value = PathProxy.DoctorProfileUrls.ADD_EDIT_FACILITY, notes = PathProxy.DoctorProfileUrls.ADD_EDIT_FACILITY)
	public Response<DoctorAddEditFacilityRequest> addEditFacility(@RequestBody DoctorAddEditFacilityRequest request) {
		if (request == null) {
			logger.warn("Request Is Empty");
			throw new BusinessException(ServiceError.InvalidInput, "Request Is Empty");
		} else if (DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId())) {
			logger.warn("Doctor Id, LocationId Is Empty");
			throw new BusinessException(ServiceError.InvalidInput, "Doctor Id, LocationId Is Empty");
		}

		DoctorAddEditFacilityRequest addEditIBSResponse = doctorProfileService.addEditFacility(request);
		transnationalService.checkDoctor(new ObjectId(request.getDoctorId()), null);
		Response<DoctorAddEditFacilityRequest> response = new Response<DoctorAddEditFacilityRequest>();
		response.setData(addEditIBSResponse);
		return response;

	}

	
	@PostMapping(value = PathProxy.DoctorProfileUrls.ADD_EDIT_GENDER)
	@ApiOperation(value = PathProxy.DoctorProfileUrls.ADD_EDIT_GENDER, notes = PathProxy.DoctorProfileUrls.ADD_EDIT_GENDER)
	public Response<DoctorGenderAddEditRequest> addEditGender(@RequestBody DoctorGenderAddEditRequest request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		DoctorGenderAddEditRequest addEditNameResponse = doctorProfileService.addEditGender(request);
		transnationalService.checkDoctor(new ObjectId(request.getDoctorId()), null);
		Response<DoctorGenderAddEditRequest> response = new Response<DoctorGenderAddEditRequest>();
		response.setData(addEditNameResponse);
		return response;
	}

	
	@PostMapping(value = PathProxy.DoctorProfileUrls.ADD_EDIT_DOB)
	@ApiOperation(value = PathProxy.DoctorProfileUrls.ADD_EDIT_DOB, notes = PathProxy.DoctorProfileUrls.ADD_EDIT_DOB)
	public Response<DoctorDOBAddEditRequest> addEditDOB(@RequestBody DoctorDOBAddEditRequest request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		DoctorDOBAddEditRequest addEditNameResponse = doctorProfileService.addEditDOB(request);
		transnationalService.checkDoctor(new ObjectId(request.getDoctorId()), null);
		Response<DoctorDOBAddEditRequest> response = new Response<DoctorDOBAddEditRequest>();
		response.setData(addEditNameResponse);
		return response;
	}

	
	@GetMapping(value = PathProxy.DoctorProfileUrls.SET_RECOMMENDATION)
	@ApiOperation(value = PathProxy.DoctorProfileUrls.SET_RECOMMENDATION, notes = PathProxy.DoctorProfileUrls.SET_RECOMMENDATION)
	public Response<DoctorClinicProfile> setRecommendation(@PathVariable("doctorId") String doctorId,
			@PathVariable("locationId") String locationId, @PathVariable("patientId") String patientId) {

		DoctorClinicProfile doctorClinicProfile = doctorProfileService.addEditRecommedation(doctorId, locationId,
				patientId);
		Response<DoctorClinicProfile> response = new Response<DoctorClinicProfile>();
		response.setData(doctorClinicProfile);
		return response;
	}

	
	@GetMapping(value = PathProxy.DoctorProfileUrls.SET_CLINIC_RECOMMENDATION)
	@ApiOperation(value = PathProxy.DoctorProfileUrls.SET_CLINIC_RECOMMENDATION, notes = PathProxy.DoctorProfileUrls.SET_CLINIC_RECOMMENDATION)
	public Response<Location> setRecommendation(@PathVariable("locationId") String locationId,
			@PathVariable("patientId") String patientId) {

		Location location = locationService.addEditRecommedation(locationId, patientId);
		Response<Location> response = new Response<Location>();

		transnationalService.checkLocation(new ObjectId(location.getId()));
		response.setData(location);
		return response;
	}

	
	@GetMapping(value = PathProxy.DoctorProfileUrls.GET_PATIENT)
	@ApiOperation(value = PathProxy.DoctorProfileUrls.GET_PATIENT, notes = PathProxy.DoctorProfileUrls.GET_PATIENT)
	public Response<DoctorContactsResponse> getPatient(@RequestParam("page") long page, @RequestParam("size") int size,
			@RequestParam("doctorId") String doctorId, @RequestParam("locationId") String locationId,
			@RequestParam("hospitalId") String hospitalId, @RequestParam("from") long from, @RequestParam("to") long to) {

		Response<DoctorContactsResponse> response = new Response<DoctorContactsResponse>();
		DoctorContactsResponse doctorContactsResponse = doctorProfileService.getPatient(page, size, doctorId,
				locationId, hospitalId, from, to);
		response.setData(doctorContactsResponse);
		return response;
	}

	
	@GetMapping(value = PathProxy.DoctorProfileUrls.GET_LABS_WITH_REPORTS_COUNT)
	@ApiOperation(value = PathProxy.DoctorProfileUrls.GET_LABS_WITH_REPORTS_COUNT, notes = PathProxy.DoctorProfileUrls.GET_LABS_WITH_REPORTS_COUNT)
	public Response<List<Clinic>> getLabWithReportCount(@PathVariable(value = "doctorId") String doctorId,
			@PathVariable(value = "locationId") String locationId, @PathVariable(value = "hospitalId") String hospitalId) {
		if (DPDoctorUtils.anyStringEmpty(doctorId) || DPDoctorUtils.anyStringEmpty(locationId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		Response<List<Clinic>> response = new Response<List<Clinic>>();
		response.setDataList(labService.getLabWithReportCount(doctorId, locationId, hospitalId));
		return response;
	}

	
	@GetMapping(value = PathProxy.DoctorProfileUrls.GET_REPORTS_FOR_SPECIFIC_DOCTOR)
	@ApiOperation(value = PathProxy.DoctorProfileUrls.GET_REPORTS_FOR_SPECIFIC_DOCTOR, notes = PathProxy.DoctorProfileUrls.GET_REPORTS_FOR_SPECIFIC_DOCTOR)
	public Response<List<Clinic>> getLabWithReportCount(
			@PathVariable(value = "prescribedByDoctorId") String prescribedByDoctorId,
			@PathVariable(value = "prescribedByLocationId") String prescribedByLocationId,
			@PathVariable(value = "prescribedByHospitalId") String prescribedByHospitalId,
			@RequestParam(value = "doctorId") String doctorId, @RequestParam(value = "locationId") String locationId,
			@RequestParam(value = "hospitalId") String hospitalId, @RequestParam(value = "size") int size,
			@RequestParam(value = "page") long page) {
		if (DPDoctorUtils.anyStringEmpty(prescribedByDoctorId)
				|| DPDoctorUtils.anyStringEmpty(prescribedByLocationId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		Response<List<Clinic>> response = new Response<List<Clinic>>();
		response.setDataList(labService.getReports(doctorId, locationId, hospitalId, prescribedByDoctorId,
				prescribedByLocationId, prescribedByHospitalId, size, page));
		return response;
	}

	
	@GetMapping(value = PathProxy.DoctorProfileUrls.GET_DOCTOR_STATS)
	@ApiOperation(value = PathProxy.DoctorProfileUrls.GET_DOCTOR_STATS, notes = PathProxy.DoctorProfileUrls.GET_DOCTOR_STATS)
	public Response<DoctorStatisticsResponse> getDoctorsStats(@PathVariable(value = "doctorId") String doctorId,
			@RequestParam(value = "locationId") String locationId,
			@DefaultValue("WEEK") @RequestParam(value = "type") String type) {
		if (DPDoctorUtils.anyStringEmpty(doctorId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		DoctorStatisticsResponse doctorStatisticsResponse = doctorStatsService.getDoctorStats(doctorId, locationId,
				type);
		Response<DoctorStatisticsResponse> response = new Response<DoctorStatisticsResponse>();
		response.setData(doctorStatisticsResponse);
		return response;
	}

	
	@GetMapping(value = PathProxy.DoctorProfileUrls.UPDATE_EMR_SETTING)
	@ApiOperation(value = PathProxy.DoctorProfileUrls.UPDATE_EMR_SETTING, notes = PathProxy.DoctorProfileUrls.UPDATE_EMR_SETTING)
	public Response<Boolean> updateEMRSetting(@PathVariable(value = "doctorId") String doctorId,
			@RequestParam(value = "discarded") Boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(doctorId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<Boolean> response = new Response<Boolean>();
		Boolean status = doctorProfileService.updateEMRSetting(doctorId, discarded);

		response.setData(status);
		return response;
	}

	
	@PostMapping(value = PathProxy.DoctorProfileUrls.ADD_EDIT_SEO)
	@ApiOperation(value = PathProxy.DoctorProfileUrls.ADD_EDIT_SEO, notes = PathProxy.DoctorProfileUrls.ADD_EDIT_SEO)
	public Response<AddEditSEORequest> addEditSEO(@RequestBody AddEditSEORequest request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		AddEditSEORequest addEditSEORequest = doctorProfileService.addEditSEO(request);
		Response<AddEditSEORequest> response = new Response<AddEditSEORequest>();
		response.setData(addEditSEORequest);
		return response;
	}

	
	@GetMapping(value = PathProxy.DoctorProfileUrls.GET_DOCTOR_PROFILE_BY_SLUG_URL)
	@ApiOperation(value = PathProxy.DoctorProfileUrls.GET_DOCTOR_PROFILE_BY_SLUG_URL, notes = PathProxy.DoctorProfileUrls.GET_DOCTOR_PROFILE_BY_SLUG_URL)
	public Response<DoctorProfile> getDoctorProfile(@PathVariable("slugURL") String slugURL,
			@PathVariable("userUId") String userUId) {
		if (DPDoctorUtils.anyStringEmpty(userUId)) {
			logger.warn("Doctor Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput, "Doctor Id Cannot Be Empty");
		}
		DoctorProfile doctorProfile = doctorProfileService.getDoctorProfile(userUId);
		if (doctorProfile != null) {
			if (doctorProfile.getImageUrl() != null) {
				doctorProfile.setImageUrl(getFinalImageURL(doctorProfile.getImageUrl()));
			}
			if (doctorProfile.getThumbnailUrl() != null) {
				doctorProfile.setThumbnailUrl(getFinalImageURL(doctorProfile.getThumbnailUrl()));
			}
			if (doctorProfile.getCoverImageUrl() != null) {
				doctorProfile.setCoverImageUrl(getFinalImageURL(doctorProfile.getCoverImageUrl()));
			}
			if (doctorProfile.getCoverThumbnailImageUrl() != null) {
				doctorProfile.setCoverThumbnailImageUrl(getFinalImageURL(doctorProfile.getCoverThumbnailImageUrl()));
			}
			if (doctorProfile.getClinicProfile() != null & !doctorProfile.getClinicProfile().isEmpty()) {
				for (DoctorClinicProfile clinicProfile : doctorProfile.getClinicProfile()) {
					if (clinicProfile.getImages() != null) {
						for (ClinicImage clinicImage : clinicProfile.getImages()) {
							if (clinicImage.getImageUrl() != null)
								clinicImage.setImageUrl(getFinalImageURL(clinicImage.getImageUrl()));
							if (clinicImage.getThumbnailUrl() != null)
								clinicImage.setThumbnailUrl(getFinalImageURL(clinicImage.getThumbnailUrl()));
						}
					}
					if (clinicProfile.getLogoUrl() != null) {
						clinicProfile.setLogoUrl(getFinalImageURL(clinicProfile.getLogoUrl()));
					}

					if (clinicProfile.getLogoThumbnailUrl() != null) {
						clinicProfile.setLogoThumbnailUrl(getFinalImageURL(clinicProfile.getLogoThumbnailUrl()));
					}
				}
			}

			doctorProfile.setDoctorSlugURL(slugURL);
			doctorProfileService.updateDoctorProfileViews(doctorProfile.getDoctorId());

		}
		Response<DoctorProfile> response = new Response<DoctorProfile>();
		response.setData(doctorProfile);
		return response;
	}

	
	@GetMapping(value = PathProxy.DoctorProfileUrls.UPDATE_PRESCRIPTION_SMS)
	@ApiOperation(value = PathProxy.DoctorProfileUrls.UPDATE_PRESCRIPTION_SMS, notes = PathProxy.DoctorProfileUrls.UPDATE_PRESCRIPTION_SMS)
	public Response<Boolean> updatePresccriptionSMS(@PathVariable("doctorId") String doctorId,
			@RequestParam("isSendSMS") boolean isSendSMS) {
		if (DPDoctorUtils.anyStringEmpty(doctorId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		Response<Boolean> response = new Response<Boolean>();
		response.setData(doctorProfileService.updatePrescriptionSMS(doctorId, isSendSMS));
		return response;
	}
	
	
	
	@GetMapping(value = PathProxy.DoctorProfileUrls.UPDATE_SAVE_TO_INVENTORY)
	@ApiOperation(value = PathProxy.DoctorProfileUrls.UPDATE_SAVE_TO_INVENTORY, notes = PathProxy.DoctorProfileUrls.UPDATE_SAVE_TO_INVENTORY)
	public Response<Boolean> updateSaveToInventory(@PathVariable("doctorId") String doctorId,@PathVariable("locationId") String locationId,
			@RequestParam("saveToInventory") boolean saveToInventory) {
		if (DPDoctorUtils.anyStringEmpty(doctorId , locationId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		Response<Boolean> response = new Response<Boolean>();
		response.setData(doctorProfileService.updateSavetoInventory(doctorId, locationId, saveToInventory));
		return response;
	}
	
	
	
	@GetMapping(value = PathProxy.DoctorProfileUrls.UPDATE_SHOW_INVENTORY)
	@ApiOperation(value = PathProxy.DoctorProfileUrls.UPDATE_SHOW_INVENTORY, notes = PathProxy.DoctorProfileUrls.UPDATE_SHOW_INVENTORY)
	public Response<Boolean> updateShowInventory(@PathVariable("doctorId") String doctorId,@PathVariable("locationId") String locationId,
			@RequestParam("showInventory") boolean showInventory) {
		if (DPDoctorUtils.anyStringEmpty(doctorId , locationId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		Response<Boolean> response = new Response<Boolean>();
		response.setData(doctorProfileService.updateShowInventory(doctorId, locationId, showInventory));
		return response;
	}
	
	
	@GetMapping(value = PathProxy.DoctorProfileUrls.UPDATE_SHOW_INVENTORY_COUNT)
	@ApiOperation(value = PathProxy.DoctorProfileUrls.UPDATE_SHOW_INVENTORY_COUNT, notes = PathProxy.DoctorProfileUrls.UPDATE_SHOW_INVENTORY_COUNT)
	public Response<Boolean> updateShowInventoryCount(@PathVariable("doctorId") String doctorId,@PathVariable("locationId") String locationId,
			@RequestParam("showInventoryCount") boolean showInventoryCount) {
		if (DPDoctorUtils.anyStringEmpty(doctorId , locationId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		Response<Boolean> response = new Response<Boolean>();
		response.setData(doctorProfileService.updateShowInventoryCount(doctorId, locationId, showInventoryCount));
		return response;
	}
	
	
	@PostMapping(value = PathProxy.DoctorProfileUrls.ADD_EDIT_ONLINE_CONSULTATION_TIME)
	@ApiOperation(value = PathProxy.DoctorProfileUrls.ADD_EDIT_ONLINE_CONSULTATION_TIME, notes = PathProxy.DoctorProfileUrls.ADD_EDIT_ONLINE_CONSULTATION_TIME)
	public Response<Boolean> addEditOnlineConsultationTime(@RequestBody DoctorOnlineWorkingTimeRequest request) {
		if (request == null) {
			logger.warn("Doctor Contact Request Is Empty");
			throw new BusinessException(ServiceError.InvalidInput, "Doctor Contact Request Is Empty");
		} else if (DPDoctorUtils.anyStringEmpty(request.getDoctorId())) {
			logger.warn("Doctor Id Is Empty");
			throw new BusinessException(ServiceError.InvalidInput, "Doctor Id");
		}
		Boolean addEditOnlineWorkingTime = doctorProfileService.addEditOnlineWorkingTime(request);
		transnationalService.addResource(new ObjectId(request.getDoctorId()), Resource.DOCTOR, false);
		if (addEditOnlineWorkingTime!=null)
			transnationalService.checkDoctor(new ObjectId(request.getDoctorId()), null);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(addEditOnlineWorkingTime);
		return response;
	
	}
	
	@GetMapping(value = PathProxy.DoctorProfileUrls.GET_ONLINE_CONSULTATION_TIME)
	@ApiOperation(value = PathProxy.DoctorProfileUrls.GET_ONLINE_CONSULTATION_TIME, notes = PathProxy.DoctorProfileUrls.GET_ONLINE_CONSULTATION_TIME)	
	public Response<DoctorOnlineWorkingTimeRequest> getOnlineConsultationTime(@PathVariable("doctorId") String doctorId) {
		if (DPDoctorUtils.anyStringEmpty(doctorId)) {
			logger.warn("Doctor Id Is Empty");
			throw new BusinessException(ServiceError.InvalidInput, "Doctor Id Is Empty");
		}
		Response<DoctorOnlineWorkingTimeRequest> response = new Response<DoctorOnlineWorkingTimeRequest>();
		
		response.setData(doctorProfileService.getOnlineWorkTiming(doctorId));
	//	response.setCount(count);
		return response;
	}
		
	
	@PostMapping(value = PathProxy.DoctorProfileUrls.ADD_EDIT_ONLINE_CONSULTATION_FEES)
	@ApiOperation(value = PathProxy.DoctorProfileUrls.ADD_EDIT_ONLINE_CONSULTATION_FEES, notes = PathProxy.DoctorProfileUrls.ADD_EDIT_ONLINE_CONSULTATION_FEES)
	public Response<DoctorOnlineConsultationFees> addEditOnlineConsultationFees(@RequestBody DoctorOnlineConsultationFees request) {
		if (request == null) {
			logger.warn("Doctor Contact Request Is Empty");
			throw new BusinessException(ServiceError.InvalidInput, "Doctor Contact Request Is Empty");
		} else if (DPDoctorUtils.anyStringEmpty(request.getDoctorId())) {
			logger.warn("Doctor Id Is Empty");
			throw new BusinessException(ServiceError.InvalidInput, "Doctor Id Is Empty");
		}
		DoctorOnlineConsultationFees addEditVisitingTimeResponse = doctorProfileService.addEditOnlineConsultingFees(request);
		transnationalService.addResource(new ObjectId(request.getDoctorId()), Resource.DOCTOR, false);
		if (addEditVisitingTimeResponse!=null)
			transnationalService.checkDoctor(new ObjectId(request.getDoctorId()), null);
		Response<DoctorOnlineConsultationFees> response = new Response<DoctorOnlineConsultationFees>();
		response.setData(addEditVisitingTimeResponse);
		return response;
	
	}
	
	@GetMapping(value = PathProxy.DoctorProfileUrls.GET_ONLINE_CONSULTATION_FEES)
	@ApiOperation(value = PathProxy.DoctorProfileUrls.GET_ONLINE_CONSULTATION_FEES, notes = PathProxy.DoctorProfileUrls.GET_ONLINE_CONSULTATION_FEES)
	public Response<DoctorOnlineConsultationFees> getOnlineConsultationFees(@PathVariable("doctorId") String doctorId) {
		if (DPDoctorUtils.anyStringEmpty(doctorId)) {
			logger.warn("Doctor Id Is Empty");
			throw new BusinessException(ServiceError.InvalidInput, "Doctor Id Is Empty");
		}
		//	Integer count = doctorProfileService.countOnlineConsultingfees(discarded, searchTerm);
		Response<DoctorOnlineConsultationFees> response = new Response<DoctorOnlineConsultationFees>();
		
		response.setData(doctorProfileService.getOnlineConsultingfees(doctorId));
	//	response.setCount(count);
		return response;
	}

	
	@PostMapping(value = PathProxy.DoctorProfileUrls.UPLOAD_REGISTRATION_DETAILS)
	@ApiOperation(value = PathProxy.DoctorProfileUrls.UPLOAD_REGISTRATION_DETAILS, notes = PathProxy.DoctorProfileUrls.UPLOAD_REGISTRATION_DETAILS)
	public Response<String> uploadRegistrationDetails(DoctorRegistrationDetails request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		String addEditCoverPictureResponse = doctorProfileService.uploadRegistrationDetails(request);
		if (addEditCoverPictureResponse != null)
			transnationalService.checkDoctor(new ObjectId(request.getDoctorId()), null);
		addEditCoverPictureResponse = getFinalImageURL(addEditCoverPictureResponse);
		Response<String> response = new Response<String>();
		response.setData(addEditCoverPictureResponse);
		return response;
	}

	

}
