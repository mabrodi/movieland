package org.dimchik.repository;

import org.dimchik.entity.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GenreRepository extends JpaRepository<Genre, Long> {

    @Query(value = "SELECT g.* FROM genres g INNER JOIN movie_genres mg ON g.id = mg.genre_id WHERE mg.movie_id = :movieId", nativeQuery = true)
    List<Genre> findAllByMovieId(@Param("movieId") Long movieId);
}
