package com.dpdocter.services.impl;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClient;
import com.amazonaws.services.simpleemail.model.RawMessage;
import com.amazonaws.services.simpleemail.model.SendRawEmailRequest;
import com.dpdocter.beans.MailAttachment;
import com.dpdocter.services.MailService;

/***
 * 
 * @author veeraj
 *
 */
@Service
public class MailServiceImpl implements MailService {

    @Value(value = "${mail.from}")
    private String FROM;

    @Value(value = "${mail.port}")
    private String PORT;

    @Value(value = "${mail.host}")
    private String HOST;

    @Value(value = "${mail.aws.key.id}")
    private String AWS_KEY;

    @Value(value = "${mail.aws.secret.key}")
    private String AWS_SECRET_KEY;
    
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
    @Transactional
    public void sendEmail(String to, String subject, String body, MailAttachment mailAttachment) throws MessagingException {
	try {
	    Session session = Session.getInstance(new Properties());
	    MimeMessage mimeMessage = new MimeMessage(session);
	    mimeMessage.setSubject(subject);

	    MimeMultipart mimeMultipart = new MimeMultipart();
	    BodyPart p = new MimeBodyPart();
	    p.setContent(body, "text/html");
	    mimeMultipart.addBodyPart(p);
	    mimeMessage.setContent(mimeMultipart, "multipart/mixed");
	    if (mailAttachment != null) {
		mimeMessage.setFileName(mailAttachment.getAttachmentName());
		DataSource ds;
		if (mailAttachment.getFileSystemResource() == null)
		    ds = new ByteArrayDataSource(mailAttachment.getInputStream(), "application/octet-stream");
		else
		    ds = new ByteArrayDataSource(new FileInputStream(mailAttachment.getFileSystemResource().getFile()), "application/octet-stream");
		mimeMessage.setDataHandler(new DataHandler(ds));
	    }

	    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	    mimeMessage.writeTo(outputStream);
	    RawMessage rawMessage = new RawMessage(ByteBuffer.wrap(outputStream.toByteArray()));

	    List<String> list = Arrays.asList(to);
	    SendRawEmailRequest rawEmailRequest = new SendRawEmailRequest(rawMessage);
	    rawEmailRequest.setDestinations(list);
	    rawEmailRequest.setSource(FROM);
	    BasicAWSCredentials credentials = new BasicAWSCredentials(AWS_KEY, AWS_SECRET_KEY);
	    new AmazonSimpleEmailServiceClient(credentials).sendRawEmail(rawEmailRequest);
	} catch (Exception ex) {
	    System.out.println("The email was not sent.");
	    System.out.println("Error message: " + ex.getMessage());
	}
    }

    @Override
    @Transactional
    public void sendEmailMultiAttach(String to, String subject, String body, List<MailAttachment> mailAttachments) throws MessagingException {
	try {
	    Session session = Session.getInstance(new Properties());
	    MimeMessage mimeMessage = new MimeMessage(session);
	    mimeMessage.setSubject(subject);
	    MimeMultipart mimeMultipart = new MimeMultipart();
	    BodyPart p = new MimeBodyPart();
	    p.setContent(body, "text/html");
	    mimeMultipart.addBodyPart(p);

	    if (mailAttachments != null && !mailAttachments.isEmpty()) {
		for (MailAttachment mailAttachment : mailAttachments) {
			System.out.println(mailAttachments.size());
		    mimeMessage.setFileName(mailAttachment.getAttachmentName());
		    DataSource ds = new ByteArrayDataSource(new FileInputStream(mailAttachment.getFileSystemResource().getFile()), "application/octet-stream");
		    mimeMessage.setDataHandler(new DataHandler(ds));
		}
	    }
	    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	    mimeMessage.writeTo(outputStream);
	    RawMessage rawMessage = new RawMessage(ByteBuffer.wrap(outputStream.toByteArray()));

	    List<String> list = Arrays.asList(to);
	    SendRawEmailRequest rawEmailRequest = new SendRawEmailRequest(rawMessage);
	    rawEmailRequest.setDestinations(list);
	    rawEmailRequest.setSource(FROM);
	    BasicAWSCredentials credentials = new BasicAWSCredentials(AWS_KEY, AWS_SECRET_KEY);
	    new AmazonSimpleEmailServiceClient(credentials).sendRawEmail(rawEmailRequest);
	} catch (Exception ex) {
	    System.out.println("The email was not sent.");
	    System.out.println("Error message: " + ex.getMessage());
	}
    }
}
