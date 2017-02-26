package com.devopsbuddy.test.integration;

import com.devopsbuddy.backend.persistence.domain.backend.Plan;
import com.devopsbuddy.backend.persistence.domain.backend.Role;
import com.devopsbuddy.backend.persistence.domain.backend.User;
import com.devopsbuddy.backend.persistence.domain.backend.UserRole;
import com.devopsbuddy.backend.persistence.repositories.PlanRepository;
import com.devopsbuddy.backend.persistence.repositories.RoleRepository;
import com.devopsbuddy.backend.persistence.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by madgergely on 2017.02.26..
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class RepositoriesIntegrationTest {

    private static final int BASIC_PLAN_ID = 1;
    private static final int BASIC_ROLE_ID = 1;

    @Autowired
    private PlanRepository planRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Before
    public void init() {
        assertNotNull(planRepository);
        assertNotNull(roleRepository);
        assertNotNull(userRepository);
    }

    @Test
    public void testCreateNewPlan() {
        Plan basicPlan = createBasicPlan();
        planRepository.save(basicPlan);
        Plan retrievedPlan = planRepository.findOne(BASIC_PLAN_ID);
        assertNotNull(retrievedPlan);
    }

    @Test
    public void testCreateNewRole() {
        Role basicRole = createBasicRole();
        roleRepository.save(basicRole);
        Role retrievedRole = roleRepository.findOne(BASIC_ROLE_ID);
        assertNotNull(retrievedRole);
    }

    @Test
    public void testCreateNewUser() {
        Plan plan = createBasicPlan();
        planRepository.save(plan);

        User user = createUser();
        user.setPlan(plan);

        Role role = createBasicRole();
        Set<UserRole> roles = new HashSet<>();
        UserRole userRole = new UserRole();
        userRole.setUser(user);
        userRole.setRole(role);
        roles.add(userRole);

        user.getUserRoles().addAll(roles);

        for (UserRole ur: roles) {
            roleRepository.save(ur.getRole());
        }

        user = userRepository.save(user);
        User retrievedUser = userRepository.findOne(user.getId());
        assertNotNull(retrievedUser);
        assertTrue(retrievedUser.getId() != 0);
        assertNotNull(retrievedUser.getPlan());
        assertNotNull(retrievedUser.getPlan().getId());
        roles = retrievedUser.getUserRoles();
        for (UserRole ur: roles) {
            assertNotNull(ur.getRole());
            assertNotNull(ur.getRole().getId());
        }
    }

    private Plan createBasicPlan() {
        Plan plan = new Plan();
        plan.setId(BASIC_PLAN_ID);
        plan.setName("basic");
        return plan;
    }

    private Role createBasicRole() {
        Role role = new Role();
        role.setId(BASIC_ROLE_ID);
        role.setName("ROLE_USER");
        return role;
    }

    private User createUser() {
        return new User();
    }
}
