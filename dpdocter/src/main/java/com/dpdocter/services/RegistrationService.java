package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.BloodGroup;
import com.dpdocter.beans.ClinicAddress;
import com.dpdocter.beans.ClinicImage;
import com.dpdocter.beans.ClinicLabProperties;
import com.dpdocter.beans.ClinicLogo;
import com.dpdocter.beans.ClinicProfile;
import com.dpdocter.beans.ClinicSpecialization;
import com.dpdocter.beans.ClinicTiming;
import com.dpdocter.beans.ConsentForm;
import com.dpdocter.beans.Feedback;
import com.dpdocter.beans.FormContent;
import com.dpdocter.beans.Location;
import com.dpdocter.beans.PatientShortCard;
import com.dpdocter.beans.Profession;
import com.dpdocter.beans.Reference;
import com.dpdocter.beans.ReferenceDetail;
import com.dpdocter.beans.RegisteredPatientDetails;
import com.dpdocter.beans.Role;
import com.dpdocter.beans.User;
import com.dpdocter.beans.UserAddress;
import com.dpdocter.beans.UserReminders;
import com.dpdocter.elasticsearch.document.ESDoctorDocument;
import com.dpdocter.elasticsearch.document.ESPatientDocument;
import com.dpdocter.request.ClinicImageAddRequest;
import com.dpdocter.request.ClinicLogoAddRequest;
import com.dpdocter.request.ClinicProfileHandheld;
import com.dpdocter.request.DoctorRegisterRequest;
import com.dpdocter.request.PatientRegistrationRequest;
import com.dpdocter.response.ClinicDoctorResponse;
import com.dpdocter.response.PatientInitialAndCounter;
import com.dpdocter.response.PatientStatusResponse;
import com.dpdocter.response.RegisterDoctorResponse;
import com.sun.jersey.multipart.FormDataBodyPart;

import common.util.web.Response;

public interface RegistrationService {
	User checkIfPatientExist(PatientRegistrationRequest request);

	RegisteredPatientDetails registerNewPatient(PatientRegistrationRequest request);

	RegisteredPatientDetails registerExistingPatient(PatientRegistrationRequest request, List<String> infoType);

	List<RegisteredPatientDetails> getUsersByPhoneNumber(String phoneNumber, String doctorId, String locationId,
			String hospitalId, String role, Boolean forChangeNumber);

	RegisteredPatientDetails getPatientProfileByUserId(String userId, String doctorId, String locationId,
			String hospitalId);

	Reference addEditReference(Reference referrence);

	Reference deleteReferrence(String referrenceId, Boolean discarded);

	List<ReferenceDetail> getReferences(String range, int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded);

	PatientInitialAndCounter getPatientInitialAndCounter(String locationId);

	Boolean updatePatientInitialAndCounter(String locationId, String patientInitial, int patientCounter, Boolean isPidHasDate);

	Location getClinicDetails(String clinicId);

	ClinicProfile updateClinicProfile(ClinicProfile request);

	ClinicAddress updateClinicAddress(ClinicAddress request);

	ClinicTiming updateClinicTiming(ClinicTiming request);

	ClinicSpecialization updateClinicSpecialization(ClinicSpecialization request);

	List<BloodGroup> getBloodGroup();

	Profession addProfession(Profession request);

	List<Profession> getProfession(int page, int size, String updatedTime);

	ClinicLogo changeClinicLogo(ClinicLogoAddRequest request);

	List<ClinicImage> addClinicImage(ClinicImageAddRequest request);

	Boolean deleteClinicImage(String locationId, int counter);

	Boolean checktDoctorExistByEmailAddress(String emailAddress);

	RegisterDoctorResponse registerNewUser(DoctorRegisterRequest request);

	RegisterDoctorResponse registerExisitingUser(DoctorRegisterRequest request);

	Role addRole(Role request);

	List<Role> getRole(String range, int page, int size, String locationId, String hospitalId, String updatedTime,
			String role);

	void checkPatientCount(String mobileNumber);

	ESDoctorDocument getESDoctorDocument(RegisterDoctorResponse doctorResponse);

	List<ClinicDoctorResponse> getUsers(int page, int size, String locationId, String hospitalId, String updatedTime,
			String role, Boolean active, String userState);

	Role deleteRole(String roleId, Boolean discarded);

	void activateDeactivateUser(String userId, String locationId, Boolean isActivate);

	ClinicLabProperties updateLabProperties(ClinicLabProperties request);

	Feedback addFeedback(Feedback request);

	ClinicProfile updateClinicProfileHandheld(ClinicProfileHandheld request);

	PatientStatusResponse getPatientStatus(String patientId, String doctorId, String locationId, String hospitalId);

	Feedback visibleFeedback(String feedbackId, Boolean isVisible);

	List<Feedback> getFeedback(int page, int size, String doctorId, String locationId, String hospitalId,
			String updatedTime, String type);

	Boolean checkPatientNumber(String oldMobileNumber, String newMobileNumber);

	Boolean changePatientNumber(String oldMobileNumber, String newMobileNumber, String otpNumber);

	List<RegisteredPatientDetails> getPatientsByPhoneNumber(String mobileNumber);

	RegisterDoctorResponse editUserInClinic(DoctorRegisterRequest request);

	Boolean updateDoctorClinicProfile();

	ESPatientDocument getESPatientDocument(RegisteredPatientDetails patient);

	Boolean updateRoleCollectionData();

	ConsentForm addConcentForm(FormDataBodyPart file, ConsentForm request);

	List<ConsentForm> getConcentForm(int page, int size, String patientId, String doctorId, String locationId,
			String hospitalId, String PID, String searchTerm, boolean discarded, long updatedTime);

	ConsentForm deleteConcentForm(String consentFormId, boolean discarded);

	String downloadConcentForm(String consentFormId);

	public void emailConsentForm(String consentFormId, String doctorId, String locationId, String hospitalId,
			String emailAddress);

	Integer updateRegisterPID(long createdTime);

	public FormContent addeditFromContent(FormContent request);

	public List<FormContent> getFormContents(int page, int size, String doctorId, String locationId, String hospitalId,
			String type, String title, String updatedTime, boolean discarded);

	public FormContent deleteFormContent(String contentId, Boolean discarded);

	UserReminders addEditPatientReminders(UserReminders request, String reminderType);

	UserReminders getPatientReminders(String userId, String reminderType);

	UserAddress addEditUserAddress(UserAddress request);

	List<UserAddress> getUserAddress(String userId, String mobileNumber, Boolean discarded);

	UserAddress deleteUserAddress(String addressId, String userId, String mobileNumber, Boolean discarded);

	Response<Object> deletePatient(String doctorId, String locationId, String hospitalId, String patientId,
			Boolean discarded, Boolean isMobileApp);

	List<PatientShortCard> getDeletedPatient(String doctorId, String locationId, String hospitalId, int page, int size, String searchTerm, String sortBy);

	RegisteredPatientDetails updatePatientNumber(String doctorId, String locationId, String hospitalId, String patientId,
			String newPatientId, String mobileNumber);
	
	Boolean setDefaultDocter(String doctorId, String locationId, String hospitalId,String defaultDoctorId);

	Boolean update();

	Boolean checkIfPNUMExist(String locationId, String hospitalId, String pNUM);
}
