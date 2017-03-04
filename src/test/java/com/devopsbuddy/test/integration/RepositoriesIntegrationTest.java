package com.devopsbuddy.test.integration;

import com.devopsbuddy.backend.persistence.domain.backend.Plan;
import com.devopsbuddy.backend.persistence.domain.backend.Role;
import com.devopsbuddy.backend.persistence.domain.backend.User;
import com.devopsbuddy.backend.persistence.domain.backend.UserRole;
import com.devopsbuddy.backend.persistence.repositories.PlanRepository;
import com.devopsbuddy.backend.persistence.repositories.RoleRepository;
import com.devopsbuddy.backend.persistence.repositories.UserRepository;
import com.devopsbuddy.enums.PlansEnum;
import com.devopsbuddy.enums.RolesEnum;
import com.devopsbuddy.utils.UsersUtils;
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
        Plan basicPlan = createPlan(PlansEnum.BASIC);
        planRepository.save(basicPlan);
        Plan retrievedPlan = planRepository.findOne(PlansEnum.BASIC.getId());
        assertNotNull(retrievedPlan);
    }

    @Test
    public void testCreateNewRole() {
        Role basicRole = createRole(RolesEnum.BASIC);
        roleRepository.save(basicRole);
        Role retrievedRole = roleRepository.findOne(RolesEnum.BASIC.getId());
        assertNotNull(retrievedRole);
    }

    @Test
    public void testCreateNewUser() {
        Plan plan = createPlan(PlansEnum.BASIC);
        planRepository.save(plan);

        User user = UsersUtils.createBasicUser();
        user.setPlan(plan);

        Role role = createRole(RolesEnum.BASIC);
        Set<UserRole> roles = new HashSet<>();
        UserRole userRole = new UserRole(user, role);
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

    private Role createRole(RolesEnum rolesEnum) {
        return new Role(rolesEnum);
    }

    private Plan createPlan(PlansEnum plansEnum) {
        return new Plan(plansEnum);
    }

}
