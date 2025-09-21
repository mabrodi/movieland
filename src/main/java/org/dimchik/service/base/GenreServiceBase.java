package org.dimchik.service.base;

import org.dimchik.dto.GenreDTO;
import org.dimchik.repository.GenreRepository;
import org.dimchik.service.GenreService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GenreServiceBase implements GenreService {
    private final GenreRepository genreRepository;

    public GenreServiceBase(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    @Override
    public List<GenreDTO> findAll() {
        return genreRepository.findAllCached();
    }
}
