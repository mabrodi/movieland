package org.dimchik.service.cache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dimchik.entity.Movie;
import org.dimchik.repository.MovieRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MovieCacheService {

    private final MovieRepository movieRepository;
    private final List<Movie> cache = new ArrayList<>();

    public synchronized Movie getMovie(long id) {
        for (Movie cached : cache) {
            if (cached.getId() == id) {
                log.info("Movie id={} loaded from cache", id);
                return cached;
            }
        }

        Movie movie = movieRepository.findById(id).orElse(null);
        if (movie != null) {
            try {
                cache.add(movie);
                log.info("Movie id={} loaded from DB and cached", id);
            } catch (OutOfMemoryError e) {
                log.warn("Not enough memory, clearing cache...");
                cache.clear();
                log.warn("Movie cache cleared");
            }
        } else {
            log.warn("Movie id={} not found in DB", id);
        }

        return movie;
    }

    public synchronized void updateMovie(Movie updatedMovie) {
        movieRepository.save(updatedMovie);

        for (int i = 0; i < cache.size(); i++) {
            Movie cached = cache.get(i);
            if (cached.getId().equals(updatedMovie.getId())) {
                cache.set(i, updatedMovie);
                log.info("🔄 Movie id={} updated in cache", updatedMovie.getId());
                return;
            }
        }

        log.info("Movie id={} updated in DB only (not in cache)", updatedMovie.getId());
    }
}
