package com.devopsbuddy.backend.service;

import static java.lang.String.format;

import com.devopsbuddy.web.domain.frontend.Feedback;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;

/**
 * Created by madgergely on 2017.02.26..
 */
public abstract class AbstractEmailService implements EmailService {

    private static final String FEEDBACK_RECEIVED = "[DevOps Buddy]: Feedback received from %s %s!";

    @Value("${default.to.address}")
    private String defaultToAddress;

    protected SimpleMailMessage prepareSimpleMailMessageFromFeedback(Feedback feedback) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(defaultToAddress);
        message.setFrom(feedback.getEmail());
        message.setSubject(format(FEEDBACK_RECEIVED, feedback.getFirstName(), feedback.getLastName()));
        message.setText(feedback.getFeedback());
        return message;
    }

    @Override
    public void sendFeedbackEmail(Feedback feedback) {
        sendGenericEmailMessage(prepareSimpleMailMessageFromFeedback(feedback));
    }
}
