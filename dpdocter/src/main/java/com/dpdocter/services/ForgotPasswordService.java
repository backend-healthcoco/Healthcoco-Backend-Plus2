package com.dpdocter.services;

import com.dpdocter.request.ForgotUsernamePasswordRequest;
import com.dpdocter.request.ResetPasswordRequest;
import com.dpdocter.response.ForgotPasswordResponse;

public interface ForgotPasswordService {
    ForgotPasswordResponse forgotPasswordForDoctor(ForgotUsernamePasswordRequest request);

    Boolean forgotPasswordForPatient(ForgotUsernamePasswordRequest request);

    void resetPassword(ResetPasswordRequest request);

    Boolean forgotUsername(ForgotUsernamePasswordRequest request);

    ForgotPasswordResponse getEmailAndMobNumberOfPatient(String username);

}
