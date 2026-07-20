package org.dimchik.enums;

import java.util.Arrays;

public enum SortDirection {
    ASC,
    DESC;

    public static SortDirection from(String value) {
        return Arrays.stream(values())
                .filter(v -> v.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException("Unknown sort direction: " + value));
    }
}
