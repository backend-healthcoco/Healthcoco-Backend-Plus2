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

import com.dpdocter.beans.DoctorContactUs;
import com.dpdocter.beans.MailAttachment;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.response.URLShortnerResponse;
import com.dpdocter.services.MailBodyGenerator;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;

import common.util.web.JacksonUtil;

@Service
public class MailBodyGeneratorImpl implements MailBodyGenerator {

	@Value(value = "${verify.link}")
	private String link;

	@Value(value = "${welcome.link}")
	private String welcomeLink;

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
	public String generateActivationEmailBody(String fName, String tokenId, String templatePath, String doctorName,
			String clinicName, String addedBy) throws Exception {

		Map<String, Object> model = new HashMap<String, Object>();
		model.put("fName", fName);
		model.put("doctorName", doctorName);
		model.put("addedBy", addedBy);
		model.put("clinicName", clinicName);
		model.put("link", link + "/" + tokenId);
		model.put("loginLink", loginLink);
		model.put("imageURL", imagePath + "templatesImage");
		model.put("contactUsEmail", contactUsEmail);
		model.put("fbLink", fbLink);
		model.put("twitterLink", twitterLink);
		model.put("linkedInLink", linkedInLink);
		model.put("googlePlusLink", googlePlusLink);
		model.put("setPasswordLink", setPasswordLink + "?uid=" + tokenId);
		String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, templatePath, "UTF-8", model);
		return text;
	}

	@Override
	@Transactional
	public String doctorWelcomeEmailBody(String fName, String tokenId, String templatePath, String doctorName,
			String clinicName) throws Exception {

		Map<String, Object> model = new HashMap<String, Object>();
		model.put("fName", fName);
		model.put("doctorName", doctorName);
		model.put("clinicName", clinicName);
		model.put("link", welcomeLink + "/" + tokenId);
		model.put("loginLink", loginLink);
		model.put("imageURL", imagePath + "templatesImage");
		model.put("contactUsEmail", contactUsEmail);
		model.put("fbLink", fbLink);
		model.put("twitterLink", twitterLink);
		model.put("linkedInLink", linkedInLink);
		model.put("googlePlusLink", googlePlusLink);
		// model.put("setPasswordLink", setPasswordLink + "?uid=" + tokenId);
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
		String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "forgotPasswordTemplate.vm", "UTF-8",
				model);
		return text;
	}

	@Override
	@Transactional
	public String generateContactEmailBody(String fName, String type, String mobileNumber, String emailAddress,
			String city) {
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("fName", fName);
		model.put("city", city);
		model.put("type", type);
		model.put("mobileNumber", mobileNumber);
		model.put("emailAddress", emailAddress);

		String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "contactmail.vm", "UTF-8", model);
		return text;
	}

	@SuppressWarnings("deprecation")
	@Override
	@Transactional
	public String generateContactEmailBody(DoctorContactUs contactUs, String type) {
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("fName", contactUs.getTitle() + " " + contactUs.getFirstName());
		model.put("city", contactUs.getCity());
		model.put("type", type);
		model.put("mobileNumber", contactUs.getMobileNumber());
		model.put("emailAddress", contactUs.getEmailAddress());
		model.put("deviceType", contactUs.getDeviceType());
		model.put("specialities", contactUs.getSpecialities());

		String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "contactmail.vm", "UTF-8", model);
		return text;
	}

	@SuppressWarnings("deprecation")
	@Override
	@Transactional
	public String generateDoctorReferenceEmailBody(String fName, String mobileNumber, String locationName,
			String labName) {
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("fName", "Dr." + " " + fName);
		model.put("locationName", locationName);
		model.put("mobileNumber", mobileNumber);
		model.put("labName", labName);

		String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "doctorReferenceMail.vm", "UTF-8",
				model);
		return text;
	}

	@SuppressWarnings("deprecation")
	@Override
	@Transactional
	public String generatePrescriptionListMail(String collectionBody, String requestBody) {
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("collectionBody", collectionBody);
		model.put("requestBody", requestBody);

		String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "prescriptionListMail.vm", "UTF-8",
				model);
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
		String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "addIssueTemplate.vm", "UTF-8",
				model);
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
		String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "resetPasswordSuccess.vm", "UTF-8",
				model);
		return text;
	}

	@Override
	@Transactional
	public String generateRecordsShareOtpBeforeVerificationEmailBody(String emailAddress, String firstName,
			String doctorName) {
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("fName", firstName);
		model.put("doctorName", doctorName);
		model.put("imageURL", imagePath + "templatesImage");
		model.put("contactUsEmail", contactUsEmail);
		model.put("fbLink", fbLink);
		model.put("twitterLink", twitterLink);
		model.put("linkedInLink", linkedInLink);
		model.put("googlePlusLink", googlePlusLink);
		String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine,
				"recordShareOtpBeforeVerificationTemplate.vm", "UTF-8", model);
		return text;
	}

	@Override
	@Transactional
	public String generateRecordsShareOtpAfterVerificationEmailBody(String emailAddress, String firstName,
			String doctorName) {
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("fName", firstName);
		model.put("doctorName", doctorName);
		model.put("imageURL", imagePath + "templatesImage");
		model.put("contactUsEmail", contactUsEmail);
		model.put("fbLink", fbLink);
		model.put("twitterLink", twitterLink);
		model.put("linkedInLink", linkedInLink);
		model.put("googlePlusLink", googlePlusLink);
		String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine,
				"recordShareOtpAfterVerificationTemplate.vm", "UTF-8", model);
		return text;
	}

	@Override
	@Transactional
	public String generateAppointmentEmailBody(String doctorName, String patientName, String dateTime,
			String clinicName, String templatePath, String branch) {
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("doctorName", doctorName);
		model.put("patientName", patientName);
		model.put("dateTime", dateTime);
		model.put("clinicName", clinicName);
		model.put("branch", branch);
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
	public String generateEMREmailBody(String patientName, String doctorName, String clinicName, String clinicAddress,
			String mailRecordCreatedDate, String medicalRecordType, String templatePath) {
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

	public String generatePaymentEmailBody(String orderId, String planName, String amount, String patientName,
			String time, String templatePath) {
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("orderId", orderId);
		model.put("planName", planName);
		model.put("amount", amount);
		model.put("patientName", patientName);
		model.put("time", time);
		String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, templatePath, "UTF-8", model);
		return text;
	}

	@Override
	public String generateFeedbackEmailBody(String patientName, String doctorName, String clinicName,
			String uniqueFeedbackId, String templatePath) {
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
	public String generateAppLinkEmailBody(String appType, String bitLink, String appDeviceType, String templatePath) {
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("appDeviceType", appDeviceType);
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

	@Override
	public String generateRecordEmailBody(String doctorName, String clinicName, String patientName, String recordName,
			String uniqueRecordId, String templatePath) {
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("doctorName", doctorName);
		model.put("clinicName", clinicName);
		model.put("patientName", patientName);
		model.put("recordName", recordName);
		model.put("uniqueRecordId", uniqueRecordId);
		model.put("imageURL", imagePath + "templatesImage");
		model.put("contactUsEmail", contactUsEmail);
		model.put("fbLink", fbLink);
		model.put("twitterLink", twitterLink);
		model.put("linkedInLink", linkedInLink);
		model.put("googlePlusLink", googlePlusLink);
		String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, templatePath, "UTF-8", model);
		return text;

	}

	@SuppressWarnings("deprecation")
	@Override
	@Transactional
	public String generateExceptionEmailBody(String exception) {
		Map<String, Object> model = new HashMap<String, Object>();

		model.put("exceptionMsg", exception);

		String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "exceptionMail.vm", "UTF-8", model);
		return text;
	}

	@SuppressWarnings("deprecation")
	@Override
	public String generateDentalImagingInvoiceEmailBody(String doctorName, String dentalImagingLab, String patientName,
			List<MailAttachment> reports, String templatePath) {
		String text = "";
		try {
			Map<String, Object> model = new HashMap<String, Object>();
			model.put("doctorName", doctorName);
			model.put("dentalImagingLab", dentalImagingLab);
			model.put("patientName", patientName);
			model.put("reports", reports);
			text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, templatePath, "UTF-8", model);
			return text;
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
		return text;

	}

	@Override
	@Transactional
	public String nutritionReferenceEmailBody(String patientName, String mobileNumber, String birthDate,
			String profession, String gender, String address, String city, String pinCode, String doctorName,
			String planName, String subplan,String templatePath) throws Exception {
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("patientName", patientName);
		model.put("doctorName", doctorName);
		model.put("mobileNumber", mobileNumber);
		model.put("birthDate", birthDate);
		model.put("profession", profession);
		model.put("gender", gender);
		model.put("address", address);
		model.put("city", city);
		model.put("pinCode", pinCode);
		model.put("planName", planName);
		model.put("subplan", subplan);
		// model.put("setPasswordLink", setPasswordLink + "?uid=" + tokenId);
		@SuppressWarnings("deprecation")
		String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, templatePath, "UTF-8", model);
		return text;
	}


}
