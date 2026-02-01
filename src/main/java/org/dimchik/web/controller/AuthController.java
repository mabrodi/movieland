package org.dimchik.web.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.dimchik.dto.LoginDTO;
import org.dimchik.service.AuthService;
import org.dimchik.web.request.LoginRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("login")
    public LoginDTO login(@RequestBody @Valid LoginRequest request) {
        return authService.login(request);
    }

    @DeleteMapping("logout")
    public void logout(@RequestHeader("Authorization") String authorization) {
        authService.logout(authorization);
    }
}
