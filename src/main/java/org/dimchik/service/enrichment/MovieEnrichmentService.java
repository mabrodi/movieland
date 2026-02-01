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
import org.dimchik.repository.mapper.MovieMapper;
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
    private final MovieMapper movieMapper;

    @Value("${movie.enrichment.timeout-seconds:5}")
    private int timeoutSeconds;

    private final ExecutorService executor = Executors.newCachedThreadPool();

    public MovieFullDTO enrich(Movie movie) {
        MovieFullDTO dto = movieMapper.toFullDto(movie);

        Callable<List<GenreDTO>> genresTask = () -> loadGenres(movie.getId());
        Callable<List<CountryDTO>> countriesTask = () -> loadCountries(movie.getId());
        Callable<List<ReviewDTO>> reviewsTask = () -> loadReviews(movie.getId());

        Future<List<GenreDTO>> genresFuture = executor.submit(genresTask);
        Future<List<CountryDTO>> countriesFuture = executor.submit(countriesTask);
        Future<List<ReviewDTO>> reviewsFuture = executor.submit(reviewsTask);

        List<GenreDTO> genres = getWithTimeout(genresFuture, "genres", movie.getId());
        List<CountryDTO> countries = getWithTimeout(countriesFuture, "countries", movie.getId());
        List<ReviewDTO> reviews = getWithTimeout(reviewsFuture, "reviews", movie.getId());

        dto.setGenres(genres != null ? genres : Collections.emptyList());
        dto.setCountries(countries != null ? countries : Collections.emptyList());
        dto.setReviews(reviews != null ? reviews : Collections.emptyList());

        return dto;
    }

    private List<GenreDTO> loadGenres(Long movieId) {
        try {
            List<Genre> genres = genreRepository.findAllByMovieId(movieId);
            return genres.stream()
                    .peek(genre -> {
                        if (Thread.currentThread().isInterrupted()) {
                            throw new RuntimeException("Interrupted while mapping genres");
                        }
                    })
                    .map(genre -> new GenreDTO(genre.getId(), genre.getName()))
                    .toList();

        } catch (RuntimeException e) {
            if (Thread.currentThread().isInterrupted()) {
                log.warn("Genres loading interrupted for movie id={}", movieId);
                return Collections.emptyList();
            }
            log.error("Error loading genres for movie {}: {}", movieId, e.getMessage(), e);
            return Collections.emptyList();
        } catch (Exception e) {
            log.error("Error loading genres for movie {}: {}", movieId, e.getMessage());
            return Collections.emptyList();
        }
    }

    private List<CountryDTO> loadCountries(Long movieId) {
        try {
            List<Country> countries = countryRepository.findAllByMovieId(movieId);

            return countries.stream()
                    .peek(country -> {
                        if (Thread.currentThread().isInterrupted()) {
                            throw new RuntimeException("Interrupted while mapping countries");
                        }
                    })
                    .map(country -> new CountryDTO(country.getId(), country.getName()))
                    .toList();
        } catch (RuntimeException e) {
            if (Thread.currentThread().isInterrupted()) {
                log.warn("Countries loading interrupted for movie id={}", movieId);
                return Collections.emptyList();
            }
            log.error("Error loading countries for movie {}: {}", movieId, e.getMessage(), e);
            return Collections.emptyList();
        } catch (Exception e) {
            log.error("Error loading countries for movie {}: {}", movieId, e.getMessage());
            return Collections.emptyList();
        }
    }

    private List<ReviewDTO> loadReviews(Long movieId) {
        try {
            List<Review> reviews = reviewRepository.findAllByMovieId(movieId);

            return reviews.stream()
                    .peek(review -> {
                        if (Thread.currentThread().isInterrupted()) {
                            throw new RuntimeException("Interrupted while mapping reviews");
                        }
                    })
                    .map(review -> {
                        UserDTO user = new UserDTO(
                                review.getAuthor().getId(),
                                review.getAuthor().getName()
                        );
                        return new ReviewDTO(review.getId(), review.getComment(), user);
                    })
                    .toList();

        } catch (RuntimeException e) {
            if (Thread.currentThread().isInterrupted()) {
                log.warn("Reviews loading interrupted for movie id={}", movieId);
                return Collections.emptyList();
            }
            log.error("Error loading reviews for movie {}: {}", movieId, e.getMessage(), e);
            return Collections.emptyList();
        } catch (Exception e) {
            log.error("Error loading reviews for movie {}: {}", movieId, e.getMessage());
            return Collections.emptyList();
        }
    }

    private <T> List<T> getWithTimeout(Future<List<T>> future, String name, long movieId) {
        try {
            return future.get(timeoutSeconds, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            log.warn("Timeout ({}s) while loading {} for movie id={}. Cancelling task.",
                    timeoutSeconds, name, movieId);
            future.cancel(true);
            return Collections.emptyList();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("{} loading interrupted for movie id={}", name, movieId);
            return Collections.emptyList();
        } catch (ExecutionException e) {
            log.error("Error during {} loading for movie id={}: {}", name, movieId, e.getCause().getMessage(), e.getCause());
            return Collections.emptyList();
        }
    }
}
