package org.dimchik.repository.custom;

import lombok.AllArgsConstructor;
import org.dimchik.dto.GenreDTO;
import org.dimchik.service.cache.GenreCacheService;

import java.util.List;

@AllArgsConstructor
public class GenreRepositoryCustomImpl implements GenreRepositoryCustom {
    private final GenreCacheService genreCacheService;

    @Override
    public List<GenreDTO> findAllCached() {
        return genreCacheService.findAll();
    }
}
