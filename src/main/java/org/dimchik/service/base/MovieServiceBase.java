package org.dimchik.service.base;

import lombok.AllArgsConstructor;
import org.dimchik.common.enums.Currency;
import org.dimchik.common.enums.SortDirection;
import org.dimchik.common.request.CreateMovieRequest;
import org.dimchik.common.request.MovieByIdRequest;
import org.dimchik.common.request.MovieRequest;
import org.dimchik.common.request.UpdateMovieRequest;
import org.dimchik.dto.MovieDTO;
import org.dimchik.dto.MovieFullDTO;
import org.dimchik.entity.Country;
import org.dimchik.entity.Genre;
import org.dimchik.entity.Movie;
import org.dimchik.entity.Poster;
import org.dimchik.repository.CountryRepository;
import org.dimchik.repository.GenreRepository;
import org.dimchik.repository.PosterRepository;
import org.dimchik.service.cache.MovieCacheService;
import org.dimchik.service.cache.RateCacheService;
import org.dimchik.service.enrichment.MovieEnrichmentService;
import org.dimchik.web.exception.ResourceNotFoundException;
import org.dimchik.repository.MovieRepository;
import org.dimchik.repository.mapper.MovieRowMapper;
import org.dimchik.repository.specification.MovieSortSpecification;
import org.dimchik.service.MovieService;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@AllArgsConstructor
@Service
public class MovieServiceBase implements MovieService {
    private final MovieRepository movieRepository;
    private final GenreRepository genreRepository;
    private final CountryRepository countryRepository;
    private final PosterRepository posterRepository;
    private final MovieRowMapper movieRowMapper;
    private final RateCacheService rateCacheService;
    private final MovieEnrichmentService movieEnrichmentService;
    private final MovieCacheService movieCacheService;

    @Override
    public List<MovieDTO> findAll(MovieRequest request) {
        Sort sort = buildSort(request.getRatingSortDirection(), request.getPriceSortDirection());
        List<Movie> movieList = movieRepository.findAll(sort);

        return convertToDtoList(movieList);
    }

    @Override
    public List<MovieDTO> random(int count) {
        List<MovieDTO> movieResponseDTOList;

        List<Movie> movieList = movieRepository.findAll();
        if (!movieList.isEmpty()) {
            Collections.shuffle(movieList);
            movieResponseDTOList = movieList.stream()
                    .map(movieRowMapper::convertToDTO)
                    .limit(count)
                    .toList();
        } else {
            movieResponseDTOList = Collections.emptyList();
        }

        return movieResponseDTOList;
    }

    @Override
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
    public List<MovieDTO> findByGenreId(long genreId) {
        List<Movie> movieList = movieRepository.findMoviesByGenreId(genreId);

        return convertToDtoList(movieList);
    }

    @Transactional
    @Override
    public MovieFullDTO create(CreateMovieRequest request) {
        Movie movie = movieRowMapper.convertCreateMovieRequestToMovie(request);
        createAndUpdateGenres(movie, request.getGenres());
        createAndUpdateCountries(movie, request.getCountries());

        Poster poster = new Poster();
        poster.setMovie(movie);
        poster.setPicturePath(request.getPicturePath());

        posterRepository.save(poster);
        Movie createdMovie = movieRepository.save(movie);
        movieCacheService.getMovie(createdMovie.getId());

        return movieRowMapper.convertToFullDTO(createdMovie);
    }

    @Transactional
    @Override
    public MovieFullDTO update(long id, UpdateMovieRequest request) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + id));

        if (request.getNameRussian() != null) {
            movie.setNameRussian(request.getNameRussian());
        }
        if (request.getNameNative() != null) {
            movie.setNameNative(request.getNameNative());
        }
        if (request.getPicturePath() != null) {
            Poster poster = movie.getPoster();
            if (poster == null) {
                poster = new Poster();
                poster.setMovie(movie);
            }
            poster.setPicturePath(request.getPicturePath());
            posterRepository.save(poster);
        }

        if (request.getCountries() != null) {
            createAndUpdateCountries(movie, request.getCountries());
        }
        if (request.getGenres() != null) {
            createAndUpdateGenres(movie, request.getGenres());
        }

        Movie updatedMovie = movieRepository.save(movie);
        movieCacheService.updateMovie(updatedMovie);

        return movieRowMapper.convertToFullDTO(updatedMovie);
    }

    private void createAndUpdateGenres(Movie movie, List<Long> genreIds) {
        if (genreIds == null || genreIds.isEmpty()) {
            return;
        }

        List<Genre> genres = genreIds.stream()
                .map(id -> genreRepository.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("Genre not found with id: " + id)))
                .toList();

        movie.setGenres(genres);
    }

    private void createAndUpdateCountries(Movie movie, List<Long> countryIds) {
        if (countryIds == null || countryIds.isEmpty()) {
            return;
        }

        List<Country> countries = countryIds.stream()
                .map(id -> countryRepository.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("Country not found with id: " + id)))
                .toList();

        movie.setCountries(countries);
    }


    private Sort buildSort(SortDirection rating, SortDirection price) {
        Sort sort = Sort.unsorted();

        if (rating != null) {
            sort = sort.and(MovieSortSpecification.sortByRating(rating));
        }
        if (price != null) {
            sort = sort.and(MovieSortSpecification.sortByPrice(price));
        }

        return sort.isUnsorted() ? Sort.by("id").ascending() : sort;
    }

    private List<MovieDTO> convertToDtoList(List<Movie> movies) {
        if (movies == null || movies.isEmpty()) {
            return Collections.emptyList();
        }
        return movies.stream()
                .map(movieRowMapper::convertToDTO)
                .toList();
    }
}
