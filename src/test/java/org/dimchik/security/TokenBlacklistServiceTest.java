package org.dimchik.security;

import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class TokenBlacklistServiceTest {
    private final TokenBlacklistService blacklistService = new TokenBlacklistService();

    @Test
    void shouldReturnFalse_whenTokenNotBlacklisted() {
        assertFalse(blacklistService.isBlacklisted("token-1"));
    }

    @Test
    void shouldReturnTrueWhenTokenIsBlacklistedAndNotExpired() {
        String token = "token-2";
        Date future = new Date(System.currentTimeMillis() + 60_000);

        blacklistService.blacklist(token, future);

        assertTrue(blacklistService.isBlacklisted(token));
    }

    @Test
    void shouldReturnFalseAndRemoveTokenWhenTokenIsExpired() {
        String token = "token-3";
        Date past = new Date(System.currentTimeMillis() - 1_000);

        blacklistService.blacklist(token, past);

        assertFalse(blacklistService.isBlacklisted(token));

        assertFalse(blacklistService.isBlacklisted(token));
    }

    @Test
    void multipleTokensShouldBeHandledIndependently() {
        String activeToken = "active";
        String expiredToken = "expired";

        blacklistService.blacklist(
                activeToken,
                new Date(System.currentTimeMillis() + 60_000)
        );
        blacklistService.blacklist(
                expiredToken,
                new Date(System.currentTimeMillis() - 1_000)
        );

        assertTrue(blacklistService.isBlacklisted(activeToken));
        assertFalse(blacklistService.isBlacklisted(expiredToken));
    }

    @Test
    void shouldNotThrowWhenCheckingUnknownToken() {
        assertDoesNotThrow(() -> blacklistService.isBlacklisted("unknown"));
    }

}