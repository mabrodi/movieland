package org.dimchik.service;

import org.dimchik.entity.Country;
import org.dimchik.entity.Movie;

import java.util.List;

public interface CountryService {
    void enrichSingleMovieByCountries(Movie movie);

    List<Country> findAllIds(List<Long> ids);
}
