package com.dpdocter.services;

import java.util.List;

import com.dpdocter.collections.UserCollection;

public interface MailBodyGenerator {
    public String generateActivationEmailBody(String fName, String tokenId, String templatePath, String doctorName,String clinicName) throws Exception;

    public String generateForgotPasswordEmailBody(String fName, String tokenId);

    public String generateForgotUsernameEmailBody(List<UserCollection> userCollection);

    public String generateIssueTrackEmailBody(String userName, String firstName, String middleName, String lastName);

    public String generateResetPasswordSuccessEmailBody(String firstName);

    public String generateRecordsShareOtpBeforeVerificationEmailBody(String emailAddress, String firstName, String doctorName);

    public String generateRecordsShareOtpAfterVerificationEmailBody(String emailAddress, String firstName, String doctorName);

 	String generateAppointmentEmailBody(String doctorName, String patientName, String dateTime, String clinicName, String templatePath);

	String generateEmailBody(String userName, String resumeType, String templatePath) throws Exception;

	public String generateEMREmailBody(String patientName, String doctorName, String clinicName, String clinicAddress, String mailRecordCreatedDate, String medicalRecordType, String templatePath);

	public String generateFeedbackEmailBody(String patientName, String doctorName, String locationName, String uniqueFeedbackId, String templatePath);

	String generateAppLinkEmailBody(String appType, String bitLink, String appDeviceType, String templatePath);

}
