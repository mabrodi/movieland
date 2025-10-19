package org.dimchik.service.base;

import org.dimchik.common.request.LoginRequest;
import org.dimchik.dto.LoginDTO;
import org.dimchik.dto.AuthSessionDTO;
import org.dimchik.dto.UserAuthDTO;
import org.dimchik.service.SecurityService;
import org.dimchik.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

@Service
public class SecurityServiceBase implements SecurityService {
    private final List<AuthSessionDTO> sessions;
    private final UserService userService;

    @Value("${session.ttl}")
    private Duration sessionTtl;

    public SecurityServiceBase(UserService userService) {
        sessions = new ArrayList<>();
        this.userService = userService;
    }

    public LoginDTO login(LoginRequest request) {
        UserAuthDTO user = userService.authenticate(request.getEmail(), request.getPassword());
        String token = UUID.randomUUID().toString();
        AuthSessionDTO session = new AuthSessionDTO(token, user, LocalDateTime.now());
        sessions.add(session);

        return new LoginDTO(token, user.getName());
    }

    public void logout(String token) {
        sessions.removeIf(auth -> auth.getToken().equals(token));
    }

    public AuthSessionDTO findSessionByToken(String token) {
        return sessions.stream()
                .filter(auth -> auth.getToken().equals(token))
                .findFirst()
                .orElse(null);
    }

    public void cleanupExpiredSessions() {
        LocalDateTime now = LocalDateTime.now();

        Iterator<AuthSessionDTO> iterator = sessions.iterator();
        while (iterator.hasNext()) {
            AuthSessionDTO session = iterator.next();
            Duration age = Duration.between(session.getCreatedAt(), now);

            if (age.compareTo(sessionTtl) > 0) {
                iterator.remove();
            }
        }
    }
}
