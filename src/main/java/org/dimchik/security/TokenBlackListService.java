package org.dimchik.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class TokenBlackListService {
    @Value("${security.jwt.ttl-seconds}")
    private int limit;
    private final Map<String, Instant> list = new ConcurrentHashMap<>();

    public void add(String token) {
        list.put(token, Instant.now());
    }

    public boolean contains(String token) {
        return list.containsKey(token);
    }

    public int size() {
        return list.size();
    }

    @Scheduled(cron = "${security.jwt.blacklist.cleanup-cron}")
    private void clear() {
        Instant now = Instant.now();
        list.entrySet().removeIf(entry ->
                entry.getValue().plusSeconds(limit).isBefore(now));
    }
}
