package com.dpdocter.services;

import javax.mail.MessagingException;

import com.dpdocter.beans.MailAttachment;

public interface MailService {
	void sendEmail(String to, String subject, String body,MailAttachment mailAttachment) throws MessagingException;
}
