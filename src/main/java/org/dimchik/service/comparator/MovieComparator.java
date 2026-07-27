package org.dimchik.service.comparator;

import org.dimchik.entity.Movie;
import org.dimchik.enums.SortDirection;

import java.util.Comparator;

public class MovieComparator {
    public static Comparator<Movie> build(SortDirection rating, SortDirection price) {
        Comparator<Movie> comparator = Comparator.comparing(Movie::getId);

        if (rating != null) {
            comparator = direction(Comparator.comparingDouble(Movie::getRating), rating);
        }

        if (price != null) {
            comparator = comparator.thenComparing(
                    direction(Comparator.comparingDouble(Movie::getPrice), price)
            );
        }

        return comparator;
    }

    private static <T> Comparator<T> direction(Comparator<T> comparator, SortDirection direction) {
        return direction == SortDirection.DESC
                ? comparator.reversed()
                : comparator;
    }
}
