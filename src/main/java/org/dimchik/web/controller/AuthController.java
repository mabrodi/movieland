package org.dimchik.web.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.dimchik.web.response.TokenResponse;
import org.dimchik.service.AuthService;
import org.dimchik.web.request.LoginRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public TokenResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/refresh")
    public TokenResponse refresh(@RequestHeader("Authorization") String authorization) {
        return authService.refresh(authorization);
    }
}
