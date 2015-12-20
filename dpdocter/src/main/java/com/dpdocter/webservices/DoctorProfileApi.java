package com.dpdocter.webservices;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

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
import com.dpdocter.beans.WorkingSchedule;
import com.dpdocter.enums.Resource;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.request.DoctorAchievementAddEditRequest;
import com.dpdocter.request.DoctorAddEditIBSRequest;
import com.dpdocter.request.DoctorAppointmentNumbersAddEditRequest;
import com.dpdocter.request.DoctorAppointmentSlotAddEditRequest;
import com.dpdocter.request.DoctorConsultationFeeAddEditRequest;
import com.dpdocter.request.DoctorContactAddEditRequest;
import com.dpdocter.request.DoctorEducationAddEditRequest;
import com.dpdocter.request.DoctorExperienceAddEditRequest;
import com.dpdocter.request.DoctorExperienceDetailAddEditRequest;
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
import com.dpdocter.solr.services.SolrRegistrationService;
import common.util.web.DPDoctorUtils;
import common.util.web.Response;

@Component
@Path(PathProxy.DOCTOR_PROFILE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DoctorProfileApi {

    private static Logger logger = Logger.getLogger(DoctorProfileApi.class.getName());

    @Autowired
    private SolrRegistrationService solrRegistrationService;

    @Autowired
    private DoctorProfileService doctorProfileService;

    @Autowired
    private TransactionalManagementService transnationalService;

    @Context
    private UriInfo uriInfo;

    @Value(value = "${IMAGE_URL_ROOT_PATH}")
    private String imageUrlRootPath;

    @Path(value = PathProxy.DoctorProfileUrls.ADD_EDIT_NAME)
    @POST
    public Response<Boolean> addEditName(DoctorNameAddEditRequest request) {
	if (request == null) {
	    logger.warn("Request Cannot Be null");
	    throw new BusinessException(ServiceError.InvalidInput, "Request Cannot Be null");
	}
	Boolean addEditNameResponse = doctorProfileService.addEditName(request);
	if (addEditNameResponse)
	    solrRegistrationService.addEditName(request);
	Response<Boolean> response = new Response<Boolean>();
	response.setData(addEditNameResponse);
	return response;
    }

    @Path(value = PathProxy.DoctorProfileUrls.ADD_EDIT_EXPERIENCE)
    @POST
    public Response<Boolean> addEditExperience(DoctorExperienceAddEditRequest request) {
	if (request == null) {
	    logger.warn("Doctor Experience Request Is Empty");
	    throw new BusinessException(ServiceError.InvalidInput, "Doctor Experience Request Is Empty");
	}
	DoctorExperience experienceResponse = doctorProfileService.addEditExperience(request);
	if (experienceResponse != null)
	    solrRegistrationService.addEditExperience(request.getDoctorId(), experienceResponse);
	Response<Boolean> response = new Response<Boolean>();
	response.setData(true);
	return response;
    }

    @Path(value = PathProxy.DoctorProfileUrls.ADD_EDIT_CONTACT)
    @POST
    public Response<Boolean> addEditContact(DoctorContactAddEditRequest request) {
	if (request == null) {
	    logger.warn("Doctor Contact Request Is Empty");
	    throw new BusinessException(ServiceError.InvalidInput, "Doctor Contact Request Is Empty");
	}
	Boolean addEditContactResponse = doctorProfileService.addEditContact(request);
	Response<Boolean> response = new Response<Boolean>();
	response.setData(addEditContactResponse);
	return response;
    }

    @Path(value = PathProxy.DoctorProfileUrls.ADD_EDIT_EDUCATION)
    @POST
    public Response<Boolean> addEditEducation(DoctorEducationAddEditRequest request) {
	if (request == null) {
	    logger.warn("Doctor Education Request Is Empty");
	    throw new BusinessException(ServiceError.InvalidInput, "Doctor Education Request Is Empty");
	}
	Boolean addEditEducationResponse = doctorProfileService.addEditEducation(request);
	Response<Boolean> response = new Response<Boolean>();
	response.setData(addEditEducationResponse);
	return response;
    }

    @Path(value = PathProxy.DoctorProfileUrls.ADD_EDIT_MEDICAL_COUNCILS)
    @POST
    public Response<Boolean> addEditMedicalCouncils(List<MedicalCouncil> medicalCouncils) {
	if (medicalCouncils == null || medicalCouncils.isEmpty()) {
	    logger.warn("Medical Councils Cannot Be Empty");
	    throw new BusinessException(ServiceError.InvalidInput, "Medical Councils Cannot Be Empty");
	}
	Boolean addEditMedicalCouncil = doctorProfileService.addEditMedicalCouncils(medicalCouncils);
	Response<Boolean> response = new Response<Boolean>();
	response.setData(addEditMedicalCouncil);
	return response;
    }

    @Path(value = PathProxy.DoctorProfileUrls.GET_MEDICAL_COUNCILS)
    @GET
    public Response<MedicalCouncil> getMedicalCouncils() {
	List<MedicalCouncil> medicalCouncils = doctorProfileService.getMedicalCouncils();
	Response<MedicalCouncil> response = new Response<MedicalCouncil>();
	response.setDataList(medicalCouncils);
	return response;
    }

    @Path(value = PathProxy.DoctorProfileUrls.ADD_EDIT_SPECIALITY)
    @POST
    public Response<Boolean> addEditSpeciality(DoctorSpecialityAddEditRequest request) {
	if (request == null) {
	    logger.warn("Doctor Speciality Request Is Empty");
	    throw new BusinessException(ServiceError.InvalidInput, "Doctor Speciality Request Is Empty");
	}
	List<String> specialityResponse = doctorProfileService.addEditSpeciality(request);
	request.setSpeciality(specialityResponse);
	if (specialityResponse != null)
	    solrRegistrationService.addEditSpeciality(request);
	Response<Boolean> response = new Response<Boolean>();
	response.setData(true);
	return response;
    }

    @Path(value = PathProxy.DoctorProfileUrls.ADD_EDIT_ACHIEVEMENT)
    @POST
    public Response<Boolean> addEditAchievement(DoctorAchievementAddEditRequest request) {
	if (request == null) {
	    logger.warn("Doctor Achievement Request Is Empty");
	    throw new BusinessException(ServiceError.InvalidInput, "Doctor Achievement Request Is Empty");
	}
	Boolean addEditSpecialityResponse = doctorProfileService.addEditAchievement(request);
	Response<Boolean> response = new Response<Boolean>();
	response.setData(addEditSpecialityResponse);
	return response;
    }

    @Path(value = PathProxy.DoctorProfileUrls.ADD_EDIT_PROFESSIONAL_STATEMENT)
    @POST
    public Response<Boolean> addEditProfessionalStatement(DoctorProfessionalStatementAddEditRequest request) {
	if (request == null) {
	    logger.warn("Doctor Professional Request Is Empty");
	    throw new BusinessException(ServiceError.InvalidInput, "Doctor Professional Statement Request Is Empty");
	}
	Boolean addEditProfessionalStatementResponse = doctorProfileService.addEditProfessionalStatement(request);
	Response<Boolean> response = new Response<Boolean>();
	response.setData(addEditProfessionalStatementResponse);
	return response;
    }

    @Path(value = PathProxy.DoctorProfileUrls.ADD_EDIT_REGISTRATION_DETAIL)
    @POST
    public Response<Boolean> addEditRegistrationDetail(DoctorRegistrationAddEditRequest request) {
	if (request == null) {
	    logger.warn("Doctor Registration Detail Request Is Empty");
	    throw new BusinessException(ServiceError.InvalidInput, "Doctor Registration Detail Request Is Empty");
	}
	Boolean addEditRegistrationDetailResponse = doctorProfileService.addEditRegistrationDetail(request);
	Response<Boolean> response = new Response<Boolean>();
	response.setData(addEditRegistrationDetailResponse);
	return response;
    }

    @Path(value = PathProxy.DoctorProfileUrls.ADD_EDIT_EXPERIENCE_DETAIL)
    @POST
    public Response<Boolean> addEditExperienceDetail(DoctorExperienceDetailAddEditRequest request) {
	if (request == null) {
	    logger.warn("Doctor Experience Detail Request Is Empty");
	    throw new BusinessException(ServiceError.InvalidInput, "Doctor Experience Detail Request Is Empty");
	}
	Boolean addEditExperienceDetailResponse = doctorProfileService.addEditExperienceDetail(request);
	Response<Boolean> response = new Response<Boolean>();
	response.setData(addEditExperienceDetailResponse);
	return response;
    }

    @Path(value = PathProxy.DoctorProfileUrls.ADD_EDIT_PROFILE_PICTURE)
    @POST
    public Response<String> addEditProfilePicture(DoctorProfilePictureAddEditRequest request) {
	if (request == null) {
	    logger.warn("Doctor Profile Picture Request Is Empty");
	    throw new BusinessException(ServiceError.InvalidInput, "Doctor Profile Picture Request Is Empty");
	}
	String addEditProfilePictureResponse = doctorProfileService.addEditProfilePicture(request);
	transnationalService.addResource(request.getDoctorId(), Resource.DOCTOR, false);
	if (addEditProfilePictureResponse != null)
	    solrRegistrationService.addEditProfilePicture(request.getDoctorId(), addEditProfilePictureResponse);
	addEditProfilePictureResponse = getFinalImageURL(addEditProfilePictureResponse);
	Response<String> response = new Response<String>();
	response.setData(addEditProfilePictureResponse);
	return response;
    }

    @Path(value = PathProxy.DoctorProfileUrls.ADD_EDIT_COVER_PICTURE)
    @POST
    public Response<String> addEditCoverPicture(DoctorProfilePictureAddEditRequest request) {
	if (request == null) {
	    logger.warn("Doctor Profile Picture Request Is Empty");
	    throw new BusinessException(ServiceError.InvalidInput, "Doctor Profile Picture Request Is Empty");
	}
	String addEditCoverPictureResponse = doctorProfileService.addEditCoverPicture(request);
	addEditCoverPictureResponse = getFinalImageURL(addEditCoverPictureResponse);
	Response<String> response = new Response<String>();
	response.setData(addEditCoverPictureResponse);
	return response;
    }

    @Path(value = PathProxy.DoctorProfileUrls.ADD_EDIT_PROFESSIONAL_MEMBERSHIP)
    @POST
    public Response<Boolean> addEditProfessionalMembership(DoctorProfessionalAddEditRequest request) {
	if (request == null) {
	    logger.warn("Doctor Professional Membership Request Is Empty");
	    throw new BusinessException(ServiceError.InvalidInput, "Doctor Professional Membership Request Is Empty");
	}
	Boolean addEditProfessionalMembershipResponse = doctorProfileService.addEditProfessionalMembership(request);
	Response<Boolean> response = new Response<Boolean>();
	response.setData(addEditProfessionalMembershipResponse);
	return response;
    }

    @Path(value = PathProxy.DoctorProfileUrls.GET_DOCTOR_PROFILE)
    @GET
    public Response<DoctorProfile> getDoctorProfile(@PathParam("doctorId") String doctorId, @PathParam("locationId") String locationId,
	    @PathParam("hospitalId") String hospitalId) {
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

    @Path(value = PathProxy.DoctorProfileUrls.INSERT_PROFESSIONAL_MEMBERSHIPS)
    @POST
    public Response<Boolean> insertProfessionalMemberships(List<ProfessionalMembership> professionalMemberships) {
	if (professionalMemberships == null || professionalMemberships.isEmpty()) {
	    logger.warn("Professional Memberships Cannot Be Empty");
	    throw new BusinessException(ServiceError.InvalidInput, "Professional Memberships Cannot Be Empty");
	}
	Boolean insertProfessionalMembershipResponse = doctorProfileService.insertProfessionalMemberships(professionalMemberships);
	Response<Boolean> response = new Response<Boolean>();
	response.setData(insertProfessionalMembershipResponse);
	return response;
    }

    @Path(value = PathProxy.DoctorProfileUrls.GET_PROFESSIONAL_MEMBERSHIPS)
    @GET
    public Response<ProfessionalMembership> getProfessionalMemberships() {
	List<ProfessionalMembership> professionalMemberships = doctorProfileService.getProfessionalMemberships();
	Response<ProfessionalMembership> response = new Response<ProfessionalMembership>();
	response.setDataList(professionalMemberships);
	return response;
    }

    @Path(value = PathProxy.DoctorProfileUrls.ADD_EDIT_APPOINTMENT_NUMBERS)
    @POST
    public Response<Boolean> addEditAppointmentNumbers(DoctorAppointmentNumbersAddEditRequest request) {
	if (request == null) {
	    logger.warn("Doctor Contact Request Is Empty");
	    throw new BusinessException(ServiceError.InvalidInput, "Doctor Contact Request Is Empty");
	} else if (DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId())) {
	    logger.warn("Doctor Id, LocationId Is Empty");
	    throw new BusinessException(ServiceError.InvalidInput, "Doctor Id, LocationId Is Empty");
	}
	Boolean addEditAppointmentNumbersResponse = doctorProfileService.addEditAppointmentNumbers(request);
	Response<Boolean> response = new Response<Boolean>();
	response.setData(addEditAppointmentNumbersResponse);
	return response;
    }

    @Path(value = PathProxy.DoctorProfileUrls.ADD_EDIT_VISITING_TIME)
    @POST
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
	if (addEditVisitingTimeResponse)
	    solrRegistrationService.addEditVisitingTime(request);
	Response<Boolean> response = new Response<Boolean>();
	response.setData(addEditVisitingTimeResponse);
	return response;
    }

    @Path(value = PathProxy.DoctorProfileUrls.ADD_EDIT_CONSULTATION_FEE)
    @POST
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
	if (addEditConsultationFeeResponse)
	    solrRegistrationService.addEditConsultationFee(request);
	Response<Boolean> response = new Response<Boolean>();
	response.setData(addEditConsultationFeeResponse);
	return response;
    }

    @Path(value = PathProxy.DoctorProfileUrls.ADD_EDIT_APPOINTMENT_SLOT)
    @POST
    public Response<Boolean> addEditAppointmentSlot(DoctorAppointmentSlotAddEditRequest request) {
	if (request == null) {
	    logger.warn("Doctor Contact Request Is Empty");
	    throw new BusinessException(ServiceError.InvalidInput, "Doctor Contact Request Is Empty");
	} else if (DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId())) {
	    logger.warn("Doctor Id, LocationId Is Empty");
	    throw new BusinessException(ServiceError.InvalidInput, "Doctor Id, LocationId Is Empty");
	}
	Boolean addEditAppointmentSlotResponse = doctorProfileService.addEditAppointmentSlot(request);
	Response<Boolean> response = new Response<Boolean>();
	response.setData(addEditAppointmentSlotResponse);
	return response;
    }

    @Path(value = PathProxy.DoctorProfileUrls.ADD_EDIT_GENERAL_INFO)
    @POST
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
	if (addEditGeneralInfoResponse)
	    solrRegistrationService.addEditGeneralInfo(request);
	Response<Boolean> response = new Response<Boolean>();
	response.setData(addEditGeneralInfoResponse);
	return response;
    }

    private String getFinalImageURL(String imageURL) {
	if (imageURL != null) {
	    String finalImageURL = uriInfo.getBaseUri().toString().replace(uriInfo.getBaseUri().getPath(), imageUrlRootPath);
	    return finalImageURL + imageURL;
	} else
	    return null;
    }

    @Path(value = PathProxy.DoctorProfileUrls.GET_SPECIALITIES)
    @GET
    public Response<Speciality> getSpeciality() {
	List<Speciality> specialities = doctorProfileService.getSpecialities();
	Response<Speciality> response = new Response<Speciality>();
	response.setDataList(specialities);
	return response;
    }

    @Path(value = PathProxy.DoctorProfileUrls.GET_EDUCATION_INSTITUTES)
    @GET
    public Response<EducationInstitute> getEducationInstitutes(@QueryParam("page") int page, @QueryParam("size") int size) {
	List<EducationInstitute> educationInstitutes = doctorProfileService.getEducationInstitutes(page, size);
	Response<EducationInstitute> response = new Response<EducationInstitute>();
	response.setDataList(educationInstitutes);
	return response;
    }

    @Path(value = PathProxy.DoctorProfileUrls.GET_EDUCATION_QUALIFICATIONS)
    @GET
    public Response<EducationQualification> getEducationQualifications(@QueryParam("page") int page, @QueryParam("size") int size) {
	List<EducationQualification> qualifications = doctorProfileService.getEducationQualifications(page, size);
	Response<EducationQualification> response = new Response<EducationQualification>();
	response.setDataList(qualifications);
	return response;
    }

    @Path(value = PathProxy.DoctorProfileUrls.ADD_EDIT_MULTIPLE_DATA)
    @POST
    public Response<DoctorMultipleDataAddEditResponse> addEditMultipleData(DoctorMultipleDataAddEditRequest request) {
	if (request == null) {
	    logger.warn("Request Cannot Be null");
	    throw new BusinessException(ServiceError.InvalidInput, "Request Cannot Be null");
	}
	DoctorMultipleDataAddEditResponse addEditNameResponse = doctorProfileService.addEditMultipleData(request);
	transnationalService.addResource(request.getDoctorId(), Resource.DOCTOR, false);
	if (addEditNameResponse != null)
	    solrRegistrationService.addEditMultipleData(addEditNameResponse);
	addEditNameResponse.setCoverImageUrl(getFinalImageURL(addEditNameResponse.getCoverImageUrl()));
	addEditNameResponse.setProfileImageUrl(getFinalImageURL(addEditNameResponse.getProfileImageUrl()));
	addEditNameResponse.setThumbnailCoverImageUrl(getFinalImageURL(addEditNameResponse.getThumbnailCoverImageUrl()));
	addEditNameResponse.setThumbnailProfileImageUrl(getFinalImageURL(addEditNameResponse.getThumbnailProfileImageUrl()));

	Response<DoctorMultipleDataAddEditResponse> response = new Response<DoctorMultipleDataAddEditResponse>();
	response.setData(addEditNameResponse);
	return response;
    }

    @Path(value = PathProxy.DoctorProfileUrls.GET_TIME_SLOTS)
    @GET
    public Response<WorkingSchedule> getTimeSlots(@PathParam("doctorId") String doctorId, @PathParam("locationId") String locationId,
	    @QueryParam("day") String day) {
	if (DPDoctorUtils.anyStringEmpty(doctorId)) {
	    throw new BusinessException(ServiceError.InvalidInput, "Doctor Id Cannot Be Empty");
	}

	List<WorkingSchedule> workingSchedules = doctorProfileService.getTimeSlots(doctorId, locationId, day);
	Response<WorkingSchedule> response = new Response<WorkingSchedule>();
	response.setDataList(workingSchedules);
	return response;
    }

    @Path(value = PathProxy.DoctorProfileUrls.ON_OFF_IBS)
    @POST
    public Response<Boolean> addEditIBS(DoctorAddEditIBSRequest request) {
	if (request == null) {
	    logger.warn("Doctor IBS Request Is Empty");
	    throw new BusinessException(ServiceError.InvalidInput, "Doctor IBS Request Is Empty");
	} else if (DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId())) {
	    logger.warn("Doctor Id, LocationId Is Empty");
	    throw new BusinessException(ServiceError.InvalidInput, "Doctor Id, LocationId Is Empty");
	}

	Boolean addEditIBSResponse = doctorProfileService.addEditIBS(request);
	Response<Boolean> response = new Response<Boolean>();
	response.setData(addEditIBSResponse);
	return response;

    }
}
