package org.dimchik.service.base;

import lombok.RequiredArgsConstructor;
import org.dimchik.dto.LoginDTO;
import org.dimchik.security.JwtService;
import org.dimchik.security.TokenBlacklistService;
import org.dimchik.service.AuthService;
import org.dimchik.web.request.LoginRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class AuthServiceBase implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final TokenBlacklistService tokenBlacklistService;

    @Override
    public LoginDTO login(LoginRequest request) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            UserDetails user = (UserDetails) auth.getPrincipal();
            String token = jwtService.generate(user);

            return new LoginDTO(token, "Bearer");

        } catch (BadCredentialsException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }
    }

    @Override
    public void logout(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing Bearer token");
        }

        String token = authorization.substring(7);

        Date exp = jwtService.extractExpiration(token);
        tokenBlacklistService.blacklist(token, exp);
    }
}
