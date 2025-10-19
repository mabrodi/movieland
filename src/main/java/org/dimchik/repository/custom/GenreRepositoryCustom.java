package org.dimchik.repository.custom;

import org.dimchik.dto.GenreDTO;

import java.util.List;

public interface GenreRepositoryCustom {
    List<GenreDTO> findAllCached();
}
