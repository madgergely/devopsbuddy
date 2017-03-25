package com.devopsbuddy.test.integration;

import com.devopsbuddy.backend.persistence.domain.backend.PasswordResetToken;
import com.devopsbuddy.backend.persistence.domain.backend.User;
import com.devopsbuddy.backend.persistence.repositories.PasswordResetTokenRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * Created by madgergely on 2017.03.25..
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class PasswordResetTokenIntegrationTest extends AbstractIntegrationTest {

    @Value("${token.expiration.length.minutes}")
    private int expirationTimeInMinutes;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Rule
    public TestName testName = new TestName();

    @Before
    public void init() {
        assertFalse(expirationTimeInMinutes == 0);
    }

    @Test
    public void testTokenExpirationLength() {
        User user = createUser(testName);
        assertNotNull(user);
        assertNotNull(user.getId());

        LocalDateTime now = LocalDateTime.now(Clock.systemDefaultZone());
        String token = UUID.randomUUID().toString();

        LocalDateTime expectedTime = now.plusMinutes(expirationTimeInMinutes);

        PasswordResetToken passwordResetToken = createPasswordResetToken(token, user, now);

        LocalDateTime actualTime = passwordResetToken.getExpireDate();
        assertNotNull(actualTime);
        assertEquals(expectedTime, actualTime);
    }

    @Test
    public void testFindTokenByTokenValue() {
        User user = createUser(testName);
        String token = UUID.randomUUID().toString();
        LocalDateTime now = LocalDateTime.now(Clock.systemDefaultZone());

        createPasswordResetToken(token, user, now);

        PasswordResetToken retrievedPasswordResetToken = passwordResetTokenRepository.findByToken(token);
        assertNotNull(retrievedPasswordResetToken);
        assertNotNull(retrievedPasswordResetToken.getId());
        assertNotNull(retrievedPasswordResetToken.getUser());
    }

    @Test
    public void testDeleteToken() {
        User user = createUser(testName);
        String token = UUID.randomUUID().toString();
        LocalDateTime now = LocalDateTime.now(Clock.systemDefaultZone());

        PasswordResetToken passwordResetToken = createPasswordResetToken(token, user, now);
        long tokenId = passwordResetToken.getId();
        passwordResetTokenRepository.delete(tokenId);

        PasswordResetToken shouldNotExistToken = passwordResetTokenRepository.findOne(tokenId);
        assertNull(shouldNotExistToken);
    }

    @Test
    public void testCascadeDeleteFromUserEntity() {
        User user = createUser(testName);
        String token = UUID.randomUUID().toString();
        LocalDateTime now = LocalDateTime.now(Clock.systemDefaultZone());

        PasswordResetToken passwordResetToken = createPasswordResetToken(token, user, now);
        passwordResetToken.getId();

        userRepository.delete(user.getId());

        Set<PasswordResetToken> shouldBeEmpty = passwordResetTokenRepository.findAllByUserId(user.getId());
        assertTrue(shouldBeEmpty.isEmpty());
    }

    @Test
    public void testMultipleTokensAreReturnedWhenQueringByUserId() {
        User user = createUser(testName);
        LocalDateTime now = LocalDateTime.now(Clock.systemDefaultZone());

        String token1 = UUID.randomUUID().toString();
        String token2 = UUID.randomUUID().toString();
        String token3 = UUID.randomUUID().toString();

        Set<PasswordResetToken> tokens = new HashSet<>();
        tokens.add(createPasswordResetToken(token1, user, now));
        tokens.add(createPasswordResetToken(token2, user, now));
        tokens.add(createPasswordResetToken(token3, user, now));

        passwordResetTokenRepository.save(tokens);

        User foundUser = userRepository.findOne(user.getId());

        Set<PasswordResetToken> actualTokens = passwordResetTokenRepository.findAllByUserId(user.getId());
        assertTrue(actualTokens.size() == tokens.size());
        List<String> tokensAsList = tokens.stream().map(prt -> prt.getToken()).collect(Collectors.toList());
        List<String> actualTokensAsList = actualTokens.stream().map(prt -> prt.getToken()).collect(Collectors.toList());
        assertEquals(tokensAsList, actualTokensAsList);
    }

    private PasswordResetToken createPasswordResetToken(String token, User user, LocalDateTime localDateTime) {
        PasswordResetToken passwordResetToken = new PasswordResetToken(token, user, localDateTime, expirationTimeInMinutes);
        passwordResetTokenRepository.save(passwordResetToken);
        assertNotNull(passwordResetToken.getId());
        return passwordResetToken;
    }
}
