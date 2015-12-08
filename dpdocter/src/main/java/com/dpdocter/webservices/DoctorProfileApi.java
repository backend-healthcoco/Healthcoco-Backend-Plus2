package com.dpdocter.webservices;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dpdocter.beans.DoctorClinicProfile;
import com.dpdocter.beans.DoctorProfile;
import com.dpdocter.beans.MedicalCouncil;
import com.dpdocter.beans.ProfessionalMembership;
import com.dpdocter.beans.WorkingSchedule;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.request.DoctorAchievementAddEditRequest;
import com.dpdocter.request.DoctorAddEditIBSRequest;
import com.dpdocter.request.DoctorContactAddEditRequest;
import com.dpdocter.request.DoctorEducationAddEditRequest;
import com.dpdocter.request.DoctorExperienceAddEditRequest;
import com.dpdocter.request.DoctorNameAddEditRequest;
import com.dpdocter.request.DoctorProfessionalAddEditRequest;
import com.dpdocter.request.DoctorProfilePictureAddEditRequest;
import com.dpdocter.request.DoctorRegistrationAddEditRequest;
import com.dpdocter.request.DoctorSpecialityAddEditRequest;
import com.dpdocter.services.DoctorProfileService;
import common.util.web.DPDoctorUtils;
import common.util.web.Response;

