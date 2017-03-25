package com.devopsbuddy.test.integration;

import com.devopsbuddy.backend.persistence.domain.backend.*;
import com.devopsbuddy.backend.persistence.repositories.PasswordResetTokenRepository;
import com.devopsbuddy.backend.persistence.repositories.PlanRepository;
import com.devopsbuddy.backend.persistence.repositories.RoleRepository;
import com.devopsbuddy.backend.persistence.repositories.UserRepository;
import com.devopsbuddy.enums.PlansEnum;
import com.devopsbuddy.enums.RolesEnum;
import com.devopsbuddy.utils.UserUtils;
import org.junit.rules.TestName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertNotNull;

/**
 * Created by madgergely on 2017.03.25..
 */
public abstract class AbstractIntegrationTest {

    @Value("${token.expiration.length.minutes}")
    protected int expirationTimeInMinutes;

    @Autowired
    protected PlanRepository planRepository;

    @Autowired
    protected RoleRepository roleRepository;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected PasswordResetTokenRepository passwordResetTokenRepository;

    protected Role createRole(RolesEnum rolesEnum) {
        return new Role(rolesEnum);
    }

    protected Plan createPlan(PlansEnum plansEnum) {
        return new Plan(plansEnum);
    }

    protected User createUser(String username, String email) {
        Plan basicPlan = createPlan(PlansEnum.BASIC);
        planRepository.save(basicPlan);

        User basicUser = UserUtils.createBasicUser(username, email);
        basicUser.setPlan(basicPlan);

        Role basicRole = createRole(RolesEnum.BASIC);
        roleRepository.save(basicRole);

        Set<UserRole> userRoles = new HashSet<>();
        UserRole userRole = new UserRole(basicUser, basicRole);
        userRoles.add(userRole);

        basicUser.getUserRoles().addAll(userRoles);
        basicUser = userRepository.save(basicUser);
        return basicUser;
    }

    protected User createUser(TestName testName) {
        return createUser(testName.getMethodName(), testName.getMethodName() + "@devopsbuddy.com");
    }

    protected PasswordResetToken createPasswordResetToken(String token, User user, LocalDateTime localDateTime) {
        PasswordResetToken passwordResetToken = new PasswordResetToken(token, user, localDateTime, expirationTimeInMinutes);
        passwordResetTokenRepository.save(passwordResetToken);
        assertNotNull(passwordResetToken.getId());
        return passwordResetToken;
    }
}
