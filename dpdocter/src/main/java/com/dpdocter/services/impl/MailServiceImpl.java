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
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClient;
import com.amazonaws.services.simpleemail.model.RawMessage;
import com.amazonaws.services.simpleemail.model.SendRawEmailRequest;
import com.dpdocter.beans.MailAttachment;
import com.dpdocter.collections.EmailSubscriptionCollection;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.repository.EmailSubscriptionRepository;
import com.dpdocter.request.MailSubsciptionRequest;
import com.dpdocter.services.MailService;

@Service
public class MailServiceImpl implements MailService {

	@Value(value = "${mail.from}")
	private String FROM;

	@Value(value = "${mail.from.name}")
	private String FROM_NAME;

	@Value(value = "${mail.port}")
	private String PORT;

	@Value(value = "${mail.aws.key.id}")
	private String AWS_KEY;

	@Value(value = "${mail.aws.secret.key}")
	private String AWS_SECRET_KEY;

	@Value(value = "${mail.aws.region}")
	private String AWS_REGION;

	@Value(value = "${mail.exception.to}")
	private String TO;

	@Value(value = "${mail.exception.to.ios}")
	private String TO_IOS;

	@Value(value = "${mail.excetion.subject}")
	private String SUBJECT;

	@Value(value = "${is.env.production}")
	private String PROD_ENV;

	@Autowired
	private EmailSubscriptionRepository emailSubscriptionRepository;

	@Override
	@Transactional
	public Boolean sendEmail(String to, String subject, String body, MailAttachment mailAttachment)
			throws MessagingException {
		Boolean respone = false;
		try {
			Session session = Session.getInstance(new Properties());
			MimeMessage mimeMessage = new MimeMessage(session);
			mimeMessage.setSubject(subject);
			mimeMessage.setFrom(new InternetAddress(FROM, FROM_NAME));
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
					ds = new ByteArrayDataSource(new FileInputStream(mailAttachment.getFileSystemResource().getFile()),
							"application/octet-stream");

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
			AmazonSimpleEmailServiceClient amazonSimpleEmailServiceClient = new AmazonSimpleEmailServiceClient(
					credentials);
			amazonSimpleEmailServiceClient.setRegion(Region.getRegion(Regions.fromName(AWS_REGION)));
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
	public Boolean sendEmailMultiAttach(String to, String subject, String body, List<MailAttachment> mailAttachments)
			throws MessagingException {
		Boolean respone = false;
		try {
			Session session = Session.getInstance(new Properties());
			MimeMessage mimeMessage = new MimeMessage(session);
			mimeMessage.setSubject(subject);
			mimeMessage.setFrom(new InternetAddress(FROM, FROM_NAME));
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
					DataSource ds;
					if (mailAttachment.getFileSystemResource() == null)
						ds = new ByteArrayDataSource(mailAttachment.getInputStream(), "application/octet-stream");
					else
						ds = new ByteArrayDataSource(
								new FileInputStream(mailAttachment.getFileSystemResource().getFile()),
								"application/octet-stream");

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
			AmazonSimpleEmailServiceClient amazonSimpleEmailServiceClient = new AmazonSimpleEmailServiceClient(
					credentials);
			amazonSimpleEmailServiceClient.setRegion(Region.getRegion(Regions.fromName(AWS_REGION)));
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
	public Boolean sendExceptionMail(String body) throws MessagingException {
		Boolean status = false;
		if (PROD_ENV.equalsIgnoreCase("true")) {
			status = sendEmail(TO, SUBJECT, body, null);
		}
		return status;

	}

	@Override
	public Boolean sendExceptionMail(String subject, String body) throws MessagingException {
		Boolean status = false;
		if (PROD_ENV.equalsIgnoreCase("true")) {
			status = sendEmail(TO, subject, body, null);
		}
		return status;
	}

	@Override
	public Boolean sendMailToIOSteam(String subject, String body) throws MessagingException {
		Boolean status = false;
		status = sendEmail(TO_IOS, subject, body, null);
		return status;
	}

	@Override
	public Boolean subscribeMail(MailSubsciptionRequest request) {
		Boolean status = false;
		try {
			EmailSubscriptionCollection emailSubscriptionCollection = emailSubscriptionRepository
					.findBySubscriberId(new ObjectId(request.getSubscriberId()));
			if (emailSubscriptionCollection == null) {
				emailSubscriptionCollection = new EmailSubscriptionCollection();
			}
			emailSubscriptionCollection.setDiscarded(request.getDiscarded());
			emailSubscriptionCollection.setReason(request.getReason());
			emailSubscriptionCollection.setSubscriberId(new ObjectId(request.getSubscriberId()));
			emailSubscriptionRepository.save(emailSubscriptionCollection);
			status = true;
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown,
					"Error while mail unSubcription : " + e.getCause().getMessage());

		}
		return status;
	}

}
