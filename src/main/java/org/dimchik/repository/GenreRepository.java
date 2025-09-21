package org.dimchik.repository;

import org.dimchik.repository.custom.GenreRepositoryCustom;
import org.dimchik.entity.Genre;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GenreRepository extends JpaRepository<Genre, Long>, GenreRepositoryCustom {
}
