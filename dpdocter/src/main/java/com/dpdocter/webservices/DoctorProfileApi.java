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

import com.dpdocter.beans.MedicalCouncil;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.request.DoctorAchievementAddEditRequest;
import com.dpdocter.request.DoctorContactAddEditRequest;
import com.dpdocter.request.DoctorEducationAddEditRequest;
import com.dpdocter.request.DoctorExperienceAddEditRequest;
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

	@Autowired
	private DoctorProfileService doctorProfileService;

	@Path(value = PathProxy.DoctorProfileUrls.ADD_EDIT_NAME)
	@GET
	public Response<Boolean> addEditName(@PathParam("doctorId") String doctorId, @PathParam("title") String title, @PathParam("fname") String fname,
			@PathParam("mname") String mname, @PathParam("lname") String lname) {
		Boolean addEditNameResponse = doctorProfileService.addEditName(doctorId, title, fname, mname, lname);
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
	public Response<Boolean> addEditMedicalCouncil(List<MedicalCouncil> medicalCouncils) {
		if (medicalCouncils == null || medicalCouncils.isEmpty()) {
			throw new BusinessException(ServiceError.InvalidInput, "Medical Councils Cannot Be Empty");
		}
		Boolean addEditMedicalCouncil = doctorProfileService.addEditMedicalCouncil(medicalCouncils);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(addEditMedicalCouncil);
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

}
