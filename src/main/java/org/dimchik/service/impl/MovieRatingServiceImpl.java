package org.dimchik.service.impl;

import lombok.RequiredArgsConstructor;
import org.dimchik.dto.JwtUserDetails;
import org.dimchik.dto.response.MovieRatingResponse;
import org.dimchik.entity.Movie;
import org.dimchik.exception.AlreadyRatedException;
import org.dimchik.service.MovieRatingService;
import org.dimchik.service.cache.MovieRatingCacheService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MovieRatingServiceImpl implements MovieRatingService {
    private final MovieRatingCacheService movieRatingCacheService;

    @Override
    public MovieRatingResponse findByMovieIdAndUser(long movieId, JwtUserDetails userDetails) {
        if (!movieRatingCacheService.existsUserRating(movieId, userDetails.getId())) {
            throw new AlreadyRatedException("This movie no rating");
        }

        return new MovieRatingResponse(
                movieId,
                movieRatingCacheService.getUserMovieRating(movieId, userDetails.getId()),
                userDetails.getId()
        );
    }

    @Override
    public MovieRatingResponse create(long movieId, double rating, JwtUserDetails userDetails) {
        if (movieRatingCacheService.existsUserRating(movieId, userDetails.getId())) {
            throw new AlreadyRatedException("This movie has already been rated");
        }

        movieRatingCacheService.createUserMovieRating(movieId, rating, userDetails.getId());

        return new MovieRatingResponse(movieId, rating, userDetails.getId());
    }

    @Override
    public MovieRatingResponse update(long movieId, double rating, JwtUserDetails userDetails) {
        if (!movieRatingCacheService.existsUserRating(movieId, userDetails.getId())) {
            throw new AlreadyRatedException("This movie no rating. Should create rating ");
        }

        movieRatingCacheService.updateUserMovieRating(movieId, rating, userDetails.getId());

        return new MovieRatingResponse(movieId, rating, userDetails.getId());
    }

    @Override
    public void enrichSingleMovieByRating(Movie movie) {
        movieRatingCacheService.enrichMovieWithRating(movie);
    }

    @Override
    public void enrichSingleMovieByRating(List<Movie> movies) {
        movies.forEach(movieRatingCacheService::enrichMovieWithRating);
    }

    @Override
    public void removeRatingByMovieId(List<Long> movieIds) {
        for (long movieId : movieIds) {
            movieRatingCacheService.removeByMovieId(movieId);
        }
    }
}
