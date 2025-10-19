package org.dimchik.repository;

import org.dimchik.entity.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CountryRepository extends JpaRepository<Country, Long> {
    @Query("SELECT c FROM Country c JOIN c.movies m WHERE m.id = :movieId")
    List<Country> findAllByMovieId(@Param("movieId") Long movieId);
}
