package com.dpdocter.services;

import java.util.List;
import com.dpdocter.beans.DoctorGeneralInfo;
import com.dpdocter.beans.DoctorProfile;
import com.dpdocter.beans.MedicalCouncil;
import com.dpdocter.beans.ProfessionalMembership;
import com.dpdocter.request.DoctorAchievementAddEditRequest;
import com.dpdocter.request.DoctorAppointmentNumbersAddEditRequest;
import com.dpdocter.request.DoctorAppointmentSlotAddEditRequest;
import com.dpdocter.request.DoctorConsultationFeeAddEditRequest;
import com.dpdocter.request.DoctorContactAddEditRequest;
import com.dpdocter.request.DoctorEducationAddEditRequest;
import com.dpdocter.request.DoctorExperienceAddEditRequest;
import com.dpdocter.request.DoctorNameAddEditRequest;
import com.dpdocter.request.DoctorProfessionalAddEditRequest;
import com.dpdocter.request.DoctorProfessionalStatementAddEditRequest;
import com.dpdocter.request.DoctorProfilePictureAddEditRequest;
import com.dpdocter.request.DoctorRegistrationAddEditRequest;
import com.dpdocter.request.DoctorSpecialityAddEditRequest;
import com.dpdocter.request.DoctorVisitingTimeAddEditRequest;

public interface DoctorProfileService {

    Boolean addEditName(DoctorNameAddEditRequest request);

    Boolean addEditExperience(String doctorId, String experience);

    Boolean addEditContact(DoctorContactAddEditRequest request);

    Boolean addEditEducation(DoctorEducationAddEditRequest request);

    Boolean addEditMedicalCouncils(List<MedicalCouncil> medicalCouncils);

    List<MedicalCouncil> getMedicalCouncils();

    Boolean addEditSpeciality(DoctorSpecialityAddEditRequest request);

    Boolean addEditAchievement(DoctorAchievementAddEditRequest request);

    Boolean addEditProfessionalStatement(DoctorProfessionalStatementAddEditRequest request);

    Boolean addEditRegistrationDetail(DoctorRegistrationAddEditRequest request);

    Boolean addEditExperienceDetail(DoctorExperienceAddEditRequest request);

    String addEditProfilePicture(DoctorProfilePictureAddEditRequest request);

    String addEditCoverPicture(DoctorProfilePictureAddEditRequest request);

    DoctorProfile getDoctorProfile(String doctorId, String locationId, String hospitalId);

    Boolean insertProfessionalMemberships(List<ProfessionalMembership> professionalMemberships);

    List<ProfessionalMembership> getProfessionalMemberships();

    Boolean addEditProfessionalMembership(DoctorProfessionalAddEditRequest request);

    Boolean addEditAppointmentNumbers(DoctorAppointmentNumbersAddEditRequest request);

    Boolean addEditVisitingTime(DoctorVisitingTimeAddEditRequest request);

    Boolean addEditConsultationFee(DoctorConsultationFeeAddEditRequest request);

    Boolean addEditAppointmentSlot(DoctorAppointmentSlotAddEditRequest request);

    Boolean addEditGeneralInfo(DoctorGeneralInfo request);

}
