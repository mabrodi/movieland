package org.dimchik.service.assembler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dimchik.entity.Movie;
import org.dimchik.service.CountryService;
import org.dimchik.service.GenreService;
import org.dimchik.service.PosterService;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class MovieAssembler {
    private final GenreService genreService;
    private final CountryService countryService;
    private final PosterService posterService;

    public void enrichMovie(Movie movie, List<Long> genreIds, List<Long> countryIds, String picturePath) {
        applyGenres(movie, genreIds);
        applyCountries(movie, countryIds);
        upsertPoster(movie, picturePath);
    }

    private void applyGenres(Movie movie, List<Long> genreIds) {
        if (genreIds != null && !genreIds.isEmpty()) {
            genreService.enrichMovieByGenreIds(movie, genreIds);
        }
    }

    private void applyCountries(Movie movie, List<Long> countryIds) {
        if (countryIds != null && !countryIds.isEmpty()) {
            countryService.enrichMovieByCountryIds(movie, countryIds);
        }
    }

    private void upsertPoster(Movie movie, String picturePath) {
        if (picturePath != null) {
            posterService.upsertPoster(movie, picturePath);
        }
    }
}
