package com.dpdocter.services.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.UriInfo;

import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ui.velocity.VelocityEngineUtils;

import com.dpdocter.collections.UserCollection;
import com.dpdocter.services.MailBodyGenerator;

@Service
public class MailBodyGeneratorImpl implements MailBodyGenerator {

    @Value(value = "${VERIFY_LINK}")
    private String link;

    @Value(value = "${RESET_PASSWORD_LINK}")
    private String RESET_PASSWORD_LINK;

    @Value(value = "${RESET_PASSWORD_WEB_LINK}")
    private String RESET_PASSWORD_WEB_LINK;

    @Autowired
    private VelocityEngine velocityEngine;
    
    @Value(value = "${IMAGE_URL_ROOT_PATH}")
    private String imageUrlRootPath;

    @Override
    public String generateActivationEmailBody(String userName, String fName, String mName, String lName, String tokenId, UriInfo uriInfo) throws Exception {

	Map<String, Object> model = new HashMap<String, Object>();
	model.put("fName", fName);
	model.put("link", uriInfo.getBaseUri() + link + tokenId);
	model.put("imageURL", uriInfo.getBaseUri().toString().replace(uriInfo.getBaseUri().getPath(), imageUrlRootPath)+"/templatesImage/");
	String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "mailTemplate.vm", "UTF-8", model);
	return text;
    }

    @Override
    public String generateForgotPasswordEmailBody(String emailAddress, String fName, String mName, String lName, String userId, UriInfo uriInfo) {
    	Map<String, Object> model = new HashMap<String, Object>();
    	model.put("fName", fName);
    	model.put("emailAddress", emailAddress);
    	model.put("link", RESET_PASSWORD_LINK + "?uid=" + userId);
    	model.put("imageURL", uriInfo.getBaseUri().toString().replace(uriInfo.getBaseUri().getPath(), imageUrlRootPath)+"/templatesImage/");
    	String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "forgotPasswordTemplate.vm", "UTF-8", model);
    	return text;
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

	@Override
	public String generateResetPasswordSuccessEmailBody(String emailAddress, String firstName, UriInfo uriInfo) {
		Map<String, Object> model = new HashMap<String, Object>();
    	model.put("fName", firstName);
    	model.put("link", RESET_PASSWORD_WEB_LINK);
    	model.put("imageURL", uriInfo.getBaseUri().toString().replace(uriInfo.getBaseUri().getPath(), imageUrlRootPath)+"/templatesImage/");
    	String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "resetPasswordSuccess.vm", "UTF-8", model);
    	return text;
	}

	@Override
	public String generateRecordsShareOtpBeforeVerificationEmailBody(String emailAddress, String firstName,	String doctorName, UriInfo uriInfo) {
		Map<String, Object> model = new HashMap<String, Object>();
    	model.put("fName", firstName);
    	model.put("doctorName", doctorName);
    	model.put("imageURL", uriInfo.getBaseUri().toString().replace(uriInfo.getBaseUri().getPath(), imageUrlRootPath)+"/templatesImage/");
    	String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "recordShareOtpBeforeVerificationTemplate.vm", "UTF-8", model);
    	return text;

	}

	@Override
	public String generateRecordsShareOtpAfterVerificationEmailBody(String emailAddress, String firstName, String doctorName, UriInfo uriInfo) {
		Map<String, Object> model = new HashMap<String, Object>();
    	model.put("fName", firstName);
    	model.put("doctorName", doctorName);
    	model.put("imageURL", uriInfo.getBaseUri().toString().replace(uriInfo.getBaseUri().getPath(), imageUrlRootPath)+"/templatesImage/");
    	String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "recordShareOtpAfterVerificationTemplate.vm", "UTF-8", model);
    	return text;
	}

	@Override
	public String generateRecordsUploadedEmailBody(String userName, String firstName, String middleName,
			String lastName) {
		Map<String, Object> model = new HashMap<String, Object>();
    	model.put("fName", firstName);
    	String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "", "UTF-8", model);
    	return text;
	}

}
