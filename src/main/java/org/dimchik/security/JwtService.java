package org.dimchik.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.dimchik.enums.Role;
import org.dimchik.dto.TokenUserDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {
    private final SecretKey signingKey;
    private final long tokenTtl;

    public JwtService(
            @Value("${security.jwt.secret}") String secret,
            @Value("${security.jwt.ttl-minutes}") long ttl
    ) {
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.tokenTtl = ttl * 60 * 1000L;
    }

    public String generateToken(TokenUserDTO user) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + tokenTtl);

        return Jwts.builder()
                .subject(user.getEmail())
                .id(UUID.randomUUID().toString())
                .issuedAt(now)
                .expiration(expiration)
                .claim("userId", user.getId())
                .claim("name", user.getName())
                .claim("role", user.getRole())
                .signWith(signingKey)
                .compact();
    }

    public TokenUserDTO extractUser(String token) {
        return TokenUserDTO.builder()
                .id(parseToken(token).get("userId", Long.class))
                .name(parseToken(token).get("name", String.class))
                .email(parseToken(token).getSubject())
                .role(Role.valueOf(parseToken(token).get("role", String.class)))
                .build();
    }

    public boolean isValid(String token) {
        try {
            Claims claims = parseToken(token);
            return !claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    public String refreshToken(String token) {
        TokenUserDTO extractUserFromToken = extractUser(token);
        TokenUserDTO dto = TokenUserDTO.builder()
                .id(extractUserFromToken.getId())
                .email(extractUserFromToken.getEmail())
                .role(extractUserFromToken.getRole())
                .build();

        return generateToken(dto);
    }

    private Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
