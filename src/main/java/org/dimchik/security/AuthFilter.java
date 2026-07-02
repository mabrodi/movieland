package org.dimchik.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.dimchik.dto.UserTokenDTO;
import org.dimchik.utils.BearerTokenUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AuthFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final TokenBlackListService tokenBlackListService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (!BearerTokenUtils.hasBearerToken(header)) {
            chain.doFilter(request, response);
            return;
        }

        String token = BearerTokenUtils.extractToken(header);

        try {
            if (tokenBlackListService.contains(token)
                    && jwtService.isValid(token)
                    && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserTokenDTO userDTO = jwtService.extractUser(token);

                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                        userDTO.getEmail(),
                        null,
                        List.of(new SimpleGrantedAuthority(userDTO.getRole().name()))
                );

                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        } catch (Exception e) {
            logger.debug("Token validation failed", e);
        }

        chain.doFilter(request, response);
    }
}
