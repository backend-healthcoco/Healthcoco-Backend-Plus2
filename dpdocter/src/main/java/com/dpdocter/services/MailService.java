package com.dpdocter.services;

import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;

import com.dpdocter.beans.MailAttachment;
import com.dpdocter.request.MailSubsciptionRequest;

public interface MailService {
	Boolean sendEmail(String to, String subject, String body, MailAttachment mailAttachment) throws MessagingException;

	Boolean sendEmailMultiAttach(String to, String subject, String body, List<MailAttachment> mailAttachments)
			throws MessagingException;

	Boolean sendExceptionMail(String body) throws MessagingException;

	Boolean sendExceptionMail(String subject, String body) throws MessagingException;

	Boolean sendMailToIOSteam(String subject, String body) throws MessagingException;

	public Boolean subscribeMail(MailSubsciptionRequest request);

	Boolean sendEmailWithoutAttachment(String to, String subject, String body) throws MessagingException;

	Boolean sendEmailWithPdf(List<String> toList, String subject, String body, byte[] pdfBytes) throws MessagingException;

	Boolean sendEmailWithCsv(List<String> emails, String subject, String htmlBody, byte[] csvBytes);

	Boolean sendEmailWithMultipleCsvAttachments(List<String> emails, String subject, String htmlBody,
			Map<String, byte[]> csvFiles);
}
