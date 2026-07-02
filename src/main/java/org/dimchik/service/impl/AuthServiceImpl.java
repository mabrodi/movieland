package org.dimchik.service.impl;

import lombok.RequiredArgsConstructor;
import org.dimchik.security.TokenBlackListService;
import org.dimchik.utils.BearerTokenUtils;
import org.dimchik.web.response.TokenResponse;
import org.dimchik.dto.UserTokenDTO;
import org.dimchik.entity.User;
import org.dimchik.repository.UserRepository;
import org.dimchik.security.JwtService;
import org.dimchik.service.AuthService;
import org.dimchik.web.request.LoginRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        UserTokenDTO dto = UserTokenDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .role(user.getRole())
                .build();

        String token = jwtService.generateToken(dto);

        return new TokenResponse(token, "Bearer");
    }

    @Override
    public TokenResponse refresh(String authorization) {
        String oldToken = BearerTokenUtils.extractToken(authorization);
        if (tokenBlackListService.contains(oldToken) && !jwtService.isValid(oldToken)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token expired or invalid");
        }

        String newToken = jwtService.refreshToken(oldToken);
        tokenBlackListService.add(oldToken);

        return new TokenResponse(newToken, "Bearer");
    }

    @Override
    public void logout(String authorization) {
        tokenBlackListService.add(BearerTokenUtils.extractToken(authorization));
    }

}
