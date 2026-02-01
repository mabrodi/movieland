package org.dimchik.security;

import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenBlacklistService {
    private final Map<String, Long> blacklisted = new ConcurrentHashMap<>();

    public void blacklist(String token, Date expiresAt) {
        blacklisted.put(token, expiresAt.getTime());
    }

    public boolean isBlacklisted(String token) {
        Long expiresAt = blacklisted.get(token);
        if (expiresAt == null) return false;

        long now = System.currentTimeMillis();
        if (expiresAt <= now) {
            blacklisted.remove(token);
            return false;
        }
        return true;
    }
}
