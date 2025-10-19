package org.dimchik.web.security;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.dimchik.common.enums.Role;
import org.dimchik.dto.AuthSessionDTO;
import org.dimchik.service.SecurityService;
import org.dimchik.web.exception.AuthenticateException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AuthFilter implements Filter {
    private final SecurityService securityService;

    private static final List<Route> ROUTES = List.of(
            new Route("/api/v1/movie", "post", List.of(Role.ADMIN)),
            new Route("/api/v1/movie", "put", List.of(Role.ADMIN)),
            new Route("/api/v1/review", "post", List.of(Role.USER))
    );


    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;

        String path = request.getRequestURI();
        String method = request.getMethod().toUpperCase(Locale.ROOT);
        if (path.startsWith("/api/v1/login")) {
            chain.doFilter(req, res);
            return;
        }

        Optional<Route> matchingRoute = ROUTES.stream()
                .filter(route -> path.startsWith(route.getPath()) && method.equalsIgnoreCase(route.getMethod()))
                .findFirst();

        if (matchingRoute.isEmpty()) {
            chain.doFilter(req, res);
            return;
        }

        String token = request.getHeader("Authorization");
        if (token == null || token.isBlank()) {
            throw new AuthenticateException("Missing Authorization header");
        }

        AuthSessionDTO authSession = securityService.findSessionByToken(token);
        if (authSession == null) {
            throw new AuthenticateException("Invalid or expired token");
        }

        Role userRole = authSession.getUser().getRole();
        List<Role> allowedRoles = matchingRoute.get().getRoutes();
        if (!allowedRoles.contains(userRole)) {
            throw new AuthenticateException("Access denied for role: " + userRole);
        }

        chain.doFilter(req, res);
    }

    @Setter
    @Getter
    private static class Route {
        private String path;
        private String method;
        private List<Role> routes;

        public Route(String path, String method, List<Role> routes) {
            this.path = path;
            this.method = method;
            this.routes = routes;
        }

    }
}
