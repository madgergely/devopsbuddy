package com.devopsbuddy.utils;

import com.devopsbuddy.backend.persistence.domain.backend.User;
import com.devopsbuddy.web.controllers.ForgotPasswordController;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by madgergely on 2017.03.04..
 */
public class UserUtils {

    private UserUtils() {
        throw new AssertionError("Non instantiable");
    }

    /**
     * Creates a user with basic attributes set.
     * @param username The username
     * @param email The email
     * @return A User entity
     */
    public static User createBasicUser(String username, String email) {
        User user = new User();
        user.setUsername(username);
        user.setPassword("secret");
        user.setEmail(email);
        user.setFirstName("firstName");
        user.setLastName("lastName");
        user.setPhoneNumber("123456789");
        user.setCountry("GB");
        user.setEnabled(true);
        user.setDescription("A basic user");
        user.setProfileImageUrl("https://akarhol.com/basicuser");
        return user;
    }

    public static String createPasswordResetUrl(HttpServletRequest request, long userId, String token) {
        StringBuilder passwordResetUrl = new StringBuilder();
        passwordResetUrl.append(request.getScheme())
                .append("://")
                .append(request.getServerName())
                .append(":")
                .append(request.getServerPort())
                .append(request.getContextPath())
                .append(ForgotPasswordController.CHANGE_PASSWORD_PATH)
                .append("?id=")
                .append(userId)
                .append("&token=")
                .append(token);

        return passwordResetUrl.toString();
    }
}
