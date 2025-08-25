package org.dimchik.repository;

import org.dimchik.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MovieRepository extends JpaRepository<Movie, Long> {
    @Query("select m from Movie m join m.genres g where g.id = :genreId")
    List<Movie> findMoviesByGenreId(@Param("genreId") Long genreId);
}
