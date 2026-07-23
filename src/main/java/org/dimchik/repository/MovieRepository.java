package org.dimchik.repository;

import org.dimchik.entity.Movie;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MovieRepository extends JpaRepository<Movie, Long> {

    @Query("select m from Movie m left join fetch m.poster")
    List<Movie> findAllWithPoster(Sort sort);

    @Query("select m from Movie m left join fetch m.poster join m.genres g where g.id = :genreId")
    List<Movie> findMoviesByGenreId(@Param("genreId") Long genreId);

    @Query("select m from Movie m left join fetch m.poster order by function('random')")
    List<Movie> findRandomMovies(Pageable pageable);
}
