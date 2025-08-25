package org.dimchik.repository;

import org.dimchik.entity.Genre;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GenreResponse extends JpaRepository<Genre, Long> {
}
