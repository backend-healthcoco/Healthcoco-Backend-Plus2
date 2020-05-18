package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.DoctorContactUs;
import com.dpdocter.beans.MailAttachment;
import com.dpdocter.collections.UserCollection;

public interface MailBodyGenerator {
	/*
	 * public String generateActivationEmailBody(String fName, String tokenId,
	 * String templatePath, String doctorName, String clinicName) throws Exception;
	 */

	public String generateForgotPasswordEmailBody(String fName, String tokenId);

	public String generateForgotUsernameEmailBody(List<UserCollection> userCollection);

	public String generateIssueTrackEmailBody(String userName, String firstName, String middleName, String lastName);

	public String generateResetPasswordSuccessEmailBody(String firstName);

	public String generateRecordsShareOtpBeforeVerificationEmailBody(String emailAddress, String firstName,
			String doctorName);

	public String generateRecordsShareOtpAfterVerificationEmailBody(String emailAddress, String firstName,
			String doctorName);

	String generateAppointmentEmailBody(String doctorName, String patientName, String dateTime, String clinicName,
			String templatePath, String branch);

	String generateEmailBody(String userName, String resumeType, String templatePath) throws Exception;

	public String generateEMREmailBody(String patientName, String doctorName, String clinicName, String clinicAddress,
			String mailRecordCreatedDate, String medicalRecordType, String templatePath);

	public String generatePaymentEmailBody(String orderId, String planName, String amount, String patientName,
			String time, String templatePath);

	public String generateFeedbackEmailBody(String patientName, String doctorName, String locationName,
			String uniqueFeedbackId, String templatePath);

	String generateAppLinkEmailBody(String appType, String bitLink, String appDeviceType, String templatePath);

	String generateRecordEmailBody(String doctorName, String clinicName, String patientName, String recordName,
			String uniqueRecordId, String templatePath);

	public String generateContactEmailBody(String fName, String type, String mobileNumber, String emailAddress,
			String city);

	String generateContactEmailBody(DoctorContactUs contactUs, String type);

	String generatePrescriptionListMail(String collectionBody, String requestBody);

	String generateExceptionEmailBody(String exception);

	public String generateDoctorReferenceEmailBody(String fName, String mobileNumber, String locationName,
			String labName);

	String generateDentalImagingInvoiceEmailBody(String doctorName, String dentalImagingLab, String patientName,
			List<MailAttachment> reports, String templatePath);

	String doctorWelcomeEmailBody(String fName, String tokenId, String templatePath, String doctorName,
			String clinicName) throws Exception;

	String generateActivationEmailBody(String fName, String tokenId, String templatePath, String doctorName,
			String clinicName, String addedBy) throws Exception;

	public String nutritionReferenceEmailBody(String patientName, String mobileNumber, String birthDate,
			String profession, String gender, String address, String city, String pinCode, String doctorName,
			String planName, String subplan,String templatePath) throws Exception;

	String generateActivationEmailBodyForStaff(String fName, String tokenId, String templatePath, String doctorName,
			String clinicName, String addedBy) throws Exception;
	
	public String verifyEmailBody(String firstName,String tokenId, String templatePath) throws Exception;
}
