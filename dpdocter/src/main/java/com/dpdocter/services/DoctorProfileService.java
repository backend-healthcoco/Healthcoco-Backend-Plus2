package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.MedicalCouncil;
import com.dpdocter.request.DoctorAchievementAddEditRequest;
import com.dpdocter.request.DoctorContactAddEditRequest;
import com.dpdocter.request.DoctorEducationAddEditRequest;
import com.dpdocter.request.DoctorExperienceAddEditRequest;
import com.dpdocter.request.DoctorProfilePictureAddEditRequest;
import com.dpdocter.request.DoctorRegistrationAddEditRequest;
import com.dpdocter.request.DoctorSpecialityAddEditRequest;

public interface DoctorProfileService {

	Boolean addEditName(String doctorId, String title, String fname, String mname, String lname);

	Boolean addEditExperience(String doctorId, String experience);

	Boolean addEditContact(DoctorContactAddEditRequest request);

	Boolean addEditEducation(DoctorEducationAddEditRequest request);

	Boolean addEditMedicalCouncil(List<MedicalCouncil> medicalCouncils);

	Boolean addEditSpeciality(DoctorSpecialityAddEditRequest request);

	Boolean addEditAchievement(DoctorAchievementAddEditRequest request);

	Boolean addEditProfessionalStatement(String doctorId, String professionalStatement);

	Boolean addEditRegistrationDetail(DoctorRegistrationAddEditRequest request);

	Boolean addEditExperienceDetail(DoctorExperienceAddEditRequest request);

	String addEditProfilePicture(DoctorProfilePictureAddEditRequest request);

}
