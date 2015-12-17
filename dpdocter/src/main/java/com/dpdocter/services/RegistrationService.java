package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.BloodGroup;
import com.dpdocter.beans.ClinicAddress;
import com.dpdocter.beans.ClinicImage;
import com.dpdocter.beans.ClinicLogo;
import com.dpdocter.beans.ClinicProfile;
import com.dpdocter.beans.ClinicSpecialization;
import com.dpdocter.beans.ClinicTiming;
import com.dpdocter.beans.Location;
import com.dpdocter.beans.Profession;
import com.dpdocter.beans.Reference;
import com.dpdocter.beans.ReferenceDetail;
import com.dpdocter.beans.RegisteredPatientDetails;
import com.dpdocter.beans.Role;
import com.dpdocter.beans.User;
import com.dpdocter.request.ClinicImageAddRequest;
import com.dpdocter.request.ClinicLogoAddRequest;
import com.dpdocter.request.DoctorRegisterRequest;
import com.dpdocter.request.PatientRegistrationRequest;
import com.dpdocter.response.ClinicDoctorResponse;
import com.dpdocter.response.PatientInitialAndCounter;
import com.dpdocter.response.RegisterDoctorResponse;

public interface RegistrationService {
    User checkIfPatientExist(PatientRegistrationRequest request);

    RegisteredPatientDetails registerNewPatient(PatientRegistrationRequest request);

    RegisteredPatientDetails registerExistingPatient(PatientRegistrationRequest request);

    List<User> getUsersByPhoneNumber(String phoneNumber, String locationId, String hospitalId);

    RegisteredPatientDetails getPatientProfileByUserId(String userId, String doctorId, String locationId, String hospitalId);

    Reference addEditReference(Reference referrence);

    void deleteReferrence(String referrenceId, Boolean discarded);

    List<ReferenceDetail> getReferences(String range, int page, int size, String doctorId, String locationId, String hospitalId, String updatedTime,
	    Boolean discarded);

    String patientIdGenerator(String doctorId, String locationId, String hospitalId);

    PatientInitialAndCounter getPatientInitialAndCounter(String doctorId, String locationId);

    Boolean updatePatientInitialAndCounter(String doctorId, String locationId, String patientInitial, int patientCounter);

    Location getClinicDetails(String clinicId);

    ClinicProfile updateClinicProfile(ClinicProfile request);

    ClinicAddress updateClinicAddress(ClinicAddress request);

    ClinicTiming updateClinicTiming(ClinicTiming request);

    ClinicSpecialization updateClinicSpecialization(ClinicSpecialization request);

    BloodGroup addBloodGroup(BloodGroup request);

    List<BloodGroup> getBloodGroup();

    Profession addProfession(Profession request);

    List<Profession> getProfession(int page, int size);

    ClinicLogo changeClinicLogo(ClinicLogoAddRequest request);

    List<ClinicImage> addClinicImage(ClinicImageAddRequest request);

    Boolean deleteClinicImage(String locationId, int counter);

    Boolean checktDoctorExistByEmailAddress(String emailAddress);

    RegisterDoctorResponse registerNewUser(DoctorRegisterRequest request);

    RegisterDoctorResponse registerExisitingUser(DoctorRegisterRequest request);

    Role addRole(Role request);

    List<Role> getRole(String range, int page, int size, String locationId, String hospitalId, String updatedTime);

    void checkPatientCount(String mobileNumber);

    List<ClinicDoctorResponse> getDoctors(int page, int size, String locationId, String hospitalId, String updatedTime);

}
