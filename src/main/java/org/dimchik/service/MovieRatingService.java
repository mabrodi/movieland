package org.dimchik.service;

import org.dimchik.dto.JwtUserDetails;
import org.dimchik.dto.response.MovieRatingResponse;
import org.dimchik.entity.Movie;

import java.util.List;

public interface MovieRatingService {

    MovieRatingResponse findByMovieIdAndUser(long movieId, JwtUserDetails userDetails);

    MovieRatingResponse create(long movieId, double rating, JwtUserDetails userDetails);

    MovieRatingResponse update(long movieId, double rating, JwtUserDetails userDetails);

    void enrichSingleMovieByRating(Movie movie);

    void enrichSingleMovieByRating(List<Movie> movies);

    void removeRatingByMovieId(List<Long> movieIds);

}
