package org.dimchik.service;

import org.dimchik.dto.response.MovieDetailResponse;
import org.dimchik.dto.response.MovieResponse;
import org.dimchik.entity.Movie;
import org.dimchik.enums.SortDirection;
import org.dimchik.dto.request.CreateMovieRequest;
import org.dimchik.dto.request.UpdateMovieRequest;

import java.util.List;

public interface MovieService {
    List<MovieResponse> findAll(SortDirection ratingSortDirection, SortDirection priceSortDirection);

    MovieDetailResponse findById(long id);

    List<MovieResponse> random(int count);

    List<MovieResponse> findByGenreId(long genreId);

    MovieDetailResponse create(CreateMovieRequest request);

    MovieDetailResponse update(long id, UpdateMovieRequest request);
}
