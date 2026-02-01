package org.dimchik.service.base;

import lombok.AllArgsConstructor;
import org.dimchik.common.enums.Currency;
import org.dimchik.dto.MovieDTO;
import org.dimchik.dto.MovieFullDTO;
import org.dimchik.entity.Country;
import org.dimchik.entity.Genre;
import org.dimchik.entity.Movie;
import org.dimchik.entity.Poster;
import org.dimchik.repository.CountryRepository;
import org.dimchik.repository.GenreRepository;
import org.dimchik.repository.MovieRepository;
import org.dimchik.repository.PosterRepository;
import org.dimchik.repository.mapper.MovieMapper;
import org.dimchik.repository.specification.MovieSortSpecification;
import org.dimchik.service.MovieService;
import org.dimchik.service.cache.MovieCacheService;
import org.dimchik.service.cache.RateCacheService;
import org.dimchik.service.enrichment.MovieEnrichmentService;
import org.dimchik.web.exception.ResourceNotFoundException;
import org.dimchik.web.request.CreateMovieRequest;
import org.dimchik.web.request.MovieByIdRequest;
import org.dimchik.web.request.MovieRequest;
import org.dimchik.web.request.UpdateMovieRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@AllArgsConstructor
@Service
public class MovieServiceBase implements MovieService {
    private final MovieRepository movieRepository;
    private final MovieMapper movieMapper;
    private final MovieEnrichmentService movieEnrichmentService;
    private final MovieCacheService movieCacheService;
    private final RateCacheService rateCacheService;
    private final CountryRepository countryRepository;
    private final PosterRepository posterRepository;
    private final GenreRepository genreRepository;

    @Override
    public List<MovieDTO> findAll(MovieRequest request) {
        Sort sort = MovieSortSpecification.build(request.getRatingSortDirection(), request.getPriceSortDirection());
        List<Movie> movieList = movieRepository.findAll(sort);

        return convertToDtoList(movieList);
    }

    public MovieFullDTO findById(long id, MovieByIdRequest request) {
        Movie movie = movieCacheService.getMovie(id);
        if (movie == null) {
            throw new ResourceNotFoundException("Movie not found with id: " + id);
        }

        MovieFullDTO dto = movieEnrichmentService.enrich(movie);
        if (request.getCurrency() != Currency.UAH) {
            rateCacheService.findAll().stream()
                    .filter(rate -> request.getCurrency().name().equals(rate.getCurrency()))
                    .findFirst()
                    .ifPresent(rate -> dto.setPrice(dto.getPrice() * rate.getRate()));
        }

        return dto;
    }


    @Override
    public List<MovieDTO> random(int count) {
        List<MovieDTO> movieResponseDTOList;

        List<Movie> movieList = movieRepository.findAll();
        if (!movieList.isEmpty()) {
            Collections.shuffle(movieList);
            movieResponseDTOList = movieList.stream()
                    .map(movieMapper::toDto)
                    .limit(count)
                    .toList();
        } else {
            movieResponseDTOList = Collections.emptyList();
        }

        return movieResponseDTOList;
    }

    @Override
    public List<MovieDTO> findByGenreId(long genreId) {
        List<Movie> movieList = movieRepository.findMoviesByGenreId(genreId);

        return convertToDtoList(movieList);
    }


    @Transactional
    @Override
    public MovieFullDTO create(CreateMovieRequest request) {
        Movie movie = movieMapper.toEntity(request);

        applyGenres(movie, request.getGenres());
        applyCountries(movie, request.getCountries());
        upsertPoster(movie, request.getPicturePath());

        Movie createdMovie = movieRepository.save(movie);
        movieCacheService.updateMovie(createdMovie);

        return movieMapper.toFullDto(createdMovie);
    }

    @Transactional
    @Override
    public MovieFullDTO update(long id, UpdateMovieRequest request) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + id));

        movieMapper.updateMovieFromRequest(request, movie);

        applyGenres(movie, request.getGenres());
        applyCountries(movie, request.getCountries());
        upsertPoster(movie, request.getPicturePath());

        Movie updatedMovie = movieRepository.save(movie);
        movieCacheService.updateMovie(updatedMovie);

        return movieMapper.toFullDto(updatedMovie);
    }


    private void applyGenres(Movie movie, List<Long> genreIds) {
        if (genreIds == null || genreIds.isEmpty()) {
            return;
        }

        List<Genre> genres = genreRepository.findAllById(genreIds);
        if (genres.size() != genreIds.size()) {
            throw new IllegalArgumentException("Some genres not found");
        }
        movie.setGenres(genres);
    }

    private void applyCountries(Movie movie, List<Long> countryIds) {
        if (countryIds == null || countryIds.isEmpty()) {
            return;
        }

        List<Country> countries = countryRepository.findAllById(countryIds);
        if (countries.size() != countryIds.size()) {
            throw new IllegalArgumentException("Some countries not found");
        }
        movie.setCountries(countries);
    }

    private void upsertPoster(Movie movie, String picturePath) {
        if (picturePath == null) {
            return;
        }

        Poster poster = movie.getPoster();
        if (poster == null) {
            poster = new Poster();
            poster.setMovie(movie);
        }
        poster.setPicturePath(picturePath);
        posterRepository.save(poster);
    }


    private List<MovieDTO> convertToDtoList(List<Movie> movies) {
        if (movies == null || movies.isEmpty()) {
            return Collections.emptyList();
        }
        return movies.stream()
                .map(movieMapper::toDto)
                .toList();
    }
}
