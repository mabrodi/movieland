package org.dimchik.service.concurrent;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dimchik.entity.Movie;
import org.dimchik.service.ConcurrentEnrichmentMovieService;
import org.dimchik.service.CountryService;
import org.dimchik.service.GenreService;
import org.dimchik.service.ReviewService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConcurrentEnrichmentMovieServiceImpl implements ConcurrentEnrichmentMovieService {
    private final ExecutorService ENRICH_EXECUTOR_SERVICE = Executors.newCachedThreadPool();

    private final CountryService countryService;
    private final GenreService genreService;
    private final ReviewService reviewService;

    @Value("${concurrent.enrichment.timeout}")
    private int timeout;

    @Override
    public void enrichMovie(Movie movie) {
        long remainTime = timeout;
        long elapsedTime;
        long startTime = System.currentTimeMillis();

        List<Future<?>> futureList = new ArrayList<>();

        Runnable refreshWithCountries = () -> countryService.enrichSingleMovieByCountries(movie);
        Runnable refreshWithGenres = () -> genreService.enrichSingleMovieByGenres(movie);
        Runnable refreshWithReviews = () -> reviewService.enrichSingleMovieByReviewes(movie);

        futureList.add(ENRICH_EXECUTOR_SERVICE.submit(refreshWithCountries));
        futureList.add(ENRICH_EXECUTOR_SERVICE.submit(refreshWithGenres));
        futureList.add(ENRICH_EXECUTOR_SERVICE.submit(refreshWithReviews));

        for (Future<?> future : futureList) {
            try {
                future.get(remainTime, TimeUnit.MILLISECONDS);
                elapsedTime = System.currentTimeMillis() - startTime;
                if (remainTime - elapsedTime < 0) {
                    remainTime = 0;
                } else {
                    remainTime = remainTime - elapsedTime;
                }
            } catch (TimeoutException e) {
                log.warn("One of parallel threads was interrupted due timeout");
                future.cancel(true);
            } catch (InterruptedException e) {
                log.error("The main thread was interrupted");
                for (Future<?> futureResult : futureList) {
                    futureResult.cancel(true);
                }
                throw new RuntimeException("The main thread was interrupted");
            } catch (ExecutionException e) {
                log.warn("The result of movie enrichment can't be received");
            }
        }
    }
}
