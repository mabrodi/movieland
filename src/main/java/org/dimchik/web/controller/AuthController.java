package org.dimchik.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.dimchik.dto.response.TokenResponse;
import org.dimchik.security.PublicEndpoint;
import org.dimchik.service.AuthService;
import org.dimchik.dto.request.LoginRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name="${swagger.auth.tag.name}", description = "${swagger.auth.tag.description}")
public class AuthController {
    private final AuthService authService;

    @PublicEndpoint
    @Operation(summary = "${swagger.auth.login.summary}", description = "${swagger.auth.login.description}")
    @PostMapping("/login")
    public TokenResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @Operation(summary = "${swagger.auth.refresh.summary}", description = "${swagger.auth.refresh.description}")
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/refresh")
    public TokenResponse refresh(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
        return authService.refresh(authorization);
    }

    @Operation(summary = "${swagger.auth.logout.summary}", description = "${swagger.auth.logout.description}")
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
        authService.logout(authorization);
    }
}
