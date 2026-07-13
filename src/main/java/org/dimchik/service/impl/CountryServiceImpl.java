package org.dimchik.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dimchik.entity.Country;
import org.dimchik.entity.Movie;
import org.dimchik.repository.CountryRepository;
import org.dimchik.service.CountryService;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class CountryServiceImpl implements CountryService {
    private final CountryRepository countryRepository;

    @Override
    public void enrichSingleMovieByCountries(Movie movie) {
        log.info("Start to enrich single movie by countries");

        List<Country> countries = countryRepository.findAllByMovieId(movie.getId());

        if (!Thread.currentThread().isInterrupted()) {
            movie.setCountries(countries);
            log.info("Finish to enrich single movie by countries");
        }
    }

    @Override
    public void enrichMovieByCountryIds(Movie movie, List<Long> countryIds) {
        if (countryIds == null || countryIds.isEmpty()) {
            return;
        }

        List<Country> countries = countryRepository.findAllById(countryIds);
        if (countries.size() != countryIds.size()) {
            throw new IllegalArgumentException("Some countries not found");
        }
        movie.setCountries(countries);
    }
}
