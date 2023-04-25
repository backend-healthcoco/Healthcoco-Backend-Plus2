package com.dpdocter.thread;

import com.dpdocter.beans.MailAttachment;

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
	}

}
