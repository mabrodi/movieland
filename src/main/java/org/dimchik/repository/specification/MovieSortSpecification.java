package org.dimchik.repository.specification;

import org.dimchik.common.enums.SortDirection;
import org.springframework.data.domain.Sort;

public class MovieSortSpecification {

    public static Sort sortByRating(SortDirection direction) {
        return direction == SortDirection.ASC
                ? Sort.by(Sort.Order.asc("rating"))
                : Sort.by(Sort.Order.desc("rating"));
    }

    public static Sort sortByPrice(SortDirection direction) {
        return direction == SortDirection.ASC
                ? Sort.by(Sort.Order.asc("price"))
                : Sort.by(Sort.Order.desc("price"));
    }
}
