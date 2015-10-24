package com.dpdocter.services.impl;

import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.dpdocter.beans.MailAttachment;
import com.dpdocter.services.MailService;

/***
 * 
 * @author veeraj
 *
 */
@Service
public class MailServiceImpl implements MailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Value(value = "${mail.from}")
    private String from;

    /**
     * @param String
     *            to
     * @param String
     *            subject
     * @param String
     *            body
     * @param MailAttachment
     *            (Optional) - If any attachment is to be send with mail else
     *            should be NULL. This method sends Simple mails,MIME mails
     */
    @Override
    public void sendEmail(String to, String subject, String body, MailAttachment mailAttachment) throws MessagingException {
	MimeMessage message = javaMailSender.createMimeMessage();

	MimeMessageHelper helper = new MimeMessageHelper(message, true);
	helper.setFrom(from);
	helper.setTo(to);
	helper.setSubject(subject);
	helper.setText(body, true);
	if (mailAttachment != null) {
	    helper.addAttachment(mailAttachment.getAttachmentName(), mailAttachment.getFileSystemResource());
	}
	javaMailSender.send(message);
    }

    @Override
    public void sendEmailMultiAttach(String to, String subject, String body, List<MailAttachment> mailAttachments) throws MessagingException {
	MimeMessage message = javaMailSender.createMimeMessage();

	MimeMessageHelper helper = new MimeMessageHelper(message, true);
	helper.setFrom(from);
	helper.setTo(to);
	helper.setSubject(subject);
	helper.setText(body);
	if (mailAttachments != null && !mailAttachments.isEmpty()) {
	    for (MailAttachment mailAttachment : mailAttachments) {
		helper.addAttachment(mailAttachment.getAttachmentName(), mailAttachment.getFileSystemResource());
	    }
	}
	javaMailSender.send(message);
    }

}
