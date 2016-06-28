package com.dpdocter.services;

import java.util.List;

import javax.ws.rs.core.UriInfo;

import com.dpdocter.collections.UserCollection;

public interface MailBodyGenerator {
    public String generateActivationEmailBody(String fName, String tokenId, String templatePath) throws Exception;

    public String generateForgotPasswordEmailBody(String emailAddress, String fName, String mName, String lName, String userId, UriInfo uriInfo);

    public String generateForgotUsernameEmailBody(List<UserCollection> userCollection);

    public String generatePatientRegistrationEmailBody(String userName, char[] password, String firstName, String lastName);

    public String generateIssueTrackEmailBody(String userName, String firstName, String middleName, String lastName);

    public String generateResetPasswordSuccessEmailBody(String emailAddress, String firstName, UriInfo uriInfo);

    public String generateRecordsShareOtpBeforeVerificationEmailBody(String emailAddress, String firstName, String doctorName, UriInfo uriInfo);

    public String generateRecordsShareOtpAfterVerificationEmailBody(String emailAddress, String firstName, String doctorName, UriInfo uriInfo);

    public String generateRecordsUploadedEmailBody(String userName, String firstName, String middleName, String lastName);

	String generateAppointmentCancelEmailBody(String doctorName, String patientName, String dateTime, String clinicName, String templatePath);

	String generateEmailBody(String userName, String resumeType, String templatePath) throws Exception;

	public String generateEMREmailBody(String patientName, String doctorName, String clinicName, String clinicAddress, String mailRecordCreatedDate, String medicalRecordType, String templatePath);
}
