package com.devopsbuddy.backend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;

/**
 * Created by madgergely on 2017.02.26..
 */
public class MockEmailService extends AbstractEmailService {

    /** The application logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(MockEmailService.class);

    @Override
    public void sendGenericEmailMessage(SimpleMailMessage message) {
        LOGGER.debug("Simulating an email service...");
        LOGGER.info(message.toString());
        LOGGER.debug("Email sent.");
    }
}
