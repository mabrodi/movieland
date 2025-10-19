package org.dimchik.service.base;

import org.dimchik.common.enums.Role;
import org.dimchik.common.request.LoginRequest;
import org.dimchik.dto.AuthSessionDTO;
import org.dimchik.dto.LoginDTO;
import org.dimchik.dto.UserAuthDTO;
import org.dimchik.service.SecurityService;
import org.dimchik.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SecurityServiceBaseTest {
    private SecurityService securityService;

    @Mock
    private UserService userService;

    @BeforeEach
    void setUp() {
        securityService = new SecurityServiceBase(userService);
    }

    @Test
    void loginShouldReturnTokenAndStoreSession() {
        String email = "john@example.com";
        String password = "securePassword";

        UserAuthDTO userAuthDto = new UserAuthDTO(1L,"test", email, Role.USER);

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(email);
        loginRequest.setPassword(password);

        when(userService.authenticate(email, password)).thenReturn(userAuthDto);
        LoginDTO loginDto = securityService.login(loginRequest);

        AuthSessionDTO authSessionDTO = securityService.findSessionByToken(loginDto.getUuid());
        assertNotNull(authSessionDTO);
    }

    @Test
    void logoutShouldRemoveSession() {
        String email = "john@example.com";
        String password = "securePassword";
        UserAuthDTO userAuthDto = new UserAuthDTO(1L,"test", email, Role.USER);

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(email);
        loginRequest.setPassword(password);

        when(userService.authenticate(email, password)).thenReturn(userAuthDto);
        LoginDTO loginDto = securityService.login(loginRequest);

        AuthSessionDTO authSessionDTO = securityService.findSessionByToken(loginDto.getUuid());
        assertNotNull(authSessionDTO);

        securityService.logout(authSessionDTO.getToken());
        assertNull(securityService.findSessionByToken(authSessionDTO.getToken()));
    }

    @Test
    void findSessionByTokenShouldReturnNullIfTokenNotFound() {
        assertNull(securityService.findSessionByToken("non-existent-token"));
    }

    @Test
    void cleanupExpiredSessionsShouldRemoveOnlyOldSessions() throws Exception {
        UserAuthDTO user = new UserAuthDTO(1L, "john@example.com", "John", Role.USER);

        AuthSessionDTO recentSession = new AuthSessionDTO(
                "recent", user, LocalDateTime.now().minusMinutes(10)
        );
        AuthSessionDTO oldSession = new AuthSessionDTO(
                "old", user, LocalDateTime.now().minusHours(3)
        );

        //ttl
        Field ttlField = SecurityServiceBase.class.getDeclaredField("sessionTtl");
        ttlField.setAccessible(true);
        ttlField.set(securityService, Duration.ofHours(2));

        //sessions list
        Field sessionsField = SecurityServiceBase.class.getDeclaredField("sessions");
        sessionsField.setAccessible(true);
        List<AuthSessionDTO> sessions = (List<AuthSessionDTO>) sessionsField.get(securityService);
        sessions.add(recentSession);
        sessions.add(oldSession);

        securityService.cleanupExpiredSessions();

        assertNotNull(securityService.findSessionByToken("recent"));
        assertNull(securityService.findSessionByToken("old"));
    }

    @Test
    void loginShouldThrowExceptionWhenAuthenticationFails() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("wrong@example.com");
        loginRequest.setPassword("bad");

        doThrow(new IllegalArgumentException("Invalid credentials"))
                .when(userService).authenticate("wrong@example.com", "bad");

        assertThrows(IllegalArgumentException.class, () -> securityService.login(loginRequest));
    }
}