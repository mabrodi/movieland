package org.dimchik.service.base;

import lombok.RequiredArgsConstructor;
import org.dimchik.dto.GenreDTO;
import org.dimchik.repository.GenreRepository;
import org.dimchik.service.GenreService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GenreServiceBase implements GenreService {
    private final GenreRepository genreRepository;

    @Override
    public List<GenreDTO> findAll() {
        return genreRepository.findAllCached();
    }
}
