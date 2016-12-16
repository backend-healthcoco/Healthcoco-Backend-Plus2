package com.dpdocter.thread;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dpdocter.beans.MailAttachment;
import com.dpdocter.services.MailService;

public class EmailThread extends Thread {

	private String to;
	private String subject;
	private String body;
	private MailAttachment attachment;

	public EmailThread(String to, String subject, String body, MailAttachment attachment) {
		this.to = to;
		this.subject = subject;
		this.body = body;
		this.attachment = attachment;
	}

	@Override
	public void run() {
		/*try {
			//mailService.sendEmail(to, subject, body, attachment);
		} catch (MessagingException e) {
			e.printStackTrace();
		}*/
	}

}
