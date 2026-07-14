package org.dimchik.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dimchik.dto.response.MovieDetailResponse;
import org.dimchik.dto.response.MovieResponse;
import org.dimchik.service.*;
import org.springframework.data.domain.PageRequest;
import org.dimchik.entity.Movie;
import org.dimchik.repository.MovieRepository;
import org.dimchik.repository.specification.MovieSortSpecification;
import org.dimchik.enums.SortDirection;
import org.dimchik.service.cache.MovieCacheService;
import org.dimchik.service.mapper.MovieMapper;
import org.dimchik.web.exception.MovieNotFoundException;
import org.dimchik.dto.request.CreateMovieRequest;
import org.dimchik.dto.request.UpdateMovieRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@AllArgsConstructor
@Service
public class MovieServiceImpl implements MovieService {
    private final MovieRepository movieRepository;
    private final MovieMapper movieMapper;
    private final MovieCacheService movieCacheService;
    private final GenreService genreService;
    private final PosterService posterService;
    private final CountryService countryService;
    private final ConcurrentEnrichmentMovieService concurrentEnrichmentMovieService;

    @Override
    public List<MovieResponse> findAll(SortDirection ratingSortDirection, SortDirection priceSortDirection) {
        Sort sort = MovieSortSpecification.build(ratingSortDirection, priceSortDirection);
        List<Movie> movieList = movieRepository.findAll(sort);

        return movieMapper.toResponseList(movieList);
    }

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

        log.info("Finish to get movie by id = {}", id);

        return movieMapper.toDetailResponse(movie);
    }

    @Override
    public List<MovieResponse> random(int count) {
        List<Movie> movieList = movieRepository.findRandomMovies(PageRequest.of(0, count));

        return movieMapper.toResponseList(movieList);
    }

    @Override
    public List<MovieResponse> findByGenreId(long genreId) {
        List<Movie> movieList = movieRepository.findMoviesByGenreId(genreId);

        return movieMapper.toResponseList(movieList);
    }

    @Transactional
    @Override
    public MovieDetailResponse create(CreateMovieRequest request) {
        Movie movie = movieMapper.createMovieFromEntity(request);
        movie.setGenres(genreService.findAllIds(request.getGenres()));
        movie.setCountries(countryService.findAllIds(request.getCountries()));
        posterService.upsertPoster(movie, request.getPicturePath());
        movieRepository.save(movie);

        movieCacheService.invalidate(movie.getId());

        return movieMapper.toDetailResponse(movie);
    }

    @Transactional
    @Override
    public MovieDetailResponse update(long id, UpdateMovieRequest request) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new MovieNotFoundException(id));

        movieMapper.updateMovieFromRequest(request, movie);

        movie.setGenres(genreService.findAllIds(request.getGenres()));
        movie.setCountries(countryService.findAllIds(request.getCountries()));
        posterService.upsertPoster(movie, request.getPicturePath());
        movieRepository.save(movie);

        movieCacheService.invalidate(movie.getId());

        return movieMapper.toDetailResponse(movie);
    }
}
