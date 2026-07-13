package org.dimchik.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.assertThat;

class TokenBlackListServiceTest {

    private TokenBlackListService tokenBlackListService;

    @BeforeEach
    void setUp() throws Exception {
        tokenBlackListService = new TokenBlackListService();
        Field limitField = TokenBlackListService.class.getDeclaredField("limit");
        limitField.setAccessible(true);
        limitField.setInt(tokenBlackListService, 3600);
    }

    @Test
    void addShouldIncreaseSize() {
        tokenBlackListService.add("token1");
        tokenBlackListService.add("token2");

        assertThat(tokenBlackListService.size()).isEqualTo(2);
    }

    @Test
    void addShouldAllowOverwritingSameToken() {
        tokenBlackListService.add("token1");
        tokenBlackListService.add("token1");

        assertThat(tokenBlackListService.size()).isEqualTo(1);
    }

    @Test
    void containsShouldReturnTrueForExistingToken() {
        tokenBlackListService.add("token1");
        tokenBlackListService.add("token2");
        tokenBlackListService.add("token3");

        assertThat(tokenBlackListService.contains("token1")).isTrue();
        assertThat(tokenBlackListService.contains("token2")).isTrue();
        assertThat(tokenBlackListService.contains("token3")).isTrue();
    }

    @Test
    void containsShouldReturnFalseForNonExistingToken() {
        tokenBlackListService.add("token1");

        assertThat(tokenBlackListService.contains("token2")).isFalse();
    }

    @Test
    void containsShouldReturnFalseForEmptyBlacklist() {
        assertThat(tokenBlackListService.contains("anything")).isFalse();
        assertThat(tokenBlackListService.size()).isEqualTo(0);
    }

    @Test
    void addShouldIncreaseSizeAfterFillUp() {
        tokenBlackListService.add("1111");
        tokenBlackListService.add("2222");
        tokenBlackListService.add("3333");

        assertThat(tokenBlackListService.size()).isEqualTo(3);
        tokenBlackListService.add("4444");

        assertThat(tokenBlackListService.size()).isEqualTo(4);
        assertThat(tokenBlackListService.contains("4444")).isTrue();
    }
}