@Component
@Path(PathProxy.DOCTOR_PROFILE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DoctorProfileApi {

	private static Logger logger = Logger.getLogger(DoctorProfileApi.class.getName());
	
    @Autowired
    private DoctorProfileService doctorProfileService;

    @Path(value = PathProxy.DoctorProfileUrls.ADD_EDIT_NAME)
    @POST
    public Response<Boolean> addEditName(DoctorNameAddEditRequest request) {
	Boolean addEditNameResponse = doctorProfileService.addEditName(request);
	Response<Boolean> response = new Response<Boolean>();
	response.setData(addEditNameResponse);
	return response;
    }

    @Path(value = PathProxy.DoctorProfileUrls.ADD_EDIT_EXPERIENCE)
    @GET
    public Response<Boolean> addEditExperience(@PathParam("doctorId") String doctorId, @PathParam("experience") String experience) {
	if (DPDoctorUtils.anyStringEmpty(doctorId, experience)) {
	    throw new BusinessException(ServiceError.InvalidInput, "Doctor Id, Experience Cannot Be Empty");
	}
	Boolean addEditExperienceResponse = doctorProfileService.addEditExperience(doctorId, experience);
	Response<Boolean> response = new Response<Boolean>();
	response.setData(addEditExperienceResponse);
	return response;
    }

    @Path(value = PathProxy.DoctorProfileUrls.ADD_EDIT_CONTACT)
    @POST
    public Response<Boolean> addEditContact(DoctorContactAddEditRequest request) {
	if (request == null) {
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
	    throw new BusinessException(ServiceError.InvalidInput, "Doctor Speciality Request Is Empty");
	}
	Boolean addEditSpecialityResponse = doctorProfileService.addEditSpeciality(request);
	Response<Boolean> response = new Response<Boolean>();
	response.setData(addEditSpecialityResponse);
	return response;
    }

    @Path(value = PathProxy.DoctorProfileUrls.ADD_EDIT_ACHIEVEMENT)
    @POST
    public Response<Boolean> addEditAchievement(DoctorAchievementAddEditRequest request) {
	if (request == null) {
	    throw new BusinessException(ServiceError.InvalidInput, "Doctor Achievement Request Is Empty");
	}
	Boolean addEditSpecialityResponse = doctorProfileService.addEditAchievement(request);
	Response<Boolean> response = new Response<Boolean>();
	response.setData(addEditSpecialityResponse);
	return response;
    }

    @Path(value = PathProxy.DoctorProfileUrls.ADD_EDIT_PROFESSIONAL_STATEMENT)
    @GET
    public Response<Boolean> addEditProfessionalStatement(@PathParam("doctorId") String doctorId,
	    @PathParam("professionalStatement") String professionalStatement) {
	Boolean addEditProfessionalStatementResponse = doctorProfileService.addEditProfessionalStatement(doctorId, professionalStatement);
	Response<Boolean> response = new Response<Boolean>();
	response.setData(addEditProfessionalStatementResponse);
	return response;
    }

    @Path(value = PathProxy.DoctorProfileUrls.ADD_EDIT_REGISTRATION_DETAIL)
    @POST
    public Response<Boolean> addEditRegistrationDetail(DoctorRegistrationAddEditRequest request) {
	if (request == null) {
	    throw new BusinessException(ServiceError.InvalidInput, "Doctor Registration Detail Request Is Empty");
	}
	Boolean addEditRegistrationDetailResponse = doctorProfileService.addEditRegistrationDetail(request);
	Response<Boolean> response = new Response<Boolean>();
	response.setData(addEditRegistrationDetailResponse);
	return response;
    }

    @Path(value = PathProxy.DoctorProfileUrls.ADD_EDIT_EXPERIENCE_DETAIL)
    @POST
    public Response<Boolean> addEditExperienceDetail(DoctorExperienceAddEditRequest request) {
	if (request == null) {
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
	    throw new BusinessException(ServiceError.InvalidInput, "Doctor Profile Picture Request Is Empty");
	}
	String addEditProfilePictureResponse = doctorProfileService.addEditProfilePicture(request);
	Response<String> response = new Response<String>();
	response.setData(addEditProfilePictureResponse);
	return response;
    }

    @Path(value = PathProxy.DoctorProfileUrls.ADD_EDIT_PROFESSIONAL_MEMBERSHIP)
    @POST
    public Response<Boolean> addEditProfessionalMembership(DoctorProfessionalAddEditRequest request) {
	if (request == null) {
	    throw new BusinessException(ServiceError.InvalidInput, "Doctor Professional Membership Request Is Empty");
	}
	Boolean addEditProfessionalMembershipResponse = doctorProfileService.addEditProfessionalMembership(request);
	Response<Boolean> response = new Response<Boolean>();
	response.setData(addEditProfessionalMembershipResponse);
	return response;
    }

    @Path(value = PathProxy.DoctorProfileUrls.GET_DOCTOR_PROFILE)
    @GET
    public Response<DoctorProfile> getDoctorProfile(@PathParam("doctorId") String doctorId, @QueryParam("locationId") String locationId,
	    @QueryParam("hospitalId") String hospitalId) {
	if (DPDoctorUtils.anyStringEmpty(doctorId)) {
	    throw new BusinessException(ServiceError.InvalidInput, "Doctor Id Cannot Be Empty");
	}
	DoctorProfile doctorProfile = doctorProfileService.getDoctorProfile(doctorId, locationId, hospitalId);
	Response<DoctorProfile> response = new Response<DoctorProfile>();
	response.setData(doctorProfile);
	return response;
    }

    @Path(value = PathProxy.DoctorProfileUrls.INSERT_PROFESSIONAL_MEMBERSHIPS)
    @POST
    public Response<Boolean> insertProfessionalMemberships(List<ProfessionalMembership> professionalMemberships) {
	if (professionalMemberships == null || professionalMemberships.isEmpty()) {
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
    public Response<Boolean> addEditAppointmentNumbers(DoctorClinicProfile request) {
	if (DPDoctorUtils.anyStringEmpty(request.getUserLocationId())) {
	    throw new BusinessException(ServiceError.InvalidInput, "User Location Id Cannot Be Empty");
	}
	Boolean addEditAppointmentNumbersResponse = doctorProfileService.addEditAppointmentNumbers(request);
	Response<Boolean> response = new Response<Boolean>();
	response.setData(addEditAppointmentNumbersResponse);
	return response;
    }

    @Path(value = PathProxy.DoctorProfileUrls.ADD_EDIT_VISITING_TIME)
    @POST
    public Response<Boolean> addEditVisitingTime(DoctorClinicProfile request) {
	if (DPDoctorUtils.anyStringEmpty(request.getUserLocationId())) {
	    throw new BusinessException(ServiceError.InvalidInput, "User Location Id Cannot Be Empty");
	}
	Boolean addEditVisitingTimeResponse = doctorProfileService.addEditVisitingTime(request);
	Response<Boolean> response = new Response<Boolean>();
	response.setData(addEditVisitingTimeResponse);
	return response;
    }

    @Path(value = PathProxy.DoctorProfileUrls.ADD_EDIT_CONSULTATION_FEE)
    @POST
    public Response<Boolean> addEditConsultationFee(DoctorClinicProfile request) {
	if (DPDoctorUtils.anyStringEmpty(request.getUserLocationId())) {
	    throw new BusinessException(ServiceError.InvalidInput, "User Location Id Cannot Be Empty");
	}
	Boolean addEditConsultationFeeResponse = doctorProfileService.addEditConsultationFee(request);
	Response<Boolean> response = new Response<Boolean>();
	response.setData(addEditConsultationFeeResponse);
	return response;
    }

    @Path(value = PathProxy.DoctorProfileUrls.ADD_EDIT_APPOINTMENT_SLOT)
    @POST
    public Response<Boolean> addEditAppointmentSlot(DoctorClinicProfile request) {
	if (DPDoctorUtils.anyStringEmpty(request.getUserLocationId())) {
	    throw new BusinessException(ServiceError.InvalidInput, "User Location Id Cannot Be Empty");
	}
	Boolean addEditAppointmentSlotResponse = doctorProfileService.addEditAppointmentSlot(request);
	Response<Boolean> response = new Response<Boolean>();
	response.setData(addEditAppointmentSlotResponse);
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
