package org.dimchik.repository;

import org.dimchik.entity.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CountryRepository extends JpaRepository<Country, Long> {
    @Query(value = "SELECT c.* FROM countries c INNER JOIN movie_countries mc ON c.id = mc.country_id WHERE mc.movie_id = :movieId", nativeQuery = true)
    List<Country> findAllByMovieId(@Param("movieId") Long movieId);
}
