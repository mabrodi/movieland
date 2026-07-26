package org.dimchik.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dimchik.dto.request.FindAllMovieRequest;
import org.dimchik.dto.response.MovieDetailResponse;
import org.dimchik.dto.response.MovieResponse;
import org.dimchik.service.*;
import org.springframework.data.domain.PageRequest;
import org.dimchik.entity.Movie;
import org.dimchik.repository.MovieRepository;
import org.dimchik.repository.specification.MovieSortSpecification;
import org.dimchik.service.cache.MovieCacheService;
import org.dimchik.mapper.MovieMapper;
import org.dimchik.exception.MovieNotFoundException;
import org.dimchik.dto.request.CreateMovieRequest;
import org.dimchik.dto.request.UpdateMovieRequest;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class MovieServiceImpl implements MovieService {
    private final MovieRepository movieRepository;
    private final MovieMapper movieMapper;
    private final MovieCacheService movieCacheService;
    private final GenreService genreService;
    private final PosterService posterService;
    private final CountryService countryService;
    private final MovieRatingService movieRatingService;
    private final MovieDeletionQueueService movieDeletionQueueService;
    private final ConcurrentEnrichmentMovieService concurrentEnrichmentMovieService;


    @Transactional(readOnly = true)
    @Override
    public List<MovieResponse> findAll(FindAllMovieRequest request) {
        Sort sort = MovieSortSpecification.build(request.getRatingSortDirection(), request.getPriceSortDirection());
        List<Movie> movieList = movieRepository.findAllWithPoster(sort);
        movieRatingService.enrichSingleMovieByRating(movieList);

        return movieMapper.toResponseList(movieList);
    }

    @Transactional(readOnly = true)
    @Override
    public MovieDetailResponse findById(long id) {
        log.info("Start to get movie by id = {}", id);

        Movie movie = movieCacheService.getById(id);
        if (movie == null) {
            log.debug("Movie not found in cache, loading from DB");
            movie = movieRepository.findById(id)
                    .orElseThrow(() -> new MovieNotFoundException(id));
            concurrentEnrichmentMovieService.enrichMovie(movie);
            movieCacheService.add(movie);
        }

        movieRatingService.enrichSingleMovieByRating(movie);

        log.info("Finish to get movie by id = {}", id);
        log.info("movie genres = {}", movie.getGenres().size());
        log.info("movie countries = {}", movie.getCountries().size());
        log.info("movie reviews = {}", movie.getReviews().size());

        return movieMapper.toDetailResponse(movie);
    }

    @Transactional(readOnly = true)
    @Override
    public List<MovieResponse> random(int count) {
        List<Movie> movieList = movieRepository.findRandomMovies(PageRequest.of(0, count));
        movieRatingService.enrichSingleMovieByRating(movieList);

        return movieMapper.toResponseList(movieList);
    }

    @Transactional(readOnly = true)
    @Override
    public List<MovieResponse> findByGenreId(long genreId) {
        List<Movie> movieList = movieRepository.findMoviesByGenreId(genreId);
        movieRatingService.enrichSingleMovieByRating(movieList);

        return movieMapper.toResponseList(movieList);
    }

    @Transactional
    @Override
    public MovieDetailResponse create(CreateMovieRequest request) {
        log.info("Start to create movie");
        Movie movie = movieMapper.createMovieFromEntity(request);
        movie.setGenres(genreService.findAllIds(request.getGenres()));
        movie.setCountries(countryService.findAllIds(request.getCountries()));
        movieRepository.save(movie);
        movie.setPoster(posterService.upsertPoster(movie, request.getPicturePath()));

        log.info("end create movie");
        movieCacheService.add(movie);
        log.info("add movie cache");

        movieRatingService.enrichSingleMovieByRating(movie);

        return movieMapper.toDetailResponse(movie);
    }

    @Transactional
    @Override
    public MovieDetailResponse update(long id, UpdateMovieRequest request) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new MovieNotFoundException(id));

        log.info("Start to update movie: {}", id);

        movieMapper.updateMovieFromRequest(request, movie);

        movie.setGenres(genreService.findAllIds(request.getGenres()));
        movie.setCountries(countryService.findAllIds(request.getCountries()));
        movieRepository.save(movie);
        movie.setPoster(posterService.upsertPoster(movie, request.getPicturePath()));

        log.info("end to update movie: {}", id);

        movieCacheService.invalidate(movie.getId());
        movieCacheService.add(movie);
        log.info("update movie cache: {}", id);

        movieRatingService.enrichSingleMovieByRating(movie);

        return movieMapper.toDetailResponse(movie);
    }

    @Override
    public void queueForDeletion(long id) {
        if (!movieRepository.existsById(id)) {
            throw new MovieNotFoundException(id);
        }

        movieDeletionQueueService.scheduleForDeletion(id);
    }

    @Override
    public void removeFromDeletionQueue(long id) {
        if (!movieRepository.existsById(id)) {
            throw new MovieNotFoundException(id);
        }

        movieDeletionQueueService.cancelDeletion(id);
    }


    @Scheduled(cron = "${cron.movie-removed-cache}")
    void deleteScheduledMovies() {
        List<Long> moviesIds = movieDeletionQueueService.getPendingDeletionMovieIds();
        movieRepository.deleteAllById(moviesIds);
        movieRatingService.removeRatingByMovieId(moviesIds);
    }
}
