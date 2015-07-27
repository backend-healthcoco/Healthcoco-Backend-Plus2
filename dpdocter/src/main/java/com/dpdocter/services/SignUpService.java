package com.dpdocter.services;

import com.dpdocter.beans.DoctorSignUp;
import com.dpdocter.beans.User;
import com.dpdocter.request.DoctorSignupRequest;
import com.dpdocter.request.PatientProfilePicChangeRequest;
import com.dpdocter.request.PatientSignUpRequest;

public interface SignUpService {
    Boolean activateUser(String userId);

    DoctorSignUp doctorSignUp(DoctorSignupRequest request);

    User patientSignUp(PatientSignUpRequest request);

    Boolean checkUserNameExist(String username);

    Boolean checkMobileNumExist(String mobileNum);

    Boolean checkEmailAddressExist(String email);

    User patientProfilePicChange(PatientProfilePicChangeRequest request);
}
