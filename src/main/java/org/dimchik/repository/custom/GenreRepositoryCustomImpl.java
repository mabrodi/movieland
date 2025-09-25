package org.dimchik.repository.custom;

import lombok.AllArgsConstructor;
import org.dimchik.cache.GenreCache;
import org.dimchik.dto.GenreDTO;

import java.util.List;

@AllArgsConstructor
public class GenreRepositoryCustomImpl implements GenreRepositoryCustom {
    private final GenreCache provider;

    @Override
    public List<GenreDTO> findAllCached() {
        return provider.findAll();
    }
}
