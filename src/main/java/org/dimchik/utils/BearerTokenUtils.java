package org.dimchik.utils;

public class BearerTokenUtils {
    public static final String PREFIX = "Bearer ";

    public static boolean hasBearerToken(String header) {
        return header == null || !header.startsWith(PREFIX);
    }

    public static String extractToken(String header) {
        if (!hasBearerToken(header)) {
            throw new IllegalArgumentException("Missing Bearer token");
        }

        return header.substring(PREFIX.length());
    }
}
