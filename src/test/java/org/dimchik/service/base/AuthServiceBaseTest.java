package org.dimchik.service.base;

import org.dimchik.dto.LoginDTO;
import org.dimchik.security.JwtService;
import org.dimchik.security.TokenBlacklistService;
import org.dimchik.web.request.LoginRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceBaseTest {

    @Mock private AuthenticationManager authenticationManager;
    @Mock private JwtService jwtService;
    @Mock private TokenBlacklistService tokenBlacklistService;

    @InjectMocks
    private AuthServiceBase authService;

    @Test
    void loginShouldAuthenticateGenerateJwtAndReturnDto() {
        LoginRequest request = new LoginRequest();
        request.setEmail("u@example.com");
        request.setPassword("pass");

        UserDetails principal = new User(
                "u@example.com",
                "ignored",
                List.of(new SimpleGrantedAuthority("USER"))
        );

        Authentication authentication = mock(Authentication.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(principal);
        when(jwtService.generate(principal)).thenReturn("jwt-token");

        LoginDTO result = authService.login(request);

        assertThat(result).isNotNull();
        assertThat(result.getUuid()).isEqualTo("jwt-token");
        assertThat(result.getNickname()).isEqualTo("Bearer");

        ArgumentCaptor<UsernamePasswordAuthenticationToken> captor =
                ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);
        verify(authenticationManager).authenticate(captor.capture());

        UsernamePasswordAuthenticationToken passed = captor.getValue();
        assertThat(passed.getPrincipal()).isEqualTo("u@example.com");
        assertThat(passed.getCredentials()).isEqualTo("pass");

        verify(jwtService).generate(principal);
        verifyNoMoreInteractions(authenticationManager, jwtService);
        verifyNoInteractions(tokenBlacklistService);
    }

    @Test
    void loginShouldThrow401WhenBadCredentials() {
        var request = new LoginRequest();
        request.setEmail("u@example.com");
        request.setPassword("wrong");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("bad"));

        Throwable thrown = catchThrowable(() -> authService.login(request));

        assertThat(thrown)
                .isInstanceOf(ResponseStatusException.class);

        ResponseStatusException ex = (ResponseStatusException) thrown;
        assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(ex.getReason()).contains("Invalid email or password");

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verifyNoInteractions(jwtService, tokenBlacklistService);
    }

    @Test
    void logoutShouldBlacklistTokenWithExpirationFromJwtService() {
        String token = "abc.def.ghi";
        String header = "Bearer " + token;

        Date exp = new Date(System.currentTimeMillis() + 60_000);

        when(jwtService.extractExpiration(token)).thenReturn(exp);

        authService.logout(header);

        verify(jwtService).extractExpiration(token);
        verify(tokenBlacklistService).blacklist(token, exp);
        verifyNoMoreInteractions(jwtService, tokenBlacklistService);
        verifyNoInteractions(authenticationManager);
    }

    @Test
    void logoutShouldThrow400WhenHeaderMissing() {
        Throwable thrown = catchThrowable(() -> authService.logout(null));

        assertThat(thrown).isInstanceOf(ResponseStatusException.class);
        ResponseStatusException ex = (ResponseStatusException) thrown;
        assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(ex.getReason()).contains("Missing Bearer token");

        verifyNoInteractions(jwtService, tokenBlacklistService, authenticationManager);
    }

    @Test
    void logoutShouldThrow400WhenNotBearer() {
        Throwable thrown = catchThrowable(() -> authService.logout("Basic 123"));

        assertThat(thrown).isInstanceOf(ResponseStatusException.class);
        ResponseStatusException ex = (ResponseStatusException) thrown;
        assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(ex.getReason()).contains("Missing Bearer token");

        verifyNoInteractions(jwtService, tokenBlacklistService, authenticationManager);
    }
}