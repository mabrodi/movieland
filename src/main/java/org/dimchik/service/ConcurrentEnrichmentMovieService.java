package org.dimchik.service;

import org.dimchik.entity.Movie;

public interface ConcurrentEnrichmentMovieService {
    void enrichMovie(Movie movie);
}
