package com.dpdocter.services.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.velocity.VelocityEngineUtils;

import com.dpdocter.collections.UserCollection;
import com.dpdocter.services.MailBodyGenerator;

@Service
public class MailBodyGeneratorImpl implements MailBodyGenerator {

    @Value(value = "${verify.link}")
    private String link;

    @Value(value = "${login.link}")
    private String loginLink;

    @Value(value = "${reset.password.link}")
    private String RESET_PASSWORD_LINK;

    @Value(value = "${web.link}")
    private String RESET_PASSWORD_WEB_LINK;

    @Autowired
    private VelocityEngine velocityEngine;

    @Value(value = "${image.path}")
    private String imagePath;

    @Value(value = "${contact.us.email}")
    private String contactUsEmail;

    @Value(value = "${fb.link}")
    private String fbLink;

    @Value(value = "${twitter.link}")
    private String twitterLink;

    @Value(value = "${linkedIn.link}")
    private String linkedInLink;

    @Value(value = "${googlePlus.link}")
    private String googlePlusLink;

    @Value(value = "${set.password.link}")
    private String setPasswordLink;

    @Override
    @Transactional
    public String generateActivationEmailBody(String fName, String tokenId, String templatePath, String doctorName,String clinicName) throws Exception {

	Map<String, Object> model = new HashMap<String, Object>();
	model.put("fName", fName);
	model.put("doctorName", doctorName);
	model.put("clinicName", clinicName);
	model.put("link", link+"/"+tokenId);
	model.put("loginLink", loginLink);
	model.put("imageURL", imagePath + "templatesImage");
	model.put("contactUsEmail", contactUsEmail);
	model.put("fbLink", fbLink);
	model.put("twitterLink", twitterLink);
	model.put("linkedInLink", linkedInLink);
	model.put("googlePlusLink", googlePlusLink);
	model.put("setPasswordLink", setPasswordLink+"/"+tokenId);
	String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, templatePath, "UTF-8", model);
	return text;
    }

    @Override
    @Transactional
    public String generateForgotPasswordEmailBody(String fName, String tokenId) {
	Map<String, Object> model = new HashMap<String, Object>();
	model.put("fName", fName);
	model.put("link", RESET_PASSWORD_LINK + "?uid=" + tokenId);
	model.put("imageURL", imagePath + "templatesImage");
	model.put("contactUsEmail", contactUsEmail);
	model.put("fbLink", fbLink);
	model.put("twitterLink", twitterLink);
	model.put("linkedInLink", linkedInLink);
	model.put("googlePlusLink", googlePlusLink);
	String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "forgotPasswordTemplate.vm", "UTF-8", model);
	return text;
    }

    @Override
    @Transactional
    public String generateForgotUsernameEmailBody(List<UserCollection> userCollection) {
	StringBuffer body = new StringBuffer();
	body.append("Hi, \n Below are your usernames \n");
	for (UserCollection user : userCollection) {
	    body.append(" - " + user.getUserName() + "\n");
	}
	return body.toString();
    }

    @Override
    @Transactional
    public String generateIssueTrackEmailBody(String userName, String firstName, String middleName, String lastName) {
    	Map<String, Object> model = new HashMap<String, Object>();
    	model.put("fName", firstName);
    	model.put("link", RESET_PASSWORD_WEB_LINK);
    	model.put("imageURL", imagePath + "templatesImage");
    	model.put("contactUsEmail", contactUsEmail);
    	model.put("fbLink", fbLink);
    	model.put("twitterLink", twitterLink);
    	model.put("linkedInLink", linkedInLink);
    	model.put("googlePlusLink", googlePlusLink);
    	String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "addIssueTemplate.vm", "UTF-8", model);
    	return text;
    }

    @Override
    @Transactional
    public String generateResetPasswordSuccessEmailBody(String firstName) {
	Map<String, Object> model = new HashMap<String, Object>();
	model.put("fName", firstName);
	model.put("link", RESET_PASSWORD_WEB_LINK);
	model.put("imageURL", imagePath + "templatesImage");
	model.put("contactUsEmail", contactUsEmail);
	model.put("fbLink", fbLink);
	model.put("twitterLink", twitterLink);
	model.put("linkedInLink", linkedInLink);
	model.put("googlePlusLink", googlePlusLink);
	String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "resetPasswordSuccess.vm", "UTF-8", model);
	return text;
    }

    @Override
    @Transactional
    public String generateRecordsShareOtpBeforeVerificationEmailBody(String emailAddress, String firstName, String doctorName) {
	Map<String, Object> model = new HashMap<String, Object>();
	model.put("fName", firstName);
	model.put("doctorName", doctorName);
	model.put("imageURL", imagePath + "templatesImage");
	model.put("contactUsEmail", contactUsEmail);
	model.put("fbLink", fbLink);
	model.put("twitterLink", twitterLink);
	model.put("linkedInLink", linkedInLink);
	model.put("googlePlusLink", googlePlusLink);
	String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "recordShareOtpBeforeVerificationTemplate.vm", "UTF-8", model);
	return text;
    }

    @Override
    @Transactional
    public String generateRecordsShareOtpAfterVerificationEmailBody(String emailAddress, String firstName, String doctorName) {
	Map<String, Object> model = new HashMap<String, Object>();
	model.put("fName", firstName);
	model.put("doctorName", doctorName);
	model.put("imageURL", imagePath + "templatesImage");
	model.put("contactUsEmail", contactUsEmail);
	model.put("fbLink", fbLink);
	model.put("twitterLink", twitterLink);
	model.put("linkedInLink", linkedInLink);
	model.put("googlePlusLink", googlePlusLink);
	String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "recordShareOtpAfterVerificationTemplate.vm", "UTF-8", model);
	return text;
    }

    @Override
    @Transactional
    public String generateAppointmentEmailBody(String doctorName, String patientName, String dateTime, String clinicName, String templatePath) {
	Map<String, Object> model = new HashMap<String, Object>();
	model.put("doctorName", doctorName);
	model.put("patientName", patientName);
	model.put("dateTime", dateTime);
	model.put("clinicName", clinicName);
	model.put("imageURL", imagePath + "templatesImage");
	model.put("contactUsEmail", contactUsEmail);
	model.put("fbLink", fbLink);
	model.put("twitterLink", twitterLink);
	model.put("linkedInLink", linkedInLink);
	model.put("googlePlusLink", googlePlusLink);
	String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, templatePath, "UTF-8", model);
	return text;
    }

    @Override
    @Transactional
    public String generateEmailBody(String userName, String resumeType, String templatePath) throws Exception {

	Map<String, Object> model = new HashMap<String, Object>();
	model.put("fName", userName);
	model.put("resumeType", resumeType);
	model.put("imageURL", imagePath + "templatesImage");
	model.put("contactUsEmail", contactUsEmail);
	model.put("fbLink", fbLink);
	model.put("twitterLink", twitterLink);
	model.put("linkedInLink", linkedInLink);
	model.put("googlePlusLink", googlePlusLink);
	String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, templatePath, "UTF-8", model);
	return text;
    }

	@Override
	public String generateEMREmailBody(String patientName, String doctorName, String clinicName, String clinicAddress, String mailRecordCreatedDate, String medicalRecordType, String templatePath) {
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("fName", patientName);
		model.put("doctorName", doctorName);
		model.put("clinicName", clinicName);
		model.put("clinicAddress", clinicAddress);
		model.put("mailRecordCreatedDate", mailRecordCreatedDate);
		model.put("medicalRecordType", medicalRecordType);
		model.put("imageURL", imagePath + "templatesImage");
		model.put("contactUsEmail", contactUsEmail);
		model.put("fbLink", fbLink);
		model.put("twitterLink", twitterLink);
		model.put("linkedInLink", linkedInLink);
		model.put("googlePlusLink", googlePlusLink);
		String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, templatePath, "UTF-8", model);
		return text;
	}

	@Override
	public String generateFeedbackEmailBody(String patientName, String doctorName, String clinicName,	String uniqueFeedbackId, String templatePath) {
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("fName", patientName);
		model.put("doctorName", doctorName);
		model.put("clinicName", clinicName);
		model.put("uniqueFeedbackId", uniqueFeedbackId);
		model.put("imageURL", imagePath + "templatesImage");
		model.put("contactUsEmail", contactUsEmail);
		model.put("fbLink", fbLink);
		model.put("twitterLink", twitterLink);
		model.put("linkedInLink", linkedInLink);
		model.put("googlePlusLink", googlePlusLink);
		String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, templatePath, "UTF-8", model);
		return text;

	}

	@Override
	public String generateAppLinkEmailBody(String appType, String bitLink, String templatePath) {
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("appType", appType);
		model.put("bitLink", bitLink);
		model.put("imageURL", imagePath + "templatesImage");
		model.put("contactUsEmail", contactUsEmail);
		model.put("fbLink", fbLink);
		model.put("twitterLink", twitterLink);
		model.put("linkedInLink", linkedInLink);
		model.put("googlePlusLink", googlePlusLink);
		String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, templatePath, "UTF-8", model);
		return text;

	}

}
