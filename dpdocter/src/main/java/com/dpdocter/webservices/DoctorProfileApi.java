package com.dpdocter.webservices;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.dpdocter.beans.ClinicImage;
import com.dpdocter.beans.DoctorClinicProfile;
import com.dpdocter.beans.DoctorExperience;
import com.dpdocter.beans.DoctorGeneralInfo;
import com.dpdocter.beans.DoctorProfile;
import com.dpdocter.beans.EducationInstitute;
import com.dpdocter.beans.EducationQualification;
import com.dpdocter.beans.MedicalCouncil;
import com.dpdocter.beans.ProfessionalMembership;
import com.dpdocter.beans.Speciality;
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
import com.dpdocter.request.DoctorProfessionalAddEditRequest;
import com.dpdocter.request.DoctorProfessionalStatementAddEditRequest;
import com.dpdocter.request.DoctorProfilePictureAddEditRequest;
import com.dpdocter.request.DoctorRegistrationAddEditRequest;
import com.dpdocter.request.DoctorSpecialityAddEditRequest;
import com.dpdocter.request.DoctorVisitingTimeAddEditRequest;
import com.dpdocter.response.DoctorMultipleDataAddEditResponse;
import com.dpdocter.services.DoctorProfileService;
import com.dpdocter.services.TransactionalManagementService;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Component
@Path(PathProxy.DOCTOR_PROFILE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.DOCTOR_PROFILE_URL, description = "Endpoint for doctor profile")
public class DoctorProfileApi {

    private static Logger logger = Logger.getLogger(DoctorProfileApi.class.getName());

    @Autowired
    private DoctorProfileService doctorProfileService;

    @Autowired
    private TransactionalManagementService transnationalService;

    @Value(value = "${image.path}")
    private String imagePath;

