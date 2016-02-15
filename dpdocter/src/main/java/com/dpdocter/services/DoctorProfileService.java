package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.DoctorExperience;
import com.dpdocter.beans.DoctorGeneralInfo;
import com.dpdocter.beans.DoctorProfile;
import com.dpdocter.beans.EducationInstitute;
import com.dpdocter.beans.EducationQualification;
import com.dpdocter.beans.MedicalCouncil;
import com.dpdocter.beans.ProfessionalMembership;
import com.dpdocter.beans.Speciality;
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

public interface DoctorProfileService {

    Boolean addEditName(DoctorNameAddEditRequest request);

    DoctorExperience addEditExperience(DoctorExperienceAddEditRequest request);

    Boolean addEditContact(DoctorContactAddEditRequest request);

    Boolean addEditEducation(DoctorEducationAddEditRequest request);

    Boolean addEditMedicalCouncils(List<MedicalCouncil> medicalCouncils);

    List<MedicalCouncil> getMedicalCouncils(int page, int size, String updatedTime);

    List<String> addEditSpeciality(DoctorSpecialityAddEditRequest request);

    Boolean addEditAchievement(DoctorAchievementAddEditRequest request);

    Boolean addEditProfessionalStatement(DoctorProfessionalStatementAddEditRequest request);

    Boolean addEditRegistrationDetail(DoctorRegistrationAddEditRequest request);

    Boolean addEditExperienceDetail(DoctorExperienceDetailAddEditRequest request);

    String addEditProfilePicture(DoctorProfilePictureAddEditRequest request);

    String addEditCoverPicture(DoctorProfilePictureAddEditRequest request);

    DoctorProfile getDoctorProfile(String doctorId, String locationId, String hospitalId);

    Boolean insertProfessionalMemberships(List<ProfessionalMembership> professionalMemberships);

    List<ProfessionalMembership> getProfessionalMemberships(int page, int size, String updatedTime);

    Boolean addEditProfessionalMembership(DoctorProfessionalAddEditRequest request);

    Boolean addEditAppointmentNumbers(DoctorAppointmentNumbersAddEditRequest request);

    Boolean addEditVisitingTime(DoctorVisitingTimeAddEditRequest request);

    Boolean addEditConsultationFee(DoctorConsultationFeeAddEditRequest request);

    Boolean addEditAppointmentSlot(DoctorAppointmentSlotAddEditRequest request);

    Boolean addEditGeneralInfo(DoctorGeneralInfo request);

    List<Speciality> getSpecialities(int page, int size, String updatedTime);

    List<EducationInstitute> getEducationInstitutes(int page, int size, String updatedTime);

    List<EducationQualification> getEducationQualifications(int page, int size, String updatedTime);

    DoctorMultipleDataAddEditResponse addEditMultipleData(DoctorMultipleDataAddEditRequest request);

    Boolean addEditFacility(DoctorAddEditFacilityRequest request);

	Boolean addEditGender(DoctorGenderAddEditRequest request);

	Boolean addEditDOB(DoctorDOBAddEditRequest request);

}
