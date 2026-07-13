package org.dimchik.utils;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BearerTokenUtilsTest {

    @Test
    void hasBearerTokenShouldReturnTrueForValidBearerHeader() {
        assertThat(BearerTokenUtils.hasBearerToken("Bearer abc.def.ghi")).isTrue();
    }

    @Test
    void hasBearerTokenShouldReturnFalseForNull() {
        assertThat(BearerTokenUtils.hasBearerToken(null)).isFalse();
    }

    @Test
    void hasBearerTokenShouldReturnFalseForEmptyString() {
        assertThat(BearerTokenUtils.hasBearerToken("")).isFalse();
    }

    @Test
    void hasBearerTokenShouldReturnFalseForNonBearerHeader() {
        assertThat(BearerTokenUtils.hasBearerToken("Basic abc123")).isFalse();
        assertThat(BearerTokenUtils.hasBearerToken("Token abc.def.ghi")).isFalse();
        assertThat(BearerTokenUtils.hasBearerToken("abc.def.ghi")).isFalse();
    }

    @Test
    void extractTokenShouldReturnTokenWithoutBearerPrefix() {
        String token = BearerTokenUtils.extractToken("Bearer abc.def.ghi");
        assertThat(token).isEqualTo("abc.def.ghi");
    }

    @Test
    void extractTokenShouldThrowForNullHeader() {
        assertThatThrownBy(() -> BearerTokenUtils.extractToken(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Missing Bearer token");
    }

    @Test
    void extractTokenShouldThrowForEmptyHeader() {
        assertThatThrownBy(() -> BearerTokenUtils.extractToken(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Missing Bearer token");
    }

    @Test
    void extractTokenShouldThrowForNonBearerHeader() {
        assertThatThrownBy(() -> BearerTokenUtils.extractToken("Basic abc123"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Missing Bearer token");
    }

    @Test
    void extractTokenShouldHandleTokenWithSpaces() {
        String token = BearerTokenUtils.extractToken("Bearer abc def ghi");
        assertThat(token).isEqualTo("abc def ghi");
    }
}
