package org.dimchik.service;

import org.dimchik.web.response.TokenResponse;
import org.dimchik.web.request.LoginRequest;

public interface AuthService {
    TokenResponse login(LoginRequest request);

    TokenResponse refresh(String authorization);

    void logout(String authorization);
}
