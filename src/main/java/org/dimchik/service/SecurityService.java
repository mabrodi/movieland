package org.dimchik.service;

import org.dimchik.common.request.LoginRequest;
import org.dimchik.dto.LoginDTO;
import org.dimchik.dto.AuthSessionDTO;

public interface SecurityService {
    LoginDTO login(LoginRequest request);

    void logout(String token);

    AuthSessionDTO findSessionByToken(String token);

    void cleanupExpiredSessions();
}
