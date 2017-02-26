package com.devopsbuddy.web.controllers;

import com.devopsbuddy.backend.service.EmailService;
import com.devopsbuddy.web.domain.frontend.Feedback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by madgergely on 2017.02.26..
 */
@Controller
public class ContactController {

    /** The application logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(ContactController.class);

    /** The key which identifies the feedback payload in the model. */
    private static final String FEEDBACK_MODEL_KEY = "feedback";

    /** The Contact Us view name. */
    private static final String CONTACT_US_VIEW_NAME = "contact/contact";

    @Autowired
    private EmailService emailService;

    @RequestMapping(value = "/contact", method = RequestMethod.GET)
    public String contactGet(ModelMap model) {
        Feedback feedback = new Feedback();
        model.addAttribute(FEEDBACK_MODEL_KEY, feedback);
        return CONTACT_US_VIEW_NAME;
    }

    @RequestMapping(value = "/contact", method = RequestMethod.POST)
    public String contactPost(@ModelAttribute(FEEDBACK_MODEL_KEY) Feedback feedback) {
        LOGGER.debug("Feedback POJO content: {}", feedback);
        emailService.sendFeedbackEmail(feedback);
        return CONTACT_US_VIEW_NAME;
    }
}
