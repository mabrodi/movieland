package org.dimchik.repository.custom;

import lombok.AllArgsConstructor;
import org.dimchik.repository.cache.GenreCacheRepository;
import org.dimchik.dto.GenreDTO;

import java.util.List;

@AllArgsConstructor
public class GenreRepositoryCustomImpl implements GenreRepositoryCustom {
    private final GenreCacheRepository provider;

    @Override
    public List<GenreDTO> findAllCached() {
        return provider.findAll();
    }
}
