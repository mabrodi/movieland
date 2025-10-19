package org.dimchik.web.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.dimchik.common.request.LoginRequest;
import org.dimchik.dto.LoginDTO;
import org.dimchik.service.SecurityService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/")
@RequiredArgsConstructor
public class AuthController {
    private final SecurityService securityService;

    @PostMapping("login")
    public LoginDTO login(@RequestBody @Valid LoginRequest request) {
        return securityService.login(request);
    }

    @DeleteMapping("logout")
    public void logout(@RequestHeader("Authorization") @Valid String token) {
        securityService.logout(token);
    }
}
