package com.dpdocter.services;

import java.util.List;

import javax.ws.rs.QueryParam;

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
import com.dpdocter.beans.Location;
import com.dpdocter.beans.Profession;
import com.dpdocter.beans.Reference;
import com.dpdocter.beans.ReferenceDetail;
import com.dpdocter.beans.RegisteredPatientDetails;
import com.dpdocter.beans.Role;
import com.dpdocter.beans.User;
import com.dpdocter.beans.UserRecords;
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

public interface RegistrationService {
	User checkIfPatientExist(PatientRegistrationRequest request);

	RegisteredPatientDetails registerNewPatient(PatientRegistrationRequest request);

	RegisteredPatientDetails registerExistingPatient(PatientRegistrationRequest request);

	List<RegisteredPatientDetails> getUsersByPhoneNumber(String phoneNumber, String doctorId, String locationId,
			String hospitalId, String role);

	RegisteredPatientDetails getPatientProfileByUserId(String userId, String doctorId, String locationId,
			String hospitalId);

	Reference addEditReference(Reference referrence);

	Reference deleteReferrence(String referrenceId, Boolean discarded);

	List<ReferenceDetail> getReferences(String range, int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded);

	PatientInitialAndCounter getPatientInitialAndCounter(String locationId);

	Boolean updatePatientInitialAndCounter(String locationId, String patientInitial, int patientCounter);

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
			String role, Boolean active);

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

	Boolean registerPatients(String doctorId, String locationId, String hospitalId);

	Boolean updateDoctorClinicProfile();

	ESPatientDocument getESPatientDocument(RegisteredPatientDetails patient);

	Boolean updateRoleCollectionData();

	ConsentForm addConcentForm(FormDataBodyPart file, ConsentForm request);

	List<ConsentForm> getConcentForm(int page, int size, String patientId, String doctorId, String locationId,
			String hospitalId, String PID, String searchTerm, boolean discarded);

	ConsentForm deleteConcentForm(String consentFormId, boolean discarded);
}
