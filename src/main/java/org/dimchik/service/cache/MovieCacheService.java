package org.dimchik.service.cache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dimchik.entity.Movie;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class MovieCacheService {
    private final Map<Long, Movie> cachedMovie = new ConcurrentHashMap<>();

    public void add(Movie movie) {
        log.debug("Adding movie to cache with id = {}", movie.getId());
        cachedMovie.put(movie.getId(), movie);
    }

    public Movie getById(long id) {
        log.debug("Getting movie from cache by id = {}", id);
        return cachedMovie.get(id);
    }

    public void invalidate(long movieId) {
        log.debug("Invalidating cache for movie id = {}", movieId);
        cachedMovie.remove(movieId);
    }
}
