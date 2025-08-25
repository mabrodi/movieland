package org.dimchik.repository.specification;

import org.springframework.data.domain.Sort;

public class MovieSortSpecification {

    public static Sort sortByRating(String direction) {
        return "asc".equalsIgnoreCase(direction)
                ? Sort.by(Sort.Order.asc("rating"))
                : Sort.by(Sort.Order.desc("rating"));
    }

    public static Sort sortByPrice(String direction) {
        return "asc".equalsIgnoreCase(direction)
                ? Sort.by(Sort.Order.asc("price"))
                : Sort.by(Sort.Order.desc("price"));
    }
}
