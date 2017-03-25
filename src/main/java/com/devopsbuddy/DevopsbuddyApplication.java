package com.devopsbuddy;

import com.devopsbuddy.backend.persistence.domain.backend.Role;
import com.devopsbuddy.backend.persistence.domain.backend.User;
import com.devopsbuddy.backend.persistence.domain.backend.UserRole;
import com.devopsbuddy.backend.service.UserService;
import com.devopsbuddy.enums.PlansEnum;
import com.devopsbuddy.enums.RolesEnum;
import com.devopsbuddy.utils.UserUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@SpringBootApplication
public class DevopsbuddyApplication implements CommandLineRunner {

    /** The application logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(DevopsbuddyApplication.class);

    @Autowired
    private UserService userService;

    @Value("${webmaster.username}")
    private String webmasterUsername;
    @Value("${webmaster.password}")
    private String webmasterPassword;
    @Value("${webmaster.email}")
    private String webmasterEmail;

	public static void main(String[] args) throws ParseException {
        SpringApplication.run(DevopsbuddyApplication.class, args);
    }

    @Override
    public void run(String... strings) throws Exception {

        User user = UserUtils.createBasicUser(webmasterUsername, webmasterEmail);
        user.setPassword(webmasterPassword);
        Set<UserRole> userRoles = new HashSet<>();
        userRoles.add(new UserRole(user, new Role(RolesEnum.ADMIN)));
        LOGGER.debug("Creating user with username {}", user.getUsername());
        userService.createUser(user, PlansEnum.PRO, userRoles);
        LOGGER.info("User {} created", user.getUsername());
    }
}
