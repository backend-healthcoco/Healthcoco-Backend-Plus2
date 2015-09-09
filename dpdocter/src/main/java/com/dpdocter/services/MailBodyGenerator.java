package com.dpdocter.services;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.dpdocter.collections.UserCollection;

public interface MailBodyGenerator {
    public String generateActivationEmailBody(String userName, String fName, String mName, String lName, String tokenId) throws Exception;

    public String generateForgotPasswordEmailBody(String userName, String fName, String mName, String lName, String userId);

    public String generateForgotUsernameEmailBody(List<UserCollection> userCollection);

    public String generatePatientRegistrationEmailBody(String userName, String password, String firstName, String lastName);
}
