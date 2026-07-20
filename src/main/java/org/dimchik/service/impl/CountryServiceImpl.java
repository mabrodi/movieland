package org.dimchik.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dimchik.entity.Country;
import org.dimchik.entity.Movie;
import org.dimchik.repository.CountryRepository;
import org.dimchik.service.CountryService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
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
    public List<Country> findAllIds(List<Long> ids) {
        log.info("start give list countries by ids: {}", ids);
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }
        List<Country> countries = countryRepository.findAllById(ids);

        if (ids.size() != countries.size()) {
            throw new IllegalArgumentException("Some countries not found");
        }

        log.info("end give list countries by ids: {}", ids);

        return countries;
    }
}
