package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.CollectionBoy;
import com.dpdocter.beans.DoctorContactUs;
import com.dpdocter.beans.DoctorSignUp;
import com.dpdocter.beans.RegisteredPatientDetails;
import com.dpdocter.beans.User;
import com.dpdocter.request.DoctorSignupRequest;
import com.dpdocter.request.PatientProfilePicChangeRequest;
import com.dpdocter.request.PatientSignUpRequest;
import com.dpdocter.request.PatientSignupRequestMobile;
import com.dpdocter.response.CollectionBoyResponse;
import com.dpdocter.response.PateientSignUpCheckResponse;

public interface SignUpService {

    User patientSignUp(PatientSignUpRequest request);

    Boolean checkUserNameExist(String username);

    Boolean checkMobileNumExist(String mobileNum);

    Boolean checkEmailAddressExist(String email);

    User patientProfilePicChange(PatientProfilePicChangeRequest request);

    String verifyUser(String tokenId);

    PateientSignUpCheckResponse checkMobileNumberSignedUp(String mobileNumber);

    RegisteredPatientDetails signupNewPatient(PatientSignupRequestMobile request);

    List<RegisteredPatientDetails> signupAlreadyRegisteredPatient(PatientSignupRequestMobile request);

    boolean verifyPatientBasedOn80PercentMatchOfName(String name, String mobileNumber);

    boolean unlockPatientBasedOn80PercentMatch(String name, String mobileNumber);

    boolean checkMobileNumberExistForPatient(String mobileNumber);

	CollectionBoyResponse signupCollectionBoys(CollectionBoy collectionBoy);

	String verifyLocale(String tokenId);

	DoctorSignUp doctorSignUp(DoctorSignupRequest request);
	
	public String verifyConfexAdmin(String tokenId);

	DoctorContactUs welcomeUser(String tokenId);

}
