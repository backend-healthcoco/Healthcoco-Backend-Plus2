package com.dpdocter.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.dpdocter.collections.UserCollection;
import com.dpdocter.services.MailBodyGenerator;

@Service
public class MailBodyGeneratorImpl implements MailBodyGenerator {

	@Value(value = "${API_URL}")
    private String url;

    @Override
    public String generateActivationEmailBody(String userName, String fName, String mName, String lName, String tokenId) throws Exception {
	StringBuffer body = new StringBuffer();
	
	body.append("Dear " + fName + " " + lName + ", \n");
	body.append("Please click on below link to activate the account.\n " + url+"signup/activate/" + tokenId);
	return body.toString();
    }

    @Override
    public String generateForgotPasswordEmailBody(String userName, String fName, String mName, String lName, String userId) {
	StringBuffer body = new StringBuffer();
	body.append("Dear " + fName + " " + lName + ", \n");
	body.append("Please click on below link to Reset Password.");
	return body.toString();
    }

    @Override
    public String generatePatientRegistrationEmailBody(String userName, String password, String firstName, String lastName) {
	StringBuffer body = new StringBuffer();
	body.append("Dear " + firstName + " " + lastName + ", \n");
	body.append("Your username is " + userName + " and password is " + password);
	return body.toString();
    }

    @Override
    public String generateForgotUsernameEmailBody(List<UserCollection> userCollection) {
	StringBuffer body = new StringBuffer();
	body.append("Hi, \n Below are your usernames \n");
	for (UserCollection user : userCollection) {
	    body.append(" - " + user.getUserName() + "\n");
	}
	return body.toString();
    }

}
