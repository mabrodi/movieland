package org.dimchik.service.impl;

import lombok.RequiredArgsConstructor;
import org.dimchik.security.TokenBlackListService;
import org.dimchik.utils.BearerTokenUtils;
import org.dimchik.dto.response.TokenResponse;
import org.dimchik.dto.JwtUserDetails;
import org.dimchik.entity.User;
import org.dimchik.repository.UserRepository;
import org.dimchik.security.JwtService;
import org.dimchik.service.AuthService;
import org.dimchik.exception.InvalidCredentialsException;
import org.dimchik.exception.TokenInvalidException;
import org.dimchik.dto.request.LoginRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final TokenBlackListService tokenBlackListService;

    @Override
    public TokenResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid credentials");
        }

        JwtUserDetails dto = JwtUserDetails.builder()
                .id(user.getId())
                .email(user.getEmail())
                .role(user.getRole())
                .build();

        String token = jwtService.generateToken(dto);

        return new TokenResponse("Bearer " + token);
    }

    @Override
    public TokenResponse refresh(String authorization) {
        String oldToken = BearerTokenUtils.extractToken(authorization);
        if (tokenBlackListService.contains(oldToken) || !jwtService.isValid(oldToken)) {
            throw new TokenInvalidException("Token expired or invalid");
        }

        String newToken = jwtService.refreshToken(oldToken);
        tokenBlackListService.add(oldToken);

        return new TokenResponse("Bearer " + newToken);
    }

    @Override
    public void logout(String authorization) {
        tokenBlackListService.add(BearerTokenUtils.extractToken(authorization));
    }

}
