package org.dimchik.service;

import org.dimchik.enums.Currency;
import org.dimchik.enums.SortDirection;
import org.dimchik.web.response.MovieDetailResponse;
import org.dimchik.web.response.MovieResponse;
import org.dimchik.web.request.CreateMovieRequest;
import org.dimchik.web.request.UpdateMovieRequest;

import java.util.List;

public interface MovieService {
    List<MovieResponse> findAll(SortDirection ratingSortDirection, SortDirection priceSortDirection);

    MovieDetailResponse findById(long id, Currency currency);

    List<MovieResponse> random(int count);

    List<MovieResponse> findByGenreId(long genreId);

    MovieDetailResponse create(CreateMovieRequest request);

    MovieDetailResponse update(long id, UpdateMovieRequest request);
}
