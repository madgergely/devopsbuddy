package com.devopsbuddy.backend.service;

import com.devopsbuddy.web.domain.frontend.Feedback;
import org.springframework.mail.SimpleMailMessage;

/**
 * Created by madgergely on 2017.02.26..
 */
public interface EmailService {

    /**
     * Sends an email with the content in the Feedback POJO
     * @param feedback The feedback
     */
    void sendFeedbackEmail(Feedback feedback);

    /**
     * Sends an email with the content of the Simple Mail Message object.
     * @param message The object containing the email content
     */
    void sendGenericEmailMessage(SimpleMailMessage message);
}
