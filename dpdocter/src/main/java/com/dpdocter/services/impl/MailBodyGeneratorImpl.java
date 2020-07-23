package com.dpdocter.services.impl;

import java.io.StringWriter;
import java.util.List;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
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

	@Value(value = "${welcome.link}")
	private String welcomeLink;

	@Value(value = "${login.link}")
	private String loginLink;

	@Value(value = "${reset.password.link}")
	private String RESET_PASSWORD_LINK;

	@Value(value = "${web.link}")
	private String RESET_PASSWORD_WEB_LINK;

//	@Autowired
//	private VelocityEngine velocityEngine;

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
		VelocityContext context = new VelocityContext();
		context.put("fName", fName);
		context.put("doctorName", doctorName);
		context.put("clinicName", clinicName);
		context.put("addedBy", addedBy);
		context.put("link", welcomeLink + "/" + tokenId);
		context.put("loginLink", loginLink);
		context.put("imageURL", imagePath + "templatesImage");
		context.put("contactUsEmail", contactUsEmail);
		context.put("fbLink", fbLink);
		context.put("twitterLink", twitterLink);
		context.put("linkedInLink", linkedInLink);
		context.put("googlePlusLink", googlePlusLink);
		context.put("setPasswordLink", setPasswordLink + "?uid=" + tokenId);

		String text = mergeTemplate(context, templatePath);
		return text;
	}

	@Override
	@Transactional
	public String generateActivationEmailBodyForStaff(String fName, String tokenId, String templatePath,
			String doctorName, String clinicName, String addedby) throws Exception {

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

		String text = mergeTemplate(context, templatePath);
		return text;
	}

	@Override
	@Transactional
	public String doctorWelcomeEmailBody(String fName, String tokenId, String templatePath, String doctorName,
			String clinicName) throws Exception {

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

		String text = mergeTemplate(context, templatePath);
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

		String text = mergeTemplate(context, "forgotPasswordTemplate.vm");
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

		String text = mergeTemplate(context, "contactmail.vm");
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

		String text = mergeTemplate(context, "contactmail.vm");
		return text;
	}

	@Override
	@Transactional
	public String generateDoctorReferenceEmailBody(String fName, String mobileNumber, String locationName,
			String labName) {
		VelocityContext context = new VelocityContext();
		context.put("fName", "Dr." + " " + fName);
		context.put("locationName", locationName);
		context.put("mobileNumber", mobileNumber);
		context.put("labName", labName);

		String text = mergeTemplate(context, "doctorReferenceMail.vm");
		return text;
	}

	@Override
	@Transactional
	public String generatePrescriptionListMail(String collectionBody, String requestBody) {

		VelocityContext context = new VelocityContext();
		context.put("collectionBody", collectionBody);
		context.put("requestBody", requestBody);

		String text = mergeTemplate(context, "prescriptionListMail.vm");
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

		VelocityContext context = new VelocityContext();
		context.put("fName", firstName);
		context.put("link", RESET_PASSWORD_WEB_LINK);
		context.put("imageURL", imagePath + "templatesImage");
		context.put("contactUsEmail", contactUsEmail);
		context.put("fbLink", fbLink);
		context.put("twitterLink", twitterLink);
		context.put("linkedInLink", linkedInLink);
		context.put("googlePlusLink", googlePlusLink);

		String text = mergeTemplate(context, "addIssueTemplate.vm");
		return text;
	}

	@Override
	@Transactional
	public String generateResetPasswordSuccessEmailBody(String firstName) {

		VelocityContext context = new VelocityContext();
		context.put("fName", firstName);
		context.put("link", RESET_PASSWORD_WEB_LINK);
		context.put("imageURL", imagePath + "templatesImage");
		context.put("contactUsEmail", contactUsEmail);
		context.put("fbLink", fbLink);
		context.put("twitterLink", twitterLink);
		context.put("linkedInLink", linkedInLink);
		context.put("googlePlusLink", googlePlusLink);

		String text = mergeTemplate(context, "resetPasswordSuccess.vm");
		return text;
	}

	@Override
	@Transactional
	public String generateRecordsShareOtpBeforeVerificationEmailBody(String emailAddress, String firstName,
			String doctorName) {

		VelocityContext context = new VelocityContext();
		context.put("fName", firstName);
		context.put("doctorName", doctorName);
		context.put("imageURL", imagePath + "templatesImage");
		context.put("contactUsEmail", contactUsEmail);
		context.put("fbLink", fbLink);
		context.put("twitterLink", twitterLink);
		context.put("linkedInLink", linkedInLink);
		context.put("googlePlusLink", googlePlusLink);

		String text = mergeTemplate(context, "recordShareOtpBeforeVerificationTemplate.vm");
		return text;
	}

	@Override
	@Transactional
	public String generateRecordsShareOtpAfterVerificationEmailBody(String emailAddress, String firstName,
			String doctorName) {
		VelocityContext context = new VelocityContext();
		context.put("fName", firstName);
		context.put("doctorName", doctorName);
		context.put("imageURL", imagePath + "templatesImage");
		context.put("contactUsEmail", contactUsEmail);
		context.put("fbLink", fbLink);
		context.put("twitterLink", twitterLink);
		context.put("linkedInLink", linkedInLink);
		context.put("googlePlusLink", googlePlusLink);

		String text = mergeTemplate(context, "recordShareOtpAfterVerificationTemplate.vm");
		return text;
	}

	@Override
	@Transactional
	public String generateAppointmentEmailBody(String doctorName, String patientName, String dateTime,
			String clinicName, String templatePath, String branch) {
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

		String text = mergeTemplate(context, templatePath);
		return text;
	}

	@Override
	@Transactional
	public String generateEmailBody(String userName, String resumeType, String templatePath) throws Exception {

		VelocityContext context = new VelocityContext();
		context.put("fName", userName);
		context.put("resumeType", resumeType);
		context.put("imageURL", imagePath + "templatesImage");
		context.put("contactUsEmail", contactUsEmail);
		context.put("fbLink", fbLink);
		context.put("twitterLink", twitterLink);
		context.put("linkedInLink", linkedInLink);
		context.put("googlePlusLink", googlePlusLink);

		String text = mergeTemplate(context, templatePath);
		return text;
	}

	@Override
	public String generateEMREmailBody(String patientName, String doctorName, String clinicName, String clinicAddress,
			String mailRecordCreatedDate, String medicalRecordType, String templatePath) {

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

		String text = mergeTemplate(context, templatePath);
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

		String text = mergeTemplate(context, templatePath);
		return text;
	}

	@Override
	public String generateFeedbackEmailBody(String patientName, String doctorName, String clinicName,
			String uniqueFeedbackId, String templatePath) {

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

		String text = mergeTemplate(context, templatePath);
		return text;
	}

	@Override
	public String generateAppLinkEmailBody(String appType, String bitLink, String appDeviceType, String templatePath) {

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

		String text = mergeTemplate(context, templatePath);
		return text;
	}

	@Override
	public String generateRecordEmailBody(String doctorName, String clinicName, String patientName, String recordName,
			String uniqueRecordId, String templatePath) {

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

		String text = mergeTemplate(context, templatePath);
		return text;
	}

	@Override
	@Transactional
	public String generateExceptionEmailBody(String exception) {

		VelocityContext context = new VelocityContext();
		context.put("exceptionMsg", exception);

		String text = mergeTemplate(context, "exceptionMail.vm");
		return text;
	}

	private String mergeTemplate(VelocityContext context, String templatePath) {
		VelocityEngine velocityEngine = new VelocityEngine();
		velocityEngine.setProperty("input.encoding", "UTF-8");
		velocityEngine.setProperty("output.encoding", "UTF-8");
		velocityEngine.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS,
				"org.apache.velocity.runtime.log.Log4JLogChute");
		velocityEngine.setProperty("runtime.log", "/var/log/dpdocter/velocity.log");
		velocityEngine.setProperty("resource.loader", "class, file");
		velocityEngine.setProperty("class.resource.loader.class",
				"org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
		velocityEngine.setProperty("file.resource.loader.class",
				"org.apache.velocity.runtime.resource.loader.FileResourceLoader");
		velocityEngine.setProperty("file.resource.loader.path", "/opt/tomcat/latest/webapps/dpdocter/WEB-INF/classes");
		velocityEngine.setProperty("class.resource.loader.cache", "false");
		velocityEngine.setProperty("file.resource.loader.cache", "true");
		velocityEngine.init();

		Template template = velocityEngine.getTemplate(templatePath);

		StringWriter stringWriter = new StringWriter();

		template.setEncoding("UTF-8");
		template.merge(context, stringWriter);
		String text = stringWriter.toString();

		return text;
	}

	@Override
	public String generateDentalImagingInvoiceEmailBody(String doctorName, String dentalImagingLab, String patientName,
			List<MailAttachment> reports, String templatePath) {

		VelocityContext context = new VelocityContext();
		context.put("doctorName", doctorName);
		context.put("dentalImagingLab", dentalImagingLab);
		context.put("patientName", patientName);
		context.put("reports", reports);

		String text = mergeTemplate(context, templatePath);
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

		String text = mergeTemplate(context, templatePath);
		return text;
	}

	@Override
	@Transactional
	public String verifyEmailBody(String firstName, String tokenId, String templatePath) throws Exception {

		VelocityContext context = new VelocityContext();
		context.put("fName", firstName);
		context.put("link", link + "?uid=" + tokenId);
		context.put("imageURL", imagePath + "templatesImage");
		context.put("fbLink", fbLink);
		context.put("contactUsEmail", contactUsEmail);
		context.put("twitterLink", twitterLink);
		context.put("linkedInLink", linkedInLink);
		context.put("googlePlusLink", googlePlusLink);

		String text = mergeTemplate(context, templatePath);
		return text;
	}

	@Override
	public String generateFreeQuestionAnswerEmailBody(String emailAddress, String name,  String locationName, String templatePath,
			String doctorName) {
		VelocityContext context = new VelocityContext();
		context.put("name", name);
		context.put("locationName", locationName);
		context.put("doctorName", doctorName);
		String text = mergeTemplate(context, templatePath);
		return text;
	}
	
	@Override
	@Transactional
	public String subscriptionPaymentEmailBody(String firstName, 
			String createdDate,String transactionId,String receipt,String totalCost,
			String packageName,String paymentMode,int duration,String templatePath) {

		VelocityContext context = new VelocityContext();
		context.put("fName", firstName);
		context.put("createdDate", createdDate);
		context.put("receipt", receipt);
		context.put("totalCost", totalCost);
		context.put("transactionId", transactionId);
		context.put("packageName", packageName);
		context.put("paymentMode", paymentMode);
		context.put("duration", duration);

		String text = mergeTemplate(context, templatePath);
		return text;
	}

}
