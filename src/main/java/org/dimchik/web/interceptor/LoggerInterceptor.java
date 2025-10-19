package org.dimchik.web.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dimchik.dto.AuthSessionDTO;
import org.dimchik.service.SecurityService;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoggerInterceptor implements HandlerInterceptor {
    private final SecurityService securityService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestId = UUID.randomUUID().toString();
        MDC.put("requestId", requestId);

        String token = request.getHeader("Authorization");
        String userLogin = "guest";

        if (token != null && !token.isBlank()) {
            try {
                AuthSessionDTO session = securityService.findSessionByToken(token);
                if (session != null && session.getUser() != null) {
                    userLogin = session.getUser().getEmail();
                }
            } catch (Exception e) {
                userLogin = "unauthorized";
            }
        }

        MDC.put("user", userLogin);

        response.setHeader("X-Request-Id", requestId);

        log.info("Incoming request: {} {}", request.getMethod(), request.getRequestURI());

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        MDC.clear();
    }
}
