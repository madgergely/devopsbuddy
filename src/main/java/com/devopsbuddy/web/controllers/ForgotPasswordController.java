package com.devopsbuddy.web.controllers;

import com.devopsbuddy.backend.persistence.domain.backend.PasswordResetToken;
import com.devopsbuddy.backend.persistence.domain.backend.User;
import com.devopsbuddy.backend.service.EmailService;
import com.devopsbuddy.backend.service.I18NService;
import com.devopsbuddy.backend.service.PasswordResetTokenService;
import com.devopsbuddy.utils.UserUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

/**
 * Created by madgergely on 2017.03.25..
 */
@Controller
public class ForgotPasswordController {

    public static final String FORGOT_PASSWORD_URL_MAPPING = "/forgotmypassword";
    public static final String CHANGE_PASSWORD_PATH = "/changeuserpassword";

    /** The application logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(ForgotPasswordController.class);

    private static final String MAIL_SENT_KEY = "mailSent";
    private static final String EMAIL_ADDRESS_VIEW_NAME = "forgotpassword/emailForm";
    private static final String EMAIL_MESSAGE_TEXT_PROPERTY_NAME = "forgotmypassword.email.text";

    @Autowired
    private PasswordResetTokenService passwordResetTokenService;

    @Autowired
    private I18NService i18NService;

    @Autowired
    private EmailService emailService;

    @Value("${webmaster.email}")
    private String webMasterEmail;

    @RequestMapping(value = FORGOT_PASSWORD_URL_MAPPING, method = RequestMethod.GET)
    public String forgotPasswordget() {
        return EMAIL_ADDRESS_VIEW_NAME;
    }

    @RequestMapping(value = FORGOT_PASSWORD_URL_MAPPING, method = RequestMethod.POST)
    public String forgotPasswordPost(HttpServletRequest request, @RequestParam("email") String email, ModelMap modelMap) {

        PasswordResetToken passwordResetToken = passwordResetTokenService.createPasswordResetTokenForEmail(email);

        if (null == passwordResetToken) {
            LOGGER.warn("Couldn't find a password reset token for email {}", email);
        } else {
            User user = passwordResetToken.getUser();
            String token = passwordResetToken.getToken();

            String resetPasswordUrl = UserUtils.createPasswordResetUrl(request, user.getId(), token);
            LOGGER.debug("Reset Password URL {}", resetPasswordUrl);

            sendEmail(user.getEmail(), resetPasswordUrl, request.getLocale());
        }

        modelMap.addAttribute(MAIL_SENT_KEY, "true");

        return EMAIL_ADDRESS_VIEW_NAME;
    }

    private void sendEmail(String email, String resetPasswordUrl, Locale locale) {
        String emailText = i18NService.getMessage(EMAIL_MESSAGE_TEXT_PROPERTY_NAME, locale);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("[Devopsbuddy]: How to Reset Your Password");
        message.setText(emailText + "\r\n" + resetPasswordUrl);
        message.setFrom(webMasterEmail);
        emailService.sendGenericEmailMessage(message);
    }
}