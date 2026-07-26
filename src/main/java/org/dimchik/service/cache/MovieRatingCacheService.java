package org.dimchik.service.cache;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dimchik.dto.CachedMovieRating;
import org.dimchik.dto.MovieUserKey;
import org.dimchik.dto.PendingMovieRating;
import org.dimchik.entity.Movie;
import org.dimchik.entity.MovieRating;
import org.dimchik.enums.Action;
import org.dimchik.repository.MovieRatingRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@Slf4j
@Service
@RequiredArgsConstructor
public class MovieRatingCacheService {
    private final Map<Long, CachedMovieRating> movieRatingCache = new ConcurrentHashMap<>();
    private final ConcurrentLinkedQueue<PendingMovieRating> pendingRatings = new ConcurrentLinkedQueue<>();
    private final Map<MovieUserKey, Double> userRatingCache = new ConcurrentHashMap<>();

    private final MovieRatingRepository movieRatingRepository;

    public double getUserMovieRating(long movieId, long userId) {
        return userRatingCache.get(new MovieUserKey(movieId, userId));
    }

    public void createUserMovieRating(long movieId, double rating, long userId) {
        pendingRatings.add(PendingMovieRating.builder()
                .movieId(movieId)
                .userId(userId)
                .rating(rating)
                .action(Action.CREATE)
                .build());

        userRatingCache.put(new MovieUserKey(movieId, userId), rating);

        updateCacheMovieRating(movieId, rating);
    }

    public void updateUserMovieRating(long movieId, double rating, long userId) {
        Double oldRating = userRatingCache.get(new MovieUserKey(movieId, userId));

        pendingRatings.add(PendingMovieRating.builder()
                .movieId(movieId)
                .userId(userId)
                .rating(rating)
                .action(Action.UPDATE)
                .build());

        userRatingCache.put(new MovieUserKey(movieId, userId), rating);

        movieRatingCache.compute(movieId, (id, cached) -> {
            double average = cached.getAverageRating();
            int count = cached.getVoteCount();
            double newAverage = (average * count - oldRating + rating) / count;
            newAverage = Math.round(newAverage * 10.0) / 10.0;
            log.info("Rating: {} for movie {}", newAverage, movieId);
            cached.setAverageRating(newAverage);

            return cached;
        });
    }

    public boolean existsUserRating(long movieId, long userId) {
        return userRatingCache.containsKey(new MovieUserKey(movieId, userId));
    }

    public void enrichMovieWithRating(Movie movie) {
        log.info("Start to enrich movie by rating with Id {}", movie.getId());
        CachedMovieRating cachedMovieRating = movieRatingCache.get(movie.getId());

        if (cachedMovieRating != null) {
            movie.setRating(cachedMovieRating.getAverageRating());
            log.info("Movie with Id {} has been enriched by rating: {}", movie.getId(), movie.getRating());
        } else {
            log.info("Rating for movie {} is absent", movie.getId());
        }
    }

    public void removeByMovieId(long movieId) {
        log.info("Removing all ratings for movie id = {}", movieId);

        movieRatingCache.remove(movieId);

        userRatingCache.keySet()
                .removeIf(key -> key.getMovieId() == movieId);

        pendingRatings.removeIf(rating -> rating.getMovieId() == movieId);

        log.info("All ratings removed for movie id = {}", movieId);
    }

    @Scheduled(fixedDelayString = "${cache.movie-rating-save}", initialDelayString = "${cache.movie-rating-save}")
    private void loadUserMovieRatingToDb() {
        List<MovieRating> list = new ArrayList<>();
        PendingMovieRating pendingMovieRating;
        while ((pendingMovieRating = pendingRatings.poll()) != null) {
            if (pendingMovieRating.getAction().equals(Action.UPDATE)) {
                movieRatingRepository.updateRating(
                        pendingMovieRating.getMovieId(),
                        pendingMovieRating.getUserId(),
                        pendingMovieRating.getRating());
                continue;
            }

            list.add(MovieRating.builder()
                    .movieId(pendingMovieRating.getMovieId())
                    .userId(pendingMovieRating.getUserId())
                    .rating(pendingMovieRating.getRating()).build());
        }

        if (!list.isEmpty()) {
            movieRatingRepository.saveAll(list);
        }
    }

    public void clear() {
        log.info("Removing all ratings");

        movieRatingCache.clear();
        userRatingCache.clear();
        pendingRatings.clear();

        log.info("All ratings removed");
    }

    @PostConstruct
    public void fillMovieRatingCache() {
        log.info("Start to fill cache with movie rating");
        for (MovieRating movieRating : movieRatingRepository.findAll()) {
            updateCacheMovieRating(movieRating.getMovieId(), movieRating.getRating());

            log.info("Update cache movie rating: {} movieId: {}", movieRating.getRating(), movieRating.getMovieId());
            userRatingCache.put(
                    new MovieUserKey(
                            movieRating.getMovieId(),
                            movieRating.getUserId()
                    ),
                    movieRating.getRating()
            );
        }

        log.info("Finish to fill cache with movie rating, count : {}", movieRatingCache.size());
    }

    private void updateCacheMovieRating(long movieId, double rating) {
        movieRatingCache.compute(movieId, (id, cached) -> {
            if (cached == null) {
                cached = new CachedMovieRating();
            }

            double average = cached.getAverageRating();
            int count = cached.getVoteCount();

            double newAverage = (average * count + rating) / (count + 1);
            newAverage = Math.round(newAverage * 10.0) / 10.0;
            log.info("Rating: {} for movie {}", newAverage, movieId);

            cached.setVoteCount(count + 1);
            cached.setAverageRating(newAverage);

            return cached;
        });
    }
}
