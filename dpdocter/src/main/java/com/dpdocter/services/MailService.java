package com.dpdocter.services;

import java.util.List;

import javax.mail.MessagingException;

import com.dpdocter.beans.MailAttachment;

public interface MailService {
	Boolean sendEmail(String to, String subject, String body, MailAttachment mailAttachment) throws MessagingException;

    Boolean sendEmailMultiAttach(String to, String subject, String body, List<MailAttachment> mailAttachments) throws MessagingException;
    
    Boolean sendExceptionMail(String body) throws MessagingException;
    
    Boolean sendExceptionMail(String subject,String body) throws MessagingException;

	Boolean sendMailToIOSteam(String subject, String body) throws MessagingException;

	Boolean sendEmailWithoutAttachment(String to, String subject, String body) throws MessagingException;
}
