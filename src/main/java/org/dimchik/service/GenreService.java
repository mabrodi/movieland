package org.dimchik.service;

import org.dimchik.dto.response.GenreResponse;
import org.dimchik.entity.Genre;
import org.dimchik.entity.Movie;

import java.util.List;

public interface GenreService {
    List<GenreResponse> findAll();

    void enrichSingleMovieByGenres(Movie movie);

    List<Genre> findAllIds(List<Long> ids);
}
