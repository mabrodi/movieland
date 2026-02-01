package org.dimchik.repository.specification;

import org.dimchik.common.enums.SortDirection;
import org.springframework.data.domain.Sort;

public class MovieSortSpecification {

    public static Sort build(SortDirection rating, SortDirection price) {
        Sort sort = Sort.unsorted();

        if (rating != null) {
            sort = sort.and(sortByRating(rating));
        }
        if (price != null) {
            sort = sort.and(sortByPrice(price));
        }

        return sort.isUnsorted() ? Sort.by("id").ascending() : sort;
    }

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
