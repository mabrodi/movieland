package org.dimchik.service.cache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dimchik.entity.Movie;
import org.dimchik.repository.MovieRepository;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class MovieCacheService {

    private final MovieRepository movieRepository;
    private final Map<Long, Movie> cache = new ConcurrentHashMap<>();

    public  Movie getMovie(long id) {
        Movie cached = cache.get(id);
        if (cached != null) {
            log.info("Movie id={} loaded from cache", id);
            return cached;
        }

        Movie movie = movieRepository.findById(id).orElse(null);
        if (movie != null) {
            cache.put(id, movie);
            log.info("Movie id={} loaded from DB and cached", id);
        } else {
            log.warn("Movie id={} not found in DB", id);
        }

        return movie;
    }

    public  void updateMovie(Movie updatedMovie) {
        movieRepository.save(updatedMovie);

        Movie cached = cache.get(updatedMovie.getId());
        if (cached != null) {
            cache.put(updatedMovie.getId(), updatedMovie);
            log.info("Movie id={} updated in cache", updatedMovie.getId());
            return;
        }

        log.info("Movie id={} updated in DB only (not in cache)", updatedMovie.getId());
    }
}
