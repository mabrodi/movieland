package org.dimchik.service.impl;

import org.dimchik.dto.TokenUserDTO;
import org.dimchik.entity.User;
import org.dimchik.enums.Role;
import org.dimchik.repository.UserRepository;
import org.dimchik.web.response.TokenResponse;
import org.dimchik.security.JwtService;
import org.dimchik.web.request.LoginRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthServiceImpl authService;

    private User testUser;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");
        testUser.setPassword("encodedPassword");
        testUser.setRole(Role.USER);

        loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");
    }

    @Test
    void loginShouldReturnTokenResponseWhenCredentialsValid() {
        when(userRepository.findByEmail(loginRequest.getEmail()))
                .thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(loginRequest.getPassword(), testUser.getPassword()))
                .thenReturn(true);
        when(jwtService.generateToken(any(TokenUserDTO.class)))
                .thenReturn("test.jwt.token");

        TokenResponse response = authService.login(loginRequest);

        assertNotNull(response);
        assertEquals("test.jwt.token", response.getToken());
        assertEquals("Bearer", response.getTokenType());

        verify(userRepository).findByEmail(loginRequest.getEmail());
        verify(passwordEncoder).matches(loginRequest.getPassword(), testUser.getPassword());
        verify(jwtService).generateToken(any(TokenUserDTO.class));
    }

    @Test
    void loginShouldThrowExceptionWhenUserNotFound() {
        when(userRepository.findByEmail(loginRequest.getEmail()))
                .thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> authService.login(loginRequest)
        );

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
        assertEquals("Invalid credentials", exception.getReason());

        verify(userRepository).findByEmail(loginRequest.getEmail());
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtService, never()).generateToken(any());
    }

    @Test
    void loginShouldThrowExceptionWhenPasswordInvalid() {
        when(userRepository.findByEmail(loginRequest.getEmail()))
                .thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(loginRequest.getPassword(), testUser.getPassword()))
                .thenReturn(false);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> authService.login(loginRequest)
        );

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
        assertEquals("Invalid credentials", exception.getReason());

        verify(userRepository).findByEmail(loginRequest.getEmail());
        verify(passwordEncoder).matches(loginRequest.getPassword(), testUser.getPassword());
        verify(jwtService, never()).generateToken(any());
    }

    @Test
    void refreshShouldReturnNewTokenWhenOldTokenValid() {
        String oldToken = "Bearer old.jwt.token";
        when(jwtService.isValid("old.jwt.token")).thenReturn(true);
        when(jwtService.refreshToken("old.jwt.token")).thenReturn("new.jwt.token");

        TokenResponse response = authService.refresh(oldToken);

        assertNotNull(response);
        assertEquals("new.jwt.token", response.getToken());
        assertEquals("Bearer", response.getTokenType());

        verify(jwtService).isValid("old.jwt.token");
        verify(jwtService).refreshToken("old.jwt.token");
    }

    @Test
    void refreshShouldThrowExceptionWhenTokenInvalid() {
        String invalidToken = "Bearer invalid.token";
        when(jwtService.isValid("invalid.token")).thenReturn(false);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> authService.refresh(invalidToken)
        );

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
        assertEquals("Token expired or invalid", exception.getReason());

        verify(jwtService).isValid("invalid.token");
        verify(jwtService, never()).refreshToken(anyString());
    }

    @Test
    void refreshShouldThrowExceptionWhenAuthorizationHeaderInvalid() {
        assertThrows(ResponseStatusException.class, () -> {
            authService.refresh("InvalidHeader");
        });

        assertThrows(ResponseStatusException.class, () -> {
            authService.refresh(null);
        });
    }
}