    @Path(value = PathProxy.DoctorProfileUrls.ADD_EDIT_NAME)
    @POST
    @ApiOperation(value = PathProxy.DoctorProfileUrls.ADD_EDIT_NAME, notes = PathProxy.DoctorProfileUrls.ADD_EDIT_NAME)
    public Response<Boolean> addEditName(DoctorNameAddEditRequest request) {
	if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId())) {
	    logger.warn("Invalid Input");
	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
	}
	Boolean addEditNameResponse = doctorProfileService.addEditName(request);
	if (addEditNameResponse)transnationalService.checkDoctor(request.getDoctorId(), null);
	Response<Boolean> response = new Response<Boolean>();
	response.setData(addEditNameResponse);
	return response;
    }

    @Path(value = PathProxy.DoctorProfileUrls.ADD_EDIT_EXPERIENCE)
    @POST
    @ApiOperation(value = PathProxy.DoctorProfileUrls.ADD_EDIT_EXPERIENCE, notes = PathProxy.DoctorProfileUrls.ADD_EDIT_EXPERIENCE)
    public Response<Boolean> addEditExperience(DoctorExperienceAddEditRequest request) {
    	if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId())) {
    	    logger.warn("Invalid Input");
    	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
    	}
	DoctorExperience experienceResponse = doctorProfileService.addEditExperience(request);
	if (experienceResponse != null)transnationalService.checkDoctor(request.getDoctorId(), null);
	Response<Boolean> response = new Response<Boolean>();
	response.setData(true);
	return response;
    }

    @Path(value = PathProxy.DoctorProfileUrls.ADD_EDIT_CONTACT)
    @POST
    @ApiOperation(value = PathProxy.DoctorProfileUrls.ADD_EDIT_CONTACT, notes = PathProxy.DoctorProfileUrls.ADD_EDIT_CONTACT)
    public Response<Boolean> addEditContact(DoctorContactAddEditRequest request) {
    	if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId())) {
    	    logger.warn("Invalid Input");
    	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
    	}
	Boolean addEditContactResponse = doctorProfileService.addEditContact(request);
	Response<Boolean> response = new Response<Boolean>();
	response.setData(addEditContactResponse);
	return response;
    }

    @Path(value = PathProxy.DoctorProfileUrls.ADD_EDIT_EDUCATION)
    @POST
    @ApiOperation(value = PathProxy.DoctorProfileUrls.ADD_EDIT_EDUCATION, notes = PathProxy.DoctorProfileUrls.ADD_EDIT_EDUCATION)
    public Response<Boolean> addEditEducation(DoctorEducationAddEditRequest request) {
    	if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId())) {
    	    logger.warn("Invalid Input");
    	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
    	}
	Boolean addEditEducationResponse = doctorProfileService.addEditEducation(request);
	Response<Boolean> response = new Response<Boolean>();
	response.setData(addEditEducationResponse);
	return response;
    }

    @Path(value = PathProxy.DoctorProfileUrls.GET_MEDICAL_COUNCILS)
    @GET
    @ApiOperation(value = PathProxy.DoctorProfileUrls.GET_MEDICAL_COUNCILS, notes = PathProxy.DoctorProfileUrls.GET_MEDICAL_COUNCILS)
    public Response<MedicalCouncil> getMedicalCouncils(@QueryParam("page") int page, @QueryParam("size") int size,
	    @DefaultValue("0") @QueryParam(value = "updatedTime") String updatedTime) {
	List<MedicalCouncil> medicalCouncils = doctorProfileService.getMedicalCouncils(page, size, updatedTime);
	Response<MedicalCouncil> response = new Response<MedicalCouncil>();
	response.setDataList(medicalCouncils);
	return response;
    }

    @Path(value = PathProxy.DoctorProfileUrls.ADD_EDIT_SPECIALITY)
    @POST
    @ApiOperation(value = PathProxy.DoctorProfileUrls.ADD_EDIT_SPECIALITY, notes = PathProxy.DoctorProfileUrls.ADD_EDIT_SPECIALITY)
    public Response<Boolean> addEditSpeciality(DoctorSpecialityAddEditRequest request) {
    	if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId())) {
    	    logger.warn("Invalid Input");
    	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
    	}
	List<String> specialityResponse = doctorProfileService.addEditSpeciality(request);
	request.setSpeciality(specialityResponse);
	if (specialityResponse != null)transnationalService.checkDoctor(request.getDoctorId(), null);
	Response<Boolean> response = new Response<Boolean>();
	response.setData(true);
	return response;
    }

    @Path(value = PathProxy.DoctorProfileUrls.ADD_EDIT_ACHIEVEMENT)
    @POST
    @ApiOperation(value = PathProxy.DoctorProfileUrls.ADD_EDIT_ACHIEVEMENT, notes = PathProxy.DoctorProfileUrls.ADD_EDIT_ACHIEVEMENT)
    public Response<Boolean> addEditAchievement(DoctorAchievementAddEditRequest request) {
    	if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId())) {
    	    logger.warn("Invalid Input");
    	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
    	}
	Boolean addEditSpecialityResponse = doctorProfileService.addEditAchievement(request);
	Response<Boolean> response = new Response<Boolean>();
	response.setData(addEditSpecialityResponse);
	return response;
    }

    @Path(value = PathProxy.DoctorProfileUrls.ADD_EDIT_PROFESSIONAL_STATEMENT)
    @POST
    @ApiOperation(value = PathProxy.DoctorProfileUrls.ADD_EDIT_PROFESSIONAL_STATEMENT, notes = PathProxy.DoctorProfileUrls.ADD_EDIT_PROFESSIONAL_STATEMENT)
    public Response<Boolean> addEditProfessionalStatement(DoctorProfessionalStatementAddEditRequest request) {
    	if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId())) {
    	    logger.warn("Invalid Input");
    	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
    	}
	Boolean addEditProfessionalStatementResponse = doctorProfileService.addEditProfessionalStatement(request);
	Response<Boolean> response = new Response<Boolean>();
	response.setData(addEditProfessionalStatementResponse);
	return response;
    }

    @Path(value = PathProxy.DoctorProfileUrls.ADD_EDIT_REGISTRATION_DETAIL)
    @POST
    @ApiOperation(value = PathProxy.DoctorProfileUrls.ADD_EDIT_REGISTRATION_DETAIL, notes = PathProxy.DoctorProfileUrls.ADD_EDIT_REGISTRATION_DETAIL)
    public Response<Boolean> addEditRegistrationDetail(DoctorRegistrationAddEditRequest request) {
    	if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId())) {
    	    logger.warn("Invalid Input");
    	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
    	}
	Boolean addEditRegistrationDetailResponse = doctorProfileService.addEditRegistrationDetail(request);
	Response<Boolean> response = new Response<Boolean>();
	response.setData(addEditRegistrationDetailResponse);
	return response;
    }

    @Path(value = PathProxy.DoctorProfileUrls.ADD_EDIT_EXPERIENCE_DETAIL)
    @POST
    @ApiOperation(value = PathProxy.DoctorProfileUrls.ADD_EDIT_EXPERIENCE_DETAIL, notes = PathProxy.DoctorProfileUrls.ADD_EDIT_EXPERIENCE_DETAIL)
    public Response<Boolean> addEditExperienceDetail(DoctorExperienceDetailAddEditRequest request) {
    	if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId())) {
    	    logger.warn("Invalid Input");
    	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
    	}
	Boolean addEditExperienceDetailResponse = doctorProfileService.addEditExperienceDetail(request);
	Response<Boolean> response = new Response<Boolean>();
	response.setData(addEditExperienceDetailResponse);
	return response;
    }

    @Path(value = PathProxy.DoctorProfileUrls.ADD_EDIT_PROFILE_PICTURE)
    @POST
    @ApiOperation(value = PathProxy.DoctorProfileUrls.ADD_EDIT_PROFILE_PICTURE, notes = PathProxy.DoctorProfileUrls.ADD_EDIT_PROFILE_PICTURE)
    public Response<String> addEditProfilePicture(DoctorProfilePictureAddEditRequest request) {
    	if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId())) {
    	    logger.warn("Invalid Input");
    	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
    	}
	String addEditProfilePictureResponse = doctorProfileService.addEditProfilePicture(request);
	transnationalService.addResource(request.getDoctorId(), Resource.DOCTOR, false);
	if (addEditProfilePictureResponse != null)transnationalService.checkDoctor(request.getDoctorId(), null);
	addEditProfilePictureResponse = getFinalImageURL(addEditProfilePictureResponse);
	Response<String> response = new Response<String>();
	response.setData(addEditProfilePictureResponse);
	return response;
    }

    @Path(value = PathProxy.DoctorProfileUrls.ADD_EDIT_COVER_PICTURE)
    @POST
    @ApiOperation(value = PathProxy.DoctorProfileUrls.ADD_EDIT_COVER_PICTURE, notes = PathProxy.DoctorProfileUrls.ADD_EDIT_COVER_PICTURE)
    public Response<String> addEditCoverPicture(DoctorProfilePictureAddEditRequest request) {
    	if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId())) {
    	    logger.warn("Invalid Input");
    	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
    	}
	String addEditCoverPictureResponse = doctorProfileService.addEditCoverPicture(request);
	transnationalService.addResource(request.getDoctorId(), Resource.DOCTOR, false);
	if (addEditCoverPictureResponse != null)transnationalService.checkDoctor(request.getDoctorId(), null);
	addEditCoverPictureResponse = getFinalImageURL(addEditCoverPictureResponse);
	Response<String> response = new Response<String>();
	response.setData(addEditCoverPictureResponse);
	return response;
    }

    @Path(value = PathProxy.DoctorProfileUrls.ADD_EDIT_PROFESSIONAL_MEMBERSHIP)
    @POST
    @ApiOperation(value = PathProxy.DoctorProfileUrls.ADD_EDIT_PROFESSIONAL_MEMBERSHIP, notes = PathProxy.DoctorProfileUrls.ADD_EDIT_PROFESSIONAL_MEMBERSHIP)
    public Response<Boolean> addEditProfessionalMembership(DoctorProfessionalAddEditRequest request) {
    	if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId())) {
    	    logger.warn("Invalid Input");
    	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
    	}
	Boolean addEditProfessionalMembershipResponse = doctorProfileService.addEditProfessionalMembership(request);
	Response<Boolean> response = new Response<Boolean>();
	response.setData(addEditProfessionalMembershipResponse);
	return response;
    }

    @Path(value = PathProxy.DoctorProfileUrls.GET_DOCTOR_PROFILE)
    @GET
    @ApiOperation(value = PathProxy.DoctorProfileUrls.GET_DOCTOR_PROFILE, notes = PathProxy.DoctorProfileUrls.GET_DOCTOR_PROFILE)
    public Response<DoctorProfile> getDoctorProfile(@PathParam("doctorId") String doctorId, @QueryParam("locationId") String locationId,
	    @QueryParam("hospitalId") String hospitalId) {
	if (DPDoctorUtils.anyStringEmpty(doctorId)) {
	    logger.warn("Doctor Id Cannot Be Empty");
	    throw new BusinessException(ServiceError.InvalidInput, "Doctor Id Cannot Be Empty");
	}
	DoctorProfile doctorProfile = doctorProfileService.getDoctorProfile(doctorId, locationId, hospitalId);
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
		}
	    }
	}
	Response<DoctorProfile> response = new Response<DoctorProfile>();
	response.setData(doctorProfile);
	return response;
    }

    @Path(value = PathProxy.DoctorProfileUrls.GET_PROFESSIONAL_MEMBERSHIPS)
    @GET
    @ApiOperation(value = PathProxy.DoctorProfileUrls.GET_PROFESSIONAL_MEMBERSHIPS, notes = PathProxy.DoctorProfileUrls.GET_PROFESSIONAL_MEMBERSHIPS)
    public Response<ProfessionalMembership> getProfessionalMemberships(@QueryParam("page") int page, @QueryParam("size") int size,
	    @DefaultValue("0") @QueryParam(value = "updatedTime") String updatedTime) {
	List<ProfessionalMembership> professionalMemberships = doctorProfileService.getProfessionalMemberships(page, size, updatedTime);
	Response<ProfessionalMembership> response = new Response<ProfessionalMembership>();
	response.setDataList(professionalMemberships);
	return response;
    }

    @Path(value = PathProxy.DoctorProfileUrls.ADD_EDIT_APPOINTMENT_NUMBERS)
    @POST
    @ApiOperation(value = PathProxy.DoctorProfileUrls.ADD_EDIT_APPOINTMENT_NUMBERS, notes = PathProxy.DoctorProfileUrls.ADD_EDIT_APPOINTMENT_NUMBERS)
    public Response<Boolean> addEditAppointmentNumbers(DoctorAppointmentNumbersAddEditRequest request) {
	if (request == null) {
	    logger.warn("Doctor Contact Request Is Empty");
	    throw new BusinessException(ServiceError.InvalidInput, "Doctor Contact Request Is Empty");
	} else if (DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId())) {
	    logger.warn("Doctor Id, LocationId Is Empty");
	    throw new BusinessException(ServiceError.InvalidInput, "Doctor Id, LocationId Is Empty");
	}
	Boolean addEditAppointmentNumbersResponse = doctorProfileService.addEditAppointmentNumbers(request);
	transnationalService.addResource(request.getDoctorId(), Resource.DOCTOR, false);
	if (addEditAppointmentNumbersResponse)  transnationalService.checkDoctor(request.getDoctorId(), null);
	
	Response<Boolean> response = new Response<Boolean>();
	response.setData(addEditAppointmentNumbersResponse);
	return response;
    }

    @Path(value = PathProxy.DoctorProfileUrls.ADD_EDIT_VISITING_TIME)
    @POST
    @ApiOperation(value = PathProxy.DoctorProfileUrls.ADD_EDIT_VISITING_TIME, notes = PathProxy.DoctorProfileUrls.ADD_EDIT_VISITING_TIME)
    public Response<Boolean> addEditVisitingTime(DoctorVisitingTimeAddEditRequest request) {
	if (request == null) {
	    logger.warn("Doctor Contact Request Is Empty");
	    throw new BusinessException(ServiceError.InvalidInput, "Doctor Contact Request Is Empty");
	} else if (DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId())) {
	    logger.warn("Doctor Id, LocationId Is Empty");
	    throw new BusinessException(ServiceError.InvalidInput, "Doctor Id, LocationId Is Empty");
	}
	Boolean addEditVisitingTimeResponse = doctorProfileService.addEditVisitingTime(request);
	transnationalService.addResource(request.getDoctorId(), Resource.DOCTOR, false);
	if (addEditVisitingTimeResponse)  transnationalService.checkDoctor(request.getDoctorId(), null);
	Response<Boolean> response = new Response<Boolean>();
	response.setData(addEditVisitingTimeResponse);
	return response;
    }

    @Path(value = PathProxy.DoctorProfileUrls.ADD_EDIT_CONSULTATION_FEE)
    @POST
    @ApiOperation(value = PathProxy.DoctorProfileUrls.ADD_EDIT_CONSULTATION_FEE, notes = PathProxy.DoctorProfileUrls.ADD_EDIT_CONSULTATION_FEE)
    public Response<Boolean> addEditConsultationFee(DoctorConsultationFeeAddEditRequest request) {
	if (request == null) {
	    logger.warn("Doctor Contact Request Is Empty");
	    throw new BusinessException(ServiceError.InvalidInput, "Doctor Contact Request Is Empty");
	} else if (DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId())) {
	    logger.warn("Doctor Id, LocationId Is Empty");
	    throw new BusinessException(ServiceError.InvalidInput, "Doctor Id, LocationId Is Empty");
	}
	Boolean addEditConsultationFeeResponse = doctorProfileService.addEditConsultationFee(request);
	transnationalService.addResource(request.getDoctorId(), Resource.DOCTOR, false);
	if (addEditConsultationFeeResponse)transnationalService.checkDoctor(request.getDoctorId(), null);
	Response<Boolean> response = new Response<Boolean>();
	response.setData(addEditConsultationFeeResponse);
	return response;
    }

    @Path(value = PathProxy.DoctorProfileUrls.ADD_EDIT_APPOINTMENT_SLOT)
    @POST
    @ApiOperation(value = PathProxy.DoctorProfileUrls.ADD_EDIT_APPOINTMENT_SLOT, notes = PathProxy.DoctorProfileUrls.ADD_EDIT_APPOINTMENT_SLOT)
    public Response<Boolean> addEditAppointmentSlot(DoctorAppointmentSlotAddEditRequest request) {
	if (request == null) {
	    logger.warn("Doctor Contact Request Is Empty");
	    throw new BusinessException(ServiceError.InvalidInput, "Doctor Contact Request Is Empty");
	} else if (DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId())) {
	    logger.warn("Doctor Id, LocationId Is Empty");
	    throw new BusinessException(ServiceError.InvalidInput, "Doctor Id, LocationId Is Empty");
	}
	Boolean addEditAppointmentSlotResponse = doctorProfileService.addEditAppointmentSlot(request);
	transnationalService.addResource(request.getDoctorId(), Resource.DOCTOR, false);
	if (addEditAppointmentSlotResponse)transnationalService.checkDoctor(request.getDoctorId(), null);
	Response<Boolean> response = new Response<Boolean>();
	response.setData(addEditAppointmentSlotResponse);
	return response;
    }

    @Path(value = PathProxy.DoctorProfileUrls.ADD_EDIT_GENERAL_INFO)
    @POST
    @ApiOperation(value = PathProxy.DoctorProfileUrls.ADD_EDIT_GENERAL_INFO, notes = PathProxy.DoctorProfileUrls.ADD_EDIT_GENERAL_INFO)
    public Response<Boolean> addEditGeneralInfo(DoctorGeneralInfo request) {
	if (request == null) {
	    logger.warn("Doctor Contact Request Is Empty");
	    throw new BusinessException(ServiceError.InvalidInput, "Doctor Contact Request Is Empty");
	} else if (DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId())) {
	    logger.warn("Doctor Id, LocationId Is Empty");
	    throw new BusinessException(ServiceError.InvalidInput, "Doctor Id, LocationId Is Empty");
	}

	Boolean addEditGeneralInfoResponse = doctorProfileService.addEditGeneralInfo(request);
	transnationalService.addResource(request.getDoctorId(), Resource.DOCTOR, false);
	if (addEditGeneralInfoResponse)transnationalService.checkDoctor(request.getDoctorId(), null);
	Response<Boolean> response = new Response<Boolean>();
	response.setData(addEditGeneralInfoResponse);
	return response;
    }

    private String getFinalImageURL(String imageURL) {
	if (imageURL != null) {
	    return imagePath + imageURL;
	} else
	    return null;
    }

    @Path(value = PathProxy.DoctorProfileUrls.GET_SPECIALITIES)
    @GET
    @ApiOperation(value = PathProxy.DoctorProfileUrls.GET_SPECIALITIES, notes = PathProxy.DoctorProfileUrls.GET_SPECIALITIES)
    public Response<Speciality> getSpeciality(@QueryParam("page") int page, @QueryParam("size") int size,
	    @DefaultValue("0") @QueryParam(value = "updatedTime") String updatedTime) {
	List<Speciality> specialities = doctorProfileService.getSpecialities(page, size, updatedTime);
	Response<Speciality> response = new Response<Speciality>();
	response.setDataList(specialities);
	return response;
    }

    @Path(value = PathProxy.DoctorProfileUrls.GET_EDUCATION_INSTITUTES)
    @GET
    @ApiOperation(value = PathProxy.DoctorProfileUrls.GET_EDUCATION_INSTITUTES, notes = PathProxy.DoctorProfileUrls.GET_EDUCATION_INSTITUTES)
    public Response<EducationInstitute> getEducationInstitutes(@QueryParam("page") int page, @QueryParam("size") int size,
	    @DefaultValue("0") @QueryParam(value = "updatedTime") String updatedTime) {
	List<EducationInstitute> educationInstitutes = doctorProfileService.getEducationInstitutes(page, size, updatedTime);
	Response<EducationInstitute> response = new Response<EducationInstitute>();
	response.setDataList(educationInstitutes);
	return response;
    }

    @Path(value = PathProxy.DoctorProfileUrls.GET_EDUCATION_QUALIFICATIONS)
    @GET
    @ApiOperation(value = PathProxy.DoctorProfileUrls.GET_EDUCATION_QUALIFICATIONS, notes = PathProxy.DoctorProfileUrls.GET_EDUCATION_QUALIFICATIONS)
    public Response<EducationQualification> getEducationQualifications(@QueryParam("page") int page, @QueryParam("size") int size,
	    @DefaultValue("0") @QueryParam(value = "updatedTime") String updatedTime) {
	List<EducationQualification> qualifications = doctorProfileService.getEducationQualifications(page, size, updatedTime);
	Response<EducationQualification> response = new Response<EducationQualification>();
	response.setDataList(qualifications);
	return response;
    }

    @Path(value = PathProxy.DoctorProfileUrls.ADD_EDIT_MULTIPLE_DATA)
    @POST
    @ApiOperation(value = PathProxy.DoctorProfileUrls.ADD_EDIT_MULTIPLE_DATA, notes = PathProxy.DoctorProfileUrls.ADD_EDIT_MULTIPLE_DATA)
    public Response<DoctorMultipleDataAddEditResponse> addEditMultipleData(DoctorMultipleDataAddEditRequest request) {
	if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId())) {
	    logger.warn("Request Cannot Be null");
	    throw new BusinessException(ServiceError.InvalidInput, "Request Cannot Be null");
	}
	DoctorMultipleDataAddEditResponse addEditNameResponse = doctorProfileService.addEditMultipleData(request);
	transnationalService.addResource(request.getDoctorId(), Resource.DOCTOR, false);
	if (addEditNameResponse != null)transnationalService.checkDoctor(request.getDoctorId(), null);
	addEditNameResponse.setCoverImageUrl(getFinalImageURL(addEditNameResponse.getCoverImageUrl()));
	addEditNameResponse.setProfileImageUrl(getFinalImageURL(addEditNameResponse.getProfileImageUrl()));
	addEditNameResponse.setThumbnailCoverImageUrl(getFinalImageURL(addEditNameResponse.getThumbnailCoverImageUrl()));
	addEditNameResponse.setThumbnailProfileImageUrl(getFinalImageURL(addEditNameResponse.getThumbnailProfileImageUrl()));

	Response<DoctorMultipleDataAddEditResponse> response = new Response<DoctorMultipleDataAddEditResponse>();
	response.setData(addEditNameResponse);
	return response;
    }

    @Path(value = PathProxy.DoctorProfileUrls.ADD_EDIT_FACILITY)
    @POST
    @ApiOperation(value = PathProxy.DoctorProfileUrls.ADD_EDIT_FACILITY, notes = PathProxy.DoctorProfileUrls.ADD_EDIT_FACILITY)
    public Response<Boolean> addEditFacility(DoctorAddEditFacilityRequest request) {
	if (request == null) {
	    logger.warn("Request Is Empty");
	    throw new BusinessException(ServiceError.InvalidInput, "Request Is Empty");
	} else if (DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId())) {
	    logger.warn("Doctor Id, LocationId Is Empty");
	    throw new BusinessException(ServiceError.InvalidInput, "Doctor Id, LocationId Is Empty");
	}

	Boolean addEditIBSResponse = doctorProfileService.addEditFacility(request);
	if (addEditIBSResponse)transnationalService.checkDoctor(request.getDoctorId(), null);
	transnationalService.checkDoctor(request.getDoctorId(), null);
	Response<Boolean> response = new Response<Boolean>();
	response.setData(addEditIBSResponse);
	return response;

    }

    @Path(value = PathProxy.DoctorProfileUrls.ADD_EDIT_GENDER)
    @POST
    @ApiOperation(value = PathProxy.DoctorProfileUrls.ADD_EDIT_GENDER, notes = PathProxy.DoctorProfileUrls.ADD_EDIT_GENDER)
    public Response<Boolean> addEditGender(DoctorGenderAddEditRequest request) {
    	if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId())) {
    	    logger.warn("Invalid Input");
    	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
    	}
	Boolean addEditNameResponse = doctorProfileService.addEditGender(request);
	if (addEditNameResponse)transnationalService.checkDoctor(request.getDoctorId(), null);
	Response<Boolean> response = new Response<Boolean>();
	response.setData(addEditNameResponse);
	return response;
    }

    @Path(value = PathProxy.DoctorProfileUrls.ADD_EDIT_DOB)
    @POST
    @ApiOperation(value = PathProxy.DoctorProfileUrls.ADD_EDIT_DOB, notes = PathProxy.DoctorProfileUrls.ADD_EDIT_DOB)
    public Response<Boolean> addEditDOB(DoctorDOBAddEditRequest request) {
    	if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId())) {
    	    logger.warn("Invalid Input");
    	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
    	}
	Boolean addEditNameResponse = doctorProfileService.addEditDOB(request);
	if (addEditNameResponse)transnationalService.checkDoctor(request.getDoctorId(), null);
	Response<Boolean> response = new Response<Boolean>();
	response.setData(addEditNameResponse);
	return response;
    }
}
