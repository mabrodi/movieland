package org.dimchik.repository;

import org.dimchik.entity.MovieRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface MovieRatingRepository extends JpaRepository<MovieRating, Long> {

    @Modifying
    @Transactional
    @Query("""
        update MovieRating mr 
        set mr.rating = :rating,
            mr.updatedAt = CURRENT_TIMESTAMP
        where mr.movieId = :movieId 
          and mr.userId = :userId
    """)
    int updateRating(
            @Param("movieId") Long movieId,
            @Param("userId") Long userId,
            @Param("rating") double rating
    );
}
