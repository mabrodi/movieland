package org.dimchik.service.impl;

import org.dimchik.dto.UserToken;
import org.dimchik.entity.User;
import org.dimchik.enums.Role;
import org.dimchik.repository.UserRepository;
import org.dimchik.dto.response.TokenResponse;
import org.dimchik.security.JwtService;
import org.dimchik.security.TokenBlackListService;
import org.dimchik.exception.InvalidCredentialsException;
import org.dimchik.exception.TokenInvalidException;
import org.dimchik.dto.request.LoginRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
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

    @Mock
    private TokenBlackListService tokenBlackListService;

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
        when(jwtService.generateToken(any(UserToken.class)))
                .thenReturn("test.jwt.token");

        TokenResponse response = authService.login(loginRequest);

        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("test.jwt.token");
        assertThat(response.getTokenType()).isEqualTo("Bearer");

        ArgumentCaptor<UserToken> captor = ArgumentCaptor.forClass(UserToken.class);
        verify(jwtService).generateToken(captor.capture());
        UserToken tokenPayload = captor.getValue();
        assertThat(tokenPayload.getId()).isEqualTo(1L);
        assertThat(tokenPayload.getEmail()).isEqualTo("test@example.com");
        assertThat(tokenPayload.getRole()).isEqualTo(Role.USER);
    }

    @Test
    void loginShouldThrowExceptionWhenUserNotFound() {
        when(userRepository.findByEmail(loginRequest.getEmail()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessageContaining("Invalid credentials");

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

        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessageContaining("Invalid credentials");

        verify(userRepository).findByEmail(loginRequest.getEmail());
        verify(passwordEncoder).matches(loginRequest.getPassword(), testUser.getPassword());
        verify(jwtService, never()).generateToken(any());
    }

    @Test
    void refreshShouldReturnNewTokenWhenOldTokenValid() {
        String oldToken = "Bearer old.jwt.token";
        when(jwtService.isValid("old.jwt.token")).thenReturn(true);
        when(tokenBlackListService.contains("old.jwt.token")).thenReturn(false);
        when(jwtService.refreshToken("old.jwt.token")).thenReturn("new.jwt.token");

        TokenResponse response = authService.refresh(oldToken);

        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("new.jwt.token");
        assertThat(response.getTokenType()).isEqualTo("Bearer");

        verify(jwtService).isValid("old.jwt.token");
        verify(tokenBlackListService).contains("old.jwt.token");
        verify(jwtService).refreshToken("old.jwt.token");
        verify(tokenBlackListService).add("old.jwt.token");
    }

    @Test
    void refreshShouldThrowExceptionWhenTokenInvalid() {
        String invalidToken = "Bearer invalid.token";
        when(tokenBlackListService.contains("invalid.token")).thenReturn(false);
        when(jwtService.isValid("invalid.token")).thenReturn(false);

        assertThatThrownBy(() -> authService.refresh(invalidToken))
                .isInstanceOf(TokenInvalidException.class)
                .hasMessageContaining("Token expired or invalid");

        verify(jwtService).isValid("invalid.token");
        verify(jwtService, never()).refreshToken(anyString());
    }

    @Test
    void refreshShouldThrowExceptionWhenTokenBlacklisted() {
        String blacklistedToken = "Bearer blacklisted.token";
        when(tokenBlackListService.contains("blacklisted.token")).thenReturn(true);

        assertThatThrownBy(() -> authService.refresh(blacklistedToken))
                .isInstanceOf(TokenInvalidException.class)
                .hasMessageContaining("Token expired or invalid");

        verify(tokenBlackListService).contains("blacklisted.token");
        verify(jwtService, never()).isValid(anyString());
        verify(jwtService, never()).refreshToken(anyString());
    }

    @Test
    void refreshShouldThrowExceptionWhenAuthorizationHeaderInvalid() {
        assertThatThrownBy(() -> authService.refresh("InvalidHeader"))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> authService.refresh(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void logoutShouldAddTokenToBlacklist() {
        String authorization = "Bearer some.token";

        authService.logout(authorization);

        verify(tokenBlackListService).add("some.token");
    }
}
