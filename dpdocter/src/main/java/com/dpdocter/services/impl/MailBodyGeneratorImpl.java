package com.dpdocter.services.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.UriInfo;

import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.velocity.VelocityEngineUtils;

import com.dpdocter.collections.UserCollection;
import com.dpdocter.services.MailBodyGenerator;

import common.util.web.DPDoctorUtils;

@Service
public class MailBodyGeneratorImpl implements MailBodyGenerator {

    @Value(value = "${verify.link}")
    private String link;

    @Value(value = "${reset.password.link}")
    private String RESET_PASSWORD_LINK;

    @Value(value = "${web.link}")
    private String RESET_PASSWORD_WEB_LINK;

    @Autowired
    private VelocityEngine velocityEngine;

    @Value(value = "${image.path}")
    private String imagePath;

    @Override
    @Transactional
    public String generateActivationEmailBody(String fName, String tokenId, String templatePath) throws Exception {

	Map<String, Object> model = new HashMap<String, Object>();
	model.put("fName", fName);
	if(!DPDoctorUtils.anyStringEmpty(tokenId))model.put("link", link+"?token="+tokenId);
	model.put("imageURL", imagePath + "templatesImage/");
	String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, templatePath, "UTF-8", model);
	return text;
    }

    @Override
    @Transactional
    public String generateForgotPasswordEmailBody(String emailAddress, String fName, String mName, String lName, String userId, UriInfo uriInfo) {
	Map<String, Object> model = new HashMap<String, Object>();
	model.put("fName", fName);
	model.put("emailAddress", emailAddress);
	model.put("link", RESET_PASSWORD_LINK + "?uid=" + userId);
	model.put("imageURL", imagePath + "templatesImage");
	String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "forgotPasswordTemplate.vm", "UTF-8", model);
	return text;
    }

    @Override
    @Transactional
    public String generatePatientRegistrationEmailBody(String userName, char[] password, String firstName, String lastName) {
	StringBuffer body = new StringBuffer();
	body.append("Dear " + firstName + " " + lastName + ", \n");
	body.append("Your username is " + userName + " and password is " + password);
	return body.toString();
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
	StringBuffer body = new StringBuffer();
	body.append("Dear " + firstName + " " + lastName + ", \n");
	body.append("Issue is created");
	return body.toString();
    }

    @Override
    @Transactional
    public String generateResetPasswordSuccessEmailBody(String emailAddress, String firstName, UriInfo uriInfo) {
	Map<String, Object> model = new HashMap<String, Object>();
	model.put("fName", firstName);
	model.put("link", RESET_PASSWORD_WEB_LINK);
	model.put("imageURL", imagePath + "templatesImage/");
	String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "resetPasswordSuccess.vm", "UTF-8", model);
	return text;
    }

    @Override
    @Transactional
    public String generateRecordsShareOtpBeforeVerificationEmailBody(String emailAddress, String firstName, String doctorName, UriInfo uriInfo) {
	Map<String, Object> model = new HashMap<String, Object>();
	model.put("fName", firstName);
	model.put("doctorName", doctorName);
	model.put("imageURL", imagePath + "templatesImage/");
	String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "recordShareOtpBeforeVerificationTemplate.vm", "UTF-8", model);
	return text;
    }

    @Override
    @Transactional
    public String generateRecordsShareOtpAfterVerificationEmailBody(String emailAddress, String firstName, String doctorName, UriInfo uriInfo) {
	Map<String, Object> model = new HashMap<String, Object>();
	model.put("fName", firstName);
	model.put("doctorName", doctorName);
	model.put("imageURL", imagePath + "templatesImage/");
	String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "recordShareOtpAfterVerificationTemplate.vm", "UTF-8", model);
	return text;
    }

    @Override
    @Transactional
    public String generateRecordsUploadedEmailBody(String userName, String firstName, String middleName, String lastName) {
	Map<String, Object> model = new HashMap<String, Object>();
	model.put("fName", firstName);
	String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "", "UTF-8", model);
	return text;
    }

    @Override
    @Transactional
    public String generateAppointmentCancelEmailBody(String doctorName, String patientName, String dateTime, String clinicName, String templatePath) {
	Map<String, Object> model = new HashMap<String, Object>();
	model.put("doctorName", doctorName);
	model.put("patientName", patientName);
	model.put("dateTime", dateTime);
	model.put("clinicName", clinicName);
	model.put("imageURL", imagePath + "templatesImage");
	String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, templatePath, "UTF-8", model);
	return text;
    }

    @Override
    @Transactional
    public String generateEmailBody(String userName, String resumeType, String templatePath) throws Exception {

	Map<String, Object> model = new HashMap<String, Object>();
	model.put("fName", userName);
	model.put("resumeType", resumeType);
	model.put("imageURL", imagePath + "templatesImage/");
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
		model.put("imageURL", imagePath + "templatesImage/");
		String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, templatePath, "UTF-8", model);
		return text;
	}
}
