package com.devopsbuddy.web.controllers;

import com.devopsbuddy.backend.persistence.domain.backend.PasswordResetToken;
import com.devopsbuddy.backend.persistence.domain.backend.User;
import com.devopsbuddy.backend.service.EmailService;
import com.devopsbuddy.backend.service.I18NService;
import com.devopsbuddy.backend.service.PasswordResetTokenService;
import com.devopsbuddy.backend.service.UserService;
import com.devopsbuddy.utils.UserUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.time.Clock;
import java.time.LocalDateTime;
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
    private static final String CHANGE_PASSWORD_VIEW_NAME = "forgotpassword/changePassword";
    private static final String EMAIL_MESSAGE_TEXT_PROPERTY_NAME = "forgotmypassword.email.text";
    private static final String PASSWORD_RESET_ATTRIBUTE_NAME = "passwordReset";
    private static final String MESSAGE_ATTRIBUTE_NAME = "message";

    @Autowired
    private PasswordResetTokenService passwordResetTokenService;

    @Autowired
    private I18NService i18NService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserService userService;

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

    @RequestMapping(value = CHANGE_PASSWORD_PATH, method = RequestMethod.GET)
    public String changePasswordGet(@RequestParam("id") long id, @RequestParam("token") String token, Locale locale, ModelMap model) {
        if (StringUtils.isEmpty(token) || id == 0) {
            LOGGER.error("Invalid user id {} or token value {}", id, token);
            addModelAttributes(model, false, "Invalid user id or token value");
            return CHANGE_PASSWORD_VIEW_NAME;
        }

        PasswordResetToken passwordResetToken = passwordResetTokenService.findByToken(token);

        if (null == passwordResetToken) {
            LOGGER.warn("A token couldn't be found with value {}", token);
            addModelAttributes(model, false, "Token not found");
            return CHANGE_PASSWORD_VIEW_NAME;
        }

        User user = passwordResetToken.getUser();
        if (user.getId() != id) {
            LOGGER.error("The user id {} passed as parameter does not match the user id {} associated with the token {}", id, user.getId(), token);
            addModelAttributes(model, false, i18NService.getMessage("resetPassword.token.invalid", locale));
            return CHANGE_PASSWORD_VIEW_NAME;
        }

        if (LocalDateTime.now(Clock.systemUTC()).isAfter(passwordResetToken.getExpireDate())) {
            LOGGER.error("The token {} has expired", token);
            addModelAttributes(model, false, i18NService.getMessage("resetPassword.token.expired", locale));
            return CHANGE_PASSWORD_VIEW_NAME;
        }

        model.addAttribute(PASSWORD_RESET_ATTRIBUTE_NAME, "true");
        model.addAttribute("principalId", user.getId());

        Authentication auth = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
        return CHANGE_PASSWORD_VIEW_NAME;
    }

    @RequestMapping(value = CHANGE_PASSWORD_PATH, method = RequestMethod.POST)
    public String changeUserPasswordPost(@RequestParam(value = "principal_id", defaultValue = "0") long userId, @RequestParam("password") String password, ModelMap model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (null == authentication) {
            LOGGER.error("An unauthenticated user tried to invoke the reset password POST method");
            addModelAttributes(model, false, "You are not authorized to perform this request.");
            return CHANGE_PASSWORD_VIEW_NAME;
        }

        User user = (User) authentication.getPrincipal();
        if (user.getId() != userId) {
            LOGGER.error("Security breach! User {} is trying to make a password reset request on behalf of {}", user.getId(), userId);
            addModelAttributes(model, false, "You are not authorized to perform this request.");
            return CHANGE_PASSWORD_VIEW_NAME;
        }

        userService.updateUserPassword(userId, password);
        LOGGER.info("Password successfully updated for user {}", user.getUsername());

        model.addAttribute(PASSWORD_RESET_ATTRIBUTE_NAME, "true");
        return CHANGE_PASSWORD_VIEW_NAME;
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

    private void addModelAttributes(ModelMap model, boolean flag, String message) {
        model.addAttribute(PASSWORD_RESET_ATTRIBUTE_NAME, String.valueOf(flag));
        model.addAttribute(MESSAGE_ATTRIBUTE_NAME, message);
    }
}
