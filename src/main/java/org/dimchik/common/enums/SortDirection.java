package org.dimchik.common.enums;

public enum SortDirection {
    ASC,
    DESC;

    public static SortDirection from(String value) {
        return SortDirection.valueOf(value.toUpperCase());
    }
}
