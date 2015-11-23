package com.dpdocter.services.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ui.velocity.VelocityEngineUtils;

import com.dpdocter.collections.UserCollection;
import com.dpdocter.services.MailBodyGenerator;

@Service
public class MailBodyGeneratorImpl implements MailBodyGenerator {

    @Value(value = "${LINK}")
    private String link;

    @Value(value = "${RESET_PASSWORD_LINK}")
    private String RESET_PASSWORD_LINK;

    @Autowired
    private VelocityEngine velocityEngine;

    @Override
    public String generateActivationEmailBody(String userName, String fName, String mName, String lName, String tokenId) throws Exception {

	Map<String, Object> model = new HashMap<String, Object>();
	model.put("fName", fName);
	model.put("lName", lName);
	model.put("link", link + "signup/activate/" + tokenId);
	String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "mailTemplate.vm", "UTF-8", model);
	return text;
    }

    @Override
    public String generateForgotPasswordEmailBody(String userName, String fName, String mName, String lName, String userId) {
	StringBuffer body = new StringBuffer();
	body.append("Dear " + fName + " " + lName + ", \n");
	body.append("Please click on below link to Reset Password. \n" + RESET_PASSWORD_LINK + "?userId=" + userId);
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

    @Override
    public String generateIssueTrackEmailBody(String userName, String firstName, String middleName, String lastName) {
	StringBuffer body = new StringBuffer();
	body.append("Dear " + firstName + " " + lastName + ", \n");
	body.append("Issue is created");
	return body.toString();
    }

}
