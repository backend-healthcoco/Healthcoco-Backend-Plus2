package com.dpdocter.services.impl;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.DoctorContactUs;
import com.dpdocter.beans.MailAttachment;
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

	@Value(value = "${welcome.link}")
	private String welcomeLink;

	@Override
	@Transactional
	public String generateActivationEmailBody(String fName, String tokenId, String templatePath, String doctorName, String clinicName) throws Exception {
		VelocityContext context = new VelocityContext();		
		context.put("fName", fName);
		context.put("doctorName", doctorName);
		context.put("clinicName", clinicName);
		context.put("link", welcomeLink + "/" + tokenId);
		context.put("loginLink", loginLink);
		context.put("imageURL", imagePath + "templatesImage");
		context.put("contactUsEmail", contactUsEmail);
		context.put("fbLink", fbLink);
		context.put("twitterLink", twitterLink);
		context.put("linkedInLink", linkedInLink);
		context.put("googlePlusLink", googlePlusLink);
		context.put("setPasswordLink", setPasswordLink + "?uid=" + tokenId);
		StringWriter stringWriter = new StringWriter();
		velocityEngine.mergeTemplate(templatePath, "UTF-8", context, stringWriter);
		String text = stringWriter.toString();
		return text;
	}

	@Override
	@Transactional
	public String generateActivationEmailBodyForStaff(String fName, String tokenId, String templatePath,
			String doctorName, String clinicName) throws Exception {

		VelocityContext context = new VelocityContext();
		context.put("fName", fName);
		context.put("doctorName", doctorName);
		context.put("clinicName", clinicName);
		context.put("link", link + "/" + tokenId);
		context.put("loginLink", loginLink);
		context.put("imageURL", imagePath + "templatesImage");
		context.put("contactUsEmail", contactUsEmail);
		context.put("fbLink", fbLink);
		context.put("twitterLink", twitterLink);
		context.put("linkedInLink", linkedInLink);
		context.put("googlePlusLink", googlePlusLink);
		context.put("setPasswordLink", setPasswordLink + "?uid=" + tokenId);

		StringWriter stringWriter = new StringWriter();

		velocityEngine.mergeTemplate(templatePath, "UTF-8", context, stringWriter);
		String text = stringWriter.toString();
		return text;
	}

	@Override
	@Transactional
	public String generateForgotPasswordEmailBody(String fName, String tokenId) {
		VelocityContext context = new VelocityContext();
		context.put("fName", fName);
		context.put("link", RESET_PASSWORD_LINK + "?uid=" + tokenId);
		context.put("imageURL", imagePath + "templatesImage");
		context.put("contactUsEmail", contactUsEmail);
		context.put("fbLink", fbLink);
		context.put("twitterLink", twitterLink);
		context.put("linkedInLink", linkedInLink);
		context.put("googlePlusLink", googlePlusLink);
	    StringWriter stringWriter = new StringWriter();

		velocityEngine.mergeTemplate("forgotPasswordTemplate.vm", "UTF-8", context, stringWriter);
		String text = stringWriter.toString();
		return text;
	}

	@Override
	@Transactional
	public String generateContactEmailBody(String fName, String type, String mobileNumber, String emailAddress,
			String city) {
		VelocityContext context = new VelocityContext();
		context.put("fName", fName);
		context.put("city", city);
		context.put("type", type);
		context.put("mobileNumber", mobileNumber);
		context.put("emailAddress", emailAddress);
	    StringWriter stringWriter = new StringWriter();

		velocityEngine.mergeTemplate("contactmail.vm", "UTF-8", context, stringWriter);
		String text = stringWriter.toString();
		return text;
	}

	@Override
	@Transactional
	public String generateContactEmailBody(DoctorContactUs contactUs, String type) {
		VelocityContext context = new VelocityContext();
		context.put("fName", contactUs.getTitle() + " " + contactUs.getFirstName());
		context.put("city", contactUs.getCity());
		context.put("type", type);
		context.put("mobileNumber", contactUs.getMobileNumber());
		context.put("emailAddress", contactUs.getEmailAddress());
		context.put("deviceType", contactUs.getDeviceType());
		context.put("specialities", contactUs.getSpecialities());
	    StringWriter stringWriter = new StringWriter();

		velocityEngine.mergeTemplate("contactmail.vm", "UTF-8", context, stringWriter);
		String text = stringWriter.toString();
		return text;
	}

	@Override
	@Transactional
	public String generateDoctorReferenceEmailBody(String fName, String mobileNumber, String locationName,String labName) {
		VelocityContext context = new VelocityContext();
		context.put("fName", "Dr." + " " + fName);
		context.put("locationName", locationName);
		context.put("mobileNumber", mobileNumber);
		context.put("labName", labName);
	    StringWriter stringWriter = new StringWriter();

		velocityEngine.mergeTemplate("doctorReferenceMail.vm", "UTF-8", context, stringWriter);
		String text = stringWriter.toString();
		return text;
	}

	@Override
	@Transactional
	public String generatePrescriptionListMail(String collectionBody, String requestBody) {
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("collectionBody", collectionBody);
		model.put("requestBody", requestBody);
		
		VelocityContext context = new VelocityContext();
		context.put("collectionBody", collectionBody);
		context.put("requestBody", requestBody);
		
	    StringWriter stringWriter = new StringWriter();
	    velocityEngine.mergeTemplate("prescriptionListMail.vm", "UTF-8", context, stringWriter);
	    String text = stringWriter.toString();
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
		
		VelocityContext context = new VelocityContext();
		context.put("fName", firstName);
		context.put("link", RESET_PASSWORD_WEB_LINK);
		context.put("imageURL", imagePath + "templatesImage");
		context.put("contactUsEmail", contactUsEmail);
		context.put("fbLink", fbLink);
		context.put("twitterLink", twitterLink);
		context.put("linkedInLink", linkedInLink);
		context.put("googlePlusLink", googlePlusLink);
		
	    StringWriter stringWriter = new StringWriter();

		velocityEngine.mergeTemplate("addIssueTemplate.vm", "UTF-8", context, stringWriter);
		String text = stringWriter.toString();
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
		
		VelocityContext context = new VelocityContext();
		context.put("fName", firstName);
		context.put("link", RESET_PASSWORD_WEB_LINK);
		context.put("imageURL", imagePath + "templatesImage");
		context.put("contactUsEmail", contactUsEmail);
		context.put("fbLink", fbLink);
		context.put("twitterLink", twitterLink);
		context.put("linkedInLink", linkedInLink);
		context.put("googlePlusLink", googlePlusLink);
		
	    StringWriter stringWriter = new StringWriter();

		velocityEngine.mergeTemplate("resetPasswordSuccess.vm", "UTF-8", context, stringWriter);
		String text = stringWriter.toString();
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
		
		VelocityContext context = new VelocityContext();
		context.put("fName", firstName);
		context.put("doctorName", doctorName);
		context.put("imageURL", imagePath + "templatesImage");
		context.put("contactUsEmail", contactUsEmail);
		context.put("fbLink", fbLink);
		context.put("twitterLink", twitterLink);
		context.put("linkedInLink", linkedInLink);
		context.put("googlePlusLink", googlePlusLink);
		
	    StringWriter stringWriter = new StringWriter();

		velocityEngine.mergeTemplate("recordShareOtpBeforeVerificationTemplate.vm", "UTF-8", context, stringWriter);
		String text = stringWriter.toString();
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
		
		VelocityContext context = new VelocityContext();
		context.put("fName", firstName);
		context.put("doctorName", doctorName);
		context.put("imageURL", imagePath + "templatesImage");
		context.put("contactUsEmail", contactUsEmail);
		context.put("fbLink", fbLink);
		context.put("twitterLink", twitterLink);
		context.put("linkedInLink", linkedInLink);
		context.put("googlePlusLink", googlePlusLink);
		
	    StringWriter stringWriter = new StringWriter();

		velocityEngine.mergeTemplate("recordShareOtpAfterVerificationTemplate.vm", "UTF-8", context, stringWriter);
		String text = stringWriter.toString();
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
		
		VelocityContext context = new VelocityContext();
		context.put("doctorName", doctorName);
		context.put("patientName", patientName);
		context.put("dateTime", dateTime);
		context.put("clinicName", clinicName);
		context.put("imageURL", imagePath + "templatesImage");
		context.put("contactUsEmail", contactUsEmail);
		context.put("fbLink", fbLink);
		context.put("twitterLink", twitterLink);
		context.put("linkedInLink", linkedInLink);
		context.put("googlePlusLink", googlePlusLink);
		
	    StringWriter stringWriter = new StringWriter();

		velocityEngine.mergeTemplate(templatePath, "UTF-8", context, stringWriter);
		String text = stringWriter.toString();
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
		
		VelocityContext context = new VelocityContext();
		context.put("fName", userName);
		context.put("resumeType", resumeType);
		context.put("imageURL", imagePath + "templatesImage");
		context.put("contactUsEmail", contactUsEmail);
		context.put("fbLink", fbLink);
		context.put("twitterLink", twitterLink);
		context.put("linkedInLink", linkedInLink);
		context.put("googlePlusLink", googlePlusLink);
		
	    StringWriter stringWriter = new StringWriter();

		velocityEngine.mergeTemplate(templatePath, "UTF-8", context, stringWriter);
		String text = stringWriter.toString();
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
		
		VelocityContext context = new VelocityContext();
		context.put("fName", patientName);
		context.put("doctorName", doctorName);
		context.put("clinicName", clinicName);
		context.put("clinicAddress", clinicAddress);
		context.put("mailRecordCreatedDate", mailRecordCreatedDate);
		context.put("medicalRecordType", medicalRecordType);
		context.put("imageURL", imagePath + "templatesImage");
		context.put("contactUsEmail", contactUsEmail);
		context.put("fbLink", fbLink);
		context.put("twitterLink", twitterLink);
		context.put("linkedInLink", linkedInLink);
		context.put("googlePlusLink", googlePlusLink);
		
	    StringWriter stringWriter = new StringWriter();

		velocityEngine.mergeTemplate(templatePath, "UTF-8", context, stringWriter);
		String text = stringWriter.toString();
		return text;
	}

	public String generatePaymentEmailBody(String orderId, String planName, String amount, String patientName,
			String time, String templatePath) {
		VelocityContext context = new VelocityContext();
		context.put("orderId", orderId);
		context.put("planName", planName);
		context.put("amount", amount);
		context.put("patientName", patientName);
		context.put("time", time);
		context.put("contactUsEmail", contactUsEmail);
		context.put("fbLink", fbLink);
		context.put("twitterLink", twitterLink);
		context.put("linkedInLink", linkedInLink);
		context.put("googlePlusLink", googlePlusLink);
		StringWriter stringWriter = new StringWriter();

		velocityEngine.mergeTemplate(templatePath, "UTF-8", context, stringWriter);
		String text = stringWriter.toString();
		
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
		
		VelocityContext context = new VelocityContext();
		context.put("fName", patientName);
		context.put("doctorName", doctorName);
		context.put("clinicName", clinicName);
		context.put("uniqueFeedbackId", uniqueFeedbackId);
		context.put("imageURL", imagePath + "templatesImage");
		context.put("contactUsEmail", contactUsEmail);
		context.put("fbLink", fbLink);
		context.put("twitterLink", twitterLink);
		context.put("linkedInLink", linkedInLink);
		context.put("googlePlusLink", googlePlusLink);
		
	    StringWriter stringWriter = new StringWriter();

		velocityEngine.mergeTemplate(templatePath, "UTF-8", context, stringWriter);
		String text = stringWriter.toString();
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
		
		VelocityContext context = new VelocityContext();
		context.put("appDeviceType", appDeviceType);
		context.put("appType", appType);
		context.put("bitLink", bitLink);
		context.put("imageURL", imagePath + "templatesImage");
		context.put("contactUsEmail", contactUsEmail);
		context.put("fbLink", fbLink);
		context.put("twitterLink", twitterLink);
		context.put("linkedInLink", linkedInLink);
		context.put("googlePlusLink", googlePlusLink);
		
	    StringWriter stringWriter = new StringWriter();

		velocityEngine.mergeTemplate(templatePath, "UTF-8", context, stringWriter);
		String text = stringWriter.toString();
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
		
		VelocityContext context = new VelocityContext();
		context.put("doctorName", doctorName);
		context.put("clinicName", clinicName);
		context.put("patientName", patientName);
		context.put("recordName", recordName);
		context.put("uniqueRecordId", uniqueRecordId);
		context.put("imageURL", imagePath + "templatesImage");
		context.put("contactUsEmail", contactUsEmail);
		context.put("fbLink", fbLink);
		context.put("twitterLink", twitterLink);
		context.put("linkedInLink", linkedInLink);
		context.put("googlePlusLink", googlePlusLink);
		
	    StringWriter stringWriter = new StringWriter();

	    velocityEngine.mergeTemplate(templatePath, "UTF-8", context, stringWriter);
		String text = stringWriter.toString();
		return text;

	}

	@Override
	@Transactional
	public String generateExceptionEmailBody(String exception) {
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("exceptionMsg", exception);

		VelocityContext context = new VelocityContext();
		context.put("exceptionMsg", exception);

	    StringWriter stringWriter = new StringWriter();

		velocityEngine.mergeTemplate("exceptionMail.vm", "UTF-8", context, stringWriter);
		String text = stringWriter.toString();
		return text;
	}

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
			
			VelocityContext context = new VelocityContext();
			context.put("doctorName", doctorName);
			context.put("dentalImagingLab", dentalImagingLab);
			context.put("patientName", patientName);
			context.put("reports", reports);
			
		    StringWriter stringWriter = new StringWriter();

			velocityEngine.mergeTemplate(templatePath, "UTF-8", context, stringWriter);
		    text = stringWriter.toString();

			return text;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return text;

	}

	@Override
	@Transactional
	public String nutritionReferenceEmailBody(String patientName, String mobileNumber, String birthDate,
			String profession, String gender, String address, String city, String pinCode, String doctorName,
			String planName, String subplan, String templatePath) throws Exception {
		VelocityContext context = new VelocityContext();
		context.put("patientName", patientName);
		context.put("doctorName", doctorName);
		context.put("mobileNumber", mobileNumber);
		context.put("birthDate", birthDate);
		context.put("profession", profession);
		context.put("gender", gender);
		context.put("address", address);
		context.put("city", city);
		context.put("pinCode", pinCode);
		context.put("planName", planName);
		context.put("subplan", subplan);
		StringWriter stringWriter = new StringWriter();

		velocityEngine.mergeTemplate(templatePath, "UTF-8", context, stringWriter);
		String text = stringWriter.toString();
	    return text;
	}

}
