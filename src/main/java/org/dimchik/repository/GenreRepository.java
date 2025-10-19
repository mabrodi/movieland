package org.dimchik.repository;

import org.dimchik.repository.custom.GenreRepositoryCustom;
import org.dimchik.entity.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GenreRepository extends JpaRepository<Genre, Long>, GenreRepositoryCustom {

    @Query("SELECT g FROM Genre g JOIN g.movies m WHERE m.id = :movieId")
    List<Genre> findAllByMovieId(@Param("movieId") Long movieId);
}
