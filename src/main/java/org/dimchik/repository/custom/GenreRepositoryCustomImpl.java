package org.dimchik.repository.custom;

import org.dimchik.service.cache.GenreCacheService;
import org.dimchik.dto.GenreDTO;

import java.util.List;

public class GenreRepositoryCustomImpl implements GenreRepositoryCustom {
    private final GenreCacheService genreCache;

    public GenreRepositoryCustomImpl(GenreCacheService genreCache) {
        this.genreCache = genreCache;
    }

    @Override
    public List<GenreDTO> findAllCached() {
        return genreCache.findAll();
    }
}
