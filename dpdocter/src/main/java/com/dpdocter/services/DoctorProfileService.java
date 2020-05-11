package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.AddEditSEORequest;
import com.dpdocter.beans.DoctorClinicProfile;
import com.dpdocter.beans.DoctorContactsResponse;
import com.dpdocter.beans.DoctorGeneralInfo;
import com.dpdocter.beans.DoctorProfile;
import com.dpdocter.beans.EducationInstitute;
import com.dpdocter.beans.EducationQualification;
import com.dpdocter.beans.MedicalCouncil;
import com.dpdocter.beans.ProfessionalMembership;
import com.dpdocter.beans.Services;
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
import com.dpdocter.request.DoctorOnlineWorkingTimeRequest;
import com.dpdocter.request.DoctorProfessionalAddEditRequest;
import com.dpdocter.request.DoctorProfessionalStatementAddEditRequest;
import com.dpdocter.request.DoctorProfilePictureAddEditRequest;
import com.dpdocter.request.DoctorRegistrationAddEditRequest;
import com.dpdocter.request.DoctorServicesAddEditRequest;
import com.dpdocter.request.DoctorSpecialityAddEditRequest;
import com.dpdocter.request.DoctorVisitingTimeAddEditRequest;
import com.dpdocter.request.RegularCheckUpAddEditRequest;
import com.dpdocter.response.DoctorMultipleDataAddEditResponse;

public interface DoctorProfileService {

	DoctorNameAddEditRequest addEditName(DoctorNameAddEditRequest request);

	DoctorExperienceAddEditRequest addEditExperience(DoctorExperienceAddEditRequest request);

	DoctorContactAddEditRequest addEditContact(DoctorContactAddEditRequest request);

	DoctorEducationAddEditRequest addEditEducation(DoctorEducationAddEditRequest request);

	List<MedicalCouncil> getMedicalCouncils(long page, int size, String updatedTime);

	DoctorSpecialityAddEditRequest addEditSpeciality(DoctorSpecialityAddEditRequest request);

	DoctorAchievementAddEditRequest addEditAchievement(DoctorAchievementAddEditRequest request);

	DoctorProfessionalStatementAddEditRequest addEditProfessionalStatement(
			DoctorProfessionalStatementAddEditRequest request);

	DoctorRegistrationAddEditRequest addEditRegistrationDetail(DoctorRegistrationAddEditRequest request);

	DoctorExperienceDetailAddEditRequest addEditExperienceDetail(DoctorExperienceDetailAddEditRequest request);

	String addEditProfilePicture(DoctorProfilePictureAddEditRequest request);

	String addEditCoverPicture(DoctorProfilePictureAddEditRequest request);

	DoctorProfile getDoctorProfile(String doctorId, String locationId, String hospitalId, String patientId,
			Boolean isMobileApp, Boolean isSearched);

	List<ProfessionalMembership> getProfessionalMemberships(long page, int size, String updatedTime);

	DoctorProfessionalAddEditRequest addEditProfessionalMembership(DoctorProfessionalAddEditRequest request);

	DoctorAppointmentNumbersAddEditRequest addEditAppointmentNumbers(DoctorAppointmentNumbersAddEditRequest request);

	DoctorVisitingTimeAddEditRequest addEditVisitingTime(DoctorVisitingTimeAddEditRequest request);

	DoctorConsultationFeeAddEditRequest addEditConsultationFee(DoctorConsultationFeeAddEditRequest request);

	DoctorAppointmentSlotAddEditRequest addEditAppointmentSlot(DoctorAppointmentSlotAddEditRequest request);

	DoctorGeneralInfo addEditGeneralInfo(DoctorGeneralInfo request);

	List<Speciality> getSpecialities(long page, int size, String updatedTime);

	List<EducationInstitute> getEducationInstitutes(long page, int size, String updatedTime);

	List<EducationQualification> getEducationQualifications(long page, int size, String updatedTime);

	DoctorMultipleDataAddEditResponse addEditMultipleData(DoctorMultipleDataAddEditRequest request);

	DoctorAddEditFacilityRequest addEditFacility(DoctorAddEditFacilityRequest request);

	DoctorGenderAddEditRequest addEditGender(DoctorGenderAddEditRequest request);

	DoctorDOBAddEditRequest addEditDOB(DoctorDOBAddEditRequest request);

	public DoctorClinicProfile addEditRecommedation(String doctorId, String locationId, String patientId);

	public DoctorContactsResponse getPatient(long page, int size, String doctorId, String locationId, String hospitalId,
			long from, long to);

	DoctorClinicProfile addRegularCheckupMonths(RegularCheckUpAddEditRequest request);

	Boolean updateDoctorProfileViews(String doctorId);

	Boolean updateEMRSetting(String doctorId, Boolean discarded);

	Boolean updatePrescriptionSMS(String doctorId, Boolean isSendSMS);

	AddEditSEORequest addEditSEO(AddEditSEORequest request);

	DoctorProfile getDoctorProfile(String userUId);

	Boolean updateShowInventoryCount(String doctorId, String locationId, Boolean showInventoryCount);

	Boolean updateShowInventory(String doctorId, String locationId, Boolean showInventory);

	Boolean updateSavetoInventory(String doctorId, String locationId, Boolean saveToInventory);

	DoctorServicesAddEditRequest addEditServices(DoctorServicesAddEditRequest request);

	Boolean addEditDrugTypePlacement(String doctorId, String drugTypePlacement);

	List<Services> getServices(int page, int size, String updatedTime);
	
	DoctorOnlineWorkingTimeRequest addEditOnlineWorkingTime(DoctorOnlineWorkingTimeRequest request);
	

}
