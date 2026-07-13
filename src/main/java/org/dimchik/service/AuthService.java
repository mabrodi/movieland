package org.dimchik.service;

import org.dimchik.dto.response.TokenResponse;
import org.dimchik.dto.request.LoginRequest;

public interface AuthService {
    TokenResponse login(LoginRequest request);

    TokenResponse refresh(String authorization);

    void logout(String authorization);
}
