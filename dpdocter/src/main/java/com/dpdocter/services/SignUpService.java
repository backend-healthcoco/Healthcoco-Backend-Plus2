package com.dpdocter.services;

import com.dpdocter.beans.DoctorSignUp;
import com.dpdocter.beans.User;
import com.dpdocter.request.DoctorSignupHandheldContinueRequest;
import com.dpdocter.request.DoctorSignupHandheldRequest;
import com.dpdocter.request.DoctorSignupRequest;
import com.dpdocter.request.PatientProfilePicChangeRequest;
import com.dpdocter.request.PatientSignUpRequest;

public interface SignUpService {
    String activateUser(String tokenId);

    DoctorSignUp doctorSignUp(DoctorSignupRequest request);

    User patientSignUp(PatientSignUpRequest request);

    Boolean checkUserNameExist(String username);

    Boolean checkMobileNumExist(String mobileNum);

    Boolean checkEmailAddressExist(String email);

    User patientProfilePicChange(PatientProfilePicChangeRequest request);

    DoctorSignUp doctorHandheld(DoctorSignupHandheldRequest request);

    DoctorSignUp doctorHandheldContinue(DoctorSignupHandheldContinueRequest request);
}
