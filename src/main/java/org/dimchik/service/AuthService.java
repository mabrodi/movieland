package org.dimchik.service;

import org.dimchik.dto.LoginDTO;
import org.dimchik.web.request.LoginRequest;

public interface AuthService {
    LoginDTO login(LoginRequest request);

    void logout(String authorization);
}
