package org.dimchik.service.enrichment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dimchik.dto.*;
import org.dimchik.entity.Country;
import org.dimchik.entity.Genre;
import org.dimchik.entity.Movie;
import org.dimchik.entity.Review;
import org.dimchik.repository.CountryRepository;
import org.dimchik.repository.GenreRepository;
import org.dimchik.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class MovieEnrichmentService {
    private final GenreRepository genreRepository;
    private final CountryRepository countryRepository;
    private final ReviewRepository reviewRepository;

    @Value("${movie.enrichment.timeout-seconds}")
    private int timeoutSeconds;

    public MovieFullDTO enrich(Movie movie) {
        MovieFullDTO dto = new MovieFullDTO();
        dto.setId(movie.getId());
        dto.setNameRussian(movie.getNameRussian());
        dto.setNameNative(movie.getNameNative());
        dto.setYearOfRelease(movie.getYearOfRelease());
        dto.setRating(movie.getRating());
        dto.setPrice(movie.getPrice());
        dto.setPicturePath(movie.getPoster() != null ? movie.getPoster().getPicturePath() : null);

        Thread genresThread = new Thread(() -> dto.setGenres(loadGenres(movie.getId())));
        Thread countriesThread = new Thread(() -> dto.setCountries(loadCountries(movie.getId())));
        Thread reviewsThread = new Thread(() -> dto.setReviews(loadReviews(movie.getId())));

        genresThread.start();
        countriesThread.start();
        reviewsThread.start();

        waitWithTimeout(genresThread, "genres", movie.getId());
        waitWithTimeout(countriesThread, "countries", movie.getId());
        waitWithTimeout(reviewsThread, "reviews", movie.getId());

        if (dto.getGenres() == null) {
            dto.setGenres(Collections.emptyList());
        }
        if (dto.getCountries() == null) {
            dto.setCountries(Collections.emptyList());
        }
        if (dto.getReviews() == null) {
            dto.setReviews(Collections.emptyList());
        }

        return dto;
    }

    private List<GenreDTO> loadGenres(Long movieId) {
        try {
            List<Genre> genres = genreRepository.findAllByMovieId(movieId);
            List<GenreDTO> result = new ArrayList<>();
            for (Genre genre : genres) {
                if (Thread.currentThread().isInterrupted()) {
                    return Collections.emptyList();
                }
                result.add(new GenreDTO(genre.getId(), genre.getName()));
            }
            return result;
        } catch (Exception e) {
            log.error("Error loading genres for movie {}: {}", movieId, e.getMessage());
            return Collections.emptyList();
        }
    }

    private List<CountryDTO> loadCountries(Long movieId) {
        try {
            List<Country> countries = countryRepository.findAllByMovieId(movieId);
            List<CountryDTO> result = new ArrayList<>();
            for (Country country : countries) {
                if (Thread.currentThread().isInterrupted()) {
                    return Collections.emptyList();
                }
                result.add(new CountryDTO(country.getId(), country.getName()));
            }
            return result;
        } catch (Exception e) {
            log.error("Error loading countries for movie {}: {}", movieId, e.getMessage());
            return Collections.emptyList();
        }
    }

    private List<ReviewDTO> loadReviews(Long movieId) {
        try {
            List<Review> reviews = reviewRepository.findAllByMovieId(movieId);
            List<ReviewDTO> result = new ArrayList<>();
            for (Review review : reviews) {
                if (Thread.currentThread().isInterrupted()) {
                    return Collections.emptyList();
                }
                UserDTO user = new UserDTO(review.getAuthor().getId(), review.getAuthor().getName());
                result.add(new ReviewDTO(review.getId(), review.getComment(), user));
            }
            return result;
        } catch (Exception e) {
            log.error("Error loading reviews for movie {}: {}", movieId, e.getMessage());
            return Collections.emptyList();
        }
    }

    private void waitWithTimeout(Thread thread, String name, long movieId) {
        try {
            thread.join(timeoutSeconds * 1000L);
            if (thread.isAlive()) {
                log.warn("Timeout while loading {} for movie id={}", name, movieId);
                thread.interrupt();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("{} thread interrupted for movie id={}", name, movieId);
        }
    }
}
