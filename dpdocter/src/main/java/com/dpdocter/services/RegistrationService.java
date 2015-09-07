package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.BloodGroup;
import com.dpdocter.beans.ClinicAddress;
import com.dpdocter.beans.ClinicImage;
import com.dpdocter.beans.ClinicLogo;
import com.dpdocter.beans.ClinicProfile;
import com.dpdocter.beans.ClinicTiming;
import com.dpdocter.beans.Location;
import com.dpdocter.beans.Profession;
import com.dpdocter.beans.Reference;
import com.dpdocter.beans.RegisteredPatientDetails;
import com.dpdocter.beans.User;
import com.dpdocter.request.ClinicImageAddRequest;
import com.dpdocter.request.ClinicLogoAddRequest;
import com.dpdocter.request.PatientRegistrationRequest;
import com.dpdocter.response.PatientInitialAndCounter;
import com.dpdocter.response.ReferenceResponse;

public interface RegistrationService {
    User checkIfPatientExist(PatientRegistrationRequest request);

    RegisteredPatientDetails registerNewPatient(PatientRegistrationRequest request);

    RegisteredPatientDetails registerExistingPatient(PatientRegistrationRequest request);

    List<User> getUsersByPhoneNumber(String phoneNumber, String locationId, String hospitalId);

    RegisteredPatientDetails getPatientProfileByUserId(String userId, String doctorId, String locationId, String hospitalId);

    Reference addEditReference(Reference referrence);

    void deleteReferrence(String referrenceId);

    ReferenceResponse getReferences(String doctorId, String locationId, String hospitalId);

    String patientIdGenerator(String doctorId, String locationId, String hospitalId);

    PatientInitialAndCounter getPatientInitialAndCounter(String doctorId, String locationId);

    ReferenceResponse getCustomReferences(String doctorId, String locationId, String hospitalId);

    Boolean updatePatientInitialAndCounter(String doctorId, String locationId, String patientInitial, int patientCounter);

    Location getClinicDetails(String clinicId);

    ClinicProfile updateClinicProfile(ClinicProfile request);

    ClinicAddress updateClinicAddress(ClinicAddress request);

    ClinicTiming updateClinicTiming(ClinicTiming request);

	BloodGroup addBloodGroup(BloodGroup request);

	List<BloodGroup> getBloodGroup();

	Profession addProfession(Profession request);

	List<Profession> getProfession();

	ClinicLogo changeClinicLogo(ClinicLogoAddRequest request);

	List<ClinicImage> addClinicImage(ClinicImageAddRequest request);

	Boolean deleteClinicImage(String locationId, int counter);

}
