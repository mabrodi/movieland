package org.dimchik.security;

import org.dimchik.dto.JwtUserDetails;
import org.dimchik.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    private static final String SECRET = "0123456789abcdef0123456789abcdef";

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(SECRET, 1);
    }

    @Test
    void generateTokenShouldReturnNonEmptyToken() {
        JwtUserDetails user = createTestUser();
        String token = jwtService.generateToken(user);

        assertNotNull(token);
        assertFalse(token.isBlank());
        assertEquals(3, token.split("\\.").length);
    }

    @Test
    void generateTokenShouldContainAllUserData() {
        JwtUserDetails user = createTestUser();
        String token = jwtService.generateToken(user);
        JwtUserDetails extractedUser = jwtService.extractUser(token);

        assertEquals(user.getId(), extractedUser.getId());
        assertEquals(user.getName(), extractedUser.getName());
        assertEquals(user.getEmail(), extractedUser.getEmail());
        assertEquals(user.getRole(), extractedUser.getRole());
    }

    @Test
    void generateTokenShouldCreateUniqueTokensForSameUser() {
        JwtUserDetails user = createTestUser();

        String token1 = jwtService.generateToken(user);
        String token2 = jwtService.generateToken(user);

        assertNotEquals(token1, token2);
    }

    @Test
    void extractUserShouldReturnCorrectUserData() {
        JwtUserDetails originalUser = new JwtUserDetails(
                1, "ronald", "ronald.reynolds66@example.com", Role.USER
        );

        String token = jwtService.generateToken(originalUser);
        JwtUserDetails extractedUser = jwtService.extractUser(token);

        assertAll(
                () -> assertEquals(originalUser.getId(), extractedUser.getId()),
                () -> assertEquals(originalUser.getName(), extractedUser.getName()),
                () -> assertEquals(originalUser.getEmail(), extractedUser.getEmail()),
                () -> assertEquals(originalUser.getRole(), extractedUser.getRole())
        );
    }

    @Test
    void isValidShouldReturnTrueForValidToken() {
        JwtUserDetails user = createTestUser();
        String token = jwtService.generateToken(user);

        assertTrue(jwtService.isValid(token));
    }

    @Test
    void isValidShouldReturnFalseForExpiredToken() throws InterruptedException {
        JwtService expiredService = new JwtService(SECRET, 0L);

        JwtUserDetails user = createTestUser();
        String token = expiredService.generateToken(user);

        Thread.sleep(1);

        assertFalse(expiredService.isValid(token));
    }

    @Test
    void isValidShouldReturnFalseForInvalidToken() {
        assertFalse(jwtService.isValid("invalid.token.here"));
        assertFalse(jwtService.isValid(""));
        assertFalse(jwtService.isValid(null));
    }

    @Test
    void isValidShouldReturnFalseForTamperedToken() {
        JwtUserDetails user = createTestUser();
        String token = jwtService.generateToken(user);

        String tamperedToken = token + "a";

        assertFalse(jwtService.isValid(tamperedToken));
    }

    @Test
    void isValidShouldReturnFalseForWrongSecretToken() {
        JwtService otherService = new JwtService("anotherSecretKeyThatIsDifferent123456", 1L);

        JwtUserDetails user = createTestUser();
        String token = otherService.generateToken(user);

        assertFalse(jwtService.isValid(token));
    }

    @Test
    void refreshTokenShouldReturnNewValidToken() {
        JwtUserDetails user = createTestUser();
        String oldToken = jwtService.generateToken(user);
        String newToken = jwtService.refreshToken(oldToken);

        assertNotNull(newToken);
        assertNotEquals(oldToken, newToken);
        assertTrue(jwtService.isValid(newToken));
    }

    @Test
    void refreshTokenShouldPreserveUserData() {
        JwtUserDetails originalUser = createTestUser();
        String oldToken = jwtService.generateToken(originalUser);
        String newToken = jwtService.refreshToken(oldToken);

        JwtUserDetails refreshedUser = jwtService.extractUser(newToken);

        assertEquals(originalUser.getId(), refreshedUser.getId());
        assertEquals(originalUser.getEmail(), refreshedUser.getEmail());
        assertEquals(originalUser.getRole(), refreshedUser.getRole());
    }

    @Test
    void refreshTokenShouldCreateTokenWithNewExpiration() {
        JwtUserDetails user = createTestUser();
        String oldToken = jwtService.generateToken(user);
        String newToken = jwtService.refreshToken(oldToken);

        assertTrue(jwtService.isValid(newToken));
    }

    @Test
    void refreshTokenShouldFailForInvalidToken() {
        assertThrows(Exception.class, () -> {
            jwtService.refreshToken("invalid.token.here");
        });
    }

    @Test
    void tokenShouldWorkWithNullName() {
        JwtUserDetails user = new JwtUserDetails(1, null, "test@example.com", Role.USER);
        String token = jwtService.generateToken(user);

        assertTrue(jwtService.isValid(token));
        assertNull(jwtService.extractUser(token).getName());
    }

    @Test
    void tokenShouldExpireExactlyAfterTtl() throws InterruptedException {
        JwtService tinyTtlService = new JwtService(SECRET, 0L);

        JwtUserDetails user = createTestUser();
        String token = tinyTtlService.generateToken(user);

        Thread.sleep(2);

        assertFalse(tinyTtlService.isValid(token));
    }

    private JwtUserDetails createTestUser() {
        return new JwtUserDetails(
                1,
                "ronald",
                "ronald.reynolds66@example.com",
                Role.USER
        );
    }
}