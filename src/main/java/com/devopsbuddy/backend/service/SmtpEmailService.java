package com.devopsbuddy.backend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

/**
 * Created by madgergely on 2017.02.26..
 */
public class SmtpEmailService extends AbstractEmailService {

    /** The application logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(SmtpEmailService.class);
    private static final String SENDING_EMAIL = "Sending email for: {}";
    private static final String EMAIL_SENT = "Email sent.";

    @Autowired
    private MailSender mailSender;

    @Override
    public void sendGenericEmailMessage(SimpleMailMessage message) {
        LOGGER.debug(SENDING_EMAIL, message);
        mailSender.send(message);
        LOGGER.info(EMAIL_SENT);
    }
}
