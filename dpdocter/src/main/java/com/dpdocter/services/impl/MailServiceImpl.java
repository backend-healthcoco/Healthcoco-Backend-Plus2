package com.dpdocter.services.impl;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
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
    public Boolean sendEmail(String to, String subject, String body, MailAttachment mailAttachment) throws MessagingException {
    	Boolean respone = false;
	try {
	    Session session = Session.getInstance(new Properties());
	    MimeMessage mimeMessage = new MimeMessage(session);
	    mimeMessage.setSubject(subject);

	    Multipart mainMultipart = new MimeMultipart("related");
	    Multipart htmlAndTextMultipart = new MimeMultipart("alternative");
	    MimeBodyPart htmlBodyPart = new MimeBodyPart();
	    htmlBodyPart.setContent(body, "text/html; charset=utf-8");
	    htmlAndTextMultipart.addBodyPart(htmlBodyPart);
	    
	    MimeBodyPart htmlAndTextBodyPart = new MimeBodyPart();
	    htmlAndTextBodyPart.setContent(htmlAndTextMultipart);
	    mainMultipart.addBodyPart(htmlAndTextBodyPart);

	    if (mailAttachment != null) {
		DataSource ds;
		if (mailAttachment.getFileSystemResource() == null)
		    ds = new ByteArrayDataSource(mailAttachment.getInputStream(), "application/octet-stream");
		else
		    ds = new ByteArrayDataSource(new FileInputStream(mailAttachment.getFileSystemResource().getFile()), "application/octet-stream");
	    
		MimeBodyPart filePart = new MimeBodyPart();
	    
	    filePart.setDataHandler(new DataHandler(ds));
	    filePart.setFileName(mailAttachment.getAttachmentName());
	    mainMultipart.addBodyPart(filePart);
	    }

	    mimeMessage.setContent(mainMultipart);
	    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	    mimeMessage.writeTo(outputStream);
	    RawMessage rawMessage = new RawMessage(ByteBuffer.wrap(outputStream.toByteArray()));

	    List<String> list = Arrays.asList(to);
	    SendRawEmailRequest rawEmailRequest = new SendRawEmailRequest(rawMessage);
	    rawEmailRequest.setDestinations(list);
	    rawEmailRequest.setSource(FROM);
	    BasicAWSCredentials credentials = new BasicAWSCredentials(AWS_KEY, AWS_SECRET_KEY);
	    AmazonSimpleEmailServiceClient amazonSimpleEmailServiceClient = new AmazonSimpleEmailServiceClient(credentials);
	    amazonSimpleEmailServiceClient.configureRegion(Regions.US_WEST_2);
	    amazonSimpleEmailServiceClient.sendRawEmail(rawEmailRequest);
	    outputStream.close();
	    respone = true;
	} catch (Exception ex) {
	    System.out.println("The email was not sent.");
	    System.out.println("Error message: " + ex.getMessage());
	}
	return respone;
    }

    @Override
    @Transactional
    public Boolean sendEmailMultiAttach(String to, String subject, String body, List<MailAttachment> mailAttachments) throws MessagingException {
    	Boolean respone = false;
	try {
	    Session session = Session.getInstance(new Properties());
	    MimeMessage mimeMessage = new MimeMessage(session);
	    mimeMessage.setSubject(subject);

	    Multipart mainMultipart = new MimeMultipart("related");
	    Multipart htmlAndTextMultipart = new MimeMultipart("alternative");
	    MimeBodyPart htmlBodyPart = new MimeBodyPart();
	    htmlBodyPart.setContent(body, "text/html; charset=utf-8");
	    htmlAndTextMultipart.addBodyPart(htmlBodyPart);
	    
	    MimeBodyPart htmlAndTextBodyPart = new MimeBodyPart();
	    htmlAndTextBodyPart.setContent(htmlAndTextMultipart);
	    mainMultipart.addBodyPart(htmlAndTextBodyPart);


	    if (mailAttachments != null && !mailAttachments.isEmpty()) {
		for (MailAttachment mailAttachment : mailAttachments) {
			mimeMessage.setFileName(mailAttachment.getAttachmentName());
		    DataSource ds = new ByteArrayDataSource(new FileInputStream(mailAttachment.getFileSystemResource().getFile()), "application/octet-stream");
		    
			MimeBodyPart filePart = new MimeBodyPart();
		    
		    filePart.setDataHandler(new DataHandler(ds));
		    filePart.setFileName(mailAttachment.getAttachmentName());
		    mainMultipart.addBodyPart(filePart);
		    }
	    }
		    mimeMessage.setContent(mainMultipart);
		    
		
	    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	    mimeMessage.writeTo(outputStream);
	    RawMessage rawMessage = new RawMessage(ByteBuffer.wrap(outputStream.toByteArray()));

	    List<String> list = Arrays.asList(to);
	    SendRawEmailRequest rawEmailRequest = new SendRawEmailRequest(rawMessage);
	    rawEmailRequest.setDestinations(list);
	    rawEmailRequest.setSource(FROM);
	    BasicAWSCredentials credentials = new BasicAWSCredentials(AWS_KEY, AWS_SECRET_KEY);
	    new AmazonSimpleEmailServiceClient(credentials).sendRawEmail(rawEmailRequest);
	    outputStream.close();
	    respone = true;
	} catch (Exception ex) {
	    System.out.println("The email was not sent.");
	    System.out.println("Error message: " + ex.getMessage());
	}
	return respone;
    }
}
