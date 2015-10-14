package com.dpdocter.services;

import java.io.IOException;
import java.util.List;

import javax.mail.MessagingException;

import com.dpdocter.beans.MailAttachment;

public interface MailService {
    void sendEmail(String to, String subject, String body, MailAttachment mailAttachment) throws MessagingException;

    void sendEmailMultiAttach(String to, String subject, String body, List<MailAttachment> mailAttachments) throws MessagingException;
}
