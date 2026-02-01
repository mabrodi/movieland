package org.dimchik.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    private static final String SECRET = "0123456789abcdef0123456789abcdef";

    @BeforeEach
    void setUp() throws Exception {
        jwtService = new JwtService();
        setField(jwtService, "secret", SECRET);
        setField(jwtService, "ttlMinutes", 1L);
    }

    @Test
    void generateShouldReturnTokenExtractUsernameShouldMatch() {
        User user = new User(
                "ronald.reynolds66@example.com",
                "ignored",
                List.of(new SimpleGrantedAuthority("ADMIN"))
        );

        String token = jwtService.generate(user);

        assertNotNull(token);
        assertFalse(token.isBlank());
        assertEquals(user.getUsername(), jwtService.extractUsername(token));
    }

    @Test
    void isValidShouldBeTrueForSameUserAndNotExpired() {
        User user = new User(
                "u@example.com",
                "ignored",
                List.of(new SimpleGrantedAuthority("USER"))
        );

        String token = jwtService.generate(user);

        assertTrue(jwtService.isValid(token, user));
    }

    @Test
    void isValidShouldBeFalseForDifferentUser() {
        User user1 = new User("a@example.com", "x", List.of(new SimpleGrantedAuthority("USER")));
        User user2 = new User("b@example.com", "x", List.of(new SimpleGrantedAuthority("USER")));

        String token = jwtService.generate(user1);

        assertTrue(jwtService.isValid(token, user1));
        assertFalse(jwtService.isValid(token, user2));
    }

    @Test
    void extractExpirationShouldBeInFutureForPositiveTtl() {
        User user = new User("u@example.com", "x", List.of(new SimpleGrantedAuthority("USER")));
        String token = jwtService.generate(user);

        Date exp = jwtService.extractExpiration(token);

        assertNotNull(exp);
        assertTrue(exp.after(new Date()));
    }

    @Test
    void isValidShouldBeFalseForExpiredToken() throws Exception {
        JwtService service = new JwtService();
        setField(service, "secret", SECRET);
        setField(service, "ttlMinutes", 0L);

        User user = new User("u@example.com", "x", List.of(new SimpleGrantedAuthority("USER")));
        String token = service.generate(user);

        Thread.sleep(5);

        assertFalse(service.isValid(token, user));
    }

    @Test
    void isValidShouldBeFalseForBrokenToken() {
        User user = new User("u@example.com", "x", List.of(new SimpleGrantedAuthority("USER")));
        assertFalse(jwtService.isValid("not-a-jwt", user));
    }

    @Test
    void extractUsernameShouldThrowForBrokenToken() {
        assertThrows(Exception.class, () -> jwtService.extractUsername("not-a-jwt"));
    }

    @Test
    void tokenShouldNotValidateIfSecretIsDifferent() throws Exception {
        User user = new User("u@example.com", "x", List.of(new SimpleGrantedAuthority("USER")));
        String token = jwtService.generate(user);

        JwtService service2 = new JwtService();
        setField(service2, "secret", "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        setField(service2, "ttlMinutes", 1L);

        assertFalse(service2.isValid(token, user));
    }

    private static void setField(Object target, String fieldName, Object value) throws Exception {
        Field f = target.getClass().getDeclaredField(fieldName);
        f.setAccessible(true);
        f.set(target, value);
    }
}